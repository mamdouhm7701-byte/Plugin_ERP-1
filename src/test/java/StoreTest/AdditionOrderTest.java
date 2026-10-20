package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المخازن - ADDITION")
public class AdditionOrderTest extends StoreScenarioTest {
    @Override
    protected StoreMovementKind kind() {
        return StoreMovementKind.ADDITION;
    }

    @Override
    protected StoreModulePage movementPage() {
        return new AdditionOrderPage(driver);
    }

    @Test(groups = "stores")
    @Story("إضافة إذن استلام باختيار الأصناف من القائمة")
    public void AdditionOrderTest() {
        create("picker", true, "Suppliers");
    }

    @Test(groups = "stores")
    @Story("إضافة إذن استلام عن طريق البحث بالاسم")
    public void AddAdditionOrderTestByName() {
        create("name", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إضافة إذن الاستلام عن طريق البحث بالباركود")
    public void AddAdditionByBarcode() {
        create("barcode", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("تعديل إذن استلام موجود")
    public void EditAddition() {
        editOwn();
    }

    @Test(groups = "stores")
    @Story("حذف سند الاختبار فقط مع التحقق من عكس الرصيد")
    public void DeleteAddition() {
        deleteOwn();
    }

    @Test(groups = "stores")
    @Story("التحقق - نوع البند إلزامي في إذن الاستلام")
    public void AssertItemTypeRequired() {
        required("type", "Agents", "نوع البند");
    }

    @Test(groups = "stores")
    @Story("التحقق - المورد إلزامي عند اختيار نوع البند (موردين)")
    public void AssertCustomerRequired() {
        required("party", "Suppliers", "الموردين");
    }

    @Test(groups = "stores")
    @Story("التحقق - المندوب إلزامي عند اختيار نوع البند (مندوبين)")
    public void AssertAgentRequired() {
        required("party", "Agents", "المندوبين");
    }

    @Test(groups = "stores")
    @Story("التحقق - جهة الصرف إلزامية عند اختيار نوع البند (جهات الصرف)")
    public void AssertDistributionEntityRequired() {
        required("party", "ExchangeDestinations", "جهات الصرف");
    }

    @Test(groups = "stores")
    @Story("التحقق - خطأ عند حفظ إذن استلام بدون أصناف")
    public void AssertErrorWhenSavingWithoutProducts() {
        noItems();
    }

    @Test(groups = "stores")
    @Story("التحقق - خطأ عند حفظ إذن استلام بتاريخ قديم")
    public void AddOldDate() {
        oldDate();
    }

    @Test(groups = "stores")
    @Story("حفظ الملاحظات العربية والإنجليزية كما هي")
    public void SM_NotesRoundTrip() {
        notesRoundTrip();
    }

    @Test(groups = "stores")
    @Story("هوية المتعامل المختار دون عميل افتراضي")
    public void SM_PrimaryPartyIdentity() {
        partyIdentity("Suppliers");
    }

    @Test(groups = "stores")
    @Story("سند المندوب لا يرتبط بعميل افتراضي")
    public void SM_AgentIdentity() {
        partyIdentity("Agents");
    }

    @Test(groups = "stores")
    @Story("سند جهة الصرف لا يرتبط بمتعامل افتراضي")
    public void SM_DestinationIdentity() {
        partyIdentity("ExchangeDestinations");
    }
}
