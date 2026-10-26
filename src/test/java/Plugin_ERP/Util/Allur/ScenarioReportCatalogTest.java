package Plugin_ERP.Util.Allur;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.TestResult;
import java.lang.reflect.Method;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ScenarioReportCatalogTest {
    private static final String LIVE_CLASSES =
            """
            CoverageTest.AccountGroupsCoverageTest
            CoverageTest.ChartOfAccountsCoverageTest
            CoverageTest.CostCentersCoverageTest
            CoverageTest.PermissionsCoverageTest
            CoverageTest.TablesCoverageTest
            CoverageTest.UsersCoverageTest
            CoverageTest.ZatcaConfigurationCoverageTest
            DealerModelTest.AgentTest
            DealerModelTest.CustomerClassificationTest
            DealerModelTest.CustomersTest
            DealerModelTest.MainDealerTest
            DealerModelTest.RoutLineTest
            DealerModelTest.SupplierTest
            FinancialMovementsTest.CustomerDiscountNoticeTest
            FinancialMovementsTest.CustomerReceiptTest
            FinancialMovementsTest.ExpenseMovementTest
            FinancialMovementsTest.OtherRevenueMovementTest
            FinancialMovementsTest.SupplierBillOfExchangeTest
            FinancialMovementsTest.SupplierDiscountNoticeTest
            FinancialMovementsTest.TransfersAndSettlementsTest
            FinancialMovementsTest.TreasuryLedgerCoverageTest
            GenralAccountantTest.VouchersTest
            Invoice.ActionsCoverageTest
            Invoice.LinkedSalesReturnTest
            Invoice.PostingCoverageTest
            Invoice.QuotationTest
            Invoice.SalesEndToEndTest
            Invoice.SalesInvoiceCoverageTest
            Invoice.SalesListCoverageTest
            Invoice.SalesOrderTest
            Invoice.SalesReturnTest
            Invoice.SalesSettlementEndToEndTest
            Invoice.UnderReviewCoverageTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.AreaTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.BankTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.CashTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.DisEntTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.ExpensesTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.PaymentTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.RevenuesTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.RoutLineTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.StoreTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.TaypeOfDiscountTest
            Plugin_ERP.Tests.LoginTest.BasicDataTest.TreasuryHandoverTest
            Plugin_ERP.Tests.LoginTest.LoginTest
            ProductsTest.AddProductsTest
            ProductsTest.ProductCategoriesTest
            ProductsTest.ProductListTest
            ProductsTest.UnitTest
            Purches.PurchaseEndToEndTest
            Purches.PurchaseInvoiceCoverageTest
            Purches.PurchaseLinkedCoverageTest
            Purches.PurchaseListCoverageTest
            Purches.PurchaseOrderCoverageTest
            Purches.PurchaseReturnCoverageTest
            Purches.PurchaseSettlementEndToEndTest
            Purches.PurchaseSmokeTest
            Purches.SupplyChainEndToEndTest
            SpecialSalesTest.KitchenOrdersCoverageTest
            SpecialSalesTest.PointOfSaleTest
            SpecialSalesTest.RestaurantInvoiceTest
            StoreTest.AdditionOrderTest
            StoreTest.DamageAdjustmentTest
            StoreTest.ExchangeOrderTest
            StoreTest.ManufacturingSalesEndToEndTest
            StoreTest.ManufacturingTest
            StoreTest.OverflowAdjustmentCoverageTest
            StoreTest.ShortageAdjustmentCoverageTest
            StoreTest.StocktakingCoverageTest
            StoreTest.TransBetweenStoreTest
            StoreTest.WarehouseEndToEndTest
            """;

    public static final class Examples {
        @Story("حفظ أمر بيع نقدي")
        @Test(description = "عنوان بديل")
        @Description("تفاصيل التنفيذ مع المحافظة على هوية الاختبار الأصلية.")
        public void cashOrder() {}

        @Test(description = "حفظ دفعة جزئية")
        @Description("هذا وصف مطول وليس اسم الحالة")
        public void partialPayment() {}

        @Test
        public void saved_invoice_reopens() {}
    }

    private static Method method(String name) throws Exception {
        return Examples.class.getMethod(name);
    }

    private static String label(TestResult result, String name) {
        return result.getLabels().stream()
                .filter(l -> name.equals(l.getName()))
                .map(Label::getValue)
                .findFirst()
                .orElse(null);
    }

    @Test
    public void businessGroupingPreservesTestIdentityAndJavaClassLabels() throws Exception {
        var originalClass = new Label().setName("testClass").setValue("Invoice.SalesOrderTest");
        var result =
                new TestResult()
                        .setName("old")
                        .setFullName("Invoice.SalesFormTest.cashOrder")
                        .setHistoryId("original-history")
                        .setTestCaseId("original-case");
        result.getLabels()
                .addAll(
                        List.of(
                                originalClass,
                                new Label().setName("package").setValue("Invoice"),
                                new Label().setName("suite").setValue("Old suite")));
        ScenarioReportCatalog.apply(result, "Invoice.SalesOrderTest", method("cashOrder"));
        Assert.assertEquals(result.getFullName(), "Invoice.SalesFormTest.cashOrder");
        Assert.assertEquals(result.getHistoryId(), "original-history");
        Assert.assertEquals(result.getTestCaseId(), "original-case");
        Assert.assertTrue(result.getLabels().contains(originalClass));
        Assert.assertEquals(label(result, "parentSuite"), "المبيعات");
        Assert.assertEquals(label(result, "suite"), "أمر البيع");
        Assert.assertEquals(label(result, "subSuite"), "حالات الاختبار");
        Assert.assertEquals(label(result, "package"), "المبيعات.أمر البيع");
        Assert.assertEquals(label(result, "javaClass"), "Invoice.SalesOrderTest");
        Assert.assertEquals(label(result, "javaMethod"), "cashOrder");
        Assert.assertEquals(label(result, "testMethod"), "حفظ أمر بيع نقدي");
        ScenarioReportCatalog.apply(result, "Invoice.SalesOrderTest", method("cashOrder"));
        Assert.assertEquals(
                result.getLabels().stream().filter(l -> "package".equals(l.getName())).count(), 1L);
    }

    @Test
    public void salesOrderAndInvoiceHaveSeparateBusinessScreens() {
        Assert.assertEquals(
                ScenarioReportCatalog.screenFor("Invoice.SalesOrderTest").screen(), "أمر البيع");
        Assert.assertEquals(
                ScenarioReportCatalog.screenFor("Invoice.SalesInvoiceCoverageTest").screen(),
                "فاتورة المبيعات");
    }

    @Test
    public void everyInventoriedLiveClassHasAStaticEntry() {
        var classes = LIVE_CLASSES.strip().lines().map(String::strip).toList();
        Assert.assertEquals(classes.size(), 71);
        Assert.assertEquals(classes.stream().distinct().count(), 71L);
        for (String name : classes) {
            var entry = ScenarioReportCatalog.screenFor(name);
            Assert.assertNotNull(entry, name);
            Assert.assertFalse(entry.module().isBlank(), name);
            Assert.assertFalse(entry.screen().isBlank(), name);
        }
    }

    @Test
    public void storyIsPreferredAndDescriptionIsKeptOutOfTheTitle() throws Exception {
        var result = new TestResult();
        ScenarioReportCatalog.apply(result, "Invoice.SalesOrderTest", method("cashOrder"));
        Assert.assertEquals(result.getName(), "حفظ أمر بيع نقدي");
        Assert.assertEquals(
                result.getDescription(), "تفاصيل التنفيذ مع المحافظة على هوية الاختبار الأصلية.");
        ScenarioReportCatalog.apply(result, "Invoice.SalesOrderTest", method("partialPayment"));
        Assert.assertEquals(result.getName(), "حفظ دفعة جزئية");
        ScenarioReportCatalog.apply(
                result, "Invoice.SalesOrderTest", method("saved_invoice_reopens"));
        Assert.assertEquals(result.getName(), "saved invoice reopens");
    }

    @Test
    public void unmappedLocalChecksAreUntouched() throws Exception {
        var result =
                new TestResult()
                        .setName("Local check")
                        .setDescription("Local description")
                        .setFullName("local.original")
                        .setHistoryId("local-history");
        var original = new Label().setName("package").setValue("SuiteSupport");
        result.getLabels().add(original);
        ScenarioReportCatalog.apply(
                result, "SuiteSupport.SuiteDefinitionsTest", method("cashOrder"));
        Assert.assertEquals(result.getName(), "Local check");
        Assert.assertEquals(result.getDescription(), "Local description");
        Assert.assertEquals(result.getFullName(), "local.original");
        Assert.assertEquals(result.getHistoryId(), "local-history");
        Assert.assertEquals(result.getLabels(), List.of(original));
        Assert.assertNull(ScenarioReportCatalog.screenFor(null));
    }
}
