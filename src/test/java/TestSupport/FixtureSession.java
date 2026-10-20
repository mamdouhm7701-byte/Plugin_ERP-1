package TestSupport;

import java.util.Map;
import java.util.Objects;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/** Recovery identities are valid only in the origin and branch that created them. */
public final class FixtureSession {
    private final WebDriver driver;
    public final String origin;
    public final Object branchId;

    public FixtureSession(WebDriver driver) {
        this.driver = driver;
        Object location = script("return location.origin");
        origin = location instanceof String text ? text : "offline";
        branchId =
                new org.openqa.selenium.support.ui.WebDriverWait(
                                driver, java.time.Duration.ofSeconds(30))
                        .until(
                                d ->
                                        script(
                                                "return document.querySelector('#app')?.__vue__?.$store?.getters['app/currentBranch']?.id"));
        if (branchId == null) throw new AssertionError("Fixture branch is unavailable");
    }

    private Object script(String code) {
        return ((JavascriptExecutor) driver).executeScript(code);
    }

    public void verify() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30))
                .until(
                        d -> {
                            Object location = script("return location.origin");
                            String actual = location instanceof String text ? text : "offline";
                            Object branch =
                                    script(
                                            "return document.querySelector('#app')?.__vue__?.$store?.getters['app/currentBranch']?.id");
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
