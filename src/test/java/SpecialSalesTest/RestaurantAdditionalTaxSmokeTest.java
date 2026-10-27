package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import Plugin_ERP.Util.VueComponentAccess;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Profile-specific smoke; never enables tax or changes policies on a branch with transactions. */
public final class RestaurantAdditionalTaxSmokeTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @Test
    @Story("الصنف الخاضع للإضافية 100% يدخل الإضافية في وعاء ضريبة 15%")
    public void subjectItemAdditionalTaxMatchesIndependentOracle() {
        check(false);
    }

    @Test
    @Story("الإضافية تصيب الصنف الخاضع فقط في طلب مختلط")
    public void mixedItemsExcludeNonSubjectItemFromAdditionalTax() {
        check(true);
    }

    @Test
    @Story("الإضافية تحتسب بعد خصم عشرة بالمائة")
    public void discountPrecedesAdditionalTax() {
        check(false, true);
    }

    private void check(boolean mixed) {
        check(mixed, false);
    }

    private void check(boolean mixed, boolean discounted) {
        var branch = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        Assert.assertEquals(branch.activityType(), BranchPolicies.ActivityType.RESTAURANT);
        Object additional =
                VueComponentAccess.execute(
                        driver,
                        "const b=erpVue.root().$store.getters['app/currentBranch'];return {enabled:b.enableAdditionalTax,rate:b.additionalTaxPercentage}");
        var configuration = (Map<?, ?>) additional;
        Assert.assertEquals(
                configuration.get("enabled"),
                true,
                "Additional tax must already be enabled in the selected profile");
        number(configuration.get("rate"), "100");
        Assert.assertTrue(
                branch.taxPolicy() == TaxPolicy.NET_BASED
                        || branch.taxPolicy() == TaxPolicy.PRICE_INCLUDED,
                "ItemBased additional tax interaction is not confirmed");
        String price = branch.taxPolicy() == TaxPolicy.PRICE_INCLUDED ? "115" : "100";
        SpecialSalesTestData.Record discountType = null;
        if (discounted) {
            Object userId =
                    VueComponentAccess.execute(
                            driver, "return erpVue.root()?.$store?.state?.auth?.profile?.userId");
            Assert.assertNotNull(
                    userId, "Authenticated user identity required for an owned discount type");
            discountType =
                    data.fixture(
                            "additional-ten-percent",
                            "DiscountTypes",
                            SpecialSalesTestData.fields(
                                    "percentage",
                                    10,
                                    "secretCode",
                                    java.util.UUID.randomUUID().toString(),
                                    "isActive",
                                    true,
                                    "users",
                                    List.of(SpecialSalesTestData.fields("userId", userId))));
            number(data.read(discountType).get("percentage"), "10");
        }
        var subject = taxItem("additional-subject", price, true);
        var other = mixed ? taxItem("additional-nonsubject", price, false) : null;
        var payment = mixed || discounted ? null : data.payment();
        restartPage();
        page.addItem(
                subject.name(),
                subject.values().get("mainUnitBarcode").toString(),
                data.category().name());
        if (other != null)
            page.addItem(
                    other.name(),
                    other.values().get("mainUnitBarcode").toString(),
                    data.category().name());
        if (discounted) {
            ((RestaurantInvoicePage) page).openPayment();
            ((RestaurantInvoicePage) page).chooseMovementDiscountType(discountType.name());
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(
                            d ->
                                    new BigDecimal(
                                                            page.document()
                                                                    .get("discountPercentage")
                                                                    .toString())
                                                    .compareTo(new BigDecimal("10"))
                                            == 0);
        }
        var recipes = new ArrayList<RestaurantAdditionalTaxCalculator.Line>();
        recipes.add(
                new RestaurantAdditionalTaxCalculator.Line(
                        BigDecimal.ONE,
                        new BigDecimal(price),
                        true,
                        discounted ? new BigDecimal(price).movePointLeft(1) : BigDecimal.ZERO));
        if (mixed)
            recipes.add(
                    new RestaurantAdditionalTaxCalculator.Line(
                            BigDecimal.ONE, new BigDecimal(price), false));
        var expected =
                RestaurantAdditionalTaxCalculator.calculate(
                        recipes, branch.taxPolicy(), true, new BigDecimal("100"), 2);
        Allure.addAttachment(
                "Restaurant additional tax independent recipe",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "policy",
                                        branch.taxPolicy().code(),
                                        "price",
                                        price,
                                        "additional",
                                        expected.additionalTax(),
                                        "beforeVat",
                                        expected.beforeVat(),
                                        "vat",
                                        expected.vat(),
                                        "net",
                                        expected.net(),
                                        "document",
                                        page.document()))
                        .toString(2),
                ".json");
        page.waitNet(expected.net().toPlainString());
        number(page.document().get("net"), expected.net().toPlainString());
        number(
                page.document().get("totalAdditionalTaxes"),
                expected.additionalTax().toPlainString());
        number(page.document().get("netBeforeTaxes"), expected.beforeVat().toPlainString());
        number(page.document().get("totalTaxes"), expected.vat().toPlainString());
        Assert.assertEquals(lines().size(), mixed ? 2 : 1);
        number(data.balance(subject, 1), "1000");
        if (!mixed && !discounted) {
            var restaurant = (RestaurantInvoicePage) page;
            restaurant.openPayment();
            page.addOtherPayment(payment.name(), expected.net().toPlainString());
            prepareSave();
            restaurant.payAndClose();
            var invoice = saved();
            var persisted = data.read(invoice);
            number(persisted.get("totalAdditionalTaxes"), expected.additionalTax().toPlainString());
            number(persisted.get("netBeforeTaxes"), expected.beforeVat().toPlainString());
            number(persisted.get("totalTaxes"), expected.vat().toPlainString());
            number(persisted.get("net"), expected.net().toPlainString());
            number(data.balance(subject, 1), "999");
            Allure.addAttachment(
                    "Saved restaurant additional tax",
                    "application/json",
                    new org.json.JSONObject(persisted).toString(2),
                    ".json");
        }
        new BranchProfile("additional-tax-smoke", branch)
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
    }

    private SpecialSalesTestData.Record taxItem(String key, String price, boolean subject) {
        var values = new LinkedHashMap<>(data.itemValues());
        values.put("salesPrice", new BigDecimal(price));
        values.put("minSalesPrice", 0);
        values.put("maxSalesPrice", 1000);
        values.put("isTaxable", true);
        values.put("itemTax", 15);
        values.put("isAdditionalTaxable", subject);
        var owned = data.fixture(key, "Items", values);
        var read = data.read(owned);
        Assert.assertEquals(
                read.get("isAdditionalTaxable"), subject, "Persisted item additional-tax flag");
        return owned;
    }
}
