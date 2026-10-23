package Purches;

import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المشتريات - ORDER")
public final class PurchaseOrderCoverageTest extends PurchaseFormTest {
    protected PurchaseDocumentKind kind() {
        return PurchaseDocumentKind.ORDER;
    }

    @Test
    @Story("أمر الشراء النقدي ينشئ سند قبض ولا يغير المخزون")
    @Description(
            "حفظ أمر شراء نقدي بوسيلة الدفع والمورد المحددين، ثم البحث عن سند القبض الفعلي الخاص"
                    + " بهذا المورد والتحقق من قيمته مع بقاء المخزون دون تغيير.")
    public void cashOrderCreatesReceiptWithoutStockMovement() {
        var order = create("picker", "Cash", false);
        var receipts = data.ownedSupplierReceipts(supplier.id());
        io.qameta.allure.Allure.addAttachment(
                "Cash purchase order receipt evidence",
                "application/json",
                new org.json.JSONObject(
                                PurchaseTestData.fields(
                                        "order", data.read(order), "receipts", receipts))
                        .toString(),
                ".json");
        org.testng.Assert.assertFalse(
                receipts.isEmpty(), "Cash purchase order did not create a supplier receipt");
        var total =
                receipts.stream()
                        .map(r -> number(r.get("value")))
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        sameNumber(total, "300");
        assertStock("0");
    }
}
