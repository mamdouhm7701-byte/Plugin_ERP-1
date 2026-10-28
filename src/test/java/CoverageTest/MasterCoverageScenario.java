/* شرح الملف: تجهيز الخطوات المشتركة للحالات ثم تنظيف بياناتها، ضمن خطوات التشغيل والتقارير.
 */
package CoverageTest;

import static TestSupport.BasicDataFixtures.fields;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import TestSupport.BasicDataFixtures;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.Story;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class MasterCoverageScenario extends BasicDataScenarioTest {
    protected abstract String endpoint();

    protected abstract String route();

    protected abstract String title();

    private ScreenPage page;
    private String code;

    private boolean table() {
        return endpoint().equals("Tables");
    }

    private boolean tree() {
        return Set.of("Accounts", "CostCenters").contains(endpoint());
    }

    private String newCode() {
        return Integer.toString(
                java.util.concurrent.ThreadLocalRandom.current().nextInt(7000000, 9999999));
    }

    private Map<String, Object> seedValues() {
        return table()
                ? fields("code", newCode(), "chairsCount", 4)
                : tree()
                        ? fields(
                                "code",
                                newCode(),
                                "type",
                                "Main",
                                "nature",
                                "Debit",
                                "finalAccountType",
                                "Budget",
                                "level",
                                1,
                                "previousBalances",
                                List.of(),
                                "prevDebit",
                                0,
                                "prevCredit",
                                0,
                                "isDistributable",
                                false,
                                "isSharedBetweenBranches",
                                false)
                        : fields();
    }

    private BasicDataFixtures.Record seed() {
        return basicData().fixture("master-" + endpoint(), endpoint(), seedValues());
    }

    private void open() {
        page = new ScreenPage(driver);
        page.open(route(), title());
        basicData().installCapture();
    }

    private void newForm() {
        if (tree()) {
            var parent = seed();
            open();
            page.treeNode(parent.id(), parent.name());
            page.click("جديد");
            page.choose("الحساب الرئيسى", parent.name());
            page.choose("نوع الحساب", "فرعي");
            if (endpoint().equals("Accounts")) {
                page.choose("طبيعة الحساب", "مدين");
                page.choose("تأثير الحساب", "ميزانيه");
            }
        } else {
            open();
            page.click("جديد");
        }
        code = newCode();
    }

    private void fill(String marker) {
        if (table()) {
            page.field("Table Number", code);
            page.field("Chairs Count", "4");
        } else {
            if (tree()) page.type(By.id(endpoint().equals("Accounts") ? "accCode" : "code"), code);
            page.field("arabicName", marker);
            page.field("englishName", "English " + marker);
        }
        page.notes(marker);
    }

    private BasicDataFixtures.Record create() {
        newForm();
        String marker = ownedName(endpoint(), "create");
        fill(marker);
        page.click("حفظ");
        var saved = basicData().awaitSaved(endpoint(), marker);
        var stored = basicData().read(saved);
        io.qameta.allure.Allure.addAttachment(
                "Master save and readback",
                "application/json",
                new org.json.JSONObject(
                                fields("sent", saved.values(), "id", saved.id(), "stored", stored))
                        .toString(),
                ".json");
        Assert.assertNotNull(stored, "Saved master must be readable by its captured ID");
        // Some master detail DTOs omit notes; the separate reopen contract verifies UI persistence.
        if (stored.containsKey("notes")) Assert.assertEquals(stored.get("notes"), marker);
        if (table()) {
            Assert.assertEquals(stored.get("code").toString(), code);
            Assert.assertEquals(stored.get("chairsCount").toString(), "4");
        } else {
            Assert.assertEquals(stored.get("arabicName"), marker);
            Assert.assertEquals(stored.get("englishName"), "English " + marker);
            if (tree()) Assert.assertEquals(stored.get("type"), "Sub");
        }
        page.dismissSuccess();
        return saved;
    }

    private void edit(BasicDataFixtures.Record record) {
        open();
        if (tree()) selectTreeRecord(record);
        else
            page.rowAction(
                    table() ? basicData().read(record).get("code").toString() : record.name(),
                    "edit");
        if (!table())
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(
                            d ->
                                    record.name()
                                            .equals(
                                                    page.value(
                                                            By.cssSelector(
                                                                    "input[name='arabicName']"))));
    }

    private void selectTreeRecord(BasicDataFixtures.Record record) {
        // Child rows are created lazily when the owned root is expanded.
        page.expandTreeNode(seed().id());
        page.treeNode(record.id(), record.name());
    }

    @Test
    @Story("إنشاء سجل من الشاشة ومطابقة البيانات المحفوظة")
    public void createsAndReadsBackExactOwnedRecord() {
        var record = create();
        if (tree()) {
            open();
            selectTreeRecord(record);
            Assert.assertTrue(
                    page.visibleElement(page.treeLabel(record.id()))
                            .getText()
                            .contains(record.name()),
                    "Tree label differs from saved master name");
        }
    }

    @Test
    @Story("الملاحظات المحفوظة تبقى عند إعادة فتح السجل")
    public void savedNotesSurviveReopening() {
        var record = create();
        edit(record);
        Assert.assertEquals(
                page.value(By.cssSelector("textarea")),
                record.name(),
                "Saved notes disappeared after reopening the owned master");
    }

    @Test
    @Story("تعديل السجل يحافظ على هويته ويحفظ التغيير")
    public void editsOwnedRecordWithoutCreatingAnother() {
        var record = create();
        edit(record);
        if (table()) page.field("Chairs Count", "7");
        else page.field("englishName", "Updated " + record.name());
        page.click("حفظ");
        new WebDriverWait(driver, Duration.ofSeconds(25))
                .until(
                        d -> {
                            var stored = basicData().read(record);
                            return table()
                                    ? stored.get("chairsCount").toString().equals("7")
                                    : ("Updated " + record.name())
                                            .equals(stored.get("englishName"));
                        });
        Assert.assertEquals(basicData().read(record).get("id"), record.id());
    }

    @Test
    @Story("رفض الحقول المطلوبة دون حفظ سجل")
    public void missingRequiredFieldCannotPersist() {
        newForm();
        String marker = ownedName(endpoint(), "required");
        fill(marker);
        if (table()) page.field("Chairs Count", "");
        else page.field("arabicName", "");
        var save = page.visibleElement(page.button("حفظ"));
        if (save.isEnabled()) {
            page.click("حفظ");
            Assert.assertTrue(page.validationVisible(), "Required-field feedback must be visible");
        } else Assert.assertFalse(save.isEnabled(), "Invalid tree account must not be savable");
        Assert.assertNull(basicData().captured(endpoint(), marker), "Invalid master was persisted");
    }

    @Test
    @Story("إلغاء الإنشاء لا يحفظ السجل")
    public void abandoningFormDoesNotSave() {
        newForm();
        String marker = ownedName(endpoint(), "abandon");
        fill(marker);
        if (tree()) open();
        else page.closeModal();
        Assert.assertNull(basicData().captured(endpoint(), marker));
    }

    @Test
    @Story("إلغاء الحذف ثم تأكيده يؤثران على السجل المملوك فقط")
    public void cancelAndConfirmDeleteOwnRecord() {
        var record = create();
        open();
        String row = table() ? basicData().read(record).get("code").toString() : record.name();
        if (tree()) {
            selectTreeRecord(record);
            page.click("حذف");
        } else page.rowAction(row, "delete");
        page.confirm(false);
        Assert.assertNotNull(basicData().read(record));
        if (tree()) page.click("حذف");
        else page.rowAction(row, "delete");
        page.confirm(true);
        new WebDriverWait(driver, Duration.ofSeconds(25))
                .until(d -> basicData().read(record) == null);
    }
}
