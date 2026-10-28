/* شرح الملف: تجهيز مخازن الحركة وأصنافها ووحداتها والمتعامل المختار، وحفظ الرصيد الأصلي قبل التنفيذ.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.testng.annotations.*;

final class StorePreparationSteps {
    private final StoreScenarioTest owner;

    StorePreparationSteps(StoreScenarioTest owner) {
        this.owner = owner;
    }

    String baselineMode() {
        return owner.kind() == StoreMovementKind.EXCHANGE ? "picker" : "barcode";
    }

    String defaultParty() {
        return owner.kind() == StoreMovementKind.EXCHANGE
                ? "Customers"
                : owner.kind() == StoreMovementKind.ADDITION ? "Suppliers" : "Agents";
    }

    StoreTestData.Record ownItem(int index) {
        return owner.kind() == StoreMovementKind.MANUFACTURING
                ? owner.data.manufactured(index)
                : owner.data.item(index);
    }

    StoreTestData.Record ownParty(String type) {
        return switch (type) {
            case "Customers" -> owner.data.customer();
            case "Suppliers" -> owner.data.supplier();
            case "Agents" -> owner.data.agent(owner.kind() == StoreMovementKind.ADDITION);
            case "ExchangeDestinations" -> owner.data.destination();
            default -> throw new IllegalArgumentException(type);
        };
    }

    String partyLabel(String type) {
        return switch (type) {
            case "Customers" -> "العملاء";
            case "Suppliers" -> "الموردين";
            case "Agents" -> "المندوبين";
            case "ExchangeDestinations" -> "جهات الصرف";
            default -> throw new IllegalArgumentException(type);
        };
    }

    void prepare(String omitted, String partyType) {
        owner.item = owner.ownItem(1);
        owner.data.store(2);
        if (owner.kind().party()) owner.selectedParty = owner.ownParty(partyType);
        owner.snapshot(owner.item);
        if (owner.kind() == StoreMovementKind.MANUFACTURING) owner.snapshot(owner.data.item(2));
        owner.page.smOpen(owner.kind().route + "/new");
        owner.page.smCapture(owner.marker, owner.data.store(1).id(), owner.item.id());
        if (!"store".equals(omitted))
            owner.page.smChoose(
                    owner.kind() == StoreMovementKind.TRANSFER ? "من مخزن" : "المخزن",
                    owner.data.store(1).name());
        else if (owner.kind() != StoreMovementKind.TRANSFER) owner.page.smClearChoice("المخزن");
        if (owner.kind() == StoreMovementKind.TRANSFER && !"destination".equals(omitted))
            owner.page.smChoose("الي مخزن", owner.data.store(2).name());
        if (owner.kind().party() && !"type".equals(omitted)) {
            owner.page.smChoose("نوع البند", partyLabel(partyType));
            if (!"party".equals(omitted))
                owner.page.smChoose(partyLabel(partyType), owner.ownParty(partyType).name());
        }
        if (owner.kind() != StoreMovementKind.MANUFACTURING) owner.page.smNotes(owner.marker);
    }

    void snapshot(StoreTestData.Record record) {
        for (int index = 1; index <= 2; index++)
            owner.initialBalances.put(
                    record.id() + ":" + index, owner.number(owner.data.balance(record, index)));
    }

    BigDecimal initial(StoreTestData.Record record, int index) {
        return owner.initialBalances.get(record.id() + ":" + index);
    }

    void fill(String mode) {
        owner.add(owner.item, mode);
        owner.page.smQuantity(owner.item.name(), "3");
    }

    void add(StoreTestData.Record record, String mode) {
        switch (mode) {
            case "barcode" ->
                    owner.page.smAddBarcode(
                            record.values().get("mainUnitBarcode").toString(), record.name());
            case "picker" -> owner.page.smAddPicker(record.name());
            default -> owner.page.smAddName(record.name());
        }
    }
}
