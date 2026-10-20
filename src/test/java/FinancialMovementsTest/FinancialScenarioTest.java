package FinancialMovementsTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

/** Isolated scenario orchestration. Locators and browser interactions live in the POM. */
public abstract class FinancialScenarioTest extends BaseTest {
    protected abstract FinancialMovementKind kind();

    protected abstract FinancialMovementPage movementPage();

    protected FinancialTestData data;
    protected FinancialMovementPage page;
    protected String marker, mainWindow;
    private final List<FinancialTestData> pending = new ArrayList<>();
    private FinancialTestData.Record party;
    private String fromType = "Safe", toType = "Bank";
    private boolean directAccountMode;

    @BeforeMethod(alwaysRun = true)
    public void newFinancialScenario() {
        marker = "QA-FM-" + UUID.randomUUID().toString().substring(0, 12);
        if (data == null) {
            data = new FinancialTestData(driver);
            pending.add(data);
        }
        data.beginScenario();
        page = movementPage();
        mainWindow = driver.getWindowHandle();
        party = null;
        fromType = "Safe";
        toType = "Bank";
        allocationInvoice = null;
    }

    private void prepare(boolean accountMode) {
        directAccountMode = accountMode;
        switch (kind()) {
            case RECEIPT, CUSTOMER_DISCOUNT -> {
                party = data.customer(1);
                data.agent(false);
            }
            case PAYMENT, SUPPLIER_DISCOUNT -> {
                party = data.supplier(1);
                data.agent(true);
            }
            case EXPENSE -> party = accountMode ? data.account("expense-direct") : data.expense();
            case INCOME -> party = accountMode ? data.account("income-direct") : data.income();
            case TRANSFER -> {
                data.party(fromType, 1);
                data.party(toType, 2);
            }
        }
        if (!kind().discount() && kind() != FinancialMovementKind.TRANSFER) data.payment();
        page.openList(kind().route);
        page.openNew();
        page.captureSaves(marker);
        if (accountMode) page.movementType("Accounts");
    }

    private void fill(String amount, boolean accountMode, boolean tax) {
        fill(amount, accountMode, tax, null);
    }

    private void fill(String amount, boolean accountMode, boolean tax, String omitted) {
        if (kind() == FinancialMovementKind.TRANSFER) {
            page.transferParty(1, fromType, data.party(fromType, 1).name());
            if ("toParty".equals(omitted)) page.selectTransferType(2, toType);
            else page.transferParty(2, toType, data.party(toType, 2).name());
        } else {
            if (!"party".equals(omitted))
                page.choose(accountMode ? "رقم الحساب" : kind().partyLabel, party.name());
            if (kind() == FinancialMovementKind.RECEIPT
                    || kind() == FinancialMovementKind.PAYMENT
                    || kind().discount())
                page.choose(
                        "مندوب",
                        data.agent(
                                        kind() == FinancialMovementKind.PAYMENT
                                                || kind()
                                                        == FinancialMovementKind.SUPPLIER_DISCOUNT)
                                .name());
            if (!kind().discount() && !"payment".equals(omitted)) {
                if ("paymentOption".equals(omitted)) page.choose("طريقة الدفع", "اخري");
                else page.payment(data.payment().name());
            }
        }
        page.amount(amount);
        page.notes(marker);
        if (kind() == FinancialMovementKind.EXPENSE) page.taxable(tax);
    }

    @SuppressWarnings("unchecked")
    private List<FinancialTestData.Record> capture() {
        List<FinancialTestData.Record> out = new ArrayList<>();
        if (page == null || !marker.equals(page.captureMarker())) return out;
        data.rememberAttempts(page.capturedAttempts(), marker);
        for (Map<String, Object> saved : page.capturedSaves()) {
            Map<String, Object> values = (Map<String, Object>) saved.get("values");
            String endpoint = (String) saved.get("endpoint");
            Assert.assertEquals(endpoint, kind().endpoint, "Unexpected financial endpoint");
            Object id = saved.get("id");
            String note = (String) values.get("notes");
            Assert.assertTrue(note.startsWith(marker), "Refuse to track an unrelated movement");
            data.track(endpoint, id, note, values);
            out.add(new FinancialTestData.Record(endpoint, id, note, values));
        }
        return out;
    }

