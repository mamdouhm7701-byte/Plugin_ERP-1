/* شرح الملف: اختبارات حركة المصروفات: تعديل حركة مصروف مع الضريبة والحفظ والطباعة ونتأكد من
 * الحسابات؛ حذف حركة مصروف؛ رفض مبلغ سالب؛ تصدير قائمة الحركات إلى Excel؛ إضافة حركة مصروف مع
 * الضريبة والحفظ والطباعة ونتأكد من الحسابات.
 */
package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("حركة المصروفات")
@Owner("MAMDOUH")
public class ExpenseMovementTest extends FinancialScenarioTest {
    @Override
    protected FinancialMovementKind kind() {
        return FinancialMovementKind.EXPENSE;
    }

    @Override
    protected FinancialMovementPage movementPage() {
        return new ExpenseMovementPage(driver);
    }

    @Test(groups = "financial")
    @Story("إضافة حركة مصروف مع الضريبة والحفظ والطباعة والتحقق من الحسابات")
    public void AddExpenseMovementWithSelectTax_AssertValue() {
        create(true, false, true, false, "1000");
    }

    @Test(groups = "financial")
    @Story("تعديل حركة مصروف مع الضريبة والحفظ والطباعة والتحقق من الحسابات")
    public void EditExpenseMovementWithSelectTax_AssertValue() {
        edit(true, true);
    }

    @Test(groups = "financial")
    @Story("إضافة حركة مصروف بنوع حساب محاسبي والحفظ")
    public void AddExpenseMovementWithSelectAccountantAndClickSave() {
        create(false, false, false, true, "1000");
    }

    @Test(groups = "financial")
    @Story("إضافة حركة مصروف بنوع مصروف والحفظ")
    public void AddExpenseMovementWithSelectExpenseAndClickSave() {
        create(false, false, false, false, "1000");
    }

    @Test(groups = "financial")
    @Story("حذف حركة مصروف")
    public void DeleteExpenseMovement() {
        deleteOwn();
    }

    @Test(groups = "financial")
    @Story("التحقق - نوع المصروف إلزامي")
    public void AssertTypeExpenseErrorMessage() {
        invalid("party");
    }

    @Test(groups = "financial")
    @Story("التحقق - طريقة الدفع إلزامية في حركة المصروف")
    public void AssertPaymentMethodErrorMessage() {
        invalid("payment");
    }

    @Test(groups = "financial")
    @Story("التحقق - خيار طريقة الدفع إلزامي في حركة المصروف")
    public void AssertPaymentOptionMethodErrorMessage() {
        invalid("paymentOption");
    }

    @Test(groups = "financial")
    @Story("التحقق - المبلغ إلزامي في حركة المصروف")
    public void AssertValueErrorMessage() {
        invalid("value");
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
