/* شرح الملف: انتظار تحميل الفرع الأصلي بعد التنقل، ورفض الوصول لبيانات التجربة إذا تغير الفرع أو البيئة.
 * فائدته: يحافظ على عزل التننت والفرع قبل قراءة بيانات التجهيز أو تعديلها.
 */
package TestSupport;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class FixtureContextGuard {
    private final WebDriver driver;
    private final String origin;
    private final Object sessionBranchId;

    public FixtureContextGuard(WebDriver driver, String origin, Object sessionBranchId) {
        this.driver = driver;
        this.origin = origin;
        this.sessionBranchId = sessionBranchId;
    }

    public void verify() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d -> {
                            var snapshot = FixtureContextSnapshot.read(d);
                            Object activeOrigin = snapshot.origin(),
                                    activeBranch = snapshot.branchId();
                            if (!origin.equals(activeOrigin)
                                    || (activeBranch != null
                                            && !java.util.Objects.equals(
                                                    sessionBranchId, activeBranch)))
                                throw new AssertionError(
                                        "ERP origin or branch changed; refusing fixture access");
                            return java.util.Objects.equals(sessionBranchId, activeBranch);
                        });
    }
}
