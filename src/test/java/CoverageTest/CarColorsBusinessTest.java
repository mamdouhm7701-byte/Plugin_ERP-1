/* شرح الملف: اختبار ألوان السيارات والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.Allure;
import java.nio.file.*;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class CarColorsBusinessTest extends BasicDataScenarioTest {
    @Test
    public void createReopenEditAndDeleteOwnedColor() throws Exception {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CAR_SHOWROOM);
        profile.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        var page = new ScreenPage(driver);
        page.open("car-colors", "الوان السيارات");
        String marker = ownedName("CarColors", "color-lifecycle");
        String code =
                Integer.toString(
                        java.util.concurrent.ThreadLocalRandom.current().nextInt(7000000, 9999999));
        page.click("جديد");
        page.field("code", code);
        page.field("arabicName", marker);
        page.field("englishName", "English " + marker);
        page.notes(marker);
        page.click("حفظ");
        var record = basicData().awaitSaved("CarColors", marker);
        var stored = basicData().read(record);
        Assert.assertEquals(stored.get("arabicName"), marker);
        Assert.assertEquals(stored.get("englishName"), "English " + marker);
        Assert.assertEquals(stored.get("code").toString(), code);
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
        Assert.assertNotNull(basicData().read(record), "Cancelled delete removed the owned color");
        page.rowAction(marker, "delete");
        page.confirm(true);
        wait.until(d -> basicData().read(record) == null);
        profile.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        var evidence =
                Map.of(
                        "activity",
                        "CarShowroom",
                        "route",
                        "#/car-colors",
                        "profileFingerprint",
                        profile.fingerprint(),
                        "branchId",
                        profile.expected().branchId(),
                        "journey",
                        "CREATE_READ_REOPEN_UPDATE_CANCEL_DELETE_DELETE",
                        "status",
                        "PASSED",
                        "recordId",
                        record.id(),
                        "stored",
                        updated,
                        "capturedUtc",
                        java.time.Instant.now().toString());
        String json = new org.json.JSONObject(evidence).toString(2);
        Allure.addAttachment("Car color owned lifecycle", "application/json", json, ".json");
        Path path = Path.of("target/activity-business-evidence/CarShowroom/car-colors.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, json);
    }
}
