/* شرح الملف: مساعدة لفتح ملف المتعامل والتنقل بين بيانات الاتصال والبيانات الأساسية وشروط التعامل.
 * فائدتها الوصول إلى المعلومات المطلوبة عند التسجيل أو التعديل.
 */
package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class DealerFormControls {
    private final AbstractForDealer owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    DealerFormControls(AbstractForDealer owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private static final By DEALER_SAVE_BUTTON = By.cssSelector("button[data-action-type='save']");
    // Locators

    private By NewButton =
            By.xpath(
                    "//div[contains(@class,'justify-content-end')]//button[@data-action-type='new']");
    private By ContactDataTab =
            By.xpath("//a[@role='tab' and contains(normalize-space(),'بيانات الاتصال')]");
    private By BasicDataTab =
            By.xpath("//a[@role='tab' and normalize-space()='البيانات الأساسية']");
    private By customerControlTab =
            By.xpath("//a[@role='tab' and contains(normalize-space(),'التحكم في العملاء')]");
    private By SupplerControlTab =
            By.xpath("//a[@role='tab' and contains(normalize-space(),'التحكم في المورد')]");

    private boolean isElementDisplayed(WebElement element) {
        return owner.dealerElementDisplayed(element);
    }

    void ClickNewButtonForDealer() {
        owner.rememberDealerListLocation();
        WebElement button =
                new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(
                                d ->
                                        d.findElements(NewButton).stream()
                                                .filter(this::isElementDisplayed)
                                                .filter(WebElement::isEnabled)
                                                .filter(
                                                        element ->
                                                                Boolean.TRUE.equals(
                                                                        ((org.openqa.selenium
                                                                                                .JavascriptExecutor)
                                                                                        d)
                                                                                .executeScript(
                                                                                        "arguments[0].scrollIntoView({block:'center'});"
                                                                                                + "const r=arguments[0].getBoundingClientRect();"
                                                                                                + "const top=document.elementFromPoint(r.x+r.width/2,r.y+r.height/2);"
                                                                                                + "return top===arguments[0]||arguments[0].contains(top);",
                                                                                        element)))
                                                .findFirst()
                                                .orElse(null));
        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                isVisible(DEALER_SAVE_BUTTON)
                                        && isVisible(By.cssSelector("input[name='arabicName']")));
        showDealerBasicDataIfPresent();
    }

    void showDealerBasicDataIfPresent() {
        if (driver.findElements(BasicDataTab).stream().anyMatch(this::isElementDisplayed)) {
            owner.ClickBasicDataTab();
        }
    }

    void ClickContactDataTab() {
        utilites.ClickElement(ContactDataTab);
    }

    void ClickBasicDataTab() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .ignoring(org.openqa.selenium.ElementClickInterceptedException.class)
                .until(
                        d -> {
                            WebElement tab = d.findElement(BasicDataTab);
                            if (!tab.isDisplayed() || !tab.isEnabled()) return false;
                            tab.click();
                            return true;
                        });
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElement(BasicDataTab).getAttribute("class").contains("active"));
    }

    void ClickCustomerControlTab() {
        utilites.ClickElement(customerControlTab);
    }

    void ClickSupplierControlTab() {
        utilites.ClickElement(SupplerControlTab);
    }

    boolean isDealerCreationFormDisplayed() {
        return isVisible(DEALER_SAVE_BUTTON)
                && isVisible(By.cssSelector("input[name='arabicName']"));
    }

    String getMissingDealerCommonFormControls() {
        List<String> missing = new java.util.ArrayList<>();
        addMissingControl(missing, "الاسم العربي", By.cssSelector("input[name='arabicName']"));
        addMissingControl(missing, "الاسم الإنجليزي", By.cssSelector("input[name='englishName']"));
        addMissingControl(missing, "الملاحظات", By.cssSelector("textarea"));
        addMissingControl(missing, "حفظ", DEALER_SAVE_BUTTON);
        return String.join("، ", missing);
    }

    void closeDealerFormWithoutSaving() {
        // A dismissed confirmation can remain visible during its closing animation.
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector(".swal2-container")));
        WebElement closeButton = findDealerCloseButton();
        if (closeButton != null) {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", closeButton);
        } else {
            // Some Dealer forms are full-page states without a close button; return to the list
            // route.
            if (owner.dealerListLocation() != null) {
                driver.navigate().to(owner.dealerListLocation());
            } else {
                driver.navigate().back();
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(DEALER_SAVE_BUTTON));
    }

    WebElement findDealerCloseButton() {
        List<By> candidates =
                List.of(
                        By.cssSelector("button[data-action-type='close']"),
                        By.cssSelector("button[data-action-type='cancel']"),
                        By.xpath(
                                "//button[normalize-space()='إغلاق' or normalize-space()='اغلاق']"),
                        By.xpath(
                                "//button[normalize-space()='إلغاء' or normalize-space()='الغاء']"),
                        By.cssSelector("button[aria-label='Close'],button[aria-label='Cancel']"));
        for (By candidate : candidates) {
            for (WebElement button : driver.findElements(candidate)) {
                if (isElementDisplayed(button)
                        && button.findElements(
                                        By.xpath(
                                                "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' swal2-container ')]"))
                                .isEmpty()) {
                    return button;
                }
            }
        }
        return null;
    }

    void addMissingControl(List<String> missing, String label, By locator) {
        if (!isVisible(locator)) {
            missing.add(label);
        }
    }

    String getDealerArabicNameValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']"))
                .getAttribute("value")
                .trim();
    }

    boolean isVisible(By locator) {
        return driver.findElements(locator).stream().anyMatch(this::isElementDisplayed);
    }

    private By DateInput = By.xpath("//input[@placeholder='YYYY-MM-DD']");

    private By AddNoteForStorgTrans = By.xpath("//textarea[@label-text='notes']");

    void AddDateForDealer() {
        utilites.waits().waitForOverlayToDisappear();
        String todayDate = utilites.Utility().TypeTodayDate("dd/MM/yyyy");
        utilites.typeTextAndVerify(DateInput, todayDate);
        utilites.waits().waitForOverlayToDisappear();
    }

    void AddNoteForStorgTrans(String notes) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(AddNoteForStorgTrans, notes);
        utilites.waits().waitForOverlayToDisappear();
    }
}
