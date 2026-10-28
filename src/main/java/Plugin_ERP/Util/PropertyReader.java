/* شرح الملف: اختيار رابط البرنامج وقراءة الإعدادات المطلوبة للتشغيل.
 */
package Plugin_ERP.Util;

import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.Models.TaxMode;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Properties;

public class PropertyReader {

    private static final ConfigurationProperties CONFIGURATION =
            new ConfigurationProperties(
                    Path.of("src", "main", "resources"),
                    Path.of("config", "Login.local.properties"),
                    System::getenv,
                    System::getProperties);

    static {
        loadProperties();
    }

    public static Properties loadProperties() {
        return CONFIGURATION.reload();
    }

    public static String getProperty(String key) {
        return CONFIGURATION.get(key);
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
