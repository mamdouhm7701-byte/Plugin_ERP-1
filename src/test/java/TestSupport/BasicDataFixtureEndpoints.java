/* شرح الملف: قائمة الشاشات المسموح لتجهيز البيانات الأساسية بإنشاء سجلات تجربة فيها واسترجاعها. */
package TestSupport;

final class BasicDataFixtureEndpoints {
    private BasicDataFixtureEndpoints() {}

    public static final java.util.Set<String> ALLOWED =
            java.util.Set.of(
                    "Accounts",
                    "Banks",
                    "Safes",
                    "PaymentMethods",
                    "Stores",
                    "Areas",
                    "ExchangeDestinations",
                    "Incomes",
                    "Expenses",
                    "DiscountTypes",
                    "AgentItinerariesStatus",
                    "Vouchers",
                    "SafeTransactions",
                    "users",
                    "Tables",
                    "CarColors",
                    "CastingTypes",
                    "CementTypes",
                    "MaxTemperatures",
                    "Cars",
                    "Drivers",
                    "Projects",
                    "AccountGroups",
                    "CostCenters",
                    "PriceLists",
                    "Stocktakings",
                    "ShortageAdjustments",
                    "OverflowAdjustments",
                    "UnderReviews");
}
