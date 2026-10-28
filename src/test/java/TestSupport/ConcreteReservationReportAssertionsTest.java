package TestSupport;

import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ConcreteReservationReportAssertionsTest {
    private List<Cell> report() {
        var cells = new ArrayList<Cell>();
        String[] groups = {"المتبقي", "المنصرف", "اصل الحجز"};
        for (int i = 0; i < groups.length; i++) {
            cells.add(new Cell(groups[i], i * 140, 0, 130, 20, "page1"));
            cells.add(new Cell("قيمة", i * 140, 30, 65, 20, "page1"));
            cells.add(new Cell("كمية", i * 140 + 70, 30, 60, 20, "page1"));
            for (int y : new int[] {60, 100}) {
                cells.add(new Cell(i == 1 ? "0" : "200", i * 140, y, 65, 20, "page1"));
                cells.add(new Cell(i == 1 ? "0" : "2", i * 140 + 70, y, 60, 20, "page1"));
            }
        }
        cells.add(new Cell("owned item", 450, 60, 150, 40, "page1"));
        cells.add(new Cell("الاجمالي العام", 450, 100, 150, 20, "page1"));
        return cells;
    }

    private void verify(List<Cell> cells) {
        ConcreteReservationReportAssertions.verify(
                cells, "owned item", new BigDecimal("2"), new BigDecimal("100"));
    }

    @Test
    public void verifiesItemAndGrandTotalAcrossRepeatedQuantityHeadings() {
        verify(report());
    }

    @Test
    public void partialDeliveryReconcilesConsumedAndRemainingColumns() {
        var cells =
                report().stream()
                        .map(
                                c ->
                                        c.y() >= 60 && c.x() < 280
                                                ? new Cell(
                                                        c.x() % 140 < 65 ? "100" : "1",
                                                        c.x(),
                                                        c.y(),
                                                        c.width(),
                                                        c.height(),
                                                        c.page())
                                                : c)
                        .toList();
        ConcreteReservationReportAssertions.verify(
                cells, "owned item", new BigDecimal("2"), new BigDecimal("100"), BigDecimal.ONE);
        Assert.expectThrows(AssertionError.class, () -> verify(cells));
    }

    @Test
    public void correctTotalCannotHideWrongOwnedItemQuantity() {
        var cells = report();
        cells.set(4, new Cell("3", 70, 60, 60, 20, "page1"));
        Assert.expectThrows(AssertionError.class, () -> verify(cells));
    }

    @Test
    public void correctItemCannotHideWrongGrandTotal() {
        var cells = report();
        cells.set(5, new Cell("201", 0, 100, 65, 20, "page1"));
        Assert.expectThrows(AssertionError.class, () -> verify(cells));
    }

    @Test
    public void headingsFromAnotherPageCannotQualifyTheOwnedRow() {
        var cells =
                report().stream()
                        .map(
                                c ->
                                        c.y() < 60
                                                ? new Cell(
                                                        c.text(),
                                                        c.x(),
                                                        c.y(),
                                                        c.width(),
                                                        c.height(),
                                                        "page2")
                                                : c)
                        .toList();
        Assert.expectThrows(AssertionError.class, () -> verify(cells));
    }
}
