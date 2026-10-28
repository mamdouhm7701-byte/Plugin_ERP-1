/* شرح الملف: تجهيز فاتورة مستقلة لسيناريوهات القوائم والمرتجعات، بقيم الكمية والسعر والمدفوع التي يحددها الاختبار.
 * فائدته: يفصل شكل فاتورة التجهيز عن مسؤولية تتبع السجلات واسترجاعها وحذفها.
 */
package Invoice;

import static Invoice.SalesTestData.fields;

import Invoice.SalesTestData.Record;
import java.util.*;

final class SalesSeedInvoices {
    private final SalesTestData data;

    SalesSeedInvoices(SalesTestData data) {
        this.data = data;
    }

    Record create(
            int index, String type, String date, String quantity, String paid, Record salesAgent) {
        Record item = data.item(1), customer = data.customer();
        java.math.BigDecimal qty = new java.math.BigDecimal(quantity),
                total = qty.multiply(new java.math.BigDecimal("100")),
                amount = new java.math.BigDecimal(paid);
        Map<String, Object> line =
                fields(
                        "lineSerial",
                        1,
                        "storeId",
                        data.store(1).id(),
                        "itemId",
                        item.id(),
                        "unitId",
                        data.unit(1).id(),
                        "unitConversionRate",
                        1,
                        "quantity",
                        qty,
                        "price",
                        100,
                        "total",
                        total,
                        "net",
                        total,
                        "costPrice",
                        qty.multiply(new java.math.BigDecimal("80")),
                        "discountPercentage",
                        0,
                        "discountValue",
                        0,
                        "additions",
                        List.of(),
                        "attributes",
                        List.of(),
                        "attachmentDetails",
                        List.of(),
                        "roundedAdditions",
                        0,
                        "roundedNet",
                        total);
        List<?> payments =
                amount.signum() == 0
                        ? List.of()
                        : List.of(
                                fields(
                                        "paymentMethod",
                                        "Other",
                                        "otherPaymentMethodId",
                                        data.payment().id(),
                                        "bankId",
                                        data.bank().id(),
                                        "value",
                                        amount,
                                        "cashPaid",
                                        0));
        return data.fixture(
                "seed-" + index,
                "ItemTransactions-" + type,
                fields(
                        "transactionType",
                        type,
                        "transactionChannel",
                        "Normal",
                        "transactionSource",
                        "Web",
                        "transactionDate",
                        date,
                        "transactionTime",
                        "12:00:00",
                        "dueDate",
                        date,
                        "paymentType",
                        amount.signum() == 0 ? "Credit" : "Cash",
                        "dealerId",
                        customer.id(),
                        "agentId",
                        salesAgent.id(),
                        "storeId",
                        data.store(1).id(),
                        "isOffline",
                        false,
                        "isPosted",
                        false,
                        "isComplete",
                        false,
                        "paid",
                        amount,
                        "total",
                        total,
                        "netBeforeTaxes",
                        total,
                        "net",
                        total,
                        "roundedNet",
                        total,
                        "totalTaxes",
                        0,
                        "roundedTotalTaxes",
                        0,
                        "discountValue",
                        0,
                        "discountPercentage",
                        0,
                        "totalAdditionalTaxes",
                        0,
                        "lines",
                        List.of(line),
                        "taxes",
                        List.of(),
                        "payments",
                        payments,
                        "expenses",
                        List.of(),
                        "costCenters",
                        List.of(),
                        "extraProperties",
                        List.of()));
    }
}
