package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbstractForStore extends AbstractForDealer{
    public AbstractForStore(WebDriver driver) {
        super(driver);
    }


    private By BarcodeRadioBtn = By.cssSelector("input[type='radio'][value='barcode']");
    private By NameRadioBtn = By.cssSelector("input[type='radio'][value='name']");
    private By SearchInput = By.cssSelector("input[placeholder='بحث ....']");

    private By getSuggestionItem(String name) {
        return By.xpath("//span[@class='my-suggestion-item' and contains(text(), '" + name + "')]");
    }
    private By AddItemsBtn = By.xpath("//button[contains(text(), 'إضافة أصناف')]");


    private By getPlusBtnByRowNumber(int rowNumber) {
        return By.xpath("(//tbody//tr[@role='row' and @data-pk])[" + rowNumber + "]//button[.//svg[contains(@class,'feather-plus')]]");
    }

    private By getMinusBtnByRowNumber(int rowNumber) {
        return By.xpath("(//tbody//tr[@role='row' and @data-pk])[" + rowNumber + "]//button[.//svg[contains(@class,'feather-minus')]]");
    }

    private By SaveForBageProductBtn = By.xpath("//div[contains(@class,'modal-content')]//button[normalize-space()='حفظ']");

    private final By SaveAndPrintButton = By.cssSelector("button[data-action-type='saveAndPrint']");
    private final By EditableRows = By.xpath("//tbody//tr[@role='row' and .//input[not(@type='hidden')]]");

    private By AssertExsistItemTaype  = By.xpath("//small[contains(text(),'نوع البند مطلوب')]");

    private By AssertExsistCustomre  = By.xpath("//small[contains(text(),'العملاء مطلوب')]");
    private By AssertExpenditureCategories    = By.xpath("//small[contains(text(),'جهات الصرف مطلوب')]");
    private By AssertAgnt    = By.xpath("//small[contains(text(),'المندوبين مطلوب')]");
    private By AssertSupplier    = By.xpath("//small[contains(text(),'الموردين مطلوب')]");
    private By AssertStore    = By.xpath("//small[contains(text(),'المخزن مطلوب')]");




    //Methods
    @Step("التأكيد على ان نوع البند مطلوب")
    public Boolean AssertExsistItemTaype(){
        return utilites.isDisplayed(AssertExsistItemTaype);
    }
    @Step("التأكيد على ان العملاء مطلوب")
    public Boolean AssertExsistCustomre(){
        return utilites.isDisplayed(AssertExsistCustomre);
    }

    @Step("التأكيد على ان جهات الصرف مطلوب")
    public Boolean AssertExpenditureCategories(){
        return utilites.isDisplayed(AssertExpenditureCategories);
    }
    @Step("التأكيد على ان المندوبي مطلوب")
    public Boolean AssertAgnt(){
        return utilites.isDisplayed(AssertAgnt);
    }
@Step("التاكيد ان المخزن مطلوب")
public Boolean AssertSotreRequired(){
        return utilites.isDisplayed(AssertStore);
}

@Step("التأكيد ان الموردين مطلوب")
public Boolean AssertSupplier(){
    return utilites.isDisplayed(AssertSupplier);
}
    @Step("الضغط على ايقونه الباركود")
    public void ClickOnBarcodeRadioBtn() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(BarcodeRadioBtn);
    }

    @Step("الضغط على ايقونه الاسم")
    public void ClickOnNameRadioBtn() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(NameRadioBtn);
    }
     @Step("البحث عن : {searchText}")
     public void Search(String searchText) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(SearchInput,searchText);
     }

    @Step("الضغط على حفظ من قائمه الاصناف")
    public void ClickSaveForProductBtn() {
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(SaveForBageProductBtn);
        utilites.waits().waitForOverlayToDisappear();

    }

    @Step("الضغط على زرار اضافه الاصناف")
    public void ClickAddItemsBtn() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(AddItemsBtn);
        utilites.waits().waitForOverlayToDisappear();

    }


    @Step("البحث عن منتج واختياره من القائمة البحث بالاسم")
    public void SearchAndSelectProduct(String productName) {
        utilites.waits().waitForOverlayToDisappear();

        int maxRetries = 3;
        boolean found = false;

        for (int attempt = 1; attempt <= maxRetries && !found; attempt++) {
            try {
                // امسح الحقل الأول قبل أي محاولة (حتى الأولى، احتياطًا)
                SearchInputElement().clear();

                utilites.TaypeTextB(SearchInput, productName);
                utilites.ClickElement(getSuggestionItem(productName));
                utilites.waits().waitForOverlayToDisappear();

                found = waitForProductToAppearInTable(productName, 5);

            } catch (Exception e) {
                System.out.println("⚠️ المحاولة " + attempt + " فشلت بسبب: " + e.getMessage());
                found = false;
            }

            if (!found) {
                System.out.println("🔁 إعادة المحاولة رقم " + (attempt + 1) + " للصنف: " + productName);
            }
        }

        if (!found) {
            throw new RuntimeException("❌ فشل إضافة الصنف بعد " + maxRetries + " محاولات: " + productName);
        }
    }

    private WebElement SearchInputElement() {
        return driver.findElement(SearchInput);
    }

    private boolean waitForProductToAppearInTable(String productName, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(d -> {
                List<WebElement> matches = d.findElements(
                        By.xpath("//tbody//tr[@role='row']//td[contains(text(), '" + productName + "')]")
                );
                return !matches.isEmpty();
            });
            System.out.println("✅ تم التأكد من إضافة الصنف: " + productName);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Step("الضغط على زر الزياده لأول عدد أصناف معين")
    public void ClickPlusButtonByRow(int numberOfRows, int timesPerRow) {
     utilites.ClickPlusButtonByRow(numberOfRows, timesPerRow);
    }

    @Step("الضغط على زر النقصان لأول عدد أصناف معين")
    public void ClickMinusButtonByRow(int numberOfRows, int timesPerRow) {
        utilites.ClickMinusButtonByRow(numberOfRows, timesPerRow);

    }

    public void debugPrintAllRows() {
        utilites.grid().debugPrintAllRows();
    }


    public void WaitEditeForBills() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.waits().waitForExchangeEditPageForEditBills(SaveAndPrintButton,EditableRows);
    }


    @Step("حذف الصفوف: {rowNumbers}")
    public void clickDeleteRows(int... rowNumbers){
        utilites.waits().waitForOverlayToDisappear();
        WaitEditeForBills();
        utilites.grid().clickDeleteRows(rowNumbers);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم حذف اى صف");


    }

    @Step("التعديل على اخر صف موجود")
    public void EditForBills() {
        utilites.waits().waitForOverlayToDisappear();
        super.Edit();
        WaitEditeForBills();
        LogUtils.info("تم الضغط على تعديل اخر صف موجود");
    }

    @Step("التعديل على اخر صف موجود")
    public void EditForDamage(){
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(
                utilites.getLastEditButtonForAdjustDamage()
        );
        WaitEditeForBills();
        LogUtils.info("تم الضغط على تعديل اخر صف موجود");
    }


    public void DeleteForDamage(){
        utilites.grid().deleteAllRowsSvg(Clickyestodelet);
        LogUtils.info("تم حذف جميع السجلات في الجريد");
    }



    @Step("اختيار الكميات : {rowNumber} {quantity}")
    public void SetQuantityByRow(int rowNumber, int quantity) {
        WebElement quantityInput = utilites.waits().waitForQuantityInputByRow(rowNumber);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                quantityInput
        );

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        quantityInput.click();
        quantityInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        quantityInput.sendKeys(Keys.DELETE);
        quantityInput.sendKeys(String.valueOf(quantity));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                quantityInput
        );

        // اخرج من الحقل عشان تتفعل أي معالجة تلقائية للرقم (blur)
        quantityInput.sendKeys(Keys.TAB);

        // استنى وتأكد إن القيمة فعلاً اتحدثت في الـ DOM (بغض النظر عن شكل الفورمات)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> {
            try {
                WebElement input = utilites.waits().waitForQuantityInputByRow(rowNumber);
                String actualValue = input.getAttribute("value");
                return actualValue != null && normalizeQuantity(actualValue).equals(String.valueOf(quantity));
            } catch (Exception e) {
                return false;
            }
        });

        LogUtils.info("الكمية فى صف " + rowNumber + " التغير الى " + quantity);
    }

    private String normalizeQuantity(String value) {
        if (value == null) return "";
        String v = value.trim();
        if (v.contains(".")) {
            String[] parts = v.split("\\.");
            if (parts[1].matches("0+")) {
                return parts[0];
            }
        }
        return v;
    }

    @Step("اضافة الكمية فى سطر : {values}")
    public void SetQuantitiesForRows(int... values) {
        utilites.waits().waitForOverlayToDisappear();
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Values must be pairs: row number, quantity");
        }

        for (int i = 0; i < values.length; i += 2) {
            int rowNumber = values[i];
            int quantity = values[i + 1];
            SetQuantityByRow(rowNumber, quantity);
        }
    }


    public Map<String, String> GetNamesAndQuantities() {
        Map<String, String> data = new LinkedHashMap<>();

        String quantityColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الكمية']]")
        ).getAttribute("aria-colindex");

        List<WebElement> rows = driver.findElements(
                By.xpath("//tbody//tr[@role='row']")
        );

        for (WebElement row : rows) {
            String name = row.findElement(
                    By.xpath(".//td[@aria-colindex='3']")
            ).getText().trim();

            String quantity = row.findElement(
                    By.xpath(".//td[@aria-colindex='" + quantityColIndex + "']//input[not(@type='hidden')]")
            ).getAttribute("value").trim();

            System.out.printf(
                    "Item=%s , Quantity=%s%n",
                    name,
                    quantity
            );

            data.put(name, quantity);
        }

        return data;
    }

    @Step("مقارنة الأسماء والكميات مع التقرير")
    public void CompareNamesAndQuantitiesWithReport(Map<String, String> expectedData) {

        for (Map.Entry<String, String> entry : expectedData.entrySet()) {
            String name = entry.getKey();
            String quantity = entry.getValue();

            boolean nameFound = utilites.validation().IsTextDisplayed(name);
            Assert.assertTrue(nameFound, "الاسم غير موجود في التقرير: " + name);

            boolean quantityFound = utilites.validation().IsTextDisplayed(quantity);
            Assert.assertTrue(quantityFound, "الكمية غير موجودة في التقرير للصنف " + name + ": " + quantity);

            System.out.println("✅ تم التأكد من: " + name + " - الكمية: " + quantity);
        }
    }
    public void waitUntilMessageDisappear(String messageText) {
        int maxRetries = 3;
        boolean disappeared = false;

        for (int attempt = 1; attempt <= maxRetries && !disappeared; attempt++) {
            utilites.waits().waitForOverlayToDisappear();

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.xpath("//small[contains(text(),'" + messageText + "')]")
                ));

                disappeared = true;
                System.out.println("✅ الرسالة '" + messageText + "' اختفت في المحاولة رقم " + attempt);

            } catch (TimeoutException e) {
                System.out.println("⚠️ المحاولة " + attempt + " فشلت، الرسالة '" + messageText + "' لسه ظاهرة، جاري عمل Refresh...");

                if (attempt < maxRetries) {
                    driver.navigate().refresh();
                    utilites.waits().waitForOverlayToDisappear();
                }
            }
        }

        if (!disappeared) {
            throw new AssertionError(
                    "الرسالة '" + messageText + "' لم تختف خلال الوقت المحدد بعد " + maxRetries + " محاولات."
            );
        }
    }

    @Step("حذف :{}")
    public void DeleteByName(String Text  ) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.grid().deleteAllRowsByType(Text,Clickyestodelet);
    }

}
