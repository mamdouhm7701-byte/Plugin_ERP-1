/* شرح الملف: مراجعة الصفوف والقيم داخل ملف إكسل الناتج، ضمن الحركات المالية.
 */
package FinancialMovementsTest;

import java.io.*;
import java.math.BigDecimal;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.*;

public final class ExportedWorkbookAssertionsTest {
    @DataProvider
    public Object[][] formats() {
        return new Object[][] {{false}, {true}};
    }

    @Test(dataProvider = "formats")
    public void validatesArabicPartyAndDecimalAmountInBothFormats(boolean xlsx) throws IOException {
        ExportedWorkbookAssertions.assertOwnedRow(
                workbook(xlsx), "7012345", "عميل اختبار", new BigDecimal("100.01"));
    }

    @Test(dataProvider = "formats")
    public void amountOnAnotherRowAndWrongPartyCannotSatisfyExpectedDocument(boolean xlsx)
            throws IOException {
        byte[] contents = workbook(xlsx);
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        ExportedWorkbookAssertions.assertOwnedRow(
                                contents, "7012345", "عميل اختبار", new BigDecimal("1001")));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        ExportedWorkbookAssertions.assertOwnedRow(
                                contents, "7012345", "عميل مختلف", new BigDecimal("100.01")));
    }

    @Test(dataProvider = "formats")
    public void aNumericDocumentCodeCannotSubstituteForTheAmountColumn(boolean xlsx)
            throws IOException {
        byte[] contents;
        try (var book = WorkbookFactory.create(new ByteArrayInputStream(workbook(xlsx)));
                var out = new ByteArrayOutputStream()) {
            book.getSheetAt(0).getRow(1).getCell(0).setCellValue("100");
            book.write(out);
            contents = out.toByteArray();
        }
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        ExportedWorkbookAssertions.assertOwnedRow(
                                contents, "100", "عميل اختبار", new BigDecimal("100")));
    }

    private byte[] workbook(boolean xlsx) throws IOException {
        try (Workbook book = xlsx ? new XSSFWorkbook() : new HSSFWorkbook();
                var out = new ByteArrayOutputStream()) {
            Sheet sheet = book.createSheet("حركات");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("الكود");
            header.createCell(1).setCellValue("الطرف");
            header.createCell(2).setCellValue("القيمة");
            Row first = sheet.createRow(1);
            first.createCell(0).setCellValue("7012345");
            first.createCell(1).setCellValue("عميل اختبار");
            first.createCell(2).setCellValue(100.01);
            Row second = sheet.createRow(2);
            second.createCell(0).setCellValue("7012346");
            second.createCell(1).setCellValue("عميل مختلف");
            second.createCell(2).setCellValue(1001);
            book.write(out);
            return out.toByteArray();
        }
    }
}
