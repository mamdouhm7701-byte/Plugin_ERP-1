/* شرح الملف: يمنع اختبارات الإطار المحلية من الدخول في رن ERP، مع بقائها ضمن CI للتحقق من الكود. */
package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;
import org.testng.xml.internal.Parser;

public class LiveModuleIsolationTest {
    @Test
    public void localCiContractsDoNotRunInsideLiveModules() throws Exception {
        var root = Path.of("src/test/resources");
        var ci = classes(root.resolve("ci-testng.xml"));
        var collisions = new ArrayList<String>();
        for (String module :
                List.of(
                        "sales",
                        "purchases",
                        "store",
                        "products",
                        "financial-movements",
                        "special-sales",
                        "regression",
                        "smoke",
                        "sanity",
                        "e2e"))
            for (String name : classes(root.resolve(module + "-testng.xml")))
                if (ci.contains(name)) collisions.add(module + ":" + name);
        Assert.assertTrue(
                collisions.isEmpty(),
                "Local CI contracts must remain in CI, outside branch-bound ERP runs: "
                        + collisions);
    }

    private Set<String> classes(Path file) throws Exception {
        var result = new HashSet<String>();
        for (var suite : new Parser(file.toString()).parseToList()) collect(suite, result);
        return result;
    }

    private void collect(XmlSuite suite, Set<String> result) {
        for (var test : suite.getTests())
            for (var c : test.getXmlClasses()) result.add(c.getName());
        for (var child : suite.getChildSuites()) collect(child, result);
    }
}
