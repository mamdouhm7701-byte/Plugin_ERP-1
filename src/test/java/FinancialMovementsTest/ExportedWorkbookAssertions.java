package FinancialMovementsTest;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import org.apache.poi.ss.usermodel.*;

/** Reads actual XLS/XLSX cells, keeping code, party and amount on the same exported row. */
final class ExportedWorkbookAssertions {
    static void assertOwnedRow(byte[] contents, String code, String party, BigDecimal amount) {
        try (Workbook book = WorkbookFactory.create(new ByteArrayInputStream(contents))) {
            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            record ExportRow(Map<Integer, String> cells, int amountColumn) {}
            List<ExportRow> matching = new ArrayList<>();
            for (Sheet sheet : book) {
                int amountColumn = -1, codeColumn = -1;
                for (Row row : sheet) {
                    Map<Integer, String> cells = new LinkedHashMap<>();
                    for (Cell cell : row) {
                        String value = formatter.formatCellValue(cell).trim();
                        cells.put(cell.getColumnIndex(), value);
                        if (Set.of("القيمة", "المبلغ", "Value", "Amount").contains(value))
                            amountColumn = cell.getColumnIndex();
                        if (Set.of("الكود", "Code").contains(value))
                            codeColumn = cell.getColumnIndex();
                    }
                    if (codeColumn >= 0 && code.equals(cells.get(codeColumn)))
                        matching.add(new ExportRow(cells, amountColumn));
                }
            }
            if (matching.size() != 1)
                throw new AssertionError(
                        "Expected one exported row for code "
                                + code
                                + "; found "
                                + matching.size());
            var row = matching.getFirst();
            if (party != null
                    && row.cells().values().stream().noneMatch(value -> value.contains(party)))
                throw new AssertionError("Exported party differs for " + code + ": " + row);
            if (row.amountColumn() < 0)
                throw new AssertionError("Exported amount column is missing");
            String actual = row.cells().getOrDefault(row.amountColumn(), "");
            boolean monetaryMatch;
            try {
                monetaryMatch =
                        new BigDecimal(
                                                new Plugin_ERP.Bots.ValidationActions(
                                                                null, null, null)
                                                        .normalizePrice(actual))
                                        .compareTo(amount)
                                == 0;
            } catch (NumberFormatException nonNumeric) {
                monetaryMatch = false;
            }
            if (!monetaryMatch)
                throw new AssertionError("Exported amount differs for " + code + ": " + row);
        } catch (IOException failure) {
            throw new AssertionError("Cannot read exported workbook", failure);
        }
    }
}
