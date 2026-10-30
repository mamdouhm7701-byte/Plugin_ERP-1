/* شرح الملف: اختبار سلامة تجهيز الخطوات المشتركة للحالات ثم تنظيف بياناتها، ضمن المبيعات.
 */
package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

public abstract class SalesScenarioTest extends BaseTest {
    final org.openqa.selenium.WebDriver caseDriver() {
        return driver;
    }

    protected SalesDocumentKind kind() {
        return SalesDocumentKind.INVOICE;
    }

    protected SalesModulePage page;
    protected SalesTestData data;
    protected SalesTestData.Record item, customer, agent, payment, priceList, discountType;
    protected String marker, mainWindow, paymentType;
    protected java.util.function.Consumer<Map<String, Object>> printContentAssertions =
            stored -> {};
    protected final Map<Integer, BigDecimal> initialBalances = new LinkedHashMap<>();
    protected final Set<Object> allowedItems = new HashSet<>();
    private final List<SalesTestData> pending = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void awaitSalesSession() {
        requirePreparedSession();
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        new SalesModulePage(driver).slAwaitSession();
    }

    @BeforeMethod(alwaysRun = true)
    public void beginSalesScenario() {
        requirePreparedSession();
        printContentAssertions = stored -> {};
        page =
                switch (kind()) {
                    case INVOICE -> new Plugin_ERP.System_Modules.Login.Invoice.Invoice(driver);
                    case QUOTATION -> new QuotationPage(driver);
                    case ORDER -> new SalesOrderPage(driver);
                    case RETURN -> new SalesReturnPage(driver);
                };
        marker = "QA-SL-" + UUID.randomUUID().toString().substring(0, 12);
        mainWindow = page.slWindowHandle();
        if (data == null || !pending.contains(data)) {
            data = new SalesTestData(driver);
            pending.add(data);
        }
        data.beginScenario();
        initialBalances.clear();
        allowedItems.clear();
        item = null;
        payment = null;
        priceList = null;
        discountType = null;
    }

    protected SalesTestData.Record ownItem(int index) {
        var record = data.item(index);
        allowedItems.add(record.id());
        return record;
    }

    protected TestSupport.SalesTaxProfile salesTax;

    protected String salesNet(String beforeTax) {
        return salesTax.net(beforeTax);
    }

    protected SalesTestData.Record preparedScenarioItem() {
        return ownItem(1);
    }

    protected void prepare(String paymentType) {
        prepare(paymentType, false, false);
    }

    protected void prepare(String paymentType, boolean usePriceList, boolean useDiscountType) {
        prepare(paymentType, usePriceList, useDiscountType, true);
    }

    protected void prepare(
            String paymentType,
            boolean usePriceList,
            boolean useDiscountType,
            boolean selectCustomer) {
        this.paymentType = paymentType;
        item = preparedScenarioItem();
        allowedItems.add(item.id());
        customer = data.customer();
        var customerProfile = data.read(customer);
        Assert.assertEquals(
                customerProfile.get("isTaxable"),
                Boolean.TRUE,
                "Normal scenario customer must be taxable");
        Assert.assertEquals(
                customerProfile.get("isOnTheFly"),
                Boolean.FALSE,
                "Normal scenario customer must retain its own ledger");
        Assert.assertEquals(
                customerProfile.get("creditNote"),
                Boolean.TRUE,
                "Normal scenario customer must allow collection receipts");
        agent = data.agent();
        String capability =
                switch (kind()) {
                    case QUOTATION -> "quotation";
                    case RETURN -> "salesReturn";
                    case ORDER -> "salesOrder";
                    case INVOICE -> paymentType.equals("Cash") ? "cashSales" : "creditSales";
                };
        Assert.assertEquals(
                customerProfile.get(capability),
                true,
                "Owned customer fixture does not permit " + kind());
        if (paymentType.equals("Cash") && kind() != SalesDocumentKind.QUOTATION)
            payment = data.payment();
        if (usePriceList) priceList = data.priceList();
        if (useDiscountType) discountType = data.discountType();
        page.slOpen(kind().route + "/new");
        page.slFormReady();
        if (kind() == SalesDocumentKind.RETURN && paymentType.equals("Cash")) {
            var beforeFunding = number(data.balance(item, 1));
            data.seedInvoice(999, "Sales", page.slDocumentDate(), "3", "300");
            awaitOwnedStock(
                    beforeFunding.subtract(new BigDecimal("3")), "Funding invoice stock effect");
        }
        for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slChoose("المخزن", data.store(1).name());
        if (kind() != SalesDocumentKind.QUOTATION)
            page.slChoose("نوع السداد", paymentType.equals("Cash") ? "نقدي" : "آجل");
        if (selectCustomer) page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        if (usePriceList) page.slChoose("قوائم الأسعار", priceList.name());
        else if (page.slHasChoice("قوائم الأسعار") && !page.slChoiceEmpty("قوائم الأسعار"))
            page.slClearChoice("قوائم الأسعار");
        page.slNotes(marker);
        salesTax = TestSupport.SalesTaxProfile.capture(driver);
    }

