# اختبارات ودعم المخازن

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-997302a596bb"></a>
## AdditionOrderTest.java

**المصدر:** [src/test/java/StoreTest/AdditionOrderTest.java](../../../src/test/java/StoreTest/AdditionOrderTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 109 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected StoreMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreModulePage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AdditionOrderTest()` | public · @Test | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void AddAdditionOrderTestByName()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddAdditionByBarcode()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditAddition()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteAddition()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertItemTypeRequired()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertCustomerRequired()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertAgentRequired()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertDistributionEntityRequired()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertErrorWhenSavingWithoutProducts()` | public · @Test | حالة تحقق مرتبطة بـالخطأ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddOldDate()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_NotesRoundTrip()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_PrimaryPartyIdentity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_AgentIdentity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_DestinationIdentity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.AdditionOrderTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-ed9be355104f"></a>
## AdjustmentCoverageScenario.java

**المصدر:** [src/test/java/StoreTest/AdjustmentCoverageScenario.java](../../../src/test/java/StoreTest/AdjustmentCoverageScenario.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 99 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [BasicDataFixtures.java](TEST_TestSupport_AR.md#file-e25770849138)

**ملفات تشير إليه مباشرة:** [OverflowAdjustmentCoverageTest.java](TEST_StoreTest_AR.md#file-648891d99b08)، [ShortageAdjustmentCoverageTest.java](TEST_StoreTest_AR.md#file-9ae2f084aa7e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract String endpoint();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract String route();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract int stockDirection();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract String title();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `private void openDetail()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-d3a6127526af"></a>
## DamageAdjustmentTest.java

**المصدر:** [src/test/java/StoreTest/DamageAdjustmentTest.java](../../../src/test/java/StoreTest/DamageAdjustmentTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 67 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected StoreMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreModulePage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddDamageAdjustmentTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddDamageAdjustmentByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddDamageAdjustmentByBarcodeTest()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditAddDamageAdjustment()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteDamageAdjustment()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddOldDateTest()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithoutQuantityTest()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_NotesRoundTrip()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.DamageAdjustmentTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-d1a8e1785324"></a>
## ExchangeOrderTest.java

**المصدر:** [src/test/java/StoreTest/ExchangeOrderTest.java](../../../src/test/java/StoreTest/ExchangeOrderTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 109 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected StoreMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreModulePage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddExchangeWithItemsList()` | public · @Test | حالة تحقق مرتبطة بـالأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddExchangeByName()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddExchangeByBarcode()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditExchange()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteExchange()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertItemTypeRequired()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertCustomerRequired()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertAgentRequired()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertDistributionEntityRequired()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertErrorWhenSavingWithoutProducts()` | public · @Test | حالة تحقق مرتبطة بـالخطأ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddOldDate()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_NotesRoundTrip()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_PrimaryPartyIdentity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_AgentIdentity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_DestinationIdentity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.ExchangeOrderTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-6c4c9db203b7"></a>
## InventoryCoverageScenario.java

**المصدر:** [src/test/java/StoreTest/InventoryCoverageScenario.java](../../../src/test/java/StoreTest/InventoryCoverageScenario.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 78 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [BasicDataFixtures.java](TEST_TestSupport_AR.md#file-e25770849138)

**ملفات تشير إليه مباشرة:** [StocktakingCoverageTest.java](TEST_StoreTest_AR.md#file-85150ffba28a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract String endpoint();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract String route();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract String title();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `protected abstract int stockDirection();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void prepare(String quantity)` | protected | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `protected BasicDataFixtures.Record save()` | protected | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `protected void balance(BigDecimal expected)` | protected | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `public void savesFractionalQuantityAndChecksStockEffect()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / الكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

<a id="file-cfde8472b5ed"></a>
## ManufacturingSalesEndToEndTest.java

**المصدر:** [src/test/java/StoreTest/ManufacturingSalesEndToEndTest.java](../../../src/test/java/StoreTest/ManufacturingSalesEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 80 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ManufacturingPage.java](MAIN_PAGES_StoreModel_AR.md#file-fc537914e024)، [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)، [SalesWorkflow.java](TEST_Invoice_AR.md#file-1d10b6cfe30c)، [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)، [WorkflowCleanupOrder.java](TEST_TestSupport_AR.md#file-510f62deecd0)، [WorkflowEntity.java](TEST_TestSupport_AR.md#file-d394a5f89a8c)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static WorkflowEntity ref(StoreTestData.Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void balance(StoreTestData.Record record, int store, BigDecimal expected)` | private | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `private SalesWorkflow seller(int store, StoreTestData.Record customer)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void debt(StoreTestData.Record customer, BigDecimal before, String amount)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private BigDecimal signed(java.util.Map<String, Object> row)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void manufacturingThenSellingConsumesComponentsOnlyOnce()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void manufacturingTransferAndFractionalSaleRetainUnsoldProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleanStoreScenario(ITestResult result)` | public | تنظيف المخزن، حسب المدخلات والتخصص. |
| `public void retryStoreCleanup()` | public | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.ManufacturingSalesEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-a48f28d303ea"></a>
## ManufacturingTest.java

**المصدر:** [src/test/java/StoreTest/ManufacturingTest.java](../../../src/test/java/StoreTest/ManufacturingTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 67 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected StoreMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreModulePage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddManufacturingTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddManufacturingByBarcodeTest()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddManufacturingByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteManufacturingByBarcodeTest()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertStoreRequired()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertErorrPopUpWhenAddProuctWithOutStore()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertStorePopUpWhenAddProuctWithOutBalancee()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_EditManufacturing()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.ManufacturingTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-648891d99b08"></a>
## OverflowAdjustmentCoverageTest.java

**المصدر:** [src/test/java/StoreTest/OverflowAdjustmentCoverageTest.java](../../../src/test/java/StoreTest/OverflowAdjustmentCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 7 · **الأب المباشر:** `AdjustmentCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [AdjustmentCoverageScenario.java](TEST_StoreTest_AR.md#file-ed9be355104f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `protected int stockDirection()` | protected | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.OverflowAdjustmentCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9ae2f084aa7e"></a>
## ShortageAdjustmentCoverageTest.java

**المصدر:** [src/test/java/StoreTest/ShortageAdjustmentCoverageTest.java](../../../src/test/java/StoreTest/ShortageAdjustmentCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 7 · **الأب المباشر:** `AdjustmentCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [AdjustmentCoverageScenario.java](TEST_StoreTest_AR.md#file-ed9be355104f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `protected int stockDirection()` | protected | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.ShortageAdjustmentCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-85150ffba28a"></a>
## StocktakingCoverageTest.java

**المصدر:** [src/test/java/StoreTest/StocktakingCoverageTest.java](../../../src/test/java/StoreTest/StocktakingCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 7 · **الأب المباشر:** `InventoryCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [InventoryCoverageScenario.java](TEST_StoreTest_AR.md#file-6c4c9db203b7)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `protected int stockDirection()` | protected | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.StocktakingCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-973438e9bb26"></a>
## StoreChoiceTest.java

**المصدر:** [src/test/java/StoreTest/StoreChoiceTest.java](../../../src/test/java/StoreTest/StoreChoiceTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 104

**يرتبط مباشرة بملفات داخل المشروع:** [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] redrawStages()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void reacquiresInputAfterDropdownRedraw(String failingOperation)` | public · @Test | حالة تحقق مرتبطة بـالقائمة المنسدلة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ambiguousOwnedOptionsStillStopSelection()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `StoreModulePage page()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `protected Object js(String script, Object... args)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private WebElement element(boolean input)` | private | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.StoreChoiceTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-c622e88ca0a4"></a>
## StoreCleanupTest.java

**المصدر:** [src/test/java/StoreTest/StoreCleanupTest.java](../../../src/test/java/StoreTest/StoreCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 803

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void fixtureCallWaitsForOriginalBranchToHydrateAfterNavigation()` | public · @Test | حالة تحقق مرتبطة بـبيانات الاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixtureCallStillRejectsAnActualBranchChangeBeforeMutation()` | public · @Test | حالة تحقق مرتبطة بـبيانات الاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixtureCallStillRejectsAnActualOriginChangeBeforeMutation()` | public · @Test | حالة تحقق مرتبطة بـبيانات الاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void postedInventoryFixtureCreatesOnlyTheStoreItsAuditNeeds()` | public · @Test | حالة تحقق مرتبطة بـالجرد / بيانات الاختبار / المخزن / سجل المراجعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainCreateStillRejectsUntrackedMovementInOwnedStore()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainCreateDoesNotConfuseEarlierOwnedMovementInSharedStore()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void normalCustomerIsTaxableAndKeepsItsOwnAccount()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletesOwnedMovementBeforeItsItemsAndStores()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversStoreCommittedBeforeLostCreateResponse()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainStoreLookupFailurePreservesItsAccounts()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainStoreMustStillReferenceItsOwnedAccount()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / المملوك للاختبار / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void resumesPendingStoreCreationFromDurableJournal() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالمخزن / سجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsStockDependenciesWhenMovementDeletionFails()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesTrackingUnownedRecord()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void checksIdentityBeforeFirstDeletion()` | public · @Test | حالة تحقق مرتبطة بـالأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansManufacturingWithoutNotesUsingOwnedStoreAndItems()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات / المملوك للاختبار / المخزن / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsManufacturingWithChangedStoreIdentity()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsManufacturingWithChangedItemIdentity()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void manufacturingIdentityMismatch(boolean storeChanged)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void recoversWarehouseCreateCommittedBeforeLostResponse()` | public · @Test | حالة تحقق مرتبطة بـالمخازن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainWarehouseLookupFailureRetainsAllParents()` | public · @Test | حالة تحقق مرتبطة بـالمخازن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversUpdatedNoteAfterLostUpdateResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsMissingDeleteWhenOwnedRecordStillExists()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversUiItemCommittedBeforeFailedResponse()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversInvalidItemByUniqueEnglishMarker()` | public · @Test | حالة تحقق مرتبطة بـالصنف / الإنجليزي / علامة الملكية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void verifiesCommittedBranchUnlinkAfterLostResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retriesUncommittedDeleteBeforeParentAccounts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsEmptyStringReadAfterCommittedDeleteTimeout()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansOwnedItemCommittedBeforeCreateFailure()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingMovementDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingMasterDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void updatedRecordDeletesBeforeItsNewReference()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageScopeSharesOnlyStableReferences()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public String smCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> smSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> smAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public void smSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String smDiagnosticMarkup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String smDiagnostics()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.StoreCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9da77cc8d37c"></a>
## StoreExportAssertions.java

**المصدر:** [src/test/java/StoreTest/StoreExportAssertions.java](../../../src/test/java/StoreTest/StoreExportAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 104

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private StoreExportAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format)` | داخل الحزمة | مقارنة متوقعة التصدير، حسب المدخلات والتخصص. |
| `private static void installDownloadCapture(WebDriver driver)` | private | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-8e534777bc79"></a>
## StoreFixtureApi.java

**المصدر:** [src/test/java/StoreTest/StoreFixtureApi.java](../../../src/test/java/StoreTest/StoreFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 60 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `StoreFixtureApi(WebDriver driver, String origin, Object sessionBranchId)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-25ed598eb1e0"></a>
## StoreFixtureApiStub.java

**المصدر:** [src/test/java/StoreTest/StoreFixtureApiStub.java](../../../src/test/java/StoreTest/StoreFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 172

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-753f3cd1002c"></a>
## StoreFixtureDefinitions.java

**المصدر:** [src/test/java/StoreTest/StoreFixtureDefinitions.java](../../../src/test/java/StoreTest/StoreFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 84

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `StoreFixtureDefinitions(StoreTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String,Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record inventoryItem(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الجرد / الصنف، حسب المدخلات والتخصص. |
| `Record manufactured()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record manufactured(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record agent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record destination()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record supplier()` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |

<a id="file-0e0b53a1e7f7"></a>
## StoreFixtureRecovery.java

**المصدر:** [src/test/java/StoreTest/StoreFixtureRecovery.java](../../../src/test/java/StoreTest/StoreFixtureRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 27 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] journalIds) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-044c3e34d526"></a>
## StoreMovementKind.java

**المصدر:** [src/test/java/StoreTest/StoreMovementKind.java](../../../src/test/java/StoreTest/StoreMovementKind.java)

**دوره:** تعريف أنواع مستندات/حركات المجال وربطها بمساراتها أو خصائص سلوكها.

**حالته:** مصدر نشط · **عدد أسطره:** 25

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `StoreMovementKind(String route, String list, String endpoint, String type)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean party()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-ec2ec4d5c577"></a>
## StoreScenarioTest.java

**المصدر:** [src/test/java/StoreTest/StoreScenarioTest.java](../../../src/test/java/StoreTest/StoreScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 168 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [AdditionOrderTest.java](TEST_StoreTest_AR.md#file-997302a596bb)، [DamageAdjustmentTest.java](TEST_StoreTest_AR.md#file-d3a6127526af)، [ExchangeOrderTest.java](TEST_StoreTest_AR.md#file-d1a8e1785324)، [ManufacturingSalesEndToEndTest.java](TEST_StoreTest_AR.md#file-cfde8472b5ed)، [ManufacturingTest.java](TEST_StoreTest_AR.md#file-a48f28d303ea)، [TransBetweenStoreTest.java](TEST_StoreTest_AR.md#file-9294fe16f7c9)، [WarehouseEndToEndTest.java](TEST_StoreTest_AR.md#file-fca1c61db45d)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract StoreMovementKind kind();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract StoreModulePage movementPage();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `protected String baselineMode()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String defaultParty()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreTestData.Record ownItem(int index)` | protected | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `private StoreTestData.Record ownParty(String type)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String partyLabel(String type)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void prepare(String omitted,String partyType)` | protected | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `private void snapshot(StoreTestData.Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private BigDecimal initial(StoreTestData.Record record,int index)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void fill(String mode)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void add(StoreTestData.Record record,String mode)` | private | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreTestData.Record save(boolean print)` | protected | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `private void verifyReport(StoreTestData.Record record)` | private | التحقق من التقرير، حسب المدخلات والتخصص. |
| `protected static BigDecimal number(Object n)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void sameNumber(Object actual,String expected)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void balance(StoreTestData.Record record,int index,BigDecimal expected)` | private | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `protected void assertEffect(String quantity)` | protected | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreTestData.Record create(String mode,boolean print,String party)` | protected | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void partyIdentity(String type)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean hasId(Object id)` | private | السؤال عن وجود القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void editOwn()` | protected | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void find(StoreTestData.Record record)` | private | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void deleteOwn()` | protected | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void required(String missing,String party,String expected)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void rejectQuantity(String quantity)` | protected | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `protected void noItems()` | protected | تنفيذ جزء متعلق بـ الأصناف، حسب المدخلات والتخصص. |
| `protected void oldDate()` | protected | تنفيذ جزء متعلق بـ التاريخ، حسب المدخلات والتخصص. |
| `protected void pickerWithoutStore()` | protected | تنفيذ جزء متعلق بـ نافذة الاختيار / المخزن، حسب المدخلات والتخصص. |
| `protected void sameStore()` | protected | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `protected void pickerWithoutDestination()` | protected | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `protected void notesRoundTrip()` | protected | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `private void export(String format)` | private | تصدير التصدير، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.StoreScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-b2db87a81e82"></a>
## StoreSuitePolicy.java

**المصدر:** [src/test/java/StoreTest/StoreSuitePolicy.java](../../../src/test/java/StoreTest/StoreSuitePolicy.java)

**دوره:** قواعد مستوى/اختيار حالات المجال أو ضبطها؛ ليست صفحة أو وصف بيانات ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 20

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void alter(List<XmlSuite> suites)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-6ee30a2c9337"></a>
## StoreTestData.java

**المصدر:** [src/test/java/StoreTest/StoreTestData.java](../../../src/test/java/StoreTest/StoreTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 492

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `StoreTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private StoreTestData(WebDriver driver,String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `static Map<String, Object> fields(Object... pairs)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record store(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private boolean containsStore(Map<String,Object> branch,Object storeId)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private void changeBranchStore(Record link,boolean add)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Map<String,Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record inventoryItem(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الجرد / الصنف، حسب المدخلات والتخصص. |
| `Record manufactured()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record manufactured(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record agent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record destination()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record supplier()` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Object balance(Record item,int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `void awaitOriginalSession()` | داخل الحزمة | انتظار الجلسة، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean existsInList(Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void track(String endpoint, Object id, String note, Map<String,Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `void preserveInventoryAuditHistory()` | داخل الحزمة | تنفيذ جزء متعلق بـ الجرد / سجل المراجعة، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean owns(Record record,Map<String,Object> stored)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void journalAttempts()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع / المحاولات، حسب المدخلات والتخصص. |
| `Map<String,Object> rememberFixtureAttempt(String endpoint,String name,Map<String,Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `else if(entity instanceof Map<?,?> result&&result.get("pageData") instanceof List<?> list&&result.get("totalRecords") instanceof Number count)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-9294fe16f7c9"></a>
## TransBetweenStoreTest.java

**المصدر:** [src/test/java/StoreTest/TransBetweenStoreTest.java](../../../src/test/java/StoreTest/TransBetweenStoreTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 79 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected StoreMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected StoreModulePage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void TransBetweenStoreTest()` | public · @Test | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void TransBetweenStoreAddWWithBarcodeTest()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / الباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TransBetweenStoreAddWithNameTest()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditTransBetweenStore()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteAllTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertToStoreRequired()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertErorrWhenChosenOneStore()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CantChosenSameStore()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CantSaveWithOutChosenProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SM_NotesRoundTrip()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.TransBetweenStoreTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9a6d69781352"></a>
## TransferWorkflow.java

**المصدر:** [src/test/java/StoreTest/TransferWorkflow.java](../../../src/test/java/StoreTest/TransferWorkflow.java)

**دوره:** خطوات رحلة أعمال معاد استخدامها، بدل تكرار تسلسل عدة شاشات داخل كل اختبار.

**حالته:** مصدر نشط · **عدد أسطره:** 66

**يرتبط مباشرة بملفات داخل المشروع:** [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)، [WorkflowEntity.java](TEST_TestSupport_AR.md#file-d394a5f89a8c)

**ملفات تشير إليه مباشرة:** [SupplyChainEndToEndTest.java](TEST_Purches_AR.md#file-81dbab96b057)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public TransferWorkflow(WebDriver driver, WorkflowEntity item)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void transfer(WorkflowEntity from, WorkflowEntity to, String quantity)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void capture()` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-fca1c61db45d"></a>
## WarehouseEndToEndTest.java

**المصدر:** [src/test/java/StoreTest/WarehouseEndToEndTest.java](../../../src/test/java/StoreTest/WarehouseEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 108 · **الأب المباشر:** `StoreScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)، [StoreScenarioTest.java](TEST_StoreTest_AR.md#file-ec2ec4d5c577)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void beginStoreScenario()` | public | بدء نطاق المخزن، حسب المدخلات والتخصص. |
| `private StoreTestData.Record stage(StoreMovementKind movement, int from, int to, String quantity)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void balance(int store, java.math.BigDecimal expected)` | private | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `public void receiptThenTransferThenIssueFromDestination()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void transferThenReverseTransferRestoresBothStores()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void receiptTransferAndDamagePreserveUnrelatedStock()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void manufactureThenTransferThenIssueFinishedProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalRoundTripPreservesBothStoreBalances()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `StoreTest.WarehouseEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

