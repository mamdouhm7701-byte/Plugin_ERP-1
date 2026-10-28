/* شرح الملف: مراجعة تأكيد حذف البنك قبل انتظار اختفاء رسالة التأكيد.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BankDeleteOrderingTest {
    @Test
    public void confirmsVisibleDialogBeforeWaitingForItsDisappearance() {
        AtomicInteger clicks = new AtomicInteger();
        WebElement button =
                (WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebElement.class},
                                (proxy, method, args) ->
                                        switch (method.getName()) {
                                            case "isDisplayed", "isEnabled" -> true;
                                            case "click" -> {
                                                clicks.incrementAndGet();
                                                yield null;
                                            }
                                            default -> throw new AssertionError(method.getName());
                                        });
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) ->
                                        switch (method.getName()) {
                                            case "findElement" -> button;
                                            case "findElements" -> List.of(button);
                                            case "toString" -> "bank-confirmation";
                                            default -> throw new AssertionError(method.getName());
                                        });
        BankPage page =
                new BankPage(driver) {
                    @Override
                    void recordWaitAlertToDisappear() {
                        Assert.assertTrue(
                                clicks.get() > 0,
                                "Cannot wait for confirmation to disappear before confirming it");
                    }

                    @Override
                    void recordWaitRows() {}

                    @Override
                    void recordDismissSaveSuccessIfPresent() {}
                };
        new BankRecords(page, driver).ConfirmBankDelete();
        Assert.assertEquals(clicks.get(), 1);
    }
}
