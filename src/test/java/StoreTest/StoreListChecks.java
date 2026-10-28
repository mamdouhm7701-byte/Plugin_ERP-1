/* شرح الملف: مراجعة قائمة الحركات المخزنية والبحث ومسحه وتصديرها إلى PDF وExcel.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

final class StoreListChecks {
    private final StoreScenarioTest owner;

    StoreListChecks(StoreScenarioTest owner) {
        this.owner = owner;
    }

    void SM_ListStructure() {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        owner.find(record);
        Assert.assertTrue(owner.page.smHeaders().size() >= 3);
    }

    void SM_ExportPdf() {
        owner.export("pdf");
    }

    void SM_ExportExcel() {
        owner.export("excel");
    }

    void export(String format) {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        owner.find(record);
        StoreExportAssertions.assertExportDownloaded(
                owner.caseDriver(), () -> owner.page.smExport(format), format);
    }

    void SM_SearchAndReset() {
        var record = owner.create(owner.baselineMode(), false, owner.defaultParty());
        owner.find(record);
        if (owner.kind() == StoreMovementKind.DAMAGE) {
            Assert.assertFalse(
                    owner.page.smHasListSearch(),
                    "Damage-list contract changed; add explicit search coverage");
            owner.find(record);
            Assert.assertTrue(
                    owner.page.smHasDocument(record.name()),
                    "Owned damage record must survive reopening its list");
            Allure.addAttachment(
                    "Damage list search capability",
                    "text/plain",
                    "No search control is exposed; verified list reopening, not text-search coverage.");
            return;
        }
        Assert.assertTrue(owner.page.smHasListSearch());
        owner.page.smSearch("999999999999");
        new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(25))
                .until(d -> !owner.page.smHasDocument(record.name()));
        owner.page.smSearch(owner.data.read(record).get("code").toString());
        new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(25))
                .until(d -> owner.page.smHasDocument(record.name()));
    }
}
