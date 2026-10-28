/* شرح الملف: تحديد متى نجهز التقرير ومتى نفتحه.
 */
package Plugin_ERP.Util.Allur;

import java.util.Properties;

public record AllureReportPolicy(boolean generate, boolean open) {
    public static AllureReportPolicy current() {
        return from(System.getProperties(), System.getenv("CI"));
    }

    static AllureReportPolicy from(Properties properties, String ci) {
        boolean localDefault = !Boolean.parseBoolean(ci);
        boolean generate =
                Boolean.parseBoolean(
                        properties.getProperty(
                                "allure.report.generate", Boolean.toString(localDefault)));
        boolean open =
                generate
                        && Boolean.parseBoolean(
                                properties.getProperty(
                                        "allure.report.open", Boolean.toString(localDefault)));
        return new AllureReportPolicy(generate, open);
    }
}
