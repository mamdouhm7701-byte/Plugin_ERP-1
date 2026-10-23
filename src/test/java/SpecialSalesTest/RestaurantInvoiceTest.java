package SpecialSalesTest;

import Plugin_ERP.System_Modules.Login.SpecialSales.*;

import io.qameta.allure.*;

import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("المبيعات")
@Feature("فاتورة المطعم")
public class RestaurantInvoiceTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    private SpecialSalesTestData.Record table;

    private RestaurantInvoicePage restaurant() {
        return (RestaurantInvoicePage) page;
    }

    @Step("إنشاء طلب بصنف وتعليقه على الطاولة")
    private SpecialSalesTestData.Record holdOrder() {
        table = data.table();
        restartPage();
        restaurant().type("داخلية");
        restaurant().selectTable(data.read(table).get("code").toString());
        add();
        page.waitTotal("100");
        page.notes(page.captureMarker());
        prepareSave();
        restaurant().hold();
        var invoice = saved();
        Assert.assertEquals(data.read(invoice).get("isOnHold"), true, "مطابقة حالة تعليق الفاتورة");
        number(data.balance(item, 1), "999");
        number(data.balance(item, 2), "1000");
        return invoice;
    }

    @Step("استرجاع الطلب المعلق وفتح أصنافه")
    private void reopen(SpecialSalesTestData.Record invoice) {
        restaurant().type("داخلية");
        restaurant().selectTable(data.read(table).get("code").toString());
        restaurant().awaitInvoice(invoice.id());
    }

    @Test
    @Story("إعدادات الشاشة عند الفتح")
    @Description("التحقق من قناة المطعم والعميل والمخزن المحددين.")
    public void ownedDefaultsAndChannel() {
        Assert.assertEquals(page.document().get("transactionChannel"), "Resturant", "مطابقة قناة البيع");
        Assert.assertEquals(page.document().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(page.document().get("storeId"), data.store(1).id(), "مطابقة المخزن");
    }

    @Test
    @Story("قائمة الطعام")
    @Description(
            "اختيار تصنيف وصنف مملوكين للاختبار والتحقق من الإجمالي قبل الحفظ وعدم خصم المخزون عند"
                    + " مجرد الإضافة.")
    public void catalogAddsItem() {
        add();
        Assert.assertEquals(lines().size(), 1, "مطابقة عدد بنود الفاتورة");
        number(page.document().get("total"), "100");
        number(data.balance(item, 1), "1000");
    }

    @Test
    @Story("الكمية")
    @Description("زيادة وإنقاص صنف المطعم وإعادة حساب قيمة الطلب.")
    public void quantityButtons() {
        add();
        page.changeQuantity(item.name(), true);
        page.waitTotal("200");
        page.changeQuantity(item.name(), false);
        page.waitTotal("100");
    }

    @Test
    @Story("حذف بند")
    @Description("إزالة الصنف قبل الحفظ وإعادة الإجمالي للصفر دون حركة مخزون.")
    public void removeCartLine() {
        add();
        page.removeLine(item.name());
        page.waitTotal("0");
        Assert.assertTrue(lines().isEmpty());
        number(data.balance(item, 1), "1000");
    }

    @Test
    @Story("نوع الطلب")
    @Description("تبديل الطلب إلى توصيل ثم خارجية والتحقق من احتفاظ الشاشة بالنوع المختار.")
    public void deliveryAndExternal() {
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.type("توصيل");
        Assert.assertEquals(page.document().get("restaurantInvoiceType"), "Delivery", "مطابقة نوع طلب المطعم");
        restaurant.type("خارجية");
        Assert.assertEquals(page.document().get("restaurantInvoiceType"), "External", "مطابقة نوع طلب المطعم");
    }

    @Test
    @Story("تعليق الطلب")
    @Description(
            "حفظ طلب داخلي معلق على الطاولة المحددة يخصم الكمية فور التعليق من المخزن المحدد فقط.")
    public void holdDeductsStockImmediately() {
        holdOrder();
    }

    @Test
    @Story("استرجاع المعلق")
    @Description("إعادة فتح طاولة معلقة يعيد نفس الفاتورة والأصناف والكميات دون خصم مخزون جديد.")
    public void reopeningHoldKeepsItems() {
        var invoice = holdOrder();
        reopen(invoice);
        Assert.assertEquals(lines().size(), 1, "مطابقة عدد بنود الفاتورة");
        page.waitTotal("100");
        number(data.balance(item, 1), "999");
    }

    @Test
    @Story("ملاحظات الطلب")
    @Description(
            "الاحتفاظ بملاحظات الطلب بعد تعليقه وإعادة فتح الطاولة؛ الاختبار يكشف فقدان الملاحظات"
                    + " في استرجاع الطلب.")
    public void reopeningHoldKeepsNotes() {
        var invoice = holdOrder();
        reopen(invoice);
        Assert.assertEquals(
                page.document().get("notes"),
                page.captureMarker(),
                "Held restaurant notes must survive reopening");
    }

    @Test
    @Story("الزيادة على المعلق")
    @Description(
            "إضافة وحدة إلى طلب معلق ثم تعليقه مجددًا تخصم الوحدة الجديدة فقط وتحفظ نفس الفاتورة"
                    + " بإجمالي وحدتين.")
    public void addingToHoldDeductsOnlyIncrement() {
        var invoice = holdOrder();
        reopen(invoice);
        add();
        page.waitTotal("200");
        page.notes(page.captureMarker());
        prepareSave();
        int savesBeforeUpdate = page.saves().size();
        restaurant().hold();
        page.awaitSaveCount(savesBeforeUpdate + 1);
        var updated = saved();
        Assert.assertEquals(updated.id(), invoice.id());
        number(data.read(updated).get("total"), "200");
        number(data.balance(item, 1), "998");
        number(data.balance(item, 2), "1000");
    }

    @Test
    @Story("دفع المعلق")
    @Description(
            "دفع طلب معلق بطريقة الدفع المحددة وإغلاقه يحفظ الدفع دون تكرار خصم المخزون المحجوز عند"
                    + " التعليق.")
    public void payingHoldDoesNotDeductAgain() {
        var payment = data.payment();
        var invoice = holdOrder();
        reopen(invoice);
        page.notes(page.captureMarker());
        restaurant().openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        restaurant().payAndClose();
        var paid = saved();
        Assert.assertEquals(paid.id(), invoice.id());
        var stored = data.read(paid);
        Assert.assertEquals(stored.get("isComplete"), true, "مطابقة حالة اكتمال الفاتورة");
        Assert.assertEquals(stored.get("isOnHold"), false, "مطابقة حالة تعليق الفاتورة");
        Assert.assertTrue(stored.get("payments").toString().contains(payment.id().toString()), "التحقق من دفعات الفاتورة");
        number(data.balance(item, 1), "999");
    }

    @Test
    @Story("دفع طلب خارجي")
    @Description(
            "حفظ طلب خارجي مدفوع بطريقة الدفع المحددة والتحقق من مرجع الدفع وخصم الصنف من المخزن.")
    public void savesPaidExternalOrder() {
        var payment = data.payment();
        restartPage();
        add();
        page.waitTotal("100");
        page.notes(page.captureMarker());
        restaurant().openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        restaurant().payAndClose();
        var invoice = saved();
        Assert.assertTrue(
                data.read(invoice).get("payments").toString().contains(payment.id().toString()), "التحقق من دفعات الفاتورة");
        number(data.balance(item, 1), "999");
    }

    @Test
    @Story("الكمية الكسرية")
    @Description(
            "تغيير كمية صنف المطعم إلى وحدة ونصف وإعادة حساب الإجمالي دون حفظ أو تغيير المخزون.")
    public void fractionalQuantity() {
        add();
        page.quantity(item.name(), "1.5");
        page.waitTotal("150");
        number(data.balance(item, 1), "1000");
    }

    @Test
    @Story("إلغاء فاتورة مدفوعة")
    @Description(
            "حذف فاتورة مطعم مدفوعة من صفها المحدد بالمعرف بعد تأكيد المستخدم يعيد المخزون ويزيل"
                    + " الفاتورة وبيانات دفعها.")
    public void deletingPaidOrderRestoresStock() {
        var payment = data.payment();
        var invoice = holdOrder();
        reopen(invoice);
        page.notes(page.captureMarker());
        restaurant().openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        restaurant().payAndClose();
        var paid = saved();
        var stored = data.read(paid);
        Assert.assertFalse(((java.util.List<?>) stored.get("payments")).isEmpty(), "التحقق من دفعات الفاتورة");
        restaurant().invoices();
        page.invoiceAction(paid.id(), stored.get("code").toString(), "trash");
        page.confirm(true);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(45))
                .until(
                        d ->
                                data.read(paid) == null
                                        && new java.math.BigDecimal(
                                                                data.balance(item, 1).toString())
                                                        .compareTo(new java.math.BigDecimal("1000"))
                                                == 0);
        number(data.balance(item, 2), "1000");
        customerBalance("0");
    }

    @Test
    @Story("طباعة إيصال المطعم")
    @Description(
            "طباعة فاتورة مطعم مدفوعة من القائمة والتحقق من محتوى الإيصال والطباعة الأصلية للمتصفح"
                    + " مع ثبات المخزون وعدم حفظ فاتورة أخرى.")
    public void printsPaidReceiptWithoutPostingAgain() {
        var payment = data.payment();
        var invoice = holdOrder();
        reopen(invoice);
        page.notes(page.captureMarker());
        restaurant().openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        restaurant().payAndClose();
        var paid = saved();
        var stored = data.read(paid);
        restaurant().invoices();
        int saves = page.saves().size();
        SpecialSalesPrintAssertions.assertReceipt(
                driver,
                () -> page.invoiceAction(paid.id(), stored.get("code").toString(), "printer"),
                item.name(),
                stored.get("code").toString());
        Assert.assertEquals(page.saves().size(), saves);
        number(data.balance(item, 1), "999");
        Assert.assertNotNull(data.read(paid));
    }

    @Test
    @Story("حفظ وطباعة المطعم")
    @Description(
            "حفظ وطباعة طلب مطعم خارجي جديد بطريقة الدفع المحددة والتحقق من الإيصال الفعلي وحفظ فاتورة"
                    + " واحدة فقط وخصم وحدة واحدة من المخزون.")
    public void savesAndPrintsOneInvoice() {
        var payment = data.payment();
        restartPage();
        add();
        restaurant().openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        var invoice =
                new java.util.concurrent.atomic.AtomicReference<SpecialSalesTestData.Record>();
        SpecialSalesPrintAssertions.assertReceipt(
                driver,
                () -> {
                    page.saveAndPrint();
                    invoice.set(saved());
                },
                item.name(),
                () -> data.read(invoice.get()).get("code").toString());
        Assert.assertEquals(page.saves().size(), 1);
        Assert.assertTrue(
                data.read(invoice.get())
                        .get("payments")
                        .toString()
                        .contains(payment.id().toString()), "التحقق من دفعات الفاتورة");
        number(data.balance(item, 1), "999");
    }
}
