/* شرح الملف: تنفيذ حالات المناديب الخاصة بـعرض القوائم والبحث واستمرار السجلات.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import io.qameta.allure.*;
import org.testng.Assert;

final class AgentListsChecks {
    private final AgentTest owner;

    AgentListsChecks(AgentTest owner) {
        this.owner = owner;
    }

    void AG_001_AgentListStructure() {
        Agent page = new Agent(owner.caseDriver());
        page.NavigationHelper(Pages.AGENTS);
        owner.seedRecord(page, Pages.AGENTS, "LIST");
        DealerModelScreenAssertions.assertListAndActionsDisplayed(page);
    }

    void AddAgentBySearchProduct() {
        owner.prepareScenarioReferences("AddAgentBySearchProduct");
        Agent agent = new Agent(owner.caseDriver());
        String recordName = owner.ownedName(agent, Pages.AGENTS, "AddAgentBySearchProduct");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(owner.data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.SearchProduct((String) owner.data().item(1).values().get("mainUnitBarcode"));
        agent.ClickEnter();
        agent.SearchProduct((String) owner.data().item(2).values().get("mainUnitBarcode"));
        agent.ClickEnter();
        agent.WaitForCommissionItems(owner.data().item(1).name(), owner.data().item(2).name());
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertTrue(agent.getRowCount() > 0, "الجدول لا يحتوي على أي صفوف");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        owner.waitForRecord(agent, recordName);
    }
}
