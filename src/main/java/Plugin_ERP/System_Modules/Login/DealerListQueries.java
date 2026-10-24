package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Read-only dealer list queries and exact-name matching. */
final class DealerListQueries {
    private final AbstractForDealer owner;
    private final WebDriver driver;
    DealerListQueries(AbstractForDealer owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }
    private static final By DEALER_TABLE_HEADERS = By.cssSelector("table thead th");
    private static final By DEALER_TABLE_ROWS = By.cssSelector("table tbody tr[role='row']");

    boolean isDealerListStructureDisplayed() {
        boolean hasHeaders = driver.findElements(DEALER_TABLE_HEADERS).stream()
                .anyMatch(this::isElementDisplayed);
        boolean hasRowActions = driver.findElements(DEALER_TABLE_ROWS).stream()
                .filter(this::isElementDisplayed)
                .anyMatch(row -> row.findElements(By.cssSelector(
                        "button[data-action-type='edit'],button[data-action-type='delete']")).stream()
                        .anyMatch(this::isElementDisplayed));
        return hasHeaders && hasRowActions;
    }

    int getVisibleDealerRowCount() {
        return (int) driver.findElements(DEALER_TABLE_ROWS).stream()
                .filter(this::isElementDisplayed)
                .count();
    }

    String getFirstDealerArabicName() {
        List<WebElement> headers = new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .until(d -> {
                    List<WebElement> loadedHeaders = d.findElements(DEALER_TABLE_HEADERS);
                    return loadedHeaders.stream().anyMatch(header ->
                            isElementDisplayed(header) && header.getText().contains("اسم"))
                            ? loadedHeaders : null;
                });
        int nameColumn = -1;
        for (int index = 0; index < headers.size(); index++) {
            String header = headers.get(index).getText().trim();
            if (header.contains("اسم")) {
                nameColumn = index;
                break;
            }
        }
        if (nameColumn < 0) {
            throw new AssertionError("لم يتم العثور على عمود الاسم العربي في قائمة المتعاملين");
        }
        WebElement firstRow = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(DEALER_TABLE_ROWS).stream()
                        .filter(this::isElementDisplayed)
                        .filter(row -> row.findElements(By.cssSelector(
                                "button[data-action-type='edit']")).stream()
                                .anyMatch(this::isElementDisplayed))
                        .findFirst().orElse(null));
        List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));
        if (nameColumn >= cells.size()) {
            throw new AssertionError("عمود الاسم العربي خارج نطاق خلايا الصف الأول");
        }
        return cells.get(nameColumn).getText().trim();
    }

    boolean isDealerNameVisibleInList(String arabicName) {
        return owner.getExactDealerNameMatchCount(arabicName) > 0;
    }

    int getExactDealerNameMatchCount(String arabicName) {
        int matches = 0;
        for (WebElement row : driver.findElements(DEALER_TABLE_ROWS)) {
            try {
                if (isElementDisplayed(row)
                        && row.findElements(By.cssSelector("td")).stream()
                                .anyMatch(cell -> cell.getText().trim().equals(arabicName))) {
                    matches++;
                }
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                // The list can rerender while a create/edit/delete request is completing.
            }
        }
        return matches;
    }

    boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }
}
