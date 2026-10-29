/* شرح الملف: خطوات مراجعة فاتورة المطعم أو نقاط البيع: صنف معروف وأرقام مستقلة عند الحفظ والتعديل والحذف والطباعة، مع الإضافية وحدها الأدنى للمطعم. */
package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import TestSupport.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public abstract class SpecialInvoiceTaxReview extends SpecialSalesScenario {
    @Override
    protected boolean useDefaultDraft() {
        return false;
    }

    @DataProvider
    public Object[][] actions() {
        return InvoiceReviewRecipe.actions(
                BranchProfiles.selected().orElseThrow().expected().taxPolicy(),
                java.util.List.of("إضافة", "تعديل", "حذف", "طباعة"),
                true);
    }

    @Test(dataProvider = "actions")
    @io.qameta.allure.Description(
            "نضيف صنفًا بسعر100 أو10 قبل الضريبة، أو115 أو11.5 شاملها. نراجع قبل الضريبة والضريبة والصافي، ثم نراجع العملية المختارة. المطعم يطبق الإضافية100% بحد أدنى25 للوحدة عند تفعيلها. الإعفاء في سياسة الأصناف فقط.")
    public void reviewSpecialInvoice(String action, boolean requestExemption, boolean lowPrice) {
        var policy = BranchProfiles.selected().orElseThrow().expected().taxPolicy();
        boolean restaurant = page instanceof RestaurantInvoicePage;
        boolean additional =
                restaurant && Boolean.parseBoolean(System.getProperty("business.additionalTax"));
        boolean exempt = InvoiceReviewRecipe.exempt(policy, requestExemption);
        String price =
                policy == TaxPolicy.PRICE_INCLUDED
                        ? (lowPrice ? "11.5" : "115")
                        : (lowPrice ? "10" : "100");
        var values = PolicyItemRecipe.create(data.itemValues(), price, exempt, additional);
        item = data.fixture("review-tax-item", "Items", values);
        Assert.assertEquals(data.read(item).get("isAdditionalTaxable"), additional);
        var firstPayment = data.payment();
        // A second payment is needed only when increasing a saved invoice's quantity.
        var extraPayment =
                action.equals("تعديل")
                        ? data.fixture(
                                "review-extra-payment",
                                "PaymentMethods",
                                SpecialSalesTestData.fields("bankId", data.bank().id()))
                        : null;
        restartPage();
        verifyTaxSetup(exempt, additional);
        add();
        var expected = expected(policy, price, exempt, additional, 1);
        InvoiceReviewRecipe.explain(
                restaurant ? "فاتورة المطعم" : "فاتورة نقاط البيع",
                action,
                policy,
                exempt,
                item.name(),
                expected.beforeVat(),
                expected.vat(),
                expected.net());
        page.waitNet(expected.net().toPlainString());
        amounts(page.document(), expected);
        pay(firstPayment, expected.net());
        prepareSave();
        savePayment();
        var invoice = saved();
        amounts(data.read(invoice), expected);
        number(data.balance(item, 1), "999");
        if (action.equals("تعديل")) {
            invoices();
            page.invoiceAction(invoice.id(), data.read(invoice).get("code").toString(), "edit");
            page.awaitInvoice(invoice.id());
            page.quantity(item.name(), "3");
            var updatedExpected = expected(policy, price, exempt, additional, 3);
            page.waitNet(updatedExpected.net().toPlainString());
            amounts(page.document(), updatedExpected);
            BigDecimal paid =
                    ((List<?>) page.document().get("payments"))
                            .stream()
                                    .map(
                                            row ->
                                                    new BigDecimal(
                                                            ((Map<?, ?>) row)
                                                                    .get("value")
                                                                    .toString()))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            page.notes(page.captureMarker());
            pay(extraPayment, updatedExpected.net().subtract(paid));
            int count = page.saves().size();
            prepareSave();
            savePayment();
            page.awaitSaveCount(count + 1);
            var updated = saved();
            Assert.assertEquals(updated.id(), invoice.id());
            amounts(data.read(updated), updatedExpected);
            number(data.balance(item, 1), "997");
        } else if (action.equals("حذف")) {
            invoices();
            page.invoiceAction(invoice.id(), data.read(invoice).get("code").toString(), "trash");
            page.confirm(true);
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(45))
                    .until(
                            d ->
                                    data.read(invoice) == null
                                            && new BigDecimal(data.balance(item, 1).toString())
                                                            .compareTo(new BigDecimal("1000"))
                                                    == 0);
            customerBalance("0");
        } else if (action.equals("طباعة")) {
            invoices();
            int saves = page.saves().size();
            SpecialSalesPrintAssertions.assertReceipt(
                    driver,
                    () ->
                            page.invoiceAction(
                                    invoice.id(),
                                    data.read(invoice).get("code").toString(),
                                    "printer"),
                    item.name(),
                    data.read(invoice).get("code").toString());
            String text =
                    String.valueOf(
                            ((org.openqa.selenium.JavascriptExecutor) driver)
                                    .executeScript(
                                            "return window.__specialSalesPrint.printed.at(-1).text"));
            Assert.assertTrue(
                    containsAmount(text, expected.net()),
                    "الإيصال يجب أن يعرض الصافي المتوقع: " + expected.net());
            Assert.assertEquals(page.saves().size(), saves);
            amounts(data.read(invoice), expected);
            number(data.balance(item, 1), "999");
        }
        number(data.balance(item, 2), "1000");
    }

    @io.qameta.allure.Step("التأكد من إعداد ضريبة الصنف والعميل المختار قبل مراجعة أرقام الفاتورة")
    private void verifyTaxSetup(boolean exempt, boolean additional) {
        var persistedItem = data.read(item);
        var persistedCustomer = data.read(customer);
        Object selectedCustomer = page.document().get("dealerId");
        var evidence =
                Map.of(
                        "itemName", item.name(),
                        "itemIsTaxable", persistedItem.get("isTaxable"),
                        "itemTax", persistedItem.get("itemTax"),
                        "itemAdditional", persistedItem.get("isAdditionalTaxable"),
                        "customerIsTaxable", persistedCustomer.get("isTaxable"),
                        "expectedCustomerId", customer.id(),
                        "selectedCustomerId",
                                java.util.Objects.toString(selectedCustomer, "لم يتم اختيار عميل"));
        io.qameta.allure.Allure.addAttachment(
                "إعداد الصنف والعميل الفعلي",
                "application/json",
                new org.json.JSONObject(evidence).toString(2),
                ".json");
        Assert.assertEquals(
                persistedItem.get("isTaxable"),
                !exempt,
                "إعفاء الصنف المحفوظ يطابق الحالة المطلوبة");
        Assert.assertEquals(
                persistedItem.get("isAdditionalTaxable"),
                additional,
                "خضوع الصنف للإضافية يطابق الحالة المطلوبة");
        InvoiceReviewRecipe.amount(
                persistedItem.get("itemTax"),
                new BigDecimal(additional || !exempt ? "15" : "0"),
                "نسبة ضريبة الصنف المحفوظة");
        Assert.assertEquals(
                persistedCustomer.get("isTaxable"), Boolean.TRUE, "عميل التجربة خاضع للضريبة");
        Assert.assertEquals(
                selectedCustomer, customer.id(), "الفاتورة تستخدم نفس العميل الضريبي الذي جهزناه");
    }

    static boolean containsAmount(String text, BigDecimal amount) {
        var numbers =
                java.util.regex.Pattern.compile("(?<![\\d.])\\d+(?:[,.]\\d+)*(?![\\d.])")
                        .matcher(text);
        while (numbers.find())
            try {
                if (new BigDecimal(numbers.group().replace(",", "")).compareTo(amount) == 0)
                    return true;
            } catch (NumberFormatException malformed) {
                Plugin_ERP.Bots.LogUtils.debug("Receipt token is not a monetary amount");
            }
        return false;
    }

    private RestaurantAdditionalTaxCalculator.Totals expected(
            TaxPolicy policy, String price, boolean exempt, boolean additional, int quantity) {
        return RestaurantAdditionalTaxCalculator.calculate(
                List.of(
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.valueOf(quantity),
                                new BigDecimal(price),
                                additional,
                                BigDecimal.ZERO,
                                exempt)),
                policy,
                additional,
                new BigDecimal("100"),
                2);
    }

    private void amounts(
            Map<String, Object> actual, RestaurantAdditionalTaxCalculator.Totals expected) {
        InvoiceReviewRecipe.amount(
                actual.get("netBeforeTaxes"), expected.beforeVat(), "قبل الضريبة");
        number(actual.get("totalAdditionalTaxes"), expected.additionalTax().toPlainString());
        InvoiceReviewRecipe.amount(actual.get("totalTaxes"), expected.vat(), "الضريبة");
        InvoiceReviewRecipe.amount(actual.get("net"), expected.net(), "الصافي");
    }

    private void pay(SpecialSalesTestData.Record payment, BigDecimal amount) {
        if (page instanceof RestaurantInvoicePage restaurant) restaurant.openPayment();
        else ((PointOfSalePage) page).openPayment();
        page.addOtherPayment(payment.name(), amount.toPlainString());
    }

    private void savePayment() {
        if (page instanceof RestaurantInvoicePage restaurant) restaurant.payAndClose();
        else page.save();
    }

    private void invoices() {
        if (page instanceof RestaurantInvoicePage restaurant) restaurant.invoices();
        else ((PointOfSalePage) page).invoices();
    }
}
