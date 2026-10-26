package Plugin_ERP.System_Modules.Login.Coverage;

import Plugin_ERP.Util.FormInteractions;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Native UI primitives for the newly covered screens. No component-state writes. */
public class ScreenPage {
    protected final WebDriver driver;
    protected final WebDriverWait ready;
    protected final FormInteractions interactions;

    public ScreenPage(WebDriver driver) {
        this.driver = driver;
        ready = new WebDriverWait(driver, Duration.ofSeconds(30));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        interactions =
                new FormInteractions(
                        ready,
                        e ->
                                ((JavascriptExecutor) driver)
                                        .executeScript(
                                                "arguments[0].scrollIntoView({block:'center',inline:'nearest'})",
                                                e));
    }

    public static String literal(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    @Step("فتح الشاشة {route} والتحقق من عنوانها {title}")
    public void open(String route, String title) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route);
        String destination =
                PropertyReader.getApplicationUrl().replaceAll("/+$", "")
                        + "/#/"
                        + route.replaceAll("^/+", "");
        if (driver.getCurrentUrl()
                .replaceAll("/+$", "")
                .equals(destination.replaceAll("/+$", ""))) {
            // Remount through a real SPA route. A full reload races application/branch hydration.
            boolean tables = route.replaceAll("^/+|/+$", "").equals("tables");
            open(tables ? "accountGroups" : "tables", tables ? "مجموعات الحسابات" : "الطاولات");
            driver.get(destination);
        } else driver.get(destination);
        ready.until(
                d ->
                        d.findElements(By.cssSelector("h4.content-header-title")).stream()
                                .anyMatch(
                                        e -> e.isDisplayed() && e.getText().trim().equals(title)));
        // The breadcrumb updates before the asynchronous route component is mounted.
        // Read router readiness so "New" cannot target the previous screen's button.
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .withMessage("Route body is not mounted: " + route)
                .until(
                        d ->
                                Boolean.TRUE.equals(
                                        ((JavascriptExecutor) d)
                                                .executeScript(
                                                        """
            const root=document.querySelector('#app')?.__vue__,route=root?.$router?.currentRoute;
            const expected='/'+arguments[0].replace(/^\\/+|\\/+$/g,'');
            if(!route||route.path.replace(/\\/+$/g,'')!==expected)return false;
            const matched=route.matched[route.matched.length-1],vm=matched?.instances?.default;
            return !!(vm&&vm._isMounted&&!vm._isDestroyed&&vm.$el?.isConnected&&vm.$el.getClientRects?.().length);
            """,
                                                        route)));
        settled();
    }

    public void settled() {
        ready.until(
                d ->
                        d.findElements(By.cssSelector("div.b-overlay,.loading-bg")).stream()
                                .noneMatch(WebElement::isDisplayed));
    }

    public void click(By locator) {
        interactions.click(locator);
    }

    public By button(String text) {
        return By.xpath("//button[normalize-space(.)=" + literal(text) + "]");
    }

    @Step("الضغط على {text}")
    public void click(String text) {
        if (!text.equals("جديد")) {
            click(button(text));
            return;
        }
        ready.until(
                d -> {
                    WebElement view =
                            (WebElement)
                                    ((JavascriptExecutor) d)
                                            .executeScript(
                                                    """
                const r=document.querySelector('#app')?.__vue__?.$router?.currentRoute;
                return r?.matched[r.matched.length-1]?.instances?.default?.$el||null;
                """);
                    if (view == null) return false;
                    for (WebElement element : view.findElements(By.tagName("button")))
                        if (element.isDisplayed()
                                && element.isEnabled()
                                && element.getText().trim().equals(text)) {
                            ((JavascriptExecutor) d)
                                    .executeScript(
                                            "arguments[0].scrollIntoView({block:'center',behavior:'instant'})",
                                            element);
                            element.click();
                            return true;
                        }
                    return false;
                });
    }

    public boolean visible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    public WebElement visibleElement(By locator) {
        return ready.until(
                d ->
                        d.findElements(locator).stream()
                                .filter(WebElement::isDisplayed)
                                .findFirst()
                                .orElse(null));
    }

    public String value(By locator) {
        return visibleElement(locator).getDomProperty("value");
    }

    @Step("إدخال القيمة في الحقل {locator}")
    public void type(By locator, String value) {
        interactions.type(locator, value, true, FormInteractions.InputMode.CLICK_REPLACE);
    }

    public void field(String name, String value) {
        type(By.cssSelector("input[name='" + name + "']"), value);
    }

    public void notes(String marker) {
        type(By.cssSelector("textarea"), marker);
    }

    @Step("اختيار {name} من {label}")
    public void choose(String label, String name) {
        interactions.choose(
                By.xpath(
                        "//*[self::label or self::legend][normalize-space(.)="
                                + literal(label)
                                + "]/following::input[contains(@class,'vs__search')][1]"),
                name,
                FormInteractions.OptionMatch.EXACT_THEN_CONTAINS);
    }

    public void clearChoice(String label) {
        click(
                By.xpath(
                        "//*[self::label or self::legend][normalize-space(.)="
                                + literal(label)
                                + "]/following::*[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]//button[contains(@class,'vs__clear')]"));
    }

    public void check(String label, boolean selected) {
        WebElement input =
                driver.findElement(
                        By.xpath(
                                "//*[self::label or self::legend][normalize-space(.)="
                                        + literal(label)
                                        + "]/following::input[@type='checkbox'][1]"));
        Plugin_ERP.Util.NativeCheckbox.set(driver, input, selected);
        if (input.isSelected() != selected)
            throw new AssertionError("Checkbox state was not retained: " + label);
    }

    public void confirm(boolean yes) {
        click(By.cssSelector(yes ? ".swal2-confirm" : ".swal2-cancel"));
        ready.until(d -> !visible(By.cssSelector(".swal2-popup")));
    }

    public boolean confirmationVisible() {
        return visible(By.cssSelector(".swal2-popup"));
    }

    public void dismissSuccess() {
        if (visible(By.cssSelector(".swal2-popup .swal2-confirm")))
            click(By.cssSelector(".swal2-confirm"));
    }

    public void closeModal() {
        click(By.cssSelector(".modal.show button.close"));
    }

    public By row(String text) {
        return By.xpath("//tr[td[normalize-space(.)=" + literal(text) + "]]");
    }

    public void rowAction(String text, String action) {
        click(
                By.xpath(
                        "//tr[td[normalize-space(.)="
                                + literal(text)
                                + "]]//button[@data-action-type="
                                + literal(action)
                                + "]"));
    }

    public void treeNode(String name) {
        treeNode(null, name);
    }

    public By treeLabel(Object id) {
        return By.xpath("//li//label[@for=" + literal(id.toString()) + "]");
    }

    public void expandTreeNode(Object id) {
        ready.until(
                d -> {
                    for (WebElement label : d.findElements(treeLabel(id))) {
                        if (!label.isDisplayed()) continue;
                        WebElement toggle =
                                label.findElement(
                                        By.xpath("../span[contains(@class,'toggle-icon')]"));
                        if (!toggle.findElements(By.xpath("./*[contains(@class,'fa-caret-down')]"))
                                .isEmpty()) return true;
                        if (!toggle.findElements(By.xpath("./*[contains(@class,'fa-caret-right')]"))
                                .isEmpty()) {
                            ((JavascriptExecutor) d)
                                    .executeScript(
                                            "arguments[0].scrollIntoView({block:'center',behavior:'instant'})",
                                            label);
                            label.click();
                        }
                    }
                    return false;
                });
    }

    public void treeNode(Object id, String name) {
        By node =
                id == null
                        ? By.xpath(
                                "//li//label[contains(@class,'tree-text')][contains(normalize-space(.),"
                                        + literal(name)
                                        + ")]")
                        : treeLabel(id);
        ready.until(
                d -> {
                    for (WebElement target : d.findElements(node)) {
                        if (target.isDisplayed()) {
                            target.click();
                            return true;
                        }
                        for (WebElement parent :
                                target.findElements(
                                        By.xpath("ancestor::li[contains(@class,'tree-node')]"))) {
                            if (!parent.isDisplayed()
                                    || parent.findElements(
                                                    By.xpath(
                                                            "./span[contains(@class,'toggle-icon')]/*[contains(@class,'fa-caret-right')]"))
                                            .isEmpty()) continue;
                            WebElement label =
                                    parent.findElement(
                                            By.xpath("./label[contains(@class,'tree-text')]"));
                            ((JavascriptExecutor) d)
                                    .executeScript(
                                            "arguments[0].scrollIntoView({block:'center',behavior:'instant'})",
                                            label);
                            label.click();
                            return false;
                        }
                    }
                    return false;
                });
        ready.until(
                d ->
                        d.findElements(By.cssSelector("input[name='arabicName']")).stream()
                                .anyMatch(
                                        e ->
                                                e.isDisplayed()
                                                        && name.equals(e.getDomProperty("value"))));
    }

    public String bodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    public boolean validationVisible() {
        return driver
                .findElements(
                        By.cssSelector(
                                "small.text-danger,.invalid-feedback,.text-danger,.swal2-html-container"))
                .stream()
                .anyMatch(e -> e.isDisplayed() && !e.getText().isBlank());
    }

    public void search(String term) {
        WebElement input =
                visibleElement(
                        By.cssSelector(
                                "input[placeholder='بحث ....'],input[placeholder='بحث ...']"));
        type(
                By.xpath(
                        "//input[@placeholder="
                                + literal(input.getDomAttribute("placeholder"))
                                + "]"),
                term);
        input.sendKeys(Keys.ENTER);
        settled();
    }

    @Step("إضافة الصنف المملوك {name} من نافذة الأصناف")
    public void addItem(String name) {
        By add =
                By.xpath(
                        "//button[normalize-space(.)='اضافة اصناف' or normalize-space(.)='إضافة أصناف']");
        click(add);
        By search =
                By.cssSelector(
                        ".modal.show input[name='arabicName'],.modal.show input[label-text='arabicName']");
        type(search, name);
        visibleElement(search).sendKeys(Keys.ENTER);
        click(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(.),"
                                + literal(name)
                                + ")]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        By close =
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space(.)='إغلاق']");
        if (visible(close)) click(close);
        else
            click(
                    By.xpath(
                            "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space(.)='حفظ']"));
        ready.until(d -> !visible(By.cssSelector(".modal.show")));
    }

    public void tableLine(String name, String heading, String value) {
        WebElement row =
                visibleElement(
                        By.xpath(
                                "//tbody/tr[.//*[contains(normalize-space(text()),"
                                        + literal(name)
                                        + ")]]"));
        WebElement table = row.findElement(By.xpath("ancestor::table"));
        List<WebElement> headers = table.findElements(By.cssSelector("thead tr:first-child th"));
        int index = -1;
        for (int i = 0; i < headers.size(); i++)
            if (headers.get(i).getText().trim().equals(heading)) {
                index = i;
                break;
            }
        if (index < 0) throw new AssertionError("Missing column " + heading);
        WebElement input =
                row.findElements(By.cssSelector("td"))
                        .get(index)
                        .findElement(By.cssSelector("input"));
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), value, Keys.TAB);
        if (!value.equals(input.getDomProperty("value")))
            throw new AssertionError("Line input did not retain " + value);
    }
}
