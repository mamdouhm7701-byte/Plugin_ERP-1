/* شرح الملف: يتأكد أن قراءة العميل وخط السير تجهز مساعد الشاشة وتحافظ على معرف العميل والفلتر المطلوب. */
package DealerModelTest;

import java.lang.reflect.Proxy;
import java.util.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RouteCalendarReadTest {
    @Test
    public void routeReadsPrepareTheirVueNamespaceAndKeepArguments() {
        var scripts = new ArrayList<String>();
        var values = new ArrayList<Object>();
        var driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class[] {WebDriver.class, JavascriptExecutor.class},
                                (p, m, a) -> {
                                    if (m.getName().equals("executeScript")) {
                                        String script = (String) a[0];
                                        scripts.add(script);
                                        values.add(((Object[]) a[1])[0]);
                                        if (!script.contains("const erpVue"))
                                            throw new JavascriptException("erpVue is not defined");
                                        return script.contains("selected.length===1")
                                                ? Boolean.TRUE
                                                : List.of("owned-event");
                                    }
                                    return null;
                                });
        var page = new Plugin_ERP.System_Modules.Login.DealerModel.RoutLineForDealer(driver);
        page.AssertOnlyOwnedCustomerSelected("owned-customer");
        Assert.assertEquals(page.OwnedEventTitles("owned-marker"), List.of("owned-event"));
        Assert.assertEquals(values, List.of("owned-customer", "owned-marker"));
        Assert.assertEquals(scripts.size(), 2);
    }
}
