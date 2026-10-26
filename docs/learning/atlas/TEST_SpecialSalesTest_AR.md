# اختبارات ودعم الكاشير والمطعم

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-926b350dd2f5"></a>
## KitchenOrdersCoverageTest.java

**المصدر:** [src/test/java/SpecialSalesTest/KitchenOrdersCoverageTest.java](../../../src/test/java/SpecialSalesTest/KitchenOrdersCoverageTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 49 · **الأب المباشر:** `SpecialSalesScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [SpecialSalesScenario.java](TEST_SpecialSalesTest_AR.md#file-8fab08c67fe6)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SpecialSalesPage createPage()` | protected | إنشاء الصفحة، حسب المدخلات والتخصص. |
| `public void preparesDeliversAndClosesOnlyOwnedOrder()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void clickVisibleSquare(String checkbox)` | private | ضغط إجراء النقر / الظاهر، حسب المدخلات والتخصص. |
| `private void awaitLineFlag(SpecialSalesTestData.Record invoice,String flag)` | private | انتظار البند، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SpecialSalesTest.KitchenOrdersCoverageTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-e1f07bc4e59b"></a>
## PointOfSaleTest.java

**المصدر:** [src/test/java/SpecialSalesTest/PointOfSaleTest.java](../../../src/test/java/SpecialSalesTest/PointOfSaleTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 319 · **الأب المباشر:** `SpecialSalesScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [SpecialSalesScenario.java](TEST_SpecialSalesTest_AR.md#file-8fab08c67fe6)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SpecialSalesPage createPage()` | protected | إنشاء الصفحة، حسب المدخلات والتخصص. |
| `private SpecialSalesTestData.Record paidInvoice()` | private | تنفيذ جزء متعلق بـ الفاتورة، حسب المدخلات والتخصص. |
| `private void deleteInvoice(SpecialSalesTestData.Record invoice, boolean confirm)` | private | حذف الفاتورة، حسب المدخلات والتخصص. |
| `public void ownedDefaultsAndChannel()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void barcodeAddsItem()` | public · @Test | حالة تحقق مرتبطة بـالباركود / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void quantityButtons()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalQuantity()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removeCartLine()` | public · @Test | حالة تحقق مرتبطة بـالبند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void multipleItems()` | public · @Test | حالة تحقق مرتبطة بـالأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void paymentTypeSwitch()` | public · @Test | حالة تحقق مرتبطة بـالسداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesCreditAndDeductsStock()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / الدائن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesCashWithOwnedPayment()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / المملوك للاختبار / السداد؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void localHoldDoesNotDeductStock()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void editingSavedQuantityAppliesOnlyDifference()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletingSavedInvoiceRestoresStock()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cancellingDeleteKeepsInvoice()` | public · @Test | حالة تحقق مرتبطة بـالفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void printsSavedReceiptWithoutPostingAgain()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesAndPrintsOneInvoice()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SpecialSalesTest.PointOfSaleTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-fbdb31d7a2fb"></a>
## RestaurantInvoiceTest.java

**المصدر:** [src/test/java/SpecialSalesTest/RestaurantInvoiceTest.java](../../../src/test/java/SpecialSalesTest/RestaurantInvoiceTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 301 · **الأب المباشر:** `SpecialSalesScenario`

**يرتبط مباشرة بملفات داخل المشروع:** [SpecialSalesScenario.java](TEST_SpecialSalesTest_AR.md#file-8fab08c67fe6)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected SpecialSalesPage createPage()` | protected | إنشاء الصفحة، حسب المدخلات والتخصص. |
| `private RestaurantInvoicePage restaurant()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private SpecialSalesTestData.Record holdOrder()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void reopen(SpecialSalesTestData.Record invoice)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void ownedDefaultsAndChannel()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void catalogAddsItem()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void quantityButtons()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removeCartLine()` | public · @Test | حالة تحقق مرتبطة بـالبند؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deliveryAndExternal()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void holdDeductsStockImmediately()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void reopeningHoldKeepsItems()` | public · @Test | حالة تحقق مرتبطة بـالأصناف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void reopeningHoldKeepsNotes()` | public · @Test | حالة تحقق مرتبطة بـالملاحظات؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void addingToHoldDeductsOnlyIncrement()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void payingHoldDoesNotDeductAgain()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesPaidExternalOrder()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void fractionalQuantity()` | public · @Test | حالة تحقق مرتبطة بـالكمية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletingPaidOrderRestoresStock()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void printsPaidReceiptWithoutPostingAgain()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void savesAndPrintsOneInvoice()` | public · @Test | حالة تحقق مرتبطة بـعمليات الحفظ / الفاتورة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SpecialSalesTest.RestaurantInvoiceTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-3eb626a896b4"></a>
## SpecialSalesCleanupTest.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesCleanupTest.java](../../../src/test/java/SpecialSalesTest/SpecialSalesCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 176

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void normalCustomerIsTaxableAndKeepsItsOwnAccount()` | public · @Test | حالة تحقق مرتبطة بـالعميل / الحساب؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void sharesReferencesAndIsolatesCases()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void restoresDefaultsBeforeDeletingReferences()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesChangedOwnership()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void preservesExternalDefaultsChange()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void deletesBothChannelsBeforeItem()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void interruptedDefaultsCanBeRecovered() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void tableOwnershipSurvivesDtoWithoutNames()` | public · @Test | حالة تحقق مرتبطة بـالجدول؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `SpecialSalesTest.SpecialSalesCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-a6767db1eaa0"></a>
## SpecialSalesFixtureApi.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesFixtureApi.java](../../../src/test/java/SpecialSalesTest/SpecialSalesFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 71 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SpecialSalesFixtureApi(WebDriver driver, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-458ce70e7a89"></a>
## SpecialSalesFixtureApiStub.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesFixtureApiStub.java](../../../src/test/java/SpecialSalesTest/SpecialSalesFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 157

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-27a52182c38a"></a>
## SpecialSalesFixtureDefinitions.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesFixtureDefinitions.java](../../../src/test/java/SpecialSalesTest/SpecialSalesFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 283

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `SpecialSalesFixtureDefinitions(SpecialSalesTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String, Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record registeredCustomer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record customer(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record bank()` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record safe()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record table()` | داخل الحزمة | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |

<a id="file-f371401aa391"></a>
## SpecialSalesPrintAssertions.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesPrintAssertions.java](../../../src/test/java/SpecialSalesTest/SpecialSalesPrintAssertions.java)

**دوره:** مقارنات مشتركة تتكرر في سيناريوهات المجال مع رسائل فشل مفهومة.

**حالته:** مصدر نشط · **عدد أسطره:** 82

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private SpecialSalesPrintAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertReceipt(WebDriver driver, Runnable action, String itemName, String code)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static void assertReceipt( WebDriver driver, Runnable action, String itemName, java.util.function.Supplier<String> code)` | داخل الحزمة | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-1b28bd6af07a"></a>
## SpecialSalesRecovery.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesRecovery.java](../../../src/test/java/SpecialSalesTest/SpecialSalesRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 18 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-8fab08c67fe6"></a>
## SpecialSalesScenario.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesScenario.java](../../../src/test/java/SpecialSalesTest/SpecialSalesScenario.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 182 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [KitchenOrdersCoverageTest.java](TEST_SpecialSalesTest_AR.md#file-926b350dd2f5)، [PointOfSaleTest.java](TEST_SpecialSalesTest_AR.md#file-e1f07bc4e59b)، [RestaurantInvoiceTest.java](TEST_SpecialSalesTest_AR.md#file-fbdb31d7a2fb)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract SpecialSalesPage createPage();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ إنشاء الصفحة، حسب المدخلات والتخصص. |
| `public void fixtures()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void scenario()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void restartPage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `protected void prepareSave()` | protected | تجهيز القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void add()` | protected | إضافة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected List<?> lines()` | protected | تنفيذ جزء متعلق بـ البنود، حسب المدخلات والتخصص. |
| `protected SpecialSalesTestData.Record saved()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void number(Object actual, String expected)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void customerBalance(String expected)` | protected | تنفيذ جزء متعلق بـ العميل / الرصيد، حسب المدخلات والتخصص. |
| `public void evidence(ITestResult result)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void cleanup()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-6a3a5f55f28c"></a>
## SpecialSalesTestData.java

**المصدر:** [src/test/java/SpecialSalesTest/SpecialSalesTestData.java](../../../src/test/java/SpecialSalesTest/SpecialSalesTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 760

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `SpecialSalesTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private SpecialSalesTestData(WebDriver driver, String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `static SpecialSalesTestData resume(WebDriver driver, String suffix) throws java.io.IOException` | داخل الحزمة | إعادة تحميل حالة تشغيل محفوظة للتحقق أو الاسترجاع. |
| `static Map<String, Object> fields(Object... pairs)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record store(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Record configureOwnedDefaults(Record customer)` | داخل الحزمة | تنفيذ جزء متعلق بـ المملوك للاختبار، حسب المدخلات والتخصص. |
| `void restoreDefaults(Record record)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void syncBranch(Map<String, Object> branch)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private Map<String, Object> branchFor(Record link)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean containsStore(Map<String, Object> branch, Object storeId)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private void changeBranchStore(Record link, boolean add)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Map<String, Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record customer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record registeredCustomer()` | داخل الحزمة | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `private Record customer(String key)` | private | تنفيذ جزء متعلق بـ العميل، حسب المدخلات والتخصص. |
| `Record bank()` | داخل الحزمة | تنفيذ جزء متعلق بـ البنك، حسب المدخلات والتخصص. |
| `Record payment()` | داخل الحزمة | التعامل مع السداد، حسب المدخلات والتخصص. |
| `Record safe()` | داخل الحزمة | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `Record table()` | داخل الحزمة | تنفيذ جزء متعلق بـ الجدول، حسب المدخلات والتخصص. |
| `Object balance(Record item, int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الرصيد، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `Map<String, Object> lookup(String endpoint)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void recoverAttemptedItems(List<Map<String, Object>> attempts)` | داخل الحزمة | تنفيذ جزء متعلق بـ الأصناف، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void track(String endpoint, Object id, String note, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void rememberAttempts(List<Map<String, Object>> attempts, String marker)` | داخل الحزمة | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `private void recoverPendingFixtures()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String journalSuffix()` | داخل الحزمة | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private boolean existsInList(Record record)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void findOwned(Object node, String name, List<Map<String, Object>> matches)` | private | البحث عن المملوك للاختبار، حسب المدخلات والتخصص. |
| `public void close()` | public | إنهاء نطاق/مورد وتنظيفه حسب مالكه؛ اقرأ سياق الكلاس. |
