package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.*;
import org.testng.annotations.Test;
import org.testng.xml.*;

public final class NativeInteractiveSuiteListenerTest {
    @Test public void selectedCasesAndExclusiveStageRemainIntact() throws Exception {
        Path root = Path.of("").toAbsolutePath();
        Path output = Files.createTempDirectory("erp-native-plan-");
        for (String level : List.of("Smoke", "Sanity", "E2E", "Regression")) {
            Map<String,List<String>> expected = ParallelSuitePlanner.selected(root.resolve("src/test/resources/" + level.toLowerCase(Locale.ROOT) + "-testng.xml"));
            Set<String> actual = new HashSet<>();
            for (XmlSuite suite : NativeInteractiveSuiteListener.buildSuites(root, level, 3, output.resolve(level))) {
                boolean parallel = suite.getName().endsWith("/ parallel");
                Assert.assertEquals(suite.getThreadCount(), parallel ? 3 : 1);
                for (XmlTest test : suite.getTests()) for (XmlClass type : test.getXmlClasses()) {
                    Assert.assertEquals(BranchIsolationListener.independent(type.getName()), parallel);
                    for (XmlInclude method : type.getIncludedMethods())
                        Assert.assertTrue(actual.add(type.getName() + "#" + method.getName()), "Duplicate selection");
                }
            }
            Set<String> wanted = new HashSet<>();
            expected.forEach((type, methods) -> methods.forEach(method -> wanted.add(type + "#" + method)));
            Assert.assertEquals(actual, wanted, level + " must retain every original case exactly once");
        }
    }

    @Test public void workerCeilingHonoursMemoryAndCpu() {
        Assert.assertEquals(NativeInteractiveSuiteListener.safeWorkers(6, 1.29, 16), 1);
        Assert.assertEquals(NativeInteractiveSuiteListener.safeWorkers(6, 2.75, 16), 3);
        Assert.assertEquals(NativeInteractiveSuiteListener.safeWorkers(6, 6, 16), 6);
        Assert.assertEquals(NativeInteractiveSuiteListener.safeWorkers(6, 6, 4), 2);
        Assert.assertEquals(NativeInteractiveSuiteListener.safeWorkers(1, 6, 16), 1);
    }

    @Test public void qualificationKeepsTheThreeLevelsInOrder() throws Exception {
        var suites = NativeInteractiveSuiteListener.buildSuites(Path.of("").toAbsolutePath(), "Qualification", 3,
                Files.createTempDirectory("erp-native-qualification-"));
        Assert.assertEquals(suites.stream().map(XmlSuite::getName).toList(), List.of(
                "Smoke / parallel", "Smoke / exclusive", "Sanity / parallel", "Sanity / exclusive",
                "E2E / parallel", "E2E / exclusive"));
    }

    @Test public void twoRunsCannotAcquireTheSameWorkspace() throws Exception {
        Path workspace = Files.createTempDirectory("erp-live-gate-");
        var first = new NativeInteractiveSuiteListener();
        var second = new NativeInteractiveSuiteListener();
        try {
            first.acquire(workspace);
            Assert.expectThrows(IllegalStateException.class, () -> second.acquire(workspace));
            first.onExecutionFinish();
            second.acquire(workspace);
        } finally { first.onExecutionFinish(); second.onExecutionFinish(); }
    }

    @org.testng.annotations.DataProvider public Object[][] browserModes() {
        return new Object[][]{{"Headless", "LocalHeadless"}, {"Visible browsers", "Local"}};
    }

    @Test(dataProvider="browserModes") public void dynamicSelectionEmitsRealTestNgCaseEventsWithoutAnErpSession(String displayMode, String expectedExecution) {
        String execution = System.getProperty("ExecutionType"), video = System.getProperty("video.enabled");
        try {
            TestNG runner = new TestNG();
            runner.setUseDefaultListeners(false);
            XmlSuite emptyEntry = new XmlSuite(); emptyEntry.setName("Choose level");
            runner.setXmlSuites(new ArrayList<>(List.of(emptyEntry)));
            Set<String> started = ConcurrentHashMap.newKeySet(), passed = ConcurrentHashMap.newKeySet();
            runner.addListener(new ITestListener() {
                @Override public void onTestStart(ITestResult result) { started.add(result.getName()); }
                @Override public void onTestSuccess(ITestResult result) { passed.add(result.getName()); }
            });
            runner.addListener(new NativeInteractiveSuiteListener() {
                @Override String[] choose() { return new String[]{"Smoke", "3", displayMode}; }
                @Override void acquire(Path project) { /* The probe never connects to ERP. */ }
                @Override List<XmlSuite> selectedSuites(Path project, String level, int workers, Path output) {
                    XmlSuite selected = new XmlSuite(); selected.setName("Native local probe");
                    XmlTest test = new XmlTest(selected); test.setName("Live case events");
                    test.setXmlClasses(List.of(new XmlClass(NativeRunnerEventProbe.class)));
                    return List.of(selected);
                }
            });
            runner.run();
            Assert.assertEquals(System.getProperty("ExecutionType"), expectedExecution);
            Assert.assertFalse(runner.hasFailure());
            Assert.assertEquals(started, Set.of("firstCase", "secondCase"));
            Assert.assertEquals(passed, started);
        } finally {
            if (execution == null) System.clearProperty("ExecutionType"); else System.setProperty("ExecutionType", execution);
            if (video == null) System.clearProperty("video.enabled"); else System.setProperty("video.enabled", video);
        }
    }
}
