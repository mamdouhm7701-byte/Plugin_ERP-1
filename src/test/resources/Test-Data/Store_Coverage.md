# تغطية اختبارات المخازن

كل حالة مستقلة ببياناتها وبمعرفات السجلات التي أنشأتها. الحالات المشتركة الموروثة تعمل لكل شاشة على حدة، دون تعطيلها أو تخطيها.

## ExchangeOrderTest

30 حالة.

| الحالة | السيناريو |
| --- | --- |
| `AddExchangeWithItemsList` | إضافة إذن صرف باختيار الأصناف من القائمة |
| `AddExchangeByName` | إضافة إذن صرف عن طريق البحث بالاسم |
| `AddExchangeByBarcode` | إضافة إذن صرف عن طريق البحث بالباركود |
| `EditExchange` | تعديل إذن صرف موجود |
| `DeleteExchange` | حذف سند الاختبار فقط مع التحقق من عكس الرصيد |
| `AssertItemTypeRequired` | التحقق - نوع البند إلزامي في إذن الصرف |
| `AssertCustomerRequired` | التحقق - العميل إلزامي عند اختيار نوع البند (عملاء) |
| `AssertAgentRequired` | التحقق - المندوب إلزامي عند اختيار نوع البند (مندوبين) |
| `AssertDistributionEntityRequired` | التحقق - جهة الصرف إلزامية عند اختيار نوع البند (جهات الصرف) |
| `AssertErrorWhenSavingWithoutProducts` | التحقق - خطأ عند حفظ إذن صرف بدون أصناف |
| `AddOldDate` | التحقق - خطأ عند حفظ إذن الصرف بتاريخ قديم |
| `SM_NotesRoundTrip` | حفظ الملاحظات العربية والإنجليزية كما هي |
| `SM_PrimaryPartyIdentity` | هوية المتعامل المختار دون عميل افتراضي |
| `SM_AgentIdentity` | سند المندوب لا يرتبط بعميل افتراضي |
| `SM_DestinationIdentity` | سند جهة الصرف لا يرتبط بمتعامل افتراضي |
| `SM_CancelCreation` | إلغاء إنشاء سند دون تغيير الرصيد (مشتركة موروثة) |
| `SM_CancelEdit` | إلغاء تعديل السند يحافظ على بياناته ورصيده (مشتركة موروثة) |
| `SM_CancelDelete` | إلغاء حذف سند الاختبار (مشتركة موروثة) |
| `SM_RequiredStore` | المخزن مطلوب (مشتركة موروثة) |
| `SM_ZeroQuantity` | رفض الكمية الصفرية (مشتركة موروثة) |
| `SM_NegativeQuantity` | رفض الكمية السالبة (مشتركة موروثة) |
| `SM_DecimalQuantity` | قبول كمية كسرية والتحقق من أثرها على الرصيد (مشتركة موروثة) |
| `SM_StockBalanceBoundary` | كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم (مشتركة موروثة) |
| `SM_LineCalculations` | حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة (مشتركة موروثة) |
| `SM_RemoveItem` | حذف سطر صنف مملوك للاختبار قبل الحفظ (مشتركة موروثة) |
| `SM_DuplicateItem` | إضافة نفس الصنف لا تنشئ سطرين متطابقين (مشتركة موروثة) |
| `SM_ListStructure` | عرض هيكل قائمة سندات المخازن (مشتركة موروثة) |
| `SM_ExportPdf` | تصدير PDF فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_ExportExcel` | تصدير Excel فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_SearchAndReset` | البحث عن سند الاختبار وتصفير نتيجة بحث غير موجود (مشتركة موروثة) |

## AdditionOrderTest

30 حالة.

