package SuiteSupport;

import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.*;
import org.testng.IAlterSuiteListener;
import org.testng.IExecutionListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.internal.Parser;

/** Keeps IntelliJ's native TestNG events while choosing the existing isolated execution plan. */
public class NativeInteractiveSuiteListener implements IAlterSuiteListener, IExecutionListener {
    private FileChannel channel;
    private FileLock lease;

    static int safeWorkers(int requested, double freeGb, int cpus) {
        if (requested < 1 || requested > 6) throw new IllegalArgumentException("Choose 1–6 browsers");
        int memory = freeGb >= 6 ? 6 : freeGb >= 4.5 ? 4 : freeGb >= 2.75 ? 3 : freeGb >= 2 ? 2 : 1;
        return Math.min(requested, Math.min(memory, Math.max(1, cpus / 2)));
    }

    static List<XmlSuite> buildSuites(Path project, String level, int workers, Path output) throws Exception {
        List<String> levels = switch (level) {
            case "Qualification" -> List.of("Smoke", "Sanity", "E2E");
            case "Smoke", "Sanity", "E2E", "Regression" -> List.of(level);
            default -> throw new IllegalArgumentException("Unsupported test level: " + level);
        };
        List<XmlSuite> result = new ArrayList<>();
        for (String name : levels) {
            Path plan = output.resolve(name.toLowerCase(Locale.ROOT));
            ParallelSuitePlanner.plan(project.resolve("src/test/resources/" + name.toLowerCase(Locale.ROOT) + "-testng.xml"),
                    plan, workers, Set.of());
            for (String stage : List.of("parallel", "exclusive")) {
                for (XmlSuite suite : new Parser(plan.resolve(stage + ".xml").toString()).parseToList()) {
                    if (suite.getTests().stream().allMatch(test -> test.getXmlClasses().isEmpty())) continue;
                    suite.setName(name + " / " + stage);
                    suite.getTests().forEach(test -> test.setName(name + " / " + stage));
                    result.add(suite);
                }
            }
        }
        return result;
    }

    String[] choose() throws Exception { return InteractiveTestLauncher.choose(); }

    List<XmlSuite> selectedSuites(Path project, String level, int workers, Path output) throws Exception {
        return buildSuites(project, level, workers, output);
    }

    void acquire(Path project) throws Exception {
        // Also recognise script runs started before the shared file gate was introduced.
        String root = project.toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        boolean scriptRunning = ProcessHandle.allProcesses().anyMatch(process -> {
            String command = process.info().commandLine().orElse("").replace('\\', '/').toLowerCase(Locale.ROOT);
            return process.pid() != ProcessHandle.current().pid() && command.contains(root)
                    && (command.contains("test-project.ps1") || command.contains("surefirebooter")
                        || command.contains("forkedbooter") || command.contains("remotetestngstarter"));
        });
        if (scriptRunning) throw new IllegalStateException("An ERP script run is still active. Wait for it to finish.");
        Files.createDirectories(project.resolve("target"));
        channel = FileChannel.open(project.resolve("target/erp-live-run.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try { lease = channel.tryLock(); }
        catch (java.nio.channels.OverlappingFileLockException busy) { lease = null; }
        if (lease == null) { release(); throw new IllegalStateException("Another ERP run is active. Wait for it to finish."); }
        Runtime.getRuntime().addShutdownHook(new Thread(this::release, "ERP run lock release"));
    }

    @Override public void alter(List<XmlSuite> suites) {
        try {
            String[] choice = choose();
            if (choice[0] == null) {
                suites.clear();
                System.out.println("Canceled. No ERP tests started.");
                return;
            }
            Path project = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
            acquire(project);
            double freeGb = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
                    .getFreeMemorySize() / (1024d * 1024 * 1024);
            int requested = Integer.parseInt(choice[1]);
            int workers = safeWorkers(requested, freeGb, Runtime.getRuntime().availableProcessors());
            Path output = project.resolve("target/validation/native-" + UUID.randomUUID());
            List<XmlSuite> planned = selectedSuites(project, choice[0], workers, output);
            System.setProperty("ExecutionType", InteractiveTestLauncher.executionType(choice[2]));
            System.setProperty("video.enabled", "false");
            suites.clear();
            suites.addAll(planned);
            System.out.printf(Locale.ROOT, "ERP %s: requested %d browsers, selected %d; free memory %.2f GB.%n",
                    choice[0], requested, workers, freeGb);
            System.out.println("Follow cases in IntelliJ's TestNG test tree. Plan: " + output);
            System.out.println("Browser display: " + choice[2]);
        } catch (Exception failure) {
            release();
            throw new IllegalStateException("ERP tests could not start: " + failure.getMessage(), failure);
        }
    }

    private synchronized void release() {
        try { if (lease != null && lease.isValid()) lease.release(); }
        catch (java.io.IOException failure) { System.err.println("Could not release ERP run lock: " + failure.getMessage()); }
        finally {
            lease = null;
            try { if (channel != null) channel.close(); }
            catch (java.io.IOException failure) { System.err.println("Could not close ERP run lock: " + failure.getMessage()); }
            channel = null;
        }
    }

    @Override public void onExecutionFinish() { release(); }
}
