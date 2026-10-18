package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud") @Feature("سند صرف مورد") @Owner("MAMDOUH")
public class SupplierBillOfExchangeTest extends FinancialScenarioTest {
  @Override protected FinancialMovementKind kind() {return FinancialMovementKind.PAYMENT;}
  @Override protected FinancialMovementPage movementPage() {return new SupplierBillOfExchangePage(driver);}

  @Test(groups="financial") @Story("إضافة سند صرف مورد مع توزيع على الفواتير")
  public void AddSupplierBillOfExchangeTest() { create(false,true,false,false,"10"); }

  @Test(groups="financial") @Story("إضافة سند صرف مورد بالحفظ فقط")
  public void AddSupplierBillWithSaveTest() { create(false,false,false,false,"1000"); }

  @Test(groups="financial") @Story("إضافة سند صرف مورد مع الحفظ والطباعة والتحقق من التقرير")
  public void AddSupplierBillWithSaveAndPrint() { create(true,false,false,false,"1500.01"); }

  @Test(groups="financial") @Story("تعديل سند صرف مورد مع الحفظ والطباعة والتحقق من التقرير")
  public void EditSupplierBillAfterReport() { edit(true,false); }

  @Test(groups="financial") @Story("حذف سند صرف مورد")
  public void DeleteSupplierBillTest() { deleteOwn(); }

  @Test(groups="financial") @Story("التحقق - المورد إلزامي في سند الصرف")
  public void AssertRequiredErrorsMessageForSupplier() { invalid("party"); }

  @Test(groups="financial") @Story("التحقق - المبلغ وطريقة الدفع إلزاميان في سند الصرف")
  public void AssertRequiredErrorsMessageForAmount() { invalid("value"); }

  @Test(groups="financial") @Story("التحقق - طريقة الدفع إلزامية في سند الصرف")
  public void AssertRequiredErrorsMessageForPaymentMethod() { invalid("payment"); }

  @Test(groups="financial") @Story("التحقق - خيار طريقة الدفع إلزامي في سند الصرف")
  public void AssertRequiredErrorsMessageForPaymentMethodOption() { invalid("paymentOption"); }

  @Test(groups="financial") @Story("التحقق - خطأ عند إدخال تاريخ قديم في سند الصرف")
  public void AddOldDate() { invalid("date"); }

  @Override @Test(groups="financial") public void FM_CloseNewWithoutSaving() {super.FM_CloseNewWithoutSaving();}
  @Override @Test(groups="financial") public void FM_CancelEditWithoutSaving() {super.FM_CancelEditWithoutSaving();}
  @Override @Test(groups="financial") public void FM_CancelDelete() {super.FM_CancelDelete();}
  @Override @Test(groups="financial") public void FM_RejectNegativeAmount() {super.FM_RejectNegativeAmount();}
  @Override @Test(groups="financial") public void FM_RejectZeroAmount() {super.FM_RejectZeroAmount();}
  @Override @Test(groups="financial") public void FM_ListStructure() {super.FM_ListStructure();}
  @Override @Test(groups="financial") public void FM_ExportPdf() {super.FM_ExportPdf();}
  @Override @Test(groups="financial") public void FM_ExportExcel() {super.FM_ExportExcel();}
}
