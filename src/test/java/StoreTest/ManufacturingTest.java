package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المخازن - MANUFACTURING")
public class ManufacturingTest extends StoreScenarioTest {
    @Override
    protected StoreMovementKind kind() {
        return StoreMovementKind.MANUFACTURING;
    }

    @Override
    protected StoreModulePage movementPage() {
        return new ManufacturingPage(driver);
    }

    @Test(groups = "stores")
    @Story("إضافة إذن تصنيع باختيار الأصناف من القائمة")
    public void AddManufacturingTest() {
        create("picker", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إضافة إذن تصنيع باستخدام الباركود")
    public void AddManufacturingByBarcodeTest() {
        create("barcode", false, "Agents");
    }

    @Test(groups = "stores")
    @Story("إضافة إذن تصنيع باستخدام اسم الصنف")
    @Description(
            "البحث بالاسم عن صنف مصنع متاح في مخزن الاختبار غير الافتراضي، وليس صنفًا موجودًا بكاش المخزن الافتراضي.")
    public void AddManufacturingByNameTest() {
        create("name", false, "Agents");
    }

    @Test(groups = "stores")
    @Story("حذف السند فقط مع التحقق من عكس الرصيد")
    public void DeleteManufacturingByBarcodeTest() {
        deleteOwn();
    }

    @Test(groups = "stores")
    @Story("التحقق من إلزامية اختيار المخزن")
    public void AssertStoreRequired() {
        required("store", "Agents", "مخزن");
    }

    @Test(groups = "stores")
    @Story("التحقق من ظهور رسالة خطأ عند إضافة أصناف بدون اختيار مخزن")
    public void AssertErorrPopUpWhenAddProuctWithOutStore() {
        pickerWithoutStore();
    }

    @Test(groups = "stores")
    @Story("التحقق من ظهور رسالة خطأ عند تجاوز الرصيد المتاح")
    public void AssertStorePopUpWhenAddProuctWithOutBalancee() {
        rejectQuantity("600");
    }

    @Test(groups = "stores")
    @Story("تعديل إذن تصنيع والتحقق من رصيد المكونات")
    public void SM_EditManufacturing() {
        editOwn();
    }
}
