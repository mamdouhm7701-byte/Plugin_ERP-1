/* شرح الملف: قراءة بيانات الشاشة التي يحتاجها الاختبار من مكان موحد.
 */
package Plugin_ERP.Util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public final class VueComponentAccess {
    private VueComponentAccess() {}

    private static final String COMPONENT_API =
            """
        const erpVue = {
          root: () => document.querySelector('#app')?.__vue__,
          children: component => component?.$children || []
        };
        """;

    static String prepare(String script) {
        return script.contains("erpVue.") ? COMPONENT_API + script : script;
    }

    public static Object execute(WebDriver driver, String script, Object... arguments) {
        return ((JavascriptExecutor) driver).executeScript(prepare(script), arguments);
    }

    public static Object executeAsync(WebDriver driver, String script, Object... arguments) {
        return ((JavascriptExecutor) driver).executeAsyncScript(prepare(script), arguments);
    }
}
