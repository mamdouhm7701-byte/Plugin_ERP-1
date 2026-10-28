/* شرح الملف: مراجعة الحقول المطلوبة ورفض الكميات والتواريخ غير الصحيحة وحدود الرصيد وحسابات السطر.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.*;

final class StoreInputValidation {
    private final StoreScenarioTest owner;

    StoreInputValidation(StoreScenarioTest owner) {
        this.owner = owner;
    }

    void required(String missing, String party, String expected) {
        // The UI locks store selectors after adding a line; test the empty-store path before adding
        // lines.
        boolean store = missing.equals("store") || missing.equals("destination");
        owner.prepare(missing, party);
        if (!store) owner.fill(owner.baselineMode());
        String label =
                switch (missing) {
                    case "store" ->
                            owner.kind() == StoreMovementKind.TRANSFER ? "من مخزن" : "المخزن";
                    case "destination" -> "الي مخزن";
                    case "type" -> "نوع البند";
                    default -> owner.partyLabel(party);
                };
        owner.page.smClearChoice(label);
        Assert.assertTrue(
                owner.page.smChoiceEmpty(label), "Required field was not cleared: " + label);
        if (!owner.page.smSaveDisabled()) {
            owner.page.smSave(false);
            String message = owner.page.smValidation();
            Assert.assertTrue(
                    message.replace('ة', 'ه').contains(expected.replace('ة', 'ه')),
                    "Expected " + expected + ": " + message);
        }
        owner.page.smSettled();
        owner.capture();
        Assert.assertTrue(owner.page.smSaves().isEmpty(), "Required field was accepted");
        owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1));
    }

    void rejectQuantity(String quantity) {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        owner.page.smQuantity(owner.item.name(), quantity);
        if (!owner.page.smSaveDisabled()) owner.page.smSave(false);
        var invalid = owner.page.smAwaitInvalidQuantity(owner.item.name());
        Allure.addAttachment(
                "Visible quantity rejection state",
                "application/json",
                new org.json.JSONObject(invalid).toString(),
                ".json");
        owner.page.smSettled();
        owner.capture();
        Assert.assertTrue(
                owner.page.smSaves().isEmpty(), "Invalid quantity was persisted: " + quantity);
        owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1));
    }

    void noItems() {
        owner.prepare(null, owner.defaultParty());
        if (!owner.page.smSaveDisabled()) {
            owner.page.smSave(false);
            String message = owner.page.smValidation();
            Assert.assertTrue(
                    message.contains("صنف")
                            || message.contains("أصناف")
                            || message.contains("اصناف"),
                    message);
        }
        owner.page.smSettled();
        owner.capture();
        Assert.assertTrue(owner.page.smSaves().isEmpty());
    }

    void oldDate() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        owner.page.smDate(owner.page.smCurrentDate().minusYears(1).toString());
        owner.page.smSave(false);
        String message = owner.page.smValidation();
        owner.page.smSettled();
        owner.capture();
        Assert.assertTrue(
                owner.page.smSaves().isEmpty(), "Date outside active fiscal year was accepted");
        Assert.assertTrue(
                message.contains("تاريخ") || message.contains("فتر") || message.contains("سن"),
                message);
    }

    void pickerWithoutStore() {
        owner.prepare("store", owner.defaultParty());
        owner.page.smButton("إضافة أصناف");
        Assert.assertTrue(owner.page.smValidation().contains("مخزن"));
        Assert.assertTrue(owner.page.smSaves().isEmpty());
    }

    void sameStore() {
        owner.prepare("destination", "Agents");
        owner.page.smChoose("الي مخزن", owner.data.store(1).name());
        Assert.assertTrue(owner.page.smValidation().contains("مخزن"));
        Assert.assertTrue(owner.page.smSaves().isEmpty());
    }

    void pickerWithoutDestination() {
        owner.prepare("destination", "Agents");
        owner.page.smButton("إضافة أصناف");
        Assert.assertTrue(owner.page.smValidation().contains("مخزن"));
        Assert.assertTrue(owner.page.smSaves().isEmpty());
    }

    void SM_RequiredStore() {
        owner.required("store", owner.defaultParty(), "مخزن");
    }

    void SM_ZeroQuantity() {
        owner.rejectQuantity("0");
    }

    void SM_NegativeQuantity() {
        owner.rejectQuantity("-1");
    }

    void SM_DecimalQuantity() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        owner.page.smQuantity(owner.item.name(), "1.25");
        var record = owner.save(false);
        owner.sameNumber(
                ((Map<?, ?>) ((List<?>) owner.data.read(record).get("lines")).get(0))
                        .get("quantity"),
                "1.25");
        owner.assertEffect("1.25");
    }

    void SM_StockBalanceBoundary() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        String quantity = owner.kind() == StoreMovementKind.MANUFACTURING ? "500" : "1000";
        owner.page.smQuantity(owner.item.name(), quantity);
        var record = owner.save(false);
        owner.sameNumber(
                ((Map<?, ?>) ((List<?>) owner.data.read(record).get("lines")).get(0))
                        .get("quantity"),
                quantity);
        owner.assertEffect(quantity);
    }

    void SM_LineCalculations() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        Map<String, String> values = owner.page.smLineValues(owner.item.name());
        Allure.addAttachment(
                "Displayed warehouse line values",
                "application/json",
                new org.json.JSONObject(values).toString(),
                ".json");
        BigDecimal quantity = owner.number(values.get("الكمية"));
        int checked = 0;
        String totalLabel = values.containsKey("الاجمالى") ? "الاجمالى" : "الإجمالي";
        if (values.containsKey("سعر التكلفة") && values.containsKey(totalLabel)) {
            owner.sameNumber(
                    owner.number(values.get(totalLabel).replace(",", "")),
                    owner.number(values.get("سعر التكلفة").replace(",", ""))
                            .multiply(quantity)
                            .toPlainString());
            checked++;
        }
        if (values.containsKey("سعر البيع") && values.containsKey("إجمالي سعر البيع")) {
            owner.sameNumber(
                    owner.number(values.get("إجمالي سعر البيع").replace(",", "")),
                    owner.number(values.get("سعر البيع").replace(",", ""))
                            .multiply(quantity)
                            .toPlainString());
            checked++;
        }
        Assert.assertTrue(checked > 0, "No displayed price/total pair was checked: " + values);
        var record = owner.save(false);
        owner.assertEffect("3");
        Map<?, ?> stored = (Map<?, ?>) ((List<?>) owner.data.read(record).get("lines")).get(0);
        if (values.containsKey("سعر التكلفة"))
            owner.sameNumber(stored.get("costPrice"), values.get("سعر التكلفة").replace(",", ""));
        if (values.containsKey("سعر البيع"))
            owner.sameNumber(stored.get("salesPrice"), values.get("سعر البيع").replace(",", ""));
    }

    void SM_RemoveItem() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        var second = owner.ownItem(2);
        owner.add(second, owner.baselineMode());
        owner.page.smRemoveLine(second.name());
        var record = owner.save(false);
        Assert.assertEquals(
                ((List<?>) owner.data.read(record).get("lines")).size(), 1, "مطابقة بنود الفاتورة");
        owner.assertEffect("3");
    }

    void SM_DuplicateItem() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        owner.add(owner.item, owner.baselineMode());
        owner.page.smDismiss();
        Assert.assertEquals(owner.page.smLineCount(owner.item.name()), 1);
        owner.page.smQuantity(owner.item.name(), "3");
        var record = owner.save(false);
        Assert.assertEquals(
                ((List<?>) owner.data.read(record).get("lines")).size(), 1, "مطابقة بنود الفاتورة");
        owner.assertEffect("3");
    }
}
