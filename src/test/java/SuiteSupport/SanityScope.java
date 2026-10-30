/* شرح الملف: يختار الجزء الذي تغير فقط لفحص Sanity، بدل تشغيل كل أقسام البرنامج دون قصد. */
package SuiteSupport;

import java.util.*;
import javax.swing.JOptionPane;
import org.testng.xml.XmlSuite;

final class SanityScope {
    private SanityScope() {}

    private static final Map<String, String> OPTIONS = new LinkedHashMap<>();

    static {
        OPTIONS.put("المبيعات", "Sales");
        OPTIONS.put("المشتريات", "Purchases");
        OPTIONS.put("الأصناف وقوائم الأسعار", "Products");
        OPTIONS.put("المتعاملين", "Dealers");
        OPTIONS.put("المخازن", "Stores");
        OPTIONS.put("الحركات المالية", "Financial");
        OPTIONS.put("المطعم ونقاط البيع", "SpecialSales");
        OPTIONS.put("كل الأقسام — أوسع وأبطأ", "All");
    }

    static String choose() {
        Object selected =
                JOptionPane.showInputDialog(
                        null,
                        "إيه الجزء اللي اتغير وعايز تراجعه؟",
                        "نطاق Sanity",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        OPTIONS.keySet().toArray(),
                        OPTIONS.keySet().iterator().next());
        return selected == null ? null : OPTIONS.get(selected.toString());
    }

    static void filter(List<XmlSuite> suites, String scope) {
        if (scope == null || scope.isBlank() || scope.equals("All")) return;
        if (!OPTIONS.containsValue(scope))
            throw new IllegalArgumentException("Unknown sanity.scope: " + scope);
        for (var suite : suites) {
            for (var test : suite.getTests())
                test.setXmlClasses(
                        new ArrayList<>(
                                test.getXmlClasses().stream()
                                        .filter(type -> accepts(scope, type.getName()))
                                        .toList()));
            filter(suite.getChildSuites(), scope);
        }
    }

    static boolean accepts(String scope, String name) {
        if (scope == null || scope.isBlank() || scope.equals("All")) return true;
        if (name.equals("SuiteSupport.BusinessProfileVerificationTest")) return true;
        return switch (scope) {
            case "Sales" -> name.startsWith("Invoice.");
            case "Purchases" -> name.startsWith("Purches.");
            case "Products" -> name.startsWith("ProductsTest.");
            case "Dealers" -> name.startsWith("DealerModelTest.");
            case "Stores" -> name.startsWith("StoreTest.");
            case "Financial" -> name.startsWith("FinancialMovementsTest.");
            case "SpecialSales" ->
                    name.startsWith("SpecialSalesTest.")
                            || name.equals("CoverageTest.TablesCoverageTest");
            default -> false;
        };
    }
}
