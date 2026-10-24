# 8 — الإعداد والتشغيل خطوة بخطوة

[السابق](07_CALCULATIONS_AND_ASSERTIONS_AR.md) · [الرئيسية](README_AR.md) · [التالي](09_RESULTS_AND_DEBUGGING_AR.md)

## 8.1 قبل أول تشغيل

تحتاج نسخة المشروع، وJDK 21، وMaven، ومتصفحًا تدعمه الإعدادات مثل Chrome، واتصالًا ببيئة الاختبار للاختبارات الحية. JDK هو أدوات Java؛ Maven يستخدمه لتجميع الكود. IntelliJ يسهل قراءة وتشغيل المشروع، لكنه ليس مطلوبًا لتشغيل أوامر Maven من PowerShell.

افتح PowerShell داخل جذر المشروع، حيث `pom.xml`، ثم نفذ:

```powershell
java -version
mvn -version
```

وجود Java أحدث على الجهاز لا يضمن أن Maven اختار JDK الصحيح. المشروع يطلب release 21 ويستخدم Maven Toolchains لاختيار JDK مناسب. لو التجميع رفض الإصدار، راجع اختيار JDK وسجل Maven قبل تغيير كود الاختبارات.

## 8.2 الإعدادات: مين يغلب مين؟

`PropertyReader` يستخدم `ConfigurationProperties`، والترتيب الفعلي للقراءة كالتالي:

1. ملفات `.properties` تحت `src/main/resources` تُقرأ بترتيب مسارات ثابت. تكرار مفتاح في ملف لاحق قد يستبدل السابق.
2. الملف المحلي `config/Login.local.properties`، إذا كان موجودًا، يستبدل قيم الملفات العامة.
3. متغيرات البيئة المدعومة لبيانات الدخول تستبدل قيمتها. ليست كل properties لها override تلقائي من البيئة.
4. خاصية JVM مثل `-DTenant=Demo` لها الأولوية الأعلى عند طلب القيمة.

الترتيب ده معناه إنك تقدر تعمل تشغيلًا على Demo بدون تعديل الملف العام. لكن لا تفترض أن `REPORT_MAIL_ENABLED` يختار Tenant؛ إعداد البريد له قارئه المنفصل.

| الإعداد | السؤال اللي يجاوب عنه |
|---|---|
| Environment | هل نستخدم مسار Test أم نختار Tenant؟ |
| Tenant | أي نسخة ERP مقصودة، مثل Demo؟ |
| BrowserType | أي متصفح؟ |
| ExecutionType | نافذة محلية، محلي بدون نافذة، أو Remote؟ |
| defultwait | مهلة الانتظار العامة؛ الاسم مكتوب كده بالفعل |
| TaxMode / TaxPercentage | سياسة حساب عامة، مع مراعاة سياسات السيناريو الخاصة |
| DiscountType / Precision | نوع الخصم وعدد المنازل في الحساب |

`getApplicationUrl()` يستخدم TestURL إذا Environment تساوي Test؛ غير كده يستخدم المفتاح الذي اسمه Tenant. لذلك `-DTenant=Demo` وحده لا يتغلب على Environment=Test. راجع القيم المقصودة قبل التشغيل، ولا تنسخ قيم البيئة المحلية كأنها قاعدة عامة.

## 8.3 بيانات الدخول

ملف Login العام يحدد أسماء المفاتيح والسياسة؛ القيم الخاصة مكانها ملف محلي متجاهل في Git أو متغيرات بيئة. أمثلة أسماء مدعومة: `ERP_DEMO_USERNAME` و`ERP_DEMO_PASSWORD`، حسب تحويل اسم المفتاح `Demo.Username` و`Demo.Password`.

لا يوجد هنا مثال يحتوي كلمة سر فعلية. كلمة المرور ليست مادة مذاكرة ولا يجب طباعتها في التقرير. ملفات `config/*.local.properties` متجاهلة في `.gitignore`، لكن تجاهل Git لا يعني أمان مشاركة الملف نفسه مع الآخرين.

## 8.4 أول تشغيل يكون محليًا

```powershell
mvn -Pci verify
```

ده يجمع المشروع ويشغّل `ci-testng.xml`. الفحوص المختارة محلية ولا تحتاج فتح ERP؛ منها حسابات، حماية ملكية البيانات، محاكاة التنظيف، عقود XML، وأدوات التفاعل بمحاكاة عناصر.

المخرجات المعتادة: `target/ci/surefire-reports` و`target/ci/allure-results`. تعطيل فتح HTML تلقائيًا في profile لا يعني تعطيل كل سياسات إرسال التقرير إذا كانت إعدادات البريد مفعلة.

لو عايز تمنع إرسال التقرير في جلسة PowerShell التدريبية، استخدم متغير العملية مع استرجاعه:

