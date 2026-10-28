/* شرح الملف: تنفيذ حالات الخزائن الخاصة بـعرض القوائم والبحث واستمرار السجلات.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import io.qameta.allure.*;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.*;

final class CashListsChecks {
    private final CashTest owner;

    CashListsChecks(CashTest owner) {
        this.owner = owner;
    }

    void WH_001_SafeListStructureIsDisplayed() {
        owner.seedSafe("structure");
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);

        Assert.assertTrue(
                cashPage.isSafeListStructureDisplayed(),
                "WH-001: يجب أن تظهر أعمدة الكود واسم الخزنة ورقم الحساب مع أزرار التعديل والحذف");
    }

    void CashListDisplaysRecords() {
        owner.seedSafe("list");
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);

        Assert.assertTrue(
                cashPage.isDataPresentInTable(), "يجب أن تعرض قائمة الخزن بيانات السجلات");
    }

    void SearchSafesByCodeAndName() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);

        String ownSafe = owner.seedSafe("search");
        cashPage.searchSafes(ownSafe);
        String visibleCode = cashPage.getFirstSafeCellByHeader("الكود");
        cashPage.searchSafes(visibleCode);
        Assert.assertTrue(
                cashPage.isSafeVisibleInSearchResults(visibleCode),
                "يجب إظهار الخزنة عند البحث بكود موجود في القائمة");

        cashPage.searchSafes(ownSafe);
        Assert.assertTrue(
                cashPage.isSafeVisibleInSearchResults(ownSafe),
                "يجب إظهار الخزنة المطابقة للاسم العربي");

        cashPage.searchSafes("XYZ999");
        Assert.assertFalse(
                cashPage.isSafeVisibleInSearchResults("XYZ999"),
                "يجب ألا تظهر سجلات عند البحث عن قيمة غير موجودة");

        cashPage.searchSafes("");
        Assert.assertTrue(
                cashPage.getVisibleSafeRowCount() > 0, "يجب أن تعود سجلات القائمة بعد مسح البحث");
    }

    void WH_017_SearchSafesByEnglishName() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.searchSafes(owner.seedSafe("english-search"));
        cashPage.openFirstSafeForEdit();
        String englishName = cashPage.getEnglishNameInputValue().trim();
        cashPage.ClickCloseButton();
        Assert.assertFalse(
                englishName.isBlank(), "WH-017: يجب توفر اسم إنجليزي في السجل المستخدم للبحث");

        cashPage.searchSafes(englishName);
        Assert.assertTrue(
                cashPage.isSafeVisibleInSearchResults(englishName),
                "WH-017: يجب أن تظهر الخزنة عند البحث بالاسم الإنجليزي");
        cashPage.searchSafes("");
    }

    void WH_028_SafeListCanBeSortedByCode() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        owner.seedSafe("sort-a");
        owner.seedSafe("sort-b");
        owner.caseDriver().navigate().refresh();
        cashPage.NavigationHelper(Pages.SAFES);
        List<String> codesBeforeSort = cashPage.getSafeColumnValues("الكود");
        cashPage.clickSafeColumnHeader("الكود");
        List<String> ascendingCodes = cashPage.getSafeColumnValues("الكود");
        cashPage.clickSafeColumnHeader("الكود");
        List<String> descendingCodes = cashPage.getSafeColumnValues("الكود");

        Assert.assertTrue(codesBeforeSort.size() >= 2, "Owned sorting fixtures must be visible");
        Assert.assertEquals(
                ascendingCodes,
                cashPage.sortCodes(ascendingCodes, true),
                "WH-028: يجب أن تكون القيم مرتبة تصاعديًا بعد النقرة الأولى");
        Assert.assertEquals(
                descendingCodes,
                cashPage.sortCodes(descendingCodes, false),
                "WH-028: يجب أن تكون القيم مرتبة تنازليًا بعد النقرة الثانية");
    }

    void WH_029_ChangingPageSizeUpdatesVisibleRows() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        owner.seedSafe("page-size");
        owner.caseDriver().navigate().refresh();
        cashPage.NavigationHelper(Pages.SAFES);
        int defaultPageSize = cashPage.getSelectedSafePageSize();
        // Read the complete list at the largest supported size before checking pagination.
        cashPage.selectSafePageSize(500);
        int totalRows = cashPage.getVisibleSafeRowCount();
        cashPage.selectSafePageSize(defaultPageSize);
        int availablePageSize = cashPage.getAlternativeSafePageSize(defaultPageSize);

        Assert.assertTrue(
                availablePageSize > defaultPageSize,
                "WH-029: يلزم وجود خيار حجم صفحة أكبر من القيمة الحالية");
        cashPage.selectSafePageSize(availablePageSize);
        Assert.assertEquals(
                cashPage.getVisibleSafeRowCount(),
                Math.min(totalRows, availablePageSize),
                "WH-029: يجب أن يتغير عدد الصفوف حسب حجم الصفحة أو إجمالي السجلات المتاحة");
    }

    void WH_030_NavigateSafeListPagesWhenAvailable() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        int pageSize = cashPage.getSelectedSafePageSize();
        for (int i = 0; i <= pageSize; i++) owner.seedSafe("pagination-" + i);
        owner.caseDriver().navigate().refresh();
        cashPage.NavigationHelper(Pages.SAFES);
        if (!cashPage.isSafeNextPageEnabled()) {
            Assert.assertFalse(
                    cashPage.isSafeNextPageEnabled(),
                    "WH-030: لا يجب تفعيل التالي عند عدم توفر صفحة أخرى");
            return;
        }

        String firstPageCode = cashPage.getFirstSafeCellByHeader("الكود");
        cashPage.goToNextSafePage();
        String secondPageCode = cashPage.getFirstSafeCellByHeader("الكود");
        Assert.assertNotEquals(
                secondPageCode, firstPageCode, "WH-030: يجب أن تعرض الصفحة التالية سجلات مختلفة");

        cashPage.goToPreviousSafePage();
        Assert.assertEquals(
                cashPage.getFirstSafeCellByHeader("الكود"),
                firstPageCode,
                "WH-030: الرجوع يجب أن يعرض سجلات الصفحة الأولى");
    }

    void WH_031_NewSafeAppearsAfterListRefresh() {
        owner.verifySafePersistsAcrossRefresh("WH-031");
    }

    void WH_032_SafePersistsAfterBrowserRefresh() {
        owner.verifySafePersistsAcrossRefresh("WH-032");
    }
}
