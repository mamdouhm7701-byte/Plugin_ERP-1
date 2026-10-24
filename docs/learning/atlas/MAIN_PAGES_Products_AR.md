# صفحات ومكونات المنتجات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-6586d052ceb9"></a>
## AddProductsPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/AddProductsPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/AddProductsPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 270 · **الأب المباشر:** `ProductsModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [ProductsModulePage.java](MAIN_PAGES_Products_AR.md#file-2453b3e5c73a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void prStock(String store, String opening, String minimum, String cost)` | public | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prSubunit(String unit, String barcode, String rate)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prComponent(String store, String item, String quantity)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public AddProductsPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenAddProductPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void ClickNewProduct()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void AddProductName(String arabicName)` | public | إضافة الاسم، حسب المدخلات والتخصص. |
| `public void AddProductEnglishName(String englishName)` | public | إضافة الإنجليزي / الاسم، حسب المدخلات والتخصص. |
| `public void AddBarcode(String barcode)` | public | إضافة الباركود، حسب المدخلات والتخصص. |
| `public void AddSalesPrice(String salePrice)` | public | إضافة المبيعات / السعر، حسب المدخلات والتخصص. |
| `public void AddMiniSalePrice(String miniSalePrice)` | public | إضافة السعر، حسب المدخلات والتخصص. |
| `public void AddMaxSalePrice(String maxSalePrice)` | public | إضافة السعر، حسب المدخلات والتخصص. |
| `public void AddPlace(String placeOfProduction)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddCostPrice(String costPrice)` | public | إضافة السعر، حسب المدخلات والتخصص. |
| `public void AddOpenAmount(String OpenAmount)` | public | إضافة المبلغ، حسب المدخلات والتخصص. |
| `public void AddLowestAvailableQuantity(String lowestAvailableQuantity)` | public | إضافة الكمية، حسب المدخلات والتخصص. |
| `public void AddStore()` | public | إضافة المخزن، حسب المدخلات والتخصص. |
| `public void ClickUnitProduct()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void AddTransUnit(String transUnit)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AddBarcode_2(String addBarcode)` | public | إضافة الباركود، حسب المدخلات والتخصص. |
| `public void AddSalePrice_2(String addSalePrice)` | public | إضافة السعر، حسب المدخلات والتخصص. |
| `public void AddMiniSalePrice_2(String addMiniSalePrice)` | public | إضافة السعر، حسب المدخلات والتخصص. |
| `public void AddMaxSalePrice_2(String addMaxSalePrice)` | public | إضافة السعر، حسب المدخلات والتخصص. |
| `public void ClickAddUnit()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickControlProduct()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickSaveEditButton()` | public | ضغط إجراء النقر / الزر، حسب المدخلات والتخصص. |
| `public void ClickDeleteStore()` | public | ضغط إجراء النقر / المخزن، حسب المدخلات والتخصص. |
| `public void ClickCompletedProduct()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickComponentTab()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickAddComponent()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void SearchComponent(String searchComponent)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void SelectProduct(String productName)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ClickCloseComponent()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void AddComponentQuantity(String value)` | public | إضافة الكمية، حسب المدخلات والتخصص. |
| `public void AddComponentNotes(String notes)` | public | إضافة الملاحظات، حسب المدخلات والتخصص. |
| `public boolean AssertBarcodeRequired()` | public | مقارنة متوقعة الباركود، حسب المدخلات والتخصص. |

<a id="file-19a235d0bf21"></a>
## ProductCategories.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductCategories.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductCategories.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 46 · **الأب المباشر:** `ProductsModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [ProductsModulePage.java](MAIN_PAGES_Products_AR.md#file-2453b3e5c73a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ProductCategories(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenProductCategoriesPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void AddNoteForProductCategory(String Notes)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean AssertErrorMessageForRequiredField()` | public | مقارنة متوقعة الخطأ / الرسالة، حسب المدخلات والتخصص. |

<a id="file-e20449d1a37e"></a>
## ProductComponents.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductComponents.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductComponents.java)

**دوره:** إجراءات وحدات الصنف أو مكوناته، مفصولة عن حقول بياناته الأساسية.

