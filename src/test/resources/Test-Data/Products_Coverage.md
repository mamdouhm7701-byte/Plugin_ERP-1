# تغطية اختبارات الأصناف

كل حالة تنشئ بياناتها، وتستخدم معرفاتها، ثم تحذفها بترتيب العلاقات. لا يوجد اعتماد على تشغيل حالة قبل أخرى أو على تننت بعينها.

## AddProductsTest

44 حالة.

| الحالة | السيناريو |
|---|---|
| `addNewProductWithValidData` | إضافة صنف بالمخزن ووحدة فرعية |
| `EditProduct` | تعديل صنف الاختبار |
| `AddComponent` | إضافة مكون مستقل للصنف |
| `DeleteProduct` | حذف صنف الاختبار |
| `AddProductWithOutBarcode` | الباركود الأساسي مطلوب |
| `AddProductWithOutCategory` | الفئة مطلوبة |
| `AddProductWithOutUnit` | الوحدة الأساسية مطلوبة |
| `AddProductWithOutStock` | مخزن واحد على الأقل مطلوب |
| `AddProductWithSameUnit` | رفض الوحدة الأساسية كوحدة فرعية |
| `AddProductWithNoTransUnit` | معامل التحويل مطلوب |
| `AddTransWithoutBarcode` | باركود الوحدة الفرعية مطلوب |
| `PR_RequiredArabic` | اسم الصنف العربي مطلوب |
| `PR_SimpleProduct` | حفظ صنف بوحدة أساسية فقط |
| `PR_SubunitRateOne` | رفض معامل تحويل يساوي واحد |
| `PR_SubunitNegativeRate` | رفض معامل تحويل سالب |
| `PR_DuplicateSubunit` | رفض تكرار الوحدة الفرعية |
| `PR_DuplicateStore` | رفض تكرار المخزن |
| `PR_NegativeOpening` | رفض رصيد افتتاحي سالب |
| `PR_NegativeMinimum` | رفض الحد الأدنى السالب |
| `PR_NegativeCost` | رفض تكلفة سالبة |
| `PR_ControlFlags` | حفظ صلاحيات استخدام الصنف |
| `PR_StockBalances` | حفظ رصيد المخزن وتكلفته |
| `PR_CancelNew` | إغلاق دون حفظ |
| `PR_CancelEdit` | إلغاء التعديل دون حفظ |
| `PR_CancelDelete` | إلغاء حذف سجل الاختبار |
| `PR_SearchAndReset` | البحث وعدم وجود نتائج وإعادة البحث |
| `PR_ListStructure` | أعمدة القائمة وسجل الاختبار |
| `PR_ExportPdf` | تصدير PDF صالح |
| `PR_ExportExcel` | تصدير Excel صالح |
| `PR_OptionalEnglish` | حفظ دون الاسم الإنجليزي الاختياري |
| `PR_NegativeSalePrice` | رفض سعر بيع سالب |
| `PR_MinimumAboveSalePrice` | رفض حد أدنى أعلى من سعر البيع |
| `PR_MaximumBelowSalePrice` | رفض حد أقصى أقل من سعر البيع |
| `PR_MinimumAboveMaximum` | رفض حد أدنى أكبر من الحد الأقصى |
| `PR_SalePriceBelowMinimum` | رفض سعر بيع أقل من الحد الأدنى |
| `PR_SalePriceAboveMaximum` | رفض سعر بيع أكبر من الحد الأقصى |
| `PR_MaximumBelowMinimum` | رفض حد أقصى أقل من الحد الأدنى |
| `PR_AllPricesEqual` | قبول تساوي أسعار البيع الثلاثة مع الكسور |
| `PR_SalePriceEqualsMinimum` | قبول سعر البيع عند الحد الأدنى |
| `PR_SalePriceEqualsMaximum` | قبول سعر البيع عند الحد الأقصى |
| `PR_DuplicateMainBarcode` | التحقق من تكرار باركود صنف مملوك للاختبار |
| `PR_UpdatePrices` | تعديل أسعار صنف الاختبار مع الاحتفاظ بالكسور |
| `PR_AddSecondStore` | ربط الصنف بمخزنين مستقلين |
| `PR_ArabicProductName` | حفظ اسم عربي للصنف مع بيانات الاختبار الفريدة |

## ProductCategoriesTest

14 حالة.

