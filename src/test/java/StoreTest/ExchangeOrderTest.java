package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المخازن - EXCHANGE")
public class ExchangeOrderTest extends StoreScenarioTest {
    @Override
    protected StoreMovementKind kind() {
        return StoreMovementKind.EXCHANGE;
    }

    @Override
    protected StoreModulePage movementPage() {
        return new ExchangeOrderPage(driver);
    }

    @Test(groups = "stores")
    @Story("إضافة إذن صرف باختيار الأصناف من القائمة")
    public void AddExchangeWithItemsList() {
        create("picker", true, "Customers");
    }

    @Test(groups = "stores")
    @Story("إضافة إذن صرف عن طريق البحث بالاسم")
    public void AddExchangeByName() {
        create("name", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("إضافة إذن صرف عن طريق البحث بالباركود")
    public void AddExchangeByBarcode() {
        create("barcode", true, "Agents");
    }

    @Test(groups = "stores")
    @Story("تعديل إذن صرف موجود")
    public void EditExchange() {
        editOwn();
    }

    @Test(groups = "stores")
    @Story("حذف سند الاختبار فقط مع التحقق من عكس الرصيد")
    public void DeleteExchange() {
        deleteOwn();
    }

    @Test(groups = "stores")
    @Story("التحقق - نوع البند إلزامي في إذن الصرف")
    public void AssertItemTypeRequired() {
        required("type", "Agents", "نوع البند");
    }

    @Test(groups = "stores")
    @Story("التحقق - العميل إلزامي عند اختيار نوع البند (عملاء)")
    public void AssertCustomerRequired() {
        required("party", "Customers", "العملاء");
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
    @Story("التحقق - خطأ عند حفظ إذن صرف بدون أصناف")
    public void AssertErrorWhenSavingWithoutProducts() {
        noItems();
    }

    @Test(groups = "stores")
    @Story("التحقق - خطأ عند حفظ إذن الصرف بتاريخ قديم")
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
        partyIdentity("Customers");
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
