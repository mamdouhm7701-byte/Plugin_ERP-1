package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.CustomerDiscountNoticePage;
import Plugin_ERP.System_Modules.Login.FinancialMovements.SupplierDiscountNoticePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SupplierDiscountNoticeTest extends BaseTest {



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story("Add Supplier Discount Notice")
    @Description("This test case verifies the functionality of adding a supplier discount notice," +
            " ensuring that the calculations for net before tax, tax, and total are accurate." +
            " It also validates that the report generated reflects the correct supplier information, amount, date, " +
            "notes, and financial calculations. The test includes steps to assert that the total amount is correctly calculated" +
            " as the sum of net before tax and tax, and that the tax is accurately calculated as 15% of the net before tax. Finally," +
            " it confirms that the supplier receipt is added successfully with the correct details.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 1)
    public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndReport() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
        supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        supplierDiscountNoticePage.ClickSaveAndPrintButton();
        ;
        supplierDiscountNoticePage.AssertTextInReport("مورد عام");
        supplierDiscountNoticePage.AssertNumberInReport("100");
        supplierDiscountNoticePage.AssertDateInReport(supplierDiscountNoticePage.GetTodayDate());
        supplierDiscountNoticePage.AssertTextInReport("auto test");
        supplierDiscountNoticePage.AssertNumberInReport("86.96");
        supplierDiscountNoticePage.AssertNumberInReport("13.04");
        supplierDiscountNoticePage.CloserReport();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد عام");

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story("Edit Supplier Discount Notice")
    @Description("This test case verifies the functionality of Editing a Supplier discount notice," +
            " ensuring that the calculations for net before tax, tax, and total are accurate." +
            " It also validates that the report generated reflects the correct Supplier information, amount, date, " +
            "notes, and financial calculations. The test includes steps to assert that the total amount is correctly calculated" +
            " as the sum of net before tax and tax, and that the tax is accurately calculated as 15% of the net before tax. Finally," +
            " it confirms that the Supplier receipt is added successfully with the correct details.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 2)
    public void EditSupplierDiscountNoticeAnd_VerifyCalculationsAndReport() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.Edit();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("200");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد جديد","المورد");
        supplierDiscountNoticePage.AddNotes("auto test edit");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("محمود","مندوب");
        supplierDiscountNoticePage.ClickSaveAndPrintButton();
        supplierDiscountNoticePage.AssertTextInReport("مورد جديد");
        supplierDiscountNoticePage.AssertNumberInReport("200");
        supplierDiscountNoticePage.AssertDateInReport(supplierDiscountNoticePage.GetTodayDate());

        supplierDiscountNoticePage.AssertTextInReport("auto test edit");
        supplierDiscountNoticePage.AssertNumberInReport("173.91");
        supplierDiscountNoticePage.AssertNumberInReport("26.09");
        supplierDiscountNoticePage.CloserReport();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد جديد");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story("Add  Supplier Discount Notice")
    @Description("This test case verifies the functionality of adding a supplier discount notice with custom receipt handling," +
            " ensuring that the calculations for net before tax, tax, and total are accurate." +
            " It also validates that the report generated reflects the correct supplier information, amount, date, " +
            "notes, and financial calculations. The test includes steps to assert that the total amount is correctly calculated" +
            " as the sum of net before tax and tax, and that the tax is accurately calculated as 15% of the net before tax. Finally," +
            " it confirms that the supplier receipt is added successfully with the correct details after handling custom receipt.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 3)
    public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndCustom() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("10");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        supplierDiscountNoticePage.SaveCustomButton();
        supplierDiscountNoticePage.HandelCustomReceipt(1, "10");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد عام");

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story(" Add Supplier Discount Notice")
    @Description("This test case verifies the functionality of adding a supplier discount notice," +
            " ensuring that the calculations for net before tax, tax, and total are accurate." +
            " It also validates that the report generated reflects the correct supplier information, amount, date, " +
            "notes, and financial calculations. The test includes steps to assert that the total amount is correctly calculated" +
            " as the sum of net before tax and tax, and that the tax is accurately calculated as 15% of the net before tax. Finally," +
            " it confirms that the supplier receipt is added successfully with the correct details.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 4)
    public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndSave() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد عام");

    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story(" Delete  Supplier Discount Notice")
    @Description("This test case verifies the functionality of deleting a supplier discount notice," +
            " ensuring that the deletion process works correctly and that the notice is removed from the system." +
            " It includes steps to open the supplier discount notice page, perform the deletion action, and confirm that" +
            " the notice is no longer present in the system after deletion.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 5)
    public void Delete() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.DeleteAll();






    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story(" Add Supplier Discount Notice With Invalid Date")
    @Description("This test case verifies the functionality of adding a supplier discount notice with an invalid date," +
            " ensuring that the system correctly identifies and handles the error condition." +
            " It includes steps to open the supplier discount notice page, enter an invalid date, and attempt to save the notice." +
            " The test asserts that an error popup is displayed, indicating that the date entered is not valid.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 6)
    public void InvalidDate() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.isErrorPopupDisplayed();

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story(" Add Supplier Discount Notice With Invalid Agent Name")
    @Description("This test case verifies the functionality of adding a supplier discount notice with an invalid agent name," +
            " ensuring that the system correctly identifies and handles the error condition." +
            " It includes steps to open the supplier discount notice page, enter an invalid agent name, and attempt to save the notice." +
            " The test asserts that an error popup is displayed, indicating that the agent name entered is not valid.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 7)
    public void InvalidAgentName() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.isErrorPopupDisplayed();

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Financial Movements")
    @Story(" Add Supplier Discount Notice With Invalid Value")
    @Description("This test case verifies the functionality of adding a supplier discount notice with an invalid value," +
            " ensuring that the system correctly identifies and handles the error condition." +
            " It includes steps to open the supplier discount notice page, enter an invalid value, and attempt to save the notice." +
            " The test asserts that an error popup is displayed, indicating that the value entered is not valid.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 8)
    public void InvalidValue() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.IsAmountRequiredErrorDisplayed();
    }

}
