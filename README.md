# Plugin ERP — الاختبارات الآلية

إطار اختبارات Java وSelenium وTestNG لشاشات ERP، مع تقارير Allure واختبارات محلية للحسابات وملكية بيانات الاختبار وتنظيفها.

## التشغيل

يلزم Maven وJDK 21. يختار Maven إصدار Java المناسب عبر Toolchains؛ راجع [إعداد Java](docs/testing/JAVA_SETUP_AR.md).

إعدادات البيئة والمتصفح في `src/main/resources/WebData.properties`. بيانات الدخول محلية في `config/Login.local.properties` أو متغيرات البيئة؛ راجع [إعداد بيانات الدخول](docs/testing/CREDENTIALS_AR.md). لا تحفظ كلمات المرور في Git.

فحص محلي سريع لا يتصل بنظام ERP:

```powershell
mvn '-Dtest=RuntimeCompatibilityTest,BrowserLifecycleTest,SuiteDefinitionsTest,SalesCalculationTest,PurchaseCalculationTest' test
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
