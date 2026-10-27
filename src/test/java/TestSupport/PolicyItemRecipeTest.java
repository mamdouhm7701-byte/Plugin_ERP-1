package TestSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PolicyItemRecipeTest {
    @Test
    public void explicitPolicyRecipeSeedsStockAndKeepsOriginalUntouched() {
        var store =
                new LinkedHashMap<String, Object>(
                        Map.of(
                                "storeId",
                                "owned-store",
                                "openingBalance",
                                0,
                                "currentBalance",
                                0,
                                "costPrice",
                                80));
        var original =
                new LinkedHashMap<String, Object>(
                        Map.of("stores", List.of(store), "itemTax", 0, "isTaxable", false));
        var taxable = PolicyItemRecipe.create(original, "115", false);
        var exempt = PolicyItemRecipe.create(original, "115", true);
        Assert.assertEquals(taxable.get("itemTax"), 15);
        Assert.assertEquals(exempt.get("itemTax"), 0);
        Assert.assertEquals(taxable.get("isTaxable"), true);
        Assert.assertEquals(exempt.get("isTaxable"), false);
        var result = (Map<?, ?>) ((List<?>) taxable.get("stores")).getFirst();
        Assert.assertEquals(result.get("openingBalance"), 1000);
        Assert.assertEquals(result.get("currentBalance"), 1000);
        Assert.assertEquals(store.get("currentBalance"), 0);
        Assert.assertEquals(original.get("itemTax"), 0);
    }
}
