/* شرح الملف: تنفيذ حالات المناديب الخاصة بـرفض المدخلات غير الصحيحة.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import io.qameta.allure.*;
import org.testng.Assert;

final class AgentValidationChecks {
    private final AgentTest owner;

    AgentValidationChecks(AgentTest owner) {
        this.owner = owner;
    }

    void AseertArabicNameRequired() {
        Agent agent = new Agent(owner.caseDriver());
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddEnglishName(owner.ownedName(agent, Pages.AGENTS, "INVALID-ARABIC-NAME"));
        agent.ClickSaveButton();
        Assert.assertTrue(
                agent.IsArabicNameRequiredErrorDisplayed(),
                "Error message for required Arabic name is not displayed");
    }

    void AseertAgentNaturalRequired() {
        Agent agent = new Agent(owner.caseDriver());
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(owner.ownedName(agent, Pages.AGENTS, "VALIDATION"));
        agent.ClickSaveButton();
        Assert.assertTrue(
                agent.AssertAgentNatural(),
                "Error message for required Agent Natural is not displayed");
    }
}
