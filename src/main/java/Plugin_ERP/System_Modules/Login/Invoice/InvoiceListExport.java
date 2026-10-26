package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Chooses an existing invoice-list export control; never creates or saves a document. */
final class InvoiceListExport {
    private final WebDriver driver;
    private static final String EXPORT_SEARCHABLE_TEXT =
            "translate(concat(@class,' ',@src,' ',@alt,' ',@title,' ',@aria-label,' ',normalize-space(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";

    InvoiceListExport(WebDriver driver) {
        this.driver = driver;
    }

    void exportInvoices(String format) {
        String normalizedFormat = format == null ? "" : format.trim().toLowerCase();
        if (!normalizedFormat.equals("pdf")
                && !normalizedFormat.equals("xls")
                && !normalizedFormat.equals("xlsx")
                && !normalizedFormat.equals("excel")) {
            throw new IllegalArgumentException(
                    "Supported invoice export formats are PDF and Excel");
        }

        String exactSelector =
                normalizedFormat.equals("pdf")
                        ? "button:has(img[src*='pdf.'])"
                        : "button:has(img[src*='xls.'])";
        List<WebElement> exact = driver.findElements(By.cssSelector(exactSelector));
        if (exact.stream().anyMatch(InvoiceListExport::isDisplayed)) {
            exact.stream().filter(InvoiceListExport::isDisplayed).findFirst().orElseThrow().click();
            return;
        }
        String tokenMatch =
                normalizedFormat.equals("pdf")
                        ? "contains(" + EXPORT_SEARCHABLE_TEXT + ", 'pdf')"
                        : "(contains("
                                + EXPORT_SEARCHABLE_TEXT
                                + ", 'xls') or "
                                + "contains("
                                + EXPORT_SEARCHABLE_TEXT
                                + ", 'excel') or "
                                + "contains("
                                + EXPORT_SEARCHABLE_TEXT
                                + ", 'spreadsheet'))";
        By exportControl =
                By.xpath(
                        "//*[self::button or self::a]["
                                + tokenMatch
                                + " or .//*["
                                + tokenMatch
                                + "]]");

        List<WebElement> controls =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d -> {
                                    List<WebElement> found = d.findElements(exportControl);
                                    return found.stream().anyMatch(WebElement::isDisplayed)
                                            ? found
                                            : null;
                                });
        controls.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Invoice export control not visible: " + format))
                .click();
    }

    private static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "InvoiceListExport condition unavailable: "
                            + ignored.getClass().getSimpleName());
            return false;
        }
    }
}
