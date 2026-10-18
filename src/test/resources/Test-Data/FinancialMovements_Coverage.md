# حالات اختبار الحركات المالية

كل حالة تنشئ بياناتها وتستخدم IDs وأسماء تلك البيانات فقط. التعديل والحذف يستهدفان حركة أنشأتها الحالة نفسها، والتنظيف يتحقق من اختفاء السجل ثم يحذف البيانات التابعة بترتيب العلاقات.

## سند قبض عميل

18 حالة.

| الحالة | السيناريو |
|---|---|
| `CustomerReceiptTest.AddCustomerWithSaveAndCustomerReceiptTest` | إضافة سند قبض عميل مع توزيع على الفواتير |
| `CustomerReceiptTest.AddResetWithSaveTest` | إضافة سند قبض عميل بالحفظ فقط |
| `CustomerReceiptTest.AddNewResetWithSaveAndPrint` | إضافة سند قبض عميل مع الحفظ والطباعة والتحقق من التقرير |
| `CustomerReceiptTest.EditeAfterReportTest` | تعديل سند قبض عميل مع الحفظ والطباعة والتحقق من التقرير |
| `CustomerReceiptTest.DeleteCustomerReceiptTest` | حذف سند قبض عميل |
| `CustomerReceiptTest.AssertRequiredErrorsMessageForAgent` | التحقق - العميل إلزامي في سند القبض |
| `CustomerReceiptTest.AssertRequiredErrorsMessageForAmount` | التحقق - المبلغ وطريقة الدفع إلزاميان في سند القبض |
| `CustomerReceiptTest.AssertRequiredErrorsMessageForPaymentMethod` | التحقق - طريقة الدفع إلزامية في سند القبض |
| `CustomerReceiptTest.AssertRequiredErrorsMessageForPaymentMethodOption` | التحقق - خيار طريقة الدفع إلزامي في سند القبض |
| `CustomerReceiptTest.AddOldDate` | التحقق - خطأ عند إدخال تاريخ قديم في سند القبض |
| `CustomerReceiptTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `CustomerReceiptTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `CustomerReceiptTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `CustomerReceiptTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `CustomerReceiptTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `CustomerReceiptTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `CustomerReceiptTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `CustomerReceiptTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## سند صرف مورد

18 حالة.

| الحالة | السيناريو |
|---|---|
| `SupplierBillOfExchangeTest.AddSupplierBillOfExchangeTest` | إضافة سند صرف مورد مع توزيع على الفواتير |
| `SupplierBillOfExchangeTest.AddSupplierBillWithSaveTest` | إضافة سند صرف مورد بالحفظ فقط |
| `SupplierBillOfExchangeTest.AddSupplierBillWithSaveAndPrint` | إضافة سند صرف مورد مع الحفظ والطباعة والتحقق من التقرير |
| `SupplierBillOfExchangeTest.EditSupplierBillAfterReport` | تعديل سند صرف مورد مع الحفظ والطباعة والتحقق من التقرير |
| `SupplierBillOfExchangeTest.DeleteSupplierBillTest` | حذف سند صرف مورد |
| `SupplierBillOfExchangeTest.AssertRequiredErrorsMessageForSupplier` | التحقق - المورد إلزامي في سند الصرف |
| `SupplierBillOfExchangeTest.AssertRequiredErrorsMessageForAmount` | التحقق - المبلغ وطريقة الدفع إلزاميان في سند الصرف |
| `SupplierBillOfExchangeTest.AssertRequiredErrorsMessageForPaymentMethod` | التحقق - طريقة الدفع إلزامية في سند الصرف |
| `SupplierBillOfExchangeTest.AssertRequiredErrorsMessageForPaymentMethodOption` | التحقق - خيار طريقة الدفع إلزامي في سند الصرف |
| `SupplierBillOfExchangeTest.AddOldDate` | التحقق - خطأ عند إدخال تاريخ قديم في سند الصرف |
| `SupplierBillOfExchangeTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `SupplierBillOfExchangeTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `SupplierBillOfExchangeTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `SupplierBillOfExchangeTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `SupplierBillOfExchangeTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `SupplierBillOfExchangeTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `SupplierBillOfExchangeTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `SupplierBillOfExchangeTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## حركة المصروفات

