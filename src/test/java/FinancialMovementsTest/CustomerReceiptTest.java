package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("سند قبض عميل")
@Owner("MAMDOUH")
public class CustomerReceiptTest extends FinancialScenarioTest {
    @Override
    protected FinancialMovementKind kind() {
        return FinancialMovementKind.RECEIPT;
    }

    @Override
    protected FinancialMovementPage movementPage() {
        return new CustomerReceiptPage(driver);
    }

    @Test(groups = "financial")
    @Story("إضافة سند قبض عميل مع توزيع على الفواتير")
    public void AddCustomerWithSaveAndCustomerReceiptTest() {
        create(false, true, false, false, "100");
    }

    @Test(groups = "financial")
    @Story("إضافة سند قبض عميل بالحفظ فقط")
    public void AddResetWithSaveTest() {
        create(false, false, false, false, "1000");
    }

    @Test(groups = "financial")
    @Story("إضافة سند قبض عميل مع الحفظ والطباعة والتحقق من التقرير")
    public void AddNewResetWithSaveAndPrint() {
        create(true, false, false, false, "1500.01");
    }

    @Test(groups = "financial")
    @Story("تعديل سند قبض عميل مع الحفظ والطباعة والتحقق من التقرير")
    public void EditeAfterReportTest() {
        edit(true, false);
    }

    @Test(groups = "financial")
    @Story("حذف سند قبض عميل")
    public void DeleteCustomerReceiptTest() {
        deleteOwn();
    }

    @Test(groups = "financial")
    @Story("التحقق - العميل إلزامي في سند القبض")
    public void AssertRequiredErrorsMessageForAgent() {
        invalid("party");
    }

    @Test(groups = "financial")
    @Story("التحقق - المبلغ وطريقة الدفع إلزاميان في سند القبض")
    public void AssertRequiredErrorsMessageForAmount() {
        invalid("value");
    }

    @Test(groups = "financial")
    @Story("التحقق - طريقة الدفع إلزامية في سند القبض")
    public void AssertRequiredErrorsMessageForPaymentMethod() {
        invalid("payment");
    }

    @Test(groups = "financial")
    @Story("التحقق - خيار طريقة الدفع إلزامي في سند القبض")
    public void AssertRequiredErrorsMessageForPaymentMethodOption() {
        invalid("paymentOption");
    }

    @Test(groups = "financial")
    @Story("التحقق - خطأ عند إدخال تاريخ قديم في سند القبض")
    public void AddOldDate() {
        invalid("date");
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
