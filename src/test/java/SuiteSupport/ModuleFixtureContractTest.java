/* شرح الملف: مراجعة إمكانية تنظيف بيانات كل قسم.
 */
package SuiteSupport;

import TestSupport.BasicDataScenarioTest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.internal.Parser;

public final class ModuleFixtureContractTest {
    @Test
    public void everyBasicAndAccountingScreenOwnsItsLifecycle() throws Exception {
        for (String suite : List.of("basic-data", "general-accounting")) {
            for (var definition :
                    new Parser("src/test/resources/" + suite + "-testng.xml").parseToList()) {
                for (var test : definition.getTests()) {
                    for (var entry : test.getXmlClasses()) {
                        Class<?> type = Class.forName(entry.getName());
                        Assert.assertTrue(
                                BasicDataScenarioTest.class.isAssignableFrom(type),
                                "Screen bypasses owned fixture lifecycle: " + type.getName());
                        for (var method : type.getMethods()) {
                            var scenario = method.getAnnotation(Test.class);
                            if (scenario == null || !scenario.enabled()) continue;
                            Assert.assertEquals(
                                    scenario.dependsOnMethods().length,
                                    0,
                                    "A standalone scenario must prepare its own dependencies: "
                                            + method);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void ciKeepsTheNewCleanupRegressions() throws Exception {
        Set<String> selected =
                new Parser("src/test/resources/ci-testng.xml")
                        .parseToList().stream()
                                .flatMap(suite -> suite.getTests().stream())
                                .flatMap(test -> test.getXmlClasses().stream())
                                .map(entry -> entry.getName())
                                .collect(Collectors.toSet());
        Assert.assertTrue(
                selected.containsAll(
                        Set.of(
                                "TestSupport.BasicDataFixturesTest",
                                "SuiteSupport.ModuleFixtureContractTest")));
    }
}
