/* شرح الملف: اختبارات المناطق: إنشاء منطقة ومراجعة البيانات المحفوظة؛ تحميل بيانات المنطقة الحالية
 * عند التعديل؛ إلغاء حذف سجل المناطق؛ البحث المستقل بكود المنطقة ثم اسمها؛ رفض اسم عربي فارغ في
 * المناطق.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import io.qameta.allure.*;
import org.testng.annotations.Test;

public class AreaTest extends AreaCaseSupport {

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
        new AreaListsChecks(this).AR_001_AreaListStructureIsDisplayed();
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-002 فتح نموذج إضافة منطقة")
    @Description("يتحقق من ظهور الكود والاسم العربي والإنجليزي والملاحظات وأزرار الحفظ والإغلاق.")
    public void AR_002_AreaCreationFormControlsAreDisplayed() {
        new AreaFormsChecks(this).AR_002_AreaCreationFormControlsAreDisplayed();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-014 إغلاق نموذج الإضافة دون حفظ")
    @Description("يتحقق من أن إغلاق النموذج بعد إدخال اسم مؤقت لا ينشئ منطقة.")
    public void AR_014_CloseCreationFormWithoutSaving() {
        new AreaFormsChecks(this).AR_014_CloseCreationFormWithoutSaving();
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
        new AreaFormsChecks(this).AR_020_EditFormLoadsCurrentAreaValues();
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
        new AreaFormsChecks(this).AR_022_CancelAreaEditWithoutSaving();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-004 الاسم العربي إلزامي")
    @Description("يتحقق من منع حفظ المنطقة دون اسم عربي وظهور رسالة الإلزامية وعدم إضافة صف جديد.")
    public void AR_004_ArabicNameIsRequired() {
        new AreaValidationChecks(this).AR_004_ArabicNameIsRequired();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-005 رفض الاسم العربي المكوّن من مسافات")
    @Description("يتحقق من أن المسافات فقط لا تعتبر اسمًا صالحًا للمنطقة.")
    public void AR_005_WhitespaceNameIsRejected() {
        new AreaValidationChecks(this).AR_005_WhitespaceNameIsRejected();
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
        new AreaValidationChecks(this).AR_013_CorrectArabicNameAfterValidationError();
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
        new AreaSavingChecks(this).AR_003_CreateValidAreaAndVerifyFields();
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
        new AreaValidationChecks(this).AR_007_OptionalFieldsCanBeEmpty();
    }

    @Test(
            priority = 10,
            enabled = true,
            description = "Recheck area search by code/name and clear filters on owned records")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-015/016/018/019 البحث في قائمة المناطق")
    @Description(
            "يتحقق من البحث بالكود والاسم، والنتيجة الفارغة، ثم استعادة القائمة بعد مسح"
                    + " البحث.")
    public void AR_015_SearchAreasByCodeAndName() {
        new AreaListsChecks(this).AR_015_SearchAreasByCodeAndName();
    }

    @Test(
            priority = 11,
            enabled = true,
            description = "Recheck which displayed area fields participate in search")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-017 البحث يطابق أعمدة القائمة المعروضة فقط")
    @Description(
            "يتحقق من أن البحث لا يجد الاسم الإنجليزي غير المعروض في الجدول، لكنه يجد الاسم"
                    + " العربي الظاهر.")
    public void AR_017_SearchUsesOnlyDisplayedAreaFields() {
        new AreaListsChecks(this).AR_017_SearchUsesOnlyDisplayedAreaFields();
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-028 ترتيب قائمة المناطق حسب الكود")
    @Description("يتحقق من ترتيب قيم الكود تصاعديًا ثم تنازليًا عند النقر على عنوان العمود.")
    public void AR_028_AreaListCanBeSortedByCode() {
        new AreaListsChecks(this).AR_028_AreaListCanBeSortedByCode();
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-029 تغيير عدد الصفوف المعروضة")
    @Description("يتحقق من أن حجم الصفحة المختار يحدّث عدد صفوف جدول المناطق المعروضة.")
    public void AR_029_ChangingPageSizeUpdatesVisibleRows() {
        new AreaListsChecks(this).AR_029_ChangingPageSizeUpdatesVisibleRows();
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
        new AreaListsChecks(this).AR_030_NavigateAreaPagesWhenAvailable();
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-012 منع إنشاء سجل مكرر عند الحفظ مرتين")
    @Description("يتحقق من أن الضغط مرتين بسرعة على الحفظ لا ينشئ إلا منطقة واحدة.")
    public void AR_012_RapidDoubleSaveCreatesSingleArea() {
        new AreaSavingChecks(this).AR_012_RapidDoubleSaveCreatesSingleArea();
    }

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-021 تعديل منطقة الاختبار وحفظ التغيير")
    @Description("يعدل الاسم العربي لسجل اختبار مملوك ويتحقق من تحديث القيمة ثم يحذف السجل بأمان.")
    public void AR_021_EditOwnedAreaAndSave() {
        new AreaSavingChecks(this).AR_021_EditOwnedAreaAndSave();
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
        new AreaSavingChecks(this).AR_023_DeleteOnlyOwnedArea();
    }

    @Test(priority = 18)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-024 إلغاء حذف منطقة الاختبار")
    @Description("يتحقق من أن إلغاء نافذة التأكيد يحافظ على منطقة الاختبار ثم ينظفها بأمان.")
    public void AR_024_CancelDeleteOfOwnedArea() {
        new AreaFormsChecks(this).AR_024_CancelDeleteOfOwnedArea();
    }

    @Test(priority = 19)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-032 استمرار المنطقة بعد تحديث المتصفح")
    @Description("ينشئ منطقة اختبار ويتحقق من استمرارها بعد تحديث المتصفح.")
    public void AR_032_AreaPersistsAfterBrowserRefresh() {
        new AreaListsChecks(this).AR_032_AreaPersistsAfterBrowserRefresh();
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("AR-035 عدم تكرار المنطقة بعد إعادة فتح القائمة")
    @Description("ينشئ سجلًا واحدًا ثم يعيد فتح القائمة ويتحقق من عدم تكراره.")
    public void AR_035_ReopeningAreaListDoesNotDuplicateRecord() {
        new AreaValidationChecks(this).AR_035_ReopeningAreaListDoesNotDuplicateRecord();
    }
}
