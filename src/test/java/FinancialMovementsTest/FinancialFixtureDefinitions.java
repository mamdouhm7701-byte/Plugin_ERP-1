package FinancialMovementsTest;

import static FinancialMovementsTest.FinancialTestData.fields;

import FinancialMovementsTest.FinancialTestData.Record;
import java.util.List;
import java.util.Map;

/** Payload recipes; the owner retains registration, recovery and cleanup ordering. */
final class FinancialFixtureDefinitions {
    private final FinancialTestData data;

    FinancialFixtureDefinitions(FinancialTestData data) {
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

    Record customer(int index) {
        return data.fixture(
                "customer-" + index,
                "Customers",
                fields(
                        "isActive",
                        true,
                        "cashSales",
                        true,
                        "creditSales",
                        true,
                        "returnSales",
                        true,
                        "isTaxable",
                        true,
                        "isOnTheFly",
                        false,
                        "isClosed",
                        false,
                        "salesReturn",
                        true,
                        "quotation",
                        true,
                        "salesOrder",
                        true,
                        "salesDiscount",
                        true,
                        "creditNote",
                        true,
                        "allowanceDays",
                        0,
                        "dealerType",
                        "Customer",
                        "isSameBranch",
                        true,
                        "startDealingDate",
                        java.time.LocalDate.now().toString(),
                        "accountId",
                        account("customer-" + index).id()));
    }

    Record supplier(int index) {
        return data.fixture(
                "supplier-" + index,
                "Suppliers",
                fields(
                        "isActive",
                        true,
                        "cashPurchase",
                        true,
                        "creditPurchase",
                        true,
                        "isTaxable",
                        true,
                        "isOnTheFly",
                        false,
                        "isClosed",
                        false,
                        "purchaseReturn",
                        true,
                        "purchaseDiscount",
                        true,
                        "purchaseOrder",
                        true,
                        "debitNote",
                        true,
                        "allowanceDays",
                        0,
                        "dealerType",
                        "Supplier",
                        "isSameBranch",
                        true,
                        "startDealingDate",
                        java.time.LocalDate.now().toString(),
                        "accountId",
                        account("supplier-" + index).id()));
    }

    Record bank(int index) {
        return data.fixture(
                "bank-" + index, "Banks", fields("accountId", account("bank-" + index).id()));
    }

    Record safe(int index) {
        return data.fixture(
                "safe-" + index, "Safes", fields("accountId", account("safe-" + index).id()));
    }

    Record payment() {
        return data.fixture("payment", "PaymentMethods", fields("bankId", bank(1).id()));
    }

    Record expense() {
        return data.fixture("expense", "Expenses", fields("accountId", account("expense").id()));
    }

    Record income() {
        return data.fixture("income", "Incomes", fields("accountId", account("income").id()));
    }

    Record discount() {
        return data.fixture(
                "discount", "DiscountTypes", fields("accountId", account("discount").id()));
    }

    Record agent(boolean purchase) {
        return data.fixture(
                purchase ? "agent-purch" : "agent-sales",
                "Agents",
                fields(
                        "nature",
                        purchase ? "Purch" : "Sales",
                        "isActive",
                        true,
                        "commissionSlices",
                        List.of(),
                        "itemCommissions",
                        List.of()));
    }

    Record invoice(boolean purchase, java.math.BigDecimal allocation, String suffix) {
        Record dealer = purchase ? supplier(1) : customer(1);
        Record store =
                data.fixture("invoice-store", "Stores", fields("accountId", account("store").id()));
        Record unit = data.fixture("invoice-unit", "Units", fields("conversionRates", List.of()));
        Record mainCategory =
                data.fixture("invoice-category-main", "ItemCategories", fields("isMain", true));
        Record category =
                data.fixture(
                        "invoice-category",
                        "ItemCategories",
                        fields("isMain", false, "parentCategoryId", mainCategory.id()));
        Record item =
                data.fixture(
                        "invoice-item",
                        "Items",
                        fields(
                                "categoryId",
                                category.id(),
                                "mainUnitId",
                                unit.id(),
                                "mainUnitBarcode",
                                "FM" + suffix.replace("-", ""),
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
                                0,
                                "maxSalesPrice",
                                1000000,
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
                                "creditSales",
                                true,
                                "isScalesItem",
                                false,
                                "minSalesPrice",
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
                                "stores",
                                List.of(
                                        fields(
                                                "storeId",
                                                store.id(),
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
        java.math.BigDecimal value = allocation.multiply(java.math.BigDecimal.valueOf(2));
        Map<String, Object> line =
                fields(
                        "lineSerial",
                        1,
                        "storeId",
                        store.id(),
                        "itemId",
                        item.id(),
                        "unitId",
                        unit.id(),
                        "unitConversionRate",
                        1,
                        "quantity",
                        1,
                        "price",
                        value,
                        "total",
                        value,
                        "net",
                        value,
                        "costPrice",
                        0,
                        "discountPercentage",
                        0,
                        "discountValue",
                        0,
                        "additions",
                        List.of(),
                        "attributes",
                        List.of(),
                        "attachmentDetails",
                        List.of(),
                        "roundedAdditions",
                        0,
                        "roundedNet",
                        value);
        String date = java.time.LocalDate.now().toString();
        return data.fixture(
                purchase ? "purchase-invoice" : "sales-invoice",
                purchase ? "Purchases" : "ItemTransactions-Sales",
                fields(
                        "transactionType",
                        purchase ? "Purch" : "Sales",
                        "transactionChannel",
                        "Normal",
                        "transactionSource",
                        "Web",
                        "transactionDate",
                        date,
                        "transactionTime",
                        "12:00:00",
                        "dueDate",
                        date,
                        "paymentType",
                        "Credit",
                        "dealerId",
                        dealer.id(),
                        "storeId",
                        store.id(),
                        "isOffline",
                        false,
                        "isPosted",
                        false,
                        "isComplete",
                        false,
                        "paid",
                        0,
                        "total",
                        value,
                        "netBeforeTaxes",
                        value,
                        "net",
                        value,
                        "totalTaxes",
                        0,
                        "discountValue",
                        0,
                        "discountPercentage",
                        0,
                        "totalAdditionalTaxes",
                        0,
                        "lines",
                        List.of(line),
                        "taxes",
                        List.of(),
                        "payments",
                        List.of(),
                        "expenses",
                        List.of(),
                        "costCenters",
                        List.of(),
                        "extraProperties",
                        List.of()));
    }

    Record party(String type, int index) {
        return switch (type) {
            case "Safe" -> safe(index);
            case "Bank" -> bank(index);
            case "Customer" -> customer(index);
            case "Supplier" -> supplier(index);
            default -> throw new IllegalArgumentException(type);
        };
    }
}
