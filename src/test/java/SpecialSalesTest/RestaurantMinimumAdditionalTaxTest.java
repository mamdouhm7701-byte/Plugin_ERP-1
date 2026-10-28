/* شرح الملف: اختبارات الحد الأدنى للإضافية25 لكل وحدة: الحد الأدنى للإضافية25 لكل وحدة.
 */
package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import Plugin_ERP.Util.VueComponentAccess;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class RestaurantMinimumAdditionalTaxTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @DataProvider
    public Object[][] quantities() {
        return new Object[][] {{1}, {3}};
    }

    @Test(dataProvider = "quantities")
    public void savesMinimumAdditionalTaxPerUnitBeforeQuantity(int quantity) {
        var branch = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        Assert.assertEquals(branch.activityType(), BranchPolicies.ActivityType.RESTAURANT);
        var configuration =
                (Map<?, ?>)
                        VueComponentAccess.execute(
                                driver,
                                "const b=erpVue.root().$store.getters['app/currentBranch'];return {enabled:b.enableAdditionalTax,rate:b.additionalTaxPercentage}");
        Assert.assertEquals(configuration.get("enabled"), true);
        number(configuration.get("rate"), "100");
        String price = branch.taxPolicy() == TaxPolicy.PRICE_INCLUDED ? "11.5" : "10";
        var values = TestSupport.PolicyItemRecipe.create(data.itemValues(), price, false);
        values.put("isAdditionalTaxable", true);
        var subject = data.fixture("minimum-additional-item", "Items", values);
        var storedItem = data.read(subject);
        Assert.assertEquals(storedItem.get("isAdditionalTaxable"), true);
        number(storedItem.get("itemTax"), "15");
        var payment = data.payment();
        restartPage();
        page.addItem(
                subject.name(),
                subject.values().get("mainUnitBarcode").toString(),
                data.category().name());
        for (int count = 1; count < quantity; count++) page.changeQuantity(subject.name(), true);
        var expected =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(
                                new RestaurantAdditionalTaxCalculator.Line(
                                        BigDecimal.valueOf(quantity), new BigDecimal(price), true)),
                        branch.taxPolicy(),
                        true,
                        new BigDecimal("100"),
                        2);
        Allure.addAttachment(
                "Minimum additional tax independent recipe",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "quantity",
                                        quantity,
                                        "unitPrice",
                                        price,
                                        "policy",
                                        branch.taxPolicy().code(),
                                        "expectedAdditional",
                                        expected.additionalTax(),
                                        "expectedVat",
                                        expected.vat(),
                                        "expectedNet",
                                        expected.net(),
                                        "actual",
                                        page.document()))
                        .toString(2),
                ".json");
        page.waitNet(expected.net().toPlainString());
        assertAmounts(page.document(), expected);
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.openPayment();
        page.addOtherPayment(payment.name(), expected.net().toPlainString());
        prepareSave();
        restaurant.payAndClose();
        var invoice = saved();
        var stored = data.read(invoice);
        assertAmounts(stored, expected);
        var lines = (List<?>) stored.get("lines");
        Assert.assertEquals(lines.size(), 1);
        var line = (Map<?, ?>) lines.getFirst();
        Assert.assertEquals(line.get("itemId"), subject.id());
        number(line.get("quantity"), Integer.toString(quantity));
        number(data.balance(subject, 1), Integer.toString(1000 - quantity));
        Allure.addAttachment(
                "Saved minimum additional tax invoice",
                "application/json",
                new org.json.JSONObject(stored).toString(2),
                ".json");
    }

    private void assertAmounts(
            Map<String, Object> document, RestaurantAdditionalTaxCalculator.Totals expected) {
        number(document.get("totalAdditionalTaxes"), expected.additionalTax().toPlainString());
        number(document.get("netBeforeTaxes"), expected.beforeVat().toPlainString());
        number(document.get("totalTaxes"), expected.vat().toPlainString());
        number(document.get("net"), expected.net().toPlainString());
    }
}
