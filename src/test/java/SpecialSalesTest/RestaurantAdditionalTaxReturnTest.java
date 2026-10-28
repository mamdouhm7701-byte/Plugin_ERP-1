/* شرح الملف: اختبارات المرتجع يعيد الإضافية وضريبة الفاتورة الأصلية: المرتجع يعيد الإضافية وضريبة
 * الفاتورة الأصلية.
 */
package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

public final class RestaurantAdditionalTaxReturnTest extends SpecialSalesScenario {
    private static final String RETURN_ENDPOINT = "ItemTransactions-ReturnSales";
    private SalesModulePage returnPage;
    private String returnMarker;

    @Override
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @DataProvider
    public Object[][] quantities() {
        return new Object[][] {{1}, {3}};
    }

    @Test(dataProvider = "quantities")
    public void linkedReturnRefundsOriginalAdditionalTaxAndVat(int returnedQuantity) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(
                profile.expected().activityType(), BranchPolicies.ActivityType.RESTAURANT);
        Assert.assertTrue(Boolean.parseBoolean(System.getProperty("business.additionalTax")));
        String price = profile.expected().taxPolicy() == TaxPolicy.PRICE_INCLUDED ? "11.5" : "10";
        var values = TestSupport.PolicyItemRecipe.create(data.itemValues(), price, false);
        values.put("isAdditionalTaxable", true);
        var subject = data.fixture("return-additional-item", "Items", values);
        var payment = data.payment();
        restartPage();
        page.addItem(
                subject.name(),
                subject.values().get("mainUnitBarcode").toString(),
                data.category().name());
        page.quantity(subject.name(), "3");
        var sourceExpected = expected(profile, price, 3);
        page.waitNet(sourceExpected.net().toPlainString());
        RestaurantTaxAssertions.amounts(page.document(), sourceExpected);
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.openPayment();
        page.addOtherPayment(payment.name(), sourceExpected.net().toPlainString());
        prepareSave();
        restaurant.payAndClose();
        var source = saved();
        var storedSource = data.read(source);
        RestaurantTaxAssertions.amounts(storedSource, sourceExpected);
        Allure.addAttachment(
                "Owned restaurant return source",
                "application/json",
                new org.json.JSONObject(storedSource).toString(2),
                ".json");
        number(data.balance(subject, 1), "997");
        returnMarker = "QA-SP-RETURN-" + UUID.randomUUID();
        returnPage = new SalesModulePage(driver);
        returnPage.slLinkedReturn(source.id(), subject.name());
        returnPage.slCapture(returnMarker, data.store(1).id(), subject.id(), RETURN_ENDPOINT);
        returnPage.slNotes(returnMarker);
        Assert.assertEquals(returnPage.slDocument().get("sourceTransactionId"), source.id());
        Assert.assertEquals(returnPage.slDocument().get("dealerId"), customer.id());
        returnPage.slQuantity(subject.name(), Integer.toString(returnedQuantity));
        var returned = expected(profile, price, returnedQuantity);
        Allure.addAttachment(
                "Linked restaurant return independent expectation",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "caseId",
                                        returnedQuantity == 1
                                                ? "REST-RETURN-001"
                                                : "REST-RETURN-002",
                                        "sourceId",
                                        source.id(),
                                        "sourceQuantity",
                                        3,
                                        "returnedQuantity",
                                        returnedQuantity,
                                        "additionalRefund",
                                        returned.additionalTax(),
                                        "vatRefund",
                                        returned.vat(),
                                        "netRefund",
                                        returned.net(),
                                        "actual",
                                        returnPage.slDocument()))
                        .toString(2),
                ".json");
        returnPage.slWaitNet(returned.net().toPlainString());
        RestaurantTaxAssertions.amounts(returnPage.slDocument(), returned);
        data.rememberAttempts(
                List.of(
                        SpecialSalesTestData.fields(
                                "endpoint", RETURN_ENDPOINT, "values", returnPage.slDocument())),
                returnMarker);
        returnPage.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d -> {
                            captureReturn();
                            return !returnPage.slSaves().isEmpty();
                        });
        returnPage.slSettled();
        var saved = captureReturn();
        var stored = data.read(saved);
        RestaurantTaxAssertions.amounts(stored, returned);
        Assert.assertEquals(stored.get("sourceTransactionId"), source.id());
        var line = (Map<?, ?>) ((List<?>) stored.get("lines")).getFirst();
        Assert.assertEquals(line.get("itemId"), subject.id());
        number(line.get("quantity"), Integer.toString(returnedQuantity));
        number(data.balance(subject, 1), Integer.toString(997 + returnedQuantity));
        Allure.addAttachment(
                "Saved restaurant additional-tax return",
                "application/json",
                new org.json.JSONObject(stored).toString(2),
                ".json");
        returnPage.slDismiss();
    }

    private RestaurantAdditionalTaxCalculator.Totals expected(
            BranchProfile profile, String price, int quantity) {
        return RestaurantAdditionalTaxCalculator.calculate(
                List.of(
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.valueOf(quantity), new BigDecimal(price), true)),
                profile.expected().taxPolicy(),
                true,
                new BigDecimal("100"),
                2);
    }

    @SuppressWarnings("unchecked")
    private SpecialSalesTestData.Record captureReturn() {
        data.rememberAttempts(returnPage.slAttempts(), returnMarker);
        SpecialSalesTestData.Record last = null;
        for (var save : returnPage.slSaves()) {
            var values = (Map<String, Object>) save.get("values");
            Assert.assertEquals(save.get("endpoint"), RETURN_ENDPOINT);
            Assert.assertEquals(values.get("notes"), returnMarker);
            data.track(RETURN_ENDPOINT, save.get("id"), returnMarker, values);
            last =
                    new SpecialSalesTestData.Record(
                            RETURN_ENDPOINT, save.get("id"), returnMarker, values);
        }
        return last;
    }

    @AfterMethod(alwaysRun = true)
    public void retainReturnAttemptsForRecovery() {
        if (returnPage != null && returnMarker != null) captureReturn();
        returnPage = null;
        returnMarker = null;
    }
}
