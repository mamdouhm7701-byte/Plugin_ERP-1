/* شرح الملف: ترتيب الحالات في التقرير حسب القسم والشاشة.
 */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.TestResult;
import java.lang.reflect.Method;
import java.util.Map;
import org.testng.annotations.Test;

public final class ScenarioReportCatalog {
    private ScenarioReportCatalog() {}

    public record Entry(String module, String screen) {}

    private static final Map<String, Entry> SCREENS =
            Map.ofEntries(
                    Map.entry(
                            "Invoice.SalesInvoiceTaxReviewTest",
                            new Entry("مراجعة الفواتير", "فاتورة المبيعات")),
                    Map.entry(
                            "Purches.PurchaseInvoiceTaxReviewTest",
                            new Entry("مراجعة الفواتير", "فاتورة المشتريات")),
                    Map.entry(
                            "SpecialSalesTest.PosInvoiceTaxReviewTest",
                            new Entry("مراجعة الفواتير", "فاتورة نقاط البيع")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantInvoiceTaxReviewTest",
                            new Entry("مراجعة الفواتير", "فاتورة المطعم")),
                    Map.entry(
                            "Invoice.CarShowroomSalesTest",
                            new Entry("اختبارات النشاط", "بيع السيارات")),
                    Map.entry(
                            "Invoice.SalesFinalRoundingTest",
                            new Entry("اختبارات النشاط", "فاتورة المبيعات")),
                    Map.entry(
                            "Invoice.StocktakingAccountingTest",
                            new Entry("اختبارات النشاط", "الترحيل للحسابات")),
                    Map.entry(
                            "Invoice.SelectedBranchSalesPolicyTest",
                            new Entry("اختبارات النشاط", "فاتورة المبيعات")),
                    Map.entry(
                            "Invoice.ConstructionContractTest",
                            new Entry("اختبارات النشاط", "العقود")),
                    Map.entry(
                            "Invoice.ConstructionContractConversionTest",
                            new Entry("اختبارات النشاط", "العقود")),
                    Map.entry(
                            "Invoice.ConcreteStockJourneyTest",
                            new Entry("اختبارات النشاط", "الخرسانة")),
                    Map.entry(
                            "Invoice.ConcreteReservationReportTest",
                            new Entry("اختبارات النشاط", "تقارير الخرسانة")),
                    Map.entry(
                            "Invoice.ConcreteOperationalReportTest",
                            new Entry("اختبارات النشاط", "تقارير الخرسانة")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantAdditionalTaxBoundaryTest",
                            new Entry("المطاعم", "فاتورة المطعم")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantAdditionalTaxOrderTest",
                            new Entry("المطاعم", "فاتورة المطعم")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantAdditionalTaxReturnTest",
                            new Entry("المطاعم", "مرتجع المطعم")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest",
                            new Entry("المطاعم", "فاتورة المطعم")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest",
                            new Entry("المطاعم", "فاتورة المطعم")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest",
                            new Entry("المطاعم", "فاتورة المطعم")),
                    Map.entry(
                            "SpecialSalesTest.SelectedRestaurantPolicyTest",
                            new Entry("المطاعم", "فاتورة المطعم")),
                    Map.entry(
                            "CoverageTest.AccountGroupsCoverageTest",
                            new Entry("الحسابات العامة", "مجموعات الحسابات")),
                    Map.entry(
                            "CoverageTest.ChartOfAccountsCoverageTest",
                            new Entry("الحسابات العامة", "دليل الحسابات")),
                    Map.entry(
                            "CoverageTest.CostCentersCoverageTest",
                            new Entry("الحسابات العامة", "مراكز التكلفة")),
                    Map.entry(
                            "CoverageTest.PermissionsCoverageTest",
                            new Entry("الإعدادات والصلاحيات", "الصلاحيات وحماية الوصول")),
                    Map.entry(
                            "CoverageTest.TablesCoverageTest",
                            new Entry("نقاط البيع والمطعم", "الطاولات")),
                    Map.entry(
                            "CoverageTest.UsersCoverageTest",
                            new Entry("الإعدادات والصلاحيات", "المستخدمين")),
                    Map.entry(
                            "CoverageTest.ZatcaConfigurationCoverageTest",
                            new Entry("الإعدادات والصلاحيات", "الربط مع هيئة الزكاة")),
                    Map.entry("DealerModelTest.AgentTest", new Entry("المتعاملون", "المناديب")),
                    Map.entry(
                            "DealerModelTest.CustomerClassificationTest",
                            new Entry("المتعاملون", "تصنيف المتعاملين")),
                    Map.entry("DealerModelTest.CustomersTest", new Entry("المتعاملون", "العملاء")),
                    Map.entry(
                            "DealerModelTest.MainDealerTest",
                            new Entry("المتعاملون", "المتعامل الرئيسي")),
                    Map.entry("DealerModelTest.RoutLineTest", new Entry("المتعاملون", "خط السير")),
                    Map.entry("DealerModelTest.SupplierTest", new Entry("المتعاملون", "الموردين")),
                    Map.entry(
                            "FinancialMovementsTest.CustomerDiscountNoticeTest",
                            new Entry("الحركات المالية", "اشعار خصم عميل")),
                    Map.entry(
                            "FinancialMovementsTest.CustomerReceiptTest",
                            new Entry("الحركات المالية", "سند قبض عميل")),
                    Map.entry(
                            "FinancialMovementsTest.ExpenseMovementTest",
                            new Entry("الحركات المالية", "حركة المصروفات")),
                    Map.entry(
                            "FinancialMovementsTest.OtherRevenueMovementTest",
                            new Entry("الحركات المالية", "حركة الايرادات")),
                    Map.entry(
                            "FinancialMovementsTest.SupplierBillOfExchangeTest",
                            new Entry("الحركات المالية", "سند صرف مورد")),
                    Map.entry(
                            "FinancialMovementsTest.SupplierDiscountNoticeTest",
                            new Entry("الحركات المالية", "اشعار خصم مورد")),
                    Map.entry(
                            "FinancialMovementsTest.TransfersAndSettlementsTest",
                            new Entry("الحركات المالية", "تحويلات وتسويات")),
                    Map.entry(
                            "FinancialMovementsTest.TreasuryLedgerCoverageTest",
                            new Entry("الحركات المالية", "حركة الخزينة والأرصدة")),
                    Map.entry(
                            "GenralAccountantTest.VouchersTest",
                            new Entry("الحسابات العامة", "قيود اليومية")),
                    Map.entry(
                            "Invoice.ActionsCoverageTest",
                            new Entry("المبيعات", "إجراءات المبيعات")),
                    Map.entry(
                            "Invoice.LinkedSalesReturnTest",
                            new Entry("المبيعات", "مرتجع المبيعات")),
                    Map.entry(
                            "Invoice.PostingCoverageTest",
                            new Entry("المبيعات", "الترحيل إلى الحسابات")),
                    Map.entry("Invoice.QuotationTest", new Entry("المبيعات", "عرض السعر")),
                    Map.entry(
                            "Invoice.SalesEndToEndTest",
                            new Entry("المبيعات", "رحلة مبيعات بين شاشات متعددة")),
                    Map.entry(
                            "Invoice.SalesInvoiceCoverageTest",
                            new Entry("المبيعات", "فاتورة المبيعات")),
                    Map.entry(
                            "Invoice.SalesListCoverageTest",
                            new Entry("المبيعات", "استعراض المبيعات")),
                    Map.entry("Invoice.SalesOrderTest", new Entry("المبيعات", "أمر البيع")),
                    Map.entry("Invoice.SalesReturnTest", new Entry("المبيعات", "مرتجع المبيعات")),
                    Map.entry(
                            "Invoice.SalesSettlementEndToEndTest",
                            new Entry("المبيعات", "رحلة تسوية المبيعات بين شاشات متعددة")),
                    Map.entry(
                            "Invoice.UnderReviewCoverageTest",
                            new Entry("المبيعات", "فاتورة تحت الإجراء")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.AreaTest",
                            new Entry("البيانات الأساسية", "المناطق")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.BankTest",
                            new Entry("البيانات الأساسية", "البنوك")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.CashTest",
                            new Entry("البيانات الأساسية", "الخزن")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.DisEntTest",
                            new Entry("البيانات الأساسية", "جهات الصرف")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.ExpensesTest",
                            new Entry("البيانات الأساسية", "أنواع المصروفات")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.PaymentTest",
                            new Entry("البيانات الأساسية", "طرق الدفع")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.RevenuesTest",
                            new Entry("البيانات الأساسية", "أنواع الإيرادات")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.RoutLineTest",
                            new Entry("البيانات الأساسية", "حالات خط السير")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.StoreTest",
                            new Entry("البيانات الأساسية", "قائمة المخازن")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.TaypeOfDiscountTest",
                            new Entry("البيانات الأساسية", "أنواع الخصومات")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.BasicDataTest.TreasuryHandoverTest",
                            new Entry("البيانات الأساسية", "إستلام الخزينة")),
                    Map.entry(
                            "Plugin_ERP.Tests.LoginTest.LoginTest",
                            new Entry("الدخول والصلاحيات", "تسجيل الدخول")),
                    Map.entry("ProductsTest.AddProductsTest", new Entry("الأصناف", "الأصناف")),
                    Map.entry(
                            "ProductsTest.ProductCategoriesTest",
                            new Entry("الأصناف", "فئات الأصناف")),
                    Map.entry(
                            "ProductsTest.ProductListTest", new Entry("الأصناف", "قوائم الأسعار")),
                    Map.entry("ProductsTest.UnitTest", new Entry("الأصناف", "الوحدات")),
                    Map.entry(
                            "Purches.PurchaseEndToEndTest",
                            new Entry("المشتريات", "رحلة مشتريات بين شاشات متعددة")),
                    Map.entry(
                            "Purches.PurchaseInvoiceCoverageTest",
                            new Entry("المشتريات", "فاتورة المشتريات")),
                    Map.entry(
                            "Purches.PurchaseLinkedCoverageTest",
                            new Entry("المشتريات", "مرتجع المشتريات")),
                    Map.entry(
                            "Purches.PurchaseListCoverageTest",
                            new Entry("المشتريات", "استعراض المشتريات")),
                    Map.entry(
                            "Purches.PurchaseOrderCoverageTest",
                            new Entry("المشتريات", "أمر الشراء")),
                    Map.entry(
                            "Purches.PurchaseReturnCoverageTest",
                            new Entry("المشتريات", "مرتجع المشتريات")),
                    Map.entry(
                            "Purches.PurchaseSettlementEndToEndTest",
                            new Entry("المشتريات", "رحلة تسوية المشتريات بين شاشات متعددة")),
                    Map.entry(
                            "Purches.PurchaseSmokeTest",
                            new Entry("المشتريات", "فاتورة المشتريات")),
                    Map.entry(
                            "Purches.SupplyChainEndToEndTest",
                            new Entry("المشتريات", "رحلة سلسلة الإمداد بين شاشات متعددة")),
                    Map.entry(
                            "SpecialSalesTest.KitchenOrdersCoverageTest",
                            new Entry("نقاط البيع والمطعم", "طلبات المطبخ")),
                    Map.entry(
                            "SpecialSalesTest.PointOfSaleTest",
                            new Entry("نقاط البيع والمطعم", "فاتورة نقاط بيع")),
                    Map.entry(
                            "SpecialSalesTest.RestaurantInvoiceTest",
                            new Entry("نقاط البيع والمطعم", "فاتورة المطعم")),
                    Map.entry("StoreTest.AdditionOrderTest", new Entry("المخازن", "إذن الإضافة")),
                    Map.entry(
                            "StoreTest.DamageAdjustmentTest", new Entry("المخازن", "تسوية التالف")),
                    Map.entry("StoreTest.ExchangeOrderTest", new Entry("المخازن", "إذن الصرف")),
                    Map.entry(
                            "StoreTest.ManufacturingSalesEndToEndTest",
                            new Entry("المخازن", "رحلة تصنيع وبيع بين شاشات متعددة")),
                    Map.entry("StoreTest.ManufacturingTest", new Entry("المخازن", "أمر التصنيع")),
                    Map.entry(
                            "StoreTest.OverflowAdjustmentCoverageTest",
                            new Entry("المخازن", "تسوية الفائض")),
                    Map.entry(
                            "StoreTest.ShortageAdjustmentCoverageTest",
                            new Entry("المخازن", "تسوية العجز")),
                    Map.entry(
                            "StoreTest.StocktakingCoverageTest",
                            new Entry("المخازن", "محضر الجرد")),
                    Map.entry(
                            "StoreTest.TransBetweenStoreTest",
                            new Entry("المخازن", "التحويل بين المخازن")),
                    Map.entry(
                            "StoreTest.WarehouseEndToEndTest",
                            new Entry("المخازن", "رحلة مخازن بين شاشات متعددة")));

