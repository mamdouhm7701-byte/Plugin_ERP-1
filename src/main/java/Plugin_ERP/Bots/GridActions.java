package Plugin_ERP.Bots;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class GridActions extends BaseAction {
    private ElmentAction utilites;
    private WaitActions waitActions;

    public GridActions(WebDriver driver, WebDriverWait wait, WaitMangment waitMangment) {
        super(driver, wait, waitMangment);
        this.waitActions = new WaitActions(driver, wait, waitMangment);
    }

    public void ClickElement(By locator) {

        int attempts = 0;

        while (attempts < 3) {

            try {

                WebElement element =
                        waitMangment
                                .fluentWait()
                                .until(
                                        driver -> {
                                            WebElement el = driver.findElement(locator);

                                            return (el.isDisplayed() && el.isEnabled()) ? el : null;
                                        });

                scrollIntoView(element);

                try {

                    element.click();

                } catch (Exception e) {

                    jsClick(element);
                }

                return;

            } catch (StaleElementReferenceException e) {

                attempts++;

            } catch (Exception e) {

                try {

                    WebElement fallback = driver.findElement(locator);

                    scrollIntoView(fallback);

                    jsClick(fallback);

                    return;

                } catch (Exception ignored) {
                }

                attempts++;
            }
        }

        throw new RuntimeException("❌ Click failed after retries: " + locator);
    }

    public By getLastEditButton() {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//tr[@role='row' and @aria-rowindex='1']")));

                String pk =
                        driver.findElement(By.xpath("//tr[@role='row' and @aria-rowindex='1']"))
                                .getAttribute("data-pk");

                if (pk == null || pk.isEmpty()) {
                    attempts++;
                    Thread.sleep(500);
                    continue;
                }

                return By.xpath("//tr[@data-pk='" + pk + "']//button[@data-action-type='edit']");
            } catch (Exception e) {
                attempts++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new RuntimeException("❌ ما لقاش أول صف للتعديل بعد 3 محاولات");
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
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//tr[@role='row' and @aria-rowindex='1']")));

                String pk =
                        driver.findElement(By.xpath("//tr[@role='row' and @aria-rowindex='1']"))
                                .getAttribute("data-pk");

                if (pk == null || pk.isEmpty()) {
                    attempts++;
                    Thread.sleep(500);
                    continue;
                }

                return By.xpath("//tr[@data-pk='" + pk + "']//button[@data-action-type='delete']");
            } catch (Exception e) {
                attempts++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new RuntimeException("❌ ما لقاش أول صف للحذف بعد 3 محاولات");
    }

    public String getLastRowPk() {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//tr[@role='row' and @aria-rowindex='1']")));

                String pk =
                        driver.findElement(By.xpath("//tr[@role='row' and @aria-rowindex='1']"))
                                .getAttribute("data-pk");

                if (pk != null && !pk.isEmpty()) return pk;

                attempts++;
                Thread.sleep(500);
            } catch (Exception e) {
                attempts++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new RuntimeException("❌ ما لقاش أول صف بعد 3 محاولات");
    }

    public String getPkByText(String text) {

        int attempts = 0;

        while (attempts < 3) {

            try {

                By rowLocator =
                        By.xpath(
                                "//tr[@role='row'][.//*[contains(normalize-space(),'"
                                        + text
                                        + "')]]");

                WebElement row =
                        wait.until(ExpectedConditions.presenceOfElementLocated(rowLocator));

                System.out.println("========== ROW FOUND ==========");
                System.out.println("ROW TEXT = " + row.getText());

                String pk = row.getAttribute("data-pk");

                System.out.println("DATA PK = " + pk);

                System.out.println("OUTER HTML = " + row.getAttribute("outerHTML"));

                if (pk != null && !pk.trim().isEmpty() && !"null".equals(pk)) {

                    System.out.println("✅ PK FOUND = " + pk);

                    return pk.trim();
                }

                System.out.println("⚠️ PK IS NULL OR EMPTY - RETRYING...");

                attempts++;

                Thread.sleep(1000);

            } catch (Exception e) {

                System.out.println("❌ Attempt Failed : " + (attempts + 1));
                System.out.println("ERROR = " + e.getMessage());

                attempts++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        throw new RuntimeException("❌ لا يوجد الاسم الذى تبحث عنه " + text);
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
            return false;
        }
    }

    private By tableRows = By.cssSelector("table tbody tr[role='row']");
    private By tableLocator = By.cssSelector("table.b-table");

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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
