# اختبارات ودعم القيود اليومية

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-6e939ee3b900"></a>
## VouchersTest.java

**المصدر:** [src/test/java/GenralAccountantTest/VouchersTest.java](../../../src/test/java/GenralAccountantTest/VouchersTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 767 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [VouchersPage.java](MAIN_PAGES_GenralAccountant_AR.md#file-4d6d58ffec6e)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String accountCode(String key)` | private | تنفيذ جزء متعلق بـ الحساب / الكود، حسب المدخلات والتخصص. |
| `private VouchersPage openNewVoucher()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitForVoucherForm()` | private | انتظار النموذج، حسب المدخلات والتخصص. |
| `public void OpenVouchersPageTest()` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void VoucherDateIsPrepopulatedTest()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SaveActionsAvailableTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RejectEmptyVoucherTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void NotesAcceptArabicEnglishAndNumbersTest()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات / العربي / الإنجليزي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SearchAccountByCodeTest()` | public · @Test | حالة تحقق مرتبطة بـالحساب / الكود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SearchAccountByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالحساب / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SearchAccountWithoutResultsTest()` | public · @Test | حالة تحقق مرتبطة بـالحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SelectAccountFromSearchTest()` | public · @Test | حالة تحقق مرتبطة بـالحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CloseAccountPickerKeepsSelectedLineTest()` | public · @Test | حالة تحقق مرتبطة بـالحساب / نافذة الاختيار / القيمة المختارة / البند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ZeroAmountsKeepZeroDraftTotalsTest()` | public · @Test | حالة تحقق مرتبطة بـالإجماليات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddingLinePreservesPreviousValuesTest()` | public · @Test | حالة تحقق مرتبطة بـالبند / السابق / القيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BalancedEntryTotalsTest()` | public · @Test | حالة تحقق مرتبطة بـالإجماليات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void UnbalancedEntryTotalsTest()` | public · @Test | حالة تحقق مرتبطة بـالإجماليات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RecalculateBalanceAfterAmountEditTest()` | public · @Test | حالة تحقق مرتبطة بـالرصيد / المبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MultiLineDecimalTotalsTest()` | public · @Test | حالة تحقق مرتبطة بـالبند / الإجماليات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RemoveLineUpdatesDraftTotalsTest()` | public · @Test | حالة تحقق مرتبطة بـالبند / الإجماليات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AmountFieldsAreNumericTest()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SameLineDebitCreditDraftTest()` | public · @Test | حالة تحقق مرتبطة بـالبند / المدين / الدائن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SelectAccountFromLineDropdownTest()` | public · @Test | حالة تحقق مرتبطة بـالحساب / البند / القائمة المنسدلة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void VoucherDateFormatTest()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void LineNotesAcceptMixedTextTest()` | public · @Test | حالة تحقق مرتبطة بـالبند / الملاحظات / النص؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AccountSearchAvailableInLinesTest()` | public | تنفيذ جزء متعلق بـ الحساب / البنود، حسب المدخلات والتخصص. |
| `public void TaxSettlementTest()` | public · @Test | حالة تحقق مرتبطة بـالضريبة / التسوية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RejectUnbalancedVoucherOnSaveTest()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void RejectZeroValueVoucherOnSaveTest()` | public | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public void RejectNegativeJournalAmountTest()` | public | تنفيذ جزء متعلق بـ سجل الاسترجاع / المبلغ، حسب المدخلات والتخصص. |
| `private void assertMarkedVoucherRejected( String scenario, java.util.function.Consumer<VouchersPage> setup)` | private | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void NoMarkedValidationVouchersRemainTest()` | public · @Test | حالة تحقق مرتبطة بـالتحقق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SavePrintEditAndDeleteTestVoucher()` | public | حفظ الطباعة، حسب المدخلات والتخصص. |
| `public void RejectDebitAndCreditOnSameLineTest()` | public | تنفيذ جزء متعلق بـ المدين / الدائن / البند، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `GenralAccountantTest.VouchersTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.
