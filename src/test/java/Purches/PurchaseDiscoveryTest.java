package Purches;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import io.qameta.allure.Story;
import java.nio.file.*;
import java.util.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Test;

/** Read-only discovery; no documents or fixtures are created. */
public class PurchaseDiscoveryTest extends BaseTest {
    @Test
    @Story("فحص شاشات المشتريات وحقولها دون إنشاء أو تغيير أي بيانات")
    public void inspectPurchaseScreens() throws Exception {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        var page = new SalesModulePage(driver);
        page.slAwaitSession();
        var screens = new LinkedHashMap<String, Object>();
        for (String route :
                List.of(
                        "purch-invoice/new",
                        "purchOrder/new",
                        "return-purch/new",
                        "purch-invoice-list")) {
            page.slOpen(route);
            new org.openqa.selenium.support.ui.WebDriverWait(
                            driver, java.time.Duration.ofSeconds(35))
                    .until(
                            d ->
                                    Boolean.TRUE.equals(
                                            ((JavascriptExecutor) d)
                                                    .executeScript(
                                                            "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(arguments[0]?v.$options.methods?.invoiceProvider:(v.$data?.selectedItem?.lines&&v.$options.methods?.save))return true;}return false",
                                                            route.endsWith("list"))));
            Object state =
                    ((JavascriptExecutor) driver)
                            .executeScript(
                                    """
        const q=[document.querySelector('#app').__vue__],forms=[];
        while(q.length){const v=q.shift();q.push(...(v.$children||[]));
          if(v.$data?.selectedItem?.lines||v.$options.methods?.invoiceProvider)
            forms.push({name:v.$options.name,selectedItem:v.$data.selectedItem,methods:Object.keys(v.$options.methods||{}),
              sources:Object.fromEntries(['save','getUrl','initObj','invoiceProvider'].filter(k=>v.$options.methods?.[k]).map(k=>[k,String(v.$options.methods[k])]))});}
        return {forms,labels:[...document.querySelectorAll('label,legend,th')].filter(e=>e.getClientRects().length).map(e=>e.textContent.trim()),
          resources:performance.getEntriesByType('resource').map(r=>r.name).filter(n=>/Transactions|[.]js/.test(n)).map(n=>n.split('?')[0])};
        """);
            screens.put(route, state);
        }
        Files.writeString(
                Path.of("target/purchase-discovery.json"),
                new org.json.JSONObject(screens).toString(2));
    }
}