17 حالة.

| الحالة | السيناريو |
|---|---|
| `ExpenseMovementTest.AddExpenseMovementWithSelectTax_AssertValue` | إضافة حركة مصروف مع الضريبة والحفظ والطباعة والتحقق من الحسابات |
| `ExpenseMovementTest.EditExpenseMovementWithSelectTax_AssertValue` | تعديل حركة مصروف مع الضريبة والحفظ والطباعة والتحقق من الحسابات |
| `ExpenseMovementTest.AddExpenseMovementWithSelectAccountantAndClickSave` | إضافة حركة مصروف بنوع حساب محاسبي والحفظ |
| `ExpenseMovementTest.AddExpenseMovementWithSelectExpenseAndClickSave` | إضافة حركة مصروف بنوع مصروف والحفظ |
| `ExpenseMovementTest.DeleteExpenseMovement` | حذف حركة مصروف |
| `ExpenseMovementTest.AssertTypeExpenseErrorMessage` | التحقق - نوع المصروف إلزامي |
| `ExpenseMovementTest.AssertPaymentMethodErrorMessage` | التحقق - طريقة الدفع إلزامية في حركة المصروف |
| `ExpenseMovementTest.AssertPaymentOptionMethodErrorMessage` | التحقق - خيار طريقة الدفع إلزامي في حركة المصروف |
| `ExpenseMovementTest.AssertValueErrorMessage` | التحقق - المبلغ إلزامي في حركة المصروف |
| `ExpenseMovementTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `ExpenseMovementTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `ExpenseMovementTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `ExpenseMovementTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `ExpenseMovementTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `ExpenseMovementTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `ExpenseMovementTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `ExpenseMovementTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## الإيرادات الأخرى

15 حالة.

| الحالة | السيناريو |
|---|---|
| `OtherRevenueMovementTest.AddOtherRevenueMovementWithSaveAndPrint` | إضافة حركة إيراد مع الحفظ والطباعة والتحقق من التقرير |
| `OtherRevenueMovementTest.EditOtherRevenueMovement` | تعديل حركة إيراد مع الحفظ والطباعة والتحقق من التقرير |
| `OtherRevenueMovementTest.AddOtherRevenueMovementWithSave` | إضافة حركة إيراد بالحفظ فقط |
| `OtherRevenueMovementTest.DeleteOtherRevenueMovement` | حذف حركة إيراد |
| `OtherRevenueMovementTest.AssertTypeRequiredErrorMessage` | التحقق - نوع الإيراد إلزامي |
| `OtherRevenueMovementTest.AssertAmountRequiredErrorMessage` | التحقق - المبلغ إلزامي في حركة الإيراد |
| `OtherRevenueMovementTest.AssertPaymentMethodRequiredErrorMessage` | التحقق - طريقة الدفع إلزامية في حركة الإيراد |
| `OtherRevenueMovementTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `OtherRevenueMovementTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `OtherRevenueMovementTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `OtherRevenueMovementTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `OtherRevenueMovementTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `OtherRevenueMovementTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `OtherRevenueMovementTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `OtherRevenueMovementTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## إشعار خصم عميل

16 حالة.

