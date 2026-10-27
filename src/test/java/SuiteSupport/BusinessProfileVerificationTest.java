package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Util.VueComponentAccess;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BusinessProfileVerificationTest extends BaseTest {
    @Test
    public void selectedBranchRetainsRequestedPolicies() {
        var profile = BranchProfiles.selected().orElseThrow();
        profile.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        var configuration =
                (java.util.Map<?, ?>)
                        VueComponentAccess.execute(
                                driver,
                                "const b=erpVue.root().$store.getters['app/currentBranch'];return {enabled:!!b.enableAdditionalTax,rate:b.additionalTaxPercentage}");
        Assert.assertEquals(
                configuration.get("enabled"),
                Boolean.valueOf(System.getProperty("business.additionalTax")));
        if (Boolean.TRUE.equals(configuration.get("enabled")))
            Assert.assertEquals(
                    new java.math.BigDecimal(configuration.get("rate").toString())
                            .compareTo(new java.math.BigDecimal("100")),
                    0);
    }
}
