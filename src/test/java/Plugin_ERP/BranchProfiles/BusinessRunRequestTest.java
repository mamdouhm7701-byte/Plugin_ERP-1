/* شرح الملف: اختبارات تجميع اختيارات النشاط المطلوبة قبل بدء التشغيل.
 */
package Plugin_ERP.BranchProfiles;

import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class BusinessRunRequestTest {
    @DataProvider
    public Object[][] combinations() {
        var cases = new ArrayList<Object[]>();
        for (var activity : ActivityType.values())
            for (var discount : DiscountLevel.values())
                for (var tax : TaxPolicy.values())
                    for (var stock : StocktakingMethod.values()) {
                        cases.add(
                                new Object[] {
                                    new BusinessRunRequest(activity, discount, tax, stock, false)
                                });
                        if (activity == ActivityType.RESTAURANT)
                            cases.add(
                                    new Object[] {
                                        new BusinessRunRequest(activity, discount, tax, stock, true)
                                    });
                    }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "combinations")
    public void exactPersistedConfigurationIsRequired(BusinessRunRequest request) {
        var stored = new HashMap<String, Object>(request.fields());
        stored.put("id", "owned-test");
        request.verify(stored, "https://demo.example");
        stored.put(
                "stocktakingMethod",
                request.stocktaking() == StocktakingMethod.PERIODIC ? "Continuous" : "Periodic");
        Assert.expectThrows(
                IllegalStateException.class, () -> request.verify(stored, "https://demo.example"));
    }

    @Test
    public void additionalTaxCannotLeakIntoOtherActivities() {
        for (var activity : ActivityType.values())
            if (activity != ActivityType.RESTAURANT)
                Assert.expectThrows(
                        IllegalArgumentException.class,
                        () ->
                                new BusinessRunRequest(
                                        activity,
                                        DiscountLevel.ITEMS,
                                        TaxPolicy.NET_BASED,
                                        StocktakingMethod.CONTINUOUS,
                                        true));
        var request =
                new BusinessRunRequest(
                        ActivityType.RESTAURANT,
                        DiscountLevel.TRANSACTION,
                        TaxPolicy.PRICE_INCLUDED,
                        StocktakingMethod.PERIODIC,
                        true);
        var stored = new HashMap<String, Object>(request.fields());
        stored.put("id", "owned");
        stored.put("additionalTaxPercentage", 99);
        Assert.expectThrows(
                IllegalStateException.class, () -> request.verify(stored, "https://demo.example"));
        stored.put("additionalTaxPercentage", 100);
        stored.put("enableAdditionalTax", false);
        Assert.expectThrows(
                IllegalStateException.class, () -> request.verify(stored, "https://demo.example"));
    }
}
