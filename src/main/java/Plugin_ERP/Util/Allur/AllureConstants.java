/* شرح الملف: تحديد أماكن ملفات النتائج والتقرير.
 */
package Plugin_ERP.Util.Allur;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AllureConstants {

    // Project root
    public static final Path PROJECT_DIR =
            Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

    // Allure output
    public static final Path ALLURE_RESULTS_DIR =
            outputPath(
                    PROJECT_DIR, System.getProperty("allure.results.directory", "allure-results"));

    public static final Path ALLURE_REPORT_DIR =
            outputPath(PROJECT_DIR, System.getProperty("allure.report.directory", "allure-report"));

    static {
        if (ALLURE_RESULTS_DIR.startsWith(ALLURE_REPORT_DIR)
                || ALLURE_REPORT_DIR.startsWith(ALLURE_RESULTS_DIR))
            throw new IllegalArgumentException(
                    "Allure results and report directories must be separate");
    }

    static Path outputPath(Path project, String configured) {
        Path root = project.toAbsolutePath().normalize();
        if (configured.isBlank())
            throw new IllegalArgumentException("Allure output directory is blank");
        Path result = root.resolve(configured).normalize();
        if (!result.startsWith(root)
                || result.equals(root)
                || result.equals(root.resolve("target")))
            throw new IllegalArgumentException(
                    "Allure output must be a dedicated directory inside the project");
        String top = root.relativize(result).getName(0).toString();
        if (java.util.Set.of("src", "docs", "config", "outputs", ".git", ".idea", ".agents", ".mvn")
                .contains(top.toLowerCase(java.util.Locale.ROOT)))
            throw new IllegalArgumentException(
                    "Allure output cannot use a source or configuration directory");
        return result;
    }

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
