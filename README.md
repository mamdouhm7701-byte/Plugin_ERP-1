# Plugin ERP — الاختبارات الآلية

إطار اختبارات Java وSelenium وTestNG لشاشات ERP، مع تقارير Allure واختبارات محلية للحسابات وملكية بيانات الاختبار وتنظيفها.

## شرح بسيط للمشروع والتستات

- [دليل الحالات القابل للبحث:918 حالة](docs/testing/TEST_CASES_SIMPLE_AR.html).
- [إزاي تقرأ الحالة وخطواتها](docs/testing/TEST_CASES_SIMPLE_AR.md).
- [أنواع المشاكل ومعنى كل تصنيف](docs/testing/BUG_TYPES_SIMPLE_AR.md).
- [فرز الملفات: إيه المطلوب وإيه المؤقت](docs/testing/FILE_PURPOSE_AND_CLEANUP_AR.md).

شرح مبسط في أول كل ملف من553 ملف Java، يشمل كود التشغيل والاختبارات والمساعدات. يوجد شرح أيضًا في127 ملف تشغيل وإعدادات وأدوات. ملفات البيانات والصور لها تعريف بجانبها في `شرح_الملفات.md` لأن صيغتها لا تقبل تعليقات عادية.

## الحالة الحالية — 24 أكتوبر2026

آخر فحص محلي بعد إصلاح انتقال البيع إلى المرتجع المرتبط: **459 ناجحة، دون فشل أو تخطي**. ملفات التشغيل الحالية تختار13 حالة Smoke و57 Sanity و41 رحلة E2E و1124 حالة في Regression؛ الأعداد تعبر عن اختيارات الدوال، وقد يزيد عدد الاستدعاءات بسبب DataProvider.

إعادة تشغيل المجموعات الأربع على Demo بعد الإصلاح بانتظار نتائج المستخدم. نجاح Smoke وحده أو صحة XML لا يثبت نجاح E2E أو Regression. أحدث نتيجة كل مجموعة وحدودها تُراجع في [متابعة التحقق والإصلاحات](docs/testing/COVERAGE_VERIFICATION_2026_10_23_AR.md)، وغرض المجموعات وأوامرها في [مستويات الاختبار](docs/testing/TEST_LEVELS_AR.md). نتائج CI المستضاف أو التشغيلات التاريخية أدناه تخص نسخها وتواريخها.

## التشغيل

للفحص الموحد من PowerShell، شغّل أحد الأوامر التالية من جذر المشروع. كل تشغيل يحتفظ بسجل مستقل تحت `target/validation`، ولا يحذف سجلات الاستعادة:

```powershell
./scripts/Test-Project.ps1
./scripts/Test-Project.ps1 -Mode Acceptance
./scripts/Test-Project.ps1 -Mode Regression
```

الأمر الافتراضي يشغّل الفحوص المحلية فقط. `Acceptance` يشغّلها أولًا ثم اختبار الدخول وSmoke الحي. `Regression` يشغّلها أولًا ثم التغطية الحية الواسعة؛ تظل عيوب التطبيق المعروفة ظاهرة. يتوقف المسار عند فشل أي مرحلة. لا تشغّل أمرين في نسخة العمل نفسها بالتوازي لأن سجلات البيانات وSurefire مشتركة أثناء التنفيذ.

راجع [المراجعة الختامية وحدود التحقق](docs/testing/FINAL_REVIEW_AR.md)، و[تفاصيل إعادة التنظيم](docs/testing/REFACTORING_AR.md).

تمت مراجعة أدوات التحقق القديمة ضمن [رقم 4: إصلاح الظهور وعزل التحقق المرن](docs/testing/LEGACY_ASSERTIONS_AR.md)، مع الحفاظ على واجهاتها الحالية وإضافة اختبارات محلية لها.

يلزم Maven وJDK 21. يختار Maven إصدار Java المناسب عبر Toolchains؛ راجع [إعداد Java](docs/testing/JAVA_SETUP_AR.md).

إعدادات البيئة والمتصفح في `src/main/resources/WebData.properties`. بيانات الدخول محلية في `config/Login.local.properties` أو متغيرات البيئة؛ راجع [إعداد بيانات الدخول](docs/testing/CREDENTIALS_AR.md). لا تحفظ كلمات المرور في Git.

