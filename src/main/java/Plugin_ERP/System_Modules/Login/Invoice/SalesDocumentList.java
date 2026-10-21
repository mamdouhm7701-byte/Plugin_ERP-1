package Plugin_ERP.System_Modules.Login.Invoice;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** List filters, readiness and owned row checks; form operations stay in the page. */
final class SalesDocumentList {
    private final SalesModulePage page;
    private final WebDriver driver;
    private final WebDriverWait ready;

    SalesDocumentList(SalesModulePage page, WebDriver driver, WebDriverWait ready) {
        this.page = page;
        this.driver = driver;
        this.ready = ready;
    }

    void slListScope(String customer) {
        page.slOpen("invoice-list");
        slAwaitListReady();
        page.slButton("عرض التصفية");
        page.slChoose("العميل", customer);
        slApplyListFilters();
    }

    void slAwaitListReady() {
        ready.until(d -> slListReady());
    }

    private boolean slListReady() {
        return Boolean.TRUE.equals(
                page.js(
                        "const q=[document.querySelector('#app').__vue__];let table,owner;while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$options.methods?.invoiceProvider)owner=v;if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page'))table=v;}return Boolean(table&&owner&&!table.computedBusy&&!owner.isTableBusy&&performance.getEntriesByType('resource').some(r=>{try{return /\\/SalesTransactions$/i.test(new URL(r.name).pathname)}catch(e){return false}}))"));
    }

    void slApplyListFilters() {
        slAwaitListReady();
        ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(By.cssSelector("input[name='filteredCode']")))
                        if (input.isDisplayed() && input.isEnabled()) {
                            input.sendKeys(Keys.ENTER);
                            return true;
                        }
                    return false;
                });
        slAwaitListReady();
    }

    String slListFixtureDate() {
        page.slOpen("invoice-list");
        page.slButton("عرض التصفية");
        var from =
                java.time.LocalDate.parse(
                        driver.findElement(By.cssSelector("input[name='fromDate']"))
                                .getDomProperty("value")
                                .substring(0, 10));
        var to =
                java.time.LocalDate.parse(
                        driver.findElement(By.cssSelector("input[name='toDate']"))
                                .getDomProperty("value")
                                .substring(0, 10));
        if (to.isBefore(from)) throw new AssertionError("Default sales list date range is invalid");
        var today = java.time.LocalDate.now();
        return (today.isBefore(from) ? from : today.isAfter(to) ? to : today).toString();
    }

    List<Map<String, Object>> slListRows() {
        return (List<Map<String, Object>>)
                page.js(
                        "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page')&&Array.isArray(v.$data.localItems))return JSON.parse(JSON.stringify(v.$data.localItems));}return []");
    }

    void slWaitListIds(Set<Object> expected) {
        ready.until(
                d -> {
                    if (!slListReady()) return false;
                    var actual = new HashSet<Object>();
                    for (var row : slListRows()) actual.add(row.get("id"));
                    return actual.equals(expected);
                });
    }

    void slListText(String name, String text) {
        slAwaitListReady();
        page.slType(By.cssSelector("input[name='" + name + "']"), text, false);
        slApplyListFilters();
    }

    void slListRadio(String group, String value) {
        String anchor = group.equals("status") ? "2" : "sales";
        page.slClick(
                By.xpath(
                        "//div[@role='radiogroup' and .//input[@value='"
                                + anchor
                                + "']]//input[@value='"
                                + value
                                + "']/ancestor::label[1]"));
    }

    List<String> slHeaders() {
        return driver.findElements(By.cssSelector("table thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    void slExport(String format) {
        page.slClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }
}
