package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.PaymentPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PaymentTest extends BaseTest {
    private String unique(String suffix) {
        return "QA-PAY-" + suffix + "-" + System.currentTimeMillis();
    }

    private PaymentPage open() {
        PaymentPage p = new PaymentPage(driver);
        p.NavigationHelper(Pages.PAYMENT);
        return p;
    }

    private void create(
            PaymentPage p,
            String name,
            String english,
            String bank,
            boolean general,
            boolean integration) {
        p.ClickNew();
        p.AddArabicName(name);
        if (english != null) p.AddEnglishName(english);
        if (bank != null) p.SelectFromDropDawn(bank, "اسم البنك");
        p.SetPaymentCheckbox("عام", general);
        p.SetPaymentCheckbox("طريقة دفع افتراضية للتكامل", integration);
        p.ClickSaveButton();
        Assert.assertEquals(
                p.GetExactMatchCount(name), 1, "يجب ظهور طريقة الدفع مرة واحدة: " + name);
    }

    private void cleanup(PaymentPage p, String... names) {
        for (String n : names)
            if (n != null && !n.isBlank())
                try {
                    p.CloseFormIfOpen();
                    p.DismissAlertIfPresent();
                    if (p.GetExactMatchCount(n) > 0) {
                        p.RequestDeleteByName(n);
                        p.ConfirmDelete();
                    }
                } catch (Exception e) {
                    System.err.println(
                            "Could not clean owned payment '" + n + "': " + e.getMessage());
                }
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-001 بنية قائمة طرق الدفع")
    @Description("يتحقق من ظهور أعمدة القائمة والسجل الموجود وإجراءات الصفوف.")
    public void PAY_001_ListStructure() {
        Assert.assertTrue(open().IsListStructureDisplayed());
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-002 عناصر نموذج طريقة الدفع")
    @Description("يتحقق من ظهور حقول الاسم والبنك والملاحظات وخياري التحديد وأزرار النموذج.")
    public void PAY_002_FormControls() {
        PaymentPage p = open();
        p.ClickNew();
        Assert.assertTrue(p.GetMissingControls().isEmpty(), p.GetMissingControls());
        p.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-014 إغلاق دون حفظ")
    @Description("يتحقق من أن إغلاق النموذج لا ينشئ طريقة دفع.")
    public void PAY_014_CloseWithoutSave() {
        PaymentPage p = open();
        String n = unique("UNSAVED");
        p.ClickNew();
        p.AddArabicName(n);
        p.ClickCloseButton();
        Assert.assertEquals(p.GetExactMatchCount(n), 0);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-004 الاسم العربي مطلوب")
    @Description("يتحقق من رفض الحفظ دون اسم عربي.")
    public void PAY_004_ArabicNameRequired() {
        PaymentPage p = open();
        p.ClickNew();
        p.ClickSaveButton();
        Assert.assertTrue(p.IsArabicNameRequiredErrorDisplayed());
        p.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-005 البنك مطلوب")
    @Description("يتحقق من رفض الحفظ دون اختيار بنك.")
    public void PAY_005_BankRequired() {
        PaymentPage p = open();
        p.ClickNew();
        p.AddArabicName(unique("NO-BANK"));
        p.ClickSaveButton();
        Assert.assertTrue(p.IsBankRequiredErrorDisplayed());
        p.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-006 رفض المسافات في الاسم")
    @Description("يتحقق من رفض الاسم العربي المكون من مسافات فقط.")
    public void PAY_006_WhitespaceRejected() {
        PaymentPage p = open();
        p.ClickNew();
        p.AddArabicName("   ");
        p.SelectFromDropDawn("البنك الاول", "اسم البنك");
        p.ClickSaveButton();
        Assert.assertTrue(p.IsArabicNameRequiredErrorDisplayed());
        p.ClickCloseButton();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-003 إنشاء والتحقق من طريقة دفع")
    @Description("ينشئ طريقة دفع ويتحقق من بياناتها وخياراتها عند إعادة فتحها.")
    public void PAY_003_CreateAndVerify() {
        PaymentPage p = open();
        String n = unique("CREATE"), en = "Payment " + System.currentTimeMillis();
        try {
            create(p, n, en, "البنك الاول", true, true);
            p.OpenForEditByName(n);
            Assert.assertEquals(p.GetArabicNameValue(), n);
            Assert.assertEquals(p.GetEnglishNameValue(), en);
            Assert.assertEquals(p.GetSelectedBankValue(), "البنك الاول");
            Assert.assertTrue(p.IsPaymentCheckboxChecked("عام"));
            Assert.assertTrue(p.IsPaymentCheckboxChecked("طريقة دفع افتراضية للتكامل"));
            p.ClickCloseButton();
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-007 قبول الاسم الإنجليزي الاختياري")
    @Description("يتحقق من إمكانية الحفظ مع الاسم العربي والبنك فقط.")
    public void PAY_007_OptionalEnglishName() {
        PaymentPage p = open();
        String n = unique("OPTIONAL");
        try {
            create(p, n, null, "البنك الاول", false, false);
            p.OpenForEditByName(n);
            Assert.assertEquals(p.GetArabicNameValue(), n);
            Assert.assertTrue(
                    p.GetEnglishNameValue().isEmpty() || p.GetEnglishNameValue().equals(n));
            Assert.assertEquals(p.GetSelectedBankValue(), "البنك الاول");
            Assert.assertFalse(p.IsPaymentCheckboxChecked("عام"));
            Assert.assertFalse(p.IsPaymentCheckboxChecked("طريقة دفع افتراضية للتكامل"));
            p.ClickCloseButton();
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-021 تعديل طريقة دفع مملوكة للاختبار")
    @Description("يعدل السجل الذي أنشأه الاختبار ويتحقق من الاسم الجديد.")
    public void PAY_021_EditOwned() {
        PaymentPage p = open();
        String n = unique("EDIT"), u = n + "-UPDATED";
        try {
            create(p, n, "Edit payment", "البنك الاول", true, true);
            p.OpenForEditByName(n);
            p.AddArabicName(u);
            p.SelectFromDropDawn("بنك اتوميشن", "اسم البنك");
            p.SetPaymentCheckbox("عام", false);
            p.SetPaymentCheckbox("طريقة دفع افتراضية للتكامل", false);
            p.ClickSaveButton();
            Assert.assertEquals(p.GetExactMatchCount(u), 1);
        } finally {
            cleanup(p, u, n);
        }
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-022 إلغاء تعديل")
    @Description("يتحقق من عدم حفظ تغيير غير مؤكد في سجل الاختبار.")
    public void PAY_022_CancelEdit() {
        PaymentPage p = open();
        String n = unique("CANCEL-EDIT");
        try {
            create(p, n, null, "البنك الاول", false, false);
            p.OpenForEditByName(n);
            p.AddArabicName(n + "-TEMP");
            p.ClickCloseButton();
            p.OpenForEditByName(n);
            Assert.assertEquals(p.GetArabicNameValue(), n);
            p.ClickCloseButton();
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-023 حذف طريقة دفع مملوكة للاختبار")
    @Description("يحذف السجل المطابق الذي أنشأه الاختبار فقط.")
    public void PAY_023_DeleteOwned() {
        PaymentPage p = open();
        String n = unique("DELETE");
        create(p, n, null, "البنك الاول", false, false);
        p.RequestDeleteByName(n);
        Assert.assertTrue(p.IsDeleteConfirmationDisplayed());
        p.ConfirmDelete();
        Assert.assertEquals(p.GetExactMatchCount(n), 0);
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-024 إلغاء الحذف")
    @Description("يتحقق من بقاء طريقة الدفع عند إلغاء رسالة التأكيد.")
    public void PAY_024_CancelDelete() {
        PaymentPage p = open();
        String n = unique("CANCEL-DELETE");
        try {
            create(p, n, null, "البنك الاول", false, false);
            p.RequestDeleteByName(n);
            p.CancelDelete();
            Assert.assertEquals(p.GetExactMatchCount(n), 1);
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("PAY-032 استمرار بعد تحديث الصفحة")
    @Description("يتحقق من استمرار طريقة الدفع بعد إعادة تحميل الشاشة.")
    public void PAY_032_PersistAfterRefresh() {
        PaymentPage p = open();
        String n = unique("REFRESH");
        try {
            create(p, n, "Refresh payment", "البنك الاول", false, true);
            driver.navigate().refresh();
            p = open();
            Assert.assertEquals(p.GetExactMatchCount(n), 1);
        } finally {
            cleanup(p, n);
        }
    }
}
