/* شرح الملف: يراجع حفظ وطباعة فاتورة بيع وضريبتها، ويتأكد أن منع التعديل والحذف بعد الحفظ لا يغير الفاتورة أو المخزون. */
package Invoice;

import Plugin_ERP.BranchProfiles.BranchProfiles;
import TestSupport.*;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.*;

public final class SalesInvoiceTaxReviewTest extends SalesScenarioTest {
    @DataProvider
    public Object[][] actions() {
        return InvoiceReviewRecipe.actions(
                BranchProfiles.selected().orElseThrow().expected().taxPolicy(),
                java.util.List.of("إضافة", "منع التعديل", "منع الحذف", "طباعة"),
                false);
    }

    @Test(dataProvider = "actions")
    @io.qameta.allure.Description(
            "صنف بسعر100 قبل الضريبة أو115 شاملها. ضريبة15 وصافي115؛ المعفى في سياسة الأصناف ضريبته صفر. المبيعات المحفوظة تمنع التعديل والحذف؛ نراجع المنع وثبات الأرقام والمخزون.")
    public void reviewSalesInvoice(String action, boolean requestExemption) {
        var policy = BranchProfiles.selected().orElseThrow().expected().taxPolicy();
        boolean exempt = InvoiceReviewRecipe.exempt(policy, requestExemption);
        String price = exempt ? "100" : InvoiceReviewRecipe.price(policy);
        var reviewItem =
                data.fixture(
                        "invoice-review-item",
                        "Items",
                        InvoiceReviewRecipe.stockedItem(
                                data.itemValues(), data.item(1).values(), price, exempt));
        prepare("Credit");
        item = reviewItem;
        InvoiceReviewRecipe.explain(
                "فاتورة المبيعات",
                action,
                policy,
                exempt,
                item.name(),
                new BigDecimal("100"),
                InvoiceReviewRecipe.vat(exempt, 1),
                InvoiceReviewRecipe.net(exempt, 1));
        allowedItems.add(item.id());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        for (int i = 1; i <= 2; i++)
            initialBalances.put(i, new BigDecimal(data.balance(item, i).toString()));
        add(item, "picker");
        page.slWaitNet(InvoiceReviewRecipe.net(exempt, 1).toPlainString());
        InvoiceReviewRecipe.verify(page.slDocument(), exempt, 1);
        printContentAssertions =
                stored ->
                        Plugin_ERP.Util.PrintedInvoiceAssertions.invoice(
                                driver,
                                stored.get("code").toString(),
                                customer.name(),
                                List.of(
                                        new Plugin_ERP.Util.PrintedInvoiceAssertions.Line(
                                                item.name(),
                                                BigDecimal.ONE,
                                                new BigDecimal(price))),
                                InvoiceReviewRecipe.net(exempt, 1));
        var invoice = save(action.equals("طباعة"));
        InvoiceReviewRecipe.verify(data.read(invoice), exempt, 1);
        assertStock("1");
        if (action.startsWith("منع")) {
            page.slEdit(kind().route, invoice.id(), invoice.name(), item.name());
            if (action.equals("منع التعديل")) {
                Assert.assertTrue(page.slLineReadOnly(item.name(), "الكمية"));
                Assert.assertTrue(page.slLineReadOnly(item.name(), "السعر"));
                Assert.assertFalse(page.slHasDocumentSaveAction());
            } else Assert.assertFalse(page.slHasDocumentDeleteAction());
            InvoiceReviewRecipe.verify(data.read(invoice), exempt, 1);
            assertStock("1");
        }
    }
}
