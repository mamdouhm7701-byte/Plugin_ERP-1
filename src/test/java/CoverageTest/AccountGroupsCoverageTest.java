/* شرح الملف: اختبار مجموعات الحسابات والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

public final class AccountGroupsCoverageTest extends MasterCoverageScenario {
    protected String endpoint() {
        return "AccountGroups";
    }

    protected String route() {
        return "accountGroups";
    }

    protected String title() {
        return "مجموعات الحسابات";
    }
}
