package Plugin_ERP.Bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AlertAction extends BaseAction {


    public AlertAction(
            WebDriver driver,
            WebDriverWait wait,
            WaitMangment waitMangment
    ) {
        super(driver, wait, waitMangment);
    }

    //accept alert
    public void acceptSwalConfirm() {
        waitMangment.fluentWait().until(d -> {
            try {
                WebElement confirmBtn = driver.findElement(
                        By.cssSelector("button.swal2-confirm.btn-primary"));
                confirmBtn.click();
                return true;
            } catch (Exception e) {
                LogUtils.error("فشل الضغط على زرار التأكيد: " + e.getMessage());
                return false;
            }
        });
    }

    //dismiss alert
    public void dismissAlert() {
        waitMangment.fluentWait().until(d -> {
            try {
                WebElement cancelBtn = driver.findElement(
                        By.cssSelector("button.swal2-cancel.btn-outline-danger"));
                cancelBtn.click();
                return true;
            } catch (Exception e) {
                LogUtils.error("لم يتم الضغط على الغاء:" + e.getMessage());
                return false;
            }
        });
    }

    //get alert text
    public String getAlertText() {
        return waitMangment.fluentWait().until(d ->
        {
            try {
                return driver.switchTo().alert().getText();
            } catch (Exception e) {
                LogUtils.error("Failed to get alert text:" + e.getMessage());
                return null;

            }
        });
    }

    //send text to alert
    public void sendTextToAlert(String text) {
        waitMangment.fluentWait().until(d ->
        {
            try {
                driver.switchTo().alert().sendKeys(text);
                return true;
            } catch (Exception e) {
                LogUtils.error("Failed to send text to alert:" + e.getMessage());
                return false;
            }
        });
    }
    public String getPopupMessage(By locator) {

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        return message.getText().trim();
    }
}