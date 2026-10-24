# صفحات ومكونات الكاشير والمطعم

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-0bbc406dab89"></a>
## CounterDocumentLines.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/CounterDocumentLines.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/CounterDocumentLines.java)

**دوره:** بنود المستند أو سلة الأصناف: القراءة والكمية والأسعار والحذف والإجماليات بحسب المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 109

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `CounterDocumentLines(SpecialSalesPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `By line(String name)` | داخل الحزمة | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `void quantity(String name, String value)` | داخل الحزمة | ضبط الكمية، حسب المدخلات والتخصص. |
| `void changeQuantity(String name, boolean plus)` | داخل الحزمة | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `void removeLine(String name)` | داخل الحزمة | إزالة البند، حسب المدخلات والتخصص. |
| `void waitNet(String value)` | داخل الحزمة | انتظار الصافي، حسب المدخلات والتخصص. |
| `void waitTotal(String value)` | داخل الحزمة | انتظار الإجمالي، حسب المدخلات والتخصص. |
| `void awaitTotals()` | داخل الحزمة | انتظار الإجماليات، حسب المدخلات والتخصص. |

<a id="file-c8eecc263e25"></a>
## CounterPaymentControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/CounterPaymentControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/CounterPaymentControls.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 60

**يرتبط مباشرة بملفات داخل المشروع:** [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `CounterPaymentControls(SpecialSalesPage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void discount(String value, boolean percentage)` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `void closePayment()` | داخل الحزمة | إغلاق السداد، حسب المدخلات والتخصص. |
| `void saveAndPrint()` | داخل الحزمة | حفظ الطباعة، حسب المدخلات والتخصص. |
| `void addOtherPayment(String name, String value)` | داخل الحزمة | إضافة السداد، حسب المدخلات والتخصص. |

<a id="file-991ed2d276a4"></a>
## CounterSaveCapture.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/CounterSaveCapture.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/CounterSaveCapture.java)

**دوره:** تتبع محاولات ونتائج الحفظ المملوكة لهذا المجال؛ منفصل عن نقر زر الحفظ ومقارنة قواعد العمل.

**حالته:** مصدر نشط · **عدد أسطره:** 56

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `CounterSaveCapture(SpecialSalesPage owner)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void capture(String marker)` | داخل الحزمة | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `String captureMarker()` | داخل الحزمة | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> attempts()` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `List<Map<String, Object>> saves()` | داخل الحزمة | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `void settled()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void awaitSave()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void awaitSaveCount(int count)` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-5003d65a75ab"></a>
## PointOfSalePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/PointOfSalePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/PointOfSalePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 99 · **الأب المباشر:** `SpecialSalesPage`

**يرتبط مباشرة بملفات داخل المشروع:** [SpecialSalesPage.java](MAIN_PAGES_SpecialSales_AR.md#file-caa42ca53057)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public PointOfSalePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public String route()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String endpoint()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String channel()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void showInvoiceDetails()` | public | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `public void addItem(String name, String barcode, String category)` | public | إضافة الصنف، حسب المدخلات والتخصص. |
| `public void openPayment()` | public | فتح السداد، حسب المدخلات والتخصص. |
| `public void save()` | public | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `public void cash()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void credit()` | public | تنفيذ جزء متعلق بـ الدائن، حسب المدخلات والتخصص. |
| `public void hold()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void restore()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void invoices()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-1bb14bbf420a"></a>
## RestaurantInvoicePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/RestaurantInvoicePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/RestaurantInvoicePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 128 · **الأب المباشر:** `SpecialSalesPage`

**يرتبط مباشرة بملفات داخل المشروع:** [SpecialSalesPage.java](MAIN_PAGES_SpecialSales_AR.md#file-caa42ca53057)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public RestaurantInvoicePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public String route()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String endpoint()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String channel()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void open()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void type(String label)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void addItem(String name, String barcode, String category)` | public | إضافة الصنف، حسب المدخلات والتخصص. |
| `public void openPayment()` | public | فتح السداد، حسب المدخلات والتخصص. |
| `public void save()` | public | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `public void invoices()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void payAndClose()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void awaitInvoice(Object id)` | public | انتظار الفاتورة، حسب المدخلات والتخصص. |
| `public void hold()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectTable(String code)` | public | اختيار الجدول، حسب المدخلات والتخصص. |

<a id="file-caa42ca53057"></a>
## SpecialSalesPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/SpecialSalesPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SpecialSales/SpecialSalesPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 345 · **الأب المباشر:** `Abstract`

**يرتبط مباشرة بملفات داخل المشروع:** [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [PointOfSalePage.java](MAIN_PAGES_SpecialSales_AR.md#file-5003d65a75ab)، [RestaurantInvoicePage.java](MAIN_PAGES_SpecialSales_AR.md#file-1bb14bbf420a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SpecialSalesPage(WebDriver driver)` | protected | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public abstract String route();` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public abstract String endpoint();` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public abstract String channel();` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected Object js(String script, Object... args)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String vmScript()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void awaitSession()` | public | انتظار الجلسة، حسب المدخلات والتخصص. |
| `public void open()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void click(By locator)` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void text(By locator, String value)` | public | تنفيذ جزء متعلق بـ النص، حسب المدخلات والتخصص. |
| `public void blur()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void button(String label)` | public | تنفيذ جزء متعلق بـ الزر، حسب المدخلات والتخصص. |
| `public void choose(String label, String name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void dismiss()` | public | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String markup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String, Object> document()` | public | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `public Map<String, Object> diagnostics()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void capture(String marker)` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `public String captureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> attempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> saves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public void settled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected By line(String name)` | protected | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `public void quantity(String name, String value)` | public | ضبط الكمية، حسب المدخلات والتخصص. |
| `public void changeQuantity(String name, boolean plus)` | public | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `public void removeLine(String name)` | public | إزالة البند، حسب المدخلات والتخصص. |
| `public void discount(String value, boolean percentage)` | public | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `public void closePayment()` | public | إغلاق السداد، حسب المدخلات والتخصص. |
| `public void saveAndPrint()` | public | حفظ الطباعة، حسب المدخلات والتخصص. |
| `public void invoiceAction(Object id, String code, String icon)` | public | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `public void closeModal()` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void confirm(boolean yes)` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public boolean hasDialog()` | public | السؤال عن وجود القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void addOtherPayment(String name, String value)` | public | إضافة السداد، حسب المدخلات والتخصص. |
| `public void waitNet(String value)` | public | انتظار الصافي، حسب المدخلات والتخصص. |
| `public void waitTotal(String value)` | public | انتظار الإجمالي، حسب المدخلات والتخصص. |
| `public void awaitTotals()` | public | انتظار الإجماليات، حسب المدخلات والتخصص. |
| `public void awaitSave()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void awaitSaveCount(int count)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void awaitInvoice(Object id)` | public | انتظار الفاتورة، حسب المدخلات والتخصص. |
| `public void notes(String value)` | public | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `public void closeExtraWindows(String main)` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public abstract void addItem(String name, String barcode, String category);` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ إضافة الصنف، حسب المدخلات والتخصص. |
| `public abstract void save();` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |

