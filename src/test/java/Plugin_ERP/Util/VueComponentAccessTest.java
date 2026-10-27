package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class VueComponentAccessTest {
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
