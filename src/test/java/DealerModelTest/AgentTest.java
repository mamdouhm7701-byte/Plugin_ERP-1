/* شرح الملف: اختبارات المناديب: إلغاء تعديل المناديب؛ إلغاء حذف المناديب؛ إضافة مندوب - عمولة أصناف
 * عن طريق البحث؛ رفض اسم فارغ في المناديب؛ تصدير قائمة المناديب إلى PDF.
 */
package DealerModelTest;

import io.qameta.allure.*;
import java.io.IOException;
import org.testng.annotations.Test;

public class AgentTest extends DealerScenarioTest {
    final org.openqa.selenium.WebDriver caseDriver() {
        return driver;
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-001 بنية قائمة المناديب وأوامر الصف")
    @Description("يتحقق من ظهور قائمة المناديب وسجلاتها وأوامر تعديلها وحذفها.")
    public void AG_001_AgentListStructure() {
        new AgentListsChecks(this).AG_001_AgentListStructure();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-002 إغلاق نموذج إضافة مندوب دون حفظ")
    @Description("يتحقق من عدم إنشاء مندوب عند إغلاق نموذج الإضافة قبل الحفظ.")
    public void AG_002_CloseAgentFormWithoutSaving() {
        new AgentFormsChecks(this).AG_002_CloseAgentFormWithoutSaving();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-003 إلغاء تعديل المندوب دون حفظ")
    @Description(
            "يتحقق من تحميل بيانات المندوب المحدد ومن عدم حفظ التغيير عند إغلاق نموذج"
                    + " التعديل.")
    public void AG_003_CancelAgentEditWithoutSaving() {
        new AgentFormsChecks(this).AG_003_CancelAgentEditWithoutSaving();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-004 إلغاء حذف مندوب")
    @Description("يتحقق من ظهور تأكيد الحذف ومن بقاء المندوب عند إلغاء العملية.")
    public void AG_004_CancelAgentDelete() {
        new AgentFormsChecks(this).AG_004_CancelAgentDelete();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-005 دورة حياة مندوب مملوك للاختبار")
    @Description("ينشئ مندوبًا فريدًا، يعدله، ثم يحذف السجل نفسه ويتحقق من كل انتقال.")
    public void AG_005_OwnedAgentCreateEditDelete() {
        new AgentSavingChecks(this).AG_005_OwnedAgentCreateEditDelete();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - الصفحة الأولى")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب جديد من الصفحة الأولى، ويشمل إدخال الاسم"
                    + " العربي والإنجليزي، واختيار المنطقة وإضافة رقم الجوال والبريد الإلكتروني، وتحديد"
                    + " طبيعة المندوب ونوع العمولة وإحتساب العمولة والمخزن الافتراضي وتصنيف المندوب"
                    + " وقوائم الأسعار، والتحقق من حالة خانة (منع إصدار فاتورة خارج موقع العميل) وخانة"
                    + " (مفعل)، وإضافة الملاحظات وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentFDAtaFirstPAge() {
        new AgentSavingChecks(this).AddAgentFDAtaFirstPAge();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - تبويب العمولة")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع بيانات عمولة المندوب، ويشمل إدخال نطاق"
                    + " المبلغ (من/إلى) واختيار نوع العمولة (نسبة) وإدخال قيمة النسبة، والضغط على زر"
                    + " إضافة العمولة والتحقق من إضافتها بنجاح، وحفظ البيانات والتحقق من إضافة السجل.")
    public void AddAgentFDAtaSecondPAgeForAgentCommission() {
        new AgentSavingChecks(this).AddAgentFDAtaSecondPAgeForAgentCommission();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف عن طريق الفئة")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق اختيار فئة، ويشمل"
                    + " الانتقال لتبويب عمولة الأصناف واختيار فئة أنشأها الاختبار، والتحقق من تحميل"
                    + " البيانات في الجدول وأن عدد الصفوف يساوي عدد أصناف الاختبار، وإدخال نسبة العمولة"
                    + " وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentThirdPageProductCommission() {
        new AgentSavingChecks(this).AddAgentThirdPageProductCommission();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف لكل الأصناف")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق إضافة جميع الأصناف دفعة"
                    + " واحدة، والتحقق من تحميل البيانات في الجدول وأن عدد الصفوف يساوي عدد أصناف"
                    + " الاختبار، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentByAllProducts() {
        new AgentSavingChecks(this).AddAgentByAllProducts();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف عن طريق البحث")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق البحث عن صنف بالكود،"
                    + " ويشمل البحث عن صنفين مختلفين وإضافتهم، والتحقق من تحميل البيانات في الجدول"
                    + " ووجود صفوف، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentBySearchProduct() {
        new AgentListsChecks(this).AddAgentBySearchProduct();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف باختيار أصناف يدوياً")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق اختيار أصناف يدوياً من"
                    + " القائمة، ويشمل الضغط على زر إضافة الأصناف واختيار صنفين من القائمة وإغلاق"
                    + " النافذة، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentWithChoseItem() {
        new AgentSavingChecks(this).AddAgentWithChoseItem();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("حذف مندوب")
    @Description(
            "يتحقق هذا الاختبار من حذف المناديب المضافين، ويقوم بحذف ثلاثة سجلات متتالية"
                    + " للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Test(priority = 0)
    public void Delete() {
        new AgentSavingChecks(this).Delete();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع حفظ المندوب في حال عدم إدخال الاسم العربي،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    public void AseertArabicNameRequired() {
        new AgentValidationChecks(this).AseertArabicNameRequired();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - طبيعة المندوب إلزامية")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع حفظ المندوب في حال عدم تحديد طبيعة المندوب،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طبيعة المندوب إلزامي.")
    public void AseertAgentNaturalRequired() {
        new AgentValidationChecks(this).AseertAgentNaturalRequired();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - خطأ عند تكرار نطاق العمولة")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة نطاق عمولة مكرر بنفس القيم، ويشمل"
                    + " إضافة عمولة بنطاق معين ثم محاولة إضافة نفس النطاق مرة أخرى، والتحقق من ظهور"
                    + " رسالة خطأ تفيد بعدم إمكانية تكرار نطاق العمولة.")
    public void AseertErrorMessageWhenAddTheSamePrecentge() {
        new AgentSavingChecks(this).AseertErrorMessageWhenAddTheSamePrecentge();
    }

    @Test(priority = 90)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("تصدير قائمة المناديب إلى PDF")
    @Description("يتحقق من تنزيل PDF صالح من قائمة المناديب.")
    public void ExportAgentsToPdf() throws IOException {
        new AgentExportsChecks(this).ExportAgentsToPdf();
    }

    @Test(priority = 91)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("تصدير قائمة المناديب إلى Excel")
    @Description("يتحقق من تنزيل Excel صالح من قائمة المناديب.")
    public void ExportAgentsToExcel() throws IOException {
        new AgentExportsChecks(this).ExportAgentsToExcel();
    }
}
