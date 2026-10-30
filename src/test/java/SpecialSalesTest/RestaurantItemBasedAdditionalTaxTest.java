/* شرح الملف: يتأكد أن الصنف المعفى يظل بلا VAT عند إضافة ضريبة المطعم الإضافية، وأن غير الخاضع للإضافية يظل بلا إضافية. */
package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import Plugin_ERP.Util.VueComponentAccess;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class RestaurantItemBasedAdditionalTaxTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @Test
    @Story("الصنف المعفى يظل بلا ضريبة قيمة مضافة مع الإضافية وبدونها")
    public void exemptItemsRemainVatExemptWithAdditionalTax() {
        var branch = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        Assert.assertEquals(branch.activityType(), ActivityType.RESTAURANT);
        Assert.assertEquals(branch.taxPolicy(), TaxPolicy.ITEM_BASED);
        var configuration =
                (Map<?, ?>)
                        VueComponentAccess.execute(
                                driver,
                                "const b=erpVue.root().$store.getters['app/currentBranch'];return {enabled:b.enableAdditionalTax,rate:b.additionalTaxPercentage}");
        Assert.assertEquals(configuration.get("enabled"), true);
        number(configuration.get("rate"), "100");
        var subject = exemptItem("exempt-additional-subject", true);
        var exempt = exemptItem("exempt-without-additional", false);
        var payment = data.payment();
        var recipes =
                List.of(
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE, new BigDecimal("100"), true, BigDecimal.ZERO, true),
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                false,
                                BigDecimal.ZERO,
                                true));
        var expected =
                RestaurantAdditionalTaxCalculator.calculate(
                        recipes, TaxPolicy.ITEM_BASED, true, new BigDecimal("100"), 2);
        restartPage();
        for (var record : List.of(subject, exempt))
            page.addItem(
                    record.name(),
                    record.values().get("mainUnitBarcode").toString(),
                    data.category().name());
        Allure.addAttachment(
                "ItemBased exemption and additional tax recipe",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "additional",
                                        expected.additionalTax(),
                                        "beforeVat",
                                        expected.beforeVat(),
                                        "vat",
                                        expected.vat(),
                                        "net",
                                        expected.net(),
                                        "actual",
                                        page.document()))
                        .toString(2),
                ".json");
        page.waitNet(expected.net().toPlainString());
        verifyAmounts(page.document(), expected);
        number(data.balance(subject, 1), "1000");
        number(data.balance(exempt, 1), "1000");
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.openPayment();
        page.addOtherPayment(payment.name(), expected.net().toPlainString());
        prepareSave();
        restaurant.payAndClose();
        var invoice = saved();
        var stored = data.read(invoice);
        verifyAmounts(stored, expected);
        number(data.balance(subject, 1), "999");
        number(data.balance(exempt, 1), "999");
        Allure.addAttachment(
                "Saved ItemBased additional tax",
                "application/json",
                new org.json.JSONObject(stored).toString(2),
                ".json");
        new BranchProfile("itembased-additional", branch)
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
    }

    private SpecialSalesTestData.Record exemptItem(String key, boolean additional) {
        var values = new LinkedHashMap<>(data.itemValues());
        values.put("salesPrice", 100);
        values.put("isTaxable", false);
        values.put("itemTax", 15);
        values.put("isAdditionalTaxable", additional);
        var record = data.fixture(key, "Items", values);
        var stored = data.read(record);
        Assert.assertEquals(stored.get("isTaxable"), false, "Persisted exemption recipe");
        number(stored.get("itemTax"), "15");
        Assert.assertEquals(
                stored.get("isAdditionalTaxable"),
                additional,
                "Persisted additional-tax eligibility");
        return record;
    }

    private void verifyAmounts(
            Map<String, Object> document, RestaurantAdditionalTaxCalculator.Totals expected) {
        TestSupport.DecimalAmountAssertions.amount(
                document.get("totalAdditionalTaxes"), expected.additionalTax().toPlainString());
        TestSupport.DecimalAmountAssertions.amount(
                document.get("netBeforeTaxes"), expected.beforeVat().toPlainString());
        TestSupport.DecimalAmountAssertions.amount(
                document.get("totalTaxes"), expected.vat().toPlainString());
        TestSupport.DecimalAmountAssertions.amount(
                document.get("net"), expected.net().toPlainString());
        Assert.assertEquals(((List<?>) document.get("lines")).size(), 2);
    }
}
