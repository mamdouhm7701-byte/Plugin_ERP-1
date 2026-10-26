# اختبارات ودعم المتعاملين

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-fbd3e2f60ed9"></a>
## AgentTest.java

**المصدر:** [src/test/java/DealerModelTest/AgentTest.java](../../../src/test/java/DealerModelTest/AgentTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 402 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Agent.java](MAIN_PAGES_DealerModel_AR.md#file-e245cb653316)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void AG_001_AgentListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AG_002_CloseAgentFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AG_003_CancelAgentEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AG_004_CancelAgentDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AG_005_OwnedAgentCreateEditDelete()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddAgentFDAtaFirstPAge()` | public | إضافة الأول، حسب المدخلات والتخصص. |
| `public void AddAgentFDAtaSecondPAgeForAgentCommission()` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAgentThirdPageProductCommission()` | public | إضافة الصفحة، حسب المدخلات والتخصص. |
| `public void AddAgentByAllProducts()` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAgentBySearchProduct()` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAgentWithChoseItem()` | public | إضافة الصنف، حسب المدخلات والتخصص. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void AseertArabicNameRequired()` | public | تنفيذ جزء متعلق بـ العربي / الاسم، حسب المدخلات والتخصص. |
| `public void AseertAgentNaturalRequired()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AseertErrorMessageWhenAddTheSamePrecentge()` | public | تنفيذ جزء متعلق بـ الخطأ / الرسالة، حسب المدخلات والتخصص. |
| `public void ExportAgentsToPdf() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportAgentsToExcel() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.AgentTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-f5e0860be20c"></a>
## CustomerClassificationTest.java

**المصدر:** [src/test/java/DealerModelTest/CustomerClassificationTest.java](../../../src/test/java/DealerModelTest/CustomerClassificationTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 277 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [CustomerClassification.java](MAIN_PAGES_DealerModel_AR.md#file-c60feadee3d9)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void CC_001_ClassificationListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CC_002_CloseClassificationFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CC_003_CancelClassificationEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CC_004_CancelClassificationDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CC_005_OwnedClassificationCreateEditDelete()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddCustomerClassification()` | public | إضافة العميل، حسب المدخلات والتخصص. |
| `public void AddSupplierClassification()` | public | إضافة المورد، حسب المدخلات والتخصص. |
| `public void AddAgentClassification()` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void Edit()` | public | فتح تعديل وفق عقد الملف؛ بعض المسارات القديمة تختار آخر سجل. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void AssertMessageArabicName()` | public | مقارنة متوقعة الرسالة / العربي / الاسم، حسب المدخلات والتخصص. |
| `public void AssertMessageClassification()` | public | مقارنة متوقعة الرسالة، حسب المدخلات والتخصص. |
| `public void ExportClassificationsToPdf() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportClassificationsToExcel() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.CustomerClassificationTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-5a3d847bf4ab"></a>
## CustomersTest.java

**المصدر:** [src/test/java/DealerModelTest/CustomersTest.java](../../../src/test/java/DealerModelTest/CustomersTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 629 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Customers.java](MAIN_PAGES_DealerModel_AR.md#file-18f667c71688)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void CU_001_CustomerListStructure()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CU_002_CloseCustomerFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالعميل / النموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CU_003_CancelCustomerEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / العميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CU_004_CancelCustomerDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / العميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void CU_005_OwnedCustomerCreateEditDelete()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / العميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddCustomerFirstPage()` | public | إضافة العميل / الأول / الصفحة، حسب المدخلات والتخصص. |
| `public void AddCustomerSecondPage()` | public | إضافة العميل / الصفحة، حسب المدخلات والتخصص. |
| `public void AddCustomerThirdPage()` | public | إضافة العميل / الصفحة، حسب المدخلات والتخصص. |
| `public void AddCustomerFourthPage()` | public | إضافة العميل / الصفحة، حسب المدخلات والتخصص. |
| `public void Edit()` | public | فتح تعديل وفق عقد الملف؛ بعض المسارات القديمة تختار آخر سجل. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void SaveWithOutArabicName()` | public | حفظ العربي / الاسم، حسب المدخلات والتخصص. |
| `public void EnterWrongEmail()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void EnterWrongBankNumber()` | public | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `public void AssertErrorMessagesInDataTax()` | public | مقارنة متوقعة الخطأ / الضريبة، حسب المدخلات والتخصص. |
| `public void AssertTaxNumberStartNumber3andEndStareNumber3()` | public | مقارنة متوقعة الضريبة، حسب المدخلات والتخصص. |
| `public void AssertBuildingNumber()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AssertPostNumberInclode5number()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AssertAdress()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AsserShowErrorPopUpPhoneNumberDubelicateAndClickYes()` | public | تنفيذ جزء متعلق بـ الخطأ / النقر، حسب المدخلات والتخصص. |
| `public void AsserShowErrorPopUpPhoneNumberDubelicateAndClickCansel()` | public | تنفيذ جزء متعلق بـ الخطأ / النقر، حسب المدخلات والتخصص. |
| `public void ExportCustomersToPdf() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportCustomersToExcel() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.CustomersTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-10845a4e0547"></a>
## DealerCleanupTest.java

**المصدر:** [src/test/java/DealerModelTest/DealerCleanupTest.java](../../../src/test/java/DealerModelTest/DealerCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 248

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void retriesUncommittedDeleteBeforeParentAccounts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsEmptyStringReadAfterCommittedDeleteTimeout()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageScopeSharesOnlyStableReferences()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void resumesUncertainCreateBeforeDeletingItsDependencies() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesRecoveryInAnotherBranch() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleanupFindsOwnedDealerBeyondFirstListPage()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الأول / الصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ambiguousDealerNameStopsCleanup()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.DealerCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e4a4886a56fa"></a>
## DealerFixtureApi.java

**المصدر:** [src/test/java/DealerModelTest/DealerFixtureApi.java](../../../src/test/java/DealerModelTest/DealerFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 58 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DealerFixtureApi(WebDriver driver, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-e9e746f3495d"></a>
## DealerFixtureApiStub.java

**المصدر:** [src/test/java/DealerModelTest/DealerFixtureApiStub.java](../../../src/test/java/DealerModelTest/DealerFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 131

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-725ccc16ea05"></a>
## DealerFixtureDefinitions.java

**المصدر:** [src/test/java/DealerModelTest/DealerFixtureDefinitions.java](../../../src/test/java/DealerModelTest/DealerFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 83

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DealerFixtureDefinitions(DealerTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record area()` | داخل الحزمة | تنفيذ جزء متعلق بـ المنطقة، حسب المدخلات والتخصص. |
| `Record classification(String type)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainDealer(String type)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record agent(String nature)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account()` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record store()` | داخل الحزمة | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Record unit()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |

<a id="file-3aff85c1f7d0"></a>
## DealerFixtureRecovery.java

**المصدر:** [src/test/java/DealerModelTest/DealerFixtureRecovery.java](../../../src/test/java/DealerModelTest/DealerFixtureRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 22 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-88474b322835"></a>
## DealerMarkerCleanupTest.java

**المصدر:** [src/test/java/DealerModelTest/DealerMarkerCleanupTest.java](../../../src/test/java/DealerModelTest/DealerMarkerCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 56

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void cleansOnlyExactOwnedCalendarEvents()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversUnexpectedBlankNameSaveByExactEnglishMarker()` | public · @Test | حالة تحقق مرتبطة بـالاسم / الإنجليزي / علامة الملكية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesAmbiguousBlankNameMarkersAndForeignSession()` | public · @Test | حالة تحقق مرتبطة بـالاسم / الجلسة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.DealerMarkerCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-7f9a810ec51c"></a>
## DealerModelExportAssertions.java

**المصدر:** [src/test/java/DealerModelTest/DealerModelExportAssertions.java](../../../src/test/java/DealerModelTest/DealerModelExportAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 104

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private DealerModelExportAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format)` | داخل الحزمة | مقارنة متوقعة التصدير، حسب المدخلات والتخصص. |
| `private static void installDownloadCapture(WebDriver driver)` | private | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-28cb3ce75388"></a>
## DealerModelScreenAssertions.java

**المصدر:** [src/test/java/DealerModelTest/DealerModelScreenAssertions.java](../../../src/test/java/DealerModelTest/DealerModelScreenAssertions.java)

**دوره:** مقارنات مشتركة تتكرر في سيناريوهات المجال مع رسائل فشل مفهومة.

**حالته:** مصدر نشط · **عدد أسطره:** 115

**يرتبط مباشرة بملفات داخل المشروع:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private DealerModelScreenAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertListAndActionsDisplayed(AbstractForDealer page)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static void assertNewFormCanBeClosedWithoutSaving(AbstractForDealer page)` | داخل الحزمة | مقارنة متوقعة النموذج، حسب المدخلات والتخصص. |
| `static void assertEditCanBeCancelled(AbstractForDealer page)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static void assertEditCanBeCancelled(AbstractForDealer page, String originalName)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static void assertDeleteCanBeCancelled(AbstractForDealer page)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static void assertDeleteCanBeCancelled(AbstractForDealer page, String originalName)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static void assertOwnedCreateEditDelete( WebDriver driver, AbstractForDealer page, String originalName, String editedName, Consumer<AbstractForDealer> fillRequiredFields, Runnable openList)` | داخل الحزمة | مقارنة متوقعة المملوك للاختبار، حسب المدخلات والتخصص. |
| `private static void waitForExactCount( WebDriver driver, AbstractForDealer page, String name, int count)` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-cdcf870a4b9b"></a>
## DealerScenarioTest.java

**المصدر:** [src/test/java/DealerModelTest/DealerScenarioTest.java](../../../src/test/java/DealerModelTest/DealerScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 309 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)، [Agent.java](MAIN_PAGES_DealerModel_AR.md#file-e245cb653316)، [Customers.java](MAIN_PAGES_DealerModel_AR.md#file-18f667c71688)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [AgentTest.java](TEST_DealerModelTest_AR.md#file-fbd3e2f60ed9)، [CustomerClassificationTest.java](TEST_DealerModelTest_AR.md#file-f5e0860be20c)، [CustomersTest.java](TEST_DealerModelTest_AR.md#file-5a3d847bf4ab)، [DealerFixtureRecovery.java](TEST_DealerModelTest_AR.md#file-3aff85c1f7d0)، [MainDealerTest.java](TEST_DealerModelTest_AR.md#file-4c029045cc6a)، [RoutLineTest.java](TEST_DealerModelTest_AR.md#file-a6a59694ce1a)، [SupplierTest.java](TEST_DealerModelTest_AR.md#file-110ff2e4a9fe)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private record OwnedRecord(AbstractForDealer page, Pages route, String name)` | private | تنفيذ جزء متعلق بـ المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void beginDealerCase()` | public | بدء نطاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void journalOwnedRecords()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع / المملوك للاختبار، حسب المدخلات والتخصص. |
| `void resumeOwnedRecords(String suffix) throws java.io.IOException` | داخل الحزمة | تنفيذ جزء متعلق بـ المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected DealerTestData data()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void prepareScenarioReferences(String scenario)` | protected | تجهيز القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertOwnedEditCancelled(AbstractForDealer page, Pages route)` | protected | مقارنة متوقعة المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected void assertOwnedDeleteCancelled(AbstractForDealer page, Pages route)` | protected | مقارنة متوقعة المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected String ownedName(AbstractForDealer page, Pages route, String label)` | protected | تنفيذ جزء متعلق بـ المملوك للاختبار / الاسم، حسب المدخلات والتخصص. |
| `protected String seedRecord(AbstractForDealer page, Pages route, String label)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void waitForRecord(AbstractForDealer page, String name)` | protected | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String uniquePhone()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String uniqueEmail()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected String uniqueTaxNumber()` | protected | تنفيذ جزء متعلق بـ الضريبة، حسب المدخلات والتخصص. |
| `protected String seedCustomerPhone(Customers page)` | protected | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `protected void deleteOwnedRecords(AbstractForDealer page, Pages route, int count)` | protected | حذف المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void cleanScenarioRecords()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deletePageRecords()` | private | حذف الصفحة، حسب المدخلات والتخصص. |
| `public void retryRemainingScenarioCleanup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.DealerScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-4208454ad313"></a>
## DealerSuitePolicy.java

**المصدر:** [src/test/java/DealerModelTest/DealerSuitePolicy.java](../../../src/test/java/DealerModelTest/DealerSuitePolicy.java)

**دوره:** قواعد مستوى/اختيار حالات المجال أو ضبطها؛ ليست صفحة أو وصف بيانات ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 20

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void alter(List<XmlSuite> suites)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-5515a08d4d8e"></a>
## DealerTestData.java

**المصدر:** [src/test/java/DealerModelTest/DealerTestData.java](../../../src/test/java/DealerModelTest/DealerTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 261

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `DealerTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private DealerTestData(WebDriver driver,String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `static Map<String, Object> fields(Object... pairs)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record area()` | داخل الحزمة | تنفيذ جزء متعلق بـ المنطقة، حسب المدخلات والتخصص. |
| `Record classification(String type)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainDealer(String type)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record agent(String nature)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account()` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record store()` | داخل الحزمة | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Record unit()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void recoverPendingFixtures()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String journalSuffix()` | داخل الحزمة | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `else if(entity instanceof List<?> list)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `else if(entity instanceof Map<?,?> result&&result.get("pageData") instanceof List<?> list&&result.get("totalRecords") instanceof Number count)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean ownsDealer(Map<?,?> row, String name)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void deleteOwnedDealer(Plugin_ERP.Enums.Pages route,String name)` | داخل الحزمة | حذف المملوك للاختبار، حسب المدخلات والتخصص. |
| `private void deleteExactDealer(String endpoint, Object id, String name)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-50da8325170d"></a>
## DealerTestDataTest.java

**المصدر:** [src/test/java/DealerModelTest/DealerTestDataTest.java](../../../src/test/java/DealerModelTest/DealerTestDataTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 26 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void createLinkedFixturesInCurrentTenant()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.DealerTestDataTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-4c029045cc6a"></a>
## MainDealerTest.java

**المصدر:** [src/test/java/DealerModelTest/MainDealerTest.java](../../../src/test/java/DealerModelTest/MainDealerTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 221 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [MainDealer.java](MAIN_PAGES_DealerModel_AR.md#file-f6e2daf7a1c7)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private String unique(String suffix)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private MainDealer open()` | private | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void create(MainDealer page, String name, String kind)` | private | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void MD_001_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_002_FormControls()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_009_CloseCreationFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_010_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_011_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_003_ArabicNameRequired()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_004_KindRequired()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_005_CreateCustomer()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_006_CreateSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_007_EditOwnedRecord()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_008_DeleteOwnedRecord()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_090_ExportPdf() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void MD_091_ExportExcel() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.MainDealerTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-a6a59694ce1a"></a>
## RoutLineTest.java

**المصدر:** [src/test/java/DealerModelTest/RoutLineTest.java](../../../src/test/java/DealerModelTest/RoutLineTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 47 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [RoutLineForDealer.java](MAIN_PAGES_DealerModel_AR.md#file-fb3d8ac2b0d9)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void AddRoutLine()` | public · @Test | حالة تحقق مرتبطة بـالبند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.RoutLineTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-110ff2e4a9fe"></a>
## SupplierTest.java

**المصدر:** [src/test/java/DealerModelTest/SupplierTest.java](../../../src/test/java/DealerModelTest/SupplierTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 335 · **الأب المباشر:** `DealerScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Supplier.java](MAIN_PAGES_DealerModel_AR.md#file-c1045e9f255a)، [DealerScenarioTest.java](TEST_DealerModelTest_AR.md#file-cdcf870a4b9b)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void SU_001_SupplierListStructure()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SU_002_CloseSupplierFormWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالمورد / النموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SU_003_CancelSupplierEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SU_004_CancelSupplierDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void SU_005_OwnedSupplierCreateEditDelete()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddSupplierFirstPage()` | public | إضافة المورد / الأول / الصفحة، حسب المدخلات والتخصص. |
| `public void AddSupplierSecondPage()` | public | إضافة المورد / الصفحة، حسب المدخلات والتخصص. |
| `public void AddSupplierSupplierControlTab()` | public | إضافة المورد، حسب المدخلات والتخصص. |
| `public void Edit()` | public | فتح تعديل وفق عقد الملف؛ بعض المسارات القديمة تختار آخر سجل. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void AddSupplierWithOutArabicName()` | public | إضافة المورد / العربي / الاسم، حسب المدخلات والتخصص. |
| `public void ExportSuppliersToPdf() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ExportSuppliersToExcel() throws IOException` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `DealerModelTest.SupplierTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.
