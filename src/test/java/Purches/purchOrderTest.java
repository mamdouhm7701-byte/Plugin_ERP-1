package Purches;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.System_Modules.Login.purches.purchOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

import java.util.Map;

public class purchOrderTest extends BaseTest {

    @Test
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("أوامر الشراء")
    @Story("إنشاء أمر شراء نقدي والتحقق من الإيصال")
    @Description("ينشئ أمر شراء نقديًا، ويتحقق من إجمالياته وتفاصيل الإيصال بعد الحفظ والطباعة.")
    public void purchOrderTest(){


            purchOrderPage purchOrderpage = new purchOrderPage(driver);

        purchOrderpage.NavigationHelper(Pages.PURCHORDER);
        purchOrderpage.AddDate();
            purchOrderpage.SelectFromDropDawn("نقدي", "نوع السداد");
            purchOrderpage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
            purchOrderpage.ClickShift();
            purchOrderpage.SelectByKeys("محمد ممدوح");
            purchOrderpage.SelectFromDropDawn("تست", "مندوب");
            purchOrderpage.ClickAddItemsBtn();
            purchOrderpage.ClickPlusButtonByRow(2, 2);
            purchOrderpage.ClickSaveForProductBtn();


            // ================= Business Validation =================
            InvoiceTotals totals = purchOrderpage.validateBusinessTotalsForPurchOrder();

            Map<String, String> dataBeforeSave =
                    purchOrderpage. GetNamesQuantitiesAndPricesForPurchOrder();
            purchOrderpage.ClickCash();
            purchOrderpage.ClickPlusToAddPay();
            purchOrderpage.ClickSaveAndPrintButton();
            purchOrderpage.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
            purchOrderpage.validateReport(totals);

            // ================= Receipt Validation =================

            purchOrderpage.NavigationHelper(Pages.PURCHESINVOICELIST);

            purchOrderpage.AssertReceiptAdded("محمد ممدوح");
            purchOrderpage.AssertReceiptAdded("نقدي");
        }



    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("أوامر الشراء")
    @Story("إضافة أصناف أمر الشراء بالباركود")
    @Description("يضيف صنفًا إلى أمر الشراء بالباركود، ويدخل الكمية والسعر، ثم يتحقق من الإيصال.")
    public void AddPurchesOrderByBarcodTest() {

        purchOrderPage purchOrderpage = new purchOrderPage(driver);

        purchOrderpage.NavigationHelper(Pages.PURCHORDER);
        purchOrderpage.AddDate();
        purchOrderpage.SelectFromDropDawn("نقدي", "نوع السداد");
        purchOrderpage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        purchOrderpage.ClickShift();
        purchOrderpage.SelectByKeys("محمد ممدوح");
        purchOrderpage.SelectFromDropDawn("تست", "مندوب");
        purchOrderpage.ClickOnBarcodeRadioBtn();
        purchOrderpage.Search("8218648422");
        purchOrderpage.ClickEnter();
        purchOrderpage.SetQuantitiesForRows(1, 10);
        purchOrderpage.SetValueByRow("السعر", 1, 200);


        // ================= Business Validation =================
        InvoiceTotals totals = purchOrderpage.validateBusinessTotalsForPurchOrder();

        Map<String, String> dataBeforeSave =
                purchOrderpage. GetNamesQuantitiesAndPricesForPurchOrder();

        purchOrderpage.ClickOtherPay();
        purchOrderpage.SelectFromDropDawn("تست", "طريقة الدفع");
        purchOrderpage.ClickPlusToAddPay();
        purchOrderpage.ClickSaveAndPrintButton();
        purchOrderpage.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        purchOrderpage.validateReport(totals);

        purchOrderpage.NavigationHelper(Pages.PURCHESINVOICELIST);

        purchOrderpage.AssertReceiptAdded("محمد ممدوح");
        purchOrderpage.AssertReceiptAdded("نقدي");
    }
    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("أوامر الشراء")
    @Story("إضافة أصناف أمر الشراء بالاسم")
    @Description("يضيف أصناف أمر الشراء بالاسم ويتحقق من عملية الحفظ وبيانات الإيصال.")
    public void AddPurchesOrderByNameTest() {

        purchOrderPage purchOrderpage = new purchOrderPage(driver);

        purchOrderpage.NavigationHelper(Pages.PURCHORDER);
        purchOrderpage.SelectFromDropDawn("آجل", "نوع السداد");
        purchOrderpage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        purchOrderpage.ClickShift();
        purchOrderpage.SelectByKeys("محمد ممدوح");
        purchOrderpage.ClickOnNameRadioBtn();
        purchOrderpage.SearchAndSelectProduct("سكر");
        purchOrderpage.SearchAndSelectProduct("كرواسون");
        purchOrderpage.SearchAndSelectProduct("دقيق");
        purchOrderpage.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        purchOrderpage.SetValueByRow("السعر", 1, 200);
        purchOrderpage.SetValueByRow("السعر", 2, 200);
        purchOrderpage.SetValueByRow("السعر", 3, 200);
        purchOrderpage.validateBusinessTotalsForPurchOrder();
        purchOrderpage.ClickSaveButton();
        purchOrderpage.NavigationHelper(Pages.PURCHESINVOICELIST);

        purchOrderpage.AssertReceiptAdded("محمد ممدوح");
    }

