# صفحات ومكونات المبيعات وقائمة الفواتير

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-a0ad56722888"></a>
## Invoice.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/Invoice.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/Invoice.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 29 · **الأب المباشر:** `SalesModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)

**ملفات تشير إليه مباشرة:** [SalesInvoiceTestCases.java](TEST_Invoice_AR.md#file-68112a5cc998)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Invoice(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-626f529b0735"></a>
## InvoiceListCalendar.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListCalendar.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListCalendar.java)

**دوره:** تاريخ فلاتر قائمة الفواتير وتقويمها، بما فيه قراءة الشهر.

**حالته:** مصدر نشط · **عدد أسطره:** 125

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `InvoiceListCalendar(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void setDateFilter(WebElement hiddenInput, String value)` | داخل الحزمة | تعيين التاريخ / الفلتر، حسب المدخلات والتخصص. |
| `private boolean calendarIsVisible(String pickerId)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private String calendarCaption(String pickerId)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private int parseMonth(String text)` | private | تفسير الشهر من عنوان التقويم. |
| `private static boolean isDisplayed(WebElement element)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-b0fff7423b9a"></a>
## InvoiceListExport.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListExport.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListExport.java)

**دوره:** تحديد زر التصدير المرئي المناسب للصيغة؛ لا يثبت محتوى الملف بمفرده.

**حالته:** مصدر نشط · **عدد أسطره:** 61

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `InvoiceListExport(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void exportInvoices(String format)` | داخل الحزمة | تصدير التصدير، حسب المدخلات والتخصص. |
| `private static boolean isDisplayed(WebElement element)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-b50cc8b4e8fb"></a>
## InvoiceListFilters.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListFilters.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListFilters.java)

**دوره:** حقول وخيارات وقيم فلاتر قائمة الفواتير وخط أساس الجدول قبل التطبيق.

**حالته:** مصدر نشط · **عدد أسطره:** 350

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `InvoiceListFilters(InvoicListPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private String gridSnapshot()` | private | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `private void waitForGridRefresh(String before)` | private | انتظار الجدول، حسب المدخلات والتخصص. |
| `private void waitForGridRefresh()` | private | انتظار الجدول، حسب المدخلات والتخصص. |
| `private void setDateFilter(WebElement input, String value)` | private | تعيين التاريخ / الفلتر، حسب المدخلات والتخصص. |
| `private void slAwaitListReady()` | private | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `private void slChoose(String label, String value)` | private | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void slApplyListFilters()` | private | تنفيذ جزء متعلق بـ الفلاتر، حسب المدخلات والتخصص. |
| `void setInvoiceTextFilter(String label, String value)` | داخل الحزمة | تعيين الفاتورة / النص / الفلتر، حسب المدخلات والتخصص. |
| `void applyInvoiceFilters()` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة / الفلاتر، حسب المدخلات والتخصص. |
| `void selectInvoiceFilter(String label, String value)` | داخل الحزمة | اختيار الفاتورة / الفلتر، حسب المدخلات والتخصص. |
| `List<String> getInvoiceFilterOptions(String label)` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الفلتر، حسب المدخلات والتخصص. |
| `List<String> getInvoiceFilterOptions(String label, String query)` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الفلتر، حسب المدخلات والتخصص. |
| `String getInvoiceFilterValue(String label)` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الفلتر / القيمة، حسب المدخلات والتخصص. |
| `WebElement vueFilter(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفلتر، حسب المدخلات والتخصص. |
| `String selectedVueFilter(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ القيمة المختارة / الفلتر، حسب المدخلات والتخصص. |
| `WebElement findFilterControl(String label)` | داخل الحزمة | البحث عن الفلتر، حسب المدخلات والتخصص. |
| `void captureFilterGridBaseline()` | داخل الحزمة | مراقبة تتبع الحفظ / الفلتر / الجدول، حسب المدخلات والتخصص. |
| `static boolean isDisplayed(WebElement element)` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static String escapeXPath(String value)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ensureInvoiceFiltersVisible()` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة / الفلاتر / الظاهر، حسب المدخلات والتخصص. |
| `void selectVueOption(String inputId, String value)` | داخل الحزمة | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String getVueSelectedValue(String inputId)` | داخل الحزمة | قراءة أو إرجاع القيمة المختارة / القيمة، حسب المدخلات والتخصص. |
| `void clickVueDropdown(WebElement input)` | داخل الحزمة | ضغط إجراء النقر / القائمة المنسدلة، حسب المدخلات والتخصص. |
| `List<String> getRadioOptions(int groupIndex)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void clickRadioOption(int groupIndex, String value)` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `String getSelectedRadioValue(int groupIndex)` | داخل الحزمة | قراءة أو إرجاع القيمة المختارة / القيمة، حسب المدخلات والتخصص. |

<a id="file-48370264b9e2"></a>
## InvoiceListGrid.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListGrid.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListGrid.java)

**دوره:** قراءة الصفوف والأعمدة والفرز وانتظار استقرار تحديث الجدول.

**حالته:** مصدر نشط · **عدد أسطره:** 240

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `InvoiceListGrid(InvoicListPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private static boolean isDisplayed(WebElement element)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean isInvoiceTableDisplayed()` | داخل الحزمة | السؤال عن حالة الفاتورة / الجدول، حسب المدخلات والتخصص. |
| `List<String> getInvoiceTableHeaders()` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الجدول / عناوين الأعمدة، حسب المدخلات والتخصص. |
| `List<List<String>> getVisibleInvoiceRows()` | داخل الحزمة | قراءة أو إرجاع الظاهر / الفاتورة / الصفوف، حسب المدخلات والتخصص. |
| `boolean areVisibleRowsMatching(String expectedText)` | داخل الحزمة | تنفيذ جزء متعلق بـ الظاهر / الصفوف، حسب المدخلات والتخصص. |
| `boolean isInvoiceEmptyStateDisplayed()` | داخل الحزمة | السؤال عن حالة الفاتورة، حسب المدخلات والتخصص. |
| `String getFirstInvoiceCell(String columnName)` | داخل الحزمة | قراءة أو إرجاع الأول / الفاتورة، حسب المدخلات والتخصص. |
| `List<String> getInvoiceColumnValues(String columnName)` | داخل الحزمة | قراءة أو إرجاع الفاتورة / القيم، حسب المدخلات والتخصص. |
| `void sortInvoiceColumn(String columnName, boolean ascending)` | داخل الحزمة | فرز الفرز / الفاتورة، حسب المدخلات والتخصص. |
| `boolean isInvoiceColumnSorted(String columnName, boolean ascending)` | داخل الحزمة | السؤال عن حالة الفاتورة، حسب المدخلات والتخصص. |
| `void waitForGridRefresh()` | داخل الحزمة | انتظار الجدول، حسب المدخلات والتخصص. |
| `void waitForGridRefresh(String previousGrid)` | داخل الحزمة | انتظار الجدول، حسب المدخلات والتخصص. |
| `String gridSnapshot()` | داخل الحزمة | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `int getColumnIndex(String columnName)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int compareCellValues(String left, String right)` | داخل الحزمة | تنفيذ جزء متعلق بـ القيم، حسب المدخلات والتخصص. |
| `String normalizeComparableText(String value)` | داخل الحزمة | توحيد صيغة النص، حسب المدخلات والتخصص. |
| `boolean isEmptyStateText(String value)` | داخل الحزمة | السؤال عن حالة النص، حسب المدخلات والتخصص. |
| `List<Integer> visibleColumnIndexes()` | داخل الحزمة | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `WebElement findColumnHeader(String columnName)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String cleanHeaderText(String text)` | داخل الحزمة | تنظيف النص، حسب المدخلات والتخصص. |

<a id="file-e664812c80c9"></a>
## InvoiceListPagination.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListPagination.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListPagination.java)

**دوره:** حجم الصفحة والتنقل وأرقام الصفحات في قائمة الفواتير.

**حالته:** مصدر نشط · **عدد أسطره:** 205

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `InvoiceListPagination(InvoicListPage owner, WebDriver driver, Supplier<String> snapshot, Consumer<String> refresh)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private String gridSnapshot()` | private | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `private void waitForGridRefresh(String before)` | private | انتظار الجدول، حسب المدخلات والتخصص. |
| `private static boolean isDisplayed(WebElement element)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void setInvoicePageSize(int pageSize)` | داخل الحزمة | تعيين الفاتورة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `int getInvoicePageSize()` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `List<Integer> getInvoicePageSizeOptions()` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `boolean isNextInvoicePageEnabled()` | داخل الحزمة | السؤال عن حالة التالي / الفاتورة / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `boolean isPreviousInvoicePageEnabled()` | داخل الحزمة | السؤال عن حالة السابق / الفاتورة / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `void goToNextInvoicePage()` | داخل الحزمة | تنفيذ جزء متعلق بـ التالي / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `void goToPreviousInvoicePage()` | داخل الحزمة | تنفيذ جزء متعلق بـ السابق / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `void goToFirstInvoicePage()` | داخل الحزمة | تنفيذ جزء متعلق بـ الأول / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `void goToLastInvoicePage()` | داخل الحزمة | تنفيذ جزء متعلق بـ الأخير / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `void goToInvoicePage(int pageNumber)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `int getCurrentInvoicePageNumber()` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `String getInvoicePaginationSummary()` | داخل الحزمة | قراءة أو إرجاع الفاتورة، حسب المدخلات والتخصص. |
| `WebElement findPageSizeSelect()` | داخل الحزمة | البحث عن الصفحة / الحجم، حسب المدخلات والتخصص. |
| `WebElement findPaginationControl(String kind)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void clickPaginationControl(String kind)` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `boolean isPaginationEnabled(WebElement button)` | داخل الحزمة | السؤال عن حالة الإتاحة، حسب المدخلات والتخصص. |

<a id="file-6c6dc5cb0a4f"></a>
## InvoicListPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoicListPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/InvoicListPage.java)

**دوره:** واجهة قائمة الفواتير التي تنسق الفلاتر والجدول والتقويم والتنقل والتصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 243 · **الأب المباشر:** `SalesModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)

**ملفات تشير إليه مباشرة:** [InvoiceListTestCases.java](TEST_Invoice_AR.md#file-23ebd52858bd)، [SalesListActions.java](TEST_Invoice_AR.md#file-808989ad148f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public InvoicListPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void waitForInvoiceList()` | public | انتظار الفاتورة، حسب المدخلات والتخصص. |
| `public boolean isInvoiceTableDisplayed()` | public | السؤال عن حالة الفاتورة / الجدول، حسب المدخلات والتخصص. |
| `public List<String> getInvoiceTableHeaders()` | public | قراءة أو إرجاع الفاتورة / الجدول / عناوين الأعمدة، حسب المدخلات والتخصص. |
| `public List<List<String>> getVisibleInvoiceRows()` | public | قراءة أو إرجاع الظاهر / الفاتورة / الصفوف، حسب المدخلات والتخصص. |
| `public void setInvoiceTextFilter(String label, String value)` | public | تعيين الفاتورة / النص / الفلتر، حسب المدخلات والتخصص. |
| `public void applyInvoiceFilters()` | public | تنفيذ جزء متعلق بـ الفاتورة / الفلاتر، حسب المدخلات والتخصص. |
| `public void resetInvoiceFilters()` | public | تنفيذ جزء متعلق بـ الفاتورة / الفلاتر، حسب المدخلات والتخصص. |
| `public void selectInvoiceFilter(String label, String value)` | public | اختيار الفاتورة / الفلتر، حسب المدخلات والتخصص. |
| `public List<String> getInvoiceFilterOptions(String label)` | public | قراءة أو إرجاع الفاتورة / الفلتر، حسب المدخلات والتخصص. |
| `public List<String> getInvoiceFilterOptions(String label, String query)` | public | قراءة أو إرجاع الفاتورة / الفلتر، حسب المدخلات والتخصص. |
| `public String getInvoiceFilterValue(String label)` | public | قراءة أو إرجاع الفاتورة / الفلتر / القيمة، حسب المدخلات والتخصص. |
| `public boolean areVisibleRowsMatching(String expectedText)` | public | تنفيذ جزء متعلق بـ الظاهر / الصفوف، حسب المدخلات والتخصص. |
| `public boolean isInvoiceEmptyStateDisplayed()` | public | السؤال عن حالة الفاتورة، حسب المدخلات والتخصص. |
| `public String getFirstInvoiceCell(String columnName)` | public | قراءة أو إرجاع الأول / الفاتورة، حسب المدخلات والتخصص. |
| `public List<String> getInvoiceColumnValues(String columnName)` | public | قراءة أو إرجاع الفاتورة / القيم، حسب المدخلات والتخصص. |
| `public void sortInvoiceColumn(String columnName, boolean ascending)` | public | فرز الفرز / الفاتورة، حسب المدخلات والتخصص. |
| `public boolean isInvoiceColumnSorted(String columnName, boolean ascending)` | public | السؤال عن حالة الفاتورة، حسب المدخلات والتخصص. |
| `public void setInvoicePageSize(int pageSize)` | public | تعيين الفاتورة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public int getInvoicePageSize()` | public | قراءة أو إرجاع الفاتورة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public List<Integer> getInvoicePageSizeOptions()` | public | قراءة أو إرجاع الفاتورة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public boolean isNextInvoicePageEnabled()` | public | السؤال عن حالة التالي / الفاتورة / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `public boolean isPreviousInvoicePageEnabled()` | public | السؤال عن حالة السابق / الفاتورة / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `public void goToNextInvoicePage()` | public | تنفيذ جزء متعلق بـ التالي / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public void goToPreviousInvoicePage()` | public | تنفيذ جزء متعلق بـ السابق / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public void goToFirstInvoicePage()` | public | تنفيذ جزء متعلق بـ الأول / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public void goToLastInvoicePage()` | public | تنفيذ جزء متعلق بـ الأخير / الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public void goToInvoicePage(int pageNumber)` | public | تنفيذ جزء متعلق بـ الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public int getCurrentInvoicePageNumber()` | public | قراءة أو إرجاع الفاتورة / الصفحة، حسب المدخلات والتخصص. |
| `public String getInvoicePaginationSummary()` | public | قراءة أو إرجاع الفاتورة، حسب المدخلات والتخصص. |
| `public List<String> getInvoiceValidationMessages()` | public | قراءة أو إرجاع الفاتورة / التحقق، حسب المدخلات والتخصص. |
| `public List<String> getInvoiceRowIdentities(String columnName)` | public | قراءة أو إرجاع الفاتورة / الصف، حسب المدخلات والتخصص. |
| `public void refreshInvoiceList()` | public | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `String filterGridSnapshot()` | داخل الحزمة | تنفيذ جزء متعلق بـ الفلتر / الجدول، حسب المدخلات والتخصص. |
| `void awaitFilterGridRefresh(String before)` | داخل الحزمة | انتظار الفلتر / الجدول، حسب المدخلات والتخصص. |
| `private void waitForGridRefresh()` | private | انتظار الجدول، حسب المدخلات والتخصص. |
| `private void waitForGridRefresh(String previousGrid)` | private | انتظار الجدول، حسب المدخلات والتخصص. |
| `private String gridSnapshot()` | private | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `private static boolean isDisplayed(WebElement element)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void exportInvoices(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `void setCalendarFilter(WebElement hiddenInput, String value)` | داخل الحزمة | تعيين الفلتر، حسب المدخلات والتخصص. |

<a id="file-6a572b5fe8d4"></a>
## QuotationPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/QuotationPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/QuotationPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 9 · **الأب المباشر:** `SalesModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public QuotationPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-86abb798021c"></a>
## SalesChoiceControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesChoiceControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesChoiceControls.java)

**دوره:** اختيار وقراءة ومسح القيم في قوائم نموذج المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 92

**يرتبط مباشرة بملفات داخل المشروع:** [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesChoiceControls(SalesModulePage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void slChoose(String label, String name)` | داخل الحزمة | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void slClearChoice(String label)` | داخل الحزمة | مسح/إلغاء الاختيار، حسب المدخلات والتخصص. |
| `boolean slChoiceEmpty(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ الاختيار، حسب المدخلات والتخصص. |
| `boolean slCanClearChoice(String label)` | داخل الحزمة | التحقق من إتاحة الاختيار، حسب المدخلات والتخصص. |
| `boolean slHasChoice(String label)` | داخل الحزمة | السؤال عن وجود الاختيار، حسب المدخلات والتخصص. |

<a id="file-633e381a995a"></a>
## SalesDocumentForm.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesDocumentForm.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesDocumentForm.java)

**دوره:** حقول النموذج وجاهزيته وحفظه وتنبيهاته في مجال المستند، بدون ملكية بيانات التجربة.

**حالته:** مصدر نشط · **عدد أسطره:** 145

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesDocumentForm(SalesModulePage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void slNotes(String note)` | داخل الحزمة | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `void slSave(boolean print)` | داخل الحزمة | حفظ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean slSaveDisabled()` | داخل الحزمة | حفظ التعطيل، حسب المدخلات والتخصص. |
| `boolean slHasDocumentSaveAction()` | داخل الحزمة | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `boolean slHasDocumentDeleteAction()` | داخل الحزمة | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `void slDismiss()` | داخل الحزمة | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String slValidation()` | داخل الحزمة | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `Map<String, Object> slDocument()` | داخل الحزمة | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `void slFormReady()` | داخل الحزمة | تنفيذ جزء متعلق بـ النموذج / الجاهزية، حسب المدخلات والتخصص. |
| `String slDocumentDate()` | داخل الحزمة | تنفيذ جزء متعلق بـ المستند / التاريخ، حسب المدخلات والتخصص. |
| `void slDiscount(String value, boolean percentage)` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `void slAddOtherPayment(String method, String amount)` | داخل الحزمة | إضافة السداد، حسب المدخلات والتخصص. |
| `void slNew()` | داخل الحزمة | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String slNotesValue()` | داخل الحزمة | ضبط الملاحظات / القيمة، حسب المدخلات والتخصص. |

<a id="file-130166c5f826"></a>
## SalesDocumentLines.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesDocumentLines.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesDocumentLines.java)

**دوره:** بنود المستند أو سلة الأصناف: القراءة والكمية والأسعار والحذف والإجماليات بحسب المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 218

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesDocumentLines(SalesModulePage page, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean slLineReadOnly(String name, String column)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `private By line(String name)` | private | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `int slLineCount(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `Map<String, String> slLineValues(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند / القيم، حسب المدخلات والتخصص. |
| `void slQuantity(String name, String quantity)` | داخل الحزمة | ضبط الكمية، حسب المدخلات والتخصص. |
| `void slRemoveLine(String name)` | داخل الحزمة | إزالة البند، حسب المدخلات والتخصص. |
| `void slSetLine(String name, String column, String value)` | داخل الحزمة | تعيين البند، حسب المدخلات والتخصص. |
| `Map<String, String> slTotals()` | داخل الحزمة | تنفيذ جزء متعلق بـ الإجماليات، حسب المدخلات والتخصص. |
| `void slWaitNet(String value)` | داخل الحزمة | انتظار الصافي، حسب المدخلات والتخصص. |

<a id="file-4f4ed1526340"></a>
## SalesDocumentList.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesDocumentList.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesDocumentList.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 120

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesDocumentList(SalesModulePage page, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void slListScope(String customer)` | داخل الحزمة | تنفيذ جزء متعلق بـ النطاق، حسب المدخلات والتخصص. |
| `void slAwaitListReady()` | داخل الحزمة | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `private boolean slListReady()` | private | تنفيذ جزء متعلق بـ الجاهزية، حسب المدخلات والتخصص. |
| `void slApplyListFilters()` | داخل الحزمة | تنفيذ جزء متعلق بـ الفلاتر، حسب المدخلات والتخصص. |
| `String slListFixtureDate()` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار / التاريخ، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> slListRows()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصفوف، حسب المدخلات والتخصص. |
| `void slWaitListIds(Set<Object> expected)` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void slListText(String name, String text)` | داخل الحزمة | تنفيذ جزء متعلق بـ النص، حسب المدخلات والتخصص. |
| `void slListRadio(String group, String value)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `List<String> slHeaders()` | داخل الحزمة | تنفيذ جزء متعلق بـ عناوين الأعمدة، حسب المدخلات والتخصص. |
| `void slExport(String format)` | داخل الحزمة | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-4117ba6dd795"></a>
## SalesItemSelection.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesItemSelection.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesItemSelection.java)

**دوره:** البحث واختيار الصنف بالاسم/الباركود/النافذة بحسب ما تسمح به ميثوداته.

**حالته:** مصدر نشط · **عدد أسطره:** 165

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesItemSelection(SalesModulePage page, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void radio(String value)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void slWaitOwnedItemLookup(String name)` | داخل الحزمة | انتظار المملوك للاختبار / الصنف، حسب المدخلات والتخصص. |
| `void slAddName(String name)` | داخل الحزمة | إضافة الاسم، حسب المدخلات والتخصص. |
| `void slAddBarcode(String barcode, String name)` | داخل الحزمة | إضافة الباركود، حسب المدخلات والتخصص. |
| `void slAddBarcodeImmediately(String barcode, String name)` | داخل الحزمة | إضافة الباركود، حسب المدخلات والتخصص. |
| `boolean slOwnedItemCached(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ المملوك للاختبار / الصنف / المخزن مؤقتًا، حسب المدخلات والتخصص. |
| `Object slItemCacheDiagnostics(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `void slAddPicker(String name)` | داخل الحزمة | إضافة نافذة الاختيار، حسب المدخلات والتخصص. |
| `void slPickerSearch(String field, String value, String expectedName)` | داخل الحزمة | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `void slPickerCategory(String category, String expectedName)` | داخل الحزمة | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `List<String> slPickerRows()` | داخل الحزمة | تنفيذ جزء متعلق بـ نافذة الاختيار / الصفوف، حسب المدخلات والتخصص. |
| `void slClosePicker()` | داخل الحزمة | إغلاق نافذة الاختيار، حسب المدخلات والتخصص. |

<a id="file-24084c016c58"></a>
## SalesModulePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesModulePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesModulePage.java)

**دوره:** واجهة تنسيق قدرات مجال متكرر؛ تستدعي مكونات متخصصة وتحتفظ بنقاط دخول الاختبارات.

**حالته:** مصدر نشط · **عدد أسطره:** 392 · **الأب المباشر:** `AbstractForSales`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [Invoice.java](MAIN_PAGES_Invoice_AR.md#file-a0ad56722888)، [InvoicListPage.java](MAIN_PAGES_Invoice_AR.md#file-6c6dc5cb0a4f)، [QuotationPage.java](MAIN_PAGES_Invoice_AR.md#file-6a572b5fe8d4)، [SalesOrderPage.java](MAIN_PAGES_Invoice_AR.md#file-3f26f1218051)، [SalesReturnPage.java](MAIN_PAGES_Invoice_AR.md#file-54d29c21b2f2)، [SalesWorkflow.java](TEST_Invoice_AR.md#file-1d10b6cfe30c)، [UnderReviewCoverageTest.java](TEST_Invoice_AR.md#file-4673cafe9c87)، [SalesNavigationTest.java](TEST_FrameworkContracts_AR.md#file-d52ac859077b)؛ العرض مختصر وليس قائمة كاملة.

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public SalesModulePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void slAwaitSession()` | public | انتظار الجلسة، حسب المدخلات والتخصص. |
| `protected Object js(String script, Object... args)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static String navigationPath(String route)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void slOpen(String route)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void slType(By by, String value)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void slType(By by, String value, boolean blur)` | داخل الحزمة | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void slClick(By by)` | protected | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void slChoose(String label, String name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void slClearChoice(String label)` | public | مسح/إلغاء الاختيار، حسب المدخلات والتخصص. |
| `public boolean slChoiceEmpty(String label)` | public | تنفيذ جزء متعلق بـ الاختيار، حسب المدخلات والتخصص. |
| `public boolean slCanClearChoice(String label)` | public | التحقق من إتاحة الاختيار، حسب المدخلات والتخصص. |
| `public boolean slHasChoice(String label)` | public | السؤال عن وجود الاختيار، حسب المدخلات والتخصص. |
| `public void slNotes(String note)` | public | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `public String slDiagnosticMarkup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void utilitesReportReady()` | public | تنفيذ جزء متعلق بـ التقرير / الجاهزية، حسب المدخلات والتخصص. |
| `public void slButton(String text)` | public | تنفيذ جزء متعلق بـ الزر، حسب المدخلات والتخصص. |
| `public void slSave(boolean print)` | public | حفظ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean slSaveDisabled()` | public | حفظ التعطيل، حسب المدخلات والتخصص. |
| `public boolean slHasDocumentSaveAction()` | public | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `public boolean slHasDocumentDeleteAction()` | public | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `public boolean slLineReadOnly(String name, String column)` | public | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `public void slDismiss()` | public | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String slValidation()` | public | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `public int slLineCount(String name)` | public | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `public Map<String, String> slLineValues(String name)` | public | تنفيذ جزء متعلق بـ البند / القيم، حسب المدخلات والتخصص. |
| `private void radio(String value)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void slWaitOwnedItemLookup(String name)` | public | انتظار المملوك للاختبار / الصنف، حسب المدخلات والتخصص. |
| `public void slAddName(String name)` | public | إضافة الاسم، حسب المدخلات والتخصص. |
| `public void slAddBarcode(String barcode, String name)` | public | إضافة الباركود، حسب المدخلات والتخصص. |
| `public void slAddBarcodeImmediately(String barcode, String name)` | public | إضافة الباركود، حسب المدخلات والتخصص. |
| `public boolean slOwnedItemCached(String name)` | public | تنفيذ جزء متعلق بـ المملوك للاختبار / الصنف / المخزن مؤقتًا، حسب المدخلات والتخصص. |
| `public void slAddPicker(String name)` | public | إضافة نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void slPickerSearch(String field, String value, String expectedName)` | public | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void slPickerCategory(String category, String expectedName)` | public | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `public List<String> slPickerRows()` | public | تنفيذ جزء متعلق بـ نافذة الاختيار / الصفوف، حسب المدخلات والتخصص. |
| `public void slClosePicker()` | public | إغلاق نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void slQuantity(String name, String quantity)` | public | ضبط الكمية، حسب المدخلات والتخصص. |
| `public void slRemoveLine(String name)` | public | إزالة البند، حسب المدخلات والتخصص. |
| `public void slCapture(String marker, Object storeId, Object itemId, String endpoint)` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> slSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> slAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public void slSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String slSaveErrors()` | public | حفظ الأخطاء، حسب المدخلات والتخصص. |
| `public String slDiagnostics()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String slCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public void slEdit(String route, Object id, String marker, String itemName)` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String, Object> slDocument()` | public | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `public void slFormReady()` | public | تنفيذ جزء متعلق بـ النموذج / الجاهزية، حسب المدخلات والتخصص. |
| `public String slDocumentDate()` | public | تنفيذ جزء متعلق بـ المستند / التاريخ، حسب المدخلات والتخصص. |
| `public void slSetLine(String name, String column, String value)` | public | تعيين البند، حسب المدخلات والتخصص. |
| `public Map<String, String> slTotals()` | public | تنفيذ جزء متعلق بـ الإجماليات، حسب المدخلات والتخصص. |
| `public void slWaitNet(String value)` | public | انتظار الصافي، حسب المدخلات والتخصص. |
| `public void slDiscount(String value, boolean percentage)` | public | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `public void slAddOtherPayment(String method, String amount)` | public | إضافة السداد، حسب المدخلات والتخصص. |
| `public void slNew()` | public | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String slNotesValue()` | public | ضبط الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `public void slLinkedReturn(Object invoiceId, String itemName)` | public | تنفيذ جزء متعلق بـ المرتجع، حسب المدخلات والتخصص. |
| `public void slListScope(String customer)` | public | تنفيذ جزء متعلق بـ النطاق، حسب المدخلات والتخصص. |
| `public void slAwaitListReady()` | public | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `public void slApplyListFilters()` | public | تنفيذ جزء متعلق بـ الفلاتر، حسب المدخلات والتخصص. |
| `public String slListFixtureDate()` | public | تنفيذ جزء متعلق بـ بيانات الاختبار / التاريخ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> slListRows()` | public | تنفيذ جزء متعلق بـ الصفوف، حسب المدخلات والتخصص. |
| `public void slWaitListIds(Set<Object> expected)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void slListText(String name, String text)` | public | تنفيذ جزء متعلق بـ النص، حسب المدخلات والتخصص. |
| `public void slListRadio(String group, String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public List<String> slHeaders()` | public | تنفيذ جزء متعلق بـ عناوين الأعمدة، حسب المدخلات والتخصص. |
| `public void slExport(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `public String slWindowHandle()` | public | تنفيذ جزء متعلق بـ التبويب، حسب المدخلات والتخصص. |
| `public void slCloseAuxiliaryWindows(String main)` | public | إغلاق الإضافي، حسب المدخلات والتخصص. |
| `public void slVerifyPrintedReport(String main, String item)` | public | التحقق من التقرير، حسب المدخلات والتخصص. |
| `public void slVerifyPrintedReport(String main, String item, Runnable contentAssertions)` | public | التحقق من التقرير، حسب المدخلات والتخصص. |
| `FormInteractions formInteractions()` | داخل الحزمة | تنفيذ جزء متعلق بـ النموذج، حسب المدخلات والتخصص. |
| `void selectPaymentMode(String value)` | داخل الحزمة | اختيار السداد، حسب المدخلات والتخصص. |

<a id="file-3f26f1218051"></a>
## SalesOrderPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesOrderPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesOrderPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 9 · **الأب المباشر:** `SalesModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public SalesOrderPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-72c1e0b27bdd"></a>
## SalesPrintWindows.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesPrintWindows.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesPrintWindows.java)

**دوره:** تنسيق تبويب الطباعة والرجوع للرئيسي واستدعاء مراجعة المحتوى في مجال المستند.

**حالته:** مصدر نشط · **عدد أسطره:** 57

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesPrintWindows(SalesModulePage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `String slWindowHandle()` | داخل الحزمة | تنفيذ جزء متعلق بـ التبويب، حسب المدخلات والتخصص. |
| `void slCloseAuxiliaryWindows(String main)` | داخل الحزمة | إغلاق الإضافي، حسب المدخلات والتخصص. |
| `void slVerifyPrintedReport(String main, String item)` | داخل الحزمة | التحقق من التقرير، حسب المدخلات والتخصص. |
| `void slVerifyPrintedReport(String main, String item, Runnable contentAssertions)` | داخل الحزمة | التحقق من التقرير، حسب المدخلات والتخصص. |

<a id="file-54d29c21b2f2"></a>
## SalesReturnPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesReturnPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesReturnPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 9 · **الأب المباشر:** `SalesModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)

**ملفات تشير إليه مباشرة:** [SalesEndToEndTest.java](TEST_Invoice_AR.md#file-eb54b46bbc4a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public SalesReturnPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-e02c06b50fab"></a>
## SalesSaveCapture.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesSaveCapture.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Invoice/SalesSaveCapture.java)

**دوره:** تتبع محاولات ونتائج الحفظ المملوكة لهذا المجال؛ منفصل عن نقر زر الحفظ ومقارنة قواعد العمل.

**حالته:** مصدر نشط · **عدد أسطره:** 65

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesSaveCapture(SalesModulePage owner)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void slCapture(String marker, Object storeId, Object itemId, String endpoint)` | داخل الحزمة | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> slSaves()` | داخل الحزمة | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> slAttempts()` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `void slSettled()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String slSaveErrors()` | داخل الحزمة | حفظ الأخطاء، حسب المدخلات والتخصص. |
| `String slDiagnostics()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String slCaptureMarker()` | داخل الحزمة | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
