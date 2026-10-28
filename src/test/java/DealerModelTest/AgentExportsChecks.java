/* شرح الملف: تنفيذ حالات المناديب الخاصة بـتصدير القوائم والتحقق من الملف.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import io.qameta.allure.*;
import java.io.IOException;

final class AgentExportsChecks {
    private final AgentTest owner;

    AgentExportsChecks(AgentTest owner) {
        this.owner = owner;
    }

    void ExportAgentsToPdf() throws IOException {
        Agent agent = new Agent(owner.caseDriver());
        agent.NavigationHelper(Pages.AGENTS);
        DealerModelExportAssertions.assertExportDownloaded(
                owner.caseDriver(), () -> agent.ExportList("pdf"), "pdf");
    }

    void ExportAgentsToExcel() throws IOException {
        Agent agent = new Agent(owner.caseDriver());
        agent.NavigationHelper(Pages.AGENTS);
        DealerModelExportAssertions.assertExportDownloaded(
                owner.caseDriver(), () -> agent.ExportList("excel"), "excel");
    }
}
