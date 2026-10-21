package Plugin_ERP.Utiel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Uses disposable files and synthetic credentials; never rewrites project configuration. */
public final class ConfigurationPropertiesTest {
    @Test
    public void reloadReflectsChangedAddedAndRemovedKeys() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("app.properties", "value=first\nremoved=old\n");
            Properties first = fixture.settings.reload();
            fixture.resource("app.properties", "value=second\nadded=new\n");
            Properties second = fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("value"), "second");
            Assert.assertEquals(second.getProperty("added"), "new");
            Assert.assertNull(fixture.settings.get("removed"));
            Assert.assertFalse(second.containsKey("removed"));
            Assert.assertEquals(first.getProperty("value"), "first");
        }
    }

    @Test
    public void deletingAResourceFileRemovesItsValues() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            Path file = fixture.resource("app.properties", "value=first\n");
            fixture.settings.reload();
            Files.delete(file);
            fixture.settings.reload();
            Assert.assertNull(fixture.settings.get("value"));
        }
    }

    @Test
    public void localCredentialsReloadAndFallBackWhenTheFileIsRemoved() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("login.properties", "Demo.Password=resource\n");
            fixture.local("Demo.Password=local-first\n");
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("Demo.Password"), "local-first");
            fixture.local("Demo.Password=local-second\n");
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("Demo.Password"), "local-second");
            Files.delete(fixture.localLogin);
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("Demo.Password"), "resource");
        }
    }

    @Test
    public void explicitJvmOverridesWinAndCanBeChangedOrClearedWithoutReload() throws Exception {
        try (var fixture = new Fixture(Map.of("ERP_DEMO_PASSWORD", "environment"))) {
            fixture.resource("login.properties", "Demo.Password=resource\n");
            fixture.local("Demo.Password=local\n");
            fixture.overrides.setProperty("Demo.Password", "jvm-first");
            Assert.assertEquals(fixture.settings.reload().getProperty("Demo.Password"), "jvm-first");
            fixture.overrides.setProperty("Demo.Password", "jvm-second");
            Assert.assertEquals(fixture.settings.get("Demo.Password"), "jvm-second");
            fixture.overrides.clear();
            Assert.assertEquals(fixture.settings.get("Demo.Password"), "environment");
            Assert.assertEquals(fixture.settings.reload().getProperty("Demo.Password"), "environment");
        }
    }

    @Test
    public void clearingAnOverrideRevealsTheLatestReloadedFileValue() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("app.properties", "value=first\n");
            fixture.overrides.setProperty("value", "explicit");
            fixture.settings.reload();
            fixture.resource("app.properties", "value=second\n");
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("value"), "explicit");
            fixture.overrides.remove("value");
            Assert.assertEquals(fixture.settings.get("value"), "second");
        }
    }

    @Test
    public void blankCredentialEnvironmentValuesFallBackAndUnrelatedVariablesAreIgnored() throws Exception {
        try (var fixture = new Fixture(Map.of("ERP_DEMO_PASSWORD", "  ", "ERP_BROWSER", "ignored"))) {
            fixture.resource("app.properties", "Demo.Password=resource\nBrowser=file\n");
            fixture.local("Demo.Password=local\n");
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("Demo.Password"), "local");
            Assert.assertEquals(fixture.settings.get("Browser"), "file");
        }
    }

    @Test
    public void snapshotsCannotModifyTheCacheOrPolluteJvmOverrides() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("app.properties", "value=file\n");
            Properties snapshot = fixture.settings.reload();
            snapshot.setProperty("value", "changed-by-caller");
            Assert.assertEquals(fixture.settings.get("value"), "file");
            Assert.assertFalse(fixture.overrides.containsKey("value"));
        }
    }

    @Test
    public void failedReloadDoesNotPublishPartiallyLoadedValues() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("a.properties", "value=first\n");
            fixture.settings.reload();
            fixture.resource("a.properties", "value=second\n");
            fixture.resource("z.properties", "broken=" + (char) 92 + "uZZZZ\n");
            IllegalStateException error = Assert.expectThrows(IllegalStateException.class,
                    fixture.settings::reload);
            Assert.assertTrue(error.getMessage().contains("z.properties"));
            Assert.assertEquals(fixture.settings.get("value"), "first");
            Files.delete(fixture.resources.resolve("z.properties"));
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("value"), "second");
        }
    }

    @Test
    public void invalidLocalFileFailsExplicitlyInsteadOfUsingPartialSettings() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("app.properties", "value=first\n");
            fixture.settings.reload();
            fixture.resource("app.properties", "value=second\n");
            Files.createDirectories(fixture.localLogin);
            IllegalStateException error = Assert.expectThrows(IllegalStateException.class,
                    fixture.settings::reload);
            Assert.assertTrue(error.getMessage().contains("Login.local.properties"));
            Assert.assertEquals(fixture.settings.get("value"), "first");
        }
    }

    @Test
    public void nestedFilesHaveDeterministicPrecedenceAndKeepUtf8Text() throws Exception {
        try (var fixture = new Fixture(Map.of())) {
            fixture.resource("z.properties", "shared=last\ntext=إعداد جديد\n");
            fixture.resource("nested/a.properties", "shared=nested\n");
            fixture.resource("a.properties", "shared=first\n");
            fixture.settings.reload();
            Assert.assertEquals(fixture.settings.get("shared"), "last");
            Assert.assertEquals(fixture.settings.get("text"), "إعداد جديد");
        }
    }

    @Test
    public void publicReaderKeepsDynamicJvmOverridesWithoutExportingFileSettings() {
        String key = "Review.Override." + UUID.randomUUID();
        String previousWait = System.getProperty("defultwait");
        try {
            System.setProperty(key, "explicit");
            Assert.assertEquals(PropertyReader.loadProperties().getProperty(key), "explicit");
            Assert.assertEquals(PropertyReader.getProperty(key), "explicit");
            System.clearProperty(key);
            Assert.assertNull(PropertyReader.getProperty(key));
            Assert.assertEquals(System.getProperty("defultwait"), previousWait);
            Assert.assertNotNull(PropertyReader.getProperty("defultwait"));
        } finally {
            System.clearProperty(key);
        }
    }

    private static final class Fixture implements AutoCloseable {
        private final Path parent = Path.of("target", "configuration-tests").toAbsolutePath().normalize();
        private final Path root;
        final Path resources;
        final Path localLogin;
        final Properties overrides = new Properties();
        final ConfigurationProperties settings;

        Fixture(Map<String, String> environment) throws IOException {
            Files.createDirectories(parent);
            root = Files.createTempDirectory(parent, "reload-");
            resources = Files.createDirectories(root.resolve("resources"));
            localLogin = root.resolve("config/Login.local.properties");
            settings = new ConfigurationProperties(resources, localLogin, environment::get, () -> overrides);
        }

        Path resource(String relative, String text) throws IOException {
            Path file = resources.resolve(relative);
            Files.createDirectories(file.getParent());
            return Files.writeString(file, text);
        }

        void local(String text) throws IOException {
            Files.createDirectories(localLogin.getParent());
            Files.writeString(localLogin, text);
        }

        @Override public void close() throws IOException {
            if (!root.toAbsolutePath().normalize().startsWith(parent))
                throw new IOException("Refusing cleanup outside the temporary configuration folder");
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
            }
        }
    }
}
