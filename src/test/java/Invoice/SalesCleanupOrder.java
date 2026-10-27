package Invoice;

import java.util.ArrayList;
import java.util.List;

/** Concrete invoices remain in use until their owned delivery orders are removed. */
final class SalesCleanupOrder {
    private SalesCleanupOrder() {}

    static List<SalesTestData.Record> records(List<SalesTestData.Record> created) {
        var ordered = new ArrayList<>(created.reversed());
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
                if (delivery.isPresent()
                        && ordered.indexOf(delivery.get()) > ordered.indexOf(invoice)) {
                    ordered.remove(delivery.get());
                    ordered.add(ordered.indexOf(invoice), delivery.get());
                }
            }
        }
        return ordered;
    }
}
