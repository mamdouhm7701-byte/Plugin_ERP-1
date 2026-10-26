package Plugin_ERP.Util.Allur;

import java.util.Properties;

/** Automatic HTML generation/opening is optional; raw Allure results are always retained. */
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
