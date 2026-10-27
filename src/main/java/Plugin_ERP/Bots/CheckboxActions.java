package Plugin_ERP.Bots;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class CheckboxActions extends BaseAction {

    public CheckboxActions(WebDriver driver, WebDriverWait wait, WaitManagement waitManagement) {
        super(driver, wait, waitManagement);
    }

    public void stableFastSetCheckboxByText(String text, boolean shouldBeChecked) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        WebElement checkbox = null;

        String[] attempts = {
            "//label[contains(normalize-space(),'"
                    + text
                    + "')]/following::input[@type='checkbox'][1]",
            "(//*[self::div or self::span or self::p][contains(normalize-space(),'"
                    + text
                    + "')]/following::input[@type='checkbox'])[1]",
            "(//input[@type='checkbox'][preceding::*[contains(normalize-space(),'"
                    + text
                    + "')]])[1]",
            "(//input[@type='checkbox'][following::*[contains(normalize-space(),'"
                    + text
                    + "')]])[1]",
            "(//*[contains(normalize-space(),'"
                    + text
                    + "')]/descendant::input[@type='checkbox'])[1]"
        };

        for (String xp : attempts) {
            try {
                checkbox = driver.findElement(By.xpath(xp));
                if (checkbox != null) break;
            } catch (Exception ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "CheckboxActions fallback: " + ignored.getClass().getSimpleName());
            }
        }

        if (checkbox == null) throw new RuntimeException("❌ Checkbox not found for: " + text);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", checkbox);

        boolean current = checkbox.isSelected();
        if (current == shouldBeChecked) return;

        js.executeScript("arguments[0].click();", checkbox);

        try {
            WebElement finalCheckbox = checkbox;
            wait.until(d -> finalCheckbox.isSelected() == shouldBeChecked);
        } catch (Exception e) {
            throw new AssertionError(
                    "❌ Checkbox '"
                            + text
                            + "' expected: "
                            + shouldBeChecked
                            + " but still: "
                            + checkbox.isSelected());
        }
    }

    public void stableFastSetCheckboxByTextCloude(String text, boolean shouldBeChecked) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        WebElement clickTarget = null;
        WebElement inputCheckbox = null;

        String[] labelAttempts = {
            "//legend[contains(normalize-space(),'"
                    + text
                    + "')]/following-sibling::div//label[contains(@class,'custom-control-label')][1]",
            "//fieldset[.//*[contains(normalize-space(),'"
                    + text
                    + "')]]//label[contains(@class,'custom-control-label')][1]",
            "//*[contains(normalize-space(),'"
                    + text
                    + "')]/following::label[contains(@class,'custom-control-label')][1]"
        };

        String[] inputAttempts = {
            "//legend[contains(normalize-space(),'"
                    + text
                    + "')]/following-sibling::div//input[@type='checkbox'][1]",
            "//label[contains(normalize-space(),'"
                    + text
                    + "')]/following::input[@type='checkbox'][1]",
            "(//*[self::div or self::span or self::p][contains(normalize-space(),'"
                    + text
                    + "')]/following::input[@type='checkbox'])[1]",
            "(//input[@type='checkbox'][preceding::*[contains(normalize-space(),'"
                    + text
                    + "')]])[1]",
            "(//input[@type='checkbox'][following::*[contains(normalize-space(),'"
                    + text
                    + "')]])[1]",
            "(//*[contains(normalize-space(),'"
                    + text
                    + "')]/descendant::input[@type='checkbox'])[1]"
        };

        // دور على الـ input عشان تعرف الحالة الحالية
        for (String xp : inputAttempts) {
            try {
                inputCheckbox = driver.findElement(By.xpath(xp));
                if (inputCheckbox != null) break;
            } catch (Exception ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "CheckboxActions fallback: " + ignored.getClass().getSimpleName());
            }
        }

        if (inputCheckbox == null) throw new RuntimeException("❌ Checkbox not found for: " + text);

        // لو الحالة مظبوطة خلاص
        boolean current = inputCheckbox.isSelected();
        System.out.println("Checkbox = " + text);
        System.out.println("Found ID = " + inputCheckbox.getAttribute("id"));
        System.out.println("Current = " + current);
        System.out.println("Expected = " + shouldBeChecked);
        if (current == shouldBeChecked) return;

        // دور على الـ label عشان تضغط
        for (String xp : labelAttempts) {
            try {
                clickTarget = driver.findElement(By.xpath(xp));
                if (clickTarget != null) break;
            } catch (Exception ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "CheckboxActions fallback: " + ignored.getClass().getSimpleName());
            }
        }

        // لو مفيش label اضغط على الـ input
        if (clickTarget == null) clickTarget = inputCheckbox;

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", clickTarget);
        System.out.println("Set Checkbox: " + text);
        System.out.println("ID = " + inputCheckbox.getAttribute("id"));
        System.out.println("Selected Before = " + inputCheckbox.isSelected());
        // ✅ JS click دايماً
        js.executeScript("arguments[0].click();", clickTarget);
        System.out.println("Selected After = " + inputCheckbox.isSelected());

        // تأكيد
        try {
            WebElement finalInput = inputCheckbox;
            wait.until(d -> finalInput.isSelected() == shouldBeChecked);
        } catch (Exception e) {
            throw new AssertionError(
                    "❌ Checkbox '"
                            + text
                            + "' expected: "
                            + shouldBeChecked
                            + " but still: "
                            + inputCheckbox.isSelected());
        }
    }

    public void assertCheckboxState(String text, boolean expectedState) {

        WebElement checkbox;

        List<WebElement> dataTitle =
                driver.findElements(
                        By.xpath("//*[@data-title='" + text + "']//input[@type='checkbox']"));

        if (!dataTitle.isEmpty()) {
            checkbox = dataTitle.get(0);
        } else {
            checkbox =
                    driver.findElement(
                            By.xpath(
                                    "//legend[contains(normalize-space(),'"
                                            + text
                                            + "')]/following::input[@type='checkbox'][1]"));
        }

        Assert.assertEquals(
                checkbox.isSelected(),
                expectedState,
                "Checkbox '"
                        + text
                        + "' expected to be "
                        + (expectedState ? "checked" : "unchecked")
                        + " but was "
                        + (checkbox.isSelected() ? "checked" : "unchecked"));
    }

    public void selectRadioButton(String optionName) {
        By labelLocator =
                By.xpath(
                        "//div[contains(@class,'btn-group')]"
                                + "//label[contains(@class,'btn')][.//span[normalize-space()='"
                                + optionName
                                + "']]");
        waitManagement
                .fluentWait()
                .withMessage("Radio selection unavailable: " + optionName)
                .until(
                        d -> {
                            WebElement label = d.findElement(labelLocator);
                            String classes = label.getDomAttribute("class");
                            if (classes != null
                                    && java.util.Arrays.asList(classes.split("\\s+"))
                                            .contains("active")) return true;
                            if (!label.isDisplayed() || !label.isEnabled()) return false;
                            scrollIntoView(label);
                            label.click();
                            return false;
                        });
    }

    public void setCheckboxForDealer(String title, boolean shouldBeChecked) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // الكونتينر الخاص بالـ Checkbox
        WebElement container =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//*[@data-title='" + title + "']")));

        // الـ Input الحقيقي
        WebElement checkbox = container.findElement(By.cssSelector("input[type='checkbox']"));

        boolean currentState = checkbox.isSelected();

        System.out.println("Title = " + title);
        System.out.println("ID = " + checkbox.getAttribute("id"));
        System.out.println("Before = " + currentState);

        if (currentState == shouldBeChecked) {
            return;
        }

        // الـ Label المرتبط بالـ Input
        String id = checkbox.getAttribute("id");

        WebElement label = container.findElement(By.cssSelector("label[for='" + id + "']"));

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", label);
        js.executeScript("arguments[0].click();", label);

        wait.until(driver -> checkbox.isSelected() == shouldBeChecked);

        System.out.println("After = " + checkbox.isSelected());

        Assert.assertEquals(
                checkbox.isSelected(),
                shouldBeChecked,
                "Checkbox '" + title + "' state is incorrect.");
    }

    public void setCheckboxForTax(String text, boolean shouldBeChecked) {

        WebElement checkbox =
                driver.findElement(
                        By.xpath(
                                "//legend[contains(normalize-space(),'"
                                        + text
                                        + "')]/following::input[@type='checkbox'][1]"));

        if (checkbox.isSelected() == shouldBeChecked) return;

        String id = checkbox.getAttribute("id");

        WebElement label = driver.findElement(By.cssSelector("label[for='" + id + "']"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
    }

    public void setCheckbox(String text, boolean shouldBeChecked) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement checkbox;
        WebElement label;

        // الحالة الأولى: data-title
        List<WebElement> dataTitle =
                driver.findElements(By.xpath("//*[@data-title='" + text + "']"));

        if (!dataTitle.isEmpty()) {

            WebElement container = dataTitle.get(0);

            checkbox = container.findElement(By.cssSelector("input[type='checkbox']"));

            String id = checkbox.getAttribute("id");

            label = container.findElement(By.cssSelector("label[for='" + id + "']"));
        }
        // الحالة الثانية: legend
        else {

            WebElement fieldset =
                    driver.findElement(
                            By.xpath(
                                    "//fieldset[.//legend[contains(normalize-space(),'"
                                            + text
                                            + "')]]"));

            checkbox = fieldset.findElement(By.cssSelector("input[type='checkbox']"));

            String id = checkbox.getAttribute("id");

            label = fieldset.findElement(By.cssSelector("label[for='" + id + "']"));
        }

        if (checkbox.isSelected() == shouldBeChecked) return;

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", label);
        js.executeScript("arguments[0].click();", label);

        wait.until(d -> checkbox.isSelected() == shouldBeChecked);
    }
}
