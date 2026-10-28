/* شرح الملف: اختبار توقف الجولات عند غياب تأكيد التنظيف أو تلفه، حتى إن خرج التشغيل دون خطأ.
 * فائدته: يمنع إنشاء فرع الجولة التالية بعد انقطاع أو انهيار غير محسوم في الجولة السابقة.
 */
package SuiteSupport;

import java.nio.file.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BusinessStabilityLauncherTest {
    @Test
    public void selectionEvidenceDistinguishesIntendedTableSubsetFromMissingRequiredWork()
            throws Exception {
        var request =
                new Plugin_ERP.BranchProfiles.BusinessRunRequest(
                        Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType.RESTAURANT,
                        Plugin_ERP.BranchProfiles.BranchPolicies.DiscountLevel.TRANSACTION,
                        Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy.NET_BASED,
                        Plugin_ERP.BranchProfiles.BranchPolicies.StocktakingMethod.PERIODIC,
                        false);
        Path output = Files.createTempDirectory("stability-selection-");
        var suites =
                BusinessRunSuites.plan(
                        Path.of(System.getProperty("user.dir")),
                        request,
                        "Business checks",
                        output);
        StabilitySelectionEvidence.write(output, suites, "CAPTURED_BEFORE_RUN");
        var evidence =
                new org.json.JSONObject(
                        Files.readString(output.resolve("selection-manifest.json")));
        Assert.assertEquals(evidence.getJSONArray("expectedMethods").length(), 7);
        Assert.assertEquals(evidence.getJSONArray("explicitlyExcludedMethods").length(), 3);
        Assert.assertTrue(
                evidence.getJSONArray("expectedMethods")
                        .toList()
                        .contains(
                                "CoverageTest.TablesCoverageTest.createsAndReadsBackExactOwnedRecord"));
        Assert.assertTrue(
                evidence.getJSONArray("explicitlyExcludedMethods")
                        .toList()
                        .contains("CoverageTest.TablesCoverageTest.savedNotesSurviveReopening"));
    }

    @Test
    public void nativeBusinessPlannerCanReplaceTheInitialSuiteBeforeAnyLiveWork() {
        var engine = BusinessStabilityChild.engine();
        var altered = new java.util.concurrent.atomic.AtomicBoolean();
        engine.addListener(
                new org.testng.IAlterSuiteListener() {
                    @Override
                    public void alter(java.util.List<org.testng.xml.XmlSuite> suites) {
                        suites.clear();
                        altered.set(true);
                    }
                });
        engine.setUseDefaultListeners(false);
        engine.run();
        Assert.assertTrue(
                altered.get(),
                "Business planning must replace the starting suite without an immutable-list failure");
    }

    @Test
    public void missingCompletionCannotBeTreatedAsSuccess() throws Exception {
        Path round = Files.createTempDirectory("stability-no-completion-");
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 0), 2);
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 1), 2);
    }

    @Test
    public void confirmedCleanupKeepsPassOrTestFailureWithoutMaskingIt() throws Exception {
        Path round = Files.createTempDirectory("stability-completed-");
        Path marker = round.resolve("round-completion.json");
        Files.writeString(marker, "{\"cleaned\":true,\"exitCode\":0}");
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 0), 0);
        Files.writeString(marker, "{\"cleaned\":true,\"exitCode\":1}");
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 1), 1);
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 0), 2);
    }

    @Test
    public void uncertainOrCorruptedCleanupStopsTheNextRound() throws Exception {
        Path round = Files.createTempDirectory("stability-uncertain-");
        Path marker = round.resolve("round-completion.json");
        Files.writeString(marker, "{\"cleaned\":false,\"exitCode\":0}");
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 0), 2);
        Files.writeString(marker, "broken");
        Assert.assertEquals(BusinessStabilityLauncher.roundExit(round, 0), 2);
    }

    @Test
    public void overallCompletionKeepsEarlierTestFailureAfterLaterPassingRounds() throws Exception {
        Path output = completedRounds(1, 0, 0);
        Assert.assertEquals(BusinessStabilityLauncher.completionExit(output), 1);
        Assert.assertEquals(BusinessStabilityLauncher.completionExit(completedRounds(0, 0, 0)), 0);
    }

    @Test
    public void overallCompletionCannotReportSuccessForCapacityStopOrMissingRound()
            throws Exception {
        Assert.assertEquals(BusinessStabilityLauncher.completionExit(completedRounds(2)), 2);
        Assert.assertEquals(BusinessStabilityLauncher.completionExit(completedRounds(0, 0)), 2);
        Assert.assertEquals(
                BusinessStabilityLauncher.completionExit(
                        Files.createTempDirectory("missing-rounds-")),
                2);
    }

    private Path completedRounds(int... exits) throws Exception {
        Path output = Files.createTempDirectory("round-completion-exit-");
        var rows = new org.json.JSONArray();
        for (int i = 0; i < exits.length; i++) {
            String folder = "round-" + (i + 1);
            Path round = Files.createDirectories(output.resolve(folder));
            Files.writeString(
                    round.resolve("round-completion.json"),
                    new org.json.JSONObject(java.util.Map.of("cleaned", true, "exitCode", exits[i]))
                            .toString());
            rows.put(
                    new org.json.JSONObject(
                            java.util.Map.of("folder", folder, "exitCode", exits[i])));
        }
        Files.writeString(
                output.resolve("rounds.json"),
                new org.json.JSONObject().put("requested", 3).put("rounds", rows).toString());
        return output;
    }
}
