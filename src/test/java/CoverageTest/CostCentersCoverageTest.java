/* شرح الملف: اختبار مراكز التكلفة والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

public final class CostCentersCoverageTest extends MasterCoverageScenario {
    protected String endpoint() {
        return "CostCenters";
    }

    protected String route() {
        return "cost-center";
    }

    protected String title() {
        return "مراكز التكلفة";
    }
}
