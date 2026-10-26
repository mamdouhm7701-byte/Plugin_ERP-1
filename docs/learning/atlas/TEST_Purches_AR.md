# اختبارات ودعم المشتريات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-c21142c31503"></a>
## PurchaseCalculationTest.java

**المصدر:** [src/test/java/Purches/PurchaseCalculationTest.java](../../../src/test/java/Purches/PurchaseCalculationTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 261

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static List<InvoiceRow> row(String quantity, String price)` | private | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `private static void check( List<InvoiceRow> rows, String discount, DiscountType discountType, String rate, TaxMode mode, int precision, String total, String discountValue, String beforeTax, String tax, String net)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void untaxedInvoice()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void inclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void percentageDiscountBeforeExclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountWithInclusiveVat()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountWithoutVat()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullPercentageDiscount()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullFixedDiscount()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalQuantityAndRounding()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void aggregateTaxRounding()` | public · @Test | حالة تحقق مرتبطة بـالضريبة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void threeDecimalInclusiveRounding()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void emptyCalculationIsZero()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseCalculationTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9c48f6a19711"></a>
## PurchaseCleanupTest.java

**المصدر:** [src/test/java/Purches/PurchaseCleanupTest.java](../../../src/test/java/Purches/PurchaseCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 1140

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void normalCustomerIsTaxableAndKeepsItsOwnAccount()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void actualPageLifecycleDefersCleanupUntilGroupEndEvenAfterFailure()` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void puCloseAuxiliaryWindows(String main)` | public | إغلاق الإضافي، حسب المدخلات والتخصص. |
| `public void puSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String puCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public String puDiagnosticMarkup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String puDiagnostics()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void interruptedCleanupJournalsOnlyRemainingOwnedRecords() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversSparsePurchaseListThroughVerifiedDetails()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void receiptLookupUsesDetailsAndPreservesUnrelatedReceipts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retriesRolledBackDeadlockWithoutDuplicateFixture()` | public · @Test | حالة تحقق مرتبطة بـبيانات الاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void boundsRolledBackDeadlockRetries()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الخطأ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts()` | public · @Test | حالة تحقق مرتبطة بـالحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void waitsForOriginalBranchHydrationBeforeFixtureAccess()` | public · @Test | حالة تحقق مرتبطة بـبيانات الاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageFixturesAreSharedButCaseBalancesStayIsolated()` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageTransitionRetainsAndRecoversThePreviousFailedSave()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / السابق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void negativeSaveDetectsACommitBehindFailedResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversSecondReturnWithoutConfusingKnownFirstReturn()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] lostSalesKinds()` | public | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `public void recoversLostResponseForEachSalesDocumentKind( String endpointType, String storedType)` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesJournalRecoveryInAnotherTenant() throws Exception` | public · @Test | حالة تحقق مرتبطة بـسجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesJournalRecoveryInAnotherBranch() throws Exception` | public · @Test | حالة تحقق مرتبطة بـسجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void rejectForeignContext(boolean tenant) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void linkedReturnIsDeletedBeforeSourceInvoice()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void paidInvoiceIsDeletedBeforePaymentMethodAndBank()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / السداد / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletesOwnedMovementBeforeItsItemsAndStores()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversStoreCommittedBeforeLostCreateResponse()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainStoreLookupFailurePreservesItsAccounts()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void uncertainStoreMustStillReferenceItsOwnedAccount()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / المملوك للاختبار / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void resumesPendingStoreCreationFromDurableJournal() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالمخزن / سجل الاسترجاع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsStockDependenciesWhenMovementDeletionFails()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesTrackingUnownedRecord()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void checksIdentityBeforeFirstDeletion()` | public · @Test | حالة تحقق مرتبطة بـالأول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند / الملاحظات / المملوك للاختبار / المخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsSalesDocumentWithChangedStoreIdentity()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند / المخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsSalesDocumentWithChangedItemIdentity()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / المستند / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void salesDocumentIdentityMismatch(boolean storeChanged)` | private | تنفيذ جزء متعلق بـ المبيعات / المستند، حسب المدخلات والتخصص. |
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

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9d901e01d14d"></a>
## PurchaseDiscoveryTest.java

**المصدر:** [src/test/java/Purches/PurchaseDiscoveryTest.java](../../../src/test/java/Purches/PurchaseDiscoveryTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 54 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void inspectPurchaseScreens() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالمشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseDiscoveryTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-070163eff9dd"></a>
## PurchaseDocumentKind.java

**المصدر:** [src/test/java/Purches/PurchaseDocumentKind.java](../../../src/test/java/Purches/PurchaseDocumentKind.java)

**دوره:** تعريف أنواع مستندات/حركات المجال وربطها بمساراتها أو خصائص سلوكها.

**حالته:** مصدر نشط · **عدد أسطره:** 14

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseDocumentKind(String route, String endpoint, String type)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-23429daeaf06"></a>
## PurchaseEndToEndTest.java

**المصدر:** [src/test/java/Purches/PurchaseEndToEndTest.java](../../../src/test/java/Purches/PurchaseEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 54 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private PurchaseTestData.Record convert(PurchaseTestData.Record source, boolean toInvoice)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void orderThenInvoiceThenReturnRestoresSupplierAndStock()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / المرتجع / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void editedInvoiceThenReturnUsesUpdatedQuantityAndValue()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة / المرتجع / الكمية / القيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalPurchaseThenLinkedReturnRestoresBalances()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات / المرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-7a05624ed050"></a>
## PurchaseExportAssertions.java

**المصدر:** [src/test/java/Purches/PurchaseExportAssertions.java](../../../src/test/java/Purches/PurchaseExportAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 104

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private PurchaseExportAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format)` | داخل الحزمة | مقارنة متوقعة التصدير، حسب المدخلات والتخصص. |
| `private static void installDownloadCapture(WebDriver driver)` | private | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-5ecf928ce076"></a>
## PurchaseFixtureApi.java

