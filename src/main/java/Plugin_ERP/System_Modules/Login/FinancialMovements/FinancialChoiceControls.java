package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Util.FormInteractions;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Focused FinancialChoiceControls operations extracted from FinancialMovementPage. */
final class FinancialChoiceControls {
    private final FinancialMovementPage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;
    private final FormInteractions choices;

    FinancialChoiceControls(
            FinancialMovementPage owner,
            WebDriver driver,
            WebDriverWait ready,
            FormInteractions choices) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
        this.choices = choices;
    }

    void choose(String label, String name) {
        By input =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::input[contains(@class,'vs__search')][1]");
        chooseInput(input, name);
    }

    void chooseInput(By locator, String name) {
        try {
            choices.choose(
                    locator,
                    name,
                    FormInteractions.OptionMatch.EXACT_THEN_CONTAINS,
                    "Ambiguous owned dropdown option: ");
        } catch (TimeoutException failure) {
            Object snapshot =
                    Plugin_ERP.Util.VueComponentAccess.execute(
                            driver,
                            """
          const queue=[erpVue.root()], result=[];
          while(queue.length){const vm=queue.shift();if(!vm)continue;
            if(Array.isArray(vm.defaultCustomers))result.push({
              branchId:vm.currentBranch?.id, customerCount:vm.defaultCustomers.length,
              matches:vm.defaultCustomers.filter(c=>[c.arabicName,c.englishName].includes(arguments[0])),
              paymentCount:vm.paymentMethods?.length, agentCount:vm.Agents?.length});
            queue.push(...(erpVue.children(vm)||[]));}
          return JSON.stringify(result);
          """,
                            name);
            io.qameta.allure.Allure.addAttachment(
                    "Financial dropdown lookup evidence", String.valueOf(snapshot));
            throw failure;
        }
    }

    void payment(String methodName) {
        choose("طريقة الدفع", "اخري");
        // The dependent payment-method picker follows the payment-way picker.
        By input =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='طريقة الدفع']/following::input[contains(@class,'vs__search')][2]");
        chooseInput(input, methodName);
    }

    void transferParty(int side, String type, String name) {
        selectTransferType(side, type);
        By input =
                By.xpath(
                        "(//div[@role='radiogroup'])["
                                + side
                                + "]/following::input[contains(@class,'vs__search')][1]");
        chooseInput(input, name);
    }

    void selectTransferType(int side, String type) {
        String label =
                switch (type) {
                    case "Safe" -> "خزينة";
                    case "Bank" -> "البنك";
                    case "Customer" -> "العميل";
                    case "Supplier" -> "المورد";
                    default -> throw new IllegalArgumentException(type);
                };
        By radio =
                By.xpath(
                        "(//div[@role='radiogroup'])["
                                + side
                                + "]//span[normalize-space()='"
                                + label
                                + "']");
        click(radio);
    }

    void movementType(String value) {
        By locator = By.cssSelector("input[type='radio'][value='" + value + "']");
        WebElement input = driver.findElement(locator);
        if (!input.isSelected())
            click(By.xpath("//input[@type='radio' and @value='" + value + "']/ancestor::label[1]"));
        ready.until(d -> d.findElement(locator).isSelected());
    }

    void clearField(String label, int index) {
        By input =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::input[contains(@class,'vs__search')]["
                                + index
                                + "]");
        WebElement e = ready.until(ExpectedConditions.elementToBeClickable(input));
        WebElement picker =
                e.findElement(
                        By.xpath(
                                "ancestor::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"));
        List<WebElement> clear = picker.findElements(By.cssSelector("button.vs__clear"));
        if (clear.stream().anyMatch(WebElement::isDisplayed))
            clear.stream().filter(WebElement::isDisplayed).findFirst().orElseThrow().click();
        else {
            e.click();
            e.sendKeys(Keys.BACK_SPACE);
            e.sendKeys(Keys.ESCAPE);
        }
    }

    void clearTransferDestination() {
        WebElement input =
                driver.findElement(
                        By.xpath(
                                "(//div[@role='radiogroup'])[2]/following::input[contains(@class,'vs__search')][1]"));
        input.click();
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(Keys.ESCAPE);
    }

    private void click(By locator) {
        owner.clickControl(locator);
    }
}
