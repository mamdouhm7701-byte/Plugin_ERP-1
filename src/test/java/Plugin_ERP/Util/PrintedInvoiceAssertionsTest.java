/* شرح الملف: اختبارات مطابقة الأصناف والكميات والمبالغ في الفاتورة المطبوعة.
 */
package Plugin_ERP.Util;

import Plugin_ERP.Util.PrintedInvoiceAssertions.*;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PrintedInvoiceAssertionsTest {
    @Test
    public void printedDecimalAmountsAcceptToleranceButFractionalQuantityRemainsExact() {
        var cells = report();
        cells.set(10, cell("73.50", 100, 110));
        cells.set(12, cell("419.99", 300, 150));
        verify(cells);
        cells.set(6, cell("2.26", 200, 80));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        PrintedInvoiceAssertions.verify(
                                cells,
                                "12345",
                                "عميل اختبار",
                                List.of(
                                        new Line(
                                                "صنف أول",
                                                new BigDecimal("2.25"),
                                                new BigDecimal("100")),
                                        new Line(
                                                "صنف ثان",
                                                new BigDecimal("3"),
                                                new BigDecimal("73.25"))),
                                new BigDecimal("419.75")));
    }

    private Cell cell(String text, double x, double y) {
        return new Cell(text, x, y, 70, 15, "page-1");
    }

    private List<Cell> report() {
        return new ArrayList<>(
                List.of(
                        cell("رقم الفاتورة", 400, 0),
                        cell("12345", 300, 0),
                        cell("عميل اختبار", 400, 20),
                        cell("الكمية", 200, 50),
                        cell("السعر", 100, 50),
                        cell("صنف أول", 400, 80),
                        cell("2", 200, 80),
                        cell("100", 100, 80),
                        cell("صنف ثان", 400, 110),
                        cell("3", 200, 110),
                        cell("73.25", 100, 110),
                        cell("الصافي", 400, 150),
                        cell("419.75", 300, 150),
                        cell("100", 100, 190)));
    }

    private void verify(List<Cell> cells) {
        PrintedInvoiceAssertions.verify(
                cells,
                "12345",
                "عميل اختبار",
                List.of(
                        new Line("صنف أول", new BigDecimal("2"), new BigDecimal("100")),
                        new Line("صنف ثان", new BigDecimal("3"), new BigDecimal("73.25"))),
                new BigDecimal("419.75"));
    }

    @Test
    public void associatesDifferentItemsWithTheirOwnColumnsAndLabeledTotal() {
        verify(report());
    }

    @Test
    public void acceptsObservedBilingualInvoiceNumberAndAfterTaxTotalLabels() {
        var cells = report();
        cells.set(0, cell("INVOICE NO:", 400, 0));
        cells.set(11, cell("الصافي بعد الضريبة", 400, 150));
        verify(cells);
    }

    @Test
    public void correctAmountElsewhereCannotHideWrongItemPrice() {
        var cells = report();
        cells.set(7, cell("100.01", 100, 80));
        Assert.expectThrows(AssertionError.class, () -> verify(cells));
    }

    @Test
    public void swappedQuantitiesAndUnlabeledTotalCannotPass() {
        var cells = report();
        cells.set(6, cell("3", 200, 80));
        cells.set(9, cell("2", 200, 110));
        Assert.expectThrows(AssertionError.class, () -> verify(cells));
        var total = report();
        total.set(11, cell("ضريبة", 400, 150));
        Assert.expectThrows(AssertionError.class, () -> verify(total));
    }
}
