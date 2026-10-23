package CoverageTest;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import TestSupport.BasicDataScenarioTest;
import java.util.*;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;

public final class UsersCoverageTest extends BasicDataScenarioTest {
    private ScreenPage page;
    private String marker, userName;
    private void chooseCurrentBranch() {
        String branch=(String)((org.openqa.selenium.JavascriptExecutor)driver).executeScript(
                "return document.querySelector('#app').__vue__.$store.getters['app/currentBranch'].arabicName");
        page.choose("الفروع",branch);
    }
    private void form() {
        page=new ScreenPage(driver);page.open("users/new","إضافة مستخدم");
        marker=ownedName("users","user-screen"); userName="qa_cov_"+UUID.randomUUID().toString().replace("-", "");
        page.field("fullName",marker); page.field("userName",userName);
        String password=UUID.randomUUID()+"aA1!";
        page.field("new-Password",password);page.field("confirmPassword",password);
    }
    @Test @Story("إنشاء مستخدم محدود من الشاشة دون صلاحيات مدير") public void createsLimitedUserAndReadsBack() {
        form();chooseCurrentBranch(); page.click("حفظ");
        var saved=basicData().awaitSaved("users",marker); var stored=basicData().read(saved);
        Assert.assertEquals(stored.get("fullName"),marker);Assert.assertEquals(stored.get("userName"),userName);
        Assert.assertEquals(stored.get("isAdmin"),false);Assert.assertEquals(stored.get("isActive"),true);
        Assert.assertTrue(((List<?>)stored.get("permissions")).isEmpty(),"No permissions were selected");
        Assert.assertEquals(((List<?>)stored.get("branches")).size(),1);
    }
    @Test @Story("تأكيد كلمة المرور غير المطابق يمنع الحفظ") public void mismatchedPasswordsCannotSave() {
        form();page.field("confirmPassword","Different-Aa1!");
        Assert.assertFalse(page.visibleElement(By.cssSelector("button[data-action-type='save']")).isEnabled());
        Assert.assertNull(basicData().captured("users",marker));
    }
    @Test @Story("اسم المستخدم غير المسموح يمنع الحفظ") public void invalidUserNameCannotSave() {
        form();page.field("userName","invalid user name");
        Assert.assertFalse(page.visibleElement(By.cssSelector("button[data-action-type='save']")).isEnabled());
        Assert.assertNull(basicData().captured("users",marker));
    }
    @Test @Story("إلغاء إنشاء مستخدم لا ينشئ حسابًا") public void leavingFormDoesNotCreateAccount() {
        form();page.open("users","المستخدمين");Assert.assertNull(basicData().captured("users",marker));
    }
    @Test @Story("رفض نسبة خصم مستخدم تتجاوز مائة بالمائة") public void rejectsExcessiveDiscountAllowance() {
        form();chooseCurrentBranch();page.field("maxAllowedDiscount","101");page.click("حفظ");
        String message=page.visibleElement(By.cssSelector(".swal2-html-container")).getText();
        Assert.assertTrue(message.contains("100")||message.contains("١٠٠"),"Expected maximum-discount feedback: "+message);
        Assert.assertNull(basicData().captured("users",marker));
    }
}
