package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import Plugin_ERP.Enums.Pages;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public abstract class Abstract {


    //Variables
    protected WebDriver driver;
    protected ElmentAction utilites;
    protected AlertAction alertAction;
    protected WaitMangment waitMangment;
    protected BrowserAction browserAction;
    protected Actions actions;

    //Constructors
    public Abstract(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElmentAction(driver);
        this.waitMangment = new WaitMangment(driver);
        this.browserAction = new BrowserAction(driver);
        this.actions = new Actions(driver);
    }

    //Locators
    private By ClickNew = By.cssSelector("button[data-action-type='new']");
    private By Value = By.cssSelector("input[label-text='value']");
    private By Notes = By.cssSelector("textarea[label-text='Notes']");
    private By SaveAndPrintButton =
            By.cssSelector("button[data-action-type='saveAndPrint']");
    private By SaveButton =
            By.cssSelector("button[data-action-type='save']");
    public By Clickyestodelet = By.xpath("//button[contains(@class, \"swal2-confirm\") and contains(text(), \"نعم\")]");
    private By AddDate = By.cssSelector("fieldset[label-text='date'] input");
    private By Reference = By.cssSelector("input[name='reference']");
    private By AddArabicName = By.cssSelector("input[name='arabicName']");
    private By AddEnglishName = By.cssSelector("input[name='englishName']");
    private By CloseButton = By.xpath("//button[normalize-space()='إغلاق']");
    private By AssertExsistArabicName  = By.xpath("//small[contains(text(),'الاسم العربي مطلوب')]");
    private By AssertRightEmail  = By.xpath("//small[contains(text(),'البريد الالكتروني يجب ان يكون بريدا اليكتروني صحيح')]");
    private By AssertTaxNumber  = By.xpath("//small[contains(text(),'الرقم الضريبي مطلوب')]");
    private By AssertBankNumber  = By.xpath("//small[contains(text(),'الحقل رقم الحساب يجب ان يكون عدداً صحيحاً')]");
    private By AddAdress = By.xpath("//input[@class='form-control' and @id='adress']\n");
    private By PhoneInput = By.cssSelector("input#phone");

    private final By EditableRows = By.xpath("//tbody//tr[@role='row' and .//input[not(@type='hidden')]]");



//الميثودات

    //ميثودات الإضافة
    @Step("إضافة الاسم العربي: {ArabicName}")
    public void AddArabicName(String ArabicName) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(AddArabicName, ArabicName);
    }

    @Step("إضافة الاسم الإنجليزي: {EnglishName}")
    public void AddEnglishName(String EnglishName) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(AddEnglishName, EnglishName);
    }
@Step("اضافه رقم الجول :{PhoneNumber}")
public void AddPhoneNumber(String PhoneNumber) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(PhoneInput, PhoneNumber);
}
    @Step("إضافة القيمة: {value}")
    public void AddValue(String value) {
        utilites.TaypeTextB(Value, value);
    }

    @Step("إضافة المرجع: {reference}")
    public void AddReference(String reference) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(Reference, reference);
    }
