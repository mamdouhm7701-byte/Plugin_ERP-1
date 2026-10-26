package Plugin_ERP.System_Modules.Login.GenralAccountant;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Focused UI component; public page entry points and their Allure steps stay on the page. */
final class VoucherAccountPicker {
    private final VouchersPage page;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;
    private final Plugin_ERP.Bots.WaitManagement waitManagement;

    VoucherAccountPicker(
            VouchersPage page,
            WebDriver driver,
            Plugin_ERP.Bots.ElementActions utilites,
            Plugin_ERP.Bots.WaitManagement waitManagement) {
        this.page = page;
        this.driver = driver;
        this.utilites = utilites;
        this.waitManagement = waitManagement;
    }

    private static final By ADD_ACCOUNT_BUTTON =
            By.xpath("//form//button[normalize-space()='اضافة حساب']");
    private static final By ACCOUNT_DIALOG = By.cssSelector("div[role='dialog']");
    private static final By ACCOUNT_DIALOG_SEARCH =
            By.cssSelector("div[role='dialog'] input[placeholder='بحث ....']");
    private static final By ACCOUNT_DIALOG_CLOSE =
            By.cssSelector("div[role='dialog'] button[aria-label='Close']");

    void openAccountPicker() {
        utilites.ClickElement(ADD_ACCOUNT_BUTTON);
        waitManagement.fluentWait().until(d -> d.findElement(ACCOUNT_DIALOG).isDisplayed());
    }

    void searchAccount(String query) {
        WebElement input =
                waitManagement
                        .fluentWait()
                        .until(
                                org.openqa.selenium.support.ui.ExpectedConditions
                                        .elementToBeClickable(ACCOUNT_DIALOG_SEARCH));
        input.click();
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        input.sendKeys(query);
        waitManagement.fluentWait().until(d -> query.equals(input.getDomProperty("value")));
    }

    int getAccountSearchResultCount() {
        return driver.findElements(
                        By.xpath(
                                "//div[@role='dialog']//table/tbody/tr[not(contains(@class,'b-table-empty-row'))]"))
                .size();
    }

    boolean accountSearchContains(String text) {
        return driver.findElements(By.cssSelector("div[role='dialog'] table tbody")).stream()
                .anyMatch(e -> e.getText().contains(text));
    }

    int getAccountPickerRowCount() {
        return page.getAccountSearchResultCount();
    }

    void selectAccountByCode(String accountCode) {
        page.openAccountPicker();
        page.searchAccount(accountCode);
        By accountRowAddButton =
                By.xpath(
                        "//div[@role='dialog']//table/tbody/tr[td[1][normalize-space()='"
                                + accountCode
                                + "']]//button");
        utilites.ClickElement(accountRowAddButton);
        // The picker stays open after adding an account; close it before interacting with the form.
        waitManagement.fluentWait().until(d -> !d.findElements(accountRowAddButton).isEmpty());
        page.closeAccountPicker();
    }

    void searchAccountAndSelectFirst(String query) {
        page.openAccountPicker();
        page.searchAccount(query);
        waitManagement.fluentWait().until(d -> page.getAccountSearchResultCount() > 0);
        WebElement row =
                driver.findElement(
                        By.xpath(
                                "//div[@role='dialog']//table/tbody/tr[not(contains(@class,'b-table-empty-row'))][1]"));
        String summary = row.getText();
        row.findElement(By.cssSelector("button")).click();
        page.closeAccountPicker();
        io.qameta.allure.Allure.step("Selected account from VAT search: " + summary);
    }

    void closeAccountPicker() {
        utilites.ClickElement(ACCOUNT_DIALOG_CLOSE);
        waitManagement.fluentWait().until(d -> d.findElements(ACCOUNT_DIALOG).isEmpty());
    }

    void openFirstAccountPicker() {
        By firstRowSearch =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search'])[1]");
        WebElement input = driver.findElement(firstRowSearch);
        input.click();
        waitManagement
                .fluentWait()
                .until(d -> !d.findElements(By.cssSelector("li.vs__dropdown-option")).isEmpty());
    }

    void selectAccountFromFirstRow(String accountText) {
        By option =
                By.xpath(
                        "(//li[contains(@class,'vs__dropdown-option') and contains(normalize-space(),\""
                                + accountText
                                + "\")])[1]");
        utilites.ClickElement(option);
    }

    boolean isAccountDropdownSearchPresent() {
        return !driver.findElements(
                        By.xpath(
                                "//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search']"))
                .isEmpty();
    }

    void assertLineAccountSearch(String matchingCode, String otherCode) {
        By search =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search'])[1]");
        WebElement input =
                waitManagement
                        .fluentWait()
                        .until(
                                org.openqa.selenium.support.ui.ExpectedConditions
                                        .elementToBeClickable(search));
        input.click();
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        input.sendKeys(matchingCode);
        waitManagement
                .fluentWait()
                .until(
                        d -> {
                            var options =
                                    d
                                            .findElements(By.cssSelector("li.vs__dropdown-option"))
                                            .stream()
                                            .filter(WebElement::isDisplayed)
                                            .map(WebElement::getText)
                                            .toList();
                            return !options.isEmpty()
                                    && options.stream()
                                            .allMatch(text -> text.contains(matchingCode))
                                    && options.stream().noneMatch(text -> text.contains(otherCode));
                        });
        input.sendKeys(org.openqa.selenium.Keys.ESCAPE);
    }
}