    @Test(priority = 4)
    public void DeleteAll() {

        purchOrderPage purchOrderpage = new purchOrderPage(driver);

        purchOrderpage.NavigationHelper(Pages.PURCHESINVOICELIST);
        purchOrderpage.DeleteAll();
    }

    @Test(priority = 5)
    public void AddWithOutSupplier() {

        purchOrderPage purchOrderpage = new purchOrderPage(driver);

        purchOrderpage.NavigationHelper(Pages.PURCHORDER);

        purchOrderpage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        purchOrderpage.ClickShift();
        purchOrderpage.ClickOnNameRadioBtn();
        purchOrderpage.SearchAndSelectProduct("سكر");
        purchOrderpage.SelectFromDropDawn("آجل", "نوع السداد");
        purchOrderpage.ClickSaveButton();
        Assert.assertTrue(purchOrderpage.isErrorPopupDisplayed());
    }

    @Test(priority = 6)
    public void AddWithOutProduct() {

        purchOrderPage purchOrderpage = new purchOrderPage(driver);

        purchOrderpage.NavigationHelper(Pages.PURCHORDER);
        driver.navigate().refresh();
        purchOrderpage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        purchOrderpage.ClickShift();
        purchOrderpage.SelectByKeys("محمد ممدوح");
        purchOrderpage.SelectFromDropDawn("آجل", "نوع السداد");
        Assert.assertTrue(purchOrderpage.AssertSaveButtonIsNotChecked());
    }

    @Test(priority = 7)
    public void TransToPurchaseBill() {
        purchOrderPage purchOrderpage = new purchOrderPage(driver);
        purchOrderpage.NavigationHelper(Pages.PURCHORDER);
        purchOrderpage.SelectFromDropDawn("آجل", "نوع السداد");
        purchOrderpage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        purchOrderpage.ClickShift();
        purchOrderpage.SelectByKeys("محمد ممدوح");
        purchOrderpage.ClickOnNameRadioBtn();
        purchOrderpage.SearchAndSelectProduct("سكر");
        purchOrderpage.SearchAndSelectProduct("كرواسون");
        purchOrderpage.SearchAndSelectProduct("دقيق");
        purchOrderpage.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        purchOrderpage.SetValueByRow("السعر", 1, 200);
        purchOrderpage.SetValueByRow("السعر", 2, 200);
        purchOrderpage.SetValueByRow("السعر", 3, 200);
        purchOrderpage.validateBusinessTotalsForPurchOrder();
        purchOrderpage.ClickSaveButton();
        purchOrderpage.NavigationHelper(Pages.PURCHESINVOICELIST);
        purchOrderpage.clickToTransToPurchesOrder();
        InvoiceTotals totals = purchOrderpage.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
                purchOrderpage.GetNamesQuantitiesAndPrices();

        purchOrderpage.ClickOtherPay();
        purchOrderpage.SelectFromDropDawn("تست", "طريقة الدفع");
        purchOrderpage.ClickPlusToAddPay();
        purchOrderpage.ClickSaveAndPrintButton();
        purchOrderpage.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        purchOrderpage.validateReport(totals);

        purchOrderpage.NavigationHelper(Pages.PURCHESINVOICELIST);

        purchOrderpage.AssertReceiptAdded("محمد ممدوح");
        purchOrderpage.AssertReceiptAdded("نقدي");

    }

}