    private FinancialTestData.Record persist(boolean print, String amount) {
        if (!page.hasSaveStarted()) {
            if (print) page.saveAndPrintMovement();
            else page.saveMovement();
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(
                            d -> {
                                data.rememberAttempts(page.capturedAttempts(), marker);
                                return !page.capturedSaves().isEmpty();
                            });
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Movement save did not complete. Validation: " + page.validationMessages(), e);
        }
        List<FinancialTestData.Record> saves = capture();
        FinancialTestData.Record record = saves.get(saves.size() - 1);
        Map<String, Object> stored = data.read(record);
        Assert.assertNotNull(stored, "Movement was not persisted");
        page.identifyMovement(marker, stored.get("code"));
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(
                new BigDecimal(stored.get("value").toString()).compareTo(new BigDecimal(amount)),
                0,
                "Persisted amount differs");
        assertOwnedRelationships(stored);
        if (allocationInvoice != null) {
            Object allocations =
                    stored.get(
                            kind().discount()
                                    ? "discountTransactionAllocations"
                                    : "financialTransactionAllocations");
            Assert.assertTrue(
                    allocations instanceof List<?> rows && rows.size() == 1,
                    "Expected one owned invoice allocation");
            Map<?, ?> allocated = (Map<?, ?>) ((List<?>) allocations).get(0);
            Assert.assertEquals(allocated.get("itemTransactionId"), allocationInvoice.id());
            Object allocatedAmount = allocated.get(kind().discount() ? "allocatedValue" : "value");
            Assert.assertNotNull(
                    allocatedAmount, "Persisted allocation amount missing: " + allocated);
            Assert.assertEquals(
                    new BigDecimal(allocatedAmount.toString()).compareTo(new BigDecimal(amount)),
                    0);
        }
        if (print) {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(d -> d.getWindowHandles().size() > 1);
            String report =
                    driver.getWindowHandles().stream()
                            .filter(h -> !h.equals(mainWindow))
                            .findFirst()
                            .orElseThrow();
            driver.switchTo().window(report);
            try {
                page.awaitReport();
                page.AssertTextInReport(marker);
                page.AssertNumberInReport(amount);
                page.AssertDateInReport(
                        java.time.LocalDate.parse(
                                        stored.get("transactionDate").toString().substring(0, 10))
                                .format(
                                        java.time.format.DateTimeFormatter.ofPattern(
                                                "dd/MM/yyyy")));
                if (party != null) page.AssertTextInReport(party.name());
                if (kind() == FinancialMovementKind.TRANSFER) {
                    page.AssertTextInReport(data.party(fromType, 1).name());
                    page.AssertTextInReport(data.party(toType, 2).name());
                } else if (!kind().discount()) {
                    page.AssertTextInReport("اخري");
                    page.AssertTextInReport(data.bank(1).name());
                }
            } catch (RuntimeException | AssertionError failure) {
                page.attachReportEvidence();
                throw failure;
            } finally {
                driver.close();
                driver.switchTo().window(mainWindow);
            }
        }
        page.waitForList();
        return record;
    }

    private void assertOwnedRelationships(Map<String, Object> stored) {
        if (kind() == FinancialMovementKind.TRANSFER) {
            Assert.assertEquals(stored.get("fromType"), fromType);
            Assert.assertEquals(stored.get("toType"), toType);
            Assert.assertEquals(stored.get("from" + fromType + "Id"), data.party(fromType, 1).id());
            Assert.assertEquals(stored.get("to" + toType + "Id"), data.party(toType, 2).id());
        } else if (kind() == FinancialMovementKind.EXPENSE
                || kind() == FinancialMovementKind.INCOME) {
            String key =
                    directAccountMode
                            ? "accountId"
                            : kind() == FinancialMovementKind.EXPENSE ? "expenseId" : "incomeId";
            Assert.assertEquals(
                    stored.get(key), party.id(), "Movement must use its own account/type");
        } else {
            Assert.assertEquals(
                    stored.get("dealerId"), party.id(), "Movement must use its own dealer");
            Assert.assertEquals(
                    stored.get("agentId"),
                    data.agent(
                                    kind() == FinancialMovementKind.PAYMENT
                                            || kind() == FinancialMovementKind.SUPPLIER_DISCOUNT)
                            .id());
        }
        if (!kind().discount() && kind() != FinancialMovementKind.TRANSFER) {
            Assert.assertEquals(stored.get("paymentMethodId"), data.payment().id());
            Assert.assertEquals(stored.get("bankId"), data.bank(1).id());
        }
    }

