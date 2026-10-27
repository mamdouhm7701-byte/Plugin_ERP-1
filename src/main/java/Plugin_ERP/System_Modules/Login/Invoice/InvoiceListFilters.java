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

/** Invoice-list filters, selected values and the pending filter baseline. */
final class InvoiceListFilters {
    private final InvoiceFilterControls filterControls;
    private final InvoicListPage owner;
    private final WebDriver driver;

    InvoiceListFilters(InvoicListPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        filterControls = new InvoiceFilterControls(driver, this::waitForGridRefresh);
    }

    private String pendingFilterGridBaseline;

    private String gridSnapshot() {
        return owner.filterGridSnapshot();
    }

    private void waitForGridRefresh(String before) {
        owner.awaitFilterGridRefresh(before);
    }

    private void waitForGridRefresh() {
        waitForGridRefresh(null);
    }

    private void setDateFilter(WebElement input, String value) {
        owner.setCalendarFilter(input, value);
    }

    private void slAwaitListReady() {
        owner.slAwaitListReady();
    }

    private void slChoose(String label, String value) {
        owner.slChoose(label, value);
    }

    private void slApplyListFilters() {
        owner.slApplyListFilters();
    }

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
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(
                        d -> {
                            try {
                                return expectedValue.equals(
                                        findFilterControl(label).getAttribute("value"));
                            } catch (org.openqa.selenium.StaleElementReferenceException
                                    | org.openqa.selenium.NoSuchElementException ignored) {
                                Plugin_ERP.Bots.LogUtils.debug(
                                        "InvoiceListFilters condition unavailable: "
                                                + ignored.getClass().getSimpleName());
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
        List<WebElement> matchingChoice =
                driver
                        .findElements(
                                By.xpath(
                                        "//fieldset[.//*[self::legend or self::label][contains(normalize-space(.), '"
                                                + escapeXPath(label)
                                                + "')]]"
                                                + "//label[normalize-space(.)='"
                                                + escapeXPath(value)
                                                + "' or .//*[normalize-space(.)='"
                                                + escapeXPath(value)
                                                + "']]"
                                                + " | //*[@role='radio' or @role='option'][normalize-space(.)='"
                                                + escapeXPath(value)
                                                + "']"))
                        .stream()
                        .filter(InvoiceListFilters::isDisplayed)
                        .toList();
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
            By option =
                    By.xpath(
                            "//*[(@role='option' or contains(@class,'vs__dropdown-option') or self::li) and normalize-space(.)='"
                                    + escapeXPath(value)
                                    + "']");
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.elementToBeClickable(option))
                    .click();
        }
    }

    List<String> getInvoiceFilterOptions(String label) {
        return owner.getInvoiceFilterOptions(label, "");
    }

    List<String> getInvoiceFilterOptions(String label, String query) {
        ensureInvoiceFiltersVisible();
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status"))
            return getRadioOptions(1);
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type"))
            return getRadioOptions(0);
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            return new Select(control)
                    .getOptions().stream().map(WebElement::getText).map(String::trim).toList();
        }
        if ("radio".equalsIgnoreCase(control.getAttribute("type"))
                || "checkbox".equalsIgnoreCase(control.getAttribute("type"))) {
            List<WebElement> choices =
                    control.findElements(By.xpath("ancestor::fieldset[1]//label"));
            if (!choices.isEmpty()) {
                return choices.stream()
                        .filter(InvoiceListFilters::isDisplayed)
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(text -> !text.isEmpty())
                        .toList();
            }
        }
        WebElement search = control.findElement(By.cssSelector("input.vs__search"));
        clickVueDropdown(control);
        String listboxId = control.getAttribute("aria-owns");
        By optionsLocator =
                listboxId == null || listboxId.isBlank()
                        ? By.cssSelector(".vs__dropdown-option, [role='option']")
                        : By.cssSelector(
                                "#"
                                        + listboxId
                                        + " .vs__dropdown-option, #"
                                        + listboxId
                                        + " [role='option']");
        boolean hasOptions =
                driver.findElements(optionsLocator).stream()
                        .anyMatch(InvoiceListFilters::isDisplayed);
        if (!hasOptions
                && !query.isBlank()
                && search.isDisplayed()
                && search.isEnabled()
                && !"true".equalsIgnoreCase(search.getAttribute("readonly"))) {
            try {
                search.sendKeys(query);
            } catch (org.openqa.selenium.InvalidElementStateException ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "InvoiceListFilters fallback: " + ignored.getClass().getSimpleName());

                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const input=arguments[0];input.value=arguments[1];"
                                        + "input.dispatchEvent(new Event('input',{bubbles:true}));"
                                        + "input.dispatchEvent(new Event('change',{bubbles:true}));",
                                search,
                                query);
            }
        }
        if (!hasOptions) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(
                                d ->
                                        d.findElements(optionsLocator).stream()
                                                .anyMatch(InvoiceListFilters::isDisplayed));
            } catch (org.openqa.selenium.TimeoutException ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "InvoiceListFilters fallback: " + ignored.getClass().getSimpleName());

                return List.of();
            }
        }
        String containerId = control.getAttribute("aria-owns");
        if (containerId != null && !containerId.isBlank()) {
            return driver
                    .findElements(
                            By.cssSelector(
                                    "#"
                                            + containerId
                                            + " li, #"
                                            + containerId
                                            + " [role='option']"))
                    .stream()
                    .filter(InvoiceListFilters::isDisplayed)
                    .map(WebElement::getText)
                    .map(String::trim)
                    .toList();
        }
        return driver
                .findElements(
                        By.cssSelector(".vs__dropdown-menu li, [role='listbox'] [role='option']"))
                .stream()
                .filter(InvoiceListFilters::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    String getInvoiceFilterValue(String label) {
        return filterControls.getInvoiceFilterValue(label);
    }

    WebElement vueFilter(String label) {
        return filterControls.vueFilter(label);
    }

    String selectedVueFilter(String label) {
        return filterControls.selectedVueFilter(label);
    }

    WebElement findFilterControl(String label) {
        return filterControls.findFilterControl(label);
    }

    void captureFilterGridBaseline() {
        if (pendingFilterGridBaseline == null) pendingFilterGridBaseline = gridSnapshot();
    }

    static boolean isDisplayed(WebElement element) {
        return InvoiceFilterControls.isDisplayed(element);
    }

    static String escapeXPath(String value) {
        return InvoiceFilterControls.escapeXPath(value);
    }

    void ensureInvoiceFiltersVisible() {
        filterControls.ensureInvoiceFiltersVisible();
    }

    void selectVueOption(String inputId, String value) {
        filterControls.selectVueOption(inputId, value);
    }

    String getVueSelectedValue(String inputId) {
        return filterControls.getVueSelectedValue(inputId);
    }

    void clickVueDropdown(WebElement input) {
        filterControls.clickVueDropdown(input);
    }

    List<String> getRadioOptions(int groupIndex) {
        return filterControls.getRadioOptions(groupIndex);
    }

    void clickRadioOption(int groupIndex, String value) {
        filterControls.clickRadioOption(groupIndex, value);
    }

    String getSelectedRadioValue(int groupIndex) {
        return filterControls.getSelectedRadioValue(groupIndex);
    }
}
