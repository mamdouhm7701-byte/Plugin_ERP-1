/* شرح الملف: التقاط الحركة المحفوظة ومطابقة مخزنها وصنفها ووحدتها وطباعتها، ثم مراجعة أثرها على الرصيد.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

final class StoreSaveVerification {
    private final StoreScenarioTest owner;

    StoreSaveVerification(StoreScenarioTest owner) {
        this.owner = owner;
    }

    List<StoreTestData.Record> capture() {
        List<StoreTestData.Record> records = new ArrayList<>();
        if (!owner.marker.equals(owner.page.smCaptureMarker())) return records;
        owner.data.rememberAttempts(owner.page.smAttempts(), owner.page.smSaves(), owner.marker);
        for (var saved : owner.page.smSaves()) {
            Assert.assertEquals(saved.get("endpoint"), owner.kind().endpoint);
            Map<String, Object> values = (Map<String, Object>) saved.get("values");
            String note =
                    owner.kind() == StoreMovementKind.MANUFACTURING
                            ? owner.marker
                            : (String) values.get("notes");
            Assert.assertTrue(
                    note != null && note.startsWith(owner.marker),
                    "Refuse unrelated warehouse document");
            owner.data.track(owner.kind().endpoint, saved.get("id"), note, values);
            records.add(
                    new StoreTestData.Record(owner.kind().endpoint, saved.get("id"), note, values));
        }
        owner.data.rememberAttempts(owner.page.smAttempts(), owner.page.smSaves(), owner.marker);
        return records;
    }

    StoreTestData.Record save(boolean print) {
        int before = owner.page.smSaves().size();
        owner.page.smSave(print);
        try {
            new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(40))
                    .until(
                            d -> {
                                owner.data.rememberAttempts(
                                        owner.page.smAttempts(),
                                        owner.page.smSaves(),
                                        owner.marker);
                                return owner.page.smSaves().size() > before;
                            });
        } catch (TimeoutException e) {
            throw new AssertionError("Warehouse save failed: " + owner.page.smSaveErrors(), e);
        }
        owner.page.smSettled();
        var records = owner.capture();
        var record = records.get(records.size() - 1);
        var stored = owner.data.read(record);
        Allure.addAttachment(
                "Warehouse persisted entity",
                "application/json",
                new org.json.JSONObject(
                                StoreTestData.fields("sent", record.values(), "stored", stored))
                        .toString(),
                ".json");
        Assert.assertNotNull(stored);
        if (owner.kind() != StoreMovementKind.MANUFACTURING)
            Assert.assertEquals(stored.get("notes"), record.name(), "مطابقة الملاحظات");
        Assert.assertEquals(stored.get("storeId"), owner.data.store(1).id(), "مطابقة المخزن");
        Assert.assertEquals(
                stored.get("transactionType").toString().toLowerCase(Locale.ROOT),
                owner.kind().type.toLowerCase(Locale.ROOT),
                "مطابقة نوع الحركة");
        List<Map<String, Object>> lines = (List<Map<String, Object>>) stored.get("lines");
        Assert.assertFalse(lines.isEmpty());
        Assert.assertTrue(
                lines.stream().allMatch(l -> l.get("itemId").equals(owner.item.id())),
                "Unexpected item in owned document");
        Assert.assertTrue(
                lines.stream().allMatch(l -> l.get("unitId").equals(owner.data.unit(1).id())),
                "Unexpected unit in owned document");
        if (owner.kind() == StoreMovementKind.TRANSFER)
            Assert.assertEquals(stored.get("destinationStoreId"), owner.data.store(2).id());
        if (owner.kind().party()) {
            Assert.assertEquals(stored.get("selectedDestinationId"), owner.selectedParty.id());
            Assert.assertEquals(
                    stored.get("exchangeDestinationType"),
                    record.values().get("exchangeDestinationType"));
        }
        if (print) owner.verifyReport(record);
        else owner.page.smDismiss();
        return record;
    }

    void verifyReport(StoreTestData.Record record) {
        new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(40))
                .until(d -> d.getWindowHandles().size() > 1);
        owner.caseDriver()
                .switchTo()
                .window(
                        owner.caseDriver().getWindowHandles().stream()
                                .filter(h -> !h.equals(owner.mainWindow))
                                .findFirst()
                                .orElseThrow());
        try {
            owner.page.utilitesReportReady();
            owner.page.AssertTextInReport(owner.item.name());
            owner.page.AssertNumberInReport("3");
        } catch (RuntimeException | AssertionError e) {
            Allure.addAttachment(
                    "Warehouse report",
                    "image/png",
                    new java.io.ByteArrayInputStream(
                            ((TakesScreenshot) owner.caseDriver())
                                    .getScreenshotAs(OutputType.BYTES)),
                    "png");
            throw e;
        } finally {
            owner.caseDriver().close();
            owner.caseDriver().switchTo().window(owner.mainWindow);
            owner.page.smDismiss();
        }
    }

    void sameNumber(Object actual, String expected) {
        Assert.assertTrue(
                owner.number(actual).compareTo(new BigDecimal(expected)) == 0,
                "Expected " + expected + ", actual " + actual);
    }

    void fixtureBalance(StoreTestData.Record record, int index, BigDecimal expected) {
        new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(30))
                .until(
                        d ->
                                owner.number(owner.data.balance(record, index)).compareTo(expected)
                                        == 0);
    }

    void assertEffect(String quantity) {
        BigDecimal qty = new BigDecimal(quantity);
        switch (owner.kind()) {
            case EXCHANGE, DAMAGE ->
                    owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1).subtract(qty));
            case ADDITION ->
                    owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1).add(qty));
            case TRANSFER -> {
                owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1).subtract(qty));
                owner.fixtureBalance(owner.item, 2, owner.initial(owner.item, 2).add(qty));
            }
            case MANUFACTURING -> {
                owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1).add(qty));
                owner.fixtureBalance(
                        owner.data.item(2),
                        1,
                        owner.initial(owner.data.item(2), 1)
                                .subtract(qty.multiply(BigDecimal.valueOf(2))));
            }
        }
    }

    StoreTestData.Record create(String mode, boolean print, String party) {
        owner.prepare(null, party);
        owner.fill(mode);
        var record = owner.save(print);
        owner.assertEffect("3");
        return record;
    }
}
