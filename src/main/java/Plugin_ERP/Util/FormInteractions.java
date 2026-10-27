package Plugin_ERP.Util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Reusable form actions, using the page's existing wait and scrolling behavior. */
public final class FormInteractions {
    public enum OptionMatch {
        CODE_SEGMENT,
        NAME_AT_EDGE,
        EXACT,
        EXACT_THEN_CONTAINS;

        public boolean matches(String text, String name) {
            if (this == EXACT || this == EXACT_THEN_CONTAINS) return text.trim().equals(name);
            String normalized = text.trim().replaceAll("\\s+", " ");
            return normalized.equals(name)
                    || (this == CODE_SEGMENT
                            ? Arrays.asList(normalized.split(" - ")).contains(name)
                            : normalized.startsWith(name + " - ")
                                    || normalized.endsWith(" - " + name));
        }
    }

    public enum InputMode {
        CLICK_REPLACE,
        CLEAR_REPLACE
    }

    private final WebDriverWait ready;
    private final Consumer<WebElement> scroll;

    public FormInteractions(WebDriverWait ready, Consumer<WebElement> scroll) {
        this.ready = ready;
        this.scroll = scroll;
    }

    public void click(By locator) {
        ready.until(
                driver -> {
                    for (WebElement element : driver.findElements(locator)) {
                        scroll.accept(element);
                        if (element.isDisplayed() && element.isEnabled()) {
                            element.click();
                            return true;
                        }
                    }
                    return false;
                });
    }

    public void type(By locator, String value, boolean blur, InputMode mode) {
        ready.until(
                driver -> {
                    for (WebElement element : driver.findElements(locator)) {
                        scroll.accept(element);
                        if (!element.isDisplayed() || !element.isEnabled()) continue;
                        if (mode == InputMode.CLICK_REPLACE) {
                            element.click();
                            element.getDomProperty("value");
                            if (value.isEmpty())
                                element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
                            else element.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
                        } else {
                            element.sendKeys(Keys.CONTROL + "a");
                            element.sendKeys(Keys.DELETE);
                            if (!value.isEmpty()) element.sendKeys(value);
                        }
                        if (blur) element.sendKeys(Keys.TAB);
                        return true;
                    }
                    return false;
                });
    }

    public void choose(By locator, String name, OptionMatch match) {
        choose(locator, name, match, "Ambiguous owned option: ");
    }

    public void choose(By locator, String name, OptionMatch match, String ambiguityMessage) {
        ready.until(
                driver -> {
                    for (WebElement input : driver.findElements(locator)) {
                        scroll.accept(input);
                        if (!input.isDisplayed() || !input.isEnabled()) continue;
                        if (!name.equals(input.getDomProperty("value"))) {
                            try {
                                input.click();
                                input.sendKeys(Keys.CONTROL + "a");
                                input.sendKeys(name);
                            } catch (ElementNotInteractableException updatingDropdown) {
                                Plugin_ERP.Bots.LogUtils.debug(
                                        "FormInteractions fallback: "
                                                + updatingDropdown.getClass().getSimpleName());

                                // Reacquire the input within the same bounded page wait.
                                return false;
                            }
                        }
                        String id = input.getDomAttribute("aria-controls");
                        List<WebElement> options =
                                driver
                                        .findElements(
                                                By.xpath(
                                                        "//ul[@id='"
                                                                + id
                                                                + "']//li[not(@aria-disabled='true')]"))
                                        .stream()
                                        .filter(WebElement::isDisplayed)
                                        .filter(option -> match.matches(option.getText(), name))
                                        .toList();
                        if (options.isEmpty() && match == OptionMatch.EXACT_THEN_CONTAINS) {
                            options =
                                    driver
                                            .findElements(
                                                    By.xpath(
                                                            "//ul[@id='"
                                                                    + id
                                                                    + "']//li[not(@aria-disabled='true')]"))
                                            .stream()
                                            .filter(WebElement::isDisplayed)
                                            .filter(option -> option.getText().contains(name))
                                            .toList();
                        }
                        if (options.isEmpty()) return false;
                        if (options.size() != 1) throw new AssertionError(ambiguityMessage + name);
                        options.get(0).click();
                        return true;
                    }
                    return false;
                });
    }
}
