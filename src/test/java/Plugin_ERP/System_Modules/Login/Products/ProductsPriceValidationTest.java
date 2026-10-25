package Plugin_ERP.System_Modules.Login.Products;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ProductsPriceValidationTest {
    @Test public void readsOnlyVisibleFeedbackInsideItsPriceField() {
        WebElement sale = field("80", true, List.of(
                message("سعر البيع أقل من الحد الأدنى", true),
                message("hidden stale validation", false), message(" ", true)));
        WebElement minimum = field("90", true, List.of());
        WebElement maximum = field("110", true, List.of(message("أعلى سعر بيع", true)));
        ProductsModulePage page = page(Map.of("salesPrice", List.of(sale),
                "minSalesPrice", List.of(minimum), "maxSalesPrice", List.of(maximum)));

        Assert.assertEquals(page.prPriceValidationMessages(), Map.of(
                "salesPrice", "سعر البيع أقل من الحد الأدنى", "maxSalesPrice", "أعلى سعر بيع"));
        Assert.assertEquals(page.prFieldValue("salesPrice"), "80");
    }

    @Test public void ignoresHiddenDuplicateInputsAndDoesNotTreatMissingFeedbackAsAnError() {
        ProductsModulePage page = page(Map.of("salesPrice", List.of(
                field("stale", false, List.of(message("stale price error", true))),
                field("0", true, List.of())),
                "minSalesPrice", List.of(field("0", true, List.of())),
                "maxSalesPrice", List.of(field("0", true, List.of()))));
        Assert.assertEquals(page.prFieldValue("salesPrice"), "0");
        Assert.assertTrue(page.prPriceValidationMessages().isEmpty());
    }

    @Test(expectedExceptions = AssertionError.class,
            expectedExceptionsMessageRegExp = "Visible product field missing: salesPrice")
    public void missingPriceInputCannotProduceAnInventedValue() {
        page(Map.of()).prFieldValue("salesPrice");
    }

    @Test public void detectsNativeAndAccessibleDisabledSaveButtons() {
        Assert.assertTrue(page(Map.of("save", List.of(saveButton(false, null)))).prSaveDisabled());
        Assert.assertTrue(page(Map.of("save", List.of(saveButton(true, "true")))).prSaveDisabled());
        Assert.assertFalse(page(Map.of("save", List.of(saveButton(true, "false")))).prSaveDisabled());
    }

    private ProductsModulePage page(Map<String, List<WebElement>> fields) {
        WebDriver driver = (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebDriver.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "findElements" -> {
                        String selector = args[0].toString();
                        if (selector.equals("By.cssSelector: form button[data-action-type='save']"))
                            yield fields.getOrDefault("save", List.of());
                        for (String name : List.of("salesPrice", "minSalesPrice", "maxSalesPrice")) {
                            if (selector.equals("By.cssSelector: form input[name='" + name + "']"))
                                yield fields.getOrDefault(name, List.of());
                        }
                        throw new AssertionError("Validation must remain scoped to price fields: " + selector);
                    }
                    case "toString" -> "product-price-driver";
                    default -> throw new AssertionError("Unexpected browser action: " + method.getName());
                });
        return new ProductsModulePage(driver);
    }

    private WebElement field(String value, boolean displayed, List<WebElement> feedback) {
        WebElement group = (WebElement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebElement.class}, (proxy, method, args) -> {
                    if (method.getName().equals("findElements")) {
                        Assert.assertEquals(args[0].toString(), "By.cssSelector: small.text-danger,.invalid-feedback");
                        return feedback;
                    }
                    throw new AssertionError(method.getName());
                });
        return (WebElement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebElement.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "isDisplayed" -> displayed;
                    case "getDomProperty" -> {
                        Assert.assertEquals(args[0], "value");
                        yield value;
                    }
                    case "findElements" -> {
                        Assert.assertTrue(args[0].toString().startsWith("By.xpath: ancestor::*["),
                                "Feedback must come from the input's containing field group");
                        yield List.of(group);
                    }
                    default -> throw new AssertionError(method.getName());
                });
    }

    private WebElement message(String text, boolean displayed) {
        return (WebElement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebElement.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "isDisplayed" -> displayed;
                    case "getText" -> text;
                    default -> throw new AssertionError(method.getName());
                });
    }

    private WebElement saveButton(boolean enabled, String ariaDisabled) {
        return (WebElement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebElement.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "isDisplayed" -> true;
                    case "isEnabled" -> enabled;
                    case "getDomAttribute" -> {
                        Assert.assertEquals(args[0], "aria-disabled");
                        yield ariaDisabled;
                    }
                    default -> throw new AssertionError(method.getName());
                });
    }
}
