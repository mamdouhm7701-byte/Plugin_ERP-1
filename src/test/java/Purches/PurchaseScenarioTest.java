package Purches;

import Plugin_ERP.System_Modules.Login.purches.PurchaseModulePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

/** Each class owns one page fixture scope; each case owns its supplier and items. */
public abstract class PurchaseScenarioTest extends BaseTest {
    protected abstract PurchaseDocumentKind kind();

    protected PurchaseDocumentKind convertedKind;

    protected PurchaseDocumentKind documentKind() {
        return convertedKind == null ? kind() : convertedKind;
    }

    protected PurchaseModulePage page;
    protected PurchaseTestData data;
    protected PurchaseTestData.Record item, supplier, agent, payment;
    protected String marker, mainWindow, paymentType;
    protected java.util.function.Consumer<Map<String, Object>> printContentAssertions =
            stored -> {};
    protected final Map<Integer, BigDecimal> initialBalances = new LinkedHashMap<>();
    protected final Set<Object> allowedItems = new HashSet<>();

    @BeforeClass(alwaysRun = true)
    public void awaitPurchaseSession() {
        requirePreparedSession();
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        new PurchaseModulePage(driver).puAwaitSession();
    }

    @BeforeMethod(alwaysRun = true)
    public void beginPurchaseScenario() {
        requirePreparedSession();
        printContentAssertions = stored -> {};
        convertedKind = null;
        page = new PurchaseModulePage(driver);
        mainWindow = driver.getWindowHandle();
        marker = "QA-PU-" + UUID.randomUUID().toString().substring(0, 12);
        if (data == null) data = new PurchaseTestData(driver);
        data.beginScenario();
        initialBalances.clear();
        allowedItems.clear();
        payment = null;
    }

    protected PurchaseTestData.Record ownItem(int index) {
        var record = data.item(index);
        allowedItems.add(record.id());
        return record;
    }

    protected void prepare(String paymentType) {
        this.paymentType = paymentType;
        item = ownItem(1);
        supplier = data.supplier();
        agent = data.agent();
        if (paymentType.equals("Cash")) payment = data.payment();
        page.puOpen(documentKind().route + "/new");
        page.puFormReady();
        try {
            page.puAwaitOwnedLookups(data.store(1).id(), supplier.id(), agent.id());
        } catch (org.openqa.selenium.TimeoutException initialLoad) {
            Allure.addAttachment("Purchase lookup initialization retry", initialLoad.getMessage());
            page.puOpen(documentKind().route + "/new");
            page.puFormReady();
            page.puAwaitOwnedLookups(data.store(1).id(), supplier.id(), agent.id());
        }
        for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        page.puChoose("المخزن", data.store(1).name());
        page.puChoose("نوع السداد", paymentType.equals("Cash") ? "نقدي" : "آجل");
        page.puChoose("المورد", supplier.name());
        page.puChoose("مندوب", agent.name());
        page.puNotes(marker);
    }

    protected void add(PurchaseTestData.Record record, String mode) {
        switch (mode) {
            case "name" -> page.puAddName(record.name());
            case "barcode" ->
                    page.puAddBarcode(
                            record.values().get("mainUnitBarcode").toString(), record.name());
            default -> page.puAddPicker(record.name());
        }
    }

    protected void fill(String mode) {
        add(item, mode);
        page.puQuantity(item.name(), "3");
        page.puSetLine(
                item.name(),
                documentKind() == PurchaseDocumentKind.INVOICE ? "سعر الشراء" : "السعر",
                "100");
        page.puWaitNet("300");
    }

    @SuppressWarnings("unchecked")
    protected List<PurchaseTestData.Record> capture() {
        if (!marker.equals(page.puCaptureMarker())) return List.of();
        data.rememberAttempts(page.puAttempts(), page.puSaves(), marker);
        var records = new ArrayList<PurchaseTestData.Record>();
        for (var saved : page.puSaves()) {
            Assert.assertEquals(saved.get("endpoint"), documentKind().endpoint);
            var values = (Map<String, Object>) saved.get("values");
            String note =
                    values.get("notes") instanceof String text && text.startsWith(marker)
                            ? text
                            : marker;
            data.track(documentKind().endpoint, saved.get("id"), note, values);
            records.add(
                    new PurchaseTestData.Record(
                            documentKind().endpoint, saved.get("id"), note, values));
        }
        return records;
    }

