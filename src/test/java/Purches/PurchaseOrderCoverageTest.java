/* شرح الملف: يراجع أن أمر الشراء النقدي ينشئ سند صرف للمورد، وأن الآجل لا ينشئ سند صرف، مع ثبات المخزون في الحالتين.
 */
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
    @Story("أمر الشراء النقدي ينشئ سند صرف للمورد ولا يغير المخزون")
    @Description(
            "حفظ أمر شراء نقدي بوسيلة الدفع والمورد المحددين، ثم البحث عن سند الصرف الفعلي الخاص"
                    + " بهذا المورد والتحقق من قيمته مع بقاء المخزون دون تغيير.")
    public void cashOrderCreatesReceiptWithoutStockMovement() {
        var order = create("picker", "Cash", false);
        var payments = data.ownedSupplierPayments(supplier.id());
        io.qameta.allure.Allure.addAttachment(
                "سند صرف أمر الشراء النقدي",
                "application/json",
                new org.json.JSONObject(
                                PurchaseTestData.fields(
                                        "order", data.read(order), "payments", payments))
                        .toString(),
                ".json");
        org.testng.Assert.assertFalse(
                payments.isEmpty(), "لم ينشئ أمر الشراء النقدي سند صرف للمورد");
        var total =
                payments.stream()
                        .map(r -> number(r.get("value")))
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        sameNumber(total, "300");
        assertStock("0");
    }

    @Test
    @Story("أمر الشراء الآجل لا ينشئ سند صرف ولا يغير رصيد المورد أو المخزون")
    @Description(
            "أنشئ أمر شراء آجل بكمية 3 وسعر 100 لمورد الاختبار. احفظه، ثم تأكد من عدم وجود سند صرف لهذا المورد، ومن ثبات رصيده وكمية المخزون.")
    public void creditOrderDoesNotCreatePaymentOrChangeBalances() {
        prepare("Credit");
        var before = data.read(supplier);
        fill("picker");
        save(false);
        org.testng.Assert.assertTrue(
                data.ownedSupplierPayments(supplier.id()).isEmpty(),
                "أمر الشراء الآجل أنشأ سند صرف للمورد رغم عدم وجود دفع");
        assertSupplierDelta(before, "0");
        assertStock("0");
    }
}
