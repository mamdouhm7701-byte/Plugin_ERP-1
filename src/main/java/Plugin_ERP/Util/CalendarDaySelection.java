/* شرح الملف: يختار اليوم والشهر والسنة المطلوبة من التقويم المفتوح، حتى لا يضغط يومًا بنفس الرقم من شهر مجاور. */
package Plugin_ERP.Util;

import java.time.Duration;
import java.time.LocalDate;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CalendarDaySelection {
    private CalendarDaySelection() {}

    public static void select(WebDriver driver, String pickerId, String value) {
        String date = LocalDate.parse(value).toString();
        String inputName = "example-datepicker".equals(pickerId) ? "fromDate" : "toDate";
        By dateInput = By.cssSelector("input[name='" + inputName + "']");
        UiPolling.configure(new WebDriverWait(driver, Duration.ofSeconds(8)))
                .ignoring(StaleElementReferenceException.class)
                .withMessage("لم يظهر يوم التقويم المطلوب " + date + " في " + pickerId)
                .until(
                        current -> {
                            WebElement picker = current.findElement(By.id(pickerId));
                            WebElement calendar =
                                    picker.findElement(
                                            By.xpath(
                                                    "ancestor-or-self::*[contains(concat(' ',normalize-space(@class),' '),' b-form-datepicker ')][1]"));
                            By day =
                                    By.cssSelector(
                                            ".b-calendar [data-date='"
                                                    + date
                                                    + "']:not([aria-disabled='true']):not([aria-hidden='true']) .btn");
                            for (WebElement button : calendar.findElements(day)) {
                                if (!button.isDisplayed() || !button.isEnabled()) continue;
                                button.click();
                                return true;
                            }
                            return false;
                        });
        UiPolling.configure(new WebDriverWait(driver, Duration.ofSeconds(5)))
                .ignoring(StaleElementReferenceException.class)
                .withMessage("لم يحتفظ حقل التقويم بالتاريخ المطلوب " + date)
                .until(
                        current ->
                                date.equals(
                                        current.findElement(dateInput).getDomProperty("value")));
    }
}