| الحالة | السيناريو |
| --- | --- |
| `AdditionOrderTest` | إضافة إذن استلام باختيار الأصناف من القائمة |
| `AddAdditionOrderTestByName` | إضافة إذن استلام عن طريق البحث بالاسم |
| `AddAdditionByBarcode` | إضافة إذن الاستلام عن طريق البحث بالباركود |
| `EditAddition` | تعديل إذن استلام موجود |
| `DeleteAddition` | حذف سند الاختبار فقط مع التحقق من عكس الرصيد |
| `AssertItemTypeRequired` | التحقق - نوع البند إلزامي في إذن الاستلام |
| `AssertCustomerRequired` | التحقق - المورد إلزامي عند اختيار نوع البند (موردين) |
| `AssertAgentRequired` | التحقق - المندوب إلزامي عند اختيار نوع البند (مندوبين) |
| `AssertDistributionEntityRequired` | التحقق - جهة الصرف إلزامية عند اختيار نوع البند (جهات الصرف) |
| `AssertErrorWhenSavingWithoutProducts` | التحقق - خطأ عند حفظ إذن استلام بدون أصناف |
| `AddOldDate` | التحقق - خطأ عند حفظ إذن استلام بتاريخ قديم |
| `SM_NotesRoundTrip` | حفظ الملاحظات العربية والإنجليزية كما هي |
| `SM_PrimaryPartyIdentity` | هوية المتعامل المختار دون عميل افتراضي |
| `SM_AgentIdentity` | سند المندوب لا يرتبط بعميل افتراضي |
| `SM_DestinationIdentity` | سند جهة الصرف لا يرتبط بمتعامل افتراضي |
| `SM_CancelCreation` | إلغاء إنشاء سند دون تغيير الرصيد (مشتركة موروثة) |
| `SM_CancelEdit` | إلغاء تعديل السند يحافظ على بياناته ورصيده (مشتركة موروثة) |
| `SM_CancelDelete` | إلغاء حذف سند الاختبار (مشتركة موروثة) |
| `SM_RequiredStore` | المخزن مطلوب (مشتركة موروثة) |
| `SM_ZeroQuantity` | رفض الكمية الصفرية (مشتركة موروثة) |
| `SM_NegativeQuantity` | رفض الكمية السالبة (مشتركة موروثة) |
| `SM_DecimalQuantity` | قبول كمية كسرية والتحقق من أثرها على الرصيد (مشتركة موروثة) |
| `SM_StockBalanceBoundary` | كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم (مشتركة موروثة) |
| `SM_LineCalculations` | حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة (مشتركة موروثة) |
| `SM_RemoveItem` | حذف سطر صنف مملوك للاختبار قبل الحفظ (مشتركة موروثة) |
| `SM_DuplicateItem` | إضافة نفس الصنف لا تنشئ سطرين متطابقين (مشتركة موروثة) |
| `SM_ListStructure` | عرض هيكل قائمة سندات المخازن (مشتركة موروثة) |
| `SM_ExportPdf` | تصدير PDF فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_ExportExcel` | تصدير Excel فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_SearchAndReset` | البحث عن سند الاختبار وتصفير نتيجة بحث غير موجود (مشتركة موروثة) |

## ManufacturingTest

23 حالة.

| الحالة | السيناريو |
| --- | --- |
| `AddManufacturingTest` | إضافة إذن تصنيع باختيار الأصناف من القائمة |
| `AddManufacturingByBarcodeTest` | إضافة إذن تصنيع باستخدام الباركود |
| `AddManufacturingByNameTest` | إضافة إذن تصنيع باستخدام اسم الصنف |
| `DeleteManufacturingByBarcodeTest` | حذف سند الاختبار فقط مع التحقق من عكس الرصيد |
| `AssertStoreRequired` | التحقق من إلزامية اختيار المخزن |
| `AssertErorrPopUpWhenAddProuctWithOutStore` | التحقق من ظهور رسالة خطأ عند إضافة أصناف بدون اختيار مخزن |
| `AssertStorePopUpWhenAddProuctWithOutBalancee` | التحقق من ظهور رسالة خطأ عند تجاوز الرصيد المتاح |
| `SM_EditManufacturing` | تعديل إذن تصنيع والتحقق من رصيد المكونات |
| `SM_CancelCreation` | إلغاء إنشاء سند دون تغيير الرصيد (مشتركة موروثة) |
| `SM_CancelEdit` | إلغاء تعديل السند يحافظ على بياناته ورصيده (مشتركة موروثة) |
| `SM_CancelDelete` | إلغاء حذف سند الاختبار (مشتركة موروثة) |
| `SM_RequiredStore` | المخزن مطلوب (مشتركة موروثة) |
| `SM_ZeroQuantity` | رفض الكمية الصفرية (مشتركة موروثة) |
| `SM_NegativeQuantity` | رفض الكمية السالبة (مشتركة موروثة) |
| `SM_DecimalQuantity` | قبول كمية كسرية والتحقق من أثرها على الرصيد (مشتركة موروثة) |
| `SM_StockBalanceBoundary` | كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم (مشتركة موروثة) |
| `SM_LineCalculations` | حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة (مشتركة موروثة) |
| `SM_RemoveItem` | حذف سطر صنف مملوك للاختبار قبل الحفظ (مشتركة موروثة) |
| `SM_DuplicateItem` | إضافة نفس الصنف لا تنشئ سطرين متطابقين (مشتركة موروثة) |
| `SM_ListStructure` | عرض هيكل قائمة سندات المخازن (مشتركة موروثة) |
| `SM_ExportPdf` | تصدير PDF فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_ExportExcel` | تصدير Excel فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_SearchAndReset` | البحث عن سند الاختبار وتصفير نتيجة بحث غير موجود (مشتركة موروثة) |

