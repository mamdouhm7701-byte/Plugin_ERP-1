package Plugin_ERP.Utiel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

/** File settings are isolated from explicit JVM overrides and published only after a full read. */
final class ConfigurationProperties {
    private final Path resources;
    private final Path localLogin;
    private final Function<String, String> environment;
    private final Supplier<Properties> systemProperties;
    private volatile Properties fileProperties = new Properties();

    ConfigurationProperties(Path resources, Path localLogin, Function<String, String> environment,
                            Supplier<Properties> systemProperties) {
        this.resources = resources;
        this.localLogin = localLogin;
        this.environment = environment;
        this.systemProperties = systemProperties;
    }

    synchronized Properties reload() {
        Properties fresh = new Properties();
        try (var files = Files.walk(resources)) {
            for (Path file : files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .sorted(Comparator.comparing(path -> resources.relativize(path).toString()
                            .replace('\\', '/'))).toList()) {
                read(file, fresh);
            }
        } catch (IOException failure) {
            throw new IllegalStateException("Cannot read configuration directory: " + resources, failure);
        }
        if (Files.exists(localLogin)) read(localLogin, fresh);

        Properties snapshot = new Properties();
        snapshot.putAll(fresh);
        for (String key : fresh.stringPropertyNames()) {
            String value = environmentOverride(key);
            if (value != null) snapshot.setProperty(key, value);
        }
        snapshot.putAll(systemProperties.get());
        fileProperties = fresh;
        return snapshot;
    }

    String get(String key) {
        String override = systemProperties.get().getProperty(key);
        if (override != null) return override;
        String fromEnvironment = environmentOverride(key);
        return fromEnvironment != null ? fromEnvironment : fileProperties.getProperty(key);
    }

    private String environmentOverride(String key) {
        if (!(key.endsWith(".Username") || key.endsWith(".Password")
                || key.equals("InvalidUsername") || key.equals("InvalidPassword"))) return null;
        String name = "ERP_" + key.replaceAll("[^A-Za-z0-9]", "_").toUpperCase(Locale.ROOT);
        String value = environment.apply(name);
        return value == null || value.isBlank() ? null : value;
    }

    private static void read(Path file, Properties into) {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            into.load(reader);
        } catch (IOException | IllegalArgumentException failure) {
            throw new IllegalStateException("Cannot load configuration file: " + file, failure);
        }
    }
}