**حالته:** مصدر نشط · **عدد أسطره:** 62

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ProductComponents(AddProductsPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void ClickCompletedProduct()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickComponentTab()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void ClickAddComponent()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void SearchComponent(String searchComponent)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void SelectProduct(String productName)` | داخل الحزمة | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void ClickCloseComponent()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void AddComponentQuantity(String value)` | داخل الحزمة | إضافة الكمية، حسب المدخلات والتخصص. |
| `void AddComponentNotes(String notes)` | داخل الحزمة | إضافة الملاحظات، حسب المدخلات والتخصص. |

<a id="file-2a560a5f90c3"></a>
## ProductListPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductListPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductListPage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 134 · **الأب المباشر:** `ProductsModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [ProductsModulePage.java](MAIN_PAGES_Products_AR.md#file-2453b3e5c73a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void prAddItem(String name, String price)` | public | إضافة الصنف، حسب المدخلات والتخصص. |
| `public void prItemPrice(String name, String price)` | public | تنفيذ جزء متعلق بـ الصنف / السعر، حسب المدخلات والتخصص. |
| `public ProductListPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private By reportText(String text)` | private | تنفيذ جزء متعلق بـ التقرير / النص، حسب المدخلات والتخصص. |
| `private By reportRow(String productName, String price)` | private | تنفيذ جزء متعلق بـ التقرير / الصف، حسب المدخلات والتخصص. |
| `public void OpenProductListPage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public void ClickAddProduct()` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void ClickCloseButton()` | public | إغلاق نموذج مع مراعاة حالة الشاشة. |
| `public void ClickSaveAndPrintButton_1()` | public | ضغط إجراء النقر / الطباعة / الزر، حسب المدخلات والتخصص. |
| `public void SelectProductByCode(int... codes)` | public | اختيار الكود، حسب المدخلات والتخصص. |
| `public void EditPriceList()` | public | تعديل السعر، حسب المدخلات والتخصص. |
| `public void DeletePriceList()` | public | حذف السعر، حسب المدخلات والتخصص. |
| `public void AddNewProductList(int... prices)` | public | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void AssertPriceListReport()` | public | مقارنة متوقعة السعر / التقرير، حسب المدخلات والتخصص. |
| `public boolean AssertErorrMsg()` | public | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-e938529cd60b"></a>
## ProductRecordList.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductRecordList.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductRecordList.java)

**دوره:** قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.

**حالته:** مصدر نشط · **عدد أسطره:** 57

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ProductRecordList(ProductsModulePage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private void prClick(By locator)` | private | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `private List<WebElement> prNameInputs()` | private | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `void prSearch(String name)` | داخل الحزمة | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `boolean prHasRow(String name)` | داخل الحزمة | السؤال عن وجود الصف، حسب المدخلات والتخصص. |
| `void prWaitRow(String name)` | داخل الحزمة | انتظار الصف، حسب المدخلات والتخصص. |
| `void prEdit(String name)` | داخل الحزمة | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void prDelete(String name)` | داخل الحزمة | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void prRowAction(String name,String action)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |

<a id="file-92929c15a0d4"></a>
## ProductSaveCapture.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductSaveCapture.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductSaveCapture.java)

**دوره:** تتبع محاولات ونتائج الحفظ المملوكة لهذا المجال؛ منفصل عن نقر زر الحفظ ومقارنة قواعد العمل.

**حالته:** مصدر نشط · **عدد أسطره:** 50

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ProductSaveCapture(ProductsModulePage owner, WebDriver driver, WebDriverWait ready)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void prCapture(String marker)` | داخل الحزمة | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `String prCaptureMarker()` | داخل الحزمة | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `List<Map<String,Object>> prSaves()` | داخل الحزمة | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `List<Map<String,Object>> prAttempts()` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `void prSettled()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String prSaveErrors()` | داخل الحزمة | حفظ الأخطاء، حسب المدخلات والتخصص. |

<a id="file-2453b3e5c73a"></a>
## ProductsModulePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductsModulePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductsModulePage.java)

**دوره:** واجهة تنسيق قدرات مجال متكرر؛ تستدعي مكونات متخصصة وتحتفظ بنقاط دخول الاختبارات.

**حالته:** مصدر نشط · **عدد أسطره:** 180 · **الأب المباشر:** `Abstract`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [AddProductsPage.java](MAIN_PAGES_Products_AR.md#file-6586d052ceb9)، [ProductCategories.java](MAIN_PAGES_Products_AR.md#file-19a235d0bf21)، [ProductListPage.java](MAIN_PAGES_Products_AR.md#file-2a560a5f90c3)، [UnitePage.java](MAIN_PAGES_Products_AR.md#file-0299b6a306a3)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ProductsModulePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void prAwaitSession()` | public | انتظار الجلسة، حسب المدخلات والتخصص. |
| `public void prOpen(Pages route)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prNew()` | public | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean prFormVisible()` | public | تنفيذ جزء متعلق بـ النموذج / الظاهر، حسب المدخلات والتخصص. |
| `public boolean prFormPresent()` | public | تنفيذ جزء متعلق بـ النموذج، حسب المدخلات والتخصص. |
| `private List<WebElement> prNameInputs()` | private | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `protected WebElement visible(By locator)` | protected | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `public void prType(By locator,String value)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void prClick(By locator)` | protected | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void prArabic(String name)` | public | تنفيذ جزء متعلق بـ العربي، حسب المدخلات والتخصص. |
| `public void prEnglish(String name)` | public | تنفيذ جزء متعلق بـ الإنجليزي، حسب المدخلات والتخصص. |
| `public void prNotes(String notes)` | public | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `public void prField(String name,String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prTitledField(String title,String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prChoose(String label,String name)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prCheck(String label,boolean selected)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prTab(String text)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prButton(String text)` | public | تنفيذ جزء متعلق بـ الزر، حسب المدخلات والتخصص. |
| `public void prSave()` | public | حفظ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prSavePrint()` | public | حفظ الطباعة، حسب المدخلات والتخصص. |
| `public void prDismiss()` | public | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prWaitList()` | public | انتظار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prDiscard()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String prValidation()` | public | تنفيذ جزء متعلق بـ التحقق، حسب المدخلات والتخصص. |
| `public void prSearch(String name)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public boolean prHasRow(String name)` | public | السؤال عن وجود الصف، حسب المدخلات والتخصص. |
| `public void prWaitRow(String name)` | public | انتظار الصف، حسب المدخلات والتخصص. |
| `public void prEdit(String name)` | public | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void prDelete(String name)` | public | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void prRowAction(String name,String action)` | private | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `public void prConfirm(boolean confirm)` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public void prExport(String format)` | public | تصدير التصدير، حسب المدخلات والتخصص. |
| `public List<String> prHeaders()` | public | تنفيذ جزء متعلق بـ عناوين الأعمدة، حسب المدخلات والتخصص. |
| `public void prCapture(String marker)` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `public String prCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public List<Map<String,Object>> prSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String,Object>> prAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public void prSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String prSaveErrors()` | public | حفظ الأخطاء، حسب المدخلات والتخصص. |
| `public void prAwaitReport()` | public | انتظار التقرير، حسب المدخلات والتخصص. |
| `public void prPickItem(String name)` | public | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `public void prClosePicker()` | public | إغلاق نافذة الاختيار، حسب المدخلات والتخصص. |
| `public int prFormRowsContaining(String name)` | public | تنفيذ جزء متعلق بـ النموذج / الصفوف، حسب المدخلات والتخصص. |
| `void clickRecordControl(By locator)` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `List<WebElement> recordNameInputs()` | داخل الحزمة | تسجيل/تمرير الاسم، حسب المدخلات والتخصص. |

<a id="file-a3d2ce6e3217"></a>
## ProductUnits.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductUnits.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/ProductUnits.java)

**دوره:** إجراءات وحدات الصنف أو مكوناته، مفصولة عن حقول بياناته الأساسية.

**حالته:** مصدر نشط · **عدد أسطره:** 58

**يرتبط مباشرة بملفات داخل المشروع:** [AlertAction.java](MAIN_Bots_AR.md#file-741424a4cfd7)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ProductUnits(AddProductsPage owner, WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void ClickUnitProduct()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `void AddTransUnit(String transUnit)` | داخل الحزمة | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void AddBarcode_2(String addBarcode)` | داخل الحزمة | إضافة الباركود، حسب المدخلات والتخصص. |
| `void AddSalePrice_2(String addSalePrice)` | داخل الحزمة | إضافة السعر، حسب المدخلات والتخصص. |
| `void AddMiniSalePrice_2(String addMiniSalePrice)` | داخل الحزمة | إضافة السعر، حسب المدخلات والتخصص. |
| `void AddMaxSalePrice_2(String addMaxSalePrice)` | داخل الحزمة | إضافة السعر، حسب المدخلات والتخصص. |
| `void ClickAddUnit()` | داخل الحزمة | ضغط إجراء النقر، حسب المدخلات والتخصص. |

<a id="file-0299b6a306a3"></a>
## UnitePage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Products/UnitePage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Products/UnitePage.java)

**دوره:** تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.

**حالته:** مصدر نشط · **عدد أسطره:** 35 · **الأب المباشر:** `ProductsModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [ProductsModulePage.java](MAIN_PAGES_Products_AR.md#file-2453b3e5c73a)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public UnitePage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void OpenUnitePage()` | public | فتح الصفحة، حسب المدخلات والتخصص. |
| `public boolean AssertUniteNameRequired()` | public | مقارنة متوقعة الاسم، حسب المدخلات والتخصص. |

