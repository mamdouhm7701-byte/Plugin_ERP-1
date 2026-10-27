package Plugin_ERP.BranchProfiles;

import java.time.Duration;
import java.util.Optional;
import org.openqa.selenium.WebDriver;

/** Rechecks core session policies before fixture transport; legacy transport remains unchanged. */
public final class BranchProfileGuard {
    private final Optional<BranchProfile> expected;
    private final AdditionalTaxExpectation additional = AdditionalTaxExpectation.capture();

    private BranchProfileGuard(Optional<BranchProfile> expected) {
        this.expected = expected;
    }

    public static BranchProfileGuard capture() {
        return new BranchProfileGuard(BranchProfileScope.currentOrSelected());
    }

    public void verify(WebDriver driver) {
        if (expected.isPresent())
            expected.orElseThrow()
                    .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        if (expected.isPresent()) additional.verify(driver);
    }
}
