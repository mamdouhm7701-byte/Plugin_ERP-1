# صفحات ومكونات القيود اليومية

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-e9a6a9908346"></a>
## VoucherAccountPicker.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VoucherAccountPicker.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VoucherAccountPicker.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 131

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `VoucherAccountPicker(VouchersPage page, WebDriver driver, Plugin_ERP.Bots.ElementActions utilites, Plugin_ERP.Bots.WaitManagement waitMangment)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void openAccountPicker()` | داخل الحزمة | فتح الحساب / نافذة الاختيار، حسب المدخلات والتخصص. |
| `void searchAccount(String query)` | داخل الحزمة | البحث عن الحساب، حسب المدخلات والتخصص. |
| `int getAccountSearchResultCount()` | داخل الحزمة | قراءة أو إرجاع الحساب، حسب المدخلات والتخصص. |
| `boolean accountSearchContains(String text)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `int getAccountPickerRowCount()` | داخل الحزمة | قراءة أو إرجاع الحساب / نافذة الاختيار / الصف، حسب المدخلات والتخصص. |
| `void selectAccountByCode(String accountCode)` | داخل الحزمة | اختيار الحساب / الكود، حسب المدخلات والتخصص. |
| `void searchAccountAndSelectFirst(String query)` | داخل الحزمة | البحث عن الحساب / الأول، حسب المدخلات والتخصص. |
| `void closeAccountPicker()` | داخل الحزمة | إغلاق الحساب / نافذة الاختيار، حسب المدخلات والتخصص. |
| `void openFirstAccountPicker()` | داخل الحزمة | فتح الأول / الحساب / نافذة الاختيار، حسب المدخلات والتخصص. |
| `void selectAccountFromFirstRow(String accountText)` | داخل الحزمة | اختيار الحساب / الأول / الصف، حسب المدخلات والتخصص. |
| `boolean isAccountDropdownSearchPresent()` | داخل الحزمة | السؤال عن حالة الحساب / القائمة المنسدلة، حسب المدخلات والتخصص. |
| `void assertLineAccountSearch(String matchingCode,String otherCode)` | داخل الحزمة | مقارنة متوقعة البند / الحساب، حسب المدخلات والتخصص. |

<a id="file-a7ee48718ff7"></a>
## VoucherLines.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VoucherLines.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VoucherLines.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 222

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `VoucherLines(VouchersPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `int getSelectedLineCount()` | داخل الحزمة | قراءة أو إرجاع القيمة المختارة / البند، حسب المدخلات والتخصص. |
| `By lineAmountInput(int lineIndex, int columnIndex)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند / المبلغ، حسب المدخلات والتخصص. |
| `By selectedLine(int lineIndex)` | داخل الحزمة | تنفيذ جزء متعلق بـ القيمة المختارة / البند، حسب المدخلات والتخصص. |
| `By lineElement(int lineIndex, String selector)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند / عنصر الشاشة، حسب المدخلات والتخصص. |
| `int getSettlementCheckboxCount()` | داخل الحزمة | قراءة أو إرجاع التسوية / مربع الاختيار، حسب المدخلات والتخصص. |
| `void setSettlementChecked(boolean checked)` | داخل الحزمة | تعيين التسوية / حالة التحديد، حسب المدخلات والتخصص. |
| `boolean isSettlementChecked(int lineIndex)` | داخل الحزمة | السؤال عن حالة التسوية / حالة التحديد، حسب المدخلات والتخصص. |
| `By settlementCheckbox()` | داخل الحزمة | تنفيذ جزء متعلق بـ التسوية / مربع الاختيار، حسب المدخلات والتخصص. |
| `boolean hasTaxDistributionAction(int lineIndex)` | داخل الحزمة | السؤال عن وجود الضريبة، حسب المدخلات والتخصص. |
| `void openTaxDistribution(int lineIndex)` | داخل الحزمة | فتح الضريبة، حسب المدخلات والتخصص. |
| `BigDecimal getTaxAmountValue(int lineIndex)` | داخل الحزمة | قراءة أو إرجاع الضريبة / المبلغ / القيمة، حسب المدخلات والتخصص. |
| `void setDebit(int lineIndex, String amount)` | داخل الحزمة | تعيين المدين، حسب المدخلات والتخصص. |
| `void setCredit(int lineIndex, String amount)` | داخل الحزمة | تعيين الدائن، حسب المدخلات والتخصص. |
| `String getDebitValue(int lineIndex)` | داخل الحزمة | قراءة أو إرجاع المدين / القيمة، حسب المدخلات والتخصص. |
| `String getCreditValue(int lineIndex)` | داخل الحزمة | قراءة أو إرجاع الدائن / القيمة، حسب المدخلات والتخصص. |
| `boolean isNumericAmountInput(By locator)` | داخل الحزمة | السؤال عن حالة المبلغ، حسب المدخلات والتخصص. |
| `boolean isLineAccountSelected(int lineIndex)` | داخل الحزمة | السؤال عن حالة البند / الحساب / القيمة المختارة، حسب المدخلات والتخصص. |
| `void setLineNotes(int lineIndex, String notes)` | داخل الحزمة | تعيين البند / الملاحظات، حسب المدخلات والتخصص. |
| `String getLineNotes(int lineIndex)` | داخل الحزمة | قراءة أو إرجاع البند / الملاحظات، حسب المدخلات والتخصص. |
| `By lineNoteInput(int lineIndex)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `void setInputValue(By locator, String value)` | داخل الحزمة | تعيين القيمة، حسب المدخلات والتخصص. |
| `void removeLine(int lineIndex)` | داخل الحزمة | إزالة البند، حسب المدخلات والتخصص. |
| `BigDecimal getDebitTotal()` | داخل الحزمة | قراءة أو إرجاع المدين / الإجمالي، حسب المدخلات والتخصص. |
| `BigDecimal getCreditTotal()` | داخل الحزمة | قراءة أو إرجاع الدائن / الإجمالي، حسب المدخلات والتخصص. |
| `BigDecimal readTotal(int columnIndex)` | داخل الحزمة | قراءة الإجمالي، حسب المدخلات والتخصص. |
| `void assertTotals(String debit, String credit)` | داخل الحزمة | مقارنة متوقعة الإجماليات، حسب المدخلات والتخصص. |
| `boolean isBalanced()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-631d4ca0368e"></a>
## VoucherRecordList.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VoucherRecordList.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VoucherRecordList.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 96

**يرتبط مباشرة بملفات داخل المشروع:** [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `VoucherRecordList(VouchersPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean hasVoucherMarkerInList(String marker)` | داخل الحزمة | السؤال عن وجود علامة الملكية، حسب المدخلات والتخصص. |
| `boolean hasVoucherMarkerPrefixInList(String markerPrefix)` | داخل الحزمة | السؤال عن وجود علامة الملكية، حسب المدخلات والتخصص. |
| `List<String> getVoucherRowsContaining(String marker)` | داخل الحزمة | قراءة أو إرجاع الصفوف، حسب المدخلات والتخصص. |
| `void deleteVoucherRowsContaining(String markerPrefix)` | داخل الحزمة | حذف الصفوف، حسب المدخلات والتخصص. |
| `void editVoucherListRowByMarker(String marker)` | داخل الحزمة | تعديل الصف / علامة الملكية، حسب المدخلات والتخصص. |
| `void deleteVoucherListRowByMarker(String marker)` | داخل الحزمة | حذف الصف / علامة الملكية، حسب المدخلات والتخصص. |

<a id="file-4d6d58ffec6e"></a>
## VouchersPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VouchersPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/GenralAccountant/VouchersPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 352 · **الأب المباشر:** `Abstract`

**يرتبط مباشرة بملفات داخل المشروع:** [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)

**ملفات تشير إليه مباشرة:** [VouchersTest.java](TEST_GenralAccountantTest_AR.md#file-6e939ee3b900)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public VouchersPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public int getSelectedLineCount()` | public | قراءة أو إرجاع القيمة المختارة / البند، حسب المدخلات والتخصص. |
| `public void openAccountPicker()` | public | فتح الحساب / نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void searchAccount(String query)` | public | البحث عن الحساب، حسب المدخلات والتخصص. |
| `public int getAccountSearchResultCount()` | public | قراءة أو إرجاع الحساب، حسب المدخلات والتخصص. |
| `public boolean accountSearchContains(String text)` | public | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `public int getAccountPickerRowCount()` | public | قراءة أو إرجاع الحساب / نافذة الاختيار / الصف، حسب المدخلات والتخصص. |
| `public void selectAccountByCode(String accountCode)` | public | اختيار الحساب / الكود، حسب المدخلات والتخصص. |
| `public void searchAccountAndSelectFirst(String query)` | public | البحث عن الحساب / الأول، حسب المدخلات والتخصص. |
| `public void closeAccountPicker()` | public | إغلاق الحساب / نافذة الاختيار، حسب المدخلات والتخصص. |
| `public int getSettlementCheckboxCount()` | public | قراءة أو إرجاع التسوية / مربع الاختيار، حسب المدخلات والتخصص. |
| `public void setSettlementChecked(boolean checked)` | public | تعيين التسوية / حالة التحديد، حسب المدخلات والتخصص. |
| `public boolean isSettlementChecked(int lineIndex)` | public | السؤال عن حالة التسوية / حالة التحديد، حسب المدخلات والتخصص. |
| `public void selectTaxAccountFromLine(String accountText)` | public | اختيار الضريبة / الحساب / البند، حسب المدخلات والتخصص. |
| `public void selectAccountTaxableByCode(String accountCode)` | public | اختيار الحساب / الكود، حسب المدخلات والتخصص. |
| `public boolean hasTaxDistributionAction(int lineIndex)` | public | السؤال عن وجود الضريبة، حسب المدخلات والتخصص. |
| `public void openTaxDistribution(int lineIndex)` | public | فتح الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getTaxAmountValue(int lineIndex)` | public | قراءة أو إرجاع الضريبة / المبلغ / القيمة، حسب المدخلات والتخصص. |
| `public void setDebit(int lineIndex, String amount)` | public | تعيين المدين، حسب المدخلات والتخصص. |
| `public void setCredit(int lineIndex, String amount)` | public | تعيين الدائن، حسب المدخلات والتخصص. |
| `public String getDebitValue(int lineIndex)` | public | قراءة أو إرجاع المدين / القيمة، حسب المدخلات والتخصص. |
| `public String getCreditValue(int lineIndex)` | public | قراءة أو إرجاع الدائن / القيمة، حسب المدخلات والتخصص. |
| `public boolean isNumericAmountInput(By locator)` | public | السؤال عن حالة المبلغ، حسب المدخلات والتخصص. |
| `public void selectAccountFromLineDropdown(int lineIndex, String accountText)` | public | اختيار الحساب / البند / القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public String getVoucherReportText()` | public | قراءة أو إرجاع التقرير / النص، حسب المدخلات والتخصص. |
| `public boolean isLineAccountSelected(int lineIndex)` | public | السؤال عن حالة البند / الحساب / القيمة المختارة، حسب المدخلات والتخصص. |
| `public void setLineNotes(int lineIndex, String notes)` | public | تعيين البند / الملاحظات، حسب المدخلات والتخصص. |
| `public String getLineNotes(int lineIndex)` | public | قراءة أو إرجاع البند / الملاحظات، حسب المدخلات والتخصص. |
| `public boolean isVoucherFormDisplayed()` | public | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `public void waitForFormReady()` | public | انتظار النموذج / الجاهزية، حسب المدخلات والتخصص. |
| `public boolean isAccountDropdownSearchPresent()` | public | السؤال عن حالة الحساب / القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public void assertLineAccountSearch(String matchingCode,String otherCode)` | public | مقارنة متوقعة البند / الحساب، حسب المدخلات والتخصص. |
| `public void searchVoucherLines(String query)` | public | البحث عن البنود، حسب المدخلات والتخصص. |
| `public boolean isErrorPopupDisplayed()` | public | السؤال عن حالة الخطأ / الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `public boolean isDateInIsoFormat()` | public | السؤال عن حالة التاريخ، حسب المدخلات والتخصص. |
| `public boolean isVoucherDateValid()` | public | السؤال عن حالة التاريخ، حسب المدخلات والتخصص. |
| `public void removeLine(int lineIndex)` | public | إزالة البند، حسب المدخلات والتخصص. |
| `public String getNotes()` | public | قراءة أو إرجاع الملاحظات، حسب المدخلات والتخصص. |
| `public void setNotes(String notes)` | public | تعيين الملاحظات، حسب المدخلات والتخصص. |
| `public String getVoucherDate()` | public | قراءة أو إرجاع التاريخ، حسب المدخلات والتخصص. |
| `public BigDecimal getDebitTotal()` | public | قراءة أو إرجاع المدين / الإجمالي، حسب المدخلات والتخصص. |
| `public BigDecimal getCreditTotal()` | public | قراءة أو إرجاع الدائن / الإجمالي، حسب المدخلات والتخصص. |
| `public void assertTotals(String debit, String credit)` | public | مقارنة متوقعة الإجماليات، حسب المدخلات والتخصص. |
| `public boolean isBalanced()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean hasValidationErrors()` | public | السؤال عن وجود التحقق / الأخطاء، حسب المدخلات والتخصص. |
| `public boolean isSaveAvailable()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean isSaveAndPrintAvailable()` | public | السؤال عن حالة الطباعة، حسب المدخلات والتخصص. |
| `public boolean isReferenceReadOnly()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean isTransactionTypeReadOnly()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void openFirstAccountPicker()` | public | فتح الأول / الحساب / نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void selectAccountFromFirstRow(String accountText)` | public | اختيار الحساب / الأول / الصف، حسب المدخلات والتخصص. |
| `public boolean hasVoucherMarkerInList(String marker)` | public | السؤال عن وجود علامة الملكية، حسب المدخلات والتخصص. |
| `public boolean hasVoucherMarkerPrefixInList(String markerPrefix)` | public | السؤال عن وجود علامة الملكية، حسب المدخلات والتخصص. |
| `public List<String> getVoucherRowsContaining(String marker)` | public | قراءة أو إرجاع الصفوف، حسب المدخلات والتخصص. |
| `public void deleteVoucherRowsContaining(String markerPrefix)` | public | حذف الصفوف، حسب المدخلات والتخصص. |
| `public void editVoucherListRowByMarker(String marker)` | public | تعديل الصف / علامة الملكية، حسب المدخلات والتخصص. |
| `public void deleteVoucherListRowByMarker(String marker)` | public | حذف الصف / علامة الملكية، حسب المدخلات والتخصص. |
| `void clickLineElement(By locator)` | داخل الحزمة | ضغط إجراء النقر / البند / عنصر الشاشة، حسب المدخلات والتخصص. |
| `Plugin_ERP.Bots.WaitManagement lineWaits()` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
