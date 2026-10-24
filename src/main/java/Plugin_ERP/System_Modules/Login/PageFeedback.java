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

/** Page Feedback: focused operations used by the original page facade. */
final class PageFeedback {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
    PageFeedback(Abstract owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }
    private By SaveButton = By.cssSelector("button[data-action-type='save']");
    private By AssertExsistArabicName = By.xpath("//small[contains(text(),'الاسم العربي مطلوب')]");
    private By AssertRightEmail =
            By.xpath(
                    "//small[contains(text(),'البريد الالكتروني يجب ان يكون بريدا اليكتروني صحيح')]");
    private By AssertTaxNumber = By.xpath("//small[contains(text(),'الرقم الضريبي مطلوب')]");
    private By AssertBankNumber =
            By.xpath("//small[contains(text(),'الحقل رقم الحساب يجب ان يكون عدداً صحيحاً')]");

    boolean IsArabicNameRequiredErrorDisplayed() {
        return utilites.isDisplayed(AssertExsistArabicName);
    }

    boolean IsRightEmailErrorDisplayed() {
        return utilites.isDisplayed(AssertRightEmail);
    }

    boolean IsTaxNumberRequiredErrorDisplayed() {
        return utilites.isDisplayed(AssertTaxNumber);
    }

    boolean AssertNumberOfBankWithOutLetter() {
        return utilites.isDisplayed(AssertBankNumber);
    }

    void AssertCheckboxIsChecked(String checkboxName, boolean expectedState) {
        utilites.checkbox().assertCheckboxState(checkboxName, expectedState);
    }

    boolean AssertSaveButtonIsNotChecked() {
        return utilites.isDisplayed(SaveButton);
    }

    boolean isErrorPopupDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.cssSelector(".swal2-container.swal2-backdrop-show")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    void AssertPopupAndClickOK() {
        utilites.alert().acceptSwalConfirm();
        LogUtils.info("تم التحقق من ظهور البوب اب والضغط على موافق");
    }

    void AssertPopupAndClickCancel() {
        utilites.alert().dismissAlert();
        LogUtils.info("تم التحقق من ظهور البوب اب والضغط على الغاء");
    }
}
