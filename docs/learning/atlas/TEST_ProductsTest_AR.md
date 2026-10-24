# اختبارات ودعم المنتجات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-1f456fd284a4"></a>
## AddProductsTest.java

**المصدر:** [src/test/java/ProductsTest/AddProductsTest.java](../../../src/test/java/ProductsTest/AddProductsTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 455 · **الأب المباشر:** `ProductScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ProductScenarioTest.java](TEST_ProductsTest_AR.md#file-cdd1aade6005)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected Kind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected ProductsModulePage modulePage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void addNewProductWithValidData()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddComponent()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithOutBarcode()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithOutCategory()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithOutUnit()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithOutStock()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithSameUnit()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductWithNoTransUnit()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddTransWithoutBarcode()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_RequiredArabic()` | public · @Test | حالة تحقق مرتبطة بـالعربي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SimpleProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SubunitRateOne()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SubunitNegativeRate()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_DuplicateSubunit()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_DuplicateStore()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_NegativeOpening()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_NegativeMinimum()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_NegativeCost()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ControlFlags()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_StockBalances()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelNew()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SearchAndReset()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_OptionalEnglish()` | public · @Test | حالة تحقق مرتبطة بـالإنجليزي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void subunitInvalid(String reason)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void PR_NegativeSalePrice()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_MinimumAboveSalePrice()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_MaximumBelowSalePrice()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_MinimumAboveMaximum()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SalePriceBelowMinimum()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SalePriceAboveMaximum()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_MaximumBelowMinimum()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_AllPricesEqual()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SalePriceEqualsMinimum()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SalePriceEqualsMaximum()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void enterPrices(String sale, String minimum, String maximum)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void pricesValid(String sale, String minimum, String maximum)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void pricesInvalid(String sale, String minimum, String maximum)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void PR_DuplicateMainBarcode()` | public · @Test | حالة تحقق مرتبطة بـالباركود؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_UpdatePrices()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_AddSecondStore()` | public · @Test | حالة تحقق مرتبطة بـالمخزن؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ArabicProductName()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `private void stockInvalid(String reason)` | private | التعامل مع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `ProductsTest.AddProductsTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-9cb7fb3349a4"></a>
## ProductCategoriesTest.java

**المصدر:** [src/test/java/ProductsTest/ProductCategoriesTest.java](../../../src/test/java/ProductsTest/ProductCategoriesTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 130 · **الأب المباشر:** `ProductScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ProductScenarioTest.java](TEST_ProductsTest_AR.md#file-cdd1aade6005)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected Kind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected ProductsModulePage modulePage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddProductCategory()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddBranchProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditBranchProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteBranchProduct()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddProductCategoryWithoutArabicName()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CategoryFlags()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelNew()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SearchAndReset()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_OptionalEnglish()` | public · @Test | حالة تحقق مرتبطة بـالإنجليزي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `ProductsTest.ProductCategoriesTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-50ff9236f08d"></a>
## ProductListTest.java

**المصدر:** [src/test/java/ProductsTest/ProductListTest.java](../../../src/test/java/ProductsTest/ProductListTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 127 · **الأب المباشر:** `ProductScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ProductScenarioTest.java](TEST_ProductsTest_AR.md#file-cdd1aade6005)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void PR_DuplicatePriceListItem()` | public · @Test | حالة تحقق مرتبطة بـالسعر / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `protected Kind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected ProductsModulePage modulePage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddNewProductList()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditPriceList()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeletePriceList()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AssertErorrMsg()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SaveWithoutPrint()` | public · @Test | حالة تحقق مرتبطة بـالطباعة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_DecimalPrice()` | public · @Test | حالة تحقق مرتبطة بـالسعر؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelNew()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SearchAndReset()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_OptionalEnglish()` | public · @Test | حالة تحقق مرتبطة بـالإنجليزي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `ProductsTest.ProductListTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-cdd1aade6005"></a>
## ProductScenarioTest.java

**المصدر:** [src/test/java/ProductsTest/ProductScenarioTest.java](../../../src/test/java/ProductsTest/ProductScenarioTest.java)

**دوره:** تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.

**حالته:** مصدر نشط · **عدد أسطره:** 423 · **الأب المباشر:** `BaseTest`

**يرتبط مباشرة بملفات داخل المشروع:** [Pages.java](MAIN_Enums_AR.md#file-0cf000b33a7e)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)

**ملفات تشير إليه مباشرة:** [AddProductsTest.java](TEST_ProductsTest_AR.md#file-1f456fd284a4)، [ProductCategoriesTest.java](TEST_ProductsTest_AR.md#file-9cb7fb3349a4)، [ProductListTest.java](TEST_ProductsTest_AR.md#file-50ff9236f08d)، [UnitTest.java](TEST_ProductsTest_AR.md#file-aa5bfc2c00f1)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected abstract Kind kind();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected abstract ProductsModulePage modulePage();` | protected | عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void awaitProductsSession()` | public | انتظار الجلسة، حسب المدخلات والتخصص. |
| `public void newProductScenario()` | public | بدء جديد القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void prerequisites()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void openNew()` | protected | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void fill(String omitted)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected List<ProductsTestData.Record> capture()` | protected | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `protected ProductsTestData.Record save(boolean print)` | protected | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `protected void verifyReport(ProductsTestData.Record record)` | protected | التحقق من التقرير، حسب المدخلات والتخصص. |
| `protected Map<String, Object> values()` | protected | تنفيذ جزء متعلق بـ القيم، حسب المدخلات والتخصص. |
| `protected ProductsTestData.Record seed()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void locate(ProductsTestData.Record record)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertNumber(Object actual, String expected)` | protected | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void create()` | protected | إنشاء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void assertDetails(Map<String, Object> stored)` | protected | مقارنة متوقعة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void edit(boolean print)` | protected | تعديل القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void deleteOwn()` | protected | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void required(String omitted, String expected)` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void commonCancelNew()` | protected | تنفيذ جزء متعلق بـ الإلغاء، حسب المدخلات والتخصص. |
| `protected void commonCancelEdit()` | protected | تنفيذ جزء متعلق بـ الإلغاء، حسب المدخلات والتخصص. |
| `protected void commonCancelDelete()` | protected | تنفيذ جزء متعلق بـ الإلغاء، حسب المدخلات والتخصص. |
| `protected void commonSearch()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void commonList()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected void commonExport(String format)` | protected | تنفيذ جزء متعلق بـ التصدير، حسب المدخلات والتخصص. |
| `private void assertOptionalEnglish(Map<String, Object> stored)` | private | مقارنة متوقعة الإنجليزي، حسب المدخلات والتخصص. |
| `protected void optionalEnglish()` | protected | تنفيذ جزء متعلق بـ الإنجليزي، حسب المدخلات والتخصص. |
| `public void cleanProductScenario()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void retryProductCleanup()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `ProductsTest.ProductScenarioTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-476a8774ae02"></a>
## ProductsCleanupTest.java

**المصدر:** [src/test/java/ProductsTest/ProductsCleanupTest.java](../../../src/test/java/ProductsTest/ProductsCleanupTest.java)

**دوره:** فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.

**حالته:** مصدر نشط · **عدد أسطره:** 423

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void rejectsMissingDeleteWhenOwnedRecordStillExists()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversUiItemCommittedBeforeFailedResponse()` | public · @Test | حالة تحقق مرتبطة بـالصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void recoversInvalidItemByUniqueEnglishMarker()` | public · @Test | حالة تحقق مرتبطة بـالصنف / الإنجليزي / علامة الملكية؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved()` | public · @Test | حالة تحقق مرتبطة بـالمخزن / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void verifiesCommittedBranchUnlinkAfterLostResponse()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retriesUncommittedDeleteBeforeParentAccounts()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsEmptyStringReadAfterCommittedDeleteTimeout()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void cleansOwnedItemCommittedBeforeCreateFailure()` | public · @Test | حالة تحقق مرتبطة بـالمملوك للاختبار / الصنف؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingProductDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void acceptsVerifiedMissingMasterDefaultDto()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void updatedRecordDeletesBeforeItsNewReference()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void pageScopeSharesOnlyStableReferences()` | public · @Test | حالة تحقق مرتبطة بـالصفحة / النطاق؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة / المملوك للاختبار؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void resumesUncertainCreateBeforeDeletingItsDependencies() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void refusesRecoveryInAnotherBranch() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception` | public · @Test | حالة تحقق مرتبطة بـالصفحة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public String prCaptureMarker()` | public | مراقبة تتبع الحفظ / علامة الملكية، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> prSaves()` | public | تنفيذ جزء متعلق بـ عمليات الحفظ، حسب المدخلات والتخصص. |
| `public List<Map<String, Object>> prAttempts()` | public | تنفيذ جزء متعلق بـ المحاولات، حسب المدخلات والتخصص. |
| `public void prSettled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected Kind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `ProductsTest.ProductsCleanupTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

<a id="file-25960e99d6a1"></a>
## ProductsExportAssertions.java

**المصدر:** [src/test/java/ProductsTest/ProductsExportAssertions.java](../../../src/test/java/ProductsTest/ProductsExportAssertions.java)

**دوره:** مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.

**حالته:** مصدر نشط · **عدد أسطره:** 104

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ProductsExportAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `static void assertExportDownloaded(WebDriver driver, Runnable exportAction, String format)` | داخل الحزمة | مقارنة متوقعة التصدير، حسب المدخلات والتخصص. |
| `private static void installDownloadCapture(WebDriver driver)` | private | تنفيذ جزء متعلق بـ تتبع الحفظ، حسب المدخلات والتخصص. |

<a id="file-30e95009698e"></a>
## ProductsFixtureApi.java

**المصدر:** [src/test/java/ProductsTest/ProductsFixtureApi.java](../../../src/test/java/ProductsTest/ProductsFixtureApi.java)

**دوره:** نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.

**حالته:** مصدر نشط · **عدد أسطره:** 58 · **الأب المباشر:** `AssertionError`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ProductsFixtureApi(WebDriver driver, Runnable contextGuard)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c6849a253296"></a>
## ProductsFixtureApiStub.java

**المصدر:** [src/test/java/ProductsTest/ProductsFixtureApiStub.java](../../../src/test/java/ProductsTest/ProductsFixtureApiStub.java)

**دوره:** بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 151

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `WebDriver driver()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Object request(Object[] args)` | داخل الحزمة | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-d4757876714c"></a>
## ProductsFixtureDefinitions.java

**المصدر:** [src/test/java/ProductsTest/ProductsFixtureDefinitions.java](../../../src/test/java/ProductsTest/ProductsFixtureDefinitions.java)

**دوره:** وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.

**حالته:** مصدر نشط · **عدد أسطره:** 53

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ProductsFixtureDefinitions(ProductsTestData data)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Map<String,Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |

<a id="file-75de5fa5def9"></a>
## ProductsFixtureRecovery.java

**المصدر:** [src/test/java/ProductsTest/ProductsFixtureRecovery.java](../../../src/test/java/ProductsTest/ProductsFixtureRecovery.java)

**دوره:** مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.

**حالته:** مصدر نشط · **عدد أسطره:** 17 · **الأب المباشر:** `Plugin_ERP.Tests.LoginTest.BaseTest`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-13b6fcff12f4"></a>
## ProductsSuitePolicy.java

**المصدر:** [src/test/java/ProductsTest/ProductsSuitePolicy.java](../../../src/test/java/ProductsTest/ProductsSuitePolicy.java)

**دوره:** قواعد مستوى/اختيار حالات المجال أو ضبطها؛ ليست صفحة أو وصف بيانات ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 20

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void alter(List<XmlSuite> suites)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-9af17924b3a1"></a>
## ProductsTestData.java

**المصدر:** [src/test/java/ProductsTest/ProductsTestData.java](../../../src/test/java/ProductsTest/ProductsTestData.java)

**دوره:** مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.

**حالته:** مصدر نشط · **عدد أسطره:** 319

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Record(String endpoint, Object id, String name, Map<String, Object> values)` | داخل الحزمة | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `ProductsTestData(WebDriver driver)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private ProductsTestData(WebDriver driver,String suffix)` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `void beginScenario()` | داخل الحزمة | بدء نطاق بيانات مستقل للحالة الحالية. |
| `private String fixtureKey(String key)` | private | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `static Map<String, Object> fields(Object... pairs)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record account(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ الحساب، حسب المدخلات والتخصص. |
| `Record unit(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record mainCategory()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record category()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record store(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private boolean containsStore(Map<String,Object> branch,Object storeId)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `private void changeBranchStore(Record link,boolean add)` | private | تنفيذ جزء متعلق بـ المخزن، حسب المدخلات والتخصص. |
| `Map<String,Object> itemValues()` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف / القيم، حسب المدخلات والتخصص. |
| `Record item(int index)` | داخل الحزمة | تنفيذ جزء متعلق بـ الصنف، حسب المدخلات والتخصص. |
| `Record priceList()` | داخل الحزمة | تنفيذ جزء متعلق بـ السعر، حسب المدخلات والتخصص. |
| `Map<String, Object> read(Record record)` | داخل الحزمة | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `Record fixture(String key, String endpoint, Map<String, Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ بيانات الاختبار، حسب المدخلات والتخصص. |
| `private Map<String, Object> call(String action, String endpoint, Map<String, Object> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `void track(String endpoint, Object id, String note, Map<String,Object> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void recoverPendingFixtures()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String journalSuffix()` | داخل الحزمة | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private void journal()` | private | تنفيذ جزء متعلق بـ سجل الاسترجاع، حسب المدخلات والتخصص. |
| `private static boolean retryable(ApiFailure failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void deleteRecord(Record record)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean references(Object value, Object id)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-aa5bfc2c00f1"></a>
## UnitTest.java

**المصدر:** [src/test/java/ProductsTest/UnitTest.java](../../../src/test/java/ProductsTest/UnitTest.java)

**دوره:** حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.

**حالته:** مصدر نشط · **عدد أسطره:** 93 · **الأب المباشر:** `ProductScenarioTest`

**يرتبط مباشرة بملفات داخل المشروع:** [ProductScenarioTest.java](TEST_ProductsTest_AR.md#file-cdd1aade6005)

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `protected Kind kind()` | protected | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `protected ProductsModulePage modulePage()` | protected | تنفيذ جزء متعلق بـ الصفحة، حسب المدخلات والتخصص. |
| `public void AddNewUnite()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void EditUnite()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void DeleteUnite()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void AddUniteWithoutArabicName()` | public · @Test | حالة تحقق مرتبطة بـالعربي / الاسم؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelNew()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelEdit()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_CancelDelete()` | public · @Test | حالة تحقق مرتبطة بـالإلغاء؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_SearchAndReset()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ListStructure()` | public · @Test | حالة تحقق مرتبطة بـالقدرة التي يحددها اسم الدالة؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportPdf()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_ExportExcel()` | public · @Test | حالة تحقق مرتبطة بـالتصدير؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |
| `public void PR_OptionalEnglish()` | public · @Test | حالة تحقق مرتبطة بـالإنجليزي؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار. |

**علاقته بالتشغيل:** ابحث عن الاسم الكامل `ProductsTest.UnitTest` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.

