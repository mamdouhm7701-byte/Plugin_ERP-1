/* شرح الملف: منع تغيير إعدادات نفس الفرع من أكثر من تجربة في نفس الوقت.
 */
package TestSupport;

public final class BranchMutationLock {
    private static final Object MONITOR = new Object();

    private BranchMutationLock() {}

    public static Object monitor() {
        return MONITOR;
    }
}
