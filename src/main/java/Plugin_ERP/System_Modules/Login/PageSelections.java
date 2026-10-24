package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import Plugin_ERP.Enums.Pages;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Page Selections: focused operations used by the original page facade. */
final class PageSelections {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
    private final org.openqa.selenium.interactions.Actions actions;
    PageSelections(Abstract owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
        this.actions = owner.context().actions();
    }

    void SelectFromDropDawn(String Name, String Type) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectVueOptionboth(Type, Name);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم اختيار: " + Name);
    }

    void SelectFromDropDawnByText(String Name, String Type) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectVueOptionByDataTitle(Name, Type);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم اختيار: " + Name);
    }

    void selectTreasuryHandoverDropdown(String label, String value) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectTreasuryHandoverDropdown(label, value);
    }

    void SelectByKeys(String option) {
        actions.sendKeys(Keys.TAB)
                .pause(Duration.ofMillis(500))
                .sendKeys(option)
                .pause(Duration.ofMillis(500))
                .sendKeys(Keys.ENTER)
                .perform();
    }

    void ClickEnter() {
        actions.pause(Duration.ofMillis(500)).sendKeys(Keys.ENTER).perform();
    }

    void ClickShift() {
        actions.pause(Duration.ofMillis(500)).sendKeys(Keys.SHIFT).perform();
    }

    void clickOnEmptyArea() {
        WebElement body = driver.findElement(By.tagName("body"));

        new Actions(driver).moveToElement(body, 5, 5).click().perform();
    }

    void SetCheckboxValue(String checkboxName, boolean value) {
        utilites.checkbox().stableFastSetCheckboxByTextCloude(checkboxName, value);
    }

    void SelectCheckboxByText(String checkboxName, boolean value) {
        utilites.checkbox().stableFastSetCheckboxByText(checkboxName, value);
    }

    void SmartCheck(String checkboxName, boolean expectedState) {
        utilites.checkbox().setCheckbox(checkboxName, expectedState);
    }

    void AddNewProductList(int count) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectFirstProducts(count);
    }
}
