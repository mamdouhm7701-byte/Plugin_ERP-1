/* شرح الملف: اختبار سلامة Export selection must never click a hidden control or accept an
 * unsupported format.
 */
package Plugin_ERP.System_Modules.Login.Invoice;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class InvoiceListExportTest {
    @DataProvider
    public Object[][] formats() {
        return new Object[][] {{"pdf"}, {"xls"}, {"xlsx"}, {" EXCEL "}};
    }

    @Test(dataProvider = "formats")
    public void clicksTheVisibleExportExactlyOnce(String format) {
        AtomicInteger clicks = new AtomicInteger();
        AtomicInteger hiddenClicks = new AtomicInteger();
        WebDriver driver = driver(List.of(element(false, hiddenClicks), element(true, clicks)));
        new InvoiceListExport(driver).exportInvoices(format);
        Assert.assertEquals(clicks.get(), 1);
        Assert.assertEquals(hiddenClicks.get(), 0);
    }

    @Test
    public void hiddenExactControlFallsBackToVisibleSemanticControl() {
        AtomicInteger clicks = new AtomicInteger();
        AtomicInteger hiddenClicks = new AtomicInteger();
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("findElements")) {
                                        return args[0].toString().startsWith("By.cssSelector")
                                                ? List.of(element(false, hiddenClicks))
                                                : List.of(element(true, clicks));
                                    }
                                    if (method.getName().equals("toString")) return "export-driver";
                                    throw new AssertionError(
                                            "Unexpected browser call: " + method.getName());
                                });
        new InvoiceListExport(driver).exportInvoices("pdf");
        Assert.assertEquals(clicks.get(), 1);
        Assert.assertEquals(hiddenClicks.get(), 0);
    }

    @DataProvider
    public Object[][] unsupported() {
        return new Object[][] {{null}, {""}, {"csv"}};
    }

    @Test(dataProvider = "unsupported")
    public void rejectsUnsupportedFormatBeforeTouchingBrowser(String format) {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> new InvoiceListExport(null).exportInvoices(format));
    }

    private WebDriver driver(List<WebElement> controls) {
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("findElements")) return controls;
                            if (method.getName().equals("toString")) return "export-driver";
                            throw new AssertionError(
                                    "Unexpected browser call: " + method.getName());
                        });
    }

    private WebElement element(boolean visible, AtomicInteger clicks) {
        return (WebElement)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) -> {
                            return switch (method.getName()) {
                                case "isDisplayed" -> visible;
                                case "click" -> {
                                    clicks.incrementAndGet();
                                    yield null;
                                }
                                case "toString" -> "export-control";
                                default ->
                                        throw new AssertionError(
                                                "Unexpected element call: " + method.getName());
                            };
                        });
    }
}
