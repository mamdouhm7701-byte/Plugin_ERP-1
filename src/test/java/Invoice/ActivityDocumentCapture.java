/* شرح الملف: متابعة المستند الذي حفظته التجربة حتى يمكن مراجعته وتنظيفه.
 */
package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import java.util.*;
import org.testng.Assert;

final class ActivityDocumentCapture {
    private ActivityDocumentCapture() {}

    @SuppressWarnings("unchecked")
    static List<SalesTestData.Record> collect(
            SalesTestData data, SalesModulePage page, String marker, String endpoint) {
        if (endpoint == null || !marker.equals(page.slCaptureMarker())) return List.of();
        data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
        var records = new ArrayList<SalesTestData.Record>();
        for (var saved : page.slSaves()) {
            Assert.assertEquals(
                    saved.get("endpoint"), endpoint, "Activity document endpoint changed");
            var values = (Map<String, Object>) saved.get("values");
            Assert.assertEquals(
                    values.get("notes"),
                    marker,
                    "Owned activity document marker must survive save");
            data.track(endpoint, saved.get("id"), marker, values);
            records.add(new SalesTestData.Record(endpoint, saved.get("id"), marker, values));
        }
        return records;
    }
}
