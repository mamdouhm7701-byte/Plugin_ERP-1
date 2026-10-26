package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Page Form Fields: focused operations used by the original page facade. */
final class PageFormFields {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    PageFormFields(Abstract owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private By Value = By.cssSelector("input[label-text='value']");
    private By Notes = By.cssSelector("textarea[label-text='Notes']");
    private By AddDate = By.cssSelector("fieldset[label-text='date'] input");
    private By Reference = By.cssSelector("input[name='reference']");
    private By AddArabicName = By.cssSelector("input[name='arabicName']");
    private By AddEnglishName = By.cssSelector("input[name='englishName']");
    private By AddAdress = By.xpath("//input[@class='form-control' and @id='adress']\n");
    private By PhoneInput = By.cssSelector("input#phone");

    void AddArabicName(String ArabicName) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(AddArabicName, ArabicName);
    }

    void AddEnglishName(String EnglishName) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(AddEnglishName, EnglishName);
    }

    void AddPhoneNumber(String PhoneNumber) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(PhoneInput, PhoneNumber);
    }

    void AddValue(String value) {
        utilites.typeTextAndVerify(Value, value);
    }

    void AddReference(String reference) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(Reference, reference);
    }

    void AddAdress(String adress) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(AddAdress, adress);
    }

    void AddNotes(String notes) {
        utilites.typeTextAndVerify(Notes, notes);
    }

    String AddDate() {
        utilites.waits().waitForOverlayToDisappear();
        String todayDate = utilites.Utility().TypeTodayDate("dd/MM/yyyy");
        utilites.typeTextAndVerify(AddDate, todayDate);
        utilites.waits().waitForOverlayToDisappear();
        return todayDate;
    }

    String GetTodayDate() {
        return utilites.Utility().TypeTodayDate("dd/MM/yyyy");
    }

    void AddPastYearDate() {
        utilites.waits().waitForOverlayToDisappear();
        String pastYearDate = utilites.Utility().TypePastYearDate("dd/MM/yyyy");
        utilites.typeTextAndVerify(AddDate, pastYearDate);
        utilites.waits().waitForOverlayToDisappear();
    }
}
