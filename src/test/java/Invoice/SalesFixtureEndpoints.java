/* شرح الملف: أنواع الحركات والبيانات الأساسية المسموح لمالك التجربة بتتبعها واسترجاعها داخل هذا القسم فقط.
 */
package Invoice;

final class SalesFixtureEndpoints {
    private SalesFixtureEndpoints() {}

    static final java.util.Set<String> MOVEMENTS =
            java.util.Set.of(
                    "ItemTransactions-Sales",
                    "ItemTransactions-Contract",
                    "ConcreteDelivery",
                    "ItemTransactions-ConcreteSales",
                    "ItemTransactions-Reservations",
                    "Purchases",
                    "ItemTransactions-quotation",
                    "ItemTransactions-SalesOrder",
                    "ItemTransactions-ReturnSales");
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
                    "DiscountTypes",
                    "CarColors");
}
