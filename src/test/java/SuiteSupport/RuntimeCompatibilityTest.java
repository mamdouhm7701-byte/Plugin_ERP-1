package SuiteSupport;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** Prevent successful test runs with broken AspectJ instrumentation. No browser needed. */
@Listeners(AllureTestNg.class)
public final class RuntimeCompatibilityTest {
    @Test
    public void java21RunsWithWorkingAllureStepInstrumentation() {
        Assert.assertEquals(Runtime.version().feature(), 21,
                "Tests must use the project's Java 21 toolchain");
        String parent = Allure.getLifecycle().getCurrentTestCaseOrStep().orElseThrow();
        String step = currentAnnotatedStep();
        Assert.assertNotEquals(step, parent,
                "@Step must create an Allure step; verify the AspectJ javaagent and JDK compatibility");
        Assert.assertEquals(Allure.getLifecycle().getCurrentTestCaseOrStep().orElseThrow(), parent,
                "The annotated step must finish and restore its parent");
    }

    @Step("Verify AspectJ records an annotated Allure step")
    private String currentAnnotatedStep() {
        return Allure.getLifecycle().getCurrentTestCaseOrStep().orElseThrow();
    }
}
