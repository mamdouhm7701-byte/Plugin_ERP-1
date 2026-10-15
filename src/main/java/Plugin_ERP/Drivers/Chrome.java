package Plugin_ERP.Drivers;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Chrome extends AbstractDriver {
    private ChromeOptions getOptions()

    {//options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--force-device-scale-factor=0.9");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--incognito");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        Map<String, Object> downloadPreferences = new HashMap<>();
        downloadPreferences.put(
                "download.default_directory",
                Path.of(System.getProperty("user.dir"), "target", "cash-export-downloads")
                        .toAbsolutePath().toString()
        );
        downloadPreferences.put("download.prompt_for_download", false);
        downloadPreferences.put("download.directory_upgrade", true);
        options.setExperimentalOption("prefs", downloadPreferences);
        String executionType = PropertyReader.getProperty("ExecutionType");
        if ("LocalHeadless".equalsIgnoreCase(executionType)) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.setAcceptInsecureCerts(true);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);


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
            return new ChromeDriver(getOptions());
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

