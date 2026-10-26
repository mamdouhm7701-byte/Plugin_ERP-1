# صفحات ومكونات المشتريات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-45ada3f4c25b"></a>
## PurchaseCalendar.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseCalendar.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseCalendar.java)

**دوره:** اختيار التاريخ في تقويم المشتريات وقراءة أسماء الشهور العربية والإنجليزية والرقمية.

**حالته:** مصدر نشط · **عدد أسطره:** 171

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseCalendar(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void selectDate(WebElement hiddenInput, String value)` | داخل الحزمة | اختيار التاريخ، حسب المدخلات والتخصص. |
| `private boolean calendarIsVisible(String pickerId)` | private | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `private String calendarCaption(String pickerId)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static int parseMonth(String text)` | داخل الحزمة | تفسير الشهر من عنوان التقويم. |

<a id="file-2e6161be5916"></a>
## PurchaseChoiceControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseChoiceControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseChoiceControls.java)

**دوره:** اختيار وقراءة ومسح القيم في قوائم نموذج المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 95

**يرتبط مباشرة بملفات داخل المشروع:** [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseChoiceControls(PurchaseModulePage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void puChoose(String label, String name)` | داخل الحزمة | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puClearChoice(String label)` | داخل الحزمة | مسح/إلغاء الاختيار، حسب المدخلات والتخصص. |
| `boolean puChoiceEmpty(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ الاختيار، حسب المدخلات والتخصص. |
| `boolean puCanClearChoice(String label)` | داخل الحزمة | التحقق من إتاحة الاختيار، حسب المدخلات والتخصص. |
| `boolean puHasChoice(String label)` | داخل الحزمة | السؤال عن وجود الاختيار، حسب المدخلات والتخصص. |

<a id="file-af9f5be8d0d7"></a>
## PurchaseDocumentForm.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentForm.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentForm.java)

**دوره:** حقول النموذج وجاهزيته وحفظه وتنبيهاته في مجال المستند، بدون ملكية بيانات التجربة.

**حالته:** مصدر نشط · **عدد أسطره:** 164

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseDocumentForm(PurchaseModulePage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puNotes(String note)` | داخل الحزمة | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `void puSave(boolean print)` | داخل الحزمة | حفظ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean puSaveDisabled()` | داخل الحزمة | حفظ التعطيل، حسب المدخلات والتخصص. |
| `boolean puHasDocumentSaveAction()` | داخل الحزمة | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `boolean puHasDocumentDeleteAction()` | داخل الحزمة | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `void puDismiss()` | داخل الحزمة | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String puValidation()` | داخل الحزمة | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `Map<String, Object> puDocument()` | داخل الحزمة | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `void puFormReady()` | داخل الحزمة | تنفيذ جزء متعلق بـ النموذج / الجاهزية، حسب المدخلات والتخصص. |
| `void puAwaitOwnedLookups(Object store, Object supplier, Object agent)` | داخل الحزمة | انتظار المملوك للاختبار، حسب المدخلات والتخصص. |
| `String puDocumentDate()` | داخل الحزمة | تنفيذ جزء متعلق بـ المستند / التاريخ، حسب المدخلات والتخصص. |
| `void puDiscount(String value, boolean percentage)` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `java.math.BigDecimal puUserDiscountLimit()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `void puAddOtherPayment(String method, String amount)` | داخل الحزمة | إضافة السداد، حسب المدخلات والتخصص. |
| `void puNew()` | داخل الحزمة | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String puNotesValue()` | داخل الحزمة | ضبط الملاحظات / القيمة، حسب المدخلات والتخصص. |

<a id="file-9e05d98d7719"></a>
## PurchaseDocumentLines.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentLines.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentLines.java)

**دوره:** بنود المستند أو سلة الأصناف: القراءة والكمية والأسعار والحذف والإجماليات بحسب المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 221

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseDocumentLines(PurchaseModulePage page, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean puLineReadOnly(String name, String column)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `private By line(String name)` | private | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `int puLineCount(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `Map<String, String> puLineValues(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند / القيم، حسب المدخلات والتخصص. |
| `void puQuantity(String name, String quantity)` | داخل الحزمة | ضبط الكمية، حسب المدخلات والتخصص. |
| `void puRemoveLine(String name)` | داخل الحزمة | إزالة البند، حسب المدخلات والتخصص. |
| `void puSetLine(String name, String column, String value)` | داخل الحزمة | تعيين البند، حسب المدخلات والتخصص. |
| `Map<String, String> puTotals()` | داخل الحزمة | تنفيذ جزء متعلق بـ الإجماليات، حسب المدخلات والتخصص. |
| `void puWaitNet(String value)` | داخل الحزمة | انتظار الصافي، حسب المدخلات والتخصص. |

<a id="file-bc5c73ec63df"></a>
## PurchaseDocumentList.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentList.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentList.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 178

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseDocumentList(PurchaseModulePage page, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void puListScope(String customer)` | داخل الحزمة | تنفيذ جزء متعلق بـ النطاق، حسب المدخلات والتخصص. |
| `void puAwaitListReady()` | داخل الحزمة | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `private boolean puListReady()` | private | تنفيذ جزء متعلق بـ الجاهزية، حسب المدخلات والتخصص. |
| `void puApplyListFilters()` | داخل الحزمة | تنفيذ جزء متعلق بـ الفلاتر، حسب المدخلات والتخصص. |
| `String puListFixtureDate()` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار / التاريخ، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> puListRows()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصفوف، حسب المدخلات والتخصص. |
| `void puWaitListIds(Set<Object> expected)` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puListText(String name, String text)` | داخل الحزمة | تنفيذ جزء متعلق بـ النص، حسب المدخلات والتخصص. |
| `void puListRadio(String group, String value)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `List<String> puHeaders()` | داخل الحزمة | تنفيذ جزء متعلق بـ عناوين الأعمدة، حسب المدخلات والتخصص. |
| `void puPageSize(int size)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصفحة / الحجم، حسب المدخلات والتخصص. |
| `void puNextPage()` | داخل الحزمة | تنفيذ جزء متعلق بـ التالي / الصفحة، حسب المدخلات والتخصص. |
| `void puFirstPage()` | داخل الحزمة | تنفيذ جزء متعلق بـ الأول / الصفحة، حسب المدخلات والتخصص. |
| `void puSort(String label)` | داخل الحزمة | فرز الفرز، حسب المدخلات والتخصص. |
| `void puExport(String format)` | داخل الحزمة | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-ca18ac0f7698"></a>
## PurchaseDocumentTransitions.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentTransitions.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseDocumentTransitions.java)

**دوره:** تنسيق تحويل مستند أو إنشاء مرتجع مرتبط أو حذف مستند الاختبار المحدد.

**حالته:** مصدر نشط · **عدد أسطره:** 79

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseDocumentTransitions(PurchaseModulePage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void puLinkedReturn(Object invoiceId, String itemName)` | داخل الحزمة | تنفيذ جزء متعلق بـ المرتجع، حسب المدخلات والتخصص. |
| `void puConvertOwned(String supplier, String code, boolean toInvoice, String itemName)` | داخل الحزمة | تنفيذ جزء متعلق بـ المملوك للاختبار، حسب المدخلات والتخصص. |
| `void puDeleteOwned(String supplier, String code)` | داخل الحزمة | حذف المملوك للاختبار، حسب المدخلات والتخصص. |

<a id="file-9c66d6319233"></a>
## PurchaseItemSelection.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseItemSelection.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseItemSelection.java)

**دوره:** البحث واختيار الصنف بالاسم/الباركود/النافذة بحسب ما تسمح به ميثوداته.

**حالته:** مصدر نشط · **عدد أسطره:** 165

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseItemSelection(PurchaseModulePage page, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void radio(String value)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puWaitOwnedItemLookup(String name)` | داخل الحزمة | انتظار المملوك للاختبار / الصنف، حسب المدخلات والتخصص. |
| `void puAddName(String name)` | داخل الحزمة | إضافة الاسم، حسب المدخلات والتخصص. |
| `void puAddBarcode(String barcode, String name)` | داخل الحزمة | إضافة الباركود، حسب المدخلات والتخصص. |
| `void puAddBarcodeImmediately(String barcode, String name)` | داخل الحزمة | إضافة الباركود، حسب المدخلات والتخصص. |
| `boolean puOwnedItemCached(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ المملوك للاختبار / الصنف / المخزن مؤقتًا، حسب المدخلات والتخصص. |
| `Object puItemCacheDiagnostics(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `void puAddPicker(String name)` | داخل الحزمة | إضافة نافذة الاختيار، حسب المدخلات والتخصص. |
| `void puPickerSearch(String field, String value, String expectedName)` | داخل الحزمة | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `void puPickerCategory(String category, String expectedName)` | داخل الحزمة | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `List<String> puPickerRows()` | داخل الحزمة | تنفيذ جزء متعلق بـ نافذة الاختيار / الصفوف، حسب المدخلات والتخصص. |
| `void puClosePicker()` | داخل الحزمة | إغلاق نافذة الاختيار، حسب المدخلات والتخصص. |

<a id="file-26ced787f2c9"></a>
## PurchaseModulePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseModulePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseModulePage.java)

**دوره:** واجهة تنسيق قدرات مجال متكرر؛ تستدعي مكونات متخصصة وتحتفظ بنقاط دخول الاختبارات.

**حالته:** مصدر نشط · **عدد أسطره:** 441 · **الأب المباشر:** `AbstractForPurchases`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [PurchaseScenarioTest.java](TEST_Purches_AR.md#file-68923271f3e1)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public PurchaseModulePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void puAwaitSession()` | public | انتظار الجلسة، حسب المدخلات والتخصص. |
| `protected Object js(String script, Object... args)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puOpen(String route)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puType(By by, String value)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puType(By by, String value, boolean blur)` | داخل الحزمة | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void puClick(By by)` | protected | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void puChoose(String label, String name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puClearChoice(String label)` | public | مسح/إلغاء الاختيار، حسب المدخلات والتخصص. |
| `public boolean puChoiceEmpty(String label)` | public | تنفيذ جزء متعلق بـ الاختيار، حسب المدخلات والتخصص. |
| `public boolean puCanClearChoice(String label)` | public | التحقق من إتاحة الاختيار، حسب المدخلات والتخصص. |
| `public boolean puHasChoice(String label)` | public | السؤال عن وجود الاختيار، حسب المدخلات والتخصص. |
| `public void puNotes(String note)` | public | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `public String puDiagnosticMarkup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void utilitesReportReady()` | public | تنفيذ جزء متعلق بـ التقرير / الجاهزية، حسب المدخلات والتخصص. |
| `public void puButton(String text)` | public | تنفيذ جزء متعلق بـ الزر، حسب المدخلات والتخصص. |
| `public void puSave(boolean print)` | public | حفظ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean puSaveDisabled()` | public | حفظ التعطيل، حسب المدخلات والتخصص. |
| `public boolean puHasDocumentSaveAction()` | public | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `public boolean puHasDocumentDeleteAction()` | public | السؤال عن وجود المستند، حسب المدخلات والتخصص. |
| `public boolean puLineReadOnly(String name, String column)` | public | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `public void puDismiss()` | public | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String puValidation()` | public | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `public int puLineCount(String name)` | public | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `public Map<String, String> puLineValues(String name)` | public | تنفيذ جزء متعلق بـ البند / القيم، حسب المدخلات والتخصص. |
| `private void radio(String value)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puWaitOwnedItemLookup(String name)` | public | انتظار المملوك للاختبار / الصنف، حسب المدخلات والتخصص. |
| `public void puAddName(String name)` | public | إضافة الاسم، حسب المدخلات والتخصص. |
| `public void puAddBarcode(String barcode, String name)` | public | إضافة الباركود، حسب المدخلات والتخصص. |
| `public void puAddBarcodeImmediately(String barcode, String name)` | public | إضافة الباركود، حسب المدخلات والتخصص. |
| `public boolean puOwnedItemCached(String name)` | public | تنفيذ جزء متعلق بـ المملوك للاختبار / الصنف / المخزن مؤقتًا، حسب المدخلات والتخصص. |
| `public void puAddPicker(String name)` | public | إضافة نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void puPickerSearch(String field, String value, String expectedName)` | public | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void puPickerCategory(String category, String expectedName)` | public | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `public List<String> puPickerRows()` | public | تنفيذ جزء متعلق بـ نافذة الاختيار / الصفوف، حسب المدخلات والتخصص. |
| `public void puClosePicker()` | public | إغلاق نافذة الاختيار، حسب المدخلات والتخصص. |
| `public void puQuantity(String name, String quantity)` | public | ضبط الكمية، حسب المدخلات والتخصص. |
| `public void puRemoveLine(String name)` | public | إزالة البند، حسب المدخلات والتخصص. |
| `public void puCapture(String marker, Object storeId, Object itemId, String endpoint)` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> puSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> puAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public void puSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String puSaveErrors()` | public | حفظ الأخطاء، حسب المدخلات والتخصص. |
| `public String puDiagnostics()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puWaitSaveOutcome()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String puCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public void puEdit(String route, Object id, String marker, String itemName)` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String, Object> puDocument()` | public | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `public void puFormReady()` | public | تنفيذ جزء متعلق بـ النموذج / الجاهزية، حسب المدخلات والتخصص. |
| `public void puAwaitOwnedLookups(Object store, Object supplier, Object agent)` | public | انتظار المملوك للاختبار، حسب المدخلات والتخصص. |
| `public String puDocumentDate()` | public | تنفيذ جزء متعلق بـ المستند / التاريخ، حسب المدخلات والتخصص. |
| `public void puSetLine(String name, String column, String value)` | public | تعيين البند، حسب المدخلات والتخصص. |
| `public Map<String, String> puTotals()` | public | تنفيذ جزء متعلق بـ الإجماليات، حسب المدخلات والتخصص. |
| `public void puWaitNet(String value)` | public | انتظار الصافي، حسب المدخلات والتخصص. |
| `public void puDiscount(String value, boolean percentage)` | public | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `public java.math.BigDecimal puUserDiscountLimit()` | public | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `public void puAddOtherPayment(String method, String amount)` | public | إضافة السداد، حسب المدخلات والتخصص. |
| `public void puNew()` | public | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String puNotesValue()` | public | ضبط الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `public void puLinkedReturn(Object invoiceId, String itemName)` | public | تنفيذ جزء متعلق بـ المرتجع، حسب المدخلات والتخصص. |
| `public void puConvertOwned(String supplier, String code, boolean toInvoice, String itemName)` | public | تنفيذ جزء متعلق بـ المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void puDeleteOwned(String supplier, String code)` | public | حذف المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void puListScope(String customer)` | public | تنفيذ جزء متعلق بـ النطاق، حسب المدخلات والتخصص. |
| `public void puAwaitListReady()` | public | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `public void puApplyListFilters()` | public | تنفيذ جزء متعلق بـ الفلاتر، حسب المدخلات والتخصص. |
| `public String puListFixtureDate()` | public | تنفيذ جزء متعلق بـ بيانات الاختبار / التاريخ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> puListRows()` | public | تنفيذ جزء متعلق بـ الصفوف، حسب المدخلات والتخصص. |
| `public void puWaitListIds(Set<Object> expected)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puListText(String name, String text)` | public | تنفيذ جزء متعلق بـ النص، حسب المدخلات والتخصص. |
| `public void puListRadio(String group, String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public List<String> puHeaders()` | public | تنفيذ جزء متعلق بـ عناوين الأعمدة، حسب المدخلات والتخصص. |
| `public void puPageSize(int size)` | public | تنفيذ جزء متعلق بـ الصفحة / الحجم، حسب المدخلات والتخصص. |
| `public void puNextPage()` | public | تنفيذ جزء متعلق بـ التالي / الصفحة، حسب المدخلات والتخصص. |
| `public void puFirstPage()` | public | تنفيذ جزء متعلق بـ الأول / الصفحة، حسب المدخلات والتخصص. |
| `public void puSort(String label)` | public | فرز الفرز، حسب المدخلات والتخصص. |
| `public void puExport(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `public String puWindowHandle()` | public | تنفيذ جزء متعلق بـ التبويب، حسب المدخلات والتخصص. |
| `public void puCloseAuxiliaryWindows(String main)` | public | إغلاق الإضافي، حسب المدخلات والتخصص. |
| `public void puVerifyPrintedReport(String main, String item)` | public | التحقق من التقرير، حسب المدخلات والتخصص. |
| `public void puVerifyPrintedReport(String main, String item, Runnable contentAssertions)` | public | التحقق من التقرير، حسب المدخلات والتخصص. |
| `public void puDateFilter(String name, String value)` | public | تنفيذ جزء متعلق بـ التاريخ / الفلتر، حسب المدخلات والتخصص. |
| `private void setDateFilter(WebElement hiddenInput, String value)` | private | تعيين التاريخ / الفلتر، حسب المدخلات والتخصص. |
| `FormInteractions formInteractions()` | داخل الحزمة | تنفيذ جزء متعلق بـ النموذج، حسب المدخلات والتخصص. |
| `void selectPaymentMode(String value)` | داخل الحزمة | اختيار السداد، حسب المدخلات والتخصص. |

<a id="file-9364a510d60a"></a>
## PurchasePrintWindows.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchasePrintWindows.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchasePrintWindows.java)

**دوره:** تنسيق تبويب الطباعة والرجوع للرئيسي واستدعاء مراجعة المحتوى في مجال المستند.

**حالته:** مصدر نشط · **عدد أسطره:** 57

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchasePrintWindows(PurchaseModulePage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `String puWindowHandle()` | داخل الحزمة | تنفيذ جزء متعلق بـ التبويب، حسب المدخلات والتخصص. |
| `void puCloseAuxiliaryWindows(String main)` | داخل الحزمة | إغلاق الإضافي، حسب المدخلات والتخصص. |
| `void puVerifyPrintedReport(String main, String item)` | داخل الحزمة | التحقق من التقرير، حسب المدخلات والتخصص. |
| `void puVerifyPrintedReport(String main, String item, Runnable contentAssertions)` | داخل الحزمة | التحقق من التقرير، حسب المدخلات والتخصص. |

<a id="file-89da3311f68a"></a>
## PurchaseSaveCapture.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseSaveCapture.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchaseSaveCapture.java)

**دوره:** تتبع محاولات ونتائج الحفظ المملوكة لهذا المجال؛ منفصل عن نقر زر الحفظ ومقارنة قواعد العمل.

**حالته:** مصدر نشط · **عدد أسطره:** 84

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseSaveCapture(PurchaseModulePage owner)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object js(String script, Object... args)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puCapture(String marker, Object storeId, Object itemId, String endpoint)` | داخل الحزمة | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> puSaves()` | داخل الحزمة | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> puAttempts()` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `void puSettled()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String puSaveErrors()` | داخل الحزمة | حفظ الأخطاء، حسب المدخلات والتخصص. |
| `String puDiagnostics()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void puWaitSaveOutcome()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String puCaptureMarker()` | داخل الحزمة | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |

<a id="file-83d91741f555"></a>
## PurchesBillsPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchesBillsPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/PurchesBillsPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 32 · **الأب المباشر:** `AbstractForPurchases`

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [AbstractForStore.java](MAIN_PAGES_Shared_AR.md#file-faf7e732889e)

**ملفات تشير إليه مباشرة:** [DocumentGridEditing.java](MAIN_PAGES_Shared_AR.md#file-14d530217dad)، [DocumentItemControls.java](MAIN_PAGES_Shared_AR.md#file-90e4c2a522d6)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [DocumentQuantityPrintCheck.java](MAIN_PAGES_Shared_AR.md#file-6ab0f716fbfb)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public PurchesBillsPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void ClickToTransToReturnOrder()` | public | ضغط إجراء النقر / المرتجع، حسب المدخلات والتخصص. |

<a id="file-4a93b7c44de8"></a>
## purchOrderPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/purchOrderPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/purchOrderPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 215 · **الأب المباشر:** `AbstractForPurchases`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public purchOrderPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void clickToTransToPurchesOrder()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `protected BigDecimal getSummaryValue(String label)` | protected | قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص. |
| `public List<InvoiceRow> getInvoiceRowsForPurchOrder()` | public | قراءة أو إرجاع الفاتورة / الصفوف، حسب المدخلات والتخصص. |
| `public InvoiceTotals validateBusinessTotalsForPurchOrder()` | public | التحقق من الإجماليات، حسب المدخلات والتخصص. |
| `public void saveAndPrintForReport()` | public | حفظ الطباعة / التقرير، حسب المدخلات والتخصص. |
| `public Map<String, String> GetNamesQuantitiesAndPricesForPurchOrder()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-e431afd355fd"></a>
## ReturnOrderPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/ReturnOrderPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/ReturnOrderPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 168 · **الأب المباشر:** `AbstractForPurchases`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ReturnOrderPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void validateInvoiceAllBusinessForReturnOrder()` | public | التحقق من الفاتورة / المرتجع، حسب المدخلات والتخصص. |
| `protected BigDecimal getSummaryValue(String label)` | protected | قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص. |
| `public List<InvoiceRow> getInvoiceRowsForReturnOrder()` | public | قراءة أو إرجاع الفاتورة / الصفوف / المرتجع، حسب المدخلات والتخصص. |
| `public InvoiceTotals validateBusinessTotalsForReturnOrder()` | public | التحقق من الإجماليات / المرتجع، حسب المدخلات والتخصص. |
| `public void saveAndPrintForReport()` | public | حفظ الطباعة / التقرير، حسب المدخلات والتخصص. |
| `public Map<String, String> GetNamesQuantitiesAndPricesForReturnOrder()` | public | قراءة أو إرجاع المرتجع، حسب المدخلات والتخصص. |
| `public void ClickPlusToAddPayForReturnOrder()` | public | ضغط إجراء النقر / المرتجع، حسب المدخلات والتخصص. |
| `public Boolean AssertSupplierForReturnOrder()` | public | مقارنة متوقعة المورد / المرتجع، حسب المدخلات والتخصص. |
| `public void waitUntilSummaryValuesLoaded()` | public | انتظار القيم، حسب المدخلات والتخصص. |

<a id="file-ad20db7cd3b8"></a>
## ReturnOrderRows.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/purches/ReturnOrderRows.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/purches/ReturnOrderRows.java)

**دوره:** تحويل بنود الشاشة وقيمها إلى نماذج أو لقطة بيانات للمقارنة والحساب.

**حالته:** مصدر نشط · **عدد أسطره:** 141

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ReturnOrderRows(ReturnOrderPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `List<InvoiceRow> getInvoiceRowsForReturnOrder()` | داخل الحزمة | قراءة أو إرجاع الفاتورة / الصفوف / المرتجع، حسب المدخلات والتخصص. |
| `Map<String, String> GetNamesQuantitiesAndPricesForReturnOrder()` | داخل الحزمة | قراءة أو إرجاع المرتجع، حسب المدخلات والتخصص. |
