package Plugin_ERP.Util.Allur;

import java.nio.file.Path;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class AllureReportPolicyTest {
    @DataProvider
    public Object[][] policies() {
        return new Object[][] {
            {null, null, null, true, true},
            {"true", null, null, false, false},
            {"TRUE", null, null, false, false},
            {null, "false", "true", false, false},
            {null, "true", "false", true, false},
            {"true", "true", null, true, false},
            {"true", "true", "true", true, true},
            {"true", "false", "false", false, false}
        };
    }

    @Test(dataProvider = "policies")
    public void ciDefaultsKeepRawResultsAndExplicitFlagsControlHtml(
            String ci,
            String generate,
            String open,
            boolean expectedGenerate,
            boolean expectedOpen) {
        Properties properties = new Properties();
        if (generate != null) properties.setProperty("allure.report.generate", generate);
        if (open != null) properties.setProperty("allure.report.open", open);
        AllureReportPolicy policy = AllureReportPolicy.from(properties, ci);
        Assert.assertEquals(policy.generate(), expectedGenerate);
        Assert.assertEquals(policy.open(), expectedOpen);
    }

    @DataProvider
    public Object[][] unsafePaths() {
        return new Object[][] {
            {"../outside"},
            {"."},
            {"target"},
            {"src/report"},
            {"docs/report"},
            {"config/report"},
            {"outputs/report"},
            {".git/report"}
        };
    }

    @Test(dataProvider = "unsafePaths")
    public void refusesCleaningTheProjectSourcesOrAnOutsideDirectory(String path) {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> AllureConstants.outputPath(Path.of("target", "path-check-project"), path));
    }

    @Test
    public void dedicatedRunPathsAreResolvedWithinTheProject() {
        Path project = Path.of("target", "path-check-project").toAbsolutePath().normalize();
        Path output = project.resolve("target/ci/run-one/allure-results");
        Assert.assertEquals(
                AllureConstants.outputPath(project, "target/ci/run-one/allure-results"), output);
        Assert.assertEquals(AllureConstants.outputPath(project, output.toString()), output);
    }
}
