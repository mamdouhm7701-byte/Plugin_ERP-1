/* شرح الملف: اختبار تسوية العجز والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package StoreTest;

public final class ShortageAdjustmentCoverageTest extends AdjustmentCoverageScenario {
    protected String endpoint() {
        return "ShortageAdjustments";
    }

    protected String route() {
        return "shortage-adjustment";
    }

    protected String title() {
        return "تعديل تسوية العجز";
    }

    protected int stockDirection() {
        return -1;
    }
}
