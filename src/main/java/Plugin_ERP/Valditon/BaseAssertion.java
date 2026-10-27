package Plugin_ERP.Valditon;

import Plugin_ERP.Bots.ElementActions;
import Plugin_ERP.Bots.WaitManagement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class BaseAssertion {
    protected final WebDriver driver;
    protected final WaitManagement waitManagement;
    protected final ElementActions elmentAction;

    public BaseAssertion(WebDriver driver) {
        this.driver = driver;
        this.waitManagement = new WaitManagement(driver);
        this.elmentAction = new ElementActions(driver);
    }

    protected abstract void assertrue(boolean condition, String message);

    protected abstract void asserfalse(boolean condition, String message);

    protected abstract void assertequals(Object actual, Object expected, String message);

    protected void Equals(String actual, String expected, String message) {
        assertequals(actual, expected, message);
    }

    protected void Visible(By locator) {
        boolean flag;
        try {
            waitManagement
                    .fluentWait()
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            flag = true;
        } catch (TimeoutException notVisible) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "BaseAssertion fallback: " + notVisible.getClass().getSimpleName());

            // Keep hard/soft assertion behavior consistent when the bounded wait expires.
            flag = false;
        }
        assertrue(flag, "❌ العنصر المحدد غير مرئي: " + locator.toString());
    }

    // verify page uerl
    protected void PageURL(String expectedURL) {
        String actualURL = driver.getCurrentUrl();
        assertequals(
                actualURL,
                expectedURL,
                "❌ عنوان URL للصفحة غير مطابق. المتوقع: " + expectedURL + "، الفعلي: " + actualURL);
    }

    // verify page title
    protected void PageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        assertequals(
                actualTitle,
                expectedTitle,
                "❌ عنوان الصفحة غير مطابق. المتوقع: " + expectedTitle + "، الفعلي: " + actualTitle);
    }
}
