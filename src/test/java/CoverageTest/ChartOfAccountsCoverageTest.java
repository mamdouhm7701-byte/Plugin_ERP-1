package CoverageTest;

public final class ChartOfAccountsCoverageTest extends MasterCoverageScenario {
    protected String endpoint() {
        return "Accounts";
    }

    protected String route() {
        return "chart-of-account";
    }

    protected String title() {
        return "دليل الحسابات";
    }
}
