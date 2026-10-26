package SuiteSupport;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.internal.Parser;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

/** تحقق محلي لتعريفات التشغيل؛ لا يفتح المتصفح ولا ينشئ بيانات ERP. */
public final class SuiteDefinitionsTest {
  private static final Path RESOURCES = Path.of("src", "test", "resources");

  @DataProvider(name = "testLevels")
  public Object[][] testLevels() {
    return new Object[][] {
        {"smoke"}, {"sanity"}, {"regression"}, {"e2e"}, {"login"}, {"ci"}, {"coverage-expansion"}, {"coverage-observations"}, {"coverage-recovery"}, {"coverage-verification"},
        {"additional/standalone/browser-isolation"},
        {"additional/standalone/parallel-three-pilot"},
        {"additional/standalone/parallel-three-screens"},
        {"additional/standalone/point-of-sale"}, {"additional/standalone/restaurant"}, {"special-sales"}, {"basic-data"}, {"general-accounting"},
        {"additional/standalone/fixture-lifecycle"}, {"additional/internal/regression-additional"}, {"additional/internal/regression-workflows"},
        {"additional/standalone/fixture-refactor-smoke"},
        {"additional/standalone/fixture-data-refactor-smoke"},
        {"additional/standalone/purchase-calendar-smoke"},
        {"additional/standalone/document-list-refactor-smoke"},
        {"additional/standalone/form-interactions-smoke"},
        {"additional/standalone/page-object-refactor"},
        {"additional/standalone/page-object-core-refactor"},
        {"additional/standalone/page-object-final-refactor"},
        {"additional/standalone/products-finance-interactions-smoke"},
        {"additional/standalone/acceptance"}, {"additional/standalone/module-lifecycle-smoke"}, {"additional/standalone/allure-journey-smoke"}
    };
  }

  @Test public void parallelPilotHasExactlyThreeIsolatedSequentialLanes() throws Exception {
    var suites = new Parser(RESOURCES.resolve("additional/standalone/parallel-three-pilot-testng.xml")
        .toString()).parseToList();
    Assert.assertEquals(suites.size(), 1);
    var suite = suites.getFirst();
    Assert.assertEquals(suite.getParallel(), XmlSuite.ParallelMode.TESTS);
    Assert.assertEquals(suite.getThreadCount(), 3);
    Assert.assertEquals(suite.getTests().size(), 3);
    for (var lane : suite.getTests()) {
      Assert.assertEquals(lane.getParallel(), XmlSuite.ParallelMode.NONE);
      Assert.assertEquals(lane.getClasses().size(), 1);
      Assert.assertEquals(lane.getClasses().getFirst().getIncludedMethods().size(), 1);
    }
    Assert.assertEquals(new HashSet<>(selectedCases("additional/standalone/parallel-three-pilot")), Set.of(
        "ProductsTest.UnitTest#AddNewUnite",
        "Plugin_ERP.Tests.LoginTest.BasicDataTest.BankTest#BK_003_CreateBankAndVerifySavedFields",
        "DealerModelTest.CustomerClassificationTest#CC_005_OwnedClassificationCreateEditDelete"));
  }

  @Test(dataProvider = "testLevels")
  public void suiteSelectsRealEnabledTestsWithoutDuplicates(String level) throws Exception {
    List<String> cases = selectedCases(level);
    Assert.assertFalse(cases.isEmpty(), "Empty suite: " + level);
    Set<String> unique = new HashSet<>();
    for (String test : cases) {
      Assert.assertTrue(unique.add(test), "Repeated class/method selection in " + level + ": " + test);
    }
  }

