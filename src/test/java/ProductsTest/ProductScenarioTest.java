package ProductsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Products.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

/** Business assertions and isolated scenarios; browser interactions belong in Page Objects. */
public abstract class ProductScenarioTest extends BaseTest {
    protected enum Kind {
        ITEM(Pages.ADDITEM, "Items"),
        CATEGORY(Pages.CATEGORIES, "ItemCategories"),
        UNIT(Pages.UNITS, "Units"),
        PRICE(Pages.PRICELIST, "PriceLists");
        final Pages route;
        final String endpoint;

        Kind(Pages route, String endpoint) {
            this.route = route;
            this.endpoint = endpoint;
        }
    }

    protected abstract Kind kind();

    protected abstract ProductsModulePage modulePage();

    protected ProductsTestData data;
    protected ProductsModulePage page;
    protected String marker, mainWindow;
    private final List<ProductsTestData> pending = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void awaitProductsSession() {
        requirePreparedSession();
        modulePage().prAwaitSession();
    }

    @BeforeMethod(alwaysRun = true)
    public void newProductScenario() {
        requirePreparedSession();
        marker = "QA-PR-" + UUID.randomUUID().toString().substring(0, 12);
        mainWindow = driver.getWindowHandle();
        page = modulePage();
        Assert.assertFalse(
                driver.getCurrentUrl().contains("/login"), "ERP session did not complete login");
        if (data == null) {
            data = new ProductsTestData(driver);
            pending.add(data);
        }
        data.beginScenario();
    }

    protected void prerequisites() {
        switch (kind()) {
            case ITEM -> {
                data.category();
                data.unit(1);
                data.unit(2);
                data.store(1);
            }
            case PRICE -> data.item(1);
            case CATEGORY -> data.mainCategory();
            case UNIT -> {}
        }
    }

    protected void openNew() {
        prerequisites();
        page.prOpen(kind().route);
        page.prNew();
        page.prCapture(marker);
    }

    protected void fill(String omitted) {
        if (!"name".equals(omitted)) page.prArabic(marker);
        page.prEnglish(marker + " EN");
        if (kind() != Kind.UNIT) page.prNotes(marker);
        switch (kind()) {
            case ITEM -> {
                AddProductsPage item = (AddProductsPage) page;
                if (!"category".equals(omitted)) page.prChoose("الفئة", data.category().name());
                if (!"unit".equals(omitted)) page.prChoose("الوحدة الأساسية", data.unit(1).name());
                if (!"barcode".equals(omitted))
                    page.prField(
                            "mainUnitBarcode",
                            "PR" + UUID.randomUUID().toString().replace("-", ""));
                page.prField("salesPrice", "100.25");
                page.prField("minSalesPrice", "90");
                page.prField("maxSalesPrice", "110");
                if (!"store".equals(omitted)) item.prStock(data.store(1).name(), "0", "0", "80");
            }
            case CATEGORY -> {
                page.prCheck("رئيسية", true);
            }
            case PRICE -> ((ProductListPage) page).prAddItem(data.item(1).name(), "125.50");
            case UNIT -> {}
        }
    }

    @SuppressWarnings("unchecked")
    protected List<ProductsTestData.Record> capture() {
        if (!marker.equals(page.prCaptureMarker())) return List.of();
        data.rememberAttempts(page.prAttempts(), marker);
        List<ProductsTestData.Record> records = new ArrayList<>();
        for (Map<String, Object> saved : page.prSaves()) {
            Map<String, Object> values = (Map<String, Object>) saved.get("values");
            String endpoint = (String) saved.get("endpoint");
            Assert.assertEquals(endpoint, kind().endpoint);
            String name =
                    java.util.stream.Stream.of("arabicName", "notes", "englishName")
                            .map(values::get)
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .filter(v -> v.startsWith(marker))
                            .findFirst()
                            .orElseThrow(
                                    () -> new AssertionError("Refuse unrelated product record"));
            data.track(endpoint, saved.get("id"), name, values);
            records.add(new ProductsTestData.Record(endpoint, saved.get("id"), name, values));
        }
        return records;
    }

