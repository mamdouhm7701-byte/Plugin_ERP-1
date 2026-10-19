package Purches;
import io.qameta.allure.*;
import org.testng.annotations.Test;
@Epic("Web Cloud") @Feature("المشتريات - ORDER")
public final class PurchaseOrderCoverageTest extends PurchaseFormTest {
 protected PurchaseDocumentKind kind(){return PurchaseDocumentKind.ORDER;}
}
