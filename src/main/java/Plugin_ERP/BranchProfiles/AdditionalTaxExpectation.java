package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.VueComponentAccess;
import java.math.BigDecimal;
import java.util.Map;
import org.openqa.selenium.WebDriver;

/** Optional business-run guard; an absent property preserves every legacy call path. */
public final class AdditionalTaxExpectation {
    private final Boolean enabled;

    private AdditionalTaxExpectation(Boolean enabled) {
        this.enabled = enabled;
    }

    public static AdditionalTaxExpectation capture() {
        String value = System.getProperty("business.additionalTax");
        if (value != null && !value.equals("true") && !value.equals("false"))
            throw new IllegalArgumentException("business.additionalTax must be true or false");
        return new AdditionalTaxExpectation(value == null ? null : Boolean.valueOf(value));
    }

    public void verify(WebDriver driver) {
        if (enabled == null) return;
        var actual =
                (Map<?, ?>)
                        VueComponentAccess.execute(
                                driver,
                                "const b=erpVue.root()?.$store?.getters['app/currentBranch'];return {enabled:!!b?.enableAdditionalTax,rate:b?.additionalTaxPercentage}");
        if (!enabled.equals(actual.get("enabled"))
                || (enabled
                        && new BigDecimal(String.valueOf(actual.get("rate")))
                                        .compareTo(new BigDecimal("100"))
                                != 0))
            throw new IllegalStateException(
                    "Restaurant additional-tax settings changed during the business run");
    }
}
