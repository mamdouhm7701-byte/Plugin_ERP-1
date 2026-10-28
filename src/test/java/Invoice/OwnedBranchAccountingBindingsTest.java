package Invoice;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class OwnedBranchAccountingBindingsTest {
    @Test
    public void restoresOnlyOwnedFieldsAndKeepsOtherLinks() {
        var result =
                OwnedBranchAccountingBindings.restored(
                        Map.of("salesAccountId", "ours", "other", "foreign"),
                        Map.of("salesAccountId", "previous"),
                        Map.of("salesAccountId", "ours"));
        Assert.assertEquals(result, Map.of("salesAccountId", "previous", "other", "foreign"));
    }

    @Test
    public void removesOriginallyAbsentLinkButNeverOverwritesAThirdPartyChange() {
        Assert.assertTrue(
                OwnedBranchAccountingBindings.restored(
                                Map.of("salesAccountId", "ours"),
                                Map.of(),
                                Map.of("salesAccountId", "ours"))
                        .isEmpty());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        OwnedBranchAccountingBindings.restored(
                                Map.of("salesAccountId", "changed"),
                                Map.of(),
                                Map.of("salesAccountId", "ours")));
    }
}