  @Test
  public void quickChecksStaySmallAndRegressionIncludesTheWorkflows() throws Exception {
    List<String> smoke = selectedCases("smoke");
    List<String> sanity = selectedCases("sanity");
    List<String> e2e = selectedCases("e2e");
    List<String> regression = selectedCases("regression");
    Assert.assertEquals(smoke.size(), 13, "Core availability plus representative new screens and access protection");
    Assert.assertEquals(sanity.size(), 56, "Focused screen checks after removal of the unavailable audit screen");
    Assert.assertEquals(e2e.size(), 41, "Existing journeys plus adjustments, kitchen, posting and treasury");
    Assert.assertTrue(regression.size() > sanity.size());
    Assert.assertTrue(regression.containsAll(smoke), "Regression omitted a smoke contract");
    Assert.assertTrue(regression.containsAll(sanity), "Regression omitted a sanity contract");
    Assert.assertTrue(regression.containsAll(e2e), "Regression omitted an E2E workflow");
    Assert.assertFalse(regression.stream().anyMatch(name -> name.startsWith("Invoice.InvoiceTest#")
        || name.startsWith("Invoice.SalesInvoiceTestCases#")
        || name.startsWith("Purches.PurchesBillsTest#")), "Do not duplicate compatibility aliases");
  }

  @Test public void expandedCoverageIsDistributedByTestLevel() throws Exception {
    var smoke=selectedCases("smoke");var sanity=selectedCases("sanity");
    var e2e=selectedCases("e2e");var regression=selectedCases("regression");
    Assert.assertTrue(smoke.containsAll(Set.of(
        "CoverageTest.TablesCoverageTest#createsAndReadsBackExactOwnedRecord",
        "StoreTest.StocktakingCoverageTest#savesFractionalQuantityAndChecksStockEffect",
        "Invoice.UnderReviewCoverageTest#savesUnderReviewWithoutPostingInventory",
        "CoverageTest.PermissionsCoverageTest#unauthenticatedDirectRouteRequiresLogin")));
    Assert.assertFalse(smoke.stream().anyMatch(c->c.startsWith("Invoice.PostingCoverageTest#")
        ||c.startsWith("FinancialMovementsTest.TreasuryLedgerCoverageTest#")
        ||c.startsWith("StoreTest.ShortageAdjustmentCoverageTest#")
        ||c.startsWith("StoreTest.OverflowAdjustmentCoverageTest#")),"Keep longer posted-document journeys out of smoke");
    var sanityClasses=new HashSet<String>();sanity.forEach(c->sanityClasses.add(c.substring(0,c.indexOf('#'))));
    var expansionClasses=new HashSet<String>();selectedCases("coverage-expansion")
        .forEach(c->expansionClasses.add(c.substring(0,c.indexOf('#'))));
    Assert.assertTrue(sanityClasses.containsAll(expansionClasses),"Sanity omitted a newly covered screen");
    Assert.assertTrue(sanity.containsAll(Set.of(
        "FinancialMovementsTest.CustomerReceiptTest#FM_ExportExcel",
        "Invoice.SalesInvoiceCoverageTest#savesAndPrintsOwnedInvoice",
        "Purches.PurchaseInvoiceCoverageTest#savesAndPrintsOwnedDocument",
        "ProductsTest.ProductListTest#PR_SaveWithoutPrint")));
    Assert.assertTrue(e2e.containsAll(Set.of(
        "StoreTest.ShortageAdjustmentCoverageTest#generatedFractionalAdjustmentMatchesOwnedItemAndStock",
        "StoreTest.OverflowAdjustmentCoverageTest#generatedFractionalAdjustmentMatchesOwnedItemAndStock",
        "SpecialSalesTest.KitchenOrdersCoverageTest#preparesDeliversAndClosesOnlyOwnedOrder",
        "Invoice.PostingCoverageTest#postsAndUnpostsOnlyCapturedOwnedInvoice",
        "FinancialMovementsTest.TreasuryLedgerCoverageTest#reconcilesIndependentCashAndOtherPaymentLedgerInScreenAndPrint")));
    Assert.assertTrue(regression.containsAll(selectedCases("coverage-expansion")));
    Assert.assertTrue(regression.containsAll(selectedCases("coverage-observations")),"Regression omitted an audit correction");
  }