    @SuppressWarnings("unchecked")
    protected PurchaseTestData.Record save(boolean print) {
        var doc = page.puDocument();
        if (paymentType.equals("Cash") && ((List<?>) doc.get("payments")).isEmpty())
            page.puAddOtherPayment(payment.name(), page.puTotals().get("الصافي"));
        int count = page.puSaves().size();
        page.puSave(print);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(45))
                    .until(
                            d -> {
                                data.rememberAttempts(page.puAttempts(), page.puSaves(), marker);
                                return page.puSaves().size() > count;
                            });
        } catch (org.openqa.selenium.TimeoutException failure) {
            throw new AssertionError(
                    "Purchase document was not saved: " + page.puSaveErrors(), failure);
        }
        page.puSettled();
        var records = capture();
        var record = records.get(records.size() - 1);
        var stored = data.read(record);
        Allure.addAttachment(
                "Saved purchase document",
                "application/json",
                new org.json.JSONObject(
                                PurchaseTestData.fields("sent", record.values(), "stored", stored))
                        .toString(),
                ".json");
        Assert.assertNotNull(stored);
        Assert.assertEquals(stored.get("notes"), record.name(), "مطابقة الملاحظات");
        Assert.assertEquals(stored.get("storeId"), data.store(1).id(), "مطابقة المخزن");
        Assert.assertEquals(stored.get("dealerId"), supplier.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(stored.get("agentId"), agent.id(), "مطابقة المندوب");
        Assert.assertTrue(
                documentKind().type.equalsIgnoreCase(String.valueOf(stored.get("transactionType"))),
                "Persisted purchase document has the wrong transaction type");
        var lines = (List<Map<String, Object>>) stored.get("lines");
        Assert.assertFalse(lines.isEmpty());
        Assert.assertTrue(
                lines.stream().allMatch(l -> allowedItems.contains(l.get("itemId"))),
                "Unexpected item in purchase document");
        if (paymentType.equals("Cash")) {
            var payments = (List<Map<String, Object>>) stored.get("payments");
            Assert.assertFalse(payments.isEmpty());
            Assert.assertTrue(
                    payments.stream()
                            .allMatch(p -> payment.id().equals(p.get("otherPaymentMethodId"))));
            var paid =
                    payments.stream()
                            .map(p -> number(p.get("value")))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            sameNumber(paid, stored.get("net").toString());
            Assert.assertEquals(stored.get("paymentType"), "Cash", "مطابقة نوع السداد");
        }
        if (print)
            page.puVerifyPrintedReport(
                    mainWindow, item.name(), () -> printContentAssertions.accept(stored));
        else page.puDismiss();
        return record;
    }

    protected PurchaseTestData.Record create(String mode, String pay, boolean print) {
        prepare(pay);
        var before = data.read(supplier);
        fill(mode);
        var saved = save(print);
        assertStock("3");
        if (!pay.equals("Cash"))
            assertSupplierDelta(
                    before,
                    documentKind() == PurchaseDocumentKind.INVOICE
                            ? "-300"
                            : documentKind() == PurchaseDocumentKind.RETURN ? "300" : "0");
        else if (documentKind() != PurchaseDocumentKind.ORDER) assertSupplierDelta(before, "0");
        return saved;
    }

    protected static BigDecimal number(Object value) {
        Assert.assertNotNull(value);
        return new BigDecimal(value.toString().replace(",", ""));
    }

    @io.qameta.allure.Step("مطابقة القيمة: المتوقع {expected}، الفعلي {actual}")
    protected static void sameNumber(Object actual, String expected) {
        Assert.assertEquals(
                number(actual).compareTo(new BigDecimal(expected)),
                0,
                "Expected " + expected + ", actual " + actual);
    }

    protected static BigDecimal signedBalance(Map<String, Object> value) {
        return number(value.get("currentDebit"))
                .add(number(value.get("prevDebit")))
                .subtract(number(value.get("currentCredit")))
                .subtract(number(value.get("prevCredit")));
    }

    @io.qameta.allure.Step("التحقق من تغير رصيد المورد: المتوقع {delta}")
    protected void assertSupplierDelta(Map<String, Object> before, String delta) {
        var expected = signedBalance(before).add(number(delta));
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> signedBalance(data.read(supplier)).compareTo(expected) == 0);
    }

    @io.qameta.allure.Step("التحقق من أثر المشتريات على كمية المخزون: {quantity}")
    protected void assertStock(String quantity) {
        var expected = initialBalances.get(1);
        if (documentKind() == PurchaseDocumentKind.INVOICE)
            expected = expected.add(number(quantity));
        else if (documentKind() == PurchaseDocumentKind.RETURN)
            expected = expected.subtract(number(quantity));
        final var target = expected;
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> number(data.balance(item, 1)).compareTo(target) == 0);
        sameNumber(data.balance(item, 2), initialBalances.get(2).toPlainString());
    }

    protected void rejectSave() {
        var before = data.documentIdentities();
        if (!page.puSaveDisabled()) {
            page.puSave(false);
            page.puWaitSaveOutcome();
            page.puSettled();
            var captured = capture();
            for (var record : captured)
                Allure.addAttachment(
                        "Unexpected saved purchase",
                        "application/json",
                        new org.json.JSONObject(
                                        PurchaseTestData.fields(
                                                "sent",
                                                record.values(),
                                                "stored",
                                                data.read(record)))
                                .toString(),
                        ".json");
            Assert.assertTrue(page.puSaves().isEmpty(), "Invalid purchase was saved");
        }
        data.verifyNoNewDocuments(before);
        assertStock("0");
    }

    @AfterMethod(alwaysRun = true)
    public void finishPurchaseCase(ITestResult result) {
        if (data == null || page == null) return;
        if (result != null && !result.isSuccess())
            try {
                Allure.addAttachment(
                        "Purchase failure DOM", "text/html", page.puDiagnosticMarkup(), ".html");
                Allure.addAttachment(
                        "Purchase diagnostics", "application/json", page.puDiagnostics(), ".json");
            } catch (RuntimeException ignored) {
            }
        page.puCloseAuxiliaryWindows(mainWindow);
        page.puSettled();
        capture();
    }

    @AfterClass(alwaysRun = true)
    public void cleanPurchasePage() {
        if (data == null) return;
        List<String> failures = new ArrayList<>();
        try {
            finishPurchaseCase(null);
        } catch (RuntimeException | AssertionError failure) {
            failures.add(failure.toString());
        }
        try {
            data.close();
            data = null;
        } catch (RuntimeException | AssertionError failure) {
            failures.add(failure.toString());
        }
        if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
    }
}