    protected void create(
            boolean print, boolean allocation, boolean tax, boolean accountMode, String amount) {
        prepare(accountMode);
        fill(amount, accountMode, tax);
        if (tax || kind().discount()) assertTaxIdentity();
        if (allocation) allocateOwnInvoice(amount);
        persist(print, amount);
    }

    protected void edit(boolean print, boolean tax) {
        edit(print, tax, false);
    }

    protected void edit(boolean print, boolean tax, boolean accountMode) {
        prepare(accountMode);
        fill("100", accountMode, tax);
        FinancialTestData.Record saved = persist(false, "100");
        if (kind() == FinancialMovementKind.TRANSFER) {
            data.bank(1);
            data.safe(2);
            page.openList(kind().route);
        }
        page.editOwned(marker);
        page.captureSaves(marker);
        page.amount("200.25");
        if (kind() == FinancialMovementKind.TRANSFER) {
            fromType = "Bank";
            toType = "Safe";
            page.transferParty(1, fromType, data.bank(1).name());
            page.transferParty(2, toType, data.safe(2).name());
        }
        persist(print, "200.25");
        Assert.assertEquals(
                new BigDecimal(data.read(saved).get("value").toString())
                        .compareTo(new BigDecimal("200.25")),
                0);
    }

    protected void deleteOwn() {
        prepare(false);
        fill("100", false, false);
        FinancialTestData.Record saved = persist(false, "100");
        page.requestDeleteOwned(marker);
        page.confirmDelete(true);
        page.waitForRemoval(marker);
        Assert.assertNull(data.read(saved), "Deleted movement still returned by API");
    }

    protected void transfer(String from, String to, boolean print) {
        fromType = from;
        toType = to;
        create(print, false, false, false, "100.25");
    }

    protected void invalid(String field) {
        prepare(false);
        fill("100", false, false, field);
        switch (field) {
            case "value" -> page.amount("0");
            case "negative" -> page.amount("-1");
            case "party", "payment", "paymentOption" -> {}
            case "date" -> page.outsideFiscalYear();
            case "sameParty" -> page.transferParty(2, fromType, data.party(fromType, 1).name());
            case "toParty" -> {}
            default -> throw new IllegalArgumentException(field);
        }
        page.saveMovement();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .ignoring(StaleElementReferenceException.class)
                .until(
                        d ->
                                !page.validationMessages().isEmpty()
                                        || !page.capturedSaves().isEmpty());
        page.awaitSavesSettled();
        capture();
        Assert.assertTrue(page.capturedSaves().isEmpty(), "Invalid movement was saved: " + field);
        Assert.assertTrue(page.isFormPresent(), "Invalid submission unexpectedly left the form");
        Assert.assertTrue(
                page.validationFor(field, kind().partyLabel),
                "Expected validation for " + field + ": " + page.validationMessages());
    }

    private FinancialTestData.Record allocationInvoice;

    protected void allocateOwnInvoice(String amount) {
        allocationInvoice =
                data.invoice(
                        kind() == FinancialMovementKind.PAYMENT
                                || kind() == FinancialMovementKind.SUPPLIER_DISCOUNT,
                        new BigDecimal(amount));
        Map<String, Object> invoice = data.read(allocationInvoice);
        Assert.assertNotNull(invoice, "Owned allocation invoice missing");
        page.allocate(invoice.get("code").toString(), amount);
    }