    protected ProductsTestData.Record save(boolean print) {
        int before = page.prSaves().size();
        if (print) page.prSavePrint();
        else page.prSave();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(35))
                    .until(
                            d -> {
                                data.rememberAttempts(page.prAttempts(), marker);
                                return page.prSaves().size() > before;
                            });
        } catch (TimeoutException e) {
            throw new AssertionError("Product save failed: " + page.prSaveErrors(), e);
        }
        page.prSettled();
        List<ProductsTestData.Record> records = capture();
        ProductsTestData.Record record = records.get(records.size() - 1);
        Map<String, Object> stored = data.read(record);
        io.qameta.allure.Allure.addAttachment(
                "Saved product entity",
                "application/json",
                new org.json.JSONObject(
                                ProductsTestData.fields("sent", record.values(), "stored", stored))
                        .toString(),
                ".json");
        Assert.assertNotNull(stored);
        Assert.assertEquals(stored.get("arabicName"), record.name(), "مطابقة الاسم العربي");
        Object expectedEnglish = record.values().get("englishName");
        if (expectedEnglish == null || expectedEnglish.toString().isBlank())
            assertOptionalEnglish(stored);
        else Assert.assertEquals(stored.get("englishName"), expectedEnglish, "مطابقة الاسم الإنجليزي");
        if (record.values().containsKey("notes"))
            Assert.assertEquals(stored.get("notes"), record.values().get("notes"), "مطابقة الملاحظات");
        if (print) verifyReport(record);
        page.prWaitList();
        return record;
    }

    protected void verifyReport(ProductsTestData.Record record) {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> d.getWindowHandles().size() > 1);
        String report =
                driver.getWindowHandles().stream()
                        .filter(h -> !h.equals(mainWindow))
                        .findFirst()
                        .orElseThrow();
        driver.switchTo().window(report);
        try {
            page.prAwaitReport();
            page.AssertTextInReport(record.name());
            page.AssertTextInReport(record.values().get("englishName").toString());
            if (kind() == Kind.PRICE) {
                page.AssertTextInReport(data.item(1).name());
                page.AssertNumberInReport(
                        record.values().get("items") instanceof List<?> rows
                                ? ((Map<?, ?>) rows.get(0)).get("salesPrice").toString()
                                : "125.5");
            }
        } catch (RuntimeException | AssertionError e) {
            io.qameta.allure.Allure.addAttachment(
                    "Product report",
                    "image/png",
                    new java.io.ByteArrayInputStream(
                            ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)),
                    "png");
            throw e;
        } finally {
            driver.close();
            driver.switchTo().window(mainWindow);
        }
    }

    protected Map<String, Object> values() {
        return switch (kind()) {
            case ITEM -> data.itemValues();
            case CATEGORY -> ProductsTestData.fields("isMain", true);
            case UNIT -> ProductsTestData.fields("conversionRates", List.of());
            case PRICE ->
                    ProductsTestData.fields(
                            "pricelistDate",
                            java.time.LocalDate.now().toString(),
                            "items",
                            List.of(
                                    ProductsTestData.fields(
                                            "itemId",
                                            data.item(1).id(),
                                            "unitId",
                                            data.unit(1).id(),
                                            "salesPrice",
                                            125,
                                            "minSalesPrice",
                                            100,
                                            "maxSalesPrice",
                                            150)));
        };
    }

    protected ProductsTestData.Record seed() {
        prerequisites();
        return data.fixture(marker, kind().endpoint, values());
    }

    protected void locate(ProductsTestData.Record record) {
        page.prOpen(kind().route);
        page.prSearch(record.name());
        page.prWaitRow(record.name());
        page.prCapture(marker);
    }

    @io.qameta.allure.Step("مطابقة القيمة: المتوقع {expected}، الفعلي {actual}")
    protected void assertNumber(Object actual, String expected) {
        Assert.assertNotNull(actual);
        Assert.assertTrue(
                new BigDecimal(actual.toString()).compareTo(new BigDecimal(expected)) == 0,
                "Expected numeric value " + expected + " but stored " + actual);
    }

    protected void create() {
        openNew();
        fill(null);
        ProductsTestData.Record record = save(false);
        assertDetails(data.read(record));
    }

    @SuppressWarnings("unchecked")
    protected void assertDetails(Map<String, Object> stored) {
        switch (kind()) {
            case ITEM -> {
                Assert.assertEquals(stored.get("categoryId"), data.category().id());
                Assert.assertEquals(stored.get("mainUnitId"), data.unit(1).id());
                assertNumber(stored.get("salesPrice"), "100.25");
                List<Map<String, Object>> stores = (List<Map<String, Object>>) stored.get("stores");
                Assert.assertEquals(stores.size(), 1);
                Assert.assertEquals(stores.get(0).get("storeId"), data.store(1).id(), "مطابقة المخزن");
                assertNumber(stores.get(0).get("costPrice"), "80");
            }
            case CATEGORY -> Assert.assertEquals(stored.get("isMain"), true);
            case PRICE -> {
                List<Map<String, Object>> items = (List<Map<String, Object>>) stored.get("items");
                Assert.assertEquals(items.size(), 1);
                Assert.assertEquals(items.get(0).get("itemId"), data.item(1).id(), "مطابقة الصنف");
                Assert.assertEquals(items.get(0).get("unitId"), data.unit(1).id(), "مطابقة وحدة القياس");
                assertNumber(items.get(0).get("salesPrice"), "125.50");
            }
            case UNIT -> Assert.assertTrue(stored.get("id") != null, "التحقق من هوية السجل");
        }
    }

    protected void edit(boolean print) {
        ProductsTestData.Record initial = seed();
        locate(initial);
        page.prEdit(initial.name());
        page.prArabic(marker + " EDIT");
        page.prEnglish(marker + " EDIT EN");
        if (kind() == Kind.PRICE)
            ((ProductListPage) page).prItemPrice(data.item(1).name(), "250.75");
        ProductsTestData.Record updated = save(print);
        Assert.assertEquals(updated.id(), initial.id());
        Assert.assertEquals(data.read(updated).get("arabicName"), marker + " EDIT", "مطابقة الاسم العربي");
    }

    protected void deleteOwn() {
        ProductsTestData.Record record = seed();
        locate(record);
        page.prDelete(record.name());
        page.prConfirm(true);
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> data.read(record) == null);
        Assert.assertNull(data.read(record));
    }

    protected void required(String omitted, String expected) {
        openNew();
        fill(omitted);
        page.prSave();
        String messages = page.prValidation();
        page.prSettled();
        capture();
        Assert.assertTrue(page.prSaves().isEmpty(), "Invalid record was saved");
        Assert.assertTrue(page.prFormPresent(), "Invalid form closed");
        boolean relevant =
                messages.replace('ة', 'ه').contains(expected.replace('ة', 'ه'))
                        || ("barcode".equals(omitted) && messages.contains("mainUnitBarcode"));
        Assert.assertTrue(relevant, "Expected " + expected + " validation: " + messages);
    }

    protected void commonCancelNew() {
        openNew();
        page.prArabic(marker);
        page.prDiscard();
        Assert.assertTrue(page.prSaves().isEmpty());
    }

    protected void commonCancelEdit() {
        ProductsTestData.Record record = seed();
        locate(record);
        page.prEdit(record.name());
        page.prArabic(marker + " UNSAVED");
        page.prDiscard();
        Assert.assertEquals(data.read(record).get("arabicName"), marker, "مطابقة الاسم العربي");
        Assert.assertTrue(page.prSaves().isEmpty());
    }

    protected void commonCancelDelete() {
        ProductsTestData.Record record = seed();
        locate(record);
        page.prDelete(record.name());
        page.prConfirm(false);
        Assert.assertNotNull(data.read(record));
        Assert.assertTrue(page.prHasRow(record.name()));
    }

    protected void commonSearch() {
        ProductsTestData.Record record = seed();
        locate(record);
        Assert.assertTrue(page.prHasRow(marker));
        page.prSearch(marker + "-NO-MATCH");
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d -> !page.prHasRow(marker));
        page.prSearch(marker);
        page.prWaitRow(marker);
    }

    protected void commonList() {
        ProductsTestData.Record record = seed();
        locate(record);
        Assert.assertTrue(page.prHeaders().size() >= 2);
        Assert.assertTrue(page.prHasRow(marker));
    }

    protected void commonExport(String format) {
        ProductsTestData.Record record = seed();
        locate(record);
        ProductsExportAssertions.assertExportDownloaded(
                driver, () -> page.prExport(format), format);
    }

    private void assertOptionalEnglish(Map<String, Object> stored) {
        Object english = stored.get("englishName");
        Assert.assertTrue(
                english == null
                        || english.toString().isBlank()
                        || english.equals(stored.get("arabicName")),
                "Unexpected English-name fallback: " + english);
    }

    protected void optionalEnglish() {
        openNew();
        fill(null);
        page.prEnglish("");
        ProductsTestData.Record record = save(false);
        assertOptionalEnglish(data.read(record));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanProductScenario() {
        if (driver == null) return;
        for (String handle : new ArrayList<>(driver.getWindowHandles()))
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        driver.switchTo().window(mainWindow);
        if (page != null && data != null && marker.equals(page.prCaptureMarker())) {
            data.rememberAttempts(page.prAttempts(), marker);
            page.prSettled();
            capture();
            data.recoverAttemptedItems(page.prAttempts());
            io.qameta.allure.Allure.addAttachment(
                    "Product case requests",
                    "application/json",
                    new org.json.JSONArray(page.prAttempts()).toString(),
                    ".json");
        }
    }

    @AfterClass(alwaysRun = true)
    public void retryProductCleanup() {
        List<String> errors = new ArrayList<>();
        try {
            cleanProductScenario();
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
