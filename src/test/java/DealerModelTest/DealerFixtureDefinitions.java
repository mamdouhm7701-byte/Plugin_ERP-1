/* شرح الملف: تحديد البيانات المطلوبة لإنشاء سجلات التجربة، ضمن العملاء والموردين والمناديب.
 */
package DealerModelTest;

import static DealerModelTest.DealerTestData.fields;

import DealerModelTest.DealerTestData.Record;
import java.util.List;
import java.util.UUID;

final class DealerFixtureDefinitions {
    private final DealerTestData data;

    DealerFixtureDefinitions(DealerTestData data) {
        this.data = data;
    }

    Record area() {
        return data.fixture("area", "Areas", fields());
    }

    Record classification(String type) {
        return data.fixture(
                "class-" + type, "DealerClassifications", fields("classificationType", type));
    }

    Record mainDealer(String type) {
        return data.fixture("main-" + type, "MainDealers", fields("type", type));
    }

    Record agent(String nature) {
        return data.fixture(
                "agent-" + nature,
                "Agents",
                fields(
                        "nature",
                        nature,
                        "commissionSlices",
                        List.of(),
                        "itemCommissions",
                        List.of(),
                        "isActive",
                        true,
                        "areaId",
                        area().id(),
                        "dealersClassificationId",
                        classification("Agents").id()));
    }

    Record account() {
        Record parent =
                data.fixture(
                        "account-main",
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
                "account-sub",
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

    Record store() {
        return data.fixture("store", "Stores", fields("accountId", account().id()));
    }

    Record unit() {
        return data.fixture("unit", "Units", fields("conversionRates", List.of()));
    }

    Record category() {
        Record parent = data.fixture("category-main", "ItemCategories", fields("isMain", true));
        return data.fixture(
                "category-sub",
                "ItemCategories",
                fields("isMain", false, "parentCategoryId", parent.id()));
    }

    Record item(int index) {
        String barcode = "QA" + UUID.randomUUID().toString().replace("-", "");
        return data.fixture(
                "item-" + index,
                "Items",
                fields(
                        "categoryId",
                        category().id(),
                        "mainUnitId",
                        unit().id(),
                        "mainUnitBarcode",
                        barcode,
                        "salesPrice",
                        100,
                        "minSalesPrice",
                        90,
                        "maxSalesPrice",
                        110,
                        "isScalesItem",
                        false,
                        "isTaxable",
                        false,
                        "isStoreItem",
                        true,
                        "type",
                        "RawMaterial",
                        "itemTax",
                        0,
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
                        "stores",
                        List.of(
                                fields(
                                        "storeId",
                                        store().id(),
                                        "itemPlace",
                                        "QA",
                                        "openingBalance",
                                        0,
                                        "currentBalance",
                                        0,
                                        "minimumLimit",
                                        0,
                                        "reorderLimit",
                                        0,
                                        "costPrice",
                                        80,
                                        "isActive",
                                        true))));
    }

    Record priceList() {
        return data.fixture(
                "price-list",
                "PriceLists",
                fields(
                        "items",
                        List.of(),
                        "pricelistDate",
                        java.time.LocalDate.now().toString(),
                        "code",
                        Integer.toString(
                                java.util.concurrent.ThreadLocalRandom.current()
                                        .nextInt(10000000, 99999999))));
    }
}
