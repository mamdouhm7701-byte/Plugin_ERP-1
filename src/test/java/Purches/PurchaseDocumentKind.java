package Purches;

enum PurchaseDocumentKind {
    INVOICE("purch-invoice", "Purchases", "Purch"),
    ORDER("purchOrder", "PurchaseOrders", "PurchOrder"),
    RETURN("return-purch", "ReturnPurchases", "ReturnPurch");
    final String route, endpoint, type;

    PurchaseDocumentKind(String route, String endpoint, String type) {
        this.route = route;
        this.endpoint = endpoint;
        this.type = type;
    }
}