GitHub Actions يشغّل الفحوص المحلية على Windows وUbuntu باستخدام Java 21. لإعادة نفس الفحص دون الاتصال بـERP استخدم `mvn -Pci verify`. النتائج داخل `target/ci`؛ لا يُفتح Allure تلقائيًا. راجع [دليل CI والتقارير](docs/testing/CI_AR.md).

يمكن إرسال تقريري Allure وTestNG إلى Gmail بعد التشغيل المحلي أو GitHub Actions، مع عدة مستلمين؛ راجع [إعداد إرسال التقارير](docs/testing/REPORT_MAIL_AR.md).

تم التحقق من نجاح **396 حالة على كل نظام داخل GitHub نفسه**؛ [نتيجة التشغيل المستضاف](docs/testing/GITHUB_CI_VALIDATION_AR.md).

فحص محلي سريع لا يتصل بنظام ERP:

```powershell
mvn '-Dtest=RuntimeCompatibilityTest,BrowserLifecycleTest,ConfigurationPropertiesTest,SuiteDefinitionsTest,SalesCalculationTest,PurchaseCalculationTest' test
```

اختبار الدخول الفعلي:

```powershell
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/login-testng.xml' test
```

اختبارات Smoke تنشئ بيانات فعلية تخص الاختبار وتنظفها؛ اختر البيئة والفرع المناسبين قبل التشغيل:

```powershell
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/smoke-testng.xml' test
```

جميع مداخل التشغيل موضحة في [دليل ملفات الاختبارات](src/test/resources/README.md). تظل المجموعات متسلسلة؛ لكل كلاس شاشة متصفح مستقل، والحالات داخل الكلاس تحتفظ بسياق تجهيز بياناته. راجع [دورة حياة البيانات](docs/testing/PAGE_FIXTURE_LIFECYCLE.md).

## تنظيم الملفات

| المسار | المحتوى |
| --- | --- |
| `src/main/java` | صفحات النظام وأدوات Selenium والإعدادات والحسابات |
| `src/test/java` | حالات الاختبار وتجهيز البيانات وتنظيفها والتحقق المحلي |
| `src/test/resources` | ملفات TestNG وبيانات الاختبارات |
| `docs` | التشغيل والتغطية والنتائج والعيوب المعروفة |
| `outputs` | المستندات والجداول المسلّمة وسكربتات إنشائها؛ ليست مطلوبة لبناء Java |
| `target` | نواتج Maven وسجلات بيانات الاختبار والاسترجاع المحلية |

المكتبات المحمّلة وملفات IDE والتقارير والصور والفيديوهات الناتجة عن التشغيل محلية ويتجاهلها Git. أداة Allure الموجودة تحت `allure-cli` قابلة للتنزيل التلقائي من كود التقارير عند غيابها. سكربتات إنشاء المستندات قد تحتاج مكتبات Node/Python الخاصة ببيئة إنشائها؛ لا يعتمد بناء Java عليها.

ملفات `target` قد تحتوي سجلات استرجاع لبيانات لم يكتمل تنظيفها. راجع [دورة حياة البيانات](docs/testing/PAGE_FIXTURE_LIFECYCLE.md) قبل حذفها أو تشغيل `mvn clean` بعد تشغيل حي غير مكتمل.

## تنسيق الكود والأسماء

شغّل `mvn spotless:apply` لتنسيق Java وتنظيف المسافات في الملفات النصية المحددة. `mvn verify` يتضمن `spotless:check`؛ الفحص يرفض اختلاف التنسيق ولا يعيد كتابة الملفات تلقائيًا. إعداد Google Java Format مثبت بنمط AOSP. لا يشمل التنسيق التقارير والمخرجات وبيانات الدخول المحلية.

الأسماء الحالية: `Plugin_ERP.Util` و`Plugin_ERP.Listeners` و`ElementActions` و`WaitManagement` و`Firefox`، والكتابة عبر `typeText` و`typeTextAndVerify`. حدثت مراجع Java وTestNG والسكربتات؛ إعدادات IDE الشخصية القديمة قد تحتاج اختيار الكلاس باسمه الجديد.

