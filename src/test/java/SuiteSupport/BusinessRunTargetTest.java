/* شرح الملف: اختبارات تحديد البيئة والرابط وبيانات الدخول المطلوبة لهذه التجربة.
 */
package SuiteSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BusinessRunTargetTest {
    @Test
    public void branchCreationAcceptsSelectedCloudAndTestOriginsButRejectsRedirectedTenants() {
        BusinessBranchPreparation.verifyCreationOrigin(
                "https://alpha.example", "https://alpha.example/app");
        BusinessBranchPreparation.verifyCreationOrigin(
                "http://test.example:15055", "http://test.example:15055");
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BusinessBranchPreparation.verifyCreationOrigin(
                                "https://another.example", "https://alpha.example"));
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BusinessBranchPreparation.verifyCreationOrigin(
                                "http://test.example:15056", "http://test.example:15055"));
    }

    private final Map<String, String> credentials =
            Map.of(
                    "Alpha.Username",
                    "alpha-user",
                    "Alpha.Password",
                    "alpha-secret",
                    "Test.Username",
                    "test-user",
                    "Test.Password",
                    "test-secret");

    @Test
    public void cloudTenantAndRunOnlyCredentialsUseTheirOwnKeys() {
        var target =
                new BusinessRunTarget("Cloud", "Alpha", "https://alpha.example/#/login", "", null);
        var settings = target.settings(credentials::get);
        Assert.assertEquals(settings.get("Environment"), "Cloud");
        Assert.assertEquals(settings.get("Tenant"), "Alpha");
        Assert.assertEquals(settings.get("Alpha"), "https://alpha.example");
        Assert.assertEquals(settings.get("Alpha.Username"), "alpha-user");
        Assert.assertFalse(settings.containsKey("TestURL"));
        Assert.assertFalse(target.toString().contains("secret"));
        Assert.assertFalse(target.description().containsKey("password"));
    }

    @Test
    public void alternateTestEndpointUsesSelectedUrlAndTestCredentialKeys() {
        var target =
                new BusinessRunTarget("Test", "NewTestURL", "http://test.example:15055/", "", null);
        var settings = target.settings(credentials::get);
        Assert.assertEquals(settings.get("TestURL"), "http://test.example:15055");
        Assert.assertEquals(settings.get("Test.Username"), "test-user");
        Assert.assertEquals(settings.get("Test.Password"), "test-secret");
    }

    @Test
    public void newTenantAcceptsExplicitCredentialsWithoutStoredSettings() {
        char[] password = "run-secret".toCharArray();
        var target =
                new BusinessRunTarget(
                        "Cloud", "NewTenant", "https://new.example", "run-user", password);
        Arrays.fill(password, 'x');
        var settings = target.settings(key -> null);
        Assert.assertEquals(settings.get("NewTenant.Password"), "run-secret");
        Assert.assertEquals(settings.get("NewTenant.Username"), "run-user");
        Assert.assertFalse(target.toString().contains("run-secret"));
        target.clearSecrets();
    }

    @Test
    public void discoverySeparatesCloudTenantsFromTestUrls() {
        var properties = new Properties();
        properties.setProperty("Alpha", "https://alpha.example");
        properties.setProperty("Alpha.Username", "user");
        properties.setProperty("TestURL", "http://test.example:15055");
        properties.setProperty("NewTestURL", "http://newtest.example");
        properties.setProperty("UnrelatedUrl", "https://api.example");
        Assert.assertEquals(BusinessRunTarget.tenants(properties, "Cloud"), List.of("Alpha"));
        Assert.assertEquals(
                new HashSet<>(BusinessRunTarget.tenants(properties, "Test")),
                Set.of("TestURL", "NewTestURL"));
    }

    @Test
    public void invalidOrIncompleteTargetsStopBeforeAnyApiAction() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        new BusinessRunTarget(
                                "Cloud", "Alpha", "https://user:secret@example.com", "", null));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        new BusinessRunTarget(
                                "Cloud", "../Alpha", "https://alpha.example", "", null));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> new BusinessRunTarget("Other", "Alpha", "https://alpha.example", "", null));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        new BusinessRunTarget("Test", "TestURL", "http://test.example", "", null)
                                .settings(key -> null));
    }
}
