package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.TransfersAndSettlementsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

public class TransfersAndSettlementsTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("إضافة تحويل وتسوية جديد")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل وتسوية جديدة بين الحسابات المختلفة، مع التحقق من صحة بيانات التقرير والتأكد من حفظ العملية وإضافتها بنجاح داخل النظام.")
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


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تعديل تحويل وتسوية")
    @Description("يتحقق هذا الاختبار من تعديل عملية تحويل وتسوية موجودة، مع التأكد من حفظ البيانات الجديدة وصحة التقرير وانعكاس التعديلات داخل النظام.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من خزينة إلى خزينة")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين خزينتين والتأكد من حفظ العملية بنجاح.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من خزينة إلى بنك")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من خزينة إلى حساب بنكي مع التحقق من التقرير وإضافة العملية بنجاح.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من خزينة إلى عميل")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من خزينة إلى حساب عميل والتأكد من حفظها وإضافتها بنجاح.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من خزينة إلى مورد")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من خزينة إلى مورد مع التحقق من صحة التقرير وإضافة العملية.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من بنك إلى خزينة")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من حساب بنكي إلى خزينة مع التحقق من حفظ العملية بنجاح.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من بنك إلى بنك")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين حسابين بنكيين مع التحقق من صحة التقرير وإضافة العملية.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من بنك إلى عميل")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من حساب بنكي إلى عميل والتأكد من حفظ العملية بنجاح.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من بنك إلى مورد")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من حساب بنكي إلى مورد مع التحقق من صحة البيانات والتقرير.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من عميل إلى خزينة")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من عميل إلى خزينة والتأكد من تسجيلها بنجاح.")
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
    @Feature("التحويلات والتسويات")
    @Story("تحويل من عميل إلى بنك")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من عميل إلى حساب بنكي مع التحقق من حفظها.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من عميل إلى عميل")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين حسابي عميل والتأكد من نجاح العملية.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من عميل إلى مورد")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من عميل إلى مورد مع التحقق من صحة التقرير وإضافة العملية.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من مورد إلى خزينة")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من مورد إلى خزينة والتأكد من حفظها بنجاح.")
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

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من مورد إلى بنك")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من مورد إلى حساب بنكي مع التحقق من صحة العملية.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من مورد إلى عميل")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل من مورد إلى عميل والتأكد من تسجيلها بنجاح.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("تحويل من مورد إلى مورد")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين موردين مع التحقق من حفظ العملية وإضافتها بنجاح.")
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

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("حذف جميع عمليات التحويل والتسوية")
    @Description("يتحقق هذا الاختبار من حذف جميع عمليات التحويل والتسوية الموجودة داخل النظام والتأكد من نجاح عملية الحذف.")
    @Test(priority = 19)
    public void Delete(){
        TransfersAndSettlementsPage transfersAndSettlementsPage = new TransfersAndSettlementsPage(driver);
                transfersAndSettlementsPage.NavigationHelper(Pages.TRANSACTIONS);

        transfersAndSettlementsPage.DeleteAll();

    }
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("التحقق من منع التحويل لنفس الحساب")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة التحويل بين نفس الحساب كمصدر ووجهة.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("التحقق من رفض قيمة تحويل سالبة")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند إدخال قيمة تحويل سالبة ومنع حفظ العملية.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويلات والتسويات")
    @Story("التحقق من إلزامية اختيار جميع أطراف التحويل")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة حفظ عملية تحويل بدون استكمال جميع بيانات أطراف التحويل.")
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
