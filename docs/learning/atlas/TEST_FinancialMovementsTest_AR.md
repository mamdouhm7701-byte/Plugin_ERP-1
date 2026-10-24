# اختبارات ودعم الحركات المالية

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-182f72fd3521"></a>
## CustomerDiscountNoticeTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/CustomerDiscountNoticeTest.java](../../../src/test/java/FinancialMovementsTest/CustomerDiscountNoticeTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 116 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndReport()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الخصم / التقرير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditCustomerDiscountNoticeAnd_VerifyCalculationsAndReport()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الخصم / التقرير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndCustom()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddCustomerDiscountNoticeAnd_VerifyCalculationsAndSave()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void InvalidDate()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvalidAgentName()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvalidValue()` | public · @Test | حالة تحقق مرتبطة بـالقيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.CustomerDiscountNoticeTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-091f02390bc8"></a>
## CustomerReceiptTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/CustomerReceiptTest.java](../../../src/test/java/FinancialMovementsTest/CustomerReceiptTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 128 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddCustomerWithSaveAndCustomerReceiptTest()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddResetWithSaveTest()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddNewResetWithSaveAndPrint()` | public · @Test | حالة تحقق مرتبطة بـالطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditeAfterReportTest()` | public · @Test | حالة تحقق مرتبطة بـالتقرير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteCustomerReceiptTest()` | public · @Test | حالة تحقق مرتبطة بـالعميل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForAgent()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForAmount()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / المبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForPaymentMethod()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForPaymentMethodOption()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddOldDate()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.CustomerReceiptTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-6c96b0105b2d"></a>
## ExpenseMovementTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/ExpenseMovementTest.java](../../../src/test/java/FinancialMovementsTest/ExpenseMovementTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 122 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddExpenseMovementWithSelectTax_AssertValue()` | public · @Test | حالة تحقق مرتبطة بـالضريبة / القيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditExpenseMovementWithSelectTax_AssertValue()` | public · @Test | حالة تحقق مرتبطة بـالضريبة / القيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddExpenseMovementWithSelectAccountantAndClickSave()` | public · @Test | حالة تحقق مرتبطة بـالنقر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddExpenseMovementWithSelectExpenseAndClickSave()` | public · @Test | حالة تحقق مرتبطة بـالنقر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteExpenseMovement()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertTypeExpenseErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertPaymentMethodErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالسداد / الخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertPaymentOptionMethodErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالسداد / الخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertValueErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالقيمة / الخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.ExpenseMovementTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-6d55f89ce2be"></a>
## ExportedWorkbookAssertions.java

