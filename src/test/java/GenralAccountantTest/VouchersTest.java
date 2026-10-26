package GenralAccountantTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import TestSupport.BasicDataScenarioTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VouchersTest extends BasicDataScenarioTest {

    private String accountCode(String key) {
        return basicData().account(key).values().get("code").toString();
    }

    @io.qameta.allure.Step("فتح نموذج قيد يومية جديد")
    private VouchersPage openNewVoucher() {
        VouchersPage page = new VouchersPage(driver);
        page.NavigationHelper(Pages.VOUCHERS);
        driver.findElement(By.xpath("//button[normalize-space()='جديد']")).click();
        waitForVoucherForm();
        return page;
    }

    private void waitForVoucherForm() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(d -> !d.findElements(By.cssSelector("form #textarea")).isEmpty());
        new VouchersPage(driver).waitForFormReady();
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("فتح شاشة القيود اليومية")
    @Description("يفتح قائمة القيود اليومية دون تعديل أي سجل.")
    public void OpenVouchersPageTest() {
        new VouchersPage(driver).NavigationHelper(Pages.VOUCHERS);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/vouchers/"),
                "The vouchers page URL should contain /vouchers/.");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إظهار تاريخ القيد")
    @Description(
            "يتحقق من أن نموذج القيد الجديد يعرض تاريخًا ابتدائيًا؛ لا يختبر حفظ التاريخ أو"
                    + " إعادة فتحه.")
    public void VoucherDateIsPrepopulatedTest() {
        VouchersPage page = openNewVoucher();

        Assert.assertFalse(
                page.getVoucherDate().isBlank(), "A default voucher date should be displayed.");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إتاحة الحفظ والطباعة")
    @Description("يتحقق من ظهور أزرار الحفظ والطباعة في نموذج القيد الجديد دون الضغط عليها.")
    public void SaveActionsAvailableTest() {
        VouchersPage page = openNewVoucher();

        Assert.assertTrue(page.isSaveAvailable(), "The Save action should be available.");
        Assert.assertTrue(
                page.isSaveAndPrintAvailable(), "The Save and Print action should be available.");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("منع حفظ قيد فارغ")
    @Description(
            "يحاول حفظ النموذج الفارغ ويتأكد من بقائه مفتوحًا وغير محفوظ؛ واجهة الديمو لا"
                    + " تعرض رسالة تحقق مرئية.")
    public void RejectEmptyVoucherTest() {
        VouchersPage page = openNewVoucher();
        page.setNotes(ownedName("Vouchers", "invalid-empty"));
        page.ClickSaveButton();

        Assert.assertTrue(
                page.isVoucherFormDisplayed(),
                "The empty voucher must remain unsaved in the entry form.");
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("حفظ الملاحظات في نموذج القيد")
    @Description("يتحقق من إدخال الملاحظات العربية والإنجليزية والأرقام في مسودة القيد دون حفظها.")
    public void NotesAcceptArabicEnglishAndNumbersTest() {
        VouchersPage page = openNewVoucher();
        String notes = "مراجعة Test 123";
        page.setNotes(notes);

        Assert.assertEquals(page.getNotes(), notes);
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("اختيار الحساب بالبحث عن الكود")
    @Description("يفتح نافذة الحسابات ويبحث بكود معروف دون إضافة الحساب إلى القيد أو حفظه.")
    public void SearchAccountByCodeTest() {
        basicData().account("search");

        VouchersPage page = openNewVoucher();
        page.openAccountPicker();
        page.searchAccount(accountCode("search"));

        Assert.assertTrue(
                driver.findElement(By.cssSelector("div[role='dialog'] table tbody"))
                        .getText()
                        .contains(accountCode("search")),
                "The account search should show the matching code.");

        page.closeAccountPicker();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("البحث في الحسابات بالاسم")
    @Description("يبحث عن حساب باسمه في نافذة الحسابات دون اختياره أو حفظ أي شيء.")
    public void SearchAccountByNameTest() {
        basicData().account("search-name");

        VouchersPage page = openNewVoucher();
        page.openAccountPicker();
        page.searchAccount(basicData().account("search-name").name());

        Assert.assertTrue(
                driver.findElement(By.cssSelector("div[role='dialog'] table tbody"))
                        .getText()
                        .contains(basicData().account("search-name").name()),
                "The account search should show the matching name.");

        page.closeAccountPicker();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("البحث عن حساب غير موجود")
    @Description("يتحقق من أن البحث عن نص غير مطابق لا يعرض حسابات؛ لا يغير البيانات.")
    public void SearchAccountWithoutResultsTest() {
        VouchersPage page = openNewVoucher();
        page.openAccountPicker();
        page.searchAccount("__NO_ACCOUNT_MATCH__");

        Assert.assertEquals(
                page.getAccountSearchResultCount(),
                0,
                "An unmatched search should not return account rows.");

        page.closeAccountPicker();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("اختيار حساب من نافذة البحث")
    @Description("يختار حسابًا موجودًا من نافذة الحسابات ويتحقق من إضافته إلى المسودة دون حفظها.")
    public void SelectAccountFromSearchTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));

        Assert.assertEquals(page.getSelectedLineCount(), 1);
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("اختيار حساب وإغلاق نافذة الاختيار")
    @Description(
            "يضيف حسابًا إلى مسودة القيد، ثم يفتح نافذة الحسابات ويغلقها دون اختيار آخر"
                    + " ويتحقق من بقاء البند.")
    public void CloseAccountPickerKeepsSelectedLineTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.openAccountPicker();
        page.closeAccountPicker();

        Assert.assertEquals(
                page.getSelectedLineCount(),
                1,
                "Closing the account picker should preserve the selected draft line.");
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إجماليات المبالغ الصفرية")
    @Description(
            "يتحقق من أن إجماليات المسودة تظل صفرًا عند ترك المبالغ دون إدخال؛ لا يستنتج"
                    + " قاعدة السماح بالحفظ.")
    public void ZeroAmountsKeepZeroDraftTotalsTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));

        page.assertTotals("0", "0");
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إضافة بند دون تغيير البنود السابقة")
    @Description("يتحقق من بقاء إجمالي البند السابق كما هو بعد إضافة بند جديد إلى المسودة.")
    public void AddingLinePreservesPreviousValuesTest() {
        basicData().account("debit");
        basicData().account("extra");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.setDebit(1, "125.50");
        page.selectAccountByCode(accountCode("extra"));

        Assert.assertEquals(page.getSelectedLineCount(), 2);
        page.assertTotals("125.50", "0");
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("التحقق من توازن مسودة القيد")
    @Description(
            "يضيف حسابين إلى المسودة ويدخل 1000 مدينًا و1000 دائنًا ويتحقق من الإجماليات دون"
                    + " إنشاء سجل محفوظ.")
    public void BalancedEntryTotalsTest() {
        basicData().account("debit");
        basicData().account("credit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.selectAccountByCode(accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "1000");

        page.assertTotals("1000", "1000");
        Assert.assertTrue(page.isBalanced(), "Equal debit and credit totals should be balanced.");
    }

    @Test(priority = 14)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("التحقق من عدم اتزان المسودة")
    @Description(
            "يتحقق من اختلاف إجمالي المدين عن الدائن عندما تكون المسودة 1000 مقابل 900، دون"
                    + " الضغط على الحفظ.")
    public void UnbalancedEntryTotalsTest() {
        basicData().account("debit");
        basicData().account("credit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.selectAccountByCode(accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "900");

        page.assertTotals("1000", "900");
        Assert.assertFalse(
                page.isBalanced(), "An entry with unequal totals must be reported as unbalanced.");
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إعادة احتساب الاتزان بعد تعديل مبلغ")
    @Description("يعدل مبلغ المدين بعد إدخاله ويتحقق من إعادة احتساب الإجماليات؛ لا يحفظ القيد.")
    public void RecalculateBalanceAfterAmountEditTest() {
        basicData().account("debit");
        basicData().account("credit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.selectAccountByCode(accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "1000");
        Assert.assertTrue(page.isBalanced());

        page.setDebit(1, "900");

        page.assertTotals("900", "1000");
        Assert.assertFalse(page.isBalanced());
    }

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("قيد متعدد البنود ودقة الكسور")
    @Description("يتحقق من جمع أكثر من بند ومن احتساب الكسور العشرية في المسودة دون حفظها.")
    public void MultiLineDecimalTotalsTest() {
        basicData().account("debit");
        basicData().account("extra");
        basicData().account("credit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.selectAccountByCode(accountCode("extra"));
        page.selectAccountByCode(accountCode("credit"));
        page.setDebit(1, "100.25");
        page.setDebit(2, "25.25");
        page.setCredit(3, "125.50");

        page.assertTotals("125.50", "125.50");
        Assert.assertTrue(page.isBalanced());
    }

    @Test(priority = 17)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("حذف بند من مسودة القيد")
    @Description("يحذف بندًا من المسودة ويتحقق من تحديث عدد البنود والإجماليات دون حفظ القيد.")
    public void RemoveLineUpdatesDraftTotalsTest() {
        basicData().account("debit");
        basicData().account("credit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.selectAccountByCode(accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "1000");
        page.removeLine(1);

        Assert.assertEquals(page.getSelectedLineCount(), 1);
        page.assertTotals("0", "1000");
    }

    @Test(priority = 18)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-007 حقل المبلغ الرقمي")
    @Description(
            "يفحص أن حقلي المدين والدائن يستخدمان مدخلات رقمية؛ لا يثبت وحده قاعدة منع المبلغ"
                    + " السالب.")
    public void AmountFieldsAreNumericTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        By debit =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])[1]//td[@aria-colindex='3']//input");
        By credit =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])[1]//td[@aria-colindex='4']//input");

        Assert.assertTrue(page.isNumericAmountInput(debit));
        Assert.assertTrue(page.isNumericAmountInput(credit));
    }

    @Test(priority = 19)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-009 إدخال المدين والدائن في البند نفسه")
    @Description("يوثق سلوك جمع المدين والدائن في البند نفسه على مستوى المسودة دون حفظ.")
    public void SameLineDebitCreditDraftTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.setDebit(1, "100");
        page.setCredit(1, "100");

        Assert.assertEquals(page.getDebitValue(1), "100");
        Assert.assertEquals(page.getCreditValue(1), "100");
        page.assertTotals("100", "100");
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-016 اختيار حساب من القائمة المنسدلة")
    @Description("يختار حسابًا من قائمة الحسابات داخل صف القيد ويتحقق من إضافته للمسودة.")
    public void SelectAccountFromLineDropdownTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.selectAccountFromLineDropdown(1, "سيارة رقم 1");

        Assert.assertTrue(page.isLineAccountSelected(1));
    }

    @Test(priority = 21)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-028 فحص التاريخ الافتراضي")
    @Description("يتحقق من ظهور تاريخ ابتدائي صالح وفق المتصفح دون إدخال تاريخ مخالف أو حفظ القيد.")
    public void VoucherDateFormatTest() {
        VouchersPage page = openNewVoucher();

        Assert.assertFalse(page.getVoucherDate().isBlank());
        Assert.assertTrue(page.isVoucherDateValid());
    }

    @Test(priority = 22)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-032 حفظ ملاحظة البند")
    @Description("يتحقق من إدخال الملاحظات العربية والإنجليزية والأرقام في صف المسودة.")
    public void LineNotesAcceptMixedTextTest() {
        basicData().account("debit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));
        page.setLineNotes(1, "بند Test 123");

        Assert.assertEquals(page.getLineNotes(1), "بند Test 123");
    }

    @Test(
            priority = 23,
            enabled = true,
            description =
                    "Search account choices inside a voucher line and preserve the existing lines")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-040 البحث في خيارات حساب البند")
    @Description(
            "يختار حسابين ثم يبحث داخل خيارات حساب البند، ويتأكد من ظهور الحساب المطابق"
                    + " وبقاء البندين الموجودين دون تغيير.")
    public void AccountSearchAvailableInLinesTest() {
        basicData().account("debit");
        basicData().account("credit");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("debit"));

        Assert.assertTrue(page.isAccountDropdownSearchPresent());
        page.selectAccountByCode(accountCode("credit"));
        page.assertLineAccountSearch(
                basicData().account("debit").name(), basicData().account("credit").name());
        Assert.assertEquals(
                page.getSelectedLineCount(),
                2,
                "Searching account choices must preserve both existing voucher lines.");
    }

    @Test(priority = 24)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-020 اختبار علامة تسوية الضريبة")
    @Description(
            "يتحقق من إمكانية تحديد وإلغاء علامة تسوية الضريبة فقط دون حفظ القيد أو فحص أثرها"
                    + " اللاحق.")
    public void TaxSettlementTest() {
        basicData().account("tax");

        VouchersPage page = openNewVoucher();
        page.selectAccountByCode(accountCode("tax"));
        Assert.assertTrue(
                page.getSettlementCheckboxCount() > 0,
                "The voucher UI should expose the tax-settlement checkbox.");
        page.setSettlementChecked(true);
        Assert.assertTrue(page.isSettlementChecked(1));
        page.setSettlementChecked(false);
        Assert.assertFalse(page.isSettlementChecked(1));
    }

    @Test(
            priority = 25,
            description =
                    "Submits a uniquely marked unbalanced test voucher, confirms it was not saved, and cleans it up if the application accepts it.")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-002 رفض حفظ القيد غير المتزن")
    @Description(
            "يرسل مدينًا 1000 ودائنًا 900 على قيد اختبار بعلامة فريدة؛ يتحقق من رفض الحفظ أو"
                    + " يحذف القيد فورًا إن قبله النظام.")
    public void RejectUnbalancedVoucherOnSaveTest() {
        basicData().account("debit");
        basicData().account("credit");

        String marker = ownedName("Vouchers", "unbalanced");
        VouchersPage page = openNewVoucher();
        boolean markerExists = false;
        boolean unexpectedlySaved = false;
        try {
            page.selectAccountByCode(accountCode("debit"));
            page.selectAccountByCode(accountCode("credit"));
            page.setDebit(1, "1000");
            page.setCredit(2, "900");
            page.setNotes(marker);
            page.ClickSaveButton();
            page.NavigationHelper(Pages.VOUCHERS);
            markerExists = page.hasVoucherMarkerInList(marker);
            unexpectedlySaved = markerExists;
            if (markerExists) {
                page.deleteVoucherListRowByMarker(marker);
                markerExists = false;
            }
            Assert.assertFalse(
                    unexpectedlySaved,
                    "An unbalanced voucher must not persist; any accepted test record is deleted immediately.");
        } finally {
            if (markerExists) {
                page.NavigationHelper(Pages.VOUCHERS);
                if (page.hasVoucherMarkerInList(marker)) page.deleteVoucherListRowByMarker(marker);
            }
        }
    }

    @Test(
            priority = 26,
            description =
                    "Submits a zero-value marked voucher and deletes it immediately if accepted.")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-006 منع القيد ذي المبالغ الصفرية")
    @Description(
            "يحاول حفظ قيد صفري بعلامة فريدة، ويتأكد أنه لم يظهر بالقائمة ويحذفه فورًا إن"
                    + " قبله النظام.")
    public void RejectZeroValueVoucherOnSaveTest() {
        basicData().account("debit");

        assertMarkedVoucherRejected(
                "ZERO",
                page -> {
                    page.selectAccountByCode(accountCode("debit"));
                });
    }

    @Test(
            priority = 27,
            description =
                    "Submits a balanced signed-value marked voucher and deletes it immediately if accepted.")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-007 رفض المبلغ السالب عند التحقق")
    @Description(
            "يحاول حفظ مدين -100 ودائن -100 بقيد اختبار مميز، ويتأكد من رفضه أو يحذفه فورًا"
                    + " إن قبله النظام.")
    public void RejectNegativeJournalAmountTest() {
        basicData().account("debit");
        basicData().account("credit");

        assertMarkedVoucherRejected(
                "NEGATIVE",
                page -> {
                    page.selectAccountByCode(accountCode("debit"));
                    page.selectAccountByCode(accountCode("credit"));
                    page.setDebit(1, "-100");
                    page.setCredit(2, "-100");
                });
    }

    private void assertMarkedVoucherRejected(
            String scenario, java.util.function.Consumer<VouchersPage> setup) {
        String marker = ownedName("Vouchers", scenario);
        VouchersPage page = openNewVoucher();
        boolean wasPersisted = false;
        boolean unexpectedlySaved = false;
        try {
            setup.accept(page);
            page.setNotes(marker);
            page.ClickSaveButton();
            page.NavigationHelper(Pages.VOUCHERS);
            wasPersisted = page.hasVoucherMarkerInList(marker);
            unexpectedlySaved = wasPersisted;
            if (wasPersisted) {
                page.deleteVoucherListRowByMarker(marker);
                wasPersisted = false;
            }
            Assert.assertFalse(
                    unexpectedlySaved,
                    "A "
                            + scenario
                            + " voucher must be rejected; any accepted test record is deleted immediately.");
        } finally {
            if (wasPersisted) {
                page.NavigationHelper(Pages.VOUCHERS);
                if (page.hasVoucherMarkerInList(marker)) page.deleteVoucherListRowByMarker(marker);
            }
        }
    }

    @Test(priority = 28)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("تنظيف قيود الاختبار")
    @Description("يتحقق من عدم بقاء أي قيد تجريبي من الحالات التي تنظف بياناتها تلقائيًا.")
    public void NoMarkedValidationVouchersRemainTest() {
        basicData().collectUi();
        basicData().close();
    }

    @Test(
            priority = 29,
            description =
                    "Creates one uniquely marked test voucher, checks save/list/report/edit, then deletes that exact voucher.")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("دورة حفظ وطباعة وتعديل وحذف قيد اختباري")
    @Description(
            "ينشئ قيدًا متزنًا على الحسابات الموجودة، ويتحقق من الأرقام في الشاشة والتقرير"
                    + " وإعادة الفتح ثم يحذف قيد الاختبار المحدد بالعلامة الفريدة.")
    public void SavePrintEditAndDeleteTestVoucher() {
        basicData().account("debit");
        basicData().account("credit");
        basicData().account("extra");

        String marker = ownedName("Vouchers", "saved-entry");
        VouchersPage page = new VouchersPage(driver);
        boolean saved = false;
        try {
            page = openNewVoucher();
            String originalDate = page.getVoucherDate();
            page.selectAccountByCode(accountCode("debit"));
            page.selectAccountByCode(accountCode("credit"));
            page.selectAccountByCode(accountCode("extra"));
            page.setDebit(1, "125.50");
            page.setCredit(2, "125.50");
            Assert.assertEquals(page.getSelectedLineCount(), 3);
            page.removeLine(3);
            Assert.assertEquals(page.getSelectedLineCount(), 2);
            page.setNotes(marker);
            page.assertTotals("125.50", "125.50");
            page.ClickSaveButton();
            saved = true;

            page.NavigationHelper(Pages.VOUCHERS);
            Assert.assertTrue(
                    page.hasVoucherMarkerInList(marker),
                    "Saved voucher should appear in the list.");
            page.editVoucherListRowByMarker(marker);
            Assert.assertEquals(
                    page.getVoucherDate(),
                    originalDate,
                    "The default voucher date should persist when the saved entry is reopened.");
            Assert.assertEquals(page.getNotes(), marker);
            Assert.assertEquals(
                    new BigDecimal(page.getDebitValue(1)).compareTo(new BigDecimal("125.50")), 0);
            Assert.assertEquals(
                    new BigDecimal(page.getCreditValue(2)).compareTo(new BigDecimal("125.50")), 0);

            page.setDebit(1, "150.75");
            page.setCredit(2, "150.75");
            page.ClickSaveAndPrintButton();
            String report = page.getVoucherReportText();
            Assert.assertTrue(
                    report.contains("150.75"), "Print report should reflect edited amount.");
            driver.close();
            driver.switchTo().window(driver.getWindowHandles().iterator().next());

            page.NavigationHelper(Pages.VOUCHERS);
            Assert.assertTrue(page.hasVoucherMarkerInList(marker));
            page.deleteVoucherListRowByMarker(marker);
            saved = false;
        } finally {
            if (saved) {
                try {
                    if (driver.getWindowHandles().size() > 1) {
                        driver.close();
                        driver.switchTo().window(driver.getWindowHandles().iterator().next());
                    }
                    page.NavigationHelper(Pages.VOUCHERS);
                    if (page.hasVoucherMarkerInList(marker)) {
                        page.deleteVoucherListRowByMarker(marker);
                    }
                } catch (Exception cleanupFailure) {
                    throw new AssertionError(
                            "Could not clean up the test voucher marked " + marker, cleanupFailure);
                }
            }
        }
    }

    @Test(
            priority = 30,
            description =
                    "Submits a balanced voucher with both values on one line and deletes it if accepted.")
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-009 قاعدة المدين والدائن في البند نفسه")
    @Description(
            "يحاول حفظ بند مدين 100 ودائن 100 بعلامة فريدة، ويتأكد من رفضه أو يحذفه فورًا إن"
                    + " قبله النظام.")
    public void RejectDebitAndCreditOnSameLineTest() {
        basicData().account("debit");

        assertMarkedVoucherRejected(
                "SAME-LINE",
                page -> {
                    page.selectAccountByCode(accountCode("debit"));
                    page.setDebit(1, "100");
                    page.setCredit(1, "100");
                });
    }
}
