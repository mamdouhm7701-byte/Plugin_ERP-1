package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.TreasuryHandoverPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import TestSupport.BasicDataScenarioTest;
import TestSupport.BasicDataFixtures;
import TestSupport.TreasuryFixtures;
import org.testng.Assert;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class TreasuryHandoverTest extends BasicDataScenarioTest {

    private TreasuryFixtures treasury;
    private TreasuryFixtures treasury() {
        if (treasury == null) treasury = new TreasuryFixtures(driver);
        return treasury;
    }

    @AfterClass(alwaysRun = true)
    public void cleanTreasuryHandovers() {
        if (treasury == null) return;
        try { treasury.close(); basicData().preserveAuditHistory(); }
        catch (RuntimeException | AssertionError failure) {
            basicData().retainForRecovery("Owned treasury handover could not be closed and reviewed");
            throw failure;
        }
    }

    private void submit(TreasuryHandoverPage page, BasicDataFixtures.Record safe, BasicDataFixtures.Record user) {
        treasury().expect(safe, user);
        page.NavigationHelper(Pages.SAFETRANSACTIONS);
        page.ClickNew();
        page.selectTreasuryHandoverDropdown("المستخدم", user.values().get("userName").toString());
        page.selectTreasuryHandoverDropdown("خزينة", safe.name());
        page.AddOpenBalance(1000);
        page.ClickSaveButton();
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسليم الخزينة")
    @Story("إضافة تسليم خزينة ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من إضافة تسليم خزينة جديد ببيانات صحيحة، ويشمل اختيار"
                    + " المستخدم والخزينة وإدخال الرصيد الافتتاحي، وحفظ البيانات والتحقق من إضافة السجل"
                    + " بنجاح.")
    @Link(name = "Test Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidTreasuryHandoverTest() {
        var user = basicData().user("handover-create");
        var safe = basicData().safe(1);
        submit(new TreasuryHandoverPage(driver), safe, user);
        var row = treasury().awaitSingle(safe,user);
        Assert.assertEquals(new java.math.BigDecimal(row.get("openingBalance").toString()).compareTo(new java.math.BigDecimal("1000")), 0, "مطابقة الرصيد الافتتاحي");
        Assert.assertEquals(row.get("isClosed"), false, "مطابقة حالة إغلاق الخزينة");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تسليم الخزينة")
    @Story("التحقق - خطأ عند تكرار تسليم خزينة لنفس المستخدم")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إنشاء تسليم خزينة لنفس المستخدم المفتوح"
                    + " مسبقاً، ويتحقق من ظهور رسالة خطأ تفيد بعدم إمكانية تكرار التسليم لنفس المستخدم.")
    @Link(name = "Test Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidTreasuryHandoverTest() {
        var user = basicData().user("handover-duplicate");
        var safe = basicData().safe(1);
        treasury().open(safe,user,1000);
        TreasuryHandoverPage page = new TreasuryHandoverPage(driver);
        submit(page,safe,user);
        Assert.assertTrue(page.isErrorPopupDisplayed(), "An already open owned handover must reject duplication");
        Assert.assertEquals(treasury().requireSingle(safe,user).get("isClosed"), false, "مطابقة حالة إغلاق الخزينة");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسليم الخزينة")
    @Story("فتح تسليم الخزينة للمراجعة والتحقق من الرصيد في التقرير")
    @Description(
            "يتحقق هذا الاختبار من فتح تسليم الخزينة للمراجعة، والحصول على إجمالي الرصيد عند"
                    + " الإغلاق، وحفظ وطباعة التقرير، والتحقق من تطابق إجمالي الرصيد في التقرير مع"
                    + " البيانات المدخلة.")
    @Link(name = "Test Link", url = "https://binfares.plugin-erp.com/#/login")
    public void OpenTreasuryHandover() {
        var user = basicData().user("handover-report");
        var safe = basicData().safe(1);
        Object id = treasury().open(safe,user,1000);
        TreasuryHandoverPage page = new TreasuryHandoverPage(driver);
        page.OpenTreasuryForReview(id);
        var totals = treasury().totals(id);
        var amounts = TestSupport.TreasuryAmounts.from(totals, java.math.BigDecimal.ZERO);
        Allure.addAttachment("Treasury transaction ledger", "application/json",
                new org.json.JSONObject(totals).toString(), ".json");
        page.AssertClosingAmounts(amounts.cash(), amounts.total(), amounts.actual(), amounts.deficit());
        page.SaveAndPrint();
        page.AssertPrintedAmounts(amounts.cash(), amounts.total(), amounts.actual(), amounts.deficit());
        driver.close();
        driver.switchTo().window(driver.getWindowHandles().iterator().next());
        Assert.assertEquals(treasury().requireSingle(safe,user).get("isClosed"), true, "مطابقة حالة إغلاق الخزينة");
    }
}
