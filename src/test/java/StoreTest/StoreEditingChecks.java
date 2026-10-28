/* شرح الملف: مراجعة هوية المتعامل وتعديل وحذف الحركة المملوكة وإلغاء التغييرات دون فقد السجل.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

final class StoreEditingChecks {
    private final StoreScenarioTest owner;

    StoreEditingChecks(StoreScenarioTest owner) {
        this.owner = owner;
    }

    void partyIdentity(String type) {
        var record = owner.create(owner.baselineMode(), false, type);
        var stored = owner.data.read(record);
        Object dealer = stored.get("dealerId"), agent = stored.get("agentId");
        if (type.equals("Customers") || type.equals("Suppliers")) {
            if (owner.hasId(dealer))
                Assert.assertEquals(
                        dealer,
                        owner.selectedParty.id(),
                        "Document contains a default dealer instead of its selected party");
        } else
            Assert.assertFalse(
                    owner.hasId(dealer),
                    "Document contains an unexpected default dealer: " + dealer);
        if (type.equals("Agents")) {
            if (owner.hasId(agent)) Assert.assertEquals(agent, owner.selectedParty.id());
        } else
            Assert.assertFalse(
                    owner.hasId(agent), "Document contains an unexpected agent: " + agent);
    }

    boolean hasId(Object id) {
        return id != null
                && !id.toString().isBlank()
                && !id.toString().equals("0")
                && !id.toString().equals("00000000-0000-0000-0000-000000000000");
    }

    void editOwn() {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        owner.page.smEdit(owner.kind().route, record.id(), record.name(), owner.item.name());
        owner.page.smCapture(owner.marker, owner.data.store(1).id(), owner.item.id());
        if (owner.kind() != StoreMovementKind.MANUFACTURING)
            owner.page.smNotes(owner.marker + " EDIT");
        owner.page.smQuantity(owner.item.name(), "5");
        var updated = owner.save(false);
        Assert.assertEquals(updated.id(), record.id());
        owner.sameNumber(
                ((Map<?, ?>) ((List<?>) owner.data.read(updated).get("lines")).get(0))
                        .get("quantity"),
                "5");
        owner.assertEffect("5");
    }

    void find(StoreTestData.Record record) {
        owner.page.smFind(
                owner.kind().list, record.name(), record.id(), owner.data.read(record).get("code"));
    }

    void deleteOwn() {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        owner.find(record);
        owner.page.smDeleteOwn(record.name(), true);
        new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(30))
                .until(d -> owner.data.read(record) == null);
        owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1));
        owner.fixtureBalance(owner.item, 2, owner.initial(owner.item, 2));
        if (owner.kind() == StoreMovementKind.MANUFACTURING)
            owner.fixtureBalance(owner.data.item(2), 1, owner.initial(owner.data.item(2), 1));
    }

    void notesRoundTrip() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        owner.page.smNotes(owner.marker + " ملاحظات warehouse QA");
        var record = owner.save(false);
        Assert.assertEquals(
                owner.data.read(record).get("notes"),
                owner.marker + " ملاحظات warehouse QA",
                "مطابقة الملاحظات");
        owner.assertEffect("3");
    }

    void SM_CancelCreation() {
        owner.prepare(null, owner.defaultParty());
        owner.fill(owner.baselineMode());
        owner.page.smOpen(owner.kind().list);
        Assert.assertTrue(owner.page.smSaves().isEmpty());
        owner.fixtureBalance(owner.item, 1, owner.initial(owner.item, 1));
    }

    void SM_CancelEdit() {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        Object notes = owner.data.read(record).get("notes");
        owner.page.smEdit(owner.kind().route, record.id(), record.name(), owner.item.name());
        owner.page.smCapture(owner.marker, owner.data.store(1).id(), owner.item.id());
        owner.page.smQuantity(owner.item.name(), "8");
        if (owner.kind() != StoreMovementKind.MANUFACTURING)
            owner.page.smNotes(owner.marker + " UNSAVED");
        owner.page.smOpen(owner.kind().list);
        Assert.assertEquals(owner.data.read(record).get("notes"), notes, "مطابقة الملاحظات");
        owner.assertEffect("3");
    }

    void SM_CancelDelete() {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        owner.find(record);
        owner.page.smDeleteOwn(record.name(), false);
        Assert.assertNotNull(owner.data.read(record));
        owner.assertEffect("3");
    }
}
