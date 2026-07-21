package Plugin_ERP.Utiel.Allur;

import Plugin_ERP.Bots.LogUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static Plugin_ERP.Utiel.PropertyReader.getProperty;

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
            putSafe(props, "URL", getProperty("url"));
            putSafe(props, "Execution Type", getProperty("ExecutionType"));

            // Browser (prefer real from capabilities; fallback to properties)
            String bName = (browserName != null && !browserName.isBlank()) ? browserName : getProperty("browserType");
            String bVer  = (browserVersion != null && !browserVersion.isBlank()) ? browserVersion : getProperty("browserVersion");

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
        if (!writtenOnce) {
            writeEnvironment(null, null);
        }
    }

    private static void putSafe(Properties props, String key, String value) {
        props.setProperty(key, (value == null || value.isBlank()) ? "N/A" : value);
    }
}
