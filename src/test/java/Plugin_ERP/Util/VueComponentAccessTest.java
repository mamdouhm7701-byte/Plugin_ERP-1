package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class VueComponentAccessTest {
    @Test
    public void componentNamespaceIsNeverPassedToAnUnpreparedExecutor() throws Exception {
        var invocation =
                java.util.regex.Pattern.compile(
                        "execute(?:Async)?Script\\(\\s*(?:\"[^\"\\r\\n]*erpVue\\.|\"\"\"(?:(?!\"\"\").)*?erpVue\\.)",
                        java.util.regex.Pattern.DOTALL);
        var violations = new java.util.ArrayList<String>();
        for (String root : java.util.List.of("src/main/java", "src/test/java")) {
            try (var files = java.nio.file.Files.walk(java.nio.file.Path.of(root))) {
                for (var file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                    if (invocation.matcher(java.nio.file.Files.readString(file)).find())
                        violations.add(file.toString());
                }
            }
        }
        Assert.assertTrue(
                violations.isEmpty(),
                "Use VueComponentAccess so erpVue is defined before execution: " + violations);
    }

    @Test
    public void componentReadsHaveOneRootBoundaryAndPreserveArguments() {
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    String script = (String) args[0];
                                    Assert.assertTrue(script.contains("const erpVue"));
                                    Assert.assertTrue(script.endsWith("return erpVue.root();"));
                                    Assert.assertEquals(
                                            (Object[]) args[1], new Object[] {"owned-id", 17});
                                    return "result";
                                });
        Assert.assertEquals(
                VueComponentAccess.execute(driver, "return erpVue.root();", "owned-id", 17),
                "result");
        Assert.assertEquals(
                VueComponentAccess.executeAsync(driver, "return erpVue.root();", "owned-id", 17),
                "result");
    }

    @Test
    public void unrelatedScriptsStayUnchanged() {
        Assert.assertEquals(
                VueComponentAccess.prepare("return location.origin"), "return location.origin");
    }
}
