/* شرح الملف: اختبارات اختيار حزمة الاختبارات وطريقة عرض المتصفح قبل التشغيل.
 */
package SuiteSupport;

import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class InteractiveTestLauncherTest {
    @Test
    public void optionalProfileReachesChildProcessWithoutChangingLegacyCommand() {
        var shell = Path.of("pwsh.exe");
        var root = Path.of(".");
        var original = InteractiveTestLauncher.command(shell, root, "Smoke", 3, "Headless");
        Assert.assertEquals(
                InteractiveTestLauncher.command(shell, root, "Smoke", 3, "Headless", null),
                original);
        var selected =
                InteractiveTestLauncher.command(shell, root, "Smoke", 3, "Headless", "reference");
        Assert.assertEquals(selected.subList(0, original.size()), original);
        Assert.assertEquals(
                selected.subList(original.size(), selected.size()),
                List.of("-BranchProfile", "reference"));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        InteractiveTestLauncher.command(
                                shell, root, "Smoke", 3, "Headless", "../other"));
    }

    @Test
    public void selectedBrowserDisplayReachesTheScript() {
        for (String mode : List.of("Headless", "Visible browsers")) {
            var arguments =
                    InteractiveTestLauncher.command(
                            Path.of("pwsh.exe"), Path.of("."), "Smoke", 3, mode);
            Assert.assertEquals(arguments.get(arguments.size() - 2), "-ExecutionType");
            Assert.assertEquals(
                    arguments.getLast(), mode.equals("Headless") ? "LocalHeadless" : "Local");
        }
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> InteractiveTestLauncher.executionType("Unknown"));
    }

    @Test
    public void selectedLevelAndBrowserCountArePassedAsSeparateArguments() {
        Path root = Path.of("C:/Project with spaces");
        Path powershell = Path.of("C:/PowerShell/pwsh.exe");
        var command = InteractiveTestLauncher.command(powershell, root, "Regression", 5);
        Assert.assertEquals(
                command,
                List.of(
                        powershell.toString(),
                        "-NoProfile",
                        "-File",
                        root.resolve("scripts/Test-Project.ps1").toString(),
                        "-Mode",
                        "Regression",
                        "-Workers",
                        "5"));
    }

    @Test
    public void allSupportedCountsCanBeSelectedWithoutChangingAnyXml() {
        for (int workers = 1; workers <= 6; workers++)
            Assert.assertEquals(
                    InteractiveTestLauncher.command(
                                    Path.of("pwsh.exe"), Path.of("."), "Smoke", workers)
                            .getLast(),
                    Integer.toString(workers));
    }

    @Test
    public void invalidSelectionsCannotStartAnUnboundedRun() {
        for (int workers : new int[] {0, 7})
            Assert.expectThrows(
                    IllegalArgumentException.class,
                    () ->
                            InteractiveTestLauncher.command(
                                    Path.of("pwsh.exe"), Path.of("."), "Smoke", workers));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        InteractiveTestLauncher.command(
                                Path.of("pwsh.exe"), Path.of("."), "Unknown", 3));
    }
}
