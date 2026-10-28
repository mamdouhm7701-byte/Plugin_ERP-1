/* شرح الملف: اختبارات اشعار خصم عميل: تعديل إشعار خصم عميل؛ حذف إشعار خصم عميل؛ رفض مبلغ سالب؛
 * تصدير قائمة الحركات إلى Excel؛ إغلاق الحركة الجديدة دون حفظ.
 */
package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("إشعار خصم عميل")
@Owner("MAMDOUH")
public class CustomerDiscountNoticeTest extends FinancialScenarioTest {
    @Override
    protected FinancialMovementKind kind() {
        return FinancialMovementKind.CUSTOMER_DISCOUNT;
    }

    @Override
    protected FinancialMovementPage movementPage() {
        return new CustomerDiscountNoticePage(driver);
    }

    @Test(groups = "financial")
    @Story("إضافة إشعار خصم عميل")
    public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndReport() {
        create(true, false, true, false, "100");
    }

    @Test(groups = "financial")
    @Story("تعديل إشعار خصم عميل")
    public void EditCustomerDiscountNoticeAnd_VerifyCalculationsAndReport() {
        edit(true, true);
    }

    @Test(groups = "financial")
    @Story("إضافة إشعار خصم عميل مع سند مخصص")
    public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndCustom() {
        create(false, true, true, false, "10");
    }

    @Test(groups = "financial")
    @Story("إضافة إشعار خصم عميل")
    public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndSave() {
        create(false, false, true, false, "100");
    }

    @Test(groups = "financial")
    @Story("حذف إشعار خصم عميل")
    public void Delete() {
        deleteOwn();
    }

    @Test(groups = "financial")
    @Story("التحقق - إضافة إشعار خصم عميل بتاريخ غير صالح")
    public void InvalidDate() {
        invalid("date");
    }

    @Test(groups = "financial")
    @Story("التحقق - إضافة إشعار خصم عميل باسم مندوب غير صالح")
    public void InvalidAgentName() {
        invalid("party");
    }

    @Test(groups = "financial")
    @Story("التحقق - إضافة إشعار خصم عميل بقيمة غير صالحة")
    public void InvalidValue() {
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
