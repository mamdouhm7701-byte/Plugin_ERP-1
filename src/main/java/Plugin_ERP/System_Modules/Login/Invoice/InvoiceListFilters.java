package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Invoice-list filters, selected values and the pending filter baseline. */
final class InvoiceListFilters {
    private final InvoicListPage owner;
    private final WebDriver driver;
    InvoiceListFilters(InvoicListPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }
    private String pendingFilterGridBaseline;
    private String gridSnapshot() { return owner.filterGridSnapshot(); }
    private void waitForGridRefresh(String before) { owner.awaitFilterGridRefresh(before); }
    private void waitForGridRefresh() { waitForGridRefresh(null); }
    private void setDateFilter(WebElement input, String value) { owner.setCalendarFilter(input, value); }
    private void slAwaitListReady() { owner.slAwaitListReady(); }
    private void slChoose(String label, String value) { owner.slChoose(label, value); }
    private void slApplyListFilters() { owner.slApplyListFilters(); }

    void setInvoiceTextFilter(String label, String value) {
        slAwaitListReady();
        captureFilterGridBaseline();
        WebElement input = findFilterControl(label);
        if ("hidden".equalsIgnoreCase(input.getAttribute("type"))) {
            setDateFilter(input, value);
            return;
        }
        input.clear();
        if (value != null && !value.isEmpty()) {
            input.sendKeys(value);
        }
        String expectedValue = value == null ? "" : value;
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
            try {
                return expectedValue.equals(findFilterControl(label).getAttribute("value"));
            } catch (org.openqa.selenium.StaleElementReferenceException
                     | org.openqa.selenium.NoSuchElementException ignored) {
                return false;
            }
        });
    }

    void applyInvoiceFilters() {
        // Text filters use the screen's Enter handler to refresh the provider.
        slApplyListFilters();
        String baseline = pendingFilterGridBaseline;
        pendingFilterGridBaseline = null;
        waitForGridRefresh(baseline);
    }

    void selectInvoiceFilter(String label, String value) {
        captureFilterGridBaseline();
        ensureInvoiceFiltersVisible();
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status")) {
            String text = value.equals("مدفوع جزئي") ? "مدفوع جزء" : value;
            clickRadioOption(1, text);
            return;
        }
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type")) {
            clickRadioOption(0, value);
            return;
        }
        if (normalizedLabel.contains("مندوب") || normalizedLabel.contains("agent")) {
            slChoose("مندوب", value);
            return;
        }
        if (normalizedLabel.contains("عميل") || normalizedLabel.contains("customer")) {
            slChoose("العميل", value);
            return;
        }
        List<WebElement> matchingChoice = driver.findElements(By.xpath(
                "//fieldset[.//*[self::legend or self::label][contains(normalize-space(.), '" + escapeXPath(label) + "')]]"
                        + "//label[normalize-space(.)='" + escapeXPath(value) + "' or .//*[normalize-space(.)='" + escapeXPath(value) + "']]"
                        + " | //*[@role='radio' or @role='option'][normalize-space(.)='" + escapeXPath(value) + "']"
        )).stream().filter(InvoiceListFilters::isDisplayed).toList();
        if (!matchingChoice.isEmpty()) {
            matchingChoice.get(0).click();
            return;
        }
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            new Select(control).selectByVisibleText(value);
        } else {
            control.click();
            control.sendKeys(value);
            By option = By.xpath("//*[(@role='option' or contains(@class,'vs__dropdown-option') or self::li) and normalize-space(.)='" + escapeXPath(value) + "']");
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(ExpectedConditions.elementToBeClickable(option)).click();
        }
    }

    List<String> getInvoiceFilterOptions(String label) { return owner.getInvoiceFilterOptions(label, ""); }

    List<String> getInvoiceFilterOptions(String label, String query) {
        ensureInvoiceFiltersVisible();
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status")) return getRadioOptions(1);
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type")) return getRadioOptions(0);
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            return new Select(control).getOptions().stream().map(WebElement::getText).map(String::trim).toList();
        }
        if ("radio".equalsIgnoreCase(control.getAttribute("type"))
                || "checkbox".equalsIgnoreCase(control.getAttribute("type"))) {
            List<WebElement> choices = control.findElements(By.xpath("ancestor::fieldset[1]//label"));
            if (!choices.isEmpty()) {
                return choices.stream().filter(InvoiceListFilters::isDisplayed).map(WebElement::getText)
                        .map(String::trim).filter(text -> !text.isEmpty()).toList();
            }
        }
        WebElement search = control.findElement(By.cssSelector("input.vs__search"));
        clickVueDropdown(control);
        String listboxId = control.getAttribute("aria-owns");
        By optionsLocator = listboxId == null || listboxId.isBlank()
                ? By.cssSelector(".vs__dropdown-option, [role='option']")
                : By.cssSelector("#" + listboxId + " .vs__dropdown-option, #" + listboxId + " [role='option']");
        boolean hasOptions = driver.findElements(optionsLocator).stream().anyMatch(InvoiceListFilters::isDisplayed);
        if (!hasOptions && !query.isBlank() && search.isDisplayed() && search.isEnabled()
                && !"true".equalsIgnoreCase(search.getAttribute("readonly"))) {
            try {
                search.sendKeys(query);
            } catch (org.openqa.selenium.InvalidElementStateException ignored) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "const input=arguments[0];input.value=arguments[1];"
                                + "input.dispatchEvent(new Event('input',{bubbles:true}));"
                                + "input.dispatchEvent(new Event('change',{bubbles:true}));", search, query);
            }
        }
        if (!hasOptions) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                        d.findElements(optionsLocator).stream().anyMatch(InvoiceListFilters::isDisplayed));
            } catch (org.openqa.selenium.TimeoutException ignored) {
                return List.of();
            }
        }
        String containerId = control.getAttribute("aria-owns");
        if (containerId != null && !containerId.isBlank()) {
            return driver.findElements(By.cssSelector("#" + containerId + " li, #" + containerId + " [role='option']"))
                    .stream().filter(InvoiceListFilters::isDisplayed).map(WebElement::getText).map(String::trim).toList();
        }
        return driver.findElements(By.cssSelector(".vs__dropdown-menu li, [role='listbox'] [role='option']"))
                .stream().filter(InvoiceListFilters::isDisplayed).map(WebElement::getText).map(String::trim).toList();
    }

    String getInvoiceFilterValue(String label) {
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status")) return getSelectedRadioValue(1);
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type")) return getSelectedRadioValue(0);
        if (normalizedLabel.contains("مندوب") || normalizedLabel.contains("agent")) return selectedVueFilter("مندوب");
        if (normalizedLabel.contains("عميل") || normalizedLabel.contains("customer")) return selectedVueFilter("العميل");
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            return new Select(control).getFirstSelectedOption().getText().trim();
        }
        String value = control.getAttribute("value");
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        try {
            WebElement selected = control.findElement(By.xpath("ancestor::fieldset[1]//input[@type='radio' and @checked]/following-sibling::*[1]"));
            return selected.getText().trim();
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            return "";
        }
    }

    WebElement vueFilter(String label) {
        return driver.findElement(By.xpath("//*[self::label or self::legend][normalize-space()='" + label + "']/following::div[@role='combobox'][1]"));
    }

    String selectedVueFilter(String label) {
        return vueFilter(label).findElements(By.cssSelector(".vs__selected")).stream().map(WebElement::getText).map(String::trim).findFirst().orElse("");
    }

    WebElement findFilterControl(String label) {
        ensureInvoiceFiltersVisible();
        String key = label == null ? "" : label.toLowerCase(Locale.ROOT);
        if (key.contains("مندوب") || key.contains("agent")) return vueFilter("مندوب");
        if (key.equals("العميل") || key.equals("customer")) return vueFilter("العميل");
        String selector = switch (key) {
            case "رقم الجوال", "الجوال", "جوال", "الهاتف", "phone", "mobile" -> "input[name='phone']";
            case "اسم العميل", "customer name" -> "input[name='onTheFlyDealerName']";
            case "الملاحظات", "ملاحظات", "notes" -> "input[name='notes']";
            case "الكود", "code" -> "input[name='filteredCode']";
            case "رقم الطلب", "order number" -> "input[name='orderNumber']";
            case "من تاريخ", "from date" -> "input[name='fromDate']";
            case "إلى تاريخ", "الي تاريخ", "to date" -> "input[name='toDate']";
            default -> null;
        };
        if (selector != null) {
            List<WebElement> exact = driver.findElements(By.cssSelector(selector));
            if (!exact.isEmpty()) return exact.get(0);
        }
        if (key.contains("عميل") || key.contains("customer")) return vueFilter("العميل");
        String needle = escapeXPath(label.toLowerCase(Locale.ROOT));
        List<WebElement> labelled = driver.findElements(By.xpath(
                "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + needle + "')]/following::input[1] | "
                        + "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + needle + "')]/following::select[1] | "
                        + "//*[self::label or self::legend][contains(normalize-space(.), '" + escapeXPath(label) + "')]/following::*[self::input or self::select][1]"
        ));
        WebElement control = labelled.stream().filter(InvoiceListFilters::isDisplayed).findFirst().orElse(null);
        if (control != null) {
            return control;
        }

        List<WebElement> byPlaceholder = driver.findElements(By.cssSelector(
                "input[placeholder*='" + label + "'], select[aria-label*='" + label + "'], input[aria-label*='" + label + "']"));
        control = byPlaceholder.stream().filter(InvoiceListFilters::isDisplayed).findFirst().orElse(null);
        if (control != null) {
            return control;
        }

        String normalized = label.toLowerCase(Locale.ROOT);
        List<WebElement> candidates = driver.findElements(By.cssSelector("input, select"));
        int fallbackIndex = switch (normalized) {
            case "mobile", "phone", "رقم الجوال", "الجوال" -> 0;
            case "customer", "customer name", "العميل", "اسم العميل" -> 1;
            case "agent", "مندوب", "المندوب" -> 2;
            case "notes", "الملاحظات" -> 3;
            case "code", "الكود" -> 4;
            case "order number", "رقم الطلب" -> 5;
            case "from date", "من تاريخ" -> 6;
            case "to date", "إلى تاريخ" -> 7;
            case "payment status", "حالة السداد" -> 8;
            case "movement type", "نوع الحركة" -> 9;
            default -> -1;
        };
        List<WebElement> filterCandidates = candidates.stream().filter(InvoiceListFilters::isDisplayed).toList();
        if (fallbackIndex >= 0 && fallbackIndex < filterCandidates.size()) {
            return filterCandidates.get(fallbackIndex);
        }
        throw new IllegalStateException("Invoice filter control not found for label: " + label);
    }

    void captureFilterGridBaseline() {
        if (pendingFilterGridBaseline == null) pendingFilterGridBaseline = gridSnapshot();
    }

    static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }

    static String escapeXPath(String value) {
        return value.replace("'", "&apos;");
    }

    void ensureInvoiceFiltersVisible() {
        new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
            try {
                WebElement show = d.findElements(By.cssSelector("button")).stream()
                        .filter(InvoiceListFilters::isDisplayed)
                        .filter(button -> button.getText().contains("عرض التصفية"))
                        .findFirst().orElse(null);
                if (show != null) {
                    ((org.openqa.selenium.JavascriptExecutor) d).executeScript("arguments[0].click()", show);
                    return true;
                }
                return d.findElements(By.cssSelector("input[name='filteredCode'], input[name='fromDate']"))
                        .stream().anyMatch(InvoiceListFilters::isDisplayed);
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                return false;
            }
        });
    }

    void selectVueOption(String inputId, String value) {
        WebElement combo = driver.findElement(By.id(inputId));
        WebElement input = combo.findElement(By.cssSelector("input.vs__search"));
        By option = By.xpath("//*[contains(@class,'vs__dropdown-option') and normalize-space(.)='" + escapeXPath(value) + "']");
        if (driver.findElements(option).stream().noneMatch(InvoiceListFilters::isDisplayed)) {
            clickVueDropdown(combo);
        }
        if (input.isDisplayed() && input.isEnabled() && !"true".equalsIgnoreCase(input.getAttribute("readonly"))) {
            try {
                String current = input.getAttribute("value");
                if (current == null || !current.contains(value)) {
                input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
                input.sendKeys(value);
                }
            } catch (org.openqa.selenium.InvalidElementStateException ignored) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "const input=arguments[0];input.value=arguments[1];"
                                + "input.dispatchEvent(new Event('input',{bubbles:true}));"
                                + "input.dispatchEvent(new Event('change',{bubbles:true}));", input, value);
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(8)).until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    String getVueSelectedValue(String inputId) {
        Object selected = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const input=document.getElementById(arguments[0]);let root=input;"
                        + "for(let i=0;root&&i<8;i++,root=root.parentElement){const item=root.querySelector('.vs__selected');"
                        + "if(item)return (item.innerText||item.textContent||'').trim();}return '';", inputId);
        return selected == null ? "" : selected.toString().trim();
    }

    void clickVueDropdown(WebElement input) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const control=arguments[0],select=control.closest('.v-select');"
                        + "const toggle=(control.matches('.vs__dropdown-toggle')&&control)"
                        + "||(select&&select.querySelector('.vs__dropdown-toggle'));"
                        + "if(toggle){toggle.click();return true;}control.click();return true;", input);
    }

    List<String> getRadioOptions(int groupIndex) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        if (groups.size() <= groupIndex) return List.of();
        return groups.get(groupIndex).findElements(By.cssSelector("label span")).stream()
                .filter(InvoiceListFilters::isDisplayed).map(WebElement::getText).map(String::trim).toList();
    }

    void clickRadioOption(int groupIndex, String value) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        WebElement group = groups.get(groupIndex);
        WebElement label = group.findElements(By.cssSelector("label")).stream()
                .filter(option -> option.getText().trim().equals(value))
                .findFirst().orElseThrow(() -> new IllegalStateException("Radio option not found: " + value));
        label.click();
        waitForGridRefresh();
    }

    String getSelectedRadioValue(int groupIndex) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        if (groups.size() <= groupIndex) return "";
        return groups.get(groupIndex).findElements(By.cssSelector("label.active span")).stream()
                .findFirst().map(WebElement::getText).map(String::trim).orElse("");
    }
}
