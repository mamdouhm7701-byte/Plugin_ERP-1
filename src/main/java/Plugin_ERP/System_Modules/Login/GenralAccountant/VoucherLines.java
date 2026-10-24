package Plugin_ERP.System_Modules.Login.GenralAccountant;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/** Journal line amounts, notes, tax settlement and draft totals. */
final class VoucherLines {
    private final VouchersPage owner;
    private final WebDriver driver;
    VoucherLines(VouchersPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    int getSelectedLineCount() {
        return (int) driver.findElements(
                        By.xpath(
                                "//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!='']"))
                .stream().filter(WebElement::isDisplayed).count();
    }

    By lineAmountInput(int lineIndex, int columnIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        if (columnIndex != 3 && columnIndex != 4)
            throw new IllegalArgumentException("Amount column must be debit (3) or credit (4)");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]//td[@aria-colindex='"
                        + columnIndex
                        + "']//input[@type='number']");
    }

    By selectedLine(int lineIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]");
    }

    By lineElement(int lineIndex, String selector) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]//"
                        + selector);
    }

    int getSettlementCheckboxCount() {
        return driver.findElements(
                        By.cssSelector("form input[type='checkbox'], form [role='checkbox']"))
                .size();
    }

    void setSettlementChecked(boolean checked) {
        By locator = settlementCheckbox();
        WebElement checkbox = driver.findElement(locator);
        if (checkbox.isSelected() != checked) {
            String id = checkbox.getAttribute("id");
            List<WebElement> labels =
                    id == null || id.isBlank()
                            ? List.of()
                            : driver.findElements(By.cssSelector("form label[for='" + id + "']"));
            if (!labels.isEmpty()) {
                WebElement label = labels.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", label);
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", checkbox);
            }
        }
    }

    boolean isSettlementChecked(int lineIndex) {
        return driver.findElement(settlementCheckbox()).isSelected();
    }

    By settlementCheckbox() {
        return By.cssSelector("form input[type='checkbox'], form [role='checkbox']");
    }

    boolean hasTaxDistributionAction(int lineIndex) {
        return !driver.findElements(
                        lineElement(lineIndex, "button[@data-action-type='shareToTaxAccount']"))
                .isEmpty();
    }

    void openTaxDistribution(int lineIndex) {
        driver.findElement(lineElement(lineIndex, "button[@data-action-type='shareToTaxAccount']"))
                .click();
    }

    BigDecimal getTaxAmountValue(int lineIndex) {
        String text =
                driver.findElement(
                                lineElement(
                                        lineIndex,
                                        "input[@data-tax-amount or contains(@name,'tax') or contains(@aria-label,'ضريبة')]"))
                        .getDomProperty("value");
        return new BigDecimal(text);
    }

    void setDebit(int lineIndex, String amount) {
        setInputValue(lineAmountInput(lineIndex, 3), amount);
    }

    void setCredit(int lineIndex, String amount) {
        setInputValue(lineAmountInput(lineIndex, 4), amount);
    }

    String getDebitValue(int lineIndex) {
        return driver.findElement(lineAmountInput(lineIndex, 3)).getDomProperty("value");
    }

    String getCreditValue(int lineIndex) {
        return driver.findElement(lineAmountInput(lineIndex, 4)).getDomProperty("value");
    }

    boolean isNumericAmountInput(By locator) {
        return "number".equals(driver.findElement(locator).getAttribute("type"));
    }

    boolean isLineAccountSelected(int lineIndex) {
        return !driver.findElement(
                        By.xpath(
                                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr)["
                                        + lineIndex
                                        + "]//td[@aria-colindex='2']//span[contains(@class,'vs__selected')]"))
                .getText()
                .trim()
                .isEmpty();
    }

    void setLineNotes(int lineIndex, String notes) {
        setInputValue(lineNoteInput(lineIndex), notes);
    }

    String getLineNotes(int lineIndex) {
        return driver.findElement(lineNoteInput(lineIndex)).getDomProperty("value");
    }

    By lineNoteInput(int lineIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]//td[@aria-colindex='6']//input");
    }

    void setInputValue(By locator, String value) {
        WebElement input = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript(
                        "const el=arguments[0]; const setter=Object.getOwnPropertyDescriptor(HTMLInputElement.prototype,'value').set; setter.call(el,arguments[1]); el.dispatchEvent(new Event('input',{bubbles:true})); el.dispatchEvent(new Event('change',{bubbles:true}));",
                        input,
                        value);
        owner.lineWaits()
                .fluentWait()
                .until(
                        d -> {
                            WebElement current = d.findElement(locator);
                            String actual = current.getDomProperty("value");
                            if ("number".equals(current.getAttribute("type"))) {
                                try {
                                    return new BigDecimal(value).compareTo(new BigDecimal(actual))
                                            == 0;
                                } catch (Exception ignored) {
                                    return false;
                                }
                            }
                            return value.equals(actual);
                        });
    }

    void removeLine(int lineIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        By removeButton =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                                + lineIndex
                                + "]//*[name()='svg' and contains(@class,'feather-trash')]");
        owner.clickLineElement(removeButton);
    }

    BigDecimal getDebitTotal() {
        return readTotal(3);
    }

    BigDecimal getCreditTotal() {
        return readTotal(4);
    }

    BigDecimal readTotal(int columnIndex) {
        String text =
                driver.findElement(
                                By.xpath(
                                        "//form//table[.//th[normalize-space()='مدين']]/tfoot/tr/th[@aria-colindex='"
                                                + columnIndex
                                                + "']"))
                        .getText()
                        .replaceAll("[^0-9.,-]", "")
                        .replace(",", "");
        return text.isBlank() ? BigDecimal.ZERO : new BigDecimal(text);
    }

    void assertTotals(String debit, String credit) {
        Assert.assertEquals(
                owner.getDebitTotal().compareTo(new BigDecimal(debit)), 0, "إجمالي المدين: المتوقع " + debit + "، الفعلي " + owner.getDebitTotal());
        Assert.assertEquals(
                owner.getCreditTotal().compareTo(new BigDecimal(credit)), 0, "إجمالي الدائن: المتوقع " + credit + "، الفعلي " + owner.getCreditTotal());
    }

    boolean isBalanced() {
        return owner.getDebitTotal().compareTo(owner.getCreditTotal()) == 0;
    }
}
