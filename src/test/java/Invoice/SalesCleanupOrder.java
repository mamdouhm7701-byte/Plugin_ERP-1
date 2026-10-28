/* شرح الملف: ترتيب تنظيف مرتجع البيع وفاتورته وبياناته.
 */
package Invoice;

import java.util.ArrayList;
import java.util.List;

final class SalesCleanupOrder {
    private SalesCleanupOrder() {}

    static List<SalesTestData.Record> records(List<SalesTestData.Record> created) {
        var ordered = new ArrayList<>(created.reversed());
        // An amended contract is tracked after its first partial invoice. Remove all
        // exact owned billing children before deleting that updated parent.
        for (var invoice : List.copyOf(ordered)) {
            if (!invoice.endpoint().equals("ItemTransactions-Sales")
                    || !"Contract"
                            .equalsIgnoreCase(
                                    String.valueOf(invoice.values().get("sourceTransactionType"))))
                continue;
            ordered.stream()
                    .filter(
                            record ->
                                    record.endpoint().equals("ItemTransactions-Contract")
                                            && record.id()
                                                    .equals(
                                                            invoice.values()
                                                                    .get("sourceTransactionId")))
                    .findFirst()
                    .ifPresent(contract -> moveBefore(ordered, invoice, contract));
        }
        for (var invoice : List.copyOf(ordered)) {
            if (!invoice.endpoint().equals("ItemTransactions-ConcreteSales")
                    || !(invoice.values().get("concreteDeliveryOrderIdsList")
                            instanceof List<?> links)) continue;
            for (Object id : links) {
                var delivery =
                        ordered.stream()
                                .filter(
                                        record ->
                                                record.endpoint().equals("ConcreteDelivery")
                                                        && record.id().equals(id))
                                .findFirst();
                delivery.ifPresent(record -> moveBefore(ordered, record, invoice));
            }
        }
        return ordered;
    }

    private static void moveBefore(
            List<SalesTestData.Record> ordered,
            SalesTestData.Record first,
            SalesTestData.Record second) {
        if (ordered.indexOf(first) > ordered.indexOf(second)) {
            ordered.remove(first);
            ordered.add(ordered.indexOf(second), first);
        }
    }
}
