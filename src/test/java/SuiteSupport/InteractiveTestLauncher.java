/* شرح الملف: اختيار حزمة الاختبارات وطريقة عرض المتصفح قبل التشغيل.
 */
package SuiteSupport;

import java.awt.GridLayout;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class InteractiveTestLauncher {
    private static final List<String> LEVELS =
            List.of("Smoke", "Sanity", "E2E", "Regression", "Qualification");

    private InteractiveTestLauncher() {}

    static List<String> command(Path powershell, Path project, String level, int workers) {
        if (!LEVELS.contains(level)) throw new IllegalArgumentException("Unsupported test level");
        if (workers < 1 || workers > 6)
            throw new IllegalArgumentException("Choose between 1 and 6 browsers");
        return List.of(
                powershell.toString(),
                "-NoProfile",
                "-File",
                project.resolve("scripts/Test-Project.ps1").toString(),
                "-Mode",
                level,
                "-Workers",
                Integer.toString(workers));
    }

    static String executionType(String displayMode) {
        return switch (displayMode) {
            case "Headless" -> "LocalHeadless";
            case "Visible browsers" -> "Local";
            default -> throw new IllegalArgumentException("Choose Headless or Visible browsers");
        };
    }

    static List<String> command(
            Path powershell, Path project, String level, int workers, String displayMode) {
        List<String> arguments = new ArrayList<>(command(powershell, project, level, workers));
        arguments.addAll(List.of("-ExecutionType", executionType(displayMode)));
        return arguments;
    }

    static List<String> command(
            Path powershell,
            Path project,
            String level,
            int workers,
            String displayMode,
            String profile) {
        var arguments = new ArrayList<>(command(powershell, project, level, workers, displayMode));
        if (profile != null && !profile.isBlank()) {
            if (!profile.matches("[a-z0-9][a-z0-9_-]{0,63}"))
                throw new IllegalArgumentException("Invalid branch profile name");
            arguments.addAll(List.of("-BranchProfile", profile));
        }
        return arguments;
    }

    private static Path powershell() {
        List<Path> candidates = new ArrayList<>();
        for (String directory :
                System.getenv().getOrDefault("PATH", "").split(java.io.File.pathSeparator))
            if (!directory.isBlank()) candidates.add(Path.of(directory, "pwsh.exe"));
        String programFiles = System.getenv("ProgramFiles");
        if (programFiles != null)
            candidates.add(Path.of(programFiles, "PowerShell", "7", "pwsh.exe"));
        candidates.add(
                Path.of(
                        System.getProperty("user.home"),
                        ".cache",
                        "codex-runtimes",
                        "codex-primary-runtime",
                        "dependencies",
                        "native",
                        "powershell",
                        "pwsh.exe"));
        return candidates.stream()
                .filter(Files::isRegularFile)
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "PowerShell 7 (pwsh.exe) was not found on this computer."));
    }

    private static void prepareMavenPath(Map<String, String> environment)
            throws java.io.IOException {
        String pathKey =
                environment.keySet().stream()
                        .filter(k -> k.equalsIgnoreCase("PATH"))
                        .findFirst()
                        .orElse("PATH");
        String current = environment.getOrDefault(pathKey, "");
        for (String directory : current.split(java.io.File.pathSeparator))
            if (!directory.isBlank() && Files.isRegularFile(Path.of(directory, "mvn.cmd"))) return;
        List<Path> candidates = new ArrayList<>();
        for (String key : List.of("MAVEN_HOME", "M2_HOME"))
            if (environment.get(key) != null) candidates.add(Path.of(environment.get(key), "bin"));
        Path localTools = Path.of(System.getProperty("user.home"), "tools");
        if (Files.isDirectory(localTools))
            try (var directories = Files.list(localTools)) {
                directories
                        .filter(p -> p.getFileName().toString().startsWith("apache-maven-"))
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> candidates.add(p.resolve("bin")));
            }
        Path bin =
                candidates.stream()
                        .filter(p -> Files.isRegularFile(p.resolve("mvn.cmd")))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Maven (mvn.cmd) was not found. Check IntelliJ's"
                                                        + " inherited PATH or MAVEN_HOME."));
        environment.put(pathKey, bin + java.io.File.pathSeparator + current);
    }

    static String[] choose() throws Exception {
        var choice = new java.util.concurrent.CompletableFuture<String[]>();
        SwingUtilities.invokeLater(
                () -> {
                    JFrame window = new JFrame("ERP Test Runner");
                    window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    JComboBox<String> level = new JComboBox<>(LEVELS.toArray(String[]::new));
                    JSpinner workers = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
                    JComboBox<String> displayMode =
                            new JComboBox<>(new String[] {"Headless", "Visible browsers"});
                    JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
                    panel.add(new JLabel("Test level"));
                    panel.add(level);
                    panel.add(new JLabel("Maximum parallel browsers (1â€“6)"));
                    panel.add(workers);
                    panel.add(new JLabel("Browser display"));
                    panel.add(displayMode);
                    panel.add(new JLabel("Independent cases overlap; exceptions stay sequential."));
                    panel.add(
                            new JLabel(
                                    "Available memory may reduce the actual number; see the Run"
                                            + " console."));
                    JButton start = new JButton("Start tests");
                    JButton cancel = new JButton("Cancel");
                    JPanel buttons = new JPanel();
                    buttons.add(start);
                    buttons.add(cancel);
                    JPanel content = new JPanel(new java.awt.BorderLayout(0, 16));
                    content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
                    content.add(panel, java.awt.BorderLayout.CENTER);
                    content.add(buttons, java.awt.BorderLayout.SOUTH);
                    window.setContentPane(content);
                    start.addActionListener(
                            event -> {
                                try {
                                    workers.commitEdit();
                                } catch (java.text.ParseException invalid) {
                                    workers.requestFocusInWindow();
                                    return;
                                }
                                choice.complete(
                                        new String[] {
                                            Objects.toString(level.getSelectedItem()),
                                            workers.getValue().toString(),
                                            Objects.toString(displayMode.getSelectedItem())
                                        });
                                window.dispose();
                            });
                    cancel.addActionListener(
                            event -> {
                                choice.complete(new String[2]);
                                window.dispose();
                            });
                    window.addWindowListener(
                            new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent event) {
                                    choice.complete(new String[2]);
                                }
                            });
                    window.pack();
                    window.setResizable(false);
                    window.setLocationRelativeTo(null);
                    window.setAlwaysOnTop(true);
                    window.setVisible(true);
                    window.toFront();
                });
        return choice.get();
    }

    public static void main(String[] args) throws Exception {
        Path project = Path.of(args.length == 0 ? "." : args[0]).toAbsolutePath().normalize();
        if (!Files.isRegularFile(project.resolve("scripts/Test-Project.ps1")))
            throw new IllegalArgumentException(
                    "Select this project's root as the working directory.");
        String[] selected = choose();
        if (selected[0] == null) {
            System.out.println("Canceled. No tests started.");
            return;
        }
        ProcessBuilder builder =
                new ProcessBuilder(
                                command(
                                        powershell(),
                                        project,
                                        selected[0],
                                        Integer.parseInt(selected[1]),
                                        selected[2],
                                        System.getProperty("branch.profile")))
                        .directory(project.toFile())
                        .inheritIO();
        builder.environment().put("JAVA_HOME", System.getProperty("java.home"));
        prepareMavenPath(builder.environment());
        System.out.println(
                "Starting "
                        + selected[0]
                        + " / "
                        + selected[2]
                        + "; requested maximum browsers: "
                        + selected[1]);
        int exitCode = builder.start().waitFor();
        if (exitCode != 0)
            throw new IllegalStateException(
                    "Test run exited with code "
                            + exitCode
                            + ". See the Run console and saved results.");
    }
}
