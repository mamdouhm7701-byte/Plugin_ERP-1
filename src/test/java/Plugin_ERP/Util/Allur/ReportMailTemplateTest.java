/* شرح الملف: اختبارات تجهيز نص رسالة التقرير وأعداد النجاح والفشل.
 */
package Plugin_ERP.Util.Allur;

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportMailTemplateTest {
    @Test
    public void failureMailShowsActualCountsAndEscapesUntrustedContent() {
        var view =
                new ReportMailTemplate.Run(
                        "Smoke <script>",
                        "Test & Demo",
                        "run-42",
                        10,
                        6,
                        2,
                        1,
                        1,
                        0,
                        65000,
                        List.of(
                                new ReportMailTemplate.Failure(
                                        "save <img>", "Denied & unavailable")));
        String html = ReportMailTemplate.html(view);
        Assert.assertTrue(html.contains("dir=\"rtl\""));
        Assert.assertTrue(html.contains("60%"));
        Assert.assertTrue(html.contains("Smoke &lt;script&gt;"));
        Assert.assertTrue(html.contains("Denied &amp; unavailable"));
        Assert.assertFalse(html.contains("save <img>"));
        Assert.assertTrue(ReportMailTemplate.subject(view).contains("3"));
        Assert.assertTrue(ReportMailTemplate.plain(view).contains("run-42"));
    }

    @Test
    public void emptyRunIsNotPresentedAsSuccessful() {
        var view =
                new ReportMailTemplate.Run("Empty", "Test", "id", 0, 0, 0, 0, 0, 0, 0, List.of());
        Assert.assertTrue(ReportMailTemplate.html(view).contains("لا توجد نتائج"));
        Assert.assertFalse(ReportMailTemplate.html(view).contains("NaN"));
    }
}
