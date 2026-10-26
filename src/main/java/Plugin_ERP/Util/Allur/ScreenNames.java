package Plugin_ERP.Util.Allur;

import io.qameta.allure.Allure;
import java.util.Map;

/** Business screen labels for navigation steps; routing itself is unchanged. */
public final class ScreenNames {
    private ScreenNames() {}

    private static final Map<String, String> LABELS =
            Map.ofEntries(
                    Map.entry("agents", "المندوبين"),
                    Map.entry("agentItinerary", "خط سير المندوب"),
                    Map.entry("areas", "المناطق"),
                    Map.entry("banks", "البنوك"),
                    Map.entry("safes", "الخزائن"),
                    Map.entry("exchange-destinations", "جهات الصرف"),
                    Map.entry("expense-type", "أنواع المصروفات"),
                    Map.entry("payment-methods", "طرق الدفع"),
                    Map.entry("income-type", "أنواع الإيرادات"),
                    Map.entry("warehouses", "المخازن"),
                    Map.entry("agent-itineraries-status", "حالات خط السير"),
                    Map.entry("safe-transaction", "تسليم الخزينة"),
                    Map.entry("discount-types", "أنواع الخصومات"),
                    Map.entry("items", "الأصناف"),
                    Map.entry("items-category", "فئات الأصناف"),
                    Map.entry("price-list", "قوائم الأسعار"),
                    Map.entry("units", "وحدات القياس"),
                    Map.entry("customer-discount", "إشعارات خصم العملاء"),
                    Map.entry("collection-transactions", "سندات القبض"),
                    Map.entry("expense-transactions", "سندات المصروفات"),
                    Map.entry("income-transactions", "سندات الإيرادات"),
                    Map.entry("payment-transactions", "سندات الصرف"),
                    Map.entry("supplier-discount", "إشعارات خصم الموردين"),
                    Map.entry("transfers-and-settlements", "التحويلات والتسويات"),
                    Map.entry("dealersClassification", "تصنيفات المتعاملين"),
                    Map.entry("customers", "العملاء"),
                    Map.entry("mainDealer", "المتعاملين الرئيسيين"),
                    Map.entry("suppliers", "الموردين"),
                    Map.entry("vouchers", "القيود اليومية"),
                    Map.entry("exchange-order", "أوامر الصرف"),
                    Map.entry("addition-order", "أوامر الإضافة"),
                    Map.entry("manufacturing-order", "أوامر التصنيع"),
                    Map.entry("storage-transfer", "التحويل بين المخازن"),
                    Map.entry("damage-adjustment", "تسوية التالف"),
                    Map.entry("purch-invoice", "فاتورة المشتريات"),
                    Map.entry("purch-invoice-list", "فواتير المشتريات"),
                    Map.entry("purchOrder", "أوامر الشراء"),
                    Map.entry("return-purch", "مرتجعات المشتريات"),
                    Map.entry("invoice", "فاتورة المبيعات"),
                    Map.entry("invoice-list", "فواتير المبيعات"),
                    Map.entry("sales-order", "أوامر البيع"),
                    Map.entry("quotation", "عروض الأسعار"),
                    Map.entry("return-sales", "مرتجعات المبيعات"),
                    Map.entry("point-of-sale", "فاتورة نقاط البيع"),
                    Map.entry("restaurant-menu", "فاتورة المطعم"));

    public static String name(String route) {
        String path = route.contains("#/") ? route.substring(route.indexOf("#/") + 2) : route;
        path = path.replaceAll("^/+", "").split("[/?]", 2)[0];
        for (var entry : LABELS.entrySet())
            if (entry.getKey().equalsIgnoreCase(path)) return entry.getValue();
        return path;
    }

    public static void opening(String route) {
        Allure.getLifecycle().updateStep(step -> step.setName("فتح شاشة " + name(route)));
    }
}