الكتابة تستخدم `TextEntryActions`: تحقق من القيمة، ثلاث محاولات Selenium، وسبب فشل محفوظ دون تسجيل النص المدخل. اختبار تسليم الخزينة يقارن إجمالي API والشاشة والطباعة بتوقع مستقل من الرصيد الافتتاحي المملوك للاختبار دون حركات؛ لا يدعي تغطية التسوية المختلطة. مراجعة fallback في [سجل الاستثناءات](docs/testing/SILENT_CATCH_AUDIT_AR.md).

حالة تحسينات الصيانة والتغطية ونتائج التحقق الأحدث لهذه الدفعة في [سجل النقاط](docs/testing/ALL_POINTS_STATUS_2026_10_27_AR.md). تشغيل فحص محلي بمخرجات منفصلة عن IntelliJ: `mvn -Derp.build.directory=target/maven-verification -Pci verify`.

تسجيل حالات الفشل اختياري: اضبط `video.enabled=true` في `src/main/resources/video.properties` أو مرر `-Dvideo.enabled=true`؛ القيمة الافتراضية false. يحفظ MP4 للحالات الفاشلة فقط ويرفقه في Allure. [تفاصيل تسجيل المتصفح وحدوده](docs/testing/FAILURE_VIDEO_AR.md).

اختيار إعداد فرع متوقع اختياري عبر `-Dbranch.profile=public-reference` بعد تجهيز ملفه تحت `config/branch-profiles`. بدون الخاصية يبقى المسار الحالي كما هو. [التجهيز وحدود التحقق](docs/testing/BRANCH_PROFILE_SETUP_AR.md). [عقد الحساب وحالة ربط الموديولات](docs/testing/BRANCH_FINANCE_CONTRACT_AR.md). [الشاشات حسب النشاط](docs/testing/ACTIVITY_COVERAGE_AR.md). [تجهيز فروع الاختبار](docs/testing/BRANCH_PROVISIONING_AR.md). [Smoke الإضافية للمطاعم](docs/testing/RESTAURANT_ADDITIONAL_TAX_AR.md).

تشغيل النشاط والسياسات من IntelliJ: [دليل ERP - Business run](docs/testing/BUSINESS_RUNNER_AR.md).

بعد التشغيل، ملخص أسباب الفشل والأدلة والتكرار وإعادة الاختبار في `target/failure-review/summary.html`.
للـCI يكون بجانب نتائج تشغيله المنفصلة. [طريقة قراءة الملخص ومتابعة الإصلاح](docs/testing/FAILURE_REVIEW_AR.md).

لقياس قابلية تكرار نفس المجموعة ثلاث مرات: `./scripts/Test-Stability.ps1 -Mode Local -Repeats 3`.
استخدم `-Mode Business` لاختيار إعدادات البيزنس مرة واحدة وتشغيل الجولات بفروع جديدة.
[معنى نتائج الثبات وحدودها](docs/testing/REPEATABILITY_AR.md).

تشغيل البيزنس يراجع سعة الاشتراك قبل التجهيز، ويمنع تكرار إنشاء البيانات بعد رفض حد الترخيص
مع استمرار التنظيف. [فحص السعة وحدود الحماية](docs/testing/CAPACITY_READINESS_AR.md).

لربط نتائج محفوظة بالشاشات ومعرفة أبطأ المجموعات وأوقات تجهيزها وتنظيفها:
`python -X utf8 scripts/build_run_evidence.py allure-results target/run-evidence`.
[قراءة أدلة التغطية والوقت وحدودها](docs/testing/RUN_EVIDENCE_AR.md).


تقرير الصيانة والتحقق الحالي: [حالة الصيانة وحدود التأهيل الحي](docs/testing/FINAL_MAINTENANCE_STATUS_AR.md).

اختيار ملف XML مع وصفه ومراجعة الفواتير الأربع على سياسات الضريبة الثلاث: [دليل ERP - XML review](docs/testing/XML_REVIEW_RUNNER_AR.md).

تنظيف بيانات تشغيل توقف سابقًا من نافذة مستقلة: [دليل ERP - Cleanup](docs/testing/CLEANUP_RUNNER_AR.md).

[مراجعة آخر تشغيل XML وأسباب الفشل والوقت والإصلاحات المثبتة](docs/testing/LAST_XML_RUN_REVIEW_AR.md).

[تحسين الوقت على مستوى الإطار وإصلاح توقف الانتظار مع الفيديو](docs/testing/FRAMEWORK_SPEED_IMPROVEMENTS_AR.md).
