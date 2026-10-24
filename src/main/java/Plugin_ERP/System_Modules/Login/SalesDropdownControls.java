package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Read the options and selected values of sales form dropdowns. */
final class SalesDropdownControls {
    private final WebDriver driver;
    SalesDropdownControls(WebDriver driver) {
        this.driver = driver;
    }

    List<String> getDropdownOptions(String label) {
        By dropdownInput = dropdownInput(label);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(dropdownInput));
        input.click();

        String listboxId = input.getAttribute("aria-controls");
        if (listboxId == null || listboxId.isBlank()) {
            String comboboxId =
                    input.findElement(By.xpath("ancestor::div[contains(@class,'v-select')]"))
                            .getAttribute("id");
            listboxId = comboboxId == null ? "" : comboboxId.replace("__combobox", "__listbox");
        }
        if (listboxId.isBlank()) {
            throw new IllegalStateException("تعذر تحديد قائمة الخيارات للحقل: " + label);
        }

        By optionsLocator = By.cssSelector("#" + listboxId + " li");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionsLocator));
        List<WebElement> options =
                driver.findElements(optionsLocator).stream()
                        .filter(WebElement::isDisplayed)
                        .collect(Collectors.toList());

        List<String> optionTexts =
                options.stream()
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(text -> !text.isEmpty())
                        .collect(Collectors.toList());
        input.sendKeys(org.openqa.selenium.Keys.ESCAPE);
        return optionTexts;
    }

    String getSelectedDropdownOption(String label) {
        By selectedOption = selectedDropdownOption(label);
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(selectedOption))
                .getText()
                .trim();
    }

    By dropdownInput(String label) {
        return switch (label) {
            case "المخزن" -> By.cssSelector(".v-select[name='StoreId'] input.vs__search");
            case "العميل", "العملاء" ->
                    By.cssSelector("fieldset[label='arabicName'] input.vs__search");
            case "مندوب" -> By.cssSelector(".v-select[name='agent'] input.vs__search");
            case "قوائم الأسعار" -> By.cssSelector(".v-select[name='priceLists'] input.vs__search");
            default ->
                    By.xpath(
                            "//*[self::legend or self::label][normalize-space()='"
                                    + label
                                    + "']"
                                    + "/following::input[contains(@class,'vs__search')][1]");
        };
    }

    By selectedDropdownOption(String label) {
        if (label.equals("العميل") || label.equals("العملاء")) {
            return By.xpath(
                    "//input[@id='vs3__combobox']/ancestor::div[contains(@class,'vs__dropdown-toggle')][1]"
                            + "//*[contains(concat(' ', normalize-space(@class), ' '), ' vs__selected ')]");
        }
        return By.xpath(
                "//*[self::legend or self::label][normalize-space()='"
                        + label
                        + "']"
                        + "/following::*[contains(concat(' ', normalize-space(@class), ' '), ' vs__selected ')][1]");
    }
}
