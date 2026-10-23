package Purches;

import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("تحويل مستندات المشتريات")
public final class PurchaseLinkedCoverageTest extends PurchaseFormActions {
    protected PurchaseDocumentKind kind() {
        return PurchaseDocumentKind.INVOICE;
    }

    @Test
    @Story("تحويل فاتورة الاختبار إلى مرتجع مرتبط يمنع تسجيل كمية مختلفة عن المصدر")
    @Description(
            "إنشاء فاتورة ثم تحويل صفها المحدد بالكود إلى مرتجع؛ إذا كان حقل الكمية"
                    + " مفتوحاً نحاول تغييره ونتحقق من رفض حفظ التغيير، ثم نتحقق من حفظ الكمية الأصلية"
                    + " ورابط المصدر وأثر المخزون، مع حذف المرتجع قبل المصدر.")
    public void linkedReturnKeepsOriginalQuantity() {
        linkedReturn();
    }

    @Test
    @Story("تحويل أمر الشراء إلى فاتورة دون تكرار أثر المخزون")
    @Description(
            "إنشاء أمر شراء لا يؤثر على المخزون، وتحويل صفه وحده إلى فاتورة والتحقق من رابط"
                    + " المصدر وزيادة المخزون عند حفظ الفاتورة فقط.")
    public void ownedOrderConvertsToPurchase() {
        convertOrder();
    }
}
