package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BankPage extends AbstractForDealer {
  private final By rows = By.cssSelector("table tbody tr[role='row']");
  private final By headers = By.cssSelector("table thead th");
  private final By search = By.cssSelector("input[placeholder*='بحث']");
  private final By save = By.cssSelector("button[data-action-type='save']");

  public BankPage(WebDriver driver) {
    super(driver);
  }

  private boolean visible(By locator) {
    return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
  }

  private void waitRows() {
    new WebDriverWait(driver, Duration.ofSeconds(20))
        .until(
            d ->
                d
                    .findElements(
                        By.cssSelector(
                            "div.b-overlay, div.position-absolute.bg-light.rounded-lg, .loading-bg,"
                                + " .spinner-border"))
                    .stream()
                    .noneMatch(
                        e -> {
                          try {
                            return e.isDisplayed();
                          } catch (Exception ignored) {
                            return false;
                          }
                        }));
  }

  @Override
  public void ClickCloseButton() {
    if (visible(By.xpath("//button[normalize-space()='إغلاق']"))) super.ClickCloseButton();
  }

  @Override
  public void ClickNew() {
    waitRows();
    super.ClickNew();
    new WebDriverWait(driver, Duration.ofSeconds(15))
        .until(ExpectedConditions.visibilityOfElementLocated(save));
  }

  @Override
  public void ClickSaveButton() {
    super.ClickSaveButton();
    By success =
        By.xpath(
            "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحفظ')]");
    try {
      new WebDriverWait(driver, Duration.ofSeconds(3))
          .until(ExpectedConditions.visibilityOfElementLocated(success));
      new WebDriverWait(driver, Duration.ofSeconds(10))
          .until(
              ExpectedConditions.invisibilityOfElementLocated(
                  By.cssSelector(".swal2-container.swal2-backdrop-show")));
    } catch (org.openqa.selenium.TimeoutException ignored) {
      /* Validation errors do not show success. */
    }
    waitRows();
    if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) {
      new WebDriverWait(driver, Duration.ofSeconds(15))
          .until(ExpectedConditions.visibilityOfElementLocated(search));
      waitRows();
    }
  }

  @Step("البحث في قائمة البنوك")
  public void SearchBank(String value) {
    waitRows();
    By closeForm = By.xpath("//button[normalize-space()='إغلاق']");
    if (visible(closeForm)) ClickCloseButton();
    waitAlertToDisappear();
    WebElement input =
        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(ExpectedConditions.elementToBeClickable(search));
    ((org.openqa.selenium.JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView({block:'center'});", input);
    new org.openqa.selenium.interactions.Actions(driver).moveToElement(input).click().perform();
    input.clear();
    if (value != null && !value.isBlank()) input.sendKeys(value);
    input.sendKeys(Keys.ENTER);
    waitRows();
  }

  private void waitAlertToDisappear() {
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(
            d ->
                d.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show")).stream()
                    .noneMatch(
                        e -> {
                          try {
                            return e.isDisplayed();
                          } catch (Exception ignored) {
                            return false;
                          }
                        }));
  }

  private void dismissSaveSuccessIfPresent() {
    By popup =
        By.xpath(
            "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحفظ')]");
    try {
      new WebDriverWait(driver, Duration.ofSeconds(2))
          .until(ExpectedConditions.visibilityOfElementLocated(popup));
      new WebDriverWait(driver, Duration.ofSeconds(5))
          .until(
              d -> {
                List<WebElement> buttons =
                    d.findElements(
                        By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"));
                if (buttons.isEmpty()) return false;
                try {
                  ((org.openqa.selenium.JavascriptExecutor) d)
                      .executeScript("arguments[0].click();", buttons.get(0));
                  return true;
                } catch (Exception ignored) {
                  return false;
                }
              });
      waitAlertToDisappear();
    } catch (org.openqa.selenium.TimeoutException ignored) {
    }
  }

  @Step("التحقق من بنية قائمة البنوك")
  public boolean IsBankListStructureDisplayed() {
    List<String> text =
        driver.findElements(headers).stream()
            .filter(WebElement::isDisplayed)
            .map(e -> e.getText().trim())
            .toList();
    return text.stream().anyMatch(v -> v.contains("الكود"))
        && text.stream().anyMatch(v -> v.contains("اسم البنك"))
        && text.stream().anyMatch(v -> v.contains("رقم الحساب"))
        && driver.findElements(rows).stream()
            .filter(WebElement::isDisplayed)
            .anyMatch(
                r ->
                    !r.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty()
                        && !r.findElements(By.cssSelector("button[data-action-type='delete']"))
                            .isEmpty());
  }

  @Step("عناصر نموذج البنك غير الظاهرة")
  public String GetMissingBankFormControls() {
    List<String> missing = new java.util.ArrayList<>();
    if (!visible(By.cssSelector("input[name='code']"))
        && !visible(By.xpath("//*[normalize-space()='الكود']/following::input[1]")))
      missing.add("الكود");
    if (!visible(By.cssSelector("input[name='arabicName']"))) missing.add("الاسم العربي");
    if (!visible(By.cssSelector("input[name='englishName']"))) missing.add("الاسم الإنجليزي");
    if (!visible(By.cssSelector("input#adress"))) missing.add("العنوان");
    if (!visible(By.cssSelector("input#phone"))) missing.add("الهاتف");
    if (!visible(By.cssSelector("input[name='email']"))) missing.add("البريد الإلكتروني");
    if (!visible(By.cssSelector("input[name='accountNumber']"))) missing.add("رقم الحساب");
    if (!visible(By.cssSelector("input[name='iban']"))) missing.add("IBAN");
    if (!visible(By.xpath("//textarea[@label='Notes']"))) missing.add("الملاحظات");
    if (!visible(save)) missing.add("حفظ");
    if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) missing.add("إغلاق");
    return String.join("، ", missing);
  }

  @Step("عدد صفوف البنوك القابلة للتعديل")
  public int GetEditableBankCount() {
    return (int)
        driver.findElements(rows).stream()
            .filter(WebElement::isDisplayed)
            .filter(
                r -> !r.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty())
            .count();
  }

  private WebElement findRow(String name) {
    return new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(
            d ->
                d.findElements(rows).stream()
                    .filter(WebElement::isDisplayed)
                    .filter(
                        r ->
                            r.findElements(By.cssSelector("td")).stream()
                                .anyMatch(c -> c.getText().trim().equals(name)))
                    .findFirst()
                    .orElse(null));
  }

  @Step("عدد البنوك المطابقة بالاسم الدقيق")
  public int GetExactBankMatchCount(String name) {
    return (int)
        driver.findElements(rows).stream()
            .filter(WebElement::isDisplayed)
            .filter(
                r ->
                    r.findElements(By.cssSelector("td")).stream()
                        .anyMatch(c -> c.getText().trim().equals(name)))
            .count();
  }

  private int columnIndex(String label) {
    List<WebElement> list = driver.findElements(headers);
    for (int i = 0; i < list.size(); i++) if (list.get(i).getText().contains(label)) return i;
    throw new IllegalStateException("Bank column not found: " + label);
  }

  @Step("قراءة أول خلية من عمود البنك")
  public String GetFirstBankCellByHeader(String label) {
    int i = columnIndex(label);
    WebElement row =
        driver.findElements(rows).stream()
            .filter(WebElement::isDisplayed)
            .filter(
                r -> !r.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No editable Bank row"));
    return row.findElements(By.cssSelector("td")).get(i).getText().trim();
  }

  public void OpenFirstBankForEdit() {
    WebElement row =
        driver.findElements(rows).stream()
            .filter(WebElement::isDisplayed)
            .filter(
                r -> !r.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No editable Bank row"));
    openEdit(row);
  }

  @Step("فتح البنك المطابق بالاسم للتعديل")
  public void OpenBankForEditByName(String name) {
    openEdit(findRow(name));
  }

  private void openEdit(WebElement row) {
    row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.visibilityOfElementLocated(save));
  }

  private String value(By locator) {
    return driver.findElement(locator).getAttribute("value").trim();
  }

  public String GetArabicNameValue() {
    return value(By.cssSelector("input[name='arabicName']"));
  }

  public String GetEnglishNameValue() {
    return value(By.cssSelector("input[name='englishName']"));
  }

  public String GetEmailValue() {
    return value(By.cssSelector("input[name='email']"));
  }

  public String GetAddressValue() {
    return value(By.cssSelector("input#adress"));
  }

  public String GetBankAccountValue() {
    return value(By.cssSelector("input[name='accountNumber']"));
  }

  public String GetIbanValue() {
    return value(By.cssSelector("input[name='iban']"));
  }

  public String GetBankCodeValue() {
    return value(By.cssSelector("input[name='code']"));
  }

  public String GetNotesValue() {
    return driver.findElement(By.xpath("//textarea[@label='Notes']")).getAttribute("value");
  }

  public boolean IsBankArabicNameRequiredDisplayed() {
    return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
        || driver.findElements(By.cssSelector("input[name='arabicName'].is-invalid")).stream()
            .anyMatch(WebElement::isDisplayed);
  }

  public boolean IsInvalidBankEmailDisplayed() {
    return visible(
            By.xpath(
                "//*[contains(normalize-space(),'البريد الالكتروني') and (contains(.,'صحيح') or"
                    + " contains(.,'صالح'))]"))
        || driver.findElements(By.cssSelector("input[name='email'].is-invalid")).stream()
            .anyMatch(WebElement::isDisplayed);
  }

  @Step("طلب حذف البنك المملوك للاختبار بالاسم المطابق")
  public void RequestDeleteBankByName(String name) {
    findRow(name).findElement(By.cssSelector("button[data-action-type='delete']")).click();
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup")));
  }

  public boolean IsBankDeleteConfirmationDisplayed() {
    return visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
        && visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"));
  }

  @Step("تأكيد حذف بنك الاختبار")
  public void ConfirmBankDelete() {
    waitAlertToDisappear();
    WebElement confirm =
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm")));
    confirm.click();
    waitRows();
    By success =
        By.xpath(
            "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحذف')]");
    try {
      new WebDriverWait(driver, Duration.ofSeconds(3))
          .until(ExpectedConditions.visibilityOfElementLocated(success));
      dismissSaveSuccessIfPresent();
    } catch (org.openqa.selenium.TimeoutException ignored) {
    }
  }

  public void CancelBankDelete() {
    driver
        .findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
        .click();
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(
            ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".swal2-container.swal2-backdrop-show")));
  }
}
