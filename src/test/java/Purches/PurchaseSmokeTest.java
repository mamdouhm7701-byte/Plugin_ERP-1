package Purches;
import io.qameta.allure.Story;
import org.testng.annotations.Test;
public final class PurchaseSmokeTest extends PurchaseScenarioTest {
 protected PurchaseDocumentKind kind(){return PurchaseDocumentKind.INVOICE;}
 @Test @Story("حفظ فاتورة مشتريات آجلة ببيانات مستقلة والتحقق من زيادة المخزون ورصيد المورد")
 public void savesOwnedCreditPurchase(){create("picker","Credit",false);}
}
