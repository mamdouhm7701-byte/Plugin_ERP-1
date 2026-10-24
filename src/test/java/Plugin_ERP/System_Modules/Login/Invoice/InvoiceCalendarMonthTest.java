package Plugin_ERP.System_Modules.Login.Invoice;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InvoiceCalendarMonthTest {
    @DataProvider public Object[][] captions() {
        return new Object[][] {{"October 2026", 10}, {"Oct 2026", 10}, {"May 2026", 5},
                {"أكتوبر 2026", 10}, {"10/2026", 10}};
    }
    @Test(dataProvider = "captions")
    public void readsArabicEnglishAndNumericCalendarCaptions(String caption, int expected) throws Exception {
        var parser = InvoiceListCalendar.class.getDeclaredMethod("parseMonth", String.class);
        parser.setAccessible(true);
        Assert.assertEquals(parser.invoke(new InvoiceListCalendar(null), caption), expected);
    }
}
