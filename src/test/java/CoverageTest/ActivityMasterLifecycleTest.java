package CoverageTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.Allure;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Positive master lifecycle with server-assigned code; manual-code behavior is a separate contract.
 */
public final class ActivityMasterLifecycleTest extends BasicDataScenarioTest {
    private record Master(String endpoint, String route, String title) {}

    @DataProvider
    public Object[][] activityMasters() {
        return switch (BranchProfiles.selected().orElseThrow().expected().activityType()) {
            case CONCRETE ->
                    new Object[][] {
                        {"CastingTypes", "castingTypes", "نوع الصبة"},
                        {"CementTypes", "cementTypes", "نوع الأسمنت"},
                        {"MaxTemperatures", "maxTemperature", "اقصي درجة حرارة"}
                    };
            case CAR_SHOWROOM -> new Object[][] {{"CarColors", "car-colors", "الوان السيارات"}};
            default ->
                    throw new IllegalArgumentException(
                            "This lifecycle is qualified for Concrete and CarShowroom masters only");
        };
    }

    @Test(dataProvider = "activityMasters")
    public void ownedMasterCreateReopenUpdateAndDeletePreservesIdentity(
            String endpoint, String route, String title) {
        var profile = BranchProfiles.selected().orElseThrow();
        var master = new Master(endpoint, route, title);
        var page = new ScreenPage(driver);
        page.open(master.route(), master.title());
        String marker = ownedName(master.endpoint(), "activity-master");
        page.click("جديد");
        page.field("arabicName", marker);
        page.field("englishName", "English " + marker);
        page.notes(marker);
        page.click("حفظ");
        var record = basicData().awaitSaved(master.endpoint(), marker);
        var stored = basicData().read(record);
        Assert.assertEquals(stored.get("arabicName"), marker);
        Assert.assertEquals(stored.get("englishName"), "English " + marker);
        Assert.assertNotNull(stored.get("code"), "Server-assigned master code is missing");
        String code = stored.get("code").toString();
        Assert.assertFalse(code.isBlank());
        page.dismissSuccess();
        page.rowAction(marker, "edit");
        var wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        wait.until(d -> marker.equals(page.value(By.cssSelector("input[name='arabicName']"))));
        Assert.assertEquals(page.value(By.cssSelector("input[name='code']")), code);
        Assert.assertEquals(page.value(By.cssSelector("textarea")), marker);
        page.field("englishName", "Updated " + marker);
        page.click("حفظ");
        wait.until(d -> ("Updated " + marker).equals(basicData().read(record).get("englishName")));
        var updated = basicData().read(record);
        Assert.assertEquals(updated.get("id"), record.id());
        Assert.assertEquals(updated.get("arabicName"), marker);
        Assert.assertEquals(updated.get("code").toString(), code);
        page.dismissSuccess();
        page.rowAction(marker, "delete");
        page.confirm(false);
        Assert.assertNotNull(basicData().read(record), "Cancel delete removed an owned master");
        page.rowAction(marker, "delete");
        page.confirm(true);
        wait.until(d -> basicData().read(record) == null);
        profile.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        Allure.addAttachment(
                "Activity master owned lifecycle",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "profile",
                                        profile.expected().fields(),
                                        "endpoint",
                                        master.endpoint(),
                                        "route",
                                        master.route(),
                                        "stored",
                                        stored,
                                        "updated",
                                        updated,
                                        "deleted",
                                        true,
                                        "manualCodeValidation",
                                        "NOT_RUN"))
                        .toString(2),
                ".json");
    }
}
