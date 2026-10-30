/* شرح الملف: تنفيذ تجهيز وقراءة وحذف بيانات التجربة على الفرع الصحيح، ضمن الأصناف.
 */
package ProductsTest;

import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.WebDriver;

final class ProductsFixtureApi {
    private final Plugin_ERP.BranchProfiles.BranchProfileGuard profileGuard =
            Plugin_ERP.BranchProfiles.BranchProfileGuard.capture();
    private final WebDriver driver;
    private final Runnable contextGuard;

    ProductsFixtureApi(WebDriver driver, Runnable contextGuard) {
        this.driver = driver;
        this.contextGuard = contextGuard;
    }

    static final class ApiFailure extends TestSupport.FixtureApiFailure {

        ApiFailure(int status, String message) {
            super(status, message);
        }
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        return TestSupport.FixtureCreateRetry.run(
                action, endpoint, () -> callOnce(action, endpoint, values));
    }

    private Map<String, Object> callOnce(
            String action, String endpoint, Map<String, Object> values) {
        Plugin_ERP.BranchProfiles.VerificationSnapshot.verify(
                driver,
                () -> {
                    profileGuard.verify(driver);
                    contextGuard.run();
                });
        Plugin_ERP.Util.RunEnvironmentGate.beforeCreate(action);
        driver.manage()
                .timeouts()
                .scriptTimeout(Duration.ofSeconds(action.equals("delete") ? 60 : 30));
        Object result =
                Plugin_ERP.Util.VueComponentAccess.executeAsync(
                        driver,
                        """
        const [action, endpoint, values, done] = arguments;
        const root = erpVue.root();
        if (!root?.$store) { done({error:'Authenticated ERP session is not ready'}); return; }
        const queue = [root]; let branchId = null;
        while (queue.length) {
          const vm = queue.shift();
          if (vm.currentBranch?.id) { branchId = vm.currentBranch.id; break; }
          queue.push(...(erpVue.children(vm) || []));
        }
        const args = {url:endpoint};
        if (action === 'create') {
          if (!branchId) { done({error:'No current branch selected'}); return; }
          args.data = {...values, branchId};
        } else if(action === 'update') {args.data=values;} else { args.id = values.id; }
        root.$store.dispatch('app/' + action, args).then(result => {
          if (action === 'delete') { done({ok:true}); return; }
          const entity = result?.data || result;
          done({id: entity?.id || (typeof entity === 'string' || typeof entity === 'number' ? entity : null),
            code: entity?.code || null,
            entity: action === 'get' ? JSON.parse(JSON.stringify(entity)) : null});
        }).catch(error => done({status: error?.response?.status || error?.status || error?.data?.status || 0,
          error: JSON.stringify(error?.response?.data || error?.data || {status:error?.status, message:String(error)})}));
        """,
                        action,
                        endpoint,
                        values);
        Map<String, Object> response = (Map<String, Object>) result;
        if (response.get("error") != null) {
            Plugin_ERP.Util.RunEnvironmentGate.observe(action, endpoint, response.get("error"));
            int status = response.get("status") instanceof Number number ? number.intValue() : 0;
            throw new ApiFailure(
                    status, "Fixture " + action + " " + endpoint + ": " + response.get("error"));
        }
        return response;
    }
}
