# ملفات تشغيل الاختبارات

يلزم JDK 21؛ يختاره Maven تلقائيًا للبناء والاختبارات باستخدام Toolchains. راجع `docs/testing/JAVA_SETUP_AR.md` لإعداد جهاز جديد وتشخيص توافق Java وAspectJ.

ملفات التشغيل الأساسية موجودة مباشرة في هذا المجلد:

- الفحوص المحلية الآلية: `ci-testng.xml`، وتشغيلها المفضل `mvn -Pci verify` لعزل النتائج وتعطيل توليد وفتح Allure تلقائيًا. لا يتصل هذا المدخل بـERP ولا يحتاج بيانات دخول.

- مستويات الاختبار: `smoke-testng.xml` و`sanity-testng.xml` و`regression-testng.xml` و`e2e-testng.xml`.
- الموديولات: `products-testng.xml` و`dealer-model-testng.xml` و`store-testng.xml` و`financial-movements-testng.xml` و`sales-testng.xml` و`purchases-testng.xml` و`special-sales-testng.xml`.
- المصادقة: `login-testng.xml` يختبر الدخول الصحيح ورفض حساب عشوائي غير موجود، بجلسة متصفح جديدة لكل حالة، دون وراثة جلسة `BaseTest`.
- موديول `special-sales` يشمل نقاط البيع والمطعم واختبارات تنظيف بياناتهما.

مثال تشغيل من جذر المشروع في PowerShell:

```powershell
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/smoke-testng.xml' test
```

الملفات الإضافية داخل `additional/`:

| المجلد | الاستخدام |
| --- | --- |
| `legacy/` | مداخل توافق المبيعات والمشتريات وملف `testng.xml` القديم للبيانات الأساسية؛ خارج Regression |
| `standalone/` | تشغيل نقاط البيع أو المطعم منفرداً، وPurchase Smoke، وفحص دورة حياة البيانات |
| `internal/` | أجزاء مساعدة يجمعها Regression تلقائياً: التغطية الإضافية والرحلات المشتركة |

لتشغيل مدخل إضافي استخدم مساره الكامل، مثلاً `src/test/resources/additional/standalone/restaurant-testng.xml`.
لا تجمع مدخل الشاشة المنفرد مع `special-sales-testng.xml` في التشغيل نفسه حتى لا تتكرر الحالات.

`additional/standalone/browser-isolation-testng.xml` فحص حي من ثماني حالات عبر الوحدات والعملاء والمبيعات والمشتريات: متصفح جديد لكل كلاس مع الاحتفاظ بتنظيف بياناته قبل الإغلاق. أما `SuiteSupport.BrowserLifecycleTest` فهو فحص محلي لملكية المتصفح وإغلاقه عند فشل الدخول أو فشل الإغلاق.
احتفظنا بتعريفات التغطية كما هي؛ النقل لتنظيم مداخل التشغيل.

`Test-Data/` يحتوي بيانات الاختبارات، وليس ملفات تشغيل. `pom.xml` في جذر المشروع يخص بناء Maven.

لتشغيل اختبارات المصادقة فقط:

```powershell
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/login-testng.xml' test
```

بيانات الدخول الصحيح تأتي من `config/Login.local.properties` أو متغيرات البيئة، كما هو موضح في `docs/testing/CREDENTIALS_AR.md`. حساب الاختبار المرفوض يُنشأ اسمه عشوائيًا تحت `example.invalid` ولا يستخدم حسابًا حقيقيًا. رسالة الرفض الحالية للحساب غير الموجود هي `User.UserNotFound`؛ يمكن تغيير التوقع باستخدام `-DLogin.UnknownUserMessage=...` للبيئات ذات الترجمة المختلفة. هذه الحالة تثبت رفض حساب غير موجود؛ لا تغطي كلمة مرور خاطئة لحساب قائم أو سياسة قفل الحسابات.
