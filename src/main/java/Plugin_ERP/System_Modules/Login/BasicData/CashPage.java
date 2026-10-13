package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.text.Utilities;
import java.time.Duration;
import java.util.List;

public class CashPage extends AbstractForDealer {
    //Constructors
    public CashPage
    (WebDriver driver) {
        super(driver);
    }

    //Locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");

    private By BasicData = By.xpath("//span[contains(text(),'البيانات الأساسية')]");
    private By Enteringthetreasury = By.xpath("//span[contains(text(),'البيانات الأساسية')]/following::span[contains(text(),'الخزن')]\n");
    private final By GridSearch = By.cssSelector("input[placeholder*='بحث']");
    private final By SafeRows = By.xpath("//tbody//tr[@role='row']");

    //Methods
    @Step("فتح قائمه الخزن")
    public void OpenCashPage() {
        utilites.ClickElement(BasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(Enteringthetreasury);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);


    }

    @Step("التحقق من ظهور نموذج إضافة خزنة")
    public boolean isCashCreationFormDisplayed() {
        return driver.findElements(By.cssSelector("input[name='arabicName']"))
                .stream().anyMatch(element -> element.isDisplayed());
    }

    @Step("البحث في قائمة الخزن عن: {searchText}")
    public void searchSafes(String searchText) {
        WebElement search = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(GridSearch));
        search.clear();
        if (searchText != null && !searchText.isEmpty()) {
            search.sendKeys(searchText);
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(By.cssSelector(".b-overlay"))
                        .stream().noneMatch(WebElement::isDisplayed));
    }

    @Step("التحقق من ظهور خزنة في نتائج البحث: {safeText}")
    public boolean isSafeVisibleInSearchResults(String safeText) {
        return driver.findElements(SafeRows).stream()
                .anyMatch(row -> row.getText().contains(safeText));
    }

    @Step("الحصول على عدد صفوف قائمة الخزن")
    public int getVisibleSafeRowCount() {
        return (int) driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .count();
    }

    @Step("تصدير قائمة الخزن بصيغة {format}")
    public void exportSafes(String format) {
        String normalizedFormat = format.trim().toLowerCase();
        if (!normalizedFormat.equals("pdf") && !normalizedFormat.equals("xls")) {
            throw new IllegalArgumentException("Supported safe export formats are PDF and XLS");
        }

        String lower = "translate(concat(@class,' ',@src,' ',@alt,' ',@title,' ',@aria-label,' ',normalize-space(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
        String tokenMatch = normalizedFormat.equals("pdf")
                ? "contains(" + lower + ", 'pdf')"
                : "(contains(" + lower + ", 'xls') or contains(" + lower + ", 'excel') or " +
                        "contains(" + lower + ", 'spreadsheet'))";
        By exportControl = By.xpath(
                "//*[self::button or self::a][" + tokenMatch + " or .//*[" + tokenMatch + "]]"
        );

        List<WebElement> controls = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    List<WebElement> found = d.findElements(exportControl);
                    return found.stream().anyMatch(WebElement::isDisplayed) ? found : null;
                });
        WebElement control = controls.stream().filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Export control not visible: " + format));
        control.click();
    }
    

}
