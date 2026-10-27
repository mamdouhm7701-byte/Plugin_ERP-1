package TestSupport;

import java.math.BigDecimal;
import java.util.*;

/** Explicit tax and opening-stock recipe for owned policy validation items. */
public final class PolicyItemRecipe {
    private PolicyItemRecipe() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> create(
            Map<String, Object> defaults, String price, boolean exempt) {
        var values = new LinkedHashMap<>(defaults);
        values.put("isTaxable", !exempt);
        values.put("itemTax", exempt ? 0 : 15);
        values.put("isAdditionalTaxable", false);
        values.put("salesPrice", new BigDecimal(price));
        values.put("minSalesPrice", 0);
        values.put("maxSalesPrice", 1000);
        var stores = (List<Map<String, Object>>) defaults.get("stores");
        if (stores == null || stores.isEmpty())
            throw new IllegalArgumentException("Owned policy item requires an explicit store");
        var seeded = new ArrayList<Map<String, Object>>();
        for (int index = 0; index < stores.size(); index++) {
            var store = new LinkedHashMap<>(stores.get(index));
            if (index == 0) {
                store.put("openingBalance", 1000);
                store.put("currentBalance", 1000);
            }
            seeded.add(store);
        }
        values.put("stores", seeded);
        return values;
    }
}
