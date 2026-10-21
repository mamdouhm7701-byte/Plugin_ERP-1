# Plugin ERP — الاختبارات الآلية

إطار اختبارات Java وSelenium وTestNG لشاشات ERP، مع تقارير Allure واختبارات محلية للحسابات وملكية بيانات الاختبار وتنظيفها.

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
