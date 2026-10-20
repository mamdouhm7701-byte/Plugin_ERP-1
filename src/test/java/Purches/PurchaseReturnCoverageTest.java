package Purches;
import io.qameta.allure.*;
import org.testng.annotations.Test;
@Epic("Web Cloud") @Feature("المشتريات - RETURN")
public final class PurchaseReturnCoverageTest extends PurchaseFormTest {
 protected PurchaseDocumentKind kind(){return PurchaseDocumentKind.RETURN;}
 @Test @Story("إعادة حساب المستند بعد خصم عشرة بالمائة")
 @Description("تطبيق خصم عشرة بالمائة والتحقق من صافي المرتجع؛ التحقق من الحفظ الصحيح أو الرفض الصريح لصلاحية الخصم دون أثر على المخزون أو المورد، ومطابقة حد المستخدم إذا كان معروضاً.")
 public void percentageDiscountRecalculates(){discount(true);}
 @Test @Story("إعادة حساب المستند بعد خصم بالقيمة")
 @Description("تطبيق خصم بقيمة خمسة عشر على إجمالي ثلاثمائة والتحقق من الصافي؛ التحقق من الحفظ الصحيح أو الرفض الصريح لصلاحية الخصم دون أثر على المخزون أو المورد، ومطابقة حد المستخدم إذا كان معروضاً.")
 public void fixedDiscountRecalculates(){discount(false);}
}
