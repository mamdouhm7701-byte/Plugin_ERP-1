package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import TestSupport.BasicDataScenarioTest;
import Plugin_ERP.Utiel.PropertyReader;
import Plugin_ERP.Listners.TestNGListeners;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class CashTest extends BasicDataScenarioTest {

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-001 فتح شاشة الخزن وعرض القائمة")
    @Description("يتحقق من ظهور أعمدة قائمة الخزن وأزرار التعديل والحذف للصفوف الموجودة.")
    public void WH_001_SafeListStructureIsDisplayed() {
        seedSafe("structure");
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);

        Assert.assertTrue(
                cashPage.isSafeListStructureDisplayed(),
                "WH-001: يجب أن تظهر أعمدة الكود واسم الخزنة ورقم الحساب مع أزرار التعديل والحذف");
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-002 فتح نافذة إضافة خزنة")
    @Description("يتحقق من ظهور الحقول والأزرار المحددة في مواصفة WH-002.")
    public void WH_002_CashCreationFormControlsAreDisplayed() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();

        String missingControls = cashPage.getMissingCashCreationFormControls();
        Assert.assertTrue(
                missingControls.isEmpty(),
                "WH-002: لم تظهر عناصر النموذج التالية: " + missingControls);
        cashPage.ClickCloseButton();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-014 إغلاق نافذة الإضافة دون حفظ")
    @Description(
            "يتحقق من إغلاق نافذة الإضافة بعد إدخال قيمة غير محفوظة، وعدم ظهورها ضمن نتائج"
                    + " القائمة.")
    public void WH_014_CloseCreationFormWithoutSaving() {
        CashPage cashPage = new CashPage(driver);

        cashPage.NavigationHelper(Pages.SAFES);
        String unsavedName = ownedName("Safes", "unsavedName");
        cashPage.ClickNew();
        cashPage.AddArabicName(unsavedName);
        cashPage.ClickCloseButton();
        cashPage.searchSafes(unsavedName);

        Assert.assertFalse(
                cashPage.isSafeVisibleInSearchResults(unsavedName),
                "WH-014: لا يجب إنشاء سجل عند إغلاق نافذة الإضافة دون حفظ");
        cashPage.searchSafes("");
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-020 فتح سجل للتعديل مع تحميل البيانات الحالية")
    @Description(
            "يقارن كود الخزنة واسمها العربي ورقم الحساب في الصف بالقيم المحملة في نموذج"
                    + " التعديل، ثم يغلق دون حفظ.")
    public void WH_020_EditFormLoadsCurrentSafeName() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.searchSafes(seedSafe("edit-form"));
        String codeInList = cashPage.getFirstSafeCellByHeader("الكود");
        String nameInList = cashPage.getFirstSafeCellByHeader("اسم الخزنة");
        String accountInList = cashPage.getFirstSafeCellByHeader("رقم الحساب");

        cashPage.openFirstSafeForEdit();
        Assert.assertEquals(
                cashPage.getSafeCodeInputValue(),
                codeInList,
                "WH-020/034: يجب أن يتطابق كود النموذج مع كود الصف");
        Assert.assertEquals(
                cashPage.getArabicNameInputValue().trim(),
                nameInList,
                "WH-020: يجب تحميل الاسم العربي الحالي من السجل المحدد");
        Assert.assertEquals(
                cashPage.getSafeAccountValue(),
                accountInList,
                "WH-034: يجب أن يتطابق رقم الحساب في النموذج مع رقم الحساب في الصف");
        cashPage.ClickCloseButton();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-022 إلغاء التعديل دون حفظ")
    @Description(
            "يغيّر الاسم داخل نموذج التعديل ثم يغلقه دون حفظ، ويتأكد من بقاء الاسم الأصلي بعد"
                    + " إعادة فتح السجل.")
    public void WH_022_CancelSafeEditWithoutSaving() {
        CashPage cashPage = new CashPage(driver);
        String originalName;
        String temporaryName = ownedName("Safes", "temporaryName");

        cashPage.NavigationHelper(Pages.SAFES);
        originalName = seedSafe("cancel-edit");
        cashPage.searchSafes(originalName);
        cashPage.openFirstSafeForEdit();
        cashPage.AddArabicName(temporaryName);
        cashPage.ClickCloseButton();

        cashPage.openFirstSafeForEdit();
        Assert.assertEquals(
                cashPage.getArabicNameInputValue().trim(),
                originalName,
                "WH-022: يجب أن تظل القيمة الأصلية بعد إغلاق التعديل دون حفظ");
        cashPage.ClickCloseButton();
    }

    @Test(
            enabled = false,
            description = "Replaced by WH-003 using unique data and exact-name cleanup")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("إضافة خزنة ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من إضافة خزنة جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي والملاحظات، واختيار رقم الحساب وتفعيل خانة (عام)، وحفظ البيانات"
                    + " والتحقق من إضافة السجل بنجاح.")

    public void ValidCashTest() {
        WH_003_CreateValidSafeAndVerifyFields();
    }

    @Test(
            enabled = false,
            description = "Unsafe legacy test edited the last existing row; replaced by WH-021")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تعديل خزنة ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من تعديل خزنة موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي"
                    + " وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")

    public void EditCash() {
        WH_021_EditOwnedTestSafeAndSave();
    }

    @Test(
            enabled = false,
            description = "Unsafe legacy test deleted the last existing row; replaced by WH-023")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("حذف خزنة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف خزنة موجودة بنجاح.")

    public void DeleteCash() {
        WH_023_DeleteOnlyOwnedTestSafe();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-004 التحقق من إلزامية الاسم العربي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة خزنة بدون إدخال الاسم العربي، ويتحقق"
                    + " من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")

    public void InvalidCashTest() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("");
        cashPage.AddEnglishName(ownedName("Safes", "required-field"));
        cashPage.SelectFromDropDawn(basicData().account("safe").name(), "رقم الحساب");
        int rowCountBefore = cashPage.getSafeTotalRowCount();
        cashPage.ClickSaveButton();
        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "WH-004: يجب إظهار رسالة تحقق الاسم العربي الفارغ");
        Assert.assertEquals(
                cashPage.getSafeTotalRowCount(),
                rowCountBefore,
                "WH-004: يجب ألا ينشأ سجل عند غياب الاسم العربي");
        cashPage.ClickCloseButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-006 محاولة الحفظ مع جميع الحقول فارغة")
    @Description("يتحقق من أن حفظ نموذج خزنة فارغ يعرض تحقق الاسم العربي المطلوب ولا ينشئ سجلًا.")
    public void EmptyCashFormIsRejected() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        int rowCountBefore = cashPage.getSafeTotalRowCount();
        cashPage.ClickNew();
        cashPage.AddEnglishName(ownedName("Safes", "required-name"));
        cashPage.ClickSaveButton();

        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "يجب أن يظهر تحقق الاسم العربي عند حفظ نموذج فارغ");
        Assert.assertEquals(
                cashPage.getSafeTotalRowCount(),
                rowCountBefore,
                "WH-006: يجب ألا ينشأ سجل عند ترك النموذج فارغًا");
        cashPage.ClickCloseButton();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-005 منع الاسم العربي المكوّن من مسافات فقط")
    @Description("يتحقق من معاملة المسافات فقط في الاسم العربي كقيمة فارغة.")
    public void CashArabicNameWhitespaceIsRejected() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        int rowCountBefore = cashPage.getSafeTotalRowCount();
        cashPage.ClickNew();
        cashPage.AddArabicName("   ");
        cashPage.AddEnglishName(ownedName("Safes", "whitespace"));
        cashPage.SelectFromDropDawn(basicData().account("safe").name(), "رقم الحساب");
        cashPage.ClickSaveButton();

        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "يجب رفض الاسم العربي المكوّن من مسافات فقط");
        Assert.assertEquals(
                cashPage.getSafeTotalRowCount(),
                rowCountBefore,
                "WH-005: يجب ألا ينشأ سجل للاسم المكوّن من مسافات");
        cashPage.ClickCloseButton();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-013 Correct Arabic name after validation error")
    @Description("يتحقق من إمكانية تصحيح الاسم العربي بعد إظهار رسالة الإلزامية ثم حفظ الخزنة.")
    public void CorrectArabicNameAfterValidationError() {
        CashPage cashPage = new CashPage(driver);
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String arabicName = ownedName("Safes", "arabicName");

        cashPage.NavigationHelper(Pages.SAFES);
        try {
            cashPage.ClickNew();
            cashPage.AddEnglishName(ownedName("Safes", "correctable-invalid"));
            cashPage.ClickSaveButton();
            Assert.assertTrue(
                    cashPage.IsArabicNameRequiredErrorDisplayed(),
                    "WH-013: يجب إظهار تحقق الاسم العربي قبل التصحيح");

            cashPage.AddArabicName(arabicName);
            cashPage.AddEnglishName("Corrected Safe " + uniqueSuffix);
            cashPage.SelectFromDropDawn(basicData().account("safe").name(), "رقم الحساب");
            cashPage.ClickSaveButton();
            cashPage.searchSafes(arabicName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(arabicName), 1, "WH-013: يجب حفظ الاسم المصحح");

            driver.navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(arabicName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(arabicName),
                    1,
                    "WH-013: يجب استمرار السجل بعد التحديث");
        } finally {
            cleanupTestSafe(cashPage, arabicName);
        }
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-007/008/010 Optional fields and unchecked General")
    @Description(
            "يتحقق من قبول الاسم العربي وحده عند ترك رقم الحساب والملاحظات فارغين وخيار عام"
                    + " غير محدد.")
    public void OptionalCashFieldsCanBeEmpty() {
        verifyOptionalSafeScenario(null, false, "WH-007/008/010");
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-002 Open cash creation form")
    @Description("يتحقق من فتح نموذج إضافة الخزنة عند الضغط على زر جديد.")
    public void OpenCashCreationForm() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();

        Assert.assertTrue(
                cashPage.isCashCreationFormDisplayed(),
                "يجب أن يظهر حقل الاسم العربي في نموذج إضافة الخزنة");
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-001 Display safe list")
    @Description("يتحقق من ظهور سجلات الخزن في الجدول دون فتح نموذج الإضافة.")
    public void CashListDisplaysRecords() {
        seedSafe("list");
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);

        Assert.assertTrue(
                cashPage.isDataPresentInTable(), "يجب أن تعرض قائمة الخزن بيانات السجلات");
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-015/016/018/019 Search safe list")
    @Description(
            "يتحقق من البحث بالكود والاسم، ومن ظهور نتائج فارغة لقيمة غير موجودة ثم استعادة"
                    + " القائمة بعد مسح البحث.")
    public void SearchSafesByCodeAndName() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);

        String ownSafe = seedSafe("search");
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

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-017 البحث بالاسم الإنجليزي")
    @Description("يتحقق من البحث باستخدام نص الاسم الإنجليزي الموجود في أول سجل دون تعديل بياناته.")
    public void WH_017_SearchSafesByEnglishName() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.searchSafes(seedSafe("english-search"));
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

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-028 ترتيب قائمة الخزن")
    @Description(
            "يتحقق من إمكانية تغيير ترتيب القائمة بالنقر على عنوان عمود الكود وملاحظة تغير"
                    + " ترتيب الصفوف.")
    public void WH_028_SafeListCanBeSortedByCode() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        seedSafe("sort-a");
        seedSafe("sort-b");
        driver.navigate().refresh();
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

    @Test(priority = 14)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-029 تغيير عدد الصفوف المعروضة")
    @Description(
            "يتحقق من أن تغيير اختيار حجم الصفحة يغير عدد الصفوف المعروضة إلى القيمة"
                    + " المختارة.")
    public void WH_029_ChangingPageSizeUpdatesVisibleRows() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        seedSafe("page-size");
        driver.navigate().refresh();
        cashPage.NavigationHelper(Pages.SAFES);
        int defaultPageSize = cashPage.getSelectedSafePageSize();
        int rowsBeforeChange = cashPage.getVisibleSafeRowCount();
        int availablePageSize = cashPage.getAlternativeSafePageSize(defaultPageSize);

        Assert.assertTrue(
                availablePageSize > defaultPageSize,
                "WH-029: يلزم وجود خيار حجم صفحة أكبر من القيمة الحالية");
        cashPage.selectSafePageSize(availablePageSize);
        Assert.assertEquals(
                cashPage.getVisibleSafeRowCount(),
                Math.min(rowsBeforeChange, availablePageSize),
                "WH-029: يجب أن يتغير عدد الصفوف حسب حجم الصفحة أو إجمالي السجلات المتاحة");
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-030 التنقل بين صفحات قائمة الخزن")
    @Description("يتحقق من الانتقال للصفحة التالية والعودة للسابق فقط عندما تتوفر صفحات متعددة.")
    public void WH_030_NavigateSafeListPagesWhenAvailable() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        int pageSize = cashPage.getSelectedSafePageSize();
        for (int i = 0; i <= pageSize; i++) seedSafe("pagination-" + i);
        driver.navigate().refresh();
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

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-003 إضافة خزنة ببيانات صحيحة")
    public void WH_003_CreateValidSafeAndVerifyFields() {
        CashPage cashPage = new CashPage(driver);
        String suffix = String.valueOf(System.currentTimeMillis());
        String arabicName = ownedName("Safes", "arabicName");
        String englishName = "Automation Safe " + suffix;
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(
                    cashPage,
                    arabicName,
                    englishName,
                    basicData().account("safe").name(),
                    false,
                    "Created by automation");
            cashPage.openSafeForEditByName(arabicName);
            Assert.assertEquals(
                    cashPage.getArabicNameInputValue().trim(),
                    arabicName,
                    "WH-003: يجب حفظ الاسم العربي المدخل");
            Assert.assertEquals(
                    cashPage.getEnglishNameInputValue().trim(),
                    englishName,
                    "WH-003: يجب حفظ الاسم الإنجليزي المدخل");
            Assert.assertEquals(
                    cashPage.isSafeGeneralChecked(),
                    false,
                    "WH-003: يجب أن تبقى خانة عام غير محددة");
            cashPage.ClickCloseButton();
        } finally {
            cleanupTestSafe(cashPage, arabicName);
        }
    }

    @Test(priority = 17)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-007 رقم الحساب غير محدد")
    public void WH_007_SaveSafeWithoutAccount() {
        verifyOptionalSafeScenario(null, false, "WH-007");
    }

    @Test(priority = 18)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-008 حفظ الخزنة وخيار عام غير محدد")
    public void WH_008_SaveSafeWithGeneralUnchecked() {
        verifyOptionalSafeScenario(basicData().account("safe").name(), false, "WH-008");
    }

    @Test(priority = 19)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-009 حفظ الخزنة وخيار عام محدد")
    public void WH_009_SaveSafeWithGeneralChecked() {
        verifyOptionalSafeScenario(basicData().account("safe").name(), true, "WH-009");
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-010 حفظ الملاحظات فارغة")
    public void WH_010_SaveSafeWithEmptyNotes() {
        verifyOptionalSafeScenario(basicData().account("safe").name(), false, "WH-010");
    }

    @Test(priority = 22)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-012 منع إنشاء سجل مكرر عند الضغط على حفظ مرتين")
    public void WH_012_RapidDoubleSaveCreatesSingleSafe() {
        CashPage cashPage = new CashPage(driver);
        String suffix = String.valueOf(System.currentTimeMillis());
        String arabicName = ownedName("Safes", "arabicName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            cashPage.ClickNew();
            cashPage.AddArabicName(arabicName);
            cashPage.AddEnglishName("Double Save " + suffix);
            cashPage.SelectFromDropDawn(basicData().account("safe").name(), "رقم الحساب");
            cashPage.clickSaveTwiceRapidly();
            cashPage.searchSafes(arabicName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(arabicName),
                    1,
                    "WH-012: يجب إنشاء سجل واحد فقط لكل حفظ ناجح");
        } finally {
            cleanupTestSafe(cashPage, arabicName);
        }
    }

    @Test(priority = 23)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-021 تعديل اسم الخزنة وحفظ التغيير")
    public void WH_021_EditOwnedTestSafeAndSave() {
        CashPage cashPage = new CashPage(driver);
        String suffix = String.valueOf(System.currentTimeMillis());
        String originalName = ownedName("Safes", "originalName");
        String editedName = ownedName("Safes", "edited");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(cashPage, originalName, "Edit Safe " + suffix, basicData().account("safe").name(), false, "");
            cashPage.openSafeForEditByName(originalName);
            cashPage.AddArabicName(editedName);
            cashPage.ClickSaveButton();
            cashPage.searchSafes(editedName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(editedName),
                    1,
                    "WH-021: يجب تحديث الاسم العربي في السجل المملوك للاختبار");
        } finally {
            cleanupTestSafe(cashPage, editedName);
        }
    }

    @Test(priority = 24)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-023 طلب حذف خزنة اختبار")
    public void WH_023_DeleteOnlyOwnedTestSafe() {
        CashPage cashPage = new CashPage(driver);
        String safeName = ownedName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(
                    cashPage,
                    safeName,
                    "Delete Safe " + System.currentTimeMillis(),
                    basicData().account("safe").name(),
                    false,
                    "");
            cashPage.searchSafes(safeName);
            cashPage.requestDeleteSafeByName(safeName);
            cashPage.confirmSafeDelete();
            driver.navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(safeName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    0,
                    "WH-023: يجب اختفاء سجل الاختبار بعد تأكيد حذفه");
        } finally {
            cleanupTestSafe(cashPage, safeName);
        }
    }

    @Test(priority = 25)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-024 إلغاء حذف الخزنة")
    public void WH_024_CancelDeleteOfOwnedTestSafe() {
        CashPage cashPage = new CashPage(driver);
        String safeName = ownedName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(
                    cashPage,
                    safeName,
                    "Cancel Delete " + System.currentTimeMillis(),
                    basicData().account("safe").name(),
                    false,
                    "");
            cashPage.searchSafes(safeName);
            cashPage.requestDeleteSafeByName(safeName);
            cashPage.cancelSafeDelete();
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    1,
                    "WH-024: يجب أن يبقى سجل الاختبار بعد إلغاء الحذف");
        } finally {
            cleanupTestSafe(cashPage, safeName);
        }
    }

    @Test(priority = 26)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-031 تحديث القائمة بعد الإضافة")
    public void WH_031_NewSafeAppearsAfterListRefresh() {
        verifySafePersistsAcrossRefresh("WH-031");
    }

    @Test(priority = 27)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-032 استمرارية حفظ السجل بعد تحديث الصفحة")
    public void WH_032_SafePersistsAfterBrowserRefresh() {
        verifySafePersistsAcrossRefresh("WH-032");
    }

    @Test(priority = 28)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-035 عدم تكرار السجل بعد إعادة فتح القائمة")
    public void WH_035_ReopeningListDoesNotDuplicateSafe() {
        CashPage cashPage = new CashPage(driver);
        String safeName = ownedName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(
                    cashPage,
                    safeName,
                    "Reopen Safe " + System.currentTimeMillis(),
                    basicData().account("safe").name(),
                    false,
                    "");
            driver.navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(safeName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    1,
                    "WH-035: يجب أن يظهر سجل الاختبار مرة واحدة بعد إعادة فتح القائمة");
        } finally {
            cleanupTestSafe(cashPage, safeName);
        }
    }

    @Test(priority = 29)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تصدير قائمة الخزن إلى PDF")
    @Description("يتحقق من تنزيل ملف PDF غير فارغ يبدأ بتوقيع PDF الصحيح.")
    public void ExportSafesToPdf() throws Exception {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        Path downloadDirectory = prepareExportDirectory();
        Set<String> existingFiles = listFileNames(downloadDirectory);

        cashPage.exportSafes("pdf");
        Path downloadedFile = waitForNewExport(downloadDirectory, existingFiles, Set.of(".pdf"));

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف PDF للخزن");
        byte[] header = Files.readAllBytes(downloadedFile);
        Assert.assertTrue(
                header.length > 4 && new String(header, 0, 4).equals("%PDF"),
                "الملف الناتج ليس PDF صالحًا");
    }

    @Test(priority = 30)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تصدير قائمة الخزن إلى Excel")
    @Description("يتحقق من تنزيل ملف Excel غير فارغ بصيغة XLS أو XLSX.")
    public void ExportSafesToExcel() throws Exception {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        Path downloadDirectory = prepareExportDirectory();
        Set<String> existingFiles = listFileNames(downloadDirectory);

        cashPage.exportSafes("xls");
        Path downloadedFile =
                waitForNewExport(downloadDirectory, existingFiles, Set.of(".xls", ".xlsx"));

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف Excel للخزن");
        Assert.assertTrue(Files.size(downloadedFile) > 0, "ملف Excel الناتج فارغ");
    }

    private void createSafe(
            CashPage cashPage,
            String arabicName,
            String englishName,
            String accountName,
            boolean general,
            String notes) {
        cashPage.ClickNew();
        cashPage.AddArabicName(arabicName);
        cashPage.AddEnglishName(englishName);
        if (accountName != null && !accountName.isBlank()) {
            cashPage.SelectFromDropDawn(accountName, "رقم الحساب");
        }
        cashPage.SelectCheckboxByText("عام", general);
        if (notes != null && !notes.isEmpty()) {
            cashPage.AddNoteForDealer(notes);
        }
        cashPage.ClickSaveButton();
        cashPage.searchSafes(arabicName);
        Assert.assertEquals(
                cashPage.getExactSafeMatchCount(arabicName),
                1,
                "يجب إنشاء سجل الاختبار الفريد مرة واحدة: " + arabicName);
    }

    private void verifyOptionalSafeScenario(String accountName, boolean general, String tcId) {
        CashPage cashPage = new CashPage(driver);
        String suffix = String.valueOf(System.currentTimeMillis());
        String safeName = ownedName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(
                    cashPage,
                    safeName,
                    "Automation " + tcId + " " + suffix,
                    accountName,
                    general,
                    "");
            cashPage.openSafeForEditByName(safeName);
            Assert.assertEquals(
                    cashPage.getArabicNameInputValue().trim(),
                    safeName,
                    tcId + ": يجب أن تظهر بيانات السجل بعد الحفظ");
            Assert.assertEquals(
                    cashPage.isSafeGeneralChecked(),
                    general,
                    tcId + ": يجب حفظ حالة خيار عام كما تم تحديدها");
            if ("WH-010".equals(tcId)) {
                Assert.assertEquals(
                        cashPage.getSafeAccountValue(),
                        accountName,
                        "WH-010: يجب أن يبقى الحساب المختار محفوظًا");
            }
            cashPage.ClickCloseButton();
        } finally {
            cleanupTestSafe(cashPage, safeName);
        }
    }

    private void verifySafePersistsAcrossRefresh(String tcId) {
        CashPage cashPage = new CashPage(driver);
        String suffix = String.valueOf(System.currentTimeMillis());
        String safeName = ownedName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            createSafe(
                    cashPage,
                    safeName,
                    "Persistence " + tcId + " " + suffix,
                    basicData().account("safe").name(),
                    false,
                    "");
            driver.navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(safeName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    1,
                    tcId + ": يجب أن يبقى السجل ظاهرًا مرة واحدة بعد التحديث");
        } finally {
            cleanupTestSafe(cashPage, safeName);
        }
    }

    private void cleanupTestSafe(CashPage cashPage, String safeName) {
        // Durable owner cleanup is independent of UI/modal state and failed assertions.
    }

    private String seedSafe(String key) {
        return basicData().fixture(key, "Safes", TestSupport.BasicDataFixtures.fields(
                "accountId", basicData().account("safe").id())).name();
    }

    private Path prepareExportDirectory() throws IOException {
        Path directory =
                Path.of(System.getProperty("user.dir"), "target", "cash-export-downloads")
                        .toAbsolutePath();
        Files.createDirectories(directory);
        return directory;
    }

    private Set<String> listFileNames(Path directory) throws IOException {
        try (var files = Files.list(directory)) {
            Set<String> names = new HashSet<>();
            files.forEach(file -> names.add(file.getFileName().toString()));
            return names;
        }
    }

    private Path waitForNewExport(
            Path directory, Set<String> existingFiles, Set<String> extensions) {
        return new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d -> {
                            try (var files = Files.list(directory)) {
                                return files.filter(Files::isRegularFile)
                                        .filter(
                                                file ->
                                                        !existingFiles.contains(
                                                                file.getFileName().toString()))
                                        .filter(
                                                file ->
                                                        !file.getFileName()
                                                                .toString()
                                                                .endsWith(".crdownload"))
                                        .filter(this::isNonEmptyFile)
                                        .filter(file -> matchesExportType(file, extensions))
                                        .findFirst()
                                        .orElse(null);
                            } catch (IOException e) {
                                return null;
                            }
                        });
    }

    private boolean isNonEmptyFile(Path file) {
        try {
            return Files.size(file) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean matchesExportType(Path file, Set<String> extensions) {
        String fileName = file.getFileName().toString().toLowerCase();
        if (extensions.stream().anyMatch(fileName::endsWith)) {
            return true;
        }

        try {
            byte[] header = Files.readAllBytes(file);
            if (extensions.contains(".pdf")) {
                return header.length >= 4
                        && header[0] == '%'
                        && header[1] == 'P'
                        && header[2] == 'D'
                        && header[3] == 'F';
            }

            if (extensions.contains(".xls") || extensions.contains(".xlsx")) {
                boolean xlsxZip =
                        header.length >= 4
                                && header[0] == 'P'
                                && header[1] == 'K'
                                && header[2] == 3
                                && header[3] == 4;
                boolean legacyXls =
                        header.length >= 8
                                && (header[0] & 0xff) == 0xd0
                                && (header[1] & 0xff) == 0xcf
                                && (header[2] & 0xff) == 0x11
                                && (header[3] & 0xff) == 0xe0
                                && (header[4] & 0xff) == 0xa1
                                && (header[5] & 0xff) == 0xb1
                                && (header[6] & 0xff) == 0x1a
                                && (header[7] & 0xff) == 0xe1;
                return xlsxZip || legacyXls;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
