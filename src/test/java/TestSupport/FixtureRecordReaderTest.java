/* شرح الملف: يراجع أن غياب السجل يثبت بالقراءة، وأن خطأ الخادم وحده لا يسمح باعتباره محذوفًا. */
package TestSupport;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureRecordReaderTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private final Row customer = new Row("Customers", "customer", "QA-owned", Map.of());

    @Test
    public void acceptsBlankAndDefaultDeletedDtoButKeepsErrorDtoVisible() {
        Assert.assertNull(
                new FixtureRecordReader((u, v) -> Map.of("entity", ""), Set.of()).read(customer));
        Assert.assertNull(
                new FixtureRecordReader((u, v) -> Map.of("entity", Map.of()), Set.of())
                        .read(customer));
        Assert.assertEquals(
                new FixtureRecordReader(
                                (u, v) -> Map.of("entity", Map.of("error", "failed")), Set.of())
                        .read(customer)
                        .get("error"),
                "failed");
    }

    @Test
    public void knownMissingHttpCodesCanConfirmAbsenceButOtherFailureIsPreserved() {
        for (int status : List.of(404, 410))
            Assert.assertNull(
                    new FixtureRecordReader(
                                    (u, v) -> {
                                        throw new FixtureApiFailure(status, "missing");
                                    },
                                    Set.of())
                            .read(customer));
        var failure = new FixtureApiFailure(403, "forbidden");
        Assert.assertSame(
                Assert.expectThrows(
                        FixtureApiFailure.class,
                        () ->
                                new FixtureRecordReader(
                                                (u, v) -> {
                                                    throw failure;
                                                },
                                                Set.of())
                                        .read(customer)),
                failure);
    }

    @Test
    public void serverErrorRequiresApprovedEndpointAndVerifiedAbsenceInList() {
        var failure = new FixtureApiFailure(500, "detail unavailable");
        var reader =
                new FixtureRecordReader(
                        (u, v) -> {
                            if (u.equals("Customers")) throw failure;
                            return Map.of(
                                    "entity", Map.of("pageData", List.of(), "totalRecords", 0));
                        },
                        Set.of("Customers"));
        Assert.assertNull(reader.read(customer));
        Assert.assertSame(
                Assert.expectThrows(
                        FixtureApiFailure.class,
                        () ->
                                new FixtureRecordReader(
                                                (u, v) -> {
                                                    throw failure;
                                                },
                                                Set.of())
                                        .read(customer)),
                failure);
    }

    @Test
    public void existingRecordAfterServerErrorStillRaisesOriginalFailure() {
        var failure = new FixtureApiFailure(500, "detail unavailable");
        var reader =
                new FixtureRecordReader(
                        (u, v) -> {
                            if (u.equals("Customers")) throw failure;
                            return Map.of(
                                    "entity",
                                    Map.of(
                                            "pageData",
                                            List.of(Map.of("id", "customer")),
                                            "totalRecords",
                                            1));
                        },
                        Set.of("Customers"));
        Assert.assertSame(
                Assert.expectThrows(FixtureApiFailure.class, () -> reader.read(customer)), failure);
    }

    @Test
    public void malformedReadOrNonAdvancingListNeverProvesDeletion() {
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        new FixtureRecordReader((u, v) -> Map.of("entity", List.of()), Set.of())
                                .read(customer));
        var calls = new AtomicInteger();
        var reader =
                new FixtureRecordReader(
                        (u, v) -> {
                            calls.incrementAndGet();
                            if (u.equals("Customers")) throw new FixtureApiFailure(500, "failed");
                            return Map.of(
                                    "entity",
                                    Map.of(
                                            "pageData",
                                            List.of(Map.of("id", "other")),
                                            "totalRecords",
                                            2));
                        },
                        Set.of("Customers"));
        Assert.expectThrows(AssertionError.class, () -> reader.read(customer));
        Assert.assertEquals(calls.get(), 3);
    }
}
