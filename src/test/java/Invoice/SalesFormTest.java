/* شرح الملف: اختبار إدخال فاتورة البيع والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package Invoice;

import io.qameta.allure.Story;
import org.testng.annotations.Test;

public abstract class SalesFormTest extends SalesFormActions {
    @Test
    @Story("حفظ مستند بالصنف المختار من قائمة الأصناف")
    @Override
    public void savesOwnedItemFromPicker() {
        super.savesOwnedItemFromPicker();
    }

    @Test
    @Story("حفظ مستند بإضافة الصنف بالباركود")
    @Override
    public void savesOwnedItemByBarcode() {
        super.savesOwnedItemByBarcode();
    }

    @Test
    @Story("حفظ مستند بإضافة الصنف بالاسم")
    @Override
    public void savesOwnedItemByName() {
        super.savesOwnedItemByName();
    }

    @Test
    @Story(
            "التحقق من الدفع النقدي بالبيانات المدخلة وأثره حسب نوع المستند وعدم تسجيل دفعات لعرض السعر")
    @Override
    public void cashDocumentUsesOwnedPayment() {
        super.cashDocumentUsesOwnedPayment();
    }

    @Test
    @Story("حفظ كمية كسرية والتحقق من بقائها دون تغيير")
    @Override
    public void fractionalQuantityPersists() {
        super.fractionalQuantityPersists();
    }

    @Test
    @Story("حفظ صنفين مختلفين والتحقق من بيانات كل بند")
    @Override
    public void twoOwnedItemsPersist() {
        super.twoOwnedItemsPersist();
    }

    @Test
    @Story("حذف بند قبل الحفظ والتحقق من عدم تخزينه")
    @Override
    public void removedItemIsNotSaved() {
        super.removedItemIsNotSaved();
    }

    @Test
    @Story("إعادة حساب صافي المستند بعد الخصم بالنسبة")
    @Override
    public void percentageDiscountRecalculates() {
        super.percentageDiscountRecalculates();
    }

    @Test
    @Story("إعادة حساب صافي المستند بعد الخصم بالقيمة")
    @Override
    public void fixedDiscountRecalculates() {
        super.fixedDiscountRecalculates();
    }

    @Test
    @Story("حفظ الملاحظات العربية والإنجليزية واسترجاعها كما هي")
    @Override
    public void unicodeNotesPersist() {
        super.unicodeNotesPersist();
    }

    @Test
    @Story("منع حفظ مستند بدون أصناف")
    @Override
    public void emptyDocumentCannotSave() {
        super.emptyDocumentCannotSave();
    }

    @Test
    @Story("منع حفظ بند بكمية صفرية")
    @Override
    public void zeroQuantityCannotSave() {
        super.zeroQuantityCannotSave();
    }

    @Test
    @Story("منع حفظ بند بكمية سالبة")
    @Override
    public void negativeQuantityCannotSave() {
        super.negativeQuantityCannotSave();
    }

    @Test
    @Story("منع الحفظ بدون اختيار العميل")
    @Override
    public void missingCustomerCannotSave() {
        super.missingCustomerCannotSave();
    }

    @Test
    @Story("منع الحفظ بدون اختيار المخزن")
    @Override
    public void missingStoreCannotSave() {
        super.missingStoreCannotSave();
    }

    @Test
    @Story("منع الحفظ بسعر سالب")
    @Override
    public void negativePriceCannotSave() {
        super.negativePriceCannotSave();
    }

    @Test
    @Story("التحقق من الحد الأقصى لطول الملاحظات")
    @Override
    public void notesRespectMaximumLength() {
        super.notesRespectMaximumLength();
    }

    @Test
    @Story("زر جديد يمسح البنود غير المحفوظة")
    @Override
    public void newClearsUnsavedLines() {
        super.newClearsUnsavedLines();
    }

    @Test
    @Story("منع حذف فاتورة البيع والسماح بحذف مستندات المبيعات الأخرى مع عكس الأثر")
    @Override
    public void savedDocumentDeletePolicy() {
        super.savedDocumentDeletePolicy();
    }

    @Test
    @Story("إعادة فتح المستند المحفوظ والتحقق من البيانات المدخلة")
    @Override
    public void savedDocumentReopensWithOwnedValues() {
        super.savedDocumentReopensWithOwnedValues();
    }

    @Test
    @Story("منع تعديل فاتورة البيع والسماح بتعديل مستندات المبيعات الأخرى")
    @Override
    public void savedDocumentEditPolicy() {
        super.savedDocumentEditPolicy();
    }
}
