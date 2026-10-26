package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElementActions;
import Plugin_ERP.Bots.WaitManagement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/** Dependencies shared by components belonging to one page and browser session. */
public record PageContext(
        WebDriver driver,
        ElementActions elements,
        WaitManagement waits,
        BrowserAction browser,
        Actions actions) {}
