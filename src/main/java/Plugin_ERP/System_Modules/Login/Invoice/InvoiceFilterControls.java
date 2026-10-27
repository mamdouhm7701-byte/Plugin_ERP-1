package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Focused InvoiceFilterControls operations extracted from InvoiceListFilters. */
final class InvoiceFilterControls {
    private final WebDriver driver;
    private final Runnable refresh;

    InvoiceFilterControls(WebDriver driver, Runnable refresh) {
        this.driver = driver;
        this.refresh = refresh;
    }

    void ensureInvoiceFiltersVisible() {
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(
                        d -> {
                            try {
                                WebElement show =
                                        d.findElements(By.cssSelector("button")).stream()
                                                .filter(InvoiceFilterControls::isDisplayed)
                                                .filter(
                                                        button ->
                                                                button.getText()
                                                                        .contains("عرض التصفية"))
                                                .findFirst()
                                                .orElse(null);
                                if (show != null) {
                                    ((org.openqa.selenium.JavascriptExecutor) d)
                                            .executeScript("arguments[0].click()", show);
                                    return true;
                                }
                                return d
                                        .findElements(
                                                By.cssSelector(
                                                        "input[name='filteredCode'], input[name='fromDate']"))
                                        .stream()
                                        .anyMatch(InvoiceFilterControls::isDisplayed);
                            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                                Plugin_ERP.Bots.LogUtils.debug(
                                        "InvoiceListFilters condition unavailable: "
                                                + ignored.getClass().getSimpleName());
                                return false;
                            }
                        });
    }

    void selectVueOption(String inputId, String value) {
        WebElement combo = driver.findElement(By.id(inputId));
        WebElement input = combo.findElement(By.cssSelector("input.vs__search"));
        By option =
                By.xpath(
                        "//*[contains(@class,'vs__dropdown-option') and normalize-space(.)='"
                                + escapeXPath(value)
                                + "']");
        if (driver.findElements(option).stream().noneMatch(InvoiceFilterControls::isDisplayed)) {
            clickVueDropdown(combo);
        }
        if (input.isDisplayed()
                && input.isEnabled()
                && !"true".equalsIgnoreCase(input.getAttribute("readonly"))) {
            try {
                String current = input.getAttribute("value");
                if (current == null || !current.contains(value)) {
                    input.sendKeys(
                            org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
                    input.sendKeys(value);
                }
            } catch (org.openqa.selenium.InvalidElementStateException ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "InvoiceFilterControls fallback: " + ignored.getClass().getSimpleName());

                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const input=arguments[0];input.value=arguments[1];"
                                        + "input.dispatchEvent(new Event('input',{bubbles:true}));"
                                        + "input.dispatchEvent(new Event('change',{bubbles:true}));",
                                input,
                                value);
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(option))
                .click();
    }

