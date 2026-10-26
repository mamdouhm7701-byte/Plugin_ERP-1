# Plugin ERP — الاختبارات الآلية

إطار اختبارات Java وSelenium وTestNG لشاشات ERP، مع تقارير Allure واختبارات محلية للحسابات وملكية بيانات الاختبار وتنظيفها.

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