## TransBetweenStoreTest

25 حالة.

| الحالة | السيناريو |
| --- | --- |
| `TransBetweenStoreTest` | إنشاء تحويل بين المخازن باختيار الأصناف من القائمة |
| `TransBetweenStoreAddWWithBarcodeTest` | إنشاء تحويل بين المخازن باستخدام الباركود |
| `TransBetweenStoreAddWithNameTest` | إنشاء تحويل بين المخازن باستخدام اسم الصنف |
| `EditTransBetweenStore` | تعديل عملية تحويل بين المخازن |
| `DeleteAllTest` | حذف سند الاختبار فقط مع التحقق من عكس الرصيد |
| `AssertToStoreRequired` | التحقق من إلزامية اختيار مخزن الوجهة |
| `AssertErorrWhenChosenOneStore` | التحقق من عدم إضافة أصناف قبل اختيار مخزن الوجهة |
| `CantChosenSameStore` | التحقق من عدم اختيار نفس المخزن كمصدر ووجهة |
| `CantSaveWithOutChosenProduct` | التحقق من عدم حفظ التحويل بدون أصناف |
| `SM_NotesRoundTrip` | حفظ الملاحظات العربية والإنجليزية كما هي |
| `SM_CancelCreation` | إلغاء إنشاء سند دون تغيير الرصيد (مشتركة موروثة) |
| `SM_CancelEdit` | إلغاء تعديل السند يحافظ على بياناته ورصيده (مشتركة موروثة) |
| `SM_CancelDelete` | إلغاء حذف سند الاختبار (مشتركة موروثة) |
| `SM_RequiredStore` | المخزن مطلوب (مشتركة موروثة) |
| `SM_ZeroQuantity` | رفض الكمية الصفرية (مشتركة موروثة) |
| `SM_NegativeQuantity` | رفض الكمية السالبة (مشتركة موروثة) |
| `SM_DecimalQuantity` | قبول كمية كسرية والتحقق من أثرها على الرصيد (مشتركة موروثة) |
| `SM_StockBalanceBoundary` | كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم (مشتركة موروثة) |
| `SM_LineCalculations` | حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة (مشتركة موروثة) |
| `SM_RemoveItem` | حذف سطر صنف مملوك للاختبار قبل الحفظ (مشتركة موروثة) |
| `SM_DuplicateItem` | إضافة نفس الصنف لا تنشئ سطرين متطابقين (مشتركة موروثة) |
| `SM_ListStructure` | عرض هيكل قائمة سندات المخازن (مشتركة موروثة) |
| `SM_ExportPdf` | تصدير PDF فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_ExportExcel` | تصدير Excel فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_SearchAndReset` | البحث عن سند الاختبار وتصفير نتيجة بحث غير موجود (مشتركة موروثة) |

## DamageAdjustmentTest

23 حالة.

