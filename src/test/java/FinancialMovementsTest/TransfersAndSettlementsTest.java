package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.TransfersAndSettlementsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

public class TransfersAndSettlementsTest extends BaseTest {

    @AllureId("1")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Add New Transfer And Settlement")
    @Description("This test case verifies the functionality of creating a new transfer and settlement transaction between " +
            "a cash account and a bank account. The test validates that the transaction is saved successfully," +
            " printed correctly, and that the generated report contains the expected date, amount, notes, " +
            "debit account, credit account, and related entities. It also confirms that the transaction appears successfully " +
            "in the transfers and settlements list.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 1)
        public void AddNewTransferAndSettlement_SaveAndPrint_AndAssertResults() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
        transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);
        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("1000");
        transfersAndSettlementsPage.AddNotes("Test Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");;
        transfersAndSettlementsPage.SelectCreditOption("البنك");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("البنك","تست");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("1000");
        transfersAndSettlementsPage.AssertTextInReport("Test Transfer");
        transfersAndSettlementsPage.AssertTextInReport("نيو");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("نيو");

    }


    @AllureId("02")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Edit Transfer And Settlement")
    @Description("This test case verifies the functionality of editing an existing transfer and settlement transaction. It ensures that all updated values including date, amount, notes, debit side, and credit side are saved correctly. The test validates the generated report and confirms that the modified transaction is reflected successfully in the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 2)
    public void EditTransferAndSettlement_SaveAndPrint_AndAssertResults() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        transfersAndSettlementsPage.Edit();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("2000");
        transfersAndSettlementsPage.AddNotes("Edited Transfer");
        transfersAndSettlementsPage.SelectDebitOption("البنك");
        transfersAndSettlementsPage.SelectCreditOption("خزينة");
        transfersAndSettlementsPage.SelectRightSide("البنك","تست");
        transfersAndSettlementsPage.SelectLeftSide("خزينة","نيو");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("2000");
        transfersAndSettlementsPage.AssertTextInReport("Edited Transfer");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.AssertTextInReport("نيو");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("تست");
    }
    @AllureId("03")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Cash To Cash Transfer")
    @Description("This test case verifies the creation of a transfer transaction between two cash accounts. It validates successful saving, printing, report generation, and correct display of transaction details including date, amount, notes, source cash account, and destination cash account.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 3)
    public void AddFromCashToCash_SaveAndPrint_AndAssertResults() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("500");
        transfersAndSettlementsPage.AddNotes("Cash to Cash Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("خزينة");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("خزينة","تست");
        transfersAndSettlementsPage.ClickSaveButton();
        transfersAndSettlementsPage.AssertReceiptAdded("نيو");

    }
    @AllureId("04")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Cash To Bank Transfer")
    @Description("This test case verifies the functionality of transferring funds from a cash account to a bank account. It ensures that the transaction is processed successfully, the printed report contains accurate information, and the transfer record is added correctly to the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 4)
    public void AddFromCashToBank_SaveAndPrint_AndAssertResults() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("1500");
        transfersAndSettlementsPage.AddNotes("Cash to Bank Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("البنك");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("البنك","تست");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("1500");
        transfersAndSettlementsPage.AssertTextInReport("Cash to Bank Transfer");
        transfersAndSettlementsPage.AssertTextInReport("نيو");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("نيو");

    }
    @AllureId("05")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Cash To Customer Transfer")
    @Description("This test case verifies the functionality of creating a transfer transaction from a cash account to a customer account." +
            " The test validates transaction creation, report generation, displayed details, " +
            "and successful addition of the transfer record.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 5)
    public void AddFromCashToAgent_SaveAndPrint_AndAssertResults() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("2500");
        transfersAndSettlementsPage.AddNotes("Cash to Agent Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("العميل");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("العميل","تستر");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("2500");
        transfersAndSettlementsPage.AssertTextInReport("Cash to Agent Transfer");
        transfersAndSettlementsPage.AssertTextInReport("نيو");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("نيو");

    }
    @AllureId("06")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Cash To Supplier Transfer")
    @Description("This test case verifies the functionality of creating a transfer transaction from a cash account to a supplier account. It validates successful saving, report generation, displayed supplier information, and transaction registration within the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 6)
    public void FromCashToSupplier(){

        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("3000");
        transfersAndSettlementsPage.AddNotes("Cash to Supplier Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("المورد");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("المورد","مورد رمل");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("3000");
        transfersAndSettlementsPage.AssertTextInReport("Cash to Supplier Transfer");
        transfersAndSettlementsPage.AssertTextInReport("نيو");
        transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("نيو");
}
    @AllureId("07")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Bank To Cash Transfer")
    @Description("This test case verifies the functionality of transferring funds from a bank account to a cash account. It ensures that the transfer is saved successfully, reflected in the generated report, and displayed correctly within the transactions list.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 7)
    public void fromBankToCash() {

    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("4000");
    transfersAndSettlementsPage.AddNotes("Bank to Cash Transfer");
    transfersAndSettlementsPage.SelectDebitOption("البنك");
    transfersAndSettlementsPage.SelectCreditOption("خزينة");
    transfersAndSettlementsPage.SelectRightSide("البنك", "تست");
    transfersAndSettlementsPage.SelectLeftSide("خزينة", "نيو");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("4000");
    transfersAndSettlementsPage.AssertTextInReport("Bank to Cash Transfer");
    transfersAndSettlementsPage.AssertTextInReport("نيو");
    transfersAndSettlementsPage.AssertTextInReport("تست");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تست");
}
    @AllureId("08")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Bank To Bank Transfer")
    @Description("This test case verifies the functionality of creating a transfer transaction between two bank accounts. It validates transaction details, report accuracy, successful saving, and proper registration of the transfer within the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 8)
    public void fromBankToBank(){
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("5000");
        transfersAndSettlementsPage.AddNotes("Bank to Bank Transfer");
        transfersAndSettlementsPage.SelectDebitOption("البنك");
        transfersAndSettlementsPage.SelectCreditOption("البنك");
        transfersAndSettlementsPage.SelectRightSide("البنك", "تست");
        transfersAndSettlementsPage.SelectLeftSide("البنك", "تست تعديل");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("5000");
        transfersAndSettlementsPage.AssertTextInReport("Bank to Bank Transfer");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("تست");

}
    @AllureId("09")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Bank To Customer Transfer")
    @Description("This test case verifies the functionality of transferring funds from a bank account to a customer account. It ensures that the transaction is saved successfully, printed correctly, and reflected accurately in the generated report and transaction listing.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 9)
    public void fromBankToAgent() {
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("6000");
    transfersAndSettlementsPage.AddNotes("Bank to Agent Transfer");
    transfersAndSettlementsPage.SelectDebitOption("البنك");
    transfersAndSettlementsPage.SelectCreditOption("العميل");
    transfersAndSettlementsPage.SelectRightSide("البنك", "تست");
    transfersAndSettlementsPage.SelectLeftSide("العميل", "تستر");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
    transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("6000");
    transfersAndSettlementsPage.AssertTextInReport("Bank to Agent Transfer");
    transfersAndSettlementsPage.AssertTextInReport("تست");
    transfersAndSettlementsPage.AssertTextInReport("تستر");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تست");
}
    @AllureId("10")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Bank To Supplier Transfer")
    @Description("This test case verifies the functionality of transferring funds from a bank account to a supplier account. The test validates transaction details, generated report information, successful saving, and proper registration of the transaction within the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 10)
    public void fromBankToSupplier(){
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("7000");
    transfersAndSettlementsPage.AddNotes("Bank to Supplier Transfer");
    transfersAndSettlementsPage.SelectDebitOption("البنك");
    transfersAndSettlementsPage.SelectCreditOption("المورد");
    transfersAndSettlementsPage.SelectRightSide("البنك", "تست");
    transfersAndSettlementsPage.SelectLeftSide("المورد", "مورد رمل");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("7000");
    transfersAndSettlementsPage.AssertTextInReport("Bank to Supplier Transfer");
    transfersAndSettlementsPage.AssertTextInReport("تست");
    transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تست");
}
    @AllureId("11")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Customer To Cash Transfer")
    @Description("This test case verifies the functionality of transferring funds from a customer account to a cash account. It ensures that all transaction details are processed correctly and validates the generated report and transaction record after saving.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 11)
    public void fromAgentToCash() {
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("8000");
    transfersAndSettlementsPage.AddNotes("Agent to Cash Transfer");
    transfersAndSettlementsPage.SelectDebitOption("العميل");
    transfersAndSettlementsPage.SelectCreditOption("خزينة");
    transfersAndSettlementsPage.SelectRightSide("العميل", "تستر");
    transfersAndSettlementsPage.SelectLeftSide("خزينة", "نيو");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("8000");
    transfersAndSettlementsPage.AssertTextInReport("Agent to Cash Transfer");
    transfersAndSettlementsPage.AssertTextInReport("تستر");
    transfersAndSettlementsPage.AssertTextInReport("نيو");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تستر");
}
    @AllureId("12")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Customer To Bank Transfer")
    @Description("This test case verifies the functionality of transferring funds from a customer account to a bank account. It validates successful transaction creation, report generation, displayed transaction data, and successful registration in the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 12)
    public void fromAgentToBank() {
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("9000");
    transfersAndSettlementsPage.AddNotes("Agent to Bank Transfer");
    transfersAndSettlementsPage.SelectDebitOption("العميل");
    transfersAndSettlementsPage.SelectCreditOption("البنك");
    transfersAndSettlementsPage.SelectRightSide("العميل", "تستر");
    transfersAndSettlementsPage.SelectLeftSide("البنك", "تست");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("9000");
    transfersAndSettlementsPage.AssertTextInReport("Agent to Bank Transfer");
    transfersAndSettlementsPage.AssertTextInReport("تستر");
    transfersAndSettlementsPage.AssertTextInReport("تست");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تستر");
}
    @AllureId("13")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Customer To Customer Transfer")
    @Description("This test case verifies the functionality of creating a transfer transaction between two customer accounts. The test validates that the transfer is saved successfully, printed correctly, and displayed accurately in the generated report and transaction records.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 13)
    public void fromAgentToAgent() {
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("10000");
    transfersAndSettlementsPage.AddNotes("Agent to Agent Transfer");
    transfersAndSettlementsPage.SelectDebitOption("العميل");
    transfersAndSettlementsPage.SelectCreditOption("العميل");
    transfersAndSettlementsPage.SelectRightSide("العميل", "عام تست");
    transfersAndSettlementsPage.SelectRightSide("مندوب", "تست");
    transfersAndSettlementsPage.SelectLeftSide("العميل", "تستر");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("10000");
    transfersAndSettlementsPage.AssertTextInReport("Agent to Agent Transfer");
    transfersAndSettlementsPage.AssertTextInReport("تستر");
    transfersAndSettlementsPage.AssertTextInReport("تست");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تستر");
}
    @AllureId("14")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Customer To Supplier Transfer")
    @Description("This test case verifies the functionality of transferring funds from a customer account to a supplier account. It validates transaction creation, report accuracy, successful saving, and proper visibility of transaction details in the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 14)
    public void fromAgentToSupplier() {
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("11000");
    transfersAndSettlementsPage.AddNotes("Agent to Supplier Transfer");
    transfersAndSettlementsPage.SelectDebitOption("العميل");
    transfersAndSettlementsPage.SelectCreditOption("المورد");
    transfersAndSettlementsPage.SelectRightSide("العميل", "تستر");
    transfersAndSettlementsPage.SelectLeftSide("المورد", "مورد رمل");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("11000");
    transfersAndSettlementsPage.AssertTextInReport("Agent to Supplier Transfer");
    transfersAndSettlementsPage.AssertTextInReport("تستر");
    transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("تستر");
}
    @AllureId("15")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Supplier To Cash Transfer")
    @Description("This test case verifies the functionality of transferring funds from a supplier account to a cash account. It validates successful transaction processing, printed report details, and proper registration of the transfer record.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 15)
    public void fromSupplierToCash() {
    TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

      transfersAndSettlementsPage.ClickNew();
    transfersAndSettlementsPage.AddDate();
    transfersAndSettlementsPage.AddValue("12000");
    transfersAndSettlementsPage.AddNotes("Supplier to Cash Transfer");
    transfersAndSettlementsPage.SelectDebitOption("المورد");
    transfersAndSettlementsPage.SelectCreditOption("خزينة");
    transfersAndSettlementsPage.SelectRightSide("المورد", "مورد رمل");
    transfersAndSettlementsPage.SelectLeftSide("خزينة", "نيو");
    transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
    transfersAndSettlementsPage.AssertNumberInReport("12000");
    transfersAndSettlementsPage.AssertTextInReport("Supplier to Cash Transfer");
    transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
    transfersAndSettlementsPage.AssertTextInReport("نيو");
    transfersAndSettlementsPage.CloserReport();
    transfersAndSettlementsPage.AssertReceiptAdded("مورد رمل");}

    @AllureId("16")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Supplier To Bank Transfer")
    @Description("This test case verifies the functionality of transferring funds from a supplier account to a bank account. It validates successful transaction saving, report generation, and accurate reflection of the transfer within the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 16)
    public void fromSupplierToBank() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("13000");
        transfersAndSettlementsPage.AddNotes("Supplier to Bank Transfer");
        transfersAndSettlementsPage.SelectDebitOption("المورد");
        transfersAndSettlementsPage.SelectCreditOption("البنك");
        transfersAndSettlementsPage.SelectRightSide("المورد", "مورد رمل");
        transfersAndSettlementsPage.SelectLeftSide("البنك", "تست");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("13000");
        transfersAndSettlementsPage.AssertTextInReport("Supplier to Bank Transfer");
        transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
        transfersAndSettlementsPage.AssertTextInReport("تست");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("مورد رمل");
    }
    @AllureId("17")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Supplier To Customer Transfer")
    @Description("This test case verifies the functionality of transferring funds from a supplier account to a customer account. It validates transaction details, report information, successful saving, and proper registration in the transfers list.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 17)
    public void fromSupplierToAgent() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("14000");
        transfersAndSettlementsPage.AddNotes("Supplier to Agent Transfer");
        transfersAndSettlementsPage.SelectDebitOption("المورد");
        transfersAndSettlementsPage.SelectCreditOption("العميل");
        transfersAndSettlementsPage.SelectRightSide("المورد", "مورد رمل");
        transfersAndSettlementsPage.SelectLeftSide("العميل", "تستر");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("14000");
        transfersAndSettlementsPage.AssertTextInReport("Supplier to Agent Transfer");
        transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
        transfersAndSettlementsPage.AssertTextInReport("تستر");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("مورد رمل");
    }
    @AllureId("18")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Supplier To Supplier Transfer")
    @Description("This test case verifies the functionality of creating a transfer transaction between two supplier accounts. It validates successful transaction creation, report generation, displayed supplier information, and successful registration within the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 18)
    public void fromSupplierToSupplier() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("15000");
        transfersAndSettlementsPage.AddNotes("Supplier to Supplier Transfer");
        transfersAndSettlementsPage.SelectDebitOption("المورد");
        transfersAndSettlementsPage.SelectCreditOption("المورد");
        transfersAndSettlementsPage.SelectRightSide("المورد", "مورد رمل");
        transfersAndSettlementsPage.SelectRightSide("مندوب", "تست");
        transfersAndSettlementsPage.SelectLeftSide("المورد", "مورد عام");
        transfersAndSettlementsPage.ClickSaveAndPrintButton();
        transfersAndSettlementsPage.AssertDateInReport(transfersAndSettlementsPage.GetTodayDate());
        transfersAndSettlementsPage.AssertNumberInReport("15000");
        transfersAndSettlementsPage.AssertTextInReport("Supplier to Supplier Transfer");
        transfersAndSettlementsPage.AssertTextInReport("مورد رمل");
        transfersAndSettlementsPage.CloserReport();
        transfersAndSettlementsPage.AssertReceiptAdded("مورد رمل");
    }

    @AllureId("19")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Delete Transfer And Settlement Records")
    @Description("This test case verifies the functionality of deleting transfers and settlements records." +
            " It ensures that existing records can be removed successfully and validates that " +
            "the deleted transactions no longer appear in the system.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 19)
    public void Delete(){
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.DeleteAll();

    }
    @AllureId("20")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Validate Same Source And Destination Restriction")
    @Description("This test case verifies that the system prevents creating a transfer between the same source and destination account." +
            " It validates that an appropriate validation message is displayed and that the transaction cannot be saved.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 20)
    public void AssertTypeDontSendToSameType(){
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("16000");
        transfersAndSettlementsPage.AddNotes("Cash to Cash Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("خزينة");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("خزينة","نيو");
        transfersAndSettlementsPage.isErrorPopupDisplayed();
    }
    @AllureId("21")

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Validate Negative Transfer Amount")
    @Description("This test case verifies that the system rejects invalid negative transfer amounts. It ensures that validation rules are enforced and that an appropriate error message is displayed when attempting to save the transaction.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 21)
    public void AssertValueErorrMessage(){
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("-500");
        transfersAndSettlementsPage.AddNotes("Invalid Value Transfer");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("البنك");
        transfersAndSettlementsPage.SelectRightSide("خزينة","نيو");
        transfersAndSettlementsPage.SelectLeftSide("البنك","تست");
        transfersAndSettlementsPage.ClickSaveButton();
        transfersAndSettlementsPage.isErrorPopupDisplayed();;
    }
    @AllureId("22")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("Transfers And Settlements")
    @Story("Validate Required Destination Side")
    @Description("This test case verifies that the system enforces selection of all required transfer sides before saving. It validates that an error message is displayed when mandatory destination information is missing.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    @Test(priority = 22)
    public void AssertselectEverySideOption() {
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
               transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

       transfersAndSettlementsPage.ClickNew();
        transfersAndSettlementsPage.AddDate();
        transfersAndSettlementsPage.AddValue("17000");
        transfersAndSettlementsPage.AddNotes("Test All Options");
        transfersAndSettlementsPage.SelectDebitOption("خزينة");
        transfersAndSettlementsPage.SelectCreditOption("البنك");
        transfersAndSettlementsPage.SelectRightSide("خزينة", "نيو");
        transfersAndSettlementsPage.ClickSaveButton();
        transfersAndSettlementsPage.isErrorPopupDisplayed();
    }
}