| الحالة | السيناريو |
|---|---|
| `AddProductCategory` | إضافة فئة رئيسية |
| `AddBranchProduct` | إضافة فئة فرعية بربط مستقل |
| `EditBranchProduct` | تعديل فئة فرعية مستقلة |
| `DeleteBranchProduct` | حذف فئة الاختبار فقط |
| `AddProductCategoryWithoutArabicName` | اسم الفئة العربي مطلوب |
| `PR_CategoryFlags` | حفظ خصائص فئة المطعم ونقاط البيع |
| `PR_CancelNew` | إغلاق دون حفظ |
| `PR_CancelEdit` | إلغاء التعديل دون حفظ |
| `PR_CancelDelete` | إلغاء حذف سجل الاختبار |
| `PR_SearchAndReset` | البحث وعدم وجود نتائج وإعادة البحث |
| `PR_ListStructure` | أعمدة القائمة وسجل الاختبار |
| `PR_ExportPdf` | تصدير PDF صالح |
| `PR_ExportExcel` | تصدير Excel صالح |
| `PR_OptionalEnglish` | حفظ دون الاسم الإنجليزي الاختياري |

## ProductListTest

15 حالة.

| الحالة | السيناريو |
|---|---|
| `PR_DuplicatePriceListItem` | رفض تكرار نفس الصنف والوحدة في قائمة الأسعار |
| `AddNewProductList` | إضافة قائمة بأسعار أصناف الاختبار والطباعة |
| `EditPriceList` | تعديل وطباعة قائمة الاختبار |
| `DeletePriceList` | حذف قائمة الاختبار |
| `AssertErorrMsg` | اسم القائمة العربي مطلوب |
| `PR_SaveWithoutPrint` | حفظ قائمة الأسعار دون طباعة |
| `PR_DecimalPrice` | الاحتفاظ بكسور السعر |
| `PR_CancelNew` | إغلاق دون حفظ |
| `PR_CancelEdit` | إلغاء التعديل دون حفظ |
| `PR_CancelDelete` | إلغاء حذف سجل الاختبار |
| `PR_SearchAndReset` | البحث وعدم وجود نتائج وإعادة البحث |
| `PR_ListStructure` | أعمدة القائمة وسجل الاختبار |
| `PR_ExportPdf` | تصدير PDF صالح |
| `PR_ExportExcel` | تصدير Excel صالح |
| `PR_OptionalEnglish` | حفظ دون الاسم الإنجليزي الاختياري |

## ProductsCleanupTest

15 حالة.

| الحالة | السيناريو |
|---|---|
| `acceptsMissingDeleteOnlyAfterReadConfirmsAbsence` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `rejectsMissingDeleteWhenOwnedRecordStillExists` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `recoversUiItemCommittedBeforeFailedResponse` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `recoversInvalidItemByUniqueEnglishMarker` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `verifiesCommittedBranchUnlinkAfterLostResponse` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `retriesUncommittedDeleteBeforeParentAccounts` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `acceptsEmptyStringReadAfterCommittedDeleteTimeout` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `retainsBlockedParentsAndRetriesOnlyItsOwnRecords` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `cleansOwnedItemCommittedBeforeCreateFailure` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `acceptsVerifiedMissingProductDefaultDto` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `acceptsVerifiedMissingMasterDefaultDto` | سلامة التنظيف وترتيب العلاقات وردود الخادم |
| `updatedRecordDeletesBeforeItsNewReference` | سلامة التنظيف وترتيب العلاقات وردود الخادم |

## UnitTest

12 حالة.

| الحالة | السيناريو |
|---|---|
| `AddNewUnite` | إضافة وحدة مستقلة |
| `EditUnite` | تعديل وحدة الاختبار |
| `DeleteUnite` | حذف وحدة الاختبار فقط |
| `AddUniteWithoutArabicName` | الاسم العربي مطلوب |
| `PR_CancelNew` | إغلاق دون حفظ |
| `PR_CancelEdit` | إلغاء التعديل دون حفظ |
| `PR_CancelDelete` | إلغاء حذف سجل الاختبار |
| `PR_SearchAndReset` | البحث وعدم وجود نتائج وإعادة البحث |
| `PR_ListStructure` | أعمدة القائمة وسجل الاختبار |
| `PR_ExportPdf` | تصدير PDF صالح |
| `PR_ExportExcel` | تصدير Excel صالح |
| `PR_OptionalEnglish` | حفظ دون الاسم الإنجليزي الاختياري |

الإجمالي: **100 حالة**. إدراج الحالة لا يعني نجاح تشغيلها؛ النتائج الفعلية موجودة في تقرير التشغيل.
