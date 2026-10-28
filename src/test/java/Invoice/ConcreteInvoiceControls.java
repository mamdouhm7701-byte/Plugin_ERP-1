/* شرح الملف: اختيار إذن تسليم الخرسانة الصحيح داخل فاتورته.
 */
package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import io.qameta.allure.Allure;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

final class ConcreteInvoiceControls {
    private ConcreteInvoiceControls() {}

    static void selectOwnedDelivery(
            WebDriver driver,
            SalesModulePage page,
            SalesTestData.Record delivery,
            SalesTestData.Record item) {
        page.slButton("اذونات تسليم الخرسانة");
        var ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d,
                                        """
                const r=erpVue.root().$router.currentRoute,vm=r.matched.at(-1).instances.default;
                return vm.concreteDeliveryOrders?.some(row=>String(row.transactionId)===String(arguments[0]));
                """,
                                        delivery.id())));
        Allure.addAttachment(
                "Owned delivery invoice selection",
                "text/html",
                page.slDiagnosticMarkup(),
                ".html");
        var selection =
                ready.until(
                        d ->
                                d
                                        .findElements(
                                                org.openqa.selenium.By.xpath(
                                                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[td[contains(.,'"
                                                                + item.name()
                                                                + "')]]//input[@type='checkbox']"))
                                        .stream()
                                        .findFirst()
                                        .orElse(null));
        selection.sendKeys(org.openqa.selenium.Keys.SPACE);
        ready.until(d -> selection.isSelected());
        confirmSelection(driver, Duration.ofSeconds(35));
        ready.until(
                d ->
                        d.findElements(org.openqa.selenium.By.cssSelector(".modal.show")).stream()
                                .noneMatch(org.openqa.selenium.WebElement::isDisplayed));
        Assert.assertEquals(
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver,
                        """
                const r=erpVue.root().$router.currentRoute,vm=r.matched.at(-1).instances.default;
                return vm.concreteDeliveryOrderIdsList?.some(id=>String(id)===String(arguments[0]))===true;
                """,
                        delivery.id()),
                Boolean.TRUE,
                "Invoice must reference the exact owned delivery");
    }

    static void confirmSelection(WebDriver driver, Duration timeout) {
        new WebDriverWait(driver, timeout)
                .ignoring(org.openqa.selenium.ElementClickInterceptedException.class)
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .until(
                        d -> {
                            var button =
                                    d
                                            .findElements(
                                                    org.openqa.selenium.By.cssSelector(
                                                            ".modal.show button"))
                                            .stream()
                                            .filter(org.openqa.selenium.WebElement::isDisplayed)
                                            .filter(org.openqa.selenium.WebElement::isEnabled)
                                            .filter(b -> b.getText().trim().equals("حفظ"))
                                            .findFirst()
                                            .orElse(null);
                            if (button == null) return false;
                            button.click();
                            return true;
                        });
    }
}
