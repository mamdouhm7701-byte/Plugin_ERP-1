package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المخازن - DAMAGE")
public class DamageAdjustmentTest extends StoreScenarioTest {
    @Override
    protected StoreMovementKind kind() {
        return StoreMovementKind.DAMAGE;
    }

    @Override
    protected StoreModulePage movementPage() {
        return new DamageAdjustmentPage(driver);
    }

    @Test(groups = "stores")
    @Story("إضافة تسوية توالف باختيار الأصناف من القائمة")
    public void AddDamageAdjustmentTest() {
        create("picker", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إضافة تسوية توالف باستخدام اسم الصنف")
    public void AddDamageAdjustmentByNameTest() {
        create("name", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إضافة تسوية توالف باستخدام الباركود")
    public void AddDamageAdjustmentByBarcodeTest() {
        create("barcode", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("تعديل تسوية توالف")
    public void EditAddDamageAdjustment() {
        editOwn();
    }

    @Test(groups = "stores")
    @Story("حذف السند فقط مع التحقق من عكس الرصيد")
    public void DeleteDamageAdjustment() {
        deleteOwn();
    }

    @Test(groups = "stores")
    @Story("التحقق من عدم السماح بالحفظ بتاريخ قديم")
    public void AddOldDateTest() {
        oldDate();
    }

    @Test(groups = "stores")
    @Story("التحقق من عدم إضافة صنف بدون كمية")
    public void AddProductWithoutQuantityTest() {
        rejectQuantity("0");
    }

    @Test(groups = "stores")
    @Story("حفظ الملاحظات العربية والإنجليزية كما هي")
    public void SM_NotesRoundTrip() {
        notesRoundTrip();
    }
}
