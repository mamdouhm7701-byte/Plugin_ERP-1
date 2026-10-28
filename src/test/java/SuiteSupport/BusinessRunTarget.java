/* شرح الملف: تحديد البيئة والرابط وبيانات الدخول المطلوبة لهذه التجربة.
 */
package SuiteSupport;

import Plugin_ERP.Util.PropertyReader;
import java.net.URI;
import java.util.*;

final class BusinessRunTarget {
    private final String environment;
    private final String tenant;
    private final String url;
    private final String username;
    private final char[] password;

    BusinessRunTarget(
            String environment, String tenant, String url, String username, char[] password) {
        if (!Set.of("Cloud", "Test").contains(environment))
            throw new IllegalArgumentException("اختر Cloud أو Test");
        if (tenant == null || !tenant.trim().matches("[A-Za-z0-9][A-Za-z0-9_-]{0,63}"))
            throw new IllegalArgumentException("اكتب اسم تننت صالحًا بحروف إنجليزية وأرقام وشرطة");
        if (Set.of("Environment", "Tenant", "ExecutionType").contains(tenant.trim()))
            throw new IllegalArgumentException(
                    "اسم التننت يتعارض مع اسم إعداد تشغيل؛ اختر اسمًا آخر");
        this.environment = environment;
        this.tenant = tenant.trim();
        this.url = normalizeUrl(url);
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? new char[0] : password.clone();
    }

    static BusinessRunTarget configured(String environment, String tenant) {
        String key = environment.equals("Test") && !isTestUrlKey(tenant) ? "TestURL" : tenant;
        return new BusinessRunTarget(
                environment, tenant, PropertyReader.getProperty(key), "", null);
    }

    static List<String> tenants(Properties properties, String environment) {
        return properties.stringPropertyNames().stream()
                .filter(
                        key ->
                                environment.equals("Test")
                                        ? isTestUrlKey(key)
                                        : !isTestUrlKey(key)
                                                && properties.containsKey(key + ".Username"))
                .filter(
                        key -> {
                            try {
                                normalizeUrl(properties.getProperty(key));
                                return true;
                            } catch (IllegalArgumentException invalid) {
                                return false;
                            }
                        })
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private static boolean isTestUrlKey(String key) {
        return key.equals("TestURL") || key.endsWith("TestURL");
    }

    static String normalizeUrl(String value) {
        try {
            var uri = URI.create(Objects.toString(value, "").trim());
            if (!Set.of("http", "https").contains(Objects.toString(uri.getScheme(), ""))
                    || uri.getHost() == null
                    || uri.getUserInfo() != null
                    || uri.getQuery() != null) throw new IllegalArgumentException();
            return new URI(
                            uri.getScheme(),
                            null,
                            uri.getHost(),
                            uri.getPort(),
                            uri.getPath(),
                            null,
                            null)
                    .toString()
                    .replaceAll("/+$", "");
        } catch (Exception invalid) {
            throw new IllegalArgumentException(
                    "اكتب رابط البرنامج كاملًا يبدأ بـhttp:// أو https://");
        }
    }

    Map<String, String> settings() {
        return settings(PropertyReader::getProperty);
    }

    Map<String, String> settings(java.util.function.Function<String, String> configured) {
        String fallback = environment.equals("Test") ? "Test" : tenant;
        String configuredPrefix =
                environment.equals("Test") && configured.apply(tenant + ".Username") != null
                        ? tenant
                        : fallback;
        String user =
                username.isBlank() ? configured.apply(configuredPrefix + ".Username") : username;
        String secret =
                password.length == 0
                        ? configured.apply(configuredPrefix + ".Password")
                        : new String(password);
        if (user == null || user.isBlank() || secret == null || secret.isBlank())
            throw new IllegalArgumentException(
                    "بيانات الدخول غير مكتملة للتننت المختار؛ أدخلها في نافذة التشغيل أو إعدادات الدخول المحلية");
        var settings = new LinkedHashMap<String, String>();
        settings.put("Environment", environment);
        settings.put("Tenant", tenant);
        settings.put(environment.equals("Test") ? "TestURL" : tenant, url);
        settings.put(fallback + ".Username", user);
        settings.put(fallback + ".Password", secret);
        return settings;
    }

    void clearSecrets() {
        Arrays.fill(password, '\0');
    }

    Map<String, String> description() {
        return Map.of("environment", environment, "tenant", tenant, "url", url);
    }

    @Override
    public String toString() {
        return description().toString();
    }
}
