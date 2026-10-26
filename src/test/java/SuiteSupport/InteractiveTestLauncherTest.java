package SuiteSupport;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;

public final class InteractiveTestLauncherTest {
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
