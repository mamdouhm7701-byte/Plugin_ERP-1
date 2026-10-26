package Invoice;

import io.qameta.allure.Story;
import org.testng.annotations.Test;

public final class SalesOrderTest extends SalesFormTest {
    @Test
    @Story("تطبيق قائمة أسعار أنشأها الاختبار على أمر البيع")
    @Override
    public void ownedPriceListIsApplied() {
        super.ownedPriceListIsApplied();
    }

    @Override
    protected SalesDocumentKind kind() {
        return SalesDocumentKind.ORDER;
    }

    @Test
    @Story("أمر البيع الآجل لا يغير المخزون ولا رصيد العميل")
    public void creditOrderDoesNotChangeCustomerBalance() {
        prepare("Credit");
        var before = data.read(customer);
        fill("picker");
        save(false);
        assertStock("0");
        assertCustomerDelta(before, "0");
    }

    @Test
    @Story("أمر البيع النقدي يسجل دفعة مقدمة للعميل دون تغيير المخزون")
    public void cashOrderRecordsCustomerAdvanceWithoutStockMovement() {
        prepare("Cash");
        var before = data.read(customer);
        fill("picker");
        save(false);
        assertStock("0");
        assertCustomerDelta(before, "-300");
    }
}
