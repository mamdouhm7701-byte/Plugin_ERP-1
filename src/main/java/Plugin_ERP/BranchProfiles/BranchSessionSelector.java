package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.VueComponentAccess;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Explicit opt-in branch selection by server identifier, followed by complete profile verification.
 */
public final class BranchSessionSelector {
    private BranchSessionSelector() {}

    public static void select(WebDriver driver, BranchProfile target) {
        var initial = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        if (!initial.origin().equals(target.expected().origin()))
            throw new IllegalStateException("Selected branch belongs to another origin");
        if (initial.branchId().equals(target.expected().branchId())) {
            target.verify(initial);
            return;
        }
        Object response =
                VueComponentAccess.executeAsync(
                        driver,
                        """
            const done=arguments[arguments.length-1];
            erpVue.root().$store.dispatch('app/get',{url:'Branches',id:arguments[0]})
              .then(value=>done({id:value?.id,name:value?.arabicName}),
                    error=>done({error:'Cannot resolve selected branch'}));
            """,
                        target.expected().branchId());
        if (!(response instanceof Map<?, ?> branch)
                || !target.expected().branchId().equals(String.valueOf(branch.get("id")))
                || !(branch.get("name") instanceof String name)
                || name.isBlank())
            throw new IllegalStateException("Could not resolve selected branch identity");
        var ready = new WebDriverWait(driver, Duration.ofSeconds(30));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        String text = xpathLiteral(name);
        By group =
                By.xpath(
                        "//li[.//a[@role='menuitem'][normalize-space()="
                                + text
                                + "]]/a[@role='button'][@aria-haspopup='true']");
        ready.until(
                d -> {
                    var toggles =
                            d.findElements(group).stream().filter(WebElement::isDisplayed).toList();
                    if (toggles.size() != 1)
                        throw new IllegalStateException(
                                "Selected branch menu is missing or ambiguous");
                    // Native keyboard activation also works while dashboard toasts cover the
                    // header.
                    toggles.getFirst().sendKeys(Keys.ENTER);
                    return true;
                });
        By option =
                By.xpath(
                        "//li[./a[@role='button'][@aria-haspopup='true']]//a[@role='menuitem'][normalize-space()="
                                + text
                                + "]");
        ready.until(
                d -> {
                    var options =
                            d.findElements(option).stream()
                                    .filter(WebElement::isDisplayed)
                                    .toList();
                    if (options.isEmpty()) return false;
                    if (options.size() != 1)
                        throw new IllegalStateException("Selected branch name is ambiguous");
                    // A long native branch menu can extend below the fixed header viewport.
                    // Activate the exact resolved link using its normal keyboard behavior.
                    options.getFirst().sendKeys(Keys.ENTER);
                    return true;
                });
        ready.until(
                d ->
                        target.expected()
                                .branchId()
                                .equals(
                                        BranchProfileReader.capture(d, Duration.ofSeconds(5))
                                                .branchId()));
        driver.navigate().refresh();
        target.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
    }

    private static String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }
}
