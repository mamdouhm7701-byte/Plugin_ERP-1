/* شرح الملف: شاشة التحويل بين المخازن: بنحدد المخزن المرسل والمخزن المستلم والأصناف والكميات.
 * فائدتها توثيق انتقال البضاعة بين أماكن التخزين.
 */
package Plugin_ERP.System_Modules.Login.StoreModel;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TransBetweenStors extends StoreModulePage {

    public TransBetweenStors(WebDriver driver) {
        super(driver);
    }

    private By AssertToStore = By.xpath("//small[contains(text(),'الي مخزن مطلوب')]");

    @Step("التأكيد على ان الى مخزن مطلوب")
    public Boolean AssertToStore() {
        return utilites.isDisplayed(AssertToStore);
    }
}
