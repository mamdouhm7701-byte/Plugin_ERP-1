package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AgentTest extends BaseTest {
    private String AgentURL="https://binfares.plugin-erp.com/#/agents/";


    @Test(priority=1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - الصفحة الأولى")
    @Description("يتحقق هذا الاختبار من إضافة مندوب جديد من الصفحة الأولى، ويشمل إدخال الاسم العربي والإنجليزي، واختيار المنطقة وإضافة رقم الجوال والبريد الإلكتروني، وتحديد طبيعة المندوب ونوع العمولة وإحتساب العمولة والمخزن الافتراضي وتصنيف المندوب وقوائم الأسعار، والتحقق من حالة خانة (منع إصدار فاتورة خارج موقع العميل) وخانة (مفعل)، وإضافة الملاحظات وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentFDAtaFirstPAge(){
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("مندوب 1");
        agent.AddEnglishName("Agent 1");
        agent.SelectFromDropDawn("55","اسم المنطقة");
        agent.AddAgentMobileNumber();
        agent.AddEmail("Mo@mo.com");
        agent.SelectFromDropDawn("بيع","طبيعه المندوب");
        agent.SelectFromDropDawn("بيع","نوع العمولة");
        agent.SelectFromDropDawn("مبيعات","إحتساب العموله");
        agent.SelectFromDropDawn("55","المخزن الأفتراضى");
        agent.SelectFromDropDawn("مناديب","تصنيف المندوب");
        agent.SelectFromDropDawn("vip","قوائم الأسعار");
        agent.SmartCheck("منع اصدار فاتورة خارج موقع العميل",true);
        agent.AssertCheckboxIsChecked("منع اصدار فاتورة خارج موقع العميل",true);
        agent.AssertCheckboxIsChecked("مفعل",true);
        agent.AddNoteForDealer("ملاحظات علي المندوب");
        agent.ClickSaveButton();
        agent.AssertReceiptAdded("مندوب 1");
    }

    @Test(priority=2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - تبويب العمولة")
    @Description("يتحقق هذا الاختبار من إضافة مندوب مع بيانات عمولة المندوب، ويشمل إدخال نطاق المبلغ (من/إلى) واختيار نوع العمولة (نسبة) وإدخال قيمة النسبة، والضغط على زر إضافة العمولة والتحقق من إضافتها بنجاح، وحفظ البيانات والتحقق من إضافة السجل.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentFDAtaSecondPAgeForAgentCommission() {
        Agent agent = new Agent(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("مندوب 2");
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.ClickAgentCommissionTab();
        agent.EnterFromAmount("100");
        agent.EnterToAmount("2000");
        agent.SelectByKeys("نسبه");
        agent.EnterPercentage("10");
        agent.ClickAddCommissionButton();
        agent.ClickSaveButton();
        agent.AssertReceiptAdded("مندوب 2");
    }

    @Test(priority=3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف عن طريق الفئة")
    @Description("يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق اختيار فئة، ويشمل الانتقال لتبويب عمولة الأصناف واختيار فئة (مقاولات)، والتحقق من تحميل البيانات في الجدول وأن عدد الصفوف أكثر من 5، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentThirdPageProductCommission(){
        Agent agent = new Agent(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("اضافه الفئة");
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn("55","المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.SelectFromDropDawn("مقاولات","الفئة");
        agent.waitForTableDataToLoad();
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertTrue(agent.getRowCount() > 5, "الجدول لا يحتوي على أي صفوف");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        agent.AssertReceiptAdded("اضافه الفئة");
    }

    @Test(priority=4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف لكل الأصناف")
    @Description("يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق إضافة جميع الأصناف دفعة واحدة، والتحقق من تحميل البيانات في الجدول وأن عدد الصفوف أكثر من 5، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentByAllProducts(){
        Agent agent = new Agent(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("اضافه كل الاصناف");
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn("55","المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.ClickAddAllItems();
        agent.waitForTableDataToLoad();
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertTrue(agent.getRowCount() > 5, "الجدول لا يحتوي على أي صفوف");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        agent.AssertReceiptAdded("اضافه كل الاصناف");
    }

    @Test(priority=5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف عن طريق البحث")
    @Description("يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق البحث عن صنف بالكود، ويشمل البحث عن صنفين مختلفين وإضافتهم، والتحقق من تحميل البيانات في الجدول ووجود صفوف، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentBySearchProduct(){
        Agent agent = new Agent(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("مندوب بحث");
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn("55","المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.SearchProduct("124345511");
        agent.ClickEnter();
        agent.SearchProduct("9015677471");
        agent.ClickEnter();
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertTrue(agent.getRowCount() > 0, "الجدول لا يحتوي على أي صفوف");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        agent.AssertReceiptAdded("مندوب بحث");
    }

    @Test(priority=6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف باختيار أصناف يدوياً")
    @Description("يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق اختيار أصناف يدوياً من القائمة، ويشمل الضغط على زر إضافة الأصناف واختيار صنفين من القائمة وإغلاق النافذة، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentWithChoseItem() {
        Agent agent = new Agent(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("اختيار اصناف");
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn("55","المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.ClickAddItemsButton();
        agent.AddNewProductList(2);
        agent.ClickCloseButton();
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        agent.AssertReceiptAdded("اختيار اصناف");
    }

    @Test(priority=7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("حذف مندوب")
    @Description("يتحقق هذا الاختبار من حذف المناديب المضافين، ويقوم بحذف ثلاثة سجلات متتالية للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Delete(){
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.Delete();
        agent.Delete();
        agent.Delete();
    }

    @Test(priority=8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ المندوب في حال عدم إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AseertArabicNameRequired(){
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.ClickSaveButton();
        Assert.assertTrue(agent.IsArabicNameRequiredErrorDisplayed(), "Error message for required Arabic name is not displayed");
    }

    @Test(priority=9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - طبيعة المندوب إلزامية")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ المندوب في حال عدم تحديد طبيعة المندوب، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طبيعة المندوب إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AseertAgentNaturalRequired(){
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("مندوب 1");
        agent.ClickSaveButton();
        Assert.assertTrue(agent.AssertAgentNatural(), "Error message for required Agent Natural is not displayed");
    }

    @Test(priority=10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - خطأ عند تكرار نطاق العمولة")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة نطاق عمولة مكرر بنفس القيم، ويشمل إضافة عمولة بنطاق معين ثم محاولة إضافة نفس النطاق مرة أخرى، والتحقق من ظهور رسالة خطأ تفيد بعدم إمكانية تكرار نطاق العمولة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AseertErrorMessageWhenAddTheSamePrecentge(){
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName("مندوب 2");
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.ClickAgentCommissionTab();
        agent.EnterFromAmount("100");
        agent.EnterToAmount("2000");
        agent.SelectByKeys("نسبه");
        agent.EnterPercentage("10");
        agent.ClickAddCommissionButton();
        agent.EnterFromAmount("100");
        agent.EnterToAmount("2000");
        agent.SelectByKeys("نسبه");
        agent.EnterPercentage("10");
        agent.ClickAddCommissionButton();
        agent.isErrorPopupDisplayed();
    }
}