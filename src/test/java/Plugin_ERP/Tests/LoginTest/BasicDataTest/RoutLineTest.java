package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.RoutLine;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RoutLineTest extends BasicDataScenarioTest {
    @org.testng.annotations.AfterMethod(alwaysRun = true)
    public void releaseOwnedDocumentKinds() {
        collectBasicDataScenario();
        basicData().close();
    }

    private String unique(String s) {
        return ownedName("AgentItinerariesStatus", s);
    }

    private RoutLine open() {
        RoutLine p = new RoutLine(driver);
        p.NavigationHelper(Pages.ROUTELINESTATUES);
        return p;
    }

    private void create(RoutLine p, String n, String en, String type, boolean location) {
        p.ClickNew();
        p.AddArabicName(n);
        if (en != null) p.AddEnglishName(en);
        p.SelectRadiBouton(type);
        p.AddNoteForDealer("QA route test");
        p.SetRouteLocation(location);
        p.ClickSaveButton();
        var saved = basicData().awaitSaved("AgentItinerariesStatus", n);
        Assert.assertEquals(
                basicData().read(saved).get("arabicName"),
                n,
                "Owned route status must be persisted before checking its row");
        Assert.assertEquals(p.GetExactMatchCount(n), 1, "يجب ظهور حالة خط السير مرة واحدة: " + n);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-001 بنية القائمة")
    @Description("يتحقق من أعمدة حالة خط السير ونوع المستند.")
    public void RL_001_ListStructure() {
        RoutLine p = open();
        create(p, unique("LIST"), null, "فاتورة", false);
        Assert.assertTrue(p.IsListStructureDisplayed());
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-002 عناصر النموذج")
    @Description("يتحقق من عناصر نموذج حالة خط السير.")
    public void RL_002_FormControls() {
        RoutLine p = open();
        p.ClickNew();
        Assert.assertTrue(p.GetMissingControls().isEmpty(), p.GetMissingControls());
        p.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-014 إغلاق دون حفظ")
    @Description("يتحقق من عدم إنشاء حالة عند إغلاق النموذج دون حفظ.")
    public void RL_014_CloseWithoutSave() {
        RoutLine p = open();
        String n = unique("UNSAVED");
        p.ClickNew();
        p.AddArabicName(n);
        p.ClickCloseButton();
        Assert.assertEquals(p.GetExactMatchCount(n), 0);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-004 الاسم مطلوب")
    @Description("يتحقق من رفض الحفظ دون الاسم العربي.")
    public void RL_004_ArabicNameRequired() {
        RoutLine p = open();
        p.ClickNew();
        p.AddNoteForDealer(unique("INVALID-NAME"));
        p.ClickSaveButton();
        Assert.assertTrue(p.IsArabicNameRequiredDisplayed());
        p.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-003 إنشاء والتحقق")
    @Description("إعادة فحص حفظ نوع المستند فاتورة ومطابقته عند فتح السجل المملوك.")
    public void RL_003_CreateAndVerify() {
        RoutLine p = open();
        String n = unique("CREATE"), en = "Route " + System.currentTimeMillis();
        create(p, n, en, "فاتورة", true);
        p.OpenForEditByName(n);
        Assert.assertEquals(p.GetArabicNameValue(), n);
        Assert.assertEquals(p.GetEnglishNameValue(), en);
        Assert.assertEquals(p.GetDocumentTypeValue(), "فاتورة");
        Assert.assertTrue(p.IsRouteLocationChecked());
        Assert.assertFalse(p.GetCodeValue().isBlank());
        p.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-007 نوع التحصيل وخيار الموقع")
    @Description("إعادة فحص حفظ نوع المستند تحصيل وخيار الموقع في السجل المملوك.")
    public void RL_007_CollectionTypeAndLocationOff() {
        RoutLine p = open();
        String n = unique("COLLECTION");
        create(p, n, null, "تحصيل", false);
        p.OpenForEditByName(n);
        Assert.assertEquals(p.GetDocumentTypeValue(), "تحصيل");
        Assert.assertFalse(p.IsRouteLocationChecked());
        p.ClickCloseButton();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-021 تعديل سجل الاختبار")
    @Description("يعدل الاسم ونوع المستند في السجل المحدد.")
    public void RL_021_EditOwned() {
        RoutLine p = open();
        String n = unique("EDIT"), u = unique("UPDATED");
        create(p, n, "Route edit", "فاتورة", false);
        p.OpenForEditByName(n);
        p.AddArabicName(u);
        p.SelectRadiBouton("تحصيل");
        p.ClickSaveButton();
        Assert.assertEquals(p.GetExactMatchCount(u), 1);
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-022 إلغاء تعديل")
    @Description("يتحقق من إلغاء التغيير عند إغلاق النموذج.")
    public void RL_022_CancelEdit() {
        RoutLine p = open();
        String n = unique("CANCEL-EDIT");
        create(p, n, null, "فاتورة", false);
        p.OpenForEditByName(n);
        p.AddArabicName(unique("TEMP"));
        p.ClickCloseButton();
        p.OpenForEditByName(n);
        Assert.assertEquals(p.GetArabicNameValue(), n);
        p.ClickCloseButton();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-023 حذف سجل الاختبار")
    @Description("يحذف السجل المطابق الذي أنشأه الاختبار فقط.")
    public void RL_023_DeleteOwned() {
        RoutLine p = open();
        String n = unique("DELETE");
        create(p, n, null, "فاتورة", false);
        p.RequestDeleteByName(n);
        Assert.assertTrue(p.IsDeleteConfirmationDisplayed());
        p.ConfirmDelete();
        Assert.assertEquals(p.GetExactMatchCount(n), 0);
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-024 إلغاء الحذف")
    @Description("يتحقق من بقاء السجل عند إلغاء تأكيد الحذف.")
    public void RL_024_CancelDelete() {
        RoutLine p = open();
        String n = unique("CANCEL-DELETE");
        create(p, n, null, "فاتورة", false);
        p.RequestDeleteByName(n);
        p.CancelDelete();
        Assert.assertEquals(p.GetExactMatchCount(n), 1);
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حالة خط السير")
    @Story("RL-032 الاستمرار بعد تحديث الصفحة")
    @Description("يتحقق من استمرار السجل بعد تحديث الشاشة.")
    public void RL_032_PersistAfterRefresh() {
        RoutLine p = open();
        String n = unique("REFRESH");
        create(p, n, "Route refresh", "فاتورة", true);
        driver.navigate().refresh();
        p = open();
        Assert.assertEquals(p.GetExactMatchCount(n), 1);
    }
}
