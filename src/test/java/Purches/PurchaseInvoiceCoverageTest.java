/* شرح الملف: اختبارات فاتورة المشتريات: إعادة حساب المستند بعد خصم بالقيمة؛ إعادة حساب المستند بعد
 * خصم عشرة بالمائة.
 */
package Purches;

import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المشتريات - INVOICE")
public final class PurchaseInvoiceCoverageTest extends PurchaseFormTest {
    protected PurchaseDocumentKind kind() {
        return PurchaseDocumentKind.INVOICE;
    }

    @Test
    @Story("إعادة حساب المستند بعد خصم عشرة بالمائة")
    @Description(
            "تطبيق خصم عشرة بالمائة والتحقق من صافي الشاشة؛ التحقق من الحفظ الصحيح أو الرفض"
                    + " الصريح لصلاحية الخصم دون أثر على المخزون أو المورد، ومطابقة حد المستخدم إذا كان"
                    + " معروضاً.")
    public void percentageDiscountRecalculates() {
        discount(true);
    }

    @Test
    @Story("إعادة حساب المستند بعد خصم بالقيمة")
    @Description(
            "تطبيق خصم بقيمة خمسة عشر على إجمالي ثلاثمائة والتحقق من الصافي؛ التحقق من الحفظ"
                    + " الصحيح أو الرفض الصريح لصلاحية الخصم دون أثر على المخزون أو المورد، ومطابقة حد"
                    + " المستخدم إذا كان معروضاً.")
    public void fixedDiscountRecalculates() {
        discount(false);
    }
}