@Step("اضافه العنوان: {adress}")
public void AddAdress(String adress) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(AddAdress, adress);
}
    @Step("إضافة ملاحظات: {notes}")
    public void AddNotes(String notes) {
        utilites.TaypeTextB(Notes, notes);
    }

    @Step("إضافة التاريخ: {todayDate}")
    public String AddDate() {
        utilites.waits().waitForOverlayToDisappear();
        String todayDate = utilites.Utility().TypeTodayDate("dd/MM/yyyy");
        utilites.TaypeTextB(AddDate, todayDate);
        utilites.waits().waitForOverlayToDisappear();
        return todayDate;

    }
    public String GetTodayDate() {
        return utilites.Utility().TypeTodayDate("dd/MM/yyyy");
    }
    @Step("إضافة التاريخ العام السابق: {pastYearDate}")
    public void AddPastYearDate() {
        utilites.waits().waitForOverlayToDisappear();
        String pastYearDate = utilites.Utility().TypePastYearDate("dd/MM/yyyy");
        utilites.TaypeTextB(AddDate, pastYearDate);
        utilites.waits().waitForOverlayToDisappear();
    }

    public String generateRandomNumber() {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int digit = (int) (Math.random() * 10);
            number.append(digit);
        }
        return number.toString();
    }

    //ميثودات الضغط
    @Step("الضغط على زر جديد")
    public void ClickNew() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickNew);
        utilites.waits().waitForOverlayToDisappear();

    }

    @Step("الضغط على زر إغلاق")
    public void ClickCloseButton() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(CloseButton);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم الضغط على زر الإغلاق");
    }

    @Step("الضغط على زر الحفظ والطباعة")
    public void ClickSaveAndPrintButton() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(SaveAndPrintButton);
        LogUtils.info("تم الحفظ والطباعة");
        utilites.waits().switchToNewTab();
        LogUtils.info("تم فتح التقرير");
        utilites.waits().waitForReportReady(60);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم ظهور كل بيانات التقرير");
    }

    @Step("الضغط على زر الحفظ")
    public void ClickSaveButton() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(SaveButton);
        LogUtils.info("تم الضغط على زر الحفظ");

    }


    //ميثودات التعديل والحذف
    @Step("الضغط على زر التعديل لآخر سجل في الجريد")
    public void Edit() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(
                utilites.getLastEditButtonForCustomerReceipt()
        );
        utilites.waits().waitForOverlayToDisappear();
       utilites.waits(). waitForExchangeEditPage(SaveButton);

        LogUtils.error("تم الضغط على زر التعديل لآخر سجل في الجريد");
    }


    @Step("الضغط على زر الحذف لآخر سجل في الجريد")
    public void Delete() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.grid().deleteRow(
                utilites.grid().getLastDeleteButton(),
                Clickyestodelet
        );
        LogUtils.info("تم حذف آخر سجل في الجريد");
    }

    @Step("حذف جميع السجلات")
    public void DeleteAll() {
        utilites.grid().deleteAllRows(Clickyestodelet);
        LogUtils.info("تم حذف جميع السجلات في الجريد");
    }


    //ميثودات التحقق
    @Step("التأكد من أن الاسم العربي مطلوب")
    public boolean IsArabicNameRequiredErrorDisplayed() {
        return utilites.isDisplayed(AssertExsistArabicName);
    }

    @Step("التأكد من أن البريد الإلكتروني صحيح")
    public boolean IsRightEmailErrorDisplayed() {
        return utilites.isDisplayed(AssertRightEmail);
    }

    @Step("التأكد من أن الرقم الضريبي مطلوب بعد اختيار Checkbox عميل شركة")
    public boolean IsTaxNumberRequiredErrorDisplayed() {
        return utilites.isDisplayed(AssertTaxNumber);
    }

    @Step("التأكد من وجود الإيصال: {ArabicName}")
    public void AssertReceiptAdded(String ArabicName) {
        utilites.waits().waitForOverlayToDisappear();
        String pk = utilites.grid().getPkByText(ArabicName);
        Assert.assertNotNull(
                pk,
                "❌ السجل غير موجود: " + ArabicName
        );
        LogUtils.info("✅ السجل موجود بـ PK = " + pk);
    }

    public void CloserReport() {
        browserAction.closeCurrentTab();
        browserAction.switchToMainTab();
        LogUtils.info("تم إغلاق التقرير والرجوع للتاب الرئيسي");
    }

    @Step("التأكد من ظهور النص: {Text}")
    public void AssertTextInReport(String Text) {
        Assert.assertTrue(utilites.validation().IsTextDisplayed(Text));

        LogUtils.info("تم التحقق من النص في التقرير: " + Text);
    }

    @Step("التأكد من ظهور الرقم: {Number}")
    public void AssertNumberInReport(String Number) {
        Assert.assertTrue( utilites.validation().IsPriceDisplayedForFin(Number));
        LogUtils.info("تم التحقق من الرقم في التقرير: " + Number);
    }

    @Step("التأكد من ظهور التاريخ: {Date}")
    public void AssertDateInReport(String Date) {
        Assert.assertTrue(utilites.validation().IsDateDisplayed(Date));
        LogUtils.info("تم التحقق من التاريخ في التقرير: " + Date);
    }

    @Step("التأكد من أن أرقام البنك أرقام فقط ولا تحتوي على أحرف")
    public boolean AssertNumberOfBankWithOutLetter() {
        return utilites.isDisplayed(AssertBankNumber);
    }

    @Step("التأكد من أن الـ Checkbox محدد: {checkboxName}")
    public void AssertCheckboxIsChecked(String checkboxName, boolean expectedState) {
        utilites.checkbox().assertCheckboxState(checkboxName, expectedState);
    }
