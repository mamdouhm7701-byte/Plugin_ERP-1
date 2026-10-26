package StoreTest;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import TestSupport.BasicDataFixtures;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

public abstract class InventoryCoverageScenario extends BaseTest {
    protected abstract String endpoint();

    protected abstract String route();

    protected abstract String title();

    protected abstract int stockDirection();

    protected StoreTestData refs;
    protected BasicDataFixtures documents;
    protected ScreenPage page;
    protected StoreTestData.Record item;
    protected String marker;
    protected BigDecimal before;

    @BeforeClass(alwaysRun = true)
    public void ownInventoryReferences() {
        requirePreparedSession();
        page = new ScreenPage(driver);
        refs = new StoreTestData(driver);
        documents = new BasicDataFixtures(driver);
    }

    @BeforeMethod(alwaysRun = true)
    public void beginInventoryCase() {
        requirePreparedSession();
        refs.beginScenario();
        documents.beginScenario();
    }

    protected void prepare(String quantity) {
        item = refs.item(1);
        before = new BigDecimal(refs.balance(item, 1).toString());
        marker = documents.reserveName(endpoint(), "inventory");
        page.open(route() + "/new", title());
        documents.installCapture();
        page.choose("المخزن", refs.store(1).name());
        page.notes(marker);
        page.addItem(item.name());
        page.tableLine(item.name(), stockDirection() == 0 ? "الرصيد الفعلي" : "الكمية", quantity);
    }

    protected BasicDataFixtures.Record save() {
        page.click("حفظ");
        if (stockDirection() == 0) {
            page.visibleElement(By.cssSelector(".swal2-cancel"));
            page.confirm(true);
        }
        var record = documents.awaitSaved(endpoint(), marker);
        var stored = documents.read(record);
        Assert.assertNotNull(stored);
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(stored.get("storeId"), refs.store(1).id());
        var lines = (List<?>) stored.get("lines");
        Assert.assertEquals(lines.size(), 1);
        Assert.assertEquals(((Map<?, ?>) lines.get(0)).get("itemId"), item.id());
        io.qameta.allure.Allure.addAttachment(
                "Saved inventory document",
                "application/json",
                new org.json.JSONObject(stored).toString(),
                ".json");
        page.dismissSuccess();
        return record;
    }

    protected void balance(BigDecimal expected) {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .withMessage("Inventory balance expected " + expected)
                .until(
                        d ->
                                new BigDecimal(refs.balance(item, 1).toString()).compareTo(expected)
                                        == 0);
    }

    @Test
    @Story("حفظ الجرد أو التسوية ومطابقة البند وأثر الكمية الكسرية")
    public void savesFractionalQuantityAndChecksStockEffect() {
        prepare("1.25");
        var record = save();
        var stored = documents.read(record);
        var line = (Map<?, ?>) ((List<?>) stored.get("lines")).get(0);
        Object value = line.get(stockDirection() == 0 ? "actualQuantity" : "quantity");
        Assert.assertEquals(new BigDecimal(value.toString()).compareTo(new BigDecimal("1.25")), 0);
        balance(before.add(new BigDecimal("1.25").multiply(BigDecimal.valueOf(stockDirection()))));
    }

    @Test
    @Story("إلغاء الإنشاء يحافظ على رصيد الصنف")
    public void cancelCreationPreservesStock() {
        prepare("2");
        page.open(
                route(),
                stockDirection() == 0
                        ? "محضر الجرد"
                        : stockDirection() < 0 ? "تسوية العجز" : "تسوية الفائض");
        Assert.assertNull(documents.captured(endpoint(), marker));
        balance(before);
    }

    @Test
    @Story("السجل المحفوظ يفتح من القائمة بنفس الهوية")
    public void reopensCapturedInventoryDocument() {
        prepare("2");
        var record = save();
        page.open(
                route() + "/edit/" + record.id(),
                stockDirection() == 0
                        ? "تعديل محضر"
                        : stockDirection() < 0 ? "تعديل تسوية عجز" : "تعديل تسوية فائض");
        Assert.assertEquals(page.value(By.cssSelector("textarea")), marker);
        Assert.assertNotNull(documents.read(record));
    }

    @AfterMethod(alwaysRun = true)
    public void collectInventoryWrites() {
        // A store may have only one open stocktaking; remove each owned draft before the next case.
        if (documents != null) {
            documents.awaitUiSettled();
            documents.collectUi();
            documents.close();
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanInventoryReferences() {
        if (documents != null) documents.close();
        if (refs != null) refs.close();
    }
}
