# حصر مسؤوليات الصفحات والمكونات — 24 أكتوبر 2026

يشمل الحصر ملفات Java داخل `System_Modules/Login` بعد فصل المسؤوليات. حالة كل ملف موضحة أدناه. هذا حصر مسؤوليات ومراجعة مصدر، وليس ادعاء تشغيل كل ميثود حيًا. الاستثناءات الوظيفية السابقة باقية: الإحصائيات والتقارير المستقلة والاشتراك وترحيل الفواتير للهيئة والقيود المجمعة وإعدادات الشركة والفروع. قراءة طباعة مستند داخل اختبار الحفظ جزء من التحقق الحالي وليست إضافة لتغطية التقارير المستقلة.

| الملف | الأسطر | الميثودات ذات جسم | الأب | المسؤولية |
|---|---:|---:|---|---|
| `Abstract.java` | 297 | 47 | `` | سياق الصفحة وتفويض الحقول والتنقل والسجلات والطباعة والاختيارات والتنبيهات |
| `AbstractForBills.java` | 9 | 1 | `DocumentPage` | واجهة توافق رفيعة فوق DocumentPage |
| `AbstractForDealer.java` | 388 | 67 | `Abstract` | حقول المتعامل وتنسيق مكونات الفورم والقائمة وإجراءات السجل |
| `AbstractForPurchases.java` | 8 | 1 | `DocumentPage` | واجهة توافق رفيعة فوق DocumentPage |
| `AbstractForSales.java` | 198 | 26 | `DocumentPage` | تخصص المبيعات وتفويض قراءة صفوفها وإجمالياتها وفورمها |
| `AbstractForStore.java` | 8 | 1 | `DocumentPage` | واجهة توافق رفيعة فوق DocumentPage |
| `BasicData/AreaGridControls.java` | 213 | 21 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/AreaPage.java` | 292 | 39 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: AreaRecordActions |
| `BasicData/AreaRecordActions.java` | 171 | 13 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/BankPage.java` | 265 | 35 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: BankRecords |
| `BasicData/BankRecords.java` | 200 | 19 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/CashFormReader.java` | 138 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/CashListControls.java` | 302 | 19 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/CashPage.java` | 386 | 48 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: CashFormReader, CashRecordActions |
| `BasicData/CashRecordActions.java` | 185 | 13 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/DistributionEntityPage.java` | 216 | 29 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: DistributionRecords |
| `BasicData/DistributionRecords.java` | 235 | 19 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/ExpenseRecords.java` | 196 | 17 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/ExpensesPage.java` | 244 | 31 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: ExpenseRecords |
| `BasicData/GridProviderWait.java` | 39 | 2 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/MainBage.java` | 25 | 1 | `` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `BasicData/PaymentPage.java` | 224 | 29 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: PaymentRecords |
| `BasicData/PaymentRecords.java` | 153 | 13 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/RevenueRecords.java` | 150 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/RevenuesPage.java` | 216 | 28 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: RevenueRecords |
| `BasicData/RouteRecords.java` | 150 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `BasicData/RoutLine.java` | 258 | 30 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: RouteRecords |
| `BasicData/StorePage.java` | 201 | 26 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: WarehouseRecords |
| `BasicData/TreasuryHandoverPage.java` | 111 | 9 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `BasicData/TypesofdiscountsPage.java` | 212 | 17 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `BasicData/WarehouseRecords.java` | 132 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Coverage/ScreenPage.java` | 181 | 35 | `` | عناصر شاشة ضمن التغطية الحالية؛ السيناريو والبيانات خارج الصفحة |
| `DealerFormControls.java` | 152 | 15 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DealerListQueries.java` | 100 | 7 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DealerModel/Agent.java` | 184 | 16 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `DealerModel/CustomerClassification.java` | 48 | 3 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `DealerModel/Customers.java` | 95 | 10 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `DealerModel/MainDealer.java` | 149 | 23 | `AbstractForDealer` | صفحة منسقة؛ التنفيذ المفصول: MainDealerRecords |
| `DealerModel/MainDealerRecords.java` | 203 | 15 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DealerModel/RoutLineForDealer.java` | 247 | 34 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `DealerModel/Supplier.java` | 66 | 6 | `AbstractForDealer` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `DealerRecordActions.java` | 158 | 14 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentColumnEditing.java` | 77 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentGridEditing.java` | 63 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentItemControls.java` | 92 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentPage.java` | 357 | 57 | `Abstract` | تنسيق قدرات المستند المشتركة؛ لا يرث من موديول آخر |
| `DocumentPaymentControls.java` | 60 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentPrintChecks.java` | 82 | 4 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentQuantityPrintCheck.java` | 77 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `DocumentSummaryReader.java` | 55 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `FinancialMovements/CustomerDiscountNoticePage.java` | 140 | 14 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `FinancialMovements/CustomerReceiptPage.java` | 76 | 8 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `FinancialMovements/ExpenseMovementPage.java` | 137 | 13 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `FinancialMovements/FinancialMovementKind.java` | 25 | 2 | `` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `FinancialMovements/FinancialMovementPage.java` | 299 | 53 | `Abstract` | صفحة منسقة؛ التنفيذ المفصول: FinancialRecordList, FinancialSaveCapture |
| `FinancialMovements/FinancialRecordList.java` | 76 | 16 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `FinancialMovements/FinancialSaveCapture.java` | 58 | 7 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `FinancialMovements/OtherRevenueMovementPage.java` | 69 | 7 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `FinancialMovements/SupplierBillOfExchangePage.java` | 84 | 9 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `FinancialMovements/SupplierDiscountNoticePage.java` | 139 | 14 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `FinancialMovements/TransfersAndSettlementsPage.java` | 91 | 6 | `FinancialMovementPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `GenralAccountant/VoucherAccountPicker.java` | 131 | 13 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `GenralAccountant/VoucherLines.java` | 222 | 28 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `GenralAccountant/VoucherRecordList.java` | 96 | 7 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `GenralAccountant/VouchersPage.java` | 352 | 59 | `Abstract` | صفحة منسقة؛ التنفيذ المفصول: VoucherLines, VoucherRecordList |
| `Invoice/Invoice.java` | 29 | 1 | `SalesModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `Invoice/InvoiceListCalendar.java` | 125 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/InvoiceListExport.java` | 61 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/InvoiceListFilters.java` | 350 | 27 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/InvoiceListGrid.java` | 240 | 21 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/InvoiceListPagination.java` | 205 | 20 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/InvoicListPage.java` | 243 | 41 | `SalesModulePage` | صفحة منسقة؛ التنفيذ المفصول: InvoiceListFilters, InvoiceListGrid |
| `Invoice/QuotationPage.java` | 9 | 1 | `SalesModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `Invoice/SalesChoiceControls.java` | 92 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/SalesDocumentForm.java` | 145 | 16 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/SalesDocumentLines.java` | 218 | 11 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/SalesDocumentList.java` | 120 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/SalesItemSelection.java` | 165 | 14 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/SalesModulePage.java` | 392 | 74 | `AbstractForSales` | صفحة منسقة؛ التنفيذ المفصول: SalesChoiceControls, SalesDocumentForm, SalesPrintWindows, SalesSaveCapture |
| `Invoice/SalesOrderPage.java` | 9 | 1 | `SalesModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `Invoice/SalesPrintWindows.java` | 57 | 5 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Invoice/SalesReturnPage.java` | 9 | 1 | `SalesModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `Invoice/SalesSaveCapture.java` | 65 | 9 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `InvoiceBusinessValidation.java` | 143 | 9 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Login/LoginPage.java` | 84 | 6 | `` | تسجيل الدخول والتحقق من جاهزية الجلسة |
| `PageContext.java` | 11 | 1 | `` | اعتمادات جلسة واحدة؛ لا حالة مشتركة بين الصفحات |
| `PageFeedback.java` | 77 | 10 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `PageFormFields.java` | 83 | 11 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `PageNavigation.java` | 46 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `PageRecordActions.java` | 78 | 8 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `PageSelections.java` | 84 | 12 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `PrintedDocumentControls.java` | 60 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Products/AddProductsPage.java` | 270 | 36 | `ProductsModulePage` | صفحة منسقة؛ التنفيذ المفصول: ProductComponents, ProductUnits |
| `Products/ProductCategories.java` | 46 | 4 | `ProductsModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `Products/ProductComponents.java` | 62 | 9 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Products/ProductListPage.java` | 134 | 15 | `ProductsModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `Products/ProductRecordList.java` | 57 | 11 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Products/ProductSaveCapture.java` | 50 | 7 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Products/ProductsModulePage.java` | 180 | 52 | `Abstract` | صفحة منسقة؛ التنفيذ المفصول: ProductRecordList, ProductSaveCapture |
| `Products/ProductUnits.java` | 58 | 8 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `Products/UnitePage.java` | 35 | 3 | `ProductsModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `PurchaseInvoiceRows.java` | 76 | 2 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `PurchaseInvoiceSnapshot.java` | 86 | 2 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseCalendar.java` | 171 | 5 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseChoiceControls.java` | 95 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseDocumentForm.java` | 164 | 18 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseDocumentLines.java` | 221 | 11 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseDocumentList.java` | 178 | 16 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseDocumentTransitions.java` | 79 | 4 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseItemSelection.java` | 165 | 14 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseModulePage.java` | 441 | 84 | `AbstractForPurchases` | صفحة منسقة؛ التنفيذ المفصول: PurchaseChoiceControls, PurchaseDocumentForm, PurchaseDocumentTransitions, PurchasePrintWindows, PurchaseSaveCapture |
| `purches/PurchasePrintWindows.java` | 57 | 5 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchaseSaveCapture.java` | 84 | 10 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `purches/PurchesBillsPage.java` | 32 | 2 | `AbstractForPurchases` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `purches/purchOrderPage.java` | 215 | 7 | `AbstractForPurchases` | عناصر وقراءات خاصة بالشاشة أو مكون قائم؛ لم يستدع الحصر فصلًا إضافيًا مستقلًا |
| `purches/ReturnOrderPage.java` | 168 | 10 | `AbstractForPurchases` | صفحة منسقة؛ التنفيذ المفصول: ReturnOrderRows |
| `purches/ReturnOrderRows.java` | 141 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesDropdownControls.java` | 90 | 5 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesFormControls.java` | 123 | 11 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesInvoiceRows.java` | 83 | 2 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesInvoiceSnapshot.java` | 136 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesItemDialog.java` | 69 | 4 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesPrintedTotals.java` | 53 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SalesSummaryReader.java` | 107 | 3 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SpecialSales/CounterDocumentLines.java` | 109 | 8 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SpecialSales/CounterPaymentControls.java` | 60 | 5 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SpecialSales/CounterSaveCapture.java` | 56 | 8 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `SpecialSales/PointOfSalePage.java` | 99 | 13 | `SpecialSalesPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `SpecialSales/RestaurantInvoicePage.java` | 128 | 14 | `SpecialSalesPage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `SpecialSales/SpecialSalesPage.java` | 345 | 39 | `Abstract` | صفحة منسقة؛ التنفيذ المفصول: CounterDocumentLines, CounterPaymentControls, CounterSaveCapture |
| `StoreModel/AdditionOrderPage.java` | 11 | 1 | `StoreModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `StoreModel/DamageAdjustmentPage.java` | 10 | 1 | `StoreModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `StoreModel/ExchangeOrderPage.java` | 21 | 1 | `StoreModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `StoreModel/ManufacturingPage.java` | 11 | 1 | `StoreModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `StoreModel/StoreDocumentLines.java` | 114 | 7 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `StoreModel/StoreItemSelection.java` | 101 | 7 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `StoreModel/StoreModulePage.java` | 294 | 41 | `AbstractForStore` | صفحة منسقة؛ التنفيذ المفصول: StoreItemSelection, StoreRecordList, StoreSaveCapture |
| `StoreModel/StoreRecordList.java` | 116 | 9 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `StoreModel/StoreSaveCapture.java` | 66 | 9 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `StoreModel/TransBetweenStors.java` | 20 | 2 | `StoreModulePage` | تخصص شاشة فوق منسق المجال؛ احتفظ بالتفاصيل المحلية |
| `StoreProductSearch.java` | 105 | 6 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |
| `StoreQuantityEditor.java` | 122 | 5 | `` | مكوّن داخلي متخصص؛ لا يمثل سيناريو اختبار |

الأعداد محسوبة من المصدر بعد حجب النصوص والتعليقات. بقاء الملف طويلًا بسبب واجهة توافق وتفويض لا يعني أنه يحتفظ بتنفيذ المسؤوليات المنقولة. التفاصيل الدقيقة لكل ميثود في `target/page-refactor-evidence/phase3/method-inventory.json`.
