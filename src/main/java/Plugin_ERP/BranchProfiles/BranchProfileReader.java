package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.VueComponentAccess;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Reads configuration only. It never dispatches an API write or selects another branch. */
public final class BranchProfileReader {
    private BranchProfileReader() {}

    public static BranchSnapshot capture(WebDriver driver, Duration timeout) {
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
                      stocktakingMethod:branch.stocktakingMethod};
                    """));
        if (!(result instanceof Map<?, ?> values))
            throw new IllegalStateException("Unexpected branch profile response");
        var fields = new java.util.HashMap<String, Object>();
        values.forEach((key, value) -> fields.put(key.toString(), value));
        return BranchSnapshot.from(fields);
    }
}
