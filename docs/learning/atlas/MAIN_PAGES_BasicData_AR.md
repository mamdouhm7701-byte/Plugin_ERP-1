# صفحات ومكونات البيانات الأساسية

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-0c373f9ce1c1"></a>
## AreaGridControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/AreaGridControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/AreaGridControls.java)

**دوره:** قراءة وترتيب وتنقل جدول الشاشة وتغيير حجمه أو تصديره بحسب قدراته.

**حالته:** مصدر نشط · **عدد أسطره:** 213

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `AreaGridControls(AreaPage page, WebDriver driver, Runnable awaitRows, java.util.function.Function<String, WebElement> rowFinder)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void waitForAreaRows()` | private | انتظار المنطقة / الصفوف، حسب المدخلات والتخصص. |
| `private WebElement findAreaRow(String name)` | private | البحث عن المنطقة / الصف، حسب المدخلات والتخصص. |
| `int GetVisibleAreaRowCount()` | داخل الحزمة | قراءة أو إرجاع الظاهر / المنطقة / الصف، حسب المدخلات والتخصص. |
| `int GetExactAreaMatchCount(String name)` | داخل الحزمة | قراءة أو إرجاع المنطقة، حسب المدخلات والتخصص. |
| `String GetFirstAreaCellByHeader(String headerName)` | داخل الحزمة | قراءة أو إرجاع الأول / المنطقة، حسب المدخلات والتخصص. |
| `String GetAreaCellByName(String name, String headerName)` | داخل الحزمة | قراءة أو إرجاع المنطقة / الاسم، حسب المدخلات والتخصص. |
| `java.util.List<String> GetAreaColumnValues(String headerName)` | داخل الحزمة | قراءة أو إرجاع المنطقة / القيم، حسب المدخلات والتخصص. |
| `int getColumnIndex(String headerName)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetAreaPageSize()` | داخل الحزمة | قراءة أو إرجاع المنطقة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `int GetAlternativeAreaPageSize(int current)` | داخل الحزمة | قراءة أو إرجاع المنطقة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `void SelectAreaPageSize(int size)` | داخل الحزمة | اختيار المنطقة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `WebElement getPageSizeSelect()` | داخل الحزمة | قراءة أو إرجاع الصفحة / الحجم، حسب المدخلات والتخصص. |
| `void GoToNextAreaPage()` | داخل الحزمة | تنفيذ جزء متعلق بـ التالي / المنطقة / الصفحة، حسب المدخلات والتخصص. |
| `void GoToPreviousAreaPage()` | داخل الحزمة | تنفيذ جزء متعلق بـ السابق / المنطقة / الصفحة، حسب المدخلات والتخصص. |
| `boolean IsNextAreaPageEnabled()` | داخل الحزمة | السؤال عن حالة التالي / المنطقة / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `void clickPageButton(boolean next)` | داخل الحزمة | ضغط إجراء النقر / الصفحة / الزر، حسب المدخلات والتخصص. |
| `java.util.List<WebElement> getPageButtons()` | داخل الحزمة | قراءة أو إرجاع الصفحة، حسب المدخلات والتخصص. |
| `boolean isPageButtonEnabled(WebElement button)` | داخل الحزمة | السؤال عن حالة الصفحة / الزر / الإتاحة، حسب المدخلات والتخصص. |
| `void ClickAreaColumnHeader(String headerName)` | داخل الحزمة | ضغط إجراء النقر / المنطقة، حسب المدخلات والتخصص. |
| `java.util.List<String> SortAreaCodes(java.util.List<String> values, boolean ascending)` | داخل الحزمة | فرز الفرز / المنطقة، حسب المدخلات والتخصص. |

