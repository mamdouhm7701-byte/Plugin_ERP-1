/* شرح الملف: إخفاء رسائل Allure المتوقعة عند التجهيز خارج أي حالة أو خطوة مسجلة فقط.
 * فائدته: إبقاء أخطاء التقارير داخل الحالات ظاهرة مع تقليل ضوضاء التجهيز.
 */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public final class AllureContextNoiseFilter extends AbstractFilter {
    private static boolean installed;

    public static synchronized void install() {
        if (installed) return;
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        var config = context.getConfiguration();
        String name = "io.qameta.allure.AllureLifecycle";
        var logger = config.getLoggerConfig(name);
        if (!logger.getName().equals(name)) {
            logger = new LoggerConfig(name, logger.getLevel(), true);
            config.addLogger(name, logger);
        }
        var filter = new AllureContextNoiseFilter();
        filter.start();
        logger.addFilter(filter);
        context.updateLoggers();
        installed = true;
    }

    public static boolean expectedOutsideContext(String message, boolean hasContext) {
        if (hasContext || message == null) return false;
        return message.equals("Could not start step: no test case running")
                || message.equals("Could not add attachment: no test is running")
                || message.equals("Could not update step: no step running")
                || message.equals("Could not stop step: no step running")
                || message.matches(
                        "Could not (update|stop) step: step with uuid [0-9a-f-]+ not found");
    }

    @Override
    public Result filter(LogEvent event) {
        var current = org.testng.Reporter.getCurrentTestResult();
        boolean testIsRunning =
                current != null && (current.getMethod() == null || current.getMethod().isTest());
        return expectedOutsideContext(
                        event.getMessage().getFormattedMessage(),
                        testIsRunning
                                || Allure.getLifecycle().getCurrentTestCaseOrStep().isPresent())
                ? Result.DENY
                : Result.NEUTRAL;
    }
}