```powershell
$previousMail = $env:REPORT_MAIL_ENABLED
$env:REPORT_MAIL_ENABLED = 'false'
try {
    mvn -Pci verify
    $runExit = $LASTEXITCODE
} finally {
    $env:REPORT_MAIL_ENABLED = $previousMail
}
Write-Host "Maven exit code: $runExit"
```

ده يخص إعداد خدمة التقرير في المشروع، مش ميزة خاصة في Maven. لا ترسل تقارير أو تفعّل خدمة خارجية لمجرد تجربة دليل.

## 8.5 تشغيل الدخول ثم المجموعات الحية

الأمثلة التالية تفترض أن Environment مضبوطة للـTenant المقصود، وأن Demo بيئة اختبار مسموح بها، وبيانات الدخول متاحة. كل أمر مستقل، ولا تشغلهم بالتوازي داخل نفس نسخة العمل.

```powershell
mvn '-DTenant=Demo' '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/login-testng.xml' test

mvn '-DTenant=Demo' '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/smoke-testng.xml' test

mvn '-DTenant=Demo' '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/sanity-testng.xml' test

mvn '-DTenant=Demo' '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/e2e-testng.xml' test

mvn '-DTenant=Demo' '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/regression-testng.xml' test
```

وجود quotes حول كل معامل `-D` في PowerShell يقلل سوء تفسير بعض الرموز. أمر `test` لا يختار Smoke من تلقاء نفسه؛ الاختيار هنا من مسار XML.

تبدأ بالدخول وSmoke، لكن نجاحهما مش إذن لتجاهل فشل E2E. Regression قد يستغرق وقتًا طويلًا بحسب البيئة والبيانات والشبكة؛ لا تعتبر مدة ثابتة ضمانًا لاكتماله.

## 8.6 أمر يراجع حالة محددة

للفحوص المحلية المعتادة يمكن اختيار كلاس أو ميثود بـSurefire:

```powershell
mvn '-Dtest=SalesCalculationTest' test
```

لتحديد حالات حية متداخلة التجهيز أو موروثة، ملف XML صغير غالبًا أوضح من الاعتماد على اسم غير متحقق منه. لا تكتب اسمًا من الذاكرة: راجع ملف الاختبار و`@Test` وتعريفه الفعلي. اختيار حالة لا يلغي `BeforeClass` ولا تكلفة تجهيزها وتنظيفها.

## 8.7 سكربت التشغيل الموحد

```powershell
./scripts/Test-Project.ps1
./scripts/Test-Project.ps1 -Mode Acceptance
./scripts/Test-Project.ps1 -Mode Regression
```

الافتراضي Local. Acceptance يشغّل المحلي ثم ملفًا يجمع الدخول وSmoke. Regression يشغّل المحلي ثم التغطية الواسعة. كل تشغيل له GUID وفولدر تحت `target/validation` مع سجلات. السكربت لا ينفذ `clean` ويتوقف لو فشلت مرحلة.

**فرق مهم:** السكربت الحالي لا يضيف `-DTenant=Demo` بنفسه، لذلك يعتمد على إعدادات البيئة الفعلية. الأوامر المباشرة فوق أظهرت override صريحًا. لا تفترض إن كتابة Acceptance وحدها تختار Demo. ولا تفترض أن السكربت يشغّل Sanity وE2E كأربع مراحل منفصلة؛ اقرأ اختياره الفعلي.

## 8.8 لو الشاشة مش ظاهرة

LocalHeadless يعني المتصفح شغال بدون نافذة مرئية؛ تابع السجل والصور والتقرير. لو تحتاج مشاهدة الخطوات، ExecutionType=Local يطلب نافذة محلية وفق المصنع. Remote يحتاج خدمة Selenium Grid وإعداد remoteHost وremotePort؛ ليس شرطًا للتشغيل المحلي.

## 8.9 تشغيل GitHub Actions

ملف `.github/workflows/local-checks.yml` يشغّل فحوص CI المحلية على Windows وUbuntu باستخدام Java 21، ويفحص الأسرار ويحتفظ بالتقارير كـartifacts. ده **ليس تشغيل Regression حي على ERP** في التعريف الحالي. أخضر GitHub لا يعني أن جميع شاشات Demo اجتازت الاختبار.

## 8.10 قبل ما تقفل الجهاز

تأكد أن Maven انتهى، واقرأ نتيجة الحالة والتنظيف، واحفظ مجلد الأدلة لو هتشغل محاولة جديدة. لا تطفئ العملية أثناء Cleanup ثم تعتبر غياب رسالة فشل نجاحًا. لو اضطررت توقفها، احتفظ بالسجلات واتبع فصل الاسترجاع.
