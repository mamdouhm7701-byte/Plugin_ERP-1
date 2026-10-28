/* شرح الملف: فتح إعدادات الفرع ومراجعة النشاط والخصم والضريبة والجرد.
 */
package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class BranchSettingsPage implements BranchConfigurationWorkflow.Settings {
    private final WebDriver driver;
    private final BranchSnapshot identity;
    private final WebDriverWait ready;
    private final FormInteractions actions;
    private static final Map<String, String> LABELS =
            Map.of(
                    "activityType",
                    "نوع النشاط",
                    "discountLevel",
                    "نوع الخصم",
                    "taxPolicy",
                    "سياسة الضريبة",
                    "stocktakingMethod",
                    "نظام الجرد");

    public BranchSettingsPage(WebDriver driver, BranchSnapshot identity) {
        this.driver = driver;
        this.identity = identity;
        ready = new WebDriverWait(driver, Duration.ofSeconds(40));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        actions =
                new FormInteractions(
                        ready,
                        element ->
                                ((JavascriptExecutor) driver)
                                        .executeScript(
                                                "arguments[0].scrollIntoView({block:'center'})",
                                                element));
    }

    public BranchSnapshot current() {
        return BranchProfileReader.capture(driver, Duration.ofSeconds(30));
    }

    public void openForm() {
        var actual = current();
        if (!identity.origin().equals(actual.origin())
                || !identity.branchId().equals(actual.branchId()))
            throw new IllegalStateException("Branch settings identity changed");
        driver.get(
                identity.origin()
                        + "/#/branches/edit/"
                        + java.net.URLEncoder.encode(
                                identity.branchId(), java.nio.charset.StandardCharsets.UTF_8));
        ready.until(d -> readForm() != null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readForm() {
        return (Map<String, Object>)
                VueComponentAccess.execute(
                        driver,
                        """
            const q=[erpVue.root()],seen=new Set();
            while(q.length){const vm=q.shift();if(!vm||seen.has(vm))continue;seen.add(vm);q.push(...erpVue.children(vm));
              const value=vm.$data?.selectedItem;
              if(value&&String(value.id)===arguments[0]&&['activityType','discountLevel','taxPolicy','stocktakingMethod'].every(k=>value[k]!=null))
                return JSON.parse(JSON.stringify(value));
            }return null;
            """,
                        identity.branchId());
    }

    public Map<String, Object> formValues() {
        return ready.until(d -> readForm());
    }

    public void requireEditable(Set<String> fields) {
        for (String field : fields) {
            var input = control(field);
            if (!input.isEnabled())
                throw new IllegalStateException(
                        "Branch setting '"
                                + field
                                + "' is disabled by the application; no draft edit or save was attempted");
        }
    }

    private WebElement control(String field) {
        if (!LABELS.containsKey(field))
            throw new IllegalArgumentException("Unsupported settings field");
        String selector =
                field.equals("activityType")
                        ? "//fieldset[./legend[normalize-space()='"
                                + LABELS.get(field)
                                + "']]//input[@type='search']"
                        : "//label[@for='" + field + "']/parent::div//input[@type='search']";
        if (driver.findElements(By.xpath(selector)).stream().noneMatch(WebElement::isDisplayed)) {
            String tab = field.equals("activityType") ? "البيانات" : "عدادات الفرع";
            actions.click(
                    By.xpath(
                            "//*[self::a or self::button][contains(normalize-space(),'"
                                    + tab
                                    + "')]"));
        }
        return ready.until(
                d ->
                        d.findElements(By.xpath(selector)).stream()
                                .filter(WebElement::isDisplayed)
                                .findFirst()
                                .orElse(null));
    }

    public void select(String field, String code) {
        if (!LABELS.containsKey(field))
            throw new IllegalArgumentException("Unsupported settings field");
        if (code.equals(formValues().get(field))) return;
        if (field.equals("activityType")
                && driver
                        .findElements(
                                By.cssSelector(
                                        "fieldset[label-text='activityType'] input[type='search']"))
                        .stream()
                        .noneMatch(WebElement::isDisplayed))
            actions.click(
                    By.xpath(
                            "//*[@role='tab'][contains(normalize-space(),'البيانات')] | //a[contains(@class,'nav-link')][contains(normalize-space(),'البيانات')]"));
        if (!field.equals("activityType"))
            actions.click(
                    By.xpath(
                            "//*[self::a or self::button][contains(normalize-space(),'عدادات الفرع')]"));
        Object label =
                VueComponentAccess.execute(
                        driver,
                        """
            const q=[erpVue.root()],seen=new Set(),labels=new Set();
            while(q.length){const vm=q.shift();if(!vm||seen.has(vm))continue;seen.add(vm);q.push(...erpVue.children(vm));
              for(const key of Object.keys(vm)){const values=vm[key];if(!Array.isArray(values))continue;
                for(const value of values)if(value?.id===arguments[0]&&typeof value.arabicName==='string')labels.add(value.arabicName);
              }
            }
            if(labels.size!==1)throw new Error('Missing or ambiguous persisted policy option');
            return [...labels][0];
            """,
                        code);
        String selector =
                field.equals("activityType")
                        ? "//fieldset[./legend[normalize-space()='"
                                + LABELS.get(field)
                                + "']]//input[@type='search']"
                        : "//label[@for='" + field + "']/parent::div//input[@type='search']";
        var input =
                ready.until(
                        d ->
                                d.findElements(By.xpath(selector)).stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        if (!input.isEnabled())
            throw new IllegalStateException(
                    "Branch setting '"
                            + field
                            + "' is disabled by the application; use a configurable test branch. No save was attempted");
        actions.choose(By.xpath(selector), label.toString(), FormInteractions.OptionMatch.EXACT);
        ready.until(d -> code.equals(formValues().get(field)));
    }

    public void save() {
        String previousRoute = driver.getCurrentUrl();
        actions.click(By.cssSelector("button[data-action-type='save'][type='submit']"));
        ready.withMessage("Branch settings save was not confirmed; run will not start")
                .until(
                        d ->
                                !previousRoute.equals(d.getCurrentUrl())
                                        || d
                                                .findElements(
                                                        By.cssSelector(
                                                                ".swal2-success, .toast-success, .b-toast-success"))
                                                .stream()
                                                .anyMatch(WebElement::isDisplayed));
    }

    public void reloadSession() {
        driver.get(identity.origin() + "/#/");
        driver.navigate().refresh();
        current();
    }
}
