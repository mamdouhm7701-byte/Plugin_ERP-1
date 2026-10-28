/* شرح الملف: تنفيذ تجهيز وقراءة وحذف بيانات التجربة على الفرع الصحيح، ضمن المخازن.
 */
package StoreTest;

import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.WebDriver;

final class StoreFixtureApi {
    private final Plugin_ERP.BranchProfiles.BranchProfileGuard profileGuard =
            Plugin_ERP.BranchProfiles.BranchProfileGuard.capture();
    private final WebDriver driver;
    private final String origin;
    private final Object sessionBranchId;

    StoreFixtureApi(WebDriver driver, String origin, Object sessionBranchId) {
        this.driver = driver;
        this.origin = origin;
        this.sessionBranchId = sessionBranchId;
    }

    static final class ApiFailure extends TestSupport.FixtureApiFailure {

        ApiFailure(int status, String message) {
            super(status, message);
        }
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        profileGuard.verify(driver);
        Plugin_ERP.Util.RunEnvironmentGate.beforeCreate(action);
        driver.manage()
                .timeouts()
                .scriptTimeout(Duration.ofSeconds(action.equals("delete") ? 60 : 30));
        Object result =
                Plugin_ERP.Util.VueComponentAccess.executeAsync(
                        driver,
                        """
        const [action, endpoint, values, expectedOrigin, expectedBranch, done] = arguments;
        const root = erpVue.root();
        if (!root?.$store) { done({error:'Authenticated ERP session is not ready'}); return; }
        if(location.origin!==expectedOrigin||root.$store.getters['app/currentBranch']?.id!==expectedBranch){done({error:'ERP origin or branch changed; refusing fixture access'});return;}
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
                        values,
                        origin,
                        sessionBranchId);
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