**المصدر:** [src/test/java/Purches/PurchaseFixtureApi.java](../../../src/test/java/Purches/PurchaseFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 63 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseFixtureApi(WebDriver driver, String origin, Object sessionBranchId, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-fe6382ff729f"></a>
## PurchaseFixtureApiStub.java

**المصدر:** [src/test/java/Purches/PurchaseFixtureApiStub.java](../../../src/test/java/Purches/PurchaseFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 223

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c981beef8152"></a>
## PurchaseFixtureDefinitions.java

**المصدر:** [src/test/java/Purches/PurchaseFixtureDefinitions.java](../../../src/test/java/Purches/PurchaseFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 80

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseFixtureDefinitions(PurchaseTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String,Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record agent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record otherSalesAgent()` | داخل الحزمة | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record destination()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record supplier()` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record customer(int allowedDiscount)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record bank()` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record safe()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record discountType()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |

<a id="file-d14954baa517"></a>
## PurchaseFixtureRecovery.java

**المصدر:** [src/test/java/Purches/PurchaseFixtureRecovery.java](../../../src/test/java/Purches/PurchaseFixtureRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 19 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-12fceea3105e"></a>
## PurchaseFormActions.java

**المصدر:** [src/test/java/Purches/PurchaseFormActions.java](../../../src/test/java/Purches/PurchaseFormActions.java)

**دوره:** خطوات وتحقيقات مشتركة تعرضها اختبارات المجال النهائية دون نسخ نفس الجسم لكل نوع.

**حالته:** مصدر نشط · **عدد أسطره:** 339 · **الأب المباشر:** `PurchaseScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseScenarioTest.java](TEST_Purches_AR.md#file-68923271f3e1)

**ملفات تشير إليه مباشرة:** [PurchaseEndToEndTest.java](TEST_Purches_AR.md#file-23429daeaf06)، [PurchaseFormTest.java](TEST_Purches_AR.md#file-8880a685e0a4)، [PurchaseLinkedCoverageTest.java](TEST_Purches_AR.md#file-e841ae23ba53)، [PurchesBillsTest.java](TEST_Purches_AR.md#file-69710b6371e0)، [purchOrderTest.java](TEST_Purches_AR.md#file-0b81882c7803)، [ReturnOrderTest.java](TEST_Purches_AR.md#file-4ba40e5702e1)، [SupplyChainEndToEndTest.java](TEST_Purches_AR.md#file-81dbab96b057)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected void picker()` | protected | تنفيذ جزء متعلق بـ نافذة الاختيار، حسب المدخلات والتخصص. |
| `protected void barcode()` | protected | تنفيذ جزء متعلق بـ الباركود، حسب المدخلات والتخصص. |
| `protected void name()` | protected | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `protected void cash()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void print()` | protected | تنفيذ جزء متعلق بـ الطباعة، حسب المدخلات والتخصص. |
| `protected void empty()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void invalidQuantity(String quantity)` | protected | تنفيذ جزء متعلق بـ الكمية، حسب المدخلات والتخصص. |
| `protected void invalidPrice()` | protected | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `protected void missing(String label)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void fractional()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void discount(boolean percent)` | protected | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `protected void notes()` | protected | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `protected void fresh()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void remove()` | protected | إزالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void multiple()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void reopen()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void edit()` | protected | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void delete()` | protected | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void linkedReturn()` | protected | تنفيذ جزء متعلق بـ المرتجع، حسب المدخلات والتخصص. |
| `protected void convertOrder()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertSource(PurchaseTestData.Record record, Object expected)` | protected | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static Map<String, Object> firstLine(Map<String, Object> doc)` | protected | تنفيذ جزء متعلق بـ الأول / البند، حسب المدخلات والتخصص. |

<a id="file-8880a685e0a4"></a>
## PurchaseFormTest.java

**المصدر:** [src/test/java/Purches/PurchaseFormTest.java](../../../src/test/java/Purches/PurchaseFormTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 193 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)

**ملفات تشير إليه مباشرة:** [PurchaseInvoiceCoverageTest.java](TEST_Purches_AR.md#file-c2d11685b37f)، [PurchaseOrderCoverageTest.java](TEST_Purches_AR.md#file-38f0c720db49)، [PurchaseReturnCoverageTest.java](TEST_Purches_AR.md#file-1bb38d129e85)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void savesOwnedItemFromPicker()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الصنف / نافذة الاختيار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesOwnedItemByBarcode()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الصنف / الباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesOwnedItemByName()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الصنف / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cashDocumentUsesOwnedPayment()` | public · @Test | حالة تحقق مرتبطة بـالمستند / المملوك للاختبار / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalQuantityPersists()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void twoOwnedItemsPersist()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removedItemIsNotSaved()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void unicodeNotesPersist()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void emptyDocumentCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالمستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void zeroQuantityCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void negativeQuantityCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void missingSupplierCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void missingStoreCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void negativePriceCannotSave()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void newClearsUnsavedLines()` | public · @Test | حالة تحقق مرتبطة بـالبنود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savedDocumentReopensWithOwnedValues()` | public · @Test | حالة تحقق مرتبطة بـالمستند / المملوك للاختبار / القيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savedDocumentCanBeEdited()` | public · @Test | حالة تحقق مرتبطة بـالمستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletesOnlyOwnedDocument()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesAndPrintsOwnedDocument()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / المستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseFormTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-c2d11685b37f"></a>
## PurchaseInvoiceCoverageTest.java

**المصدر:** [src/test/java/Purches/PurchaseInvoiceCoverageTest.java](../../../src/test/java/Purches/PurchaseInvoiceCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 32 · **الأب المباشر:** `PurchaseFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormTest.java](TEST_Purches_AR.md#file-8880a685e0a4)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void percentageDiscountRecalculates()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountRecalculates()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseInvoiceCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e841ae23ba53"></a>
## PurchaseLinkedCoverageTest.java

**المصدر:** [src/test/java/Purches/PurchaseLinkedCoverageTest.java](../../../src/test/java/Purches/PurchaseLinkedCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 31 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void linkedReturnKeepsOriginalQuantity()` | public | تنفيذ جزء متعلق بـ المرتجع / الكمية، حسب المدخلات والتخصص. |
| `public void ownedOrderConvertsToPurchase()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / المشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseLinkedCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-027f2cd42e18"></a>
## PurchaseListCoverageTest.java

**المصدر:** [src/test/java/Purches/PurchaseListCoverageTest.java](../../../src/test/java/Purches/PurchaseListCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 153 · **الأب المباشر:** `PurchaseScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseScenarioTest.java](TEST_Purches_AR.md#file-68923271f3e1)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void resetPurchaseListDate()` | public | تنفيذ جزء متعلق بـ المشتريات / التاريخ، حسب المدخلات والتخصص. |
| `private PurchaseTestData.Record seed(int index)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void scope(PurchaseTestData.Record... docs)` | private | تنفيذ جزء متعلق بـ النطاق، حسب المدخلات والتخصص. |
| `public void supplierFilterIsOwned()` | public · @Test | حالة تحقق مرتبطة بـالمورد / الفلتر / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exactCodeFilter()` | public · @Test | حالة تحقق مرتبطة بـالكود / الفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void referenceFilter()` | public · @Test | حالة تحقق مرتبطة بـالفلتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void unmatchedCodeClearsRows()` | public · @Test | حالة تحقق مرتبطة بـالكود / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void clearCodeRestoresSupplierScope()` | public · @Test | حالة تحقق مرتبطة بـالكود / المورد / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] sizes()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void pageSizePreservesResults(int size)` | public · @Test | حالة تحقق مرتبطة بـالصفحة / الحجم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void sortKeepsOwnedDocuments()` | public · @Test | حالة تحقق مرتبطة بـالفرز / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void conflictingFiltersAreEmpty()` | public · @Test | حالة تحقق مرتبطة بـالفلاتر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void dateRangeDoesNotLeaveStaleRows()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exportsPdf()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exportsExcel()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void export(String format)` | private | تصدير التصدير، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseListCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-38f0c720db49"></a>
## PurchaseOrderCoverageTest.java

**المصدر:** [src/test/java/Purches/PurchaseOrderCoverageTest.java](../../../src/test/java/Purches/PurchaseOrderCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 38 · **الأب المباشر:** `PurchaseFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormTest.java](TEST_Purches_AR.md#file-8880a685e0a4)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void cashOrderCreatesReceiptWithoutStockMovement()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseOrderCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-1bb38d129e85"></a>
## PurchaseReturnCoverageTest.java

**المصدر:** [src/test/java/Purches/PurchaseReturnCoverageTest.java](../../../src/test/java/Purches/PurchaseReturnCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 32 · **الأب المباشر:** `PurchaseFormTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormTest.java](TEST_Purches_AR.md#file-8880a685e0a4)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void percentageDiscountRecalculates()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fixedDiscountRecalculates()` | public · @Test | حالة تحقق مرتبطة بـالخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseReturnCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-68923271f3e1"></a>
## PurchaseScenarioTest.java

**المصدر:** [src/test/java/Purches/PurchaseScenarioTest.java](../../../src/test/java/Purches/PurchaseScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 299 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseModulePage.java](MAIN_PAGES_purches_AR.md#file-26ced787f2c9)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)، [PurchaseListCoverageTest.java](TEST_Purches_AR.md#file-027f2cd42e18)، [PurchaseSettlementEndToEndTest.java](TEST_Purches_AR.md#file-6492663b7f2d)، [PurchaseSmokeTest.java](TEST_Purches_AR.md#file-cd165977bd82)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract PurchaseDocumentKind kind();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected PurchaseDocumentKind documentKind()` | protected | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `public void awaitPurchaseSession()` | public | انتظار المشتريات / الجلسة، حسب المدخلات والتخصص. |
| `public void beginPurchaseScenario()` | public | بدء نطاق المشتريات، حسب المدخلات والتخصص. |
| `protected PurchaseTestData.Record ownItem(int index)` | protected | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `protected void prepare(String paymentType)` | protected | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `protected void add(PurchaseTestData.Record record, String mode)` | protected | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void fill(String mode)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected List<PurchaseTestData.Record> capture()` | protected | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `protected PurchaseTestData.Record save(boolean print)` | protected | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `protected PurchaseTestData.Record create(String mode, String pay, boolean print)` | protected | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static BigDecimal number(Object value)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static void sameNumber(Object actual, String expected)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected static BigDecimal signedBalance(Map<String, Object> value)` | protected | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `protected void assertSupplierDelta(Map<String, Object> before, String delta)` | protected | مقارنة متوقعة المورد، حسب المدخلات والتخصص. |
| `protected void assertStock(String quantity)` | protected | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void rejectSave()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void finishPurchaseCase(ITestResult result)` | public | تنفيذ جزء متعلق بـ المشتريات، حسب المدخلات والتخصص. |
| `public void cleanPurchasePage()` | public | تنظيف المشتريات / الصفحة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-6492663b7f2d"></a>
## PurchaseSettlementEndToEndTest.java

**المصدر:** [src/test/java/Purches/PurchaseSettlementEndToEndTest.java](../../../src/test/java/Purches/PurchaseSettlementEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 61 · **الأب المباشر:** `PurchaseScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialWorkflow.java](TEST_FinancialMovementsTest_AR.md#file-736d81307b79)، [PurchaseScenarioTest.java](TEST_Purches_AR.md#file-68923271f3e1)، [WorkflowCleanupOrder.java](TEST_TestSupport_AR.md#file-510f62deecd0)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private FinancialWorkflow settlement(PurchaseTestData.Record invoice)` | private | تنفيذ جزء متعلق بـ التسوية، حسب المدخلات والتخصص. |
| `public void creditPurchaseThenPartialAndFinalPayment()` | public · @Test | حالة تحقق مرتبطة بـالدائن / المشتريات / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void paymentEditAndDeleteRecalculateSupplierDebt()` | public · @Test | حالة تحقق مرتبطة بـالسداد / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fullPaymentThenDeletionRestoresSupplierDebt()` | public · @Test | حالة تحقق مرتبطة بـالسداد / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void finishPurchaseCase(ITestResult result)` | public | تنفيذ جزء متعلق بـ المشتريات، حسب المدخلات والتخصص. |
| `public void cleanPurchasePage()` | public | تنظيف المشتريات / الصفحة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseSettlementEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-cd165977bd82"></a>
## PurchaseSmokeTest.java

**المصدر:** [src/test/java/Purches/PurchaseSmokeTest.java](../../../src/test/java/Purches/PurchaseSmokeTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 16 · **الأب المباشر:** `PurchaseScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseScenarioTest.java](TEST_Purches_AR.md#file-68923271f3e1)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void savesOwnedCreditPurchase()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / الدائن / المشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchaseSmokeTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-2b6728a0e801"></a>
## PurchaseTestData.java

**المصدر:** [src/test/java/Purches/PurchaseTestData.java](../../../src/test/java/Purches/PurchaseTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 494

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private void awaitContext()` | private | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `PurchaseTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private PurchaseTestData(WebDriver driver,String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
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
| `Record agent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record otherSalesAgent()` | داخل الحزمة | تنفيذ جزء متعلق بـ المبيعات، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record destination()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record supplier()` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record customer(int allowedDiscount)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Object balance(Record item,int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `private Map<String,Object> rememberSeedAttempt(String endpoint,String name,Map<String,Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record bank()` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record safe()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record discountType()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `Record seedPurchase(int index,String type,String date,String quantity,String paid)` | داخل الحزمة | تنفيذ جزء متعلق بـ المشتريات، حسب المدخلات والتخصص. |
| `Record seedPurchase(int index,String type,String date,String quantity,String paid,Record salesAgent)` | داخل الحزمة | تنفيذ جزء متعلق بـ المشتريات، حسب المدخلات والتخصص. |
| `void deleteOwned(Record record)` | داخل الحزمة | حذف المملوك للاختبار، حسب المدخلات والتخصص. |
| `java.util.Set<String> documentIdentities()` | داخل الحزمة | تنفيذ جزء متعلق بـ المستند، حسب المدخلات والتخصص. |
| `void verifyNoNewDocuments(java.util.Set<String> before)` | داخل الحزمة | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean existsInList(Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String,Object> createFixture(String endpoint,Map<String,Object> values)` | private | إنشاء بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void track(String endpoint, Object id, String note, Map<String,Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean owns(Record record,Map<String,Object> stored)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void journalAttempts()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع / المحاولات، حسب المدخلات والتخصص. |
| `Map<String,Object> rememberFixtureAttempt(String endpoint,String name,Map<String,Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `else if(entity instanceof List<?> list)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `else if(entity instanceof Map<?,?> result&&result.get("pageData") instanceof List<?> list&&result.get("totalRecords") instanceof Number count)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-69710b6371e0"></a>
## PurchesBillsTest.java

**المصدر:** [src/test/java/Purches/PurchesBillsTest.java](../../../src/test/java/Purches/PurchesBillsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 107 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void PurchaseBillsTest()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByBarcodTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByPriceWithCashPayTest()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByPriceWithOtherPayTest()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsWithSaveBuutonTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteAll()` | public · @Test | حذف عام قديم؛ لا تساويه بتنظيف السجلات المملوكة. |
| `public void AddWithOutSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddWithOutProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TransToReturnPurchesOrder()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.PurchesBillsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-0b81882c7803"></a>
## purchOrderTest.java

**المصدر:** [src/test/java/Purches/purchOrderTest.java](../../../src/test/java/Purches/purchOrderTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 79 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void purchOrderTest()` | public · @Test | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void AddPurchesOrderByBarcodTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesOrderByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteAll()` | public · @Test | حذف عام قديم؛ لا تساويه بتنظيف السجلات المملوكة. |
| `public void AddWithOutSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddWithOutProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void TransToPurchaseBill()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.purchOrderTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-4ba40e5702e1"></a>
## ReturnOrderTest.java

**المصدر:** [src/test/java/Purches/ReturnOrderTest.java](../../../src/test/java/Purches/ReturnOrderTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 88 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected PurchaseDocumentKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddReturnOrderTest()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddReturnOrderTestWithDiscount()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByBarcodTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsByNameTest()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddPurchesBillsWithSaveBuutonTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteReturnOrderTest()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddWithOutSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddWithOutProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.ReturnOrderTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-81dbab96b057"></a>
## SupplyChainEndToEndTest.java

**المصدر:** [src/test/java/Purches/SupplyChainEndToEndTest.java](../../../src/test/java/Purches/SupplyChainEndToEndTest.java)

**دوره:** اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.

**حالته:** مصدر نشط · **عدد أسطره:** 144 · **الأب المباشر:** `PurchaseFormActions`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialWorkflow.java](TEST_FinancialMovementsTest_AR.md#file-736d81307b79)، [SalesWorkflow.java](TEST_Invoice_AR.md#file-1d10b6cfe30c)، [PurchaseFormActions.java](TEST_Purches_AR.md#file-12fceea3105e)، [TransferWorkflow.java](TEST_StoreTest_AR.md#file-9a6d69781352)، [WorkflowCleanupOrder.java](TEST_TestSupport_AR.md#file-510f62deecd0)، [WorkflowEntity.java](TEST_TestSupport_AR.md#file-d394a5f89a8c)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static WorkflowEntity ref(PurchaseTestData.Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private PurchaseTestData.Record buy(String quantity)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private TransferWorkflow transfer(String quantity)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private SalesWorkflow selling()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void stock(BigDecimal source, BigDecimal destination)` | private | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void customerDebt(String delta)` | private | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `public void purchaseTransferAndSaleKeepStockAndPartyLedgersConsistent()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void supplyChainThenCollectionAndSupplierPaymentClearBothDebts()` | public · @Test | حالة تحقق مرتبطة بـالمورد / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void bothReturnsAndReverseTransferRestoreTheWholeChain()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalSupplyChainPreservesQuantitiesAndAmounts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void partialTransferAndSaleRetainUnsoldStockInBothStores()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void partialSaleReturnKeepsRemainingDestinationStockAndDebt()` | public · @Test | حالة تحقق مرتبطة بـالمرتجع؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void captureChildren()` | private | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `public void finishPurchaseCase(ITestResult result)` | public | تنفيذ جزء متعلق بـ المشتريات، حسب المدخلات والتخصص. |
| `public void cleanPurchasePage()` | public | تنظيف المشتريات / الصفحة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Purches.SupplyChainEndToEndTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.
