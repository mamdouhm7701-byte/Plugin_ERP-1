# التحقق من عزل المتصفح — 21 أكتوبر 2026

أصبح متصفح `BaseTest` خاصًا بنسخة كلاس الشاشة بدل متغير ساكن مشترك بين جميع الكلاسات. يبدأ في `BeforeClass` ويغلق بعد تنظيف بيانات الكلاس في `AfterClass`. الحالات داخل الكلاس تحتفظ بالمراجع الثابتة الخاصة بالمجموعة وتعمل تسلسليًا. اختبارات المصادقة المستقلة تستمر بجلسة جديدة لكل حالة.

تنتظر مرحلة الدخول ظهور النموذج ثم الصفحة الرئيسية، بدل انتظار خمس ثوانٍ. أزيلت 18 مهلة ثابتة قبل التنقل في اختبارات العملاء والموردين. يظل انتظار تحميل الشاشات والنماذج في أدوات الصفحات نفسها؛ لم تُزل جميع فترات الانتظار في المشروع.

الفحص الحي النهائي نجح في ثماني حالات عبر أربع شاشات:

نجح التحقق المحلي النهائي في **254 حالة دون فشل أو تخطٍ**، بما فيها حسابات المبيعات والمشتريات وتعريفات التشغيل وتوافق Java وAspectJ وملكية المتصفح وتنظيف بيانات الموديولات واسترجاعها.

| الكلاس | الحالات | النتيجة |
| --- | --- | --- |
| `ProductsTest.UnitTest` | `AddNewUnite`, `PR_CancelNew` | 2 ناجحتان |
| `DealerModelTest.CustomersTest` | `CU_002_CloseCustomerFormWithoutSaving`, `SaveWithOutArabicName` | 2 ناجحتان |
| `Invoice.SalesInvoiceCoverageTest` | `savesOwnedItemFromPicker`, `emptyDocumentCannotSave` | 2 ناجحتان |
| `Purches.PurchaseInvoiceCoverageTest` | `savesOwnedItemFromPicker`, `emptyDocumentCannotSave` | 2 ناجحتان |

سجّل التشغيل تهيئة أربعة متصفحات. نجحت إعدادات `setup` و`tearDown` لكل كلاس، ولم تفشل أي مرحلة تجهيز أو تنظيف. كانت سجلات البيانات والمحاولات الثمانية التي كتبها هذا التشغيل فارغة بعد التنظيف. لم تُحذف السجلات التاريخية الأخرى.

التشغيل الأول كشف أن تكبير نافذة Chrome في Headless يمنع اجتياز التحقق من عنوان الصفحة الرئيسية في هذه البيئة. نجح الفحص بعد تثبيت نافذة Headless على 1920×1080. أضيف اختبار محلي لهذا السلوك مع اختبارات ملكية المتصفح، وإغلاقه عند فشل الدخول، وتحرير مرجع المصنع عند فشل الإغلاق، ومنع استبدال متصفح ما زال نشطًا.

لإعادة الفحص الحي:

```powershell
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/browser-isolation-testng.xml' test
```

الأدلة المحلية الناتجة عن التنفيذ: `target/browser-isolation-live-final.log` و`target/browser-isolation-live-testng-results.xml` و`target/browser-isolation-live-journal-check.json`. نتائج التحقق المحلي النهائي في `target/browser-isolation-local-final.log`. ملفات target نواتج محلية لا تحفظ في Git.

هذا تحقق محدد من دورة حياة المتصفح والانتقال بين الشاشات؛ لا يمثل تشغيل Regression كاملًا أو إثبات صلاحية التشغيل المتوازي.
