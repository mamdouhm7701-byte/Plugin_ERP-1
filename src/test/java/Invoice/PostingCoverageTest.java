package Invoice;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import TestSupport.FixtureJournals;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;

/** Posting is scoped to one captured owned sales code, never a branch-wide posting run. */
public final class PostingCoverageTest extends SalesScenarioTest {
    @Test @Story("ترحيل فاتورة مملوكة ثم إلغاء ترحيلها مع بقاء فاتورة التحكم دون تغيير")
    public void postsAndUnpostsOnlyCapturedOwnedInvoice() {
        prepare("Credit");fill("picker");var target=save(false);
        prepare("Credit");fill("picker");var control=save(false);
        var targetBefore=data.read(target);var controlBefore=data.read(control);
        Assert.assertEquals(targetBefore.get("isPosted"),false,"Posting journey requires an unposted owned source");
        String code=targetBefore.get("code").toString();
        Assert.assertNotEquals(code,controlBefore.get("code").toString());
        var screen=new ScreenPage(driver);screen.open("post-vouchers","الترحيل الي الحسابات");
        screen.type(By.cssSelector("input[label-text='fromCode']"),code);
        screen.type(By.cssSelector("input[label-text='toCode']"),code);screen.check("المبيعات",true);
        ((JavascriptExecutor)driver).executeScript("""
            const store=document.querySelector('#app').__vue__.$store,dispatch=store.dispatch.bind(store),code=String(arguments[0]);
            store.dispatch=function(action,payload,...rest){
              if(action==='app/create'&&['Posting/post','Posting/unPost'].includes(payload?.url)){
                const d=payload.data;
                if(String(d.fromCode)!==code||String(d.toCode)!==code||d.includeSales!==true||
                    Object.entries(d).some(([k,v])=>k.startsWith('include')&&k!=='includeSales'&&v===true))
                  throw Error('Refusing posting outside the single owned sales code');
              }
              return dispatch(action,payload,...rest);
            };
            """,code);
        Path recovery=Path.of("target","posting-recovery",target.id()+".json");
        var session=new TestSupport.FixtureSession(driver);
        FixtureJournals.write(recovery,List.of(SalesTestData.fields("origin",session.origin,"branchId",session.branchId,
                "endpoint",target.endpoint(),"id",target.id(),"code",code,"notes",target.name())));
        boolean posted=false;
        try {
            screen.click("ترحيل");awaitPosted(target,true);posted=true;screen.dismissSuccess();
            Assert.assertEquals(data.read(control).get("isPosted"),controlBefore.get("isPosted"),"Posting range affected the control invoice");
        } finally {
            if(posted||Boolean.TRUE.equals(data.read(target).get("isPosted"))){
                screen.dismissSuccess();screen.click("الغاء الترحيل");screen.visibleElement(By.cssSelector(".swal2-cancel"));screen.confirm(true);
                awaitPosted(target,false);screen.dismissSuccess();
            }
            if(!Boolean.TRUE.equals(data.read(target).get("isPosted")))FixtureJournals.write(recovery,List.of());
        }
        Assert.assertEquals(data.read(control).get("isPosted"),controlBefore.get("isPosted"));
    }
    private void awaitPosted(SalesTestData.Record record,boolean expected){
        new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->Objects.equals(data.read(record).get("isPosted"),expected));
    }
}
