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

import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestNGListeners implements IExecutionListener, ITestListener {

    private static final ThreadLocal<Long> testStartTime = new ThreadLocal<>();
    private static final AtomicBoolean envUpdatedWithRealBrowser = new AtomicBoolean(false);

    // ================= Execution =================

    @Override
    public void onExecutionStart() {
        System.out.println(">>> Execution Started");

        // 1) تحميل properties
        PropertyReader.loadProperties();

        // 2) history: من report القديم -> results (قبل أي delete)
        AllureBinaryManger.copyHistoryFromReportToResults();

        // 3) clean results فقط (سيب التقرير القديم كـ fallback)
        AllureBinaryManger.cleanAllure(true, false);

        // 4) اكتب environment “base” (قبل ما Capabilities تبقى جاهزة)
        AllureEnvironment.writeBaseEnvironmentOnce();
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

        // ✅ أول مرة بس: هات browserName/version الحقيقي من Capabilities واكتبهم في environment.properties
        if (envUpdatedWithRealBrowser.compareAndSet(false, true)) {
            try {
                WebDriver driver = WebDriverFactory.getDriver();
                if (driver instanceof RemoteWebDriver rwd) {
                    Capabilities caps = rwd.getCapabilities();
                    String bName = String.valueOf(caps.getBrowserName());
                    String bVer  = String.valueOf(caps.getBrowserVersion());
                    AllureEnvironment.writeEnvironment(bName, bVer);
                }
            } catch (Exception e) {
                LogUtils.error("Failed to update Allure env with real browser info: " + e.getMessage());
            }
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
