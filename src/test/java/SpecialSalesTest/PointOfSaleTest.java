package SpecialSalesTest;

import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("المبيعات")
@Feature("فاتورة نقاط البيع")
public class PointOfSaleTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage() {
        return new PointOfSalePage(driver);
    }

    @Step("إنشاء فاتورة وإضافة الصنف ثم سدادها وحفظها")
    private SpecialSalesTestData.Record paidInvoice() {
        var payment = data.payment();
        restartPage();
        add();
        page.waitTotal("100");
        ((PointOfSalePage) page).openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        page.save();
        return saved();
    }

    private void deleteInvoice(SpecialSalesTestData.Record invoice, boolean confirm) {
        var stored = data.read(invoice);
        ((PointOfSalePage) page).invoices();
        page.invoiceAction(invoice.id(), stored.get("code").toString(), "trash");
        page.confirm(confirm);
    }

    @Test
    @Story("إعدادات الشاشة عند الفتح")
    @Description(
            "التحقق من قناة نقاط البيع والمخزن والعميل المحددين دون الاعتماد على تننت" + " معينة.")
    public void ownedDefaultsAndChannel() {
        Assert.assertEquals(
                page.document()
                        .get("transactionChannel")
                        .toString()
                        .toLowerCase(java.util.Locale.ROOT),
                "pos",
                "مطابقة قناة البيع");
        Assert.assertEquals(
                page.document().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(page.document().get("storeId"), data.store(1).id(), "مطابقة المخزن");
    }

    @Test
    @Story("إضافة الباركود")
    @Description("إضافة صنف بالباركود والتحقق من الكمية والإجمالي دون حفظ أو تغيير المخزون.")
    public void barcodeAddsItem() {
        add();
        Assert.assertEquals(lines().size(), 1, "مطابقة عدد بنود الفاتورة");
        number(page.document().get("total"), "100");
        number(data.balance(item, 1), "1000");
    }

    @Test
    @Story("الكمية")
    @Description("زيادة كمية صنف بزر الزيادة ثم إنقاصها والتحقق من إعادة حساب الإجمالي.")
    public void quantityButtons() {
        add();
        page.changeQuantity(item.name(), true);
        page.waitTotal("200");
        page.changeQuantity(item.name(), false);
        page.waitTotal("100");
    }

    @Test
    @Story("الكمية")
    @Description("إدخال كمية كسرية والتحقق من ضرب الكمية في سعر الوحدة.")
    public void fractionalQuantity() {
        add();
        page.quantity(item.name(), "1.5");
        page.waitTotal("150");
    }

    @Test
    @Story("حذف بند")
    @Description("حذف بند من السلة غير المحفوظة وإعادة الإجماليات للصفر دون خصم المخزون.")
    public void removeCartLine() {
        add();
        page.removeLine(item.name());
        page.waitTotal("0");
        Assert.assertTrue(lines().isEmpty());
        number(data.balance(item, 1), "1000");
    }

    @Test
    @Story("تعدد الأصناف")
    @Description("إضافة صنفين مختلفين والتحقق من احتفاظ السلة بكليهما وجمع القيم.")
    public void multipleItems() {
        var second = data.item(2);
        restartPage();
        add();
        page.addItem(
                second.name(),
                String.valueOf(second.values().get("mainUnitBarcode")),
                data.category().name());
        page.waitTotal("200");
        Assert.assertEquals(lines().size(), 2, "مطابقة عدد بنود الفاتورة");
    }

    @Test
    @Story("نوع السداد")
    @Description("التبديل بين النقدي والآجل والتحقق من قيمة نوع السداد في نموذج نقاط البيع.")
    public void paymentTypeSwitch() {
        var pos = (PointOfSalePage) page;
        pos.credit();
        Assert.assertEquals(page.document().get("paymentType"), "Credit", "مطابقة نوع السداد");
        pos.cash();
        Assert.assertEquals(page.document().get("paymentType"), "Cash", "مطابقة نوع السداد");
    }

    @Test
    @Story("حفظ آجل")
    @Description(
            "حفظ فاتورة نقاط بيع آجلة بالبيانات المدخلة والتحقق من قراءة الفاتورة وخصم كمية الصنف من"
                    + " المخزن وزيادة مديونية العميل المسجل فور الحفظ بصافي الفاتورة طبقًا لقاعدة العمل"
                    + " المؤكدة.")
    public void savesCreditAndDeductsStock() {
        customer = data.registeredCustomer();
        Assert.assertEquals(
                data.read(customer).get("isTaxable"), Boolean.TRUE, "مطابقة تطبيق الضريبة");
        Assert.assertEquals(
                data.read(customer).get("isOnTheFly"), Boolean.FALSE, "مطابقة نوع العميل");
        restartPage();
        add();
        var pos = (PointOfSalePage) page;
        pos.credit();
        pos.choose("العميل", customer.name());
        pos.showInvoiceDetails();
        page.notes(page.captureMarker());
        prepareSave();
        page.save();
        var invoice = saved();
        var stored = data.read(invoice);
        Assert.assertEquals(stored.get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
        Allure.addAttachment(
                "فاتورة نقاط البيع الآجلة المحفوظة",
                "application/json",
                new org.json.JSONObject(stored).toString(2),
                ".json");
        Assert.assertEquals(
                stored.get("paymentType"),
                "Credit",
                "The persisted POS invoice must remain a credit sale");
        number(stored.get("discountPercentage"), "0");
        Assert.assertTrue(
                new java.math.BigDecimal(stored.get("net").toString()).signum() > 0,
                "Credit fixture must have an unpaid positive amount");
        Assert.assertEquals(
                stored.get("transactionChannel").toString().toLowerCase(java.util.Locale.ROOT),
                "pos",
                "مطابقة قناة البيع");
        number(data.balance(item, 1), "999");
        customerBalance(stored.get("net").toString());
    }

    @Test
    @Story("السداد النقدي")
    @Description("حفظ فاتورة نقدية بطريقة الدفع المحددة والتحقق من حفظ مرجع الدفع وخصم المخزون.")
    public void savesCashWithOwnedPayment() {
        var payment = data.payment();
        restartPage();
        add();
        ((PointOfSalePage) page).showInvoiceDetails();
        page.notes(page.captureMarker());
        ((PointOfSalePage) page).openPayment();
        page.addOtherPayment(payment.name(), page.document().get("net").toString());
        prepareSave();
        page.save();
        var invoice = saved();
        var stored = data.read(invoice);
        Assert.assertTrue(
                stored.get("payments").toString().contains(payment.id().toString()),
                "التحقق من دفعات الفاتورة");
        Assert.assertEquals(
                stored.get("storeId"), data.store(1).id(), "مطابقة مخزن الفاتورة المحفوظة");
        var storedLines = (java.util.List<?>) stored.get("lines");
        Assert.assertEquals(storedLines.size(), 1, "مطابقة عدد البنود المحفوظة");
        var line = (java.util.Map<?, ?>) storedLines.getFirst();
        Assert.assertEquals(line.get("itemId"), item.id(), "مطابقة الصنف المحفوظ");
        Assert.assertEquals(line.get("unitId"), data.unit(1).id(), "مطابقة الوحدة المحفوظة");
        number(line.get("quantity"), "1");
        StockBalanceWait.await(
                () -> data.balance(item, 1), "999", java.time.Duration.ofSeconds(30));
        customerBalance("0");
    }

    @Test
    @Story("تعليق السلة")
    @Description(
            "تعليق سلة نقاط البيع محليًا يفرغ السلة دون حفظ فاتورة أو خصم مخزون؛ يختلف ذلك عن تعليق"
                    + " طلب المطعم.")
    public void localHoldDoesNotDeductStock() {
        add();
        ((PointOfSalePage) page).hold();
        page.waitTotal("0");
        Assert.assertTrue(lines().isEmpty());
        Assert.assertTrue(page.attempts().isEmpty());
        number(data.balance(item, 1), "1000");
    }

    @Test
    @Story("تعديل نقاط البيع")
    @Description(
            "تعديل كمية فاتورة نقاط البيع المحفوظة إلى ثلاث وحدات والتحقق من حفظ نفس المعرف وخصم"
                    + " فرق الكمية فقط.")
    public void editingSavedQuantityAppliesOnlyDifference() {
        var extraPayment =
                data.fixture(
                        "extra-payment",
                        "PaymentMethods",
                        SpecialSalesTestData.fields("bankId", data.bank().id()));
        var invoice = paidInvoice();
        var stored = data.read(invoice);
        ((PointOfSalePage) page).invoices();
        page.invoiceAction(invoice.id(), stored.get("code").toString(), "edit");
        page.awaitInvoice(invoice.id());
        page.quantity(item.name(), "3");
        page.waitTotal("300");
        page.notes(page.captureMarker());
        var alreadyPaid =
                ((java.util.List<?>) page.document().get("payments"))
                        .stream()
                                .map(
                                        row ->
                                                new java.math.BigDecimal(
                                                        ((java.util.Map<?, ?>) row)
                                                                .get("value")
                                                                .toString()))
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        var remaining =
                new java.math.BigDecimal(page.document().get("net").toString())
                        .subtract(alreadyPaid);
        ((PointOfSalePage) page).openPayment();
        page.addOtherPayment(extraPayment.name(), remaining.toPlainString());
        int count = page.saves().size();
        prepareSave();
        page.save();
        page.awaitSaveCount(count + 1);
        var updated = saved();
        Assert.assertEquals(updated.id(), invoice.id());
        number(data.read(updated).get("total"), "300");
        number(data.balance(item, 1), "997");
        number(data.balance(item, 2), "1000");
        customerBalance("0");
    }

    @Test
    @Story("حذف نقاط البيع")
    @Description("حذف فاتورة نقاط بيع مملوكة من الواجهة يعيد كمية المخزون ويزيل الفاتورة نهائيًا.")
    public void deletingSavedInvoiceRestoresStock() {
        var invoice = paidInvoice();
        deleteInvoice(invoice, true);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(45))
                .until(
                        d ->
                                data.read(invoice) == null
                                        && new java.math.BigDecimal(
                                                                data.balance(item, 1).toString())
                                                        .compareTo(new java.math.BigDecimal("1000"))
                                                == 0);
        number(data.balance(item, 2), "1000");
        customerBalance("0");
    }

    @Test
    @Story("تأكيد الحذف")
    @Description(
            "رفض تأكيد حذف فاتورة نقاط بيع يبقي الفاتورة والمخزون كما هما حتى تنظيف مجموعة الصفحة.")
    public void cancellingDeleteKeepsInvoice() {
        var invoice = paidInvoice();
        deleteInvoice(invoice, false);
        Assert.assertNotNull(data.read(invoice));
        number(data.balance(item, 1), "999");
    }

    @Test
    @Story("طباعة الإيصال")
    @Description(
            "طباعة فاتورة نقاط بيع مدفوعة من صفها المحدد بالمعرف، والتحقق من استدعاء طباعة المتصفح"
                    + " ومحتوى الإيصال دون إنشاء فاتورة أو حركة مخزون إضافية.")
    public void printsSavedReceiptWithoutPostingAgain() {
        var invoice = paidInvoice();
        var stored = data.read(invoice);
        ((PointOfSalePage) page).invoices();
        int saves = page.saves().size();
        SpecialSalesPrintAssertions.assertReceipt(
                driver,
                () -> page.invoiceAction(invoice.id(), stored.get("code").toString(), "printer"),
                item.name(),
                stored.get("code").toString());
        Assert.assertEquals(page.saves().size(), saves);
        number(data.balance(item, 1), "999");
        Assert.assertNotNull(data.read(invoice));
    }

    @Test
    @Story("حفظ وطباعة نقاط البيع")
    @Description(
            "حفظ وطباعة فاتورة نقاط بيع نقدية جديدة والتحقق من الإيصال الفعلي وحفظ فاتورة واحدة فقط"
                    + " وخصم المخزون مرة واحدة.")
    public void savesAndPrintsOneInvoice() {
        var payment = data.payment();
        restartPage();
        add();
        ((PointOfSalePage) page).openPayment();
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
                        .contains(payment.id().toString()),
                "التحقق من دفعات الفاتورة");
        number(data.balance(item, 1), "999");
    }
}
