package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Opt-in live setup contract; verifies branch selection before any fixture is created. */
public final class BranchSessionSelectionLiveTest extends BaseTest {
    @Test
    public void selectedProfileMatchesAfterLoginAndRefresh() {
        requirePreparedSession();
        var expected = BranchProfiles.selected().orElseThrow();
        expected.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        driver.navigate().refresh();
        expected.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        Assert.assertNotNull(driver.getWindowHandle());
    }
}