    protected void assertTaxIdentity() {
        BigDecimal[] v = page.taxValues();
        Assert.assertTrue(
                v[0].add(v[1]).subtract(v[2]).abs().compareTo(new BigDecimal("0.01")) <= 0,
                "Net + tax must equal total");
        Assert.assertTrue(v[1].signum() >= 0, "Tax cannot be negative");
        Assert.assertTrue(
                v[0].multiply(new BigDecimal("0.15"))
                                .subtract(v[1])
                                .abs()
                                .compareTo(new BigDecimal("0.01"))
                        <= 0,
                "Tax must equal 15% of the net amount for the taxable fixture");
    }

    @Test
    @Story("إغلاق نموذج حركة مالية دون حفظ")
    public void FM_CloseNewWithoutSaving() {
        prepare(false);
        page.notes(marker);
        page.closeWithoutSaving();
        Assert.assertFalse(page.hasRow(marker));
    }

    @Test
    @Story("إلغاء تعديل حركة مملوكة للاختبار")
    public void FM_CancelEditWithoutSaving() {
        prepare(false);
        fill("100", false, false);
        FinancialTestData.Record saved = persist(false, "100");
        page.editOwned(marker);
        page.amount("250");
        page.notes(marker + "-UNSAVED");
        page.closeWithoutSaving();
        Map<String, Object> stored = data.read(saved);
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(
                new BigDecimal(stored.get("value").toString()).compareTo(new BigDecimal("100")), 0);
    }

    @Test
    @Story("إلغاء حذف حركة مملوكة للاختبار")
    public void FM_CancelDelete() {
        prepare(false);
        fill("100", false, false);
        FinancialTestData.Record saved = persist(false, "100");
        page.requestDeleteOwned(marker);
        page.confirmDelete(false);
        Assert.assertNotNull(data.read(saved));
        Assert.assertTrue(page.hasRow(marker));
    }

    @Test
    @Story("رفض قيمة مالية سالبة")
    public void FM_RejectNegativeAmount() {
        invalid("negative");
    }

    @Test
    @Story("رفض القيمة صفر")
    public void FM_RejectZeroAmount() {
        invalid("value");
    }

    @Test
    @Story("بنية قائمة الحركات وسجل الاختبار")
    public void FM_ListStructure() {
        prepare(false);
        fill("100", false, false);
        persist(false, "100");
        Assert.assertTrue(page.hasListHeaders());
        page.waitForRow(marker);
    }

    @Test
    @Story("تصدير قائمة الحركات PDF صالح")
    public void FM_ExportPdf() {
        export("pdf");
    }

    @Test
    @Story("تصدير قائمة الحركات Excel صالح")
    public void FM_ExportExcel() {
        export("excel");
    }

    private void export(String format) {
        prepare(false);
        fill("100", false, false);
        persist(false, "100");
        page.waitForRow(marker);
        FinancialExportAssertions.assertExportDownloaded(driver, () -> page.export(format), format);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanFinancialScenario() {
        if (driver == null) return;
        for (String handle : new ArrayList<>(driver.getWindowHandles()))
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        driver.switchTo().window(mainWindow);
        if (data != null) {
            if (page != null && marker.equals(page.captureMarker())) {
                data.rememberAttempts(page.capturedAttempts(), marker);
                page.awaitSavesSettled();
            }
            capture();
            if (page != null && marker.equals(page.captureMarker()))
                io.qameta.allure.Allure.addAttachment(
                        "Financial case requests",
                        "application/json",
                        new org.json.JSONArray(page.capturedAttempts()).toString(),
                        ".json");
        }
    }

    @AfterClass(alwaysRun = true)
    public void retryFinancialCleanup() {
        List<String> errors = new ArrayList<>();
        try {
            cleanFinancialScenario();
        } catch (AssertionError | RuntimeException e) {
            errors.add(e.toString());
        }
        for (var context : new ArrayList<>(pending))
            try {
                context.close();
                pending.remove(context);
                if (data == context) data = null;
            } catch (AssertionError | RuntimeException e) {
                errors.add(e.toString());
            }
        if (!errors.isEmpty()) throw new AssertionError(String.join("; ", errors));
    }
}
