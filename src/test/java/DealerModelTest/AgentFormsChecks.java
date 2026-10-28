/* شرح الملف: تنفيذ حالات المناديب الخاصة بـفتح النماذج وإلغاء التغييرات.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import io.qameta.allure.*;

final class AgentFormsChecks {
    private final AgentTest owner;

    AgentFormsChecks(AgentTest owner) {
        this.owner = owner;
    }

    void AG_002_CloseAgentFormWithoutSaving() {
        Agent page = new Agent(owner.caseDriver());
        page.NavigationHelper(Pages.AGENTS);
        DealerModelScreenAssertions.assertNewFormCanBeClosedWithoutSaving(page);
    }

    void AG_003_CancelAgentEditWithoutSaving() {
        Agent page = new Agent(owner.caseDriver());
        page.NavigationHelper(Pages.AGENTS);
        owner.assertOwnedEditCancelled(page, Pages.AGENTS);
    }

    void AG_004_CancelAgentDelete() {
        Agent page = new Agent(owner.caseDriver());
        page.NavigationHelper(Pages.AGENTS);
        owner.assertOwnedDeleteCancelled(page, Pages.AGENTS);
    }
}
