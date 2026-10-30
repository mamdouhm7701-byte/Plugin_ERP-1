/* شرح الملف: مساعدة لتعديل أصناف مستند البيع وكمياتها وقيمها وحذف البنود ومراجعة الإجماليات.
 * فائدتها ضبط تفاصيل البيع وصافي المستند.
 */
package Plugin_ERP.System_Modules.Login.Invoice;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

final class SalesDocumentLines {
    private final SalesModulePage page;
    private final WebDriver driver;
    private final WebDriverWait ready;

    SalesDocumentLines(SalesModulePage page, WebDriver driver, WebDriverWait ready) {
        this.page = page;
        this.driver = driver;
        this.ready = ready;
    }

    private Object js(String script, Object... args) {
        return page.js(script, args);
    }

    boolean slLineReadOnly(String name, String column) {
        WebElement row =
                ready.until(
                        d ->
                                d.findElements(line(name)).stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        String index =
                row
                        .findElement(By.xpath("ancestor::table[1]"))
                        .findElements(By.cssSelector("thead th"))
                        .stream()
                        .filter(e -> column.equals(e.getDomProperty("textContent").trim()))
                        .map(e -> e.getDomAttribute("aria-colindex"))
                        .findFirst()
                        .orElseThrow();
        var inputs =
                row.findElements(
                        By.cssSelector(
                                "td[aria-colindex='" + index + "'] input:not([type='hidden'])"));
        return !inputs.isEmpty()
                && inputs.stream()
                        .allMatch(e -> !e.isEnabled() || e.getDomAttribute("readonly") != null);
    }

    private By line(String name) {
        return By.xpath("//form//tbody/tr[.//td[contains(normalize-space(),'" + name + "')]]");
    }

    int slLineCount(String name) {
        return (int)
                driver.findElements(line(name)).stream().filter(WebElement::isDisplayed).count();
    }

    Map<String, String> slLineValues(String name) {
        return ready.until(
                d -> {
                    WebElement row =
                            d.findElements(line(name)).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return null;
                    Map<String, String> values = new LinkedHashMap<>();
                    for (WebElement header :
                            row.findElement(By.xpath("ancestor::table[1]"))
                                    .findElements(By.cssSelector("thead th"))) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", header);
                        String label = header.getText().trim();
                        if (label.isBlank()) continue;
                        List<WebElement> cells =
                                row.findElements(
                                        By.cssSelector(
                                                "td[aria-colindex='"
                                                        + header.getDomAttribute("aria-colindex")
                                                        + "']"));
                        if (cells.isEmpty()) continue;
                        WebElement cell = cells.get(0);
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", cell);
                        List<WebElement> inputs =
                                cell.findElements(By.cssSelector("input:not([type='hidden'])"));
                        values.put(
                                label,
                                inputs.isEmpty()
                                        ? cell.getText().trim()
                                        : inputs.get(0).getDomProperty("value"));
                    }
                    return values;
                });
    }

    void slQuantity(String name, String quantity) {
        ready.until(
                d -> {
                    WebElement row =
                            d.findElements(line(name)).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return false;
                    WebElement table = row.findElement(By.xpath("ancestor::table[1]"));
                    String col =
                            table.findElements(By.cssSelector("thead th")).stream()
                                    .filter(
                                            e ->
                                                    e.getDomProperty("textContent")
                                                            .trim()
                                                            .equals("الكمية"))
                                    .map(e -> e.getDomAttribute("aria-colindex"))
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new AssertionError("Quantity column missing"));
                    List<WebElement> inputs =
                            row.findElements(
                                    By.cssSelector(
                                            "td[aria-colindex='"
                                                    + col
                                                    + "'] input:not([type='hidden'])"));
                    for (WebElement e : inputs) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (!e.isDisplayed() || !e.isEnabled()) continue;
                        e.click();
                        e.getDomProperty("value");
                        e.sendKeys(Keys.chord(Keys.CONTROL, "a"), quantity, Keys.TAB);
                        return true;
                    }
                    return false;
                });
    }

    void slRemoveLine(String name) {
        page.slClick(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-trash')]]"));
        ready.until(d -> page.slLineCount(name) == 0);
    }

    void slSetLine(String name, String column, String value) {
        ready.until(
                d -> {
                    WebElement row =
                            d.findElements(line(name)).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return false;
                    WebElement table = row.findElement(By.xpath("ancestor::table[1]"));
                    String index =
                            table.findElements(By.cssSelector("thead th")).stream()
                                    .filter(
                                            e ->
                                                    e.getDomProperty("textContent")
                                                            .trim()
                                                            .equals(column))
                                    .map(e -> e.getDomAttribute("aria-colindex"))
                                    .findFirst()
                                    .orElseThrow(
                                            () ->
                                                    new AssertionError(
                                                            "Missing line column: " + column));
                    for (WebElement input :
                            row.findElements(
                                    By.cssSelector(
                                            "td[aria-colindex='"
                                                    + index
                                                    + "'] input:not([type='hidden'])"))) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", input);
                        if (input.isDisplayed() && input.isEnabled()) {
                            input.click();
                            input.getDomProperty("value");
                            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), value, Keys.TAB);
                            return true;
                        }
                    }
                    return false;
                });
    }

    Map<String, String> slTotals() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String label : List.of("الاجمالى", "خصم", "قبل الضريبة", "الضريبة", "الصافي")) {
            for (WebElement input :
                    driver.findElements(
                            By.xpath(
                                    "//span[contains(@class,'font-weight-bolder') and normalize-space()='"
                                            + label
                                            + "']/parent::div/following-sibling::div[1]//input"))) {
                if (input.isDisplayed()) {
                    result.put(label, input.getDomProperty("value"));
                    break;
                }
            }
        }
        return result;
    }

    void slWaitNet(String value) {
        try {
            ready.until(
                    d -> {
                        String actual = page.slTotals().get("الصافي");
                        return Plugin_ERP.Util.DecimalAmountComparison.matches(actual, value);
                    });
        } catch (org.openqa.selenium.TimeoutException failure) {
            throw new AssertionError(
                    "صافي السند غير مطابق: المتوقع "
                            + value
                            + "، الفعلي "
                            + page.slTotals().get("الصافي"),
                    failure);
        }
    }
}
