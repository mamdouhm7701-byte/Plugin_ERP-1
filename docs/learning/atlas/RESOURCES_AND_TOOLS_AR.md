# الإعداد والموارد والسكربتات وملفات الجذر

[فهرس الأطلس](README_AR.md)

تعرض هذه البطاقات الدور وأسماء البنى أو المفاتيح؛ لا تعرض قيم الدخول أو إعدادات خاصة. الملفات المحلية *.local.* لا تدخل الحصر النصي.

## .githooks/pre-commit

**الملف:** [pre-commit](../../../.githooks/pre-commit)

فحص عند محاولة commit وفق محتوى hook؛ لا يكفي وجوده دون تفعيل hooksPath.

## .github/workflows/local-checks.yml

**الملف:** [local-checks.yml](../../../.github/workflows/local-checks.yml)

GitHub Actions للفحوص المحلية على Windows وUbuntu ورفع أدلتها؛ ليس ERP Regression حيًا.

## .gitignore

**الملف:** [.gitignore](../../../.gitignore)

أنواع ملفات لا تدخل Git، ومنها النتائج والاعتمادات المحلية؛ ليس نظام تنظيف ولا تشفيرًا.

## add_report_charts.py

**الملف:** [add_report_charts.py](../../../add_report_charts.py)

سكربت جانبي لمعالجة مخططات تقرير؛ راجع مساراته قبل تنفيذه.

## chart-template.xml

**الملف:** [chart-template.xml](../../../chart-template.xml)

قالب XML لمخطط يستخدمه سكربت تقرير جانبي، وليس TestNG suite.

**بنية XML:** الجذر `Chart`؛ أكثر العناصر: `Style` × 45، `Color` × 22، `Border` × 21، `FontFamily` × 11، `FontSize` × 11، `ChartCustomPaletteColor` × 10، `Caption` × 6، `BackgroundGradientType` × 5. عدد العناصر ليس عدد حالات منفذة.

## config/report-mail.properties

**الملف:** [report-mail.properties](../../../config/report-mail.properties)

إعداد عام لخدمة البريد؛ يعرض الدليل أسماء المفاتيح فقط ولا ينسخ مستلمين أو أسرارًا.

**أسماء المفاتيح فقط:** `enabled`، `username`، `to` القيم لا تظهر في الدليل.

## ms-chart-reference.rdl

**الملف:** [ms-chart-reference.rdl](../../../ms-chart-reference.rdl)

ملف مرجعي لتصميم تقرير بصيغة RDL، وليس اختبار Selenium.

## pom.xml

**الملف:** [pom.xml](../../../pom.xml)

مواصفات البناء والمكتبات والإضافات وprofile CI. اقرأ فصل البنية والتشغيل قبل تعديل إصدار مكتبة.

**بنية XML:** الجذر `project`؛ أكثر العناصر: `groupId` × 23، `artifactId` × 23، `version` × 23، `dependency` × 17، `plugin` × 5، `configuration` × 5، `allure.results.directory` × 3، `allure.report.directory` × 3. عدد العناصر ليس عدد حالات منفذة.

## README.md

**الملف:** [README.md](../../../README.md)

مدخل المشروع القديم وفيه معلومات تشغيل ونتائج بتاريخها؛ بعض أرقامه تاريخية مقارنة بالدليل الحالي.

## repair_executive.py

**الملف:** [repair_executive.py](../../../repair_executive.py)

سكربت عمل جانبي لإصلاح قالب تقرير RDL باستخدام chart-template؛ لا يُشغل ضمن Maven.

## scripts/build_learning_guide.py

**الملف:** [build_learning_guide.py](../../../scripts/build_learning_guide.py)

أداة توليد هذا الأطلس ونسخة HTML من الدليل؛ تقرأ المصادر وتكتب التوثيق فقط.

## scripts/check_secrets.py

**الملف:** [check_secrets.py](../../../scripts/check_secrets.py)

يراجع الملفات المسموح فحصها لمنع تسريب أسرار وتقارير خاصة إلى Git.

## scripts/Connect-ReportDrive.ps1

**الملف:** [Connect-ReportDrive.ps1](../../../scripts/Connect-ReportDrive.ps1)

إعداد اتصال Drive لتوصيل تقارير كبيرة؛ وظيفة خارجية مستقلة عن اختبار ERP.

## scripts/DocumentationSuiteInventory.java

**الملف:** [DocumentationSuiteInventory.java](../../../scripts/DocumentationSuiteInventory.java)

حصر اختيار TestNG الفعلي باستخدام قارئ عقود المشروع؛ يكتب بيانات مرجع المجموعات دون تنفيذ الاختبارات أو فتح ERP.

