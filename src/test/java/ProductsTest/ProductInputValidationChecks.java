/* شرح الملف: مراجعة رفض أسعار الصنف ووحداته وأرصدة مخزنه غير الصحيحة، مع حفظ دليل أي سجل قُبل خطأً.
 * فائدته: يجمع قواعد الإدخال في مكان واحد ويُبقي أسماء الحالات الأصلية في ملف تشغيل الأصناف.
 */
package ProductsTest;

import Plugin_ERP.System_Modules.Login.Products.*;
import java.util.*;
import org.testng.Assert;

final class ProductInputValidationChecks {
    private final AddProductsTest owner;

    ProductInputValidationChecks(AddProductsTest owner) {
        this.owner = owner;
    }

    void subunitInvalid(String reason) {
        owner.openNew();
        owner.fill(null);
        String barcode = "PR" + UUID.randomUUID().toString().replace("-", "");
        String unit = reason.equals("same") ? owner.data.unit(1).name() : owner.data.unit(2).name();
        String rate =
                reason.equals("rate")
                        ? "0"
                        : reason.equals("one") ? "1" : reason.equals("negative") ? "-2" : "10";
        if (reason.equals("duplicate"))
            ((AddProductsPage) owner.page).prSubunit(unit, barcode, "10");
        ((AddProductsPage) owner.page)
                .prSubunit(unit, reason.equals("barcode") ? "" : barcode, rate);
        String message = owner.page.prValidation();
        Assert.assertFalse(message.isBlank());
        Assert.assertEquals(
                owner.page.prFormRowsContaining(unit),
                reason.equals("duplicate") ? 1 : 0,
                "Invalid subunit must not be added");
        Assert.assertTrue(owner.page.prSaves().isEmpty());
    }

    void enterPrices(String sale, String minimum, String maximum) {
        owner.page.prField("minSalesPrice", minimum);
        owner.page.prField("maxSalesPrice", maximum);
        owner.page.prField("salesPrice", sale);
        owner.assertNumber(owner.page.prFieldValue("salesPrice"), sale);
        owner.assertNumber(owner.page.prFieldValue("minSalesPrice"), minimum);
        owner.assertNumber(owner.page.prFieldValue("maxSalesPrice"), maximum);
    }

    void pricesValid(String sale, String minimum, String maximum) {
        owner.openNew();
        owner.fill(null);
        enterPrices(sale, minimum, maximum);
        var record = owner.save(false);
        var stored = owner.data.read(record);
        owner.assertNumber(stored.get("salesPrice"), sale);
        owner.assertNumber(stored.get("minSalesPrice"), minimum);
        owner.assertNumber(stored.get("maxSalesPrice"), maximum);
    }

    void pricesInvalid(String sale, String minimum, String maximum) {
        owner.openNew();
        owner.fill(null);
        enterPrices(sale, minimum, maximum);
        Map<String, String> messages = new LinkedHashMap<>(owner.page.prPriceValidationMessages());
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(
                            owner.caseDriver(), java.time.Duration.ofSeconds(10))
                    .until(
                            d -> {
                                messages.putAll(owner.page.prPriceValidationMessages());
                                return !messages.isEmpty();
                            });
        } catch (org.openqa.selenium.TimeoutException missingInlineFeedback) {
            // Still exercise Save so missing feedback cannot conceal a persisted invalid product.
        }
        io.qameta.allure.Allure.addAttachment(
                "Price fields before save",
                "image/png",
                new java.io.ByteArrayInputStream(
                        ((org.openqa.selenium.TakesScreenshot) owner.caseDriver())
                                .getScreenshotAs(org.openqa.selenium.OutputType.BYTES)),
                ".png");
        if (!owner.page.prSaveDisabled()) owner.page.prSave();
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(
                            owner.caseDriver(), java.time.Duration.ofSeconds(30))
                    .until(
                            d -> {
                                messages.putAll(owner.page.prPriceValidationMessages());
                                return !messages.isEmpty() || !owner.page.prSaves().isEmpty();
                            });
        } catch (org.openqa.selenium.TimeoutException missingOutcome) {
            // Capture requests and any saved entity before reporting missing field feedback.
        }
        owner.page.prSettled();
        var records = owner.capture();
        io.qameta.allure.Allure.addAttachment(
                "Visible price field validation",
                "application/json",
                new org.json.JSONObject(messages).toString(),
                ".json");
        for (var record : records) {
            var stored = owner.data.read(record);
            io.qameta.allure.Allure.addAttachment(
                    "Unexpected saved product prices",
                    "application/json",
                    new org.json.JSONObject(
                                    ProductsTestData.fields(
                                            "id",
                                            record.id(),
                                            "expectedRule",
                                            "minimum <= sale <= maximum; sale >= 0",
                                            "entered",
                                            ProductsTestData.fields(
                                                    "salesPrice",
                                                    sale,
                                                    "minSalesPrice",
                                                    minimum,
                                                    "maxSalesPrice",
                                                    maximum),
                                            "sent",
                                            record.values(),
                                            "stored",
                                            stored))
                            .toString(),
                    ".json");
            Assert.assertTrue(
                    stored == null,
                    "Application accepted invalid prices: minimum="
                            + minimum
                            + ", sale="
                            + sale
                            + ", maximum="
                            + maximum
                            + "; owned record="
                            + record.id());
        }
        Assert.assertTrue(owner.page.prSaves().isEmpty(), "Invalid price save was accepted");
        Assert.assertFalse(
                messages.isEmpty(),
                "Expected a visible validation message under a price field; save errors: "
                        + owner.page.prSaveErrors());
    }

    void stockInvalid(String reason) {
        owner.openNew();
        owner.fill(reason.equals("duplicate") ? null : "store");
        ((AddProductsPage) owner.page)
                .prStock(
                        owner.data.store(1).name(),
                        reason.equals("opening") ? "-1" : "0",
                        reason.equals("minimum") ? "-1" : "0",
                        reason.equals("cost") ? "-1" : "80");
        Assert.assertFalse(owner.page.prValidation().isBlank());
        Assert.assertEquals(
                owner.page.prFormRowsContaining(owner.data.store(1).name()),
                reason.equals("duplicate") ? 1 : 0);
        Assert.assertTrue(owner.page.prSaves().isEmpty());
    }
}
