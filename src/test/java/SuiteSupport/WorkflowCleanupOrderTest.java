package SuiteSupport;

import TestSupport.WorkflowCleanupOrder;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Local regressions for the financial-child / invoice-parent cleanup boundary. */
public final class WorkflowCleanupOrderTest {
    @Test
    public void failedFinancialChildBlocksSourceAndRetainsOnlyUnresolvedChildren() {
        List<String> events = new ArrayList<>();
        AutoCloseable success = () -> events.add("settlement deleted");
        AutoCloseable blocked =
                () -> {
                    throw new AssertionError("payment still exists");
                };
        List<AutoCloseable> children = new ArrayList<>(List.of(success, blocked));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        WorkflowCleanupOrder.closeChildrenBeforeParents(
                                children, () -> events.add("invoice deleted")));
        Assert.assertEquals(events, List.of("settlement deleted"));
        Assert.assertEquals(children, List.of(blocked));
    }

    @Test
    public void successfulRetryDeletesSourceOnlyAfterTheChild() {
        List<String> events = new ArrayList<>();
        boolean[] blocked = {true};
        AutoCloseable child =
                () -> {
                    if (blocked[0]) throw new IllegalStateException("connection unavailable");
                    events.add("child");
                };
        List<AutoCloseable> children = new ArrayList<>(List.of(child));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        WorkflowCleanupOrder.closeChildrenBeforeParents(
                                children, () -> events.add("parent")));
        blocked[0] = false;
        WorkflowCleanupOrder.closeChildrenBeforeParents(children, () -> events.add("parent"));
        Assert.assertEquals(events, List.of("child", "parent"));
        Assert.assertTrue(children.isEmpty());
    }

    @Test
    public void failingSourceIsStillReportedAfterSuccessfulChildCleanup() {
        List<AutoCloseable> children = new ArrayList<>(List.of(() -> {}));
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        WorkflowCleanupOrder.closeChildrenBeforeParents(
                                children,
                                () -> {
                                    throw new IllegalStateException("source remains");
                                }));
        Assert.assertTrue(children.isEmpty());
    }
}