<a id="file-3f7bf8c58091"></a>
## AreaPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/AreaPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/AreaPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 292 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AreaTest.java](TEST_Tests_AR.md#file-bc1b1188af8f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AreaPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenAreaPage()` | public | فتح المنطقة / الصفحة، حسب المدخلات والتخصص. |
| `public void SearchArea(String name)` | public | البحث عن المنطقة، حسب المدخلات والتخصص. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void waitForAreaSaveSuccessPopupToDisappear()` | private | انتظار المنطقة / الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `public void EditAreaByName(String name)` | public | تعديل المنطقة / الاسم، حسب المدخلات والتخصص. |
| `public void DeleteAreaByName(String name)` | public | حذف المنطقة / الاسم، حسب المدخلات والتخصص. |
| `public boolean IsAreaDisplayed(String name)` | public | السؤال عن حالة المنطقة، حسب المدخلات والتخصص. |
| `public boolean IsAreaArabicNameRequiredDisplayed()` | public | السؤال عن حالة المنطقة / العربي / الاسم، حسب المدخلات والتخصص. |
| `private WebElement findAreaRow(String name)` | private | البحث عن المنطقة / الصف، حسب المدخلات والتخصص. |
| `public boolean IsAreaListStructureDisplayed()` | public | السؤال عن حالة المنطقة، حسب المدخلات والتخصص. |
| `public String GetMissingAreaFormControls()` | public | قراءة أو إرجاع المنطقة / النموذج، حسب المدخلات والتخصص. |
| `public boolean IsAreaCreationFormDisplayed()` | public | السؤال عن حالة المنطقة / النموذج، حسب المدخلات والتخصص. |
| `public int GetVisibleAreaRowCount()` | public | قراءة أو إرجاع الظاهر / المنطقة / الصف، حسب المدخلات والتخصص. |
| `public int GetExactAreaMatchCount(String name)` | public | قراءة أو إرجاع المنطقة، حسب المدخلات والتخصص. |
| `public String GetFirstAreaCellByHeader(String headerName)` | public | قراءة أو إرجاع الأول / المنطقة، حسب المدخلات والتخصص. |
| `public String GetAreaCellByName(String name, String headerName)` | public | قراءة أو إرجاع المنطقة / الاسم، حسب المدخلات والتخصص. |
| `public java.util.List<String> GetAreaColumnValues(String headerName)` | public | قراءة أو إرجاع المنطقة / القيم، حسب المدخلات والتخصص. |
| `public void OpenFirstAreaForEdit()` | public | فتح الأول / المنطقة، حسب المدخلات والتخصص. |
| `public String GetAreaCodeInputValue()` | public | قراءة أو إرجاع المنطقة / الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameInputValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameInputValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetAreaNotesValue()` | public | قراءة أو إرجاع المنطقة / الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `public int GetAreaPageSize()` | public | قراءة أو إرجاع المنطقة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public int GetAlternativeAreaPageSize(int current)` | public | قراءة أو إرجاع المنطقة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public void SelectAreaPageSize(int size)` | public | اختيار المنطقة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public void GoToNextAreaPage()` | public | تنفيذ جزء متعلق بـ التالي / المنطقة / الصفحة، حسب المدخلات والتخصص. |
| `public void GoToPreviousAreaPage()` | public | تنفيذ جزء متعلق بـ السابق / المنطقة / الصفحة، حسب المدخلات والتخصص. |
| `public boolean IsNextAreaPageEnabled()` | public | السؤال عن حالة التالي / المنطقة / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `public void ClickAreaColumnHeader(String headerName)` | public | ضغط إجراء النقر / المنطقة، حسب المدخلات والتخصص. |
| `public java.util.List<String> SortAreaCodes(java.util.List<String> values, boolean ascending)` | public | فرز الفرز / المنطقة، حسب المدخلات والتخصص. |
| `public void RequestDeleteAreaByName(String name)` | public | طلب إجراء المنطقة / الاسم، حسب المدخلات والتخصص. |
| `public boolean IsAreaDeleteConfirmationDisplayed()` | public | السؤال عن حالة المنطقة، حسب المدخلات والتخصص. |
| `public void ConfirmAreaDelete()` | public | تأكيد التأكيد / المنطقة، حسب المدخلات والتخصص. |
| `public void CancelAreaDelete()` | public | إلغاء الإلغاء / المنطقة، حسب المدخلات والتخصص. |
| `public void ClickSaveTwiceRapidly()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `private void waitForAreaRows()` | private | انتظار المنطقة / الصفوف، حسب المدخلات والتخصص. |
| `void recordWaitForAreaSaveSuccessPopupToDisappear()` | داخل الحزمة | تسجيل/تمرير المنطقة / الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `void recordWaitForAreaRows()` | داخل الحزمة | تسجيل/تمرير المنطقة / الصفوف، حسب المدخلات والتخصص. |

