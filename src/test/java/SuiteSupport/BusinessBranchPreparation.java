package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Reuses a verified explicit profile or creates an owned branch from live new-form defaults. */
public final class BusinessBranchPreparation {
    private BusinessBranchPreparation() {}

    public static BranchProfile prepare(BusinessRunRequest request, Path project, Path output)
            throws Exception {
        Files.createDirectories(output);
        Files.writeString(
                output.resolve("request.json"),
                new org.json.JSONObject(request.fields()).toString(2),
                StandardOpenOption.CREATE_NEW);
        migrateLegacyRegistries(project);
        var driver = WebDriverFactory.initDriver();
        try {
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            var login = new LoginPage(driver);
            login.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
            login.AssertLoginPageTitle();
            var original = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
            Path profiles = project.resolve("config/branch-profiles");
            Files.createDirectories(profiles);
            BranchProfile selected = matching(driver, request, profiles, original.origin());
            if (selected == null)
                selected = create(driver, request, project, profiles, output, original.origin());
            // Newly created branches enter the header menu only after session hydration.
            driver.get(original.origin() + "/#/");
            driver.navigate().refresh();
            BranchProfileReader.capture(driver, Duration.ofSeconds(30));
            BranchSessionSelector.select(driver, selected);
            request.verify(read(driver, selected.expected().branchId()), original.origin());
            selected.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
            Files.writeString(
                    output.resolve("verified.json"),
                    new org.json.JSONObject(
                                    Map.of(
                                            "profile",
                                            selected.name(),
                                            "fingerprint",
                                            selected.fingerprint(),
                                            "expected",
                                            selected.expected().fields(),
                                            "business",
                                            request.fields(),
                                            "verified",
                                            true))
                            .toString(2),
                    StandardOpenOption.CREATE_NEW);
            return selected;
        } catch (Exception | AssertionError failure) {
            if (driver instanceof TakesScreenshot camera)
                Files.write(
                        output.resolve("failure.png"), camera.getScreenshotAs(OutputType.BYTES));
            throw failure;
        } finally {
            WebDriverFactory.quitDriver();
        }
    }

    static boolean isProfileFile(Path file) {
        return Files.isRegularFile(file)
                && file.getFileName().toString().matches("[a-z0-9][a-z0-9_-]{0,63}\\.properties");
    }

    static Path registryPath(Path project, String key) throws java.io.IOException {
        if (!key.matches("[a-f0-9]{16}"))
            throw new IllegalArgumentException("Invalid owned branch registry key");
        Path durable = project.resolve("config/business-branch-state").resolve(key + ".json");
        Files.createDirectories(durable.getParent());
        Path legacy = project.resolve("target/business-branch-registry").resolve(key + ".json");
        if (!Files.exists(durable) && Files.isRegularFile(legacy)) Files.copy(legacy, durable);
        return durable;
    }

    static void migrateLegacyRegistries(Path project) throws java.io.IOException {
        Path legacy = project.resolve("target/business-branch-registry");
        if (!Files.isDirectory(legacy)) return;
        try (var files = Files.list(legacy)) {
            for (Path file :
                    files.filter(Files::isRegularFile)
                            .filter(p -> p.getFileName().toString().matches("[a-f0-9]{16}\\.json"))
                            .toList())
                registryPath(project, file.getFileName().toString().substring(0, 16));
        }
    }

    private static BranchProfile matching(
            WebDriver driver, BusinessRunRequest request, Path profiles, String origin)
            throws Exception {
        List<Path> candidates;
        try (var files = Files.list(profiles)) {
            candidates = files.filter(BusinessBranchPreparation::isProfileFile).sorted().toList();
        }
        for (Path file : candidates) {
            String name = file.getFileName().toString().replaceFirst("\\.properties$", "");
            var profile = BranchProfiles.load(name, profiles).orElseThrow();
            if (!profile.expected().origin().equals(origin) || !request.matches(profile.expected()))
                continue;
            var stored = read(driver, profile.expected().branchId());
            // A different extra-tax variant is a different business branch, never a policy edit.
            if (Boolean.TRUE.equals(stored.get("enableAdditionalTax")) != request.additionalTax())
                continue;
            request.verify(stored, origin);
            profile.verify(snapshot(stored, origin));
            return profile;
        }
        return null;
    }

