/* شرح الملف: اختبار سلامة تجهيز الخطوات المشتركة للحالات ثم تنظيف بياناتها، ضمن المخازن.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.*;

public abstract class StoreScenarioTest extends BaseTest {
    final org.openqa.selenium.WebDriver caseDriver() {
        return driver;
    }

    protected abstract StoreMovementKind kind();

    protected abstract StoreModulePage movementPage();

    protected StoreTestData data;
    protected StoreModulePage page;
    protected StoreTestData.Record item;
    protected String marker, mainWindow;
    private final List<StoreTestData> pending = new ArrayList<>();
    final Map<String, BigDecimal> initialBalances = new LinkedHashMap<>();
    StoreTestData.Record selectedParty;

    @BeforeClass(alwaysRun = true)
    public void awaitStoreSession() {
        requirePreparedSession();
        movementPage().smAwaitSession();
    }

    @BeforeMethod(alwaysRun = true)
    public void beginStoreScenario() {
        requirePreparedSession();
        marker = "QA-ST-" + UUID.randomUUID().toString().substring(0, 12);
        page = movementPage();
        mainWindow = driver.getWindowHandle();
        if (data == null) {
            data = new StoreTestData(driver);
            pending.add(data);
        }
        data.beginScenario();
        item = null;
        initialBalances.clear();
        selectedParty = null;
    }

    // Keep feature scenarios independent of the separately tested name/barcode lookup defects.
    protected String baselineMode() {
        return new StorePreparationSteps(this).baselineMode();
    }

    protected String defaultParty() {
        return new StorePreparationSteps(this).defaultParty();
    }

    protected StoreTestData.Record ownItem(int index) {
        return new StorePreparationSteps(this).ownItem(index);
    }

    StoreTestData.Record ownParty(String type) {
        return new StorePreparationSteps(this).ownParty(type);
    }

    String partyLabel(String type) {
        return new StorePreparationSteps(this).partyLabel(type);
    }

    protected void prepare(String omitted, String partyType) {
        new StorePreparationSteps(this).prepare(omitted, partyType);
    }

    void snapshot(StoreTestData.Record record) {
        new StorePreparationSteps(this).snapshot(record);
    }

    BigDecimal initial(StoreTestData.Record record, int index) {
        return new StorePreparationSteps(this).initial(record, index);
    }

    protected void fill(String mode) {
        new StorePreparationSteps(this).fill(mode);
    }

    void add(StoreTestData.Record record, String mode) {
        new StorePreparationSteps(this).add(record, mode);
    }

    @SuppressWarnings("unchecked")
    protected List<StoreTestData.Record> capture() {
        return new StoreSaveVerification(this).capture();
    }

    protected StoreTestData.Record save(boolean print) {
        return new StoreSaveVerification(this).save(print);
    }

    void verifyReport(StoreTestData.Record record) {
        new StoreSaveVerification(this).verifyReport(record);
    }

    protected static BigDecimal number(Object n) {
        Assert.assertNotNull(n);
        return new BigDecimal(n.toString());
    }

    protected void sameNumber(Object actual, String expected) {
        new StoreSaveVerification(this).sameNumber(actual, expected);
    }

    void fixtureBalance(StoreTestData.Record record, int index, BigDecimal expected) {
        new StoreSaveVerification(this).fixtureBalance(record, index, expected);
    }

    @io.qameta.allure.Step("التحقق من أثر الحركة على المخزون بكمية {quantity}")
    protected void assertEffect(String quantity) {
        new StoreSaveVerification(this).assertEffect(quantity);
    }

    protected StoreTestData.Record create(String mode, boolean print, String party) {
        return new StoreSaveVerification(this).create(mode, print, party);
    }

    protected void partyIdentity(String type) {
        new StoreEditingChecks(this).partyIdentity(type);
    }

    boolean hasId(Object id) {
        return new StoreEditingChecks(this).hasId(id);
    }

    protected void editOwn() {
        new StoreEditingChecks(this).editOwn();
    }

    void find(StoreTestData.Record record) {
        new StoreEditingChecks(this).find(record);
    }

    protected void deleteOwn() {
        new StoreEditingChecks(this).deleteOwn();
    }

    protected void required(String missing, String party, String expected) {
        new StoreInputValidation(this).required(missing, party, expected);
    }

    protected void rejectQuantity(String quantity) {
        new StoreInputValidation(this).rejectQuantity(quantity);
    }

    protected void noItems() {
        new StoreInputValidation(this).noItems();
    }

    protected void oldDate() {
        new StoreInputValidation(this).oldDate();
    }

    protected void pickerWithoutStore() {
        new StoreInputValidation(this).pickerWithoutStore();
    }

    protected void sameStore() {
        new StoreInputValidation(this).sameStore();
    }

    protected void pickerWithoutDestination() {
        new StoreInputValidation(this).pickerWithoutDestination();
    }

    @Test(groups = "stores")
    @Story("إلغاء إنشاء سند دون تغيير الرصيد")
    public void SM_CancelCreation() {
        new StoreEditingChecks(this).SM_CancelCreation();
    }

    @Test(groups = "stores")
    @Story("إلغاء تعديل السند يحافظ على بياناته ورصيده")
    public void SM_CancelEdit() {
        new StoreEditingChecks(this).SM_CancelEdit();
    }

    @Test(groups = "stores")
    @Story("إلغاء حذف السند")
    public void SM_CancelDelete() {
        new StoreEditingChecks(this).SM_CancelDelete();
    }

    @Test(groups = "stores")
    @Story("المخزن مطلوب")
    public void SM_RequiredStore() {
        new StoreInputValidation(this).SM_RequiredStore();
    }

    @Test(groups = "stores")
    @Story("رفض الكمية الصفرية")
    public void SM_ZeroQuantity() {
        new StoreInputValidation(this).SM_ZeroQuantity();
    }

    @Test(groups = "stores")
    @Story("رفض الكمية السالبة")
    public void SM_NegativeQuantity() {
        new StoreInputValidation(this).SM_NegativeQuantity();
    }

    @Test(groups = "stores")
    @Story("قبول كمية كسرية والتحقق من أثرها على الرصيد")
    public void SM_DecimalQuantity() {
        new StoreInputValidation(this).SM_DecimalQuantity();
    }

    @Test(groups = "stores")
    @Story("كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم")
    public void SM_StockBalanceBoundary() {
        new StoreInputValidation(this).SM_StockBalanceBoundary();
    }

    @Test(groups = "stores")
    @Story("حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة")
    public void SM_LineCalculations() {
        new StoreInputValidation(this).SM_LineCalculations();
    }

    @Test(groups = "stores")
    @Story("حذف سطر صنف مملوك للاختبار قبل الحفظ")
    public void SM_RemoveItem() {
        new StoreInputValidation(this).SM_RemoveItem();
    }

    @Test(groups = "stores")
    @Story("إضافة نفس الصنف لا تنشئ سطرين متطابقين")
    public void SM_DuplicateItem() {
        new StoreInputValidation(this).SM_DuplicateItem();
    }

    protected void notesRoundTrip() {
        new StoreEditingChecks(this).notesRoundTrip();
    }

    @Test(groups = "stores")
    @Story("عرض هيكل قائمة سندات المخازن")
    public void SM_ListStructure() {
        new StoreListChecks(this).SM_ListStructure();
    }

    @Test(groups = "stores")
    @Story("تصدير PDF فعلي لقائمة السندات")
    public void SM_ExportPdf() {
        new StoreListChecks(this).SM_ExportPdf();
    }

    @Test(groups = "stores")
    @Story("تصدير Excel فعلي لقائمة السندات")
    public void SM_ExportExcel() {
        new StoreListChecks(this).SM_ExportExcel();
    }

    void export(String format) {
        new StoreListChecks(this).export(format);
    }

    @Test(groups = "stores")
    @Story("البحث وإعادة التصفية في الأذونات، أو إعادة فتح قائمة التوالف التي لا تدعم البحث")
    public void SM_SearchAndReset() {
        new StoreListChecks(this).SM_SearchAndReset();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanStoreScenario(org.testng.ITestResult result) {
        if (driver == null || page == null || data == null || pending.isEmpty()) return;
        if (result != null && !result.isSuccess()) {
            Allure.addAttachment(
                    "Warehouse failure DOM", "text/html", page.smDiagnosticMarkup(), ".html");
            Allure.addAttachment(
                    "Warehouse lookup diagnostics",
                    "application/json",
                    page.smDiagnostics(),
                    ".json");
        }
        for (String window : new ArrayList<>(driver.getWindowHandles()))
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                driver.close();
            }
        driver.switchTo().window(mainWindow);
        List<String> failures = new ArrayList<>();
        if (pending.contains(data))
            try {
                if (marker.equals(page.smCaptureMarker()))
                    data.rememberAttempts(page.smAttempts(), page.smSaves(), marker);
                page.smSettled();
                capture();
            } catch (AssertionError | RuntimeException e) {
                failures.add(e.getMessage());
            }
        if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
    }

    @AfterClass(alwaysRun = true)
    public void retryStoreCleanup() {
        List<String> failures = new ArrayList<>();
        try {
            cleanStoreScenario(null);
        } catch (AssertionError | RuntimeException e) {
            failures.add(e.toString());
        }
        for (var context : new ArrayList<>(pending))
            try {
                context.close();
                pending.remove(context);
                if (data == context) data = null;
            } catch (AssertionError | RuntimeException e) {
                failures.add(e.toString());
            }
        if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
    }
}
