/* شرح الملف: مراجعة تفعيل الضريبة الإضافية ونسبتها قبل اختبار المطعم.
 */
package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.VueComponentAccess;
import java.math.BigDecimal;
import java.util.Map;
import org.openqa.selenium.WebDriver;

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
        verify(Boolean.TRUE.equals(actual.get("enabled")), actual.get("rate"));
    }

    public void verify(boolean actualEnabled, Object actualRate) {
        if (enabled == null) return;
        if (!enabled.equals(actualEnabled)
                || (enabled
                        && new BigDecimal(String.valueOf(actualRate))
                                        .compareTo(new BigDecimal("100"))
                                != 0))
            throw new IllegalStateException(
                    "Restaurant additional-tax settings changed during the business run");
    }
}
