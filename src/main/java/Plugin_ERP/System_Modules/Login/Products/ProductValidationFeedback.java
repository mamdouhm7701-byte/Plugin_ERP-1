/* شرح الملف: مساعدة لقراءة رسائل نقص بيانات الصنف ومشكلات الأسعار وحالة الحفظ. فائدتها معرفة
 * البيانات المطلوب تصحيحها قبل تسجيل الصنف.
 */
package Plugin_ERP.System_Modules.Login.Products;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class ProductValidationFeedback {
    private final WebDriver driver;
    private final WebDriverWait ready;

    ProductValidationFeedback(WebDriver driver, WebDriverWait ready) {
        this.driver = driver;
        this.ready = ready;
    }

    String prValidation() {
        return ready.until(
                d -> {
                    List<String> messages = new ArrayList<>();
                    for (WebElement e :
                            d.findElements(
                                    By.cssSelector(
                                            "small.text-danger,.invalid-feedback,.swal2-title,.swal2-html-container"))) {
                        if (e.getDomProperty("textContent").isBlank()) continue;
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                d,
                                "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                e);
                        if (e.isDisplayed() && !e.getText().isBlank()) messages.add(e.getText());
                    }
                    for (WebElement input :
                            d.findElements(
                                    By.cssSelector(
                                            "form:not([novalidate]) input:invalid,form:not([novalidate]) textarea:invalid"))) {
                        String message = input.getDomProperty("validationMessage");
                        if (message == null || message.isBlank()) continue;
                        String label =
                                String.valueOf(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d,
                                                "const e=arguments[0];return e.closest('[data-title]')?.getAttribute('data-title')||Array.from(e.labels||[]).map(l=>l.textContent).join(' ')||e.name",
                                                input));
                        messages.add(label + ": " + message);
                    }
                    // Some controls show a red invalid border while their message remains d-none.
                    // Read the actual accessibility validation state, without revealing hidden
                    // text.
                    for (WebElement input :
                            d.findElements(
                                    By.cssSelector(
                                            "form input[aria-invalid='true'],form textarea[aria-invalid='true']"))) {
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                d,
                                "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                input);
                        if (!input.isDisplayed() || !input.isEnabled()) continue;
                        String label =
                                String.valueOf(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d,
                                                "const e=arguments[0];return e.closest('[data-title]')?.getAttribute('data-title')||Array.from(e.labels||[]).map(l=>l.textContent).join(' ')||e.name",
                                                input));
                        if (!label.isBlank()) messages.add(label + ": aria-invalid=true");
                    }
                    return messages.isEmpty()
                            ? null
                            : String.join(" | ", new LinkedHashSet<>(messages));
                });
    }

    Map<String, String> prPriceValidationMessages() {
        Map<String, String> messages = new LinkedHashMap<>();
        for (String name : List.of("salesPrice", "minSalesPrice", "maxSalesPrice")) {
            for (WebElement input :
                    driver.findElements(By.cssSelector("form input[name='" + name + "']"))) {
                if (!input.isDisplayed()) continue;
                List<WebElement> groups =
                        input.findElements(
                                By.xpath(
                                        "ancestor::*[self::fieldset or contains(concat(' ',normalize-space(@class),' '),' form-group ')][1]"));
                if (groups.isEmpty()) continue;
                Set<String> feedback = new LinkedHashSet<>();
                for (WebElement message :
                        groups.get(0)
                                .findElements(
                                        By.cssSelector("small.text-danger,.invalid-feedback"))) {
                    if (message.isDisplayed()) {
                        String text = message.getText().trim();
                        if (!text.isBlank()) feedback.add(text);
                    }
                }
                if (!feedback.isEmpty()) messages.put(name, String.join(" | ", feedback));
                break;
            }
        }
        return messages;
    }

    boolean prSaveDisabled() {
        List<WebElement> buttons =
                driver.findElements(By.cssSelector("form button[data-action-type='save']"));
        if (buttons.stream().noneMatch(WebElement::isDisplayed))
            buttons =
                    driver.findElements(
                            By.xpath(
                                    "//form//button[@data-action-type='save' or normalize-space()='حفظ']"));
        WebElement save =
                buttons.stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElseThrow(
                                () -> new AssertionError("Visible product save button missing"));
        return !save.isEnabled() || "true".equals(save.getDomAttribute("aria-disabled"));
    }
}
