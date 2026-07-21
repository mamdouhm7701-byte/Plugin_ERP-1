package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.CustomerDiscountNoticePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class CustomerDiscountNoticeTest extends BaseTest {


    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة إشعار خصم عميل")
    @Description("يتحقق هذا الاختبار من وظيفة إضافة إشعار خصم عميل، " +
            "والتأكد من دقة حسابات الصافي قبل الضريبة والضريبة والإجمالي. " +
            "كما يتحقق من أن التقرير الناتج يعكس بيانات العميل الصحيحة والقيمة والتاريخ " +
            "والملاحظات والحسابات المالية. ويتضمن الاختبار خطوات للتأكد من أن الإجمالي محسوب بشكل صحيح" +
            " كمجموع الصافي قبل الضريبة والضريبة، وأن الضريبة محسوبة بدقة كـ 15% من الصافي قبل الضريبة. وأخيرًا،" +
            " يتأكد من إضافة سند العميل بنجاح بالتفاصيل الصحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndReport() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.ClickNew();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.AddValue("100");
        customerDiscountNoticePage.SelectFromDropDawn("pre","العميل");
        customerDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = customerDiscountNoticePage.getNetBeforeTax();
        double tax = customerDiscountNoticePage.getTax();
        double total = customerDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                customerDiscountNoticePage.getTax();

        double expectedTax =
                customerDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                customerDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                customerDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        customerDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        customerDiscountNoticePage.ClickSaveAndPrintButton();

        customerDiscountNoticePage.AssertTextInReport("pre");
        customerDiscountNoticePage.AssertNumberInReport("100");
        customerDiscountNoticePage.AssertDateInReport(customerDiscountNoticePage.GetTodayDate());
        customerDiscountNoticePage.AssertTextInReport("auto test");
        customerDiscountNoticePage.AssertNumberInReport("86.96");
        customerDiscountNoticePage.AssertNumberInReport("13.04");
        customerDiscountNoticePage.CloserReport();
        customerDiscountNoticePage.AssertReceiptAdded("pre");

    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("تعديل إشعار خصم عميل")
    @Description("يتحقق هذا الاختبار من وظيفة تعديل إشعار خصم عميل، " +
            "والتأكد من دقة حسابات الصافي قبل الضريبة والضريبة والإجمالي. " +
            "كما يتحقق من أن التقرير الناتج يعكس بيانات العميل الصحيحة والقيمة والتاريخ " +
            "والملاحظات والحسابات المالية. ويتضمن الاختبار خطوات للتأكد من أن الإجمالي محسوب بشكل صحيح" +
            " كمجموع الصافي قبل الضريبة والضريبة، وأن الضريبة محسوبة بدقة كـ 15% من الصافي قبل الضريبة. وأخيرًا،" +
            " يتأكد من إضافة سند العميل بنجاح بالتفاصيل الصحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditCustomerDiscountNoticeAnd_VerifyCalculationsAndReport() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.Edit();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.AddValue("200");
        customerDiscountNoticePage.SelectFromDropDawn("تستر","العميل");
        customerDiscountNoticePage.AddNotes("auto test edit");
        double netBeforeTax = customerDiscountNoticePage.getNetBeforeTax();
        double tax = customerDiscountNoticePage.getTax();
        double total = customerDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                customerDiscountNoticePage.getTax();

        double expectedTax =
                customerDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                customerDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                customerDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        customerDiscountNoticePage.SelectFromDropDawn("كود جديد","مندوب");
        customerDiscountNoticePage.ClickSaveAndPrintButton();
        customerDiscountNoticePage.AssertTextInReport("تستر");
        customerDiscountNoticePage.AssertNumberInReport("200");
        customerDiscountNoticePage.AssertDateInReport(customerDiscountNoticePage.GetTodayDate());
        customerDiscountNoticePage.AssertTextInReport("auto test edit");
        customerDiscountNoticePage.AssertNumberInReport("173.91");
        customerDiscountNoticePage.AssertNumberInReport("26.09");
        customerDiscountNoticePage.CloserReport();
        customerDiscountNoticePage.AssertReceiptAdded("تستر");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة إشعار خصم عميل مع سند مخصص")
    @Description("يتحقق هذا الاختبار من وظيفة إضافة إشعار خصم عميل مع التعامل مع السند المخصص، " +
            "والتأكد من دقة حسابات الصافي قبل الضريبة والضريبة والإجمالي. " +
            "كما يتحقق من أن التقرير الناتج يعكس بيانات العميل الصحيحة والقيمة والتاريخ " +
            "والملاحظات والحسابات المالية. ويتضمن الاختبار خطوات للتأكد من أن الإجمالي محسوب بشكل صحيح" +
            " كمجموع الصافي قبل الضريبة والضريبة، وأن الضريبة محسوبة بدقة كـ 15% من الصافي قبل الضريبة. وأخيرًا،" +
            " يتأكد من إضافة سند العميل بنجاح بالتفاصيل الصحيحة بعد التعامل مع السند المخصص.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndCustom() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.ClickNew();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.AddValue("10");
        customerDiscountNoticePage.SelectFromDropDawn("pre","العميل");
        customerDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = customerDiscountNoticePage.getNetBeforeTax();
        double tax = customerDiscountNoticePage.getTax();
        double total = customerDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                customerDiscountNoticePage.getTax();

        double expectedTax =
                customerDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                customerDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                customerDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        customerDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        customerDiscountNoticePage.SaveCustomButton();
        customerDiscountNoticePage.HandelCustomReceipt(1, "10");
        customerDiscountNoticePage.ClickSaveButton();
        customerDiscountNoticePage.AssertReceiptAdded("pre");

    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة إشعار خصم عميل")
    @Description("يتحقق هذا الاختبار من وظيفة إضافة إشعار خصم عميل، " +
            "والتأكد من دقة حسابات الصافي قبل الضريبة والضريبة والإجمالي. " +
            "كما يتحقق من أن التقرير الناتج يعكس بيانات العميل الصحيحة والقيمة والتاريخ " +
            "والملاحظات والحسابات المالية. ويتضمن الاختبار خطوات للتأكد من أن الإجمالي محسوب بشكل صحيح" +
            " كمجموع الصافي قبل الضريبة والضريبة، وأن الضريبة محسوبة بدقة كـ 15% من الصافي قبل الضريبة. وأخيرًا،" +
            " يتأكد من إضافة سند العميل بنجاح بالتفاصيل الصحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndSave() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.ClickNew();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.AddValue("100");
        customerDiscountNoticePage.SelectFromDropDawn("pre","العميل");
        customerDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = customerDiscountNoticePage.getNetBeforeTax();
        double tax = customerDiscountNoticePage.getTax();
        double total = customerDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                customerDiscountNoticePage.getTax();

        double expectedTax =
                customerDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                customerDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                customerDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        customerDiscountNoticePage.SelectFromDropDawn("تست", "مندوب");
        customerDiscountNoticePage.ClickSaveButton();
        customerDiscountNoticePage.AssertReceiptAdded("pre");

    }


    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("حذف إشعار خصم عميل")
    @Description("يتحقق هذا الاختبار من وظيفة حذف إشعار خصم عميل، " +
            "والتأكد من أن عملية الحذف تتم بشكل صحيح وأن الإشعار يُحذف من النظام. " +
            "ويتضمن خطوات لفتح صفحة إشعار خصم العميل، وتنفيذ عملية الحذف، والتأكد من " +
            "أن الإشعار لم يعد موجودًا في النظام بعد الحذف.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Delete() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.DeleteAll();
    }


    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - إضافة إشعار خصم عميل بتاريخ غير صالح")
    @Description("يتحقق هذا الاختبار من وظيفة إضافة إشعار خصم عميل بتاريخ غير صالح، " +
            "والتأكد من أن النظام يتعرف على حالة الخطأ ويتعامل معها بشكل صحيح. " +
            "ويتضمن خطوات لفتح صفحة إشعار خصم العميل، وإدخال تاريخ غير صالح، ومحاولة حفظ الإشعار. " +
            "ويتحقق الاختبار من ظهور رسالة خطأ تفيد بأن التاريخ المدخل غير صالح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidDate() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.ClickNew();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.AddValue("100");
        customerDiscountNoticePage.SelectFromDropDawn("pre", "العميل");
        customerDiscountNoticePage.ClickSaveButton();
        customerDiscountNoticePage.isErrorPopupDisplayed();

    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - إضافة إشعار خصم عميل باسم مندوب غير صالح")
    @Description("يتحقق هذا الاختبار من وظيفة إضافة إشعار خصم عميل باسم مندوب غير صالح، " +
            "والتأكد من أن النظام يتعرف على حالة الخطأ ويتعامل معها بشكل صحيح. " +
            "ويتضمن خطوات لفتح صفحة إشعار خصم العميل، وإدخال اسم مندوب غير صالح، ومحاولة حفظ الإشعار. " +
            "ويتحقق الاختبار من ظهور رسالة خطأ تفيد بأن اسم المندوب المدخل غير صالح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidAgentName() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.ClickNew();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.AddValue("100");
        customerDiscountNoticePage.ClickSaveButton();
        customerDiscountNoticePage.IsAgentRequiredErrorDisplayed();

    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - إضافة إشعار خصم عميل بقيمة غير صالحة")
    @Description("يتحقق هذا الاختبار من وظيفة إضافة إشعار خصم عميل بقيمة غير صالحة، " +
            "والتأكد من أن النظام يتعرف على حالة الخطأ ويتعامل معها بشكل صحيح. " +
            "ويتضمن خطوات لفتح صفحة إشعار خصم العميل، وإدخال قيمة غير صالحة، ومحاولة حفظ الإشعار. " +
            "ويتحقق الاختبار من ظهور رسالة خطأ تفيد بأن القيمة المدخلة غير صالحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidValue() {
        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
        customerDiscountNoticePage.NavigationHelper(Pages.CUSTOMERSDISCOUNT);
        customerDiscountNoticePage.ClickNew();
        customerDiscountNoticePage.AddDate();
        customerDiscountNoticePage.SelectFromDropDawn("pre", "العميل");
        customerDiscountNoticePage.ClickSaveButton();
        customerDiscountNoticePage.IsAmountRequiredErrorDisplayed();
    }


}













//    @Test
//    public void VerifyTotalAmountCalculation() {
//        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
//        customerDiscountNoticePage.OpenCustomerDiscountNotice();
//        customerDiscountNoticePage.ClickNew();
//        customerDiscountNoticePage.AddValue("100");
//        BigDecimal[] values =
//                customerDiscountNoticePage.getFinancialValues();
//
//        BigDecimal netBeforeTax = values[0];
//        BigDecimal tax = values[1];
//        BigDecimal actualTotal = values[2];
//
//        BigDecimal expectedTotal =
//                netBeforeTax.add(tax);
//
//        Allure.step("Net Before Tax = " + netBeforeTax);
//        Allure.step("Tax = " + tax);
//        Allure.step("Expected Total = " + expectedTotal);
//        Allure.step("Actual Total = " + actualTotal);
//
//        Assert.assertTrue(
//                actualTotal.subtract(expectedTotal)
//                        .abs()
//                        .compareTo(new BigDecimal("0.01")) <= 0,
//                "Total Amount Calculation Failed"
//        );
//    }
//
//
//    @Test
//    public void VerifyTotalAmountCalculation_1() {
//        CustomerDiscountNoticePage customerDiscountNoticePage = new CustomerDiscountNoticePage(driver);
//        customerDiscountNoticePage.OpenCustomerDiscountNotice();
//        customerDiscountNoticePage.ClickNew();
//        customerDiscountNoticePage.AddDate();
//        customerDiscountNoticePage.AddValue("1220");
//        customerDiscountNoticePage.SelectAgent("pre");
////Asserting the total amount calculation
//        double netBeforeTax = customerDiscountNoticePage.getNetBeforeTax();
//        double tax = customerDiscountNoticePage.getTax();
//        double total = customerDiscountNoticePage.getTotal();
//
//        Allure.step("Net Before Tax : " + netBeforeTax);
//        Allure.step("Tax : " + tax);
//        Allure.step("Actual Total : " + total);
//
//        Assert.assertEquals(
//                total,
//                netBeforeTax + tax,
//                0.01
//        );
//        customerDiscountNoticePage.SelectDeliver("تست");
//
//        customerDiscountNoticePage.ClickSaveButton();
//        customerDiscountNoticePage.AssertCustomerReceiptAdded("pre");
//
//    }