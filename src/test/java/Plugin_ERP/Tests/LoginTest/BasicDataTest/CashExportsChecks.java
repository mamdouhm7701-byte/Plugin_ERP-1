/* شرح الملف: تنفيذ حالات الخزائن الخاصة بـتصدير القوائم والتحقق من الملف.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import io.qameta.allure.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.*;

final class CashExportsChecks {
    private final CashTest owner;

    CashExportsChecks(CashTest owner) {
        this.owner = owner;
    }

    void ExportSafesToPdf() throws Exception {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        Path downloadDirectory = owner.prepareExportDirectory();
        Set<String> existingFiles = owner.listFileNames(downloadDirectory);

        cashPage.exportSafes("pdf");
        Path downloadedFile =
                owner.waitForNewExport(downloadDirectory, existingFiles, Set.of(".pdf"));

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف PDF للخزن");
        byte[] header = Files.readAllBytes(downloadedFile);
        Assert.assertTrue(
                header.length > 4 && new String(header, 0, 4).equals("%PDF"),
                "الملف الناتج ليس PDF صالحًا");
    }

    void ExportSafesToExcel() throws Exception {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        Path downloadDirectory = owner.prepareExportDirectory();
        Set<String> existingFiles = owner.listFileNames(downloadDirectory);

        cashPage.exportSafes("xls");
        Path downloadedFile =
                owner.waitForNewExport(downloadDirectory, existingFiles, Set.of(".xls", ".xlsx"));

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف Excel للخزن");
        Assert.assertTrue(Files.size(downloadedFile) > 0, "ملف Excel الناتج فارغ");
    }
}