  @Test
  public void specialSalesUseFullRegressionAndOnlyPersistedJourneysInE2e() throws Exception {
    String pos = "SpecialSalesTest.PointOfSaleTest#";
    String restaurant = "SpecialSalesTest.RestaurantInvoiceTest#";
    String cleanup = "SpecialSalesTest.SpecialSalesCleanupTest#";
    List<String> special = selectedCases("special-sales");
    List<String> regression = selectedCases("regression");
    List<String> e2e = selectedCases("e2e");
    List<String> smoke = selectedCases("smoke");
    List<String> sanity = selectedCases("sanity");
    Assert.assertTrue(smoke.contains(pos + "savesCashWithOwnedPayment"));
    Assert.assertTrue(smoke.contains(restaurant + "savesPaidExternalOrder"));
    Assert.assertTrue(sanity.containsAll(Set.of(
        pos + "savesCashWithOwnedPayment", pos + "savesCreditAndDeductsStock",
        pos + "cancellingDeleteKeepsInvoice", restaurant + "savesPaidExternalOrder",
        restaurant + "addingToHoldDeductsOnlyIncrement", restaurant + "payingHoldDoesNotDeductAgain")),
        "Cash, credit, held-order totals and non-duplicate stock posting remain in sanity");
    Assert.assertEquals(special.stream().filter(c -> c.startsWith(pos)).count(), 15L);
    Assert.assertEquals(special.stream().filter(c -> c.startsWith(restaurant)).count(), 15L);
    Assert.assertEquals(special.stream().filter(c -> c.startsWith(cleanup)).count(), 8L);
    Assert.assertTrue(regression.containsAll(special), "Regression must retain both complete screens and recovery contracts");
    Assert.assertTrue(selectedCases("additional/standalone/fixture-lifecycle").containsAll(
        special.stream().filter(c -> c.startsWith(cleanup)).toList()));
    Set<String> cashierJourneys = Set.of(
        pos + "editingSavedQuantityAppliesOnlyDifference",
        pos + "deletingSavedInvoiceRestoresStock",
        pos + "printsSavedReceiptWithoutPostingAgain",
        restaurant + "addingToHoldDeductsOnlyIncrement",
        restaurant + "payingHoldDoesNotDeductAgain",
        restaurant + "deletingPaidOrderRestoresStock",
        restaurant + "printsPaidReceiptWithoutPostingAgain");
    Set<String> selectedCashierJourneys = new HashSet<>(e2e.stream()
        .filter(c -> c.startsWith(pos) || c.startsWith(restaurant)).toList());
    Assert.assertEquals(selectedCashierJourneys, cashierJourneys,
        "Draft-only screen checks must not silently expand E2E");
    Assert.assertTrue(e2e.stream().noneMatch(c -> c.startsWith(cleanup)),
        "Mock cleanup regressions do not establish a live business journey");
    Assert.assertTrue(smoke.stream().noneMatch(c -> c.startsWith(cleanup))
        && sanity.stream().noneMatch(c -> c.startsWith(cleanup)),
        "Screen smoke and sanity must not be inflated with local cleanup tests");
  }

  @Test
  public void regressionRetainsAllPriceBusinessCasesWithoutHidingKnownFailures() throws Exception {
    List<String> regression = selectedCases("regression");
    Set<String> priceCases = Set.of(
        "PR_AllPricesZero", "PR_AllPricesEqual",
        "PR_SalePriceEqualsMinimum", "PR_SalePriceEqualsMaximum",
        "PR_NegativeSalePrice", "PR_MinimumAboveSalePrice", "PR_MaximumBelowSalePrice",
        "PR_MinimumAboveMaximum", "PR_MaximumBelowMinimum",
        "PR_SalePriceBelowMinimum", "PR_SalePriceAboveMaximum");
    Class<?> type = Class.forName("ProductsTest.AddProductsTest");
    for (String name : priceCases) {
      String selected = type.getName() + "#" + name;
      Assert.assertEquals(java.util.Collections.frequency(regression, selected), 1,
          "Regression must select each price contract exactly once: " + selected);
      Test annotation = type.getMethod(name).getAnnotation(Test.class);
      Assert.assertNotNull(annotation, "Missing price test annotation: " + name);
      Assert.assertTrue(annotation.enabled(), "Price contract must stay enabled: " + name);
      Assert.assertEquals(annotation.expectedExceptions().length, 0,
          "Known price defects must remain failures: " + name);
    }
  }

