/* شرح الملف: يثبت قبول علامة رفض الكمية في حقلها، وكشف الحفظ المخالف فورًا بدل انتهاء الانتظار. */
package Plugin_ERP.System_Modules.Login.StoreModel;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreQuantityRejectionTest {
    private StoreDocumentLines lines(boolean saved, String aria, String css) {
        WebElement input =
                (WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebElement.class},
                                (p, m, a) ->
                                        switch (m.getName()) {
                                            case "isDisplayed" -> true;
                                            case "getDomProperty" -> "0";
                                            case "getDomAttribute" ->
                                                    switch (a[0].toString()) {
                                                        case "aria-invalid" -> aria;
                                                        case "class" -> css;
                                                        default -> "";
                                                    };
                                            default -> null;
                                        });
        WebElement row =
                (WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebElement.class},
                                (p, m, a) ->
                                        m.getName().equals("isDisplayed")
                                                ? true
                                                : m.getName().equals("findElements")
                                                        ? List.of(input)
                                                        : null);
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (p, m, a) ->
                                        switch (m.getName()) {
                                            case "executeScript" ->
                                                    saved
                                                            ? List.of(
                                                                    Map.of("id", "unexpected-save"))
                                                            : List.of();
                                            case "findElements" -> List.of(row);
                                            default -> null;
                                        });
        return new StoreDocumentLines(
                new StoreModulePage(driver),
                driver,
                new WebDriverWait(driver, Duration.ofSeconds(1)));
    }

    @Test
    public void acceptsEitherVisibleInvalidSignalOnQuantityItself() {
        Assert.assertEquals(
                lines(false, "true", "form-control")
                        .smAwaitInvalidQuantity("owned")
                        .get("quantity"),
                "0");
        Assert.assertEquals(
                lines(false, null, "form-control is-invalid")
                        .smAwaitInvalidQuantity("owned")
                        .get("quantity"),
                "0");
    }

    @Test
    public void savedInvalidQuantityFailsRatherThanWaitingForARejectionThatCannotAppear() {
        var failure =
                Assert.expectThrows(
                        AssertionError.class,
                        () -> lines(true, "false", "form-control").smAwaitInvalidQuantity("owned"));
        Assert.assertTrue(failure.getMessage().contains("persisted instead of rejected"));
    }
}