    private static BranchProfile create(
            WebDriver driver,
            BusinessRunRequest request,
            Path project,
            Path profiles,
            Path output,
            String origin)
            throws Exception {
        if (!"Cloud".equalsIgnoreCase(PropertyReader.getProperty("Environment"))
                || !"Demo".equals(PropertyReader.getProperty("Tenant")))
            throw new IllegalStateException(
                    "Automatic branch creation is currently qualified on Cloud/Demo only; no existing branch was edited");
        String key =
                HexFormat.of()
                        .formatHex(
                                java.security.MessageDigest.getInstance("SHA-256")
                                        .digest(
                                                (origin + new TreeMap<>(request.fields()))
                                                        .getBytes(
                                                                java.nio.charset.StandardCharsets
                                                                        .UTF_8)))
                        .substring(0, 16);
        String name = "QA-BUS-" + key;
        String profileName = "business-" + key;
        Path registry = registryPath(project, key);
        if (Files.exists(registry)) {
            var pending = new org.json.JSONObject(Files.readString(registry));
            if (!origin.equals(pending.optString("origin"))
                    || !name.equals(pending.optString("owner"))
                    || !profileName.equals(pending.optString("profile")))
                throw new IllegalStateException(
                        "Owned branch registry identity changed; creation and reuse refused");
            if (!pending.has("branchId"))
                throw new IllegalStateException(
                        "An earlier branch creation has an unresolved response. Inspect "
                                + registry
                                + " before retrying; duplicate creation refused");
            var stored = read(driver, pending.getString("branchId"));
            request.verify(stored, origin);
            if (!pending.getString("owner").equals(stored.get("arabicName")))
                throw new IllegalStateException("Branch ownership changed");
            return publish(profiles, pending.getString("profile"), snapshot(stored, origin));
        }
        var page = new ScreenPage(driver);
        page.open("branches/new", "انشاء فرع");
        page.choose(
                "نوع النشاط",
                switch (request.activity()) {
                    case PUBLIC -> "عام";
                    case RESTAURANT -> "مطاعم";
                    case CONSTRUCTION -> "مقاولات";
                    case CONCRETE -> "خرسانة";
                    case CAR_SHOWROOM -> "معرض سيارات";
                });
        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        @SuppressWarnings("unchecked")
        Map<String, Object> defaults =
                (Map<String, Object>)
                        wait.until(
                                d ->
                                        VueComponentAccess.execute(
                                                d,
                                                """
            const r=erpVue.root()?.$router?.currentRoute,vm=r?.matched?.at(-1)?.instances?.default;
            const value=vm?.selectedItem;
            return r?.path==='/branches/new'&&value&&!value.id&&
              ['discountLevel','taxPolicy','stocktakingMethod','accounts','stores','scalesNature'].every(k=>k in value)&&value.accounts&&'salesAccountId' in value.accounts?JSON.parse(JSON.stringify(value)):null;
            """));
        var payload = new LinkedHashMap<>(defaults);
        payload.remove("id");
        // Native save normalizes the unused scale enum to 0 (captured branch-create contract).
        if (payload.get("scalesNature") == null) payload.put("scalesNature", 0);
        payload.put("arabicName", name);
        payload.put("englishName", name);
        payload.putAll(request.fields());
        var ownership =
                new org.json.JSONObject(
                        Map.of(
                                "owner",
                                name,
                                "profile",
                                profileName,
                                "origin",
                                origin,
                                "business",
                                request.fields(),
                                "status",
                                "CREATE_PENDING",
                                "retained",
                                true));
        Files.writeString(registry, ownership.toString(2), StandardOpenOption.CREATE_NEW);
        Files.writeString(
                output.resolve("create-intent.json"),
                new org.json.JSONObject(payload).toString(2),
                StandardOpenOption.CREATE_NEW);
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));
        @SuppressWarnings("unchecked")
        var response =
                (Map<String, Object>)
                        VueComponentAccess.executeAsync(
                                driver,
                                """
            const done=arguments[arguments.length-1];
            erpVue.root().$store.dispatch('app/create',{url:'Branches',data:arguments[0]}).then(result=>{
              const value=result?.data||result;
              done({id:value?.id||(typeof value==='string'||typeof value==='number'?value:null)});
            },error=>done({error:JSON.stringify(error?.response?.data||error?.data||{status:error?.status,message:error?.message,errors:error?.errors})}));
            """,
                                payload);
        Files.writeString(
                output.resolve("create-response.json"),
                new org.json.JSONObject(response).toString(2),
                StandardOpenOption.CREATE_NEW);
        if (response.get("id") == null || response.get("error") != null)
            throw new IllegalStateException(
                    "Branch creation was not confirmed; preserve pending registry " + registry);
        String id = response.get("id").toString();
        ownership.put("branchId", id).put("status", "CREATED_AWAITING_READBACK");
        Files.writeString(registry, ownership.toString(2));
        var stored = read(driver, id);
        request.verify(stored, origin);
        if (!name.equals(stored.get("arabicName")))
            throw new IllegalStateException("New branch ownership mismatch");
        Files.writeString(
                output.resolve("created-readback.json"),
                new org.json.JSONObject(stored).toString(2),
                StandardOpenOption.CREATE_NEW);
        var profile = publish(profiles, profileName, snapshot(stored, origin));
        ownership.put("status", "VERIFIED");
        Files.writeString(registry, ownership.toString(2));
        return profile;
    }

    private static BranchProfile publish(Path profiles, String name, BranchSnapshot snapshot)
            throws Exception {
        var profile = new BranchProfile(name, snapshot);
        Path file = profiles.resolve(name + ".properties");
        if (Files.exists(file)) {
            BranchProfiles.load(name, profiles).orElseThrow().verify(snapshot);
            return profile;
        }
        String value =
                snapshot.fields().entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .collect(java.util.stream.Collectors.joining("\n"))
                        + "\n";
        Files.writeString(file, value, StandardOpenOption.CREATE_NEW);
        return profile;
    }

    private static BranchSnapshot snapshot(Map<String, Object> stored, String origin) {
        var fields = new HashMap<>(stored);
        fields.put("origin", origin);
        fields.put("branchId", stored.get("id"));
        return BranchSnapshot.from(fields);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> read(WebDriver driver, String id) {
        var result =
                (Map<String, Object>)
                        VueComponentAccess.executeAsync(
                                driver,
                                """
            const done=arguments[arguments.length-1];erpVue.root().$store.dispatch('app/get',{url:'Branches',id:arguments[0]})
              .then(value=>done({value:value?.data||value}),error=>done({error:String(error)}));
            """,
                                id);
        if (!(result.get("value") instanceof Map<?, ?>))
            throw new IllegalStateException("Cannot read selected branch " + id);
        return (Map<String, Object>) result.get("value");
    }
}
