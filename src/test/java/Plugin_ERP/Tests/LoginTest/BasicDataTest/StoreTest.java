package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.StorePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreTest extends BaseTest {
    private String unique(String suffix) {
        return "QA-STORE-" + suffix + "-" + System.currentTimeMillis();
    }

    private StorePage open() {
        StorePage p = new StorePage(driver);
        p.NavigationHelper(Pages.WAREHOUSES);
        return p;
    }

    private void create(StorePage p, String name, String english, String account) {
        p.ClickNew();
        p.AddArabicName(name);
        if (english != null) p.AddEnglishName(english);
        if (account != null) p.SelectFromDropDawn(account, "رقم الحساب");
        p.ClickSaveButton();
        Assert.assertEquals(
                p.GetExactMatchCount(name), 1, "يجب ظهور سجل الاختبار مرة واحدة: " + name);
    }

    private void cleanup(StorePage p, String... names) {
        for (String n : names)
            try {
                p.CloseFormIfOpen();
                if (n != null && p.GetExactMatchCount(n) > 0) {
                    p.RequestDeleteByName(n);
                    p.ConfirmDelete(n);
                }
            } catch (Exception e) {
                System.err.println(
                        "Could not clean owned Store row '" + n + "': " + e.getMessage());
            }
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-001 بنية القائمة")
    @Description("يتحقق من أعمدة قائمة المخازن.")
    public void STORE_001_ListStructure() {
        Assert.assertTrue(open().IsListStructureDisplayed());
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-002 عناصر النموذج")
    @Description("يتحقق من ظهور حقول المخزن وأزرار الحفظ والإغلاق.")
    public void STORE_002_FormControls() {
        StorePage p = open();
        p.ClickNew();
        Assert.assertEquals(p.GetMissingControls(), "");
        p.ClickCloseButton();
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-003 إغلاق دون حفظ")
    @Description("يتحقق من عدم إنشاء سجل عند إغلاق النموذج قبل الحفظ.")
    public void STORE_003_CloseWithoutSave() {
        StorePage p = open();
        String n = unique("UNSAVED");
        p.ClickNew();
        p.AddArabicName(n);
        p.ClickCloseButton();
        Assert.assertEquals(p.GetExactMatchCount(n), 0);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-004 الاسم العربي مطلوب")
    @Description("يتحقق من رفض حفظ نموذج دون الاسم العربي.")
    public void STORE_004_ArabicNameRequired() {
        StorePage p = open();
        p.ClickNew();
        p.ClickSaveButton();
        Assert.assertTrue(p.IsArabicNameRequiredDisplayed());
        p.ClickCloseButton();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-005 إنشاء والتحقق من بيانات المخزن")
    @Description("ينشئ مخزنًا فريدًا ويقارن الاسم والكود والحساب بعد إعادة فتحه.")
    public void STORE_005_CreateAndVerify() {
        StorePage p = open();
        String n = unique("CREATE"), en = "Store " + System.currentTimeMillis();
        try {
            create(p, n, en, "تسويق جوجل");
            p.OpenForEditByName(n);
            Assert.assertEquals(p.GetArabicNameValue(), n);
            Assert.assertEquals(p.GetEnglishNameValue(), en);
            Assert.assertEquals(p.GetSelectedAccountValue(), "تسويق جوجل");
            Assert.assertFalse(p.GetCodeValue().isBlank());
            p.ClickCloseButton();
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-006 تعديل سجل اختبار")
    @Description("يعدل الاسم العربي لسجل أنشأه الاختبار ويتأكد من التغيير.")
    public void STORE_006_EditOwned() {
        StorePage p = open();
        String n = unique("EDIT"), u = n + "-UPDATED";
        try {
            create(p, n, null, "تسويق جوجل");
            p.OpenForEditByName(n);
            p.AddArabicName(u);
            p.ClickSaveButton();
            Assert.assertEquals(p.GetExactMatchCount(u), 1);
        } finally {
            cleanup(p, u, n);
        }
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-007 حذف سجل اختبار")
    @Description("يحذف سجلًا أنشأه الاختبار بعد تأكيد نافذة الحذف.")
    public void STORE_007_DeleteOwned() {
        StorePage p = open();
        String n = unique("DELETE");
        try {
            create(p, n, null, "تسويق جوجل");
            p.RequestDeleteByName(n);
            Assert.assertTrue(p.IsDeleteConfirmationDisplayed());
            p.ConfirmDelete(n);
            Assert.assertEquals(p.GetExactMatchCount(n), 0);
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-008 إلغاء الحذف")
    @Description("يتحقق من بقاء سجل الاختبار عند إلغاء الحذف.")
    public void STORE_008_CancelDelete() {
        StorePage p = open();
        String n = unique("CANCEL-DELETE");
        try {
            create(p, n, null, "تسويق جوجل");
            p.RequestDeleteByName(n);
            p.CancelDelete();
            Assert.assertEquals(p.GetExactMatchCount(n), 1);
        } finally {
            cleanup(p, n);
        }
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("ST-009 استمرار بعد التحديث")
    @Description("يتحقق من استمرار مخزن الاختبار بعد تحديث الصفحة.")
    public void STORE_009_PersistAfterRefresh() {
        StorePage p = open();
        String n = unique("REFRESH");
        try {
            create(p, n, "Refresh store", "تسويق جوجل");
            driver.navigate().refresh();
            p = open();
            Assert.assertEquals(p.GetExactMatchCount(n), 1);
        } finally {
            cleanup(p, n);
        }
    }
}
