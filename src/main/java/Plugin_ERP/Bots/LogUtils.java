package Plugin_ERP.Bots;

import java.util.logging.Logger;

public class LogUtils {

    private LogUtils() {
        // prevent instantiation
    }

    private static Logger getLogger() {
        String className = Thread.currentThread()
                .getStackTrace()[3]
                .getClassName();

        return Logger.getLogger(className);
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void warn(String message) {
        getLogger().warning(message);
    }

    public static void error(String message) {
        getLogger().severe(message);
    }

    public static void fatal(String message) {
        getLogger().severe("[FATAL] " + message);
    }

    public static void debug(String message) {
        getLogger().fine(message);
    }
}
