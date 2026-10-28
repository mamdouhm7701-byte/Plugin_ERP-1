/* شرح الملف: مراجعة ظهور الحجز الصحيح ضمن أذون التسليم.
 */
package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import io.qameta.allure.Allure;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class ConcreteReservationEvidence {
    private ConcreteReservationEvidence() {}

    static void verify(
            WebDriver driver,
            SalesModulePage page,
            SalesTestData data,
            SalesTestData.Record item,
            SalesTestData.Record reservation,
            Map<String, Object> stored) {
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .until(
                        d ->
                                d
                                        .findElements(
                                                org.openqa.selenium.By.cssSelector(".modal.show"))
                                        .stream()
                                        .anyMatch(org.openqa.selenium.WebElement::isDisplayed));
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Boolean.TRUE.equals(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d,
                                                """
                    const r=erpVue.root().$router.currentRoute,vm=r.matched.at(-1).instances.default;
                    return Array.isArray(vm.allData)&&vm.allData.some(row=>String(row.id)===String(arguments[0]));
                    """,
                                                reservation.id())));
        Allure.addAttachment(
                "Owned reservation in delivery picker",
                "text/html",
                page.slDiagnosticMarkup(),
                ".html");
        Object source =
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver,
                        """
            const r=erpVue.root().$router.currentRoute,vm=r.matched.at(-1).instances.default;
            return {rows:vm.allData?.filter(row=>String(row.id)===String(arguments[0])),keys:Object.keys(vm.$data||{}),
              methods:Object.fromEntries(Object.entries(vm.$options.methods||{}).filter(([key])=>/res|row|line|import|add/i.test(key)).map(([key,value])=>[key,String(value)]))};
            """,
                        reservation.id());
        Allure.addAttachment(
                "Owned reservation source identity",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "storedReservation",
                                        stored,
                                        "picker",
                                        source,
                                        "stockBefore",
                                        1000,
                                        "stockAfterReservation",
                                        data.balance(item, 1)))
                        .toString(2),
                ".json");
    }
}
