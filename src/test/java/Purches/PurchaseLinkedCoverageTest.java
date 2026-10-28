/* شرح الملف: اختبارات رفض تجاوز كمية فاتورة الشراء: رفض تجاوز كمية فاتورة الشراء؛ تحويل أمر الشراء
 * إلى فاتورة دون تكرار أثر المخزون؛ مرتجع مشتريات جزئي من ثلاثة لوحدتين.
 */
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
    @Story("مرتجع مشتريات جزئي يحافظ على كمية الفاتورة الأصلية")
    @Description(
            "فاتورة بكمية٣ ثم مرتجع مرتبط بكمية٢؛ التحقق من قيمة المرتجع٢٠٠ وأثر المخزون"
                    + " ورصيد المورد ورابط المصدر، مع بقاء كمية وقيمة الفاتورة الأصلية دون تغيير.")
    public void linkedReturnKeepsOriginalQuantity() {
        linkedReturn();
    }

    @Test
    @Story("منع مرتجع مشتريات مرتبط يتجاوز كمية المصدر")
    public void linkedReturnCannotExceedSourceQuantity() {
        var invoice = prepareLinkedReturn();
        page.puQuantity(item.name(), "4");
        rejectSave();
        sameNumber(firstLine(data.read(invoice)).get("quantity"), "3");
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
