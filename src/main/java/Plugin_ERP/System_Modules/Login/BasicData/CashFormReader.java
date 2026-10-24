package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import javax.swing.text.Utilities;
import junit.framework.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Cash Form Reader: focused operations used by the original page facade. */
final class CashFormReader {
    private final CashPage owner;
    private final WebDriver driver;
    CashFormReader(CashPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private boolean isVisible(By locator) { return owner.recordIsVisible(locator); }

    boolean isCashCreationFormDisplayed() {
        return isVisible(By.cssSelector("input[name='arabicName']"));
    }

    boolean isSafeGeneralChecked() {
        String generalText = "\u0639\u0627\u0645";
        String[] locators = {
            "//legend[contains(normalize-space(),'"
                    + generalText
                    + "')]/following-sibling::div//input[@type='checkbox'][1]",
            "//label[contains(normalize-space(),'"
                    + generalText
                    + "')]/following::input[@type='checkbox'][1]",
            "(//*[self::div or self::span or self::p][contains(normalize-space(),'"
                    + generalText
                    + "')]/following::input[@type='checkbox'])[1]",
            "(//input[@type='checkbox'][preceding::*[contains(normalize-space(),'"
                    + generalText
                    + "')]])[1]",
            "(//input[@type='checkbox'][following::*[contains(normalize-space(),'"
                    + generalText
                    + "')]])[1]",
            "(//*[contains(normalize-space(),'"
                    + generalText
                    + "')]/descendant::input[@type='checkbox'])[1]"
        };
        for (String locator : locators) {
            List<WebElement> inputs = driver.findElements(By.xpath(locator));
            if (!inputs.isEmpty()) return inputs.get(0).isSelected();
        }
        throw new IllegalStateException("Safe general checkbox input was not found");
    }

    String getMissingCashCreationFormControls() {
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (!isVisible(
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='الكود']/following::input[1]")))
            missing.add("الكود");
        if (!isVisible(By.cssSelector("input[name='arabicName']"))) missing.add("الاسم العربي");
        if (!isVisible(By.cssSelector("input[name='englishName']"))) missing.add("الاسم الإنجليزي");
        if (!isVisible(
                By.xpath(
                        "//label[normalize-space()='رقم الحساب']/following::div[contains(@class,'vs__dropdown-toggle')][1]")))
            missing.add("رقم الحساب");
        By generalLabel = By.xpath("//label[contains(normalize-space(),'عام')]");
        By generalCheckbox =
                By.xpath(
                        "//label[contains(normalize-space(),'عام')]/following::input[@type='checkbox'][1]");
        if (!isVisible(generalLabel) || driver.findElements(generalCheckbox).isEmpty())
            missing.add("عام");
        if (!isVisible(By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']")))
            missing.add("الملاحظات");
        if (!isVisible(By.cssSelector("button[data-action-type='save']"))) missing.add("حفظ");
        if (!isVisible(By.xpath("//button[normalize-space()='إغلاق']"))) missing.add("إغلاق");
        return String.join("، ", missing);
    }

    String getArabicNameInputValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']")).getAttribute("value");
    }

    String getEnglishNameInputValue() {
        return driver.findElement(By.cssSelector("input[name='englishName']"))
                .getAttribute("value");
    }

    String getNotesValue() {
        return findNotesField().getAttribute("value");
    }

    int getNotesMaxLength() {
        List<WebElement> fields =
                driver.findElements(
                        By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']"));
        if (fields.isEmpty()) {
            return -1;
        }
        String maxLength = fields.get(0).getAttribute("maxlength");
        if (maxLength == null || maxLength.isBlank()) {
            return -1;
        }
        return Integer.parseInt(maxLength);
    }

    WebElement findNotesField() {
        return driver
                .findElements(
                        By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cash notes field is not visible"));
    }

    String getSafeCodeInputValue() {
        return driver.findElement(
                        By.xpath(
                                "//*[self::label or self::legend][normalize-space()='الكود']/following::input[1]"))
                .getAttribute("value")
                .trim();
    }

    String getSafeAccountValue() {
        List<WebElement> selectedValues =
                driver.findElements(
                        By.xpath(
                                "//label[normalize-space()='رقم الحساب']/following::div[contains(@class,'vs__dropdown-toggle')][1]//div[contains(@class,'vs__selected')]"));
        return selectedValues.isEmpty() ? "" : selectedValues.get(0).getText().trim();
    }
}
