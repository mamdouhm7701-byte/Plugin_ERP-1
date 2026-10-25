package Plugin_ERP.System_Modules.Login.StoreModel;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Line editing and displayed totals for this document form; the page retains its public API. */
final class StoreDocumentLines {
    private final StoreModulePage page;
    private final WebDriver driver;
    private final WebDriverWait ready;

    StoreDocumentLines(StoreModulePage page, WebDriver driver, WebDriverWait ready) {
        this.page = page;
        this.driver = driver;
        this.ready = ready;
    }

    private Object js(String script, Object... args) {
        return page.js(script, args);
    }

    private By line(String name) {
        return By.xpath("//form//tbody/tr[.//td[contains(normalize-space(),'" + name + "')]]");
    }

    int smLineCount(String name) {
        return (int)
                driver.findElements(line(name)).stream().filter(WebElement::isDisplayed).count();
    }

    Map<String, String> smLineValues(String name) {
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

    void smQuantity(String name, String quantity) {
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
                                    .filter(e -> e.getText().trim().equals("الكمية"))
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
                        e.sendKeys(Keys.CONTROL + "a");
                        e.sendKeys(quantity);
                        e.sendKeys(Keys.TAB);
                        return new java.math.BigDecimal(e.getDomProperty("value"))
                                .compareTo(new java.math.BigDecimal(quantity)) == 0;
                    }
                    return false;
                });
    }

    Map<String, String> smAwaitInvalidQuantity(String name) {
        return ready.until(d -> {
            for (WebElement row : d.findElements(line(name))) {
                if (!row.isDisplayed()) continue;
                for (WebElement input : row.findElements(By.xpath(
                        "./td[@aria-colindex=ancestor::table/thead/tr/th[normalize-space(.)='الكمية']/@aria-colindex]//input[not(@type='hidden')]"))) {
                    if (!input.isDisplayed()) continue;
                    String state = input.getDomAttribute("aria-invalid");
                    String classes = input.getDomAttribute("class");
                    if ("true".equals(state) && classes != null
                            && Arrays.asList(classes.split("\\s+")).contains("is-invalid")) {
                        return Map.of("quantity", input.getDomProperty("value"),
                                "aria-invalid", state, "class", classes,
                                "title", Objects.toString(input.getDomAttribute("title"), ""));
                    }
                }
            }
            return null;
        });
    }

    void smRemoveLine(String name) {
        page.smClick(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//*[local-name()='svg' and contains(@class,'feather-trash')]"));
        ready.until(d -> page.smLineCount(name) == 0);
    }
}
