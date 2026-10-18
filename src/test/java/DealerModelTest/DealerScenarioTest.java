package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;

/** Isolated records for the original, fully enabled Dealer scenarios. */
public class DealerScenarioTest extends BaseTest {
  private record OwnedRecord(AbstractForDealer page, Pages route, String name) {}
  private final List<OwnedRecord> ownedRecords = new ArrayList<>();
  private DealerTestData scenarioData;
  private final List<DealerTestData> pendingFixtureCleanup = new ArrayList<>();

  protected DealerTestData data() {
    if (scenarioData == null) scenarioData = new DealerTestData(driver);
    return scenarioData;
  }

  protected void prepareScenarioReferences(String scenario) {
    String testClass = getClass().getSimpleName();
    if (testClass.equals("AgentTest")) {
      if (scenario.equals("AddAgentFDAtaFirstPAge")) {
        data().area(); data().classification("Agents"); data().store(); data().priceList();
      } else if (List.of("AddAgentThirdPageProductCommission", "AddAgentByAllProducts",
          "AddAgentBySearchProduct", "AddAgentWithChoseItem").contains(scenario)) {
        data().item(1); data().item(2);
      }
    } else if ((testClass.equals("CustomersTest") &&
        List.of("AddCustomerFirstPage", "Edit").contains(scenario)) ||
        (testClass.equals("SupplierTest") &&
        List.of("AddSupplierFirstPage", "Edit").contains(scenario))) {
      String type = testClass.equals("CustomersTest") ? "Customers" : "Suppliers";
      data().area(); data().account(); data().classification(type); data().mainDealer(type);
      data().agent(type.equals("Customers") ? "Sales" : "Purch");
    }
  }

  protected void assertOwnedEditCancelled(AbstractForDealer page, Pages route) {
    String name = seedRecord(page, route, "CANCEL-EDIT");
    DealerModelScreenAssertions.assertEditCanBeCancelled(page, name);
  }

  protected void assertOwnedDeleteCancelled(AbstractForDealer page, Pages route) {
    String name = seedRecord(page, route, "CANCEL-DELETE");
    DealerModelScreenAssertions.assertDeleteCanBeCancelled(page, name);
  }

  protected String ownedName(AbstractForDealer page, Pages route, String label) {
    String shortLabel = label.substring(0, Math.min(label.length(), 24));
    String name = "QA-" + shortLabel + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    ownedRecords.add(new OwnedRecord(page, route, name));
    return name;
  }

  protected String seedRecord(AbstractForDealer page, Pages route, String label) {
    String name = ownedName(page, route, label);
    page.NavigationHelper(route);
    page.ClickNewButtonForDealer();
    page.AddArabicName(name);
    if (page instanceof Agent) {
      page.SelectDealerOption("طبيعه المندوب", "بيع");
    } else if (route == Pages.CUSTOMERCLASS) {
      // Classification type uses the standard dropdown, not a searchable Vue selector.
      page.SelectFromDropDawn("العملاء", "نوع التصنيف");
    } else if (route == Pages.MAINDEALERS) {
      page.SelectFromDropDawn("العملاء", "النوع");
    }
    page.ClickSaveButton();
    waitForRecord(page, name);
    page.dismissDealerSaveSuccessIfPresent();
    return name;
  }

  protected void waitForRecord(AbstractForDealer page, String name) {
    new WebDriverWait(driver, Duration.ofSeconds(20))
        .until(d -> page.getExactDealerNameMatchCount(name) == 1);
  }

  protected String uniquePhone() {
    String digits = Long.toString(System.nanoTime());
    return "05" + digits.substring(digits.length() - 8);
  }

  protected String uniqueEmail() {
    return "qa." + java.util.UUID.randomUUID() + "@example.com";
  }

  protected String uniqueTaxNumber() {
    String digits = Long.toString(System.nanoTime());
    return "3" + digits.substring(digits.length() - 13) + "3";
  }

  protected String seedCustomerPhone(Customers page) {
    String name = ownedName(page, Pages.CUSTOMERS, "PHONE-ORIGINAL");
    String phone = uniquePhone();
    page.NavigationHelper(Pages.CUSTOMERS);
    page.ClickNewButtonForDealer();
    page.AddArabicName(name);
    page.ClickContactDataTab();
    page.AddMobileNumber(phone);
    page.ClickSaveButton();
    waitForRecord(page, name);
    page.dismissDealerSaveSuccessIfPresent();
    return phone;
  }

  protected void deleteOwnedRecords(AbstractForDealer page, Pages route, int count) {
    List<String> names = new ArrayList<>();
    for (int index = 0; index < count; index++) {
      names.add(seedRecord(page, route, "DELETE-" + index));
    }
    for (String name : names) {
      page.requestDealerDeleteByName(name);
      page.confirmDealerDeleteByName(name);
      Assert.assertEquals(page.getExactDealerNameMatchCount(name), 0);
    }
  }

  @AfterMethod(alwaysRun = true)
  public void cleanScenarioRecords() {
    List<String> failures = new ArrayList<>();
    List<OwnedRecord> pendingRecords = new ArrayList<>();
    if (scenarioData != null) {
      pendingFixtureCleanup.add(scenarioData);
      scenarioData = null;
    }
    for (int index = ownedRecords.size() - 1; index >= 0; index--) {
      OwnedRecord record = ownedRecords.get(index);
      try {
        if (record.page().isDealerCreationFormDisplayed()) {
          record.page().closeDealerFormWithoutSaving();
        }
        record.page().NavigationHelper(record.route());
        record.page().dismissDealerSaveSuccessIfPresent();
        if (record.page().getExactDealerNameMatchCount(record.name()) > 0) {
          record.page().requestDealerDeleteByName(record.name());
          record.page().confirmDealerDeleteByName(record.name());
        }
      } catch (RuntimeException | AssertionError failure) {
        pendingRecords.add(record);
        failures.add(record.name() + ": " + failure.getMessage());
      }
    }
    ownedRecords.removeIf(record -> !pendingRecords.contains(record));
    if (!failures.isEmpty()) {
      throw new AssertionError("Scenario records remain; prerequisite cleanup is deferred: "
          + String.join("; ", failures));
    }
    List<DealerTestData> unresolved = new ArrayList<>();
    for (DealerTestData context : pendingFixtureCleanup) {
      try { context.close(); }
      catch (RuntimeException | AssertionError failure) {
        unresolved.add(context);
        failures.add(failure.getMessage());
      }
    }
    pendingFixtureCleanup.clear();
    pendingFixtureCleanup.addAll(unresolved);
    if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
  }

  @AfterClass(alwaysRun = true)
  public void retryRemainingScenarioCleanup() {
    if (!ownedRecords.isEmpty() || !pendingFixtureCleanup.isEmpty() || scenarioData != null) {
      cleanScenarioRecords();
    }
  }
}