**المصدر:** [src/test/java/FinancialMovementsTest/ExportedWorkbookAssertions.java](../../../src/test/java/FinancialMovementsTest/ExportedWorkbookAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 42

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `static void assertOwnedRow(byte[] contents, String code, String party, BigDecimal amount)` | داخل الحزمة | مقارنة متوقعة المملوك للاختبار / الصف، حسب المدخلات والتخصص. |
| `record ExportRow(Map<Integer,String> cells,int amountColumn)` | داخل الحزمة | تصدير التصدير / الصف، حسب المدخلات والتخصص. |

<a id="file-69e3f587612d"></a>
## ExportedWorkbookAssertionsTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/ExportedWorkbookAssertionsTest.java](../../../src/test/java/FinancialMovementsTest/ExportedWorkbookAssertionsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 42

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private byte[] workbook(boolean xlsx) throws IOException` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.ExportedWorkbookAssertionsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-5b102d2bee00"></a>
## FinancialCleanupTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialCleanupTest.java](../../../src/test/java/FinancialMovementsTest/FinancialCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 434

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void identifiedCollectionUpdateCanRecoverWithoutDetailBranchMetadata()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pendingUpdateRejectsDifferentDealer()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pendingUpdateRejectsDifferentBranch()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pendingCreateStillRequiresBranchEvidence()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pendingUpdateRejectsDifferentDocumentIdentity()` | public · @Test | حالة تحقق مرتبطة بـالمستند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pendingUpdateRejectsDifferentIdentityEvenWhenBranchMatches()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void newFormZeroIdentityCanRecoverWithFullBranchEvidence()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void verifyPendingUpdateRecovery(boolean changedDealer, boolean changedBranch, boolean allowed)` | private | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void verifyPendingUpdateRecovery(boolean changedDealer, boolean changedBranch, boolean allowed, String intendedId)` | private | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void verifyPendingUpdateRecovery(boolean changedDealer, boolean changedBranch, boolean allowed, String intendedId, boolean includeBranch)` | private | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void normalCustomerIsTaxableAndKeepsItsOwnAccount()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retriesUncommittedDeleteBeforeParentAccounts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsEmptyStringReadAfterCommittedDeleteTimeout()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansOwnedItemCommittedBeforeCreateFailure()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingTransactionDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingMasterDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void updatedMovementDeletesBeforeItsNewReference()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void confirmsCustomerAbsenceWithoutHidingExistingRecordErrors()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الأخطاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageScopeSharesOnlyStableReferences()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void resumesUncertainCreateBeforeDeletingItsDependencies() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesRecoveryInAnotherBranch() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public String captureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> capturedSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> capturedAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public void awaitSavesSettled()` | public | انتظار عمليات الحفظ، حسب المدخلات والتخصص. |
| `public void waitsForBranchHydrationAfterClosingTheForm()` | public · @Test | حالة تحقق مرتبطة بـالنموذج؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.FinancialCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-0341520421b1"></a>
## FinancialExportAssertions.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialExportAssertions.java](../../../src/test/java/FinancialMovementsTest/FinancialExportAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 109

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private FinancialExportAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format, String code, String party, java.math.BigDecimal amount)` | داخل الحزمة | مقارنة متوقعة التصدير، حسب المدخلات والتخصص. |
| `private static void installDownloadCapture(WebDriver driver)` | private | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-2f215ea40c00"></a>
## FinancialFixtureApi.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialFixtureApi.java](../../../src/test/java/FinancialMovementsTest/FinancialFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 59 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `FinancialFixtureApi(WebDriver driver, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-d9dc6e85d153"></a>
## FinancialFixtureApiStub.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialFixtureApiStub.java](../../../src/test/java/FinancialMovementsTest/FinancialFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 143

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-3e5e8598654c"></a>
## FinancialFixtureDefinitions.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialFixtureDefinitions.java](../../../src/test/java/FinancialMovementsTest/FinancialFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 100

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `FinancialFixtureDefinitions(FinancialTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record customer(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record supplier(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record bank(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record safe(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record expense()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record income()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record discount()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record invoice(boolean purchase,java.math.BigDecimal allocation,String suffix)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `Record party(String type,int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c208fe4706c3"></a>
## FinancialFixtureRecovery.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialFixtureRecovery.java](../../../src/test/java/FinancialMovementsTest/FinancialFixtureRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 16 · **الأب المباشر:** `Plugin_ERP.Tests.LoginTest.BaseTest`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-aa2e51cd24e5"></a>
## FinancialFixtureTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialFixtureTest.java](../../../src/test/java/FinancialMovementsTest/FinancialFixtureTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 38 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void createdFixturesAppearInFinancialLookups()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private boolean contains(Object list, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.FinancialFixtureTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-f0a00d0bbba5"></a>
## FinancialScenarioTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialScenarioTest.java](../../../src/test/java/FinancialMovementsTest/FinancialScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 466 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [CustomerDiscountNoticeTest.java](TEST_FinancialMovementsTest_AR.md#file-182f72fd3521)، [CustomerReceiptTest.java](TEST_FinancialMovementsTest_AR.md#file-091f02390bc8)، [ExpenseMovementTest.java](TEST_FinancialMovementsTest_AR.md#file-6c96b0105b2d)، [OtherRevenueMovementTest.java](TEST_FinancialMovementsTest_AR.md#file-22abd1dd4a66)، [SupplierBillOfExchangeTest.java](TEST_FinancialMovementsTest_AR.md#file-999c4997fe69)، [SupplierDiscountNoticeTest.java](TEST_FinancialMovementsTest_AR.md#file-b54a112323ac)، [TransfersAndSettlementsTest.java](TEST_FinancialMovementsTest_AR.md#file-9491c1878ced)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract FinancialMovementKind kind();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract FinancialMovementPage movementPage();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void newFinancialScenario()` | public | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void prepare(boolean accountMode)` | private | تجهيز سياق الحالة وبياناتها واختيارات المستند. |
| `private void fill(String amount, boolean accountMode, boolean tax)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void fill(String amount, boolean accountMode, boolean tax, String omitted)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private List<FinancialTestData.Record> capture()` | private | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `private FinancialTestData.Record persist(boolean print, String amount)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void assertOwnedRelationships(Map<String, Object> stored)` | private | مقارنة متوقعة المملوك للاختبار، حسب المدخلات والتخصص. |
| `protected void create( boolean print, boolean allocation, boolean tax, boolean accountMode, String amount)` | protected | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void edit(boolean print, boolean tax)` | protected | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void edit(boolean print, boolean tax, boolean accountMode)` | protected | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void deleteOwn()` | protected | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void transfer(String from, String to, boolean print)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void invalid(String field)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void allocateOwnInvoice(String amount)` | protected | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `protected void assertTaxIdentity()` | protected | مقارنة متوقعة الضريبة، حسب المدخلات والتخصص. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void export(String format)` | private | تصدير التصدير، حسب المدخلات والتخصص. |
| `public void cleanFinancialScenario()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void retryFinancialCleanup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.FinancialScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-6a0c012d6283"></a>
## FinancialSuitePolicy.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialSuitePolicy.java](../../../src/test/java/FinancialMovementsTest/FinancialSuitePolicy.java)

**دوره:** قواعد مستوى/اختيار حالات المجال أو ضبطها؛ ليست صفحة أو وصف بيانات ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 22

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void alter(List<XmlSuite> suites)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-b9b375bca508"></a>
## FinancialTestData.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialTestData.java](../../../src/test/java/FinancialMovementsTest/FinancialTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 323

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `FinancialTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private FinancialTestData(WebDriver driver,String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `static Map<String, Object> fields(Object... pairs)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record customer(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record supplier(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ المورد، حسب المدخلات والتخصص. |
| `Record bank(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record safe(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record expense()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record income()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record discount()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |
| `Record agent(boolean purchase)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record invoice(boolean purchase,java.math.BigDecimal allocation)` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `Record party(String type,int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean existsInList(Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void track(String endpoint, Object id, String note, Map<String,Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void recoverPendingFixtures()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String journalSuffix()` | داخل الحزمة | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `void preserveTreasuryAuditHistory()` | داخل الحزمة | تنفيذ جزء متعلق بـ سجل المراجعة، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-736d81307b79"></a>
## FinancialWorkflow.java

**المصدر:** [src/test/java/FinancialMovementsTest/FinancialWorkflow.java](../../../src/test/java/FinancialMovementsTest/FinancialWorkflow.java)

**دوره:** خطوات رحلة أعمال معاد استخدامها، بدل تكرار تسلسل عدة شاشات داخل كل اختبار.

**حالته:** مصدر نشط · **عدد أسطره:** 143

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialMovementKind.java](MAIN_PAGES_FinancialMovements_AR.md#file-0a8202d84376)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**ملفات تشير إليه مباشرة:** [SalesSettlementEndToEndTest.java](TEST_Invoice_AR.md#file-5e0d70f6fb59)، [PurchaseSettlementEndToEndTest.java](TEST_Purches_AR.md#file-6492663b7f2d)، [SupplyChainEndToEndTest.java](TEST_Purches_AR.md#file-81dbab96b057)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record Movement(Object id, String note, String endpoint, Map<String, Object> values)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public FinancialWorkflow(WebDriver driver, boolean purchase, Object partyId, String partyName, String agentName, Object invoiceId, Object invoiceCode)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public Movement pay(String amount)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Movement edit(Movement original, String amount)` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void delete(Movement movement)` | public | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Map<String, Object> read(Movement movement)` | public | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private Movement save(String amount)` | private | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `public void capture()` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `private static void equalAmount(Object actual, String expected)` | private | تنفيذ جزء متعلق بـ المبلغ، حسب المدخلات والتخصص. |
| `public void close()` | public | إنهاء نطاق/مورد وتنظيفه حسب مالكه؛ اقرأ سياق الكلاس. |

<a id="file-22abd1dd4a66"></a>
## OtherRevenueMovementTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/OtherRevenueMovementTest.java](../../../src/test/java/FinancialMovementsTest/OtherRevenueMovementTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 110 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddOtherRevenueMovementWithSaveAndPrint()` | public · @Test | حالة تحقق مرتبطة بـالطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditOtherRevenueMovement()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddOtherRevenueMovementWithSave()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteOtherRevenueMovement()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertTypeRequiredErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertAmountRequiredErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ / الخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertPaymentMethodRequiredErrorMessage()` | public · @Test | حالة تحقق مرتبطة بـالسداد / الخطأ / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.OtherRevenueMovementTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-999c4997fe69"></a>
## SupplierBillOfExchangeTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/SupplierBillOfExchangeTest.java](../../../src/test/java/FinancialMovementsTest/SupplierBillOfExchangeTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 128 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddSupplierBillOfExchangeTest()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddSupplierBillWithSaveTest()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddSupplierBillWithSaveAndPrint()` | public · @Test | حالة تحقق مرتبطة بـالمورد / الطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditSupplierBillAfterReport()` | public · @Test | حالة تحقق مرتبطة بـالمورد / التقرير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteSupplierBillTest()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForSupplier()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForAmount()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / المبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForPaymentMethod()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertRequiredErrorsMessageForPaymentMethodOption()` | public · @Test | حالة تحقق مرتبطة بـالأخطاء / الرسالة / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddOldDate()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.SupplierBillOfExchangeTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-b54a112323ac"></a>
## SupplierDiscountNoticeTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/SupplierDiscountNoticeTest.java](../../../src/test/java/FinancialMovementsTest/SupplierDiscountNoticeTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 116 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndReport()` | public · @Test | حالة تحقق مرتبطة بـالمورد / الخصم / التقرير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditSupplierDiscountNoticeAnd_VerifyCalculationsAndReport()` | public · @Test | حالة تحقق مرتبطة بـالمورد / الخصم / التقرير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndCustom()` | public · @Test | حالة تحقق مرتبطة بـالمورد / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndSave()` | public · @Test | حالة تحقق مرتبطة بـالمورد / الخصم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void InvalidDate()` | public · @Test | حالة تحقق مرتبطة بـالتاريخ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvalidAgentName()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void InvalidValue()` | public · @Test | حالة تحقق مرتبطة بـالقيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.SupplierDiscountNoticeTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9491c1878ced"></a>
## TransfersAndSettlementsTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/TransfersAndSettlementsTest.java](../../../src/test/java/FinancialMovementsTest/TransfersAndSettlementsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 200 · **الأب المباشر:** `FinancialScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [FinancialScenarioTest.java](TEST_FinancialMovementsTest_AR.md#file-f0a00d0bbba5)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected FinancialMovementKind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected FinancialMovementPage movementPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddNewTransferAndSettlement_SaveAndPrint_AndAssertResults()` | public · @Test | حالة تحقق مرتبطة بـالتسوية / الطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditTransferAndSettlement_SaveAndPrint_AndAssertResults()` | public · @Test | حالة تحقق مرتبطة بـالتسوية / الطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddFromCashToCash_SaveAndPrint_AndAssertResults()` | public · @Test | حالة تحقق مرتبطة بـالطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddFromCashToBank_SaveAndPrint_AndAssertResults()` | public · @Test | حالة تحقق مرتبطة بـالبنك / الطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddFromCashToAgent_SaveAndPrint_AndAssertResults()` | public · @Test | حالة تحقق مرتبطة بـالطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FromCashToSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromBankToCash()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromBankToBank()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromBankToAgent()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromBankToSupplier()` | public · @Test | حالة تحقق مرتبطة بـالبنك / المورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromAgentToCash()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromAgentToBank()` | public · @Test | حالة تحقق مرتبطة بـالبنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromAgentToAgent()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromAgentToSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromSupplierToCash()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromSupplierToBank()` | public · @Test | حالة تحقق مرتبطة بـالمورد / البنك؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromSupplierToAgent()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fromSupplierToSupplier()` | public · @Test | حالة تحقق مرتبطة بـالمورد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void Delete()` | public · @Test | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void AssertTypeDontSendToSameType()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertValueErorrMessage()` | public · @Test | حالة تحقق مرتبطة بـالقيمة / الرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertselectEverySideOption()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CloseNewWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelEditWithoutSaving()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectNegativeAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_RejectZeroAmount()` | public · @Test | حالة تحقق مرتبطة بـالمبلغ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void FM_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.TransfersAndSettlementsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-aaf8d34190e8"></a>
## TreasuryLedgerCoverageTest.java

**المصدر:** [src/test/java/FinancialMovementsTest/TreasuryLedgerCoverageTest.java](../../../src/test/java/FinancialMovementsTest/TreasuryLedgerCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 138 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Chrome.java](MAIN_Drivers_AR.md#file-14889194f9e4)، [TreasuryHandoverPage.java](MAIN_PAGES_BasicData_AR.md#file-ca1ef3ba6311)، [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [LoginPage.java](MAIN_PAGES_Login_AR.md#file-c3f664f98635)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private void exact(Object actual,String expected)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `FinancialMovementsTest.TreasuryLedgerCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

