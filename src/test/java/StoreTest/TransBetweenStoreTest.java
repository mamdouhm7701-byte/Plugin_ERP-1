/* شرح الملف: اختبارات التحويل بين المخازن: إنشاء تحويل بين المخازن باستخدام الباركود؛ تعديل عملية
 * تحويل بين المخازن؛ الحذف المعتمد يعكس أثر المستند مرة واحدة؛ نتأكد من عدم إضافة أصناف قبل اختيار
 * مخزن الوجهة؛ نتأكد من إلزامية اختيار مخزن الوجهة.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المخازن - TRANSFER")
public class TransBetweenStoreTest extends StoreScenarioTest {
    @Override
    protected StoreMovementKind kind() {
        return StoreMovementKind.TRANSFER;
    }

    @Override
    protected StoreModulePage movementPage() {
        return new TransBetweenStors(driver);
    }

    @Test(groups = "stores")
    @Story("إنشاء تحويل بين المخازن باختيار الأصناف من القائمة")
    public void TransBetweenStoreTest() {
        create("picker", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إنشاء تحويل بين المخازن باستخدام الباركود")
    public void TransBetweenStoreAddWWithBarcodeTest() {
        create("barcode", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إنشاء تحويل بين المخازن باستخدام اسم الصنف")
    @Description(
            "البحث بالاسم عن صنف متاح في مخزن المصدر المملوك غير الافتراضي، وليس صنفًا موجودًا بكاش المخزن الافتراضي.")
    public void TransBetweenStoreAddWithNameTest() {
        create("name", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("تعديل عملية تحويل بين المخازن")
    public void EditTransBetweenStore() {
        editOwn();
    }

    @Test(groups = "stores")
    @Story("حذف السند فقط مع التحقق من عكس الرصيد")
    public void DeleteAllTest() {
        deleteOwn();
    }

    @Test(groups = "stores")
    @Story("التحقق من إلزامية اختيار مخزن الوجهة")
    public void AssertToStoreRequired() {
        required("destination", "Agents", "مخزن");
    }

    @Test(groups = "stores")
    @Story("التحقق من عدم إضافة أصناف قبل اختيار مخزن الوجهة")
    public void AssertErorrWhenChosenOneStore() {
        pickerWithoutDestination();
    }

    @Test(groups = "stores")
    @Story("التحقق من عدم اختيار نفس المخزن كمصدر ووجهة")
    public void CantChosenSameStore() {
        sameStore();
    }

    @Test(groups = "stores")
    @Story("التحقق من عدم حفظ التحويل بدون أصناف")
    public void CantSaveWithOutChosenProduct() {
        noItems();
    }

    @Test(groups = "stores")
    @Story("حفظ الملاحظات العربية والإنجليزية كما هي")
    public void SM_NotesRoundTrip() {
        notesRoundTrip();
    }
}
