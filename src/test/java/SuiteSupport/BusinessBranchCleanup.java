/* شرح الملف: حذف فرع التجربة فقط بعد التأكد من انتهاء تنظيف بياناته.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import org.json.*;

final class BusinessBranchCleanup {
    private BusinessBranchCleanup() {}

    static boolean pendingRecovery(Path project, String branchId) throws Exception {
        Path target = project.resolve("target");
        if (!Files.isDirectory(target)) return false;
        try (var directories = Files.list(target)) {
            for (var directory :
                    directories
                            .filter(Files::isDirectory)
                            .filter(
                                    p ->
                                            p.getFileName().toString().endsWith("fixtures")
                                                    || p.getFileName()
                                                            .toString()
                                                            .endsWith("attempts")
                                                    || Set.of(
                                                                    "dealer-page-records",
                                                                    "posting-recovery",
                                                                    "treasury-audit",
                                                                    "treasury-audit-masters",
                                                                    "treasury-audit-financial",
                                                                    "inventory-audit-documents")
                                                            .contains(p.getFileName().toString()))
                            .toList()) {
                try (var files = Files.list(directory)) {
                    for (var file : files.filter(p -> p.toString().endsWith(".json")).toList()) {
                        String text = Files.readString(file);
                        var rows = new JSONArray(text);
                        if (!rows.isEmpty() && text.contains(branchId)) return true;
                    }
                }
            }
        }
        return false;
    }

    static void remove(Path project, BranchProfile profile, Path output) throws Exception {
        if (!profile.name().matches("business-[a-f0-9]{16}")) return;
        Path registry =
                project.resolve("config/business-branch-state")
                        .resolve(profile.name().substring("business-".length()) + ".json");
        if (!Files.isRegularFile(registry)) return;
        var state = new JSONObject(Files.readString(registry));
        String id = profile.expected().branchId();
        if (!id.equals(state.optString("branchId"))
                || !profile.name().equals(state.optString("profile"))
                || !profile.expected().origin().equals(state.optString("origin")))
            throw new IllegalStateException("Branch cleanup registry identity mismatch");
        if ("DELETED".equals(state.optString("status"))) return;
        if (pendingRecovery(project, id))
            throw new IllegalStateException(
                    "Owned branch retained: pending fixture recovery for " + id);
        var driver = WebDriverFactory.initDriver();
        try {
            driver.get(PropertyReader.getApplicationUrl() + "/#/login");
            var login = new LoginPage(driver);
            login.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
            login.AssertLoginPageTitle();
            if (!profile.expected()
                    .origin()
                    .equals(BranchProfileReader.capture(driver, Duration.ofSeconds(30)).origin()))
                throw new IllegalStateException("Branch cleanup origin mismatch");
            Object result =
                    VueComponentAccess.executeAsync(
                            driver,
                            """
                const done=arguments[arguments.length-1],id=arguments[0],owner=arguments[1],s=erpVue.root().$store;
                s.dispatch('app/get',{url:'Branches',id}).then(async raw=>{
                  const b=raw?.data||raw;
                  if(!b||b.id!==id||b.arabicName!==owner||b.englishName!==owner||b.isMaster===true)
                    return done({status:'OWNERSHIP_REFUSED'});
                  try {await s.dispatch('app/delete',{url:'Branches',id});
                    try {const raw=await s.dispatch('app/get',{url:'Branches',id}),b=raw?.data||raw;
                      done({status:!b||b===''||!b.id?'DELETED':'STILL_PRESENT'});
                    }catch(e){done({status:[404,410].includes(e?.response?.status||e?.status)?'DELETED':'READBACK_UNCERTAIN'});}
                  }catch(e){done({status:'DELETE_REFUSED',error:JSON.stringify(e?.response?.data||e?.data||{message:e?.message})});}
                },e=>done({status:'READ_REFUSED',error:String(e)}));
                """,
                            id,
                            state.getString("owner"));
            var evidence = new JSONObject((Map<?, ?>) result).put("branchId", id);
            Files.createDirectories(output);
            Files.writeString(output.resolve("branch-cleanup.json"), evidence.toString(2));
            if (!"DELETED".equals(evidence.optString("status")))
                throw new IllegalStateException(
                        "Owned branch cleanup requires recovery: " + evidence);
            state.put("status", "DELETED")
                    .put("retained", false)
                    .put("deletedUtc", java.time.Instant.now().toString());
            Files.writeString(registry, state.toString(2));
        } finally {
            WebDriverFactory.quitDriver();
        }
    }
}