@Step("التحقق ان زرارالحفظ غير قابل للضغط")
public boolean AssertSaveButtonIsNotChecked(){
        return utilites.isDisplayed(SaveButton);
}

    //ميثودات الاختيار
    @Step("الاختيار من القائمة المنسدلة: {Name} من: {Type}")
    public void SelectFromDropDawn(String Name, String Type) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectVueOptionboth(Type, Name);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم اختيار: " + Name);
    }
@Step("الاختيار من القائمة المنسدلة: {Name} من: {Type} باستخدام DataTitle")
    public void SelectFromDropDawnByText(String Name, String Type) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectVueOptionByDataTitle(Name, Type);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم اختيار: " + Name);
    }

    @Step("الاختيار من القائمة المنسدلة: {label} من: {value}")
    public void selectTreasuryHandoverDropdown(String label, String value) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectTreasuryHandoverDropdown(label, value);
    }

    @Step("الاختيار بالأزرار: {option}")
    public void SelectByKeys(String option) {
        actions.sendKeys(Keys.TAB)
                .pause(Duration.ofMillis(500))
                .sendKeys(option)
                .pause(Duration.ofMillis(500))
                .sendKeys(Keys.ENTER)
                .perform();
    }


    @Step("الضغط على انتر لاختيار الصنف")
    public void ClickEnter(){
        actions .pause(Duration.ofMillis(500))
                .sendKeys(Keys.ENTER)
                .perform();
    }

    @Step("الضغط على شيفت للانتقال")
    public void ClickShift(){
        actions .pause(Duration.ofMillis(500))
                .sendKeys(Keys.SHIFT)
                .perform();
    }
    public void clickOnEmptyArea() {
        WebElement body = driver.findElement(By.tagName("body"));

        new Actions(driver)
                .moveToElement(body, 5, 5)
                .click()
                .perform();
    }
    @Step("تعيين قيمة الـ Checkbox: {checkboxName} إلى {value}")
    public void SetCheckboxValue(String checkboxName, boolean value) {
        utilites.checkbox().stableFastSetCheckboxByTextCloude(checkboxName, value);
    }

    @Step("اختيار الـ Checkbox: {checkboxName} إلى {value}")
    public void SelectCheckboxByText(String checkboxName, boolean value) {
        utilites.checkbox().stableFastSetCheckboxByText(checkboxName, value);
    }

    @Step("تغير حالةCheckbox: {checkboxName} إلى {expectedState}")
    public void SmartCheck(String checkboxName, boolean expectedState) {
        utilites.checkbox().setCheckbox(checkboxName, expectedState);
    }


    //ميثودات الـ Popup
    @Step("التحقق من ظهور نافذة الخطأ")
    public boolean isErrorPopupDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".swal2-container.swal2-backdrop-show")
                    ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("التاكد من ظهور بوب اب والضغط على موافق")
    public void AssertPopupAndClickOK() {
        utilites.alert().acceptSwalConfirm();
        LogUtils.info("تم التحقق من ظهور البوب اب والضغط على موافق");
    }

    @Step("التاكد من ظهور بوب اب والضغط على الغاء")
    public void AssertPopupAndClickCancel() {
        utilites.alert().dismissAlert();
        LogUtils.info("تم التحقق من ظهور البوب اب والضغط على الغاء");
    }

    @Step("اضافه اول {count} اصناف من القائمه")
    public void AddNewProductList(int count) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.dropdown().selectFirstProducts(count);
    }

    @Step("الانتقال الى الرابط: {url}")
    public void NavigateTo(String url) {
        browserAction.navigateWithRetry(url);
        LogUtils.info("تم الانتقال الى: " + url);
    }
    @Step("اضافه رابط الصفحه:{route}")
    public void  NavigationHelper(Pages route) {
        utilites.waits().waitForOverlayToDisappear();

        browserAction.OpenNavigationHelper(Pages.valueOf(route.name()));
        utilites.waits().waitForOverlayToDisappear();
        driver.navigate().refresh();
        utilites.waits().waitForOverlayToDisappear();

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



    }

    }





