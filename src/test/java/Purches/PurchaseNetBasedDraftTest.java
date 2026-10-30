/* شرح الملف: اختبار ضريبة مسودة المشتريات والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package Purches;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.System_Modules.Login.purches.PurchaseModulePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class PurchaseNetBasedDraftTest extends BaseTest {
    @DataProvider
    public Object[][] documents() {
        return new Object[][] {{"purch-invoice"}, {"purchOrder"}, {"return-purch"}};
    }

    @Test(dataProvider = "documents")
    public void movementDiscountAndVatMatchIndependentRecipe(String route) {
        requirePreparedSession();
        var baseline = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        Assert.assertEquals(
                baseline.taxPolicy(),
                TaxPolicy.NET_BASED,
                "This explicit live contract requires NetBased; it must not change the branch policy");
        Assert.assertEquals(
                baseline.discountLevel(),
                DiscountLevel.TRANSACTION,
                "This explicit live contract requires movement discounts");
        var page = new PurchaseModulePage(driver);
        page.puOpen(route + "/new");
        page.puFormReady();
        var names = page.puAddFirstTwoExistingItems();
        for (String name : names) {
            page.puQuantity(name, "1");
            page.puSetLine(name, route.equals("purch-invoice") ? "سعر الشراء" : "السعر", "100");
        }
        page.puDiscount("10", false);
        var inputs =
                List.of(
                        new PolicyInvoiceLine(
                                "first",
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                BigDecimal.ZERO,
                                DiscountType.VALUE,
                                false),
                        new PolicyInvoiceLine(
                                "second",
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                BigDecimal.ZERO,
                                DiscountType.VALUE,
                                false));
        var expected =
                BranchInvoiceCalculator.calculateMovementDiscount(
                        inputs, new BigDecimal("10"), DiscountType.VALUE, TaxPolicy.NET_BASED, 2);
        page.puWaitNet(expected.getNet().toPlainString());
        var actual = page.puTotals();
        Map<String, BigDecimal> amounts =
                Map.of(
                        "الاجمالى",
                        expected.getTotal(),
                        "خصم",
                        expected.getDiscount(),
                        "قبل الضريبة",
                        expected.getBeforeTax(),
                        "الضريبة",
                        expected.getTax(),
                        "الصافي",
                        expected.getNet());
        amounts.forEach(
                (label, amount) -> {
                    Assert.assertNotNull(actual.get(label), "Missing displayed total: " + label);
                    TestSupport.DecimalAmountAssertions.matchesExpected(
                            actual.get(label).replace(",", ""), amount);
                });
        new BranchProfile("live-draft-baseline", baseline)
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        Allure.addAttachment(
                "Unsaved policy draft",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "route",
                                        route,
                                        "activity",
                                        baseline.activityType().code(),
                                        "taxPolicy",
                                        baseline.taxPolicy().code(),
                                        "discountLevel",
                                        baseline.discountLevel().code(),
                                        "saved",
                                        false,
                                        "actual",
                                        actual))
                        .toString(2),
                ".json");
        Allure.addAttachment(
                "Unsaved draft screenshot",
                "image/png",
                new java.io.ByteArrayInputStream(
                        ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)),
                ".png");
    }
}
