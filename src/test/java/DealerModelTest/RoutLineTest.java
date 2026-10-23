package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.RoutLineForDealer;
import io.qameta.allure.*;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.support.ui.WebDriverWait;
import static DealerModelTest.DealerTestData.fields;

/** An independently runnable calendar scenario; cleanup also recovers uncertain UI saves. */
public class RoutLineTest extends DealerScenarioTest {
    @Test
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("خطوط سير المندوبين")
    @Story("إضافة خط سير وربط المندوب بالأحداث اليومية")
    @Description("ينشئ مندوبًا وعميلًا وحالة مملوكة، ويضيف زياراتهم فقط ثم ينظف السجلات.")
    public void AddRoutLine() {
        var agent = data().agent("Sales");
        var status = data().fixture("route-status", "AgentItinerariesStatus",
                fields("documentKind", "Invoice", "isRouteLocation", false));
        var customer = data().fixture("route-customer", "Customers",
                fields("agentId", agent.id(), "areaId", data().area().id(),
                        "accountId", data().account().id(), "isActive", true,
                        "dealersClassificationId", data().classification("Customers").id()));
        RoutLineForDealer page = new RoutLineForDealer(driver);
        String marker = ownedName(page, Pages.DEALERROUTELINE, "CALENDAR");
        page.NavigationHelper(Pages.DEALERROUTELINE);
        page.AddRoutLine();
        page.PrepareOwnedRoute(agent.id(), customer.id(), status.id(),
                new TestSupport.FixtureSession(driver).branchId);
        page.AddArabicRoutLine(marker);
        page.AddVisitCount("5");
        page.AddDayCount("10");
        page.ClickToDeletAllButton();
        page.ClickAddAllButton();
        page.AssertOnlyOwnedCustomerSelected(customer.id());
        page.AddNotesForRoutLine(marker);
        Assert.assertTrue(page.OwnedEventTitles(marker).isEmpty());
        page.ClickToSaveButton();
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !page.OwnedEventTitles(marker).isEmpty());
        Assert.assertFalse(page.OwnedEventTitles(marker).isEmpty(),
                "Owned route visits must be visible in the calendar");
        Allure.addAttachment("Owned route calendar events", String.join("\n", page.OwnedEventTitles(marker)));
    }
}
