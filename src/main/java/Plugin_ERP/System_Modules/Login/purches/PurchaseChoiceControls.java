package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.Util.FormInteractions;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Select, clear and inspect purchase document dropdowns. */
final class PurchaseChoiceControls {
    private final PurchaseModulePage owner;
    private final WebDriver driver;

    PurchaseChoiceControls(PurchaseModulePage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    void puChoose(String label, String name) {
        By by =
                By.xpath(
                        "//*[self::label or self::legend][(normalize-space()='"
                                + label
                                + "' or normalize-space(text())='"
                                + label
                                + "')]/following::input[contains(@class,'vs__search')][1]");
        owner.formInteractions().choose(by, name, FormInteractions.OptionMatch.CODE_SEGMENT);
    }

    void puClearChoice(String label) {
        owner.ready.until(
                d -> {
                    for (WebElement dropdown :
                            d.findElements(
                                    By.xpath(
                                            "//*[self::label or self::legend][(normalize-space()='"
                                                    + label
                                                    + "' or normalize-space(text())='"
                                                    + label
                                                    + "')]/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"))) {
                        owner.js(
                                "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                dropdown);
                        if (dropdown.findElements(By.cssSelector(".vs__selected")).isEmpty())
                            return true;
                        for (WebElement button :
                                dropdown.findElements(By.cssSelector("button.vs__clear")))
                            if (button.isDisplayed() && button.isEnabled()) {
                                button.click();
                                return true;
                            }
                    }
                    return false;
                });
        owner.ready.until(d -> owner.puChoiceEmpty(label));
    }

    boolean puChoiceEmpty(String label) {
        List<WebElement> dropdowns =
                driver.findElements(
                        By.xpath(
                                "//*[self::label or self::legend][(normalize-space()='"
                                        + label
                                        + "' or normalize-space(text())='"
                                        + label
                                        + "')]/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"));
        return !dropdowns.isEmpty()
                && dropdowns.get(0).findElements(By.cssSelector(".vs__selected")).isEmpty();
    }

    boolean puCanClearChoice(String label) {
        return driver
                .findElements(
                        By.xpath(
                                "//*[self::label or self::legend][(normalize-space()='"
                                        + label
                                        + "' or normalize-space(text())='"
                                        + label
                                        + "')]/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]//button[contains(@class,'vs__clear')]"))
                .stream()
                .anyMatch(e -> e.isDisplayed() && e.isEnabled());
    }

    boolean puHasChoice(String label) {
        return driver
                .findElements(
                        By.xpath(
                                "//*[self::label or self::legend][(normalize-space()='"
                                        + label
                                        + "' or normalize-space(text())='"
                                        + label
                                        + "')]"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }
}
