# اختبارات ودعم المكونات والإطار

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-754dbe9b1ba7"></a>
## BankDeleteOrderingTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/BasicData/BankDeleteOrderingTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/BasicData/BankDeleteOrderingTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 37

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.BasicData.BankDeleteOrderingTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9483fc0d6f76"></a>
## CashListControlsTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/BasicData/CashListControlsTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/BasicData/CashListControlsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 90

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void waitsForFirstRowWhenProviderFinishesBeforeDomRowsRender()` | public · @Test | حالة تحقق مرتبطة بـالأول / الصف / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void waitsForTheProviderBeforeReadingGridRows()` | public · @Test | حالة تحقق مرتبطة بـالجدول / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private WebElement element(String text, List<WebElement> children)` | private | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |
| `public Object[][] numericDirections()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ordersNumericCodesWithoutMutatingTheDisplayedSnapshot( boolean ascending, List<String> expected)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void keepsTheCaseInsensitiveFallbackForNonNumericCodes()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.BasicData.CashListControlsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-165f96b38937"></a>
## DocumentPageIsolationTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/DocumentPageIsolationTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/DocumentPageIsolationTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 55

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static WebDriver browser()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.DocumentPageIsolationTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-aad377edf919"></a>
## InvoiceCalendarMonthTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceCalendarMonthTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceCalendarMonthTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 18

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void readsArabicEnglishAndNumericCalendarCaptions(String caption, int expected) throws Exception` | public · @Test | حالة تحقق مرتبطة بـالعربي / الإنجليزي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.Invoice.InvoiceCalendarMonthTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-c38a57f6d427"></a>
## InvoiceListExportTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListExportTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/Invoice/InvoiceListExportTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 78

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] formats()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void clicksTheVisibleExportExactlyOnce(String format)` | public · @Test | حالة تحقق مرتبطة بـالظاهر / التصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void hiddenExactControlFallsBackToVisibleSemanticControl()` | public · @Test | حالة تحقق مرتبطة بـالظاهر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] unsupported()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void rejectsUnsupportedFormatBeforeTouchingBrowser(String format)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private WebDriver driver(List<WebElement> controls)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private WebElement element(boolean visible, AtomicInteger clicks)` | private | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.Invoice.InvoiceListExportTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-72093b88e0cd"></a>
## SalesListScopeTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/Invoice/SalesListScopeTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/Invoice/SalesListScopeTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 68

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `void scope()` | داخل الحزمة | تنفيذ جزء متعلق بـ النطاق، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.Invoice.SalesListScopeTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-3e9c83e99b1d"></a>
## InvoiceRowReadersTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/InvoiceRowReadersTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/InvoiceRowReadersTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 78

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void salesReadsFractionalValuesAndSkipsHiddenRows()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات / القيم / الصفوف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void purchaseRetainsItsExistingAllRowsPolicyAndPurchasePriceColumn()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات / الصفوف / السعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void legacySnapshotEntryPointsKeepNamesQuantitiesAndSalesPrices()` | public · @Test | حالة تحقق مرتبطة بـالمبيعات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private static WebDriver grid()` | private | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `private static WebElement row(String name, boolean visible)` | private | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `private static WebElement cell(String value)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.InvoiceRowReadersTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-8ff02d32e90f"></a>
## PageComponentDispatchTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/PageComponentDispatchTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/PageComponentDispatchTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 90 · **الأب المباشر:** `AbstractForPurchases`

**يرتبط مباشرة بملفات داخل المشروع:** [InvoiceRow.java](MAIN_Models_AR.md#file-dff4d2713009)، [InvoiceTotals.java](MAIN_Models_AR.md#file-027b46379317)، [ReportTotals.java](MAIN_Models_AR.md#file-26aee4dee40f)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)، [AbstractForPurchases.java](MAIN_PAGES_Shared_AR.md#file-fe552e2e485f)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private static WebDriver noBrowser()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void purchaseCalculationRetainsSubclassReadersAndPolicies()` | public · @Test | حالة تحقق مرتبطة بـالمشتريات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] tolerances()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void invoiceAssertionsRetainRoundingTolerance(String actualNet, boolean accepted)` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] taxModes()` | public | تنفيذ جزء متعلق بـ الضريبة، حسب المدخلات والتخصص. |
| `public void reportAssertionsRetainTaxModeAndDetectWrongNet(TaxMode mode)` | public · @Test | حالة تحقق مرتبطة بـالتقرير / الضريبة / الصافي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private static InvoiceTotals totals()` | private | تنفيذ جزء متعلق بـ الإجماليات، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.PageComponentDispatchTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-74dae15aa3f4"></a>
## PurchaseCalendarTest.java

**المصدر:** [src/test/java/Plugin_ERP/System_Modules/Login/purches/PurchaseCalendarTest.java](../../../src/test/java/Plugin_ERP/System_Modules/Login/purches/PurchaseCalendarTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 47

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] calendarCaptions()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void recognizesCalendarCaption(String caption, int expectedMonth)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] unknownCaptions()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void rejectsUnknownCaptionExplicitly(String caption)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.System_Modules.Login.purches.PurchaseCalendarTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-d2ce0f84bf3e"></a>
## AllureReportPolicyTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/Allur/AllureReportPolicyTest.java](../../../src/test/java/Plugin_ERP/Utiel/Allur/AllureReportPolicyTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 54

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] policies()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ciDefaultsKeepRawResultsAndExplicitFlagsControlHtml( String ci, String generate, String open, boolean expectedGenerate, boolean expectedOpen)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] unsafePaths()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void refusesCleaningTheProjectSourcesOrAnOutsideDirectory(String path)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void dedicatedRunPathsAreResolvedWithinTheProject()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.Allur.AllureReportPolicyTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-8a1aa606c293"></a>
## DriveReportStoreTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/Allur/DriveReportStoreTest.java](../../../src/test/java/Plugin_ERP/Utiel/Allur/DriveReportStoreTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 146

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.Allur.DriveReportStoreTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-21988170bbab"></a>
## ReportMailTemplateTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/Allur/ReportMailTemplateTest.java](../../../src/test/java/Plugin_ERP/Utiel/Allur/ReportMailTemplateTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 25

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.Allur.ReportMailTemplateTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-1c0ef77c1509"></a>
## ReportMailTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/Allur/ReportMailTest.java](../../../src/test/java/Plugin_ERP/Utiel/Allur/ReportMailTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 33

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.Allur.ReportMailTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-a47d5925ca66"></a>
## ScenarioAccessTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/Allur/ScenarioAccessTest.java](../../../src/test/java/Plugin_ERP/Utiel/Allur/ScenarioAccessTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 63

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.Allur.ScenarioAccessTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-cb14841e9765"></a>
## ScenarioJourneyReportTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/Allur/ScenarioJourneyReportTest.java](../../../src/test/java/Plugin_ERP/Utiel/Allur/ScenarioJourneyReportTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 90

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private List<io.qameta.allure.model.StepResult> report(Runnable scenario, boolean reporting)` | private | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.Allur.ScenarioJourneyReportTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-a3f7cd12cb7b"></a>
## ConfigurationPropertiesTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/ConfigurationPropertiesTest.java](../../../src/test/java/Plugin_ERP/Utiel/ConfigurationPropertiesTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 203

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void reloadReflectsChangedAddedAndRemovedKeys() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletingAResourceFileRemovesItsValues() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void localCredentialsReloadAndFallBackWhenTheFileIsRemoved() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void explicitJvmOverridesWinAndCanBeChangedOrClearedWithoutReload() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void clearingAnOverrideRevealsTheLatestReloadedFileValue() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void blankCredentialEnvironmentValuesFallBackAndUnrelatedVariablesAreIgnored() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void snapshotsCannotModifyTheCacheOrPolluteJvmOverrides() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void failedReloadDoesNotPublishPartiallyLoadedValues() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقيم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void invalidLocalFileFailsExplicitlyInsteadOfUsingPartialSettings() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void nestedFilesHaveDeterministicPrecedenceAndKeepUtf8Text() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالنص؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void publicReaderKeepsDynamicJvmOverridesWithoutExportingFileSettings()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `Path resource(String relative, String text) throws IOException` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void local(String text) throws IOException` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.ConfigurationPropertiesTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e1dfe823fa6e"></a>
## DropdownPolicyTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/DropdownPolicyTest.java](../../../src/test/java/Plugin_ERP/Utiel/DropdownPolicyTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 86

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] policies()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void selectsAccordingToModulePolicy(String mode, List<String> labels, String expected)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void exactPolicyDoesNotAcceptContainedName()` | public · @Test | حالة تحقق مرتبطة بـالاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ambiguousFallbackKeepsDiagnosticMessageAndSelectsNothing()` | public · @Test | حالة تحقق مرتبطة بـالرسالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void duplicateExactMatchesAreRejectedBeforeFallback()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `FormInteractions actions()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement element(String label)` | داخل الحزمة | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.DropdownPolicyTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-4c5b29f46561"></a>
## FormInteractionsTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/FormInteractionsTest.java](../../../src/test/java/Plugin_ERP/Utiel/FormInteractionsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 109

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public Object[][] matchingPolicies()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void preservesModuleOptionMatching(String policy, String text, boolean expected)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public Object[][] inputModes()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void preservesInputReplacementAndBlur(String mode, String value, boolean blur)` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void ignoresHiddenAndDisabledInputs()` | public · @Test | حالة تحقق مرتبطة بـالتعطيل؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void clicksOnlyAvailableElement()` | public · @Test | حالة تحقق مرتبطة بـعنصر الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `FormInteractions actions()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `WebElement element(boolean shown, boolean enabled)` | داخل الحزمة | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.FormInteractionsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-755f5d74c592"></a>
## InteractionAccuracyTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/InteractionAccuracyTest.java](../../../src/test/java/Plugin_ERP/Utiel/InteractionAccuracyTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 100

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private WebDriver browser(Call call)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private WebElement element(String text, boolean enabled, boolean blocked, AtomicInteger clicks)` | private | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.InteractionAccuracyTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-3c58e9522f19"></a>
## PrintedInvoiceAssertionsTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/PrintedInvoiceAssertionsTest.java](../../../src/test/java/Plugin_ERP/Utiel/PrintedInvoiceAssertionsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 35

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private Cell cell(String text,double x,double y)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private List<Cell> report()` | private | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `private void verify(List<Cell> cells)` | private | مراجعة سياق أو عقد حسب الكلاس، مثل ثبات الفرع والعنوان. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.PrintedInvoiceAssertionsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-d52ac859077b"></a>
## SalesNavigationTest.java

**المصدر:** [src/test/java/Plugin_ERP/Utiel/SalesNavigationTest.java](../../../src/test/java/Plugin_ERP/Utiel/SalesNavigationTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 22 · **الأب المباشر:** `SalesModulePage`

**يرتبط مباشرة بملفات داخل المشروع:** [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `static String path(String route)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Utiel.SalesNavigationTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-d14a7d3cbf66"></a>
## LegacyAssertionsTest.java

