package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/** Dependencies shared by components belonging to one page and browser session. */
public record PageContext(WebDriver driver, ElmentAction elements, WaitMangment waits,
                          BrowserAction browser, Actions actions) {}
