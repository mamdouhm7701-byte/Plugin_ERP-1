/* شرح الملف: متابعة بداية الحالات ونتائجها وجمع الصور والتقارير عند الانتهاء.
 */
package Plugin_ERP.Listeners;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Util.Allur.AllureBinaryManger;
import Plugin_ERP.Util.Allur.AllureConstants;
import Plugin_ERP.Util.Allur.AllureEnvironment;
import Plugin_ERP.Util.Allur.AllureReportPolicy;
import Plugin_ERP.Util.PropertyReader;
import Plugin_ERP.Util.ScreenShotsUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.openqa.selenium.WebDriver;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListeners
        implements IExecutionListener,
                ITestListener,
                org.testng.IReporter,
                org.testng.IInvokedMethodListener {

    @Override
    public void beforeInvocation(org.testng.IInvokedMethod method, ITestResult result) {
        if (result == null
                || !(result.getInstance()
                        instanceof Plugin_ERP.Util.RunEnvironmentGate.ProtectedScenario)) return;
        var scenario = method.getTestMethod();
        if (method.isTestMethod()
                || scenario.isBeforeClassConfiguration()
                || scenario.isBeforeMethodConfiguration())
            Plugin_ERP.Util.RunEnvironmentGate.current().beforeScenario();
    }

    @Override
    public void generateReport(
            java.util.List<org.testng.xml.XmlSuite> xmlSuites,
            java.util.List<org.testng.ISuite> suites,
            String outputDirectory) {
        if (suites.stream().allMatch(suite -> suite.getAllMethods().isEmpty())) {
            System.out.println(
                    "No test cases were selected. IntelliJ may have rerun configuration "
                            + "methods only (setup/fixtures/cleanup). Use Alt+Shift+F10 > ERP - Business run "
                            + "or run an actual @Test method; no business checks ran in this selection.");
            return;
        }
        if (!Plugin_ERP.Util.Allur.ReportMail.enabled() || !hasAllureResults()) return;
        try {
            Plugin_ERP.Util.Allur.ReportMail.validateConfiguration();
            // Regenerate from this run even when CI disables automatic HTML/opening.
            AllureBinaryManger.generateReportOnly();
            Path mailReport = Files.createTempDirectory("plugin-erp-testng-");
            try {
                new org.testng.reporters.EmailableReporter2()
                        .generateReport(xmlSuites, suites, mailReport.toString());
                Plugin_ERP.Util.Allur.ReportMail.send(mailReport);
                LogUtils.info(
                        "sent".equals(System.getProperty("report.mail.last.status"))
                                ? "SMTP accepted summary and Allure/TestNG delivery."
                                : "SMTP accepted summary; large report delivery incomplete. See target/report-mail/deliveries.");
            } finally {
                try (var paths = Files.walk(mailReport)) {
                    for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList())
                        Files.deleteIfExists(path);
                }
            }
        } catch (Exception failure) {
            // Keep test outcomes intact and do not leak SMTP authentication diagnostics.
            System.setProperty("report.mail.last.status", "failed");
            LogUtils.error(
                    "Report email failed ("
                            + failure.getClass().getSimpleName()
                            + "). Check mail configuration, connectivity and attachment size; reports remain local.");
        }
    }

    private static final ThreadLocal<Long> testStartTime = new ThreadLocal<>();

    // ================= Execution =================

    @Override
    public void onExecutionStart() {
        Plugin_ERP.Util.Allur.AllureContextNoiseFilter.install();
        System.out.println(">>> Execution Started");

        // 1) تحميل properties
        PropertyReader.loadProperties();
        if (System.getProperty("allure.run.id", "").isBlank())
            System.setProperty("allure.run.id", "run-" + java.util.UUID.randomUUID());

        // Preserve trend history while clearing only raw files from the previous run.
        AllureBinaryManger.prepareResultsForRun();

        // Add run metadata before tests start.
        AllureEnvironment.writeBaseEnvironmentOnce();
        writeExecutorMetadata();
    }

    private void writeExecutorMetadata() {
        Path executorPath = AllureConstants.ALLURE_RESULTS_DIR.resolve("executor.json");
        try {
            Files.createDirectories(executorPath.getParent());
            long buildOrder = nextBuildOrder();
            System.setProperty("allure.build.order", Long.toString(buildOrder));
            String json =
                    "{\n"
                            + "  \"name\": \"Local IntelliJ / Maven\",\n"
                            + "  \"type\": \"local\",\n"
                            + "  \"buildName\": \"Plugin_ERP "
                            + Instant.now().toString()
                            + "\",\n"
                            + "  \"buildOrder\": "
                            + buildOrder
                            + ",\n"
                            + "  \"buildUrl\": \"\",\n"
                            + "  \"reportUrl\": \"\"\n"
                            + "}\n";
            Files.writeString(executorPath, json);
        } catch (Exception e) {
            LogUtils.error("Failed to write Allure executor metadata: " + e.getMessage());
        }
    }

    private long nextBuildOrder() {
        Path historyTrend = AllureConstants.RESULTS_HISTORY_DIR.resolve("history-trend.json");
        long previous = 0;
        try {
            if (Files.isRegularFile(historyTrend)) {
                Matcher matcher =
                        Pattern.compile("\\\"buildOrder\\\"\\s*:\\s*(\\d+)")
                                .matcher(Files.readString(historyTrend));
                while (matcher.find())
                    previous = Math.max(previous, Long.parseLong(matcher.group(1)));
            }
        } catch (Exception e) {
            LogUtils.error("Could not read previous Allure build order: " + e.getMessage());
        }
        return previous + 1;
    }

    @Override
    public void onExecutionFinish() {
        System.out.println(">>> Execution Finished");
        Plugin_ERP.Util.Allur.RunFailureOverview.generate();

        try {
            WebDriverFactory.quitDriver();
        } catch (Exception ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "TestNGListeners fallback: " + ignored.getClass().getSimpleName());
        }

        // مهم: ما ترميش RuntimeException هنا
        if (!AllureReportPolicy.current().generate()) {
            LogUtils.info("Automatic Allure HTML generation disabled; raw results retained.");
        } else if (hasAllureResults()) {
            try {
                AllureBinaryManger.generateAndOpenDetached();
            } catch (Exception e) {
                LogUtils.error(
                        "Allure report generation failed but tests finished: " + e.getMessage());
            }
        } else {
            LogUtils.info("No Allure test results were produced; report generation skipped.");
        }
    }

    private boolean hasAllureResults() {
        Path resultsDir = AllureConstants.ALLURE_RESULTS_DIR;
        if (!Files.isDirectory(resultsDir)) return false;

        try (Stream<Path> files = Files.list(resultsDir)) {
            return files.anyMatch(
                    path -> {
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
        Plugin_ERP.Util.FailureVideoRecorder.begin(
                result.getTestClass().getName() + "." + result.getName());
        var scenarioMethod = result.getMethod().getConstructorOrMethod().getMethod();
        String scenarioClass = result.getTestClass().getName();
        io.qameta.allure.Allure.getLifecycle()
                .updateTestCase(
                        test ->
                                Plugin_ERP.Util.Allur.ScenarioReportCatalog.apply(
                                        test, scenarioClass, scenarioMethod));
        io.qameta.allure.Allure.getLifecycle()
                .getCurrentTestCase()
                .ifPresent(
                        id ->
                                io.qameta.allure.Allure.getLifecycle()
                                        .updateTestCase(
                                                id,
                                                Plugin_ERP.Util.Allur.FailureReviewScope::apply));
        if (WebDriverFactory.getDriver() != null) {
            Plugin_ERP.Util.Allur.ScenarioAccess.capture(
                    WebDriverFactory.getDriver(), "الشاشة عند بدء الحالة");
            Plugin_ERP.Util.Allur.ScenarioAssertions.begin();
        } else Plugin_ERP.Util.Allur.ScenarioAssertions.end();

        // Update browser values without dropping the other run metadata.
        try {
            WebDriver driver = WebDriverFactory.getDriver();
            var browser = AllureEnvironment.browserDetails(driver);
            if (!browser.isEmpty()) {
                AllureEnvironment.updateBrowser(
                        browser.get("Browser"), browser.get("Browser Version"));
            }
        } catch (Exception e) {
            LogUtils.error(
                    "Failed to update Allure environment with browser capabilities: "
                            + e.getMessage());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Plugin_ERP.Util.FailureVideoRecorder.complete(false);
        Plugin_ERP.Util.Allur.ScenarioAssertions.end();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Path recording = Plugin_ERP.Util.FailureVideoRecorder.complete(true);
        if (recording != null) {
            try (var video = Files.newInputStream(recording)) {
                io.qameta.allure.Allure.addAttachment(
                        "Failed test browser recording", "video/mp4", video, ".mp4");
            } catch (java.io.IOException failure) {
                LogUtils.warn("Could not attach failure video; file retained locally");
            }
        }
        Plugin_ERP.Util.Allur.ScenarioAssertions.end();
        io.qameta.allure.Allure.getLifecycle()
                .updateTestCase(
                        test ->
                                Plugin_ERP.Util.Allur.FailureClassification.apply(
                                        test, result == null ? null : result.getThrowable()));
        try {
            Plugin_ERP.Util.Allur.ScenarioFailureReport.attach(result);
        } catch (RuntimeException reportFailure) {
            LogUtils.error(
                    "Could not attach readable failure summary; original test failure retained.");
        }
        takeScreenshot(result, "FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        io.qameta.allure.Allure.getLifecycle()
                .updateTestCase(
                        test ->
                                Plugin_ERP.Util.Allur.FailureClassification.apply(
                                        test.setStatus(io.qameta.allure.model.Status.SKIPPED),
                                        result == null ? null : result.getThrowable()));
        Plugin_ERP.Util.FailureVideoRecorder.complete(false);
        Plugin_ERP.Util.Allur.ScenarioAssertions.end();
    }

    // ================= Utils =================

    private void takeScreenshot(ITestResult result, String status) {
        WebDriver driver = WebDriverFactory.getDriver();
        if (driver == null) return;

        Plugin_ERP.Util.Allur.ScenarioAccess.capture(
                driver, "FAIL".equals(status) ? "الشاشة وقت الخطأ" : "الشاشة عند انتهاء الحالة");

        ScreenShotsUtils.takeScreenShot(driver, status + "_" + result.getName());
    }
}
