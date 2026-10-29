/* شرح الملف: يراجع إضافة وتعديل وحذف وطباعة فاتورة مشتريات بصنف معروف، ويقارن الضريبة والصافي بأرقام مستقلة حسب سياسة الفرع. */
package Purches;

import Plugin_ERP.BranchProfiles.BranchProfiles;
import TestSupport.*;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.*;

public final class PurchaseInvoiceTaxReviewTest extends PurchaseScenarioTest {
    protected PurchaseDocumentKind kind() {
        return PurchaseDocumentKind.INVOICE;
    }

    @DataProvider
    public Object[][] actions() {
        return InvoiceReviewRecipe.actions(
                BranchProfiles.selected().orElseThrow().expected().taxPolicy(),
                java.util.List.of("إضافة", "تعديل", "حذف", "طباعة"),
                false);
    }

    @Test(dataProvider = "actions")
    @io.qameta.allure.Description(
            "صنف بسعر100 قبل الضريبة أو115 شاملها. الضريبة15 والصافي115. المعفى تحت سياسة الأصناف ضريبته صفر وصافيه100. التعديل يضاعف الكمية ثم يراجع الأرقام؛ الحذف يعيد المخزون.")
    public void reviewPurchaseInvoice(String action, boolean requestExemption) {
        var policy = BranchProfiles.selected().orElseThrow().expected().taxPolicy();
        boolean exempt = InvoiceReviewRecipe.exempt(policy, requestExemption);
        String price = InvoiceReviewRecipe.price(policy);
        var reviewItem =
                data.fixture(
                        "invoice-review-item",
                        "Items",
                        InvoiceReviewRecipe.stockedItem(
                                data.itemValues(), data.item(1).values(), price, exempt));
        var supplierValues = new java.util.LinkedHashMap<>(data.supplier().values());
        supplierValues.put("isTaxable", true);
        var taxableSupplier = data.fixture("review-taxable-supplier", "Suppliers", supplierValues);
        prepare("Credit");
        supplier = taxableSupplier;
        Assert.assertEquals(
                data.read(supplier).get("isTaxable"), true, "مورد مراجعة الضريبة خاضع للضريبة");
        page.puChoose("المورد", supplier.name());
        // Changing the supplier clears its dependent agent selection in the form.
        page.puChoose("مندوب", agent.name());
        Assert.assertEquals(
                page.puDocument().get("agentId"),
                agent.id(),
                "إعادة اختيار المندوب بعد تغيير المورد قبل الحفظ");
        item = reviewItem;
        InvoiceReviewRecipe.explain(
                "فاتورة المشتريات",
                action,
                policy,
                exempt,
                item.name(),
                new BigDecimal("100"),
                InvoiceReviewRecipe.vat(exempt, 1),
                InvoiceReviewRecipe.net(exempt, 1));
        allowedItems.add(item.id());
        page.puCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
        add(item, "picker");
        page.puSetLine(item.name(), "سعر الشراء", exempt ? "100" : price);
        page.puWaitNet(InvoiceReviewRecipe.net(exempt, 1).toPlainString());
        InvoiceReviewRecipe.verify(page.puDocument(), exempt, 1);
        printContentAssertions =
                stored ->
                        Plugin_ERP.Util.PrintedInvoiceAssertions.invoice(
                                driver,
                                stored.get("code").toString(),
                                supplier.name(),
                                List.of(
                                        new Plugin_ERP.Util.PrintedInvoiceAssertions.Line(
                                                item.name(),
                                                BigDecimal.ONE,
                                                new BigDecimal(exempt ? "100" : price))),
                                InvoiceReviewRecipe.net(exempt, 1));
        var invoice = save(action.equals("طباعة"));
        InvoiceReviewRecipe.verify(data.read(invoice), exempt, 1);
        assertStock("1");
        if (action.equals("تعديل")) {
            page.puEdit(kind().route, invoice.id(), marker, item.name());
            page.puCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
            page.puQuantity(item.name(), "2");
            page.puWaitNet(InvoiceReviewRecipe.net(exempt, 2).toPlainString());
            InvoiceReviewRecipe.verify(page.puDocument(), exempt, 2);
            var updated = save(false);
            Assert.assertEquals(updated.id(), invoice.id());
            InvoiceReviewRecipe.verify(data.read(updated), exempt, 2);
            assertStock("2");
        } else if (action.equals("حذف")) {
            page.puDeleteOwned(supplier.name(), data.read(invoice).get("code").toString());
            Assert.assertNull(data.read(invoice));
            assertStock("0");
        }
    }
}
