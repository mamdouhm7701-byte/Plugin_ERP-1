package Plugin_ERP.Utiel.Allur;

import Plugin_ERP.Bots.LogUtils;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AllureUtils {

    private AllureUtils() {
        // prevent instantiation
    }

    /** Delete allure-results folder quietly */
    public static void cleanAllureResults() {
        try {
            FileUtils.deleteDirectory(new File("allure-results"));
        } catch (IOException ignored) {
            // ignore
        }
    }

    /** Attach screenshot file to Allure */
    public static void attachScreenshotToAllure(String attachmentName, String filePath) {
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            LogUtils.info("Screenshot file not found: " + filePath);
            return;
        }

        try (InputStream is = Files.newInputStream(path)) {
            Allure.addAttachment(attachmentName, "image/png", is, ".png");
        } catch (Exception e) {
            LogUtils.info("Failed to attach screenshot to Allure report: " + e.getMessage());
        }
    }

    /** Generic file attachment to Allure (logs, html, json, etc.) */
    public static void attachFileToAllure(
            String attachmentName, String filePath, String mimeType, String extension) {
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            LogUtils.info("File not found: " + filePath);
            return;
        }

        try (InputStream is = Files.newInputStream(path)) {
            Allure.addAttachment(attachmentName, mimeType, is, extension);
        } catch (Exception e) {
            LogUtils.info("Failed to attach file to Allure report: " + e.getMessage());
        }
    }
}
