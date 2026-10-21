package SpecialSalesTest;

import static SpecialSalesTest.SpecialSalesTestData.fields;

import SpecialSalesTest.SpecialSalesTestData.Record;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Payload definitions; ownership, branch state, recovery and cleanup stay with the owner. */
final class SpecialSalesFixtureDefinitions {
    private final SpecialSalesTestData data;

    SpecialSalesFixtureDefinitions(SpecialSalesTestData data) {
        this.data = data;
    }

    Record account(String key) {
        Record parent =
                data.fixture(
                        "account-main-" + key,
                        "Accounts",
                        fields(
                                "type",
                                "Main",
                                "code",
                                Integer.toString(
                                        java.util.concurrent.ThreadLocalRandom.current()
                                                .nextInt(10000000, 99999999)),
                                "nature",
                                "Debit",
                                "finalAccountType",
                                "Budget",
                                "level",
                                1,
                                "isDistributable",
                                false,
                                "isSharedBetweenBranches",
                                false,
                                "prevDebit",
                                0,
                                "prevCredit",
                                0,
                                "previousBalances",
                                List.of()));
        return data.fixture(
                "account-sub-" + key,
                "Accounts",
                fields(
                        "type",
                        "Sub",
                        "nature",
                        "Debit",
                        "code",
                        parent.values().get("code") + "01",
                        "finalAccountType",
                        "Budget",
                        "parentId",
                        parent.id(),
                        "level",
                        2,
                        "isDistributable",
                        false,
                        "isSharedBetweenBranches",
                        false,
                        "prevDebit",
                        0,
                        "prevCredit",
                        0,
                        "previousBalances",
                        List.of()));
    }

    Record unit(int index) {
        return data.fixture("unit-" + index, "Units", fields("conversionRates", List.of()));
    }

    Record mainCategory() {
        return data.fixture("category-main", "ItemCategories", fields("isMain", true));
    }

    Record category() {
        return data.fixture(
                "category",
                "ItemCategories",
                fields(
                        "isMain",
                        false,
                        "isForResturants",
                        true,
                        "isForPOS",
                        true,
                        "parentCategoryId",
                        mainCategory().id()));
    }

    Map<String, Object> itemValues() {
        return fields(
                "categoryId",
                category().id(),
                "mainUnitId",
                unit(1).id(),
                "mainUnitBarcode",
                "PR" + UUID.randomUUID().toString().replace("-", ""),
                "type",
                "RawMaterial",
                "isStoreItem",
                true,
                "isTaxable",
                false,
                "itemTax",
                0,
                "salesPrice",
                100,
                "minSalesPrice",
                90,
                "maxSalesPrice",
                110,
                "sales",
                true,
                "purch",
                true,
                "returnSales",
                true,
                "returnPurch",
                true,
                "quotation",
                true,
                "salesOrder",
                true,
                "purchOrder",
                true,
                "storageTransfer",
                true,
                "additionOrder",
                true,
                "exchangeOrder",
                true,
                "damageAdjustment",
                true,
                "isScalesItem",
                false,
                "subUnits",
                List.of(),
                "components",
                List.of(),
                "additions",
                List.of(),
                "offers",
                List.of(),
                "discounts",
                List.of(),
                "stores",
                List.of(
                        fields(
                                "storeId",
                                data.store(1).id(),
                                "itemPlace",
                                "QA",
                                "openingBalance",
                                1000,
                                "currentBalance",
                                1000,
                                "minimumLimit",
                                0,
                                "reorderLimit",
                                0,
                                "costPrice",
                                80,
                                "isActive",
                                true),
                        fields(
                                "storeId",
                                data.store(2).id(),
                                "openingBalance",
                                1000,
                                "currentBalance",
                                1000,
                                "minimumLimit",
                                0,
                                "reorderLimit",
                                0,
                                "costPrice",
                                80,
                                "isActive",
                                true)));
    }

    Record item(int index) {
        return data.fixture("item-" + index, "Items", itemValues());
    }

    Record customer() {
        return customer("customer");
    }

    Record registeredCustomer() {
        return customer("registered-customer");
    }

    Record customer(String key) {
        return data.fixture(
                key,
                "Customers",
                fields(
                        "discount",
                        0,
                        "applyDiscount",
                        false,
                        "isActive",
                        true,
                        "cashSales",
                        true,
                        "creditNote",
                        true,
                        "salesDiscount",
                        true,
                        "creditSales",
                        true,
                        "salesReturn",
                        true,
                        "quotation",
                        true,
                        "isTaxable",
                        true,
                        "isClosed",
                        false,
                        "isOnTheFly",
                        false,
                        "salesOrder",
                        true,
                        "dealerType",
                        "Customer",
                        "isSameBranch",
                        true,
                        "allowanceDays",
                        0,
                        "accountId",
                        account(key).id()));
    }

    Record bank() {
        return data.fixture("bank", "Banks", fields("accountId", account("bank").id()));
    }

    Record payment() {
        return data.fixture("payment", "PaymentMethods", fields("bankId", bank().id()));
    }

    Record safe() {
        return data.fixture("safe", "Safes", fields("accountId", account("safe").id()));
    }

    Record table() {
        return data.fixture(
                "table",
                "Tables",
                fields("isActive", true, "isClosed", false, "isOnHold", false, "chairsCount", "4"));
    }

    Record priceList() {
        Record item = item(1);
        return data.fixture(
                "price-list",
                "PriceLists",
                fields(
                        "pricelistDate",
                        java.time.LocalDate.now().toString(),
                        "items",
                        List.of(
                                fields(
                                        "itemId",
                                        item.id(),
                                        "unitId",
                                        unit(1).id(),
                                        "salesPrice",
                                        125,
                                        "minSalesPrice",
                                        100,
                                        "maxSalesPrice",
                                        150))));
    }
}
