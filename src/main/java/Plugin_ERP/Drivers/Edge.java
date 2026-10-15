package Plugin_ERP.Drivers;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class Edge extends AbstractDriver{

    private EdgeOptions getOptions()

    {//options
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--incognito");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        String executionType = PropertyReader.getProperty("ExecutionType");
        if ("LocalHeadless".equalsIgnoreCase(executionType)) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.setAcceptInsecureCerts(true);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);


        return options;
    }

    @Override
    public WebDriver createDriver() {
        String type = PropertyReader.getProperty("ExecutionType");

        if (type == null) {
            LogUtils.error("ExecutionType is null");
            throw new IllegalArgumentException("ExecutionType is null");
        }

        // Local + LocalHeadless
        if (type.equalsIgnoreCase("Local") || type.equalsIgnoreCase("LocalHeadless")) {
            return new EdgeDriver(getOptions());
        }

        // Remote
        if (type.equalsIgnoreCase("Remote")) {
            try {
                String host = remotehost;
                String port = remoteport;

                String gridUrl = "http://" + host + ":" + port + "/wd/hub";
                return new RemoteWebDriver(new URL(gridUrl), getOptions());
            } catch (Exception e) {
                LogUtils.error("Failed to create RemoteWebDriver: " + e.getMessage());
                throw new RuntimeException("Failed to create RemoteWebDriver", e);
            }
        }

        LogUtils.error("Invalid Execution Type: " + type);
        throw new IllegalArgumentException("Invalid Execution Type: " + type);
    }
}
