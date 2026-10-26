package StoreTest;

import Invoice.SalesWorkflow;
import Plugin_ERP.System_Modules.Login.StoreModel.ManufacturingPage;
import Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage;
import TestSupport.WorkflowCleanupOrder;
import TestSupport.WorkflowEntity;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/** التصنيع ثم فاتورة بيع حقيقية للمنتج نفسه؛ اختيار بيانات مشتركة لا يعني نسخ المنتج. */
public final class ManufacturingSalesEndToEndTest extends StoreScenarioTest {
    private final List<SalesWorkflow> sales = new ArrayList<>();
    private final List<TransferWorkflow> transfers = new ArrayList<>();

    @Override
    protected StoreMovementKind kind() {
        return StoreMovementKind.MANUFACTURING;
    }

    @Override
    protected StoreModulePage movementPage() {
        return new ManufacturingPage(driver);
    }

    private static WorkflowEntity ref(StoreTestData.Record record) {
        return new WorkflowEntity(record.id(), record.name());
    }

    private void balance(StoreTestData.Record record, int store, BigDecimal expected) {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> number(data.balance(record, store)).compareTo(expected) == 0);
    }

    private SalesWorkflow seller(int store, StoreTestData.Record customer) {
        var flow =
                new SalesWorkflow(
                        driver,
                        ref(item),
                        ref(data.store(store)),
                        ref(customer),
                        ref(data.agent(false)));
        sales.add(flow);
        return flow;
    }

    private void debt(StoreTestData.Record customer, BigDecimal before, String amount) {
        var expected = before.add(number(sales.get(sales.size() - 1).net(amount)));
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> signed(data.read(customer)).compareTo(expected) == 0);
    }

    private BigDecimal signed(java.util.Map<String, Object> row) {
        return number(row.get("currentDebit"))
                .add(number(row.get("prevDebit")))
                .subtract(number(row.get("currentCredit")))
                .subtract(number(row.get("prevCredit")));
    }

    @Test
    @Story("E2E تصنيع3 ثم بيع المنتج النهائي3 يستهلك المكونات مرة واحدة")
    public void manufacturingThenSellingConsumesComponentsOnlyOnce() {
        var component = data.item(2);
        var componentBefore = number(data.balance(component, 1));
        var customer = data.customer();
        var customerBefore = signed(data.read(customer));
        create("picker", false, "Agents");
        var finished = item;
        balance(component, 1, componentBefore.subtract(number("6")));
        balance(finished, 1, number("3"));
        seller(1, customer).sell("3");
        balance(finished, 1, number("0"));
        balance(finished, 2, number("0"));
        balance(component, 1, componentBefore.subtract(number("6")));
        debt(customer, customerBefore, "300");
    }

    @Test
    @Story("E2E تصنيع ثم نقل المنتج وبيع جزء كسري من مخزن الوجهة")
    public void manufacturingTransferAndFractionalSaleRetainUnsoldProduct() {
        var component = data.item(2);
        var componentBefore = number(data.balance(component, 1));
        var customer = data.customer();
        var customerBefore = signed(data.read(customer));
        create("picker", false, "Agents");
        var finished = item;
        balance(component, 1, componentBefore.subtract(number("6")));
        var flow = new TransferWorkflow(driver, ref(item));
        transfers.add(flow);
        flow.transfer(ref(data.store(1)), ref(data.store(2)), "3");
        balance(finished, 1, number("0"));
        balance(finished, 2, number("3"));
        seller(2, customer).sell("1.25");
        balance(finished, 1, number("0"));
        balance(finished, 2, number("1.75"));
        balance(component, 1, componentBefore.subtract(number("6")));
        debt(customer, customerBefore, "125");
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void cleanStoreScenario(ITestResult result) {
        for (var flow : sales) flow.capture();
        for (var flow : transfers) flow.capture();
        super.cleanStoreScenario(result);
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void retryStoreCleanup() {
        WorkflowCleanupOrder.closeChildrenBeforeParents(
                sales,
                () ->
                        WorkflowCleanupOrder.closeChildrenBeforeParents(
                                transfers, () -> super.retryStoreCleanup()));
    }
}
