/* شرح الملف: ينفذ تنظيف التجربة المختارة فقط بعد قفل التشغيل وإعادة التحقق من السجلات، ويحفظ نسخة استرجاع ونتيجة واضحة دون تجاوز فشل الحذف. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.BranchProfile;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import org.json.JSONObject;

class CleanupRunExecution {
    record Result(
            boolean dataCleaned, boolean branchDeleted, Path output, List<String> remaining) {}

    NativeInteractiveSuiteListener gate() {
        return new NativeInteractiveSuiteListener();
    }

    void recover(CleanupRunCatalog.Journal journal) throws Exception {
        String[] args = {journal.suffix()};
        switch (journal.module()) {
            case "sales" -> Invoice.SalesFixtureRecovery.main(args);
            case "purchase" -> Purches.PurchaseFixtureRecovery.main(args);
            case "special-sales" -> SpecialSalesTest.SpecialSalesRecovery.main(args);
            case "store" -> StoreTest.StoreFixtureRecovery.main(args);
            case "financial" -> FinancialMovementsTest.FinancialFixtureRecovery.main(args);
            case "products" -> ProductsTest.ProductsFixtureRecovery.main(args);
            case "basic-data" -> TestSupport.BasicDataFixturesRecovery.main(args);
            case "dealer" ->
                    DealerModelTest.DealerFixtureRecovery.main(
                            new String[] {"fixtures", journal.suffix()});
            case "dealer-page-records" ->
                    DealerModelTest.DealerFixtureRecovery.main(
                            new String[] {"page", journal.suffix()});
            default ->
                    throw new IllegalArgumentException("لا يوجد تنظيف آلي لهذا النوع من السجلات.");
        }
    }

    void deleteBranch(Path project, BranchProfile profile, Path output) throws Exception {
        BusinessBranchCleanup.remove(project, profile, output);
        var state =
                new JSONObject(
                        Files.readString(
                                project.resolve(
                                        "config/business-branch-state/"
                                                + profile.name().substring(9)
                                                + ".json")));
        if (!"DELETED".equals(state.optString("status")))
            throw new IllegalStateException("Branch deletion was not confirmed");
    }

    Result execute(
            Path project,
            CleanupRunCatalog.Entry selected,
            BusinessRunTarget target,
            boolean removeBranch,
            Consumer<String> progress)
            throws Exception {
        var lock = gate();
        var previous = new LinkedHashMap<String, String>();
        try {
            lock.acquire(project);
            var entry = CleanupRunCatalog.revalidate(project, selected);
            if (!BusinessRunTarget.normalizeUrl(target.description().get("url"))
                    .equals(entry.profile().expected().origin()))
                throw new IllegalArgumentException("رابط الدخول لا يطابق بيئة السجلات المختارة.");
            Path output = project.resolve("target/cleanup/" + UUID.randomUUID());
            Files.createDirectories(output);
            backup(project, entry, output.resolve("before"));
            var settings = target.settings();
            settings.put("branch.profile", entry.profile().name());
            settings.put("branch.profile.fingerprint", entry.profile().fingerprint());
            settings.put("branch.profile.select", "true");
            settings.put("ExecutionType", "LocalHeadless");
            settings.put("erp.run.evidence.output", output.toString());
            settings.put("allure.report.generate", "false");
            settings.put("allure.report.open", "false");
            settings.put("allure.results.directory", output.resolve("allure-results").toString());
            try {
                settings.forEach(
                        (key, value) -> {
                            previous.put(key, System.getProperty(key));
                            System.setProperty(key, value);
                        });
            } finally {
                settings.clear();
                target.clearSecrets();
            }
            var errors = new ArrayList<String>();
            progress.accept(
                    "بدأ تنظيف " + entry.owner() + " على " + entry.profile().expected().origin());
            for (var journal : entry.journals()) {
                if (!journal.supported()) {
                    errors.add("يحتاج مراجعة خاصة: " + CleanupRunCatalog.label(journal.module()));
                    continue;
                }
                progress.accept(
                        "تنظيف "
                                + CleanupRunCatalog.label(journal.module())
                                + " — "
                                + journal.records()
                                + " سجل...");
                try {
                    recover(journal);
                    if (!empty(journal.files()))
                        throw new IllegalStateException("Journal still contains records");
                    progress.accept("اكتمل تنظيف " + CleanupRunCatalog.label(journal.module()));
                } catch (Exception | AssertionError failure) {
                    String message =
                            "لم يكتمل تنظيف "
                                    + CleanupRunCatalog.label(journal.module())
                                    + " ("
                                    + failure.getClass().getSimpleName()
                                    + ")؛ السجلات محفوظة.";
                    errors.add(message);
                    progress.accept(message);
                }
            }
            boolean pending =
                    BusinessBranchCleanup.pendingRecovery(
                            project, entry.profile().expected().branchId());
            boolean cleaned = errors.isEmpty() && !pending;
            boolean deleted = false;
            if (cleaned && removeBranch) {
                progress.accept("البيانات انتهت؛ جارٍ حذف فرع التجربة والتحقق من اختفائه...");
                try {
                    deleteBranch(project, entry.profile(), output);
                    deleted = true;
                } catch (Exception | AssertionError failure) {
                    errors.add(
                            "تعذر تأكيد حذف الفرع ("
                                    + failure.getClass().getSimpleName()
                                    + ")؛ احتفظ بسجل الاسترجاع.");
                }
            } else if (pending) errors.add("توجد سجلات متبقية تمنع حذف الفرع.");
            var result = new Result(cleaned, deleted, output, List.copyOf(errors));
            Files.writeString(
                    output.resolve("summary.json"),
                    new JSONObject(
                                    Map.of(
                                            "profile",
                                            entry.profile().name(),
                                            "branchId",
                                            entry.profile().expected().branchId(),
                                            "origin",
                                            entry.profile().expected().origin(),
                                            "target",
                                            target.description(),
                                            "dataCleaned",
                                            cleaned,
                                            "branchDeletionRequested",
                                            removeBranch,
                                            "branchDeleted",
                                            deleted,
                                            "remaining",
                                            errors))
                            .toString(2));
            progress.accept(
                    "النتيجة: "
                            + (cleaned ? "البيانات نظيفة" : "ما زالت هناك بيانات تحتاج مراجعة")
                            + "؛ "
                            + (deleted ? "حذف الفرع مؤكد" : "الفرع لم يُحذف")
                            + ". التقرير: "
                            + output);
            return result;
        } finally {
            target.clearSecrets();
            previous.forEach(
                    (key, value) -> {
                        if (value == null) System.clearProperty(key);
                        else System.setProperty(key, value);
                    });
            lock.onExecutionFinish();
        }
    }

    private static boolean empty(List<Path> files) throws Exception {
        for (var file : files)
            if (Files.exists(file) && !new org.json.JSONArray(Files.readString(file)).isEmpty())
                return false;
        return true;
    }

    private static void backup(Path project, CleanupRunCatalog.Entry entry, Path output)
            throws Exception {
        var files = new LinkedHashSet<Path>();
        files.add(entry.registry());
        files.add(
                project.resolve(
                        "config/branch-profiles/" + entry.profile().name() + ".properties"));
        entry.journals().forEach(j -> files.addAll(j.files()));
        for (var file : files) {
            CleanupRunCatalog.within(project, file);
            Path destination = output.resolve(project.relativize(file));
            Files.createDirectories(destination.getParent());
            Files.copy(file, destination);
        }
    }
}
