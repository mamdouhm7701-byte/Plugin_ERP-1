/* شرح الملف: يثبت إعادة قراءة العنصر الذي استبدلته الشاشة، مع الاحتفاظ بنتيجة عدم الوجود وإظهار الأخطاء الأخرى. */
package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StableDomReadTest {
    private WebDriver driver() {
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[] {WebDriver.class},
                        (p, m, a) -> null);
    }

    @Test
    public void rereadsAfterAnElementIsReplaced() {
        var count = new AtomicInteger();
        Assert.assertEquals(
                StableDomRead.read(
                        driver(),
                        () -> {
                            if (count.incrementAndGet() == 1)
                                throw new StaleElementReferenceException("replaced");
                            return "fresh";
                        }),
                "fresh");
        Assert.assertEquals(count.get(), 2);
    }

    @Test
    public void falseAndNullAreResultsNotReasonsToWait() {
        var count = new AtomicInteger();
        Assert.assertFalse(
                StableDomRead.read(
                        driver(),
                        () -> {
                            count.incrementAndGet();
                            return false;
                        }));
        Assert.assertNull(StableDomRead.read(driver(), () -> null));
        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void unrelatedErrorsStayVisibleWithoutRetry() {
        var count = new AtomicInteger();
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        StableDomRead.read(
                                driver(),
                                () -> {
                                    count.incrementAndGet();
                                    throw new IllegalStateException("real failure");
                                }));
        Assert.assertEquals(count.get(), 1);
    }
}
