/* شرح الملف: يوقف تكرار الأوامر على متصفح ثبت تعطله، مع السماح بإغلاقه وحفظ سجلات الاسترجاع. انتهاء انتظار عنصر وحده لا يعتبر تعطلًا للمتصفح. */
package Plugin_ERP.Util;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

public final class BrowserSessionHealth {
    private static final Map<WebDriver, AtomicBoolean> SESSIONS =
            Collections.synchronizedMap(new IdentityHashMap<>());

    private BrowserSessionHealth() {}

    public static WebDriver decorate(WebDriver raw) {
        var blocked = new AtomicBoolean();
        var decorator =
                new org.openqa.selenium.support.decorators.WebDriverDecorator<WebDriver>() {
                    @Override
                    public Object call(
                            org.openqa.selenium.support.decorators.Decorated<?> target,
                            Method method,
                            Object[] args)
                            throws Throwable {
                        if (blocked.get() && !method.getName().equals("quit")) throw unavailable();
                        try {
                            return super.call(target, method, args);
                        } catch (Throwable failure) {
                            if (terminal(failure)) blocked.set(true);
                            throw failure;
                        }
                    }
                };
        var decorated = decorator.decorate(raw);
        SESSIONS.put(decorated, blocked);
        return decorated;
    }

    public static void alias(WebDriver source, WebDriver alias) {
        var state = SESSIONS.get(source);
        if (state != null) SESSIONS.put(alias, state);
    }

    public static boolean available(WebDriver driver) {
        var state = SESSIONS.get(driver);
        return state == null || !state.get();
    }

    public static void require(WebDriver driver) {
        if (!available(driver))
            throw new org.testng.SkipException(
                    "BROWSER_SESSION_UNAVAILABLE: المتصفح توقف عن الاستجابة؛ لم تنفذ الحالة التالية. سجلات البيانات محفوظة للتنظيف.");
    }

    public static void release(WebDriver driver) {
        var state = SESSIONS.get(driver);
        if (state != null)
            synchronized (SESSIONS) {
                SESSIONS.entrySet().removeIf(e -> e.getValue() == state);
            }
    }

    private static WebDriverException unavailable() {
        return new WebDriverException(
                "BROWSER_SESSION_UNAVAILABLE: المتصفح توقف عن الاستجابة؛ لن نكرر الأوامر عليه. سجلات البيانات محفوظة للاسترجاع.");
    }

    public static boolean terminal(Throwable failure) {
        var seen = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        while (failure != null && seen.add(failure)) {
            String text = Objects.toString(failure.getMessage(), "").toLowerCase(Locale.ROOT);
            if (text.contains("timed out receiving message from renderer")
                    || text.contains("tab crashed")
                    || text.contains("chrome not reachable")
                    || text.contains("invalid session id")
                    || text.contains("disconnected: not connected to devtools")
                    || text.contains("session deleted because of page crash")) return true;
            failure = failure.getCause();
        }
        return false;
    }
}
