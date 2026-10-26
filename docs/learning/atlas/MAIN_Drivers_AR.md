# مصانع المتصفحات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-a1539b959b16"></a>
## AbstractDriver.java

**المصدر:** [src/main/java/Plugin_ERP/Drivers/AbstractDriver.java](../../../src/main/java/Plugin_ERP/Drivers/AbstractDriver.java)

**دوره:** عقد إنشاء المتصفح وقراءة عنوان Selenium Grid عند الاستخدام البعيد.

**حالته:** مصدر نشط · **عدد أسطره:** 11

**يرتبط مباشرة بملفات داخل المشروع:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [Chrome.java](MAIN_Drivers_AR.md#file-14889194f9e4)، [Edge.java](MAIN_Drivers_AR.md#file-e957d0c8ef7e)، [Firefox.java](MAIN_Drivers_AR.md#file-cafcec966ff0)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public abstract WebDriver createDriver();` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-6a481899dc7e"></a>
## Browser.java

**المصدر:** [src/main/java/Plugin_ERP/Drivers/Browser.java](../../../src/main/java/Plugin_ERP/Drivers/Browser.java)

**دوره:** تعداد أنواع المتصفح وربط النوع بمصنعه.

**حالته:** مصدر نشط · **عدد أسطره:** 25

**ملفات تشير إليه مباشرة:** [LoginTest.java](TEST_Tests_AR.md#file-3a9647b96039)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AbstractDriver getDriverFactory()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public AbstractDriver getDriverFactory()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public AbstractDriver getDriverFactory()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public abstract AbstractDriver getDriverFactory();` | public | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-14889194f9e4"></a>
## Chrome.java

**المصدر:** [src/main/java/Plugin_ERP/Drivers/Chrome.java](../../../src/main/java/Plugin_ERP/Drivers/Chrome.java)

**دوره:** خيارات Chrome وإنشاء متصفحه محليًا أو بدون نافذة أو عبر Selenium Grid حسب الإعداد.

**حالته:** مصدر نشط · **عدد أسطره:** 83 · **الأب المباشر:** `AbstractDriver`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [AbstractDriver.java](MAIN_Drivers_AR.md#file-a1539b959b16)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [PermissionsCoverageTest.java](TEST_CoverageTest_AR.md#file-3c11bfe3a379)، [TreasuryLedgerCoverageTest.java](TEST_FinancialMovementsTest_AR.md#file-aaf8d34190e8)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ChromeOptions getOptions()` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public WebDriver createDriver()` | public | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-e957d0c8ef7e"></a>
## Edge.java

**المصدر:** [src/main/java/Plugin_ERP/Drivers/Edge.java](../../../src/main/java/Plugin_ERP/Drivers/Edge.java)

**دوره:** خيارات Edge وإنشاء متصفحه حسب إعداد طريقة التنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 70 · **الأب المباشر:** `AbstractDriver`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [AbstractDriver.java](MAIN_Drivers_AR.md#file-a1539b959b16)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private EdgeOptions getOptions()` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public WebDriver createDriver()` | public | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-cafcec966ff0"></a>
## Firefox.java

**المصدر:** [src/main/java/Plugin_ERP/Drivers/Firefox.java](../../../src/main/java/Plugin_ERP/Drivers/Firefox.java)

**دوره:** مصنع Firefox بالاسم الإملائي الحالي في المشروع؛ راجع التنفيذ لمعرفة طرق التشغيل المدعومة.

**حالته:** مصدر نشط · **عدد أسطره:** 59 · **الأب المباشر:** `AbstractDriver`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [AbstractDriver.java](MAIN_Drivers_AR.md#file-a1539b959b16)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private FirefoxOptions getOptions()` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public WebDriver createDriver()` | public | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-933f9d647645"></a>
## WebDriverFactory.java

**المصدر:** [src/main/java/Plugin_ERP/Drivers/WebDriverFactory.java](../../../src/main/java/Plugin_ERP/Drivers/WebDriverFactory.java)

**دوره:** مصنع المتصفح: يختار النوع ويحتفظ بمتصفح الخيط ويمنع بدء آخر فوق جلسة نشطة.

**حالته:** مصدر نشط · **عدد أسطره:** 54

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [CashFormReader.java](MAIN_PAGES_BasicData_AR.md#file-0efcc911edbb)، [CashPage.java](MAIN_PAGES_BasicData_AR.md#file-ed12e0c67c34)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [CashTest.java](TEST_Tests_AR.md#file-e758c254da3e)، [TaypeOfDiscountTest.java](TEST_Tests_AR.md#file-f61729975a8f)، [TreasuryHandoverTest.java](TEST_Tests_AR.md#file-99e7d8e37af9)، [BrowserLifecycleTest.java](TEST_SuiteSupport_AR.md#file-42042d81f938)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private WebDriverFactory()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private static WebDriver getDriver(String driverType)` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static WebDriver initDriver()` | public | تهيئة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static WebDriver getDriver()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void quitDriver()` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void quitDriver(WebDriver ownedDriver)` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
