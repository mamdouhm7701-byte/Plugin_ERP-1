package Plugin_ERP.Utiel;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.Models.TaxMode;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

public class PropertyReader {

    static {
        loadProperties();
    }

    public static Properties loadProperties() {
        try {
            Properties properties = new Properties();

            Collection<File> propertiesFiles =
                    FileUtils.listFiles(
                            new File("src/main/resources"), new String[] {"properties"}, true);

            propertiesFiles.forEach(
                    file -> {
                        try (var reader = new InputStreamReader(
                                FileUtils.openInputStream(file), StandardCharsets.UTF_8)) {
                            properties.load(reader);
                        } catch (IOException e) {
                            LogUtils.info(
                                    "Error loading properties file: "
                                            + file.getName()
                                            + " -); "
                                            + e.getMessage());
                        }
                    });

            // Local credentials stay outside Maven resources and version control.
            Path localLogin = Path.of("config", "Login.local.properties");
            if (Files.isRegularFile(localLogin)) {
                try (var reader = Files.newBufferedReader(localLogin, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }

            // CI can provide credentials without creating a file.
            for (String key : properties.stringPropertyNames()) {
                if (key.endsWith(".Username") || key.endsWith(".Password")
                        || key.equals("InvalidUsername") || key.equals("InvalidPassword")) {
                    String environmentKey = "ERP_" + key.replaceAll("[^A-Za-z0-9]", "_")
                            .toUpperCase(java.util.Locale.ROOT);
                    String value = System.getenv(environmentKey);
                    if (value != null && !value.isBlank()) properties.setProperty(key, value);
                }
            }

            properties.putAll(System.getProperties());
            System.getProperties().putAll(properties);

            return properties;

        } catch (Exception e) {
            LogUtils.info("Error loading properties: " + e.getMessage());
            return null;
        }
    }

    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    public static String getApplicationUrl() {

        String environment = getProperty("Environment");

        if (environment.equalsIgnoreCase("Test")) {
            return getProperty("TestURL");
        }

        String tenant = getProperty("Tenant");

        return getProperty(tenant);
    }

    public static String getValidUsername() {

        String environment = PropertyReader.getProperty("Environment");

        if (environment.equalsIgnoreCase("Test")) {
            return getProperty("Test.Username");
        }

        String tenant = PropertyReader.getProperty("Tenant");
        return getProperty(tenant + ".Username");
    }

    public static String getValidPassword() {

        String environment = PropertyReader.getProperty("Environment");

        if (environment.equalsIgnoreCase("Test")) {
            return getProperty("Test.Password");
        }

        String tenant = PropertyReader.getProperty("Tenant");
        return getProperty(tenant + ".Password");
    }

    public class PolicyManager {

        private PolicyManager() {}

        public static TaxMode getTaxMode() {
            return TaxMode.valueOf(PropertyReader.getProperty("TaxMode").trim().toUpperCase());
        }

        public static DiscountType getDiscountType() {
            return DiscountType.valueOf(
                    PropertyReader.getProperty("DiscountType").trim().toUpperCase());
        }

        public static BigDecimal getTaxPercentage() {
            return new BigDecimal(PropertyReader.getProperty("TaxPercentage"));
        }

        public static int getPrecision() {
            return Integer.parseInt(PropertyReader.getProperty("Precision"));
        }

        public static boolean isExclusive() {
            return getTaxMode() == TaxMode.EXCLUSIVE;
        }
    }
}