| الحالة | السيناريو |
| --- | --- |
| `AddDamageAdjustmentTest` | إضافة تسوية توالف باختيار الأصناف من القائمة |
| `AddDamageAdjustmentByNameTest` | إضافة تسوية توالف باستخدام اسم الصنف |
| `AddDamageAdjustmentByBarcodeTest` | إضافة تسوية توالف باستخدام الباركود |
| `EditAddDamageAdjustment` | تعديل تسوية توالف |
| `DeleteDamageAdjustment` | حذف سند الاختبار فقط مع التحقق من عكس الرصيد |
| `AddOldDateTest` | التحقق من عدم السماح بالحفظ بتاريخ قديم |
| `AddProductWithoutQuantityTest` | التحقق من عدم إضافة صنف بدون كمية |
| `SM_NotesRoundTrip` | حفظ الملاحظات العربية والإنجليزية كما هي |
| `SM_CancelCreation` | إلغاء إنشاء سند دون تغيير الرصيد (مشتركة موروثة) |
| `SM_CancelEdit` | إلغاء تعديل السند يحافظ على بياناته ورصيده (مشتركة موروثة) |
| `SM_CancelDelete` | إلغاء حذف سند الاختبار (مشتركة موروثة) |
| `SM_RequiredStore` | المخزن مطلوب (مشتركة موروثة) |
| `SM_ZeroQuantity` | رفض الكمية الصفرية (مشتركة موروثة) |
| `SM_NegativeQuantity` | رفض الكمية السالبة (مشتركة موروثة) |
| `SM_DecimalQuantity` | قبول كمية كسرية والتحقق من أثرها على الرصيد (مشتركة موروثة) |
| `SM_StockBalanceBoundary` | كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم (مشتركة موروثة) |
| `SM_LineCalculations` | حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة (مشتركة موروثة) |
| `SM_RemoveItem` | حذف سطر صنف مملوك للاختبار قبل الحفظ (مشتركة موروثة) |
| `SM_DuplicateItem` | إضافة نفس الصنف لا تنشئ سطرين متطابقين (مشتركة موروثة) |
| `SM_ListStructure` | عرض هيكل قائمة سندات المخازن (مشتركة موروثة) |
| `SM_ExportPdf` | تصدير PDF فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_ExportExcel` | تصدير Excel فعلي لقائمة السندات (مشتركة موروثة) |
| `SM_SearchAndReset` | البحث عن سند الاختبار وتصفير نتيجة بحث غير موجود (مشتركة موروثة) |

## StoreCleanupTest

29 حالة.

| الحالة | السيناريو |
| --- | --- |
| `deletesOwnedMovementBeforeItsItemsAndStores` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `recoversStoreCommittedBeforeLostCreateResponse` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `uncertainStoreLookupFailurePreservesItsAccounts` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `uncertainStoreMustStillReferenceItsOwnedAccount` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `resumesPendingStoreCreationFromDurableJournal` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `retainsStockDependenciesWhenMovementDeletionFails` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `refusesTrackingUnownedRecord` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `checksIdentityBeforeFirstDeletion` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `cleansManufacturingWithoutNotesUsingOwnedStoreAndItems` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `rejectsManufacturingWithChangedStoreIdentity` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `rejectsManufacturingWithChangedItemIdentity` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `recoversWarehouseCreateCommittedBeforeLostResponse` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `uncertainWarehouseLookupFailureRetainsAllParents` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `recoversUpdatedNoteAfterLostUpdateResponse` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `acceptsMissingDeleteOnlyAfterReadConfirmsAbsence` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `rejectsMissingDeleteWhenOwnedRecordStillExists` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `recoversUiItemCommittedBeforeFailedResponse` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `recoversInvalidItemByUniqueEnglishMarker` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `verifiesCommittedBranchUnlinkAfterLostResponse` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `retriesUncommittedDeleteBeforeParentAccounts` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `acceptsEmptyStringReadAfterCommittedDeleteTimeout` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `retainsBlockedParentsAndRetriesOnlyItsOwnRecords` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `cleansOwnedItemCommittedBeforeCreateFailure` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `acceptsVerifiedMissingMovementDefaultDto` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `acceptsVerifiedMissingMasterDefaultDto` | سلامة ملكية البيانات وترتيب حذف العلاقات |
| `updatedRecordDeletesBeforeItsNewReference` | سلامة ملكية البيانات وترتيب حذف العلاقات |

الإجمالي: **160 حالة** = 131 واجهة + 29 تنظيف وعلاقات. الحالات الأصلية الـ45 محفوظة بأسمائها. إدراج الحالة ليس ادعاءً بنجاحها؛ نتائج التشغيل الفعلية منفصلة.
