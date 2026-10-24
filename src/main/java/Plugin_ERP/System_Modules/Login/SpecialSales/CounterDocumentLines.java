package Plugin_ERP.System_Modules.Login.SpecialSales;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Counter cart quantities, removal and total synchronization. */
final class CounterDocumentLines {
    private final SpecialSalesPage owner;
    private final WebDriver driver;
    CounterDocumentLines(SpecialSalesPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    By line(String name) {
        return By.xpath(
                "//section[@id='lines']//div[contains(concat(' ',normalize-space(@class),' '),'"
                        + " ecommerce-card ')][.//*[normalize-space(text())='"
                        + name
                        + "']]");
    }

    void quantity(String name, String value) {
        var card =
                owner.ready.until(
                        d ->
                                d.findElements(owner.line(name)).stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        var input = card.findElement(By.cssSelector("input[placeholder='الكمية']"));
        input.click();
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(value);
        owner.blur();
    }

    void changeQuantity(String name, boolean plus) {
        var card = driver.findElement(owner.line(name));
        card.findElement(
                        By.xpath(
                                ".//div[contains(@class,'quantity-input')]//button[.//*[local-name()='svg'"
                                    + " and contains(@class,'feather-"
                                        + (plus ? "plus" : "minus")
                                        + "')]]"))
                .click();
    }

    void removeLine(String name) {
        var card = driver.findElement(owner.line(name));
        card.findElement(
                        By.xpath(
                                ".//button[.//*[local-name()='svg' and"
                                        + " contains(@class,'feather-x')]]"))
                .click();
    }

    void waitNet(String value) {
        try {
            owner.ready.until(d -> new java.math.BigDecimal(owner.document().get("net").toString())
                    .compareTo(new java.math.BigDecimal(value)) == 0);
        } catch (TimeoutException failure) {
            throw new AssertionError("صافي الفاتورة غير مطابق: المتوقع " + value
                    + "، الفعلي " + owner.document().get("net"), failure);
        }
    }

    void waitTotal(String value) {
        try {
            owner.ready.until(d -> new java.math.BigDecimal(owner.document().get("total").toString())
                    .compareTo(new java.math.BigDecimal(value)) == 0);
        } catch (TimeoutException failure) {
            throw new AssertionError("إجمالي الفاتورة غير مطابق: المتوقع " + value
                    + "، الفعلي " + owner.document().get("total"), failure);
        }
    }

    void awaitTotals() {
        try {
            owner.ready.until(
                    d ->
                            Boolean.TRUE.equals(
                                    owner.js(
                                            owner.vmScript()
                                                    + "return"
                                                    + " Math.abs(Number(vm.selectedItem.total)-(vm.selectedItem.lines||[]).reduce((sum,line)=>sum+Number(line.total||0),0))<0.00001;")));
        } catch (TimeoutException failure) {
            var value = owner.document();
            var sum =
                    ((List<?>) value.get("lines"))
                            .stream()
                                    .map(
                                            row ->
                                                    new java.math.BigDecimal(
                                                            ((Map<?, ?>) row)
                                                                    .get("total")
                                                                    .toString()))
                                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            throw new AssertionError(
                    "إجمالي الفاتورة لا يطابق مجموع البنود: المتوقع " + sum + "، الفعلي " + value.get("total"),
                    failure);
        }
    }
}
