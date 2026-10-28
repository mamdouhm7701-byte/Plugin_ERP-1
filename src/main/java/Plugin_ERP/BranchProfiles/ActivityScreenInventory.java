/* شرح الملف: حصر الشاشات المتاحة لكل نشاط.
 */
package Plugin_ERP.BranchProfiles;

import Plugin_ERP.Util.VueComponentAccess;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;

public final class ActivityScreenInventory {
    private ActivityScreenInventory() {}

    public static Map<String, Object> capture(WebDriver driver) {
        var branch = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        Object navigation =
                VueComponentAccess.execute(
                        driver,
                        """
            const root=erpVue.root();
            const walk=(routes,parent='')=>routes.flatMap(route=>{
              const path=route.path.startsWith('/')?route.path:(parent.endsWith('/')?parent.slice(0,-1):parent)+'/'+route.path;
              return [{path,name:route.name??null},...walk(route.children||[],path)];
            });
            const links=[...document.querySelectorAll('.main-menu a[href], .vertical-menu a[href]')]
              .map(a=>({route:a.hash||'',label:a.textContent.trim(),visible:!!a.getClientRects().length}))
              .filter(a=>a.route&&a.label);
            return {navigationLinks:links,registeredRoutes:walk(root.$router.options.routes)};
            """);
        new BranchProfile("screen-inventory", branch)
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        var result = new LinkedHashMap<String, Object>();
        result.put("branch", branch.fields());
        result.put("capturedUtc", java.time.Instant.now().toString());
        result.put("evidence", navigation);
        result.put(
                "interpretation",
                "Menu presence and route registration only; permissions and business behavior unverified");
        return result;
    }
}
