# اختبارات ودعم الدخول والبيانات الأساسية

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-d2bcce799a1e"></a>
## BaseTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BaseTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BaseTest.java)

**دوره:** تجهيز المتصفح والدخول قبل كلاس الاختبارات، وحماية الجلسة، ثم إغلاق المتصفح المملوك.

**حالته:** مصدر نشط · **عدد أسطره:** 83

**يرتبط مباشرة بملفات داخل المشروع:** [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [AuditLogCoverageTest.java](TEST_CoverageTest_AR.md#file-67388f9fb2ba)، [SelectedFixtureRecoveryTest.java](TEST_CoverageTest_AR.md#file-a946dc1f3ddf)، [ZatcaConfigurationCoverageTest.java](TEST_CoverageTest_AR.md#file-eae457cc2254)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)، [DealerTestDataTest.java](TEST_DealerModelTest_AR.md#file-50da8325170d)، [FinancialFixtureTest.java](TEST_FinancialMovementsTest_AR.md#file-aa2e51cd24e5)، [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)، [TreasuryLedgerCoverageTest.java](TEST_FinancialMovementsTest_AR.md#file-aaf8d34190e8)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected final void requirePreparedSession()` | protected | منع متابعة السيناريو إذا فشل التجهيز أو لا توجد جلسة جاهزة. |
| `public void setup() throws Exception` | public | تجهيز المتصفح والدخول قبل اختبارات الكلاس. |
| `protected WebDriver createDriver()` | protected | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void login()` | protected | تنفيذ جزء متعلق بـ تسجيل الدخول، حسب المدخلات والتخصص. |
| `public void tearDown()` | public | إغلاق المتصفح المملوك وتنظيف مراجع الجلسة. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BaseTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-bc1b1188af8f"></a>
## AreaTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/AreaTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/AreaTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 490 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AreaPage.java](MAIN_PAGES_BasicData_AR.md#file-3f7bf8c58091)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String uniqueName(String caseId)` | private | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `private AreaPage openAreas()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void createArea(AreaPage page, String arabicName, String englishName, String notes)` | private | إنشاء المنطقة، حسب المدخلات والتخصص. |
| `private void seedAreas(String key, int count)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AR_001_AreaListStructureIsDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_002_AreaCreationFormControlsAreDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة / النموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_014_CloseCreationFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_020_EditFormLoadsCurrentAreaValues()` | public · @Test | حالة تحقق مرتبطة بـالنموذج / المنطقة / القيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_022_CancelAreaEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / المنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_004_ArabicNameIsRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_005_WhitespaceNameIsRejected()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_013_CorrectArabicNameAfterValidationError()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم / التحقق / الخطأ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_003_CreateValidAreaAndVerifyFields()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_007_OptionalFieldsCanBeEmpty()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_015_SearchAreasByCodeAndName()` | public | تنفيذ جزء متعلق بـ الكود / الاسم، حسب المدخلات والتخصص. |
| `public void AR_017_SearchUsesOnlyDisplayedAreaFields()` | public | تنفيذ جزء متعلق بـ المنطقة، حسب المدخلات والتخصص. |
| `public void AR_028_AreaListCanBeSortedByCode()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة / الكود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_029_ChangingPageSizeUpdatesVisibleRows()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / الحجم / الظاهر / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_030_NavigateAreaPagesWhenAvailable()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_012_RapidDoubleSaveCreatesSingleArea()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_021_EditOwnedAreaAndSave()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_023_DeleteOnlyOwnedArea()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_024_CancelDeleteOfOwnedArea()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / المملوك للاختبار / المنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_032_AreaPersistsAfterBrowserRefresh()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AR_035_ReopeningAreaListDoesNotDuplicateRecord()` | public · @Test | حالة تحقق مرتبطة بـالمنطقة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.AreaTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-44bcd35d3f70"></a>
## BankTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/BankTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/BankTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 303 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [BankPage.java](MAIN_PAGES_BasicData_AR.md#file-fe24b8cdd695)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String uniqueName(String suffix)` | private | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `private BankPage openBanks()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void fillMinimum(BankPage page, String name)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void createBank(BankPage page, String name)` | private | إنشاء البنك، حسب المدخلات والتخصص. |
| `public void BK_001_BankListStructureIsDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_002_BankFormControlsAreDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالبنك / النموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_014_CloseFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_020_EditFormLoadsValues()` | public · @Test | حالة تحقق مرتبطة بـالنموذج / القيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_022_CancelEditDoesNotChangeBank()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_004_ArabicNameIsRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_005_WhitespaceNameIsRejected()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_008_InvalidEmailIsRejected()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_003_CreateBankAndVerifySavedFields()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_007_OptionalFieldsCanBeEmpty()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_021_EditOwnedBank()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_023_DeleteOwnedBank()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_024_CancelDeleteRetainsBank()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void BK_032_BankPersistsAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.BankTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e758c254da3e"></a>
## CashTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/CashTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/CashTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 997 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [CashPage.java](MAIN_PAGES_BasicData_AR.md#file-ed12e0c67c34)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void WH_001_SafeListStructureIsDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_002_CashCreationFormControlsAreDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_014_CloseCreationFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_020_EditFormLoadsCurrentSafeName()` | public · @Test | حالة تحقق مرتبطة بـالنموذج / الخزنة / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_022_CancelSafeEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / الخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ValidCashTest()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void EditCash()` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void DeleteCash()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvalidCashTest()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void EmptyCashFormIsRejected()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CashArabicNameWhitespaceIsRejected()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CorrectArabicNameAfterValidationError()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم / التحقق / الخطأ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void OptionalCashFieldsCanBeEmpty()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void OpenCashCreationForm()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CashListDisplaysRecords()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SearchSafesByCodeAndName()` | public · @Test | حالة تحقق مرتبطة بـالكود / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_017_SearchSafesByEnglishName()` | public · @Test | حالة تحقق مرتبطة بـالإنجليزي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_028_SafeListCanBeSortedByCode()` | public · @Test | حالة تحقق مرتبطة بـالخزنة / الكود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_029_ChangingPageSizeUpdatesVisibleRows()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / الحجم / الظاهر / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_030_NavigateSafeListPagesWhenAvailable()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_003_CreateValidSafeAndVerifyFields()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_007_SaveSafeWithoutAccount()` | public · @Test | حالة تحقق مرتبطة بـالخزنة / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_008_SaveSafeWithGeneralUnchecked()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_009_SaveSafeWithGeneralChecked()` | public · @Test | حالة تحقق مرتبطة بـالخزنة / حالة التحديد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_010_SaveSafeWithEmptyNotes()` | public · @Test | حالة تحقق مرتبطة بـالخزنة / الملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_012_RapidDoubleSaveCreatesSingleSafe()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_021_EditOwnedTestSafeAndSave()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_023_DeleteOnlyOwnedTestSafe()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_024_CancelDeleteOfOwnedTestSafe()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / المملوك للاختبار / الخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_031_NewSafeAppearsAfterListRefresh()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_032_SafePersistsAfterBrowserRefresh()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void WH_035_ReopeningListDoesNotDuplicateSafe()` | public · @Test | حالة تحقق مرتبطة بـالخزنة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportSafesToPdf() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportSafesToExcel() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void createSafe( CashPage cashPage, String arabicName, String englishName, String accountName, boolean general, String notes)` | private | إنشاء الخزنة، حسب المدخلات والتخصص. |
| `private void verifyOptionalSafeScenario(String accountName, boolean general, String tcId)` | private | التحقق من الخزنة، حسب المدخلات والتخصص. |
| `private void verifySafePersistsAcrossRefresh(String tcId)` | private | التحقق من الخزنة، حسب المدخلات والتخصص. |
| `private void cleanupTestSafe(CashPage cashPage, String safeName)` | private | تنظيف الخزنة، حسب المدخلات والتخصص. |
| `private String seedSafe(String key)` | private | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `private Path prepareExportDirectory() throws IOException` | private | تجهيز التصدير، حسب المدخلات والتخصص. |
| `private Set<String> listFileNames(Path directory) throws IOException` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private Path waitForNewExport( Path directory, Set<String> existingFiles, Set<String> extensions)` | private | انتظار التصدير، حسب المدخلات والتخصص. |
| `private boolean isNonEmptyFile(Path file)` | private | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean matchesExportType(Path file, Set<String> extensions)` | private | مطابقة التصدير، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.CashTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-2d1a897250de"></a>
## DisEntTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/DisEntTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/DisEntTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 236 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [DistributionEntityPage.java](MAIN_PAGES_BasicData_AR.md#file-b20ba4a5652e)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String uniqueName(String suffix)` | private | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `private DistributionEntityPage openPage()` | private | فتح الصفحة، حسب المدخلات والتخصص. |
| `private void create(DistributionEntityPage page, String name, String english)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void DE_001_ListStructureIsDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_002_FormControlsAreDisplayed()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_014_CloseWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_004_ArabicNameIsRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_005_WhitespaceNameIsRejected()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_003_CreateAndVerifyEntity()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_007_AccountCanBeOmitted()` | public · @Test | حالة تحقق مرتبطة بـالحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_021_EditOwnedEntity()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_022_CancelEditRetainsOriginal()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_023_DeleteOwnedEntity()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_024_CancelDeleteRetainsEntity()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DE_032_PersistsAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.DisEntTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-111123857b49"></a>
## ExpensesTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/ExpensesTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/ExpensesTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 264 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [ExpensesPage.java](MAIN_PAGES_BasicData_AR.md#file-0b55d5c7cbdf)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String unique(String suffix)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private ExpensesPage open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void create( ExpensesPage p, String name, String english, String account, boolean shared)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void cleanup(ExpensesPage p, String... names)` | private | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void EX_001_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_002_FormControls()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_014_CloseWithoutSave()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_004_ArabicNameRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_005_WhitespaceRejected()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_003_CreateAndVerify()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_007_OptionalFields()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_021_EditOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_022_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_023_DeleteOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_024_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EX_032_PersistAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.ExpensesTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-f4f7f3a5da0c"></a>
## PaymentTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/PaymentTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/PaymentTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 262 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [PaymentPage.java](MAIN_PAGES_BasicData_AR.md#file-89a4abcc4db1)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String unique(String suffix)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private PaymentPage open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void create( PaymentPage p, String name, String english, String bank, boolean general, boolean integration)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void PAY_001_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_002_FormControls()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_014_CloseWithoutSave()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_004_ArabicNameRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_005_BankRequired()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_006_WhitespaceRejected()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_003_CreateAndVerify()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_007_OptionalEnglishName()` | public · @Test | حالة تحقق مرتبطة بـالإنجليزي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_021_EditOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_022_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_023_DeleteOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_024_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PAY_032_PersistAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.PaymentTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-78fe3003f784"></a>
## RevenuesTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/RevenuesTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/RevenuesTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 263 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [RevenuesPage.java](MAIN_PAGES_BasicData_AR.md#file-dd431550a62a)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String unique(String s)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private RevenuesPage open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void create(RevenuesPage p, String n, String en, String account, boolean general)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void cleanup(RevenuesPage p, String... names)` | private | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void REV_001_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_002_FormControls()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_014_CloseWithoutSave()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_004_ArabicNameRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_005_WhitespaceRejected()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_003_CreateAndVerify()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_007_OptionalFields()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_021_EditOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_022_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_023_DeleteOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_024_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void REV_032_PersistAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.RevenuesTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9f2a53e742aa"></a>
## RoutLineTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/RoutLineTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/RoutLineTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 213 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [RoutLine.java](MAIN_PAGES_BasicData_AR.md#file-fd8f8913ef28)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String unique(String s)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private RoutLine open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void create(RoutLine p, String n, String en, String type, boolean location)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void RL_001_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_002_FormControls()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_014_CloseWithoutSave()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_004_ArabicNameRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_003_CreateAndVerify()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_007_CollectionTypeAndLocationOff()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_021_EditOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_022_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_023_DeleteOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_024_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void RL_032_PersistAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.RoutLineTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-7c0fbb3a6a55"></a>
## StoreTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/StoreTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/StoreTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 198 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [StorePage.java](MAIN_PAGES_BasicData_AR.md#file-ccf2392eb482)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String unique(String suffix)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private StorePage open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void create(StorePage p, String name, String english, String account)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void cleanup(StorePage p, String... names)` | private | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void STORE_001_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_002_FormControls()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_003_CloseWithoutSave()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_004_ArabicNameRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_005_CreateAndVerify()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_006_EditOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_007_DeleteOwned()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_008_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void STORE_009_PersistAfterRefresh()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.StoreTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-f61729975a8f"></a>
## TaypeOfDiscountTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/TaypeOfDiscountTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/TaypeOfDiscountTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 166 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [TypesofdiscountsPage.java](MAIN_PAGES_BasicData_AR.md#file-df58c29cce49)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void ValidTaypeOfDiscountTest()` | public | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `public void EditTypeOfDiscount()` | public | تعديل الخصم، حسب المدخلات والتخصص. |
| `public void DeleteTypeOfDiscount()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertArabicNameRequired()` | public | مقارنة متوقعة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void AssertSecretCodeRequired()` | public | مقارنة متوقعة الكود، حسب المدخلات والتخصص. |
| `public void AssertPercentageRequired()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String seedDiscount(String key)` | private | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.TaypeOfDiscountTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-99e7d8e37af9"></a>
## TreasuryHandoverTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/TreasuryHandoverTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/BasicDataTest/TreasuryHandoverTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 117 · **الأب المباشر:** `BasicDataScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [TreasuryHandoverPage.java](MAIN_PAGES_BasicData_AR.md#file-ca1ef3ba6311)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [BasicDataFixtures.java](TEST_TestSupport_AR.md#file-e25770849138)، [BasicDataScenarioTest.java](TEST_TestSupport_AR.md#file-cf6cb5547507)، [TreasuryFixtures.java](TEST_TestSupport_AR.md#file-f5bdb9a9ad66)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private TreasuryFixtures treasury()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void cleanTreasuryHandovers()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void submit(TreasuryHandoverPage page, BasicDataFixtures.Record safe, BasicDataFixtures.Record user)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ValidTreasuryHandoverTest()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void InvalidTreasuryHandoverTest()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenTreasuryHandover()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.BasicDataTest.TreasuryHandoverTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-3a9647b96039"></a>
## LoginTest.java

**المصدر:** [src/test/java/Plugin_ERP/Tests/LoginTest/LoginTest.java](../../../src/test/java/Plugin_ERP/Tests/LoginTest/LoginTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 73

**يرتبط مباشرة بملفات داخل المشروع:** [Browser.java](MAIN_Drivers_AR.md#file-6a481899dc7e)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [ScreenShotsUtils.java](MAIN_Utiel_AR.md#file-db7a1ec24b8d)

**ملفات تشير إليه مباشرة:** [CiSuiteContractTest.java](TEST_SuiteSupport_AR.md#file-17f5ebee2127)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void openLogin()` | public | فتح تسجيل الدخول، حسب المدخلات والتخصص. |
| `public void closeLogin(ITestResult result)` | public | إغلاق تسجيل الدخول، حسب المدخلات والتخصص. |
| `public void LoginTest()` | public · @Test | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void InvalidLoginTest()` | public · @Test | حالة تحقق مرتبطة بـتسجيل الدخول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Tests.LoginTest.LoginTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.