  @Test public void parallelScreensRetainOnlyTheReviewedClassesAndTheirRegressionCases() throws Exception {
    var suites = new Parser(RESOURCES.resolve("additional/standalone/parallel-three-screens-testng.xml")
        .toString()).parseToList();
    Assert.assertEquals(suites.size(), 1);
    var suite = suites.getFirst();
    Assert.assertEquals(suite.getParallel(), XmlSuite.ParallelMode.TESTS);
    Assert.assertEquals(suite.getThreadCount(), 3);
    Assert.assertEquals(suite.getTests().size(), 3);
    var classes = new HashSet<String>();
    for (var lane : suite.getTests()) {
      Assert.assertEquals(lane.getParallel(), XmlSuite.ParallelMode.NONE);
      Assert.assertEquals(lane.getClasses().size(), 1);
      classes.add(lane.getClasses().getFirst().getName());
      Assert.assertTrue(lane.getClasses().getFirst().getIncludedMethods().isEmpty());
      Assert.assertTrue(lane.getClasses().getFirst().getExcludedMethods().isEmpty());
    }
    Assert.assertEquals(classes, Set.of("ProductsTest.UnitTest",
        "Plugin_ERP.Tests.LoginTest.BasicDataTest.BankTest", "DealerModelTest.CustomerClassificationTest"));
    var expected = selectedCases("regression").stream()
        .filter(key -> classes.contains(key.substring(0, key.indexOf('#')))).toList();
    Assert.assertEquals(new HashSet<>(selectedCases("additional/standalone/parallel-three-screens")),
        new HashSet<>(expected));
  }

  private static List<String> selectedCases(String level) throws Exception {
    List<String> selected = new ArrayList<>();
    for (XmlSuite suite : new Parser(RESOURCES.resolve(level + "-testng.xml").toString()).parseToList()) {
      collect(suite, selected);
    }
    return selected;
  }

  private static void collect(XmlSuite suite, List<String> selected) throws Exception {
    boolean pilot = Set.of("Parallel pilot - three independent screens",
        "Parallel screens - three independent screens").contains(suite.getName());
    Assert.assertEquals(suite.getParallel(), pilot ? XmlSuite.ParallelMode.TESTS : XmlSuite.ParallelMode.NONE,
        "Page fixture lifecycles require sequential suites: " + suite.getName());
    for (XmlTest test : suite.getTests()) {
      Assert.assertEquals(test.getParallel(), XmlSuite.ParallelMode.NONE,
          "Cases within a page fixture group require sequential tests: " + test.getName());
      for (XmlClass xmlClass : test.getXmlClasses()) {
        Class<?> type = Class.forName(xmlClass.getName());
        List<Method> methods = new ArrayList<>();
        Test classAnnotation = type.getAnnotation(Test.class);
        Assert.assertTrue(classAnnotation == null || classAnnotation.enabled(),
            "Disabled test class selected: " + type.getName());
        for (Method method : type.getMethods()) {
          Test annotation = method.getAnnotation(Test.class);
          if (annotation != null && annotation.enabled()) methods.add(method);
        }
        for (var include : xmlClass.getIncludedMethods()) {
          Assert.assertTrue(methods.stream().anyMatch(m -> Pattern.matches(include.getName(), m.getName())),
              "Include does not match an enabled @Test: " + type.getName() + "." + include.getName());
        }
        int before = selected.size();
        for (Method method : methods) {
          boolean included = xmlClass.getIncludedMethods().isEmpty()
              || xmlClass.getIncludedMethods().stream().anyMatch(i -> Pattern.matches(i.getName(), method.getName()));
          boolean excluded = xmlClass.getExcludedMethods().stream()
              .anyMatch(e -> Pattern.matches(e, method.getName()));
          if (included && !excluded) selected.add(type.getName() + "#" + method.getName());
        }
        Assert.assertTrue(selected.size() > before, "Class selected no enabled tests: " + type.getName());
      }
    }
    for (XmlSuite child : suite.getChildSuites()) collect(child, selected);
  }
}
