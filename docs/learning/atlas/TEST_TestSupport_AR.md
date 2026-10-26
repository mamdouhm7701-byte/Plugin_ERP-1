# اختبارات ودعم التجهيز المشترك

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-e25770849138"></a>
## BasicDataFixtures.java

**المصدر:** [src/test/java/TestSupport/BasicDataFixtures.java](../../../src/test/java/TestSupport/BasicDataFixtures.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 362

**ملفات تشير إليه مباشرة:** [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)، [SelectedFixtureRecoveryTest.java](TEST_CoverageTest_AR.md#file-a946dc1f3ddf)، [UnderReviewCoverageTest.java](TEST_Invoice_AR.md#file-4673cafe9c87)، [TreasuryHandoverTest.java](TEST_Tests_AR.md#file-99e7d8e37af9)، [AdjustmentCoverageScenario.java](TEST_StoreTest_AR.md#file-ed9be355104f)، [InventoryCoverageScenario.java](TEST_StoreTest_AR.md#file-6c4c9db203b7)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record Record(String endpoint, Object id, String name, Map<String, Object> values)` | public | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public BasicDataFixtures(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private BasicDataFixtures(WebDriver driver,String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void preserveAuditHistory()` | public | تنفيذ جزء متعلق بـ سجل المراجعة، حسب المدخلات والتخصص. |
| `public void preserveAuditHistory(String folder)` | public | تنفيذ جزء متعلق بـ سجل المراجعة، حسب المدخلات والتخصص. |
| `public void retainForRecovery(String reason)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void beginScenario()` | public | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `public static Map<String, Object> fields(Object... pairs)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Record account(String key)` | public | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `public Record bank(int index)` | public | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `public Record safe(int index)` | public | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `public Record user(String key)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Object query(String endpoint)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Record captured(String endpoint, String name)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Record awaitSaved(String endpoint, String name)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String, Object> read(Record record)` | public | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean underReviewExistsInList(Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Record fixture(String key, String endpoint, Map<String, Object> values)` | public | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void track(String endpoint, Object id, String note, Map<String,Object> values)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void recoverPendingFixtures()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String journalSuffix()` | public | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private static void requireEndpoint(String endpoint)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String reserveName(String endpoint, String label)` | public | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `public void collectUi()` | public | تجميع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void installCapture()` | public | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |
| `public void awaitUiSettled()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private Map<String,Object> cleanupIdentity(Record record)` | private | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c429296aa796"></a>
## BasicDataFixturesApi.java

**المصدر:** [src/test/java/TestSupport/BasicDataFixturesApi.java](../../../src/test/java/TestSupport/BasicDataFixturesApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 61 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `BasicDataFixturesApi(WebDriver driver, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-f591e486d141"></a>
## BasicDataFixturesApiStub.java

**المصدر:** [src/test/java/TestSupport/BasicDataFixturesApiStub.java](../../../src/test/java/TestSupport/BasicDataFixturesApiStub.java)

**دوره:** محاكاة ردود نقل البيانات لدعم فحوص الكود محليًا.

**حالته:** مصدر نشط · **عدد أسطره:** 158

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c8e882d4fc3a"></a>
## BasicDataFixturesDefinitions.java

**المصدر:** [src/test/java/TestSupport/BasicDataFixturesDefinitions.java](../../../src/test/java/TestSupport/BasicDataFixturesDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 32

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `BasicDataFixturesDefinitions(BasicDataFixtures data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record bank(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record safe(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |

<a id="file-74edc87f0fc3"></a>
## BasicDataFixturesRecovery.java

**المصدر:** [src/test/java/TestSupport/BasicDataFixturesRecovery.java](../../../src/test/java/TestSupport/BasicDataFixturesRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 14 · **الأب المباشر:** `Plugin_ERP.Tests.LoginTest.BaseTest`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-dcb41e2125ba"></a>
## BasicDataFixturesTest.java

**المصدر:** [src/test/java/TestSupport/BasicDataFixturesTest.java](../../../src/test/java/TestSupport/BasicDataFixturesTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 125

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `TestSupport.BasicDataFixturesTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-cf6cb5547507"></a>
## BasicDataScenarioTest.java

**المصدر:** [src/test/java/TestSupport/BasicDataScenarioTest.java](../../../src/test/java/TestSupport/BasicDataScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 59 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)، [PermissionsCoverageTest.java](TEST_CoverageTest_AR.md#file-3c11bfe3a379)، [UsersCoverageTest.java](TEST_CoverageTest_AR.md#file-23f4288c4be3)، [VouchersTest.java](TEST_GenralAccountantTest_AR.md#file-6e939ee3b900)، [AreaTest.java](TEST_Tests_AR.md#file-bc1b1188af8f)، [BankTest.java](TEST_Tests_AR.md#file-44bcd35d3f70)، [CashTest.java](TEST_Tests_AR.md#file-e758c254da3e)، [DisEntTest.java](TEST_Tests_AR.md#file-2d1a897250de)؛ العرض مختصر وليس قائمة كاملة.

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected final BasicDataFixtures basicData()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected final String ownedName(String endpoint, String label)` | protected | تنفيذ جزء متعلق بـ المملوك للاختبار / الاسم، حسب المدخلات والتخصص. |
| `public void beginBasicDataScenario()` | public | بدء نطاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void collectBasicDataScenario()` | public | تجميع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void cleanBasicDataFixtures()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `TestSupport.BasicDataScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-137c308279a5"></a>
## FixtureBrowser.java

**المصدر:** [src/test/java/TestSupport/FixtureBrowser.java](../../../src/test/java/TestSupport/FixtureBrowser.java)

**دوره:** أداة دعم لاختبارات fixture المحلية لمحاكاة ما يلزم من جلسة المتصفح.

**حالته:** مصدر نشط · **عدد أسطره:** 74

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public FixtureBrowser(WebDriver api) throws Exception` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void bind(Plugin_ERP.Tests.LoginTest.BaseTest scenario) throws IllegalAccessException` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void addAuxiliaryWindow()` | public | إضافة الإضافي / التبويب، حسب المدخلات والتخصص. |
| `public void close() throws IllegalAccessException` | public | إنهاء نطاق/مورد وتنظيفه حسب مالكه؛ اقرأ سياق الكلاس. |

<a id="file-cd3b35793c7a"></a>
## FixtureJournals.java

**المصدر:** [src/test/java/TestSupport/FixtureJournals.java](../../../src/test/java/TestSupport/FixtureJournals.java)

**دوره:** حفظ وقراءة سجلات ملكية بيانات الاختبار محليًا.

**حالته:** مصدر نشط · **عدد أسطره:** 43

**ملفات تشير إليه مباشرة:** [PostingCoverageTest.java](TEST_Invoice_AR.md#file-ae7931450ae7)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private FixtureJournals()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static void write(Path path, Object rows)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-fbd774cea6e0"></a>
## FixtureSession.java

**المصدر:** [src/test/java/TestSupport/FixtureSession.java](../../../src/test/java/TestSupport/FixtureSession.java)

**دوره:** يحفظ عنوان خدمة ERP والفرع ويتحقق من نفس السياق قبل التعامل مع بيانات التجربة.

**حالته:** مصدر نشط · **عدد أسطره:** 54

**ملفات تشير إليه مباشرة:** [PermissionsCoverageTest.java](TEST_CoverageTest_AR.md#file-3c11bfe3a379)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public FixtureSession(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private Object script(String code)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void verify()` | public | مراجعة سياق أو عقد حسب الكلاس، مثل ثبات الفرع والعنوان. |
| `public void verify(Map<String, Object> row)` | public | مراجعة سياق أو عقد حسب الكلاس، مثل ثبات الفرع والعنوان. |

<a id="file-f53c7b7ee236"></a>
## PendingFixtureWrites.java

**المصدر:** [src/test/java/TestSupport/PendingFixtureWrites.java](../../../src/test/java/TestSupport/PendingFixtureWrites.java)

**دوره:** تسجيل محاولة إنشاء قبل إرسالها ومعالجة التزام خادم ضاع رده.

**حالته:** مصدر نشط · **عدد أسطره:** 152

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public PendingFixtureWrites(FixtureSession session, String folder, String suffix)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void persist()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void start(String endpoint, String name, Map<String, Object> values)` | public | تسجيل بداية محاولة أو إجراء. |
| `public void committed(String endpoint, String name)` | public | إزالة محاولة معلقة بعد التأكد من تسجيلها. |
| `public void resume(Set<String> allowed, String prefix) throws java.io.IOException` | public | إعادة تحميل حالة تشغيل محفوظة للتحقق أو الاسترجاع. |
| `public void recover( BiFunction<String, Map<String, Object>, Map<String, Object>> api, Recovered recovered)` | public | البحث عن آثار محاولة سابقة ومتابعة ملكيتها بأمان. |
| `private static boolean matchesName(Map<?, ?> row, String name)` | private | مطابقة الاسم، حسب المدخلات والتخصص. |
| `private static void find(Object node, String name, List<Map<String, Object>> matches)` | private | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-047a4a04ebe2"></a>
## SalesTaxProfile.java

**المصدر:** [src/test/java/TestSupport/SalesTaxProfile.java](../../../src/test/java/TestSupport/SalesTaxProfile.java)

**دوره:** قراءة وتفسير سياسة ضريبة المبيعات لحساب المتوقع المناسب لسياقها.

**حالته:** مصدر نشط · **عدد أسطره:** 38

**ملفات تشير إليه مباشرة:** [SalesTaxProfileTest.java](TEST_SuiteSupport_AR.md#file-74df0c4fda8e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record SalesTaxProfile(String policy, List<BigDecimal> rates)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static SalesTaxProfile capture(WebDriver driver)` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `public String net(String amountAfterDiscount)` | public | تنفيذ جزء متعلق بـ الصافي، حسب المدخلات والتخصص. |

<a id="file-e5d49c5182a1"></a>
## TreasuryAmounts.java

**المصدر:** [src/test/java/TestSupport/TreasuryAmounts.java](../../../src/test/java/TestSupport/TreasuryAmounts.java)

**دوره:** تمثيل/مراجعة مبالغ تخص سيناريوهات تسليم الخزينة.

**حالته:** مصدر نشط · **عدد أسطره:** 26

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record TreasuryAmounts(BigDecimal cash, BigDecimal total, BigDecimal actual)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static TreasuryAmounts from(Map<String,Object> totals, BigDecimal actual)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static BigDecimal sum(Map<String,Object> row, String[] fields)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public BigDecimal deficit()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-9e972627600e"></a>
## TreasuryAmountsTest.java

**المصدر:** [src/test/java/TestSupport/TreasuryAmountsTest.java](../../../src/test/java/TestSupport/TreasuryAmountsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 27

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private Map<String,Object> ledger()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `TestSupport.TreasuryAmountsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-f5bdb9a9ad66"></a>
## TreasuryFixtures.java

**المصدر:** [src/test/java/TestSupport/TreasuryFixtures.java](../../../src/test/java/TestSupport/TreasuryFixtures.java)

**دوره:** تجهيز بيانات تدفقات الخزينة المملوكة للاختبارات.

**حالته:** مصدر نشط · **عدد أسطره:** 152

**ملفات تشير إليه مباشرة:** [TreasuryHandoverTest.java](TEST_Tests_AR.md#file-99e7d8e37af9)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public TreasuryFixtures(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private TreasuryFixtures(WebDriver driver, String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static TreasuryFixtures resume(WebDriver driver, String suffix) throws java.io.IOException` | public | إعادة تحميل حالة تشغيل محفوظة للتحقق أو الاسترجاع. |
| `public void expect(BasicDataFixtures.Record safe, BasicDataFixtures.Record user)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Object open(BasicDataFixtures.Record safe, BasicDataFixtures.Record user, double balance)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String,Object> awaitSingle(BasicDataFixtures.Record safe, BasicDataFixtures.Record user)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String,Object> requireSingle(BasicDataFixtures.Record safe, BasicDataFixtures.Record user)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String,Object> totals(Object id)` | public | تنفيذ جزء متعلق بـ الإجماليات، حسب المدخلات والتخصص. |
| `static boolean same(Object a,Object b)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private List<Map<String,Object>> matching(Object safeId,Object userId)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void persist()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void verifyParent(String endpoint,Object id,Object name)` | private | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-510f62deecd0"></a>
## WorkflowCleanupOrder.java

**المصدر:** [src/test/java/TestSupport/WorkflowCleanupOrder.java](../../../src/test/java/TestSupport/WorkflowCleanupOrder.java)

**دوره:** تحديد ترتيب تنظيف كيانات رحلة مركبة وفق اعتمادها على بعضها.

**حالته:** مصدر نشط · **عدد أسطره:** 27

**ملفات تشير إليه مباشرة:** [SalesSettlementEndToEndTest.java](TEST_Invoice_AR.md#file-5e0d70f6fb59)، [PurchaseSettlementEndToEndTest.java](TEST_Purches_AR.md#file-6492663b7f2d)، [SupplyChainEndToEndTest.java](TEST_Purches_AR.md#file-81dbab96b057)، [ManufacturingSalesEndToEndTest.java](TEST_StoreTest_AR.md#file-cfde8472b5ed)، [WorkflowCleanupOrderTest.java](TEST_SuiteSupport_AR.md#file-d8b5fbecbe47)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private WorkflowCleanupOrder()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static <T extends AutoCloseable> void closeChildrenBeforeParents( List<T> children, Runnable parentCleanup)` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-d394a5f89a8c"></a>
## WorkflowEntity.java

**المصدر:** [src/test/java/TestSupport/WorkflowEntity.java](../../../src/test/java/TestSupport/WorkflowEntity.java)

**دوره:** وصف كيان يشارك في ترتيب تنظيف رحلة عمل.

**حالته:** مصدر نشط · **عدد أسطره:** 10

**ملفات تشير إليه مباشرة:** [SalesWorkflow.java](TEST_Invoice_AR.md#file-1d10b6cfe30c)، [SupplyChainEndToEndTest.java](TEST_Purches_AR.md#file-81dbab96b057)، [ManufacturingSalesEndToEndTest.java](TEST_StoreTest_AR.md#file-cfde8472b5ed)، [TransferWorkflow.java](TEST_StoreTest_AR.md#file-9a6d69781352)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record WorkflowEntity(Object id, String name)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