## scripts/Send-ReportMail.ps1

**الملف:** [Send-ReportMail.ps1](../../../scripts/Send-ReportMail.ps1)

إعادة إرسال تقارير وفق إعداد الخدمة والمستلمين.

## scripts/Set-ReportMail.ps1

**الملف:** [Set-ReportMail.ps1](../../../scripts/Set-ReportMail.ps1)

مساعد إعداد خدمة إرسال التقرير؛ لا تشغله كتمرين دون قصد تغيير الإعداد.

## scripts/Test-Project.ps1

**الملف:** [Test-Project.ps1](../../../scripts/Test-Project.ps1)

يشغل الفحوص المحلية ثم القبول أو Regression حسب Mode ويحفظ سجلًا منفصلًا ولا ينفذ clean.

## scripts/test_check_secrets.py

**الملف:** [test_check_secrets.py](../../../scripts/test_check_secrets.py)

فحص محلي لسلوك كاشف الأسرار.

## src/main/resources/allure.properties

**الملف:** [allure.properties](../../../src/main/resources/allure.properties)

إعداد مسار/سلوك Allure وفق المفاتيح الموجودة.

**أسماء المفاتيح فقط:** `allure.results.directory`، `allure.link.issue.pattern`، `allure.link.tms.pattern` القيم لا تظهر في الدليل.

## src/main/resources/Cash.properties

**الملف:** [Cash.properties](../../../src/main/resources/Cash.properties)

سياسات/إعدادات مالية عامة تستخدمها أجزاء الحساب.

**أسماء المفاتيح فقط:** `ArabicnameCash`، `EnglishnameCash`، `Notes`، `Linkacc`، `Linkacc2` القيم لا تظهر في الدليل.

## src/main/resources/log4j2.properties

**الملف:** [log4j2.properties](../../../src/main/resources/log4j2.properties)

تنسيق ومستويات رسائل logging.

**أسماء المفاتيح فقط:** `property.basePath`، `appender`، `appender.file.type`، `appender.file.name`، `appender.file.fileName`، `appender.file.layout.type`، `appender.file.layout.pattern`، `appender.stdout.type`، `appender.stdout.name`، `appender.stdout.layout.type`، `appender.stdout.layout.disableAnsi`، `appender.stdout.layout.noConsoleNoAnsi`، `appender.stdout.layout.pattern`، `rootLogger.level`، `rootLogger.appenderRef.file.ref`، `rootLogger.appenderRef.stdout.ref` القيم لا تظهر في الدليل.

## src/main/resources/Login.properties

**الملف:** [Login.properties](../../../src/main/resources/Login.properties)

مفاتيح/قالب بيانات الدخول العام؛ الأسرار الفعلية تُدار محليًا أو بالبيئة.

**أسماء المفاتيح فقط:** `Binfares.Username`، `Binfares.Password`، `Edu.Username`، `Edu.Password`، `Hotfix.Username`، `Hotfix.Password`، `Demo.Username`، `Demo.Password`، `Test.Username`، `Test.Password`، `InvalidUsername`، `InvalidPassword`، `Crm.Username`، `Crm.Password`، `Hesba.Username`، `Hesba.Password` القيم لا تظهر في الدليل.

## src/main/resources/META-INF/aop.xml

**الملف:** [aop.xml](../../../src/main/resources/META-INF/aop.xml)

ملف XML يحدد دوره من الجذر والمصدر؛ لا يعني امتداده أنه TestNG.

**بنية XML:** الجذر `aspectj`؛ أكثر العناصر: `aspectj` × 1، `aspects` × 1، `aspect` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/main/resources/SeleniumGrid.properties

**الملف:** [SeleniumGrid.properties](../../../src/main/resources/SeleniumGrid.properties)

عنوان خدمة المتصفح البعيد وبيانات اتصال Grid غير المحلية.

**أسماء المفاتيح فقط:** `remotePort`، `remoteHost` القيم لا تظهر في الدليل.

## src/main/resources/services/org.testng.ITestNGListener

**الملف:** [org.testng.ITestNGListener](../../../src/main/resources/services/org.testng.ITestNGListener)

تسجيل خدمة listener لاكتشافها من TestNG حسب آلية الخدمة؛ لا يحتوي حالات اختبار.

## src/main/resources/services/resources/Policy.properties

**الملف:** [Policy.properties](../../../src/main/resources/services/resources/Policy.properties)

ملف دعم ضمن المسار المذكور؛ راجع بنيته واستخداماته قبل تشغيله أو تعديله.

