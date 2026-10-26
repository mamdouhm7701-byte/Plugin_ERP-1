# اختبارات ودعم المبيعات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-026860c005f6"></a>
## ActionsCoverageTest.java

**المصدر:** [src/test/java/Invoice/ActionsCoverageTest.java](../../../src/test/java/Invoice/ActionsCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 31 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void copiesOwnedInvoiceIntoUnsavedFormWithoutChangingSource()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الفاتورة / النموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.ActionsCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-23ebd52858bd"></a>
## InvoiceListTestCases.java

**المصدر:** [src/test/java/Invoice/InvoiceListTestCases.java](../../../src/test/java/Invoice/InvoiceListTestCases.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 466 · **الأب المباشر:** `SalesListActions`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoicListPage.java](MAIN_PAGES_Invoice_AR.md#file-6c6dc5cb0a4f)، [SalesListActions.java](TEST_Invoice_AR.md#file-808989ad148f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private InvoicListPage list()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void scoped(SalesTestData.Record... records)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void paged()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void BrowseInvoiceListShowsDefaultRows()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvoiceListTableShowsColumnsAndAlignedRows()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الجدول / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] notFoundInvoiceTextFilters()` | public | تنفيذ جزء متعلق بـ الفاتورة / النص / الفلاتر، حسب المدخلات والتخصص. |
| `public void NotFoundInvoiceTextFiltersShowEmptyState(String filter, String testCaseId)` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / النص / الفلاتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] existingInvoiceTextFilters()` | public | تنفيذ جزء متعلق بـ الفاتورة / النص / الفلاتر، حسب المدخلات والتخصص. |
| `public void PartialCodeFilterReturnsOnlyMatchingRows()` | public · @Test | حالة تحقق مرتبطة بـالكود / الفلتر / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ResetInvoiceFiltersReturnsToDefaultResults()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الفلاتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ChangingAppliedFilterRefreshesResults()` | public · @Test | حالة تحقق مرتبطة بـالفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] invoicePageSizes()` | public | تنفيذ جزء متعلق بـ الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public void InvoicePageSizeCanBeSelected(int pageSize)` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الصفحة / الحجم / القيمة المختارة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvoicePageSizeOptionsAreExpected()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الصفحة / الحجم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PaginationMovesForwardAndBack()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PaginationCanOpenAnExplicitPageNumber()` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PreviousIsDisabledOnFirstInvoicePage()` | public · @Test | حالة تحقق مرتبطة بـالسابق / التعطيل / الأول / الفاتورة / الصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void LastInvoicePageDisablesNext()` | public · @Test | حالة تحقق مرتبطة بـالأخير / الفاتورة / الصفحة / التالي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FilterCanBeAppliedFromAdvancedPage()` | public · @Test | حالة تحقق مرتبطة بـالفلتر / الصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private String codeFor(Object id)` | private | تنفيذ جزء متعلق بـ الكود، حسب المدخلات والتخصص. |
| `public void EmptyFiltersAndRefreshKeepInvoiceListHealthy()` | public · @Test | حالة تحقق مرتبطة بـالفلاتر / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RefreshAfterApplyingFilterKeepsAConsistentState()` | public · @Test | حالة تحقق مرتبطة بـالفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvoiceRowsDoNotRepeatAcrossAdjacentPages()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SortingInvoiceCodeAscendingAndDescending()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الكود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SortingByDateColumn()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] invoiceDropdownFilters()` | public | تنفيذ جزء متعلق بـ الفاتورة / القائمة المنسدلة / الفلاتر، حسب المدخلات والتخصص. |
| `public void InvoiceDropdownFilterCanSelectAnExistingOption( String filterLabel, String column, String[] headerAliases, String testCaseId)` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / القائمة المنسدلة / الفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] invoicePaymentStatuses()` | public | تنفيذ جزء متعلق بـ الفاتورة / السداد، حسب المدخلات والتخصص. |
| `public void PaymentStatusFilterShowsOnlySelectedStatus(String status, String testCaseId)` | public · @Test | حالة تحقق مرتبطة بـالسداد / الفلتر / القيمة المختارة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PaymentStatusAllRestoresUnrestrictedResults()` | public · @Test | حالة تحقق مرتبطة بـالسداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvoiceMovementTypeCanBeFilteredAndResetToAll()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ValidAndSingleDayInvoiceDateRangesAreAccepted()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / التاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BroadValidInvoiceDateRangeKeepsTheInvoiceListAvailable()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / التاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvalidInvoiceDateRangeDoesNotReturnMisleadingRows()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / التاريخ / المرتجع / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DateRangeWithoutRecordsShowsNoStaleRows()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CombinedInvoiceFiltersAndSortingKeepBothConditions()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الفلاتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CombinedCodeAndDateFilterReturnsOnlyMatchingCode()` | public · @Test | حالة تحقق مرتبطة بـالكود / التاريخ / الفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ConflictingInvoiceFiltersShowNoResults()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الفلاتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PaginationPreservesFilterAndSortAndResetsPageForNewFilter()` | public · @Test | حالة تحقق مرتبطة بـالفلتر / الفرز / الصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SwitchingPageNumberAndPageSizeFromLaterPageWorks()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / الحجم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PageSizeWorksWithFilterAndSortAndSmallResultSet()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / الحجم / الفلتر / الفرز؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AdjacentInvoicePagesHaveDistinctRowsAndReturnToFirst()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الصفوف / المرتجع / الأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvoiceTablePageSizeCanExceedSmallResultSet()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الجدول / الصفحة / الحجم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportInvoiceListToPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportInvoiceListToExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.InvoiceListTestCases` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-7db05cbec00c"></a>
## InvoiceTest.java

