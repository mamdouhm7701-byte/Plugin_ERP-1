package Plugin_ERP.Utiel.Allur;

import io.qameta.allure.Allure;
import io.qameta.allure.model.TestResult;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Listeners(io.qameta.allure.testng.AllureTestNg.class)
public class ScenarioAccessTest {
    @Test public void recordsActualTenantRouteAndUnmaskedSessionCredentials() {
        String parent = Allure.getLifecycle().getCurrentTestCase().orElseThrow();
        String id = UUID.randomUUID().toString();
        String originalTenant = System.getProperty("Tenant");
        String originalEnvironment = System.getProperty("Environment");
        String[] url = {"https://tenant-a.example/#/login"};
        WebDriver driver = (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebDriver.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getCurrentUrl" -> url[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        Allure.getLifecycle().scheduleTestCase(new TestResult().setUuid(id).setName("Access metadata regression"));
        Allure.getLifecycle().startTestCase(id);
        try {
            System.setProperty("Environment", "Live");
            System.setProperty("Tenant", "tenant-a");
            ScenarioAccess.login(driver, "developer-a", "plain-test-password-a");
            url[0] = "https://tenant-b.example/#/sales/invoice/42";
            System.setProperty("Tenant", "tenant-b");
            ScenarioAccess.login(driver, "developer-b", "plain-test-password-b");
            ScenarioAccess.capture(driver, "الشاشة وقت الخطأ");
            ScenarioAccess.capture(driver, "الشاشة وقت الخطأ");
            Allure.getLifecycle().updateTestCase(id, test -> {
                Assert.assertTrue(test.getLinks().stream().anyMatch(link ->
                        "الشاشة وقت الخطأ".equals(link.getName()) && url[0].equals(link.getUrl())));
                Assert.assertEquals(test.getLinks().stream().filter(link ->
                        "الشاشة وقت الخطأ".equals(link.getName())).count(), 1L);
                for (var parameter : test.getParameters()) {
                    String expected = switch (parameter.getName()) {
                        case "التننت" -> "tenant-b";
                        case "اسم المستخدم" -> "developer-b";
                        case "كلمة المرور" -> "plain-test-password-b";
                        default -> throw new AssertionError(parameter.getName());
                    };
                    Assert.assertEquals(parameter.getValue(), expected);
                    Assert.assertEquals(parameter.getMode(), io.qameta.allure.model.Parameter.Mode.DEFAULT);
                }
                Assert.assertEquals(test.getParameters().size(), 3);
            });
        } finally {
            if (originalTenant == null) System.clearProperty("Tenant"); else System.setProperty("Tenant", originalTenant);
            if (originalEnvironment == null) System.clearProperty("Environment"); else System.setProperty("Environment", originalEnvironment);
            Allure.getLifecycle().stopTestCase(id);
            Allure.getLifecycle().writeTestCase(id);
            Allure.getLifecycle().setCurrentTestCase(parent);
        }
    }
}
