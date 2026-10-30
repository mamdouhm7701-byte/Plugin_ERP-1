/* شرح الملف: يراجع أن النص وحده لا يكفي أثناء التحميل، وأن التقرير الفارغ أو المتغير يظل قيد الانتظار بمهلة واحدة. */
package Plugin_ERP.Bots;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ReportReadinessTest {
    @Test
    public void textCannotFinishTheWaitWhileAnyLoaderRemainsVisible() {
        var report = new Snapshot();
        report.loading = true;
        Assert.assertFalse(ReportReadiness.ready(report.driver()));
        report.loading = false;
        Assert.assertTrue(ReportReadiness.ready(report.driver()));
    }

    @Test
    public void bothDocumentCompletionAndActualTextAreRequired() {
        var report = new Snapshot();
        report.state = "interactive";
        Assert.assertFalse(ReportReadiness.ready(report.driver()));
        report.state = "complete";
        report.text = " \n ";
        Assert.assertFalse(ReportReadiness.ready(report.driver()));
        report.text = "Invoice 115";
        Assert.assertTrue(ReportReadiness.ready(report.driver()));
    }

    @Test
    public void aReplacedReportRetriesAndAnUnfinishedReportTimesOut() {
        var report = new Snapshot();
        report.stale = true;
        Assert.assertFalse(ReportReadiness.ready(report.driver()));
        report.stale = false;
        report.loading = true;
        Assert.expectThrows(
                TimeoutException.class,
                () -> ReportReadiness.await(report.driver(), Duration.ZERO));
    }

    private static final class Snapshot {
        String state = "complete", text = "Invoice 115";
        boolean loading, stale;

        WebDriver driver() {
            return (WebDriver)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                            (proxy, method, args) -> {
                                if (method.getName().equals("executeScript")) return state;
                                if (method.getName().equals("toString"))
                                    return "Report wait browser";
                                if (!method.getName().equals("findElements"))
                                    throw new AssertionError(method.getName());
                                if (stale)
                                    throw new StaleElementReferenceException("Report replaced");
                                if (args[0].toString().contains("progressbar"))
                                    return List.of(element(false, ""), element(loading, ""));
                                return List.of(element(true, text));
                            });
        }

        WebElement element(boolean visible, String content) {
            return (WebElement)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebElement.class},
                            (proxy, method, args) ->
                                    switch (method.getName()) {
                                        case "isDisplayed" -> visible;
                                        case "getText" -> content;
                                        default -> throw new AssertionError(method.getName());
                                    });
        }
    }
}
