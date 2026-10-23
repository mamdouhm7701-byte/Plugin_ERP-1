package CoverageTest;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;
public final class AuditLogCoverageTest extends BaseTest {
    @Test @Story("الملف التاريخي يفتح مساره وجدوله دون التحويل إلى الإحصائيات")
    public void directAuditRouteDisplaysAuditTrail() {
        var page=new ScreenPage(driver);
        try { page.open("logs","الملف التاريخي"); }
        catch(org.openqa.selenium.TimeoutException failure) {
            throw new AssertionError("ERP audit route did not render its title/table; actual URL="+driver.getCurrentUrl(),failure);
        }
        Assert.assertTrue(driver.getCurrentUrl().contains("#/logs"),"Audit log route unexpectedly redirected");
        Assert.assertTrue(page.visible(By.cssSelector("table")),"Audit trail table must be available");
    }
}
