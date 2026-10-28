/* شرح الملف: تنفيذ حالات المناديب الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import io.qameta.allure.*;
import org.testng.Assert;

final class AgentSavingChecks {
    private final AgentTest owner;

    AgentSavingChecks(AgentTest owner) {
        this.owner = owner;
    }

    void AG_005_OwnedAgentCreateEditDelete() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Agent page = new Agent(owner.caseDriver());
        String originalName = owner.ownedName(page, Pages.AGENTS, "LIFECYCLE-ORIGINAL");
        String editedName = owner.ownedName(page, Pages.AGENTS, "LIFECYCLE-EDITED");
        page.NavigationHelper(Pages.AGENTS);

        DealerModelScreenAssertions.assertOwnedCreateEditDelete(
                owner.caseDriver(),
                page,
                originalName,
                editedName,
                form -> {
                    form.AddEnglishName("QA Agent " + suffix);
                    ((Agent) form).AddAgentMobileNumber();
                    form.AddEmail("qa.agent." + suffix + "@example.com");
                    form.SelectDealerOption("طبيعه المندوب", "بيع");
                },
                () -> page.NavigationHelper(Pages.AGENTS));
    }

    void AddAgentFDAtaFirstPAge() {
        owner.prepareScenarioReferences("AddAgentFDAtaFirstPAge");
        Agent agent = new Agent(owner.caseDriver());
        String recordName = owner.ownedName(agent, Pages.AGENTS, "AddAgentFDAtaFirstPAge");
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.AddEnglishName("Test " + recordName);
        agent.SelectFromDropDawn(owner.data().area().name(), "اسم المنطقة");
        agent.AddAgentMobileNumber();
        agent.AddEmail(owner.uniqueEmail());
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn("بيع", "نوع العمولة");
        agent.SelectFromDropDawn("مبيعات", "إحتساب العموله");
        agent.SelectFromDropDawn(owner.data().store().name(), "المخزن الأفتراضى");
        agent.SelectFromDropDawn(owner.data().classification("Agents").name(), "تصنيف المندوب");
        agent.SelectFromDropDawn(owner.data().priceList().name(), "قوائم الأسعار");
        agent.SmartCheck("منع اصدار فاتورة خارج موقع العميل", true);
        agent.AssertCheckboxIsChecked("منع اصدار فاتورة خارج موقع العميل", true);
        agent.AssertCheckboxIsChecked("مفعل", true);
        agent.AddNoteForDealer("ملاحظات علي المندوب");
        agent.ClickSaveButton();
        owner.waitForRecord(agent, recordName);
    }

    void AddAgentFDAtaSecondPAgeForAgentCommission() {
        Agent agent = new Agent(owner.caseDriver());
        String recordName =
                owner.ownedName(agent, Pages.AGENTS, "AddAgentFDAtaSecondPAgeForAgentCommission");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.ClickAgentCommissionTab();
        agent.EnterFromAmount("100");
        agent.EnterToAmount("2000");
        agent.SelectByKeys("نسبه");
        agent.EnterPercentage("10");
        agent.ClickAddCommissionButton();
        agent.ClickSaveButton();
        owner.waitForRecord(agent, recordName);
    }

    void AddAgentThirdPageProductCommission() {
        owner.prepareScenarioReferences("AddAgentThirdPageProductCommission");
        Agent agent = new Agent(owner.caseDriver());
        String recordName =
                owner.ownedName(agent, Pages.AGENTS, "AddAgentThirdPageProductCommission");
        owner.caseDriver().navigate().refresh();
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(owner.data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.SelectFromDropDawn(owner.data().category().name(), "الفئة");
        agent.WaitForCommissionItems(owner.data().item(1).name(), owner.data().item(2).name());
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertEquals(agent.getRowCount(), 2, "يجب تحميل الصنفين اللذين أنشأهما التست");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        owner.waitForRecord(agent, recordName);
    }

    void AddAgentByAllProducts() {
        owner.prepareScenarioReferences("AddAgentByAllProducts");
        Agent agent = new Agent(owner.caseDriver());
        String recordName = owner.ownedName(agent, Pages.AGENTS, "AddAgentByAllProducts");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(owner.data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.ClickAddAllItems();
        agent.WaitForCommissionItems(owner.data().item(1).name(), owner.data().item(2).name());
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertEquals(agent.getRowCount(), 2, "يجب تحميل الصنفين اللذين أنشأهما التست");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        owner.waitForRecord(agent, recordName);
    }

    void AddAgentWithChoseItem() {
        owner.prepareScenarioReferences("AddAgentWithChoseItem");
        Agent agent = new Agent(owner.caseDriver());
        String recordName = owner.ownedName(agent, Pages.AGENTS, "AddAgentWithChoseItem");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(owner.data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.ClickAddItemsButton();
        agent.AddCommissionItemsByName(owner.data().item(1).name(), owner.data().item(2).name());
        agent.ClickCloseButton();
        agent.WaitForCommissionItems(owner.data().item(1).name(), owner.data().item(2).name());
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        owner.waitForRecord(agent, recordName);
    }

    void Delete() {
        Agent agent = new Agent(owner.caseDriver());
        owner.deleteOwnedRecords(agent, Pages.AGENTS, 3);
    }

    void AseertErrorMessageWhenAddTheSamePrecentge() {
        Agent agent = new Agent(owner.caseDriver());
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(owner.ownedName(agent, Pages.AGENTS, "VALIDATION"));
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.ClickAgentCommissionTab();
        agent.EnterFromAmount("100");
        agent.EnterToAmount("2000");
        agent.SelectByKeys("نسبه");
        agent.EnterPercentage("10");
        agent.ClickAddCommissionButton();
        agent.EnterFromAmount("100");
        agent.EnterToAmount("2000");
        agent.SelectByKeys("نسبه");
        agent.EnterPercentage("10");
        agent.ClickAddCommissionButton();
        Assert.assertTrue(
                agent.isErrorPopupDisplayed(),
                "يجب رفض إضافة نطاق العمولة المكرر وإظهار رسالة خطأ");
    }
}