**المصدر:** [src/test/java/Plugin_ERP/Valditon/LegacyAssertionsTest.java](../../../src/test/java/Plugin_ERP/Valditon/LegacyAssertionsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 161 · **الأب المباشر:** `BaseAssertion`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseAssertion.java](MAIN_Valditon_AR.md#file-aa32f2af2150)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void visibleElementPasses()` | public · @Test | حالة تحقق مرتبطة بـالظاهر / عنصر الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void hiddenElementFailsEvenWhenPresent()` | public · @Test | حالة تحقق مرتبطة بـعنصر الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void waitsForHiddenElementToBecomeVisible()` | public · @Test | حالة تحقق مرتبطة بـعنصر الشاشة / الظاهر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void missingElementFails()` | public · @Test | حالة تحقق مرتبطة بـعنصر الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void staleElementCanBeReacquired()` | public · @Test | حالة تحقق مرتبطة بـعنصر الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void browserFailureIsNotReportedAsInvisibility()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void hiddenElementIsCollectedBySoftAssertion()` | public · @Test | حالة تحقق مرتبطة بـعنصر الشاشة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void softFailuresAreCombinedAndResetAfterFailure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void workersDoNotConsumeEachOthersSoftFailures() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageAndValueChecksKeepActualExpectedOrder()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / القيمة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private static void withWait(String seconds, Runnable action)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static WebDriver browser(Supplier<WebElement> lookup)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static WebElement element(boolean displayed)` | private | تنفيذ جزء متعلق بـ عنصر الشاشة، حسب المدخلات والتخصص. |
| `void visible()` | داخل الحزمة | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `void url(String expected)` | داخل الحزمة | تنفيذ جزء متعلق بـ عنوان الشاشة، حسب المدخلات والتخصص. |
| `void title(String expected)` | داخل الحزمة | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `void equal(String actual, String expected, String message)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertrue(boolean condition, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void asserfalse(boolean condition, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertequals(Object actual, Object expected, String message)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void visible()` | داخل الحزمة | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `void check(boolean condition, String message)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void equal(Object actual, Object expected, String message)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `Plugin_ERP.Valditon.LegacyAssertionsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

