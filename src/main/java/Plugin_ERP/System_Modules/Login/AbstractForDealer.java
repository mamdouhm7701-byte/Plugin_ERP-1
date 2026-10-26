package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AbstractForDealer extends Abstract {
    private final DealerFormControls form;
    private final DealerRecordActions recordActions;
    private final DealerListQueries listQueries;

    private String dealerListUrlBeforeForm;

    public AbstractForDealer(WebDriver driver) {
        super(driver);
        recordActions = new DealerRecordActions(this, driver);
        listQueries = new DealerListQueries(this, driver);
        form = new DealerFormControls(this, driver);
    }

    // Locators

    private By DateInput = By.xpath("//input[@placeholder='YYYY-MM-DD']");
    private By AllowanceDays = By.cssSelector("input[name='allowanceDays']");
    private By commercialRegisterNumber = By.cssSelector("input[name='commercialRegisterNumber']");
    private By DiscountValue = By.cssSelector("input[placeholder='قيمة الخصم']");
    private By ValueLimit = By.cssSelector("input[name='value']");
    private By AddNote = By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']");

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

    private By AssertTaxNumber =
            By.xpath(
                    "//small[contains(text(),'الحقل رقم المبني يجب ان يحتوي على 4 حروف على الأقل')]");
    private By AssertPostNumber =
            By.xpath(
                    "//small[contains(text(),'الحقل الرمز البريدي يجب ان يحتوي على 5 حروف على الأقل')]");

    private By AssertAdress = By.xpath("//small[contains(text(),'عنوان العميل مطلوب')]");
    private By AssertAgentNatural = By.xpath("//small[contains(text(),'طبيعه المندوب مطلوب')]");
    private By AssertClassification = By.xpath("//small[contains(text(),'نوع التصنيف مطلوب')]");
    private By AssertMainDealer = By.xpath("//small[contains(text(),'النوع مطلوب')]");
    private By AddNoteForStorgTrans = By.xpath("//textarea[@label-text='notes']");

    // الميثودات
    // ميثودات الضغط
    @Step("الضغط على زر جديد")
    public void ClickNewButtonForDealer() {
        form.ClickNewButtonForDealer();
    }

    private void showDealerBasicDataIfPresent() {
        form.showDealerBasicDataIfPresent();
    }

    @Step("اختيار {option} من قائمة {fieldTitle} في شاشة المتعامل")
    public void SelectDealerOption(String fieldTitle, String option) {
        utilites.dropdown().selectVueOptionByDataTitle(fieldTitle, option);
    }

    @Step("الضغط على تاب بيانات التواصل")
    public void ClickContactDataTab() {
        form.ClickContactDataTab();
    }

    @Step("الضغط على تبويب البيانات الأساسية للمتعامل")
    public void ClickBasicDataTab() {
        form.ClickBasicDataTab();
    }

    @Step("الضغط على تاب التحكم في العميل")
    public void ClickCustomerControlTab() {
        form.ClickCustomerControlTab();
    }

    @Step("الضغط على تاب التحكم في المورد")
    public void ClickSupplierControlTab() {
        form.ClickSupplierControlTab();
    }

    // ميثودات الإضافة
    @Step("إضافة التاريخ: {todayDate}")
    public void AddDateForDealer() {
        utilites.waits().waitForOverlayToDisappear();
        String todayDate = utilites.Utility().TypeTodayDate("dd/MM/yyyy");
        utilites.typeTextAndVerify(DateInput, todayDate);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("اضافه ملاحظات :{notes}")
    public void AddNoteForStorgTrans(String notes) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(AddNoteForStorgTrans, notes);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("إضافة أيام السماح: {allowanceDays}")
    public void AddAllowanceDays(String allowanceDays) {
        utilites.typeTextAndVerify(AllowanceDays, allowanceDays);
    }

    @Step("إضافة رقم السجل التجاري: {commercialRegisterNumber}")
    public void AddCommercialRegisterNumber(String commercialRegisterNumber) {
        utilites.typeTextAndVerify(this.commercialRegisterNumber, commercialRegisterNumber);
    }

    @Step("إضافة قيمة الخصم: {discountValue}")
    public void AddDiscountValue(String discountValue) {
        utilites.typeTextAndVerify(DiscountValue, discountValue);
    }

    @Step("إضافة حد القيمة: {valueLimit}")
    public void AddValueLimit(String valueLimit) {
        utilites.typeTextAndVerify(ValueLimit, valueLimit);
    }

    @Step("إضافة ملاحظة: {note}")
    public void AddNoteForDealer(String note) {
        utilites.typeTextAndVerify(AddNote, note);
    }

    @Step("إضافة اسم جهة الاتصال: {contactPersonName}")
    public void AddContactPersonName(String contactPersonName) {
        utilites.typeTextAndVerify(ContactPersonName, contactPersonName);
    }

    @Step("إضافة الخط الأرضي: {landLine}")
    public void AddLandLine(String landLine) {
        utilites.typeTextAndVerify(LandLine, landLine);
    }

    @Step("إضافة البريد الإلكتروني: {email}")
    public void AddEmail(String email) {
        utilites.typeTextAndVerify(Email, email);
    }

    @Step("إضافة رقم إضافي: {extraNumber}")
    public void AddExtraNumber(String extraNumber) {
        utilites.typeTextAndVerify(ExtraNumber, extraNumber);
    }

    @Step("إضافة رقم تعريفي آخر: {anotherId}")
    public void AddAnotherId(String anotherId) {
        utilites.typeTextAndVerify(AnotherId, anotherId);
    }

    @Step("إضافة اسم البنك: {bankName}")
    public void AddBankName(String bankName) {
        utilites.typeTextAndVerify(BankName, bankName);
    }

    @Step("إضافة رقم الحساب: {accountNumber}")
    public void AddAccountNumber(String accountNumber) {
        utilites.typeTextAndVerify(AccountNumber, accountNumber);
    }

    @Step("إضافة رقم هاتف الوكيل: {agentPhone}")
    public void AddAgentPhone(String agentPhone) {
        utilites.typeTextAndVerify(AgentPhone, agentPhone);
    }

    @Step("إضافة رقم الآيبان: {iban}")
    public void AddIban(String iban) {
        utilites.typeTextAndVerify(Iban, iban);
    }

    @Step("إضافة الرقم الضريبي: {taxNumber}")
    public void AddTaxNumber(String taxNumber) {
        utilites.typeTextAndVerify(TaxNumber, taxNumber);
    }

    @Step("إضافة الدولة: {country}")
    public void AddCountry(String country) {
        utilites.typeTextAndVerify(Country, country);
    }

    @Step("إضافة المدينة: {city}")
    public void AddCity(String city) {
        utilites.typeTextAndVerify(City, city);
    }

    @Step("إضافة الحي: {district}")
    public void AddDistrict(String district) {
        utilites.typeTextAndVerify(District, district);
    }

    @Step("إضافة رقم المبنى: {buildingNumber}")
    public void AddBuildingNumber(String buildingNumber) {
        utilites.typeTextAndVerify(BuildingNumber, buildingNumber);
    }

    @Step("إضافة الرمز البريدي: {postalCode}")
    public void AddPostalCode(String postalCode) {
        utilites.typeTextAndVerify(PostalCode, postalCode);
    }

    @Step("إضافة اسم الشارع: {streetName}")
    public void AddStreetName(String streetName) {
        utilites.typeTextAndVerify(StreetName, streetName);
    }

    // ميثودات الـ Checkbox
    @Step("تعيين قيمة الـ Checkbox: {text} إلى {shouldBeChecked}")
    public void SetCheckboxValueForDealer(String text, boolean shouldBeChecked) {
        utilites.checkbox().setCheckboxForDealer(text, shouldBeChecked);
    }

    @Step("تعيين قيمة الـ Checkbox الضريبي: {text} إلى {shouldBeChecked}")
    public void SetCheckboxValueForTax(String text, boolean shouldBeChecked) {
        utilites.checkbox().setCheckboxForTax(text, shouldBeChecked);
    }

    // ميثودات التحقق
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
    public void waitForTableDataToLoad() {
        utilites.grid().waitForTableDataToLoad(2);
    }

    @Step("التأكد من أن البيانات موجودة في الجدول")
    public boolean isDataPresentInTable() {
        return utilites.grid().isTableDataDisplayed();
    }

    @Step("التاكد من عدد الصفوف")
    public int getRowCount() {
        int count = utilites.grid().getTableRowsCount();
        LogUtils.info("عدد الصفوف في الجدول: " + count);
        return count;
    }

    @Step("التحقق من ظهور قائمة المتعاملين وأعمدتها")
    public boolean isDealerListStructureDisplayed() {
        return listQueries.isDealerListStructureDisplayed();
    }

    @Step("عدد صفوف قائمة المتعاملين الظاهرة")
    public int getVisibleDealerRowCount() {
        return listQueries.getVisibleDealerRowCount();
    }

    @Step("التحقق من ظهور نموذج إنشاء المتعامل")
    public boolean isDealerCreationFormDisplayed() {
        return form.isDealerCreationFormDisplayed();
    }

    @Step("الحقول والأزرار المشتركة الناقصة من نموذج المتعامل")
    public String getMissingDealerCommonFormControls() {
        return form.getMissingDealerCommonFormControls();
    }

    @Step("إغلاق نموذج المتعامل دون حفظ")
    public void closeDealerFormWithoutSaving() {
        form.closeDealerFormWithoutSaving();
    }

    @Step("فتح أول متعامل للتعديل دون حفظ")
    public void openFirstDealerForEdit() {
        recordActions.openFirstDealerForEdit();
    }

    @Step("قراءة الاسم العربي لأول سجل في القائمة")
    public String getFirstDealerArabicName() {
        return listQueries.getFirstDealerArabicName();
    }

    @Step("قراءة الاسم العربي الحالي من نموذج المتعامل")
    public String getDealerArabicNameValue() {
        return form.getDealerArabicNameValue();
    }

    @Step("طلب حذف أول سجل دون تأكيد")
    public void requestDeleteFirstDealer() {
        recordActions.requestDeleteFirstDealer();
    }

    @Step("إلغاء تأكيد حذف السجل")
    public void cancelDealerDelete() {
        recordActions.cancelDealerDelete();
    }

    @Step("التحقق من ظهور نافذة تأكيد الحذف")
    public boolean isDealerDeleteConfirmationDisplayed() {
        return recordActions.isDealerDeleteConfirmationDisplayed();
    }

    @Step("إغلاق إشعار نجاح حفظ سجل المتعامل عند ظهوره")
    public void dismissDealerSaveSuccessIfPresent() {
        recordActions.dismissDealerSaveSuccessIfPresent();
    }

    @Step("التحقق من عدم وجود سجل بالاسم الدقيق: {arabicName}")
    public boolean isDealerNameVisibleInList(String arabicName) {
        return listQueries.isDealerNameVisibleInList(arabicName);
    }

    @Step("عدد السجلات المطابقة للاسم العربي بدقة: {arabicName}")
    public int getExactDealerNameMatchCount(String arabicName) {
        return listQueries.getExactDealerNameMatchCount(arabicName);
    }

    @Step("فتح سجل المتعامل المطابق للاسم للتعديل: {arabicName}")
    public void openDealerForEditByName(String arabicName) {
        recordActions.openDealerForEditByName(arabicName);
    }

    @Step("طلب حذف سجل الاختبار المطابق للاسم: {arabicName}")
    public void requestDealerDeleteByName(String arabicName) {
        recordActions.requestDealerDeleteByName(arabicName);
    }

    @Step("تأكيد حذف سجل الاختبار المطابق للاسم: {arabicName}")
    public void confirmDealerDeleteByName(String arabicName) {
        recordActions.confirmDealerDeleteByName(arabicName);
    }

    private boolean isVisible(By locator) {
        return form.isVisible(locator);
    }

    private boolean isElementDisplayed(WebElement element) {
        return listQueries.isElementDisplayed(element);
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

    void rememberDealerListLocation() {
        dealerListUrlBeforeForm = driver.getCurrentUrl();
    }

    boolean dealerElementDisplayed(WebElement element) {
        return isElementDisplayed(element);
    }

    boolean dealerControlVisible(By locator) {
        return isVisible(locator);
    }

    void showDealerBasicData() {
        showDealerBasicDataIfPresent();
    }

    String dealerListLocation() {
        return dealerListUrlBeforeForm;
    }
}
