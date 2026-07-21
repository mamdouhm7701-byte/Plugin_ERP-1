package Plugin_ERP.Enums;

import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;

public enum Pages {

    AGENTS("/#/agents"),
    AREA("/#/areas"),
    BANK("/#/banks"),
    SAFES("/#/safes"),
    DISTRIBUTION("/#/exchange-destinations"),
    EXPENSES("/#/expense-type"),
    PAYMENT("/#/payment-methods"),
    REVENUES("/#/income-type"),
    WAREHOUSES("/#/warehouses"),
    ROUTELINESTATUES("/#/agent-itineraries-status"),
    SAFETRANSACTIONS("/#/safe-transaction/"),
    DISCONTTAYPES("/#/discount-types/"),
    ADDITEM("/#/Items/"),
    CATEGORIES("/#/items-category"),
    PRICELIST("/#/price-list/"),
    UNITS("/#/units/"),
    CUSTOMERSDISCOUNT("/#/customer-discount/"),
    COLLICTION("/#/collection-transactions/"),
    EXPENSE("/#/expense-transactions/"),
    OTHERREVENUE("/#/income-transactions/"),
    SUPPLIERBILLS("/#/payment-transactions/"),
    SUPPLIERDISCOUNT("/#/supplier-discount/"),
    TRANSACTIONS("/#/transfers-and-settlements/"),
    CUSTOMERCLASS("/#/dealersClassification/"),
    CUSTOMERS("/#/customers/"),
    MAINDEALERS("/#/mainDealer/"),
    SUPPLIERS("/#/suppliers/"),
    VOUCHERS("/#/vouchers/"),
    EXCHANGES("/#/exchange-order/new"),
    ADDITION("/#/addition-order/new"),
    ORDERS("/#/orders/"),
    CRM("/invoices"),
    MANUFACTURING("/manufacturing#/manufacturing-order/new")
;

    private final String route;

    Pages(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}