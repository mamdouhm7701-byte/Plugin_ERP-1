package Purches;
import io.qameta.allure.*;
import org.testng.annotations.Test;
@Epic("Web Cloud") @Feature("المشتريات - RETURN")
public final class PurchaseReturnCoverageTest extends PurchaseFormTest {
 protected PurchaseDocumentKind kind(){return PurchaseDocumentKind.RETURN;}
 @Test @Story("إعادة حساب المستند بعد خصم عشرة بالمائة")
 @Description("إعادة حساب المستند بعد خصم عشرة بالمائة، باستخدام بيانات يولدها الاختبار في الفرع الحالي، مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المملوكة للاختبار بعد انتهائها.")
 public void percentageDiscountRecalculates(){discount(true);}
 @Test @Story("إعادة حساب المستند بعد خصم بالقيمة")
 @Description("إعادة حساب المستند بعد خصم بالقيمة، باستخدام بيانات يولدها الاختبار في الفرع الحالي، مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المملوكة للاختبار بعد انتهائها.")
 public void fixedDiscountRecalculates(){discount(false);}
}
