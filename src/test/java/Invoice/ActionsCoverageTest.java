package Invoice;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;
/** Tests the copy/lookup action on an owned invoice; excludes invoice aggregation explicitly. */
public final class ActionsCoverageTest extends SalesScenarioTest {
    @Test @Story("نسخ فاتورة مملوكة إلى نموذج غير محفوظ دون تعديل المصدر أو المخزون")
    public void copiesOwnedInvoiceIntoUnsavedFormWithoutChangingSource() {
        prepare("Credit");fill("picker");var invoice=save(false);var stored=data.read(invoice);
        var screen=new ScreenPage(driver);screen.open("actions","الاجراءات");
        screen.choose("نوع الحركة","الفواتير");screen.choose("الحركه","مبيعات");
        screen.type(By.id("code"),stored.get("code").toString());
        screen.click(By.xpath("//button[@type='submit' and .//*[local-name()='svg' and contains(@class,'feather-search')]]"));
        String card="//div[contains(concat(' ',normalize-space(@class),' '),' card ') and contains(@class,'border-primary')][.//span[normalize-space(.)="
                +ScreenPage.literal(stored.get("code").toString())+"]]";
        screen.visibleElement(By.xpath(card));
        screen.click(By.xpath(card+"//button[.//*[local-name()='svg' and contains(@class,'feather-copy')]]"));
        new org.openqa.selenium.support.ui.WebDriverWait(driver,java.time.Duration.ofSeconds(30)).until(d->
                d.getCurrentUrl().contains("sourceTransactionId="+invoice.id())&&d.getCurrentUrl().contains("action=copy"));
        page.slFormReady();
        new org.openqa.selenium.support.ui.WebDriverWait(driver,java.time.Duration.ofSeconds(30)).until(d->
                customer.id().equals(page.slDocument().get("dealerId"))&&!((java.util.List<?>)page.slDocument().get("lines")).isEmpty());
        var copied=page.slDocument();var lines=(java.util.List<?>)copied.get("lines");Assert.assertEquals(lines.size(),1);
        var line=(java.util.Map<?,?>)lines.getFirst();Assert.assertEquals(line.get("itemId"),item.id());sameNumber(line.get("quantity"),"3");
        Assert.assertTrue(copied.get("id")==null||copied.get("id").toString().equals("0"),"Copy must remain a new document");
        page.slOpen("invoice-list");
        Assert.assertEquals(data.read(invoice).get("id"),invoice.id());Assert.assertEquals(data.read(invoice).get("notes"),invoice.name());assertStock("3");
    }
}
