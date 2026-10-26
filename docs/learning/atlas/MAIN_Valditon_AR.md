# التحقق القديم

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-aa32f2af2150"></a>
## BaseAssertion.java

**المصدر:** [src/main/java/Plugin_ERP/Valditon/BaseAssertion.java](../../../src/main/java/Plugin_ERP/Valditon/BaseAssertion.java)

**دوره:** واجهة تحقق قديمة تربط قراءة العناصر باستدعاء hard أو soft assertion.

**حالته:** مصدر نشط · **عدد أسطره:** 60

**يرتبط مباشرة بملفات داخل المشروع:** [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)

**ملفات تشير إليه مباشرة:** [Validation.java](MAIN_Valditon_AR.md#file-ab3310891667)، [LegacyAssertionsTest.java](TEST_FrameworkContracts_AR.md#file-d14a7d3cbf66)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public BaseAssertion(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `protected abstract void assertrue(boolean condition, String message);` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract void asserfalse(boolean condition, String message);` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract void assertequals(Object actual, Object expected, String message);` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void Equals(String actual, String expected, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void Visible(By locator)` | protected | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `protected void PageURL(String expectedURL)` | protected | تنفيذ جزء متعلق بـ الصفحة / عنوان الشاشة، حسب المدخلات والتخصص. |
| `protected void PageTitle(String expectedTitle)` | protected | تنفيذ جزء متعلق بـ الصفحة / العنوان، حسب المدخلات والتخصص. |

<a id="file-ab3310891667"></a>
## Validation.java

**المصدر:** [src/main/java/Plugin_ERP/Valditon/Validation.java](../../../src/main/java/Plugin_ERP/Valditon/Validation.java)

**دوره:** تنفيذ soft assertions قديم مرتبط بالخيط ويجمع الفشل عند AsserAll مع تنظيف حالته.

**حالته:** مصدر نشط · **عدد أسطره:** 51 · **الأب المباشر:** `BaseAssertion`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [BaseAssertion.java](MAIN_Valditon_AR.md#file-aa32f2af2150)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static SoftAssert currentAssertions()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Validation(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `protected void assertrue(boolean condition, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void asserfalse(boolean condition, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertequals(Object actual, Object expected, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void AsserAll()` | public | إظهار فشل soft assertions المجمعة ثم إزالة حالتها. |

<a id="file-79373a4a6f09"></a>
## Varification.java

**المصدر:** [src/main/java/Plugin_ERP/Valditon/Varification.java](../../../src/main/java/Plugin_ERP/Valditon/Varification.java)

**دوره:** المحتوى معلق/تعليقات فقط في النسخة الحالية؛ لا يوجد تنفيذ Java نشط.

**حالته:** تعليقات فقط · **عدد أسطره:** 32

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.
