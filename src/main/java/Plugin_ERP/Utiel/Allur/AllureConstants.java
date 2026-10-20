package Plugin_ERP.Utiel.Allur;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AllureConstants {

    // Project root
    public static final Path PROJECT_DIR = Paths.get(System.getProperty("user.dir"));

    // Allure output
    public static final Path ALLURE_RESULTS_DIR = PROJECT_DIR.resolve("allure-results");

    public static final Path ALLURE_REPORT_DIR = PROJECT_DIR.resolve("allure-report");

    // History
    public static final Path REPORT_HISTORY_DIR = ALLURE_REPORT_DIR.resolve("history");

    public static final Path RESULTS_HISTORY_DIR = ALLURE_RESULTS_DIR.resolve("history");

    // ================= CLI Download & Extract =================

    // هنحمل Allure CLI داخل المشروع نفسه (أنضف وأسلم)
    public static final Path ALLURE_CLI_DIR = PROJECT_DIR.resolve("allure-cli");

    // مكان تحميل الـ zip
    public static final Path DOWNLOAD_DIR = ALLURE_CLI_DIR.resolve("downloads");

    // مكان فك الضغط
    public static final Path EXTRACTED = ALLURE_CLI_DIR.resolve("extracted");

    // GitHub release base URL
    public static final String ALLURE_ZIP_BASE_URL =
            "https://github.com/allure-framework/allure2/releases/download/";
}
