package Invoice;
public final class QuotationTest extends SalesFormTest {
  @org.testng.annotations.Test @io.qameta.allure.Story("تطبيق قائمة أسعار أنشأها الاختبار على عرض السعر") @Override public void ownedPriceListIsApplied(){super.ownedPriceListIsApplied();}
  @Override protected SalesDocumentKind kind(){return SalesDocumentKind.QUOTATION;}
}
