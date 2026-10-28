/* شرح الملف: اختبارات دورة حفظ وتعديل وطباعة وحذف القيد نفسه: دورة حفظ وتعديل وطباعة وحذف القيد
 * نفسه؛ حذف سطر يعيد حساب إجمالي القيد؛ البحث في خيارات حساب البند؛ قيد غير متوازن يرفض عند الحفظ؛
 * تحديد وإلغاء علامة تسوية الضريبة.
 */
package GenralAccountantTest;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

public class VouchersTest extends VouchersCaseSupport {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("فتح شاشة القيود اليومية")
    @Description("يفتح قائمة القيود اليومية دون تعديل أي سجل.")
    public void OpenVouchersPageTest() {
        new VouchersFormsChecks(this).OpenVouchersPageTest();
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
        new VouchersSavingChecks(this).VoucherDateIsPrepopulatedTest();
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إتاحة الحفظ والطباعة")
    @Description("يتحقق من ظهور أزرار الحفظ والطباعة في نموذج القيد الجديد دون الضغط عليها.")
    public void SaveActionsAvailableTest() {
        new VouchersSavingChecks(this).SaveActionsAvailableTest();
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
        new VouchersValidationChecks(this).RejectEmptyVoucherTest();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("حفظ الملاحظات في نموذج القيد")
    @Description("يتحقق من إدخال الملاحظات العربية والإنجليزية والأرقام في مسودة القيد دون حفظها.")
    public void NotesAcceptArabicEnglishAndNumbersTest() {
        new VouchersSavingChecks(this).NotesAcceptArabicEnglishAndNumbersTest();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("اختيار الحساب بالبحث عن الكود")
    @Description("يفتح نافذة الحسابات ويبحث بكود معروف دون إضافة الحساب إلى القيد أو حفظه.")
    public void SearchAccountByCodeTest() {
        new VouchersListsChecks(this).SearchAccountByCodeTest();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("البحث في الحسابات بالاسم")
    @Description("يبحث عن حساب باسمه في نافذة الحسابات دون اختياره أو حفظ أي شيء.")
    public void SearchAccountByNameTest() {
        new VouchersListsChecks(this).SearchAccountByNameTest();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("البحث عن حساب غير موجود")
    @Description("يتحقق من أن البحث عن نص غير مطابق لا يعرض حسابات؛ لا يغير البيانات.")
    public void SearchAccountWithoutResultsTest() {
        new VouchersListsChecks(this).SearchAccountWithoutResultsTest();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("اختيار حساب من نافذة البحث")
    @Description("يختار حسابًا موجودًا من نافذة الحسابات ويتحقق من إضافته إلى المسودة دون حفظها.")
    public void SelectAccountFromSearchTest() {
        new VouchersListsChecks(this).SelectAccountFromSearchTest();
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
        new VouchersFormsChecks(this).CloseAccountPickerKeepsSelectedLineTest();
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
        new VouchersValidationChecks(this).ZeroAmountsKeepZeroDraftTotalsTest();
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إضافة بند دون تغيير البنود السابقة")
    @Description("يتحقق من بقاء إجمالي البند السابق كما هو بعد إضافة بند جديد إلى المسودة.")
    public void AddingLinePreservesPreviousValuesTest() {
        new VouchersSavingChecks(this).AddingLinePreservesPreviousValuesTest();
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
        new VouchersSavingChecks(this).BalancedEntryTotalsTest();
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
        new VouchersValidationChecks(this).UnbalancedEntryTotalsTest();
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("إعادة احتساب الاتزان بعد تعديل مبلغ")
    @Description("يعدل مبلغ المدين بعد إدخاله ويتحقق من إعادة احتساب الإجماليات؛ لا يحفظ القيد.")
    public void RecalculateBalanceAfterAmountEditTest() {
        new VouchersSavingChecks(this).RecalculateBalanceAfterAmountEditTest();
    }

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("قيد متعدد البنود ودقة الكسور")
    @Description("يتحقق من جمع أكثر من بند ومن احتساب الكسور العشرية في المسودة دون حفظها.")
    public void MultiLineDecimalTotalsTest() {
        new VouchersSavingChecks(this).MultiLineDecimalTotalsTest();
    }

    @Test(priority = 17)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("حذف بند من مسودة القيد")
    @Description("يحذف بندًا من المسودة ويتحقق من تحديث عدد البنود والإجماليات دون حفظ القيد.")
    public void RemoveLineUpdatesDraftTotalsTest() {
        new VouchersSavingChecks(this).RemoveLineUpdatesDraftTotalsTest();
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
        new VouchersValidationChecks(this).AmountFieldsAreNumericTest();
    }

    @Test(priority = 19)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-009 إدخال المدين والدائن في البند نفسه")
    @Description("يوثق سلوك جمع المدين والدائن في البند نفسه على مستوى المسودة دون حفظ.")
    public void SameLineDebitCreditDraftTest() {
        new VouchersSavingChecks(this).SameLineDebitCreditDraftTest();
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-016 اختيار حساب من القائمة المنسدلة")
    @Description("يختار حسابًا من قائمة الحسابات داخل صف القيد ويتحقق من إضافته للمسودة.")
    public void SelectAccountFromLineDropdownTest() {
        new VouchersSavingChecks(this).SelectAccountFromLineDropdownTest();
    }

    @Test(priority = 21)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-028 فحص التاريخ الافتراضي")
    @Description("يتحقق من ظهور تاريخ ابتدائي صالح وفق المتصفح دون إدخال تاريخ مخالف أو حفظ القيد.")
    public void VoucherDateFormatTest() {
        new VouchersFormsChecks(this).VoucherDateFormatTest();
    }

    @Test(priority = 22)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("JV-032 حفظ ملاحظة البند")
    @Description("يتحقق من إدخال الملاحظات العربية والإنجليزية والأرقام في صف المسودة.")
    public void LineNotesAcceptMixedTextTest() {
        new VouchersSavingChecks(this).LineNotesAcceptMixedTextTest();
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
        new VouchersListsChecks(this).AccountSearchAvailableInLinesTest();
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
        new VouchersSavingChecks(this).TaxSettlementTest();
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
        new VouchersValidationChecks(this).RejectUnbalancedVoucherOnSaveTest();
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
        new VouchersValidationChecks(this).RejectZeroValueVoucherOnSaveTest();
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
        new VouchersValidationChecks(this).RejectNegativeJournalAmountTest();
    }

    @Test(priority = 28)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("الحسابات العامة - قيود يومية")
    @Story("تنظيف قيود الاختبار")
    @Description("يتحقق من عدم بقاء أي قيد تجريبي من الحالات التي تنظف بياناتها تلقائيًا.")
    public void NoMarkedValidationVouchersRemainTest() {
        new VouchersValidationChecks(this).NoMarkedValidationVouchersRemainTest();
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
        new VouchersSaving2Checks(this).SavePrintEditAndDeleteTestVoucher();
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
        new VouchersValidationChecks(this).RejectDebitAndCreditOnSameLineTest();
    }
}
