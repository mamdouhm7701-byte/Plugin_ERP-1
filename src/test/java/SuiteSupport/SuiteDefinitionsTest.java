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
        {"smoke"}, {"sanity"}, {"regression"}, {"e2e"}, {"login"},
        {"additional/standalone/browser-isolation"},
        {"additional/standalone/point-of-sale"}, {"additional/standalone/restaurant"}, {"special-sales"},
        {"additional/standalone/fixture-lifecycle"}, {"additional/internal/regression-additional"}, {"additional/internal/regression-workflows"}
    };
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
    Assert.assertEquals(smoke.size(), 9, "Core ERP plus cash POS and paid restaurant smoke checks");
    Assert.assertEquals(sanity.size(), 24, "Three focused checks in each of eight module scopes");
    Assert.assertEquals(e2e.size(), 36, "29 ERP journeys plus 3 POS and 4 restaurant journeys");
    Assert.assertTrue(regression.size() > sanity.size());
    Assert.assertTrue(regression.containsAll(smoke), "Regression omitted a smoke contract");
    Assert.assertTrue(regression.containsAll(sanity), "Regression omitted a sanity contract");
    Assert.assertTrue(regression.containsAll(e2e), "Regression omitted an E2E workflow");
    Assert.assertFalse(regression.stream().anyMatch(name -> name.startsWith("Invoice.InvoiceTest#")
        || name.startsWith("Invoice.SalesInvoiceTestCases#")
        || name.startsWith("Purches.PurchesBillsTest#")), "Do not duplicate compatibility aliases");
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

  private static List<String> selectedCases(String level) throws Exception {
    List<String> selected = new ArrayList<>();
    for (XmlSuite suite : new Parser(RESOURCES.resolve(level + "-testng.xml").toString()).parseToList()) {
      collect(suite, selected);
    }
    return selected;
  }

  private static void collect(XmlSuite suite, List<String> selected) throws Exception {
    Assert.assertEquals(suite.getParallel(), XmlSuite.ParallelMode.NONE,
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
