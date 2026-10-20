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
import org.testng.annotations.BeforeMethod;

/** Isolated records for the original, fully enabled Dealer scenarios. */
public class DealerScenarioTest extends BaseTest {
  private record OwnedRecord(AbstractForDealer page, Pages route, String name) {}
  private final List<OwnedRecord> ownedRecords = new ArrayList<>();
  private DealerTestData scenarioData;
  private final List<DealerTestData> pendingFixtureCleanup = new ArrayList<>();
  private String mainWindow;
  private String pageSuffix=java.util.UUID.randomUUID().toString().substring(0,12);
  private TestSupport.FixtureSession uiSession;
  private String fixtureSuffix;

  @BeforeMethod(alwaysRun=true) public void beginDealerCase(){
    mainWindow=driver.getWindowHandle();if(uiSession==null)uiSession=new TestSupport.FixtureSession(driver);uiSession.verify();data().beginScenario();
  }

  private void journalOwnedRecords(){
    if(uiSession==null)uiSession=new TestSupport.FixtureSession(driver);
    var rows=ownedRecords.stream().map(r->DealerTestData.fields("origin",uiSession.origin,"branchId",uiSession.branchId,"fixtureJournal",fixtureSuffix,"route",r.route().name(),"name",r.name())).toList();
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","dealer-page-records",pageSuffix+".json"),rows);
  }

  @SuppressWarnings("unchecked") void resumeOwnedRecords(String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid dealer page journal ID");
    pageSuffix=suffix;uiSession=new TestSupport.FixtureSession(driver);
    var path=java.nio.file.Path.of("target","dealer-page-records",suffix+".json");
    for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
      var row=(java.util.Map<String,Object>)value;uiSession.verify(row);String name=(String)row.get("name");
      if(row.get("fixtureJournal") instanceof String suffixId){if(fixtureSuffix!=null&&!fixtureSuffix.equals(suffixId))throw new AssertionError("Mixed dealer fixture journals");fixtureSuffix=suffixId;}
      Pages route=Pages.valueOf((String)row.get("route"));
      if(name==null||!name.startsWith("QA-")||!java.util.Set.of(Pages.AGENTS,Pages.CUSTOMERS,Pages.SUPPLIERS,Pages.MAINDEALERS,Pages.CUSTOMERCLASS).contains(route))throw new AssertionError("Invalid owned dealer page record");
      ownedRecords.add(new OwnedRecord(new AbstractForDealer(driver),route,name));
    }
    if(fixtureSuffix!=null)scenarioData=DealerTestData.resume(driver,fixtureSuffix);
  }

  protected DealerTestData data() {
    if (scenarioData == null){scenarioData = new DealerTestData(driver);fixtureSuffix=scenarioData.journalSuffix();}
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
    journalOwnedRecords();
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
    try{
      journalOwnedRecords();
      io.qameta.allure.Allure.addAttachment("Owned dealer records retained until page end","application/json",new org.json.JSONArray(ownedRecords.stream().map(r->DealerTestData.fields("route",r.route().name(),"name",r.name())).toList()).toString(),".json");
    }finally{
      if(driver!=null&&mainWindow!=null){
        try{for(String handle:new ArrayList<>(driver.getWindowHandles()))if(!handle.equals(mainWindow)){driver.switchTo().window(handle);driver.close();}}
        finally{driver.switchTo().window(mainWindow);}
      }
    }
  }

  private void deletePageRecords() {
    List<String> failures = new ArrayList<>();
    List<OwnedRecord> pendingRecords = new ArrayList<>();
    DealerTestData cleanupContext=scenarioData!=null?scenarioData:pendingFixtureCleanup.isEmpty()?data():pendingFixtureCleanup.get(0);
    if (scenarioData != null) {
      pendingFixtureCleanup.add(scenarioData);
      scenarioData = null;
    }
    for (int index = ownedRecords.size() - 1; index >= 0; index--) {
      OwnedRecord record = ownedRecords.get(index);
      try {
        uiSession.verify();
        cleanupContext.deleteOwnedDealer(record.route(),record.name());
        ownedRecords.remove(record);journalOwnedRecords();
      } catch (RuntimeException | AssertionError failure) {
        pendingRecords.add(record);
        failures.add(record.name() + ": " + failure.getMessage());
      }
    }
    ownedRecords.removeIf(record -> !pendingRecords.contains(record));
    journalOwnedRecords();
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
    List<String> failures=new ArrayList<>();
    try{cleanScenarioRecords();}catch(RuntimeException|AssertionError failure){failures.add(failure.toString());}
    if (!ownedRecords.isEmpty() || !pendingFixtureCleanup.isEmpty() || scenarioData != null) {
      try{deletePageRecords();}catch(RuntimeException|AssertionError failure){failures.add(failure.toString());}
    }
    if(!failures.isEmpty())throw new AssertionError(String.join("; ",failures));
  }
}
