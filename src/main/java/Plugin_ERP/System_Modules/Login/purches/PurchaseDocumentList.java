/* شرح الملف: مساعدة للبحث في مستندات الشراء وترتيبها وتصفحها وتصديرها. فائدتها مراجعة المشتريات
 * المسجلة والوصول للمستند المطلوب.
 */
package Plugin_ERP.System_Modules.Login.purches;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class PurchaseDocumentList {
    private final PurchaseListPage page;
    private final WebDriver driver;
    private final WebDriverWait ready;

    PurchaseDocumentList(PurchaseListPage page, WebDriver driver, WebDriverWait ready) {
        this.page = page;
        this.driver = driver;
        this.ready = ready;
    }

    void puListScope(String customer) {
        page.puOpen("purch-invoice-list");
        puAwaitListReady();
        page.puButton("عرض التصفية");
        page.puChoose("المورد", customer);
        puApplyListFilters();
    }

    void puAwaitListReady() {
        ready.until(d -> puListReady());
    }

    private boolean puListReady() {
        return Boolean.TRUE.equals(
                page.js(
                        "const q=[erpVue.root()];let table,owner;while(q.length){const v=q.shift();q.push(...(erpVue.children(v)||[]));if(v.$options.methods?.invoiceProvider)owner=v;if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page'))table=v;}return Boolean(table&&owner&&!table.computedBusy&&!owner.isTableBusy&&performance.getEntriesByType('resource').some(r=>{try{return /\\/PurchaseLists$/i.test(new URL(r.name).pathname)}catch(e){return false}}))"));
    }

    void puApplyListFilters() {
        puAwaitListReady();
        ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(By.cssSelector("input[name='codeFilter']")))
                        if (input.isDisplayed() && input.isEnabled()) {
                            input.sendKeys(Keys.ENTER);
                            return true;
                        }
                    return false;
                });
        puAwaitListReady();
    }

    String puListFixtureDate() {
        page.puOpen("purch-invoice-list");
        puAwaitListReady();
        page.puButton("عرض التصفية");
        ready.until(
                d ->
                        !d.findElements(By.cssSelector("input[name='fromDate']")).isEmpty()
                                && !d.findElements(By.cssSelector("input[name='toDate']"))
                                        .isEmpty());
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
        if (to.isBefore(from))
            throw new AssertionError("Default purchase list date range is invalid");
        var today = java.time.LocalDate.now();
        return (today.isBefore(from) ? from : today.isAfter(to) ? to : today).toString();
    }

    List<Map<String, Object>> puListRows() {
        return (List<Map<String, Object>>)
                page.js(
                        "const q=[erpVue.root()];while(q.length){const v=q.shift();q.push(...(erpVue.children(v)||[]));if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page')&&Array.isArray(v.$data.localItems))return JSON.parse(JSON.stringify(v.$data.localItems));}return []");
    }

    void puWaitListIds(Set<Object> expected) {
        ready.until(
                d -> {
                    if (!puListReady()) return false;
                    var actual = new HashSet<Object>();
                    for (var row : puListRows()) actual.add(row.get("id"));
                    return actual.equals(expected);
                });
    }

    void puListText(String name, String text) {
        puAwaitListReady();
        if (name.equals("fromDate") || name.equals("toDate")) {
            page.puDateFilter(name, text);
            return;
        }
        page.puType(
                By.cssSelector(
                        "input[name='"
                                + (name.equals("filteredCode") ? "codeFilter" : name)
                                + "']"),
                text,
                false);
        puApplyListFilters();
    }

    void puListRadio(String group, String value) {
        String anchor = group.equals("status") ? "2" : "sales";
        page.puClick(
                By.xpath(
                        "//div[@role='radiogroup' and .//input[@value='"
                                + anchor
                                + "']]//input[@value='"
                                + value
                                + "']/ancestor::label[1]"));
    }

    List<String> puHeaders() {
        return driver.findElements(By.cssSelector("table thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    void puPageSize(int size) {
        ready.until(
                d -> {
                    for (WebElement select : d.findElements(By.tagName("select")))
                        if (select.isDisplayed()
                                && new Select(select)
                                        .getOptions().stream()
                                                .anyMatch(
                                                        o ->
                                                                o.getDomProperty("value")
                                                                        .equals(
                                                                                Integer.toString(
                                                                                        size)))) {
                            new Select(select).selectByValue(Integer.toString(size));
                            return true;
                        }
                    return false;
                });
        puAwaitListReady();
    }

    void puNextPage() {
        page.puClick(
                By.cssSelector(
                        "button.page-link[aria-label='Go to next page'],a.page-link[aria-label='Go to next page']"));
    }

    void puFirstPage() {
        page.puClick(
                By.cssSelector(
                        "button.page-link[aria-label='Go to first page'],a.page-link[aria-label='Go to first page']"));
    }

    void puSort(String label) {
        page.puClick(
                By.xpath(
                        "//table//th[.//*[normalize-space()='"
                                + label
                                + "'] or normalize-space()='"
                                + label
                                + "']"));
        puAwaitListReady();
    }

    void puExport(String format) {
        page.puClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }
}
