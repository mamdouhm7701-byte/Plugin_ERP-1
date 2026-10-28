package Plugin_ERP.System_Modules.Login.SpecialSales;

import io.qameta.allure.Step;
import org.openqa.selenium.*;

/** Restaurant orders are a separate page/workflow, never a normal-invoice subclass. */
public final class RestaurantInvoicePage extends SpecialSalesPage {
    public RestaurantInvoicePage(WebDriver driver) {
        super(driver);
    }

    public String route() {
        return "restaurant-menu";
    }

    public String endpoint() {
        return "ItemTransactions-RestaurantSales";
    }

    public String channel() {
        return "Resturant";
    }

    @Override
    public void open() {
        super.open();
        closeModal();
        type("خارجية");
    }

    @Step("اختيار نوع الطلب: {label}")
    public void type(String label) {
        click(
                By.xpath(
                        "//section[@id='options']//h6[normalize-space()='"
                                + label
                                + "']/ancestor::div[contains(@class,'order-type-card')][1]"));
    }

    @Step("إضافة الصنف {name} بالباركود {barcode}")
    public void addItem(String name, String barcode, String category) {
        try {
            ready.until(
                    d ->
                            Boolean.TRUE.equals(
                                    js(
                                            """
                                        const queue=[erpVue.root()];
                                        while(queue.length){const vm=queue.shift();if(!vm)continue;queue.push(...(erpVue.children(vm)||[]));
                                          if(Array.isArray(vm.$data?.allItems))return !vm.isLoadingItems&&vm.allItems.some(item=>item.arabicName===arguments[0]||item.englishName===arguments[0]);
                                        }return false;
                                        """,
                                            name)));
        } catch (TimeoutException failure) {
            try {
                Object catalog =
                        js(
                                """
                    const queue=[erpVue.root()];
                    while(queue.length){const vm=queue.shift();queue.push(...(erpVue.children(vm)||[]));
                      if(Array.isArray(vm.$data?.allItems))return {
                        expectedName:arguments[0],count:vm.allItems.length,loading:vm.isLoadingItems,
                        storeId:vm.selectedItem?.storeId,defaultStoreId:vm.currentBranch?.defaultStoreId,
                        ownItems:vm.allItems.filter(item=>(item.arabicName||'').startsWith('QA-SP-')).map(item=>({id:item.id,name:item.arabicName,categoryId:item.categoryId})),
                        methods:Object.fromEntries(Object.entries(vm.$options.methods||{}).filter(([key])=>/item|categor|load/i.test(key)).map(([key,value])=>[key,String(value)]))};
                    }return {error:'Restaurant catalog component is missing'};
                    """,
                                name);
                io.qameta.allure.Allure.addAttachment(
                        "Restaurant catalog readiness",
                        "application/json",
                        org.json.simple.JSONValue.toJSONString(catalog),
                        ".json");
            } catch (RuntimeException diagnosticsFailure) {
                failure.addSuppressed(diagnosticsFailure);
            }
            throw failure;
        }
        click(
                By.xpath(
                        "//div[contains(@class,'catalog-tile')]//*[normalize-space(text())='"
                                + category
                                + "']"));
        click(
                By.xpath(
                        "//div[contains(@class,'catalog-tile')]//*[normalize-space(text())='"
                                + name
                                + "']"));
        ready.until(
                d ->
                        ((java.util.List<?>) document().get("lines"))
                                .stream()
                                        .anyMatch(
                                                v ->
                                                        ((java.util.Map<?, ?>) v)
                                                                .containsValue(name)));
        awaitTotals();
    }

    @Step("اختيار نوع خصم حركة المطعم: {name}")
    public void chooseMovementDiscountType(String name) {
        var actions =
                new Plugin_ERP.Util.FormInteractions(
                        ready,
                        element -> js("arguments[0].scrollIntoView({block:'center'})", element));
        actions.choose(
                By.xpath(
                        "//div[@id='sidebar-invoice-add-payment']//span[normalize-space()='نوع الخصم']/parent::div/following-sibling::div[1]//input[@type='search']"),
                name,
                Plugin_ERP.Util.FormInteractions.OptionMatch.EXACT);
    }

    @Step("فتح شاشة سداد الفاتورة")
    public void openPayment() {
        button("دفع");
    }

    @Step("حفظ الفاتورة")
    public void save() {
        click(
                By.xpath(
                        "//div[@id='sidebar-invoice-add-payment']//button[normalize-space()='حفظ']"));
    }

    @Step("فتح قائمة الفواتير")
    public void invoices() {
        closePayment();
        button("الفواتير");
    }

    @Step("سداد الفاتورة وإغلاقها")
    public void payAndClose() {
        int count = saves().size();
        save();
        ready.until(
                d ->
                        saves().size() > count
                                || driver
                                        .findElements(
                                                By.cssSelector(".swal2-container .swal2-confirm"))
                                        .stream()
                                        .anyMatch(WebElement::isDisplayed));
        if (saves().size() == count) confirm(true);
        ready.until(d -> saves().size() > count);
        settled();
    }

    public void awaitInvoice(Object id) {
        ready.until(d -> id.equals(document().get("id")));
        awaitTotals();
    }

    @Step("تعليق الفاتورة الحالية")
    public void hold() {
        button("تعليق");
    }

    @Step("اختيار الطاولة: {code}")
    public void selectTable(String code) {
        click(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//h6[normalize-space()='"
                                + code
                                + "']"));
    }
}
