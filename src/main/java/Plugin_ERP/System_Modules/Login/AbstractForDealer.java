package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AbstractForDealer extends Abstract {
    private static final By DEALER_TABLE_HEADERS = By.cssSelector("table thead th");
    private static final By DEALER_TABLE_ROWS = By.cssSelector("table tbody tr[role='row']");
    private static final By DEALER_SAVE_BUTTON = By.cssSelector("button[data-action-type='save']");
    private static final By DEALER_DELETE_CONFIRM = By.cssSelector(
            ".swal2-container.swal2-backdrop-show .swal2-confirm");
    private static final By DEALER_DELETE_CANCEL = By.cssSelector(
            ".swal2-container.swal2-backdrop-show .swal2-cancel");
    private String dealerListUrlBeforeForm;
    public AbstractForDealer(WebDriver driver) {
        super(driver);
    }

    //Locators

    private By NewButton = By.xpath("//div[contains(@class,'justify-content-end')]//button[@data-action-type='new']");
    private By DateInput = By.xpath("//input[@placeholder='YYYY-MM-DD']");
    private By AllowanceDays = By.cssSelector("input[name='allowanceDays']");
    private By commercialRegisterNumber = By.cssSelector("input[name='commercialRegisterNumber']");
    private By DiscountValue = By.cssSelector("input[placeholder='قيمة الخصم']");
    private By ValueLimit = By.cssSelector("input[name='value']");
    private By AddNote = By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']");
    private By ContactDataTab = By.xpath(
            "//a[@role='tab' and contains(normalize-space(),'بيانات الاتصال')]");
    private By BasicDataTab = By.xpath(
            "//a[@role='tab' and normalize-space()='البيانات الأساسية']");
    private By ContactPersonName = By.cssSelector("input[name='contactPerson']");
    private By LandLine = By.cssSelector("div[data-title='التليفون الأرضي'] input");
    private By Email = By.cssSelector("input[name='email']");
    private By ExtraNumber = By.cssSelector("input[name='extraNumber']");
    private By AnotherId = By.cssSelector("input[name='anotherId']");
    private By BankName = By.cssSelector("input[name='bankName']");
    private By AccountNumber = By.cssSelector("input[name='accountNumber']");
    private By AgentPhone = By.cssSelector("label[for='agentPhone'] + input");
    private By Iban = By.cssSelector("input[name='iban']");
    private By TaxNumber = By.cssSelector("input[name='taxNumber']");
    private By Country = By.cssSelector("input[name='country']");
    private By City = By.cssSelector("input[name='city']");
    private By District = By.cssSelector("input[name='district']");
    private By BuildingNumber = By.cssSelector("input[name='buildingNumber']");
    private By PostalCode = By.cssSelector("input[name='postalCode']");
    private By StreetName = By.cssSelector("input[name='streetName']");
    private By customerControlTab = By.xpath(
            "//a[@role='tab' and contains(normalize-space(),'التحكم في العملاء')]");
    private By SupplerControlTab = By.xpath(
            "//a[@role='tab' and contains(normalize-space(),'التحكم في المورد')]");
    private By AssertTaxNumber  = By.xpath("//small[contains(text(),'الحقل رقم المبني يجب ان يحتوي على 4 حروف على الأقل')]");
    private By AssertPostNumber  = By.xpath("//small[contains(text(),'الحقل الرمز البريدي يجب ان يحتوي على 5 حروف على الأقل')]");

    private By AssertAdress  = By.xpath("//small[contains(text(),'عنوان العميل مطلوب')]");
    private By AssertAgentNatural = By.xpath("//small[contains(text(),'طبيعه المندوب مطلوب')]");
    private By AssertClassification = By.xpath("//small[contains(text(),'نوع التصنيف مطلوب')]");
    private By AssertMainDealer = By.xpath("//small[contains(text(),'النوع مطلوب')]");
    private By AddNoteForStorgTrans =By.xpath("//textarea[@label-text='notes']");





    //الميثودات
//ميثودات الضغط
    @Step("الضغط على زر جديد")
    public void ClickNewButtonForDealer() {
        dealerListUrlBeforeForm = driver.getCurrentUrl();
        WebElement button = new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> d.findElements(NewButton).stream()
                        .filter(this::isElementDisplayed)
                        .filter(WebElement::isEnabled)
                        .filter(element -> Boolean.TRUE.equals(
                                ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                                        "arguments[0].scrollIntoView({block:'center'});"
                                                + "const r=arguments[0].getBoundingClientRect();"
                                                + "const top=document.elementFromPoint(r.x+r.width/2,r.y+r.height/2);"
                                                + "return top===arguments[0]||arguments[0].contains(top);", element)))
                        .findFirst().orElse(null));
        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> isVisible(DEALER_SAVE_BUTTON)
                        && isVisible(By.cssSelector("input[name='arabicName']")));
        showDealerBasicDataIfPresent();
    }

    private void showDealerBasicDataIfPresent() {
        if (driver.findElements(BasicDataTab).stream().anyMatch(this::isElementDisplayed)) {
            ClickBasicDataTab();
        }
    }

    @Step("اختيار {option} من قائمة {fieldTitle} في شاشة المتعامل")
    public void SelectDealerOption(String fieldTitle, String option) {
        utilites.dropdown().selectVueOptionByDataTitle(fieldTitle, option);
    }

    @Step("الضغط على تاب بيانات التواصل")
    public void ClickContactDataTab() {
        utilites.ClickElement(ContactDataTab);
    }

    @Step("الضغط على تبويب البيانات الأساسية للمتعامل")
    public void ClickBasicDataTab() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .ignoring(org.openqa.selenium.ElementClickInterceptedException.class)
                .until(d -> {
                    WebElement tab = d.findElement(BasicDataTab);
                    if (!tab.isDisplayed() || !tab.isEnabled()) return false;
                    tab.click();
                    return true;
                });
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElement(BasicDataTab).getAttribute("class").contains("active"));
    }

    @Step("الضغط على تاب التحكم في العميل")
    public void ClickCustomerControlTab() {
        utilites.ClickElement(customerControlTab);
    }


    @Step("الضغط على تاب التحكم في المورد")
    public void ClickSupplierControlTab() {
        utilites.ClickElement(SupplerControlTab);
    }

    //ميثودات الإضافة
    @Step("إضافة التاريخ: {todayDate}")
    public void AddDateForDealer() {
        utilites.waits().waitForOverlayToDisappear();
        String todayDate = utilites.Utility().TypeTodayDate("dd/MM/yyyy");
        utilites.TaypeTextB(DateInput, todayDate);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("اضافه ملاحظات :{notes}")
    public void AddNoteForStorgTrans(String notes) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(AddNoteForStorgTrans,notes);
        utilites.waits().waitForOverlayToDisappear();
    }
    @Step("إضافة أيام السماح: {allowanceDays}")
    public void AddAllowanceDays(String allowanceDays) {
        utilites.TaypeTextB(AllowanceDays, allowanceDays);
    }

    @Step("إضافة رقم السجل التجاري: {commercialRegisterNumber}")
    public void AddCommercialRegisterNumber(String commercialRegisterNumber) {
        utilites.TaypeTextB(this.commercialRegisterNumber, commercialRegisterNumber);
    }

    @Step("إضافة قيمة الخصم: {discountValue}")
    public void AddDiscountValue(String discountValue) {
        utilites.TaypeTextB(DiscountValue, discountValue);
    }

    @Step("إضافة حد القيمة: {valueLimit}")
    public void AddValueLimit(String valueLimit) {
        utilites.TaypeTextB(ValueLimit, valueLimit);
    }

    @Step("إضافة ملاحظة: {note}")
    public void AddNoteForDealer(String note) {
        utilites.TaypeTextB(AddNote, note);
    }

    @Step("إضافة اسم جهة الاتصال: {contactPersonName}")
    public void AddContactPersonName(String contactPersonName) {
        utilites.TaypeTextB(ContactPersonName, contactPersonName);
    }

    @Step("إضافة الخط الأرضي: {landLine}")
    public void AddLandLine(String landLine) {
        utilites.TaypeTextB(LandLine, landLine);
    }

    @Step("إضافة البريد الإلكتروني: {email}")
    public void AddEmail(String email) {
        utilites.TaypeTextB(Email, email);
    }

    @Step("إضافة رقم إضافي: {extraNumber}")
    public void AddExtraNumber(String extraNumber) {
        utilites.TaypeTextB(ExtraNumber, extraNumber);
    }

    @Step("إضافة رقم تعريفي آخر: {anotherId}")
    public void AddAnotherId(String anotherId) {
        utilites.TaypeTextB(AnotherId, anotherId);
    }

    @Step("إضافة اسم البنك: {bankName}")
    public void AddBankName(String bankName) {
        utilites.TaypeTextB(BankName, bankName);
    }

    @Step("إضافة رقم الحساب: {accountNumber}")
    public void AddAccountNumber(String accountNumber) {
        utilites.TaypeTextB(AccountNumber, accountNumber);
    }

    @Step("إضافة رقم هاتف الوكيل: {agentPhone}")
    public void AddAgentPhone(String agentPhone) {
        utilites.TaypeTextB(AgentPhone, agentPhone);
    }

    @Step("إضافة رقم الآيبان: {iban}")
    public void AddIban(String iban) {
        utilites.TaypeTextB(Iban, iban);
    }

    @Step("إضافة الرقم الضريبي: {taxNumber}")
    public void AddTaxNumber(String taxNumber) {
        utilites.TaypeTextB(TaxNumber, taxNumber);
    }

    @Step("إضافة الدولة: {country}")
    public void AddCountry(String country) {
        utilites.TaypeTextB(Country, country);
    }

    @Step("إضافة المدينة: {city}")
    public void AddCity(String city) {
        utilites.TaypeTextB(City, city);
    }

    @Step("إضافة الحي: {district}")
    public void AddDistrict(String district) {
        utilites.TaypeTextB(District, district);
    }

    @Step("إضافة رقم المبنى: {buildingNumber}")
    public void AddBuildingNumber(String buildingNumber) {
        utilites.TaypeTextB(BuildingNumber, buildingNumber);
    }

    @Step("إضافة الرمز البريدي: {postalCode}")
    public void AddPostalCode(String postalCode) {
        utilites.TaypeTextB(PostalCode, postalCode);
    }

    @Step("إضافة اسم الشارع: {streetName}")
    public void AddStreetName(String streetName) {
        utilites.TaypeTextB(StreetName, streetName);
    }


    //ميثودات الـ Checkbox
    @Step("تعيين قيمة الـ Checkbox: {text} إلى {shouldBeChecked}")
    public void SetCheckboxValueForDealer(String text, boolean shouldBeChecked) {
        utilites.checkbox().setCheckboxForDealer(text, shouldBeChecked);
    }

    @Step("تعيين قيمة الـ Checkbox الضريبي: {text} إلى {shouldBeChecked}")
    public void SetCheckboxValueForTax(String text, boolean shouldBeChecked) {
        utilites.checkbox().setCheckboxForTax(text, shouldBeChecked);
    }


    //ميثودات التحقق
    @Step("التأكد من أن رقم المبنى يحتوي على 4 أرقام")
    public boolean AssertBuildingNumber() {
        return utilites.isDisplayed(AssertTaxNumber);
    }

    @Step("التأكد من أن العنوان مطلوب بعد اختيار Checkbox عميل شركة")
    public boolean AssertAdress() {
        return utilites.isDisplayed(AssertAdress);
    }

    @Step("التأكد من أن الرقم البريدي 5 أرقام بعد اختيار Checkbox عميل شركة")
    public boolean AssertPostNumber() {
        return utilites.isDisplayed(AssertPostNumber);
    }

