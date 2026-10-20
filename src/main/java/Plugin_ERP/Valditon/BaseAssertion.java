package Plugin_ERP.Valditon;

import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class BaseAssertion {
    protected final WebDriver driver;
    protected final WaitMangment waitMangment;
    protected final ElmentAction elmentAction;

    public BaseAssertion(WebDriver driver) {
        this.driver = driver;
        this.waitMangment = new WaitMangment(driver);
        this.elmentAction = new ElmentAction(driver);
    }

    protected abstract void assertrue(boolean condition, String message);

    protected abstract void asserfalse(boolean condition, String message);

    protected abstract void assertequals(Object actual, Object expected, String message);

    protected void Equals(String actual, String expected, String message) {
        assertequals(actual, expected, message);
    }

    protected void Visible(By locator) {
        boolean flag =
                waitMangment
                        .fluentWait()
                        .until(
                                driver1 -> {
                                    try {
                                        driver1.findElement(locator).isDisplayed();
                                        return true;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                });
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
