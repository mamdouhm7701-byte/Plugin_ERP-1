package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.Invoice;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;

/** Original case IDs retained; all inputs now come from this scenario's fixtures. */
public class SalesInvoiceTestCases extends SalesFormActions {
    @Test
    @Story("إنشاء فاتورة بيع نقدية مكتملة وفتح طباعتها")
    public void TC_SI_001_CreateCompleteSalesInvoice() {
        create("picker", "Cash", true);
    }

    @Test
    @Story("إضافة الصنف إلى الفاتورة بالباركود وحفظه")
    public void TC_SI_011_AddItemByBarcode() {
        savesOwnedItemByBarcode();
    }

    @Test
    @Story("إضافة الصنف بالاسم العربي وحفظه")
    public void TC_SI_012_AddItemByArabicName() {
        savesOwnedItemByName();
    }

    @Test
    @Story("إضافة الصنف من نافذة الأصناف وحفظه")
    public void TC_SI_013_AddItemsFromDialog() {
        savesOwnedItemFromPicker();
    }

    @Test
    @Story("فلترة نافذة الأصناف بتصنيف أنشأه الاختبار")
    public void TC_SI_014_FilterItemsByCategory() {
        prepare("Credit");
        page.slPickerCategory(data.category().name(), item.name());
        try {
            Assert.assertFalse(page.slPickerRows().isEmpty());
            Assert.assertTrue(
                    page.slPickerRows().stream().allMatch(row -> row.contains(item.name())));
        } finally {
            page.slClosePicker();
        }
    }

    @Test
    @Story("البحث بباركود الصنف داخل نافذة الأصناف")
    public void TC_SI_015_SearchByBarcodeInAddItemsDialog() {
        pickerSearch("barcode", true);
    }

    @Test
    @Story("البحث بالاسم العربي لالصنف داخل نافذة الأصناف")
    public void TC_SI_016_SearchByArabicNameInAddItemsDialog() {
        pickerSearch("arabicName", false);
    }

    private void pickerSearch(String field, boolean barcode) {
        prepare("Credit");
        String value = barcode ? item.values().get("mainUnitBarcode").toString() : item.name();
        page.slPickerSearch(field, value, item.name());
        try {
            Assert.assertTrue(
                    page.slPickerRows().stream()
                            .anyMatch(row -> row.contains(item.name()) && row.contains(value)));
        } finally {
            page.slClosePicker();
        }
    }

    @Test
    @Story("تبديل ملء الشاشة مع الحفاظ على العميل المختار")
    public void TC_SI_021_ToggleInvoiceFullscreen() {
        prepare("Credit");
        Invoice invoice = (Invoice) page;
        boolean before = invoice.isInvoiceSideMenuDisplayed();
        invoice.toggleInvoiceFullscreen();
        try {
            Assert.assertEquals(invoice.isInvoiceSideMenuDisplayed(), !before);
            Assert.assertEquals(page.slDocument().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
        } finally {
            if (invoice.isInvoiceSideMenuDisplayed() != before) invoice.toggleInvoiceFullscreen();
        }
    }

    @Test
    @Story("اختيار المخزن الذي أنشأه الاختبار")
    public void TC_SI_005_WarehouseOptionsAreAvailable() {
        prepare("Credit");
        Assert.assertEquals(page.slDocument().get("storeId"), data.store(1).id(), "مطابقة المخزن");
    }

    @Test
    @Story("نوع الدفع الافتراضي في الفاتورة الجديدة نقدي")
    public void TC_SI_004_DefaultPaymentTypeIsCash() {
        page.slOpen("invoice/new");
        page.slFormReady();
        Assert.assertEquals(page.slDocument().get("paymentType"), "Cash", "مطابقة نوع السداد");
    }

    @Test
    @Story("اختيار العميل الذي أنشأه الاختبار")
    public void TC_SI_007_CustomerOptionsAreAvailable() {
        prepare("Credit");
        Assert.assertEquals(page.slDocument().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
    }

    @Test
    @Story("اختيار المندوب الذي أنشأه الاختبار")
    public void TC_SI_008_SalesAgentOptionsAreAvailable() {
        prepare("Credit");
        Assert.assertEquals(page.slDocument().get("agentId"), agent.id(), "مطابقة المندوب");
    }

    @Test
    @Story("اختيار قائمة أسعار الاختبار وتطبيق سعرها")
    public void TC_SI_010_PriceListOptionsAreAvailable() {
        ownedPriceListIsApplied();
    }

    @Test
    @Story("إنشاء فاتورة بيع آجلة والتحقق من بياناتها وأثرها")
    public void TC_SI_020_CreateCreditSalesInvoice() {
        create("picker", "Credit", false);
    }

    @Test
    @Story("حساب خصم الفاتورة بالنسبة")
    public void TC_SI_025_InvoiceWithPercentageDiscount() {
        percentageDiscountRecalculates();
    }

    @Test
    @Story("حساب خصم الفاتورة بالقيمة")
    public void TC_SI_026_InvoiceWithFixedDiscount() {
        fixedDiscountRecalculates();
    }

    @Test
    @Story("حفظ ملاحظات الفاتورة العربية والإنجليزية")
    public void TC_SI_028_EnterInvoiceComment() {
        unicodeNotesPersist();
    }

    @Test
    @Story("زر جديد يمسح بنود الفاتورة غير المحفوظة")
    public void TC_SI_029_NewInvoiceResetsCurrentInvoice() {
        newClearsUnsavedLines();
    }

    @Test
    @Story("إنشاء فاتورة بيع آجلة بأصناف الاختبار")
    public void AddInvoice() {
        create("picker", "Credit", false);
    }

    @Test
    @Story("إنشاء فاتورة بيع نقدية بوسيلة دفع الاختبار")
    public void AddInvoiceCash() {
        create("picker", "Cash", false);
    }

    @Test
    @Story("إنشاء فاتورة بيع بإضافة الصنف بالاسم")
    public void AddPurchesBillsByNameTest() {
        create("name", "Credit", false);
    }

    @Test
    @Story("إنشاء فاتورة بيع بإضافة الصنف بالباركود")
    public void AddPurchesBillsByBarcodTest() {
        create("barcode", "Credit", false);
    }

    @Test
    @Story("حفظ فاتورة بيع بصنف مختار من القائمة")
    public void AddPurchesBillsWithSaveBuutonTest() {
        create("picker", "Credit", false);
    }

    @Test
    @Story("منع حفظ فاتورة البيع بدون أصناف")
    public void AddWithOutProduct() {
        emptyDocumentCannotSave();
    }
}
