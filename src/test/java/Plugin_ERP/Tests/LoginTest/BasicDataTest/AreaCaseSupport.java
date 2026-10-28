/* شرح الملف: خطوات تجهيز وتحقق مشتركة لحالات المناطق، على نفس جلسة وبيانات الحالة الأصلية.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import static TestSupport.BasicDataFixtures.fields;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.*;
import org.testng.Assert;

abstract class AreaCaseSupport extends BasicDataScenarioTest {
    final org.openqa.selenium.WebDriver caseDriver() {
        return driver;
    }

    final TestSupport.BasicDataFixtures caseData() {
        return basicData();
    }

    final String caseName(String endpoint, String label) {
        return ownedName(endpoint, label);
    }

    final long caseRecordCount(String endpoint) {
        return recordCount(endpoint);
    }

    final String uniqueName(String caseId) {
        return ownedName("Areas", caseId);
    }

    final AreaPage openAreas() {
        AreaPage page = new AreaPage(driver);
        page.NavigationHelper(Pages.AREA);
        return page;
    }

    final void createArea(AreaPage page, String arabicName, String englishName, String notes) {
        page.ClickNew();
        page.AddArabicName(arabicName);
        if (englishName != null) page.AddEnglishName(englishName);
        if (notes != null) page.AddNoteForDealer(notes);
        page.ClickSaveButton();
        page.SearchArea(arabicName);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(arabicName),
                1,
                "يجب أن يظهر سجل الاختبار الفريد مرة واحدة: " + arabicName);
    }

    final void seedAreas(String key, int count) {
        for (int i = 0; i < count; i++) {
            basicData().fixture(key + "-" + i, "Areas", fields());
        }
    }
}