    protected void add(SalesTestData.Record record, String mode) {
        switch (mode) {
            case "name" -> page.slAddName(record.name());
            case "barcode" ->
                    page.slAddBarcode(
                            record.values().get("mainUnitBarcode").toString(), record.name());
            default -> page.slAddPicker(record.name());
        }
    }

    protected void fill(String mode) {
        add(item, mode);
        page.slQuantity(item.name(), "3");
        page.slWaitNet(salesNet(priceList == null ? "300" : "375"));
    }

    @SuppressWarnings("unchecked")
    protected List<SalesTestData.Record> capture() {
        if (!marker.equals(page.slCaptureMarker())) return List.of();
        data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
        List<SalesTestData.Record> records = new ArrayList<>();
        for (var saved : page.slSaves()) {
            String endpoint = (String) saved.get("endpoint");
            Assert.assertTrue(
                    Arrays.stream(SalesDocumentKind.values())
                            .anyMatch(k -> k.endpoint.equals(endpoint)),
                    "Unexpected captured sales endpoint: " + endpoint);
            Map<String, Object> values = (Map<String, Object>) saved.get("values");
            String note =
                    values.get("notes") instanceof String text && text.startsWith(marker)
                            ? text
                            : marker;
            // A failed transition can leave the previous document's capture active.
            // Cleanup must keep its real type, not relabel it as the next journey step.
            data.track(endpoint, saved.get("id"), note, values);
            records.add(new SalesTestData.Record(endpoint, saved.get("id"), note, values));
        }
        data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
        return records;
    }

    @SuppressWarnings("unchecked")
    protected SalesTestData.Record save(boolean print) {
        return new SalesPersistenceChecks(this).save(print);
    }

    protected SalesTestData.Record create(String mode, String paymentType, boolean print) {
        prepare(paymentType);
        var before = data.read(customer);
        fill(mode);
        var record = save(print);
        assertStock("3");
        String delta =
                switch (kind()) {
                    case INVOICE -> paymentType.equals("Cash") ? "0" : "300";
                    case RETURN -> paymentType.equals("Cash") ? "0" : "-300";
                    case ORDER -> paymentType.equals("Cash") ? "-300" : "0";
                    case QUOTATION -> "0";
                };
        assertCustomerDelta(before, delta);
        return record;
    }

    protected static BigDecimal number(Object value) {
        Assert.assertNotNull(value);
        return new BigDecimal(value.toString().replace(",", ""));
    }

    @io.qameta.allure.Step("التحقق من المبلغ أو الكمية: المتوقع {expected}، الفعلي {actual}")
    protected static void sameNumber(Object actual, String expected) {
        Assert.assertEquals(
                number(actual).compareTo(new BigDecimal(expected)),
                0,
                "Expected " + expected + ", actual " + actual);
    }

    protected static BigDecimal customerBalance(Map<String, Object> value) {
        return number(value.get("currentDebit"))
                .add(number(value.get("prevDebit")))
                .subtract(number(value.get("currentCredit")))
                .subtract(number(value.get("prevCredit")));
    }

