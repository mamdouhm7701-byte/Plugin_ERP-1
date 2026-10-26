package Plugin_ERP.Bots;

import java.lang.reflect.Proxy;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TextEntryActionsTest {
    @Test
    public void inputThatIgnoresTypingMustFailInsteadOfPassing() {
        WebElement input = input();
        var actions = new ElementActions(browser(input));
        String previous = System.getProperty("defultwait");
        System.setProperty("defultwait", "0");
        try {
            RuntimeException failure =
                    Assert.expectThrows(
                            RuntimeException.class,
                            () -> actions.typeText(By.id("input"), "expected"));
            Assert.assertNotNull(
                    failure.getCause(), "The last failed interaction must remain diagnosable");
        } finally {
            if (previous == null) System.clearProperty("defultwait");
            else System.setProperty("defultwait", previous);
        }
    }

    @Test
    public void passwordWhitespaceIsPreserved() {
        String[] value = {""};
        WebElement input =
                (WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebElement.class},
                                (proxy, method, args) ->
                                        switch (method.getName()) {
                                            case "isDisplayed", "isEnabled" -> true;
                                            case "getAttribute", "getDomProperty" ->
                                                    "type".equals(args[0]) ? "password" : value[0];
                                            case "click" -> null;
                                            case "sendKeys" -> {
                                                String text =
                                                        ((CharSequence[]) args[0])[0].toString();
                                                if (text.equals(Keys.DELETE.toString()))
                                                    value[0] = "";
                                                else if (!text.contains(Keys.CONTROL.toString()))
                                                    value[0] = text;
                                                yield null;
                                            }
                                            default -> throw new AssertionError(method.getName());
                                        });
        new ElementActions(browser(input))
                .typeTextAndVerify(By.id("password"), "  exact  secret  ");
        Assert.assertEquals(value[0], "  exact  secret  ");
    }

    private WebElement input() {
        return (WebElement)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "isDisplayed", "isEnabled" -> true;
                                    case "getAttribute", "getDomProperty" ->
                                            "type".equals(args[0]) ? "text" : "";
                                    case "click", "sendKeys" -> null;
                                    default -> throw new AssertionError(method.getName());
                                });
    }

    private WebDriver browser(WebElement input) {
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "findElement" -> input;
                                    case "findElements" -> java.util.List.of(input);
                                    case "executeScript" -> null;
                                    default -> throw new AssertionError(method.getName());
                                });
    }
}
