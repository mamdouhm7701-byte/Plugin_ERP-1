# صفحات ومكونات الحركات المالية

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-405c1db73294"></a>
## CustomerDiscountNoticePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/CustomerDiscountNoticePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/CustomerDiscountNoticePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 140 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public CustomerDiscountNoticePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public By getAllocationInput(int rowIndex)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsAgentRequiredErrorDisplayed()` | public | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsAmountRequiredErrorDisplayed()` | public | السؤال عن حالة المبلغ / الخطأ، حسب المدخلات والتخصص. |
| `public void OpenCustomerDiscountNotice()` | public | فتح العميل / الخصم، حسب المدخلات والتخصص. |
| `public void SaveCustomButton()` | public | حفظ الزر، حسب المدخلات والتخصص. |
| `public void CanselCustomReceipt()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void HandelCustomReceipt(int rowIndex, String Number)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public BigDecimal[] getFinancialValues()` | public | قراءة أو إرجاع القيم، حسب المدخلات والتخصص. |
| `public double getNetBeforeTax()` | public | قراءة أو إرجاع الصافي / الضريبة، حسب المدخلات والتخصص. |
| `public double getTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public double getTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |
| `public double getExpectedTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public double getExpectedTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |

<a id="file-8896b80e2ad1"></a>
## CustomerReceiptPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/CustomerReceiptPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/CustomerReceiptPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 76 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public CustomerReceiptPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public By getAllocationInput(int rowIndex)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenFinancialMovementsPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void SaveCustomButton()` | public | حفظ الزر، حسب المدخلات والتخصص. |
| `public void HandelCustomReceipt(int rowIndex, String Number)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsAgentRequiredErrorDisplayed()` | public | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsAmountRequiredErrorDisplayed()` | public | السؤال عن حالة المبلغ / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsPaymentMethodRequiredErrorDisplayed()` | public | السؤال عن حالة السداد / الخطأ، حسب المدخلات والتخصص. |

<a id="file-b36a74638da1"></a>
## ExpenseMovementPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/ExpenseMovementPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/ExpenseMovementPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 137 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ExpenseMovementPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenExpenseMovementPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void SelectExpenseType()` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SelectAcountType()` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsAmountRequiredErrorDisplayed()` | public | السؤال عن حالة المبلغ / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsTypeRequiredErrorDisplayed()` | public | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsPaymentMethodRequiredErrorDisplayed()` | public | السؤال عن حالة السداد / الخطأ، حسب المدخلات والتخصص. |
| `public BigDecimal[] getFinancialValues()` | public | قراءة أو إرجاع القيم، حسب المدخلات والتخصص. |
| `public double getNetBeforeTax()` | public | قراءة أو إرجاع الصافي / الضريبة، حسب المدخلات والتخصص. |
| `public double getTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public double getTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |
| `public double getExpectedTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public double getExpectedTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |

<a id="file-0a8202d84376"></a>
## FinancialMovementKind.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialMovementKind.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialMovementKind.java)

**دوره:** تعريف أنواع مستندات/حركات المجال وربطها بمساراتها أو خصائص سلوكها.

**حالته:** مصدر نشط · **عدد أسطره:** 25

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