    /** Returns null for local checks and classes outside the live business catalog. */
    public static Entry screenFor(String className) {
        return className == null ? null : SCREENS.get(className);
    }

    public static void apply(TestResult test, String className, Method method) {
        Entry entry = screenFor(className);
        if (entry == null) return;
        label(test, "parentSuite", entry.module());
        label(test, "suite", entry.screen());
        label(test, "subSuite", "حالات الاختبار");
        label(test, "package", entry.module() + "." + entry.screen());
        label(test, "javaClass", className);
        if (method == null) return;
        Story story = method.getAnnotation(Story.class);
        Test annotation = method.getAnnotation(Test.class);
        String name = story == null ? "" : compact(story.value());
        if (name.isBlank() && annotation != null) name = compact(annotation.description());
        if (name.isBlank()) name = humanize(method.getName());
        String plainTitle = ScenarioCaseDescriptions.title(className, method.getName());
        if (plainTitle != null && !plainTitle.isBlank()) name = plainTitle;
        test.setName(shorten(name, 160));
        label(test, "javaMethod", method.getName());
        label(test, "testMethod", test.getName());
        Description description = method.getAnnotation(Description.class);
        if (description != null && !compact(description.value()).isBlank())
            test.setDescription(shorten(compact(description.value()), 600));
        String explanation = ScenarioCaseDescriptions.html(className, method.getName());
        if (explanation != null) test.setDescriptionHtml(explanation);
    }

    private static void label(TestResult test, String name, String value) {
        test.getLabels().removeIf(label -> name.equals(label.getName()));
        test.getLabels().add(new Label().setName(name).setValue(value));
    }

    private static String compact(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private static String humanize(String name) {
        return compact(name.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replaceAll("[_-]+", " "));
    }

    private static String shorten(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max - 1).stripTrailing() + "…";
    }
}
