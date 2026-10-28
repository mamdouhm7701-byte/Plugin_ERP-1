/* شرح الملف: أنواع الحركات والبيانات الأساسية المسموح لمالك التجربة بتتبعها واسترجاعها داخل هذا القسم فقط.
 */
package Purches;

final class PurchaseFixtureEndpoints {
    private PurchaseFixtureEndpoints() {}

    static final java.util.Set<String> MOVEMENTS =
            java.util.Set.of("Purchases", "PurchaseOrders", "ReturnPurchases");
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
                    "ExchangeDestinations",
                    "Banks",
                    "Safes",
                    "PaymentMethods",
                    "PriceLists",
                    "DiscountTypes");
}
