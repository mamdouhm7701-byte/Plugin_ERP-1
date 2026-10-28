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

/** Creates one API branch per business run, using live new-form defaults and verified policies. */
public final class BusinessBranchPreparation {
    private BusinessBranchPreparation() {}

    static void verifyCreationOrigin(String actualOrigin, String requestedUrl) {
        var actual = java.net.URI.create(actualOrigin).resolve("/");
        var requested = java.net.URI.create(requestedUrl).resolve("/");
        if (!actual.equals(requested))
            throw new IllegalStateException(
                    "Authenticated origin differs from the selected target; branch creation refused");
    }

    static String activityLabel(List<? extends Map<String, ?>> options, String activity) {
        var matches = options.stream().filter(option -> activity.equals(option.get("id"))).toList();
        if (matches.size() != 1
                || !(matches.getFirst().get("arabicName") instanceof String name)
                || name.isBlank())
            throw new IllegalStateException(
                    "Native activity option is missing or ambiguous: " + activity);
        return name;
    }

    public static BranchProfile prepare(BusinessRunRequest request, Path project, Path output)
            throws Exception {
        return prepare(request, project, output, profile -> {});
    }

    static BranchProfile prepare(
            BusinessRunRequest request,
            Path project,
            Path output,
            java.util.function.Consumer<BranchProfile> confirmedCreation)
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
            verifyCreationOrigin(original.origin(), PropertyReader.getApplicationUrl());
            Path profiles = project.resolve("config/branch-profiles");
            Files.createDirectories(profiles);
            BranchProfile selected =
                    create(
                            driver,
                            request,
                            project,
                            profiles,
                            output,
                            original.origin(),
                            confirmedCreation);
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

    static String creationKey(BusinessRunRequest request, String origin, Path output)
            throws Exception {
        return HexFormat.of()
                .formatHex(
                        java.security.MessageDigest.getInstance("SHA-256")
                                .digest(
                                        (origin
                                                        + "\n"
                                                        + output.toAbsolutePath().normalize()
                                                        + "\n"
                                                        + new TreeMap<>(request.fields()))
                                                .getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .substring(0, 16);
    }

    private static BranchProfile create(
            WebDriver driver,
            BusinessRunRequest request,
            Path project,
            Path profiles,
            Path output,
            String origin,
            java.util.function.Consumer<BranchProfile> confirmedCreation)
            throws Exception {
        String key = creationKey(request, origin, output);
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
        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        @SuppressWarnings("unchecked")
        var activities =
                (List<Map<String, ?>>)
                        wait.until(
                                d ->
                                        VueComponentAccess.execute(
                                                d,
                                                """
                const vm=erpVue.root()?.$router?.currentRoute?.matched?.at(-1)?.instances?.default;
                return Array.isArray(vm?.activites)&&vm.activites.length?vm.activites:null;
                """));
        page.choose("نوع النشاط", activityLabel(activities, request.activity().code()));
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
        var expectedFields = new HashMap<String, Object>(request.fields());
        expectedFields.put("origin", origin);
        expectedFields.put("branchId", id);
        confirmedCreation.accept(
                new BranchProfile(profileName, BranchSnapshot.from(expectedFields)));
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
