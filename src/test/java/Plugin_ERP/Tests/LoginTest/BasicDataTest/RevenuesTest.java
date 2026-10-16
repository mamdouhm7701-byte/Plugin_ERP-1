package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.RevenuesPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RevenuesTest extends BaseTest {
  private String unique(String s) {
    return "QA-REV-" + s + "-" + System.currentTimeMillis();
  }

  private RevenuesPage open() {
    RevenuesPage p = new RevenuesPage(driver);
    p.NavigationHelper(Pages.REVENUES);
    return p;
  }

  private void create(RevenuesPage p, String n, String en, String account, boolean general) {
    p.ClickNew();
    p.AddArabicName(n);
    if (en != null) p.AddEnglishName(en);
    if (account != null) p.SelectFromDropDawn(account, "رقم الحساب");
    p.SetRevenueShared(general);
    p.ClickSaveButton();
    Assert.assertEquals(p.GetExactMatchCount(n), 1, "يجب ظهور الإيراد مرة واحدة: " + n);
  }

  private void cleanup(RevenuesPage p, String... ns) {
    for (String n : ns)
      if (n != null && !n.isBlank())
        try {
          p.CloseFormIfOpen();
          p.DismissAlertIfPresent();
          if (p.GetExactMatchCount(n) > 0) {
            p.RequestDeleteByName(n);
            p.ConfirmDelete();
          }
        } catch (Exception e) {
          System.err.println("Could not clean owned revenue '" + n + "': " + e.getMessage());
        }
  }

  @Test(priority = 0)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.CRITICAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-001 بنية القائمة")
  @Description("يتحقق من أعمدة قائمة الإيرادات وإجراءات الصفوف.")
  public void REV_001_ListStructure() {
    Assert.assertTrue(open().IsListStructureDisplayed());
  }

  @Test(priority = 1)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.CRITICAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-002 عناصر النموذج")
  @Description("يتحقق من عناصر نموذج الإيراد الظاهرة.")
  public void REV_002_FormControls() {
    RevenuesPage p = open();
    p.ClickNew();
    Assert.assertTrue(p.GetMissingControls().isEmpty(), p.GetMissingControls());
    p.ClickCloseButton();
  }

  @Test(priority = 2)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.NORMAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-014 إغلاق دون حفظ")
  @Description("يتحقق من عدم إنشاء الإيراد عند إغلاق النموذج.")
  public void REV_014_CloseWithoutSave() {
    RevenuesPage p = open();
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
  @Feature("أنواع الإيرادات")
  @Story("REV-004 الاسم العربي مطلوب")
  @Description("يتحقق من رفض الحفظ دون اسم عربي.")
  public void REV_004_ArabicNameRequired() {
    RevenuesPage p = open();
    p.ClickNew();
    p.ClickSaveButton();
    Assert.assertTrue(p.IsArabicNameRequiredDisplayed());
    p.ClickCloseButton();
  }

  @Test(priority = 4)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.NORMAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-005 رفض المسافات")
  @Description("يتحقق من رفض اسم مكون من مسافات.")
  public void REV_005_WhitespaceRejected() {
    RevenuesPage p = open();
    p.ClickNew();
    p.AddArabicName("   ");
    p.ClickSaveButton();
    Assert.assertTrue(p.IsArabicNameRequiredDisplayed());
    p.ClickCloseButton();
  }

  @Test(priority = 5)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.CRITICAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-003 إنشاء والتحقق")
  @Description("ينشئ إيرادًا ويتحقق من الاسم والحساب وخيار عام بعد إعادة فتحه.")
  public void REV_003_CreateAndVerify() {
    RevenuesPage p = open();
    String n = unique("CREATE"), en = "Revenue " + System.currentTimeMillis();
    try {
      create(p, n, en, "تسويق جوجل", true);
      p.OpenForEditByName(n);
      Assert.assertEquals(p.GetArabicNameValue(), n);
      Assert.assertEquals(p.GetEnglishNameValue(), en);
      Assert.assertEquals(p.GetSelectedAccountValue(), "تسويق جوجل");
      Assert.assertTrue(p.IsRevenueSharedChecked());
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
  @Feature("أنواع الإيرادات")
  @Story("REV-007 الحقول الاختيارية")
  @Description("يتحقق من إمكانية الحفظ دون اسم إنجليزي أو حساب وإلغاء خيار عام.")
  public void REV_007_OptionalFields() {
    RevenuesPage p = open();
    String n = unique("OPTIONAL");
    try {
      create(p, n, null, null, false);
      p.OpenForEditByName(n);
      Assert.assertEquals(p.GetArabicNameValue(), n);
      Assert.assertTrue(p.GetEnglishNameValue().isEmpty() || p.GetEnglishNameValue().equals(n));
      Assert.assertTrue(p.GetSelectedAccountValue().isEmpty());
      Assert.assertFalse(p.IsRevenueSharedChecked());
      p.ClickCloseButton();
    } finally {
      cleanup(p, n);
    }
  }

  @Test(priority = 7)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.CRITICAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-021 تعديل سجل الاختبار")
  @Description("يعدل السجل الذي أنشأه الاختبار فقط.")
  public void REV_021_EditOwned() {
    RevenuesPage p = open();
    String n = unique("EDIT"), u = n + "-UPDATED";
    try {
      create(p, n, "Edit revenue", "تسويق جوجل", false);
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
  @Feature("أنواع الإيرادات")
  @Story("REV-022 إلغاء تعديل")
  @Description("يتحقق من عدم حفظ التعديل عند إغلاق النموذج.")
  public void REV_022_CancelEdit() {
    RevenuesPage p = open();
    String n = unique("CANCEL-EDIT");
    try {
      create(p, n, null, null, false);
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

  @Test(priority = 9)
  @Owner("MAMDOUH")
  @Severity(SeverityLevel.CRITICAL)
  @Epic("Web Cloud")
  @Feature("أنواع الإيرادات")
  @Story("REV-023 حذف سجل الاختبار")
  @Description("يحذف السجل المطابق الذي أنشأه الاختبار.")
  public void REV_023_DeleteOwned() {
    RevenuesPage p = open();
    String n = unique("DELETE");
    try {
      create(p, n, null, null, false);
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
  @Feature("أنواع الإيرادات")
  @Story("REV-024 إلغاء الحذف")
  @Description("يتحقق من بقاء السجل عند إلغاء الحذف.")
  public void REV_024_CancelDelete() {
    RevenuesPage p = open();
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
  @Feature("أنواع الإيرادات")
  @Story("REV-032 استمرار بعد التحديث")
  @Description("يتحقق من بقاء الإيراد بعد تحديث الصفحة.")
  public void REV_032_PersistAfterRefresh() {
    RevenuesPage p = open();
    String n = unique("REFRESH");
    try {
      create(p, n, "Refresh revenue", "تسويق جوجل", true);
      driver.navigate().refresh();
      p = open();
      Assert.assertEquals(p.GetExactMatchCount(n), 1);
    } finally {
      cleanup(p, n);
    }
  }
}
