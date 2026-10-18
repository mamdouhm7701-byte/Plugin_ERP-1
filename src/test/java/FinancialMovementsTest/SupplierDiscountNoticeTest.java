package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud") @Feature("إشعار خصم مورد") @Owner("MAMDOUH")
public class SupplierDiscountNoticeTest extends FinancialScenarioTest {
  @Override protected FinancialMovementKind kind() {return FinancialMovementKind.SUPPLIER_DISCOUNT;}
  @Override protected FinancialMovementPage movementPage() {return new SupplierDiscountNoticePage(driver);}

  @Test(groups="financial") @Story("إضافة إشعار خصم مورد مع التحقق من الحسابات والتقرير")
  public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndReport() { create(true,false,true,false,"100"); }

  @Test(groups="financial") @Story("تعديل إشعار خصم مورد")
  public void EditSupplierDiscountNoticeAnd_VerifyCalculationsAndReport() { edit(true,true); }

  @Test(groups="financial") @Story("إضافة إشعار خصم مورد مع سند مخصص")
  public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndCustom() { create(false,true,true,false,"10"); }

  @Test(groups="financial") @Story("إضافة إشعار خصم مورد وحفظه مباشرة")
  public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndSave() { create(false,false,true,false,"100"); }

  @Test(groups="financial") @Story("حذف إشعار خصم مورد مملوك للاختبار")
  public void Delete() { deleteOwn(); }

  @Test(groups="financial") @Story("التحقق من عدم الحفظ ببيانات تاريخ غير صحيحة")
  public void InvalidDate() { invalid("date"); }

  @Test(groups="financial") @Story("التحقق من إلزامية اختيار المورد")
  public void InvalidAgentName() { invalid("party"); }

  @Test(groups="financial") @Story("التحقق من إلزامية إدخال قيمة الخصم")
  public void InvalidValue() { invalid("value"); }

  @Override @Test(groups="financial") public void FM_CloseNewWithoutSaving() {super.FM_CloseNewWithoutSaving();}
  @Override @Test(groups="financial") public void FM_CancelEditWithoutSaving() {super.FM_CancelEditWithoutSaving();}
  @Override @Test(groups="financial") public void FM_CancelDelete() {super.FM_CancelDelete();}
  @Override @Test(groups="financial") public void FM_RejectNegativeAmount() {super.FM_RejectNegativeAmount();}
  @Override @Test(groups="financial") public void FM_RejectZeroAmount() {super.FM_RejectZeroAmount();}
  @Override @Test(groups="financial") public void FM_ListStructure() {super.FM_ListStructure();}
  @Override @Test(groups="financial") public void FM_ExportPdf() {super.FM_ExportPdf();}
  @Override @Test(groups="financial") public void FM_ExportExcel() {super.FM_ExportExcel();}
}