**أسماء المفاتيح فقط:** `TaxMode`، `TaxPercentage`، `Precision`، `DiscountType` القيم لا تظهر في الدليل.

## src/main/resources/video.properties

**الملف:** [video.properties](../../../src/main/resources/video.properties)

إعدادات الفيديو؛ وجودها لا يثبت تسجيل كل اختبار.

**أسماء المفاتيح فقط:** `video.enabled`، `video.mode`، `video.save.mode`، `video.folder`، `recorder.type` القيم لا تظهر في الدليل.

## src/main/resources/Wait.properties

**الملف:** [Wait.properties](../../../src/main/resources/Wait.properties)

إعدادات مدد الانتظار العامة.

**أسماء المفاتيح فقط:** `defultwait` القيم لا تظهر في الدليل.

## src/main/resources/WebData.properties

**الملف:** [WebData.properties](../../../src/main/resources/WebData.properties)

اختيار البيئة والـTenant والمتصفح وطريقة التشغيل والروابط. القيم محلية متغيرة؛ لا تُنسخ هنا.

**أسماء المفاتيح فقط:** `BrowserType`، `ExecutionType`، `Environment`، `Tenant`، `Binfares`، `Gamal`، `Demo`، `Edu`، `Hotfix`، `TestURL`، `NewTestURL`، `Crm`، `Hesba` القيم لا تظهر في الدليل.

## src/test/resources/additional/internal/regression-additional-testng.xml

