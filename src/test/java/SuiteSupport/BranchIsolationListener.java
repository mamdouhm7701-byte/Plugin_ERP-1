package SuiteSupport;

import org.testng.IClassListener;
import org.testng.ITestClass;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Owned-data classes may overlap; unreviewed or branch-default classes stay exclusive. */
public final class BranchIsolationListener implements IClassListener {
    private static final Set<String> INDEPENDENT =
            Set.of(
                    "Invoice.SalesEndToEndTest",
                    "Invoice.SalesSettlementEndToEndTest",
                    "Purches.PurchaseEndToEndTest",
                    "Purches.PurchaseSettlementEndToEndTest",
                    "Purches.SupplyChainEndToEndTest",
                    "Purches.PurchaseLinkedCoverageTest",
                    "StoreTest.ManufacturingSalesEndToEndTest",
                    "StoreTest.WarehouseEndToEndTest",
                    "StoreTest.ShortageAdjustmentCoverageTest",
                    "StoreTest.OverflowAdjustmentCoverageTest",
                    "ProductsTest.UnitTest",
                    "ProductsTest.ProductCategoriesTest",
                    "ProductsTest.AddProductsTest",
                    "ProductsTest.ProductListTest",
                    "Invoice.SalesInvoiceCoverageTest",
                    "Invoice.UnderReviewCoverageTest",
                    "Purches.PurchaseInvoiceCoverageTest",
                    "StoreTest.TransBetweenStoreTest",
                    "StoreTest.StocktakingCoverageTest",
                    "FinancialMovementsTest.CustomerReceiptTest",
                    "CoverageTest.TablesCoverageTest",
                    "CoverageTest.AccountGroupsCoverageTest",
                    "CoverageTest.CostCentersCoverageTest",
                    "CoverageTest.ChartOfAccountsCoverageTest",
                    "CoverageTest.UsersCoverageTest",
                    "CoverageTest.ZatcaConfigurationCoverageTest",
                    "Invoice.ActionsCoverageTest",
                    // Same reviewed fixture owners: per-class data and guarded branch-store
                    // membership.
                    "Invoice.QuotationTest",
                    "Invoice.SalesOrderTest",
                    "Invoice.SalesReturnTest",
                    "Invoice.LinkedSalesReturnTest",
                    "Invoice.SalesListCoverageTest",
                    "Purches.PurchaseOrderCoverageTest",
                    "Purches.PurchaseReturnCoverageTest",
                    "Purches.PurchaseListCoverageTest",
                    "StoreTest.ExchangeOrderTest",
                    "StoreTest.AdditionOrderTest",
                    "StoreTest.ManufacturingTest",
                    "StoreTest.DamageAdjustmentTest",
                    // These independent master screens also passed the three-browser live pilot.
                    "DealerModelTest.CustomerClassificationTest",
                    "Plugin_ERP.Tests.LoginTest.BasicDataTest.BankTest");
    private static final ReentrantReadWriteLock GATE = new ReentrantReadWriteLock(true);
    private final ThreadLocal<Deque<Lock>> heldLocks = ThreadLocal.withInitial(ArrayDeque::new);

    public static boolean independent(String className) {
        return INDEPENDENT.contains(className);
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        Lock lock = independent(testClass.getName()) ? GATE.readLock() : GATE.writeLock();
        lock.lock();
        heldLocks.get().push(lock);
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        Deque<Lock> classLocks = heldLocks.get();
        if (classLocks.isEmpty()) {
            throw new IllegalStateException(
                    "Missing class isolation lease: " + testClass.getName());
        }
        classLocks.pop().unlock();
        if (classLocks.isEmpty()) {
            heldLocks.remove();
        }
    }
}
