package TestSupport;

import java.util.ArrayList;
import java.util.List;

/** Cross-module children must be removed before the fixture owner of their source documents. */
public final class WorkflowCleanupOrder {
    private WorkflowCleanupOrder() {}

    public static <T extends AutoCloseable> void closeChildrenBeforeParents(
            List<T> children, Runnable parentCleanup) {
        List<String> failures = new ArrayList<>();
        for (T child : new ArrayList<>(children)) {
            try {
                child.close();
                children.remove(child);
            } catch (Exception | AssertionError failure) {
                failures.add(failure.toString());
            }
        }
        if (!failures.isEmpty()) {
            throw new AssertionError(
                    "Workflow children remain; source cleanup is deferred: "
                            + String.join("; ", failures));
        }
        parentCleanup.run();
    }
}
