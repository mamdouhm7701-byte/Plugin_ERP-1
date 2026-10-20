package SpecialSalesTest;

import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;

import io.qameta.allure.Allure;

import org.openqa.selenium.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.*;

/** Page-scoped references, case-scoped mutable data, and unconditional group cleanup. */
public abstract class SpecialSalesScenario extends BaseTest {
    protected SpecialSalesPage page;
    protected SpecialSalesTestData data;
    protected SpecialSalesTestData.Record item, customer, defaults;
    private String mainWindow;

    protected abstract SpecialSalesPage createPage();

    @BeforeClass(alwaysRun = true)
    public void fixtures() {
        page = createPage();
        page.awaitSession();
        data = new SpecialSalesTestData(driver);
        mainWindow = driver.getWindowHandle();
    }

    @BeforeMethod(alwaysRun = true)
    public void scenario() {
        data.beginScenario();
        customer = data.customer();
        item = data.item(1);
        defaults = data.configureOwnedDefaults(customer);
        restartPage();
    }

    protected void restartPage() {
        page.open();
        page.capture("QA-SP-" + UUID.randomUUID());
        if (page instanceof PointOfSalePage pos) pos.showInvoiceDetails();
        page.notes(page.captureMarker());
    }

    protected void prepareSave() {
        data.rememberAttempts(
                List.of(
                        SpecialSalesTestData.fields(
                                "endpoint", page.endpoint(), "values", page.document())),
                page.captureMarker());
    }

    protected void add() {
        page.addItem(
                item.name(),
                String.valueOf(item.values().get("mainUnitBarcode")),
                data.category().name());
    }

    protected List<?> lines() {
        return (List<?>) page.document().get("lines");
    }

    @SuppressWarnings("unchecked")
    protected SpecialSalesTestData.Record saved() {
        page.awaitSave();
        String marker = page.captureMarker();
        data.rememberAttempts(page.attempts(), marker);
        var save = page.saves().getLast();
        var values = (Map<String, Object>) save.get("values");
        data.track(page.endpoint(), save.get("id"), marker, values);
        return new SpecialSalesTestData.Record(page.endpoint(), save.get("id"), marker, values);
    }

    protected void number(Object actual, String expected) {
        org.testng.Assert.assertEquals(
                new BigDecimal(actual.toString()).compareTo(new BigDecimal(expected)),
                0,
                "Actual=" + actual + ", expected=" + expected);
    }

    protected void customerBalance(String expected) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(
                            driver, java.time.Duration.ofSeconds(45))
                    .until(
                            d -> {
                                var value = data.read(customer);
                                var actual =
                                        new BigDecimal(value.get("currentDebit").toString())
                                                .add(
                                                        new BigDecimal(
                                                                value.get("prevDebit").toString()))
                                                .subtract(
                                                        new BigDecimal(
                                                                value.get("currentCredit")
                                                                        .toString()))
                                                .subtract(
                                                        new BigDecimal(
                                                                value.get("prevCredit")
                                                                        .toString()));
                                return actual.compareTo(new BigDecimal(expected)) == 0;
                            });
        } catch (TimeoutException failure) {
            throw new AssertionError(
                    "Owned customer balance did not reach "
                            + expected
                            + "; actual DTO="
                            + new org.json.JSONObject(data.read(customer)),
                    failure);
        } finally {
            Allure.addAttachment(
                    "رصيد عميل الاختبار",
                    "application/json",
                    new org.json.JSONObject(data.read(customer)).toString(),
                    ".json");
        }
    }

    @SuppressWarnings("unchecked")
    @AfterMethod(alwaysRun = true)
    public void evidence(ITestResult result) {
        try {
            if (page != null) {
                String marker = page.captureMarker();
                if (marker != null) data.rememberAttempts(page.attempts(), marker);
                page.settled();
                if (marker != null) {
                    data.rememberAttempts(page.attempts(), marker);
                    for (var saved : page.saves())
                        data.track(
                                (String) saved.get("endpoint"),
                                saved.get("id"),
                                marker,
                                (Map<String, Object>) saved.get("values"));
                }
                Allure.addAttachment(
                        "بيانات الشاشة والطلبات",
                        "application/json",
                        new org.json.JSONObject(page.diagnostics()).toString(2),
                        ".json");
                if (!result.isSuccess()) {
                    Allure.addAttachment(
                            "صورة الفشل",
                            "image/png",
                            new java.io.ByteArrayInputStream(
                                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)),
                            ".png");
                    Allure.addAttachment(
                            "HTML الشاشة وقت الفشل", "text/html", page.markup(), ".html");
                }
            }
        } finally {
            try {
                if (defaults != null) {
                    data.restoreDefaults(defaults);
                    defaults = null;
                }
            } finally {
                if (page != null) {
                    page.closeModal();
                    page.closeExtraWindows(mainWindow);
                }
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (data != null) data.close();
    }
}
