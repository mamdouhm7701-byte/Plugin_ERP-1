package FinancialMovementsTest;

import static TestSupport.BasicDataFixtures.fields;

import Plugin_ERP.Drivers.Chrome;
import Plugin_ERP.System_Modules.Login.BasicData.TreasuryHandoverPage;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Util.PropertyReader;
import TestSupport.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Real receipts/payments are authored by the same limited user as the owned handover. */
public final class TreasuryLedgerCoverageTest extends BaseTest {
    @Test
    public void reconcilesIndependentCashAndOtherPaymentLedgerInScreenAndPrint() {
        var basic = new BasicDataFixtures(driver);
        var refs = new FinancialTestData(driver);
        var treasury = new TreasuryFixtures(driver);
        WebDriver operator = null;
        boolean opened = false;
        try {
            basic.beginScenario();
            refs.beginScenario();
            var safe = basic.safe(1);
            var customer = refs.customer(1);
            var supplier = refs.supplier(1);
            var salesAgent = refs.agent(false);
            var purchaseAgent = refs.agent(true);
            var payment = refs.payment();
            String username = "qa_treasury_" + UUID.randomUUID().toString().replace("-", "");
            String password = UUID.randomUUID() + "Aa1!";
            var permissions =
                    List.of(
                            "viewCollectionTransactions",
                            "addCollectionTransactions",
                            "viewPaymentTransactions",
                            // The aggregate omits UserSafes without this permission, even for an
                            // owned open handover.
                            "addPaymentTransactions",
                            "EnableSaveCollectionWithoutAllocation",
                            "viewCustomers",
                            "viewSuppliers",
                            "viewAgents",
                            "viewSafes",
                            "viewSafeTransactions",
                            "viewPaymentMethods");
            var user =
                    basic.fixture(
                            "ledger-user",
                            "users",
                            fields(
                                    "userName",
                                    username,
                                    "passwordHash",
                                    password,
                                    "isAdmin",
                                    false,
                                    "isActive",
                                    true,
                                    "agentId",
                                    null,
                                    "maxAllowedDiscount",
                                    0,
                                    "paymentMethodType",
                                    "Safe",
                                    "safeId",
                                    safe.id(),
                                    "permissions",
                                    permissions.stream()
                                            .map(id -> fields("permissionId", id))
                                            .toList(),
                                    "branches",
                                    List.of(
                                            fields(
                                                    "branchId",
                                                    new FixtureSession(driver).branchId)),
                                    "defaultOtherPaymentMethods",
                                    List.of(fields("paymentMethodId", payment.id())),
                                    "imageUrl",
                                    "",
                                    "phone",
                                    "",
                                    "email",
                                    "",
                                    "base64Image",
                                    "",
                                    "imageExtension",
                                    ""));
            opened = true;
            Object handover = treasury.open(safe, user, 1000);
            Object openingLookup =
                    basic.query(
                            "Aggregates/salesViewModel?userId="
                                    + user.id()
                                    + "&isClosed=false&transactionType=sales&dealerType=sales&agentId=");
            io.qameta.allure.Allure.addAttachment(
                    "Owned treasury opening and lookup",
                    "application/json",
                    new org.json.JSONObject(
                                    fields(
                                            "handover",
                                            treasury.requireSingle(safe, user),
                                            "userSafes",
                                            openingLookup instanceof Map<?, ?> lookup
                                                    ? lookup.get("UserSafes")
                                                    : openingLookup))
                            .toString(),
                    ".json");
            operator = new Chrome().createDriver();
            operator.get(PropertyReader.getApplicationUrl() + "/#/login");
            var login = new LoginPage(operator);
            login.Login(
                    Plugin_ERP.Util.LoginUsernames.withinSameCompany(
                            PropertyReader.getValidUsername(), username),
                    password);
            login.assertAuthenticatedBranch(new FixtureSession(driver).branchId, user.id());
            movement(
                    operator,
                    refs,
                    FinancialMovementKind.RECEIPT,
                    customer,
                    salesAgent,
                    safe,
                    payment,
                    "125.50",
                    false);
            movement(
                    operator,
                    refs,
                    FinancialMovementKind.PAYMENT,
                    supplier,
                    purchaseAgent,
                    safe,
                    payment,
                    "40.25",
                    false);
            movement(
                    operator,
                    refs,
                    FinancialMovementKind.RECEIPT,
                    customer,
                    salesAgent,
                    safe,
                    payment,
                    "55.75",
                    true);
            var expected =
                    new TreasuryAmounts(
                            new BigDecimal("1085.25"),
                            new BigDecimal("1141.00"),
                            new BigDecimal("1080"));
            var totals = treasury.totals(handover);
            exact(totals.get("openingBalance"), "1000");
            exact(totals.get("cashCollectionTransaction"), "125.50");
            exact(totals.get("cashSupplierPayment"), "40.25");
            exact(totals.get("otherCollectionTransaction"), "55.75");
            Assert.assertEquals(
                    TreasuryAmounts.from(totals, expected.actual())
                            .cash()
                            .compareTo(expected.cash()),
                    0);
            Assert.assertEquals(
                    TreasuryAmounts.from(totals, expected.actual())
                            .total()
                            .compareTo(expected.total()),
                    0);
            io.qameta.allure.Allure.addAttachment(
                    "Independent treasury ledger",
                    "application/json",
                    new org.json.JSONObject(
                                    fields(
                                            "opening",
                                            "1000",
                                            "cashReceipt",
                                            "125.50",
                                            "cashPayment",
                                            "40.25",
                                            "otherReceipt",
                                            "55.75",
                                            "expectedCash",
                                            expected.cash(),
                                            "expectedTotal",
                                            expected.total(),
                                            "api",
                                            totals))
                            .toString(),
                    ".json");
            var close = new TreasuryHandoverPage(driver);
            close.OpenTreasuryForReview(handover);
            new ScreenPage(driver).type(By.cssSelector("input[name='actualBalance']"), "1080");
            close.AssertClosingAmounts(
                    expected.cash(), expected.total(), expected.actual(), expected.deficit());
            String main = driver.getWindowHandle();
            close.SaveAndPrint();
            try {
                close.AssertPrintedAmounts(
                        expected.cash(), expected.total(), expected.actual(), expected.deficit());
            } finally {
                driver.close();
                driver.switchTo().window(main);
            }
            Assert.assertEquals(treasury.requireSingle(safe, user).get("isClosed"), true);
        } catch (RuntimeException | AssertionError failure) {
            if (operator != null)
                try {
                    io.qameta.allure.Allure.addAttachment(
                            "رابط جلسة موظف الخزينة عند التوقف", operator.getCurrentUrl());
                    io.qameta.allure.Allure.addAttachment(
                            "صورة جلسة موظف الخزينة",
                            "image/png",
                            new java.io.ByteArrayInputStream(
                                    ((TakesScreenshot) operator).getScreenshotAs(OutputType.BYTES)),
                            ".png");
                } catch (RuntimeException captureFailure) {
                    failure.addSuppressed(captureFailure);
                }
            throw failure;
        } finally {
            if (operator != null) operator.quit();
            if (opened) {
                try {
                    treasury.close();
                } finally {
                    try {
                        basic.preserveAuditHistory();
                    } finally {
                        refs.preserveTreasuryAuditHistory();
                    }
                }
            } else {
                refs.close();
                basic.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void movement(
            WebDriver operator,
            FinancialTestData refs,
            FinancialMovementKind kind,
            FinancialTestData.Record party,
            FinancialTestData.Record agent,
            BasicDataFixtures.Record safe,
            FinancialTestData.Record payment,
            String amount,
            boolean other) {
        var page = new FinancialMovementPage(operator);
        page.openList(kind.route);
        page.openNew();
        String marker = "QA-FM-treasury-" + UUID.randomUUID().toString().substring(0, 12);
        page.captureSaves(marker);
        page.choose(kind.partyLabel, party.name());
        page.choose("مندوب", agent.name());
        try {
            if (other) page.payment(payment.name());
            else {
                new WebDriverWait(operator, Duration.ofSeconds(30))
                        .withMessage("Owned open treasury was not loaded into the operator form")
                        .until(
                                d ->
                                        Boolean.TRUE.equals(
                                                ((JavascriptExecutor) d)
                                                        .executeScript(
                                                                """
                        const q=[document.querySelector('#app')?.__vue__];
                        while(q.length){const vm=q.shift();if(!vm)continue;
                          if(vm.safeTransactions?.safeId===arguments[0])return true;
                          q.push(...(vm.$children||[]));}return false;
                        """,
                                                                safe.id())));
                page.choose("طريقة الدفع", "خزينة");
            }
            page.amount(amount);
            page.notes(marker);
            page.saveMovement();
            new WebDriverWait(operator, Duration.ofSeconds(35))
                    .until(
                            d -> {
                                refs.rememberAttempts(page.capturedAttempts(), marker);
                                return !page.capturedSaves().isEmpty();
                            });
            var save = page.capturedSaves().getLast();
            var values = (Map<String, Object>) save.get("values");
            refs.track(kind.endpoint, save.get("id"), marker, values);
            var record =
                    new FinancialTestData.Record(kind.endpoint, save.get("id"), marker, values);
            var stored = refs.read(record);
            exact(stored.get("value"), amount);
            Assert.assertEquals(stored.get("dealerId"), party.id());
            if (!other)
                Assert.assertTrue(
                        stored.containsValue(safe.id()),
                        "Cash movement must refer to the owned safe");
        } catch (TimeoutException failure) {
            Object response =
                    ((JavascriptExecutor) operator)
                            .executeAsyncScript(
                                    """
                const done=arguments[arguments.length-1],root=document.querySelector('#app').__vue__,q=[root];let userId;
                while(q.length){const vm=q.shift();if(vm?.profile?.userId){userId=vm.profile.userId;break;}q.push(...(vm?.$children||[]));}
                if(!userId){done({error:'Operator profile identity unavailable'});return;}
                root.$store.dispatch('app/get',{url:'Aggregates/salesViewModel?userId='+userId+'&isClosed=false&transactionType=sales&dealerType=sales&agentId='})
                  .then(r=>done({userSafes:r?.UserSafes,currentUserId:r?.CurrentUser?.id}))
                  .catch(e=>done({status:e?.response?.status||e?.status,detail:e?.response?.data||e?.data||String(e)}));
                """);
            io.qameta.allure.Allure.addAttachment(
                    "Operator treasury aggregate response",
                    new org.json.JSONObject((Map<?, ?>) response).toString());
            Object lookup =
                    ((JavascriptExecutor) operator)
                            .executeScript(
                                    """
                const q=[document.querySelector('#app')?.__vue__],rows=[];
                while(q.length){const vm=q.shift();if(!vm)continue;
                  if(vm.lookups?.UserSafes)rows.push({userSafes:vm.lookups.UserSafes,safeTransactions:vm.safeTransactions});
                  q.push(...(vm.$children||[]));}return JSON.stringify(rows);
                """);
            io.qameta.allure.Allure.addAttachment(
                    "Treasury operator lookup", String.valueOf(lookup));
            io.qameta.allure.Allure.addAttachment(
                    "Treasury operator form",
                    "image/png",
                    new java.io.ByteArrayInputStream(
                            ((TakesScreenshot) operator).getScreenshotAs(OutputType.BYTES)),
                    ".png");
            throw new AssertionError(
                    "Treasury movement did not save. Validation: " + page.validationMessages(),
                    failure);
        } finally {
            refs.rememberAttempts(page.capturedAttempts(), marker);
            // Re-reading the capture must also acknowledge saves already confirmed by the server.
            // Otherwise a successful create is requeued as an uncertain write during audit
            // retention.
            for (var captured : page.capturedSaves()) {
                var values = (Map<String, Object>) captured.get("values");
                refs.track((String) captured.get("endpoint"), captured.get("id"), marker, values);
            }
        }
    }

    private void exact(Object actual, String expected) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(
                new BigDecimal(actual.toString()).compareTo(new BigDecimal(expected)), 0);
    }
}