    @io.qameta.allure.Step("التحقق من تغير رصيد العميل: المتوقع {expected}")
    protected void assertCustomerDelta(Map<String, Object> before, String expected) {
        String expectedWithTax = salesNet(expected);
        var target = customerBalance(before).add(new BigDecimal(expectedWithTax));
        var last = new java.util.concurrent.atomic.AtomicReference<Map<String, Object>>();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(
                            d -> {
                                last.set(data.read(customer));
                                return Plugin_ERP.Util.DecimalAmountComparison.matches(
                                        customerBalance(last.get()), target);
                            });
        } catch (TimeoutException failure) {
            var evidence =
                    SalesTestData.fields(
                            "before",
                            before,
                            "after",
                            last.get(),
                            "expectedDelta",
                            expectedWithTax);
            Allure.addAttachment(
                    "Owned customer balance evidence",
                    "application/json",
                    new org.json.JSONObject(evidence).toString(),
                    ".json");
            Allure.addAttachment(
                    "Owned customer account evidence",
                    "application/json",
                    new org.json.JSONObject(data.read(data.account("customer"))).toString(),
                    ".json");
            if (payment != null)
                Allure.addAttachment(
                        "Owned payment bank account evidence",
                        "application/json",
                        new org.json.JSONObject(data.read(data.account("bank"))).toString(),
                        ".json");
            throw new AssertionError(
                    "Expected customer balance delta "
                            + expectedWithTax
                            + ", actual "
                            + customerBalance(last.get()).subtract(customerBalance(before)),
                    failure);
        }
    }

    @io.qameta.allure.Step("التحقق من أثر الفاتورة على كمية المخزون: {quantity}")
    protected void assertStock(String quantity) {
        BigDecimal qty = new BigDecimal(quantity), expected = initialBalances.get(1);
        if (kind() == SalesDocumentKind.INVOICE) expected = expected.subtract(qty);
        else if (kind() == SalesDocumentKind.RETURN) expected = expected.add(qty);
        final BigDecimal balance = expected;
        awaitOwnedStock(balance, "Sales document stock effect");
        sameNumber(data.balance(item, 2), initialBalances.get(2).toPlainString());
    }

    protected void awaitOwnedStock(BigDecimal expected, String reason) {
        var actual = new java.util.concurrent.atomic.AtomicReference<BigDecimal>();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(
                            d -> {
                                actual.set(number(data.balance(item, 1)));
                                return actual.get().compareTo(expected) == 0;
                            });
        } catch (TimeoutException failure) {
            Allure.addAttachment(
                    "Owned stock evidence",
                    "application/json",
                    new org.json.JSONObject(
                                    SalesTestData.fields(
                                            "reason",
                                            reason,
                                            "expected",
                                            expected,
                                            "actual",
                                            actual.get(),
                                            "document",
                                            page.slDocument()))
                            .toString(),
                    ".json");
            throw new AssertionError(
                    reason + ": expected " + expected + ", actual " + actual.get(), failure);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanSalesScenario(ITestResult result) {
        if (data == null || page == null || pending.isEmpty()) return;
        List<String> failures = new ArrayList<>();
        if (result != null && !result.isSuccess())
            try {
                Allure.addAttachment(
                        "Sales failure DOM", "text/html", page.slDiagnosticMarkup(), ".html");
                Allure.addAttachment(
                        "Sales lookup diagnostics",
                        "application/json",
                        page.slDiagnostics(),
                        ".json");
                Allure.addAttachment(
                        "Sales form state",
                        "application/json",
                        new org.json.JSONObject(
                                        SalesTestData.fields(
                                                "document",
                                                page.slDocument(),
                                                "totals",
                                                page.slTotals()))
                                .toString(),
                        ".json");
            } catch (RuntimeException ignored) {
            }
        try {
            page.slCloseAuxiliaryWindows(mainWindow);
            if (pending.contains(data)) {
                page.slSettled();
                capture();
            }
        } catch (RuntimeException | AssertionError e) {
            failures.add(e.getMessage());
        }
        if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
    }

    @AfterClass(alwaysRun = true)
    public void retrySalesCleanup() {
        List<String> failures = new ArrayList<>();
        try {
            cleanSalesScenario(null);
        } catch (RuntimeException | AssertionError e) {
            failures.add(e.getMessage());
        }
        for (var context : new ArrayList<>(pending))
            try {
                context.close();
                pending.remove(context);
            } catch (RuntimeException | AssertionError e) {
                failures.add(e.getMessage());
            }
        if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
    }

    protected void rejectSave() {
        var before = data.documentIdentities();
        if (!page.slSaveDisabled()) {
            page.slSave(false);
            Assert.assertFalse(
                    page.slValidation().isBlank(), "Expected a visible validation error");
            page.slSettled();
        }
        capture();
        data.verifyNoNewDocuments(before);
        Assert.assertTrue(page.slSaves().isEmpty(), "Invalid sales document was saved");
        assertStock("0");
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Object> firstLine(Map<String, Object> document) {
        return (Map<String, Object>) ((List<?>) document.get("lines")).get(0);
    }
}
