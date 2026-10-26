package TestSupport;

/** One JVM coordinates the read/modify/write of the branch's store membership. */
public final class BranchMutationLock {
    private static final Object MONITOR = new Object();

    private BranchMutationLock() {}

    public static Object monitor() {
        return MONITOR;
    }
}
