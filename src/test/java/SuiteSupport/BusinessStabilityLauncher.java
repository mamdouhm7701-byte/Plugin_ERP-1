/* شرح الملف: اختيار إعدادات البيزنس مرة واحدة ثم تشغيل جولات منفصلة بنفس الاختيارات.
 * فائدته: يقيس قابلية تكرار التشغيل بفروع جديدة ويوقف الجولات إذا تعذر التجهيز أو التنظيف.
 */
package SuiteSupport;

import Plugin_ERP.Util.PropertyReader;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public final class BusinessStabilityLauncher {
    private BusinessStabilityLauncher() {}

    public static void main(String[] args) throws Exception {
        int repeats = Integer.parseInt(args[0]);
        if (repeats < 3 || repeats > 10) throw new IllegalArgumentException("Choose 3–10 rounds");
        Path output = Path.of(args[1]).toAbsolutePath().normalize();
        PropertyReader.loadProperties();
        var choice = BusinessRunDialog.choose();
        if (choice == null) {
            System.exit(2);
            return;
        }
        run(repeats, output, choice);
    }

    static void run(int repeats, Path output, BusinessRunDialog.Selection choice) throws Exception {
        if (repeats < 3 || repeats > 10) throw new IllegalArgumentException("Choose 3–10 rounds");
        Files.createDirectories(output);
        var credentials = choice.target().settings();
        choice.target().clearSecrets();
        try {
            var request = new LinkedHashMap<String, Object>(choice.target().description());
            request.put("activity", choice.business().activity().name());
            request.put("discount", choice.business().discount().name());
            request.put("tax", choice.business().tax().name());
            request.put("stocktaking", choice.business().stocktaking().name());
            request.put("additionalTax", choice.business().additionalTax());
            request.put("level", choice.level());
            request.put("execution", choice.execution());
            request.put("workers", choice.workers());
            Path config = output.resolve("request.json");
            Files.writeString(config, new JSONObject(request).toString(2));
            var rounds = new ArrayList<Map<String, Object>>();
            for (int i = 1; i <= repeats; i++) {
                Path round = output.resolve("round-" + i);
                Files.createDirectories(round);
                var command = new ArrayList<String>();
                command.add(ProcessHandle.current().info().command().orElseThrow());
                java.lang.management.ManagementFactory.getRuntimeMXBean()
                        .getInputArguments()
                        .stream()
                        .filter(arg -> arg.startsWith("-javaagent:") && arg.contains("aspectj"))
                        .forEach(command::add);
                command.addAll(
                        List.of(
                                "-Dfile.encoding=UTF-8",
                                "-Dallure.run.id=stability-" + UUID.randomUUID(),
                                "-Dallure.results.directory=" + round.resolve("allure-results"),
                                "-Dallure.report.directory=" + round.resolve("allure-report"),
                                "-Dallure.report.generate=false",
                                "-Dallure.report.open=false",
                                "-cp",
                                System.getProperty("java.class.path"),
                                BusinessStabilityChild.class.getName(),
                                config.toString(),
                                round.toString()));
                var builder =
                        new ProcessBuilder(command)
                                .redirectErrorStream(true)
                                .redirectOutput(round.resolve("run.log").toFile());
                builder.environment().put("REPORT_MAIL_ENABLED", "false");
                var process = builder.start();
                try (var input = process.getOutputStream()) {
                    input.write(
                            new JSONObject(credentials)
                                    .toString()
                                    .getBytes(java.nio.charset.StandardCharsets.UTF_8));
                }
                int exit = roundExit(round, process.waitFor());
                rounds.add(Map.of("folder", round.getFileName().toString(), "exitCode", exit));
                Files.writeString(
                        output.resolve("rounds.json"),
                        new JSONObject(Map.of("requested", repeats, "rounds", rounds)).toString(2));
                System.out.println(
                        "Stability round " + i + "/" + repeats + " finished; exit=" + exit);
                // Never start another branch while the preceding cleanup is uncertain.
                if (exit >= 2) break;
            }
        } finally {
            credentials.clear();
        }
    }

    static int roundExit(Path round, int processExit) {
        if (processExit >= 2) return processExit;
        try {
            Path marker = round.resolve("round-completion.json");
            if (!Files.isRegularFile(marker)) return 2;
            var result = new JSONObject(Files.readString(marker));
            return result.getBoolean("cleaned") && result.getInt("exitCode") == processExit
                    ? processExit
                    : 2;
        } catch (java.io.IOException | org.json.JSONException failure) {
            Plugin_ERP.Bots.LogUtils.warn(
                    "Round completion could not be verified; subsequent rounds stopped.");
            return 2;
        }
    }
}
