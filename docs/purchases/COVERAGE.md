# Purchase test cases

Descriptions are defined in the test source through Allure `@Story`; UI cases also explain the scenario through `@Description`. Inherited form contracts run separately for each concrete document type. The original 25 methods run in the compatibility suite to avoid duplicate main-suite invocations.

| Class | Method | Scenario |
|---|---|---|
| PurchaseCalculationTest | `untaxedInvoice` | حساب ثلاثة بنود بسعر مائة دون ضريبة بصافي ثلاثمائة |
| PurchaseCalculationTest | `exclusiveVat` | حساب ضريبة مضافة بنسبة خمسة عشر بالمائة بصافي ثلاثمائة وخمسة وأربعين |
| PurchaseCalculationTest | `inclusiveVat` | استخراج الضريبة من سعر شامل لها دون زيادة الصافي |
| PurchaseCalculationTest | `percentageDiscountBeforeExclusiveVat` | تطبيق خصم عشرة بالمائة قبل حساب الضريبة المضافة |
| PurchaseCalculationTest | `fixedDiscountWithInclusiveVat` | تطبيق خصم بالقيمة على سعر شامل الضريبة |
| PurchaseCalculationTest | `fixedDiscountWithoutVat` | حساب خصم بالقيمة على كمية كسرية دون ضريبة |
| PurchaseCalculationTest | `fullPercentageDiscount` | خصم كامل بالنسبة يجعل الوعاء والضريبة والصافي صفراً |
| PurchaseCalculationTest | `fullFixedDiscount` | خصم كامل بالقيمة يجعل الوعاء والضريبة والصافي صفراً |
| PurchaseCalculationTest | `fractionalQuantityAndRounding` | تقريب الكمية الكسرية والسعر والضريبة إلى منزلتين |
| PurchaseCalculationTest | `aggregateTaxRounding` | تقريب ضريبة مجموع البنود الصغيرة بدلاً من تضخيمها |
| PurchaseCalculationTest | `threeDecimalInclusiveRounding` | تقريب السعر الشامل للضريبة إلى ثلاث منازل عشرية |
| PurchaseCalculationTest | `emptyCalculationIsZero` | الحساب بدون بنود يعيد جميع الإجماليات صفراً |
| PurchaseCleanupTest | `actualPageLifecycleDefersCleanupUntilGroupEndEvenAfterFailure` | دورة الصفحة تحتفظ ببيانات الحالتين الناجحة والفاشلة وتغلق النوافذ ثم تنظف المجموعة مرة واحدة |
| PurchaseCleanupTest | `interruptedCleanupJournalsOnlyRemainingOwnedRecords` | تحديث سجل الاسترجاع بعد كل حذف ناجح حتى لا يعيد الانقطاع سجلات تم تنظيفها |
| PurchaseCleanupTest | `recoversSparsePurchaseListThroughVerifiedDetails` | استعادة مستند المشتريات من قائمة مختصرة لا تعرض الملاحظات أو المخزن مع حماية المستندات الأخرى |
| PurchaseCleanupTest | `receiptLookupUsesDetailsAndPreservesUnrelatedReceipts` | التحقق من مورد سند القبض عبر تفاصيله عندما تخفي القائمة هوية المتعامل دون تعديل أي سند |
| PurchaseCleanupTest | `retriesRolledBackDeadlockWithoutDuplicateFixture` | إعادة محاولة إنشاء بيانات الاختبار بعد إلغاء المعاملة بسبب تعارض أقفال دون تكرار السجل |
| PurchaseCleanupTest | `boundsRolledBackDeadlockRetries` | تحديد إعادة محاولة تعارض الأقفال بثلاث محاولات مع إبقاء بيانات الاستعادة |
| PurchaseCleanupTest | `customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts` | فشل قراءة المورد لا يبرر حذف عميل موجود أو حساباته |
| PurchaseCleanupTest | `recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts` | استعادة حساب ضاعت استجابته من شجرة الحسابات دون حذف حسابات أخرى |
| PurchaseCleanupTest | `waitsForOriginalBranchHydrationBeforeFixtureAccess` | انتظار تحميل الفرع الأصلي قبل الوصول إلى بيانات الاختبار |
| PurchaseCleanupTest | `pageFixturesAreSharedButCaseBalancesStayIsolated` | مشاركة بيانات الصفحة الثابتة مع عزل الأصناف والموردين والأرصدة بين الحالات |
| PurchaseCleanupTest | `pageTransitionRetainsAndRecoversThePreviousFailedSave` | الانتقال للحالة التالية يحتفظ بمحاولة الحفظ الفاشلة السابقة ويستعيدها |
| PurchaseCleanupTest | `negativeSaveDetectsACommitBehindFailedResponse` | اكتشاف حفظ فعلي خلف استجابة فاشلة في اختبار رفض الحفظ |
| PurchaseCleanupTest | `recoversSecondReturnWithoutConfusingKnownFirstReturn` | استعادة المرتجع الثاني دون الخلط بينه وبين المرتجع الأول المعروف |
| PurchaseCleanupTest | `recoversLostResponseForEachSalesDocumentKind` | استعادة مستند محفوظ ضاعت استجابته لكل نوع من مستندات المشتريات |
| PurchaseCleanupTest | `refusesJournalRecoveryInAnotherTenant` | رفض استعادة سجل التنظيف في تننت تختلف عن تننت إنشاء البيانات |
| PurchaseCleanupTest | `refusesJournalRecoveryInAnotherBranch` | رفض استعادة سجل التنظيف في فرع مختلف |
| PurchaseCleanupTest | `linkedReturnIsDeletedBeforeSourceInvoice` | حذف المرتجع المرتبط قبل الفاتورة الأصلية |
| PurchaseCleanupTest | `paidInvoiceIsDeletedBeforePaymentMethodAndBank` | حذف الفاتورة المدفوعة قبل وسيلة الدفع والبنك |
| PurchaseCleanupTest | `deletesOwnedMovementBeforeItsItemsAndStores` | حذف الحركة المملوكة للاختبار قبل أصنافها ومخازنها |
| PurchaseCleanupTest | `recoversStoreCommittedBeforeLostCreateResponse` | استعادة مخزن حُفظ قبل فقد استجابة الإنشاء |
| PurchaseCleanupTest | `uncertainStoreLookupFailurePreservesItsAccounts` | فشل التأكد من المخزن يحتفظ بحساباته |
| PurchaseCleanupTest | `uncertainStoreMustStillReferenceItsOwnedAccount` | التحقق من استمرار ارتباط المخزن غير المؤكد بحسابه المملوك للاختبار |
| PurchaseCleanupTest | `resumesPendingStoreCreationFromDurableJournal` | استئناف إنشاء مخزن معلق من سجل التنظيف المحفوظ |
| PurchaseCleanupTest | `retainsStockDependenciesWhenMovementDeletionFails` | الاحتفاظ بتبعيات المخزون عند فشل حذف الحركة |
| PurchaseCleanupTest | `refusesTrackingUnownedRecord` | رفض تسجيل سجل غير مملوك للاختبار ضمن بيانات التنظيف |
| PurchaseCleanupTest | `checksIdentityBeforeFirstDeletion` | التحقق من هوية البيانات قبل أول عملية حذف |
| PurchaseCleanupTest | `cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems` | تنظيف مستند بلا ملاحظات بعد التحقق من ملكية مخزنه وأصنافه |
| PurchaseCleanupTest | `rejectsSalesDocumentWithChangedStoreIdentity` | رفض حذف مستند تغيرت هوية مخزنه |
| PurchaseCleanupTest | `rejectsSalesDocumentWithChangedItemIdentity` | رفض حذف مستند تغيرت هوية صنفه |
| PurchaseCleanupTest | `recoversWarehouseCreateCommittedBeforeLostResponse` | استعادة إذن مخزني حُفظ قبل فقد استجابة الإنشاء |
| PurchaseCleanupTest | `uncertainWarehouseLookupFailureRetainsAllParents` | فشل البحث عن إذن مخزني يحتفظ بجميع تبعياته |
| PurchaseCleanupTest | `recoversUpdatedNoteAfterLostUpdateResponse` | استعادة الملاحظات المعدلة بعد فقد استجابة التعديل |
| PurchaseCleanupTest | `acceptsMissingDeleteOnlyAfterReadConfirmsAbsence` | قبول استجابة عدم وجود السجل عند الحذف بعد تأكيد غيابه بالقراءة |
| PurchaseCleanupTest | `rejectsMissingDeleteWhenOwnedRecordStillExists` | رفض اعتبار السجل محذوفاً عندما تؤكد القراءة وجوده |
| PurchaseCleanupTest | `recoversUiItemCommittedBeforeFailedResponse` | استعادة صنف أُنشئ من الواجهة رغم فشل الاستجابة |
| PurchaseCleanupTest | `recoversInvalidItemByUniqueEnglishMarker` | استعادة صنف غير صالح بعلامة الاختبار الإنجليزية الفريدة |
| PurchaseCleanupTest | `removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges` | إزالة ارتباط الفرع الخاص بالاختبار فقط مع حفظ التغييرات المتزامنة |
| PurchaseCleanupTest | `retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved` | الاحتفاظ بالمخزن وحساباته عند تعذر إزالة ارتباطه المملوك للاختبار |
| PurchaseCleanupTest | `verifiesCommittedBranchUnlinkAfterLostResponse` | تأكيد فك ارتباط الفرع المنفذ بعد فقد الاستجابة |
| PurchaseCleanupTest | `retriesUncommittedDeleteBeforeParentAccounts` | إعادة محاولة الحذف غير المنفذ قبل حذف الحسابات التابعة |
| PurchaseCleanupTest | `doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted` | عدم تكرار الحذف عندما نُفذ الطلب الذي انتهت مهلته |
| PurchaseCleanupTest | `acceptsEmptyStringReadAfterCommittedDeleteTimeout` | قبول قراءة نص فارغ بعد تأكيد تنفيذ الحذف المنتهية مهلته |
| PurchaseCleanupTest | `retainsBlockedParentsAndRetriesOnlyItsOwnRecords` | الاحتفاظ بالتبعيات المعطلة وإعادة محاولة تنظيف سجلات الاختبار فقط |
| PurchaseCleanupTest | `cleansOwnedItemCommittedBeforeCreateFailure` | تنظيف صنف الاختبار الذي حُفظ رغم فشل طلب الإنشاء |
| PurchaseCleanupTest | `acceptsVerifiedMissingMovementDefaultDto` | قبول نموذج حركة افتراضي بعد التحقق من غياب الحركة |
| PurchaseCleanupTest | `acceptsVerifiedMissingMasterDefaultDto` | قبول نموذج بيانات أساسية افتراضي بعد التحقق من غياب السجل |
| PurchaseCleanupTest | `updatedRecordDeletesBeforeItsNewReference` | حذف السجل المعدل قبل المرجع الجديد الذي أصبح مرتبطاً به |
| PurchaseDiscoveryTest | `inspectPurchaseScreens` | فحص شاشات المشتريات وحقولها دون إنشاء أو تغيير أي بيانات |
| PurchaseFormTest | `savesOwnedItemFromPicker` | حفظ مستند مشتريات بصنف الاختبار من القائمة |
| PurchaseFormTest | `savesOwnedItemByBarcode` | حفظ مستند مشتريات بإضافة صنف الاختبار بالباركود |
| PurchaseFormTest | `savesOwnedItemByName` | حفظ مستند مشتريات بإضافة صنف الاختبار بالاسم |
| PurchaseFormTest | `cashDocumentUsesOwnedPayment` | حفظ مستند نقدي باستخدام وسيلة دفع أنشأها الاختبار |
| PurchaseFormTest | `fractionalQuantityPersists` | حفظ كمية كسرية والتحقق من بياناتها وأثرها على المخزون |
| PurchaseFormTest | `twoOwnedItemsPersist` | حفظ صنفين مستقلين والتحقق من البنود والمخزون |
| PurchaseFormTest | `removedItemIsNotSaved` | إزالة بند قبل الحفظ والتحقق من عدم تخزينه |
| PurchaseFormTest | `unicodeNotesPersist` | حفظ الملاحظات العربية والإنجليزية واسترجاعها كما هي |
| PurchaseFormTest | `emptyDocumentCannotSave` | منع حفظ مستند مشتريات بدون أصناف |
| PurchaseFormTest | `zeroQuantityCannotSave` | منع حفظ بند بكمية صفر |
| PurchaseFormTest | `negativeQuantityCannotSave` | منع حفظ بند بكمية سالبة |
| PurchaseFormTest | `missingSupplierCannotSave` | التحقق من إلزامية المورد ومنع الحفظ بدونه |
| PurchaseFormTest | `missingStoreCannotSave` | التحقق من إلزامية المخزن ومنع الحفظ بدونه |
| PurchaseFormTest | `negativePriceCannotSave` | منع حفظ مستند بسعر شراء سالب |
| PurchaseFormTest | `newClearsUnsavedLines` | زر جديد يمسح بنود المستند غير المحفوظة |
| PurchaseFormTest | `savedDocumentReopensWithOwnedValues` | إعادة فتح المستند والتحقق من المورد والمخزن والصنف والكمية |
| PurchaseFormTest | `savedDocumentCanBeEdited` | تعديل كمية مستند الاختبار والتحقق من الحفظ وأثر المخزون |
| PurchaseFormTest | `deletesOnlyOwnedDocument` | حذف مستند الاختبار فقط والتحقق من عكس حركة المخزون |
| PurchaseFormTest | `savesAndPrintsOwnedDocument` | حفظ مستند الاختبار وفتح تقرير الطباعة والتحقق من الصنف |
| PurchaseInvoiceCoverageTest | `percentageDiscountRecalculates` | إعادة حساب المستند بعد خصم عشرة بالمائة |
| PurchaseInvoiceCoverageTest | `fixedDiscountRecalculates` | إعادة حساب المستند بعد خصم بالقيمة |
| PurchaseLinkedCoverageTest | `linkedReturnKeepsOriginalQuantity` | تحويل فاتورة الاختبار إلى مرتجع مرتبط يمنع تسجيل كمية مختلفة عن المصدر |
| PurchaseLinkedCoverageTest | `ownedOrderConvertsToPurchase` | تحويل أمر الشراء إلى فاتورة دون تكرار أثر المخزون |
| PurchaseListCoverageTest | `supplierFilterIsOwned` | فلتر المورد يعرض مستندات مورد الاختبار فقط |
| PurchaseListCoverageTest | `exactCodeFilter` | البحث بالكود الكامل يعرض الفاتورة المطابقة فقط |
| PurchaseListCoverageTest | `referenceFilter` | البحث بالمرجع يعرض فاتورة الاختبار المطابقة فقط |
| PurchaseListCoverageTest | `unmatchedCodeClearsRows` | البحث بكود غير موجود يمسح النتائج السابقة |
| PurchaseListCoverageTest | `clearCodeRestoresSupplierScope` | مسح فلتر الكود يعيد جميع مستندات مورد الاختبار |
| PurchaseListCoverageTest | `pageSizePreservesResults` | تغيير حجم الصفحة يحافظ على فواتير مورد الاختبار لكل حجم مدعوم |
| PurchaseListCoverageTest | `sortKeepsOwnedDocuments` | الترتيب تصاعدياً وتنازلياً يحافظ على مستندات مورد الاختبار |
| PurchaseListCoverageTest | `conflictingFiltersAreEmpty` | دمج فلاتر الكود والمرجع المتعارضة يعرض قائمة فارغة |
| PurchaseListCoverageTest | `dateRangeDoesNotLeaveStaleRows` | نطاق تاريخ يوم واحد يعرض فاتورة الاختبار ثم نطاق خال يمسحها |
| PurchaseListCoverageTest | `exportsPdf` | تصدير فواتير مورد الاختبار إلى ملف PDF فعلي |
| PurchaseListCoverageTest | `exportsExcel` | تصدير فواتير مورد الاختبار إلى ملف Excel فعلي |
| PurchaseOrderCoverageTest | `cashOrderCreatesReceiptWithoutStockMovement` | أمر الشراء النقدي ينشئ سند قبض ولا يغير المخزون |
| PurchaseReturnCoverageTest | `percentageDiscountRecalculates` | إعادة حساب المستند بعد خصم عشرة بالمائة |
| PurchaseReturnCoverageTest | `fixedDiscountRecalculates` | إعادة حساب المستند بعد خصم بالقيمة |
| PurchaseSmokeTest | `savesOwnedCreditPurchase` | حفظ فاتورة مشتريات آجلة ببيانات مستقلة والتحقق من زيادة المخزون ورصيد المورد |
| PurchesBillsTest | `PurchaseBillsTest` | إنشاء فاتورة مشتريات وطباعة أصناف الاختبار |
| PurchesBillsTest | `AddPurchesBillsByBarcodTest` | إنشاء فاتورة مشتريات بالباركود |
| PurchesBillsTest | `AddPurchesBillsByNameTest` | إنشاء فاتورة مشتريات بالاسم |
| PurchesBillsTest | `AddPurchesBillsByPriceWithCashPayTest` | إنشاء فاتورة مشتريات نقدية |
| PurchesBillsTest | `AddPurchesBillsByPriceWithOtherPayTest` | إنشاء فاتورة مشتريات بوسيلة دفع أخرى مملوكة للاختبار |
| PurchesBillsTest | `AddPurchesBillsWithSaveBuutonTest` | حفظ فاتورة مشتريات بأصناف الاختبار |
| PurchesBillsTest | `DeleteAll` | حذف فاتورة الاختبار فقط وعكس المخزون |
| PurchesBillsTest | `AddWithOutSupplier` | منع فاتورة مشتريات بدون مورد |
| PurchesBillsTest | `AddWithOutProduct` | منع فاتورة مشتريات بدون أصناف |
| PurchesBillsTest | `TransToReturnPurchesOrder` | تحويل فاتورة الاختبار إلى مرتجع مرتبط بكميات مقفولة |
| purchOrderTest | `purchOrderTest` | إنشاء أمر شراء بأصناف الاختبار دون تأثير على المخزون |
| purchOrderTest | `AddPurchesOrderByBarcodTest` | إنشاء أمر شراء بإضافة الصنف بالباركود |
| purchOrderTest | `AddPurchesOrderByNameTest` | إنشاء أمر شراء بإضافة الصنف بالاسم |
| purchOrderTest | `DeleteAll` | حذف أمر شراء الاختبار فقط |
| purchOrderTest | `AddWithOutSupplier` | منع أمر شراء بدون مورد |
| purchOrderTest | `AddWithOutProduct` | منع أمر شراء بدون أصناف |
| purchOrderTest | `TransToPurchaseBill` | تحويل أمر شراء الاختبار إلى فاتورة والتحقق من المخزون |
| ReturnOrderTest | `AddReturnOrderTest` | إنشاء مرتجع مشتريات حر بأصناف الاختبار |
| ReturnOrderTest | `AddReturnOrderTestWithDiscount` | إنشاء مرتجع مشتريات بخصم بالقيمة |
| ReturnOrderTest | `AddPurchesBillsByBarcodTest` | إنشاء مرتجع مشتريات بالباركود |
| ReturnOrderTest | `AddPurchesBillsByNameTest` | إنشاء مرتجع مشتريات بالاسم |
| ReturnOrderTest | `AddPurchesBillsWithSaveBuutonTest` | حفظ مرتجع مشتريات بأصناف الاختبار |
| ReturnOrderTest | `DeleteReturnOrderTest` | حذف مرتجع الاختبار فقط وعكس حركة المخزون |
| ReturnOrderTest | `AddWithOutSupplier` | منع مرتجع مشتريات بدون مورد |
| ReturnOrderTest | `AddWithOutProduct` | منع مرتجع مشتريات بدون أصناف |
