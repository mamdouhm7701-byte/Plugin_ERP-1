/* شرح الملف: تسجيل ما حدث في المتصفح والاحتفاظ بالفيديو عند الفشل إذا كان مفعّلًا.
 */
package Plugin_ERP.Util;

import Plugin_ERP.Bots.LogUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

public final class FailureVideoRecorder implements WebDriverListener {
    private static final ThreadLocal<FailureVideoRecorder> CURRENT = new ThreadLocal<>();
    private final WebDriver browser;
    private final Path directory;
    private final int fps;
    private final int maxWidth;
    private Path file;
    private AWTSequenceEncoder encoder;
    private BufferedImage lastFrame;
    private long started;
    private long nextCapture;
    private long frames;

    FailureVideoRecorder(WebDriver browser, Path directory, int fps, int maxWidth) {
        this.browser = browser;
        this.directory = directory;
        this.fps = fps;
        this.maxWidth = maxWidth;
    }

    public static boolean enabled() {
        return Boolean.parseBoolean(PropertyReader.getProperty("video.enabled"));
    }

    public static WebDriver decorate(WebDriver browser) {
        CURRENT.remove();
        if (!enabled() || !(browser instanceof TakesScreenshot)) return browser;
        int fps = integerSetting("video.frame.rate", 2, 1, 5);
        int width = integerSetting("video.max.width", 960, 320, 1920);
        String folder = PropertyReader.getProperty("video.folder");
        Path project = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path directory = project.resolve(folder == null ? "target/video" : folder).normalize();
        if (!directory.startsWith(project.resolve("target"))) {
            throw new IllegalArgumentException("Video folder must be inside target");
        }
        var recorder = new FailureVideoRecorder(browser, directory, fps, width);
        CURRENT.set(recorder);
        return new EventFiringDecorator<WebDriver>(recorder).decorate(browser);
    }

    private static int integerSetting(String key, int fallback, int min, int max) {
        String configured = PropertyReader.getProperty(key);
        int value = configured == null ? fallback : Integer.parseInt(configured);
        if (value < min || value > max) throw new IllegalArgumentException("Invalid " + key);
        return value;
    }

    public static void begin(String testName) {
        var current = CURRENT.get();
        if (current != null) current.start(testName);
    }

    public static Path complete(boolean failed) {
        var current = CURRENT.get();
        return current == null ? null : current.finish(failed);
    }

    public static void release() {
        try {
            complete(false);
        } finally {
            CURRENT.remove();
        }
    }

    void start(String testName) {
        finish(false);
        try {
            Files.createDirectories(directory);
            String safeName = testName.replaceAll("[^A-Za-z0-9._-]", "_");
            if (safeName.length() > 100) safeName = safeName.substring(0, 100);
            file = directory.resolve(safeName + "-" + UUID.randomUUID() + ".mp4");
            started = System.nanoTime();
            nextCapture = 0;
            frames = 0;
            capture();
        } catch (RuntimeException | java.io.IOException failure) {
            LogUtils.warn(
                    "Browser recording unavailable ("
                            + failure.getClass().getSimpleName()
                            + "); test outcome preserved");
            discardIncompleteRecording();
        }
    }

    @Override
    public void afterAnyCall(Object target, Method method, Object[] args, Object result) {
        if (file != null && !method.getName().equals("quit")) capture();
    }

    private void capture() {
        if (file == null || System.nanoTime() < nextCapture) return;
        try {
            byte[] bytes = ((TakesScreenshot) browser).getScreenshotAs(OutputType.BYTES);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) throw new java.io.IOException("Browser screenshot is not an image");
            int width =
                    lastFrame == null ? Math.min(maxWidth, image.getWidth()) : lastFrame.getWidth();
            width -= width % 2;
            int height =
                    lastFrame == null
                            ? Math.max(2, image.getHeight() * width / image.getWidth())
                            : lastFrame.getHeight();
            height -= height % 2;
            var frame = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            var graphics = frame.createGraphics();
            try {
                graphics.drawImage(image, 0, 0, width, height, null);
            } finally {
                graphics.dispose();
            }
            if (encoder == null)
                encoder = AWTSequenceEncoder.createSequenceEncoder(file.toFile(), fps);
            long expectedFrames = (System.nanoTime() - started) * fps / 1_000_000_000L;
            while (lastFrame != null && frames < expectedFrames) {
                encoder.encodeImage(lastFrame);
                frames++;
            }
            encoder.encodeImage(frame);
            frames++;
            lastFrame = frame;
            nextCapture = System.nanoTime() + 1_000_000_000L / fps;
        } catch (RuntimeException | java.io.IOException failure) {
            LogUtils.warn(
                    "Browser recording unavailable ("
                            + failure.getClass().getSimpleName()
                            + "); test outcome preserved");
            discardIncompleteRecording();
        }
    }

    Path finish(boolean failed) {
        Path recording = file;
        if (recording == null) return null;
        nextCapture = 0;
        capture();
        file = null;
        try {
            if (encoder != null) encoder.finish();
            if (!failed || !Files.isRegularFile(recording) || Files.size(recording) == 0) {
                Files.deleteIfExists(recording);
                return null;
            }
            return recording;
        } catch (RuntimeException | java.io.IOException failure) {
            LogUtils.warn("Video finalization failed (" + failure.getClass().getSimpleName() + ")");
            return null;
        } finally {
            encoder = null;
            lastFrame = null;
        }
    }

    private void discardIncompleteRecording() {
        Path incomplete = file;
        file = null;
        try {
            if (encoder != null) encoder.finish();
            if (incomplete != null) Files.deleteIfExists(incomplete);
        } catch (RuntimeException | java.io.IOException cleanupFailure) {
            LogUtils.debug(
                    "Incomplete video cleanup failed ("
                            + cleanupFailure.getClass().getSimpleName()
                            + ")");
        } finally {
            encoder = null;
            lastFrame = null;
        }
    }
}