<a id="file-3440e2c7639b"></a>
## AreaRecordActions.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/AreaRecordActions.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/AreaRecordActions.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 171

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `AreaRecordActions(AreaPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void waitForAreaRows()` | private | انتظار المنطقة / الصفوف، حسب المدخلات والتخصص. |
| `void SearchArea(String name)` | داخل الحزمة | البحث عن المنطقة، حسب المدخلات والتخصص. |
| `void EditAreaByName(String name)` | داخل الحزمة | تعديل المنطقة / الاسم، حسب المدخلات والتخصص. |
| `void DeleteAreaByName(String name)` | داخل الحزمة | حذف المنطقة / الاسم، حسب المدخلات والتخصص. |
| `boolean IsAreaDisplayed(String name)` | داخل الحزمة | السؤال عن حالة المنطقة، حسب المدخلات والتخصص. |
| `WebElement findAreaRow(String name)` | داخل الحزمة | البحث عن المنطقة / الصف، حسب المدخلات والتخصص. |
| `void OpenFirstAreaForEdit()` | داخل الحزمة | فتح الأول / المنطقة، حسب المدخلات والتخصص. |
| `void RequestDeleteAreaByName(String name)` | داخل الحزمة | طلب إجراء المنطقة / الاسم، حسب المدخلات والتخصص. |
| `boolean IsAreaDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة المنطقة، حسب المدخلات والتخصص. |
| `void ConfirmAreaDelete()` | داخل الحزمة | تأكيد التأكيد / المنطقة، حسب المدخلات والتخصص. |
| `void CancelAreaDelete()` | داخل الحزمة | إلغاء الإلغاء / المنطقة، حسب المدخلات والتخصص. |
| `void dismissDeleteSuccessPopup()` | داخل الحزمة | إغلاق تنبيه الرسالة المنبثقة، حسب المدخلات والتخصص. |

<a id="file-fe24b8cdd695"></a>
## BankPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/BankPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/BankPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 265 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [BankTest.java](TEST_Tests_AR.md#file-44bcd35d3f70)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public BankPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By locator)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitRows()` | private | انتظار الصفوف، حسب المدخلات والتخصص. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `public void SearchBank(String value)` | public | البحث عن البنك، حسب المدخلات والتخصص. |
| `private void waitAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `private void dismissSaveSuccessIfPresent()` | private | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsBankListStructureDisplayed()` | public | السؤال عن حالة البنك، حسب المدخلات والتخصص. |
| `public String GetMissingBankFormControls()` | public | قراءة أو إرجاع البنك / النموذج، حسب المدخلات والتخصص. |
| `public int GetEditableBankCount()` | public | قراءة أو إرجاع البنك، حسب المدخلات والتخصص. |
| `public int GetExactBankMatchCount(String name)` | public | قراءة أو إرجاع البنك، حسب المدخلات والتخصص. |
| `public String GetFirstBankCellByHeader(String label)` | public | قراءة أو إرجاع الأول / البنك، حسب المدخلات والتخصص. |
| `public void OpenFirstBankForEdit()` | public | فتح الأول / البنك، حسب المدخلات والتخصص. |
| `public void OpenBankForEditByName(String name)` | public | فتح البنك / الاسم، حسب المدخلات والتخصص. |
| `private String value(By locator)` | private | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEmailValue()` | public | قراءة أو إرجاع القيمة، حسب المدخلات والتخصص. |
| `public String GetAddressValue()` | public | قراءة أو إرجاع القيمة، حسب المدخلات والتخصص. |
| `public String GetBankAccountValue()` | public | قراءة أو إرجاع البنك / الحساب / القيمة، حسب المدخلات والتخصص. |
| `public String GetIbanValue()` | public | قراءة أو إرجاع القيمة، حسب المدخلات والتخصص. |
| `public String GetBankCodeValue()` | public | قراءة أو إرجاع البنك / الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetNotesValue()` | public | قراءة أو إرجاع الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `public boolean IsBankArabicNameRequiredDisplayed()` | public | السؤال عن حالة البنك / العربي / الاسم، حسب المدخلات والتخصص. |
| `public boolean IsInvalidBankEmailDisplayed()` | public | السؤال عن حالة البنك، حسب المدخلات والتخصص. |
| `public void RequestDeleteBankByName(String name)` | public | طلب إجراء البنك / الاسم، حسب المدخلات والتخصص. |
| `public boolean IsBankDeleteConfirmationDisplayed()` | public | السؤال عن حالة البنك، حسب المدخلات والتخصص. |
| `public void ConfirmBankDelete()` | public | تأكيد التأكيد / البنك، حسب المدخلات والتخصص. |
| `public void CancelBankDelete()` | public | إلغاء الإلغاء / البنك، حسب المدخلات والتخصص. |
| `boolean recordVisible(By locator)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitRows()` | داخل الحزمة | تسجيل/تمرير الصفوف، حسب المدخلات والتخصص. |
| `void recordWaitAlertToDisappear()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |
| `void recordDismissSaveSuccessIfPresent()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c52c85fe5e8c"></a>
## BankRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/BankRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/BankRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 200

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `BankRecords(BankPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By locator)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitRows()` | private | انتظار الصفوف، حسب المدخلات والتخصص. |
| `private void waitAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `private void dismissSaveSuccessIfPresent()` | private | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void SearchBank(String value)` | داخل الحزمة | البحث عن البنك، حسب المدخلات والتخصص. |
| `boolean IsBankListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة البنك، حسب المدخلات والتخصص. |
| `int GetEditableBankCount()` | داخل الحزمة | قراءة أو إرجاع البنك، حسب المدخلات والتخصص. |
| `WebElement findRow(String name)` | داخل الحزمة | البحث عن الصف، حسب المدخلات والتخصص. |
| `int GetExactBankMatchCount(String name)` | داخل الحزمة | قراءة أو إرجاع البنك، حسب المدخلات والتخصص. |
| `int columnIndex(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String GetFirstBankCellByHeader(String label)` | داخل الحزمة | قراءة أو إرجاع الأول / البنك، حسب المدخلات والتخصص. |
| `void OpenFirstBankForEdit()` | داخل الحزمة | فتح الأول / البنك، حسب المدخلات والتخصص. |
| `void OpenBankForEditByName(String name)` | داخل الحزمة | فتح البنك / الاسم، حسب المدخلات والتخصص. |
| `void openEdit(WebElement row)` | داخل الحزمة | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void RequestDeleteBankByName(String name)` | داخل الحزمة | طلب إجراء البنك / الاسم، حسب المدخلات والتخصص. |
| `boolean IsBankDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة البنك، حسب المدخلات والتخصص. |
| `void ConfirmBankDelete()` | داخل الحزمة | تأكيد التأكيد / البنك، حسب المدخلات والتخصص. |
| `void CancelBankDelete()` | داخل الحزمة | إلغاء الإلغاء / البنك، حسب المدخلات والتخصص. |

<a id="file-0efcc911edbb"></a>
## CashFormReader.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashFormReader.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashFormReader.java)

**دوره:** عناصر وقراءات نموذج محدد؛ فصل عن القائمة وملكية بيانات السيناريو.

**حالته:** مصدر نشط · **عدد أسطره:** 138

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `CashFormReader(CashPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean isVisible(By locator)` | private | السؤال عن حالة الظاهر، حسب المدخلات والتخصص. |
| `boolean isCashCreationFormDisplayed()` | داخل الحزمة | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `boolean isSafeGeneralChecked()` | داخل الحزمة | السؤال عن حالة الخزنة / حالة التحديد، حسب المدخلات والتخصص. |
| `String getMissingCashCreationFormControls()` | داخل الحزمة | قراءة أو إرجاع النموذج، حسب المدخلات والتخصص. |
| `String getArabicNameInputValue()` | داخل الحزمة | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `String getEnglishNameInputValue()` | داخل الحزمة | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `String getNotesValue()` | داخل الحزمة | قراءة أو إرجاع الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `int getNotesMaxLength()` | داخل الحزمة | قراءة أو إرجاع الملاحظات، حسب المدخلات والتخصص. |
| `WebElement findNotesField()` | داخل الحزمة | البحث عن الملاحظات، حسب المدخلات والتخصص. |
| `String getSafeCodeInputValue()` | داخل الحزمة | قراءة أو إرجاع الخزنة / الكود / القيمة، حسب المدخلات والتخصص. |
| `String getSafeAccountValue()` | داخل الحزمة | قراءة أو إرجاع الخزنة / الحساب / القيمة، حسب المدخلات والتخصص. |

<a id="file-e3434a34985b"></a>
## CashListControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashListControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashListControls.java)

**دوره:** قراءة وترتيب وتنقل جدول الشاشة وتغيير حجمه أو تصديره بحسب قدراته.

**حالته:** مصدر نشط · **عدد أسطره:** 302

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `CashListControls(CashPage page, WebDriver driver, Runnable awaitLoading)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void waitForCashPageLoading()` | private | انتظار الصفحة، حسب المدخلات والتخصص. |
| `void awaitReady()` | داخل الحزمة | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `int getVisibleSafeRowCount()` | داخل الحزمة | قراءة أو إرجاع الظاهر / الخزنة / الصف، حسب المدخلات والتخصص. |
| `int getSelectedSafePageSize()` | داخل الحزمة | قراءة أو إرجاع القيمة المختارة / الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `int getAlternativeSafePageSize(int currentValue)` | داخل الحزمة | قراءة أو إرجاع الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `void selectSafePageSize(int pageSize)` | داخل الحزمة | اختيار الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `private WebElement findSafePageSizeSelect()` | private | البحث عن الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `int getSafeTotalRowCount()` | داخل الحزمة | قراءة أو إرجاع الخزنة / الإجمالي / الصف، حسب المدخلات والتخصص. |
| `boolean isSafeNextPageEnabled()` | داخل الحزمة | السؤال عن حالة الخزنة / التالي / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `void goToNextSafePage()` | داخل الحزمة | تنفيذ جزء متعلق بـ التالي / الخزنة / الصفحة، حسب المدخلات والتخصص. |
| `void goToPreviousSafePage()` | داخل الحزمة | تنفيذ جزء متعلق بـ السابق / الخزنة / الصفحة، حسب المدخلات والتخصص. |
| `private List<WebElement> getPaginationButtons()` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean isPaginationButtonEnabled(WebElement button)` | private | السؤال عن حالة الزر / الإتاحة، حسب المدخلات والتخصص. |
| `String getFirstSafeCellByHeader(String headerName)` | داخل الحزمة | قراءة أو إرجاع الأول / الخزنة، حسب المدخلات والتخصص. |
| `List<String> getSafeColumnValues(String headerName)` | داخل الحزمة | قراءة أو إرجاع الخزنة / القيم، حسب المدخلات والتخصص. |
| `List<String> sortCodes(List<String> codes, boolean ascending)` | داخل الحزمة | فرز الفرز، حسب المدخلات والتخصص. |
| `void clickSafeColumnHeader(String headerName)` | داخل الحزمة | ضغط إجراء النقر / الخزنة، حسب المدخلات والتخصص. |
| `void exportSafes(String format)` | داخل الحزمة | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-ed12e0c67c34"></a>
## CashPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 386 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [CashTest.java](TEST_Tests_AR.md#file-e758c254da3e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public CashPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void NavigationHelper(Pages route)` | public | فتح شاشة مسماة مع انتظارها وفق تنفيذ الصفحة. |
| `public void OpenCashPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public boolean isCashCreationFormDisplayed()` | public | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void dismissCashSaveSuccessPopupIfShown()` | private | إغلاق تنبيه الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `public void clickSaveTwiceRapidly()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public boolean isSafeGeneralChecked()` | public | السؤال عن حالة الخزنة / حالة التحديد، حسب المدخلات والتخصص. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public boolean isSafeListStructureDisplayed()` | public | السؤال عن حالة الخزنة، حسب المدخلات والتخصص. |
| `public String getMissingCashCreationFormControls()` | public | قراءة أو إرجاع النموذج، حسب المدخلات والتخصص. |
| `private boolean isVisible(By locator)` | private | السؤال عن حالة الظاهر، حسب المدخلات والتخصص. |
| `private void waitForCashPageLoading()` | private | انتظار الصفحة، حسب المدخلات والتخصص. |
| `private void waitForCashAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public void searchSafes(String searchText)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean isSafeVisibleInSearchResults(String safeText)` | public | السؤال عن حالة الخزنة / الظاهر، حسب المدخلات والتخصص. |
| `public int getExactSafeMatchCount(String safeName)` | public | قراءة أو إرجاع الخزنة، حسب المدخلات والتخصص. |
| `public void openSafeForEditByName(String safeName)` | public | فتح الخزنة / الاسم، حسب المدخلات والتخصص. |
| `public void requestDeleteSafeByName(String safeName)` | public | طلب إجراء الخزنة / الاسم، حسب المدخلات والتخصص. |
| `public boolean isSafeDeleteConfirmationDisplayed()` | public | السؤال عن حالة الخزنة، حسب المدخلات والتخصص. |
| `public void confirmSafeDelete()` | public | تأكيد التأكيد / الخزنة، حسب المدخلات والتخصص. |
| `public void cancelSafeDelete()` | public | إلغاء الإلغاء / الخزنة، حسب المدخلات والتخصص. |
| `public int getVisibleSafeRowCount()` | public | قراءة أو إرجاع الظاهر / الخزنة / الصف، حسب المدخلات والتخصص. |
| `public int getSelectedSafePageSize()` | public | قراءة أو إرجاع القيمة المختارة / الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public int getAlternativeSafePageSize(int currentValue)` | public | قراءة أو إرجاع الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public void selectSafePageSize(int pageSize)` | public | اختيار الخزنة / الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public int getSafeTotalRowCount()` | public | قراءة أو إرجاع الخزنة / الإجمالي / الصف، حسب المدخلات والتخصص. |
| `public boolean isSafeNextPageEnabled()` | public | السؤال عن حالة الخزنة / التالي / الصفحة / الإتاحة، حسب المدخلات والتخصص. |
| `public void goToNextSafePage()` | public | تنفيذ جزء متعلق بـ التالي / الخزنة / الصفحة، حسب المدخلات والتخصص. |
| `public void goToPreviousSafePage()` | public | تنفيذ جزء متعلق بـ السابق / الخزنة / الصفحة، حسب المدخلات والتخصص. |
| `public String getFirstSafeCellByHeader(String headerName)` | public | قراءة أو إرجاع الأول / الخزنة، حسب المدخلات والتخصص. |
| `public List<String> getSafeColumnValues(String headerName)` | public | قراءة أو إرجاع الخزنة / القيم، حسب المدخلات والتخصص. |
| `public List<String> sortCodes(List<String> codes, boolean ascending)` | public | فرز الفرز، حسب المدخلات والتخصص. |
| `public void clickSafeColumnHeader(String headerName)` | public | ضغط إجراء النقر / الخزنة، حسب المدخلات والتخصص. |
| `public void openFirstSafeForEdit()` | public | فتح الأول / الخزنة، حسب المدخلات والتخصص. |
| `public String getArabicNameInputValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String getEnglishNameInputValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String getNotesValue()` | public | قراءة أو إرجاع الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `public int getNotesMaxLength()` | public | قراءة أو إرجاع الملاحظات، حسب المدخلات والتخصص. |
| `public String getSafeCodeInputValue()` | public | قراءة أو إرجاع الخزنة / الكود / القيمة، حسب المدخلات والتخصص. |
| `public String getSafeAccountValue()` | public | قراءة أو إرجاع الخزنة / الحساب / القيمة، حسب المدخلات والتخصص. |
| `public void exportSafes(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `void awaitCashLoading()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void awaitCashAlert()` | داخل الحزمة | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `boolean recordIsVisible(By locator)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitForCashPageLoading()` | داخل الحزمة | تسجيل/تمرير الصفحة، حسب المدخلات والتخصص. |
| `void recordWaitForCashAlertToDisappear()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-8b9c75ad5055"></a>
## CashRecordActions.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashRecordActions.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/CashRecordActions.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 185

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `CashRecordActions(CashPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void waitForCashPageLoading()` | private | انتظار الصفحة، حسب المدخلات والتخصص. |
| `private void waitForCashAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `void searchSafes(String searchText)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean isSafeVisibleInSearchResults(String safeText)` | داخل الحزمة | السؤال عن حالة الخزنة / الظاهر، حسب المدخلات والتخصص. |
| `int getExactSafeMatchCount(String safeName)` | داخل الحزمة | قراءة أو إرجاع الخزنة، حسب المدخلات والتخصص. |
| `void openSafeForEditByName(String safeName)` | داخل الحزمة | فتح الخزنة / الاسم، حسب المدخلات والتخصص. |
| `void requestDeleteSafeByName(String safeName)` | داخل الحزمة | طلب إجراء الخزنة / الاسم، حسب المدخلات والتخصص. |
| `boolean isSafeDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة الخزنة، حسب المدخلات والتخصص. |
| `void confirmSafeDelete()` | داخل الحزمة | تأكيد التأكيد / الخزنة، حسب المدخلات والتخصص. |
| `void dismissCashDeleteSuccessPopupIfShown()` | داخل الحزمة | إغلاق تنبيه الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `void cancelSafeDelete()` | داخل الحزمة | إلغاء الإلغاء / الخزنة، حسب المدخلات والتخصص. |
| `WebElement findSafeRowByName(String safeName)` | داخل الحزمة | البحث عن الخزنة / الصف / الاسم، حسب المدخلات والتخصص. |

<a id="file-b20ba4a5652e"></a>
## DistributionEntityPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/DistributionEntityPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/DistributionEntityPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 216 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [DisEntTest.java](TEST_Tests_AR.md#file-2d1a897250de)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public DistributionEntityPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By locator)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `public void CloseFormIfOpen()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `public void Search(String text)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetMissingFormControls()` | public | قراءة أو إرجاع النموذج، حسب المدخلات والتخصص. |
| `public int GetEditableRowCount()` | public | قراءة أو إرجاع الصف، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String name)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetFirstCellByHeader(String title)` | public | قراءة أو إرجاع الأول، حسب المدخلات والتخصص. |
| `public void OpenFirstForEdit()` | public | فتح الأول، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String name)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `private String value(By locator)` | private | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetCodeValue()` | public | قراءة أو إرجاع الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetSelectedAccountValue()` | public | قراءة أو إرجاع القيمة المختارة / الحساب / القيمة، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredDisplayed()` | public | السؤال عن حالة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String name)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public boolean IsDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ConfirmDelete()` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `boolean recordVisible(By locator)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitLoading()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recordWaitAlertToDisappear()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-cabbad08652d"></a>
## DistributionRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/DistributionRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/DistributionRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 235

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DistributionRecords(DistributionEntityPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By locator)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `void Search(String text)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetEditableRowCount()` | داخل الحزمة | قراءة أو إرجاع الصف، حسب المدخلات والتخصص. |
| `WebElement findRow(String name)` | داخل الحزمة | البحث عن الصف، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String name)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int columnIndex(String title)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String GetFirstCellByHeader(String title)` | داخل الحزمة | قراءة أو إرجاع الأول، حسب المدخلات والتخصص. |
| `void openEdit(WebElement row)` | داخل الحزمة | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void OpenFirstForEdit()` | داخل الحزمة | فتح الأول، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String name)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String name)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `boolean IsDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ConfirmDelete()` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void dismissSuccessIfPresent()` | داخل الحزمة | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |

<a id="file-adc61c26e292"></a>
## ExpenseRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/ExpenseRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/ExpenseRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 196

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ExpenseRecords(ExpensesPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By by)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetEditableRowCount()` | داخل الحزمة | قراءة أو إرجاع الصف، حسب المدخلات والتخصص. |
| `WebElement row(String n)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String n)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int col(String title)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String GetFirstCellByHeader(String title)` | داخل الحزمة | قراءة أو إرجاع الأول، حسب المدخلات والتخصص. |
| `void openEdit(WebElement r)` | داخل الحزمة | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String n)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String n)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `boolean IsDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ConfirmDelete()` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void dismissDeleteSuccess()` | داخل الحزمة | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |

<a id="file-0b55d5c7cbdf"></a>
## ExpensesPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/ExpensesPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/ExpensesPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 244 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [ExpensesTest.java](TEST_Tests_AR.md#file-111123857b49)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ExpensesPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By by)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void dismissSuccess()` | private | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public void CloseFormIfOpen()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `public void DismissAlertIfPresent()` | public | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetMissingControls()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int GetEditableRowCount()` | public | قراءة أو إرجاع الصف، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String n)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetFirstCellByHeader(String title)` | public | قراءة أو إرجاع الأول، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String n)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `private String value(By b)` | private | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetCodeValue()` | public | قراءة أو إرجاع الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetSelectedAccountValue()` | public | قراءة أو إرجاع القيمة المختارة / الحساب / القيمة، حسب المدخلات والتخصص. |
| `public boolean IsSharedChecked()` | public | السؤال عن حالة حالة التحديد، حسب المدخلات والتخصص. |
| `public void SetExpenseShared(boolean checked)` | public | تعيين القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredDisplayed()` | public | السؤال عن حالة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String n)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public boolean IsDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ConfirmDelete()` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `boolean recordVisible(By by)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitLoading()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recordWaitAlert()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-9a0716038def"></a>
## GridProviderWait.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/GridProviderWait.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/GridProviderWait.java)

**دوره:** انتظار جاهزية مزود بيانات جدول Vue لنطاق الخزن أو المناطق قبل قراءة DOM.

**حالته:** مصدر نشط · **عدد أسطره:** 39

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `GridProviderWait(WebDriver driver, String resource)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void awaitReady()` | داخل الحزمة | انتظار الجاهزية، حسب المدخلات والتخصص. |

<a id="file-687a597d8dd2"></a>
## MainBage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/MainBage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/MainBage.java)

**دوره:** صفحة/مساعد قديم مرتبط بالقائمة الرئيسية بحسب ميثوداته الحالية؛ الاسم كما هو في المصدر.

**حالته:** مصدر نشط · **عدد أسطره:** 25

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public MainBage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-89a4abcc4db1"></a>
## PaymentPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/PaymentPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/PaymentPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 224 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [PaymentTest.java](TEST_Tests_AR.md#file-f4f7f3a5da0c)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public PaymentPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By b)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public void CloseFormIfOpen()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `public void DismissAlertIfPresent()` | public | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetMissingControls()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String n)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String n)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `private String value(By b)` | private | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetSelectedBankValue()` | public | قراءة أو إرجاع القيمة المختارة / البنك / القيمة، حسب المدخلات والتخصص. |
| `private WebElement checkbox(String text)` | private | تنفيذ جزء متعلق بـ مربع الاختيار، حسب المدخلات والتخصص. |
| `public void SetPaymentCheckbox(String text, boolean checked)` | public | تعيين السداد / مربع الاختيار، حسب المدخلات والتخصص. |
| `public boolean IsPaymentCheckboxChecked(String text)` | public | السؤال عن حالة السداد / مربع الاختيار / حالة التحديد، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredErrorDisplayed()` | public | السؤال عن حالة العربي / الاسم / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsBankRequiredErrorDisplayed()` | public | السؤال عن حالة البنك / الخطأ، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String n)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public boolean IsDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ConfirmDelete()` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `boolean recordVisible(By b)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitLoading()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recordWaitAlert()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-e3459749273e"></a>
## PaymentRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/PaymentRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/PaymentRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 153

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PaymentRecords(PaymentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By b)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String n)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement row(String n)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `void openEdit(WebElement r)` | داخل الحزمة | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String n)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String n)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `boolean IsDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ConfirmDelete()` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |

