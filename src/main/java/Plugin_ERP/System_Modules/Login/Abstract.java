/* شرح الملف: مساعدة مشتركة لإدخال الاسم والتاريخ والملاحظات وحفظ السجلات والتنقل بينها. فائدتها
 * تسهيل التعامل مع البيانات الأساسية في أكثر من شاشة.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import Plugin_ERP.Enums.Pages;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public abstract class Abstract {
    private final PageFeedback feedback;
    private final PageSelections selections;
    private final PageNavigation navigation;
    private final PrintedDocumentControls printing;
    private final PageRecordActions records;
    private final PageFormFields formFields;

    // Variables
    protected WebDriver driver;
    protected ElementActions utilites;
    protected AlertAction alertAction;
    protected WaitManagement waitManagement;
    protected BrowserAction browserAction;
    protected Actions actions;
    private final PageContext context;

    public final PageContext context() {
        return context;
    }

    // Constructors
    public Abstract(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElementActions(driver);
        this.waitManagement = new WaitManagement(driver);
        this.browserAction = new BrowserAction(driver);
        this.actions = new Actions(driver);
        this.context = new PageContext(driver, utilites, waitManagement, browserAction, actions);
        formFields = new PageFormFields(this, driver);
        records = new PageRecordActions(this, driver);
        printing = new PrintedDocumentControls(this, driver);
        navigation = new PageNavigation(this, driver);
        selections = new PageSelections(this, driver);
        feedback = new PageFeedback(this, driver);
    }

    // Locators
    private By ClickNew = By.cssSelector("button[data-action-type='new']");

    public By Clickyestodelet =
            By.xpath("//button[contains(@class, \"swal2-confirm\") and contains(text(), \"نعم\")]");
    private By AddDate = By.cssSelector("fieldset[label-text='date'] input");

    private By AddArabicName = By.cssSelector("input[name='arabicName']");
    private By AddEnglishName = By.cssSelector("input[name='englishName']");

    private By AddAdress = By.xpath("//input[@class='form-control' and @id='adress']\n");

    // الميثودات

    // ميثودات الإضافة
    @Step("إضافة الاسم العربي: {ArabicName}")
    public void AddArabicName(String ArabicName) {
        formFields.AddArabicName(ArabicName);
    }

    @Step("إضافة الاسم الإنجليزي: {EnglishName}")
    public void AddEnglishName(String EnglishName) {
        formFields.AddEnglishName(EnglishName);
    }

    @Step("اضافه رقم الجول :{PhoneNumber}")
    public void AddPhoneNumber(String PhoneNumber) {
        formFields.AddPhoneNumber(PhoneNumber);
    }

    @Step("إضافة القيمة: {value}")
    public void AddValue(String value) {
        formFields.AddValue(value);
    }

    @Step("إضافة المرجع: {reference}")
    public void AddReference(String reference) {
        formFields.AddReference(reference);
    }

    @Step("اضافه العنوان: {adress}")
    public void AddAdress(String adress) {
        formFields.AddAdress(adress);
    }

    @Step("إضافة ملاحظات: {notes}")
    public void AddNotes(String notes) {
        formFields.AddNotes(notes);
    }

    @Step("إضافة التاريخ: {todayDate}")
    public String AddDate() {
        return formFields.AddDate();
    }

    public String GetTodayDate() {
        return formFields.GetTodayDate();
    }

    @Step("إضافة التاريخ العام السابق: {pastYearDate}")
    public void AddPastYearDate() {
        formFields.AddPastYearDate();
    }

    public String generateRandomNumber() {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int digit = (int) (Math.random() * 10);
            number.append(digit);
        }
        return number.toString();
    }

    // ميثودات الضغط
    @Step("الضغط على زر جديد")
    public void ClickNew() {
        records.ClickNew();
    }

    @Step("الضغط على زر إغلاق")
    public void ClickCloseButton() {
        records.ClickCloseButton();
    }

    @Step("الضغط على زر الحفظ والطباعة")
    public void ClickSaveAndPrintButton() {
        printing.ClickSaveAndPrintButton();
    }

    @Step("الضغط على زر الحفظ")
    public void ClickSaveButton() {
        records.ClickSaveButton();
    }

    // ميثودات التعديل والحذف
    @Step("الضغط على زر التعديل لآخر سجل في الجريد")
    public void Edit() {
        records.Edit();
    }

    @Step("الضغط على زر الحذف لآخر سجل في الجريد")
    public void Delete() {
        records.Delete();
    }

    @Step("حذف جميع السجلات")
    public void DeleteAll() {
        records.DeleteAll();
    }

    // ميثودات التحقق
    @Step("التأكد من أن الاسم العربي مطلوب")
    public boolean IsArabicNameRequiredErrorDisplayed() {
        return feedback.IsArabicNameRequiredErrorDisplayed();
    }

    @Step("التأكد من أن البريد الإلكتروني صحيح")
    public boolean IsRightEmailErrorDisplayed() {
        return feedback.IsRightEmailErrorDisplayed();
    }

    @Step("التأكد من أن الرقم الضريبي مطلوب بعد اختيار Checkbox عميل شركة")
    public boolean IsTaxNumberRequiredErrorDisplayed() {
        return feedback.IsTaxNumberRequiredErrorDisplayed();
    }

    @Step("التأكد من وجود الإيصال: {ArabicName}")
    public void AssertReceiptAdded(String ArabicName) {
        records.AssertReceiptAdded(ArabicName);
    }

    public void CloserReport() {
        printing.CloserReport();
    }

    @Step("التأكد من ظهور النص: {Text}")
    public void AssertTextInReport(String Text) {
        printing.AssertTextInReport(Text);
    }

    @Step("التأكد من ظهور الرقم: {Number}")
    public void AssertNumberInReport(String Number) {
        printing.AssertNumberInReport(Number);
    }

    @Step("التأكد من ظهور التاريخ: {Date}")
    public void AssertDateInReport(String Date) {
        printing.AssertDateInReport(Date);
    }

    @Step("التأكد من أن أرقام البنك أرقام فقط ولا تحتوي على أحرف")
    public boolean AssertNumberOfBankWithOutLetter() {
        return feedback.AssertNumberOfBankWithOutLetter();
    }

    @Step("التأكد من أن الـ Checkbox محدد: {checkboxName}")
    public void AssertCheckboxIsChecked(String checkboxName, boolean expectedState) {
        feedback.AssertCheckboxIsChecked(checkboxName, expectedState);
    }

    @Step("التحقق ان زرارالحفظ غير قابل للضغط")
    public boolean AssertSaveButtonIsNotChecked() {
        return feedback.AssertSaveButtonIsNotChecked();
    }

    // ميثودات الاختيار
    @Step("الاختيار من القائمة المنسدلة: {Name} من: {Type}")
    public void SelectFromDropDawn(String Name, String Type) {
        selections.SelectFromDropDawn(Name, Type);
    }

    @Step("الاختيار من القائمة المنسدلة: {Name} من: {Type} باستخدام DataTitle")
    public void SelectFromDropDawnByText(String Name, String Type) {
        selections.SelectFromDropDawnByText(Name, Type);
    }

    @Step("الاختيار من القائمة المنسدلة: {label} من: {value}")
    public void selectTreasuryHandoverDropdown(String label, String value) {
        selections.selectTreasuryHandoverDropdown(label, value);
    }

    @Step("الاختيار بالأزرار: {option}")
    public void SelectByKeys(String option) {
        selections.SelectByKeys(option);
    }

    @Step("الضغط على انتر لاختيار الصنف")
    public void ClickEnter() {
        selections.ClickEnter();
    }

    @Step("الضغط على شيفت للانتقال")
    public void ClickShift() {
        selections.ClickShift();
    }

    public void clickOnEmptyArea() {
        selections.clickOnEmptyArea();
    }

    @Step("تعيين قيمة الـ Checkbox: {checkboxName} إلى {value}")
    public void SetCheckboxValue(String checkboxName, boolean value) {
        selections.SetCheckboxValue(checkboxName, value);
    }

    @Step("اختيار الـ Checkbox: {checkboxName} إلى {value}")
    public void SelectCheckboxByText(String checkboxName, boolean value) {
        selections.SelectCheckboxByText(checkboxName, value);
    }

    @Step("تغير حالةCheckbox: {checkboxName} إلى {expectedState}")
    public void SmartCheck(String checkboxName, boolean expectedState) {
        selections.SmartCheck(checkboxName, expectedState);
    }

    // ميثودات الـ Popup
    @Step("التحقق من ظهور نافذة الخطأ")
    public boolean isErrorPopupDisplayed() {
        return feedback.isErrorPopupDisplayed();
    }

    @Step("التاكد من ظهور بوب اب والضغط على موافق")
    public void AssertPopupAndClickOK() {
        feedback.AssertPopupAndClickOK();
    }

    @Step("التاكد من ظهور بوب اب والضغط على الغاء")
    public void AssertPopupAndClickCancel() {
        feedback.AssertPopupAndClickCancel();
    }

    @Step("اضافه اول {count} اصناف من القائمه")
    public void AddNewProductList(int count) {
        selections.AddNewProductList(count);
    }

    @Step("الانتقال الى الرابط: {url}")
    public void NavigateTo(String url) {
        navigation.NavigateTo(url);
    }

    @Step("فتح شاشة {route}")
    public void NavigationHelper(Pages route) {
        navigation.NavigationHelper(route);
    }
}
