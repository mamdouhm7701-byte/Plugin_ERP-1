package StoreTest;

public final class StocktakingCoverageTest extends InventoryCoverageScenario {
    protected String endpoint() {
        return "Stocktakings";
    }

    protected String route() {
        return "inventory";
    }

    protected String title() {
        return "انشاء محضر جديد";
    }

    protected int stockDirection() {
        return 0;
    }
}
