/* شرح الملف: أرقام مستقلة وبسيطة لمراجعة الفواتير: الوحدة 100 قبل الضريبة أو115 شاملها، والإعفاء في سياسة الأصناف فقط. */
package TestSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import java.math.BigDecimal;
import java.util.Map;
import org.testng.Assert;

public final class InvoiceReviewRecipe {
    private InvoiceReviewRecipe() {}

    public static Map<String, Object> stockedItemForStores(
            Map<String, Object> defaults, Object secondStore, String price, boolean exempt) {
        if (!(defaults.get("stores") instanceof java.util.List<?> rows) || rows.isEmpty())
            throw new IllegalArgumentException("Owned stores are required");
        var stores = new java.util.ArrayList<Map<String, Object>>();
        for (Object row : rows)
            stores.add(new java.util.LinkedHashMap<>((Map<String, Object>) row));
        if (stores.stream().noneMatch(row -> secondStore.equals(row.get("storeId")))) {
            var second = new java.util.LinkedHashMap<>(stores.getFirst());
            second.put("storeId", secondStore);
            second.put("openingBalance", 0);
            second.put("currentBalance", 0);
            stores.add(second);
        }
        return stockedItem(defaults, Map.of("stores", stores), price, exempt);
    }

    public static Map<String, Object> stockedItem(
            Map<String, Object> defaults,
            Map<String, Object> stockReference,
            String price,
            boolean exempt) {
        Object stores = stockReference.get("stores");
        if (!(stores instanceof java.util.List<?> list) || list.size() < 2)
            throw new IllegalArgumentException(
                    "Invoice review needs both owned stores before creating the item");
        var values = new java.util.LinkedHashMap<>(defaults);
        values.put("stores", stores);
        return PolicyItemRecipe.create(values, price, exempt);
    }

    public static void explain(
            String screen,
            String action,
            TaxPolicy policy,
            boolean exempt,
            String item,
            BigDecimal before,
            BigDecimal vat,
            BigDecimal net) {
        if (io.qameta.allure.Allure.getLifecycle().getCurrentTestCase().isEmpty()) return;
        String tax =
                switch (policy) {
                    case NET_BASED -> "ضريبة على الصافي";
                    case ITEM_BASED -> "ضريبة على الأصناف";
                    case PRICE_INCLUDED -> "السعر شامل الضريبة";
                };
        io.qameta.allure.Allure.getLifecycle()
                .updateTestCase(
                        test ->
                                test.setName(
                                        screen
                                                + " — "
                                                + action
                                                + " — "
                                                + tax
                                                + (exempt ? " — صنف معفى" : " — صنف خاضع")));
        io.qameta.allure.Allure.description(
                "هنراجع "
                        + action
                        + " في "
                        + screen
                        + ". الصنف المستخدم: "
                        + item
                        + ". عند كمية واحدة، المتوقع قبل الضريبة "
                        + before
                        + "، والضريبة "
                        + vat
                        + "، والصافي "
                        + net
                        + ". بنقارن الشاشة والفاتورة المحفوظة؛ والتعديل يراجع الأرقام بعد تغيير الكمية. لو الحالة توقفت قبل العملية المطلوبة، نتيجتها لا تثبت تنفيذ باقي الخطوات.");
    }

    public static Object[][] actions(
            TaxPolicy policy, java.util.List<String> actions, boolean special) {
        var rows = new java.util.ArrayList<Object[]>();
        for (String action : actions)
            rows.add(special ? new Object[] {action, false, false} : new Object[] {action, false});
        if (policy == TaxPolicy.ITEM_BASED)
            for (String action : actions)
                rows.add(
                        special ? new Object[] {action, true, false} : new Object[] {action, true});
        if (special) for (String action : actions) rows.add(new Object[] {action, false, true});
        return rows.toArray(Object[][]::new);
    }

    public static String price(TaxPolicy policy) {
        return policy == TaxPolicy.PRICE_INCLUDED ? "115" : "100";
    }

    public static boolean exempt(TaxPolicy policy, boolean requested) {
        return policy == TaxPolicy.ITEM_BASED && requested;
    }

    public static BigDecimal vat(boolean exempt, int quantity) {
        return new BigDecimal(exempt ? "0" : "15").multiply(BigDecimal.valueOf(quantity));
    }

    public static BigDecimal net(boolean exempt, int quantity) {
        return new BigDecimal(exempt ? "100" : "115").multiply(BigDecimal.valueOf(quantity));
    }

    @io.qameta.allure.Step(
            "التحقق من قبل الضريبة والضريبة والصافي: عدد الوحدات {quantity}؛ معفى {exempt}")
    public static void verify(Map<String, Object> document, boolean exempt, int quantity) {
        amount(
                document.get("netBeforeTaxes"),
                new BigDecimal("100").multiply(BigDecimal.valueOf(quantity)),
                "قبل الضريبة");
        amount(document.get("totalTaxes"), vat(exempt, quantity), "الضريبة");
        amount(document.get("net"), net(exempt, quantity), "الصافي");
    }

    @io.qameta.allure.Step("التحقق من {label}: المتوقع {expected}، والقيمة المعروضة {actual}")
    public static void amount(Object actual, BigDecimal expected, String label) {
        Assert.assertNotNull(actual, label);
        DecimalAmountAssertions.matchesExpected(actual.toString().replace(",", ""), expected);
    }
}
