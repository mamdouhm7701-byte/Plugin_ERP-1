/* شرح الملف: تحميل سجل مطعم سابق بعد مطابقة الفرع والبيئة وعلامة التجربة وأنواع السجلات المسموح بها.
 * فائدته: يعيد البيانات المؤكدة ومحاولات الحفظ غير المحسومة دون تبني سجل من تجربة أو إعدادات مختلفة.
 */
package SpecialSalesTest;

import SpecialSalesTest.SpecialSalesTestData.Record;
import java.util.*;

final class SpecialSalesRecoveryState {
    @SuppressWarnings("unchecked")
    static void load(
            String suffix,
            TestSupport.FixtureSession session,
            List<Record> created,
            TestSupport.PendingFixtureWrites pendingWrites)
            throws java.io.IOException {
        var path = java.nio.file.Path.of("target", "special-sales-fixtures", suffix + ".json");
        if (java.nio.file.Files.exists(path))
            for (Object value :
                    new org.json.JSONArray(TestSupport.FixtureJournals.read(path)).toList()) {
                var row = (Map<String, Object>) value;
                session.verify(row);
                String endpoint = (String) row.get("endpoint"), name = (String) row.get("name");
                if (name == null
                        || !name.startsWith("QA-SP-")
                        || !java.util.Set.of(
                                        "Accounts",
                                        "Units",
                                        "ItemCategories",
                                        "Stores",
                                        "Items",
                                        "PriceLists",
                                        "DiscountTypes",
                                        "Customers",
                                        "Banks",
                                        "Safes",
                                        "PaymentMethods",
                                        "Tables",
                                        "ItemTransactions-PointOfSales",
                                        "ItemTransactions-RestaurantSales",
                                        "ItemTransactions-ReturnSales",
                                        "SpecialSalesBranchStoreMembership",
                                        "SpecialSalesBranchDefaults")
                                .contains(endpoint)
                        || row.get("id") == null
                        || !(row.get("values") instanceof Map<?, ?>))
                    throw new AssertionError("Invalid owned fixture journal record");
                created.add(
                        new Record(
                                endpoint,
                                row.get("id"),
                                name,
                                (Map<String, Object>) row.get("values")));
            }
        pendingWrites.resume(
                java.util.Set.of(
                        "Accounts",
                        "Units",
                        "ItemCategories",
                        "Stores",
                        "Items",
                        "PriceLists",
                        "DiscountTypes",
                        "Customers",
                        "Banks",
                        "Safes",
                        "PaymentMethods",
                        "Tables",
                        "ItemTransactions-PointOfSales",
                        "ItemTransactions-RestaurantSales",
                        "ItemTransactions-ReturnSales",
                        "SpecialSalesBranchStoreMembership"),
                "QA-SP-");
    }
}
