package Plugin_ERP.System_Modules.Login.SpecialSales;

import io.qameta.allure.Step;
import org.openqa.selenium.*;

/** The point-of-sale page has its own route, save API and controls. */
public final class PointOfSalePage extends SpecialSalesPage {
    public PointOfSalePage(WebDriver driver) {
        super(driver);
    }

    public String route() {
        return "point-of-sale";
    }

    public String endpoint() {
        return "ItemTransactions-PointOfSales";
    }

    public String channel() {
        return "pos";
    }

    @Step("عرض تفاصيل الفاتورة")
    public void showInvoiceDetails() {
        for (var input : driver.findElements(By.cssSelector("input[name='check-button']")))
            if (!input.isSelected()) {
                click(
                        By.cssSelector(
                                "label[for='"
                                        + input.getDomAttribute("id")
                                        + "'] .switch-icon-right"));
                break;
            }
    }

    @Step("إضافة الصنف {name} بالباركود {barcode}")
    public void addItem(String name, String barcode, String category) {
        var input = driver.findElement(By.cssSelector("input[placeholder='الباركود']"));
        input.click();
        input.sendKeys(barcode);
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        vmScript()
                                                + "return vm.filterItem?.barcode===arguments[0];",
                                        barcode)));
        input.sendKeys(Keys.ENTER);
        ready.until(
                d ->
                        ((java.util.List<?>) document().get("lines"))
                                .stream()
                                        .anyMatch(
                                                v ->
                                                        ((java.util.Map<?, ?>) v)
                                                                .containsValue(name)));
        awaitTotals();
    }

    @Step("فتح شاشة سداد الفاتورة")
    public void openPayment() {
        if (driver.findElements(By.xpath("//button[normalize-space()='دفع']")).stream()
                .anyMatch(e -> e.isDisplayed() && e.isEnabled())) button("دفع");
    }

    @Step("حفظ الفاتورة")
    public void save() {
        if (driver.findElements(By.xpath("//button[normalize-space()='حفظ']")).stream()
                .noneMatch(WebElement::isDisplayed)) openPayment();
        button("حفظ");
    }

    @Step("اختيار السداد النقدي")
    public void cash() {
        click(By.xpath("//label[normalize-space()='نقدي']"));
    }

    @Step("اختيار السداد الآجل")
    public void credit() {
        click(By.xpath("//label[normalize-space()='اجل' or normalize-space()='آجل']"));
    }

    @Step("تعليق الفاتورة الحالية")
    public void hold() {
        button("تعليق");
    }

    @Step("استرجاع الفاتورة المعلقة")
    public void restore() {
        button("إسترجاع");
    }

    @Step("فتح قائمة الفواتير")
    public void invoices() {
        closePayment();
        button("الفواتير");
    }
}
