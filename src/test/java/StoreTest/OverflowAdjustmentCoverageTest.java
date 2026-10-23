package StoreTest;
public final class OverflowAdjustmentCoverageTest extends AdjustmentCoverageScenario {
    protected String endpoint(){return "OverflowAdjustments";}
    protected String route(){return "overflow-adjustment";}
    protected String title(){return "تعديل تسوية فائض";}
    protected int stockDirection(){return 1;}
}
