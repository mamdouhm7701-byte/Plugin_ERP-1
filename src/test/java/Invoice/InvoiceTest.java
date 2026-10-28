/* شرح الملف: اختبار فاتورة المبيعات والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package Invoice;

import io.qameta.allure.Story;
import org.testng.annotations.Test;

public class InvoiceTest extends SalesFormActions {
    @Test
    @Story("إنشاء فاتورة بيع آجلة بأصناف الاختبار")
    public void AddInvoice() {
        create("picker", "Credit", false);
    }

    @Test
    @Story("إنشاء فاتورة بيع نقدية بوسيلة دفع الاختبار")
    public void AddInvoiceCash() {
        create("picker", "Cash", false);
    }

    @Test
    @Story("إنشاء فاتورة بيع بإضافة الصنف بالاسم")
    public void AddPurchesBillsByNameTest() {
        create("name", "Credit", false);
    }

    @Test
    @Story("إنشاء فاتورة بيع بإضافة الصنف بالباركود")
    public void AddPurchesBillsByBarcodTest() {
        create("barcode", "Credit", false);
    }

    @Test
    @Story("حفظ فاتورة بيع بصنف مختار من القائمة")
    public void AddPurchesBillsWithSaveBuutonTest() {
        create("picker", "Credit", false);
    }

    @Test
    @Story("منع حفظ فاتورة البيع بدون أصناف")
    public void AddWithOutProduct() {
        emptyDocumentCannotSave();
    }
}
