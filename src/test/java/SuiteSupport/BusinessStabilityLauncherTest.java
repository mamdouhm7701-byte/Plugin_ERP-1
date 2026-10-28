/* شرح الملف: اختبار توقف الجولات عند غياب تأكيد التنظيف أو تلفه، حتى إن خرج التشغيل دون خطأ.
 * فائدته: يمنع إنشاء فرع الجولة التالية بعد انقطاع أو انهيار غير محسوم في الجولة السابقة.
 */
package SuiteSupport;

import java.nio.file.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BusinessStabilityLauncherTest {
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
}
