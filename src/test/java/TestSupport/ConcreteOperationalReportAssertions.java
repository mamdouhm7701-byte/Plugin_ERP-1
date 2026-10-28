package TestSupport;

import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
import java.math.BigDecimal;
import java.util.*;

/** Matches owned identities and numeric values to their printed page, row and column. */
public final class ConcreteOperationalReportAssertions {
    public enum Kind {
        CARS,
        DRIVERS,
        PROJECT_DETAIL,
        PROJECT_TOTAL
    }

    public record Expected(
            String item,
            String customer,
            String project,
            String car,
            String driver,
            String deliveryCode,
            BigDecimal quantity,
            BigDecimal deliveryCount) {}

    private ConcreteOperationalReportAssertions() {}

    public static void verify(Kind kind, List<Cell> cells, Expected expected) {
        Cell row =
                unique(
                        cells.stream().filter(c -> c.text().equals(expected.item())).toList(),
                        "owned item");
        amount(cells, row, "الكمية", expected.quantity());
        if (kind == Kind.PROJECT_TOTAL) {
            Cell group = group(cells, row, "المشروع", expected.project());
            for (Cell target :
                    List.of(
                            row,
                            groupTotal(cells, row, group, "الاجمالي"),
                            below(cells, row, "الاجمالي العام"))) {
                amount(cells, target, "الكمية", expected.quantity());
                amount(cells, target, "عدد الاذونات", expected.deliveryCount());
            }
        } else {
            text(cells, row, "المشروع", expected.project());
            text(cells, row, "العميل", expected.customer());
            amount(cells, row, "كود الحركة", new BigDecimal(expected.deliveryCode()));
            if (kind != Kind.PROJECT_DETAIL) {
                Cell group =
                        group(
                                cells,
                                row,
                                kind == Kind.CARS ? "اسم السيارة" : "اسم السائق",
                                kind == Kind.CARS ? expected.car() : expected.driver());
                amount(
                        cells,
                        groupTotal(cells, row, group, "اجمالي الكمية"),
                        "الكمية",
                        expected.quantity());
                leftAmount(
                        cells, groupTotal(cells, row, group, "عدد الردود - صنف"), BigDecimal.ONE);
                leftAmount(
                        cells,
                        groupTotal(cells, row, group, "عدد الردود - فاتورة"),
                        expected.deliveryCount());
            }
        }
    }

    private static Cell group(List<Cell> cells, Cell row, String title, String expected) {
        Cell heading =
                cells.stream()
                        .filter(
                                c ->
                                        c.page().equals(row.page())
                                                && c.y() < row.y()
                                                && c.text().equals(title))
                        .max(Comparator.comparingDouble(Cell::y))
                        .orElseThrow(
                                () -> new AssertionError("Missing owned group heading: " + title));
        Cell value =
                cells.stream()
                        .filter(c -> sameRow(c, heading) && center(c) < heading.x())
                        .max(
                                Comparator.comparingDouble(
                                        ConcreteOperationalReportAssertions::center))
                        .orElseThrow(() -> new AssertionError("Missing group identity: " + title));
        if (!value.text().equals(expected))
            throw new AssertionError(
                    "Owned row grouped under " + value.text() + " instead of " + expected);
        return heading;
    }

    private static Cell below(List<Cell> cells, Cell row, String title) {
        return unique(
                cells.stream()
                        .filter(
                                c ->
                                        c.page().equals(row.page())
                                                && c.y() > row.y()
                                                && c.text().equals(title))
                        .toList(),
                title);
    }

    private static Cell groupTotal(List<Cell> cells, Cell row, Cell group, String title) {
        double nextGroup =
                cells.stream()
                        .filter(
                                c ->
                                        c.page().equals(row.page())
                                                && c.y() > group.y()
                                                && c.text().equals(group.text()))
                        .mapToDouble(Cell::y)
                        .min()
                        .orElse(Double.POSITIVE_INFINITY);
        return unique(
                cells.stream()
                        .filter(
                                c ->
                                        c.page().equals(row.page())
                                                && c.y() > row.y()
                                                && c.y() < nextGroup
                                                && c.text().equals(title))
                        .toList(),
                title + " in owned group");
    }

    private static boolean sameRow(Cell a, Cell b) {
        return a.page().equals(b.page()) && Math.abs(a.y() - b.y()) < 12;
    }

    private static double center(Cell c) {
        return c.x() + c.width() / 2;
    }

    private static Cell column(List<Cell> cells, Cell row, String title) {
        Cell heading =
                cells.stream()
                        .filter(
                                c ->
                                        c.page().equals(row.page())
                                                && c.y() < row.y()
                                                && c.text().equals(title))
                        .max(Comparator.comparingDouble(Cell::y))
                        .orElseThrow(() -> new AssertionError("Missing report heading: " + title));
        return unique(
                cells.stream()
                        .filter(c -> sameRow(c, row))
                        .filter(
                                c ->
                                        Math.abs(center(c) - center(heading))
                                                < Math.min(c.width(), heading.width()) * .45)
                        .toList(),
                title + " on " + row.text());
    }

    private static void text(List<Cell> cells, Cell row, String title, String expected) {
        String actual = column(cells, row, title).text();
        if (!actual.equals(expected))
            throw new AssertionError(
                    title + " differs on owned row: " + actual + " != " + expected);
    }

    private static void amount(List<Cell> cells, Cell row, String title, BigDecimal expected) {
        equalsAmount(column(cells, row, title), expected, title + " on " + row.text());
    }

    private static void leftAmount(List<Cell> cells, Cell label, BigDecimal expected) {
        Cell value =
                cells.stream()
                        .filter(c -> sameRow(c, label) && center(c) < label.x())
                        .filter(c -> numeric(c.text()))
                        .max(
                                Comparator.comparingDouble(
                                        ConcreteOperationalReportAssertions::center))
                        .orElseThrow(
                                () ->
                                        new AssertionError(
                                                "Missing numeric value beside " + label.text()));
        equalsAmount(value, expected, label.text());
    }

    private static String normalized(String value) {
        return value.replace(",", "").replace("٬", "").replace("٫", ".").strip();
    }

    private static boolean numeric(String value) {
        try {
            new BigDecimal(normalized(value));
            return true;
        } catch (NumberFormatException invalid) {
            return false;
        }
    }

    private static void equalsAmount(Cell value, BigDecimal expected, String label) {
        try {
            BigDecimal actual = new BigDecimal(normalized(value.text()));
            if (actual.compareTo(expected) != 0)
                throw new AssertionError(label + ": expected " + expected + ", actual " + actual);
        } catch (NumberFormatException invalid) {
            throw new AssertionError("Non-numeric report cell: " + label, invalid);
        }
    }

    private static Cell unique(List<Cell> cells, String label) {
        if (cells.size() != 1)
            throw new AssertionError("Expected one " + label + "; found " + cells.size());
        return cells.getFirst();
    }
}
