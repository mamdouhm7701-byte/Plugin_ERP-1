package Plugin_ERP.Util.Allur;

import static Plugin_ERP.Util.PropertyReader.getProperty;

import Plugin_ERP.Bots.LogUtils;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class AllureEnvironment {

    private static final Path ALLURE_RESULTS_DIR = AllureConstants.ALLURE_RESULTS_DIR;

    // علشان مانكتبش مرتين بلا داعي
    private static volatile boolean writtenOnce = false;

    /** يكتب environment.properties (ينفع تتنده أكتر من مرة - هيعمل overwrite) */
    public static synchronized void writeEnvironment(String browserName, String browserVersion) {
        try {
            Properties props = new Properties();

            // System
            putSafe(props, "OS", System.getProperty("os.name"));
            putSafe(props, "Java Version", System.getProperty("java.runtime.version"));

            // Config
            putSafe(props, "URL", Plugin_ERP.Util.PropertyReader.getApplicationUrl());
            putSafe(
                    props,
                    "Tenant",
                    "Test".equalsIgnoreCase(getProperty("Environment"))
                            ? "Test"
                            : getProperty("Tenant"));
            putSafe(props, "Execution Type", getProperty("ExecutionType"));
            putSafe(
                    props,
                    "Run ID",
                    System.getProperty("allure.run.id", "local-" + Instant.now().toEpochMilli()));
            putSafe(
                    props,
                    "Run Started",
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                            .withZone(ZoneId.systemDefault())
                            .format(Instant.now()));
            putSafe(props, "OS Version", System.getProperty("os.version"));
            putSafe(props, "OS Architecture", System.getProperty("os.arch"));
            putSafe(props, "User", System.getProperty("user.name"));
            putSafe(props, "Project", System.getProperty("project.name", "Plugin_ERP"));

            // Browser (prefer real from capabilities; fallback to properties)
            String bName =
                    (browserName != null && !browserName.isBlank())
                            ? browserName
                            : getProperty("browserType");
            String bVer =
                    (browserVersion != null && !browserVersion.isBlank())
                            ? browserVersion
                            : getProperty("browserVersion");

            putSafe(props, "Browser", bName);
            putSafe(props, "Browser Version", bVer);

            Files.createDirectories(ALLURE_RESULTS_DIR);

            Path envPath = ALLURE_RESULTS_DIR.resolve("environment.properties");
            try (OutputStream os = new FileOutputStream(envPath.toFile())) {
                props.store(os, "Allure Environment Properties");
            }

            writtenOnce = true;
            LogUtils.info("Allure environment.properties written: " + envPath.toAbsolutePath());

        } catch (Exception e) {
            LogUtils.error("Failed to write Allure environment.properties: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /** أول كتابة “أساسية” (قبل ما Capabilities تبقى جاهزة) */
    public static void writeBaseEnvironmentOnce() {
        writtenOnce = false;
        writeEnvironment(null, null);
    }

    public static synchronized void updateBrowser(String browserName, String browserVersion) {
        Properties props = new Properties();
        Path envPath = ALLURE_RESULTS_DIR.resolve("environment.properties");
        try {
            if (Files.isRegularFile(envPath)) {
                try (var input = Files.newInputStream(envPath)) {
                    props.load(input);
                }
            }
            props.setProperty("Browser", safe(browserName));
            props.setProperty("Browser Version", safe(browserVersion));
            try (OutputStream output = Files.newOutputStream(envPath)) {
                props.store(output, "Allure Environment Properties");
            }
        } catch (Exception e) {
            LogUtils.error("Failed to update Allure browser environment: " + e.getMessage());
        }
    }

    private static void putSafe(Properties props, String key, String value) {
        props.setProperty(key, safe(value));
    }

    private static String safe(String value) {
        return (value == null || value.isBlank() || value.equalsIgnoreCase("null")) ? "N/A" : value;
    }
}
