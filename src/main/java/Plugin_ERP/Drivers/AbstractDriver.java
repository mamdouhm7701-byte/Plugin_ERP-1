package Plugin_ERP.Drivers;

import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.WebDriver;

public abstract class AbstractDriver {
    protected final String remotehost= PropertyReader.getProperty("remoteHost");
    protected final String remoteport= PropertyReader.getProperty("remotePort");
    public abstract WebDriver createDriver( );
}
