package Plugin_ERP.Bots;

import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.ArrayList;

public class WaitMangment {
    private WebDriver driver;

    public WaitMangment(WebDriver driver) {
        this.driver = driver;
    }

    public FluentWait<WebDriver> fluentWait() {
        return new FluentWait<>(driver)
                .withTimeout(
                        java.time.Duration.ofSeconds(
                                Long.parseLong(PropertyReader.getProperty("defultwait"))))
                .pollingEvery(Duration.ofMillis(500))
                .ignoreAll(getExceptions());
    }

    private ArrayList<Class<? extends Exception>> getExceptions() {
        ArrayList<Class<? extends Exception>> exceptions = new ArrayList<>();
        exceptions.add(org.openqa.selenium.NoSuchElementException.class);
        exceptions.add(org.openqa.selenium.StaleElementReferenceException.class);
        exceptions.add(org.openqa.selenium.ElementNotInteractableException.class);
        exceptions.add(ElementClickInterceptedException.class);
        return exceptions;
    }
}
