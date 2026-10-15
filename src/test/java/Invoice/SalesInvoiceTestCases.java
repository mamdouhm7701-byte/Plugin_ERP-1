package Invoice;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.Invoice.Invoice;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import java.util.Map;

/** Sales invoice cases mapped to the IDs in Sales_Invoice_Test_Cases_for_Codex.xlsx. */
public class SalesInvoiceTestCases extends BaseTest {

    private Invoice openInvoice() {
        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        return invoice;
    }

    private void selectWarehouseAndItemsFromDialog(Invoice invoice) {
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickAddItemsBtn();
        invoice.ClickPlusButtonByRow(2, 2);
        invoice.ClickSaveForProductBtn();
        Assert.assertFalse(invoice.getInvoiceRows().isEmpty(), "لم تتم إضافة أصناف إلى الفاتورة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إنشاء فاتورة مبيعات ببيانات كاملة صحيحة")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مبيعات ببيانات كاملة صحيحة.")
    @Test(priority = 1, description = "TC-SI-001: إنشاء فاتورة مبيعات ببيانات كاملة صحيحة")
    public void TC_SI_001_CreateCompleteSalesInvoice() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("نقدي", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.SelectFromDropDawn("قائمه اسعار", "قوائم الأسعار");
        invoice.ClickAddItemsBtn();
        invoice.ClickPlusButtonByRow(2, 2);
        invoice.ClickSaveForProductBtn();
        Assert.assertFalse(invoice.getInvoiceRows().isEmpty(), "الفاتورة لا تحتوي على أصناف");
        invoice.ClickOtherPay();
        invoice.SelectFromDropDawn("طريقة دفع", "طريقة الدفع");
        invoice.ClickPlusToAddPay();
        invoice.validateBusinessTotals();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إضافة صنف بالبحث باستخدام الباركود")
    @Description("يتحقق هذا الاختبار من إضافة صنف بالبحث باستخدام الباركود.")
    @Test(priority = 2, description = "TC-SI-011: إضافة صنف بالبحث باستخدام الباركود")
    public void TC_SI_011_AddItemByBarcode() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("آجل", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.ClickOnBarcodeRadioBtn();
        invoice.Search("5638351925");
        invoice.ClickEnter();
        Assert.assertFalse(invoice.getInvoiceRows().isEmpty(), "لم تتم إضافة صنف بالباركود");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إضافة صنف بالبحث باستخدام الاسم")
    @Description("يتحقق هذا الاختبار من إضافة صنف بالبحث باستخدام الاسم.")
    @Test(priority = 3, description = "TC-SI-012: إضافة صنف بالبحث باستخدام الاسم")
    public void TC_SI_012_AddItemByArabicName() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickOnNameRadioBtn();
        invoice.SearchAndSelectProduct("سكر");
        Assert.assertTrue(
                invoice.getInvoiceRows().stream().anyMatch(row -> row.getItemName().contains("سكر")),
                "الصنف الذي تم البحث عنه غير موجود في جدول الفاتورة"
        );
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إضافة أصناف من زر إضافة الأصناف")
    @Description("يتحقق هذا الاختبار من إضافة أصناف من زر إضافة الأصناف.")
    @Test(priority = 4, description = "TC-SI-013: إضافة أصناف من زر إضافة الأصناف")
    public void TC_SI_013_AddItemsFromDialog() {
        Invoice invoice = openInvoice();
        selectWarehouseAndItemsFromDialog(invoice);
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من فلتر الفئة في نافذة إضافة الأصناف")
    @Description("يتحقق هذا الاختبار من التحقق من فلتر الفئة في نافذة إضافة الأصناف.")
    @Test(priority = 5, description = "TC-SI-014: التحقق من فلتر الفئة في نافذة إضافة الأصناف")
    public void TC_SI_014_FilterItemsByCategory() {
        Invoice invoice = openInvoice();
        invoice.ClickAddItemsBtn();
        try {
            invoice.SelectFromDropDawn("كراسى", "الفئة");
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15)).until(d -> {
                java.util.List<String> visibleRows = d.findElements(By.cssSelector(".modal-content tbody tr"))
                        .stream().filter(org.openqa.selenium.WebElement::isDisplayed)
                        .map(org.openqa.selenium.WebElement::getText).map(String::trim)
                        .filter(text -> !text.isEmpty() && !text.contains("لا يوجد بيانات"))
                        .toList();
                return !visibleRows.isEmpty();
            });
            java.util.List<String> rows = invoice.getAddItemsDialogRows();
            Assert.assertFalse(rows.isEmpty(), "لم تظهر أصناف بعد اختيار الفئة");
            Assert.assertFalse(rows.isEmpty(), "نتائج الفئة المختارة فارغة");
        } finally {
            invoice.ClickCloseButton();
        }
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("البحث بالباركود داخل نافذة إضافة الأصناف")
    @Description("يتحقق هذا الاختبار من البحث بالباركود داخل نافذة إضافة الأصناف.")
    @Test(priority = 6, description = "TC-SI-015: البحث بالباركود داخل نافذة إضافة الأصناف")
    public void TC_SI_015_SearchByBarcodeInAddItemsDialog() {
        Invoice invoice = openInvoice();
        invoice.ClickAddItemsBtn();
        try {
            invoice.SearchBarcodForInv( "9451630809");
            invoice.ClickEnter();
            Assert.assertTrue(invoice.getAddItemsDialogRows().stream()
                            .anyMatch(row -> row.contains("9451630809") && row.contains("سكر")),
                    "لم يظهر الصنف المرتبط بالباركود المدخل");
        } finally {
            invoice.ClickCloseButton();
        }
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("البحث بالاسم العربي داخل نافذة إضافة الأصناف")
    @Description("يتحقق هذا الاختبار من البحث بالاسم العربي داخل نافذة إضافة الأصناف.")
    @Test(priority = 7, description = "TC-SI-016: البحث بالاسم العربي داخل نافذة إضافة الأصناف")
    public void TC_SI_016_SearchByArabicNameInAddItemsDialog() {
        Invoice invoice = openInvoice();
        invoice.ClickAddItemsBtn();
        try {
            invoice.ClickOnNameRadioBtnForInv("سكر");
            invoice.ClickEnter();
            Assert.assertTrue(invoice.getAddItemsDialogRows().stream().anyMatch(row -> row.contains("سكر")),
                    "لم يظهر الصنف المطابق للاسم العربي المدخل");
        } finally {
            invoice.ClickCloseButton();
        }
    }




    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من زر تكبير الشاشة")
    @Description("يتحقق هذا الاختبار من التحقق من زر تكبير الشاشة.")
    @Test(priority = 10, description = "TC-SI-021: التحقق من زر تكبير الشاشة")
    public void TC_SI_021_ToggleInvoiceFullscreen() {
        Invoice invoice = openInvoice();
        boolean sideMenuWasDisplayed = invoice.isInvoiceSideMenuDisplayed();
        invoice.toggleInvoiceFullscreen();
        try {
            Assert.assertEquals(invoice.isInvoiceSideMenuDisplayed(), !sideMenuWasDisplayed,
                    "زر تكبير الفاتورة لم يبدل حالة القائمة الجانبية");
            Assert.assertTrue(invoice.isCustomerNameFieldDisplayed(),
                    "اختفت عناصر الفاتورة بعد تفعيل وضع تكبير الشاشة");
        } finally {
            if (invoice.isInvoiceSideMenuDisplayed() != sideMenuWasDisplayed) {
                invoice.toggleInvoiceFullscreen();
            }
        }
    }




    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من ظهور مخازن الفرع")
    @Description("يتحقق هذا الاختبار من التحقق من ظهور مخازن الفرع.")
    @Test(priority = 13, description = "TC-SI-005: التحقق من ظهور مخازن الفرع")
    public void TC_SI_005_WarehouseOptionsAreAvailable() {
        Invoice invoice = openInvoice();
        Assert.assertTrue(
                invoice.getDropdownOptions("المخزن").contains("المخزن الرئيسي"),
                "المخزن الرئيسي غير ظاهر ضمن خيارات المخازن المتاحة"
        );
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من أن نوع السداد الافتراضي نقدي")
    @Description("يتحقق هذا الاختبار من التحقق من أن نوع السداد الافتراضي نقدي.")
    @Test(priority = 14, description = "TC-SI-004: التحقق من أن نوع السداد الافتراضي نقدي")
    public void TC_SI_004_DefaultPaymentTypeIsCash() {
        Invoice invoice = openInvoice();
        Assert.assertEquals(
                invoice.getSelectedDropdownOption("نوع السداد"),
                "نقدي",
                "نوع السداد الافتراضي ليس نقديًا"
        );
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من ظهور العملاء في القائمة المنسدلة")
    @Description("يتحقق هذا الاختبار من التحقق من ظهور العملاء في القائمة المنسدلة.")
    @Test(priority = 16, description = "TC-SI-007: التحقق من ظهور العملاء في القائمة المنسدلة")
    public void TC_SI_007_CustomerOptionsAreAvailable() {
        Invoice invoice = openInvoice();
        Assert.assertFalse(invoice.getDropdownOptions("العملاء").isEmpty(), "قائمة العملاء لا تحتوي على خيارات");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من ظهور مندوبي البيع")
    @Description("يتحقق هذا الاختبار من التحقق من ظهور مندوبي البيع.")
    @Test(priority = 17, description = "TC-SI-008: التحقق من ظهور مندوبي البيع")
    public void TC_SI_008_SalesAgentOptionsAreAvailable() {
        Invoice invoice = openInvoice();
        Assert.assertFalse(invoice.getDropdownOptions("مندوب").isEmpty(), "قائمة مندوبي البيع لا تحتوي على خيارات");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من ظهور قوائم الأسعار")
    @Description("يتحقق هذا الاختبار من التحقق من ظهور قوائم الأسعار.")
    @Test(priority = 18, description = "TC-SI-010: التحقق من ظهور قوائم الأسعار")
    public void TC_SI_010_PriceListOptionsAreAvailable() {
        Invoice invoice = openInvoice();
        Assert.assertFalse(invoice.getDropdownOptions("قوائم الأسعار").isEmpty(), "قائمة الأسعار لا تحتوي على خيارات");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إنشاء فاتورة مبيعات آجلة")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مبيعات آجلة.")
    @Test(priority = 19, description = "TC-SI-020: إنشاء فاتورة مبيعات آجلة")
    public void TC_SI_020_CreateCreditSalesInvoice() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("آجل", "نوع السداد");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.SelectFromDropDawn("قائمه اسعار", "قوائم الأسعار");
        selectWarehouseAndItemsFromDialog(invoice);
        invoice.validateInvoiceAllBusiness();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إنشاء فاتورة بخصم نسبة مئوية")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة بخصم نسبة مئوية.")
    @Test(priority = 20, description = "TC-SI-025: إنشاء فاتورة بخصم نسبة مئوية")
    public void TC_SI_025_InvoiceWithPercentageDiscount() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("نقدي", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickOnBarcodeRadioBtn();
        invoice.Search("5638351925");
        invoice.ClickEnter();
        invoice.SetQuantitiesForRows(1, 10);
        invoice.SetValueByRow("السعر", 1, 200);
        invoice.AddDiscountPercntge("10");
        invoice.ClickOtherPay();
        invoice.SelectFromDropDawn("طريقة دفع", "طريقة الدفع");
        invoice.ClickPlusToAddPay();
        invoice.validateBusinessTotals();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("إنشاء فاتورة بخصم بقيمة ثابتة")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة بخصم بقيمة ثابتة.")
    @Test(priority = 21, description = "TC-SI-026: إنشاء فاتورة بخصم بقيمة ثابتة")
    public void TC_SI_026_InvoiceWithFixedDiscount() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("نقدي", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickOnNameRadioBtn();
        invoice.SearchAndSelectProduct("سكر");
        invoice.SetQuantitiesForRows(1, 10);
        invoice.SetValueByRow("السعر", 1, 200);
        invoice.AddDiscountValue("10");
        invoice.ClickOtherPay();
        invoice.SelectFromDropDawn("طريقة دفع", "طريقة الدفع");
        invoice.ClickPlusToAddPay();
        invoice.validateBusinessTotals();
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من إدخال تعليق على الفاتورة")
    @Description("يتحقق هذا الاختبار من التحقق من إدخال تعليق على الفاتورة.")
    @Test(priority = 23, description = "TC-SI-028: التحقق من إدخال تعليق على الفاتورة")
    public void TC_SI_028_EnterInvoiceComment() {
        Invoice invoice = openInvoice();
        String comment = "ملاحظة اختبار فاتورة المبيعات";
        invoice.AddNotes(comment);

        Assert.assertEquals(
                driver.findElement(By.cssSelector("textarea[label-text='Notes']")).getAttribute("value"),
                comment,
                "لم يتم إدخال التعليق في خانة ملاحظات الفاتورة"
        );
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("التحقق من زر جديد وإعادة تهيئة الفاتورة")
    @Description("يتحقق هذا الاختبار من التحقق من زر جديد وإعادة تهيئة الفاتورة.")
    @Test(priority = 24, description = "TC-SI-029: التحقق من زر جديد وإعادة تهيئة الفاتورة")
    public void TC_SI_029_NewInvoiceResetsCurrentInvoice() {
        Invoice invoice = openInvoice();
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickOnNameRadioBtn();
        invoice.SearchAndSelectProduct("سكر");
        Assert.assertFalse(invoice.getInvoiceRows().isEmpty(), "تعذر إعداد بيانات الفاتورة قبل الضغط على جديد");

        invoice.clickNewInvoice();

        Assert.assertTrue(invoice.getInvoiceRows().isEmpty(), "زر جديد لم يفرغ أصناف الفاتورة السابقة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("Add Invoice")
    @Description("يتحقق هذا الاختبار من Add Invoice.")
    @Test(priority = 25)
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
        invoice.validateBusinessTotals();
    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("Add Invoice Cash")
    @Description("يتحقق هذا الاختبار من Add Invoice Cash.")
    @Test(priority = 26)
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

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("Add Purches Bills By Name Test")
    @Description("يتحقق هذا الاختبار من Add Purches Bills By Name Test.")
    @Test(priority = 27)
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

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("Add Purches Bills By Barcod Test")
    @Description("يتحقق هذا الاختبار من Add Purches Bills By Barcod Test.")
    @Test(priority = 28)
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

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("Add Purches Bills With Save Buuton Test")
    @Description("يتحقق هذا الاختبار من Add Purches Bills With Save Buuton Test.")
    @Test(priority = 29)
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

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المبيعات")
    @Story("الاضافه من دون اصناف")
    @Description("يتحقق هذا الاختبار من Add With Out Product.")
    @Test(priority = 30)
    public void AddWithOutProduct() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        driver.navigate().refresh();
        Assert.assertTrue(invoice.AssertSaveButtonIsNotChecked());
    }
}
