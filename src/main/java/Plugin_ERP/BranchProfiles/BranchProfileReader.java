/* شرح الملف: قراءة إعدادات الفرع كما هي محفوظة في البرنامج.
 */
package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.VueComponentAccess;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class BranchProfileReader {
    private BranchProfileReader() {}

    public record RuntimeState(
            BranchSnapshot profile, boolean additionalEnabled, Object additionalRate) {}

    public static BranchSnapshot capture(WebDriver driver, Duration timeout) {
        return captureState(driver, timeout).profile();
    }

    public static RuntimeState captureState(WebDriver driver, Duration timeout) {
        Object result =
                new WebDriverWait(driver, timeout)
                        .withMessage("Authenticated branch profile is not ready")
                        .until(
                                d ->
                                        VueComponentAccess.execute(
                                                d,
                                                """
                    const store=erpVue.root()?.$store;
                    const branch=store?.getters['app/currentBranch'];
                    if(!store?.state?.auth?.token||!branch?.id)return null;
                    const required=['activityType','discountLevel','taxPolicy','stocktakingMethod'];
                    if(required.some(key=>branch[key]===null||branch[key]===undefined||branch[key]===''))return null;
                    return {origin:location.origin,branchId:branch.id,activityType:branch.activityType,
                      discountLevel:branch.discountLevel,taxPolicy:branch.taxPolicy,
                      stocktakingMethod:branch.stocktakingMethod,additionalEnabled:!!branch.enableAdditionalTax,
                      additionalRate:branch.additionalTaxPercentage};
                    """));
        if (!(result instanceof Map<?, ?> values))
            throw new IllegalStateException("Unexpected branch profile response");
        var fields = new java.util.HashMap<String, Object>();
        values.forEach((key, value) -> fields.put(key.toString(), value));
        return new RuntimeState(
                BranchSnapshot.from(fields),
                Boolean.TRUE.equals(fields.get("additionalEnabled")),
                fields.get("additionalRate"));
    }
}
