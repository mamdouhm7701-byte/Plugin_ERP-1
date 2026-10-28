/* شرح الملف: تنفيذ حالات المناطق الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
import io.qameta.allure.*;
import org.testng.Assert;

final class AreaSavingChecks {
    private final AreaTest owner;

    AreaSavingChecks(AreaTest owner) {
        this.owner = owner;
    }

    void AR_003_CreateValidAreaAndVerifyFields() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("CREATE");
        String english = "Automation Area " + System.currentTimeMillis();
        owner.createArea(page, name, english, "Created by automated Area test");
        page.EditAreaByName(name);
        Assert.assertEquals(page.GetArabicNameInputValue(), name);
        Assert.assertEquals(page.GetEnglishNameInputValue(), english);
        Assert.assertEquals(page.GetAreaNotesValue(), "Created by automated Area test");
        Assert.assertFalse(
                page.GetAreaCodeInputValue().isBlank(), "يجب تعيين كود للمنطقة المحفوظة");
        page.ClickCloseButton();
    }

    void AR_012_RapidDoubleSaveCreatesSingleArea() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("DOUBLE-SAVE");
        page.ClickNew();
        page.AddArabicName(name);
        page.AddEnglishName("Double Save Area");
        page.ClickSaveTwiceRapidly();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
    }

    void AR_021_EditOwnedAreaAndSave() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("EDIT");
        String updated = owner.uniqueName("UPDATED");
        owner.createArea(page, name, "Edit Area", "");
        page.EditAreaByName(name);
        page.AddArabicName(updated);
        page.ClickSaveButton();
        var savedEdit = owner.caseData().awaitSaved("Areas", updated);
        Assert.assertEquals(
                owner.caseData().read(savedEdit).get("arabicName"),
                updated,
                "Edited area must persist before checking its list row");
        page.SearchArea(updated);
        new org.openqa.selenium.support.ui.WebDriverWait(
                        owner.caseDriver(), java.time.Duration.ofSeconds(20))
                .until(d -> page.GetExactAreaMatchCount(updated) == 1);
        Assert.assertEquals(page.GetExactAreaMatchCount(updated), 1);
    }

    void AR_023_DeleteOnlyOwnedArea() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("DELETE");
        owner.createArea(page, name, "Delete Area", "");
        page.RequestDeleteAreaByName(name);
        Assert.assertTrue(page.IsAreaDeleteConfirmationDisplayed(), "يجب عرض تأكيد الحذف");
        page.ConfirmAreaDelete();
        owner.caseDriver().navigate().refresh();
        page = owner.openAreas();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 0);
    }
}
