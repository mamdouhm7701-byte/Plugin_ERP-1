/* شرح الملف: اختبارات فاتورة المبيعات: إضافة صنف جديد بالباركود عبر البحث في الخادم عند غيابه عن
 * الذاكرة المحلية؛ تطبيق قائمة أسعار أنشأها الاختبار على فاتورة البيع؛ حفظ فاتورة الاختبار وفتح
 * طباعتها.
 */
package Invoice;

import io.qameta.allure.Story;
import org.testng.annotations.Test;

public final class SalesInvoiceCoverageTest extends SalesFormTest {
    @Test
    @Story("تطبيق قائمة أسعار أنشأها الاختبار على فاتورة البيع")
    @Override
    public void ownedPriceListIsApplied() {
        super.ownedPriceListIsApplied();
    }

    @Test
    @Story("إضافة صنف جديد بالباركود عبر البحث في الخادم عند غيابه عن الذاكرة المحلية")
    public void barcodeApiFallbackAddsOwnedItemAbsentFromCache() {
        prepare("Credit");
        page.slWaitOwnedItemLookup(item.name());
        var second = ownItem(2);
        org.testng.Assert.assertFalse(
                page.slOwnedItemCached(second.name()),
                "New owned item unexpectedly already cached");
        page.slAddBarcodeImmediately(
                second.values().get("mainUnitBarcode").toString(), second.name());
        page.slWaitNet("100");
        var saved = save(false);
        org.testng.Assert.assertEquals(
                firstLine(data.read(saved)).get("itemId"), second.id(), "مطابقة الصنف");
        assertStock("0");
        sameNumber(data.balance(second, 1), "999");
    }

    @Test
    @Story("حفظ فاتورة الاختبار وفتح طباعتها")
    public void savesAndPrintsOwnedInvoice() {
        prepare("Credit");
        var second = ownItem(2);
        add(item, "picker");
        add(second, "picker");
        page.slQuantity(item.name(), "2");
        page.slQuantity(second.name(), "3");
        page.slSetLine(second.name(), "السعر", "73.25");
        String expectedNet = salesNet("419.75");
        page.slWaitNet(expectedNet);
        printContentAssertions =
                stored -> {
                    sameNumber(stored.get("net"), expectedNet);
                    Plugin_ERP.Util.PrintedInvoiceAssertions.invoice(
                            driver,
                            stored.get("code").toString(),
                            customer.name(),
                            java.util.List.of(
                                    new Plugin_ERP.Util.PrintedInvoiceAssertions.Line(
                                            item.name(),
                                            new java.math.BigDecimal("2"),
                                            new java.math.BigDecimal("100")),
                                    new Plugin_ERP.Util.PrintedInvoiceAssertions.Line(
                                            second.name(),
                                            new java.math.BigDecimal("3"),
                                            new java.math.BigDecimal("73.25"))),
                            new java.math.BigDecimal(expectedNet));
                };
        save(true);
        assertStock("2");
        sameNumber(data.balance(second, 1), "997");
    }
}
