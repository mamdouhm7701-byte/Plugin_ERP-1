/* شرح الملف: يتأكد أن اختيار يوم 30 يطابق الشهر والسنة والتقويم المقصود، ثم يراجع التاريخ الذي احتفظت به الشاشة. */
package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class CalendarDaySelectionTest {
    @DataProvider
    public Object[][] dates() {
        return new Object[][] {
            {"example-datepicker", "fromDate", "2026-10-30"},
            {"example-datepicker2", "toDate", "2028-02-29"}
        };
    }

    @Test(dataProvider = "dates")
    public void selectsTheFullDateInsideItsOwnPicker(String pickerId, String field, String date) {
        String[] stored = {"2026-09-30"};
        int[] clicks = {0};
        WebElement day =
                element(
                        (method, args) ->
                                switch (method) {
                                    case "isDisplayed", "isEnabled" -> true;
                                    case "click" -> {
                                        clicks[0]++;
                                        stored[0] = date;
                                        yield null;
                                    }
                                    default -> throw new AssertionError(method);
                                });
        WebElement calendar =
                element(
                        (method, args) -> {
                            Assert.assertEquals(method, "findElements");
                            String selector = args[0].toString();
                            Assert.assertTrue(selector.contains("[data-date='" + date + "']"));
                            Assert.assertTrue(selector.contains(":not([aria-disabled='true'])"));
                            Assert.assertTrue(selector.contains(":not([aria-hidden='true'])"));
                            Assert.assertTrue(
                                    selector.endsWith(" .btn"),
                                    "Click the inner day button, not its container");
                            return List.of(day);
                        });
        WebElement picker =
                element(
                        (method, args) -> {
                            Assert.assertEquals(method, "findElement");
                            Assert.assertTrue(args[0].toString().contains("b-form-datepicker"));
                            return calendar;
                        });
        WebElement hidden =
                element(
                        (method, args) -> {
                            Assert.assertEquals(method, "getDomProperty");
                            return stored[0];
                        });
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) -> {
                                    Assert.assertEquals(
                                            method.getName(),
                                            "findElement",
                                            "Do not search all visible calendars");
                                    if (args[0].equals(By.id(pickerId))) return picker;
                                    Assert.assertTrue(
                                            args[0].toString()
                                                    .contains("input[name='" + field + "']"));
                                    return hidden;
                                });
        CalendarDaySelection.select(driver, pickerId, date);
        Assert.assertEquals(clicks[0], 1);
        Assert.assertEquals(stored[0], date);
    }

    @Test
    public void refusesAnInvalidDateBeforeClickingAnything() {
        Assert.expectThrows(
                DateTimeParseException.class,
                () -> CalendarDaySelection.select(null, "example-datepicker", "2026-02-30"));
    }

    private interface Answer {
        Object call(String method, Object[] args);
    }

    private WebElement element(Answer answer) {
        return (WebElement)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) -> answer.call(method.getName(), args));
    }
}
