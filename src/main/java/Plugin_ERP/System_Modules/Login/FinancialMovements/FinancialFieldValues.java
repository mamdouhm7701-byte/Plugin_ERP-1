package Plugin_ERP.System_Modules.Login.FinancialMovements;

import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Numeric/date entry and validation for financial forms. */
final class FinancialFieldValues {
    private final FinancialMovementPage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    FinancialFieldValues(FinancialMovementPage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    private void click(By locator) {
        owner.clickControl(locator);
    }

    void outsideFiscalYear() {
        String date =
                (String)
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                """
      const root=erpVue.root(), q=[root];
      while(q.length){const vm=q.shift();q.push(...(erpVue.children(vm)||[]));if(vm.currentYear?.startDate){
        return String(vm.currentYear.startDate).slice(0,10);}}
      throw new Error('Active fiscal year not available');
      """);
        WebElement input = driver.findElement(By.cssSelector("input[placeholder='YYYY-MM-DD']"));
        java.time.LocalDate previous = java.time.LocalDate.parse(date).minusDays(1);
        String current = input.getDomProperty("value").replaceAll("[\\p{Cf}]", "");
        String separator = current.contains("/") ? "/" : "-";
        String pattern =
                current.split("[/\\-]")[0].length() == 4
                        ? "yyyy" + separator + "MM" + separator + "dd"
                        : "dd" + separator + "MM" + separator + "yyyy";
        type(
                By.cssSelector("input[placeholder='YYYY-MM-DD']"),
                previous.format(java.time.format.DateTimeFormatter.ofPattern(pattern)));
    }

    void type(By locator, String value) {
        ready.until(
                d -> {
                    WebElement e =
                            d.findElements(locator).stream()
                                    .filter(WebElement::isDisplayed)
                                    .filter(WebElement::isEnabled)
                                    .filter(v -> v.getDomAttribute("readonly") == null)
                                    .findFirst()
                                    .orElse(null);
                    if (e == null) return false;
                    Plugin_ERP.Util.VueComponentAccess.execute(
                            d, "arguments[0].scrollIntoView({block:'center'})", e);
                    e.click();
                    e.sendKeys(Keys.CONTROL + "a");
                    if (value.isEmpty()) e.sendKeys(Keys.DELETE);
                    else e.sendKeys(value);
                    e.sendKeys(Keys.TAB);
                    String actual = e.getDomProperty("value");
                    if (value.equals(actual)) return true;
                    if ("YYYY-MM-DD".equals(e.getDomAttribute("placeholder"))) {
                        try {
                            return Plugin_ERP.Util.DocumentDates.parse(value)
                                    .equals(Plugin_ERP.Util.DocumentDates.parse(actual));
                        } catch (java.time.DateTimeException invalidDate) {
                            Plugin_ERP.Bots.LogUtils.debug(
                                    "FinancialMovementPage condition unavailable: "
                                            + invalidDate.getClass().getSimpleName());
                            return false;
                        }
                    }
                    if ("number".equals(e.getDomAttribute("type"))
                            || "value".equals(e.getDomAttribute("name")))
                        try {
                            return new BigDecimal(value).compareTo(new BigDecimal(actual)) == 0;
                        } catch (NumberFormatException ignored) {
                            Plugin_ERP.Bots.LogUtils.debug(
                                    "FinancialMovementPage condition unavailable: "
                                            + ignored.getClass().getSimpleName());
                            return false;
                        }
                    return false;
                });
    }

    BigDecimal[] taxValues() {
        WebElement net =
                driver.findElement(
                        By.cssSelector("input[name='netBeforeTax'],input[name='netValue']"));
        WebElement tax = driver.findElement(By.cssSelector("input[name='tax']"));
        WebElement total = driver.findElement(By.cssSelector("input[name='value']"));
        return new BigDecimal[] {
            new BigDecimal(net.getDomProperty("value")),
            new BigDecimal(tax.getDomProperty("value")),
            new BigDecimal(total.getDomProperty("value"))
        };
    }

    List<String> validationMessages() {
        return ready.until(
                d ->
                        d
                                .findElements(
                                        By.cssSelector(
                                                "small.text-danger,.invalid-feedback,.swal2-html-container,.swal2-title"))
                                .stream()
                                .filter(WebElement::isDisplayed)
                                .map(WebElement::getText)
                                .filter(t -> !t.isBlank())
                                .toList());
    }

    boolean validationFor(String field, String label) {
        String text = String.join(" ", validationMessages());
        return switch (field) {
            case "value", "negative" ->
                    text.contains("القيمة") || text.contains("قيمة") || text.contains("value");
            case "party" -> text.contains(label) || text.contains("اختيار");
            case "payment", "paymentOption" -> text.contains("الدفع") || text.contains("البنك");
            case "date" ->
                    text.contains("سنة")
                            || text.contains("السنة")
                            || text.contains("عام")
                            || text.contains("تاريخ");
            case "sameParty" -> text.contains("نفس");
            case "toParty" -> text.contains("مطلوب") || text.contains("اختيار");
            default -> false;
        };
    }

    void taxable(boolean value) {
        By checkbox = By.cssSelector("input[name='isTaxApplied']");
        WebElement input = driver.findElement(checkbox);
        if (input.isSelected() != value) {
            new Plugin_ERP.Bots.WaitActions(driver).waitForOverlayToDisappear();
            Plugin_ERP.Util.NativeCheckbox.set(driver, input, value);
        }
        ready.until(d -> d.findElement(checkbox).isSelected() == value);
    }
}
