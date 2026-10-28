package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Util.VueComponentAccess;
import TestSupport.*;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Reconciles one posted source against seeded cost and verified owned account links, then unposts.
 */
public final class StocktakingAccountingTest extends SalesScenarioTest {
    @Test
    public void postedSaleMatchesIndependentPeriodicOrContinuousJournal() throws Exception {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), BranchPolicies.ActivityType.PUBLIC);
        prepare("Credit");
        String price =
                profile.expected().taxPolicy() == BranchPolicies.TaxPolicy.PRICE_INCLUDED
                        ? "115"
                        : "100";
        item =
                data.fixture(
                        "accounting-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), price, false));
        allowedItems.add(item.id());
        var seededStore =
                ((List<?>) data.read(item).get("stores"))
                        .stream()
                                .map(v -> (Map<?, ?>) v)
                                .filter(v -> data.store(1).id().equals(v.get("storeId")))
                                .findFirst()
                                .orElseThrow();
        sameNumber(seededStore.get("costPrice"), "80");
        Allure.addAttachment(
                "Owned seeded accounting cost",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "itemId",
                                        item.id(),
                                        "storeId",
                                        data.store(1).id(),
                                        "quantity",
                                        2,
                                        "seededCostPerUnit",
                                        80,
                                        "expectedCost",
                                        160,
                                        "persistedStore",
                                        seededStore))
                        .toString(2),
                ".json");
        page.slCapture(marker, data.store(1).id(), item.id(), "ItemTransactions-Sales");
        Object revenue = data.account("posted-sales").id(), vat = data.account("posted-vat").id();
        Object cost = data.account("posted-cost").id(),
                discount = data.account("posted-discount").id();
        Object returnSales = data.account("posted-returns").id();
        Object stock = data.store(1).values().get("accountId"),
                party = customer.values().get("accountId");
        var links =
                SalesTestData.fields(
                        "salesAccountId",
                        revenue,
                        "returnSalesAccountId",
                        returnSales,
                        "outTaxesAccountId",
                        vat,
                        "soldGoodsCostAccountId",
                        cost,
                        "storageAccountId",
                        stock,
                        "salesDiscountAccountId",
                        discount,
                        "customersAccountId",
                        party);
        var unused = data.account("posted-unused-roles");
        for (String key :
                List.of(
                        "purchAccountId",
                        "returnPurchAccountId",
                        "suppliersAccountId",
                        "purchDiscountAccountId",
                        "profitAccountId",
                        "boxAccountId",
                        "inTaxesAccountId",
                        "expensesAccountId",
                        "incomesAccountId",
                        "banksAccountId",
                        "damageAdjustmentAccountId")) links.put(key, unused.id());
        if (profile.expected().stocktakingMethod() == BranchPolicies.StocktakingMethod.CONTINUOUS) {
            links.put("overflowAdjustmentAccountId", unused.id());
            links.put("shortageAdjustmentAccountId", unused.id());
        }
        for (String key :
                List.of(
                        "agentsAccountId",
                        "exchangeDestinationsAccountId",
                        "additionalTaxAccountId")) links.put(key, unused.id());
        links.put("mainCustomersAccountId", data.account("customer").values().get("parentId"));
        for (String key :
                List.of("mainSuppliersAccountId", "mainIncomesAccountId", "mainExpensesAccountId"))
            links.put(key, unused.values().get("parentId"));
        try (var binding = new OwnedBranchAccountingBindings(driver, links)) {
            Allure.addAttachment(
                    "Verified owned accounting links",
                    "application/json",
                    new org.json.JSONObject(links).toString(2),
                    ".json");
            add(item, "picker");
            page.slQuantity(item.name(), "2");
            var totals =
                    BranchInvoiceCalculator.calculate(
                            List.of(
                                    new PolicyInvoiceLine(
                                            "posted-sale",
                                            new BigDecimal("2"),
                                            new BigDecimal(price),
                                            BigDecimal.ZERO,
                                            DiscountType.VALUE,
                                            false)),
                            profile.expected().taxPolicy(),
                            2,
                            BranchInvoiceCalculator.TaxRounding.INVOICE);
            page.slWaitNet(totals.getNet().toPlainString());
            var source = save(false);
            var stored = data.read(source);
            sameNumber(stored.get("net"), totals.getNet().toPlainString());
            sameNumber(data.balance(item, 1), "998");
            var accounts =
                    new InventoryPostingExpectation.Accounts(
                            party.toString(),
                            revenue.toString(),
                            vat.toString(),
                            revenue.toString(),
                            stock.toString(),
                            cost.toString());
            var expected =
                    InventoryPostingExpectation.signedBalances(
                            new InventoryPostingExpectation.Sale(
                                    totals.getBeforeTax(),
                                    BigDecimal.ZERO,
                                    totals.getTax(),
                                    new BigDecimal("160"),
                                    false),
                            profile.expected().stocktakingMethod(),
                            accounts);
            reconcilePostedSource(source, stored.get("code").toString(), expected);
            sameNumber(data.balance(item, 1), "998");
        }
    }

    private void reconcilePostedSource(
            SalesTestData.Record source, String code, Map<String, BigDecimal> expected)
            throws Exception {
        var screen = new ScreenPage(driver);
        screen.open("post-vouchers", "الترحيل الي الحسابات");
        screen.type(By.cssSelector("input[label-text='fromCode']"), code);
        screen.type(By.cssSelector("input[label-text='toCode']"), code);
        screen.check("المبيعات", true);
        VueComponentAccess.execute(
                driver,
                """
            const store=erpVue.root().$store,dispatch=store.dispatch.bind(store),code=String(arguments[0]);
            window.__ownedPostingEvidence=[];
            store.dispatch=function(action,payload,...rest){
              const posting=action==='app/create'&&['Posting/post','Posting/unPost'].includes(payload?.url);
              if(posting){
                const d=payload.data;
                if(String(d.fromCode)!==code||String(d.toCode)!==code||d.includeSales!==true||
                  Object.entries(d).some(([k,v])=>k.startsWith('include')&&k!=='includeSales'&&v===true))
                  throw Error('Refusing posting outside the exact owned sales code');
              }
              if(!posting)return dispatch(action,payload,...rest);
              const record={url:payload.url,request:JSON.parse(JSON.stringify(payload.data))};
              window.__ownedPostingEvidence.push(record);
              return dispatch(action,payload,...rest).then(result=>{record.result=result?.data??result;return result})
                .catch(error=>{record.error=error?.response?.data||error?.data||String(error);throw error});
            };
            """,
                code);
        var session = new FixtureSession(driver);
        Path journal = Path.of("target/posting-recovery", "source-" + source.id() + ".json");
        FixtureJournals.write(
                journal,
                List.of(
                        SalesTestData.fields(
                                "origin",
                                session.origin,
                                "branchId",
                                session.branchId,
                                "sourceId",
                                source.id(),
                                "code",
                                code,
                                "notes",
                                source.name())));
        var ownedVouchers = new ArrayList<Map<String, Object>>();
        boolean postingConfirmed = false;
        try {
            screen.click("ترحيل");
            awaitPosted(source, true);
            postingConfirmed = true;
            screen.dismissSuccess();
            var listing = data.lookup("vouchers?pageNumber=1&pageSize=500");
            Allure.addAttachment(
                    "Posted owned source voucher listing",
                    "application/json",
                    new org.json.JSONObject(listing).toString(2),
                    ".json");
            Assert.assertTrue(
                    listing.get("pageData") instanceof List<?>, "Voucher list rows are missing");
            for (Object row : (List<?>) listing.get("pageData")) {
                var candidate = (Map<?, ?>) row;
                if (!"Sales".equals(candidate.get("sourceTransactionType"))) continue;
                var detail = data.lookup("Vouchers/" + candidate.get("id"));
                if (source.id().equals(detail.get("sourceTransactionId")))
                    ownedVouchers.add(detail);
            }
            Assert.assertEquals(
                    ownedVouchers.size(),
                    1,
                    "Expected one persisted voucher for the exact owned source; recovery journal retained");
            FixtureJournals.write(
                    journal,
                    ownedVouchers.stream()
                            .map(
                                    v ->
                                            SalesTestData.fields(
                                                    "origin",
                                                    session.origin,
                                                    "branchId",
                                                    session.branchId,
                                                    "sourceId",
                                                    source.id(),
                                                    "code",
                                                    code,
                                                    "notes",
                                                    source.name(),
                                                    "voucherId",
                                                    v.get("id")))
                            .toList());
            var voucher = ownedVouchers.getFirst();
            Allure.addAttachment(
                    "Owned persisted accounting voucher",
                    "application/json",
                    new org.json.JSONObject(voucher).toString(2),
                    ".json");
            Allure.addAttachment(
                    "Independent expected account balances",
                    "application/json",
                    new org.json.JSONObject(expected).toString(2),
                    ".json");
            Assert.assertEquals(voucher.get("sourceTransactionId"), source.id());
            Assert.assertTrue(
                    voucher.get("lines") instanceof List<?>, "Voucher detail lines are missing");
            var entries =
                    ((List<?>) voucher.get("lines"))
                            .stream()
                                    .map(v -> (Map<?, ?>) v)
                                    .map(
                                            v ->
                                                    new OwnedPostingReconciliation.Entry(
                                                            source.id().toString(),
                                                            v.get("accountId").toString(),
                                                            number(v.get("debit")),
                                                            number(v.get("credit"))))
                                    .toList();
            OwnedPostingReconciliation.verify(source.id().toString(), expected, entries);
        } finally {
            Allure.addAttachment(
                    "Owned posting API evidence",
                    "application/json",
                    org.json.simple.JSONValue.toJSONString(
                            VueComponentAccess.execute(
                                    driver, "return window.__ownedPostingEvidence||[]")),
                    ".json");
            Allure.addAttachment(
                    "Owned source after posting attempt",
                    "application/json",
                    new org.json.JSONObject(data.read(source)).toString(2),
                    ".json");
            Allure.addAttachment(
                    "Voucher list after owned posting attempt",
                    "application/json",
                    new org.json.JSONObject(data.lookup("vouchers?pageNumber=1&pageSize=500"))
                            .toString(2),
                    ".json");
            if (Boolean.TRUE.equals(data.read(source).get("isPosted"))) {
                postingConfirmed = true;
                screen.dismissSuccess();
                screen.click("الغاء الترحيل");
                screen.confirm(true);
                awaitPosted(source, false);
                screen.dismissSuccess();
            }
            if (!Boolean.TRUE.equals(data.read(source).get("isPosted"))) {
                if (postingConfirmed && ownedVouchers.isEmpty())
                    throw new AssertionError(
                            "Posted source voucher was not identified; retaining recovery journal");
                var api =
                        new SalesFixtureApi(
                                driver, session.origin, session.branchId, session::verify);
                for (var captured : ownedVouchers) {
                    var current = data.lookup("Vouchers/" + captured.get("id"));
                    Assert.assertEquals(
                            current.get("sourceTransactionId"),
                            source.id(),
                            "Voucher ownership changed");
                    Assert.assertEquals(
                            current.get("isCanceled"),
                            true,
                            "Refusing deletion of an active voucher");
                    api.call("delete", "Vouchers", Map.of("id", current.get("id")));
                }
                FixtureJournals.write(journal, List.of());
            }
        }
    }

    private void awaitPosted(SalesTestData.Record source, boolean expected) {
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .withMessage("Owned source posting state was not confirmed")
                .until(
                        d -> {
                            Object error =
                                    VueComponentAccess.execute(
                                            d,
                                            """
                        const e=(window.__ownedPostingEvidence||[]).at(-1);
                        return e?.url===arguments[0]?(e.error??null):null;
                        """,
                                            expected ? "Posting/post" : "Posting/unPost");
                            if (error != null)
                                throw new AssertionError(
                                        "Owned posting rejected: "
                                                + org.json.simple.JSONValue.toJSONString(error));
                            return Objects.equals(data.read(source).get("isPosted"), expected);
                        });
    }
}