| الحالة | السيناريو |
|---|---|
| `CustomerDiscountNoticeTest.AddCustomerDiscountNoticeAnd_VerifyCalculationsAndReport` | إضافة إشعار خصم عميل |
| `CustomerDiscountNoticeTest.EditCustomerDiscountNoticeAnd_VerifyCalculationsAndReport` | تعديل إشعار خصم عميل |
| `CustomerDiscountNoticeTest.AddCustomerDiscountNoticeAnd_VerifyCalculationsAndCustom` | إضافة إشعار خصم عميل مع سند مخصص |
| `CustomerDiscountNoticeTest.AddCustomerDiscountNoticeAnd_VerifyCalculationsAndSave` | إضافة إشعار خصم عميل |
| `CustomerDiscountNoticeTest.Delete` | حذف إشعار خصم عميل |
| `CustomerDiscountNoticeTest.InvalidDate` | التحقق - إضافة إشعار خصم عميل بتاريخ غير صالح |
| `CustomerDiscountNoticeTest.InvalidAgentName` | التحقق - إضافة إشعار خصم عميل باسم مندوب غير صالح |
| `CustomerDiscountNoticeTest.InvalidValue` | التحقق - إضافة إشعار خصم عميل بقيمة غير صالحة |
| `CustomerDiscountNoticeTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `CustomerDiscountNoticeTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `CustomerDiscountNoticeTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `CustomerDiscountNoticeTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `CustomerDiscountNoticeTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `CustomerDiscountNoticeTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `CustomerDiscountNoticeTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `CustomerDiscountNoticeTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## إشعار خصم مورد

16 حالة.

| الحالة | السيناريو |
|---|---|
| `SupplierDiscountNoticeTest.AddSupplierDiscountNoticeAnd_VerifyCalculationsAndReport` | إضافة إشعار خصم مورد مع التحقق من الحسابات والتقرير |
| `SupplierDiscountNoticeTest.EditSupplierDiscountNoticeAnd_VerifyCalculationsAndReport` | تعديل إشعار خصم مورد |
| `SupplierDiscountNoticeTest.AddSupplierDiscountNoticeAnd_VerifyCalculationsAndCustom` | إضافة إشعار خصم مورد مع سند مخصص |
| `SupplierDiscountNoticeTest.AddSupplierDiscountNoticeAnd_VerifyCalculationsAndSave` | إضافة إشعار خصم مورد وحفظه مباشرة |
| `SupplierDiscountNoticeTest.Delete` | حذف إشعار خصم مورد مملوك للاختبار |
| `SupplierDiscountNoticeTest.InvalidDate` | التحقق من عدم الحفظ ببيانات تاريخ غير صحيحة |
| `SupplierDiscountNoticeTest.InvalidAgentName` | التحقق من إلزامية اختيار المورد |
| `SupplierDiscountNoticeTest.InvalidValue` | التحقق من إلزامية إدخال قيمة الخصم |
| `SupplierDiscountNoticeTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `SupplierDiscountNoticeTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `SupplierDiscountNoticeTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `SupplierDiscountNoticeTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `SupplierDiscountNoticeTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `SupplierDiscountNoticeTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `SupplierDiscountNoticeTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `SupplierDiscountNoticeTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## التحويلات والتسويات

30 حالة.

