package Plugin_ERP.System_Modules.Login.purches;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Datepicker interaction shared by the purchase list filters. */
final class PurchaseCalendar {
    private final WebDriver driver;

    PurchaseCalendar(WebDriver driver) {
        this.driver = driver;
    }

    void selectDate(WebElement hiddenInput, String value) {
        String pickerId =
                "fromDate".equals(hiddenInput.getAttribute("name"))
                        ? "example-datepicker"
                        : "example-datepicker2";
        WebElement picker = driver.findElement(By.id(pickerId));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id(pickerId)));
        picker = driver.findElement(By.id(pickerId));
        picker.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        String[] parts = value.split("-");
        if (parts.length != 3) throw new IllegalArgumentException("Date must use yyyy-MM-dd");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        wait.until(d -> calendarIsVisible(pickerId));
        String shown = calendarCaption(pickerId);
        java.time.YearMonth target = java.time.YearMonth.of(year, month);
        for (int i = 0; i < 240; i++) {
            java.util.regex.Matcher yearMatcher =
                    java.util.regex.Pattern.compile("(\\d{4})").matcher(shown);
            if (!yearMatcher.find())
                throw new IllegalStateException("Calendar year not found: " + shown);
            int shownYear = Integer.parseInt(yearMatcher.group(1));
            int shownMonth = parseMonth(shown);
            java.time.YearMonth current = java.time.YearMonth.of(shownYear, shownMonth);
            if (current.equals(target)) break;
            String direction = target.isBefore(current) ? "previous" : "next";
            String previousCaption = shown;
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript(
                            "const direction=arguments[0],picker=document.getElementById(arguments[1]);"
                                    + "const calendars=[...document.querySelectorAll('.b-calendar')];"
                                    + "const calendar=calendars.find(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                                    + "||calendars.find(c=>c.getClientRects().length);"
                                    + "const buttons=calendar?[...calendar.querySelectorAll('.b-calendar-nav button')]:[];"
                                    + "const button=buttons.find(b=>((b.getAttribute('aria-label')||'')+' '+(b.title||'')).toLowerCase().includes(direction))"
                                    + "||(buttons.length?buttons[direction==='previous'?0:buttons.length-1]:null);"
                                    + "if(button){button.click();return true;}return false;",
                            direction,
                            pickerId);
            shown =
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(
                                    d -> {
                                        try {
                                            String currentCaption = calendarCaption(pickerId);
                                            return currentCaption.equals(previousCaption)
                                                    ? null
                                                    : currentCaption;
                                        } catch (
                                                org.openqa.selenium.StaleElementReferenceException
                                                        ignored) {
                                            return null;
                                        }
                                    });
        }
        String dayText = String.valueOf(day);
        By dayButtons =
                By.cssSelector(".b-calendar button, .b-calendar [role='button'], .b-calendar .btn");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(
                            d -> {
                                try {
                                    List<WebElement> candidates =
                                            d.findElements(dayButtons).stream()
                                                    .filter(WebElement::isDisplayed)
                                                    .filter(
                                                            button ->
                                                                    button.getText()
                                                                            .trim()
                                                                            .replaceAll("\\D", "")
                                                                            .equals(dayText))
                                                    .filter(
                                                            button ->
                                                                    !button.getAttribute("class")
                                                                            .contains("text-muted"))
                                                    .filter(WebElement::isEnabled)
                                                    .toList();
                                    WebElement targetDay =
                                            candidates.stream()
                                                    .filter(
                                                            button ->
                                                                    String.valueOf(
                                                                                    button
                                                                                            .getAttribute(
                                                                                                    "aria-label"))
                                                                            .contains(
                                                                                    String.valueOf(
                                                                                            year)))
                                                    .findFirst()
                                                    .orElse(
                                                            candidates.stream()
                                                                    .findFirst()
                                                                    .orElse(null));
                                    if (targetDay == null) return false;
                                    targetDay.click();
                                    return true;
                                } catch (
                                        org.openqa.selenium.StaleElementReferenceException
                                                ignored) {
                                    return false;
                                }
                            });
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IllegalStateException("Calendar day not found: " + value, e);
        }
    }

    private boolean calendarIsVisible(String pickerId) {
        return Boolean.TRUE.equals(
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const picker=document.getElementById(arguments[0]),calendars=[...document.querySelectorAll('.b-calendar')];"
                                        + "return calendars.some(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                                        + "||calendars.some(c=>c.getClientRects().length);",
                                pickerId));
    }

    private String calendarCaption(String pickerId) {
        Object value =
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const picker=document.getElementById(arguments[0]),calendars=[...document.querySelectorAll('.b-calendar')];"
                                        + "const calendar=calendars.find(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                                        + "||calendars.find(c=>c.getClientRects().length);"
                                        + "const caption=calendar&&calendar.querySelector('.b-calendar-grid-caption');"
                                        + "return caption?(caption.innerText||caption.textContent||'').trim():'';");
        return value == null ? "" : value.toString();
    }

    static int parseMonth(String text) {
        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("(\\d{1,2})[/-]").matcher(text);
        if (matcher.find()) return Integer.parseInt(matcher.group(1));
        String normalized = text.replaceAll("\\d", "").trim();
        String[] arabicMonths = {
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر",
            "أكتوبر", "نوفمبر", "ديسمبر"
        };
        for (int i = 0; i < arabicMonths.length; i++)
            if (normalized.contains(arabicMonths[i])) return i + 1;
        String english = normalized.replaceAll("[^A-Za-z]", "").toUpperCase(Locale.ROOT);
        for (java.time.Month month : java.time.Month.values()) {
            if (english.equals(month.name())
                    || english.equals(month.name().substring(0, 3))) return month.getValue();
        }
        throw new IllegalArgumentException("Calendar month not found: " + text);
    }
}
