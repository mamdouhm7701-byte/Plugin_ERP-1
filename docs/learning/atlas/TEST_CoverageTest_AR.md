# اختبارات ودعم التغطية الموسعة

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-f0182f1aa30f"></a>
## AccountGroupsCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/AccountGroupsCoverageTest.java](../../../src/test/java/CoverageTest/AccountGroupsCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 6 · **الأب المباشر:** `MasterCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.AccountGroupsCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-67388f9fb2ba"></a>
## AuditLogCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/AuditLogCoverageTest.java](../../../src/test/java/CoverageTest/AuditLogCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 19 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void directAuditRouteDisplaysAuditTrail()` | public · @Test | حالة تحقق مرتبطة بـسجل المراجعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.AuditLogCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-ac8ee0867a22"></a>
## ChartOfAccountsCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/ChartOfAccountsCoverageTest.java](../../../src/test/java/CoverageTest/ChartOfAccountsCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 6 · **الأب المباشر:** `MasterCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.ChartOfAccountsCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-986e49c0a0c6"></a>
## CostCentersCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/CostCentersCoverageTest.java](../../../src/test/java/CoverageTest/CostCentersCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 6 · **الأب المباشر:** `MasterCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.CostCentersCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-70a588c411f7"></a>
## MasterCoverageScenario.java

**المصدر:** [src/test/java/CoverageTest/MasterCoverageScenario.java](../../../src/test/java/CoverageTest/MasterCoverageScenario.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 137 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [BasicDataFixtures.java](TEST_TestSupport_AR.md#file-e25770849138)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**ملفات تشير إليه مباشرة:** [AccountGroupsCoverageTest.java](TEST_CoverageTest_AR.md#file-f0182f1aa30f)، [ChartOfAccountsCoverageTest.java](TEST_CoverageTest_AR.md#file-ac8ee0867a22)، [CostCentersCoverageTest.java](TEST_CoverageTest_AR.md#file-986e49c0a0c6)، [TablesCoverageTest.java](TEST_CoverageTest_AR.md#file-7986aafc4a3c)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract String endpoint();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract String route();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract String title();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `private boolean table()` | private | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `private boolean tree()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String newCode()` | private | بدء جديد الكود، حسب المدخلات والتخصص. |
| `private Map<String,Object> seedValues()` | private | تنفيذ جزء متعلق بـ القيم، حسب المدخلات والتخصص. |
| `private BasicDataFixtures.Record seed()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void newForm()` | private | بدء جديد النموذج، حسب المدخلات والتخصص. |
| `private void fill(String marker)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private BasicDataFixtures.Record create()` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void edit(BasicDataFixtures.Record record)` | private | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void selectTreeRecord(BasicDataFixtures.Record record)` | private | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void createsAndReadsBackExactOwnedRecord()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savedNotesSurviveReopening()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void editsOwnedRecordWithoutCreatingAnother()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void missingRequiredFieldCannotPersist()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void abandoningFormDoesNotSave()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cancelAndConfirmDeleteOwnRecord()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / التأكيد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

<a id="file-3c11bfe3a379"></a>
## PermissionsCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/PermissionsCoverageTest.java](../../../src/test/java/CoverageTest/PermissionsCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 51 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Chrome.java](MAIN_Drivers_AR.md#file-14889194f9e4)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)، [FixtureSession.java](TEST_TestSupport_AR.md#file-fbd774cea6e0)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void restrictedUserCannotOpenProtectedRouteOrReadApi()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void unauthenticatedDirectRouteRequiresLogin()` | public · @Test | حالة تحقق مرتبطة بـتسجيل الدخول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.PermissionsCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-a946dc1f3ddf"></a>
## SelectedFixtureRecoveryTest.java

**المصدر:** [src/test/java/CoverageTest/SelectedFixtureRecoveryTest.java](../../../src/test/java/CoverageTest/SelectedFixtureRecoveryTest.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 27 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [BasicDataFixtures.java](TEST_TestSupport_AR.md#file-e25770849138)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String[] ids(String property)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void recover(String property,String owner) throws Exception` | private | البحث عن آثار محاولة سابقة ومتابعة ملكيتها بأمان. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.SelectedFixtureRecoveryTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-7986aafc4a3c"></a>
## TablesCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/TablesCoverageTest.java](../../../src/test/java/CoverageTest/TablesCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 6 · **الأب المباشر:** `MasterCoverageScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected String endpoint()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String route()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String title()` | protected | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.TablesCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-23f4288c4be3"></a>
## UsersCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/UsersCoverageTest.java](../../../src/test/java/CoverageTest/UsersCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 53 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private void chooseCurrentBranch()` | private | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void form()` | private | تنفيذ جزء متعلق بـ النموذج، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.UsersCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-eae457cc2254"></a>
## ZatcaConfigurationCoverageTest.java

**المصدر:** [src/test/java/CoverageTest/ZatcaConfigurationCoverageTest.java](../../../src/test/java/CoverageTest/ZatcaConfigurationCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 21 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void rendersConfigurationStateWithoutExternalRegistration()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `CoverageTest.ZatcaConfigurationCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.
