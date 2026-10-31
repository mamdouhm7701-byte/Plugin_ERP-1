/* شرح الملف: يراجع إعادة محاولة الحذف بعد التأكد من أثر المحاولة السابقة، وحماية السجل عند تغيّر ملكيته. */
package TestSupport;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureRecordDeletionTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private final Row row = new Row("Items", "item", "QA-owned", Map.of());

    @Test
    public void anUnreadableOutcomeNeverAllowsASecondDelete() {
        var deletes = new AtomicInteger();
        var readsAfterDelete = new AtomicInteger();
        AssertionError failure =
                Assert.expectThrows(
                        AssertionError.class,
                        () ->
                                FixtureRecordDeletion.delete(
                                        row,
                                        r -> {
                                            if (deletes.get() == 0) return Map.of("owner", "ours");
                                            readsAfterDelete.incrementAndGet();
                                            throw new FixtureApiFailure(
                                                    500, "SQL read unavailable");
                                        },
                                        (r, s) -> true,
                                        r -> {
                                            deletes.incrementAndGet();
                                            throw new TimeoutException("DELETE outcome unknown");
                                        }));
        Assert.assertEquals(deletes.get(), 1);
        Assert.assertEquals(readsAfterDelete.get(), 3);
        Assert.assertTrue(failure.getMessage().contains("لم نرسل حذفًا آخر"));
    }

    @Test
    public void aTransientReadMustRecoverBeforeRetryingAnUncommittedDelete() {
        var deletes = new AtomicInteger();
        var readAttempts = new AtomicInteger();
        var verified = new AtomicBoolean();
        FixtureRecordDeletion.delete(
                row,
                r -> {
                    if (deletes.get() == 0) return Map.of("owner", "ours");
                    if (deletes.get() == 2) return null;
                    if (readAttempts.incrementAndGet() < 3)
                        throw new FixtureApiFailure(500, "temporary read failure");
                    verified.set(true);
                    return Map.of("owner", "ours");
                },
                (r, s) -> true,
                r -> {
                    if (deletes.incrementAndGet() == 1) throw new TimeoutException("lost response");
                    Assert.assertTrue(
                            verified.get(), "Do not repeat DELETE while readback remains unknown");
                });
        Assert.assertEquals(deletes.get(), 2);
        Assert.assertEquals(readAttempts.get(), 3);
    }

    @Test
    public void aCommittedDeleteFollowedByTransientReadFailureIsNotRepeated() {
        var deletes = new AtomicInteger();
        var readsAfterDelete = new AtomicInteger();
        FixtureRecordDeletion.delete(
                row,
                r -> {
                    if (deletes.get() == 0) return Map.of("owner", "ours");
                    if (readsAfterDelete.incrementAndGet() < 3)
                        throw new TimeoutException("read unavailable");
                    return null;
                },
                (r, s) -> true,
                r -> deletes.incrementAndGet());
        Assert.assertEquals(deletes.get(), 1);
        Assert.assertEquals(readsAfterDelete.get(), 3);
    }

    @Test
    public void committedTimeoutDoesNotSendSecondDelete() {
        var state = new AtomicReference<Map<String, Object>>(Map.of("id", "item"));
        var deletes = new AtomicInteger();
        FixtureRecordDeletion.delete(
                row,
                r -> state.get(),
                (r, s) -> true,
                r -> {
                    deletes.incrementAndGet();
                    state.set(null);
                    throw new TimeoutException("lost response");
                });
        Assert.assertEquals(deletes.get(), 1);
    }

    @Test
    public void uncommittedTimeoutIsRetriedOnlyAfterReadConfirmsRecordRemains() {
        var state = new AtomicReference<Map<String, Object>>(Map.of("id", "item"));
        var deletes = new AtomicInteger();
        FixtureRecordDeletion.delete(
                row,
                r -> state.get(),
                (r, s) -> true,
                r -> {
                    if (deletes.incrementAndGet() == 1) throw new TimeoutException("not committed");
                    state.set(null);
                });
        Assert.assertEquals(deletes.get(), 2);
    }

    @Test
    public void notFoundDeleteCannotHideExistingRecordAndNonRetryableFailureIsPreserved() {
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureRecordDeletion.delete(
                                row,
                                r -> Map.of("id", "item"),
                                (r, s) -> true,
                                r -> {
                                    throw new FixtureApiFailure(400, "Common.ObjectNotFound");
                                }));
        var failure = new FixtureApiFailure(403, "forbidden");
        Assert.assertSame(
                Assert.expectThrows(
                        FixtureApiFailure.class,
                        () ->
                                FixtureRecordDeletion.delete(
                                        row,
                                        r -> Map.of("id", "item"),
                                        (r, s) -> true,
                                        r -> {
                                            throw failure;
                                        })),
                failure);
    }

    @Test
    public void changedOwnershipAfterRetryableFailurePreventsFurtherDeletion() {
        var state = new AtomicReference<Map<String, Object>>(Map.of("owner", "ours"));
        var deletes = new AtomicInteger();
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureRecordDeletion.delete(
                                row,
                                r -> state.get(),
                                (r, s) -> "ours".equals(s.get("owner")),
                                r -> {
                                    deletes.incrementAndGet();
                                    state.set(Map.of("owner", "foreign"));
                                    throw new FixtureApiFailure(500, "temporary");
                                }));
        Assert.assertEquals(deletes.get(), 1);
    }

    @Test
    public void acknowledgedButUncommittedDeleteDoesNotReadTheSameIdentityTwice() {
        var state = new AtomicReference<Map<String, Object>>(Map.of("owner", "ours"));
        var reads = new AtomicInteger();
        var deletes = new AtomicInteger();
        FixtureRecordDeletion.delete(
                row,
                r -> {
                    reads.incrementAndGet();
                    return state.get();
                },
                (r, v) -> "ours".equals(v.get("owner")),
                r -> {
                    if (deletes.incrementAndGet() == 2) state.set(null);
                });
        Assert.assertEquals(deletes.get(), 2);
        Assert.assertEquals(reads.get(), 3);
    }
}
