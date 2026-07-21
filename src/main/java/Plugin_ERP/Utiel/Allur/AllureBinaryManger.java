package Plugin_ERP.Utiel.Allur;

import Plugin_ERP.Bots.LogUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AllureBinaryManger {

    private static final String ALLURE_VERSION = "2.31.0";

    // ========================= Public APIs =========================

    public static void cleanAllure(boolean cleanResults, boolean cleanReport) {
        if (cleanResults) deleteDirIfExists(AllureConstants.ALLURE_RESULTS_DIR);
        if (cleanReport) deleteDirIfExists(AllureConstants.ALLURE_REPORT_DIR);
    }

    public static void copyHistoryFromReportToResults() {
        try {
            Path srcHistory = AllureConstants.ALLURE_REPORT_DIR.resolve("history");
            Path destHistory = AllureConstants.ALLURE_RESULTS_DIR.resolve("history");

            if (!Files.exists(srcHistory) || !Files.isDirectory(srcHistory)) {
                LogUtils.info("No Allure history to copy (source not found): " + srcHistory);
                return;
            }

            Files.createDirectories(destHistory);

            Files.walk(srcHistory).forEach(src -> {
                try {
                    Path rel = srcHistory.relativize(src);
                    Path dest = destHistory.resolve(rel);

                    if (Files.isDirectory(src)) Files.createDirectories(dest);
                    else Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception ignored) {
                }
            });

            LogUtils.info("Allure history copied to results: " + destHistory);

        } catch (Exception e) {
            LogUtils.error("Failed to copy Allure history: " + e.getMessage());
        }
    }

    /** Generate static report only */
    public static void generateReportOnly() {
        try {
            Path exe = prepareAllureCLI();
            runCommand(
                    exe.toString(),
                    "generate",
                    AllureConstants.ALLURE_RESULTS_DIR.toString(),
                    "-o",
                    AllureConstants.ALLURE_REPORT_DIR.toString(),
                    "--clean"
            );
            LogUtils.info("Allure report generated: " + AllureConstants.ALLURE_REPORT_DIR.toAbsolutePath());
        } catch (Exception e) {
            LogUtils.error("Allure generate failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate + Open DETACHED (IntelliJ run will finish normally)
     * - generate report
     * - start: allure open <reportDir> (no wait)
     * - discard stdout/stderr so Java doesn't keep handles open
     *
     * NOTE: On some Windows/JDK combos, redirectInput(DISCARD) throws:
     * "Redirect invalid for reading: WRITE" -> so we DO NOT redirect input.
     */
    public static void generateAndOpenDetached() {
        try {
            Path exe = prepareAllureCLI();

            // 1) generate
            runCommand(
                    exe.toString(),
                    "generate",
                    AllureConstants.ALLURE_RESULTS_DIR.toString(),
                    "-o",
                    AllureConstants.ALLURE_REPORT_DIR.toString(),
                    "--clean"
            );

            // 2) open (detached)
            ProcessBuilder pb = new ProcessBuilder(
                    exe.toString(),
                    "open",
                    AllureConstants.ALLURE_REPORT_DIR.toString(),
                    "--host", "localhost",
                    "--port", "0"
            );

            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            // IMPORTANT: do NOT redirect input (fixes Windows "Redirect invalid for reading: WRITE")

            pb.start(); // fire & forget

            LogUtils.info("Allure report opened (detached): " + AllureConstants.ALLURE_REPORT_DIR.toAbsolutePath());

        } catch (Exception e) {
            // IMPORTANT: Don't fail the whole run if Allure fails
            LogUtils.error("Allure generate/open (detached) failed: " + e.getMessage());
        }
    }

    // ========================= CLI Prep =========================

    private static Path prepareAllureCLI() throws Exception {
        Files.createDirectories(AllureConstants.EXTRACTED);
        Files.createDirectories(AllureConstants.DOWNLOAD_DIR);

        // 1) if already exists
        Optional<Path> existing = findPreferredAllureExecutableUnder(AllureConstants.EXTRACTED);
        if (existing.isPresent()) return existing.get();

        // 2) download & unzip
        Path zip = downloadZip();
        unzip(zip, AllureConstants.EXTRACTED);

        // 3) search again
        Optional<Path> after = findPreferredAllureExecutableUnder(AllureConstants.EXTRACTED);
        if (after.isPresent()) return after.get();

        throw new IllegalStateException("Allure executable not found after extract under: " + AllureConstants.EXTRACTED);
    }

    /**
     * On Windows, prefer allure.bat for ProcessBuilder.
     * On Linux/Mac, "allure" works.
     */
    private static Optional<Path> findPreferredAllureExecutableUnder(Path root) throws IOException {
        if (!Files.exists(root)) return Optional.empty();

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        if (isWindows) {
            Optional<Path> bat = Files.walk(root)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase("allure.bat"))
                    .findFirst();
            if (bat.isPresent()) return bat;
        }

        return Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String name = p.getFileName().toString().toLowerCase();
                    return name.equals("allure") || name.equals("allure.bat") || name.equals("allure.cmd") || name.equals("allure.exe");
                })
                .findFirst();
    }

    private static Path downloadZip() throws Exception {
        String url = AllureConstants.ALLURE_ZIP_BASE_URL
                + ALLURE_VERSION
                + "/allure-" + ALLURE_VERSION + ".zip";

        Path zipPath = AllureConstants.DOWNLOAD_DIR.resolve("allure-" + ALLURE_VERSION + ".zip");

        if (Files.exists(zipPath) && Files.size(zipPath) > (1024 * 1024)) {
            LogUtils.info("Allure zip already exists: " + zipPath);
            return zipPath;
        }

        LogUtils.info("Downloading Allure CLI: " + url);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(zipPath));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to download Allure zip. HTTP: " + response.statusCode());
        }

        LogUtils.info("Allure zip downloaded: " + zipPath);
        return zipPath;
    }

    private static void unzip(Path zip, Path toDir) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zip)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path out = toDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(out);
                } else {
                    Files.createDirectories(out.getParent());
                    Files.copy(zis, out, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void runCommand(String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        // IMPORTANT: do NOT redirect input (fixes Windows "Redirect invalid for reading: WRITE")

        Process p = pb.start();

        boolean done = p.waitFor(5, TimeUnit.MINUTES);
        if (!done) {
            p.destroyForcibly();
            throw new RuntimeException("Command timed out: " + String.join(" ", cmd));
        }

        if (p.exitValue() != 0) {
            throw new RuntimeException("Command failed (" + p.exitValue() + "): " + String.join(" ", cmd));
        }
    }

    private static void deleteDirIfExists(Path dir) {
        try {
            if (!Files.exists(dir)) return;
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (Exception ignored) {
                        }
                    });
        } catch (Exception ignored) {
        }
    }
}
