package SpecialSalesTest;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;

public final class KitchenOrdersCoverageTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage(){return new RestaurantInvoicePage(driver);}
    @Test @Story("طلب مطبخ مملوك: تحضير ثم تسليم ثم إغلاق دون تكرار خصم المخزون")
    public void preparesDeliversAndClosesOnlyOwnedOrder() {
        var table=data.table();restartPage();var restaurant=(RestaurantInvoicePage)page;
        restaurant.type("داخلية");restaurant.selectTable(data.read(table).get("code").toString());
        add();page.waitTotal("100");prepareSave();restaurant.hold();var invoice=saved();
        number(data.balance(item,1),"999");
        var kitchen=new ScreenPage(driver);kitchen.open("restaurant-orders","طلبات المطبخ");
        String row="//tr[.//td[contains(normalize-space(.),"+ScreenPage.literal(item.name())+")]]";
        Object mechanism=((org.openqa.selenium.JavascriptExecutor)driver).executeScript(
                "return document.querySelector('#app').__vue__.$store.getters['app/currentBranch'].deliveryMechanism");
        String checkbox=row+"//input[@type='checkbox']";
        clickVisibleSquare(checkbox);
        if("PrepareAndDelivery".equals(mechanism)) {
            awaitLineFlag(invoice,"isComplete");
            clickVisibleSquare(checkbox);
        } else Assert.assertEquals(mechanism,"DirectDelivery","Unknown kitchen delivery mechanism");
        awaitLineFlag(invoice,"isDelivered");
        String card="//*[self::article or self::div][contains(concat(' ',normalize-space(@class),' '),' card ') or contains(@class,'orders-modern__card')][.//tr[.//td[contains(normalize-space(.),"+ScreenPage.literal(item.name())+")]]]";
        kitchen.click(By.xpath(card+"//button[normalize-space(.)='إغلاق الفاتورة']"));
        new WebDriverWait(driver,Duration.ofSeconds(25)).until(d->Boolean.TRUE.equals(data.read(invoice).get("isComplete")));
        number(data.balance(item,1),"999");number(data.balance(item,2),"1000");
    }
    private void clickVisibleSquare(String checkbox) {
        var input=new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->d.findElements(By.xpath(checkbox)).stream().filter(e->Boolean.TRUE.equals(
                ((org.openqa.selenium.JavascriptExecutor)d).executeScript(
                        "return arguments[0].parentElement.getClientRects().length>0 && getComputedStyle(arguments[0].parentElement.parentElement).display!=='none'",e)))
                .findFirst().orElse(null));
        Plugin_ERP.Utiel.NativeCheckbox.set(driver,input,true);
    }
    private void awaitLineFlag(SpecialSalesTestData.Record invoice,String flag){
        new WebDriverWait(driver,Duration.ofSeconds(25)).until(d->{
            var lines=(List<?>)data.read(invoice).get("lines");
            return lines.stream().anyMatch(v->v instanceof Map<?,?> line&&item.id().equals(line.get("itemId"))&&Boolean.TRUE.equals(line.get(flag)));
        });
    }
}
