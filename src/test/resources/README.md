# ملفات تشغيل الاختبارات

يلزم JDK 21؛ يختاره Maven تلقائيًا للبناء والاختبارات باستخدام Toolchains. راجع `docs/testing/JAVA_SETUP_AR.md` لإعداد جهاز جديد وتشخيص توافق Java وAspectJ.

ملفات التشغيل الأساسية موجودة مباشرة في هذا المجلد:

تحديث24 أكتوبر2026: `smoke-testng.xml` يختار13 حالة، و`sanity-testng.xml` يختار57، و`e2e-testng.xml` يختار41 رحلة، و`regression-testng.xml` يختار1124 دالة دون تكرار اختيار الكلاس والدالة داخل المجموعة. يضم Regression الحالات نفسها عبر مداخل الموديولات و`coverage-expansion-testng.xml`؛ لا تضف E2E أو Sanity كاملتين إليه مرة أخرى.

`coverage-expansion-testng.xml` للشاشات الجديدة، و`coverage-observations-testng.xml` لفحوص الملاحظات، و`coverage-verification-testng.xml` يجمعهما في75 حالة. هذه مداخل تحقق مستهدفة، وليست بديلًا عن تشغيل المستويات الأربعة كاملة. `coverage-recovery-testng.xml` يستعيد فقط سجلات المالك المحددة صراحةً؛ ليس تنظيفًا عامًا لبيانات البيئة.

التشغيل الموحد من جذر المشروع: `./scripts/Test-Project.ps1` للمحلي، و`-Mode Acceptance` للمحلي ثم الدخول وSmoke، و`-Mode Regression` للمحلي ثم Regression الحي. النتائج في `target/validation/<run-id>`، دون تنظيف سجلات الاستعادة.

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

`additional/standalone/fixture-refactor-smoke-testng.xml` فحص حي محدد من ثماني حالات عبر الصرف والتصنيع ونقطة البيع والمطعم؛ يغطي إنشاء بيانات الاختبار والحفظ والأرصدة وإعدادات الفرع وتنظيف كل كلاس. مستقل عن CI المحلي. راجع `docs/testing/REFACTORING_AR.md` لأمر التشغيل والأدلة.

`additional/standalone/fixture-data-refactor-smoke-testng.xml` فحص حي من عشر حالات عبر ستة كلاسات للوحدات والأصناف والعملاء والبيانات المترابطة للمتعاملين، وبيانات الحركات المالية وسند قبض بتوزيع على فاتورة. مستقل عن CI المحلي ويستخدم تنظيف الكلاسات الحالي. تفاصيل التشغيل في `docs/testing/REFACTORING_AR.md`.

`additional/standalone/purchase-calendar-smoke-testng.xml` فحص حي لملكية نطاق المورد وفلتر تاريخ قائمة المشتريات، بحالتين وتنظيف مجموعة البيانات بعدهما. أما `PurchaseCalendarTest` ضمن CI فيفحص قراءة أسماء الأشهر محليًا دون متصفح.

`additional/standalone/document-list-refactor-smoke-testng.xml` فحص حي من أربع حالات لنطاق العميل/المورد والكود المطابق في قائمتي المبيعات والمشتريات، مع تجهيز بيانات كل كلاس وتنظيفها.

`additional/standalone/form-interactions-smoke-testng.xml` فحص حي من ست حالات للحفظ والحقول المطلوبة في المبيعات والمشتريات والتصنيع، بعد تنظيم تفاعلات النماذج المشتركة. تفاصيل النطاق والأدلة في `docs/testing/REFACTORING_AR.md`.

`additional/standalone/products-finance-interactions-smoke-testng.xml` أربع حالات حية لاختيارات الأصناف والحركات المالية: صنف بوحدة فرعية، فئة مطلوبة، سند قبض بتوزيع على فاتورة ومبلغ صفري مرفوض. يستخدم تجهيز وتنظيف الكلاسات الحاليين.

`additional/standalone/acceptance-testng.xml` يجمع اختبار الدخول وSmoke الحاليين دون نسخ أسماء الحالات؛ ملف الفحص الموحد يشغّله بعد نجاح الفحوص المحلية.
احتفظنا بتعريفات التغطية كما هي؛ النقل لتنظيم مداخل التشغيل.

`Test-Data/` يحتوي بيانات الاختبارات، وليس ملفات تشغيل. `pom.xml` في جذر المشروع يخص بناء Maven.

لتشغيل اختبارات المصادقة فقط:

```powershell
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/login-testng.xml' test
```

بيانات الدخول الصحيح تأتي من `config/Login.local.properties` أو متغيرات البيئة، كما هو موضح في `docs/testing/CREDENTIALS_AR.md`. حساب الاختبار المرفوض يُنشأ اسمه عشوائيًا تحت `example.invalid` ولا يستخدم حسابًا حقيقيًا. رسالة الرفض الحالية للحساب غير الموجود هي `User.UserNotFound`؛ يمكن تغيير التوقع باستخدام `-DLogin.UnknownUserMessage=...` للبيئات ذات الترجمة المختلفة. هذه الحالة تثبت رفض حساب غير موجود؛ لا تغطي كلمة مرور خاطئة لحساب قائم أو سياسة قفل الحسابات.

البيانات الأساسية: `basic-data-testng.xml`، والقيود اليومية: `general-accounting-testng.xml`. تجهيز البيانات والعزل والتنظيف واستثناء الإغلاق والمراجعة لتسليم الخزينة موضحة في `docs/testing/MODULE_FIXTURES_AR.md`.

فحص رحلة التقرير داخل الشاشات: `additional/standalone/allure-journey-smoke-testng.xml`. طريقة عرض الإجراءات والمقارنات ونتائج التحقق موثقة في `docs/testing/ALLURE_SCREEN_JOURNEYS_AR.md`.

إعادة التحقق بعد تقسيم Page Objects: `additional/standalone/page-object-refactor-testng.xml` للمكونات الأولى، و`additional/standalone/page-object-core-refactor-testng.xml` للكلاسات الأساسية المشتركة. يشغل كل ملف بالتتابع على Demo ببياناته المملوكة وتنظيفه المعتاد؛ لا يضاف إلى Regression حتى لا تتكرر الحالات. نتائج كل دفعة وحدود التحقق في `docs/testing/PAGE_OBJECT_REFACTOR_2026_10_24_AR.md`.

التحقق النهائي بعد فك الوراثة واستكمال فصل الصفحات: `additional/standalone/page-object-final-refactor-testng.xml`، ويختار 20 حالة تشمل الحفظ والطباعة، والمتعاملين، والبيانات الأساسية، والمنتجات، والحركات المالية، والمخازن والكاشير. الاختبارات الأساسية الأربعة تظل تستخدم الحالات نفسها ولا تحتاج تكرارها داخل ملفات التشغيل بسبب نقل تنفيذ Page Objects.
