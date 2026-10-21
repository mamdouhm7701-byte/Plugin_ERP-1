# ملفات تشغيل الاختبارات

ملفات التشغيل الأساسية موجودة مباشرة في هذا المجلد:

- مستويات الاختبار: `smoke-testng.xml` و`sanity-testng.xml` و`regression-testng.xml` و`e2e-testng.xml`.
- الموديولات: `products-testng.xml` و`dealer-model-testng.xml` و`store-testng.xml` و`financial-movements-testng.xml` و`sales-testng.xml` و`purchases-testng.xml` و`special-sales-testng.xml`.
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
احتفظنا بتعريفات التغطية كما هي؛ النقل لتنظيم مداخل التشغيل.

`Test-Data/` يحتوي بيانات الاختبارات، وليس ملفات تشغيل. `pom.xml` في جذر المشروع يخص بناء Maven.
