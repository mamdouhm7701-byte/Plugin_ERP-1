package Plugin_ERP.Util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FailureVideoRecorderTest {
    @Test
    public void successfulRunIsDiscardedAndFailureProducesPlayableContainer() throws Exception {
        Path folder = Files.createTempDirectory("browser-video-test-");
        try {
            var recorder = new FailureVideoRecorder(browser(), folder, 2, 320);
            recorder.start("success");
            Assert.assertNull(recorder.finish(false));
            try (var files = Files.list(folder)) {
                Assert.assertEquals(files.count(), 0L);
            }
            recorder.start("failure");
            Path movie = recorder.finish(true);
            Assert.assertNotNull(movie);
            byte[] bytes = Files.readAllBytes(movie);
            String contents = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
            Assert.assertTrue(contents.contains("ftyp"));
            Assert.assertTrue(
                    contents.contains("moov"), "MP4 must be finalized with its movie index");
            Assert.assertTrue(contents.contains("mdat"));
        } finally {
            try (var files = Files.list(folder)) {
                for (Path file : files.toList()) Files.delete(file);
            }
            Files.delete(folder);
        }
    }

    @Test
    public void disabledSettingDoesNotDecorateOrCaptureBrowser() {
        String previous = System.getProperty("video.enabled");
        System.setProperty("video.enabled", "false");
        try {
            WebDriver browser =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebDriver.class, TakesScreenshot.class},
                                    (proxy, method, args) -> {
                                        throw new AssertionError(
                                                "Disabled recorder touched browser");
                                    });
            Assert.assertSame(FailureVideoRecorder.decorate(browser), browser);
            FailureVideoRecorder.begin("disabled");
            Assert.assertNull(FailureVideoRecorder.complete(true));
        } finally {
            FailureVideoRecorder.release();
            if (previous == null) System.clearProperty("video.enabled");
            else System.setProperty("video.enabled", previous);
        }
    }

    private WebDriver browser() throws Exception {
        var image = new BufferedImage(64, 48, BufferedImage.TYPE_INT_RGB);
        var bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class, TakesScreenshot.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("getScreenshotAs"))
                                return bytes.toByteArray();
                            throw new AssertionError(method.getName());
                        });
    }
}