**ملفات تشير إليه مباشرة:** [FinancialWorkflow.java](TEST_FinancialMovementsTest_AR.md#file-736d81307b79)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `FinancialMovementKind(Pages route, String endpoint, String partyLabel)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public boolean discount()` | public | تنفيذ جزء متعلق بـ الخصم، حسب المدخلات والتخصص. |

<a id="file-191723319129"></a>
## FinancialMovementPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialMovementPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialMovementPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 299 · **الأب المباشر:** `Abstract`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [CustomerDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-405c1db73294)، [CustomerReceiptPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-8896b80e2ad1)، [ExpenseMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-b36a74638da1)، [OtherRevenueMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-3b11f1fa1481)، [SupplierBillOfExchangePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-3333dbde6ae0)، [SupplierDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-f3864dce667c)، [TransfersAndSettlementsPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-47e7576bf489)، [FinancialWorkflow.java](TEST_FinancialMovementsTest_AR.md#file-736d81307b79)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public FinancialMovementPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void openList(Pages route)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void openNew()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void amount(String amount)` | public | تنفيذ جزء متعلق بـ المبلغ، حسب المدخلات والتخصص. |
| `public void notes(String note)` | public | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `public String notesValue()` | public | ضبط الملاحظات / القيمة، حسب المدخلات والتخصص. |
| `public void choose(String label,String name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void chooseInput(By locator,String name)` | private | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void payment(String methodName)` | public | التعامل مع السداد، حسب المدخلات والتخصص. |
| `public void transferParty(int side,String type,String name)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectTransferType(int side,String type)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void movementType(String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void clearField(String label,int index)` | public | مسح/إلغاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void clearTransferDestination()` | public | مسح/إلغاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void outsideFiscalYear()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public BigDecimal[] taxValues()` | public | تنفيذ جزء متعلق بـ الضريبة / القيم، حسب المدخلات والتخصص. |
| `public boolean validationFor(String field,String label)` | public | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `public void taxable(boolean value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void saveMovement()` | public | حفظ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void saveAndPrintMovement()` | public | حفظ الطباعة، حسب المدخلات والتخصص. |
| `public void awaitReport()` | public | انتظار التقرير، حسب المدخلات والتخصص. |
| `public void attachReportEvidence()` | public | إرفاق التقرير، حسب المدخلات والتخصص. |
| `public void allocate(String invoiceCode,String amount)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public List<String> validationMessages()` | public | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `public boolean isFormVisible()` | public | السؤال عن حالة النموذج / الظاهر، حسب المدخلات والتخصص. |
| `public boolean isFormPresent()` | public | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `public void identifyMovement(String note,Object code)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void closeWithoutSaving()` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void dismissPopup()` | public | إغلاق تنبيه الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `private WebElement row(String note)` | private | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `public boolean hasRow(String note)` | public | السؤال عن وجود الصف، حسب المدخلات والتخصص. |
| `public void waitForRow(String note)` | public | انتظار الصف، حسب المدخلات والتخصص. |
| `public void editOwned(String note)` | public | تعديل المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void clearAllocationForEdit()` | public | مسح/إلغاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void requestDeleteOwned(String note)` | public | طلب إجراء المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void confirmDelete(boolean confirm)` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void waitForRemoval(String note)` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean hasListHeaders()` | public | السؤال عن وجود عناوين الأعمدة، حسب المدخلات والتخصص. |
| `public void waitForList()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void export(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `public void type(By locator,String value)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void click(By by)` | private | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void captureSaves(String marker)` | public | مراقبة تتبع الحفظ / عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String,Object>> capturedSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public String captureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public List<Map<String,Object>> capturedAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public boolean hasSaveStarted()` | public | السؤال عن وجود القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void awaitSavesSettled()` | public | انتظار عمليات الحفظ، حسب المدخلات والتخصص. |
| `void clickFinancialControl(By locator)` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |

<a id="file-bd8ca5afe472"></a>
## FinancialRecordList.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialRecordList.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialRecordList.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 76

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `FinancialRecordList(FinancialMovementPage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void click(By locator)` | private | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void identifyMovement(String note,Object code)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement row(String note)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `boolean matchesOwnedRow(WebElement row,String note)` | داخل الحزمة | مطابقة المملوك للاختبار / الصف، حسب المدخلات والتخصص. |
| `boolean hasRow(String note)` | داخل الحزمة | السؤال عن وجود الصف، حسب المدخلات والتخصص. |
| `void waitForRow(String note)` | داخل الحزمة | انتظار الصف، حسب المدخلات والتخصص. |
| `void editOwned(String note)` | داخل الحزمة | تعديل المملوك للاختبار، حسب المدخلات والتخصص. |
| `void requestDeleteOwned(String note)` | داخل الحزمة | طلب إجراء المملوك للاختبار، حسب المدخلات والتخصص. |
| `void clickOwnedAction(String note,String action)` | داخل الحزمة | ضغط إجراء النقر / المملوك للاختبار، حسب المدخلات والتخصص. |
| `void confirmDelete(boolean confirm)` | داخل الحزمة | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `void waitForRemoval(String note)` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean hasListHeaders()` | داخل الحزمة | السؤال عن وجود عناوين الأعمدة، حسب المدخلات والتخصص. |
| `void waitForList()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void export(String format)` | داخل الحزمة | تصدير التصدير، حسب المدخلات والتخصص. |

<a id="file-2c811c15a35a"></a>
## FinancialSaveCapture.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialSaveCapture.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/FinancialSaveCapture.java)

**دوره:** تتبع محاولات ونتائج الحفظ المملوكة لهذا المجال؛ منفصل عن نقر زر الحفظ ومقارنة قواعد العمل.

**حالته:** مصدر نشط · **عدد أسطره:** 58

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `List<Map<String,Object>> capturedAttempts()` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `FinancialSaveCapture(FinancialMovementPage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void captureSaves(String marker)` | داخل الحزمة | مراقبة تتبع الحفظ / عمليات الحفظ، حسب المدخلات والتخصص. |
| `List<Map<String,Object>> capturedSaves()` | داخل الحزمة | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `String captureMarker()` | داخل الحزمة | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `boolean hasSaveStarted()` | داخل الحزمة | السؤال عن وجود القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void awaitSavesSettled()` | داخل الحزمة | انتظار عمليات الحفظ، حسب المدخلات والتخصص. |

<a id="file-3b11f1fa1481"></a>
## OtherRevenueMovementPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/OtherRevenueMovementPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/OtherRevenueMovementPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 69 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public OtherRevenueMovementPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenOtherRevenueMovementPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void SelectRevenueTypeButton()` | public | اختيار الزر، حسب المدخلات والتخصص. |
| `public void SelectAccountTypeButton()` | public | اختيار الحساب / الزر، حسب المدخلات والتخصص. |
| `public boolean IsAmountRequiredErrorDisplayed()` | public | السؤال عن حالة المبلغ / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsTypeRequiredErrorDisplayed()` | public | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsPaymentMethodRequiredErrorDisplayed()` | public | السؤال عن حالة السداد / الخطأ، حسب المدخلات والتخصص. |

<a id="file-3333dbde6ae0"></a>
## SupplierBillOfExchangePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/SupplierBillOfExchangePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/SupplierBillOfExchangePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 84 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public SupplierBillOfExchangePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public By getAllocationInput(int rowIndex)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenFinancialMovementsPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void SaveCustomButton()` | public | حفظ الزر، حسب المدخلات والتخصص. |
| `public void HandelCustomReceipt(int rowIndex, String Number)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void CanselCustomReceipt()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsSupplierRequiredErrorDisplayed()` | public | السؤال عن حالة المورد / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsAmountRequiredErrorDisplayed()` | public | السؤال عن حالة المبلغ / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsPaymentMethodRequiredErrorDisplayed()` | public | السؤال عن حالة السداد / الخطأ، حسب المدخلات والتخصص. |

<a id="file-f3864dce667c"></a>
## SupplierDiscountNoticePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/SupplierDiscountNoticePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/SupplierDiscountNoticePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 139 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public SupplierDiscountNoticePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public By getAllocationInput(int rowIndex)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean IsSupplierRequiredErrorDisplayed()` | public | السؤال عن حالة المورد / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsAmountRequiredErrorDisplayed()` | public | السؤال عن حالة المبلغ / الخطأ، حسب المدخلات والتخصص. |
| `public void OpenSupplierDiscountNotice()` | public | فتح المورد / الخصم، حسب المدخلات والتخصص. |
| `public void SaveCustomButton()` | public | حفظ الزر، حسب المدخلات والتخصص. |
| `public void CanselCustomReceipt()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void HandelCustomReceipt(int rowIndex, String Number)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public BigDecimal[] getFinancialValues()` | public | قراءة أو إرجاع القيم، حسب المدخلات والتخصص. |
| `public double getNetBeforeTax()` | public | قراءة أو إرجاع الصافي / الضريبة، حسب المدخلات والتخصص. |
| `public double getTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public double getTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |
| `public double getExpectedTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public double getExpectedTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |

<a id="file-47e7576bf489"></a>
## TransfersAndSettlementsPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/TransfersAndSettlementsPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/FinancialMovements/TransfersAndSettlementsPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 91 · **الأب المباشر:** `FinancialMovementPage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElementActions.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitManagement.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public TransfersAndSettlementsPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenTransMovementsPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void SelectRightSide(String Label, String Name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SelectLeftSide(String Label, String Name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SelectDebitOption(String Option)` | public | اختيار المدين، حسب المدخلات والتخصص. |
| `public void SelectCreditOption(String Option)` | public | اختيار الدائن، حسب المدخلات والتخصص. |
