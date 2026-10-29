/* شرح الملف: يقرأ الرابط والفرع معًا من نفس لحظة المتصفح، لتقليل الأوامر مع إبقاء التحقق حيًا قبل كل عملية. */
package TestSupport;

import java.util.Map;
import org.openqa.selenium.WebDriver;

public record FixtureContextSnapshot(String origin, Object branchId) {
    public static FixtureContextSnapshot read(WebDriver driver) {
        Object result =
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver,
                        "/* fixture-context-snapshot */ return {origin:location.origin,branchId:erpVue.root()?.$store?.getters['app/currentBranch']?.id};");
        if (!(result instanceof Map<?, ?> row) || !(row.get("origin") instanceof String origin))
            throw new AssertionError("Cannot read fixture origin and branch together");
        return new FixtureContextSnapshot(origin, row.get("branchId"));
    }
}
