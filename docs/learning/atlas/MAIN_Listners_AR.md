# مراقب التشغيل

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-010e5aae21a3"></a>
## TestNGListeners.java

**المصدر:** [src/main/java/Plugin_ERP/Listeners/TestNGListeners.java](../../../src/main/java/Plugin_ERP/Listeners/TestNGListeners.java)

**دوره:** يراقب دورة التنفيذ ونتيجة الحالة، يجمع الأدلة، ويطبق سياسة بناء التقرير وإرساله.

**حالته:** مصدر نشط · **عدد أسطره:** 224

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [WebDriverFactory.java](MAIN_Drivers_AR.md#file-933f9d647645)، [AllureBinaryManger.java](MAIN_Utiel_AR.md#file-d01d270aef86)، [AllureConstants.java](MAIN_Utiel_AR.md#file-92ef6e0b0e4e)، [AllureEnvironment.java](MAIN_Utiel_AR.md#file-fdcdbfe78f62)، [AllureReportPolicy.java](MAIN_Utiel_AR.md#file-4fa275d1cd80)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)، [ScreenShotsUtils.java](MAIN_Utiel_AR.md#file-db7a1ec24b8d)

**ملفات تشير إليه مباشرة:** [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [CashTest.java](TEST_Tests_AR.md#file-e758c254da3e)، [TaypeOfDiscountTest.java](TEST_Tests_AR.md#file-f61729975a8f)، [TreasuryHandoverTest.java](TEST_Tests_AR.md#file-99e7d8e37af9)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public void generateReport(java.util.List<org.testng.xml.XmlSuite> xmlSuites, java.util.List<org.testng.ISuite> suites, String outputDirectory)` | public | المشاركة في بناء التقرير وفق callback TestNG وإعدادات التوصيل. |
| `public void onExecutionStart()` | public | مهام بداية التنفيذ، ومنها إعداد نتائج التقرير ومعلوماته. |
| `private void writeExecutorMetadata()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private long nextBuildOrder()` | private | تنفيذ جزء متعلق بـ التالي، حسب المدخلات والتخصص. |
| `public void onExecutionFinish()` | public | مهام النهاية وفق سياسة التقرير وإغلاق ما تبقى من المتصفح. |
| `private boolean hasAllureResults()` | private | السؤال عن وجود القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void onTestStart(ITestResult result)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void onTestSuccess(ITestResult result)` | public | تجميع أدلة نتيجة نجاح الحالة. |
| `public void onTestFailure(ITestResult result)` | public | تجميع أدلة الفشل من الجلسة الحالية. |
| `public void onTestSkipped(ITestResult result)` | public | تجميع أدلة التخطي إن توفرت الجلسة. |
| `private void takeScreenshot(ITestResult result, String status)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
