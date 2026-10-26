# أدوات Bots

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-741424a4cfd7"></a>
## AlertAction.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/AlertAction.java](../../../src/main/java/Plugin_ERP/Bots/AlertAction.java)

**دوره:** التعامل مع رسائل وتنبيهات وتأكيدات بحسب نوعها داخل المتصفح.

**حالته:** مصدر نشط · **عدد أسطره:** 95 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**ملفات تشير إليه مباشرة:** [CashFormReader.java](MAIN_PAGES_BasicData_AR.md#file-0efcc911edbb)، [CashPage.java](MAIN_PAGES_BasicData_AR.md#file-ed12e0c67c34)، [MainBage.java](MAIN_PAGES_BasicData_AR.md#file-687a597d8dd2)، [TreasuryHandoverPage.java](MAIN_PAGES_BasicData_AR.md#file-ca1ef3ba6311)، [TypesofdiscountsPage.java](MAIN_PAGES_BasicData_AR.md#file-df58c29cce49)، [CustomerDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-405c1db73294)، [CustomerReceiptPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-8896b80e2ad1)، [ExpenseMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-b36a74638da1)؛ العرض مختصر وليس قائمة كاملة.

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public AlertAction(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void acceptSwalConfirm()` | public | تنفيذ جزء متعلق بـ رسالة تأكيد الصفحة / التأكيد، حسب المدخلات والتخصص. |
| `public void dismissAlert()` | public | إغلاق تنبيه تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public String getAlertText()` | public | قراءة أو إرجاع تنبيه المتصفح / النص، حسب المدخلات والتخصص. |
| `public void sendTextToAlert(String text)` | public | تنفيذ جزء متعلق بـ النص / تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public String getPopupMessage(By locator)` | public | قراءة أو إرجاع الرسالة المنبثقة / الرسالة، حسب المدخلات والتخصص. |

<a id="file-76e2f2dada2e"></a>
## BaseAction.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/BaseAction.java](../../../src/main/java/Plugin_ERP/Bots/BaseAction.java)

**دوره:** قاعدة أدوات Selenium التي تتلقى المتصفح والانتظار وتقدم scroll وJS click ومساعدًا للنقر خارج الحقل.

**حالته:** مصدر نشط · **عدد أسطره:** 33

**ملفات تشير إليه مباشرة:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [CheckboxActions.java](MAIN_Bots_AR.md#file-f5c34f954a3c)، [DropdownActions.java](MAIN_Bots_AR.md#file-6632f3e57d86)، [GridActions.java](MAIN_Bots_AR.md#file-550ac2ee26b1)، [PriceListActions.java](MAIN_Bots_AR.md#file-3dc867d1513c)، [UtilityActions.java](MAIN_Bots_AR.md#file-e16473182bff)، [ValidationActions.java](MAIN_Bots_AR.md#file-122f5533f792)، [WaitActions.java](MAIN_Bots_AR.md#file-0ea40183d75b)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public BaseAction(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `protected void scrollIntoView(WebElement element)` | protected | تنفيذ جزء متعلق بـ التمرير، حسب المدخلات والتخصص. |
| `protected void jsClick(WebElement element)` | protected | تنفيذ جزء متعلق بـ النقر، حسب المدخلات والتخصص. |
| `public void clickOnEmptyArea()` | public | ضغط إجراء النقر / المنطقة، حسب المدخلات والتخصص. |

<a id="file-ca0683510b61"></a>
## BrowserAction.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/BrowserAction.java](../../../src/main/java/Plugin_ERP/Bots/BrowserAction.java)

**دوره:** مساعدات تنقل وتبويبات وإعادة محاولة مرتبطة بالمتصفح.

**حالته:** مصدر نشط · **عدد أسطره:** 258

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [TreasuryHandoverPage.java](MAIN_PAGES_BasicData_AR.md#file-ca1ef3ba6311)، [CustomerDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-405c1db73294)، [CustomerReceiptPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-8896b80e2ad1)، [ExpenseMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-b36a74638da1)، [OtherRevenueMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-3b11f1fa1481)، [SupplierBillOfExchangePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-3333dbde6ae0)، [SupplierDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-f3864dce667c)، [TransfersAndSettlementsPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-47e7576bf489)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public BrowserAction(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void maximizeWindow()` | public | تنفيذ جزء متعلق بـ التبويب، حسب المدخلات والتخصص. |
| `public void refreshPage()` | public | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public String getCurrentUrl()` | public | قراءة أو إرجاع عنوان الشاشة، حسب المدخلات والتخصص. |
| `public void switchToMainTab()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void navigateToUrl(String url)` | public | تنفيذ جزء متعلق بـ عنوان الشاشة، حسب المدخلات والتخصص. |
| `public void closeCurrentTab()` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void openNewTab()` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void navigateTo(String navigateAction)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void clearBrowserCache()` | public | مسح/إلغاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void navigateWithRetry(String url)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitForOverlayToDisappear()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void OpenNavigationHelper(Pages page)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-f5c34f954a3c"></a>
## CheckboxActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/CheckboxActions.java](../../../src/main/java/Plugin_ERP/Bots/CheckboxActions.java)

**دوره:** عدة استراتيجيات لقراءة وضبط ومراجعة Checkbox بحسب شكل الشاشة.

**حالته:** مصدر نشط · **عدد أسطره:** 373 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public CheckboxActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void stableFastSetCheckboxByText(String text, boolean shouldBeChecked)` | public | تنفيذ جزء متعلق بـ مربع الاختيار / النص، حسب المدخلات والتخصص. |
| `public void stableFastSetCheckboxByTextCloude(String text, boolean shouldBeChecked)` | public | تنفيذ جزء متعلق بـ مربع الاختيار / النص، حسب المدخلات والتخصص. |
| `public void assertCheckboxState(String text, boolean expectedState)` | public | مقارنة متوقعة مربع الاختيار، حسب المدخلات والتخصص. |
| `public void selectRadioButton(String optionName)` | public | اختيار الزر، حسب المدخلات والتخصص. |
| `public void setCheckboxForDealer(String title, boolean shouldBeChecked)` | public | تعيين مربع الاختيار، حسب المدخلات والتخصص. |
| `public void setCheckboxForTax(String text, boolean shouldBeChecked)` | public | تعيين مربع الاختيار / الضريبة، حسب المدخلات والتخصص. |
| `public void setCheckbox(String text, boolean shouldBeChecked)` | public | تعيين مربع الاختيار، حسب المدخلات والتخصص. |

<a id="file-6632f3e57d86"></a>
## DropdownActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/DropdownActions.java](../../../src/main/java/Plugin_ERP/Bots/DropdownActions.java)

**دوره:** اختيار قيم من قوائم Vue وواجهات قوائم قديمة مع سياسات إعادة محاولة.

**حالته:** مصدر نشط · **عدد أسطره:** 482 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public DropdownActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void selectUsersByCode(int... codes)` | public | اختيار الكود، حسب المدخلات والتخصص. |
| `public void typeAndVerify(WebElement input, String expectedText)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectVueOptionboth(String labelText, String optionText)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectVueOptionByDataTitle(String title, String optionText)` | public | اختيار العنوان، حسب المدخلات والتخصص. |
| `public void selectVueMultiOption(String label, String[] values)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectTreasuryHandoverDropdown(String label, String value)` | public | اختيار القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public void selectVueOptionLeft(String labelText, String optionText)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectProductByName(String productName)` | public | اختيار الاسم، حسب المدخلات والتخصص. |
| `public void selectFirstProducts(int count)` | public | اختيار الأول، حسب المدخلات والتخصص. |

<a id="file-11a21e33ca10"></a>
## ElementActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/ElementActions.java](../../../src/main/java/Plugin_ERP/Bots/ElementActions.java)

**دوره:** واجهة أدوات قديمة تنشئ مساعداتها وتقدم قراءة وضغطًا وكتابة وعمليات صفوف؛ ما زالت واسعة المسؤولية.

**حالته:** مصدر نشط · **عدد أسطره:** 494

**يرتبط مباشرة بملفات داخل المشروع:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [CashFormReader.java](MAIN_PAGES_BasicData_AR.md#file-0efcc911edbb)، [CashPage.java](MAIN_PAGES_BasicData_AR.md#file-ed12e0c67c34)، [MainBage.java](MAIN_PAGES_BasicData_AR.md#file-687a597d8dd2)، [TreasuryHandoverPage.java](MAIN_PAGES_BasicData_AR.md#file-ca1ef3ba6311)، [TypesofdiscountsPage.java](MAIN_PAGES_BasicData_AR.md#file-df58c29cce49)، [CustomerDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-405c1db73294)، [CustomerReceiptPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-8896b80e2ad1)، [ExpenseMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-b36a74638da1)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ElementActions(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public DropdownActions dropdown()` | public | تنفيذ جزء متعلق بـ القائمة المنسدلة، حسب المدخلات والتخصص. |
| `public ValidationActions validation()` | public | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `public WaitActions waits()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public CheckboxActions checkbox()` | public | تنفيذ جزء متعلق بـ مربع الاختيار، حسب المدخلات والتخصص. |
| `public GridActions grid()` | public | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `public PriceListActions PriceList()` | public | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `public UtilityActions Utility()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public AlertAction alert()` | public | تنفيذ جزء متعلق بـ تنبيه المتصفح، حسب المدخلات والتخصص. |
| `public String getProperty(By by, String property)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String getAttribute(By by, String attribute)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void scrollIntoView(WebElement element)` | private | تنفيذ جزء متعلق بـ التمرير، حسب المدخلات والتخصص. |
| `private void jsClick(WebElement element)` | private | تنفيذ جزء متعلق بـ النقر، حسب المدخلات والتخصص. |
| `public void ClickElement(By locator)` | public | انتظار عنصر ظاهر ومفعل ثم الضغط عليه بالطريقة المحددة. |
| `public void typeText(By locator, String value)` | public | كتابة قديمة بمحاولات تحقق وتعامل مع عناصر متغيرة. |
| `public WebElement findElement(By locator)` | public | البحث عن عنصر الشاشة، حسب المدخلات والتخصص. |
| `public String getPageTitle()` | public | قراءة أو إرجاع الصفحة / العنوان، حسب المدخلات والتخصص. |
| `public String getText(By locator)` | public | قراءة أو إرجاع النص، حسب المدخلات والتخصص. |
| `public boolean isDisplayed(By locator)` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public WebElement getVisibleElement(By locator)` | public | قراءة أو إرجاع الظاهر / عنصر الشاشة، حسب المدخلات والتخصص. |
| `public void clickOnEmptyArea()` | public | ضغط إجراء النقر / المنطقة، حسب المدخلات والتخصص. |
| `public void typeTextAndVerify(By locator, String value)` | public | كتابة قديمة موسعة مع تحقق من القيمة وبدائل معالجة. |
| `public By getLastEditButtonForCustomerReceipt()` | public | قراءة أو إرجاع الأخير / الزر / العميل، حسب المدخلات والتخصص. |
| `public By getLastEditButtonForAdjustDamage()` | public | قراءة أو إرجاع الأخير / الزر، حسب المدخلات والتخصص. |
| `public void ClickPlusButtonByRow(int numberOfRows, int timesPerRow)` | public | ضغط إجراء النقر / الزر / الصف، حسب المدخلات والتخصص. |
| `public void ClickMinusButtonByRow(int numberOfRows, int timesPerRow)` | public | ضغط إجراء النقر / الزر / الصف، حسب المدخلات والتخصص. |

<a id="file-550ac2ee26b1"></a>
## GridActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/GridActions.java](../../../src/main/java/Plugin_ERP/Bots/GridActions.java)

**دوره:** قراءة صفوف وجداول وتعديلها وحذفها؛ تميز بين حذف مملوك وحذف عام قديم عند الاستخدام.

**حالته:** مصدر نشط · **عدد أسطره:** 473 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public GridActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void ClickElement(By locator)` | public | انتظار عنصر ظاهر ومفعل ثم الضغط عليه بالطريقة المحددة. |
| `public By getLastEditButton()` | public | قراءة أو إرجاع الأخير / الزر، حسب المدخلات والتخصص. |
| `public void deleteAllRowsSvg(By confirmDeleteButton)` | public | حذف الصفوف، حسب المدخلات والتخصص. |
| `public By getLastDeleteButton()` | public | قراءة أو إرجاع الأخير / الزر، حسب المدخلات والتخصص. |
| `public String getLastRowPk()` | public | قراءة أو إرجاع الأخير / الصف، حسب المدخلات والتخصص. |
| `public String getPkByText(String text)` | public | قراءة أو إرجاع النص، حسب المدخلات والتخصص. |
| `public void clickEditByIndex(int index)` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void deleteRow(By deleteButton, By confirmDeleteButton)` | public | حذف الصف، حسب المدخلات والتخصص. |
| `public void deleteAllRows(By confirmDeleteButton)` | public | حذف الصفوف، حسب المدخلات والتخصص. |
| `private boolean hasRows()` | private | السؤال عن وجود الصفوف، حسب المدخلات والتخصص. |
| `public void waitForTableDataToLoad(int minimumExpectedRows)` | public | انتظار الجدول، حسب المدخلات والتخصص. |
| `public List<WebElement> getTableRows()` | public | قراءة أو إرجاع الجدول / الصفوف، حسب المدخلات والتخصص. |
| `public int getTableRowsCount()` | public | قراءة أو إرجاع الجدول / الصفوف، حسب المدخلات والتخصص. |
| `public boolean isTableDataDisplayed()` | public | السؤال عن حالة الجدول، حسب المدخلات والتخصص. |
| `public void debugPrintAllRows()` | public | تنفيذ جزء متعلق بـ الطباعة / الصفوف، حسب المدخلات والتخصص. |
| `public void clickDeleteRows(int... rowNumbers)` | public | ضغط إجراء النقر / الصفوف، حسب المدخلات والتخصص. |
| `public void deleteRowByPk(String pk, By deleteButton, By confirmDeleteButton)` | public | حذف الصف، حسب المدخلات والتخصص. |
| `public boolean hasRowsByType(String rowTypeText)` | public | السؤال عن وجود الصفوف، حسب المدخلات والتخصص. |
| `public String getLastRowPkByType(String rowTypeText)` | public | قراءة أو إرجاع الأخير / الصف، حسب المدخلات والتخصص. |
| `public By getLastDeleteButtonByType(String rowTypeText)` | public | قراءة أو إرجاع الأخير / الزر، حسب المدخلات والتخصص. |
| `public void deleteAllRowsByType(String rowTypeText, By confirmDeleteButton)` | public | حذف الصفوف، حسب المدخلات والتخصص. |

<a id="file-dab553e492dc"></a>
## InvoiceValidation.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/InvoiceValidation.java](../../../src/main/java/Plugin_ERP/Bots/InvoiceValidation.java)

**دوره:** الملف فارغ تمامًا في النسخة الحالية؛ لا يحتوي تنفيذًا أو كلاسًا نشطًا.

**حالته:** فارغ · **عدد أسطره:** 0

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-8a3132f9a33b"></a>
## LogUtils.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/LogUtils.java](../../../src/main/java/Plugin_ERP/Bots/LogUtils.java)

**دوره:** كتابة رسائل السجل بمستويات مختلفة؛ كتابة INFO ليست assertion.

**حالته:** مصدر نشط · **عدد أسطره:** 36

**ملفات تشير إليه مباشرة:** [Chrome.java](MAIN_Drivers_AR.md#file-14889194f9e4)، [Edge.java](MAIN_Drivers_AR.md#file-e957d0c8ef7e)، [Firefox.java](MAIN_Drivers_AR.md#file-cafcec966ff0)، [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [AbstractForDealer.java](MAIN_PAGES_Shared_AR.md#file-0c718cf5399e)، [DealerFormControls.java](MAIN_PAGES_Shared_AR.md#file-401cc8ed0aa1)، [Agent.java](MAIN_PAGES_DealerModel_AR.md#file-e245cb653316)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private LogUtils()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private static Logger getLogger()` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void info(String message)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void warn(String message)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void error(String message)` | public | تنفيذ جزء متعلق بـ الخطأ، حسب المدخلات والتخصص. |
| `public static void fatal(String message)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void debug(String message)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-3dc867d1513c"></a>
## PriceListActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/PriceListActions.java](../../../src/main/java/Plugin_ERP/Bots/PriceListActions.java)

**دوره:** عمليات قديمة خاصة بمستخدمي/أصناف وأسعار قائمة أسعار، رغم وجودها داخل أدوات عامة.

**حالته:** مصدر نشط · **عدد أسطره:** 266 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public PriceListActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public List<String> getSelectedProducts()` | public | قراءة أو إرجاع القيمة المختارة، حسب المدخلات والتخصص. |
| `public List<String> getEnteredPrices()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void enterPriceByRowIndex(Object... rowsAndPrices)` | public | تنفيذ جزء متعلق بـ السعر / الصف، حسب المدخلات والتخصص. |
| `public void selectUsersByCodeFprPriceList(int... codes)` | public | اختيار الكود / السعر، حسب المدخلات والتخصص. |
| `public By getLastEditButtonForPriceList()` | public | قراءة أو إرجاع الأخير / الزر / السعر، حسب المدخلات والتخصص. |
| `public void deleteRowForPriceList()` | public | حذف الصف / السعر، حسب المدخلات والتخصص. |
| `public String getLastRowPk()` | public | قراءة أو إرجاع الأخير / الصف، حسب المدخلات والتخصص. |
| `public void ClickElement(By locator)` | public | انتظار عنصر ظاهر ومفعل ثم الضغط عليه بالطريقة المحددة. |

<a id="file-e16473182bff"></a>
## UtilityActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/UtilityActions.java](../../../src/main/java/Plugin_ERP/Bots/UtilityActions.java)

**دوره:** تواريخ وتشخيص قيم وعناصر ومساعدات متنوعة.

**حالته:** مصدر نشط · **عدد أسطره:** 156 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public UtilityActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public Void printLocatorCount(By locator)` | public | تنفيذ جزء متعلق بـ الطباعة / محدد العنصر، حسب المدخلات والتخصص. |
| `public String TypeTodayDate(String pattern)` | public | كتابة التاريخ، حسب المدخلات والتخصص. |
| `public String TypePastYearDate(String pattern)` | public | كتابة التاريخ، حسب المدخلات والتخصص. |
| `public String generateRandomNumber()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public double getNumber(By locator)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public BigDecimal getNormalizedAmount(By locator)` | public | قراءة أو إرجاع المبلغ، حسب المدخلات والتخصص. |
| `public void getInputValue(By locator, String expectedValue)` | public | قراءة أو إرجاع القيمة، حسب المدخلات والتخصص. |
| `private String normalizeNumber(String value)` | private | توحيد صيغة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-122f5533f792"></a>
## ValidationActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/ValidationActions.java](../../../src/main/java/Plugin_ERP/Bots/ValidationActions.java)

**دوره:** قراءة وتطبيع ومطابقة نصوص وأرقام وتواريخ ظاهرة.

**حالته:** مصدر نشط · **عدد أسطره:** 219 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ValidationActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private String getText(By locator)` | private | قراءة أو إرجاع النص، حسب المدخلات والتخصص. |
| `public String normalizePrice(String value)` | public | توحيد صيغة السعر، حسب المدخلات والتخصص. |
| `private String normalizeDate(String date)` | private | توحيد صيغة التاريخ، حسب المدخلات والتخصص. |
| `private double normalizeNumericValue(String value)` | private | توحيد صيغة القيمة، حسب المدخلات والتخصص. |
| `public void assertRequiredMsg(By locator, String expectedMsg)` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void assertValue(String expected, By locator)` | public | مقارنة متوقعة القيمة، حسب المدخلات والتخصص. |
| `public boolean IsTextDisplayed(String text)` | public | السؤال عن حالة النص، حسب المدخلات والتخصص. |
| `public boolean IsPriceDisplayed(String expectedPrice)` | public | السؤال عن حالة السعر، حسب المدخلات والتخصص. |
| `public boolean IsPriceDisplayedForFin(String expectedPrice)` | public | السؤال عن حالة السعر، حسب المدخلات والتخصص. |
| `public boolean IsDateDisplayed(String expectedDate)` | public | السؤال عن حالة التاريخ، حسب المدخلات والتخصص. |
| `public boolean IsNumericPriceDisplayed(String expectedPrice)` | public | السؤال عن حالة السعر، حسب المدخلات والتخصص. |

<a id="file-0ea40183d75b"></a>
## WaitActions.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/WaitActions.java](../../../src/main/java/Plugin_ERP/Bots/WaitActions.java)

**دوره:** انتظار روابط وعناصر وجداول وتقارير وحالات شاشة.

**حالته:** مصدر نشط · **عدد أسطره:** 268 · **الأب المباشر:** `BaseAction`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAction.java](MAIN_Bots_AR.md#file-76e2f2dada2e)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public WaitActions(WebDriver driver, WebDriverWait wait, WaitManagement waitMangment)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void waitForUrlContains(String text)` | public | انتظار عنوان الشاشة، حسب المدخلات والتخصص. |
| `public void waitForElementVisible(By locator)` | public | انتظار عنصر الشاشة / الظاهر، حسب المدخلات والتخصص. |
| `public void WaitForElementVisible(By locator)` | public | انتظار عنصر الشاشة / الظاهر، حسب المدخلات والتخصص. |
| `public void waitForReportReady(int timeoutSec)` | public | انتظار التقرير / الجاهزية، حسب المدخلات والتخصص. |
| `public void switchToNewTab()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void switchToNewTab(Set<String> originalTabs)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitForOverlayToDisappear()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void waitForScreenReady(By locator)` | public | انتظار الجاهزية، حسب المدخلات والتخصص. |
| `public void waitForExchangeEditPageForEditBills(By locator1, By locator2)` | public | انتظار الصفحة، حسب المدخلات والتخصص. |
| `public void waitForExchangeEditPage(By locator1)` | public | انتظار الصفحة، حسب المدخلات والتخصص. |
| `public WebElement waitForQuantityInputByRow(int rowNumber)` | public | انتظار الكمية / الصف، حسب المدخلات والتخصص. |
| `public WebElement waitForInputByRow(String columnName, int rowNumber)` | public | انتظار الصف، حسب المدخلات والتخصص. |

<a id="file-98afc8ddbc07"></a>
## WaitManagement.java

**المصدر:** [src/main/java/Plugin_ERP/Bots/WaitManagement.java](../../../src/main/java/Plugin_ERP/Bots/WaitManagement.java)

**دوره:** بناء FluentWait من مهلة الإعداد وقائمة الاستثناءات المؤقتة.

**حالته:** مصدر نشط · **عدد أسطره:** 35

**يرتبط مباشرة بملفات داخل المشروع:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [TreasuryHandoverPage.java](MAIN_PAGES_BasicData_AR.md#file-ca1ef3ba6311)، [CustomerDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-405c1db73294)، [CustomerReceiptPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-8896b80e2ad1)، [ExpenseMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-b36a74638da1)، [OtherRevenueMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-3b11f1fa1481)، [SupplierBillOfExchangePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-3333dbde6ae0)، [SupplierDiscountNoticePage.java](MAIN_PAGES_FinancialMovements_AR.md#file-f3864dce667c)، [TransfersAndSettlementsPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-47e7576bf489)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public WaitManagement(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public FluentWait<WebDriver> fluentWait()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private ArrayList<Class<? extends Exception>> getExceptions()` | private | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
