# صفحات ومكونات القواعد المشتركة

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-f230c0052300"></a>
## Abstract.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Abstract.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Abstract.java)

**دوره:** واجهة الصفحة العامة: تجمع سياق الجلسة وتفوض الحقول والتنقل والسجلات والطباعة لأدوات منفصلة.

**حالته:** مصدر نشط · **عدد أسطره:** 297

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

**ملفات تشير إليه مباشرة:** [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [CustomerDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-405c1db73294)، [CustomerReceiptPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-8896b80e2ad1)، [ExpenseMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-b36a74638da1)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)، [FinancialRecordList.java](MAIN_PAGES_FinancialMovements_AR.md#file-bd8ca5afe472)، [FinancialSaveCapture.java](MAIN_PAGES_FinancialMovements_AR.md#file-2c811c15a35a)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public final PageContext context()` | public | إرجاع اعتمادات نفس جلسة الصفحة، لا إنشاء متصفح جديد. |
| `public Abstract(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void AddArabicName(String ArabicName)` | public | إضافة العربي / الاسم، حسب المدخلات والتخصص. |
| `public void AddEnglishName(String EnglishName)` | public | إضافة الإنجليزي / الاسم، حسب المدخلات والتخصص. |
| `public void AddPhoneNumber(String PhoneNumber)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddValue(String value)` | public | إضافة القيمة، حسب المدخلات والتخصص. |
| `public void AddReference(String reference)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAdress(String adress)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddNotes(String notes)` | public | إضافة الملاحظات، حسب المدخلات والتخصص. |
| `public String AddDate()` | public | إضافة التاريخ، حسب المدخلات والتخصص. |
| `public String GetTodayDate()` | public | قراءة أو إرجاع التاريخ، حسب المدخلات والتخصص. |
| `public void AddPastYearDate()` | public | إضافة التاريخ، حسب المدخلات والتخصص. |
| `public String generateRandomNumber()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickNew()` | public | فتح نموذج سجل جديد. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveAndPrintButton()` | public | الحفظ والطباعة ومتابعة تبويب التقرير. |
| `public void ClickSaveButton()` | public | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `public void Edit()` | public | فتح تعديل وفق عقد الملف؛ بعض المسارات القديمة تختار آخر سجل. |
| `public void Delete()` | public | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `public void DeleteAll()` | public | حذف عام قديم؛ لا تساويه بتنظيف السجلات المملوكة. |
| `public boolean IsArabicNameRequiredErrorDisplayed()` | public | السؤال عن حالة العربي / الاسم / الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsRightEmailErrorDisplayed()` | public | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `public boolean IsTaxNumberRequiredErrorDisplayed()` | public | السؤال عن حالة الضريبة / الخطأ، حسب المدخلات والتخصص. |
| `public void AssertReceiptAdded(String ArabicName)` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void CloserReport()` | public | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `public void AssertTextInReport(String Text)` | public | مقارنة متوقعة النص / التقرير، حسب المدخلات والتخصص. |
| `public void AssertNumberInReport(String Number)` | public | مقارنة متوقعة التقرير، حسب المدخلات والتخصص. |
| `public void AssertDateInReport(String Date)` | public | مقارنة متوقعة التاريخ / التقرير، حسب المدخلات والتخصص. |
| `public boolean AssertNumberOfBankWithOutLetter()` | public | مقارنة متوقعة البنك، حسب المدخلات والتخصص. |
| `public void AssertCheckboxIsChecked(String checkboxName, boolean expectedState)` | public | مقارنة متوقعة مربع الاختيار / حالة التحديد، حسب المدخلات والتخصص. |
| `public boolean AssertSaveButtonIsNotChecked()` | public | مقارنة متوقعة الزر / حالة التحديد، حسب المدخلات والتخصص. |
| `public void SelectFromDropDawn(String Name, String Type)` | public | اختيار قيمة من قائمة بواسطة الحقل ونص الاختيار. |
| `public void SelectFromDropDawnByText(String Name, String Type)` | public | اختيار النص، حسب المدخلات والتخصص. |
| `public void selectTreasuryHandoverDropdown(String label, String value)` | public | اختيار القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public void SelectByKeys(String option)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickEnter()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickShift()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void clickOnEmptyArea()` | public | ضغط إجراء النقر / المنطقة، حسب المدخلات والتخصص. |
| `public void SetCheckboxValue(String checkboxName, boolean value)` | public | تعيين مربع الاختيار / القيمة، حسب المدخلات والتخصص. |
| `public void SelectCheckboxByText(String checkboxName, boolean value)` | public | اختيار مربع الاختيار / النص، حسب المدخلات والتخصص. |
| `public void SmartCheck(String checkboxName, boolean expectedState)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean isErrorPopupDisplayed()` | public | السؤال عن حالة الخطأ / الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `public void AssertPopupAndClickOK()` | public | مقارنة متوقعة الرسالة المنبثقة / النقر، حسب المدخلات والتخصص. |
| `public void AssertPopupAndClickCancel()` | public | مقارنة متوقعة الرسالة المنبثقة / النقر / الإلغاء، حسب المدخلات والتخصص. |
| `public void AddNewProductList(int count)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void NavigateTo(String url)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void NavigationHelper(Pages route)` | public | فتح شاشة مسماة مع انتظارها وفق تنفيذ الصفحة. |

<a id="file-7fafcdb2babe"></a>
## AbstractForBills.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/AbstractForBills.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/AbstractForBills.java)

**دوره:** اسم توافق رفيع لقدرات المستندات المشتركة.

**حالته:** مصدر نشط · **عدد أسطره:** 9 · **الأب المباشر:** `DocumentPage`

**يرتبط مباشرة بملفات داخل المشروع:** [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AbstractForBills(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-0c718cf5399e"></a>
## AbstractForDealer.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/AbstractForDealer.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/AbstractForDealer.java)

**دوره:** منسق المتعاملين: حقول مشتركة مع مكونات للفورم وقراءة القائمة وإجراءات السجل.

**حالته:** مصدر نشط · **عدد أسطره:** 388 · **الأب المباشر:** `Abstract`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)

**ملفات تشير إليه مباشرة:** [AreaPage.java](MAIN_PAGES_BasicData_AR.md#file-3f7bf8c58091)، [AreaRecordActions.java](MAIN_PAGES_BasicData_AR.md#file-3440e2c7639b)، [BankPage.java](MAIN_PAGES_BasicData_AR.md#file-fe24b8cdd695)، [BankRecords.java](MAIN_PAGES_BasicData_AR.md#file-c52c85fe5e8c)، [CashFormReader.java](MAIN_PAGES_BasicData_AR.md#file-0efcc911edbb)، [CashPage.java](MAIN_PAGES_BasicData_AR.md#file-ed12e0c67c34)، [DistributionEntityPage.java](MAIN_PAGES_BasicData_AR.md#file-b20ba4a5652e)، [DistributionRecords.java](MAIN_PAGES_BasicData_AR.md#file-cabbad08652d)؛ العرض مختصر وليس قائمة كاملة.

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AbstractForDealer(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void ClickNewButtonForDealer()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `private void showDealerBasicDataIfPresent()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SelectDealerOption(String fieldTitle, String option)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickContactDataTab()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickBasicDataTab()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickCustomerControlTab()` | public | ضغط إجراء النقر / العميل، حسب المدخلات والتخصص. |
| `public void ClickSupplierControlTab()` | public | ضغط إجراء النقر / المورد، حسب المدخلات والتخصص. |
| `public void AddDateForDealer()` | public | إضافة التاريخ، حسب المدخلات والتخصص. |
| `public void AddNoteForStorgTrans(String notes)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAllowanceDays(String allowanceDays)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddCommercialRegisterNumber(String commercialRegisterNumber)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddDiscountValue(String discountValue)` | public | إضافة الخصم / القيمة، حسب المدخلات والتخصص. |
| `public void AddValueLimit(String valueLimit)` | public | إضافة القيمة، حسب المدخلات والتخصص. |
| `public void AddNoteForDealer(String note)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddContactPersonName(String contactPersonName)` | public | إضافة الاسم، حسب المدخلات والتخصص. |
| `public void AddLandLine(String landLine)` | public | إضافة البند، حسب المدخلات والتخصص. |
| `public void AddEmail(String email)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddExtraNumber(String extraNumber)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddAnotherId(String anotherId)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddBankName(String bankName)` | public | إضافة البنك / الاسم، حسب المدخلات والتخصص. |
| `public void AddAccountNumber(String accountNumber)` | public | إضافة الحساب، حسب المدخلات والتخصص. |
| `public void AddAgentPhone(String agentPhone)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddIban(String iban)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddTaxNumber(String taxNumber)` | public | إضافة الضريبة، حسب المدخلات والتخصص. |
| `public void AddCountry(String country)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddCity(String city)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddDistrict(String district)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddBuildingNumber(String buildingNumber)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddPostalCode(String postalCode)` | public | إضافة الكود، حسب المدخلات والتخصص. |
| `public void AddStreetName(String streetName)` | public | إضافة الاسم، حسب المدخلات والتخصص. |
| `public void SetCheckboxValueForDealer(String text, boolean shouldBeChecked)` | public | تعيين مربع الاختيار / القيمة، حسب المدخلات والتخصص. |
| `public void SetCheckboxValueForTax(String text, boolean shouldBeChecked)` | public | تعيين مربع الاختيار / القيمة / الضريبة، حسب المدخلات والتخصص. |
| `public boolean AssertBuildingNumber()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean AssertAdress()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean AssertPostNumber()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean AssertAgentNatural()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean AssertClassification()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean AssertKind()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitForTableDataToLoad()` | public | انتظار الجدول، حسب المدخلات والتخصص. |
| `public boolean isDataPresentInTable()` | public | السؤال عن حالة الجدول، حسب المدخلات والتخصص. |
| `public boolean isDealerListStructureDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public int getVisibleDealerRowCount()` | public | قراءة أو إرجاع الظاهر / الصف، حسب المدخلات والتخصص. |
| `public boolean isDealerCreationFormDisplayed()` | public | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `public String getMissingDealerCommonFormControls()` | public | قراءة أو إرجاع النموذج، حسب المدخلات والتخصص. |
| `public void closeDealerFormWithoutSaving()` | public | إغلاق النموذج، حسب المدخلات والتخصص. |
| `public void openFirstDealerForEdit()` | public | فتح الأول، حسب المدخلات والتخصص. |
| `public String getFirstDealerArabicName()` | public | قراءة أو إرجاع الأول / العربي / الاسم، حسب المدخلات والتخصص. |
| `public String getDealerArabicNameValue()` | public | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `public void requestDeleteFirstDealer()` | public | طلب إجراء الأول، حسب المدخلات والتخصص. |
| `public void cancelDealerDelete()` | public | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `public boolean isDealerDeleteConfirmationDisplayed()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void dismissDealerSaveSuccessIfPresent()` | public | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean isDealerNameVisibleInList(String arabicName)` | public | السؤال عن حالة الاسم / الظاهر، حسب المدخلات والتخصص. |
| `public int getExactDealerNameMatchCount(String arabicName)` | public | قراءة أو إرجاع الاسم، حسب المدخلات والتخصص. |
| `public void openDealerForEditByName(String arabicName)` | public | فتح الاسم، حسب المدخلات والتخصص. |
| `public void requestDealerDeleteByName(String arabicName)` | public | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `public void confirmDealerDeleteByName(String arabicName)` | public | تأكيد التأكيد / الاسم، حسب المدخلات والتخصص. |
| `private boolean isVisible(By locator)` | private | السؤال عن حالة الظاهر، حسب المدخلات والتخصص. |
| `private boolean isElementDisplayed(WebElement element)` | private | السؤال عن حالة عنصر الشاشة، حسب المدخلات والتخصص. |
| `public void SelectByKeysForDealers(String option)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void rememberDealerListLocation()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean dealerElementDisplayed(WebElement element)` | داخل الحزمة | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |
| `boolean dealerControlVisible(By locator)` | داخل الحزمة | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `void showDealerBasicData()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String dealerListLocation()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-fe552e2e485f"></a>
## AbstractForPurchases.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/AbstractForPurchases.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/AbstractForPurchases.java)

**دوره:** اسم توافق رفيع لصفحات المشتريات فوق DocumentPage؛ ليس أبًا للمبيعات حاليًا.

**حالته:** مصدر نشط · **عدد أسطره:** 8 · **الأب المباشر:** `DocumentPage`

**يرتبط مباشرة بملفات داخل المشروع:** [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)

**ملفات تشير إليه مباشرة:** [Invoice.java](MAIN_PAGES_Invoice_AR.md#file-a0ad56722888)، [PurchaseDocumentForm.java](MAIN_PAGES_purches_AR.md#file-af9f5be8d0d7)، [PurchaseDocumentTransitions.java](MAIN_PAGES_purches_AR.md#file-ca18ac0f7698)، [PurchaseModulePage.java](MAIN_PAGES_purches_AR.md#file-26ced787f2c9)، [PurchasePrintWindows.java](MAIN_PAGES_purches_AR.md#file-9364a510d60a)، [PurchesBillsPage.java](MAIN_PAGES_purches_AR.md#file-83d91741f555)، [purchOrderPage.java](MAIN_PAGES_purches_AR.md#file-4a93b7c44de8)، [ReturnOrderPage.java](MAIN_PAGES_purches_AR.md#file-e431afd355fd)؛ العرض مختصر وليس قائمة كاملة.

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AbstractForPurchases(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-30b503deea8e"></a>
## AbstractForSales.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/AbstractForSales.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/AbstractForSales.java)

**دوره:** تخصص المبيعات: قراءة بنودها وإجمالياتها وفورمها وطباعة أرقامها، فوق DocumentPage.

**حالته:** مصدر نشط · **عدد أسطره:** 198 · **الأب المباشر:** `DocumentPage`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)

**ملفات تشير إليه مباشرة:** [Invoice.java](MAIN_PAGES_Invoice_AR.md#file-a0ad56722888)، [InvoicListPage.java](MAIN_PAGES_Invoice_AR.md#file-6c6dc5cb0a4f)، [SalesDocumentForm.java](MAIN_PAGES_Invoice_AR.md#file-633e381a995a)، [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)، [SalesPrintWindows.java](MAIN_PAGES_Invoice_AR.md#file-72c1e0b27bdd)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AbstractForSales(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public List<String> getDropdownOptions(String label)` | public | قراءة أو إرجاع القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public String getSelectedDropdownOption(String label)` | public | قراءة أو إرجاع القيمة المختارة / القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public void searchInAddItemsDialog(String fieldLabel, String query)` | public | البحث عن الأصناف، حسب المدخلات والتخصص. |
| `public List<String> getAddItemsDialogRows()` | public | قراءة أو إرجاع الأصناف / الصفوف، حسب المدخلات والتخصص. |
| `public void setShowSelectedItems(boolean shouldBeChecked)` | public | تعيين القيمة المختارة / الأصناف، حسب المدخلات والتخصص. |
| `public boolean isCustomerNameFieldDisplayed()` | public | السؤال عن حالة العميل / الاسم، حسب المدخلات والتخصص. |
| `public void toggleInvoiceFullscreen()` | public | تنفيذ جزء متعلق بـ الفاتورة / ملء الشاشة، حسب المدخلات والتخصص. |
| `public boolean isInvoiceSideMenuDisplayed()` | public | السؤال عن حالة الفاتورة، حسب المدخلات والتخصص. |
| `public void clickAddCustomerShortcut()` | public | ضغط إجراء النقر / العميل، حسب المدخلات والتخصص. |
| `public void clickAddProductShortcut()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void clickNewInvoice()` | public | ضغط إجراء النقر / الفاتورة، حسب المدخلات والتخصص. |
| `public boolean isFormDialogDisplayed()` | public | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `public void SearchAndSelectProduct(String productName)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected BigDecimal getSummaryValue(String label)` | protected | قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص. |
| `public void validateInvoiceAllBusiness()` | public | التحقق من الفاتورة، حسب المدخلات والتخصص. |
| `public InvoiceTotals validateBusinessTotals()` | public | حساب المتوقع ومقارنته بقيم المستند وإرفاق الدليل. |
| `public List<InvoiceRow> getInvoiceRows()` | public | تحويل صفوف بنود الشاشة إلى نماذج قابلة للحساب. |
| `public void saveAndPrintForReport()` | public | حفظ الطباعة / التقرير، حسب المدخلات والتخصص. |
| `public Map<String, String> getNamesQuantitiesAndPrices()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitUntilSummaryValuesLoaded()` | public | انتظار القيم، حسب المدخلات والتخصص. |
| `public ReportTotals getReportTotals()` | public | تجميع قيم الطباعة المقروءة في نموذج إجماليات. |
| `public Map<String, String> GetNamesQuantitiesAndPricesForInv()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SearchBarcodForInv(String searchText)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickOnNameRadioBtnForInv(String searchText)` | public | ضغط إجراء النقر / الاسم، حسب المدخلات والتخصص. |
| `java.math.BigDecimal readDefaultSummary(String label)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-faf7e732889e"></a>
## AbstractForStore.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/AbstractForStore.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/AbstractForStore.java)

**دوره:** اسم توافق رفيع لصفحات المستندات المخزنية فوق DocumentPage؛ ليس مالك بيانات الاختبار.

**حالته:** مصدر نشط · **عدد أسطره:** 8 · **الأب المباشر:** `DocumentPage`

**يرتبط مباشرة بملفات داخل المشروع:** [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)

**ملفات تشير إليه مباشرة:** [PurchesBillsPage.java](MAIN_PAGES_purches_AR.md#file-83d91741f555)، [AdditionOrderPage.java](MAIN_PAGES_StoreModel_AR.md#file-7bb1e8672e4f)، [DamageAdjustmentPage.java](MAIN_PAGES_StoreModel_AR.md#file-5eb56bab290b)، [ExchangeOrderPage.java](MAIN_PAGES_StoreModel_AR.md#file-1bb839b6d07c)، [ManufacturingPage.java](MAIN_PAGES_StoreModel_AR.md#file-fc537914e024)، [StoreModulePage.java](MAIN_PAGES_StoreModel_AR.md#file-3521361fe142)، [StoreRecordList.java](MAIN_PAGES_StoreModel_AR.md#file-ecf11f7c2fdf)، [TransBetweenStors.java](MAIN_PAGES_StoreModel_AR.md#file-09b1e9c2ce03)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AbstractForStore(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-401cc8ed0aa1"></a>
## DealerFormControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerFormControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerFormControls.java)

**دوره:** عناصر وقراءات نموذج محدد؛ فصل عن القائمة وملكية بيانات السيناريو.

**حالته:** مصدر نشط · **عدد أسطره:** 152

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DealerFormControls(AbstractForDealer owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean isElementDisplayed(WebElement element)` | private | السؤال عن حالة عنصر الشاشة، حسب المدخلات والتخصص. |
| `void ClickNewButtonForDealer()` | داخل الحزمة | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `void showDealerBasicDataIfPresent()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ClickContactDataTab()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickBasicDataTab()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickCustomerControlTab()` | داخل الحزمة | ضغط إجراء النقر / العميل، حسب المدخلات والتخصص. |
| `void ClickSupplierControlTab()` | داخل الحزمة | ضغط إجراء النقر / المورد، حسب المدخلات والتخصص. |
| `boolean isDealerCreationFormDisplayed()` | داخل الحزمة | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `String getMissingDealerCommonFormControls()` | داخل الحزمة | قراءة أو إرجاع النموذج، حسب المدخلات والتخصص. |
| `void closeDealerFormWithoutSaving()` | داخل الحزمة | إغلاق النموذج، حسب المدخلات والتخصص. |
| `WebElement findDealerCloseButton()` | داخل الحزمة | البحث عن الزر، حسب المدخلات والتخصص. |
| `void addMissingControl(List<String> missing, String label, By locator)` | داخل الحزمة | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String getDealerArabicNameValue()` | داخل الحزمة | قراءة أو إرجاع العربي / الاسم / القيمة، حسب المدخلات والتخصص. |
| `boolean isVisible(By locator)` | داخل الحزمة | السؤال عن حالة الظاهر، حسب المدخلات والتخصص. |

<a id="file-880c665d2332"></a>
## DealerListQueries.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerListQueries.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerListQueries.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 100

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DealerListQueries(AbstractForDealer owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean isDealerListStructureDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int getVisibleDealerRowCount()` | داخل الحزمة | قراءة أو إرجاع الظاهر / الصف، حسب المدخلات والتخصص. |
| `String getFirstDealerArabicName()` | داخل الحزمة | قراءة أو إرجاع الأول / العربي / الاسم، حسب المدخلات والتخصص. |
| `boolean isDealerNameVisibleInList(String arabicName)` | داخل الحزمة | السؤال عن حالة الاسم / الظاهر، حسب المدخلات والتخصص. |
| `int getExactDealerNameMatchCount(String arabicName)` | داخل الحزمة | قراءة أو إرجاع الاسم، حسب المدخلات والتخصص. |
| `boolean isElementDisplayed(WebElement element)` | داخل الحزمة | السؤال عن حالة عنصر الشاشة، حسب المدخلات والتخصص. |

<a id="file-6e416607a773"></a>
## DealerRecordActions.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DealerRecordActions.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DealerRecordActions.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 158

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DealerRecordActions(AbstractForDealer owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private boolean isElementDisplayed(WebElement element)` | private | السؤال عن حالة عنصر الشاشة، حسب المدخلات والتخصص. |
| `private boolean isVisible(By locator)` | private | السؤال عن حالة الظاهر، حسب المدخلات والتخصص. |
| `private void showDealerBasicDataIfPresent()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void openFirstDealerForEdit()` | داخل الحزمة | فتح الأول، حسب المدخلات والتخصص. |
| `void waitForDealerEditData()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void requestDeleteFirstDealer()` | داخل الحزمة | طلب إجراء الأول، حسب المدخلات والتخصص. |
| `void cancelDealerDelete()` | داخل الحزمة | إلغاء الإلغاء، حسب المدخلات والتخصص. |
| `boolean isDealerDeleteConfirmationDisplayed()` | داخل الحزمة | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void dismissDealerSaveSuccessIfPresent()` | داخل الحزمة | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void openDealerForEditByName(String arabicName)` | داخل الحزمة | فتح الاسم، حسب المدخلات والتخصص. |
| `void requestDealerDeleteByName(String arabicName)` | داخل الحزمة | طلب إجراء الاسم، حسب المدخلات والتخصص. |
| `void confirmDealerDeleteByName(String arabicName)` | داخل الحزمة | تأكيد التأكيد / الاسم، حسب المدخلات والتخصص. |
| `void clickDealerRowAction(WebElement row, String action)` | داخل الحزمة | ضغط إجراء النقر / الصف، حسب المدخلات والتخصص. |

<a id="file-25745f5f5d3c"></a>
## DocumentColumnEditing.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentColumnEditing.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentColumnEditing.java)

**دوره:** قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.

**حالته:** مصدر نشط · **عدد أسطره:** 77

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentColumnEditing(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void SetValueByRow(String columnName, int rowNumber, int value)` | داخل الحزمة | تعيين القيمة / الصف، حسب المدخلات والتخصص. |
| `void SetValuesForRows(String columnName, int... values)` | داخل الحزمة | تعيين القيم / الصفوف، حسب المدخلات والتخصص. |

<a id="file-14d530217dad"></a>
## DocumentGridEditing.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentGridEditing.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentGridEditing.java)

**دوره:** قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.

**حالته:** مصدر نشط · **عدد أسطره:** 63

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [PurchesBillsPage.java](MAIN_PAGES_purches_AR.md#file-83d91741f555)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentGridEditing(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void WaitEditeForBills()` | داخل الحزمة | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void clickDeleteRows(int... rowNumbers)` | داخل الحزمة | ضغط إجراء النقر / الصفوف، حسب المدخلات والتخصص. |
| `void EditForDamage()` | داخل الحزمة | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void DeleteForDamage()` | داخل الحزمة | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void DeleteByName(String Text )` | داخل الحزمة | حذف الاسم، حسب المدخلات والتخصص. |

<a id="file-90e4c2a522d6"></a>
## DocumentItemControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentItemControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentItemControls.java)

**دوره:** قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.

**حالته:** مصدر نشط · **عدد أسطره:** 92

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [PurchesBillsPage.java](MAIN_PAGES_purches_AR.md#file-83d91741f555)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentItemControls(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Boolean AssertExsistItemTaype()` | داخل الحزمة | مقارنة متوقعة الصنف، حسب المدخلات والتخصص. |
| `Boolean AssertExsistCustomre()` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Boolean AssertExpenditureCategories()` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Boolean AssertAgnt()` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Boolean AssertSotreRequired()` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Boolean AssertSupplier()` | داخل الحزمة | مقارنة متوقعة المورد، حسب المدخلات والتخصص. |
| `void ClickOnBarcodeRadioBtn()` | داخل الحزمة | ضغط إجراء النقر / الباركود، حسب المدخلات والتخصص. |
| `void ClickOnNameRadioBtn()` | داخل الحزمة | ضغط إجراء النقر / الاسم، حسب المدخلات والتخصص. |
| `void Search(String searchText)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ClickSaveForProductBtn()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickAddItemsBtn()` | داخل الحزمة | ضغط إجراء النقر / الأصناف، حسب المدخلات والتخصص. |

<a id="file-68b4b1149fce"></a>
## DocumentPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentPage.java)

**دوره:** منسق القدرات المشتركة للمستند: البنود والقراءة والخصم والتحقق والطباعة؛ لا يرث من مجال المتعاملين.

**حالته:** مصدر نشط · **عدد أسطره:** 357 · **الأب المباشر:** `Abstract`

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [PurchesBillsPage.java](MAIN_PAGES_purches_AR.md#file-83d91741f555)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [AbstractForBills.java](MAIN_PAGES_Shared_AR.md#file-7fafcdb2babe)، [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)، [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [AbstractForStore.java](MAIN_PAGES_Shared_AR.md#file-faf7e732889e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected DocumentPage(WebDriver driver)` | protected | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public Boolean AssertExsistItemTaype()` | public | مقارنة متوقعة الصنف، حسب المدخلات والتخصص. |
| `public Boolean AssertExsistCustomre()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Boolean AssertExpenditureCategories()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Boolean AssertAgnt()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Boolean AssertSotreRequired()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public Boolean AssertSupplier()` | public | مقارنة متوقعة المورد، حسب المدخلات والتخصص. |
| `public void ClickOnBarcodeRadioBtn()` | public | ضغط إجراء النقر / الباركود، حسب المدخلات والتخصص. |
| `public void ClickOnNameRadioBtn()` | public | ضغط إجراء النقر / الاسم، حسب المدخلات والتخصص. |
| `public void Search(String searchText)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickSaveForProductBtn()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickAddItemsBtn()` | public | ضغط إجراء النقر / الأصناف، حسب المدخلات والتخصص. |
| `public void SearchAndSelectProduct(String productName)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void searchAndSelectInvoiceProduct(String productName)` | protected | البحث عن الفاتورة، حسب المدخلات والتخصص. |
| `public void ClickPlusButtonByRow(int numberOfRows, int timesPerRow)` | public | ضغط إجراء النقر / الزر / الصف، حسب المدخلات والتخصص. |
| `public void ClickMinusButtonByRow(int numberOfRows, int timesPerRow)` | public | ضغط إجراء النقر / الزر / الصف، حسب المدخلات والتخصص. |
| `public void debugPrintAllRows()` | public | تنفيذ جزء متعلق بـ الطباعة / الصفوف، حسب المدخلات والتخصص. |
| `public void WaitEditeForBills()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void clickDeleteRows(int... rowNumbers)` | public | ضغط إجراء النقر / الصفوف، حسب المدخلات والتخصص. |
| `public void EditForBills()` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void EditForDamage()` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void DeleteForDamage()` | public | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SetQuantityByRow(int rowNumber, int quantity)` | public | تعيين الكمية / الصف، حسب المدخلات والتخصص. |
| `public String normalizeQuantity(String value)` | public | توحيد صيغة الكمية، حسب المدخلات والتخصص. |
| `public void SetQuantitiesForRows(int... values)` | public | تعيين الصفوف، حسب المدخلات والتخصص. |
| `public Map<String, String> GetNamesAndQuantities()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void CompareNamesAndQuantitiesWithReport(Map<String, String> expectedData)` | public | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `public void waitUntilMessageDisappear(String messageText)` | public | انتظار الرسالة، حسب المدخلات والتخصص. |
| `public void DeleteByName(String Text )` | public | حذف الاسم، حسب المدخلات والتخصص. |
| `public void ClickCash()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickOtherPay()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickPlusToAddPay()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void AddDiscountPercntge(String discountPercntge)` | public | إضافة الخصم، حسب المدخلات والتخصص. |
| `public void AddDiscountValue(String discountValue)` | public | إضافة الخصم / القيمة، حسب المدخلات والتخصص. |
| `public void SetValueByRow(String columnName, int rowNumber, int value)` | public | تعيين القيمة / الصف، حسب المدخلات والتخصص. |
| `public void SetValuesForRows(String columnName, int... values)` | public | تعيين القيم / الصفوف، حسب المدخلات والتخصص. |
| `public Map<String, String> GetNamesQuantitiesAndPrices()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void CompareNamesQuantitiesAndPricesWithReport(Map<String, String> expectedData)` | public | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `public List<InvoiceRow> getInvoiceRows()` | public | تحويل صفوف بنود الشاشة إلى نماذج قابلة للحساب. |
| `protected BigDecimal getSummaryValue(String label)` | protected | قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص. |
| `public BigDecimal getTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |
| `public BigDecimal getBeforeTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getNet()` | public | قراءة أو إرجاع الصافي، حسب المدخلات والتخصص. |
| `public BigDecimal getDiscount()` | public | قراءة أو إرجاع الخصم، حسب المدخلات والتخصص. |
| `protected BigDecimal getReportTotal(String label)` | protected | قراءة أو إرجاع التقرير / الإجمالي، حسب المدخلات والتخصص. |
| `public ReportTotals getReportTotals()` | public | تجميع قيم الطباعة المقروءة في نموذج إجماليات. |
| `public void attachBusinessValidation(InvoiceTotals totals)` | public | إرفاق التحقق، حسب المدخلات والتخصص. |
| `public void attachReportValidation( ReportTotals reportTotals, InvoiceTotals totals, boolean isExclusive)` | public | إرفاق التقرير / التحقق، حسب المدخلات والتخصص. |
| `public void assertInvoiceTotals(InvoiceTotals totals)` | public | مقارنة إجماليات الشاشة بالإجماليات المتوقعة. |
| `public void assertReportTotals(ReportTotals report, InvoiceTotals expected, TaxMode taxMode)` | public | مقارنة الطباعة بالمتوقع مع مراعاة نوع الضريبة. |
| `public void attachReportValidation(ReportTotals reportTotals, InvoiceTotals totals)` | public | إرفاق التقرير / التحقق، حسب المدخلات والتخصص. |
| `public void assertReportTotals(ReportTotals report, InvoiceTotals expected)` | public | مقارنة الطباعة بالمتوقع مع مراعاة نوع الضريبة. |
| `public InvoiceTotals validateBusinessTotals()` | public | حساب المتوقع ومقارنته بقيم المستند وإرفاق الدليل. |
| `public void saveAndPrintForReport()` | public | حفظ الطباعة / التقرير، حسب المدخلات والتخصص. |
| `public void validateReport(InvoiceTotals totals)` | public | قراءة ومقارنة أرقام التقرير ثم تنسيق الرجوع حسب المسار. |
| `public void validateInvoiceAllBusiness()` | public | التحقق من الفاتورة، حسب المدخلات والتخصص. |

<a id="file-06066f1bdac6"></a>
## DocumentPaymentControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentPaymentControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentPaymentControls.java)

**دوره:** قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.

**حالته:** مصدر نشط · **عدد أسطره:** 60

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentPaymentControls(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void ClickCash()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickOtherPay()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickPlusToAddPay()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void AddDiscountPercntge(String discountPercntge)` | داخل الحزمة | إضافة الخصم، حسب المدخلات والتخصص. |
| `void AddDiscountValue(String discountValue)` | داخل الحزمة | إضافة الخصم / القيمة، حسب المدخلات والتخصص. |

<a id="file-e15f8897987f"></a>
## DocumentPrintChecks.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentPrintChecks.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentPrintChecks.java)

**دوره:** قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.

**حالته:** مصدر نشط · **عدد أسطره:** 82

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentPrintChecks(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void CompareNamesQuantitiesAndPricesWithReport(Map<String, String> expectedData)` | داخل الحزمة | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `BigDecimal getReportTotal(String label)` | داخل الحزمة | قراءة أو إرجاع التقرير / الإجمالي، حسب المدخلات والتخصص. |
| `ReportTotals getReportTotals()` | داخل الحزمة | تجميع قيم الطباعة المقروءة في نموذج إجماليات. |

<a id="file-6ab0f716fbfb"></a>
## DocumentQuantityPrintCheck.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentQuantityPrintCheck.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentQuantityPrintCheck.java)

**دوره:** قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.

**حالته:** مصدر نشط · **عدد أسطره:** 77

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [PurchesBillsPage.java](MAIN_PAGES_purches_AR.md#file-83d91741f555)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentQuantityPrintCheck(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void CompareNamesAndQuantitiesWithReport(Map<String, String> expectedData)` | داخل الحزمة | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `void waitUntilMessageDisappear(String messageText)` | داخل الحزمة | انتظار الرسالة، حسب المدخلات والتخصص. |

<a id="file-0b7db83fde59"></a>
## DocumentSummaryReader.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/DocumentSummaryReader.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/DocumentSummaryReader.java)

**دوره:** قراءة قيم إجماليات الشاشة أو الطباعة حسب نموذج وselectors هذا التخصص.

**حالته:** مصدر نشط · **عدد أسطره:** 55

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DocumentSummaryReader(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `BigDecimal getSummaryValue(String label)` | داخل الحزمة | قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص. |
| `BigDecimal getTotal()` | داخل الحزمة | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |

<a id="file-79ba3d4eb127"></a>
## InvoiceBusinessValidation.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/InvoiceBusinessValidation.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/InvoiceBusinessValidation.java)

**دوره:** الحساب والمقارنة وإرفاق المتوقع والفعلـي عبر قراءات المستند، مع الحفاظ على override.

**حالته:** مصدر نشط · **عدد أسطره:** 143

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `InvoiceBusinessValidation(DocumentPage owner)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void attachBusinessValidation(InvoiceTotals totals)` | داخل الحزمة | إرفاق التحقق، حسب المدخلات والتخصص. |
| `void attachReportValidation( ReportTotals reportTotals, InvoiceTotals totals, boolean isExclusive)` | داخل الحزمة | إرفاق التقرير / التحقق، حسب المدخلات والتخصص. |
| `void assertInvoiceTotals(InvoiceTotals totals)` | داخل الحزمة | مقارنة إجماليات الشاشة بالإجماليات المتوقعة. |
| `void assertReportTotals(ReportTotals report, InvoiceTotals expected, TaxMode taxMode)` | داخل الحزمة | مقارنة الطباعة بالمتوقع مع مراعاة نوع الضريبة. |
| `void attachReportValidation(ReportTotals reportTotals, InvoiceTotals totals)` | داخل الحزمة | إرفاق التقرير / التحقق، حسب المدخلات والتخصص. |
| `void assertReportTotals(ReportTotals report, InvoiceTotals expected)` | داخل الحزمة | مقارنة الطباعة بالمتوقع مع مراعاة نوع الضريبة. |
| `InvoiceTotals validateBusinessTotals()` | داخل الحزمة | حساب المتوقع ومقارنته بقيم المستند وإرفاق الدليل. |

<a id="file-7d6769cd65c4"></a>
## PageContext.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PageContext.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PageContext.java)

**دوره:** حاوية اعتمادات جلسة صفحة واحدة: المتصفح وأدوات العناصر والانتظار والتبويبات والإجراءات.

**حالته:** مصدر نشط · **عدد أسطره:** 11

**يرتبط مباشرة بملفات داخل المشروع:** [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record PageContext(WebDriver driver, ElmentAction elements, WaitMangment waits, BrowserAction browser, Actions actions)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |

<a id="file-4299488a700b"></a>
## PageFeedback.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PageFeedback.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PageFeedback.java)

**دوره:** قدرة مشتركة مفصولة من واجهة الصفحة العامة؛ تحدد أسماء ميثوداتها نوع الفعل المحدد.

**حالته:** مصدر نشط · **عدد أسطره:** 77

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PageFeedback(Abstract owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean IsArabicNameRequiredErrorDisplayed()` | داخل الحزمة | السؤال عن حالة العربي / الاسم / الخطأ، حسب المدخلات والتخصص. |
| `boolean IsRightEmailErrorDisplayed()` | داخل الحزمة | السؤال عن حالة الخطأ، حسب المدخلات والتخصص. |
| `boolean IsTaxNumberRequiredErrorDisplayed()` | داخل الحزمة | السؤال عن حالة الضريبة / الخطأ، حسب المدخلات والتخصص. |
| `boolean AssertNumberOfBankWithOutLetter()` | داخل الحزمة | مقارنة متوقعة البنك، حسب المدخلات والتخصص. |
| `void AssertCheckboxIsChecked(String checkboxName, boolean expectedState)` | داخل الحزمة | مقارنة متوقعة مربع الاختيار / حالة التحديد، حسب المدخلات والتخصص. |
| `boolean AssertSaveButtonIsNotChecked()` | داخل الحزمة | مقارنة متوقعة الزر / حالة التحديد، حسب المدخلات والتخصص. |
| `boolean isErrorPopupDisplayed()` | داخل الحزمة | السؤال عن حالة الخطأ / الرسالة المنبثقة، حسب المدخلات والتخصص. |
| `void AssertPopupAndClickOK()` | داخل الحزمة | مقارنة متوقعة الرسالة المنبثقة / النقر، حسب المدخلات والتخصص. |
| `void AssertPopupAndClickCancel()` | داخل الحزمة | مقارنة متوقعة الرسالة المنبثقة / النقر / الإلغاء، حسب المدخلات والتخصص. |

<a id="file-7f53d54d113b"></a>
## PageFormFields.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PageFormFields.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PageFormFields.java)

**دوره:** قدرة مشتركة مفصولة من واجهة الصفحة العامة؛ تحدد أسماء ميثوداتها نوع الفعل المحدد.

**حالته:** مصدر نشط · **عدد أسطره:** 83

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PageFormFields(Abstract owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void AddArabicName(String ArabicName)` | داخل الحزمة | إضافة العربي / الاسم، حسب المدخلات والتخصص. |
| `void AddEnglishName(String EnglishName)` | داخل الحزمة | إضافة الإنجليزي / الاسم، حسب المدخلات والتخصص. |
| `void AddPhoneNumber(String PhoneNumber)` | داخل الحزمة | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void AddValue(String value)` | داخل الحزمة | إضافة القيمة، حسب المدخلات والتخصص. |
| `void AddReference(String reference)` | داخل الحزمة | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void AddAdress(String adress)` | داخل الحزمة | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void AddNotes(String notes)` | داخل الحزمة | إضافة الملاحظات، حسب المدخلات والتخصص. |
| `String AddDate()` | داخل الحزمة | إضافة التاريخ، حسب المدخلات والتخصص. |
| `String GetTodayDate()` | داخل الحزمة | قراءة أو إرجاع التاريخ، حسب المدخلات والتخصص. |
| `void AddPastYearDate()` | داخل الحزمة | إضافة التاريخ، حسب المدخلات والتخصص. |

<a id="file-fd8a181416f9"></a>
## PageNavigation.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PageNavigation.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PageNavigation.java)

**دوره:** قدرة مشتركة مفصولة من واجهة الصفحة العامة؛ تحدد أسماء ميثوداتها نوع الفعل المحدد.

**حالته:** مصدر نشط · **عدد أسطره:** 46

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PageNavigation(Abstract owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void NavigateTo(String url)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void NavigationHelper(Pages route)` | داخل الحزمة | فتح شاشة مسماة مع انتظارها وفق تنفيذ الصفحة. |

<a id="file-018aad73c8c8"></a>
## PageRecordActions.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PageRecordActions.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PageRecordActions.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 78

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PageRecordActions(Abstract owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void ClickNew()` | داخل الحزمة | فتح نموذج سجل جديد. |
| `void ClickCloseButton()` | داخل الحزمة | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `void ClickSaveButton()` | داخل الحزمة | الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ. |
| `void Edit()` | داخل الحزمة | فتح تعديل وفق عقد الملف؛ بعض المسارات القديمة تختار آخر سجل. |
| `void Delete()` | داخل الحزمة | حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف. |
| `void DeleteAll()` | داخل الحزمة | حذف عام قديم؛ لا تساويه بتنظيف السجلات المملوكة. |
| `void AssertReceiptAdded(String ArabicName)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-fbd8c01f9213"></a>
## PageSelections.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PageSelections.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PageSelections.java)

**دوره:** قدرة مشتركة مفصولة من واجهة الصفحة العامة؛ تحدد أسماء ميثوداتها نوع الفعل المحدد.

**حالته:** مصدر نشط · **عدد أسطره:** 84

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PageSelections(Abstract owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void SelectFromDropDawn(String Name, String Type)` | داخل الحزمة | اختيار قيمة من قائمة بواسطة الحقل ونص الاختيار. |
| `void SelectFromDropDawnByText(String Name, String Type)` | داخل الحزمة | اختيار النص، حسب المدخلات والتخصص. |
| `void selectTreasuryHandoverDropdown(String label, String value)` | داخل الحزمة | اختيار القائمة المنسدلة، حسب المدخلات والتخصص. |
| `void SelectByKeys(String option)` | داخل الحزمة | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ClickEnter()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickShift()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void clickOnEmptyArea()` | داخل الحزمة | ضغط إجراء النقر / المنطقة، حسب المدخلات والتخصص. |
| `void SetCheckboxValue(String checkboxName, boolean value)` | داخل الحزمة | تعيين مربع الاختيار / القيمة، حسب المدخلات والتخصص. |
| `void SelectCheckboxByText(String checkboxName, boolean value)` | داخل الحزمة | اختيار مربع الاختيار / النص، حسب المدخلات والتخصص. |
| `void SmartCheck(String checkboxName, boolean expectedState)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void AddNewProductList(int count)` | داخل الحزمة | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-fa9d841b9c02"></a>
## PrintedDocumentControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PrintedDocumentControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PrintedDocumentControls.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 60

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PrintedDocumentControls(Abstract owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void ClickSaveAndPrintButton()` | داخل الحزمة | الحفظ والطباعة ومتابعة تبويب التقرير. |
| `void CloserReport()` | داخل الحزمة | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `void AssertTextInReport(String Text)` | داخل الحزمة | مقارنة متوقعة النص / التقرير، حسب المدخلات والتخصص. |
| `void AssertNumberInReport(String Number)` | داخل الحزمة | مقارنة متوقعة التقرير، حسب المدخلات والتخصص. |
| `void AssertDateInReport(String Date)` | داخل الحزمة | مقارنة متوقعة التاريخ / التقرير، حسب المدخلات والتخصص. |

<a id="file-9ff578ab87a9"></a>
## PurchaseInvoiceRows.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PurchaseInvoiceRows.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PurchaseInvoiceRows.java)

**دوره:** تحويل بنود الشاشة وقيمها إلى نماذج أو لقطة بيانات للمقارنة والحساب.

**حالته:** مصدر نشط · **عدد أسطره:** 76

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseInvoiceRows(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `List<InvoiceRow> getInvoiceRows()` | داخل الحزمة | تحويل صفوف بنود الشاشة إلى نماذج قابلة للحساب. |

<a id="file-09dda0ec1286"></a>
## PurchaseInvoiceSnapshot.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/PurchaseInvoiceSnapshot.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/PurchaseInvoiceSnapshot.java)

**دوره:** تحويل بنود الشاشة وقيمها إلى نماذج أو لقطة بيانات للمقارنة والحساب.

**حالته:** مصدر نشط · **عدد أسطره:** 86

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `PurchaseInvoiceSnapshot(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, String> GetNamesQuantitiesAndPrices()` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-35e4eb4e4ef3"></a>
## SalesDropdownControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesDropdownControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesDropdownControls.java)

**دوره:** اختيار وقراءة ومسح القيم في قوائم نموذج المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 90

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesDropdownControls(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `List<String> getDropdownOptions(String label)` | داخل الحزمة | قراءة أو إرجاع القائمة المنسدلة، حسب المدخلات والتخصص. |
| `String getSelectedDropdownOption(String label)` | داخل الحزمة | قراءة أو إرجاع القيمة المختارة / القائمة المنسدلة، حسب المدخلات والتخصص. |
| `By dropdownInput(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ القائمة المنسدلة، حسب المدخلات والتخصص. |
| `By selectedDropdownOption(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ القيمة المختارة / القائمة المنسدلة، حسب المدخلات والتخصص. |

<a id="file-89163561fdf2"></a>
## SalesFormControls.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesFormControls.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesFormControls.java)

**دوره:** عناصر وقراءات نموذج محدد؛ فصل عن القائمة وملكية بيانات السيناريو.

**حالته:** مصدر نشط · **عدد أسطره:** 123

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesFormControls(AbstractForSales owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `boolean isCustomerNameFieldDisplayed()` | داخل الحزمة | السؤال عن حالة العميل / الاسم، حسب المدخلات والتخصص. |
| `void toggleInvoiceFullscreen()` | داخل الحزمة | تنفيذ جزء متعلق بـ الفاتورة / ملء الشاشة، حسب المدخلات والتخصص. |
| `boolean isInvoiceSideMenuDisplayed()` | داخل الحزمة | السؤال عن حالة الفاتورة، حسب المدخلات والتخصص. |
| `void clickAddCustomerShortcut()` | داخل الحزمة | ضغط إجراء النقر / العميل، حسب المدخلات والتخصص. |
| `void clickAddProductShortcut()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void clickNewInvoice()` | داخل الحزمة | ضغط إجراء النقر / الفاتورة، حسب المدخلات والتخصص. |
| `boolean isFormDialogDisplayed()` | داخل الحزمة | السؤال عن حالة النموذج، حسب المدخلات والتخصص. |
| `void SearchAndSelectProduct(String productName)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void SearchBarcodForInv(String searchText)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ClickOnNameRadioBtnForInv(String searchText)` | داخل الحزمة | ضغط إجراء النقر / الاسم، حسب المدخلات والتخصص. |

<a id="file-76d5123de279"></a>
## SalesInvoiceRows.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesInvoiceRows.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesInvoiceRows.java)

**دوره:** تحويل بنود الشاشة وقيمها إلى نماذج أو لقطة بيانات للمقارنة والحساب.

**حالته:** مصدر نشط · **عدد أسطره:** 83

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesInvoiceRows(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `List<InvoiceRow> getInvoiceRows()` | داخل الحزمة | تحويل صفوف بنود الشاشة إلى نماذج قابلة للحساب. |

<a id="file-9b62cb21ada7"></a>
## SalesInvoiceSnapshot.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesInvoiceSnapshot.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesInvoiceSnapshot.java)

**دوره:** تحويل بنود الشاشة وقيمها إلى نماذج أو لقطة بيانات للمقارنة والحساب.

**حالته:** مصدر نشط · **عدد أسطره:** 136

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesInvoiceSnapshot(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, String> getNamesQuantitiesAndPrices()` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String, String> GetNamesQuantitiesAndPricesForInv()` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-6f7eb718adf6"></a>
## SalesItemDialog.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesItemDialog.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesItemDialog.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 69

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesItemDialog(AbstractForSales owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void searchInAddItemsDialog(String fieldLabel, String query)` | داخل الحزمة | البحث عن الأصناف، حسب المدخلات والتخصص. |
| `List<String> getAddItemsDialogRows()` | داخل الحزمة | قراءة أو إرجاع الأصناف / الصفوف، حسب المدخلات والتخصص. |
| `void setShowSelectedItems(boolean shouldBeChecked)` | داخل الحزمة | تعيين القيمة المختارة / الأصناف، حسب المدخلات والتخصص. |

<a id="file-a851ae717728"></a>
## SalesPrintedTotals.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesPrintedTotals.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesPrintedTotals.java)

**دوره:** قراءة قيم إجماليات الشاشة أو الطباعة حسب نموذج وselectors هذا التخصص.

**حالته:** مصدر نشط · **عدد أسطره:** 53

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesPrintedTotals(AbstractForSales owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `ReportTotals getReportTotals()` | داخل الحزمة | تجميع قيم الطباعة المقروءة في نموذج إجماليات. |
| `BigDecimal getInvoiceReportTotal(String label)` | داخل الحزمة | قراءة أو إرجاع الفاتورة / التقرير / الإجمالي، حسب المدخلات والتخصص. |

<a id="file-589319887169"></a>
## SalesSummaryReader.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/SalesSummaryReader.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/SalesSummaryReader.java)

**دوره:** قراءة قيم إجماليات الشاشة أو الطباعة حسب نموذج وselectors هذا التخصص.

**حالته:** مصدر نشط · **عدد أسطره:** 107

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SalesSummaryReader(AbstractForSales owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `BigDecimal getSummaryValue(String label)` | داخل الحزمة | قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص. |
| `void waitUntilSummaryValuesLoaded()` | داخل الحزمة | انتظار القيم، حسب المدخلات والتخصص. |

<a id="file-33c7a85dca71"></a>
## StoreProductSearch.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/StoreProductSearch.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/StoreProductSearch.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 105

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `StoreProductSearch(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `By getSuggestionItem(String name)` | داخل الحزمة | قراءة أو إرجاع الصنف، حسب المدخلات والتخصص. |
| `void SearchAndSelectProduct(String productName)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void searchAndSelectInvoiceProduct(String productName)` | داخل الحزمة | البحث عن الفاتورة، حسب المدخلات والتخصص. |
| `WebElement SearchInputElement()` | داخل الحزمة | البحث عن عنصر الشاشة، حسب المدخلات والتخصص. |
| `boolean waitForProductToAppearInTable(String productName, int timeoutSeconds)` | داخل الحزمة | انتظار الجدول، حسب المدخلات والتخصص. |

<a id="file-70e866b9852e"></a>
## StoreQuantityEditor.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/StoreQuantityEditor.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/StoreQuantityEditor.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 122

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `StoreQuantityEditor(DocumentPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void SetQuantityByRow(int rowNumber, int quantity)` | داخل الحزمة | تعيين الكمية / الصف، حسب المدخلات والتخصص. |
| `String normalizeQuantity(String value)` | داخل الحزمة | توحيد صيغة الكمية، حسب المدخلات والتخصص. |
| `void SetQuantitiesForRows(int... values)` | داخل الحزمة | تعيين الصفوف، حسب المدخلات والتخصص. |
| `Map<String, String> GetNamesAndQuantities()` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

