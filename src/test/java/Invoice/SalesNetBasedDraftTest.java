/* شرح الملف: اختبار حساب ضريبة مسودة البيع بعد الخصم والتأكد من حفظ البيانات وظهور النتيجة
 * المطلوبة.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class SalesNetBasedDraftTest extends BaseTest {
    @DataProvider
    public Object[][] documents() {
        return new Object[][] {{"invoice"}, {"quotation"}, {"sales-order"}, {"return-sales"}};
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
        var page = new SalesModulePage(driver);
        page.slOpen(route + "/new");
        page.slFormReady();
        var names = page.slAddFirstTwoExistingItems();
        for (String name : names) {
            page.slQuantity(name, "1");
            page.slSetLine(name, "السعر", "100");
        }
        page.slDiscount("10", false);
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
        page.slWaitNet(expected.getNet().toPlainString());
        var actual = page.slTotals();
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
                    Assert.assertEquals(
                            new BigDecimal(actual.get(label).replace(",", "")).compareTo(amount),
                            0,
                            route + " / " + label);
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
