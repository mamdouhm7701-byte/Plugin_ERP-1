/* شرح الملف: مراجعة الأصناف والمبالغ في الطباعة، ضمن المطعم ونقاط البيع.
 */
package SpecialSalesTest;

import io.qameta.allure.Allure;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

final class SpecialSalesPrintAssertions {
    private SpecialSalesPrintAssertions() {}

    @SuppressWarnings("unchecked")
    static void assertReceipt(WebDriver driver, Runnable action, String itemName, String code) {
        assertReceipt(driver, action, itemName, () -> code);
    }

    @SuppressWarnings("unchecked")
    static void assertReceipt(
            WebDriver driver,
            Runnable action,
            String itemName,
            java.util.function.Supplier<String> code) {
        var js = (JavascriptExecutor) driver;
        js.executeScript(
                """
                window.__specialSalesPrint={printed:[],errors:[]};
                if(!window.__specialSalesPrintInstalled){
                  window.__specialSalesPrintInstalled=true;
                  window.addEventListener('error',e=>window.__specialSalesPrint?.errors.push(String(e.message)));
                  window.addEventListener('unhandledrejection',e=>window.__specialSalesPrint?.errors.push(String(e.reason)));
                  const originalOpen=window.open;
                  window.open=function(...args){
                    const child=originalOpen.apply(this,args);
                    if(child){
                      child.print=function(...printArgs){
                        try{window.__specialSalesPrint.printed.push({text:child.document.body?.innerText||'',html:child.document.documentElement.outerHTML});}
                        catch(error){window.__specialSalesPrint.errors.push(String(error));}
                        // Capture the application's print request without opening Chrome's blocking preview.
                        return undefined;
                      };
                    }
                    return child;
                  };
                }
                """);
        action.run();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(35))
                    .until(
                            d ->
                                    Boolean.TRUE.equals(
                                            js.executeScript(
                                                    "return"
                                                            + " window.__specialSalesPrint.printed.length>0")));
        } catch (org.openqa.selenium.TimeoutException failure) {
            throw new AssertionError(
                    "لم يصل طلب طباعة الإيصال إلى الالتقاط؛ أخطاء الشاشة="
                            + js.executeScript("return window.__specialSalesPrint.errors"),
                    failure);
        }
        var receipt =
                (Map<String, Object>)
                        js.executeScript("return window.__specialSalesPrint.printed.at(-1)");
        Allure.addAttachment(
                "محتوى الإيصال عند طلب الطباعة — بدون فتح معاينة Chrome",
                "text/html",
                receipt.get("html").toString(),
                ".html");
        String text = receipt.get("text").toString();
        Assert.assertTrue(text.contains(itemName), "Printed receipt must contain the owned item");
        Assert.assertTrue(
                text.matches("(?s).*\\b" + java.util.regex.Pattern.quote(code.get()) + "\\b.*"),
                "Printed receipt must contain the saved invoice code");
    }
}