**الملف:** [regression-additional-testng.xml](../../../src/test/resources/additional/internal/regression-additional-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 7، `suite-file` × 2، `suite` × 1، `suite-files` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/internal/regression-workflows-testng.xml

**الملف:** [regression-workflows-testng.xml](../../../src/test/resources/additional/internal/regression-workflows-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 7، `include` × 7، `methods` × 2، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/legacy/purchases-compatibility-testng.xml

**الملف:** [purchases-compatibility-testng.xml](../../../src/test/resources/additional/legacy/purchases-compatibility-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 3، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/legacy/sales-compatibility-testng.xml

**الملف:** [sales-compatibility-testng.xml](../../../src/test/resources/additional/legacy/sales-compatibility-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 3، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/legacy/testng.xml

**الملف:** [testng.xml](../../../src/test/resources/additional/legacy/testng.xml)

تعريف توافق/استخدام قديم؛ لا تساويه بالمجموعة الأساسية الحديثة دون فحص اختيارها.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 11، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/acceptance-testng.xml

**الملف:** [acceptance-testng.xml](../../../src/test/resources/additional/standalone/acceptance-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite-file` × 2، `suite` × 1، `suite-files` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/allure-journey-smoke-testng.xml

**الملف:** [allure-journey-smoke-testng.xml](../../../src/test/resources/additional/standalone/allure-journey-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 5، `methods` × 5، `include` × 5، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/browser-isolation-testng.xml

**الملف:** [browser-isolation-testng.xml](../../../src/test/resources/additional/standalone/browser-isolation-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 8، `class` × 4، `methods` × 4، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/document-list-refactor-smoke-testng.xml

**الملف:** [document-list-refactor-smoke-testng.xml](../../../src/test/resources/additional/standalone/document-list-refactor-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 4، `class` × 2، `methods` × 2، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/fixture-data-refactor-smoke-testng.xml

**الملف:** [fixture-data-refactor-smoke-testng.xml](../../../src/test/resources/additional/standalone/fixture-data-refactor-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 10، `class` × 6، `methods` × 6، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/fixture-lifecycle-testng.xml

**الملف:** [fixture-lifecycle-testng.xml](../../../src/test/resources/additional/standalone/fixture-lifecycle-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 9، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/fixture-refactor-smoke-testng.xml

**الملف:** [fixture-refactor-smoke-testng.xml](../../../src/test/resources/additional/standalone/fixture-refactor-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 8، `class` × 4، `methods` × 4، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/form-interactions-smoke-testng.xml

**الملف:** [form-interactions-smoke-testng.xml](../../../src/test/resources/additional/standalone/form-interactions-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 6، `class` × 3، `methods` × 3، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/module-lifecycle-smoke-testng.xml

**الملف:** [module-lifecycle-smoke-testng.xml](../../../src/test/resources/additional/standalone/module-lifecycle-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 3، `methods` × 3، `include` × 3، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/page-object-core-refactor-testng.xml

**الملف:** [page-object-core-refactor-testng.xml](../../../src/test/resources/additional/standalone/page-object-core-refactor-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 14، `class` × 9، `methods` × 9، `test` × 7، `classes` × 7، `suite` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/page-object-final-refactor-testng.xml

**الملف:** [page-object-final-refactor-testng.xml](../../../src/test/resources/additional/standalone/page-object-final-refactor-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 20، `class` × 18، `methods` × 18، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/page-object-refactor-testng.xml

**الملف:** [page-object-refactor-testng.xml](../../../src/test/resources/additional/standalone/page-object-refactor-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 38، `class` × 8، `methods` × 8، `test` × 7، `classes` × 7، `suite` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/point-of-sale-testng.xml

**الملف:** [point-of-sale-testng.xml](../../../src/test/resources/additional/standalone/point-of-sale-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `test` × 1، `classes` × 1، `class` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/products-finance-interactions-smoke-testng.xml

**الملف:** [products-finance-interactions-smoke-testng.xml](../../../src/test/resources/additional/standalone/products-finance-interactions-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 4، `class` × 2، `methods` × 2، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/purchase-calendar-smoke-testng.xml

**الملف:** [purchase-calendar-smoke-testng.xml](../../../src/test/resources/additional/standalone/purchase-calendar-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 2، `suite` × 1، `test` × 1، `classes` × 1، `class` × 1، `methods` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/purchase-smoke-testng.xml

**الملف:** [purchase-smoke-testng.xml](../../../src/test/resources/additional/standalone/purchase-smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `test` × 1، `classes` × 1، `class` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/additional/standalone/restaurant-testng.xml

**الملف:** [restaurant-testng.xml](../../../src/test/resources/additional/standalone/restaurant-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `test` × 1، `classes` × 1، `class` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/basic-data-testng.xml

**الملف:** [basic-data-testng.xml](../../../src/test/resources/basic-data-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 11، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/ci-testng.xml

**الملف:** [ci-testng.xml](../../../src/test/resources/ci-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 46، `listener` × 2، `suite` × 1، `listeners` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/coverage-expansion-testng.xml

**الملف:** [coverage-expansion-testng.xml](../../../src/test/resources/coverage-expansion-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 15، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/coverage-observations-testng.xml

**الملف:** [coverage-observations-testng.xml](../../../src/test/resources/coverage-observations-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 24، `class` × 17، `methods` × 15، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/coverage-recovery-testng.xml

**الملف:** [coverage-recovery-testng.xml](../../../src/test/resources/coverage-recovery-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `test` × 1، `classes` × 1، `class` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/coverage-verification-testng.xml

**الملف:** [coverage-verification-testng.xml](../../../src/test/resources/coverage-verification-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite-file` × 2، `suite` × 1، `suite-files` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/dealer-model-testng.xml

**الملف:** [dealer-model-testng.xml](../../../src/test/resources/dealer-model-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 6، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/e2e-testng.xml

**الملف:** [e2e-testng.xml](../../../src/test/resources/e2e-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 18، `class` × 15، `test` × 7، `classes` × 7، `methods` × 7، `suite` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/financial-movements-testng.xml

**الملف:** [financial-movements-testng.xml](../../../src/test/resources/financial-movements-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 10، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/general-accounting-testng.xml

**الملف:** [general-accounting-testng.xml](../../../src/test/resources/general-accounting-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `test` × 1، `classes` × 1، `class` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/login-testng.xml

**الملف:** [login-testng.xml](../../../src/test/resources/login-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `test` × 1، `classes` × 1، `class` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/META-INF/services/org.testng.ITestNGListener

**الملف:** [org.testng.ITestNGListener](../../../src/test/resources/META-INF/services/org.testng.ITestNGListener)

تسجيل خدمة listener لاكتشافها من TestNG حسب آلية الخدمة؛ لا يحتوي حالات اختبار.

## src/test/resources/products-testng.xml

**الملف:** [products-testng.xml](../../../src/test/resources/products-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 5، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/purchases-testng.xml

**الملف:** [purchases-testng.xml](../../../src/test/resources/purchases-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 7، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/README.md

**الملف:** [README.md](../../../src/test/resources/README.md)

مدخل المشروع القديم وفيه معلومات تشغيل ونتائج بتاريخها؛ بعض أرقامه تاريخية مقارنة بالدليل الحالي.

## src/test/resources/regression-testng.xml

**الملف:** [regression-testng.xml](../../../src/test/resources/regression-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite-file` × 10، `suite` × 1، `suite-files` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/sales-testng.xml

**الملف:** [sales-testng.xml](../../../src/test/resources/sales-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 8، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/sanity-testng.xml

**الملف:** [sanity-testng.xml](../../../src/test/resources/sanity-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `include` × 49، `class` × 25، `methods` × 18، `test` × 14، `classes` × 14، `suite` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/smoke-testng.xml

**الملف:** [smoke-testng.xml](../../../src/test/resources/smoke-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 13، `methods` × 13، `include` × 13، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/special-sales-testng.xml

**الملف:** [special-sales-testng.xml](../../../src/test/resources/special-sales-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `test` × 3، `classes` × 3، `class` × 3، `suite` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/store-testng.xml

**الملف:** [store-testng.xml](../../../src/test/resources/store-testng.xml)

اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 6، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/BasicData.xml

**الملف:** [BasicData.xml](../../../src/test/resources/Test-Data/BasicData.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite` × 1، `suite-files` × 1، `suite-file` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/Dealer.xml

**الملف:** [Dealer.xml](../../../src/test/resources/Test-Data/Dealer.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 5، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/FinancialMovements_Coverage.md

**الملف:** [FinancialMovements_Coverage.md](../../../src/test/resources/Test-Data/FinancialMovements_Coverage.md)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/FinanicalMovment.xml

**الملف:** [FinanicalMovment.xml](../../../src/test/resources/Test-Data/FinanicalMovment.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 9، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/GeneralJournal_Automation_Coverage.md

**الملف:** [GeneralJournal_Automation_Coverage.md](../../../src/test/resources/Test-Data/GeneralJournal_Automation_Coverage.md)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/Login.data.json

**الملف:** [Login.data.json](../../../src/test/resources/Test-Data/Login.data.json)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/ProductCategories.xml

**الملف:** [ProductCategories.xml](../../../src/test/resources/Test-Data/ProductCategories.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 5، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/Products_Coverage.md

**الملف:** [Products_Coverage.md](../../../src/test/resources/Test-Data/Products_Coverage.md)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/Purches.xml

**الملف:** [Purches.xml](../../../src/test/resources/Test-Data/Purches.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 7، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/RegerationTest.xml

**الملف:** [RegerationTest.xml](../../../src/test/resources/Test-Data/RegerationTest.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `suite-file` × 5، `suite` × 1، `suite-files` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/SalesTest.xml

**الملف:** [SalesTest.xml](../../../src/test/resources/Test-Data/SalesTest.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 8، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## src/test/resources/Test-Data/Store_Coverage.md

**الملف:** [Store_Coverage.md](../../../src/test/resources/Test-Data/Store_Coverage.md)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/Store_Known_Issues.md

**الملف:** [Store_Known_Issues.md](../../../src/test/resources/Test-Data/Store_Known_Issues.md)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/Store_README.md

**الملف:** [Store_README.md](../../../src/test/resources/Test-Data/Store_README.md)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

## src/test/resources/Test-Data/StoreTest.xml

**الملف:** [StoreTest.xml](../../../src/test/resources/Test-Data/StoreTest.xml)

بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.

**بنية XML:** الجذر `suite`؛ أكثر العناصر: `class` × 6، `suite` × 1، `test` × 1، `classes` × 1. عدد العناصر ليس عدد حالات منفذة.

## style_invoice.py

**الملف:** [style_invoice.py](../../../style_invoice.py)

سكربت تنسيق جانبي لتقرير فاتورة RDL؛ يعتمد على ملف خارج المستودع حسب المصدر.

## upgrade_sales.py

**الملف:** [upgrade_sales.py](../../../upgrade_sales.py)

سكربت عمل جانبي لتعديل ملفات RDL بمسارات محلية ثابتة؛ ليس جزءًا من بناء Java المعتاد.

## ملفات محلية ومخرجات ليست مصادر للدراسة ملفًا ملفًا

`config/Login.local.properties` وملفات البريد/Drive المحلية: إعدادات خاصة لا ننسخ قيمها. `target` و`allure-results` و`allure-report` و`test-output` و`screenshots` و`videos`: نواتج وسجلات، يشرح الفصل التاسع أنواعها. ملفات `hs_err_pid*.log` و`replay_pid*.log`: تشخيصات JVM تاريخية لا تمثل سلوك الاختبارات المقصود. `.idea` وملفات IDE: تفضيلات جهاز. `.git`: تاريخ Git الداخلي. لا تنفذ حذفًا عامًا لأي منها على أساس قراءة هذه الفقرة؛ خصوصًا سجلات target قد تكون لازمة للاسترجاع.

ملفات `docs/testing` والتوثيق القديم وملفات `outputs` هي مراجع أو تسليمات تاريخية وليست المصدر التنفيذي. الدليل الجديد مستقل ويذكر عند الحاجة ما إذا كانت معلومة تاريخية أو من الكود الحالي.
