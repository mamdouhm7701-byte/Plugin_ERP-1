package Invoice;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

final class SalesExportAssertions {
  private SalesExportAssertions() {}

  static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format) {
    String normalizedFormat = format.toLowerCase(Locale.ROOT);
    Assert.assertTrue(
        normalizedFormat.equals("pdf") || normalizedFormat.equals("excel"),
        "صيغة التصدير غير مدعومة: " + format);

    installDownloadCapture(driver);
    exportAction.run();

    try {
      new WebDriverWait(driver, Duration.ofSeconds(15))
        .until(
            webDriver ->
                Boolean.TRUE.equals(
                    ((JavascriptExecutor) webDriver)
                        .executeScript(
                            "return window.__qaExportCapture "
                                + "&& (window.__qaExportCapture.blobs.length > 0 "
                                + "|| Boolean(window.__qaExportCapture.href));")));
    } catch (org.openqa.selenium.TimeoutException timeout) {
      Object errors = ((JavascriptExecutor) driver)
          .executeScript("return window.__qaExportCapture.errors;");
      throw new AssertionError("لم ينتج التصدير ملفًا. أخطاء المتصفح: " + errors, timeout);
    }

    String dataUrl =
        (String)
            ((JavascriptExecutor) driver)
                .executeAsyncScript(
                    "const done = arguments[arguments.length - 1];const capture ="
                        + " window.__qaExportCapture;const finish = blob => {  if (!blob) {"
                        + " done(null); return; }  const reader = new FileReader();  reader.onload"
                        + " = () => done(reader.result);  reader.onerror = () => done(null); "
                        + " reader.readAsDataURL(blob);};if (capture.blobs.length) {"
                        + " finish(capture.blobs[0]); }else if (capture.href &&"
                        + " capture.href.startsWith('blob:')) {  fetch(capture.href).then(response"
                        + " => response.blob()).then(finish).catch(() => done(null));} else {"
                        + " done(null); }");

    Assert.assertNotNull(dataUrl, "زر التصدير لم ينتج ملفًا قابلًا للقراءة");
    int separator = dataUrl.indexOf(',');
    Assert.assertTrue(separator > 0, "محتوى الملف الناتج غير صالح");
    byte[] contents = Base64.getDecoder().decode(dataUrl.substring(separator + 1));
    Assert.assertTrue(contents.length > 0, "ملف التصدير فارغ");

    if (normalizedFormat.equals("pdf")) {
      String signature =
          new String(contents, 0, Math.min(contents.length, 4), StandardCharsets.US_ASCII);
      Assert.assertEquals(signature, "%PDF", "الملف الناتج ليس PDF صالحًا");
    } else {
      boolean xlsxZip = contents.length >= 4 && contents[0] == 'P' && contents[1] == 'K';
      boolean legacyXls =
          contents.length >= 4 && contents[0] == (byte) 0xD0 && contents[1] == (byte) 0xCF;
      Assert.assertTrue(xlsxZip || legacyXls, "الملف الناتج ليس Excel صالحًا");
    }
  }

  private static void installDownloadCapture(WebDriver driver) {
    ((JavascriptExecutor) driver)
        .executeScript(
            "if (!window.__qaExportHookInstalled) {"
                + "  window.__qaExportHookInstalled = true;"
                + "  const recordError = error => { if (window.__qaExportCapture)"
                + "    window.__qaExportCapture.errors.push(String(error)); };"
                + "  window.addEventListener('error', event => recordError(event.message));"
                + "  window.addEventListener('unhandledrejection', event => recordError(event.reason));"
                + "  const originalConsoleError = console.error.bind(console);"
                + "  console.error = (...args) => { args.forEach(recordError); originalConsoleError(...args); };"
                + "  const originalCreateObjectURL = URL.createObjectURL.bind(URL);"
                + "  URL.createObjectURL = blob => {"
                + "    if (window.__qaExportCapture && blob instanceof Blob) {"
                + "      window.__qaExportCapture.blobs.push(blob);"
                + "    }"
                + "    return originalCreateObjectURL(blob);"
                + "  };"
                + "  document.addEventListener('click', event => {"
                + "    const anchor = event.target.closest && event.target.closest('a');"
                + "    if (anchor && (anchor.download || anchor.href.startsWith('blob:'))) {"
                + "      window.__qaExportCapture.href = anchor.href;"
                + "      window.__qaExportCapture.filename = anchor.download || '';"
                + "    }"
                + "  }, true);"
                + "}"
                + "window.__qaExportCapture = { blobs: [], href: '', filename: '', errors: [] };");
  }
}