    String getVueSelectedValue(String inputId) {
        Object selected =
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const input=document.getElementById(arguments[0]);let root=input;"
                                        + "for(let i=0;root&&i<8;i++,root=root.parentElement){const item=root.querySelector('.vs__selected');"
                                        + "if(item)return (item.innerText||item.textContent||'').trim();}return '';",
                                inputId);
        return selected == null ? "" : selected.toString().trim();
    }

    void clickVueDropdown(WebElement input) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(
                        "const control=arguments[0],select=control.closest('.v-select');"
                                + "const toggle=(control.matches('.vs__dropdown-toggle')&&control)"
                                + "||(select&&select.querySelector('.vs__dropdown-toggle'));"
                                + "if(toggle){toggle.click();return true;}control.click();return true;",
                        input);
    }

    List<String> getRadioOptions(int groupIndex) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        if (groups.size() <= groupIndex) return List.of();
        return groups.get(groupIndex).findElements(By.cssSelector("label span")).stream()
                .filter(InvoiceFilterControls::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    void clickRadioOption(int groupIndex, String value) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        WebElement group = groups.get(groupIndex);
        WebElement label =
                group.findElements(By.cssSelector("label")).stream()
                        .filter(option -> option.getText().trim().equals(value))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Radio option not found: " + value));
        label.click();
        waitForGridRefresh();
    }

    String getSelectedRadioValue(int groupIndex) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        if (groups.size() <= groupIndex) return "";
        return groups.get(groupIndex).findElements(By.cssSelector("label.active span")).stream()
                .findFirst()
                .map(WebElement::getText)
                .map(String::trim)
                .orElse("");
    }

    WebElement vueFilter(String label) {
        return driver.findElement(
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::div[@role='combobox'][1]"));
    }

    String selectedVueFilter(String label) {
        return vueFilter(label).findElements(By.cssSelector(".vs__selected")).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .findFirst()
                .orElse("");
    }

    static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "InvoiceListFilters condition unavailable: "
                            + ignored.getClass().getSimpleName());
            return false;
        }
    }

    static String escapeXPath(String value) {
        return value.replace("'", "&apos;");
    }

    private void waitForGridRefresh() {
        refresh.run();
    }

    WebElement findFilterControl(String label) {
        ensureInvoiceFiltersVisible();
        String key = label == null ? "" : label.toLowerCase(Locale.ROOT);
        if (key.contains("مندوب") || key.contains("agent")) return vueFilter("مندوب");
        if (key.equals("العميل") || key.equals("customer")) return vueFilter("العميل");
        String selector =
                switch (key) {
                    case "رقم الجوال", "الجوال", "جوال", "الهاتف", "phone", "mobile" ->
                            "input[name='phone']";
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
        List<WebElement> labelled =
                driver.findElements(
                        By.xpath(
                                "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"
                                        + needle
                                        + "')]/following::input[1] | "
                                        + "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"
                                        + needle
                                        + "')]/following::select[1] | "
                                        + "//*[self::label or self::legend][contains(normalize-space(.), '"
                                        + escapeXPath(label)
                                        + "')]/following::*[self::input or self::select][1]"));
        WebElement control =
                labelled.stream()
                        .filter(InvoiceFilterControls::isDisplayed)
                        .findFirst()
                        .orElse(null);
        if (control != null) {
            return control;
        }

        List<WebElement> byPlaceholder =
                driver.findElements(
                        By.cssSelector(
                                "input[placeholder*='"
                                        + label
                                        + "'], select[aria-label*='"
                                        + label
                                        + "'], input[aria-label*='"
                                        + label
                                        + "']"));
        control =
                byPlaceholder.stream()
                        .filter(InvoiceFilterControls::isDisplayed)
                        .findFirst()
                        .orElse(null);
        if (control != null) {
            return control;
        }

        String normalized = label.toLowerCase(Locale.ROOT);
        List<WebElement> candidates = driver.findElements(By.cssSelector("input, select"));
        int fallbackIndex =
                switch (normalized) {
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
        List<WebElement> filterCandidates =
                candidates.stream().filter(InvoiceFilterControls::isDisplayed).toList();
        if (fallbackIndex >= 0 && fallbackIndex < filterCandidates.size()) {
            return filterCandidates.get(fallbackIndex);
        }
        throw new IllegalStateException("Invoice filter control not found for label: " + label);
    }

    String getInvoiceFilterValue(String label) {
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status"))
            return getSelectedRadioValue(1);
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type"))
            return getSelectedRadioValue(0);
        if (normalizedLabel.contains("مندوب") || normalizedLabel.contains("agent"))
            return selectedVueFilter("مندوب");
        if (normalizedLabel.contains("عميل") || normalizedLabel.contains("customer"))
            return selectedVueFilter("العميل");
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            return new Select(control).getFirstSelectedOption().getText().trim();
        }
        String value = control.getAttribute("value");
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        try {
            WebElement selected =
                    control.findElement(
                            By.xpath(
                                    "ancestor::fieldset[1]//input[@type='radio' and @checked]/following-sibling::*[1]"));
            return selected.getText().trim();
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "InvoiceListFilters condition unavailable: "
                            + ignored.getClass().getSimpleName());
            return "";
        }
    }
}
