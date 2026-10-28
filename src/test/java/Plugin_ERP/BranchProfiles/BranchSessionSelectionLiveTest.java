/* شرح الملف: تجربة اختيار الفرع المطلوب داخل البرنامج الحقيقي بعد الدخول.
 */
package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

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
