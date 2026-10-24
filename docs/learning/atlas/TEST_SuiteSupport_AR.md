# اختبارات ودعم عقود التشغيل

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-42042d81f938"></a>
## BrowserLifecycleTest.java

**المصدر:** [src/test/java/SuiteSupport/BrowserLifecycleTest.java](../../../src/test/java/SuiteSupport/BrowserLifecycleTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 128 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void headlessSetupKeepsTheDesktopViewportInsteadOfMaximizing() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void closingOneClassDoesNotCloseAnotherClassBrowser() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void loginFailureClosesItsBrowserAndKeepsTheOriginalFailure()` | public · @Test | حالة تحقق مرتبطة بـتسجيل الدخول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void quitFailureReleasesFactoryAndDuplicateInitCannotReplaceAnActiveBrowser() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `void requireReady()` | داخل الحزمة | تنفيذ جزء متعلق بـ الجاهزية، حسب المدخلات والتخصص. |
| `private WebDriver.Options options()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebDriver browser()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.BrowserLifecycleTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-17f5ebee2127"></a>
## CiSuiteContractTest.java

**المصدر:** [src/test/java/SuiteSupport/CiSuiteContractTest.java](../../../src/test/java/SuiteSupport/CiSuiteContractTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 50

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [LoginTest.java](TEST_Tests_AR.md#file-3a9647b96039)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void ciDoesNotSelectLiveErpSessions() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ciRetainsLocalCleanupAndRuntimeChecks() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.CiSuiteContractTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-427be5c07b8a"></a>
## CoverageExpansionContractTest.java

**المصدر:** [src/test/java/SuiteSupport/CoverageExpansionContractTest.java](../../../src/test/java/SuiteSupport/CoverageExpansionContractTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 32

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.CoverageExpansionContractTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-736bb797f117"></a>
## DocumentDatesTest.java

**المصدر:** [src/test/java/SuiteSupport/DocumentDatesTest.java](../../../src/test/java/SuiteSupport/DocumentDatesTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 31

**يرتبط مباشرة بملفات داخل المشروع:** [DocumentDates.java](MAIN_Utiel_AR.md#file-fedb3dfc92da)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] displayedDates()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void acceptsDatesFromArabicAndIsoPickers(String displayed, String expected)` | public · @Test | حالة تحقق مرتبطة بـالعربي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsImpossibleDates()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.DocumentDatesTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e9a6f2e89ea1"></a>
## ModuleChoiceTest.java

**المصدر:** [src/test/java/SuiteSupport/ModuleChoiceTest.java](../../../src/test/java/SuiteSupport/ModuleChoiceTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 129

**يرتبط مباشرة بملفات داخل المشروع:** [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] redrawStages()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void reacquiresInputAfterDropdownRedraw(String module, String failingOperation)` | public · @Test | حالة تحقق مرتبطة بـالقائمة المنسدلة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] modules()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ambiguousOwnedOptionsStillStopSelection(String module)` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `java.util.function.BiConsumer<String, String> page(String module)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `private WebElement element(boolean input)` | private | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.ModuleChoiceTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-da1d4ef316f4"></a>
## ModuleFixtureContractTest.java

**المصدر:** [src/test/java/SuiteSupport/ModuleFixtureContractTest.java](../../../src/test/java/SuiteSupport/ModuleFixtureContractTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 43

**يرتبط مباشرة بملفات داخل المشروع:** [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void everyBasicAndAccountingScreenOwnsItsLifecycle() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ciKeepsTheNewCleanupRegressions() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.ModuleFixtureContractTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-569314d64dd0"></a>
## RuntimeCompatibilityTest.java

**المصدر:** [src/test/java/SuiteSupport/RuntimeCompatibilityTest.java](../../../src/test/java/SuiteSupport/RuntimeCompatibilityTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 29

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void java21RunsWithWorkingAllureStepInstrumentation()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private String currentAnnotatedStep()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.RuntimeCompatibilityTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-74df0c4fda8e"></a>
## SalesTaxProfileTest.java

**المصدر:** [src/test/java/SuiteSupport/SalesTaxProfileTest.java](../../../src/test/java/SuiteSupport/SalesTaxProfileTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 30

**يرتبط مباشرة بملفات داخل المشروع:** [SalesTaxProfile.java](TEST_TestSupport_AR.md#file-047a4a04ebe2)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] examples()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void expectedTaxUsesPolicyAndConfiguredRates(String policy, List<BigDecimal> rates, String base, String expected)` | public · @Test | حالة تحقق مرتبطة بـالضريبة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.SalesTaxProfileTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9fdd764c169f"></a>
## SuiteDefinitionsTest.java

**المصدر:** [src/test/java/SuiteSupport/SuiteDefinitionsTest.java](../../../src/test/java/SuiteSupport/SuiteDefinitionsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 184

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] testLevels()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void suiteSelectsRealEnabledTestsWithoutDuplicates(String level) throws Exception` | public · @Test | حالة تحقق مرتبطة بـالإتاحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void quickChecksStaySmallAndRegressionIncludesTheWorkflows() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void specialSalesUseFullRegressionAndOnlyPersistedJourneysInE2e() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالمبيعات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private static List<String> selectedCases(String level) throws Exception` | private | تنفيذ جزء متعلق بـ القيمة المختارة، حسب المدخلات والتخصص. |
| `private static void collect(XmlSuite suite, List<String> selected) throws Exception` | private | تجميع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.SuiteDefinitionsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-d8b5fbecbe47"></a>
## WorkflowCleanupOrderTest.java

**المصدر:** [src/test/java/SuiteSupport/WorkflowCleanupOrderTest.java](../../../src/test/java/SuiteSupport/WorkflowCleanupOrderTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 43

**يرتبط مباشرة بملفات داخل المشروع:** [WorkflowCleanupOrder.java](TEST_TestSupport_AR.md#file-510f62deecd0)

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SuiteSupport.WorkflowCleanupOrderTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

