package Plugin_ERP.System_Modules.Login.purches;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Caption parsing must work for every month in the ERP's Arabic and English datepicker. */
public final class PurchaseCalendarTest {
    @DataProvider
    public Object[][] calendarCaptions() {
        String[] arabic = {
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        };
        List<Object[]> cases = new ArrayList<>();
        for (Month month : Month.values()) {
            int number = month.getValue();
            cases.add(new Object[] {arabic[number - 1] + " 2026", number});
            cases.add(
                    new Object[] {
                        month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " 2026", number
                    });
            cases.add(
                    new Object[] {
                        month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                        .toLowerCase(Locale.ROOT)
                                + ", 2026",
                        number
                    });
            cases.add(new Object[] {number + "/2026", number});
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "calendarCaptions")
    public void recognizesCalendarCaption(String caption, int expectedMonth) {
        Assert.assertEquals(PurchaseCalendar.parseMonth(caption), expectedMonth);
    }

    @DataProvider
    public Object[][] unknownCaptions() {
        return new Object[][] {{""}, {"2026"}, {"not a month"}};
    }

    @Test(dataProvider = "unknownCaptions")
    public void rejectsUnknownCaptionExplicitly(String caption) {
        IllegalArgumentException failure =
                Assert.expectThrows(
                        IllegalArgumentException.class, () -> PurchaseCalendar.parseMonth(caption));
        Assert.assertTrue(failure.getMessage().contains("Calendar month not found"));
    }
}
