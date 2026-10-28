/* شرح الملف: اختبار طاولات المطعم والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

public final class TablesCoverageTest extends MasterCoverageScenario {
    protected String endpoint() {
        return "Tables";
    }

    protected String route() {
        return "tables";
    }

    protected String title() {
        return "الطاولات";
    }
}
