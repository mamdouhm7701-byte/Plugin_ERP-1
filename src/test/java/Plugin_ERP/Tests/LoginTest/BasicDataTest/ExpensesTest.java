package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.ExpensesPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExpensesTest extends BaseTest {
  private String unique(String suffix) {
    return "QA-EXP-" + suffix + "-" + System.currentTimeMillis();
  }

  private ExpensesPage open() {
    ExpensesPage p = new ExpensesPage(driver);
    p.NavigationHelper(Pages.EXPENSES);
    return p;
  }

  private void create(ExpensesPage p, String name, String english, String account, boolean shared) {
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
    for (String name : names) {
      if (name == null || name.isBlank()) continue;
      try {
        p.CloseFormIfOpen();
        p.DismissAlertIfPresent();
        if (p.GetExactMatchCount(name) == 0) continue;
        p.RequestDeleteByName(name);
        p.ConfirmDelete();
      } catch (Exception e) {
        System.err.println(
            "Could not clean owned Expense test row '" + name + "': " + e.getMessage());
      }
    }
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
      create(p, n, en, "تسويق جوجل", true);
      p.OpenForEditByName(n);
      Assert.assertEquals(p.GetArabicNameValue(), n);
      Assert.assertEquals(p.GetEnglishNameValue(), en);
      Assert.assertEquals(p.GetSelectedAccountValue(), "تسويق جوجل");
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
    String n = unique("EDIT"), u = n + "-UPDATED";
    try {
      create(p, n, "Edit expense", "تسويق جوجل", false);
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
    if (p.GetEditableRowCount() == 0) return;
    String n = p.GetFirstCellByHeader("اسم المصروف");
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
      create(p, n, null, "تسويق جوجل", false);
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
      create(p, n, "Refresh expense", "تسويق جوجل", true);
      driver.navigate().refresh();
      p = open();
      Assert.assertEquals(p.GetExactMatchCount(n), 1);
    } finally {
      cleanup(p, n);
    }
  }
}
