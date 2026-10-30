/* شرح الملف: يتأكد أن البحث عن السند يستخدم عمود الكود في جدوله نفسه، حتى لو كانت الشاشة تعرض جدولًا آخر. */
package Plugin_ERP.System_Modules.Login.FinancialMovements;

import java.lang.reflect.Proxy;
import java.util.List;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialRecordListTest {
    private WebElement text(String value) {
        return element((method, args) -> method.equals("getDomProperty") ? value : null);
    }

    private interface Answer {
        Object call(String method, Object[] args);
    }

    private WebElement element(Answer answer) {
        return (WebElement)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) -> answer.call(method.getName(), args));
    }

    @Test
    public void codeColumnBelongsToTheRowsTable() {
        WebElement table =
                element(
                        (method, args) ->
                                method.equals("findElements")
                                        ? List.of(text("التاريخ"), text("الكود"))
                                        : null);
        WebElement row =
                element(
                        (method, args) ->
                                switch (method) {
                                    case "findElement" -> table;
                                    case "findElements" -> List.of(text("2026-10-30"), text("42"));
                                    default -> null;
                                });
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("findElements"))
                                        return List.of(text("الكود"));
                                    return null;
                                });
        var list = new FinancialRecordList(null, driver, null);
        list.identifyMovement("owned", "42");
        Assert.assertTrue(list.matchesOwnedRow(row, "owned"));
        list.identifyMovement("another", "43");
        Assert.assertFalse(list.matchesOwnedRow(row, "another"));
    }
}
