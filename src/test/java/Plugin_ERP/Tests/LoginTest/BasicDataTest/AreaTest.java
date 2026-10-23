package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
import TestSupport.BasicDataScenarioTest;
import static TestSupport.BasicDataFixtures.fields;
import io.qameta.allure.*;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AreaTest extends BasicDataScenarioTest {
    private String uniqueName(String caseId) {
        return ownedName("Areas", caseId);
    }

    private AreaPage openAreas() {
        AreaPage page = new AreaPage(driver);
        page.NavigationHelper(Pages.AREA);
        return page;
    }

    private void createArea(AreaPage page, String arabicName, String englishName, String notes) {
        page.ClickNew();
        page.AddArabicName(arabicName);
        if (englishName != null) page.AddEnglishName(englishName);
        if (notes != null) page.AddNoteForDealer(notes);
        page.ClickSaveButton();
        page.SearchArea(arabicName);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(arabicName),
                1,
                "يجب أن يظهر سجل الاختبار الفريد مرة واحدة: " + arabicName);
    }

    private void seedAreas(String key, int count) {
        for (int i = 0; i < count; i++) {
            basicData().fixture(key + "-" + i, "Areas", fields());
        }
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-001 عرض بنية قائمة المناطق")
    @Description(
            "يتحقق من ظهور عمودي الكود واسم المنطقة وإجراءات التعديل والحذف للصفوف القابلة"
                    + " للتحرير.")
    public void AR_001_AreaListStructureIsDisplayed() {
        AreaPage page = openAreas();
        createArea(page, uniqueName("LIST"), null, null);
        Assert.assertTrue(
                page.IsAreaListStructureDisplayed(),
                "AR-001: يجب ظهور أعمدة وإجراءات قائمة المناطق");
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-002 فتح نموذج إضافة منطقة")
    @Description("يتحقق من ظهور الكود والاسم العربي والإنجليزي والملاحظات وأزرار الحفظ والإغلاق.")
    public void AR_002_AreaCreationFormControlsAreDisplayed() {
        AreaPage page = openAreas();
        page.ClickNew();
        Assert.assertTrue(
                page.GetMissingAreaFormControls().isEmpty(),
                "AR-002: عناصر النموذج غير الظاهرة: " + page.GetMissingAreaFormControls());
        page.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-014 إغلاق نموذج الإضافة دون حفظ")
    @Description("يتحقق من أن إغلاق النموذج بعد إدخال اسم مؤقت لا ينشئ منطقة.")
    public void AR_014_CloseCreationFormWithoutSaving() {
        AreaPage page = openAreas();
        String name = uniqueName("UNSAVED");
        page.ClickNew();
        page.AddArabicName(name);
        page.ClickCloseButton();
        page.SearchArea(name);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name), 0, "AR-014: لا يجب إنشاء سجل دون الضغط على حفظ");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-020 تحميل بيانات المنطقة الحالية عند التعديل")
    @Description(
            "يقارن كود المنطقة والاسم العربي والإنجليزي والملاحظات في النموذج بقيم الصف"
                    + " المختار.")
    public void AR_020_EditFormLoadsCurrentAreaValues() {
        AreaPage page = openAreas();
        String name = uniqueName("READ");
        createArea(page, name, null, null);
        String code = page.GetAreaCellByName(name, "الكود");
        page.EditAreaByName(name);
        Assert.assertEquals(page.GetAreaCodeInputValue(), code, "يجب تحميل الكود الحالي");
        Assert.assertEquals(page.GetArabicNameInputValue(), name, "يجب تحميل الاسم العربي الحالي");
        page.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-022 إلغاء التعديل دون حفظ")
    @Description(
            "يغير الاسم داخل نموذج منطقة موجودة ثم يغلقه دون حفظ ويتأكد من بقاء القيمة"
                    + " الأصلية.")
    public void AR_022_CancelAreaEditWithoutSaving() {
        AreaPage page = openAreas();
        String original = uniqueName("CANCEL-ORIGINAL");
        createArea(page, original, null, null);
        page.EditAreaByName(original);
        page.AddArabicName(uniqueName("EDIT-CANCEL"));
        page.ClickCloseButton();
        page.EditAreaByName(original);
        Assert.assertEquals(
                page.GetArabicNameInputValue(), original, "يجب تجاهل التعديل غير المحفوظ");
        page.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-004 الاسم العربي إلزامي")
    @Description("يتحقق من منع حفظ المنطقة دون اسم عربي وظهور رسالة الإلزامية وعدم إضافة صف جديد.")
    public void AR_004_ArabicNameIsRequired() {
        AreaPage page = openAreas();
        int before = page.GetVisibleAreaRowCount();
        page.ClickNew();
        page.AddNoteForDealer(uniqueName("INVALID-NAME"));
        page.ClickSaveButton();
        Assert.assertTrue(
                page.IsAreaArabicNameRequiredDisplayed(), "يجب إظهار رسالة الاسم العربي مطلوب");
        Assert.assertEquals(page.GetVisibleAreaRowCount(), before, "لا يجب إنشاء سجل دون اسم عربي");
        page.ClickCloseButton();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-005 رفض الاسم العربي المكوّن من مسافات")
    @Description("يتحقق من أن المسافات فقط لا تعتبر اسمًا صالحًا للمنطقة.")
    public void AR_005_WhitespaceNameIsRejected() {
        AreaPage page = openAreas();
        int before = page.GetVisibleAreaRowCount();
        page.ClickNew();
        page.AddArabicName("   ");
        page.AddNoteForDealer(uniqueName("WHITESPACE"));
        page.ClickSaveButton();
        Assert.assertTrue(
                page.IsAreaArabicNameRequiredDisplayed(), "يجب رفض الاسم المكوّن من مسافات");
        Assert.assertEquals(page.GetVisibleAreaRowCount(), before, "لا يجب إنشاء صف للمسافات فقط");
        page.ClickCloseButton();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-013 تصحيح الاسم بعد ظهور رسالة الإلزامية")
    @Description(
            "يتحقق من إمكانية استكمال الاسم بعد رسالة الخطأ ثم حفظ المنطقة والتحقق من"
                    + " استمرارها بعد التحديث.")
    public void AR_013_CorrectArabicNameAfterValidationError() {
        AreaPage page = openAreas();
        String name = uniqueName("CORRECT");
        page.ClickNew();
        page.AddNoteForDealer(uniqueName("INVALID-NAME"));
        page.ClickSaveButton();
        Assert.assertTrue(
                page.IsAreaArabicNameRequiredDisplayed(), "يجب إظهار الخطأ قبل التصحيح");
        page.AddArabicName(name);
        page.AddEnglishName("Corrected area");
        page.ClickSaveButton();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
        driver.navigate().refresh();
        page = openAreas();
        page.SearchArea(name);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name), 1, "يجب بقاء المنطقة بعد تحديث الصفحة");
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-003 إنشاء منطقة والتحقق من بياناتها")
    @Description(
            "ينشئ منطقة باسم فريد ثم يعيد فتحها ويتحقق من الاسم العربي والإنجليزي والملاحظات"
                    + " والكود المُنشأ.")
    public void AR_003_CreateValidAreaAndVerifyFields() {
        AreaPage page = openAreas();
        String name = uniqueName("CREATE");
        String english = "Automation Area " + System.currentTimeMillis();
        createArea(page, name, english, "Created by automated Area test");
        page.EditAreaByName(name);
        Assert.assertEquals(page.GetArabicNameInputValue(), name);
        Assert.assertEquals(page.GetEnglishNameInputValue(), english);
        Assert.assertEquals(page.GetAreaNotesValue(), "Created by automated Area test");
        Assert.assertFalse(
                page.GetAreaCodeInputValue().isBlank(), "يجب تعيين كود للمنطقة المحفوظة");
        page.ClickCloseButton();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-007 الحقول الاختيارية عند حفظ المنطقة")
    @Description(
            "يتحقق من قبول المنطقة دون اسم إنجليزي أو ملاحظات مدخلة، ومن تعيين الاسم"
                    + " الإنجليزي تلقائيًا إلى الاسم العربي.")
    public void AR_007_OptionalFieldsCanBeEmpty() {
        AreaPage page = openAreas();
        String name = uniqueName("OPTIONAL");
        createArea(page, name, null, null);
        page.EditAreaByName(name);
        Assert.assertEquals(page.GetArabicNameInputValue(), name);
        Assert.assertEquals(
                page.GetEnglishNameInputValue(),
                name,
                "عند ترك الاسم الإنجليزي فارغًا، الواجهة تحفظه مساويًا للاسم العربي");
        Assert.assertTrue(page.GetAreaNotesValue().isEmpty(), "الملاحظات يجب أن تبقى اختيارية");
        page.ClickCloseButton();
    }

    @Test(
            priority = 10,
            enabled = true,
            description =
                    "Recheck area search by code/name and clear filters on owned records")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-015/016/018/019 البحث في قائمة المناطق")
    @Description(
            "يتحقق من البحث بالكود والاسم، والنتيجة الفارغة، ثم استعادة القائمة بعد مسح"
                    + " البحث.")
    public void AR_015_SearchAreasByCodeAndName() {
        AreaPage page = openAreas();
        String name = uniqueName("READ");
        createArea(page, name, null, null);
        String code = page.GetAreaCellByName(name, "الكود");
        page.SearchArea(code);
        Assert.assertTrue(page.IsAreaDisplayed(name), "يجب إظهار المنطقة بالبحث بالكود");
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1, "يجب إظهار المنطقة بالاسم");
        page.SearchArea("QA-AREA-NOT-FOUND");
        new org.openqa.selenium.support.ui.WebDriverWait(driver,java.time.Duration.ofSeconds(15))
                .withMessage("Area search retained rows for a nonmatching query")
                .until(d->page.GetVisibleAreaRowCount()==0);
        Assert.assertEquals(page.GetVisibleAreaRowCount(), 0, "يجب ألا تظهر نتائج غير مطابقة");
        page.SearchArea("");
        new org.openqa.selenium.support.ui.WebDriverWait(driver,java.time.Duration.ofSeconds(15))
                .withMessage("Area list did not return after clearing search")
                .until(d->page.GetVisibleAreaRowCount()>0);
        Assert.assertTrue(page.GetVisibleAreaRowCount() > 0, "يجب استعادة القائمة بعد مسح البحث");
    }

    @Test(
            priority = 11,
            enabled = true,
            description =
                    "Recheck which displayed area fields participate in search")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-017 البحث يطابق أعمدة القائمة المعروضة فقط")
    @Description(
            "يتحقق من أن البحث لا يجد الاسم الإنجليزي غير المعروض في الجدول، لكنه يجد الاسم"
                    + " العربي الظاهر.")
    public void AR_017_SearchUsesOnlyDisplayedAreaFields() {
        AreaPage page = openAreas();
        String name = uniqueName("ENGLISH");
        String english = "Area English " + System.currentTimeMillis();
        createArea(page, name, english, null);
        page.SearchArea(english);
        new org.openqa.selenium.support.ui.WebDriverWait(driver,java.time.Duration.ofSeconds(15))
                .withMessage("Area search returned a row for its hidden English name")
                .until(d->page.GetVisibleAreaRowCount()==0);
        Assert.assertEquals(
                page.GetVisibleAreaRowCount(),
                0,
                "البحث يعتمد على الكود والاسم العربي المعروضين في الجدول، ولا يبحث بالاسم الإنجليزي"
                        + " المخفي");
        page.SearchArea(name);
        new org.openqa.selenium.support.ui.WebDriverWait(driver,java.time.Duration.ofSeconds(15))
                .withMessage("Area Arabic-name search did not display the owned row")
                .until(d->page.GetExactAreaMatchCount(name)==1);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name),
                1,
                "يجب العثور على المنطقة عبر الاسم العربي المعروض");
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-028 ترتيب قائمة المناطق حسب الكود")
    @Description("يتحقق من ترتيب قيم الكود تصاعديًا ثم تنازليًا عند النقر على عنوان العمود.")
    public void AR_028_AreaListCanBeSortedByCode() {
        seedAreas("sort", 2);
        AreaPage page = openAreas();
        Assert.assertTrue(page.GetAreaColumnValues("الكود").size() >= 2);
        page.ClickAreaColumnHeader("الكود");
        List<String> ascending = page.GetAreaColumnValues("الكود");
        page.ClickAreaColumnHeader("الكود");
        List<String> descending = page.GetAreaColumnValues("الكود");
        Assert.assertEquals(
                ascending, page.SortAreaCodes(ascending, true), "يجب فرز الكود تصاعديًا");
        Assert.assertEquals(
                descending, page.SortAreaCodes(descending, false), "يجب فرز الكود تنازليًا");
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-029 تغيير عدد الصفوف المعروضة")
    @Description("يتحقق من أن حجم الصفحة المختار يحدّث عدد صفوف جدول المناطق المعروضة.")
    public void AR_029_ChangingPageSizeUpdatesVisibleRows() {
        seedAreas("page-size", 2);
        AreaPage page = openAreas();
        int oldSize = page.GetAreaPageSize();
        int newSize = page.GetAlternativeAreaPageSize(oldSize);
        page.SelectAreaPageSize(newSize);
        Assert.assertEquals(page.GetAreaPageSize(), newSize);
        Assert.assertTrue(page.GetVisibleAreaRowCount() > 0);
        Assert.assertTrue(page.GetVisibleAreaRowCount() <= newSize);
    }

    @Test(priority = 14)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-030 التنقل بين صفحات المناطق")
    @Description(
            "يتحقق من الانتقال إلى الصفحة التالية والعودة إلى الصفحة السابقة عند توفر صفحات"
                    + " أخرى.")
    public void AR_030_NavigateAreaPagesWhenAvailable() {
        AreaPage page = openAreas();
        seedAreas("pagination", page.GetAreaPageSize() + 1);
        page = openAreas();
        Assert.assertTrue(page.IsNextAreaPageEnabled(), "Owned records must fill multiple pages");
        String first = page.GetFirstAreaCellByHeader("الكود");
        page.GoToNextAreaPage();
        Assert.assertNotEquals(page.GetFirstAreaCellByHeader("الكود"), first);
        page.GoToPreviousAreaPage();
        Assert.assertEquals(page.GetFirstAreaCellByHeader("الكود"), first);
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-012 منع إنشاء سجل مكرر عند الحفظ مرتين")
    @Description("يتحقق من أن الضغط مرتين بسرعة على الحفظ لا ينشئ إلا منطقة واحدة.")
    public void AR_012_RapidDoubleSaveCreatesSingleArea() {
        AreaPage page = openAreas();
        String name = uniqueName("DOUBLE-SAVE");
        page.ClickNew();
        page.AddArabicName(name);
        page.AddEnglishName("Double Save Area");
        page.ClickSaveTwiceRapidly();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
    }

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-021 تعديل منطقة الاختبار وحفظ التغيير")
    @Description("يعدل الاسم العربي لسجل اختبار مملوك ويتحقق من تحديث القيمة ثم يحذف السجل بأمان.")
    public void AR_021_EditOwnedAreaAndSave() {
        AreaPage page = openAreas();
        String name = uniqueName("EDIT");
        String updated = uniqueName("UPDATED");
        createArea(page, name, "Edit Area", "");
        page.EditAreaByName(name);
        page.AddArabicName(updated);
        page.ClickSaveButton();
        page.SearchArea(updated);
        Assert.assertEquals(page.GetExactAreaMatchCount(updated), 1);
    }

    @Test(priority = 17)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-023 حذف منطقة الاختبار المحددة")
    @Description(
            "يحذف سجل المنطقة الذي أنشأه الاختبار فقط ويتحقق من اختفائه بعد إعادة تحميل"
                    + " القائمة.")
    public void AR_023_DeleteOnlyOwnedArea() {
        AreaPage page = openAreas();
        String name = uniqueName("DELETE");
        createArea(page, name, "Delete Area", "");
        page.RequestDeleteAreaByName(name);
        Assert.assertTrue(page.IsAreaDeleteConfirmationDisplayed(), "يجب عرض تأكيد الحذف");
        page.ConfirmAreaDelete();
        driver.navigate().refresh();
        page = openAreas();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 0);
    }

    @Test(priority = 18)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-024 إلغاء حذف منطقة الاختبار")
    @Description("يتحقق من أن إلغاء نافذة التأكيد يحافظ على منطقة الاختبار ثم ينظفها بأمان.")
    public void AR_024_CancelDeleteOfOwnedArea() {
        AreaPage page = openAreas();
        String name = uniqueName("CANCEL-DELETE");
        createArea(page, name, "Cancel Delete Area", "");
        page.RequestDeleteAreaByName(name);
        Assert.assertTrue(page.IsAreaDeleteConfirmationDisplayed());
        page.CancelAreaDelete();
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name), 1, "يجب أن يبقى السجل بعد إلغاء الحذف");
    }

    @Test(priority = 19)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-032 استمرار المنطقة بعد تحديث المتصفح")
    @Description("ينشئ منطقة اختبار ويتحقق من استمرارها بعد تحديث المتصفح.")
    public void AR_032_AreaPersistsAfterBrowserRefresh() {
        AreaPage page = openAreas();
        String name = uniqueName("REFRESH");
        createArea(page, name, "Refresh Area", "");
        driver.navigate().refresh();
        page = openAreas();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-035 عدم تكرار المنطقة بعد إعادة فتح القائمة")
    @Description("ينشئ سجلًا واحدًا ثم يعيد فتح القائمة ويتحقق من عدم تكراره.")
    public void AR_035_ReopeningAreaListDoesNotDuplicateRecord() {
        AreaPage page = openAreas();
        String name = uniqueName("REOPEN");
        createArea(page, name, "Reopen Area", "");
        driver.navigate().refresh();
        page = openAreas();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
    }
}