@Step("التاكد ان طبيعه المندوب مطلوبه")
public boolean AssertAgentNatural() {
    return utilites.isDisplayed(AssertAgentNatural);
}
    @Step("التاكد اننوع التصنيف مطلوب")
    public boolean AssertClassification() {
        return utilites.isDisplayed(AssertClassification);
    }
    @Step("التأكد ان النوع مطلوب")
    public boolean AssertKind() {
        return utilites.isDisplayed(AssertMainDealer);
    }


@Step("الانتظار حتى ظهور البيانات")
public void waitForTableDataToLoad(){
        utilites.grid().waitForTableDataToLoad(2);

}

@Step("التأكد من أن البيانات موجودة في الجدول")
public boolean isDataPresentInTable() {
    return utilites.grid().isTableDataDisplayed();

}

@Step("التاكد من عدد الصفوف")
public int getRowCount()
{int count = utilites.grid().getTableRowsCount();
    LogUtils.info("عدد الصفوف في الجدول: " + count);
    return count;
}

    @Step("التحقق من ظهور قائمة المتعاملين وأعمدتها")
    public boolean isDealerListStructureDisplayed() {
        boolean hasHeaders = driver.findElements(DEALER_TABLE_HEADERS).stream()
                .anyMatch(this::isElementDisplayed);
        boolean hasRowActions = driver.findElements(DEALER_TABLE_ROWS).stream()
                .filter(this::isElementDisplayed)
                .anyMatch(row -> row.findElements(By.cssSelector(
                        "button[data-action-type='edit'],button[data-action-type='delete']")).stream()
                        .anyMatch(this::isElementDisplayed));
        return hasHeaders && hasRowActions;
    }

    @Step("عدد صفوف قائمة المتعاملين الظاهرة")
    public int getVisibleDealerRowCount() {
        return (int) driver.findElements(DEALER_TABLE_ROWS).stream()
                .filter(this::isElementDisplayed)
                .count();
    }

    @Step("التحقق من ظهور نموذج إنشاء المتعامل")
    public boolean isDealerCreationFormDisplayed() {
        return isVisible(DEALER_SAVE_BUTTON)
                && isVisible(By.cssSelector("input[name='arabicName']"));
    }

    @Step("الحقول والأزرار المشتركة الناقصة من نموذج المتعامل")
    public String getMissingDealerCommonFormControls() {
        List<String> missing = new java.util.ArrayList<>();
        addMissingControl(missing, "الاسم العربي", By.cssSelector("input[name='arabicName']"));
        addMissingControl(missing, "الاسم الإنجليزي", By.cssSelector("input[name='englishName']"));
        addMissingControl(missing, "الملاحظات", By.cssSelector("textarea"));
        addMissingControl(missing, "حفظ", DEALER_SAVE_BUTTON);
        return String.join("، ", missing);
    }

    @Step("إغلاق نموذج المتعامل دون حفظ")
    public void closeDealerFormWithoutSaving() {
        // A dismissed confirmation can remain visible during its closing animation.
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".swal2-container")));
        WebElement closeButton = findDealerCloseButton();
        if (closeButton != null) {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", closeButton);
        } else {
            // Some Dealer forms are full-page states without a close button; return to the list route.
            if (dealerListUrlBeforeForm != null) {
                driver.navigate().to(dealerListUrlBeforeForm);
            } else {
                driver.navigate().back();
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(DEALER_SAVE_BUTTON));
    }

    private WebElement findDealerCloseButton() {
        List<By> candidates = List.of(
                By.cssSelector("button[data-action-type='close']"),
                By.cssSelector("button[data-action-type='cancel']"),
                By.xpath("//button[normalize-space()='إغلاق' or normalize-space()='اغلاق']"),
                By.xpath("//button[normalize-space()='إلغاء' or normalize-space()='الغاء']"),
                By.cssSelector("button[aria-label='Close'],button[aria-label='Cancel']"));
        for (By candidate : candidates) {
            for (WebElement button : driver.findElements(candidate)) {
                if (isElementDisplayed(button)
                        && button.findElements(By.xpath("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' swal2-container ')]")).isEmpty()) {
                    return button;
                }
            }
        }
        return null;
    }

    private void addMissingControl(List<String> missing, String label, By locator) {
        if (!isVisible(locator)) {
            missing.add(label);
        }
    }

    @Step("فتح أول متعامل للتعديل دون حفظ")
    public void openFirstDealerForEdit() {
        dealerListUrlBeforeForm = driver.getCurrentUrl();
        WebElement row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(DEALER_TABLE_ROWS).stream()
                        .filter(this::isElementDisplayed)
                        .filter(candidate -> candidate.findElements(
                                By.cssSelector("button[data-action-type='edit']")).stream()
                                .anyMatch(this::isElementDisplayed))
                        .findFirst().orElse(null));
        clickDealerRowAction(row, "edit");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_SAVE_BUTTON));
        waitForDealerEditData();
    }

    private void waitForDealerEditData() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .until(d -> !getDealerArabicNameValue().isBlank());
        showDealerBasicDataIfPresent();
    }

    @Step("قراءة الاسم العربي لأول سجل في القائمة")
    public String getFirstDealerArabicName() {
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

    @Step("قراءة الاسم العربي الحالي من نموذج المتعامل")
    public String getDealerArabicNameValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']"))
                .getAttribute("value").trim();
    }

    @Step("طلب حذف أول سجل دون تأكيد")
    public void requestDeleteFirstDealer() {
        WebElement row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(DEALER_TABLE_ROWS).stream()
                        .filter(this::isElementDisplayed)
                        .filter(candidate -> candidate.findElements(
                                By.cssSelector("button[data-action-type='delete']")).stream()
                                .anyMatch(this::isElementDisplayed))
                        .findFirst().orElse(null));
        clickDealerRowAction(row, "delete");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_DELETE_CANCEL));
    }

    @Step("إلغاء تأكيد حذف السجل")
    public void cancelDealerDelete() {
        WebElement cancel = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_DELETE_CANCEL));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", cancel);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(DEALER_DELETE_CANCEL));
    }

    @Step("التحقق من ظهور نافذة تأكيد الحذف")
    public boolean isDealerDeleteConfirmationDisplayed() {
        return isVisible(DEALER_DELETE_CONFIRM) && isVisible(DEALER_DELETE_CANCEL);
    }

    @Step("إغلاق إشعار نجاح حفظ سجل المتعامل عند ظهوره")
    public void dismissDealerSaveSuccessIfPresent() {
        By successPopup = By.cssSelector(".swal2-container.swal2-backdrop-show");
        try {
            WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(d -> d.findElements(By.cssSelector(
                                    ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                            .stream().filter(this::isElementDisplayed).findFirst().orElse(null));
            confirm.click();
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(successPopup));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Successful saves may use a toast or close the dialog automatically.
        }
    }

    @Step("التحقق من عدم وجود سجل بالاسم الدقيق: {arabicName}")
    public boolean isDealerNameVisibleInList(String arabicName) {
        return getExactDealerNameMatchCount(arabicName) > 0;
    }

    @Step("عدد السجلات المطابقة للاسم العربي بدقة: {arabicName}")
    public int getExactDealerNameMatchCount(String arabicName) {
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

    @Step("فتح سجل المتعامل المطابق للاسم للتعديل: {arabicName}")
    public void openDealerForEditByName(String arabicName) {
        dealerListUrlBeforeForm = driver.getCurrentUrl();
        WebElement row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(DEALER_TABLE_ROWS).stream()
                        .filter(this::isElementDisplayed)
                        .filter(candidate -> candidate.findElements(By.cssSelector("td")).stream()
                                .anyMatch(cell -> cell.getText().trim().equals(arabicName)))
                        .findFirst().orElse(null));
        clickDealerRowAction(row, "edit");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_SAVE_BUTTON));
        waitForDealerEditData();
    }

    @Step("طلب حذف سجل الاختبار المطابق للاسم: {arabicName}")
    public void requestDealerDeleteByName(String arabicName) {
        WebElement row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(DEALER_TABLE_ROWS).stream()
                        .filter(this::isElementDisplayed)
                        .filter(candidate -> candidate.findElements(By.cssSelector("td")).stream()
                                .anyMatch(cell -> cell.getText().trim().equals(arabicName)))
                        .findFirst().orElse(null));
        clickDealerRowAction(row, "delete");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_DELETE_CONFIRM));
    }

    @Step("تأكيد حذف سجل الاختبار المطابق للاسم: {arabicName}")
    public void confirmDealerDeleteByName(String arabicName) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(DEALER_DELETE_CONFIRM)).click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> getExactDealerNameMatchCount(arabicName) == 0);
        By successPopup = By.xpath(
                "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحذف')]");
        try {
            WebElement successButton = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(d -> d.findElements(By.cssSelector(
                                    ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                            .stream().filter(this::isElementDisplayed)
                            .filter(button -> driver.findElements(successPopup).stream()
                                    .anyMatch(this::isElementDisplayed))
                            .findFirst().orElse(null));
            successButton.click();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Some deployments close the confirmation without a separate success dialog.
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show"))
                        .stream().noneMatch(this::isElementDisplayed));
    }

    private boolean isVisible(By locator) {
        return driver.findElements(locator).stream().anyMatch(this::isElementDisplayed);
    }

    private void clickDealerRowAction(WebElement row, String action) {
        WebElement button = row.findElement(By.cssSelector(
                "button[data-action-type='" + action + "']"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", button);
        try {
            button.click();
        } catch (org.openqa.selenium.ElementNotInteractableException exception) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", button);
        }
    }

    private boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }






    @Step("الاختيار بالأزرار: {option}")
    public void SelectByKeysForDealers(String option) {
        actions.sendKeys(Keys.TAB)
                .sendKeys(Keys.TAB)
                .pause(Duration.ofMillis(500))
                .sendKeys(option)
                .pause(Duration.ofMillis(500))
                .sendKeys(Keys.ENTER)
                .perform();
        utilites.waits().waitForOverlayToDisappear();
    }









}
