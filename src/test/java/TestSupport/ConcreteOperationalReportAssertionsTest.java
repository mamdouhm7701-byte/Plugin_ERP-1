/* شرح الملف: اختبارات مطابقة مجموعات وصفوف وكميات تقارير السيارات والسائقين والمشاريع.
 */
package TestSupport;

import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class ConcreteOperationalReportAssertionsTest {
    private final ConcreteOperationalReportAssertions.Expected expected =
            new ConcreteOperationalReportAssertions.Expected(
                    "owned-item",
                    "owned-customer",
                    "owned-project",
                    "owned-car",
                    "owned-driver",
                    "7",
                    BigDecimal.ONE,
                    BigDecimal.ONE);

    private Cell cell(String text, double x, double y) {
        return new Cell(text, x, y, 70, 25, "page-1");
    }

    private List<Cell> sample(ConcreteOperationalReportAssertions.Kind kind) {
        var cells = new ArrayList<Cell>();
        cells.add(cell("الكمية", 100, 100));
        cells.add(cell("owned-item", 400, 200));
        cells.add(cell("1.00", 100, 208));
        if (kind == ConcreteOperationalReportAssertions.Kind.PROJECT_TOTAL) {
            cells.add(cell("عدد الاذونات", 200, 100));
            cells.add(cell("1", 200, 208));
            cells.add(cell("owned-project", 400, 160));
            cells.add(cell("المشروع", 500, 160));
            for (var row : Map.of("الاجمالي", 300, "الاجمالي العام", 340).entrySet()) {
                cells.add(cell(row.getKey(), 400, row.getValue()));
                cells.add(cell("1.00", 100, row.getValue()));
                cells.add(cell("1", 200, row.getValue()));
            }
        } else {
            cells.add(cell("المشروع", 200, 100));
            cells.add(cell("العميل", 300, 100));
            cells.add(cell("كود الحركة", 500, 100));
            cells.add(cell("owned-project", 200, 200));
            cells.add(cell("owned-customer", 300, 200));
            cells.add(cell("7", 500, 208));
            if (kind != ConcreteOperationalReportAssertions.Kind.PROJECT_DETAIL) {
                cells.add(
                        cell(
                                kind == ConcreteOperationalReportAssertions.Kind.CARS
                                        ? "owned-car"
                                        : "owned-driver",
                                400,
                                160));
                cells.add(
                        cell(
                                kind == ConcreteOperationalReportAssertions.Kind.CARS
                                        ? "اسم السيارة"
                                        : "اسم السائق",
                                500,
                                160));
                cells.add(cell("اجمالي الكمية", 200, 300));
                cells.add(cell("1.00", 100, 300));
                cells.add(cell("عدد الردود - صنف", 500, 300));
                cells.add(cell("1", 400, 300));
                cells.add(cell("عدد الردود - فاتورة", 700, 300));
                cells.add(cell("1", 600, 300));
            }
        }
        return cells;
    }

    @Test
    public void expectedIdentityInAnotherGroupOrPageCannotAuthorizeOwnedRow() {
        for (var kind :
                List.of(
                        ConcreteOperationalReportAssertions.Kind.CARS,
                        ConcreteOperationalReportAssertions.Kind.DRIVERS,
                        ConcreteOperationalReportAssertions.Kind.PROJECT_TOTAL)) {
            String name =
                    kind == ConcreteOperationalReportAssertions.Kind.CARS
                            ? "owned-car"
                            : kind == ConcreteOperationalReportAssertions.Kind.DRIVERS
                                    ? "owned-driver"
                                    : "owned-project";
            for (String page : List.of("page-1", "page-2")) {
                var cells = sample(kind);
                cells.removeIf(c -> c.text().equals(name));
                cells.add(cell("foreign-group", 400, 160));
                cells.add(new Cell(name, 400, 140, 70, 25, page));
                Assert.expectThrows(
                        AssertionError.class,
                        () -> ConcreteOperationalReportAssertions.verify(kind, cells, expected));
            }
        }
    }

    @DataProvider
    public Object[][] kinds() {
        return Arrays.stream(ConcreteOperationalReportAssertions.Kind.values())
                .map(k -> new Object[] {k})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "kinds")
    public void verifiesOwnedRowAndApplicableTotals(ConcreteOperationalReportAssertions.Kind kind) {
        ConcreteOperationalReportAssertions.verify(kind, sample(kind), expected);
    }

    @Test(dataProvider = "kinds")
    public void anotherRowsCorrectQuantityCannotHideWrongOwnedQuantity(
            ConcreteOperationalReportAssertions.Kind kind) {
        var cells = sample(kind);
        cells.set(2, cell("2.00", 100, 208));
        cells.add(cell("1.00", 100, 400));
        Assert.expectThrows(
                AssertionError.class,
                () -> ConcreteOperationalReportAssertions.verify(kind, cells, expected));
    }

    @Test
    public void rejectsWrongGrandTotalAndOrderCount() {
        var kind = ConcreteOperationalReportAssertions.Kind.PROJECT_TOTAL;
        for (int column : List.of(100, 200)) {
            var cells = sample(kind);
            cells.removeIf(c -> c.x() == column && c.y() == 340);
            cells.add(cell("2", column, 340));
            Assert.expectThrows(
                    AssertionError.class,
                    () -> ConcreteOperationalReportAssertions.verify(kind, cells, expected));
        }
    }

    @Test
    public void rejectsForeignSourceAndDuplicateItemRows() {
        var kind = ConcreteOperationalReportAssertions.Kind.PROJECT_DETAIL;
        var wrong = sample(kind);
        wrong.removeIf(c -> c.x() == 500 && c.y() == 208);
        wrong.add(cell("8", 500, 208));
        Assert.expectThrows(
                AssertionError.class,
                () -> ConcreteOperationalReportAssertions.verify(kind, wrong, expected));
        var duplicate = sample(kind);
        duplicate.add(cell("owned-item", 400, 400));
        Assert.expectThrows(
                AssertionError.class,
                () -> ConcreteOperationalReportAssertions.verify(kind, duplicate, expected));
    }

    @Test
    public void aHeaderOnAnotherPageCannotAuthorizeTheQuantity() {
        var kind = ConcreteOperationalReportAssertions.Kind.PROJECT_DETAIL;
        var cells = sample(kind);
        cells.set(0, new Cell("الكمية", 100, 100, 70, 25, "page-2"));
        Assert.expectThrows(
                AssertionError.class,
                () -> ConcreteOperationalReportAssertions.verify(kind, cells, expected));
    }
}
