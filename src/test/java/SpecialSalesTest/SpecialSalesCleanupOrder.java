package SpecialSalesTest;

import java.util.*;

/**
 * A linked owned return is removed before its source even when source capture is refreshed later.
 */
final class SpecialSalesCleanupOrder {
    private SpecialSalesCleanupOrder() {}

    static List<SpecialSalesTestData.Record> records(List<SpecialSalesTestData.Record> created) {
        var ordered = new ArrayList<>(created.reversed());
        for (var child : new ArrayList<>(ordered)) {
            if (!child.endpoint().equals("ItemTransactions-ReturnSales")) continue;
            Object source = child.values().get("sourceTransactionId");
            if (source == null) continue;
            var parent =
                    ordered.stream()
                            .filter(
                                    r ->
                                            r.endpoint().equals("ItemTransactions-RestaurantSales")
                                                    && r.id().equals(source))
                            .findFirst();
            if (parent.isPresent()
                    && ordered.indexOf(child) > ordered.indexOf(parent.orElseThrow())) {
                ordered.remove(child);
                ordered.add(ordered.indexOf(parent.orElseThrow()), child);
            }
        }
        return ordered;
    }
}
