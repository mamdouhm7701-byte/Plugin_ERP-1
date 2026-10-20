package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("الإيرادات الأخرى")
@Owner("MAMDOUH")
public class OtherRevenueMovementTest extends FinancialScenarioTest {
    @Override
    protected FinancialMovementKind kind() {
        return FinancialMovementKind.INCOME;
    }

    @Override
    protected FinancialMovementPage movementPage() {
        return new OtherRevenueMovementPage(driver);
    }

    @Test(groups = "financial")
    @Story("إضافة حركة إيراد مع الحفظ والطباعة والتحقق من التقرير")
    public void AddOtherRevenueMovementWithSaveAndPrint() {
        create(true, false, false, false, "1000");
    }

    @Test(groups = "financial")
    @Story("تعديل حركة إيراد مع الحفظ والطباعة والتحقق من التقرير")
    public void EditOtherRevenueMovement() {
        edit(true, false, true);
    }

    @Test(groups = "financial")
    @Story("إضافة حركة إيراد بالحفظ فقط")
    public void AddOtherRevenueMovementWithSave() {
        create(false, false, false, false, "1000");
    }

    @Test(groups = "financial")
    @Story("حذف حركة إيراد")
    public void DeleteOtherRevenueMovement() {
        deleteOwn();
    }

    @Test(groups = "financial")
    @Story("التحقق - نوع الإيراد إلزامي")
    public void AssertTypeRequiredErrorMessage() {
        invalid("party");
    }

    @Test(groups = "financial")
    @Story("التحقق - المبلغ إلزامي في حركة الإيراد")
    public void AssertAmountRequiredErrorMessage() {
        invalid("value");
    }

    @Test(groups = "financial")
    @Story("التحقق - طريقة الدفع إلزامية في حركة الإيراد")
    public void AssertPaymentMethodRequiredErrorMessage() {
        invalid("payment");
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
