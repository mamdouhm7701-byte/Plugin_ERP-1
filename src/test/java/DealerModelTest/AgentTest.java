/* شرح الملف: اختبارات المناديب: إلغاء تعديل المناديب؛ إلغاء حذف المناديب؛ إضافة مندوب - عمولة أصناف
 * عن طريق البحث؛ رفض اسم فارغ في المناديب؛ تصدير قائمة المناديب إلى PDF.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Agent;
import io.qameta.allure.*;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AgentTest extends DealerScenarioTest {

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-001 بنية قائمة المناديب وأوامر الصف")
    @Description("يتحقق من ظهور قائمة المناديب وسجلاتها وأوامر تعديلها وحذفها.")
    public void AG_001_AgentListStructure() {
        Agent page = new Agent(driver);
        page.NavigationHelper(Pages.AGENTS);
        seedRecord(page, Pages.AGENTS, "LIST");
        DealerModelScreenAssertions.assertListAndActionsDisplayed(page);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-002 إغلاق نموذج إضافة مندوب دون حفظ")
    @Description("يتحقق من عدم إنشاء مندوب عند إغلاق نموذج الإضافة قبل الحفظ.")
    public void AG_002_CloseAgentFormWithoutSaving() {
        Agent page = new Agent(driver);
        page.NavigationHelper(Pages.AGENTS);
        DealerModelScreenAssertions.assertNewFormCanBeClosedWithoutSaving(page);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-003 إلغاء تعديل المندوب دون حفظ")
    @Description(
            "يتحقق من تحميل بيانات المندوب المحدد ومن عدم حفظ التغيير عند إغلاق نموذج"
                    + " التعديل.")
    public void AG_003_CancelAgentEditWithoutSaving() {
        Agent page = new Agent(driver);
        page.NavigationHelper(Pages.AGENTS);
        assertOwnedEditCancelled(page, Pages.AGENTS);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-004 إلغاء حذف مندوب")
    @Description("يتحقق من ظهور تأكيد الحذف ومن بقاء المندوب عند إلغاء العملية.")
    public void AG_004_CancelAgentDelete() {
        Agent page = new Agent(driver);
        page.NavigationHelper(Pages.AGENTS);
        assertOwnedDeleteCancelled(page, Pages.AGENTS);
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("AG-005 دورة حياة مندوب مملوك للاختبار")
    @Description("ينشئ مندوبًا فريدًا، يعدله، ثم يحذف السجل نفسه ويتحقق من كل انتقال.")
    public void AG_005_OwnedAgentCreateEditDelete() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Agent page = new Agent(driver);
        String originalName = ownedName(page, Pages.AGENTS, "LIFECYCLE-ORIGINAL");
        String editedName = ownedName(page, Pages.AGENTS, "LIFECYCLE-EDITED");
        page.NavigationHelper(Pages.AGENTS);

        DealerModelScreenAssertions.assertOwnedCreateEditDelete(
                driver,
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

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - الصفحة الأولى")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب جديد من الصفحة الأولى، ويشمل إدخال الاسم"
                    + " العربي والإنجليزي، واختيار المنطقة وإضافة رقم الجوال والبريد الإلكتروني، وتحديد"
                    + " طبيعة المندوب ونوع العمولة وإحتساب العمولة والمخزن الافتراضي وتصنيف المندوب"
                    + " وقوائم الأسعار، والتحقق من حالة خانة (منع إصدار فاتورة خارج موقع العميل) وخانة"
                    + " (مفعل)، وإضافة الملاحظات وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentFDAtaFirstPAge() {
        prepareScenarioReferences("AddAgentFDAtaFirstPAge");
        Agent agent = new Agent(driver);
        String recordName = ownedName(agent, Pages.AGENTS, "AddAgentFDAtaFirstPAge");
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.AddEnglishName("Test " + recordName);
        agent.SelectFromDropDawn(data().area().name(), "اسم المنطقة");
        agent.AddAgentMobileNumber();
        agent.AddEmail(uniqueEmail());
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn("بيع", "نوع العمولة");
        agent.SelectFromDropDawn("مبيعات", "إحتساب العموله");
        agent.SelectFromDropDawn(data().store().name(), "المخزن الأفتراضى");
        agent.SelectFromDropDawn(data().classification("Agents").name(), "تصنيف المندوب");
        agent.SelectFromDropDawn(data().priceList().name(), "قوائم الأسعار");
        agent.SmartCheck("منع اصدار فاتورة خارج موقع العميل", true);
        agent.AssertCheckboxIsChecked("منع اصدار فاتورة خارج موقع العميل", true);
        agent.AssertCheckboxIsChecked("مفعل", true);
        agent.AddNoteForDealer("ملاحظات علي المندوب");
        agent.ClickSaveButton();
        waitForRecord(agent, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - تبويب العمولة")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع بيانات عمولة المندوب، ويشمل إدخال نطاق"
                    + " المبلغ (من/إلى) واختيار نوع العمولة (نسبة) وإدخال قيمة النسبة، والضغط على زر"
                    + " إضافة العمولة والتحقق من إضافتها بنجاح، وحفظ البيانات والتحقق من إضافة السجل.")
    public void AddAgentFDAtaSecondPAgeForAgentCommission() {
        Agent agent = new Agent(driver);
        String recordName =
                ownedName(agent, Pages.AGENTS, "AddAgentFDAtaSecondPAgeForAgentCommission");

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
        waitForRecord(agent, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف عن طريق الفئة")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق اختيار فئة، ويشمل"
                    + " الانتقال لتبويب عمولة الأصناف واختيار فئة أنشأها الاختبار، والتحقق من تحميل"
                    + " البيانات في الجدول وأن عدد الصفوف يساوي عدد أصناف الاختبار، وإدخال نسبة العمولة"
                    + " وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentThirdPageProductCommission() {
        prepareScenarioReferences("AddAgentThirdPageProductCommission");
        Agent agent = new Agent(driver);
        String recordName = ownedName(agent, Pages.AGENTS, "AddAgentThirdPageProductCommission");
        driver.navigate().refresh();
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.SelectFromDropDawn(data().category().name(), "الفئة");
        agent.WaitForCommissionItems(data().item(1).name(), data().item(2).name());
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertEquals(agent.getRowCount(), 2, "يجب تحميل الصنفين اللذين أنشأهما التست");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        waitForRecord(agent, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف لكل الأصناف")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق إضافة جميع الأصناف دفعة"
                    + " واحدة، والتحقق من تحميل البيانات في الجدول وأن عدد الصفوف يساوي عدد أصناف"
                    + " الاختبار، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentByAllProducts() {
        prepareScenarioReferences("AddAgentByAllProducts");
        Agent agent = new Agent(driver);
        String recordName = ownedName(agent, Pages.AGENTS, "AddAgentByAllProducts");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.ClickAddAllItems();
        agent.WaitForCommissionItems(data().item(1).name(), data().item(2).name());
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertEquals(agent.getRowCount(), 2, "يجب تحميل الصنفين اللذين أنشأهما التست");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        waitForRecord(agent, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف عن طريق البحث")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق البحث عن صنف بالكود،"
                    + " ويشمل البحث عن صنفين مختلفين وإضافتهم، والتحقق من تحميل البيانات في الجدول"
                    + " ووجود صفوف، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentBySearchProduct() {
        prepareScenarioReferences("AddAgentBySearchProduct");
        Agent agent = new Agent(driver);
        String recordName = ownedName(agent, Pages.AGENTS, "AddAgentBySearchProduct");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.SearchProduct((String) data().item(1).values().get("mainUnitBarcode"));
        agent.ClickEnter();
        agent.SearchProduct((String) data().item(2).values().get("mainUnitBarcode"));
        agent.ClickEnter();
        agent.WaitForCommissionItems(data().item(1).name(), data().item(2).name());
        Assert.assertTrue(agent.isDataPresentInTable(), "الجدول لم يتم تحميله بعد");
        Assert.assertTrue(agent.getRowCount() > 0, "الجدول لا يحتوي على أي صفوف");
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        waitForRecord(agent, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("إضافة مندوب - عمولة أصناف باختيار أصناف يدوياً")
    @Description(
            "يتحقق هذا الاختبار من إضافة مندوب مع عمولة أصناف عن طريق اختيار أصناف يدوياً من"
                    + " القائمة، ويشمل الضغط على زر إضافة الأصناف واختيار صنفين من القائمة وإغلاق"
                    + " النافذة، وإدخال نسبة العمولة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddAgentWithChoseItem() {
        prepareScenarioReferences("AddAgentWithChoseItem");
        Agent agent = new Agent(driver);
        String recordName = ownedName(agent, Pages.AGENTS, "AddAgentWithChoseItem");

        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(recordName);
        agent.SelectFromDropDawn("بيع", "طبيعه المندوب");
        agent.SelectFromDropDawn(data().store().name(), "المخزن الأفتراضى");
        agent.ClickItemCommissionTab();
        agent.ClickAddItemsButton();
        agent.AddCommissionItemsByName(data().item(1).name(), data().item(2).name());
        agent.ClickCloseButton();
        agent.WaitForCommissionItems(data().item(1).name(), data().item(2).name());
        agent.EnterPercentage("5");
        agent.ClickEnter();
        agent.ClickSaveButton();
        waitForRecord(agent, recordName);
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("حذف مندوب")
    @Description(
            "يتحقق هذا الاختبار من حذف المناديب المضافين، ويقوم بحذف ثلاثة سجلات متتالية"
                    + " للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Test(priority = 0)
    public void Delete() {
        Agent agent = new Agent(driver);
        deleteOwnedRecords(agent, Pages.AGENTS, 3);
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع حفظ المندوب في حال عدم إدخال الاسم العربي،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    public void AseertArabicNameRequired() {
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddEnglishName(ownedName(agent, Pages.AGENTS, "INVALID-ARABIC-NAME"));
        agent.ClickSaveButton();
        Assert.assertTrue(
                agent.IsArabicNameRequiredErrorDisplayed(),
                "Error message for required Arabic name is not displayed");
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - طبيعة المندوب إلزامية")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع حفظ المندوب في حال عدم تحديد طبيعة المندوب،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طبيعة المندوب إلزامي.")
    public void AseertAgentNaturalRequired() {
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(ownedName(agent, Pages.AGENTS, "VALIDATION"));
        agent.ClickSaveButton();
        Assert.assertTrue(
                agent.AssertAgentNatural(),
                "Error message for required Agent Natural is not displayed");
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("التحقق - خطأ عند تكرار نطاق العمولة")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة نطاق عمولة مكرر بنفس القيم، ويشمل"
                    + " إضافة عمولة بنطاق معين ثم محاولة إضافة نفس النطاق مرة أخرى، والتحقق من ظهور"
                    + " رسالة خطأ تفيد بعدم إمكانية تكرار نطاق العمولة.")
    public void AseertErrorMessageWhenAddTheSamePrecentge() {
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        agent.ClickNew();
        agent.AddArabicName(ownedName(agent, Pages.AGENTS, "VALIDATION"));
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

    @Test(priority = 90)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("تصدير قائمة المناديب إلى PDF")
    @Description("يتحقق من تنزيل PDF صالح من قائمة المناديب.")
    public void ExportAgentsToPdf() throws IOException {
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> agent.ExportList("pdf"), "pdf");
    }

    @Test(priority = 91)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناديب")
    @Story("تصدير قائمة المناديب إلى Excel")
    @Description("يتحقق من تنزيل Excel صالح من قائمة المناديب.")
    public void ExportAgentsToExcel() throws IOException {
        Agent agent = new Agent(driver);
        agent.NavigationHelper(Pages.AGENTS);
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> agent.ExportList("excel"), "excel");
    }
}
