/* شرح الملف: اختبارات تحويلات وتسويات: تعديل تحويل وتسوية؛ حذف تحويل وتسوية خاصين بالتجربة
 * للاختبار؛ نتأكد من رفض قيمة تحويل سالبة؛ تصدير قائمة الحركات إلى Excel؛ تحويل 125.50 من خزنة إلى
 * بنك بهويتين مستقلتين.
 */
package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("التحويلات والتسويات")
@Owner("MAMDOUH")
public class TransfersAndSettlementsTest extends FinancialScenarioTest {
    @Override
    protected FinancialMovementKind kind() {
        return FinancialMovementKind.TRANSFER;
    }

    @Override
    protected FinancialMovementPage movementPage() {
        return new TransfersAndSettlementsPage(driver);
    }

    @Test(groups = "financial")
    @Story("إضافة تحويل وتسوية جديد")
    public void AddNewTransferAndSettlement_SaveAndPrint_AndAssertResults() {
        transfer("Safe", "Bank", true);
    }

    @Test(groups = "financial")
    @Story("تعديل تحويل وتسوية")
    public void EditTransferAndSettlement_SaveAndPrint_AndAssertResults() {
        edit(true, false);
    }

    @Test(groups = "financial")
    @Story("تحويل من خزينة إلى خزينة")
    public void AddFromCashToCash_SaveAndPrint_AndAssertResults() {
        transfer("Safe", "Safe", false);
    }

    @Test(groups = "financial")
    @Story("تحويل من خزينة إلى بنك")
    public void AddFromCashToBank_SaveAndPrint_AndAssertResults() {
        transfer("Safe", "Bank", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من خزينة إلى عميل")
    public void AddFromCashToAgent_SaveAndPrint_AndAssertResults() {
        transfer("Safe", "Customer", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من خزينة إلى مورد")
    public void FromCashToSupplier() {
        transfer("Safe", "Supplier", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من بنك إلى خزينة")
    public void fromBankToCash() {
        transfer("Bank", "Safe", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من بنك إلى بنك")
    public void fromBankToBank() {
        transfer("Bank", "Bank", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من بنك إلى عميل")
    public void fromBankToAgent() {
        transfer("Bank", "Customer", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من بنك إلى مورد")
    public void fromBankToSupplier() {
        transfer("Bank", "Supplier", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من عميل إلى خزينة")
    public void fromAgentToCash() {
        transfer("Customer", "Safe", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من عميل إلى بنك")
    public void fromAgentToBank() {
        transfer("Customer", "Bank", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من عميل إلى عميل")
    public void fromAgentToAgent() {
        transfer("Customer", "Customer", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من عميل إلى مورد")
    public void fromAgentToSupplier() {
        transfer("Customer", "Supplier", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من مورد إلى خزينة")
    public void fromSupplierToCash() {
        transfer("Supplier", "Safe", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من مورد إلى بنك")
    public void fromSupplierToBank() {
        transfer("Supplier", "Bank", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من مورد إلى عميل")
    public void fromSupplierToAgent() {
        transfer("Supplier", "Customer", true);
    }

    @Test(groups = "financial")
    @Story("تحويل من مورد إلى مورد")
    public void fromSupplierToSupplier() {
        transfer("Supplier", "Supplier", true);
    }

    @Test(groups = "financial")
    @Story("حذف تحويل وتسوية مملوكين للاختبار")
    public void Delete() {
        deleteOwn();
    }

    @Test(groups = "financial")
    @Story("التحقق من منع التحويل لنفس الحساب")
    public void AssertTypeDontSendToSameType() {
        invalid("sameParty");
    }

    @Test(groups = "financial")
    @Story("التحقق من رفض قيمة تحويل سالبة")
    public void AssertValueErorrMessage() {
        invalid("value");
    }

    @Test(groups = "financial")
    @Story("التحقق من إلزامية اختيار جميع أطراف التحويل")
    public void AssertselectEverySideOption() {
        invalid("toParty");
    }

    @Override
    @Test(groups = "financial")
    public void FM_CloseNewWithoutSaving() {
        super.FM_CloseNewWithoutSaving();
    }

    @Override
    @Test(groups = "financial")
    public void FM_CancelEditWithoutSaving() {
        super.FM_CancelEditWithoutSaving();
    }

    @Override
    @Test(groups = "financial")
    public void FM_CancelDelete() {
        super.FM_CancelDelete();
    }

    @Override
    @Test(groups = "financial")
    public void FM_RejectNegativeAmount() {
        super.FM_RejectNegativeAmount();
    }

    @Override
    @Test(groups = "financial")
    public void FM_RejectZeroAmount() {
        super.FM_RejectZeroAmount();
    }

    @Override
    @Test(groups = "financial")
    public void FM_ListStructure() {
        super.FM_ListStructure();
    }

    @Override
    @Test(groups = "financial")
    public void FM_ExportPdf() {
        super.FM_ExportPdf();
    }

    @Override
    @Test(groups = "financial")
    public void FM_ExportExcel() {
        super.FM_ExportExcel();
    }
}
