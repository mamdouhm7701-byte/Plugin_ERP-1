package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Read-only preparation evidence. A mounted create form never proves a branch can be saved. */
public final class BranchProvisioningReadinessCommand {
    private BranchProvisioningReadinessCommand() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected evidence JSON path");
        var driver = WebDriverFactory.initDriver();
        try {
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            var login = new LoginPage(driver);
            login.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
            login.AssertLoginPageTitle();
            var baseline = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
            driver.get(baseline.origin() + "/#/branches/new");
            var ready = new WebDriverWait(driver, Duration.ofSeconds(30));
            ready.until(
                    d ->
                            Boolean.TRUE.equals(
                                    VueComponentAccess.execute(
                                            d,
                                            """
                const route=erpVue.root()?.$router?.currentRoute;
                return route?.path==='/branches/new'&&!!route.matched?.at(-1)?.instances?.default?._isMounted;
                """)));
            new FormInteractions(
                            ready,
                            element ->
                                    ((JavascriptExecutor) driver)
                                            .executeScript(
                                                    "arguments[0].scrollIntoView({block:'center'})",
                                                    element))
                    .click(
                            By.xpath(
                                    "//*[self::a or self::button][contains(normalize-space(),'عدادات الفرع')]"));
            Object controls =
                    ready.until(
                            d ->
                                    VueComponentAccess.execute(
                                            d,
                                            """
                const fields=['discountLevel','taxPolicy','stocktakingMethod'];
                const controls=fields.map(field=>{
                  const label=document.querySelector('label[for="'+field+'"]');
                  const input=label?.parentElement.querySelector('input[type=search]');
                  return input&&input.getClientRects().length?{field,editable:!input.disabled}:null;
                });
                return controls.every(Boolean)?controls:null;
                """));
            new BranchProfile("unchanged-existing-session", baseline)
                    .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
            Path output = Path.of(args[0]);
            Files.createDirectories(output.toAbsolutePath().getParent());
            Files.writeString(
                    output,
                    new org.json.JSONObject(
                                    Map.of(
                                            "createFormMounted",
                                            true,
                                            "policyControls",
                                            controls,
                                            "saved",
                                            false,
                                            "branchCreationVerified",
                                            false,
                                            "sessionUnchanged",
                                            true,
                                            "capturedUtc",
                                            java.time.Instant.now().toString()))
                            .toString(2));
            System.out.println(
                    "Provisioning readiness evidence saved; no branch created or settings changed.");
        } finally {
            WebDriverFactory.quitDriver();
        }
    }
}
