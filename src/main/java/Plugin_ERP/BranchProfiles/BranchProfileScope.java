package Plugin_ERP.BranchProfiles;

import java.util.Optional;

/** Pins one immutable expectation to the owning test thread, including its fixture cleanup. */
public final class BranchProfileScope implements AutoCloseable {
    private static final ThreadLocal<BranchProfileScope> ACTIVE = new ThreadLocal<>();
    private final BranchProfileScope previous;
    private final BranchProfile profile;
    private final Thread owner = Thread.currentThread();
    private boolean closed;

    private BranchProfileScope(BranchProfile profile) {
        this.profile = java.util.Objects.requireNonNull(profile);
        previous = ACTIVE.get();
        ACTIVE.set(this);
    }

    public static BranchProfileScope bind(BranchProfile profile) {
        return new BranchProfileScope(profile);
    }

    public static Optional<BranchProfile> current() {
        var active = ACTIVE.get();
        return active == null ? Optional.empty() : Optional.of(active.profile);
    }

    public static Optional<BranchProfile> currentOrSelected() {
        var profile = current();
        return profile.isPresent() ? profile : BranchProfiles.selected();
    }

    @Override
    public void close() {
        if (closed) return;
        if (Thread.currentThread() != owner || ACTIVE.get() != this) {
            throw new IllegalStateException(
                    "Branch profile scopes must close on their owner thread in reverse order");
        }
        if (previous == null) ACTIVE.remove();
        else ACTIVE.set(previous);
        closed = true;
    }
}
