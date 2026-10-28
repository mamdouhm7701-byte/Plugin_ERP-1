/* شرح الملف: مراجعة تنظيف البيانات التي أنشأتها التجربة فقط، ضمن العملاء والموردين والمناديب.
 */
package DealerModelTest;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DealerMarkerCleanupTest {
    @Test
    public void cleansOnlyExactOwnedCalendarEvents() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        DealerTestData data = new DealerTestData(api.driver());
        String marker = "QA-owned-route";
        for (int i = 1; i <= 2; i++)
            api.records.put(
                    "AgentItineraries-" + i,
                    new LinkedHashMap<>(
                            Map.of(
                                    "id",
                                    "AgentItineraries-" + i,
                                    "visit",
                                    Map.of("title", marker))));
        api.records.put(
                "AgentItineraries-foreign",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "AgentItineraries-foreign",
                                "visit",
                                Map.of("title", marker + "-foreign"))));
        data.deleteOwnedDealer(Plugin_ERP.Enums.Pages.DEALERROUTELINE, marker);
        Assert.assertEquals(api.deletes.size(), 2);
        Assert.assertTrue(api.records.containsKey("AgentItineraries-foreign"));
        data.close();
    }

    @Test
    public void recoversUnexpectedBlankNameSaveByExactEnglishMarker() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        DealerTestData data = new DealerTestData(api.driver());
        String marker = "QA-invalid-name-exact";
        api.records.put(
                "Customers-owned",
                new LinkedHashMap<>(
                        Map.of("id", "Customers-owned", "arabicName", "", "englishName", marker)));
        api.records.put(
                "Customers-foreign",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "Customers-foreign",
                                "arabicName",
                                "",
                                "englishName",
                                marker + "-foreign")));
        data.deleteOwnedDealer(Plugin_ERP.Enums.Pages.CUSTOMERS, marker);
        Assert.assertFalse(api.records.containsKey("Customers-owned"));
        Assert.assertTrue(api.records.containsKey("Customers-foreign"));
        data.close();
    }

    @Test
    public void refusesAmbiguousBlankNameMarkersAndForeignSession() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        DealerTestData data = new DealerTestData(api.driver());
        String marker = "QA-invalid-name-ambiguous";
        for (int i = 1; i <= 2; i++)
            api.records.put(
                    "Customers-" + i,
                    new LinkedHashMap<>(
                            Map.of(
                                    "id",
                                    "Customers-" + i,
                                    "arabicName",
                                    "",
                                    "englishName",
                                    marker)));
        Assert.expectThrows(
                AssertionError.class,
                () -> data.deleteOwnedDealer(Plugin_ERP.Enums.Pages.CUSTOMERS, marker));
        Assert.assertTrue(api.deletes.isEmpty());
        api.activeBranch = "foreign";
        Assert.expectThrows(
                AssertionError.class,
                () -> data.deleteOwnedDealer(Plugin_ERP.Enums.Pages.CUSTOMERS, marker));
        Assert.assertTrue(api.deletes.isEmpty());
        api.activeBranch = "branch-test";
        data.close();
    }
}