<a id="file-86b213fa5f9e"></a>
## RevenueRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RevenueRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RevenueRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 150

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `RevenueRecords(RevenuesPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By b)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String n)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement row(String n)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String n)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String n)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `boolean IsDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ConfirmDelete()` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |

<a id="file-dd431550a62a"></a>
## RevenuesPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RevenuesPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RevenuesPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 216 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [RevenuesTest.java](TEST_Tests_AR.md#file-78fe3003f784)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public RevenuesPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By b)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public void CloseFormIfOpen()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `public void DismissAlertIfPresent()` | public | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetMissingControls()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String n)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String n)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `private String value(By b)` | private | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetCodeValue()` | public | قراءة أو إرجاع الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetSelectedAccountValue()` | public | قراءة أو إرجاع القيمة المختارة / الحساب / القيمة، حسب المدخلات والتخصص. |
| `public boolean IsRevenueSharedChecked()` | public | السؤال عن حالة حالة التحديد، حسب المدخلات والتخصص. |
| `public void SetRevenueShared(boolean checked)` | public | تعيين القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredDisplayed()` | public | السؤال عن حالة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String n)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public boolean IsDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ConfirmDelete()` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `boolean recordVisible(By b)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitLoading()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recordWaitAlert()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-ebf00e4daa01"></a>
## RouteRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RouteRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RouteRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 150

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `RouteRecords(RoutLine owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By locator)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String n)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement row(String n)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String n)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String n)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `boolean IsDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ConfirmDelete()` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |

<a id="file-fd8f8913ef28"></a>
## RoutLine.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RoutLine.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/RoutLine.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 258 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [RoutLineTest.java](TEST_Tests_AR.md#file-9f2a53e742aa)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private boolean visible(By locator)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public void CloseFormIfOpen()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `public void DismissAlertIfPresent()` | public | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetMissingControls()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String n)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String n)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `private String value(By b)` | private | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetCodeValue()` | public | قراءة أو إرجاع الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetDocumentTypeValue()` | public | قراءة أو إرجاع المستند / القيمة، حسب المدخلات والتخصص. |
| `public boolean IsRouteLocationChecked()` | public | السؤال عن حالة حالة التحديد، حسب المدخلات والتخصص. |
| `public void SetRouteLocation(boolean checked)` | public | تعيين القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredDisplayed()` | public | السؤال عن حالة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String n)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public boolean IsDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ConfirmDelete()` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `public RoutLine(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenRoutLinePage()` | public | فتح البند / الصفحة، حسب المدخلات والتخصص. |
| `public void SelectRadiBouton(String RadiBouton)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean recordVisible(By locator)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitLoading()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recordWaitAlert()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-ccf2392eb482"></a>
## StorePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/StorePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/StorePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 201 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [StoreTest.java](TEST_Tests_AR.md#file-7c0fbb3a6a55)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public StorePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By b)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenStorePage()` | public | فتح المخزن / الصفحة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public void DismissAlertIfPresent()` | public | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public String GetMissingControls()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String n)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String n)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetCodeValue()` | public | قراءة أو إرجاع الكود / القيمة، حسب المدخلات والتخصص. |
| `public String GetSelectedAccountValue()` | public | قراءة أو إرجاع القيمة المختارة / الحساب / القيمة، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredDisplayed()` | public | السؤال عن حالة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String n)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public boolean IsDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ConfirmDelete(String name)` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `public void CloseFormIfOpen()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `boolean recordVisible(By b)` | داخل الحزمة | تسجيل/تمرير الظاهر، حسب المدخلات والتخصص. |
| `void recordWaitLoading()` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recordWaitAlert()` | داخل الحزمة | تسجيل/تمرير تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-ca1ef3ba6311"></a>
## TreasuryHandoverPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/TreasuryHandoverPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/TreasuryHandoverPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 111 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [TreasuryLedgerCoverageTest.java](TEST_FinancialMovementsTest_AR.md#file-aaf8d34190e8)، [TreasuryHandoverTest.java](TEST_Tests_AR.md#file-99e7d8e37af9)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public TreasuryHandoverPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenTreasuryHandoverPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void AddOpenBalance(double balnce)` | public | إضافة الرصيد، حسب المدخلات والتخصص. |
| `public boolean isErrorPopupDisplayed()` | public | السؤال عن حالة الخطأ / الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `public void OpenTreasuryForReview(Object id)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SaveAndPrint()` | public | حفظ الطباعة، حسب المدخلات والتخصص. |
| `public void AssertClosingAmounts(java.math.BigDecimal cash, java.math.BigDecimal total, java.math.BigDecimal actual, java.math.BigDecimal deficit)` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AssertPrintedAmounts(java.math.BigDecimal cash, java.math.BigDecimal total, java.math.BigDecimal actual, java.math.BigDecimal deficit)` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void assertNumber(String text, java.math.BigDecimal expected)` | private | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-df58c29cce49"></a>
## TypesofdiscountsPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/TypesofdiscountsPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/TypesofdiscountsPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 212 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [TaypeOfDiscountTest.java](TEST_Tests_AR.md#file-f61729975a8f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public TypesofdiscountsPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenTypesofdiscountsPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public boolean IsPercentageRequiredErrorDisplayed()` | public | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsSecretCodeRequiredErrorDisplayed()` | public | السؤال عن حالة الكود / الخطأ، حسب المدخلات والتخصص. |
| `public void AddDiscount(int discountValue)` | public | إضافة الخصم، حسب المدخلات والتخصص. |
| `public void AddSecretCode(String secretCode)` | public | إضافة الكود، حسب المدخلات والتخصص. |
| `public void ClickToAddUsers()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickSelectUsers(int... codes)` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void AddNewTypesofdiscounts( String ArabicName, String EnglishName, int percentage, int secretCode)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void EditTypesofdiscounts( String ArabicName, String EnglishName, int percentage, int secretCode)` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void InvalidAddNewTypesofdiscounts(String ArabicName, String EnglishName)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AssertRequiredFieldsMessages()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void DeleteTypesofdiscounts()` | public | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AssertTypesofdiscountsExists(String ArabicName)` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private org.openqa.selenium.WebElement ownedRow(String name)` | private | تنفيذ جزء متعلق بـ المملوك للاختبار / الصف، حسب المدخلات والتخصص. |
| `public void OpenOwnedForEdit(String name)` | public | فتح المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void DeleteOwned(String name)` | public | حذف المملوك للاختبار، حسب المدخلات والتخصص. |

<a id="file-18db193b049f"></a>
## WarehouseRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/BasicData/WarehouseRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/BasicData/WarehouseRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 132

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WarehouseRecords(StorePage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean visible(By b)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private void waitLoading()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitAlert()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String n)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement row(String n)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String n)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String n)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `boolean IsDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ConfirmDelete(String name)` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |

