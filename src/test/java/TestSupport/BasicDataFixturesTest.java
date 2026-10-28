package TestSupport;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BasicDataFixturesTest {
    @Test
    public void activitySpecificMastersKeepForeignRowsDuringCleanup() {
        var api = new BasicDataFixturesApiStub();
        api.records.put(
                "foreign-casting",
                BasicDataFixtures.fields(
                        "id", "foreign-casting", "arabicName", "Existing business casting type"));
        try (var data = new BasicDataFixtures(api.driver())) {
            data.fixture("owned-casting", "CastingTypes", BasicDataFixtures.fields());
            data.fixture("owned-color", "CarColors", BasicDataFixtures.fields());
            data.fixture("owned-cement", "CementTypes", BasicDataFixtures.fields());
            data.fixture("owned-temperature", "MaxTemperatures", BasicDataFixtures.fields());
            data.fixture("owned-concrete-car", "Cars", BasicDataFixtures.fields());
            data.fixture("owned-driver", "Drivers", BasicDataFixtures.fields());
            data.fixture("owned-project", "Projects", BasicDataFixtures.fields());
        }
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("foreign-casting"));
    }

    @Test
    public void separateCasesAndCleanOnlyOwned() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.beginScenario();
        var first = data.bank(1);
        Assert.assertEquals(data.bank(1).id(), first.id());
        data.beginScenario();
        var second = data.bank(1);
        Assert.assertNotEquals(first.id(), second.id());
        stub.records.put(
                "foreign",
                BasicDataFixtures.fields("id", "foreign", "arabicName", "Business bank"));
        data.close();
        data.close();
        Assert.assertEquals(stub.records.size(), 1);
        Assert.assertTrue(stub.records.containsKey("foreign"));
    }

    @Test
    public void retainParentsWhenChildDeleteFails() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.bank(1);
        stub.rejectBank = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(stub.records.size(), 3);
        stub.rejectBank = false;
        data.close();
        Assert.assertTrue(stub.records.isEmpty());
    }

    @Test
    public void refuseForeignBranch() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.bank(1);
        stub.activeBranch = "foreign";
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(stub.records.size(), 3);
        stub.activeBranch = "branch-test";
        data.close();
    }

    @Test
    public void reservedNamesRecoverLostUiResponse() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        String name = data.reserveName("Areas", "created-in-ui");
        stub.records.put(
                "Areas-ui", BasicDataFixtures.fields("id", "Areas-ui", "arabicName", name));
        data.close();
        Assert.assertTrue(stub.records.isEmpty());
    }

    @Test
    public void rejectUnreservedCaptureAndForeignEndpoint() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        Assert.expectThrows(
                AssertionError.class,
                () -> data.track("Banks", "foreign", "QA-BD-foreign", BasicDataFixtures.fields()));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> data.fixture("x", "Users", BasicDataFixtures.fields()));
        data.close();
    }

    @Test
    public void userSecretsAreNotJournaled() throws Exception {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        var user = data.user("discount");
        Assert.assertFalse(user.values().containsKey("passwordHash"));
        String journal =
                java.nio.file.Files.readString(
                        java.nio.file.Path.of(
                                "target", "basic-data-fixtures", data.journalSuffix() + ".json"));
        Assert.assertFalse(journal.contains("passwordHash"));
        Assert.assertEquals(user.values().get("isAdmin"), false);
        data.close();
        Assert.assertTrue(stub.records.isEmpty());
    }

    @Test
    public void uncertainChildRetainsRecoveredDependencies() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        var parent = data.account("ui-bank");
        String name = data.reserveName("Banks", "lost-response");
        stub.records.put(
                "Banks-ui",
                BasicDataFixtures.fields(
                        "id", "Banks-ui", "arabicName", name, "accountId", parent.id()));
        stub.rejectBank = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(stub.records.size(), 3);
        stub.rejectBank = false;
        data.close();
        Assert.assertTrue(stub.records.isEmpty());
    }

    @Test
    public void refuseChangedOwnership() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        var bank = data.bank(1);
        var record = stub.records.get(bank.id());
        record.put("arabicName", "Foreign");
        record.put("englishName", "Foreign");
        record.put("notes", "Foreign");
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(stub.records.size(), 3);
        record.put("arabicName", bank.name());
        data.close();
    }

    @Test
    public void fullPaginationRecoversLastOwnedRow() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        String name = data.reserveName("Areas", "last-page");
        for (int i = 0; i < 501; i++)
            stub.records.put(
                    "Areas-foreign" + i,
                    BasicDataFixtures.fields(
                            "id", "Areas-foreign" + i, "arabicName", "Business " + i));
        stub.records.put(
                "Areas-owned", BasicDataFixtures.fields("id", "Areas-owned", "arabicName", name));
        data.close();
        Assert.assertEquals(stub.records.size(), 501);
        Assert.assertFalse(stub.records.containsKey("Areas-owned"));
    }

    @Test
    public void uncertainDeleteReadbackAndResumeAreIdempotent() throws Exception {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.bank(1);
        stub.timeOutBank = true;
        stub.commitBeforeTimeout = true;
        BasicDataFixtures.resume(stub.driver(), data.journalSuffix()).close();
        Assert.assertTrue(stub.records.isEmpty());
        data.close();
        Assert.assertTrue(stub.records.isEmpty());
    }

    @Test
    public void recoveryRejectsAnotherRunsOwnedName() throws Exception {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.bank(1);
        var path =
                java.nio.file.Path.of(
                        "target", "basic-data-fixtures", data.journalSuffix() + ".json");
        String original = java.nio.file.Files.readString(path);
        try {
            java.nio.file.Files.writeString(
                    path, original.replace(data.journalSuffix(), "00000000-000"));
            Assert.expectThrows(
                    AssertionError.class,
                    () -> BasicDataFixtures.resume(stub.driver(), data.journalSuffix()));
            Assert.assertEquals(stub.records.size(), 3);
        } finally {
            java.nio.file.Files.writeString(path, original);
            data.close();
        }
    }

    @Test
    public void inFlightUiRequestPreventsParentCleanup() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.bank(1);
        stub.uiPollFailure = true;
        Assert.expectThrows(org.openqa.selenium.TimeoutException.class, data::close);
        Assert.assertEquals(stub.records.size(), 3);
        Assert.assertTrue(stub.deletes.isEmpty());
        stub.uiPollFailure = false;
        data.close();
    }

    @Test
    public void reportWindowCollectsFromMainApplication() throws Exception {
        var stub = new BasicDataFixturesApiStub();
        try (var browser = new FixtureBrowser(stub.driver())) {
            var scenario = new BasicDataScenarioTest() {};
            browser.bind(scenario);
            scenario.beginBasicDataScenario();
            browser.addAuxiliaryWindow();
            browser.driver.switchTo().window("report-0");
            stub.beforeUiPoll = () -> Assert.assertEquals(browser.driver.getWindowHandle(), "main");
            scenario.collectBasicDataScenario();
            Assert.assertEquals(browser.closed, 1);
            scenario.cleanBasicDataFixtures();
        }
    }

    @Test
    public void nextCaseCannotDiscardAnUnsettledWrite() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.bank(1);
        stub.uiPollFailure = true;
        Assert.expectThrows(org.openqa.selenium.TimeoutException.class, data::beginScenario);
        Assert.assertTrue(stub.deletes.isEmpty());
        stub.uiPollFailure = false;
        data.close();
    }

    @Test
    public void auditedTreasuryMastersArePreservedWithoutCleanupFailure() {
        var stub = new BasicDataFixturesApiStub();
        var data = new BasicDataFixtures(stub.driver());
        data.safe(1);
        data.preserveAuditHistory();
        data.close();
        Assert.assertEquals(stub.records.size(), 3);
        Assert.assertTrue(stub.deletes.isEmpty());
    }
}
