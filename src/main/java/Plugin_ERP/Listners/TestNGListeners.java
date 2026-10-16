package Plugin_ERP.Listners;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Utiel.Allur.AllureBinaryManger;
import Plugin_ERP.Utiel.Allur.AllureEnvironment;
import Plugin_ERP.Utiel.PropertyReader;
import Plugin_ERP.Utiel.ScreenShotsUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestNGListeners implements IExecutionListener, ITestListener {

    private static final ThreadLocal<Long> testStartTime = new ThreadLocal<>();

    // ================= Execution =================

    @Override
    public void onExecutionStart() {
        System.out.println(">>> Execution Started");

        // 1) تحميل properties
        PropertyReader.loadProperties();
        System.setProperty("allure.run.id", "local-" + Instant.now().toEpochMilli());

        // Preserve trend history while clearing only raw files from the previous run.
        AllureBinaryManger.prepareResultsForRun();

        // Add run metadata before tests start.
        AllureEnvironment.writeBaseEnvironmentOnce();
        writeExecutorMetadata();
    }

    private void writeExecutorMetadata() {
        Path executorPath = Path.of(System.getProperty("user.dir"), "allure-results", "executor.json");
        try {
            Files.createDirectories(executorPath.getParent());
            long buildOrder = nextBuildOrder();
            System.setProperty("allure.build.order", Long.toString(buildOrder));
            String json = "{\n" +
                    "  \"name\": \"Local IntelliJ / Maven\",\n" +
                    "  \"type\": \"local\",\n" +
                    "  \"buildName\": \"Plugin_ERP " + Instant.now().toString() + "\",\n" +
                    "  \"buildOrder\": " + buildOrder + ",\n" +
                    "  \"buildUrl\": \"\",\n" +
                    "  \"reportUrl\": \"\"\n" +
                    "}\n";
            Files.writeString(executorPath, json);
        } catch (Exception e) {
            LogUtils.error("Failed to write Allure executor metadata: " + e.getMessage());
        }
    }

    private long nextBuildOrder() {
        Path historyTrend = Path.of(System.getProperty("user.dir"), "allure-results", "history", "history-trend.json");
        long previous = 0;
        try {
            if (Files.isRegularFile(historyTrend)) {
                Matcher matcher = Pattern.compile("\\\"buildOrder\\\"\\s*:\\s*(\\d+)")
                        .matcher(Files.readString(historyTrend));
                while (matcher.find()) previous = Math.max(previous, Long.parseLong(matcher.group(1)));
            }
        } catch (Exception e) {
            LogUtils.error("Could not read previous Allure build order: " + e.getMessage());
        }
        return previous + 1;
    }

    @Override
    public void onExecutionFinish() {
        System.out.println(">>> Execution Finished");

        try {
            WebDriverFactory.quitDriver();
        } catch (Exception ignored) {}

        // مهم: ما ترميش RuntimeException هنا
        if (hasAllureResults()) {
            try {
                AllureBinaryManger.generateAndOpenDetached();
            } catch (Exception e) {
                LogUtils.error("Allure report generation failed but tests finished: " + e.getMessage());
            }
        } else {
            LogUtils.info("No Allure test results were produced; report generation skipped.");
        }
    }

    private boolean hasAllureResults() {
        Path resultsDir = Path.of(System.getProperty("user.dir"), "allure-results");
        if (!Files.isDirectory(resultsDir)) return false;

        try (Stream<Path> files = Files.list(resultsDir)) {
            return files.anyMatch(path -> {
                String name = path.getFileName().toString();
                return name.endsWith("-result.json") || name.endsWith("-container.json");
            });
        } catch (Exception e) {
            LogUtils.error("Could not inspect Allure results: " + e.getMessage());
            return false;
        }
    }


    // ================= Tests =================

    @Override
    public void onTestStart(ITestResult result) {
        testStartTime.set(System.currentTimeMillis());

        // Update browser values without dropping the other run metadata.
        try {
            WebDriver driver = WebDriverFactory.getDriver();
            if (driver instanceof RemoteWebDriver rwd) {
                Capabilities caps = rwd.getCapabilities();
                AllureEnvironment.updateBrowser(
                        String.valueOf(caps.getBrowserName()),
                        String.valueOf(caps.getBrowserVersion()));
            }
        } catch (Exception e) {
            LogUtils.error("Failed to update Allure environment with browser capabilities: " + e.getMessage());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        takeScreenshot(result, "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        takeScreenshot(result, "FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        takeScreenshot(result, "SKIPPED");
    }

    // ================= Utils =================

    private void takeScreenshot(ITestResult result, String status) {
        WebDriver driver = WebDriverFactory.getDriver();
        if (driver == null) return;

        ScreenShotsUtils.takeScreenShot(driver, status + "_" + result.getName());
    }
}
