package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CustomersTest extends BaseTest {


    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - الصفحة الأولى")
    @Description("يتحقق هذا الاختبار من إضافة عميل جديد من الصفحة الأولى، ويشمل إدخال الاسم العربي والإنجليزي، واختيار المنطقة وقائمة الأسعار ورقم الحساب وتصنيف العميل والمندوب، وإضافة أيام السماح والتاريخ والسجل التجاري، والتحقق من حالة خانات الاختيار (خصم العميل، فعال، عام، عميل نقدي، خاضع للضريبة)، وإضافة الملاحظات، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerFirstPage() {
        Customers customers = new Customers(driver);
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("عميل جديد");
        customers.AddEnglishName("New Customer");
        customers.SelectFromDropDawn("جديد", "اسم المنطقة");
        customers.SelectFromDropDawn("تسويق جوجل", "رقم الحساب");
        customers.SelectFromDropDawn("عملاء", "تصنيف العميل");
        customers.SelectFromDropDawn("عملاء", "اسم المتعامل الرئيسي");
        customers.SelectFromDropDawn("مندوب بيع", "المندوب");
        customers.AddAllowanceDays("30");
        customers.AddDateForDealer();
        customers.AddCommercialRegisterNumber("123456789");
        customers.SelectCheckboxByText("خصم العميل", true);
        customers.AssertCheckboxIsChecked("خصم العميل", true);
        customers.AddDiscountValue("10");
        customers.AddValueLimit("1000");
        customers.AddNoteForDealer("هذا العميل مهم جدا");
        customers.AssertCheckboxIsChecked("فعال", true);
        customers.SmartCheck("عام", true);
        customers.SmartCheck("عميل نقدي", true);
        customers.AssertCheckboxIsChecked("عام", true);
        customers.AssertCheckboxIsChecked("عميل نقدي", true);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", true);
        customers.SmartCheck("خاضع للضريبة", false);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", false);
        customers.ClickSaveButton();
        customers.AssertReceiptAdded("عميل جديد");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - بيانات الاتصال")
    @Description("يتحقق هذا الاختبار من إضافة عميل جديد مع بيانات الاتصال، ويشمل إدخال اسم جهة الاتصال والعنوان ورقم الجوال والهاتف الأرضي والبريد الإلكتروني والرقم الإضافي والمعرف الآخر والموقع الجغرافي، وإضافة بيانات البنك (اسم البنك ورقم الحساب واسم الوكيل ورقم هاتفه والآيبان)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerSecondPage() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الاتصال");
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddAddress("شارع الملك فيصل");
        customers.AddMobileNumber("0591234567");
        customers.AddLandLine("0112345678");
        customers.AddEmail("muhammad@example.com");
        customers.AddExtraNumber("0597654321");
        customers.AddAnotherId("987654321");
        customers.AddLocation("https://maps.app.goo.gl/mcLw5htkdHgY7NZm9");
        customers.AddBankName("البنك الأهلي");
        customers.AddAccountNumber("1234567890123456");
        customers.AddAgentName("أحمد علي");
        customers.AddAgentPhone("0598765432");
        customers.AddIban("SA1234567890123456789012");
        customers.ClickSaveButton();
        customers.AssertReceiptAdded("بيانات الاتصال");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - بيانات الربط مع الهيئة")
    @Description("يتحقق هذا الاختبار من إضافة عميل جديد مع بيانات الربط مع هيئة الزكاة والضريبة، ويشمل إدخال الرقم الضريبي والدولة والمدينة والحي ورقم المبنى واسم الشارع والرمز البريدي، والتحقق من أن خانة (عميل شركة) غير مفعلة افتراضياً، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerThirdPage() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الربط مع الهيئة");
        customers.ClickLinkDataTax();
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12345");
        customers.AssertCheckboxIsChecked("عميل شركة", false);
        customers.ClickSaveButton();
        customers.AssertReceiptAdded("بيانات الربط مع الهيئة");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("إضافة عميل - تبويب التحكم")
    @Description("يتحقق هذا الاختبار من إضافة عميل جديد مع إعدادات التحكم في العميل، ويشمل إلغاء تفعيل جميع الصلاحيات (مبيعات نقدية، مبيعات آجلة، مرتجع مبيعات، عرض سعر، أمر بيع، خصم العميل)، والتحقق من أن جميع هذه الخانات غير مفعلة، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerFourthPage() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("التحكم فى العملاء");
        customers.ClickCustomerControlTab();
        customers.SmartCheck("مبيعات نقدية", false);
        customers.SmartCheck("مبيعات اجلة", false);
        customers.SmartCheck("مرتجع مبيعات", false);
        customers.SmartCheck("عرض سعر", false);
        customers.SmartCheck("امر بيع", false);
        customers.SmartCheck("خصم العميل", false);
        customers.AssertCheckboxIsChecked("مبيعات نقدية", false);
        customers.AssertCheckboxIsChecked("مبيعات اجلة", false);
        customers.AssertCheckboxIsChecked("مرتجع مبيعات", false);
        customers.AssertCheckboxIsChecked("عرض سعر", false);
        customers.AssertCheckboxIsChecked("امر بيع", false);
        customers.AssertCheckboxIsChecked("خصم العميل", false);
        customers.ClickSaveButton();
        customers.AssertReceiptAdded("التحكم فى العملاء");
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("تعديل عميل")
    @Description("يتحقق هذا الاختبار من تعديل بيانات عميل موجود بالكامل، ويشمل تعديل البيانات الأساسية (الاسم، المنطقة، قائمة الأسعار، رقم الحساب، المندوب، أيام السماح، التاريخ، السجل التجاري)، وتغيير حالة خانات الاختيار (فعال، عام، عميل نقدي، خاضع للضريبة، خصم العميل)، وتعديل بيانات الاتصال، وتعديل بيانات الربط مع الهيئة وتفعيل (عميل شركة)، وتعديل صلاحيات التحكم وتفعيل معظمها، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Edit() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.Edit();
        customers.AddArabicName("عميل معدل");
        customers.AddEnglishName("New Customer");
        customers.SelectFromDropDawn("منطقه معدله", "اسم المنطقة");
        customers.SelectFromDropDawn("تسويق لوحات", "رقم الحساب");
        customers.SelectFromDropDawn("عملاء", "تصنيف العميل");
        customers.SelectFromDropDawn("عملاء", "اسم المتعامل الرئيسي");
        customers.SelectFromDropDawn("مندوب تست", "المندوب");
        customers.AddAllowanceDays("100");
        customers.AddDateForDealer();
        customers.AddCommercialRegisterNumber("133456789");
        customers.SelectCheckboxByText("خصم العميل", false);
        customers.SelectCheckboxByText("خصم العميل", false);
        customers.SelectCheckboxByText("فعال", false);
        customers.AddValueLimit("10000");
        customers.AddNoteForDealer("تست معدل");
        customers.AssertCheckboxIsChecked("فعال", false);
        customers.SmartCheck("عام", false);
        customers.SmartCheck("عميل نقدي", false);
        customers.AssertCheckboxIsChecked("عام", false);
        customers.AssertCheckboxIsChecked("عميل نقدي", false);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", true);
        customers.SmartCheck("خاضع للضريبة", false);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", false);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("معدل");
        customers.AddAddress("معدل");
        customers.AddMobileNumber("05145754");
        customers.AddLandLine("0112345278");
        customers.AddEmail("muhammad@example.com");
        customers.AddExtraNumber("0597652321");
        customers.AddAnotherId("987654321");
        customers.AddLocation("https://maps.app.goo.gl/mcLw5htkdHgY7NZm9");
        customers.AddBankName("البنك الأهلي");
        customers.AddAccountNumber("1234567890123456");
        customers.AddAgentName("أحمد علي");
        customers.AddAgentPhone("0598765432");
        customers.AddIban("SA1234567890123456789012");
        customers.ClickLinkDataTax();
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12345");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickCustomerControlTab();
        customers.SmartCheck("مبيعات نقدية", true);
        customers.SmartCheck("مبيعات اجلة", true);
        customers.SmartCheck("مرتجع مبيعات", true);
        customers.SmartCheck("عرض سعر", true);
        customers.SmartCheck("امر بيع", true);
        customers.SmartCheck("خصم العميل", false);
        customers.AssertCheckboxIsChecked("مبيعات نقدية", true);
        customers.AssertCheckboxIsChecked("مبيعات اجلة", true);
        customers.AssertCheckboxIsChecked("مرتجع مبيعات", true);
        customers.AssertCheckboxIsChecked("عرض سعر", true);
        customers.AssertCheckboxIsChecked("امر بيع", true);
        customers.AssertCheckboxIsChecked("خصم العميل", false);
        customers.ClickSaveButton();
        customers.AssertReceiptAdded("عميل معدل");
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("حذف عميل")
    @Description("يتحقق هذا الاختبار من حذف العملاء المضافين، ويقوم بحذف أربعة سجلات متتالية للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Delete() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.Delete();
        customers.Delete();
        customers.Delete();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void SaveWithOutArabicName() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("");
        customers.AddEnglishName("New Customer");
        Assert.assertTrue(customers.IsArabicNameRequiredErrorDisplayed());
        customers.ClickSaveButton();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - البريد الإلكتروني غير صحيح")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض البريد الإلكتروني غير الصحيح عند إضافة عميل، ويتحقق من ظهور رسالة خطأ تفيد بأن صيغة البريد الإلكتروني غير صحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EnterWrongEmail() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الاتصال");
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddEmail("muhamm");
        customers.ClickSaveButton();
        Assert.assertTrue(customers.IsRightEmailErrorDisplayed());
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - رقم الحساب البنكي يحتوي على حروف")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض رقم الحساب البنكي الذي يحتوي على أحرف، ويتحقق من ظهور رسالة خطأ تفيد بأن رقم الحساب يجب أن يحتوي على أرقام فقط بدون حروف.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EnterWrongBankNumber() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الاتصال");
        customers.ClickContactDataTab();
        customers.AddAccountNumber("123456789012lk3456");
        customers.ClickSaveButton();
        Assert.assertTrue(customers.AssertNumberOfBankWithOutLetter());
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الرقم الضريبي إلزامي عند تفعيل عميل شركة")
    @Description("يتحقق هذا الاختبار من ظهور رسائل الخطأ المطلوبة في تبويب بيانات الربط مع الهيئة عند تفعيل خانة (عميل شركة) بدون إدخال الرقم الضريبي، ويتحقق من أن الرقم الضريبي حقل إلزامي عند تفعيل هذا الخيار.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertErrorMessagesInDataTax() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الربط مع الهيئة");
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        Assert.assertTrue(customers.IsTaxNumberRequiredErrorDisplayed());
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الرقم الضريبي يجب أن يبدأ وينتهي بـ 3")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض الرقم الضريبي الذي لا يبدأ بالرقم 3 ولا ينتهي بالرقم 3، ويتحقق من ظهور رسالة خطأ عند إدخال رقم ضريبي غير صحيح الصيغة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertTaxNumberStartNumber3andEndStareNumber3() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الربط مع الهيئة");
        customers.ClickLinkDataTax();
        customers.AddTaxNumber("123456789123451");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12345");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddAddress("شارع الملك فيصل");
        customers.ClickSaveButton();
        customers.isErrorPopupDisplayed();
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - رقم المبنى يجب أن يكون 4 أرقام على الأقل")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض رقم المبنى الذي يحتوي على أقل من 4 أرقام، ويتحقق من ظهور رسالة خطأ تفيد بأن رقم المبنى يجب أن يكون مكوناً من 4 أرقام على الأقل.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertBuildingNumber() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الربط مع الهيئة");
        customers.ClickLinkDataTax();
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("113");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12415");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickSaveButton();
        Assert.assertTrue(customers.AssertBuildingNumber());
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - الرمز البريدي يجب أن يكون 5 أرقام")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض الرمز البريدي الذي يحتوي على أقل من 5 أرقام، ويتحقق من ظهور رسالة خطأ تفيد بأن الرمز البريدي يجب أن يكون مكوناً من 5 أرقام.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertPostNumberInclode5number() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الربط مع الهيئة");
        customers.ClickLinkDataTax();
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("125");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickSaveButton();
        Assert.assertTrue(customers.AssertPostNumber());
    }

    @Test(priority = 14)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - العنوان إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال العنوان في بيانات الاتصال، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل العنوان إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertAdress() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الربط مع الهيئة");
        customers.ClickLinkDataTax();
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1113");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("11345");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddAddress("");
        Assert.assertTrue(customers.AssertAdress());
        customers.ClickSaveButton();
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - تنبيه رقم الهاتف المكرر والضغط على نعم")
    @Description("يتحقق هذا الاختبار من أن النظام يظهر نافذة تنبيه عند محاولة إدخال رقم هاتف مكرر، ويتحقق من إمكانية الضغط على زر 'نعم' في النافذة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AsserShowErrorPopUpPhoneNumberDubelicateAndClickYes() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الاتصال مكرر");
        customers.ClickContactDataTab();
        customers.AddMobileNumber("0155898720");
        customers.ClickSaveButton();
        customers.AssertPopupAndClickOK();
        customers.AssertReceiptAdded("بيانات الاتصال مكرر");
    }

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("العملاء")
    @Story("التحقق - تنبيه رقم الهاتف المكرر والضغط على إلغاء")
    @Description("يتحقق هذا الاختبار من أن النظام يظهر نافذة تنبيه عند محاولة إدخال رقم هاتف مكرر، ويتحقق من إمكانية الضغط على زر 'إلغاء' في النافذة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AsserShowErrorPopUpPhoneNumberDubelicateAndClickCansel() {
        Customers customers = new Customers(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("بيانات الاتصال مكرر");
        customers.ClickContactDataTab();
        customers.AddMobileNumber("0155898720");
        customers.ClickSaveButton();
        customers.AssertPopupAndClickCancel();
    }
}