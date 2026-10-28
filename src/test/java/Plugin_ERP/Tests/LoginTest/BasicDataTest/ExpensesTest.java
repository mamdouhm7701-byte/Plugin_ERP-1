/* شرح الملف: اختبارات المصروفات: إنشاء نوع مصروف ومراجعة البيانات المحفوظة؛ تعديل سجل اختبار؛ إلغاء
 * حذف سجل المصروفات؛ رفض اسم عربي فارغ في المصروفات؛ إلغاء تغيير الاسم في المصروفات.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.ExpensesPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExpensesTest extends BasicDataScenarioTest {
    private String unique(String suffix) {
        return ownedName("Expenses", suffix);
    }

    private ExpensesPage open() {
        // The screen loads account choices when mounted, before opening its form.
        basicData().account("expenses");
        ExpensesPage p = new ExpensesPage(driver);
        p.NavigationHelper(Pages.EXPENSES);
        return p;
    }

    private void create(
            ExpensesPage p, String name, String english, String account, boolean shared) {
        p.ClickNew();
        p.AddArabicName(name);
        if (english != null) p.AddEnglishName(english);
        if (account != null) p.SelectFromDropDawn(account, "رقم الحساب");
        p.SetExpenseShared(shared);
        p.ClickSaveButton();
        Assert.assertEquals(
                p.GetExactMatchCount(name), 1, "يجب ظهور المصروف الفريد مرة واحدة: " + name);
    }

    private void cleanup(ExpensesPage p, String... names) {
        // API owner cleanup runs even when UI assertions or modal handling fail.
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-001 بنية قائمة المصروفات")
    @Description("يتحقق من أعمدة الكود واسم المصروف ورقم الحساب وإجراءات الصفوف.")
    public void EX_001_ListStructure() {
        Assert.assertTrue(open().IsListStructureDisplayed());
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-002 عناصر نموذج المصروف")
    @Description("يتحقق من الحقول والأزرار الظاهرة في نموذج إضافة المصروف.")
    public void EX_002_FormControls() {
        ExpensesPage p = open();
        p.ClickNew();
        Assert.assertTrue(p.GetMissingControls().isEmpty(), p.GetMissingControls());
        p.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-014 إغلاق دون حفظ")
    @Description("يتحقق من عدم إنشاء المصروف عند إغلاق النموذج دون حفظ.")
    public void EX_014_CloseWithoutSave() {
        ExpensesPage p = open();
        String n = unique("UNSAVED");
        p.ClickNew();
        p.AddArabicName(n);
        p.ClickCloseButton();
        Assert.assertEquals(p.GetExactMatchCount(n), 0);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-004 الاسم العربي مطلوب")
    @Description("يتحقق من رفض النموذج الفارغ.")
    public void EX_004_ArabicNameRequired() {
        ExpensesPage p = open();
        int before = p.GetEditableRowCount();
        p.ClickNew();
        p.AddEnglishName(ownedName("Expenses", "required-name"));
        p.ClickSaveButton();
        Assert.assertTrue(p.IsArabicNameRequiredDisplayed());
        Assert.assertEquals(p.GetEditableRowCount(), before);
        p.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-005 رفض المسافات في الاسم")
    @Description("يتحقق من رفض الاسم العربي الذي يحتوي على مسافات فقط.")
    public void EX_005_WhitespaceRejected() {
        ExpensesPage p = open();
        int before = p.GetEditableRowCount();
        p.ClickNew();
        p.AddArabicName("   ");
        p.AddEnglishName(ownedName("Expenses", "invalid-name"));
        p.ClickSaveButton();
        Assert.assertTrue(p.IsArabicNameRequiredDisplayed());
        Assert.assertEquals(p.GetEditableRowCount(), before);
        p.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-003 إنشاء مصروف مع حساب وخيار عام")
    @Description("ينشئ مصروفًا ويقارن الاسم والحساب والخيار العام المحفوظة.")
    public void EX_003_CreateAndVerify() {
        ExpensesPage p = open();
        String n = unique("CREATE"), en = "Expense " + System.currentTimeMillis();
        try {
            create(p, n, en, basicData().account("expenses").name(), true);
            p.OpenForEditByName(n);
            Assert.assertEquals(p.GetArabicNameValue(), n);
            Assert.assertEquals(p.GetEnglishNameValue(), en);
            Assert.assertEquals(
                    p.GetSelectedAccountValue(), basicData().account("expenses").name());
            Assert.assertTrue(p.IsSharedChecked());
            Assert.assertFalse(p.GetCodeValue().isBlank());
            p.ClickCloseButton();
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-007 قبول الحقول الاختيارية")
    @Description("يتحقق من إمكانية الحفظ دون اسم إنجليزي أو حساب، مع إلغاء خيار عام.")
    public void EX_007_OptionalFields() {
        ExpensesPage p = open();
        String n = unique("OPTIONAL");
        try {
            create(p, n, null, null, false);
            p.OpenForEditByName(n);
            Assert.assertEquals(p.GetArabicNameValue(), n);
            Assert.assertEquals(p.GetEnglishNameValue(), n);
            Assert.assertTrue(p.GetSelectedAccountValue().isEmpty());
            Assert.assertFalse(p.IsSharedChecked());
            p.ClickCloseButton();
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-021 تعديل سجل اختبار")
    @Description("يعدل المصروف الذي أنشأه الاختبار ويتحقق من الاسم الجديد.")
    public void EX_021_EditOwned() {
        ExpensesPage p = open();
        String n = unique("EDIT"), u = unique("UPDATED");
        try {
            create(p, n, "Edit expense", basicData().account("expenses").name(), false);
            p.OpenForEditByName(n);
            p.AddArabicName(u);
            p.ClickSaveButton();
            Assert.assertEquals(p.GetExactMatchCount(u), 1);
        } finally {
            cleanup(p, u, n);
        }
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-022 إلغاء تعديل")
    @Description("يتحقق من عدم حفظ التغيير عند إغلاق نموذج التعديل.")
    public void EX_022_CancelEdit() {
        ExpensesPage p = open();
        String n = unique("CANCEL-EDIT-ORIGINAL");
        create(p, n, null, null, false);
        p.OpenForEditByName(n);
        p.AddArabicName(unique("CANCEL-EDIT"));
        p.ClickCloseButton();
        p.OpenForEditByName(n);
        Assert.assertEquals(p.GetArabicNameValue(), n);
        p.ClickCloseButton();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-023 حذف سجل اختبار")
    @Description("يحذف السجل الذي أنشأه الاختبار بالاسم المطابق فقط.")
    public void EX_023_DeleteOwned() {
        ExpensesPage p = open();
        String n = unique("DELETE");
        try {
            create(p, n, null, basicData().account("expenses").name(), false);
            p.RequestDeleteByName(n);
            Assert.assertTrue(p.IsDeleteConfirmationDisplayed());
            p.ConfirmDelete();
            Assert.assertEquals(p.GetExactMatchCount(n), 0);
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-024 إلغاء حذف")
    @Description("يتحقق من بقاء السجل بعد إلغاء الحذف ثم يحذفه كتنظيف.")
    public void EX_024_CancelDelete() {
        ExpensesPage p = open();
        String n = unique("CANCEL-DELETE");
        try {
            create(p, n, null, null, false);
            p.RequestDeleteByName(n);
            p.CancelDelete();
            Assert.assertEquals(p.GetExactMatchCount(n), 1);
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع المصروفات")
    @Story("EX-032 استمرار المصروف بعد التحديث")
    @Description("يتحقق من استمرار سجل الاختبار بعد تحديث الصفحة.")
    public void EX_032_PersistAfterRefresh() {
        ExpensesPage p = open();
        String n = unique("REFRESH");
        try {
            create(p, n, "Refresh expense", basicData().account("expenses").name(), true);
            driver.navigate().refresh();
            p = open();
            Assert.assertEquals(p.GetExactMatchCount(n), 1);
        } finally {
            cleanup(p, n);
        }
    }
}
