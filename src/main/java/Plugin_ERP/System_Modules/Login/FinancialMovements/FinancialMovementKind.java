package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Enums.Pages;

public enum FinancialMovementKind {
    RECEIPT(Pages.COLLICTION, "Collections", "العميل"),
    PAYMENT(Pages.SUPPLIERBILLS, "Payments", "المورد"),
    EXPENSE(Pages.EXPENSE, "ExpenseTransactions", "النوع"),
    INCOME(Pages.OTHERREVENUE, "IncomeTransactions", "أنواع الإيرادات"),
    CUSTOMER_DISCOUNT(Pages.CUSTOMERSDISCOUNT, "CustomerDiscountTransactions", "العميل"),
    SUPPLIER_DISCOUNT(Pages.SUPPLIERDISCOUNT, "SupplierDiscountTransactions", "المورد"),
    TRANSFER(Pages.TRANSACTIONS, "TransfersAndSettlements", null);
    public final Pages route;
    public final String endpoint, partyLabel;

    FinancialMovementKind(Pages route, String endpoint, String partyLabel) {
        this.route = route;
        this.endpoint = endpoint;
        this.partyLabel = partyLabel;
    }

    public boolean discount() {
        return this == CUSTOMER_DISCOUNT || this == SUPPLIER_DISCOUNT;
    }
}
