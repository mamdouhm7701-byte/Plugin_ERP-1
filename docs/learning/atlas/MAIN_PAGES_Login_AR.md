# صفحات ومكونات الدخول

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-c3f664f98635"></a>
## LoginPage.java

**المصدر:** [src/main/java/Plugin_ERP/System_Modules/Login/Login/LoginPage.java](../../../src/main/java/Plugin_ERP/System_Modules/Login/Login/LoginPage.java)

**دوره:** حقول وإجراءات الدخول والتحقق من الانتقال بعده.

**حالته:** مصدر نشط · **عدد أسطره:** 84

**يرتبط مباشرة بملفات داخل المشروع:** [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)

**ملفات تشير إليه مباشرة:** [PermissionsCoverageTest.java](TEST_CoverageTest_AR.md#file-3c11bfe3a379)، [TreasuryLedgerCoverageTest.java](TEST_FinancialMovementsTest_AR.md#file-aaf8d34190e8)، [BaseTest.java](TEST_Tests_AR.md#file-d2bcce799a1e)، [CashTest.java](TEST_Tests_AR.md#file-e758c254da3e)، [TaypeOfDiscountTest.java](TEST_Tests_AR.md#file-f61729975a8f)، [TreasuryHandoverTest.java](TEST_Tests_AR.md#file-99e7d8e37af9)، [LoginTest.java](TEST_Tests_AR.md#file-3a9647b96039)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public LoginPage(WebDriver driver)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void Login(String user, String pass)` | public | تنفيذ جزء متعلق بـ تسجيل الدخول، حسب المدخلات والتخصص. |
| `public void AssertLoginPageTitle()` | public | مقارنة متوقعة تسجيل الدخول / الصفحة / العنوان، حسب المدخلات والتخصص. |
| `public void waitForLoginForm()` | public | انتظار تسجيل الدخول / النموذج، حسب المدخلات والتخصص. |
| `public void assertLoginRejected(String expectedMessage)` | public | مقارنة متوقعة تسجيل الدخول، حسب المدخلات والتخصص. |
| `public void AssertInvalidLoginAlertMessage(String expectedMessage)` | public | مقارنة متوقعة تسجيل الدخول / تنبيه المتصفح / الرسالة، حسب المدخلات والتخصص. |

