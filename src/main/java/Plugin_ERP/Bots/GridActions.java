/* شرح الملف: قراءة صفوف الجداول والتعامل مع السجل المطلوب.
 */
package Plugin_ERP.Bots;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class GridActions extends BaseAction {
    private ElementActions utilites;
    private WaitActions waitActions;

    public GridActions(WebDriver driver, WebDriverWait wait, WaitManagement waitManagement) {
        super(driver, wait, waitManagement);
        this.waitActions = new WaitActions(driver, wait, waitManagement);
    }

    public void ClickElement(By locator) {
        clickWhenReady(locator);
    }

    public By getLastEditButton() {
        String pk = getLastRowPk();
        return By.xpath("//tr[@data-pk='" + pk + "']//button[@data-action-type='edit']");
    }

    public void deleteAllRowsSvg(By confirmDeleteButton) {

        int deletedCount = 0;

        while (driver.findElements(By.xpath("//*[name()='svg' and @data-action-type='delete']"))
                        .size()
                > 0) {

            waitActions.waitForOverlayToDisappear();

            deleteRow(
                    By.xpath("(//*[name()='svg' and @data-action-type='delete'])[1]"),
                    confirmDeleteButton);

            deletedCount++;

            System.out.println("🔁 Total deleted so far: " + deletedCount);
        }

        System.out.println("🏁 Done! Total rows deleted: " + deletedCount);
    }

    public By getLastDeleteButton() {
        String pk = getLastRowPk();
        return By.xpath("//tr[@data-pk='" + pk + "']//button[@data-action-type='delete']");
    }

    public String getLastRowPk() {
        return waitForRowPk(By.xpath("//tr[@role='row' and @aria-rowindex='1']"));
    }

    public String getPkByText(String text) {
        By row = By.xpath("//tr[@role='row'][.//*[contains(normalize-space(),'" + text + "')]]");
        return waitForRowPk(row);
    }

    public void clickEditByIndex(int index) {
        String xpath = "(//tr[@role='row']//button[@data-action-type='edit'])[" + index + "]";
        WebElement editBtn =
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", editBtn);
        wait.until(ExpectedConditions.elementToBeClickable(editBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
    }

    public void deleteRow(By deleteButton, By confirmDeleteButton) {

        String deletedPk = getLastRowPk();

        By deletedRow = By.xpath("//tr[@data-pk='" + deletedPk + "']");

        System.out.println("🗑️ Start deleting row with pk: " + deletedPk);

        ClickElement(deleteButton);
        ClickElement(confirmDeleteButton);

        long startTime = System.currentTimeMillis();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.numberOfElementsToBe(deletedRow, 0));

        long endTime = System.currentTimeMillis();

        System.out.println("✅ Row deleted successfully");
        System.out.println("⏱️ Delete wait time: " + ((endTime - startTime) / 1000.0) + " seconds");

        Assert.assertTrue(
                driver.findElements(deletedRow).isEmpty(),
                "❌ الصف لم يُحذف! data-pk: " + deletedPk);
    }

    public void deleteAllRows(By confirmDeleteButton) {
        int deletedCount = 0;

        System.out.println("🗑️ Start deleting all rows...");
        waitActions.waitForOverlayToDisappear();
        System.out.println(
                "Rows Count Before Loop = "
                        + driver.findElements(By.xpath("//tr[@role='row' and @aria-rowindex='1']"))
                                .size());
        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//tr[@role='row' and @aria-rowindex='1']")));
        while (hasRows()) {
            waitActions.waitForOverlayToDisappear();

            deleteRow(getLastDeleteButton(), confirmDeleteButton);

            deletedCount++;
            System.out.println("🔁 Total deleted so far: " + deletedCount);
        }

        System.out.println("🏁 Done! Total rows deleted: " + deletedCount);
    }

    private boolean hasRows() {
        try {
            List<WebElement> rows =
                    driver.findElements(By.xpath("//tr[@role='row' and @aria-rowindex='1']"));
            return !rows.isEmpty();
        } catch (Exception e) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "GridActions condition unavailable: " + e.getClass().getSimpleName());
            return false;
        }
    }

    private By tableRows = By.cssSelector("table tbody tr[role='row']");

    public void waitForTableDataToLoad(int minimumExpectedRows) {
        wait.until(
                driver -> {
                    int count = driver.findElements(tableRows).size();
                    return count > minimumExpectedRows;
                });
    }

    public List<WebElement> getTableRows() {
        return driver.findElements(tableRows);
    }

    public int getTableRowsCount() {
        return getTableRows().size();
    }

    public boolean isTableDataDisplayed() {
        return getTableRowsCount() > 0;
    }

    public void debugPrintAllRows() {
        waitActions.waitForOverlayToDisappear();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr[@role='row']"));
        System.out.println("=== عدد الصفوف: " + rows.size() + " ===");
        for (int i = 0; i < rows.size(); i++) {
            String text = rows.get(i).getText().replaceAll("\\n", " | ");
            System.out.println("Row " + (i + 1) + ": " + text);
        }
    }

    public void clickDeleteRows(int... rowNumbers) {
        for (int rowNumber : rowNumbers) {

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "document.querySelectorAll('svg.feather-trash')["
                                    + (rowNumber - 1)
                                    + "].dispatchEvent("
                                    + "new MouseEvent('click', {"
                                    + "bubbles:true,"
                                    + "cancelable:true,"
                                    + "view:window"
                                    + "}));");
        }
    }

    public void deleteRowByPk(String pk, By deleteButton, By confirmDeleteButton) {

        By deletedRow = By.xpath("//tr[@data-pk='" + pk + "']");

        System.out.println("🗑️ Start deleting row with pk: " + pk);

        ClickElement(deleteButton);
        ClickElement(confirmDeleteButton);

        long startTime = System.currentTimeMillis();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.numberOfElementsToBe(deletedRow, 0));

        long endTime = System.currentTimeMillis();

        System.out.println("✅ Row deleted successfully");
        System.out.println("⏱️ Delete wait time: " + ((endTime - startTime) / 1000.0) + " seconds");

        Assert.assertTrue(
                driver.findElements(deletedRow).isEmpty(), "❌ الصف لم يُحذف! data-pk: " + pk);
    }

    public boolean hasRowsByType(String rowTypeText) {
        return !driver.findElements(
                        By.xpath(
                                "//tr[@role='row']"
                                        + "[.//*[normalize-space(text())='"
                                        + rowTypeText
                                        + "']]"))
                .isEmpty();
    }

    public String getLastRowPkByType(String rowTypeText) {

        List<WebElement> rows =
                driver.findElements(
                        By.xpath(
                                "//tr[@role='row']"
                                        + "[.//*[normalize-space(text())='"
                                        + rowTypeText
                                        + "']]"));

        WebElement lastRow = rows.get(rows.size() - 1);

        return lastRow.getAttribute("data-pk");
    }

    public By getLastDeleteButtonByType(String rowTypeText) {

        List<WebElement> rows =
                driver.findElements(
                        By.xpath(
                                "//tr[@role='row']"
                                        + "[.//*[normalize-space(text())='"
                                        + rowTypeText
                                        + "']]"));

        int lastIndex = rows.size(); // XPath indexing 1-based

        return By.xpath(
                "(//tr[@role='row']"
                        + "[.//*[normalize-space(text())='"
                        + rowTypeText
                        + "']])["
                        + lastIndex
                        + "]"
                        + "//button[@data-action-type='delete']");
    }

    public void deleteAllRowsByType(String rowTypeText, By confirmDeleteButton) {
        int deletedCount = 0;

        System.out.println("🗑️ Start deleting rows of type [" + rowTypeText + "] only...");
        waitActions.waitForOverlayToDisappear();

        System.out.println(
                "Rows Count Before Loop = "
                        + driver.findElements(
                                        By.xpath(
                                                "//tr[@role='row']"
                                                        + "[.//*[normalize-space(text())='"
                                                        + rowTypeText
                                                        + "']]"))
                                .size());

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//tr[@role='row' and @aria-rowindex='1']")));

        while (hasRowsByType(rowTypeText)) {
            waitActions.waitForOverlayToDisappear();

            String pk = getLastRowPkByType(rowTypeText);
            By deleteBtnBy = getLastDeleteButtonByType(rowTypeText);

            deleteRowByPk(pk, deleteBtnBy, confirmDeleteButton);

            deletedCount++;
            System.out.println("🔁 Total deleted so far: " + deletedCount);
        }

        System.out.println("🏁 Done! Total rows deleted: " + deletedCount);
    }
}
