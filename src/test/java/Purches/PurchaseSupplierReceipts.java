/* شرح الملف: قراءة سندات المورد الذي أنشأته تجربة الشراء، دون تعديلها أو تبني سندات مورد آخر.
 * فائدته: يفصل فحص السندات المرتبطة عن تجهيز فواتير الشراء وتنظيفها، ويتوقف إذا لم تتقدم صفحات النتائج.
 */
package Purches;

import static Purches.PurchaseTestData.fields;

import Purches.PurchaseTestData.Record;
import java.util.*;
import java.util.function.BiFunction;

final class PurchaseSupplierReceipts {
    private final PurchaseTestData data;
    private final List<Record> created;
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> get;

    PurchaseSupplierReceipts(
            PurchaseTestData data,
            List<Record> created,
            BiFunction<String, Map<String, Object>, Map<String, Object>> get) {
        this.data = data;
        this.created = created;
        this.get = get;
    }

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> find(Object supplierId) {
        if (created.stream()
                .noneMatch(r -> r.endpoint().equals("Suppliers") && r.id().equals(supplierId)))
            throw new AssertionError("Supplier is not owned by this page");
        var found = new ArrayList<Map<String, Object>>();
        var seen = new java.util.HashSet<Object>();
        for (int page = 1; ; page++) {
            Object entity =
                    get.apply(
                                    "Collections?pageNumber="
                                            + page
                                            + "&pageSize=500&dealerId="
                                            + supplierId,
                                    fields())
                            .get("entity");
            if (!(entity instanceof Map<?, ?> dto)
                    || !(dto.get("pageData") instanceof List<?> rows)
                    || !(dto.get("totalRecords") instanceof Number total))
                throw new AssertionError("Cannot inspect supplier receipts");
            int added = 0;
            for (Object value : rows) {
                if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                    throw new AssertionError("Receipt list has no identity");
                if (!seen.add(row.get("id"))) continue;
                added++;
                Map<String, Object> receipt =
                        row.containsKey("dealerId")
                                ? (Map<String, Object>) row
                                : data.read(
                                        new Record(
                                                "Collections",
                                                row.get("id"),
                                                "QA-PU-read-only-receipt",
                                                fields()));
                if (receipt != null && supplierId.equals(receipt.get("dealerId")))
                    found.add(receipt);
            }
            if (seen.size() >= total.longValue()) return found;
            if (added == 0) throw new AssertionError("Receipt pagination did not advance");
        }
    }
}
