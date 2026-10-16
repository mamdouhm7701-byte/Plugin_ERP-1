package Invoice;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.Invoice.Invoice;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.System_Modules.Login.purches.ReturnOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class InvoiceTest extends BaseTest {


    @Test
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("فواتير المبيعات")
    @Story("إنشاء فاتورة مبيعات آجلة والتحقق من الإجماليات")
    @Description("ينشئ فاتورة مبيعات آجلة، ويتحقق من صحة بيانات الأصناف والإجماليات التجارية.")
    public void AddInvoice() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("آجل", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.SelectFromDropDawn("قائمه اسعار", "قوائم الأسعار");
        invoice.ClickAddItemsBtn();
        invoice.ClickPlusButtonByRow(2, 2);
        invoice.ClickSaveForProductBtn();
        invoice.validateInvoiceAllBusiness();
    }


    @Test
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("فواتير المبيعات")
    @Story("إنشاء فاتورة نقدية والتحقق من التقرير")
    @Description("ينشئ فاتورة مبيعات نقدية، ويتحقق من بيانات الأصناف والإجماليات في التقرير المطبوع.")
    public void AddInvoiceCash() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("نقدي", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.SelectFromDropDawn("قائمه اسعار", "قوائم الأسعار");
        invoice.ClickAddItemsBtn();
        invoice.ClickPlusButtonByRow(2, 2);
        invoice.ClickSaveForProductBtn();
        // ================= Business Validation =================
        InvoiceTotals totals = invoice.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
                invoice. GetNamesQuantitiesAndPricesForInv();

        invoice.ClickOtherPay();
        invoice.SelectFromDropDawn("طريقة دفع", "طريقة الدفع");
        invoice.ClickPlusToAddPay();
        invoice.ClickSaveAndPrintButton();
        invoice.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        invoice.validateReport(totals);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("فواتير المبيعات")
    @Story("إضافة أصناف الفاتورة بالبحث عن الاسم")
    @Description("يضيف أصناف الفاتورة بالاسم ويدخل الكميات والأسعار ثم يتحقق من إجمالياتها قبل الحفظ.")
    public void AddPurchesBillsByNameTest() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("آجل", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickShift();
        invoice.ClickOnNameRadioBtn();
        invoice.SearchAndSelectProduct("سكر");
        invoice.SetValueByRow("السعر", 1, 200);
        invoice.SearchAndSelectProduct("كرواسون");
        invoice.SetValueByRow("السعر", 2, 200);
        invoice.SearchAndSelectProduct("دقيق");
        invoice.SetValueByRow("السعر", 3, 200);
        invoice.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        invoice.validateInvoiceAllBusiness();
        invoice.ClickSaveButton();

    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("فواتير المبيعات")
    @Story("إضافة أصناف الفاتورة باستخدام الباركود")
    @Description("يضيف الأصناف بالباركود، ويطبق خصمًا، ثم يتحقق من الإجماليات وبيانات التقرير.")
    public void AddPurchesBillsByBarcodTest() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("نقدي", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.ClickOnBarcodeRadioBtn();
        invoice.Search("5638351925");
        invoice.ClickEnter();
        invoice.Search("9365129801");
        invoice.ClickEnter();
        invoice.Search("9451630809");
        invoice.ClickEnter();
        invoice.SetQuantitiesForRows(1, 10);
        invoice.SetValueByRow("السعر", 1, 200);
        invoice.AddDiscountPercntge("10");
        // ================= Business Validation =================
        InvoiceTotals totals = invoice.validateBusinessTotals();
        Map<String, String> dataBeforeSave =
                invoice.GetNamesQuantitiesAndPricesForInv();
        invoice.ClickOtherPay();
        invoice.SelectFromDropDawn("طريقة دفع", "طريقة الدفع");
        invoice.ClickPlusToAddPay();
        invoice.ClickSaveAndPrintButton();
        invoice.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        invoice.validateReport(totals);
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("فواتير المبيعات")
    @Story("حفظ فاتورة المبيعات بزر الحفظ")
    @Description("ينشئ فاتورة بأصناف مختارة بالاسم ويتحقق من إجمالياتها ثم يحفظها.")
    public void AddPurchesBillsWithSaveBuutonTest() {
        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);

        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickOnNameRadioBtn();
        invoice.SearchAndSelectProduct("سكر");
        invoice.SearchAndSelectProduct("كرواسون");
        invoice.SearchAndSelectProduct("دقيق");
        invoice.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        invoice.SetValueByRow("السعر", 1, 200);
        invoice.SetValueByRow("السعر", 2, 200);
        invoice.SetValueByRow("السعر", 3, 200);
        // ================= Business Validation =================
        InvoiceTotals totals = invoice.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
                invoice.GetNamesQuantitiesAndPricesForInv();
        invoice.ClickCash();
        invoice.ClickPlusToAddPay();
        invoice.ClickSaveButton();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("فواتير المبيعات")
    @Story("منع حفظ فاتورة بدون أصناف")
    @Description("يتحقق من أن زر الحفظ غير متاح عندما لا تحتوي الفاتورة على أي صنف.")
    public void AddWithOutProduct() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        driver.navigate().refresh();
        Assert.assertTrue(invoice.AssertSaveButtonIsNotChecked());
    }
}


