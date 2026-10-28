/* شرح الملف: أنواع الحركات والبيانات الأساسية المسموح لمالك التجربة بتتبعها واسترجاعها داخل هذا القسم فقط.
 */
package StoreTest;

final class StoreFixtureEndpoints {
    private StoreFixtureEndpoints() {}

    static final java.util.Set<String> MOVEMENTS =
            java.util.Set.of(
                    "ItemTransactions-ExchangeOrder",
                    "AdditionOrders",
                    "ManufacturingOrders",
                    "ItemTransactions-storageTransfer",
                    "DamageAdjustments");
    static final java.util.Set<String> MASTERS =
            java.util.Set.of(
                    "Accounts",
                    "Units",
                    "ItemCategories",
                    "Stores",
                    "Items",
                    "Agents",
                    "Customers",
                    "Suppliers",
                    "ExchangeDestinations");
}
