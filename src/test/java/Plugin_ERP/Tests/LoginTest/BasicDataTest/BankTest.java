/* شرح الملف: اختبارات البنوك: إنشاء بنك ومراجعة البيانات المحفوظة؛ تحميل بيانات البنك عند التعديل؛
 * إلغاء حذف سجل البنوك؛ رفض اسم عربي فارغ في البنوك؛ إلغاء تغيير الاسم في البنوك.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.BankPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BankTest extends BasicDataScenarioTest {
    private String uniqueName(String suffix) {
        return ownedName("Banks", suffix);
    }

    private BankPage openBanks() {
        BankPage page = new BankPage(driver);
        page.NavigationHelper(Pages.BANK);
        return page;
    }

    private void fillMinimum(BankPage page, String name) {
        page.ClickNew();
        page.AddArabicName(name);
    }

    private void createBank(BankPage page, String name) {
        fillMinimum(page, name);
        page.ClickSaveButton();
        page.SearchBank("");
        Assert.assertEquals(
                page.GetExactBankMatchCount(name),
                1,
                "يجب ظهور سجل البنك الفريد مرة واحدة: " + name);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-001 بنية قائمة البنوك")
    @Description("يتحقق من أعمدة الكود واسم البنك ورقم الحساب ووجود إجراءات الصفوف.")
    public void BK_001_BankListStructureIsDisplayed() {
        BankPage page = openBanks();
        createBank(page, uniqueName("LIST"));
        Assert.assertTrue(
                page.IsBankListStructureDisplayed(),
                "يجب ظهور أعمدة القائمة وإجراءات تعديل وحذف السجل");
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-002 حقول نموذج البنك")
    @Description("يتحقق من ظهور بيانات البنك وأزرار الحفظ والإغلاق في نموذج الإضافة.")
    public void BK_002_BankFormControlsAreDisplayed() {
        BankPage page = openBanks();
        page.ClickNew();
        Assert.assertTrue(
                page.GetMissingBankFormControls().isEmpty(),
                "حقول النموذج غير الظاهرة: " + page.GetMissingBankFormControls());
        page.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-014 إغلاق نموذج الإضافة دون حفظ")
    @Description("يتحقق من عدم إنشاء البنك عند إغلاق النموذج دون الحفظ.")
    public void BK_014_CloseFormWithoutSaving() {
        BankPage page = openBanks();
        String name = uniqueName("UNSAVED");
        fillMinimum(page, name);
        page.ClickCloseButton();
        Assert.assertEquals(page.GetExactBankMatchCount(name), 0);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-020 تحميل بيانات البنك عند التعديل")
    @Description("يتحقق من تحميل الاسم العربي ورقم الحساب من سجل الجدول المختار.")
    public void BK_020_EditFormLoadsValues() {
        BankPage page = openBanks();
        String name = uniqueName("LOAD");
        createBank(page, name);
        String account = "";
        page.OpenBankForEditByName(name);
        Assert.assertEquals(page.GetArabicNameValue(), name);
        Assert.assertEquals(page.GetBankAccountValue(), account);
        page.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-022 إلغاء تعديل البنك")
    @Description("يتحقق من تجاهل التغيير عند إغلاق نموذج التعديل دون حفظ.")
    public void BK_022_CancelEditDoesNotChangeBank() {
        BankPage page = openBanks();
        String original = uniqueName("CANCEL-ORIGINAL");
        createBank(page, original);
        page.OpenBankForEditByName(original);
        page.AddArabicName(uniqueName("EDIT-CANCEL"));
        page.ClickCloseButton();
        page.OpenBankForEditByName(original);
        Assert.assertEquals(page.GetArabicNameValue(), original);
        page.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-004 الاسم العربي إلزامي")
    @Description("يتحقق من منع الحفظ دون اسم عربي وعدم إضافة سجل جديد.")
    public void BK_004_ArabicNameIsRequired() {
        BankPage page = openBanks();
        long before = recordCount("Banks");
        page.ClickNew();
        page.AddNoteForDealer(uniqueName("INVALID-NAME"));
        page.ClickSaveButton();
        Assert.assertTrue(page.IsBankArabicNameRequiredDisplayed());
        Assert.assertEquals(recordCount("Banks"), before);
        page.ClickCloseButton();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-005 رفض الاسم المكون من مسافات")
    @Description("يتحقق من رفض المسافات فقط كاسم عربي للبنك.")
    public void BK_005_WhitespaceNameIsRejected() {
        BankPage page = openBanks();
        long before = recordCount("Banks");
        page.ClickNew();
        page.AddArabicName("   ");
        page.AddNoteForDealer(uniqueName("WHITESPACE"));
        page.ClickSaveButton();
        Assert.assertTrue(page.IsBankArabicNameRequiredDisplayed());
        Assert.assertEquals(recordCount("Banks"), before);
        page.ClickCloseButton();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-008 صيغة البريد الإلكتروني")
    @Description("يتحقق من رفض البريد الإلكتروني غير الصحيح دون إنشاء سجل.")
    public void BK_008_InvalidEmailIsRejected() {
        BankPage page = openBanks();
        String name = uniqueName("INVALID-EMAIL");
        long before = recordCount("Banks");
        page.ClickNew();
        page.AddArabicName(name);
        page.AddEmail("not-an-email");
        page.ClickSaveButton();
        Assert.assertTrue(page.IsInvalidBankEmailDisplayed(), "يجب إظهار خطأ صيغة البريد");
        Assert.assertEquals(recordCount("Banks"), before);
        page.ClickCloseButton();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-003 إنشاء بنك والتحقق من الحقول")
    @Description("ينشئ بنكًا اختباريًا ببيانات أساسية واختيارية ثم يتحقق من القيم بعد إعادة فتحه.")
    public void BK_003_CreateBankAndVerifySavedFields() {
        BankPage page = openBanks();
        String name = uniqueName("CREATE");
        String english = "Automation Bank " + System.currentTimeMillis();
        String email = "qa" + System.currentTimeMillis() + "@example.com";
        String iban = "SA" + System.currentTimeMillis();
        page.ClickNew();
        page.AddArabicName(name);
        page.AddEnglishName(english);
        page.AddEmail(email);
        page.AddAdress("Automation test address");
        page.AddAccountNumber("1234567890");
        page.AddIban(iban);
        page.AddNoteForDealer("Created by automated Bank test");
        page.ClickSaveButton();
        Assert.assertEquals(page.GetExactBankMatchCount(name), 1);
        page.OpenBankForEditByName(name);
        Assert.assertEquals(page.GetArabicNameValue(), name);
        Assert.assertEquals(page.GetEnglishNameValue(), english);
        Assert.assertEquals(page.GetEmailValue(), email);
        Assert.assertEquals(page.GetAddressValue(), "Automation test address");
        Assert.assertEquals(page.GetBankAccountValue(), "1234567890");
        Assert.assertEquals(page.GetIbanValue(), iban);
        Assert.assertEquals(page.GetNotesValue(), "Created by automated Bank test");
        Assert.assertFalse(page.GetBankCodeValue().isBlank());
        page.ClickCloseButton();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-007 الحقول الاختيارية")
    @Description("يتحقق من إمكانية حفظ بنك بالاسم العربي فقط.")
    public void BK_007_OptionalFieldsCanBeEmpty() {
        BankPage page = openBanks();
        String name = uniqueName("OPTIONAL");
        createBank(page, name);
        page.OpenBankForEditByName(name);
        Assert.assertEquals(page.GetArabicNameValue(), name);
        Assert.assertEquals(
                page.GetEnglishNameValue(),
                name,
                "واجهة البنك تملأ الاسم الإنجليزي تلقائيًا عند تركه فارغًا");
        Assert.assertTrue(page.GetEmailValue().isEmpty());
        Assert.assertTrue(page.GetAddressValue().isEmpty());
        Assert.assertTrue(page.GetBankAccountValue().isEmpty());
        Assert.assertTrue(page.GetIbanValue().isEmpty());
        page.ClickCloseButton();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-021 تعديل بنك مملوك للاختبار")
    @Description("يعدل سجلًا أنشأه الاختبار ثم يتحقق من ظهور الاسم الجديد ويحذف السجل بأمان.")
    public void BK_021_EditOwnedBank() {
        BankPage page = openBanks();
        String name = uniqueName("EDIT");
        String updated = uniqueName("UPDATED");
        createBank(page, name);
        page.OpenBankForEditByName(name);
        page.AddArabicName(updated);
        page.ClickSaveButton();
        Assert.assertEquals(page.GetExactBankMatchCount(updated), 1);
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-023 حذف سجل البنك المملوك")
    @Description("يحذف السجل الذي أنشأه الاختبار فقط ويتحقق من اختفائه.")
    public void BK_023_DeleteOwnedBank() {
        BankPage page = openBanks();
        String name = uniqueName("DELETE");
        createBank(page, name);
        page.RequestDeleteBankByName(name);
        Assert.assertTrue(page.IsBankDeleteConfirmationDisplayed());
        page.ConfirmBankDelete();
        Assert.assertEquals(page.GetExactBankMatchCount(name), 0);
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-024 إلغاء حذف البنك")
    @Description("يتحقق من بقاء السجل بعد إلغاء التأكيد ثم ينظفه بالاسم المطابق.")
    public void BK_024_CancelDeleteRetainsBank() {
        BankPage page = openBanks();
        String name = uniqueName("CANCEL-DELETE");
        createBank(page, name);
        page.RequestDeleteBankByName(name);
        Assert.assertTrue(page.IsBankDeleteConfirmationDisplayed());
        page.CancelBankDelete();
        Assert.assertEquals(page.GetExactBankMatchCount(name), 1);
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("BK-032 استمرار البنك بعد تحديث الصفحة")
    @Description("يتحقق من بقاء البنك الاختباري بعد تحديث المتصفح.")
    public void BK_032_BankPersistsAfterRefresh() {
        BankPage page = openBanks();
        String name = uniqueName("REFRESH");
        createBank(page, name);
        driver.navigate().refresh();
        page = openBanks();
        page.SearchBank("");
        Assert.assertEquals(page.GetExactBankMatchCount(name), 1);
    }
}
