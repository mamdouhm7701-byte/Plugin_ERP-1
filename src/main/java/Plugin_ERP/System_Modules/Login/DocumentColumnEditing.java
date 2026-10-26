package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Document Column Editing: focused operations used by the original page facade. */
final class DocumentColumnEditing {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    DocumentColumnEditing(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    void SetValueByRow(String columnName, int rowNumber, int value) {

        WebElement input = utilites.waits().waitForInputByRow(columnName, rowNumber);

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", input);

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);
        input.sendKeys(String.valueOf(value));

        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));"
                                + "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                        input);

        input.sendKeys(Keys.TAB);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d -> {
                            String actual =
                                    utilites.waits()
                                            .waitForInputByRow(columnName, rowNumber)
                                            .getAttribute("value");

                            return actual != null
                                    && owner.normalizeQuantity(actual)
                                            .equals(String.valueOf(value));
                        });

        LogUtils.info(columnName + " فى الصف " + rowNumber + " أصبح " + value);
    }

    void SetValuesForRows(String columnName, int... values) {

        utilites.waits().waitForOverlayToDisappear();

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Values must be pairs: rowNumber, value");
        }

        for (int i = 0; i < values.length; i += 2) {
            owner.SetValueByRow(columnName, values[i], values[i + 1]);
        }
    }
}
