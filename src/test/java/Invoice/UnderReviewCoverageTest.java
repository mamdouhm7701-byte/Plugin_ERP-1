/* شرح الملف: اختبارات فاتورة تحت الإجراء: رفض الكمية الصفرية في فاتورة تحت الإجراء؛ حفظ فاتورة تحت
 * الإجراء بالعميل والصنف الصحيحين دون صرف المخزون؛ إلغاء فاتورة تحت الإجراء دون حفظ؛ إعادة فتح
 * فاتورة تحت الإجراء المحفوظة.
 */
package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import TestSupport.BasicDataFixtures;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class UnderReviewCoverageTest extends BaseTest {
    private SalesTestData refs;
    private BasicDataFixtures documents;
    private SalesModulePage page;
    private SalesTestData.Record item, customer;
    private String marker;
    private String documentRoute;
    private BigDecimal before;

    @BeforeClass(alwaysRun = true)
    public void ownReferences() {
        requirePreparedSession();
        page = new SalesModulePage(driver);
        page.slAwaitSession();
        refs = new SalesTestData(driver);
        documents = new BasicDataFixtures(driver);
    }

    @BeforeMethod(alwaysRun = true)
    public void begin() {
        requirePreparedSession();
        refs.beginScenario();
        documents.beginScenario();
    }

    private void prepare() {
        item = refs.item(1);
        customer = refs.customer();
        var agent = refs.agent();
        before = new BigDecimal(refs.balance(item, 1).toString());
        marker = documents.reserveName("UnderReviews", "under-review");
        documentRoute =
                "Concrete"
                                .equals(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                driver,
                                                "return erpVue.root().$store.getters['app/currentBranch']?.activityType"))
                        ? "concrete-under-review"
                        : "underReview";
        page.slOpen(documentRoute + "/new");
        page.slFormReady();
        documents.installCapture();
        page.slChoose("المخزن", refs.store(1).name());
        page.slChoose("نوع السداد", "آجل");
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        page.slNotes(marker);
        if (documentRoute.equals("concrete-under-review"))
            page.slAddBarcode(item.values().get("mainUnitBarcode").toString(), item.name());
        else page.slAddPicker(item.name());
        page.slQuantity(item.name(), "3");
    }

    private BasicDataFixtures.Record save() {
        page.slSave(false);
        var record = documents.awaitSaved("UnderReviews", marker);
        var stored = documents.read(record);
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(stored.get("storeId"), refs.store(1).id());
        if (documentRoute.equals("concrete-under-review"))
            Assert.assertEquals(
                    record.values().get("isConcrete"),
                    Boolean.TRUE,
                    "The native Concrete save request must retain its document kind");
        io.qameta.allure.Allure.addAttachment(
                "Owned under-review document",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "route",
                                        documentRoute,
                                        "sent",
                                        record.values(),
                                        "stored",
                                        stored))
                        .toString(2),
                ".json");
        var lines = (List<?>) stored.get("lines");
        Assert.assertEquals(lines.size(), 1);
        Assert.assertEquals(((Map<?, ?>) lines.get(0)).get("itemId"), item.id());
        Assert.assertEquals(
                new BigDecimal(((Map<?, ?>) lines.get(0)).get("quantity").toString())
                        .compareTo(new BigDecimal("3")),
                0);
        return record;
    }

    @Test
    @Story("حفظ فاتورة تحت الإجراء بالعميل والصنف الصحيحين دون صرف المخزون")
    public void savesUnderReviewWithoutPostingInventory() {
        prepare();
        save();
        Assert.assertEquals(
                new BigDecimal(refs.balance(item, 1).toString()).compareTo(before),
                0,
                "Under-review draft must not post a normal sale inventory movement");
    }

    @Test
    @Story("إعادة فتح فاتورة تحت الإجراء المحفوظة")
    public void reopensSavedDocumentByCapturedId() {
        prepare();
        var saved = save();
        page.slDismiss();
        try {
            page.slEdit(documentRoute, saved.id(), marker, item.name());
        } catch (org.openqa.selenium.TimeoutException failure) {
            var route =
                    Plugin_ERP.Util.VueComponentAccess.execute(
                            driver,
                            """
                const r=erpVue.root()?.$router?.currentRoute,vm=r?.matched?.at(-1)?.instances?.default;
                return {path:r?.path,params:r?.params,componentId:vm?.id,selectedItem:vm?.selectedItem,
                  loaded:vm?._isMounted,bodyText:vm?.$el?.innerText};
                """);
            io.qameta.allure.Allure.addAttachment(
                    "Under-review native reopen diagnostics",
                    "application/json",
                    new org.json.JSONObject(
                                    Map.of(
                                            "route",
                                            route,
                                            "stored",
                                            documents.read(saved),
                                            "expectedId",
                                            saved.id()))
                            .toString(2),
                    ".json");
            throw failure;
        }
        Assert.assertEquals(page.slDocument().get("id"), saved.id());
        Assert.assertEquals(page.slDocument().get("notes"), marker);
    }

    @Test
    @Story("إلغاء فاتورة تحت الإجراء دون حفظ")
    public void leavingNewFormDoesNotCreateDocument() {
        prepare();
        page.slOpen("invoice-list");
        Assert.assertNull(documents.captured("UnderReviews", marker));
        Assert.assertEquals(new BigDecimal(refs.balance(item, 1).toString()).compareTo(before), 0);
    }

    @Test
    @Story("رفض الكمية الصفرية في فاتورة تحت الإجراء")
    public void zeroQuantityCannotPersist() {
        prepare();
        page.slQuantity(item.name(), "0");
        if (!page.slSaveDisabled()) page.slSave(false);
        page.slSettled();
        documents.collectUi();
        Assert.assertNull(
                documents.captured("UnderReviews", marker), "Zero quantity draft was persisted");
    }

    @AfterMethod(alwaysRun = true)
    public void collect() {
        if (documents != null) {
            documents.awaitUiSettled();
            documents.collectUi();
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (documents != null) documents.close();
        // A failed child cleanup deliberately prevents deletion of its references.
        if (refs != null) refs.close();
    }
}
