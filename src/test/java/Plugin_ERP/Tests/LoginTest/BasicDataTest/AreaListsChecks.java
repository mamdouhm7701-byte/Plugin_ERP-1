/* شرح الملف: تنفيذ حالات المناطق الخاصة بـعرض القوائم والبحث واستمرار السجلات.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
import io.qameta.allure.*;
import java.util.List;
import org.testng.Assert;

final class AreaListsChecks {
    private final AreaTest owner;

    AreaListsChecks(AreaTest owner) {
        this.owner = owner;
    }

    void AR_001_AreaListStructureIsDisplayed() {
        AreaPage page = owner.openAreas();
        owner.createArea(page, owner.uniqueName("LIST"), null, null);
        Assert.assertTrue(
                page.IsAreaListStructureDisplayed(),
                "AR-001: يجب ظهور أعمدة وإجراءات قائمة المناطق");
    }

    void AR_015_SearchAreasByCodeAndName() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("READ");
        owner.createArea(page, name, null, null);
        String code = page.GetAreaCellByName(name, "الكود");
        page.SearchArea(code);
        Assert.assertTrue(page.IsAreaDisplayed(name), "يجب إظهار المنطقة بالبحث بالكود");
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1, "يجب إظهار المنطقة بالاسم");
        page.SearchArea("QA-AREA-NOT-FOUND");
        new org.openqa.selenium.support.ui.WebDriverWait(
                        owner.caseDriver(), java.time.Duration.ofSeconds(15))
                .withMessage("Area search retained rows for a nonmatching query")
                .until(d -> page.GetVisibleAreaRowCount() == 0);
        Assert.assertEquals(page.GetVisibleAreaRowCount(), 0, "يجب ألا تظهر نتائج غير مطابقة");
        page.SearchArea("");
        new org.openqa.selenium.support.ui.WebDriverWait(
                        owner.caseDriver(), java.time.Duration.ofSeconds(15))
                .withMessage("Area list did not return after clearing search")
                .until(d -> page.GetVisibleAreaRowCount() > 0);
        Assert.assertTrue(page.GetVisibleAreaRowCount() > 0, "يجب استعادة القائمة بعد مسح البحث");
    }

    void AR_017_SearchUsesOnlyDisplayedAreaFields() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("ENGLISH");
        String english = "Area English " + System.currentTimeMillis();
        owner.createArea(page, name, english, null);
        page.SearchArea(english);
        new org.openqa.selenium.support.ui.WebDriverWait(
                        owner.caseDriver(), java.time.Duration.ofSeconds(15))
                .withMessage("Area search returned a row for its hidden English name")
                .until(d -> page.GetVisibleAreaRowCount() == 0);
        Assert.assertEquals(
                page.GetVisibleAreaRowCount(),
                0,
                "البحث يعتمد على الكود والاسم العربي المعروضين في الجدول، ولا يبحث بالاسم الإنجليزي"
                        + " المخفي");
        page.SearchArea(name);
        new org.openqa.selenium.support.ui.WebDriverWait(
                        owner.caseDriver(), java.time.Duration.ofSeconds(15))
                .withMessage("Area Arabic-name search did not display the owned row")
                .until(d -> page.GetExactAreaMatchCount(name) == 1);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name),
                1,
                "يجب العثور على المنطقة عبر الاسم العربي المعروض");
    }

    void AR_028_AreaListCanBeSortedByCode() {
        owner.seedAreas("sort", 2);
        AreaPage page = owner.openAreas();
        Assert.assertTrue(page.GetAreaColumnValues("الكود").size() >= 2);
        page.ClickAreaColumnHeader("الكود");
        List<String> ascending = page.GetAreaColumnValues("الكود");
        page.ClickAreaColumnHeader("الكود");
        List<String> descending = page.GetAreaColumnValues("الكود");
        Assert.assertEquals(
                ascending, page.SortAreaCodes(ascending, true), "يجب فرز الكود تصاعديًا");
        Assert.assertEquals(
                descending, page.SortAreaCodes(descending, false), "يجب فرز الكود تنازليًا");
    }

    void AR_029_ChangingPageSizeUpdatesVisibleRows() {
        owner.seedAreas("page-size", 2);
        AreaPage page = owner.openAreas();
        int oldSize = page.GetAreaPageSize();
        int newSize = page.GetAlternativeAreaPageSize(oldSize);
        page.SelectAreaPageSize(newSize);
        Assert.assertEquals(page.GetAreaPageSize(), newSize);
        Assert.assertTrue(page.GetVisibleAreaRowCount() > 0);
        Assert.assertTrue(page.GetVisibleAreaRowCount() <= newSize);
    }

    void AR_030_NavigateAreaPagesWhenAvailable() {
        AreaPage page = owner.openAreas();
        owner.seedAreas("pagination", page.GetAreaPageSize() + 1);
        page = owner.openAreas();
        Assert.assertTrue(page.IsNextAreaPageEnabled(), "Owned records must fill multiple pages");
        String first = page.GetFirstAreaCellByHeader("الكود");
        page.GoToNextAreaPage();
        Assert.assertNotEquals(page.GetFirstAreaCellByHeader("الكود"), first);
        page.GoToPreviousAreaPage();
        Assert.assertEquals(page.GetFirstAreaCellByHeader("الكود"), first);
    }

    void AR_032_AreaPersistsAfterBrowserRefresh() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("REFRESH");
        owner.createArea(page, name, "Refresh Area", "");
        owner.caseDriver().navigate().refresh();
        page = owner.openAreas();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
    }
}
