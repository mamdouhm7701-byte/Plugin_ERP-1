# صفحات ومكونات المتعاملين

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-e245cb653316"></a>
## Agent.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/Agent.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/Agent.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 184 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [AgentTest.java](TEST_DealerModelTest_AR.md#file-fbd3e2f60ed9)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Agent(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenAgentPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void ClickAgentCommissionTab()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickItemCommissionTab()` | public | ضغط إجراء النقر / الصنف، حسب المدخلات والتخصص. |
| `public void EnterFromAmount(String fromAmount)` | public | تنفيذ جزء متعلق بـ المبلغ، حسب المدخلات والتخصص. |
| `public void EnterToAmount(String toAmount)` | public | تنفيذ جزء متعلق بـ المبلغ، حسب المدخلات والتخصص. |
| `public void ClickAddCommissionButton()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `public void EnterPercentage(String percentage)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SearchProduct(String productName)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void WaitForCommissionItems(String... names)` | public | انتظار الأصناف، حسب المدخلات والتخصص. |
| `public void AddCommissionItemsByName(String... names)` | public | إضافة الأصناف / الاسم، حسب المدخلات والتخصص. |
| `public int getRowCount()` | public | قراءة أو إرجاع الصف، حسب المدخلات والتخصص. |
| `public void ClickAddItemsButton()` | public | ضغط إجراء النقر / الأصناف / الزر، حسب المدخلات والتخصص. |
| `public void ClickAddAllItems()` | public | ضغط إجراء النقر / الأصناف، حسب المدخلات والتخصص. |
| `public void AddAgentMobileNumber()` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ExportList(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-c60feadee3d9"></a>
## CustomerClassification.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/CustomerClassification.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/CustomerClassification.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 48 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [CustomerClassificationTest.java](TEST_DealerModelTest_AR.md#file-f5e0860be20c)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public CustomerClassification(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenDealerModel()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ExportList(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-18f667c71688"></a>
## Customers.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/Customers.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/Customers.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 95 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [CustomersTest.java](TEST_DealerModelTest_AR.md#file-5a3d847bf4ab)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Customers(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void ClickNewButtonForDealer()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `public void customerProfile(boolean taxable, boolean cashCustomer)` | public | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `public void OpenCustomers()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAddress(String address)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddMobileNumber(String MobileNumber)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddLocation(String Location)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAgentName(String AgentName)` | public | إضافة الاسم، حسب المدخلات والتخصص. |
| `public void ClickLinkDataTax()` | public | ضغط إجراء النقر / الضريبة، حسب المدخلات والتخصص. |
| `public void ExportList(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-f6e2daf7a1c7"></a>
## MainDealer.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/MainDealer.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/MainDealer.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 149 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [MainDealerTest.java](TEST_DealerModelTest_AR.md#file-4c029045cc6a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public MainDealer(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenMainDealerModel()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String GetMissingFormControls()` | public | قراءة أو إرجاع النموذج، حسب المدخلات والتخصص. |
| `private void addMissing(List<String> missing, String label, By locator)` | private | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int GetExactMatchCount(String arabicName)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenForEditByName(String arabicName)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `public String GetArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetEnglishNameValue()` | public | قراءة أو إرجاع الإنجليزي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public String GetSelectedKindValue()` | public | قراءة أو إرجاع القيمة المختارة / القيمة، حسب المدخلات والتخصص. |
| `public void RequestDeleteByName(String arabicName)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public void ConfirmDeleteByName(String arabicName)` | public | تأكيد التأكيد / الاسم، حسب المدخلات والتخصص. |
| `public void CancelDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `public boolean IsArabicNameRequiredDisplayed()` | public | السؤال عن حالة العربي / الاسم، حسب المدخلات والتخصص. |
| `public boolean IsKindRequiredDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ExportList(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `private WebElement findRowByName(String arabicName)` | private | البحث عن الصف / الاسم، حسب المدخلات والتخصص. |
| `private boolean isDisplayed(By locator)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean isDisplayed(WebElement element)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitForLoadingToFinish()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void waitForAlertToDisappear()` | private | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `private void dismissDeleteSuccessIfPresent()` | private | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void dismissSuccessAlertIfPresent()` | private | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-6c3436b3356b"></a>
## MainDealerRecords.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/MainDealerRecords.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/MainDealerRecords.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 203

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `MainDealerRecords(MainDealer owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean IsListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int GetExactMatchCount(String arabicName)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void OpenForEditByName(String arabicName)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void RequestDeleteByName(String arabicName)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `void ConfirmDeleteByName(String arabicName)` | داخل الحزمة | تأكيد التأكيد / الاسم، حسب المدخلات والتخصص. |
| `void CancelDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `void ExportList(String format)` | داخل الحزمة | تصدير التصدير، حسب المدخلات والتخصص. |
| `WebElement findRowByName(String arabicName)` | داخل الحزمة | البحث عن الصف / الاسم، حسب المدخلات والتخصص. |
| `boolean isDisplayed(By locator)` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static boolean isDisplayed(WebElement element)` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void waitForLoadingToFinish()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void waitForAlertToDisappear()` | داخل الحزمة | انتظار تنبيه المتصفح، حسب المدخلات والتخصص. |
| `void dismissDeleteSuccessIfPresent()` | داخل الحزمة | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void dismissSuccessAlertIfPresent()` | داخل الحزمة | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |

<a id="file-fb3d8ac2b0d9"></a>
## RoutLineForDealer.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/RoutLineForDealer.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/RoutLineForDealer.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 247 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [RoutLineTest.java](TEST_DealerModelTest_AR.md#file-a6a59694ce1a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public RoutLineForDealer(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void PrepareOwnedRoute(String agentName, String customerName, String statusName, String areaName)` | public | تجهيز المملوك للاختبار، حسب المدخلات والتخصص. |
| `private void chooseOwned(Plugin_ERP.Utiel.FormInteractions form, String label, String name)` | private | اختيار المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void AssertOnlyOwnedCustomerSelected(Object customerId)` | public | مقارنة متوقعة المملوك للاختبار / العميل / القيمة المختارة، حسب المدخلات والتخصص. |
| `public java.util.List<String> OwnedEventTitles(String marker)` | public | تنفيذ جزء متعلق بـ المملوك للاختبار، حسب المدخلات والتخصص. |
| `private By getDayCell(String date)` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private By getDayEvents(String date)` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private By getDayEventTitles(String date)` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenRoutLine()` | public | فتح البند، حسب المدخلات والتخصص. |
| `public void AddRoutLine()` | public | إضافة البند، حسب المدخلات والتخصص. |
| `public void AddArabicRoutLine(String Title)` | public | إضافة العربي / البند، حسب المدخلات والتخصص. |
| `public void AddVisitCount(String VisitCount)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddDayCount(String DayCount)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddFromDate()` | public | إضافة التاريخ، حسب المدخلات والتخصص. |
| `public void AddToDate(String ToDate)` | public | إضافة التاريخ، حسب المدخلات والتخصص. |
| `public void ClickToSaveButton()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void AddNotesForRoutLine(String Notes)` | public | إضافة الملاحظات / البند، حسب المدخلات والتخصص. |
| `public void ClickAddAllButton()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `public void ClickToDeletAllButton()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `public void ClickGatherVisitsTab()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickCalendarTab()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickDeleteBtn()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void OpenMenuIcon()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickLastRowDeleteBtn()` | public | ضغط إجراء النقر / الأخير / الصف، حسب المدخلات والتخصص. |
| `public void ClickLastRowInfoIcon()` | public | ضغط إجراء النقر / الأخير / الصف، حسب المدخلات والتخصص. |
| `public void selectToday()` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void clickFirstEventForDay(String date)` | public | ضغط إجراء النقر / الأول، حسب المدخلات والتخصص. |
| `public int getEventsCountForDay(String date)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public List<String> getEventsTitlesForDay(String date)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitForEventCountToIncrease(String date, int countBefore)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int getTodayEventsCount()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public List<String> getTodayEventsTitles()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitForTodayEventCountToIncrease(int countBefore)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c1045e9f255a"></a>
## Supplier.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/Supplier.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerModel/Supplier.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 66 · **الأب المباشر:** `AbstractForDealer`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

**ملفات تشير إليه مباشرة:** [SupplierTest.java](TEST_DealerModelTest_AR.md#file-110ff2e4a9fe)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Supplier(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenSupplier()` | public | فتح المورد، حسب المدخلات والتخصص. |
| `public void ExportList(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `public void AddAdressForSupplier(String Adress)` | public | إضافة المورد، حسب المدخلات والتخصص. |
| `public void AddMobileNumberForSupplier(String MobileNumber)` | public | إضافة المورد، حسب المدخلات والتخصص. |
| `public void AddAgentNameForSupplier(String AgentName)` | public | إضافة الاسم / المورد، حسب المدخلات والتخصص. |