| الحالة | السيناريو |
|---|---|
| `TransfersAndSettlementsTest.AddNewTransferAndSettlement_SaveAndPrint_AndAssertResults` | إضافة تحويل وتسوية جديد |
| `TransfersAndSettlementsTest.EditTransferAndSettlement_SaveAndPrint_AndAssertResults` | تعديل تحويل وتسوية |
| `TransfersAndSettlementsTest.AddFromCashToCash_SaveAndPrint_AndAssertResults` | تحويل من خزينة إلى خزينة |
| `TransfersAndSettlementsTest.AddFromCashToBank_SaveAndPrint_AndAssertResults` | تحويل من خزينة إلى بنك |
| `TransfersAndSettlementsTest.AddFromCashToAgent_SaveAndPrint_AndAssertResults` | تحويل من خزينة إلى عميل |
| `TransfersAndSettlementsTest.FromCashToSupplier` | تحويل من خزينة إلى مورد |
| `TransfersAndSettlementsTest.fromBankToCash` | تحويل من بنك إلى خزينة |
| `TransfersAndSettlementsTest.fromBankToBank` | تحويل من بنك إلى بنك |
| `TransfersAndSettlementsTest.fromBankToAgent` | تحويل من بنك إلى عميل |
| `TransfersAndSettlementsTest.fromBankToSupplier` | تحويل من بنك إلى مورد |
| `TransfersAndSettlementsTest.fromAgentToCash` | تحويل من عميل إلى خزينة |
| `TransfersAndSettlementsTest.fromAgentToBank` | تحويل من عميل إلى بنك |
| `TransfersAndSettlementsTest.fromAgentToAgent` | تحويل من عميل إلى عميل |
| `TransfersAndSettlementsTest.fromAgentToSupplier` | تحويل من عميل إلى مورد |
| `TransfersAndSettlementsTest.fromSupplierToCash` | تحويل من مورد إلى خزينة |
| `TransfersAndSettlementsTest.fromSupplierToBank` | تحويل من مورد إلى بنك |
| `TransfersAndSettlementsTest.fromSupplierToAgent` | تحويل من مورد إلى عميل |
| `TransfersAndSettlementsTest.fromSupplierToSupplier` | تحويل من مورد إلى مورد |
| `TransfersAndSettlementsTest.Delete` | حذف تحويل وتسوية مملوكين للاختبار |
| `TransfersAndSettlementsTest.AssertTypeDontSendToSameType` | التحقق من منع التحويل لنفس الحساب |
| `TransfersAndSettlementsTest.AssertValueErorrMessage` | التحقق من رفض قيمة تحويل سالبة |
| `TransfersAndSettlementsTest.AssertselectEverySideOption` | التحقق من إلزامية اختيار جميع أطراف التحويل |
| `TransfersAndSettlementsTest.FM_CloseNewWithoutSaving` | إغلاق الإضافة دون إنشاء سجل |
| `TransfersAndSettlementsTest.FM_CancelEditWithoutSaving` | إلغاء تعديل القيمة والملاحظات والتحقق من بقاء البيانات المحفوظة |
| `TransfersAndSettlementsTest.FM_CancelDelete` | إلغاء الحذف والتحقق من بقاء السجل |
| `TransfersAndSettlementsTest.FM_RejectNegativeAmount` | رفض القيمة السالبة دون إنشاء حركة |
| `TransfersAndSettlementsTest.FM_RejectZeroAmount` | رفض القيمة صفر دون إنشاء حركة |
| `TransfersAndSettlementsTest.FM_ListStructure` | بنية القائمة وظهور حركة الاختبار المحددة بكودها |
| `TransfersAndSettlementsTest.FM_ExportPdf` | تصدير ملف PDF والتحقق من محتواه الفعلي |
| `TransfersAndSettlementsTest.FM_ExportExcel` | تصدير ملف Excel والتحقق من محتواه الفعلي |

## التحقق من تجهيز البيانات والتنظيف

| الاختبار | الغرض |
|---|---|
| `FinancialFixtureTest.createdFixturesAppearInFinancialLookups` | إنشاء البيانات المساعدة والتحقق من ظهورها وربطها بالحسابات الصحيحة |
| `FinancialCleanupTest.acceptsMissingDeleteOnlyAfterReadConfirmsAbsence` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.retriesUncommittedDeleteBeforeParentAccounts` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.acceptsEmptyStringReadAfterCommittedDeleteTimeout` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.retainsBlockedParentsAndRetriesOnlyItsOwnRecords` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.cleansOwnedItemCommittedBeforeCreateFailure` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.acceptsVerifiedMissingTransactionDefaultDto` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.acceptsVerifiedMissingMasterDefaultDto` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.updatedMovementDeletesBeforeItsNewReference` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |
| `FinancialCleanupTest.confirmsCustomerAbsenceWithoutHidingExistingRecordErrors` | أمان الحذف والتحقق من ردود السيرفر وترتيب العلاقات |

الإجمالي: **141 اختبارًا**. نتائج التشغيل الفعلية مذكورة في README وتقرير Allure؛ وجود الحالة هنا لا يعني أنها نجحت في آخر تشغيل.
