package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.DistributionEntityPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DisEntTest extends BaseTest {
    private String uniqueName(String suffix) {
        return "QA-DIST-" + suffix + "-" + System.currentTimeMillis();
    }

    private DistributionEntityPage openPage() {
        DistributionEntityPage page = new DistributionEntityPage(driver);
        page.NavigationHelper(Pages.DISTRIBUTION);
        return page;
    }

    private void create(DistributionEntityPage page, String name, String english) {
        page.ClickNew();
        page.AddArabicName(name);
        if (english != null) page.AddEnglishName(english);
        page.ClickSaveButton();
        Assert.assertEquals(
                page.GetExactMatchCount(name), 1, "يجب ظهور سجل الاختبار مرة واحدة: " + name);
    }

    private void cleanup(DistributionEntityPage page, String... names) {
        for (String name : names) {
            if (name == null || name.isBlank()) continue;
            try {
                page.CloseFormIfOpen();
                page.Search("");
                if (page.GetExactMatchCount(name) == 0) continue;
                page.RequestDeleteByName(name);
                page.ConfirmDelete();
            } catch (Exception e) {
                System.err.println(
                        "Could not clean owned Distribution Entity '"
                                + name
                                + "': "
                                + e.getMessage());
            }
        }
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-001 بنية قائمة جهات الصرف")
    @Description("يتحقق من ظهور الكود واسم جهة الصرف وإجراءات الصفوف.")
    public void DE_001_ListStructureIsDisplayed() {
        Assert.assertTrue(openPage().IsListStructureDisplayed());
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-002 حقول نموذج جهة الصرف")
    @Description("يتحقق من ظهور الاسم العربي والإنجليزي ورقم الحساب والحفظ والإغلاق.")
    public void DE_002_FormControlsAreDisplayed() {
        DistributionEntityPage page = openPage();
        page.ClickNew();
        Assert.assertTrue(
                page.GetMissingFormControls().isEmpty(),
                "عناصر النموذج غير الظاهرة: " + page.GetMissingFormControls());
        page.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-014 إغلاق نموذج الإضافة دون حفظ")
    @Description("يتحقق من أن إغلاق النموذج دون حفظ لا ينشئ جهة صرف.")
    public void DE_014_CloseWithoutSaving() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("UNSAVED");
        page.ClickNew();
        page.AddArabicName(name);
        page.ClickCloseButton();
        Assert.assertEquals(page.GetExactMatchCount(name), 0);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-004 الاسم العربي إلزامي")
    @Description("يتحقق من رفض النموذج الفارغ وعدم إنشاء صف جديد.")
    public void DE_004_ArabicNameIsRequired() {
        DistributionEntityPage page = openPage();
        int before = page.GetEditableRowCount();
        page.ClickNew();
        page.ClickSaveButton();
        Assert.assertTrue(page.IsArabicNameRequiredDisplayed());
        Assert.assertEquals(page.GetEditableRowCount(), before);
        page.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-005 رفض الاسم العربي المكون من مسافات")
    @Description("يتحقق من رفض المسافات فقط في حقل الاسم العربي.")
    public void DE_005_WhitespaceNameIsRejected() {
        DistributionEntityPage page = openPage();
        int before = page.GetEditableRowCount();
        page.ClickNew();
        page.AddArabicName("   ");
        page.ClickSaveButton();
        Assert.assertTrue(page.IsArabicNameRequiredDisplayed());
        Assert.assertEquals(page.GetEditableRowCount(), before);
        page.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-003 إنشاء جهة صرف والتحقق من الحقول")
    @Description("ينشئ جهة صرف ويتحقق من الاسم العربي والإنجليزي والكود بعد إعادة فتح السجل.")
    public void DE_003_CreateAndVerifyEntity() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("CREATE");
        String english = "Distribution " + System.currentTimeMillis();
        try {
            create(page, name, english);
            page.OpenForEditByName(name);
            Assert.assertEquals(page.GetArabicNameValue(), name);
            Assert.assertEquals(page.GetEnglishNameValue(), english);
            Assert.assertFalse(page.GetCodeValue().isBlank());
            page.ClickCloseButton();
        } finally {
            cleanup(page, name);
        }
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-007 قبول رقم الحساب الاختياري")
    @Description("يتحقق من حفظ جهة صرف دون اختيار رقم حساب، كما شوهد في واجهة Demo.")
    public void DE_007_AccountCanBeOmitted() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("NO-ACCOUNT");
        try {
            create(page, name, null);
            page.OpenForEditByName(name);
            Assert.assertEquals(page.GetArabicNameValue(), name);
            Assert.assertEquals(page.GetEnglishNameValue(), name);
            Assert.assertTrue(
                    page.GetSelectedAccountValue().isEmpty(), "يجب ألا يكون هناك حساب محدد");
            page.ClickCloseButton();
        } finally {
            cleanup(page, name);
        }
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-021 تعديل جهة اختبار محفوظة")
    @Description("يعدل جهة صرف أنشأها الاختبار ويتحقق من الاسم الجديد ثم يحذف سجل الاختبار بأمان.")
    public void DE_021_EditOwnedEntity() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("EDIT");
        String updated = name + "-UPDATED";
        try {
            create(page, name, "Edit entity");
            page.OpenForEditByName(name);
            page.AddArabicName(updated);
            page.ClickSaveButton();
            Assert.assertEquals(page.GetExactMatchCount(updated), 1);
        } finally {
            cleanup(page, updated, name);
        }
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-022 إلغاء تعديل جهة الصرف")
    @Description("يتحقق من تجاهل التعديل عند إغلاق النموذج دون حفظ.")
    public void DE_022_CancelEditRetainsOriginal() {
        DistributionEntityPage page = openPage();
        String name = page.GetFirstCellByHeader("اسم جهة الصرف");
        page.OpenForEditByName(name);
        page.AddArabicName(uniqueName("CANCEL-EDIT"));
        page.ClickCloseButton();
        page.OpenForEditByName(name);
        Assert.assertEquals(page.GetArabicNameValue(), name);
        page.ClickCloseButton();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-023 حذف جهة صرف مملوكة للاختبار")
    @Description("يحذف سجل الاختبار المحدد بالاسم فقط ويتحقق من اختفائه.")
    public void DE_023_DeleteOwnedEntity() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("DELETE");
        try {
            create(page, name, null);
            page.RequestDeleteByName(name);
            Assert.assertTrue(page.IsDeleteConfirmationDisplayed());
            page.ConfirmDelete();
            Assert.assertEquals(page.GetExactMatchCount(name), 0);
        } finally {
            cleanup(page, name);
        }
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-024 إلغاء حذف جهة الصرف")
    @Description("يتحقق من بقاء جهة الصرف بعد إلغاء الحذف ثم ينظفها بالاسم المطابق.")
    public void DE_024_CancelDeleteRetainsEntity() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("CANCEL-DELETE");
        try {
            create(page, name, null);
            page.RequestDeleteByName(name);
            Assert.assertTrue(page.IsDeleteConfirmationDisplayed());
            page.CancelDelete();
            Assert.assertEquals(page.GetExactMatchCount(name), 1);
        } finally {
            cleanup(page, name);
        }
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("DE-032 استمرار الجهة بعد تحديث الصفحة")
    @Description("يتحقق من استمرار سجل جهة الصرف بعد تحديث المتصفح.")
    public void DE_032_PersistsAfterRefresh() {
        DistributionEntityPage page = openPage();
        String name = uniqueName("REFRESH");
        try {
            create(page, name, "Refresh entity");
            driver.navigate().refresh();
            page = openPage();
            Assert.assertEquals(page.GetExactMatchCount(name), 1);
        } finally {
            cleanup(page, name);
        }
    }
}
