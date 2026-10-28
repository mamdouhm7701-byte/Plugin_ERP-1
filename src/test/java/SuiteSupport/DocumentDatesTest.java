/* شرح الملف: اختبارات تحديد التواريخ المستخدمة في المستندات.
 */
package SuiteSupport;

import Plugin_ERP.Util.DocumentDates;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DocumentDatesTest {
    @DataProvider
    public Object[][] displayedDates() {
        return new Object[][] {
            {"2026-10-20", "2026-10-20"},
            {"2026/10/20", "2026-10-20"},
            {"20-\u200f10-\u200f2026", "2026-10-20"},
            {"٢٠/١٠/٢٠٢٦", "2026-10-20"},
            {"29/2/2024", "2024-02-29"}
        };
    }

    @Test(dataProvider = "displayedDates")
    public void acceptsDatesFromArabicAndIsoPickers(String displayed, String expected) {
        Assert.assertEquals(DocumentDates.parse(displayed), LocalDate.parse(expected));
    }

    @Test
    public void rejectsImpossibleDates() {
        Assert.expectThrows(DateTimeParseException.class, () -> DocumentDates.parse("30/2/2026"));
    }
}
