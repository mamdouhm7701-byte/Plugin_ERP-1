package TestSupport;

import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
import java.math.BigDecimal;
import java.util.*;

/** Matches the two-level reservation report headings to one exact item row and its grand total. */
public final class ConcreteReservationReportAssertions {
    private ConcreteReservationReportAssertions() {}

    public static void verify(
            List<Cell> cells, String item, BigDecimal quantity, BigDecimal price) {
        verify(cells, item, quantity, price, BigDecimal.ZERO);
    }

    public static void verify(
            List<Cell> cells,
            String item,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal consumed) {
        Cell row = unique(cells.stream().filter(c -> c.text().equals(item)).toList(), "owned item");
        Cell total =
                unique(
                        cells.stream().filter(c -> c.text().equals("الاجمالي العام")).toList(),
                        "grand total");
        BigDecimal value = quantity.multiply(price);
        for (Cell target : List.of(row, total)) {
            assertValue(cells, target, "اصل الحجز", "كمية", quantity);
            assertValue(cells, target, "اصل الحجز", "قيمة", value);
            assertValue(cells, target, "المنصرف", "كمية", consumed);
            assertValue(cells, target, "المنصرف", "قيمة", consumed.multiply(price));
            assertValue(cells, target, "المتبقي", "كمية", quantity.subtract(consumed));
            assertValue(
                    cells, target, "المتبقي", "قيمة", quantity.subtract(consumed).multiply(price));
        }
    }

    private static void assertValue(
            List<Cell> cells, Cell row, String groupName, String columnName, BigDecimal expected) {
        Cell group = nearestHeading(cells, row, groupName, null);
        Cell column = nearestHeading(cells, row, columnName, group);
        var values =
                cells.stream()
                        .filter(c -> c.page().equals(row.page()))
                        .filter(c -> Math.abs(c.y() - row.y()) < 3)
                        .filter(
                                c ->
                                        Math.abs(center(c) - center(column))
                                                < Math.max(c.width(), column.width()) * .55)
                        .toList();
        Cell value = unique(values, groupName + "/" + columnName + " on " + row.text());
        BigDecimal actual;
        try {
            actual =
                    new BigDecimal(
                            value.text().replace(",", "").replace("٬", "").replace("٫", "."));
        } catch (NumberFormatException failure) {
            throw new AssertionError(
                    "Printed report value is not numeric: " + value.text(), failure);
        }
        if (actual.compareTo(expected) != 0)
            throw new AssertionError(
                    groupName
                            + "/"
                            + columnName
                            + " on "
                            + row.text()
                            + ": expected "
                            + expected
                            + ", actual "
                            + actual);
    }

    private static Cell nearestHeading(List<Cell> cells, Cell row, String name, Cell group) {
        return cells.stream()
                .filter(c -> c.page().equals(row.page()) && c.y() < row.y())
                .filter(c -> c.text().equals(name))
                .filter(
                        c ->
                                group == null
                                        || (c.y() > group.y()
                                                && center(c) >= group.x() - 2
                                                && center(c) <= group.x() + group.width() + 2))
                .max(Comparator.comparingDouble(Cell::y))
                .orElseThrow(
                        () -> new AssertionError("Missing reservation report heading: " + name));
    }

    private static double center(Cell cell) {
        return cell.x() + cell.width() / 2;
    }

    private static Cell unique(List<Cell> cells, String name) {
        if (cells.size() != 1)
            throw new AssertionError("Expected one " + name + "; found " + cells.size());
        return cells.getFirst();
    }
}
