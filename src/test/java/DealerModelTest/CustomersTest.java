/* شرح الملف: اختبارات العملاء: إلغاء تعديل العملاء؛ إلغاء حذف العملاء؛ رفض اسم فارغ في العملاء؛
 * تصدير قائمة العملاء إلى PDF؛ التحقق - الرقم الضريبي إلزامي عند تفعيل عميل شركة.
 */
package DealerModelTest;

import io.qameta.allure.*;
import java.io.IOException;
import org.testng.annotations.Test;

public class CustomersTest extends DealerScenarioTest {
    final org.openqa.selenium.WebDriver caseDriver() {
        return driver;
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("CU-001 بنية قائمة العملاء وأوامر الصف")
    @Description("يتحقق من ظهور قائمة العملاء وسجلاتها وأوامر تعديلها وحذفها.")
    public void CU_001_CustomerListStructure() {
        new CustomersListsChecks(this).CU_001_CustomerListStructure();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("CU-002 إغلاق نموذج إضافة عميل دون حفظ")
    @Description("يتحقق من عدم إنشاء عميل عند إغلاق نموذج الإضافة قبل الحفظ.")
    public void CU_002_CloseCustomerFormWithoutSaving() {
        new CustomersFormsChecks(this).CU_002_CloseCustomerFormWithoutSaving();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("CU-003 إلغاء تعديل العميل دون حفظ")
    @Description("يتحقق من تحميل بيانات العميل المحدد ومن عدم حفظ التغيير عند إغلاق نموذج التعديل.")
    public void CU_003_CancelCustomerEditWithoutSaving() {
        new CustomersFormsChecks(this).CU_003_CancelCustomerEditWithoutSaving();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("CU-004 إلغاء حذف عميل")
    @Description("يتحقق من ظهور تأكيد الحذف ومن بقاء العميل عند إلغاء العملية.")
    public void CU_004_CancelCustomerDelete() {
        new CustomersFormsChecks(this).CU_004_CancelCustomerDelete();
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("CU-005 دورة حياة عميل مملوك للاختبار")
    @Description("ينشئ عميلًا فريدًا ببياناته الأساسية، يعدله، ثم يحذف السجل نفسه.")
    public void CU_005_OwnedCustomerCreateEditDelete() {
        new CustomersSavingChecks(this).CU_005_OwnedCustomerCreateEditDelete();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - الصفحة الأولى")
    @Description(
            "يتحقق هذا الاختبار من إضافة عميل جديد من الصفحة الأولى، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي، واختيار المنطقة وقائمة الأسعار ورقم الحساب وتصنيف العميل والمندوب،"
                    + " وإضافة أيام السماح والتاريخ والسجل التجاري، والتحقق من حالة خانات الاختيار (خصم"
                    + " العميل، فعال، عام، عميل نقدي، خاضع للضريبة)، وإضافة الملاحظات، وحفظ البيانات"
                    + " والتحقق من إضافة السجل بنجاح.")
    public void AddCustomerFirstPage() {
        new CustomersSavingChecks(this).AddCustomerFirstPage();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - بيانات الاتصال")
    @Description(
            "يتحقق هذا الاختبار من إضافة عميل جديد مع بيانات الاتصال، ويشمل إدخال اسم جهة"
                    + " الاتصال والعنوان ورقم الجوال والهاتف الأرضي والبريد الإلكتروني والرقم الإضافي"
                    + " والمعرف الآخر والموقع الجغرافي، وإضافة بيانات البنك (اسم البنك ورقم الحساب واسم"
                    + " الوكيل ورقم هاتفه والآيبان)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddCustomerSecondPage() {
        new CustomersSavingChecks(this).AddCustomerSecondPage();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - بيانات الربط مع الهيئة")
    @Description(
            "يتحقق هذا الاختبار من إضافة عميل جديد مع بيانات الربط مع هيئة الزكاة والضريبة،"
                    + " ويشمل إدخال الرقم الضريبي والدولة والمدينة والحي ورقم المبنى واسم الشارع والرمز"
                    + " البريدي، والتحقق من أن خانة (عميل شركة) غير مفعلة افتراضياً، ثم تفعيلها والتحقق"
                    + " من بقائها مفعلة، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddCustomerThirdPage() {
        new CustomersSavingChecks(this).AddCustomerThirdPage();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - تبويب التحكم")
    @Description(
            "يتحقق هذا الاختبار من إضافة عميل جديد مع إعدادات التحكم في العميل، ويشمل إلغاء"
                    + " تفعيل جميع الصلاحيات (مبيعات نقدية، مبيعات آجلة، مرتجع مبيعات، عرض سعر، أمر"
                    + " بيع، خصم العميل)، والتحقق من أن جميع هذه الخانات غير مفعلة، وحفظ البيانات"
                    + " والتحقق من إضافة السجل بنجاح.")
    public void AddCustomerFourthPage() {
        new CustomersSavingChecks(this).AddCustomerFourthPage();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("تعديل عميل")
    @Description(
            "يتحقق هذا الاختبار من تعديل بيانات عميل موجود بالكامل، ويشمل تعديل البيانات"
                    + " الأساسية (الاسم، المنطقة، قائمة الأسعار، رقم الحساب، المندوب، أيام السماح،"
                    + " التاريخ، السجل التجاري)، وتغيير حالة خانات الاختيار (فعال، عام، عميل نقدي، خاضع"
                    + " للضريبة، خصم العميل)، وتعديل بيانات الاتصال، وتعديل بيانات الربط مع الهيئة"
                    + " وتفعيل (عميل شركة)، وتعديل صلاحيات التحكم وتفعيل معظمها، وحفظ التعديلات والتحقق"
                    + " من تحديث السجل بنجاح.")
    public void Edit() {
        new CustomersSavingChecks(this).Edit();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("حذف عميل")
    @Description(
            "يتحقق هذا الاختبار من حذف العملاء المضافين، ويقوم بحذف أربعة سجلات متتالية"
                    + " للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Test(priority = 0)
    public void Delete() {
        new CustomersSaving2Checks(this).Delete();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال الاسم العربي، ويتحقق"
                    + " من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    public void SaveWithOutArabicName() {
        new CustomersSaving2Checks(this).SaveWithOutArabicName();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - البريد الإلكتروني غير صحيح")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يرفض البريد الإلكتروني غير الصحيح عند إضافة"
                    + " عميل، ويتحقق من ظهور رسالة خطأ تفيد بأن صيغة البريد الإلكتروني غير صحيحة.")
    public void EnterWrongEmail() {
        new CustomersSaving2Checks(this).EnterWrongEmail();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - رقم الحساب البنكي يحتوي على حروف")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يرفض رقم الحساب البنكي الذي يحتوي على أحرف،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن رقم الحساب يجب أن يحتوي على أرقام فقط بدون"
                    + " حروف.")
    public void EnterWrongBankNumber() {
        new CustomersSaving2Checks(this).EnterWrongBankNumber();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الرقم الضريبي إلزامي عند تفعيل عميل شركة")
    @Description(
            "يتحقق هذا الاختبار من ظهور رسائل الخطأ المطلوبة في تبويب بيانات الربط مع الهيئة"
                    + " عند تفعيل خانة (عميل شركة) بدون إدخال الرقم الضريبي، ويتحقق من أن الرقم الضريبي"
                    + " حقل إلزامي عند تفعيل هذا الخيار.")
    public void AssertErrorMessagesInDataTax() {
        new CustomersSaving2Checks(this).AssertErrorMessagesInDataTax();
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الرقم الضريبي يجب أن يبدأ وينتهي بـ 3")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يرفض الرقم الضريبي الذي لا يبدأ بالرقم 3 ولا"
                    + " ينتهي بالرقم 3، ويتحقق من ظهور رسالة خطأ عند إدخال رقم ضريبي غير صحيح الصيغة.")
    public void AssertTaxNumberStartNumber3andEndStareNumber3() {
        new CustomersSaving2Checks(this).AssertTaxNumberStartNumber3andEndStareNumber3();
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - رقم المبنى يجب أن يكون 4 أرقام على الأقل")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يرفض رقم المبنى الذي يحتوي على أقل من 4 أرقام،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن رقم المبنى يجب أن يكون مكوناً من 4 أرقام على"
                    + " الأقل.")
    public void AssertBuildingNumber() {
        new CustomersSaving2Checks(this).AssertBuildingNumber();
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الرمز البريدي يجب أن يكون 5 أرقام")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يرفض الرمز البريدي الذي يحتوي على أقل من 5"
                    + " أرقام، ويتحقق من ظهور رسالة خطأ تفيد بأن الرمز البريدي يجب أن يكون مكوناً من 5"
                    + " أرقام.")
    public void AssertPostNumberInclode5number() {
        new CustomersSaving2Checks(this).AssertPostNumberInclode5number();
    }

    @Test(priority = 14)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - العنوان إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال العنوان في بيانات"
                    + " الاتصال، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل العنوان إلزامي.")
    public void AssertAdress() {
        new CustomersSaving2Checks(this).AssertAdress();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - تنبيه رقم الهاتف المكرر والضغط على نعم")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يظهر نافذة تنبيه عند محاولة إدخال رقم هاتف مكرر،"
                    + " ويتحقق من إمكانية الضغط على زر 'نعم' في النافذة.")
    public void AsserShowErrorPopUpPhoneNumberDubelicateAndClickYes() {
        new CustomersSaving2Checks(this).AsserShowErrorPopUpPhoneNumberDubelicateAndClickYes();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - تنبيه رقم الهاتف المكرر والضغط على إلغاء")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يظهر نافذة تنبيه عند محاولة إدخال رقم هاتف مكرر،"
                    + " ويتحقق من إمكانية الضغط على زر 'إلغاء' في النافذة.")
    public void AsserShowErrorPopUpPhoneNumberDubelicateAndClickCansel() {
        new CustomersSaving2Checks(this).AsserShowErrorPopUpPhoneNumberDubelicateAndClickCansel();
    }

    @Test(priority = 90)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("تصدير قائمة العملاء إلى PDF")
    @Description("يتحقق من تنزيل PDF صالح من قائمة العملاء.")
    public void ExportCustomersToPdf() throws IOException {
        new CustomersExportsChecks(this).ExportCustomersToPdf();
    }

    @Test(priority = 91)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("تصدير قائمة العملاء إلى Excel")
    @Description("يتحقق من تنزيل Excel صالح من قائمة العملاء.")
    public void ExportCustomersToExcel() throws IOException {
        new CustomersExportsChecks(this).ExportCustomersToExcel();
    }
}
