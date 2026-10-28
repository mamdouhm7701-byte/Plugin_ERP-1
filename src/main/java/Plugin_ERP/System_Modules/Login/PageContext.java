/* شرح الملف: مساعدة تجمع احتياجات التعامل مع الشاشة في مكان واحد. فائدتها إتاحة إدخال البيانات
 * والتنقل والحفظ بصورة متسقة.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElementActions;
import Plugin_ERP.Bots.WaitManagement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public record PageContext(
        WebDriver driver,
        ElementActions elements,
        WaitManagement waits,
        BrowserAction browser,
        Actions actions) {}
