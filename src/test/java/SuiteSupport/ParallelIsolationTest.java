package SuiteSupport;

import TestSupport.BranchMutationLock;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.ITestClass;
import org.testng.annotations.Test;

public final class ParallelIsolationTest {
    private ITestClass type(String name) {
        return (ITestClass)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {ITestClass.class},
                        (p, m, a) -> m.getName().equals("getName") ? name : null);
    }

    @Test
    public void sixOwnedClassesCanOverlapButAnUnreviewedClassWaitsForAllOfThem() throws Exception {
        var listener = new BranchIsolationListener();
        var pool = Executors.newFixedThreadPool(7);
        var ready = new CountDownLatch(6);
        var release = new CountDownLatch(1);
        var requested = new CountDownLatch(1);
        var entered = new CountDownLatch(1);
        var active = new AtomicInteger();
        var peak = new AtomicInteger();
        List<Future<?>> readers = new ArrayList<>();
        try {
            for (int i = 0; i < 6; i++)
                readers.add(
                        pool.submit(
                                () -> {
                                    var cls = type("Invoice.SalesEndToEndTest");
                                    listener.onBeforeClass(cls);
                                    try {
                                        int count = active.incrementAndGet();
                                        peak.accumulateAndGet(count, Math::max);
                                        ready.countDown();
                                        if (!release.await(10, TimeUnit.SECONDS))
                                            throw new AssertionError("Readers not released");
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        throw new IllegalStateException(e);
                                    } finally {
                                        active.decrementAndGet();
                                        listener.onAfterClass(cls);
                                    }
                                }));
            Assert.assertTrue(ready.await(10, TimeUnit.SECONDS));
            var writer =
                    pool.submit(
                            () -> {
                                var cls = type("Unreviewed.NewScreen");
                                requested.countDown();
                                listener.onBeforeClass(cls);
                                try {
                                    Assert.assertEquals(active.get(), 0);
                                    entered.countDown();
                                } finally {
                                    listener.onAfterClass(cls);
                                }
                            });
            Assert.assertTrue(requested.await(5, TimeUnit.SECONDS));
            Assert.assertFalse(entered.await(150, TimeUnit.MILLISECONDS));
            release.countDown();
            for (var reader : readers) reader.get(10, TimeUnit.SECONDS);
            writer.get(10, TimeUnit.SECONDS);
            Assert.assertEquals(peak.get(), 6);
        } finally {
            release.countDown();
            pool.shutdownNow();
        }
    }

    @Test
    public void sixWorkersDoNotLoseExistingOrNewBranchStoreMemberships() throws Exception {
        var pool = Executors.newFixedThreadPool(6);
        var stores = new ArrayList<Integer>();
        stores.add(-1);
        var active = new AtomicInteger();
        var peak = new AtomicInteger();
        List<Future<?>> tasks = new ArrayList<>();
        try {
            for (int worker = 0; worker < 6; worker++) {
                final int index = worker;
                tasks.add(
                        pool.submit(
                                () -> {
                                    for (int row = 0; row < 100; row++)
                                        synchronized (BranchMutationLock.monitor()) {
                                            peak.accumulateAndGet(
                                                    active.incrementAndGet(), Math::max);
                                            try {
                                                var snapshot = new ArrayList<>(stores);
                                                Thread.yield();
                                                snapshot.add(index * 100 + row);
                                                stores.clear();
                                                stores.addAll(snapshot);
                                            } finally {
                                                active.decrementAndGet();
                                            }
                                        }
                                }));
            }
            for (var task : tasks) task.get(10, TimeUnit.SECONDS);
            Assert.assertEquals(stores.size(), 601);
            Assert.assertEquals(new java.util.HashSet<>(stores).size(), 601);
            Assert.assertTrue(stores.contains(-1));
            Assert.assertEquals(peak.get(), 1);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    public void branchDefaultsAndOperatorJourneysAreNotScheduledAsIndependentClasses() {
        for (String name :
                List.of(
                        "SpecialSalesTest.PointOfSaleTest",
                        "SpecialSalesTest.RestaurantInvoiceTest",
                        "SpecialSalesTest.KitchenOrdersCoverageTest",
                        "Invoice.PostingCoverageTest",
                        "FinancialMovementsTest.TreasuryLedgerCoverageTest",
                        "CoverageTest.PermissionsCoverageTest"))
            Assert.assertFalse(BranchIsolationListener.independent(name), name);
    }
}
