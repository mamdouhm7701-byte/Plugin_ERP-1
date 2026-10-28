/* شرح الملف: خطوات حفظ بيانات الصنف والتحقق منها ومن طباعتها.
 */
package ProductsTest;

import Plugin_ERP.System_Modules.Login.Products.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

final class ProductPersistenceChecks {
    private final ProductScenarioTest owner;

    ProductPersistenceChecks(ProductScenarioTest owner) {
        this.owner = owner;
    }

    ProductsTestData.Record save(boolean print) {
        int before = owner.page.prSaves().size();
        if (print) owner.page.prSavePrint();
        else owner.page.prSave();
        try {
            new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(35))
                    .until(
                            d -> {
                                owner.data.rememberAttempts(owner.page.prAttempts(), owner.marker);
                                return owner.page.prSaves().size() > before;
                            });
        } catch (TimeoutException e) {
            throw new AssertionError("Product save failed: " + owner.page.prSaveErrors(), e);
        }
        owner.page.prSettled();
        List<ProductsTestData.Record> records = owner.capture();
        ProductsTestData.Record record = records.get(records.size() - 1);
        Map<String, Object> stored = owner.data.read(record);
        io.qameta.allure.Allure.addAttachment(
                "Saved product entity",
                "application/json",
                new org.json.JSONObject(
                                ProductsTestData.fields("sent", record.values(), "stored", stored))
                        .toString(),
                ".json");
        Assert.assertNotNull(stored);
        Assert.assertEquals(stored.get("arabicName"), record.name(), "مطابقة الاسم العربي");
        Object expectedEnglish = record.values().get("englishName");
        if (expectedEnglish == null || expectedEnglish.toString().isBlank())
            owner.assertOptionalEnglish(stored);
        else
            Assert.assertEquals(
                    stored.get("englishName"), expectedEnglish, "مطابقة الاسم الإنجليزي");
        if (record.values().containsKey("notes"))
            Assert.assertEquals(
                    stored.get("notes"), record.values().get("notes"), "مطابقة الملاحظات");
        if (print) owner.verifyReport(record);
        owner.page.prWaitList();
        return record;
    }

    void verifyReport(ProductsTestData.Record record) {
        new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(30))
                .until(d -> d.getWindowHandles().size() > 1);
        String report =
                owner.caseDriver().getWindowHandles().stream()
                        .filter(h -> !h.equals(owner.mainWindow))
                        .findFirst()
                        .orElseThrow();
        owner.caseDriver().switchTo().window(report);
        try {
            owner.page.prAwaitReport();
            owner.page.AssertTextInReport(record.name());
            owner.page.AssertTextInReport(record.values().get("englishName").toString());
            if (owner.kind() == ProductScenarioTest.Kind.PRICE) {
                owner.page.AssertTextInReport(owner.data.item(1).name());
                owner.page.AssertNumberInReport(
                        record.values().get("items") instanceof List<?> rows
                                ? ((Map<?, ?>) rows.get(0)).get("salesPrice").toString()
                                : "125.5");
            }
        } catch (RuntimeException | AssertionError e) {
            io.qameta.allure.Allure.addAttachment(
                    "Product report",
                    "image/png",
                    new java.io.ByteArrayInputStream(
                            ((TakesScreenshot) owner.caseDriver())
                                    .getScreenshotAs(OutputType.BYTES)),
                    "png");
            throw e;
        } finally {
            owner.caseDriver().close();
            owner.caseDriver().switchTo().window(owner.mainWindow);
        }
    }
}
