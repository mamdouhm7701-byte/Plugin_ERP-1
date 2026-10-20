package Purches;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;
import java.util.*;
@Epic("Web Cloud") @Feature("قائمة المشتريات")
public final class PurchaseListCoverageTest extends PurchaseScenarioTest {
 protected PurchaseDocumentKind kind(){return PurchaseDocumentKind.INVOICE;}
 private String date;
 @BeforeMethod(alwaysRun=true) public void resetPurchaseListDate(){date=null;}
 private PurchaseTestData.Record seed(int index){if(date==null)date=page.puListFixtureDate();return data.seedPurchase(index,"Purchases",date,"1","0");}
 private void scope(PurchaseTestData.Record... docs){page.puListScope(data.supplier().name());page.puWaitListIds(new HashSet<>(Arrays.stream(docs).map(PurchaseTestData.Record::id).toList()));}
 @Test @Story("فلتر المورد يعرض مستندات مورد الاختبار فقط")
 public void supplierFilterIsOwned(){var a=seed(1);var b=seed(2);scope(a,b);Assert.assertEquals(page.puListRows().size(),2);}
 @Test @Story("البحث بالكود الكامل يعرض الفاتورة المطابقة فقط")
 public void exactCodeFilter(){var a=seed(1);var b=seed(2);scope(a,b);page.puListText("filteredCode",data.read(a).get("code").toString());page.puWaitListIds(Set.of(a.id()));}
 @Test @Story("البحث بالمرجع يعرض فاتورة الاختبار المطابقة فقط")
 public void referenceFilter(){var a=seed(1);var b=seed(2);scope(a,b);page.puListText("reference",a.values().get("referenceNumber").toString());page.puWaitListIds(Set.of(a.id()));}
 @Test @Story("البحث بكود غير موجود يمسح النتائج السابقة")
 public void unmatchedCodeClearsRows(){var a=seed(1);scope(a);page.puListText("filteredCode","QA-PU-NONE-"+UUID.randomUUID());page.puWaitListIds(Set.of());}
 @Test @Story("مسح فلتر الكود يعيد جميع مستندات مورد الاختبار")
 public void clearCodeRestoresSupplierScope(){var a=seed(1);var b=seed(2);scope(a,b);page.puListText("filteredCode",data.read(a).get("code").toString());page.puWaitListIds(Set.of(a.id()));page.puListText("filteredCode","");page.puWaitListIds(Set.of(a.id(),b.id()));}
 @DataProvider public Object[][] sizes(){return new Object[][]{{25},{50},{100},{500}};}
 @Test(dataProvider="sizes") @Story("تغيير حجم الصفحة يحافظ على فواتير مورد الاختبار لكل حجم مدعوم")
 public void pageSizePreservesResults(int size){var a=seed(1);var b=seed(2);scope(a,b);page.puPageSize(size);page.puWaitListIds(Set.of(a.id(),b.id()));}
 @Test @Story("الترتيب تصاعدياً وتنازلياً يحافظ على مستندات مورد الاختبار")
 public void sortKeepsOwnedDocuments(){var a=seed(1);var b=seed(2);scope(a,b);page.puSort("الكود");page.puWaitListIds(Set.of(a.id(),b.id()));page.puSort("الكود");page.puWaitListIds(Set.of(a.id(),b.id()));}
 @Test @Story("دمج فلاتر الكود والمرجع المتعارضة يعرض قائمة فارغة")
 public void conflictingFiltersAreEmpty(){var a=seed(1);var b=seed(2);scope(a,b);page.puListText("filteredCode",data.read(a).get("code").toString());page.puListText("reference",b.values().get("referenceNumber").toString());page.puWaitListIds(Set.of());}
 @Test @Story("نطاق تاريخ يوم واحد يعرض فاتورة الاختبار ثم نطاق خال يمسحها")
 public void dateRangeDoesNotLeaveStaleRows(){var a=seed(1);scope(a);page.puListText("fromDate",date);page.puListText("toDate",date);page.puWaitListIds(Set.of(a.id()));String before=java.time.LocalDate.parse(date).minusDays(1).toString();page.puListText("fromDate",before);page.puListText("toDate",before);page.puWaitListIds(Set.of());}
 @Test @Story("تصدير فواتير مورد الاختبار إلى ملف PDF فعلي")
 public void exportsPdf(){export("pdf");}
 @Test @Story("تصدير فواتير مورد الاختبار إلى ملف Excel فعلي")
 public void exportsExcel(){export("excel");}
 private void export(String format){var a=seed(1);scope(a);PurchaseExportAssertions.assertExportDownloaded(driver,()->page.puExport(format),format);}
}
