/* شرح الملف: تجهيز ربط حسابات فرع التجربة ثم إعادة الربط كما كان.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Util.VueComponentAccess;
import TestSupport.FixtureJournals;
import java.nio.file.*;
import java.util.*;
import org.openqa.selenium.WebDriver;

final class OwnedBranchAccountingBindings implements AutoCloseable {
    private final WebDriver driver;
    private final BranchProfile profile;
    private final String owner;
    private final Path journal;
    private final Map<String, Object> original;
    private final Map<String, Object> owned;

    OwnedBranchAccountingBindings(WebDriver driver, Map<String, Object> links) throws Exception {
        this.driver = driver;
        profile = BranchProfiles.selected().orElseThrow();
        if (!profile.name().matches("business-[a-f0-9]{16}"))
            throw new IllegalStateException(
                    "Accounting setup requires a fresh Business run branch");
        owner = "QA-BUS-" + profile.name().substring("business-".length());
        var registry =
                new org.json.JSONObject(
                        Files.readString(
                                Path.of(
                                        "config/business-branch-state",
                                        profile.name().substring("business-".length()) + ".json")));
        if (!profile.expected().branchId().equals(registry.optString("branchId"))
                || !profile.expected().origin().equals(registry.optString("origin"))
                || !owner.equals(registry.optString("owner"))
                || !"VERIFIED".equals(registry.optString("status")))
            throw new IllegalStateException(
                    "Fresh accounting branch registry identity is not verified");
        owned = new LinkedHashMap<>(links);
        if (owned.isEmpty()
                || owned.values().stream().anyMatch(v -> v == null || v.toString().isBlank()))
            throw new IllegalArgumentException("Verified owned account IDs are required");
        original = accounts(read());
        journal = Path.of("target/posting-recovery/bindings-" + UUID.randomUUID() + ".json");
        FixtureJournals.write(
                journal,
                List.of(
                        SalesTestData.fields(
                                "origin",
                                profile.expected().origin(),
                                "branchId",
                                profile.expected().branchId(),
                                "owner",
                                owner,
                                "kind",
                                "OWNED_BRANCH_ACCOUNT_BINDINGS",
                                "original",
                                original,
                                "owned",
                                owned)));
        try {
            update(owned, original);
            var stored = accounts(read());
            owned.forEach(
                    (key, value) -> {
                        if (!Objects.equals(stored.get(key), value))
                            throw new AssertionError("Account binding readback differs: " + key);
                    });
        } catch (Exception | AssertionError failure) {
            try {
                close();
            } catch (Exception | AssertionError cleanup) {
                failure.addSuppressed(cleanup);
            }
            throw failure;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> accounts(Map<String, Object> branch) {
        if (!(branch.get("accounts") instanceof Map<?, ?>))
            throw new AssertionError("Branch account links are missing");
        return new LinkedHashMap<>((Map<String, Object>) branch.get("accounts"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> read() {
        profile.verify(BranchProfileReader.capture(driver, java.time.Duration.ofSeconds(30)));
        Object value =
                VueComponentAccess.executeAsync(
                        driver,
                        """
            const [id,owner,origin,done]=arguments,s=erpVue.root().$store;
            if(location.origin!==origin||s.getters['app/currentBranch']?.id!==id)return done({error:'Branch session changed'});
            s.dispatch('app/get',{url:'Branches',id}).then(raw=>{
              const b=raw?.data||raw;
              if(b?.id!==id||b.arabicName!==owner||b.englishName!==owner||b.isMaster===true)
                return done({error:'Fresh branch ownership changed'});
              done({branch:b});
            },e=>done({error:String(e)}));
            """,
                        profile.expected().branchId(),
                        owner,
                        profile.expected().origin());
        var response = (Map<String, Object>) value;
        if (response.get("error") != null) throw new AssertionError(response.get("error"));
        return (Map<String, Object>) response.get("branch");
    }

    @SuppressWarnings("unchecked")
    private void update(Map<String, Object> desired, Map<String, Object> expectedCurrent) {
        Object value =
                VueComponentAccess.executeAsync(
                        driver,
                        """
            const [id,owner,origin,desired,expected,keys,done]=arguments,s=erpVue.root().$store;
            if(location.origin!==origin||s.getters['app/currentBranch']?.id!==id)return done({error:'Branch session changed'});
            s.dispatch('app/get',{url:'Branches',id}).then(async raw=>{
              const b=raw?.data||raw;
              if(b?.id!==id||b.arabicName!==owner||b.englishName!==owner||b.isMaster===true)
                return done({error:'Fresh branch ownership changed'});
              if(keys.some(k=>(b.accounts?.[k]??null)!==(expected[k]??null)))return done({error:'Concurrent account link change'});
              const accounts={...b.accounts};
              keys.forEach(k=>{if(desired[k]==null)delete accounts[k];else accounts[k]=desired[k]});
              try {await s.dispatch('app/update',{url:'Branches',data:{...b,accounts}});done({ok:true});}
              catch(e){done({error:JSON.stringify(e?.response?.data||e?.data||{message:String(e)})})}
            },e=>done({error:String(e)}));
            """,
                        profile.expected().branchId(),
                        owner,
                        profile.expected().origin(),
                        desired,
                        expectedCurrent,
                        new ArrayList<>(owned.keySet()));
        var response = (Map<String, Object>) value;
        if (!Boolean.TRUE.equals(response.get("ok")))
            throw new AssertionError("Owned account link update failed: " + response.get("error"));
    }

    static Map<String, Object> restored(
            Map<String, Object> current, Map<String, Object> original, Map<String, Object> owned) {
        var result = new LinkedHashMap<>(current);
        for (String key : owned.keySet()) {
            if (!Objects.equals(current.get(key), owned.get(key))
                    && !Objects.equals(current.get(key), original.get(key)))
                throw new AssertionError("Refusing to overwrite a changed account link: " + key);
            if (original.containsKey(key)) result.put(key, original.get(key));
            else result.remove(key);
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        var current = accounts(read());
        var previous = restored(current, original, owned);
        update(previous, current);
        var stored = accounts(read());
        for (String key : owned.keySet())
            if (!Objects.equals(stored.get(key), original.get(key)))
                throw new AssertionError("Account link restoration not confirmed: " + key);
        FixtureJournals.write(journal, List.of());
    }
}
