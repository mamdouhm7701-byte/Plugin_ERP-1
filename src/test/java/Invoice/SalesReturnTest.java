package Invoice;

public final class SalesReturnTest extends SalesFormTest {
    @org.testng.annotations.Test
    @io.qameta.allure.Story(
            "حفظ مرتجع حر بسعر مدخل يدوياً والتحقق من الإجمالي وزيادة المخزون وأثر رصيد العميل")
    public void freeReturnAcceptsEnteredUnitPrice() {
        prepare("Credit");
        var before = data.read(customer);
        add(item, "picker");
        page.slQuantity(item.name(), "3");
        page.slSetLine(item.name(), "السعر", "125");
        page.slWaitNet(salesNet("375"));
        var saved = save(false);
        sameNumber(firstLine(data.read(saved)).get("price"), "125");
        sameNumber(data.read(saved).get("net"), salesNet("375"));
        assertStock("3");
        assertCustomerDelta(before, "-375");
    }

    @Override
    protected SalesDocumentKind kind() {
        return SalesDocumentKind.RETURN;
    }
}
