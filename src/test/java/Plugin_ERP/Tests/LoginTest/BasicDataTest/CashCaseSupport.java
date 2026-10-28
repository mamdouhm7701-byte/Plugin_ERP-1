/* شرح الملف: خطوات تجهيز وتحقق مشتركة لحالات الخزائن، على نفس جلسة وبيانات الحالة الأصلية.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.*;

abstract class CashCaseSupport extends BasicDataScenarioTest {
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

    final String prepareSafeAccount() {
        String name = basicData().account("safe").name();
        // The safe form loads account choices when its component mounts.
        driver.navigate().refresh();
        basicData().installCapture();
        return name;
    }

    final void createSafe(
            CashPage cashPage,
            String arabicName,
            String englishName,
            String accountName,
            boolean general,
            String notes) {
        // API fixtures may be created after this route cached its account options.
        driver.navigate().refresh();
        cashPage.NavigationHelper(Pages.SAFES);
        basicData().installCapture();
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

    final void verifyOptionalSafeScenario(String accountName, boolean general, String tcId) {
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

    final void verifySafePersistsAcrossRefresh(String tcId) {
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

    final void cleanupTestSafe(CashPage cashPage, String safeName) {
        // Durable owner cleanup is independent of UI/modal state and failed assertions.
    }

    final String seedSafe(String key) {
        String name =
                basicData()
                        .fixture(
                                key,
                                "Safes",
                                TestSupport.BasicDataFixtures.fields(
                                        "accountId", basicData().account("safe").id()))
                        .name();
        driver.navigate().refresh();
        return name;
    }

    final Path prepareExportDirectory() throws IOException {
        Path root =
                Path.of(System.getProperty("user.dir"), "target", "cash-export-downloads")
                        .toAbsolutePath();
        Files.createDirectories(root);
        Path directory = Files.createTempDirectory(root, "case-");
        if (driver instanceof org.openqa.selenium.chromium.HasCdp cdp) {
            cdp.executeCdpCommand(
                    "Page.setDownloadBehavior",
                    java.util.Map.of("behavior", "allow", "downloadPath", directory.toString()));
        }
        return directory;
    }

    final Set<String> listFileNames(Path directory) throws IOException {
        try (var files = Files.list(directory)) {
            Set<String> names = new HashSet<>();
            files.forEach(file -> names.add(file.getFileName().toString()));
            return names;
        }
    }

    final Path waitForNewExport(Path directory, Set<String> existingFiles, Set<String> extensions) {
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

    final boolean isNonEmptyFile(Path file) {
        try {
            return Files.size(file) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    final boolean matchesExportType(Path file, Set<String> extensions) {
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
