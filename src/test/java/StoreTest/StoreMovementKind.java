package StoreTest;

enum StoreMovementKind {
    EXCHANGE("exchange-order", "orders", "ItemTransactions-ExchangeOrder", "ExchangeOrder"),
    ADDITION("addition-order", "orders", "AdditionOrders", "AdditionOrder"),
    MANUFACTURING("manufacturing-order", "orders", "ManufacturingOrders", "Manufacturing"),
    TRANSFER(
            "storage-transfer",
            "storage-transfer",
            "ItemTransactions-storageTransfer",
            "StorageTransfer"),
    DAMAGE("damage-adjustment", "damage-adjustment", "DamageAdjustments", "DamageAdjustment");
    final String route, list, endpoint, type;

    StoreMovementKind(String route, String list, String endpoint, String type) {
        this.route = route;
        this.list = list;
        this.endpoint = endpoint;
        this.type = type;
    }

    boolean party() {
        return this == EXCHANGE || this == ADDITION;
    }
}
