/* شرح الملف: اختبارات قراءة طريقة حساب ضريبة البيع المستخدمة في التجربة.
 */
package SuiteSupport;

import TestSupport.SalesTaxProfile;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SalesTaxProfileTest {
    @DataProvider
    public Object[][] examples() {
        return new Object[][] {
            {"NetBased", List.of(new BigDecimal("15")), "300", "345"},
            {"NetBased", List.of(new BigDecimal("15")), "125", "143.75"},
            {"NetBased", List.of(new BigDecimal("15")), "270", "310.50"},
            {"NetBased", List.of(new BigDecimal("15")), "-100", "-115"},
            {"NetBased", List.of(new BigDecimal("10"), new BigDecimal("5")), "100", "115"},
            {"PriceIncluded", List.of(new BigDecimal("15")), "300", "300"},
            {"ItemBased", List.of(new BigDecimal("15")), "300", "300"}
        };
    }

    @Test(dataProvider = "examples")
    public void expectedTaxUsesPolicyAndConfiguredRates(
            String policy, List<BigDecimal> rates, String base, String expected) {
        Assert.assertEquals(
                new BigDecimal(new SalesTaxProfile(policy, rates).net(base))
                        .compareTo(new BigDecimal(expected)),
                0);
    }
}
