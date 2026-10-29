/* شرح الملف: التأكد أن التنظيف والتجربة على نفس الفرع والبيئة.
 */
package TestSupport;

import java.util.Map;
import java.util.Objects;
import org.openqa.selenium.WebDriver;

public final class FixtureSession {
    private final WebDriver driver;
    public final String origin;
    public final Object branchId;

    public FixtureSession(WebDriver driver) {
        this.driver = driver;
        var snapshot =
                new org.openqa.selenium.support.ui.WebDriverWait(
                                driver, java.time.Duration.ofSeconds(30))
                        .until(
                                d -> {
                                    var current = FixtureContextSnapshot.read(d);
                                    return current.branchId() == null ? null : current;
                                });
        origin = snapshot.origin();
        branchId = snapshot.branchId();
        if (branchId == null) throw new AssertionError("Fixture branch is unavailable");
    }

    public void verify() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30))
                .until(
                        d -> {
                            var snapshot = FixtureContextSnapshot.read(d);
                            String actual = snapshot.origin();
                            Object branch = snapshot.branchId();
                            if (!origin.equals(actual)
                                    || (branch != null && !Objects.equals(branchId, branch)))
                                throw new AssertionError(
                                        "ERP origin or branch changed; refusing fixture access");
                            return Objects.equals(branchId, branch);
                        });
    }

    public void verify(Map<String, Object> row) {
        verify();
        if (!origin.equals(row.get("origin")) || !Objects.equals(branchId, row.get("branchId")))
            throw new AssertionError("Fixture journal belongs to another origin or branch");
    }
}