**المصدر:** [src/test/java/Invoice/InvoiceTest.java](../../../src/test/java/Invoice/InvoiceTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 43 · **الأب المباشر:** `SalesFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesFormActions.java](TEST_Invoice_AR.md#file-704074ab73ef)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void AddInvoice()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddInvoiceCash()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByBarcodTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsWithSaveBuutonTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddWithOutProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.InvoiceTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-ffb4cc4f3310"></a>
## LinkedSalesReturnTest.java

**المصدر:** [src/test/java/Invoice/LinkedSalesReturnTest.java](../../../src/test/java/Invoice/LinkedSalesReturnTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 80 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SalesDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void linked()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void partialLinkedReturnRestoresOnlyReturnedQuantity()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void linkedReturnCannotExceedOriginalQuantity()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullLinkedReturnRestoresSoldQuantity()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cumulativeLinkedReturnsCannotExceedSoldQuantity()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.LinkedSalesReturnTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-ae7931450ae7"></a>
## PostingCoverageTest.java

**المصدر:** [src/test/java/Invoice/PostingCoverageTest.java](../../../src/test/java/Invoice/PostingCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 59 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)، [FixtureJournals.java](TEST_TestSupport_AR.md#file-cd3b35793c7a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void postsAndUnpostsOnlyCapturedOwnedInvoice()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void awaitPosted(SalesTestData.Record record,boolean expected)` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.PostingCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-869a4f56466c"></a>
## QuotationTest.java

**المصدر:** [src/test/java/Invoice/QuotationTest.java](../../../src/test/java/Invoice/QuotationTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 15 · **الأب المباشر:** `SalesFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesFormTest.java](TEST_Invoice_AR.md#file-62dbc85e5152)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void ownedPriceListIsApplied()` | public | تنفيذ جزء متعلق بـ المملوك للاختبار / السعر، حسب المدخلات والتخصص. |
| `protected SalesDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.QuotationTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-5c619ef29cd6"></a>
## SalesCalculationTest.java

**المصدر:** [src/test/java/Invoice/SalesCalculationTest.java](../../../src/test/java/Invoice/SalesCalculationTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 261

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static List<InvoiceRow> row(String quantity, String price)` | private | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `private static void check( List<InvoiceRow> rows, String discount, DiscountType discountType, String rate, TaxMode mode, int precision, String total, String discountValue, String beforeTax, String tax, String net)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void untaxedInvoice()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void inclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void percentageDiscountBeforeExclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountWithInclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountWithoutVat()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullPercentageDiscount()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullFixedDiscount()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalQuantityAndRounding()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void aggregateTaxRounding()` | public · @Test | حالة تحقق مرتبطة بـالضريبة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void threeDecimalInclusiveRounding()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void emptyCalculationIsZero()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesCalculationTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-5ca183776399"></a>
## SalesCleanupTest.java

**المصدر:** [src/test/java/Invoice/SalesCleanupTest.java](../../../src/test/java/Invoice/SalesCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 1029

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void normalCustomerIsTaxableAndKeepsItsOwnAccount()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الخطأ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts()` | public · @Test | حالة تحقق مرتبطة بـالحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void waitsForOriginalBranchHydrationBeforeFixtureAccess()` | public · @Test | حالة تحقق مرتبطة بـبيانات الاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageFixturesAreSharedButCaseBalancesStayIsolated()` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageTransitionRetainsAndRecoversThePreviousFailedSave()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / السابق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void negativeSaveDetectsACommitBehindFailedResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversSecondReturnWithoutConfusingKnownFirstReturn()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] lostSalesKinds()` | public | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `public void recoversLostResponseForEachSalesDocumentKind( String endpointType, String storedType)` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesJournalRecoveryInAnotherTenant() throws Exception` | public · @Test | حالة تحقق مرتبطة بـسجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesJournalRecoveryInAnotherBranch() throws Exception` | public · @Test | حالة تحقق مرتبطة بـسجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void rejectForeignContext(boolean tenant) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void linkedReturnIsDeletedBeforeSourceInvoice()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void paidInvoiceIsDeletedBeforePaymentMethodAndBank()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / السداد / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletesOwnedMovementBeforeItsItemsAndStores()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversStoreCommittedBeforeLostCreateResponse()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainStoreLookupFailurePreservesItsAccounts()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainStoreMustStillReferenceItsOwnedAccount()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / المملوك للاختبار / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void resumesPendingStoreCreationFromDurableJournal() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالمخزن / سجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsStockDependenciesWhenMovementDeletionFails()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesTrackingUnownedRecord()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void checksIdentityBeforeFirstDeletion()` | public · @Test | حالة تحقق مرتبطة بـالأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند / الملاحظات / المملوك للاختبار / المخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsSalesDocumentWithChangedStoreIdentity()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند / المخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsSalesDocumentWithChangedItemIdentity()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void salesDocumentIdentityMismatch(boolean storeChanged)` | private | تنفيذ جزء متعلق بـ المبيعات / المستند، حسب المدخلات والتخصص. |
| `public void recoversWarehouseCreateCommittedBeforeLostResponse()` | public · @Test | حالة تحقق مرتبطة بـالمخازن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainWarehouseLookupFailureRetainsAllParents()` | public · @Test | حالة تحقق مرتبطة بـالمخازن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversUpdatedNoteAfterLostUpdateResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsMissingDeleteWhenOwnedRecordStillExists()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversUiItemCommittedBeforeFailedResponse()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversInvalidItemByUniqueEnglishMarker()` | public · @Test | حالة تحقق مرتبطة بـالصنف / الإنجليزي / علامة الملكية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void verifiesCommittedBranchUnlinkAfterLostResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retriesUncommittedDeleteBeforeParentAccounts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsEmptyStringReadAfterCommittedDeleteTimeout()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansOwnedItemCommittedBeforeCreateFailure()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingMovementDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingMasterDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void updatedRecordDeletesBeforeItsNewReference()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageScopeSharesOnlyStableReferences()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public String slCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public void slSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e39bd034e59e"></a>
## SalesDocumentKind.java

**المصدر:** [src/test/java/Invoice/SalesDocumentKind.java](../../../src/test/java/Invoice/SalesDocumentKind.java)

**دوره:** تعريف أنواع مستندات/حركات المجال وربطها بمساراتها أو خصائص سلوكها.

**حالته:** مصدر نشط · **عدد أسطره:** 19

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesDocumentKind(String route, String type)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean stockMovement()` | داخل الحزمة | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-eb54b46bbc4a"></a>
## SalesEndToEndTest.java

**المصدر:** [src/test/java/Invoice/SalesEndToEndTest.java](../../../src/test/java/Invoice/SalesEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 131 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesReturnPage.java](MAIN_PAGES_Invoice_AR.md#file-54d29c21b2f2)، [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SalesDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void beginSalesScenario()` | public | بدء نطاق المبيعات، حسب المدخلات والتخصص. |
| `public void invoiceThenLinkedReturnRestoresStockAndCustomerBalance()` | public | تنفيذ جزء متعلق بـ الفاتورة / المرتجع / العميل / الرصيد، حسب المدخلات والتخصص. |
| `private SalesTestData.Record returnPart(SalesTestData.Record invoice, String quantity, String net)` | private | تنفيذ جزء متعلق بـ المرتجع، حسب المدخلات والتخصص. |
| `public void saleThenTwoPartialReturnsRestoreOriginalBalances()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void partialReturnThenOverReturnIsRejectedWithoutSideEffects()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalSaleThenReturnRestoresExactBalances()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cancelledReturnDoesNotPostBeforeTheConfirmedReturn()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e09181f49403"></a>
## SalesExportAssertions.java

**المصدر:** [src/test/java/Invoice/SalesExportAssertions.java](../../../src/test/java/Invoice/SalesExportAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 104

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private SalesExportAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format)` | داخل الحزمة | مقارنة متوقعة التصدير، حسب المدخلات والتخصص. |
| `private static void installDownloadCapture(WebDriver driver)` | private | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-0f6931eb8f99"></a>
## SalesFixtureApi.java

**المصدر:** [src/test/java/Invoice/SalesFixtureApi.java](../../../src/test/java/Invoice/SalesFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 78 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesFixtureApi(WebDriver driver, String origin, Object sessionBranchId, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-07b266f0c57e"></a>
## SalesFixtureApiStub.java

**المصدر:** [src/test/java/Invoice/SalesFixtureApiStub.java](../../../src/test/java/Invoice/SalesFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 200

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-eb669bcfcc18"></a>
## SalesFixtureDefinitions.java

**المصدر:** [src/test/java/Invoice/SalesFixtureDefinitions.java](../../../src/test/java/Invoice/SalesFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 483

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesFixtureDefinitions(SalesTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String, Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record agent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record otherSalesAgent()` | داخل الحزمة | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record destination()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record supplier()` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record customer(int allowedDiscount)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record bank()` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record safe()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record discountType()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `Record seedInvoice(int index, String type, String date, String quantity, String paid)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `Record seedInvoice( int index, String type, String date, String quantity, String paid, Record salesAgent)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |

<a id="file-0193679337de"></a>
## SalesFixtureRecovery.java

**المصدر:** [src/test/java/Invoice/SalesFixtureRecovery.java](../../../src/test/java/Invoice/SalesFixtureRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 19 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-704074ab73ef"></a>
## SalesFormActions.java

**المصدر:** [src/test/java/Invoice/SalesFormActions.java](../../../src/test/java/Invoice/SalesFormActions.java)

**دوره:** خطوات وتحقيقات مشتركة تعرضها اختبارات المجال النهائية دون نسخ نفس الجسم لكل نوع.

**حالته:** مصدر نشط · **عدد أسطره:** 247 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)

**ملفات تشير إليه مباشرة:** [InvoiceTest.java](TEST_Invoice_AR.md#file-7db05cbec00c)، [SalesFormTest.java](TEST_Invoice_AR.md#file-62dbc85e5152)، [SalesInvoiceTestCases.java](TEST_Invoice_AR.md#file-68112a5cc998)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected void savesOwnedItemFromPicker()` | protected | تنفيذ جزء متعلق بـ عمليات الحفظ / المملوك للاختبار / الصنف / نافذة الاختيار، حسب المدخلات والتخصص. |
| `protected void savesOwnedItemByBarcode()` | protected | تنفيذ جزء متعلق بـ عمليات الحفظ / المملوك للاختبار / الصنف / الباركود، حسب المدخلات والتخصص. |
| `protected void savesOwnedItemByName()` | protected | تنفيذ جزء متعلق بـ عمليات الحفظ / المملوك للاختبار / الصنف / الاسم، حسب المدخلات والتخصص. |
| `protected void cashDocumentUsesOwnedPayment()` | protected | تنفيذ جزء متعلق بـ المستند / المملوك للاختبار / السداد، حسب المدخلات والتخصص. |
| `protected void fractionalQuantityPersists()` | protected | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `protected void twoOwnedItemsPersist()` | protected | تنفيذ جزء متعلق بـ المملوك للاختبار / الأصناف، حسب المدخلات والتخصص. |
| `protected void removedItemIsNotSaved()` | protected | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `protected void percentageDiscountRecalculates()` | protected | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `protected void fixedDiscountRecalculates()` | protected | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `protected void ownedPriceListIsApplied()` | protected | تنفيذ جزء متعلق بـ المملوك للاختبار / السعر، حسب المدخلات والتخصص. |
| `protected void unicodeNotesPersist()` | protected | تنفيذ جزء متعلق بـ الملاحظات، حسب المدخلات والتخصص. |
| `protected void emptyDocumentCannotSave()` | protected | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `protected void zeroQuantityCannotSave()` | protected | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `protected void negativeQuantityCannotSave()` | protected | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `protected void missingCustomerCannotSave()` | protected | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `protected void missingStoreCannotSave()` | protected | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `protected void negativePriceCannotSave()` | protected | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `protected void notesRespectMaximumLength()` | protected | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `protected void newClearsUnsavedLines()` | protected | بدء جديد البنود، حسب المدخلات والتخصص. |
| `protected void savedDocumentDeletePolicy()` | protected | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `protected void savedDocumentReopensWithOwnedValues()` | protected | تنفيذ جزء متعلق بـ المستند / المملوك للاختبار / القيم، حسب المدخلات والتخصص. |
| `protected void savedDocumentEditPolicy()` | protected | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |

<a id="file-62dbc85e5152"></a>
## SalesFormTest.java

**المصدر:** [src/test/java/Invoice/SalesFormTest.java](../../../src/test/java/Invoice/SalesFormTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 155 · **الأب المباشر:** `SalesFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesFormActions.java](TEST_Invoice_AR.md#file-704074ab73ef)

**ملفات تشير إليه مباشرة:** [QuotationTest.java](TEST_Invoice_AR.md#file-869a4f56466c)، [SalesInvoiceCoverageTest.java](TEST_Invoice_AR.md#file-28ea90434073)، [SalesOrderTest.java](TEST_Invoice_AR.md#file-104444e9b25d)، [SalesReturnTest.java](TEST_Invoice_AR.md#file-203ad8fdd31d)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void savesOwnedItemFromPicker()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الصنف / نافذة الاختيار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesOwnedItemByBarcode()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الصنف / الباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesOwnedItemByName()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الصنف / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cashDocumentUsesOwnedPayment()` | public · @Test | حالة تحقق مرتبطة بـالمستند / المملوك للاختبار / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalQuantityPersists()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void twoOwnedItemsPersist()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removedItemIsNotSaved()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void percentageDiscountRecalculates()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountRecalculates()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void unicodeNotesPersist()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void emptyDocumentCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالمستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void zeroQuantityCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void negativeQuantityCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void missingCustomerCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void missingStoreCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void negativePriceCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void notesRespectMaximumLength()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void newClearsUnsavedLines()` | public · @Test | حالة تحقق مرتبطة بـالبنود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savedDocumentDeletePolicy()` | public · @Test | حالة تحقق مرتبطة بـالمستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savedDocumentReopensWithOwnedValues()` | public · @Test | حالة تحقق مرتبطة بـالمستند / المملوك للاختبار / القيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savedDocumentEditPolicy()` | public · @Test | حالة تحقق مرتبطة بـالمستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesFormTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-28ea90434073"></a>
## SalesInvoiceCoverageTest.java

**المصدر:** [src/test/java/Invoice/SalesInvoiceCoverageTest.java](../../../src/test/java/Invoice/SalesInvoiceCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 49 · **الأب المباشر:** `SalesFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesFormTest.java](TEST_Invoice_AR.md#file-62dbc85e5152)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void ownedPriceListIsApplied()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / السعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void barcodeApiFallbackAddsOwnedItemAbsentFromCache()` | public · @Test | حالة تحقق مرتبطة بـالباركود / المملوك للاختبار / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesAndPrintsOwnedInvoice()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesInvoiceCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-68112a5cc998"></a>
## SalesInvoiceTestCases.java

**المصدر:** [src/test/java/Invoice/SalesInvoiceTestCases.java](../../../src/test/java/Invoice/SalesInvoiceTestCases.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 188 · **الأب المباشر:** `SalesFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [Invoice.java](MAIN_PAGES_Invoice_AR.md#file-a0ad56722888)، [SalesFormActions.java](TEST_Invoice_AR.md#file-704074ab73ef)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void TC_SI_001_CreateCompleteSalesInvoice()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_011_AddItemByBarcode()` | public · @Test | حالة تحقق مرتبطة بـالصنف / الباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_012_AddItemByArabicName()` | public · @Test | حالة تحقق مرتبطة بـالصنف / العربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_013_AddItemsFromDialog()` | public · @Test | حالة تحقق مرتبطة بـالأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_014_FilterItemsByCategory()` | public · @Test | حالة تحقق مرتبطة بـالفلتر / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_015_SearchByBarcodeInAddItemsDialog()` | public · @Test | حالة تحقق مرتبطة بـالباركود / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_016_SearchByArabicNameInAddItemsDialog()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void pickerSearch(String field, boolean barcode)` | private | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void TC_SI_021_ToggleInvoiceFullscreen()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / ملء الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_005_WarehouseOptionsAreAvailable()` | public · @Test | حالة تحقق مرتبطة بـالمخازن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_004_DefaultPaymentTypeIsCash()` | public · @Test | حالة تحقق مرتبطة بـالسداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_007_CustomerOptionsAreAvailable()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_008_SalesAgentOptionsAreAvailable()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_010_PriceListOptionsAreAvailable()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_020_CreateCreditSalesInvoice()` | public · @Test | حالة تحقق مرتبطة بـالدائن / المبيعات / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_025_InvoiceWithPercentageDiscount()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_026_InvoiceWithFixedDiscount()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_028_EnterInvoiceComment()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TC_SI_029_NewInvoiceResetsCurrentInvoice()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddInvoice()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddInvoiceCash()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByBarcodTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsWithSaveBuutonTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddWithOutProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesInvoiceTestCases` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-808989ad148f"></a>
## SalesListActions.java

**المصدر:** [src/test/java/Invoice/SalesListActions.java](../../../src/test/java/Invoice/SalesListActions.java)

**دوره:** خطوات وتحقيقات مشتركة تعرضها اختبارات المجال النهائية دون نسخ نفس الجسم لكل نوع.

**حالته:** مصدر نشط · **عدد أسطره:** 200 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoicListPage.java](MAIN_PAGES_Invoice_AR.md#file-6c6dc5cb0a4f)، [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)

**ملفات تشير إليه مباشرة:** [InvoiceListTestCases.java](TEST_Invoice_AR.md#file-23ebd52858bd)، [SalesListCoverageTest.java](TEST_Invoice_AR.md#file-a27d5c45897c)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected void ownCustomerFilterReturnsOnlyOwnDocuments()` | protected | تنفيذ جزء متعلق بـ العميل / الفلتر، حسب المدخلات والتخصص. |
| `protected void exactCodeFilterFindsOwnedDocument()` | protected | تنفيذ جزء متعلق بـ الكود / الفلتر / المملوك للاختبار / المستند، حسب المدخلات والتخصص. |
| `protected void notesFilterFindsOwnedDocument()` | protected | ضبط الملاحظات / الفلتر / المملوك للاختبار / المستند، حسب المدخلات والتخصص. |
| `protected void unmatchedCodeShowsNoOwnDocuments()` | protected | تنفيذ جزء متعلق بـ الكود، حسب المدخلات والتخصص. |
| `public Object[][] pageSizes()` | public | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `protected void pageSizeKeepsOwnedResults(int size)` | protected | تنفيذ جزء متعلق بـ الصفحة / الحجم / المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected void paginationHasNoDuplicatesAndReturnsToFirst()` | protected | تنفيذ جزء متعلق بـ الأول، حسب المدخلات والتخصص. |
| `protected void sortingCodeKeepsOwnedRecords()` | protected | تنفيذ جزء متعلق بـ الكود / المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected void clearingCodeRestoresOwnCustomerScope()` | protected | تنفيذ جزء متعلق بـ الكود / العميل / النطاق، حسب المدخلات والتخصص. |
| `protected void exportsOwnFilteredListToExcel()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void exportsOwnFilteredListToPdf()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void ownedAgentFilterKeepsOnlyOwnDocuments()` | protected | تنفيذ جزء متعلق بـ المملوك للاختبار / الفلتر، حسب المدخلات والتخصص. |
| `protected void paymentStatusesMatchOwnedAmounts()` | protected | التعامل مع السداد / المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected void documentTypeFilterSeparatesOwnedSalesDocuments()` | protected | تنفيذ جزء متعلق بـ المستند / الفلتر / المملوك للاختبار / المبيعات، حسب المدخلات والتخصص. |
| `protected void singleDayDateRangeKeepsOwnDocuments()` | protected | تنفيذ جزء متعلق بـ التاريخ، حسب المدخلات والتخصص. |
| `protected void dateRangeWithoutOwnRecordsClearsStaleRows()` | protected | تنفيذ جزء متعلق بـ التاريخ / الصفوف، حسب المدخلات والتخصص. |
| `protected void export(String format)` | protected | تصدير التصدير، حسب المدخلات والتخصص. |
| `protected Set<Object> ids()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected SalesTestData.Record seed(int index)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-a27d5c45897c"></a>
## SalesListCoverageTest.java

**المصدر:** [src/test/java/Invoice/SalesListCoverageTest.java](../../../src/test/java/Invoice/SalesListCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 118 · **الأب المباشر:** `SalesListActions`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesListActions.java](TEST_Invoice_AR.md#file-808989ad148f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] pageSizes()` | public | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void ownCustomerFilterReturnsOnlyOwnDocuments()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exactCodeFilterFindsOwnedDocument()` | public · @Test | حالة تحقق مرتبطة بـالكود / الفلتر / المملوك للاختبار / المستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void notesFilterFindsOwnedDocument()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات / الفلتر / المملوك للاختبار / المستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void unmatchedCodeShowsNoOwnDocuments()` | public · @Test | حالة تحقق مرتبطة بـالكود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageSizeKeepsOwnedResults(int size)` | public · @Test | حالة تحقق مرتبطة بـالصفحة / الحجم / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void paginationHasNoDuplicatesAndReturnsToFirst()` | public · @Test | حالة تحقق مرتبطة بـالأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void sortingCodeKeepsOwnedRecords()` | public · @Test | حالة تحقق مرتبطة بـالكود / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void clearingCodeRestoresOwnCustomerScope()` | public · @Test | حالة تحقق مرتبطة بـالكود / العميل / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exportsOwnFilteredListToExcel()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exportsOwnFilteredListToPdf()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ownedAgentFilterKeepsOnlyOwnDocuments()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void paymentStatusesMatchOwnedAmounts()` | public · @Test | حالة تحقق مرتبطة بـالسداد / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void documentTypeFilterSeparatesOwnedSalesDocuments()` | public · @Test | حالة تحقق مرتبطة بـالمستند / الفلتر / المملوك للاختبار / المبيعات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void singleDayDateRangeKeepsOwnDocuments()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void dateRangeWithoutOwnRecordsClearsStaleRows()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesListCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-104444e9b25d"></a>
## SalesOrderTest.java

**المصدر:** [src/test/java/Invoice/SalesOrderTest.java](../../../src/test/java/Invoice/SalesOrderTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 40 · **الأب المباشر:** `SalesFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesFormTest.java](TEST_Invoice_AR.md#file-62dbc85e5152)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void ownedPriceListIsApplied()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / السعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `protected SalesDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void creditOrderDoesNotChangeCustomerBalance()` | public · @Test | حالة تحقق مرتبطة بـالدائن / العميل / الرصيد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cashOrderRecordsCustomerAdvanceWithoutStockMovement()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesOrderTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-203ad8fdd31d"></a>
## SalesReturnTest.java

**المصدر:** [src/test/java/Invoice/SalesReturnTest.java](../../../src/test/java/Invoice/SalesReturnTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 25 · **الأب المباشر:** `SalesFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesFormTest.java](TEST_Invoice_AR.md#file-62dbc85e5152)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void freeReturnAcceptsEnteredUnitPrice()` | public | تنفيذ جزء متعلق بـ المرتجع / السعر، حسب المدخلات والتخصص. |
| `protected SalesDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesReturnTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-403601fcfbed"></a>
## SalesScenarioTest.java

**المصدر:** [src/test/java/Invoice/SalesScenarioTest.java](../../../src/test/java/Invoice/SalesScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 418 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [ActionsCoverageTest.java](TEST_Invoice_AR.md#file-026860c005f6)، [LinkedSalesReturnTest.java](TEST_Invoice_AR.md#file-ffb4cc4f3310)، [PostingCoverageTest.java](TEST_Invoice_AR.md#file-ae7931450ae7)، [SalesEndToEndTest.java](TEST_Invoice_AR.md#file-eb54b46bbc4a)، [SalesFormActions.java](TEST_Invoice_AR.md#file-704074ab73ef)، [SalesListActions.java](TEST_Invoice_AR.md#file-808989ad148f)، [SalesSettlementEndToEndTest.java](TEST_Invoice_AR.md#file-5e0d70f6fb59)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SalesDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void awaitSalesSession()` | public | انتظار المبيعات / الجلسة، حسب المدخلات والتخصص. |
| `public void beginSalesScenario()` | public | بدء نطاق المبيعات، حسب المدخلات والتخصص. |
| `protected SalesTestData.Record ownItem(int index)` | protected | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `protected String salesNet(String beforeTax)` | protected | تنفيذ جزء متعلق بـ المبيعات / الصافي، حسب المدخلات والتخصص. |
| `protected void prepare(String paymentType)` | protected | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `protected void prepare(String paymentType, boolean usePriceList, boolean useDiscountType)` | protected | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `protected void add(SalesTestData.Record record, String mode)` | protected | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void fill(String mode)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected List<SalesTestData.Record> capture()` | protected | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `protected SalesTestData.Record save(boolean print)` | protected | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `protected SalesTestData.Record create(String mode, String paymentType, boolean print)` | protected | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static BigDecimal number(Object value)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static void sameNumber(Object actual, String expected)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static BigDecimal customerBalance(Map<String, Object> value)` | private | تنفيذ جزء متعلق بـ العميل / الرصيد، حسب المدخلات والتخصص. |
| `protected void assertCustomerDelta(Map<String, Object> before, String expected)` | protected | مقارنة متوقعة العميل، حسب المدخلات والتخصص. |
| `protected void assertStock(String quantity)` | protected | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void awaitOwnedStock(BigDecimal expected, String reason)` | protected | انتظار المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void cleanSalesScenario(ITestResult result)` | public | تنظيف المبيعات، حسب المدخلات والتخصص. |
| `public void retrySalesCleanup()` | public | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `protected void rejectSave()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static Map<String, Object> firstLine(Map<String, Object> document)` | protected | تنفيذ جزء متعلق بـ الأول / البند، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-5e0d70f6fb59"></a>
## SalesSettlementEndToEndTest.java

**المصدر:** [src/test/java/Invoice/SalesSettlementEndToEndTest.java](../../../src/test/java/Invoice/SalesSettlementEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 63 · **الأب المباشر:** `SalesScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialWorkflow.java](TEST_FinancialMovementsTest_AR.md#file-736d81307b79)، [SalesScenarioTest.java](TEST_Invoice_AR.md#file-403601fcfbed)، [WorkflowCleanupOrder.java](TEST_TestSupport_AR.md#file-510f62deecd0)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private FinancialWorkflow settlement(SalesTestData.Record invoice)` | private | تنفيذ جزء متعلق بـ التسوية، حسب المدخلات والتخصص. |
| `public void creditSaleThenPartialAndFinalCollection()` | public · @Test | حالة تحقق مرتبطة بـالدائن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void collectionEditAndDeleteRecalculateCustomerDebt()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullCollectionThenDeletionRestoresOutstandingDebt()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleanSalesScenario(ITestResult result)` | public | تنظيف المبيعات، حسب المدخلات والتخصص. |
| `public void retrySalesCleanup()` | public | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.SalesSettlementEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-cad79f88d846"></a>
## SalesSuitePolicy.java

**المصدر:** [src/test/java/Invoice/SalesSuitePolicy.java](../../../src/test/java/Invoice/SalesSuitePolicy.java)

**دوره:** قواعد مستوى/اختيار حالات المجال أو ضبطها؛ ليست صفحة أو وصف بيانات ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 18

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void alter(List<XmlSuite> suites)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-ba38f72d078e"></a>
## SalesTestData.java

**المصدر:** [src/test/java/Invoice/SalesTestData.java](../../../src/test/java/Invoice/SalesTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 1057

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private void awaitContext()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `SalesTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private SalesTestData(WebDriver driver, String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static SalesTestData resume(WebDriver driver, String suffix) throws java.io.IOException` | داخل الحزمة | إعادة تحميل حالة تشغيل محفوظة للتحقق أو الاسترجاع. |
| `static Map<String, Object> fields(Object... pairs)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record store(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private Map<String, Object> branchFor(Record link)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean containsStore(Map<String, Object> branch, Object storeId)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private void changeBranchStore(Record link, boolean add)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Map<String, Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record agent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record otherSalesAgent()` | داخل الحزمة | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record destination()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record supplier()` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record customer(int allowedDiscount)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Object balance(Record item, int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `private Map<String, Object> rememberSeedAttempt( String endpoint, String name, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record bank()` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record safe()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record discountType()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `Record seedInvoice(int index, String type, String date, String quantity, String paid)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `Record seedInvoice( int index, String type, String date, String quantity, String paid, Record salesAgent)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `void deleteOwned(Record record)` | داخل الحزمة | حذف المملوك للاختبار، حسب المدخلات والتخصص. |
| `java.util.Set<String> documentIdentities()` | داخل الحزمة | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `void verifyNoNewDocuments(java.util.Set<String> before)` | داخل الحزمة | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String, Object> lookup(String endpoint)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recoverAttemptedItems(List<Map<String, Object>> attempts)` | داخل الحزمة | تنفيذ جزء متعلق بـ الأصناف، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean existsInList(Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void track(String endpoint, Object id, String note, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean owns(Record record, Map<String, Object> stored)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void rememberAttempts( List<Map<String, Object>> attempts, List<Map<String, Object>> saves, String marker)` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `private void journalAttempts()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع / المحاولات، حسب المدخلات والتخصص. |
| `Map<String, Object> rememberFixtureAttempt( String endpoint, String name, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private void recoverFixtureAttempt(Map<String, Object> attempt)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private void recoverUncertainDocuments()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void findOwned(Object node, String name, List<Map<String, Object>> matches)` | private | البحث عن المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void close()` | public | إنهاء نطاق/مورد وتنظيفه حسب مالكه؛ اقرأ سياق الكلاس. |

<a id="file-1d10b6cfe30c"></a>
## SalesWorkflow.java

**المصدر:** [src/test/java/Invoice/SalesWorkflow.java](../../../src/test/java/Invoice/SalesWorkflow.java)

**دوره:** خطوات رحلة أعمال معاد استخدامها، بدل تكرار تسلسل عدة شاشات داخل كل اختبار.

**حالته:** مصدر نشط · **عدد أسطره:** 102

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)، [WorkflowEntity.java](TEST_TestSupport_AR.md#file-d394a5f89a8c)

**ملفات تشير إليه مباشرة:** [SupplyChainEndToEndTest.java](TEST_Purches_AR.md#file-81dbab96b057)، [ManufacturingSalesEndToEndTest.java](TEST_StoreTest_AR.md#file-cfde8472b5ed)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record Document(Object id, String code, String note, String endpoint, Map<String, Object> values)` | public | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `public String net(String beforeTax)` | public | تنفيذ جزء متعلق بـ الصافي، حسب المدخلات والتخصص. |
| `public SalesWorkflow(WebDriver driver, WorkflowEntity item, WorkflowEntity store, WorkflowEntity customer, WorkflowEntity agent)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public Document sell(String quantity)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Document returnPart(Document source, String quantity)` | public | تنفيذ جزء متعلق بـ المرتجع، حسب المدخلات والتخصص. |
| `private void beginCapture()` | private | بدء نطاق تتبع الحفظ، حسب المدخلات والتخصص. |
| `private Document save(String quantity, Object sourceId)` | private | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `public void capture()` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `private static void same(Object actual, BigDecimal expected)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-4673cafe9c87"></a>
## UnderReviewCoverageTest.java

**المصدر:** [src/test/java/Invoice/UnderReviewCoverageTest.java](../../../src/test/java/Invoice/UnderReviewCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 75 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [BasicDataFixtures.java](TEST_TestSupport_AR.md#file-e25770849138)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private void prepare()` | private | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `private BasicDataFixtures.Record save()` | private | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `public void savesUnderReviewWithoutPostingInventory()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / الجرد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Invoice.UnderReviewCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.
