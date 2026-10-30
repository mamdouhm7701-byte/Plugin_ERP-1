/* شرح الملف: شاشة أنواع الخصومات: بنسجل نسبة الخصم والرمز السري والمستخدمين المسموح لهم. فائدتها
 * تنظيم الخصومات وصلاحية استخدامها.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class TypesofdiscountsPage extends AbstractForDealer {
    // Variables

    // Constructors
    public TypesofdiscountsPage(WebDriver driver) {
        super(driver);
    }

    // locators
    private By OpenBasicData =
            By.xpath(
                    "//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenTypesofdiscounts = By.xpath("//span[normalize-space()='انوع الخصومات']");
    private By AddTypesofdiscounts = By.xpath("//button[contains(text(),'جديد')]");
    private By AddArabicTypesofdiscounts = By.xpath("//input[@id='arabicName']");
    private By AddEnglishTypesofdiscounts = By.xpath("//input[@name='englishName']");
    private By SaveButton =
            By.xpath("//button[@data-action-type='save' and normalize-space(text())='حفظ']\n");
    private By Clickyestodelet =
            By.xpath("//button[contains(@class, \"swal2-confirm\") and contains(text(), \"نعم\")]");
    private By Percentage = By.xpath("//input[@name='discountPercentage']");
    private By SecrtCode = By.xpath("//input[@id='secretCode']");
    private By AddUser = By.xpath("//button[normalize-space()='إضافة مستخدمين']");
    private By CloseButton = By.xpath("//button[normalize-space()='إغلاق']");
    private By AssertRequiredarabicMsg =
            By.xpath(
                    "//small[contains(@class,'text-danger') and normalize-space()='الاسم العربي مطلوب']");
    private By AssertRequiredPercentageMsg =
            By.xpath(
                    "//small[contains(@class,'text-danger') and normalize-space()='نسبة الخصم مطلوب']");
    private By AssertRequiredSecretCodeMsg =
            By.xpath(
                    "//small[contains(@class,'text-danger') and normalize-space()='الكود السري مطلوب']");

    // Methods
    @Step("فتح قائمة الخصومات")
    public void OpenTypesofdiscountsPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.ClickElement(OpenTypesofdiscounts);
        utilites.ClickElement(OpenBasicData);
    }

    @Step("التأكد من أن نسبة الخصم مطلوب")
    public boolean IsPercentageRequiredErrorDisplayed() {
        return utilites.isDisplayed(AssertRequiredPercentageMsg);
    }

    @Step("التأكد من أن الكود السري مطلوب")
    public boolean IsSecretCodeRequiredErrorDisplayed() {
        return utilites.isDisplayed(AssertRequiredSecretCodeMsg);
    }

    @Step("اضافه قيمه الخصم المطلوب:{discountValue}")
    public void AddDiscount(int discountValue) {
        utilites.typeTextAndVerify(Percentage, String.valueOf(discountValue));
    }

    @Step("اضافه الرقم السرى: {secretCode}")
    public void AddSecretCode(String secretCode) {
        utilites.typeTextAndVerify(SecrtCode, secretCode);
    }

    @Step("الضغط على اختيار المستخدمين")
    public void ClickToAddUsers() {
        utilites.ClickElement(AddUser);
    }

    @Step("اختيار المستخدمين:{codes}")
    public void ClickSelectUsers(int... codes) {
        utilites.dropdown().selectUsersByCode(codes);
    }

    private String targetPk;

    @Step(
            "Add New Type Of Discount With ArabicName: {ArabicName},EnglishName: {EnglishName},"
                    + "percentage: {percentage},secretCode: {secretCode}")
    public void AddNewTypesofdiscounts(
            String ArabicName, String EnglishName, int percentage, int secretCode) {
        utilites.ClickElement(AddTypesofdiscounts);
        utilites.typeText(AddArabicTypesofdiscounts, ArabicName);
        utilites.typeText(AddEnglishTypesofdiscounts, EnglishName);
        utilites.typeText(Percentage, String.valueOf(percentage));
        utilites.typeText(SecrtCode, String.valueOf(secretCode));

        utilites.ClickElement(AddUser);
        utilites.dropdown().selectUsersByCode(1, 2);
        utilites.ClickElement(CloseButton);
        utilites.ClickElement(SaveButton);
        targetPk = utilites.grid().getPkByText(ArabicName);
    }

    @Step(
            "Edit New Type Of Discount With ArabicName: {ArabicName},EnglishName: {EnglishName},"
                    + "percentage: {percentage},secretCode: {secretCode}")
    public void EditTypesofdiscounts(
            String ArabicName, String EnglishName, int percentage, int secretCode) {
        utilites.ClickElement(utilites.grid().getLastEditButton());
        utilites.typeText(AddArabicTypesofdiscounts, ArabicName);
        utilites.typeText(AddEnglishTypesofdiscounts, EnglishName);
        utilites.typeText(Percentage, String.valueOf(percentage));
        utilites.typeText(SecrtCode, String.valueOf(secretCode));
        utilites.checkbox().stableFastSetCheckboxByTextCloude("فعال", false);
        utilites.ClickElement(AddUser);
        utilites.dropdown().selectUsersByCode(1, 2);
        utilites.ClickElement(CloseButton);
        utilites.checkbox().stableFastSetCheckboxByText("التأثير في الفواتير المعلقة", true);
        utilites.ClickElement(SaveButton);
    }

    @Step("Add Invalid Type Of Discount : ArabicName: {ArabicName},EnglishName: {EnglishName}")
    public void InvalidAddNewTypesofdiscounts(String ArabicName, String EnglishName) {
        utilites.ClickElement(AddTypesofdiscounts);
        utilites.typeText(AddArabicTypesofdiscounts, ArabicName);
        utilites.typeText(AddEnglishTypesofdiscounts, EnglishName);

        utilites.ClickElement(SaveButton);
    }

    @Step("Assert Required Fields Messages Displayed")
    public void AssertRequiredFieldsMessages() {
        utilites.validation().assertRequiredMsg(AssertRequiredarabicMsg, "الاسم العربي مطلوب");
        utilites.validation().assertRequiredMsg(AssertRequiredPercentageMsg, "نسبة الخصم مطلوب");
        utilites.validation().assertRequiredMsg(AssertRequiredSecretCodeMsg, "الكود السري مطلوب");
    }

    @Step("Delete Types of discounts")
    public void DeleteTypesofdiscounts() {
        String deletedPk = utilites.grid().getLastRowPk();

        By deletedRow = By.xpath("//tr[@data-pk='" + deletedPk + "']");

        System.out.println("🗑️ Start deleting row with pk: " + deletedPk);

        utilites.ClickElement(utilites.grid().getLastDeleteButton());
        utilites.ClickElement(Clickyestodelet);

        long startTime = System.currentTimeMillis();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBe(
                        deletedRow, 0));

        long endTime = System.currentTimeMillis();

        System.out.println("✅ Row deleted successfully");
        System.out.println("⏱️ Delete wait time: " + ((endTime - startTime) / 1000.0) + " seconds");

        Assert.assertTrue(
                driver.findElements(deletedRow).isEmpty(),
                "❌ الصف لم يُحذف! data-pk: " + deletedPk);
    }

    @Step("Assert Types of discounts Exists: {ArabicName}")
    public void AssertTypesofdiscountsExists(String ArabicName) {
        String actualPk = utilites.grid().getPkByText(ArabicName);
        if (actualPk == null) {
            throw new AssertionError(
                    "Expected Expenses with Arabic name '"
                            + ArabicName
                            + "' to exist, but it was not found.");
        }
    }

    private org.openqa.selenium.WebElement ownedRow(String name) {
        return new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(
                        d -> {
                            java.util.List<org.openqa.selenium.WebElement> matches =
                                    d.findElements(By.cssSelector("tr[data-pk]")).stream()
                                            .filter(r -> r.isDisplayed())
                                            .filter(
                                                    r ->
                                                            r
                                                                    .findElements(
                                                                            By.cssSelector("td"))
                                                                    .stream()
                                                                    .anyMatch(
                                                                            c ->
                                                                                    c.getText()
                                                                                            .trim()
                                                                                            .equals(
                                                                                                    name)))
                                            .toList();
                            if (matches.size() > 1)
                                throw new AssertionError("Ambiguous discount name: " + name);
                            return matches.size() == 1 ? matches.get(0) : null;
                        });
    }

    @Step("فتح السجل {name} للتعديل")
    public void OpenOwnedForEdit(String name) {
        ownedRow(name).findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        org.openqa.selenium.support.ui.ExpectedConditions
                                .visibilityOfElementLocated(SecrtCode));
    }

    @Step("حذف السجل {name}")
    public void DeleteOwned(String name) {
        org.openqa.selenium.WebElement row = ownedRow(name);
        String pk = row.getAttribute("data-pk");
        row.findElement(By.cssSelector("button[data-action-type='delete']")).click();
        utilites.ClickElement(Clickyestodelet);
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .until(
                        d ->
                                d.findElements(By.cssSelector("tr[data-pk]")).stream()
                                        .noneMatch(r -> pk.equals(r.getAttribute("data-pk"))));
    }
}
