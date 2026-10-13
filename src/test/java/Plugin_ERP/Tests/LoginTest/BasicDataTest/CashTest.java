package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
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
import java.util.HashSet;
import java.util.Set;

public class CashTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("إضافة خزنة ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة خزنة جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار رقم الحساب وتفعيل خانة (عام)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidCashTest() {
        System.out.println("Cash test started");
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("55");
        cashPage.AddEnglishName("55");
        cashPage.AddNoteForDealer("تست");
        cashPage.SelectFromDropDawn("تسويق جوجل","رقم الحساب");
        cashPage.SelectCheckboxByText("عام",true);
        cashPage.ClickSaveButton();
        cashPage.AssertReceiptAdded("55");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تعديل خزنة ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل خزنة موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditCash() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.Edit();
        cashPage.AddArabicName("معدل");
        cashPage.ClickSaveButton();
        cashPage.AssertReceiptAdded("معدل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("حذف خزنة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف خزنة موجودة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteCash() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة خزنة بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidCashTest() {
        System.out.println("Invalid Cash test started");
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("");
        Assert.assertTrue(cashPage.IsArabicNameRequiredErrorDisplayed());
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("منع الحفظ عند ترك جميع الحقول فارغة")
    @Description("يتحقق من أن حفظ نموذج خزنة فارغ يعرض تحقق الاسم العربي المطلوب ولا ينشئ سجلًا.")
    public void EmptyCashFormIsRejected() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.ClickSaveButton();

        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "يجب أن يظهر تحقق الاسم العربي عند حفظ نموذج فارغ"
        );
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("رفض الاسم العربي المكوّن من مسافات")
    @Description("يتحقق من معاملة المسافات فقط في الاسم العربي كقيمة فارغة.")
    public void CashArabicNameWhitespaceIsRejected() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("   ");
        cashPage.ClickSaveButton();

        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "يجب رفض الاسم العربي المكوّن من مسافات فقط"
        );
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تصحيح الاسم بعد ظهور رسالة الخطأ")
    @Description("يتحقق من إمكانية تصحيح الاسم العربي بعد إظهار رسالة الإلزامية ثم حفظ الخزنة.")
    public void CorrectArabicNameAfterValidationError() {
        CashPage cashPage = new CashPage(driver);
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String arabicName = "خزنة تصحيح " + uniqueSuffix;

        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.ClickSaveButton();
        Assert.assertTrue(cashPage.IsArabicNameRequiredErrorDisplayed());

        cashPage.AddArabicName(arabicName);
        cashPage.AddEnglishName("Corrected Safe " + uniqueSuffix);
        cashPage.SelectFromDropDawn("تسويق جوجل", "رقم الحساب");
        cashPage.ClickSaveButton();
        cashPage.AssertReceiptAdded(arabicName);

        driver.navigate().refresh();
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.AssertReceiptAdded(arabicName);
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("الحفظ دون رقم حساب أو ملاحظات مع خيار عام غير محدد")
    @Description("يتحقق من قبول الاسم العربي وحده عند ترك رقم الحساب والملاحظات فارغين وخيار عام غير محدد.")
    public void OptionalCashFieldsCanBeEmpty() {
        CashPage cashPage = new CashPage(driver);
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String arabicName = "خزنة اختيارية " + uniqueSuffix;

        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName(arabicName);
        cashPage.SelectCheckboxByText("عام", false);
        cashPage.ClickSaveButton();
        cashPage.AssertReceiptAdded(arabicName);
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("فتح نموذج إضافة خزنة")
    @Description("يتحقق من فتح نموذج إضافة الخزنة عند الضغط على زر جديد.")
    public void OpenCashCreationForm() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();

        Assert.assertTrue(
                cashPage.isCashCreationFormDisplayed(),
                "يجب أن يظهر حقل الاسم العربي في نموذج إضافة الخزنة"
        );
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("عرض قائمة الخزن")
    @Description("يتحقق من ظهور سجلات الخزن في الجدول دون فتح نموذج الإضافة.")
    public void CashListDisplaysRecords() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);

        Assert.assertTrue(
                cashPage.isDataPresentInTable(),
                "يجب أن تعرض قائمة الخزن بيانات السجلات"
        );
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("البحث بالكود والاسم ومسح البحث")
    @Description("يتحقق من البحث بالكود والاسم، ومن ظهور نتائج فارغة لقيمة غير موجودة ثم استعادة القائمة بعد مسح البحث.")
    public void SearchSafesByCodeAndName() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);

        cashPage.searchSafes("3");
        Assert.assertTrue(cashPage.isSafeVisibleInSearchResults("3"), "يجب إظهار الخزنة ذات الكود 3");

        cashPage.searchSafes("الخزنة الرئيسية");
        Assert.assertTrue(
                cashPage.isSafeVisibleInSearchResults("الخزنة الرئيسية"),
                "يجب إظهار الخزنة المطابقة للاسم العربي"
        );

        cashPage.searchSafes("XYZ999");
        Assert.assertFalse(
                cashPage.isSafeVisibleInSearchResults("XYZ999"),
                "يجب ألا تظهر سجلات عند البحث عن قيمة غير موجودة"
        );

        cashPage.searchSafes("");
        Assert.assertTrue(
                cashPage.getVisibleSafeRowCount() > 0,
                "يجب أن تعود سجلات القائمة بعد مسح البحث"
        );
    }

    @Test(priority = 12)
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
                "الملف الناتج ليس PDF صالحًا"
        );
    }

    @Test(priority = 13)
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
        Path downloadedFile = waitForNewExport(downloadDirectory, existingFiles, Set.of(".xls", ".xlsx"));

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف Excel للخزن");
        Assert.assertTrue(
                Files.size(downloadedFile) > 0,
                "ملف Excel الناتج فارغ"
        );
    }

    private Path prepareExportDirectory() throws IOException {
        Path directory = Path.of(
                System.getProperty("user.dir"),
                "target",
                "cash-export-downloads"
        ).toAbsolutePath();
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

    private Path waitForNewExport(Path directory, Set<String> existingFiles, Set<String> extensions) {
        return new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            try (var files = Files.list(directory)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(file -> !existingFiles.contains(file.getFileName().toString()))
                        .filter(file -> !file.getFileName().toString().endsWith(".crdownload"))
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
                boolean xlsxZip = header.length >= 4
                        && header[0] == 'P'
                        && header[1] == 'K'
                        && header[2] == 3
                        && header[3] == 4;
                boolean legacyXls = header.length >= 8
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
