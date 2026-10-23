package Plugin_ERP.Utiel.Allur;

import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Link;
import io.qameta.allure.model.Parameter;
import org.openqa.selenium.WebDriver;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/** Access details belong to the browser session and the current test result. */
public final class ScenarioAccess {
    private ScenarioAccess() {}
    private record Credentials(String username, String password) {}
    private static final Map<WebDriver, Credentials> SESSIONS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static void login(WebDriver driver, String username, String password) {
        SESSIONS.put(driver, new Credentials(username, password));
        capture(driver, "صفحة تسجيل الدخول");
    }

    public static void capture(WebDriver driver, String label) {
        if (driver == null || Allure.getLifecycle().getCurrentTestCase().isEmpty()) return;
        try {
            String url = driver.getCurrentUrl();
            Credentials credentials = SESSIONS.get(driver);
            Allure.getLifecycle().updateTestCase(test -> {
                if (credentials != null) {
                    parameter(test.getParameters(), "اسم المستخدم", credentials.username());
                    parameter(test.getParameters(), "كلمة المرور", credentials.password());
                }
                parameter(test.getParameters(), "التننت", PropertyReader.getProperty("Environment").equalsIgnoreCase("Test")
                        ? "Test" : PropertyReader.getProperty("Tenant"));
                if (url != null && (url.startsWith("https://") || url.startsWith("http://"))) {
                    test.getLinks().removeIf(link -> label.equals(link.getName()));
                    test.getLinks().add(new Link().setName(label).setType("custom").setUrl(url));
                }
            });
        } catch (org.openqa.selenium.WebDriverException ignored) {
            // A closed browser must not replace the original test outcome.
        }
    }

    private static void parameter(java.util.List<Parameter> parameters, String name, String value) {
        parameters.removeIf(parameter -> name.equals(parameter.getName()));
        parameters.add(new Parameter().setName(name).setValue(value)
                .setMode(Parameter.Mode.DEFAULT).setExcluded(true));
    }
}
