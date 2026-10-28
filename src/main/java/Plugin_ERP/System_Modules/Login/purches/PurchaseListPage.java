/* شرح الملف: مساعدة لتصفح قوائم مستندات الشراء وتطبيق شروط البحث والترتيب والتصدير. فائدتها الوصول
 * إلى المشتريات المطلوب مراجعتها.
 */
package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public abstract class PurchaseListPage extends AbstractForPurchases {
    protected final WebDriverWait ready;
    private final PurchaseDocumentList documentList;

    protected PurchaseListPage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        documentList = new PurchaseDocumentList(this, driver, ready);
    }

    protected abstract void puClick(By locator);

    public abstract void puDateFilter(String name, String value);

    public abstract void puType(By locator, String value);

    abstract void puType(By locator, String value, boolean blur);

    protected abstract Object js(String script, Object... args);

    public abstract void puOpen(String route);

    public abstract void puButton(String text);

    public abstract void puChoose(String label, String name);

    @Step("تصفية القائمة بالمتعامل: {customer}")
    public void puListScope(String customer) {
        documentList.puListScope(customer);
    }

    public void puAwaitListReady() {
        documentList.puAwaitListReady();
    }

    @Step("تطبيق فلاتر القائمة")
    public void puApplyListFilters() {
        documentList.puApplyListFilters();
    }

    public String puListFixtureDate() {
        return documentList.puListFixtureDate();
    }

    public List<Map<String, Object>> puListRows() {
        return documentList.puListRows();
    }

    public void puWaitListIds(Set<Object> expected) {
        documentList.puWaitListIds(expected);
    }

    @Step("إدخال فلتر {name}: {text}")
    public void puListText(String name, String text) {
        documentList.puListText(name, text);
    }

    @Step("اختيار {value} من فلتر {group}")
    public void puListRadio(String group, String value) {
        documentList.puListRadio(group, value);
    }

    public List<String> puHeaders() {
        return documentList.puHeaders();
    }

    @Step("عرض {size} سجل في الصفحة")
    public void puPageSize(int size) {
        documentList.puPageSize(size);
    }

    @Step("الانتقال للصفحة التالية")
    public void puNextPage() {
        documentList.puNextPage();
    }

    @Step("العودة للصفحة الأولى")
    public void puFirstPage() {
        documentList.puFirstPage();
    }

    @Step("ترتيب القائمة حسب {label}")
    public void puSort(String label) {
        documentList.puSort(label);
    }

    @Step("تصدير القائمة بصيغة {format}")
    public void puExport(String format) {
        documentList.puExport(format);
    }
}
