/* شرح الملف: ملف مبدئي للصفحة الرئيسية ضمن البيانات الأساسية، ولا توجد به وظائف عمل حاليا. فائدته
 * الحالية محدودة بتمهيد التعامل مع هذه الصفحة.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.ElementActions;
import org.openqa.selenium.WebDriver;

public class MainBage {
    // Variables
    private WebDriver driver;
    private ElementActions utilites;

    // Constructors
    public MainBage(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElementActions(driver);
    }

    // Locators

    // Methods

}
