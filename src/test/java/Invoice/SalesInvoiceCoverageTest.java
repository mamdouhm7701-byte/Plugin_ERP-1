package Invoice;

import org.testng.annotations.Test;
import io.qameta.allure.Story;

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
        org.testng.Assert.assertEquals(firstLine(data.read(saved)).get("itemId"), second.id());
        assertStock("0");
        sameNumber(data.balance(second, 1), "999");
    }

    @Test
    @Story("حفظ فاتورة الاختبار وفتح طباعتها")
    public void savesAndPrintsOwnedInvoice() {
        create("picker", "Credit", true);
    }
}
