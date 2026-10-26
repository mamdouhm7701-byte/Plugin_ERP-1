# صفحات ومكونات الشاشات الموسعة

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-12dda9e13e49"></a>
## ScreenPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Coverage/ScreenPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Coverage/ScreenPage.java)

**دوره:** عناصر UI عامة للشاشات الموسعة: فتح وجاهزية وحقول وقراءات؛ ليست حالة اختبار في حد ذاتها.

**حالته:** مصدر نشط · **عدد أسطره:** 181

**يرتبط مباشرة بملفات داخل المشروع:** [FormInteractions.java](MAIN_Utiel_AR.md#file-9b67771be2a9)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [AuditLogCoverageTest.java](TEST_CoverageTest_AR.md#file-67388f9fb2ba)، [MasterCoverageScenario.java](TEST_CoverageTest_AR.md#file-70a588c411f7)، [UsersCoverageTest.java](TEST_CoverageTest_AR.md#file-23f4288c4be3)، [ZatcaConfigurationCoverageTest.java](TEST_CoverageTest_AR.md#file-eae457cc2254)، [TreasuryLedgerCoverageTest.java](TEST_FinancialMovementsTest_AR.md#file-aaf8d34190e8)، [ActionsCoverageTest.java](TEST_Invoice_AR.md#file-026860c005f6)، [PostingCoverageTest.java](TEST_Invoice_AR.md#file-ae7931450ae7)، [KitchenOrdersCoverageTest.java](TEST_SpecialSalesTest_AR.md#file-926b350dd2f5)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public ScreenPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static String literal(String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void open(String route, String title)` | public | فتح القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void settled()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void click(By locator)` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public By button(String text)` | public | تنفيذ جزء متعلق بـ الزر، حسب المدخلات والتخصص. |
| `public boolean visible(By locator)` | public | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `public WebElement visibleElement(By locator)` | public | تنفيذ جزء متعلق بـ الظاهر / عنصر الشاشة، حسب المدخلات والتخصص. |
| `public String value(By locator)` | public | تنفيذ جزء متعلق بـ القيمة، حسب المدخلات والتخصص. |
| `public void field(String name, String value)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void notes(String marker)` | public | ضبط الملاحظات، حسب المدخلات والتخصص. |
| `public void clearChoice(String label)` | public | مسح/إلغاء الاختيار، حسب المدخلات والتخصص. |
| `public void check(String label, boolean selected)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void confirm(boolean yes)` | public | تأكيد التأكيد، حسب المدخلات والتخصص. |
| `public boolean confirmationVisible()` | public | تنفيذ جزء متعلق بـ الظاهر، حسب المدخلات والتخصص. |
| `public void dismissSuccess()` | public | إغلاق تنبيه القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void closeModal()` | public | إغلاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public By row(String text)` | public | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `public void rowAction(String text, String action)` | public | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `public void treeNode(String name)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public By treeLabel(Object id)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void expandTreeNode(Object id)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void treeNode(Object id,String name)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public String bodyText()` | public | تنفيذ جزء متعلق بـ النص، حسب المدخلات والتخصص. |
| `public boolean validationVisible()` | public | تنفيذ جزء متعلق بـ التحقق / الظاهر، حسب المدخلات والتخصص. |
| `public void search(String term)` | public | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void addItem(String name)` | public | إضافة الصنف، حسب المدخلات والتخصص. |
| `public void tableLine(String name, String heading, String value)` | public | تنفيذ جزء متعلق بـ الجدول / البند، حسب المدخلات والتخصص. |
