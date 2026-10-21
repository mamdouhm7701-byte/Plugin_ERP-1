# تقسيم الكلاسات الكبيرة

اقتصر التغيير على تجهيز بيانات المبيعات ومحاكاة API في اختبارات تنظيف المبيعات والمشتريات. ظلت كلاسات الاختبار وأسماء الحالات ومداخل TestNG كما هي.

## فصل المسؤوليات

| الملف | المسؤولية بعد الفصل |
| --- | --- |
| `Invoice/SalesTestData.java` | ملكية البيانات ونطاق الحالة والسجلات الدائمة والاسترجاع وترتيب التنظيف؛ يحتفظ بواجهات الاستدعاء السابقة |
| `Invoice/SalesFixtureDefinitions.java` | عشرون تعريفًا لإنشاء البيانات، تشمل الحسابات والأصناف والمتعاملين وطرق الدفع والمستندات الأولية؛ يستخدم التسجيل والحماية الموجودة في SalesTestData |
| `Invoice/SalesFixtureApi.java` | الاتصال بواجهة ERP داخل المتصفح؛ يستدعي حارس البيئة والفرع نفسه قبل كل طلب، ويحتفظ بالمهل ورسائل الخطأ السابقة |
| `Invoice/SalesFixtureApiStub.java` | محاكاة API للمبيعات وحالات فقد الاستجابة وفشل الحذف وتغيير الفرع |
| `Purches/PurchaseFixtureApiStub.java` | محاكاة API للمشتريات وحالات الفشل الخاصة بها |

ظلت حالات الاختبار وتوقعاتها داخل `SalesCleanupTest` و`PurchaseCleanupTest`. لكل وحدة محاكاتها المستقلة وسلوك الفشل السابق. لا توجد حالة مشتركة ساكنة أضيفت بين الاختبارات.

| الكلاس الأصلي | الأسطر قبل | الأسطر بعد |
| --- | ---: | ---: |
| SalesTestData | 1,494 | 1,060 |
| SalesCleanupTest | 1,219 | 1,029 |
| PurchaseCleanupTest | 1,352 | 1,140 |

الأرقام تشمل الأسطر الفارغة. الكلاسات الأصلية تحتفظ بالأجزاء المرتبطة بدورة حياة البيانات وتوقعات الحالات؛ يمكن تقسيم الأجزاء المتبقية في دفعات مستقلة عند وجود حدود واضحة للمسؤوليات.

## التحقق

مقارنة المصدر بالنسخة السابقة أكدت بقاء ثلاثين دالة تخص الحماية والملكية والتنظيف والاسترجاع دون تغيير. تعريفات البيانات العشرون وJavaScript الخاص بالاتصال انتقلت بالمحتوى السابق. لم تتغير أجسام حالات اختبارات التنظيف إلا أسماء نوع المحاكاة، مع إزالة imports التي انتقلت إلى الملفات الجديدة.

نجح `mvn -Pci verify` في **285 حالة دون فشل أو تخطٍ**. ونجحت حالتا `SalesInvoiceCoverageTest.savesOwnedItemFromPicker` و`emptyDocumentCannotSave` على ERP الفعلي، مع نجاح تجهيز الكلاس وتنظيفه. بعد التشغيل كان سجلا المبيعات والمحاولات اللذان أنشأهما فارغين. السجلات التاريخية الأخرى لم تُحذف.

الأدلة المحلية: `target/large-class-refactor-ci-final.log` و`target/large-class-refactor-live-final.log` و`target/refactoring-baseline/verification.json` و`target/refactoring-baseline/live-journal-check.json`. لا تُضاف نواتج target إلى Git.

فُعّلت الحالتان الحيتان عبر ملف TestNG مؤقت يختار الدوال الموروثة صراحةً؛ محاولة الاختيار الأولى عبر مرشح Surefire انتهت بصفر حالات ولم تُحسب ضمن التحقق. هذا تحقق محدد من الفصل الجديد، ولا يمثل إعادة تشغيل Regression الحي بالكامل.

## الدفعة الثانية: اتصال المشتريات

انتقل اتصال المتصفح من `Purches/PurchaseTestData.java` إلى `Purches/PurchaseFixtureApi.java`. يحتفظ المالك بواجهته السابقة، وتعريفات البيانات، وقواعد الملكية والفرع، وسجلات الاستعادة، وترقيم صفحات إيصالات المورد، وترتيب التنظيف. بقيت سياسة إعادة محاولة SQL deadlock ثلاث مرات داخل المالك دون تغيير.

مقارنة عكسية للتعديل أكدت مطابقة بقية ملف المالك للنسخة السابقة حرفيًا. انتقل جسم الاتصال كما هو، باستثناء إتاحة الاستدعاء داخل الحزمة وتفويض حارس البيئة والفرع عبر callback. لم تتغير JavaScript أو المهل أو معالجة الأخطاء.

نجح `mvn -Pci verify` في **285 حالة، دون فشل أو تخطٍ**، بما فيها حالات إعادة محاولة deadlock وحدودها. ونجحت حالتا `Purches.PurchaseInvoiceCoverageTest.savesOwnedItemFromPicker` و`emptyDocumentCannotSave` على ERP الفعلي مع اكتمال التنظيف. أصبح سجلا البيانات والمحاولات الخاصان بالتشغيل فارغين. هذا تحقق حي محدد، وليس تشغيل Regression كاملًا.

الأدلة المحلية: `target/purchase-api-refactor-ci.log` و`target/purchase-api-refactor-live.log` و`target/purchase-refactoring-baseline/verification.txt` و`target/purchase-refactoring-baseline/live-journal-check.json`. ظلت السجلات التاريخية محفوظة.

## الدفعة الثالثة: تعريفات بيانات المشتريات

انتقلت 18 دالة لتعريف الحسابات والوحدات والتصنيفات والأصناف والمندوبين والمتعاملين ووسائل الدفع إلى `Purches/PurchaseFixtureDefinitions.java`. يحتفظ `PurchaseTestData` بواجهات الاستدعاء السابقة، ويستمر التسجيل عبر `fixture` وربط المخازن عبر `store` في المالك نفسه. بقيت وصفات `seedPurchase` المرتبطة بنطاق الحالة ومعرّف التشغيل داخل المالك، مع الحماية والملكية وإعادة المحاولة والاستعادة والتنظيف.

أثبتت المقارنة العكسية مطابقة بقية ملف المالك حرفيًا للنسخة السابقة، ومطابقة أجسام التعريفات المنقولة بعد استثناء تفويض `fixture` و`store`. انخفض ملف المالك من 522 إلى 496 سطرًا؛ الهدف فصل المسؤولية، مع الإبقاء على التنسيق الحالي لهذه الدفعة.

نجحت **285 حالة محلية** عبر `mvn -Pci verify` دون فشل أو تخطٍ، ونجحت حالتا `Purches.PurchaseInvoiceCoverageTest.savesOwnedItemFromPicker` و`emptyDocumentCannotSave` على ERP الفعلي مع اكتمال التنظيف وخلو سجلي التشغيل من البيانات. لم تُحذف السجلات التاريخية، ولم يُشغّل Regression الحي بالكامل.

الأدلة المحلية: `target/purchase-definitions-ci.log` و`target/purchase-definitions-live.log` و`target/purchase-definitions-baseline/verification.json` و`target/purchase-definitions-baseline/live-journal-check.json`.

## الدفعة الرابعة: المخازن والمبيعات الخاصة

شملت هذه الدفعة وحدتين معًا، مع إبقاء التعديلات السابقة في المشتريات:

| الوحدة | تعريفات البيانات | اتصال المتصفح | محاكاة اختبارات التنظيف |
| --- | --- | --- | --- |
| المخازن | `StoreFixtureDefinitions`، ويضم 13 دالة تشمل الصنف المصنّع ومكوناته | `StoreFixtureApi` | `StoreFixtureApiStub` |
| نقطة البيع والمطعم | `SpecialSalesFixtureDefinitions`، ويضم 14 دالة تشمل العملاء والطاولات وطرق الدفع | `SpecialSalesFixtureApi` | `SpecialSalesFixtureApiStub` |

بقيت إدارة الملكية والسجلات والاستعادة وترتيب الحذف داخل المالك الحالي لكل وحدة. بقي ربط المخزن بالفرع وتسجيله قبل التحديث كما هو، وبقي حفظ إعدادات الفرع الافتراضية واستعادتها والتحقق من التغييرات الخارجية في المبيعات الخاصة كما هو. يحتفظ اتصال المبيعات الخاصة باستدعاء `FixtureSession.verify` نفسه، واتصال المخازن بفحص البيئة والفرع داخل JavaScript نفسه. لم تُوحّد اختلافات الوحدتين أثناء النقل.

| الكلاس | الأسطر قبل هذه الدفعة | الأسطر بعدها |
| --- | ---: | ---: |
| `SpecialSalesTestData` | 1,017 | 763 |
| `StoreTestData` | 503 | 466 |

انتقل كذلك 295 سطرًا من محاكاة API خارج كلاسي اختبارات التنظيف. المقارنة العكسية للملفات الفعلية أكدت مطابقة المالكين وأجسام الاتصال والتعريفات والمحاكاة للمصدر السابق، باستثناء التفويض وإتاحة الاستدعاء داخل الحزمة. بقيت أجسام حالات التنظيف كما هي، مع تبديل اسم نوع المحاكاة وإزالة imports غير المستخدمة فقط.

### إعادة تشغيل التحقق

التحقق المحلي المستقل عن ERP:

```powershell
mvn -Pci verify
```

التحقق الحي المحدد من أربع صفحات، باستخدام بيانات تسجيل الدخول المحلية المعتادة:

```powershell
mvn --batch-mode --no-transfer-progress '-DExecutionType=LocalHeadless' '-Dallure.report.generate=false' '-Dallure.results.directory=target/two-modules-live/allure-results' '-Dallure.report.directory=target/two-modules-live/allure-report' '-Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/fixture-refactor-smoke-testng.xml' test
```

يختار الملف ثماني حالات: حفظ إذن صرف ورفض إذن بلا أصناف، حفظ تصنيع وإلزامية المخزن، إعدادات نقطة البيع وحفظ فاتورة نقدية، وإعدادات المطعم وتعليق طلب مع التحقق من خصم المخزون. تستخدم الحالات بيانات مملوكة وتنظيف الكلاس الحالي، ولا تدخل ضمن CI المحلي.

### معالجة تحديث حقل اختيار المخزن

كشف التحقق الحي `ElementNotInteractableException` في `StoreModulePage.smChoose`: نجح فحص ظهور الحقل ثم رفض الكتابة بعد فتح القائمة. أصبح الاختيار يعيد اكتساب الحقل في دورة الانتظار التالية عند هذه الحالة، تحت مهلة الانتظار السابقة نفسها. تظل المطابقة الدقيقة وفحص الخيار الملتبس كما هما؛ لا توجد إعادة محاولة لحفظ المستند أو تغيير بياناته.

أضيف `StoreChoiceTest` إلى CI المحلي لمحاكاة تعذر التفاعل أثناء الفتح وأثناء الكتابة، والتحقق من إعادة اكتساب الحقل واختيار عنصر واحد، مع استمرار رفض الخيارات الملتبسة. هذه الحالات مستقلة عن المتصفح الحقيقي وERP.

### نتائج التحقق المحلي والتشغيل الأول

نجح `mvn -Pci verify` بعد الإصلاح في **288 حالة، دون فشل أو تخطٍ**، بما فيها الحالات الثلاث الجديدة. نجح التشغيل الحي الأول في **7 من 8 حالات**؛ فشل حفظ التصنيع عند التفاعل مع القائمة المذكورة، ثم اكتمل تنظيف الكلاسات الأربعة وأصبحت سجلات البيانات والمحاولات الثمانية الخاصة بالتشغيل فارغة. هذا الفشل محفوظ ضمن الأدلة، ولا يُحسب التشغيل الأول نجاحًا كاملًا.

الأدلة المحلية: `target/two-modules-ci-with-choice-fix.log` و`target/two-modules-live.log` و`target/two-modules-refactoring-baseline/final-verification.json` و`target/two-modules-refactoring-baseline/initial-live-testng-results.xml` و`target/two-modules-refactoring-baseline/initial-live-journal-check.json`.

### إعادة المخازن بعد الإصلاح

أُعيدت الحالات الأربع للصرف والتصنيع بعد تعديل انتظار القائمة، ونجحت **4 من 4 دون فشل أو تخطٍ**، بما فيها حفظ التصنيع الذي فشل أولًا. اكتمل إعداد الكلاسين وتنظيفهما وأصبحت سجلات البيانات والمحاولات الأربعة الخاصة بالإعادة فارغة. مع نجاح الحالات الأربع لنقطة البيع والمطعم في التشغيل الأول، أصبحت الحالات الحية الثماني المختارة ناجحة عبر التشغيلين؛ لم يُعَد التشغيل الأول كاملًا، ولم يُشغّل Regression الحي بالكامل.

أدلة الإعادة: `target/two-modules-store-recheck.log` و`target/two-modules-refactoring-baseline/store-recheck-testng-results.xml` و`target/two-modules-refactoring-baseline/store-recheck-journal-check.json`. ظلت السجلات التاريخية خارج هذين التشغيلين محفوظة.

## الدفعة الخامسة: الأصناف والمتعاملون والحركات المالية

تغطي الدفعة ثلاث وحدات معًا، وتضيف تسعة ملفات لفصل تعريفات البيانات واتصال المتصفح ومحاكاة API المحلية:

| الوحدة | تعريفات البيانات | الاتصال | محاكاة اختبارات التنظيف |
| --- | --- | --- | --- |
| الأصناف | `ProductsFixtureDefinitions`، سبع دوال | `ProductsFixtureApi` | `ProductsFixtureApiStub` |
| المتعاملون | `DealerFixtureDefinitions`، عشر دوال | `DealerFixtureApi` | `DealerFixtureApiStub` |
| الحركات المالية | `FinancialFixtureDefinitions`، اثنتا عشرة دالة تشمل الفاتورة المساعدة | `FinancialFixtureApi` | `FinancialFixtureApiStub` |

أصبح لكل وحدة من الوحدات السبع التي شملها التنظيم مالك لدورة حياة البيانات، وتعريفات منفصلة، واتصال مستقل، ومحاكاة مستقلة. لا تعني هذه البنية توحيد قواعد الوحدات المختلفة: مثلًا يحتفظ اتصال المتعاملين بمعالجة أخطائه السابقة، وتحتفظ المشتريات بسياسة deadlock الخاصة بها، والمبيعات الخاصة باستعادة إعدادات الفرع.

بقيت حالة السيناريو، ومفاتيح المشاركة والعزل، والملكية والسجلات و`PendingFixtureWrites` والاستعادة وترتيب الحذف داخل كل مالك. تستدعي وحدات الدفعة الخامسة `FixtureSession.verify` قبل كل اتصال كما كانت. وصفة الفاتورة المالية تتلقى معرّف التشغيل السابق صراحةً من المالك، وتستمر في التسجيل عبر `fixture` نفسه. أصبحت دالة تسجيل المتعاملين متاحة داخل الحزمة لتمكين هذا التفويض فقط.

| المالك | الأسطر قبل هذه الدفعة | الأسطر بعدها |
| --- | ---: | ---: |
| `ProductsTestData` | 359 | 320 |
| `DealerTestData` | 305 | 247 |
| `FinancialTestData` | 385 | 317 |

انتقل كذلك **385 سطرًا** من محاكاة API خارج كلاسات اختبارات التنظيف الثلاثة. أثبتت المقارنة العكسية للملفات الفعلية تطابق بقية كود المالكين وأجسام الاتصال والمحاكاة وتعريفات البيانات مع النسخ السابقة، باستثناء التفويض وصلاحية الحزمة ومعامل معرّف التشغيل. لم تتغير أجسام حالات التنظيف أو توقعاتها، باستثناء اسم نوع المحاكاة وimports غير المستخدمة.

### إعادة تشغيل فحص الدفعة الخامسة

```powershell
mvn -Pci verify
mvn --batch-mode --no-transfer-progress '-DExecutionType=LocalHeadless' '-Dallure.report.generate=false' '-Dallure.results.directory=target/three-modules-live/allure-results' '-Dallure.report.directory=target/three-modules-live/allure-report' '-Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/fixture-data-refactor-smoke-testng.xml' test
```

الملف الحي يختار عشر حالات عبر ستة كلاسات: إنشاء وحدة وإلزامية الاسم، إنشاء صنف بوحدة فرعية وإلزامية الفئة، البيانات المترابطة للمتعاملين، حفظ عميل وإغلاق نموذج دون حفظ، ظهور بيانات الحركات المالية في قوائمها، وسند قبض بتوزيع على فاتورة ورفض المبلغ الصفري. يتضمن الإعداد والتنظيف الحاليين، ويظل منفصلًا عن CI المحلي.

نجحت **288 حالة محلية دون فشل أو تخطٍ**. الأدلة: `target/three-modules-ci-final.log` و`target/three-modules-refactoring-baseline/final-verification.json`. نتائج التشغيل الحي وسجلات تنظيفه تسجّل بعد اكتماله.

### توسعة الدفعة: مكوّن تقويم المشتريات

انتقلت أربع دوال للتقويم من `PurchaseModulePage` إلى `PurchaseCalendar`، ليصبح حجم كلاس الواجهة 1,056 سطرًا بدلًا من 1,192. يحتفظ المدخل العام `puDateFilter` بانتظار جاهزية القائمة والتحقق من القيمة المختارة بعد التغيير؛ يتولى المكوّن فتح التقويم والتنقل بين الأشهر واختيار اليوم. أثبتت المقارنة مطابقة أجسام التفاعل الثلاثة السابقة، بما فيها JavaScript والمهل، ومطابقة بقية كلاس الواجهة باستثناء إنشاء المكوّن والتفويض له.

كشفت مراجعة قارئ الشهر أن تقطيع الاسم الإنجليزي إلى ثلاثة أحرف ثم تمريره إلى `Month.valueOf` لا يعمل مع معظم الأشهر؛ enum يتوقع الاسم الكامل مثل `OCTOBER`. أضيفت اختبارات محلية لجميع الأشهر بأسماء عربية وإنجليزية كاملة ومختصرة وصيغة رقمية، ولرفض عناوين غير معروفة بوضوح. التحقق الحي الإضافي في `purchase-calendar-smoke-testng.xml` يغطي نطاق المورد وتغيير التاريخ ومسح النتائج القديمة، مع تجهيز البيانات وتنظيفها.

نجح التشغيل الحي للوحدات الثلاث في **10 من 10 حالات**، دون فشل أو تخطٍ، مع اكتمال إعداد وتنظيف الكلاسات الستة. أصبحت سجلات التشغيل الثلاثة عشر الخاصة بالبيانات والمحاولات وسجلات صفحة العميل فارغة. الأدلة في `target/three-modules-live.log` و`target/three-modules-refactoring-baseline/live-testng-results.xml` و`target/three-modules-refactoring-baseline/live-journal-check.json`.

أثبت تشغيل الاختبارات قبل إصلاح قارئ الشهر وجود **25 حالة فشل**؛ وبعد الإصلاح نجحت **339 حالة محلية دون فشل أو تخطٍ**، منها 51 حالة قراءة عنوان التقويم. الأدلة في `target/purchase-calendar-before-fix-ci.log` و`target/purchase-calendar-fixed-ci.log` و`target/three-modules-refactoring-baseline/calendar-verification.json`. يحتفظ التفاعل مع التقويم بمهله وJavaScript السابقين؛ التغيير السلوكي المحدد هو دعم قراءة الشهر الإنجليزي ورسالة رفض العنوان غير المعروف.

نجح فحص قائمة المشتريات الحي في **حالتين دون فشل أو تخطٍ**، واكتمل تجهيز الكلاس وتنظيفه وأصبح سجلا البيانات والمحاولات فارغين. بذلك نجحت 12 حالة حية في هذه الدفعة عبر التشغيلين المحددين، وليس Regression كاملًا. الأدلة في `target/purchase-calendar-live.log` و`target/three-modules-refactoring-baseline/calendar-live-testng-results.xml` و`target/three-modules-refactoring-baseline/calendar-live-journal-check.json`.

لإعادة فحص التقويم الحي:

```powershell
mvn --batch-mode --no-transfer-progress '-DExecutionType=LocalHeadless' '-Dallure.report.generate=false' '-Dallure.results.directory=target/purchase-calendar-live/allure-results' '-Dallure.report.directory=target/purchase-calendar-live/allure-report' '-Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/purchase-calendar-smoke-testng.xml' test
```

## الدفعة السادسة: قوائم المبيعات والمشتريات

انتقلت 11 دالة إلى `SalesDocumentList` و15 دالة إلى `PurchaseDocumentList`: نطاق العميل/المورد، جاهزية الجدول، تطبيق الفلاتر، تاريخ البيانات المساعدة، قراءة الصفوف والتحقق من هوياتها، البحث وخيارات التصفية ورؤوس الجدول والتصدير، مع الترتيب وحجم الصفحة والتنقل في المشتريات. ظلت الواجهات العامة داخل الصفحات الحالية، وظلت عمليات النموذج والتقويم داخل مكوناتها السابقة.

| الكلاس | الأسطر قبل | الأسطر بعد |
| --- | ---: | ---: |
| `SalesModulePage` | 878 | 823 |
| `PurchaseModulePage` | 1,056 | 956 |

يستخدم كل مكوّن المتصفح وكائن الانتظار نفسيهما للصفحة المالكة؛ لا ينشئ جلسة أو مهلة جديدة. بقي اختلاف أسماء حقول الفلاتر ومسارات API بين المبيعات والمشتريات كما كان. أصبحت نسخة `Type` ذات معامل blur متاحة داخل الحزمة للتفويض، وبقيت واجهة الاستدعاء العامة كما هي. أثبتت المقارنة تطابق أجسام الدوال بعد استثناء تفويض عمليات الصفحة، وتطابق بقية ملفي الصفحات.

نجحت **339 حالة محلية** دون فشل أو تخطٍ عبر `mvn -Pci verify`. الأدلة المحلية: `target/document-lists-ci.log` و`target/document-lists-baseline/verification.json`.

ملف `additional/standalone/document-list-refactor-smoke-testng.xml` يختار أربع حالات حية: نطاق العميل والكود المطابق في المبيعات، ونطاق المورد والكود المطابق في المشتريات. يستخدم تجهيز الكلاسات وتنظيفها الحاليين، ويظل خارج CI المحلي.

نجحت **الحالات الحية الأربع دون فشل أو تخطٍ**، واكتمل تنظيف الكلاسين وأصبحت سجلات البيانات والمحاولات الأربعة الخاصة بالتشغيل فارغة. الأدلة في `target/document-lists-live.log` و`target/document-lists-baseline/live-testng-results.xml` و`target/document-lists-baseline/live-journal-check.json`. هذا فحص محدد للقوائم، ولا يغطي كل حالات التصدير والترتيب أو Regression الحي بالكامل.

## الدفعة السابعة: تفاعلات النماذج المشتركة

تتجاوز هذه الدفعة النقل داخل الوحدة: تستخدم صفحات المبيعات والمشتريات والمخازن مكوّن `Plugin_ERP.Utiel.FormInteractions` للاختيار والكتابة والضغط. يحتفظ كل مدخل في الصفحة باسمه وAllure Step، ويستخدم المكوّن كائن انتظار الصفحة وطريقة التمرير الحالية دون إنشاء متصفح أو انتظار بديل.

تم الحفاظ على اختلافين صريحين: المبيعات والمشتريات تستبدلان القيمة بعد الضغط على الحقل وتقبلان اسم الخيار كجزء مستقل بين فواصل ` - `؛ المخازن تمسح القيمة أولًا وتقبل الاسم الكامل أو الموجود بطرف الخيار. بقي XPath كل صفحة كما هو، وبقي رفض الخيارات الملتبسة. لم تتغير عمليات الحفظ أو الاستعادة أو التنظيف.

أصبحت إعادة اكتساب حقل الاختيار عند `ElementNotInteractableException` متاحة للمبيعات والمشتريات، كما كانت في المخازن. إعادة المحاولة داخل مهلة الصفحة نفسها، عند فتح/كتابة حقل البحث فقط؛ لا تعيد حفظ المستند ولا تتجاهل خطأ الخيار الملتبس.

| الصفحة | الأسطر قبل هذه الدفعة | الأسطر بعد |
| --- | ---: | ---: |
| `SalesModulePage` | 823 | 766 |
| `PurchaseModulePage` | 956 | 899 |
| `StoreModulePage` | 583 | 521 |

أضيفت 25 حالة محلية: عشر لمطابقة الخيارات حسب سياسة الوحدة، وأربع لطريقة الاستبدال والمسح وblur، وحالتان لتجاوز الحقول المخفية والمعطلة، وتسع للتفاعل مع الصفحات الثلاث عند تحديث القائمة ورفض الخيارات الملتبسة. نجحت **364 حالة محلية دون فشل أو تخطٍ** عبر `mvn -Pci verify`. لا تحتاج هذه الحالات إلى ERP أو متصفح حقيقي.

أكدت مقارنة المصدر بقاء بقية كود الصفحات وXPath الاختيار دون تغيير، مع الحفاظ على سياسة المطابقة والكتابة والانتظار والتمرير. الأدلة: `target/form-interactions-ci.log` و`target/form-interactions-baseline/verification.json`.

فحص حي محدد عبر `additional/standalone/form-interactions-smoke-testng.xml` يختار ست حالات: حفظ فاتورة مبيعات ومشتريات من قائمة الأصناف ورفض المستند الفارغ، وحفظ التصنيع وإلزامية المخزن. يستخدم دورة تجهيز وتنظيف الكلاسات الحالية، ويظل خارج CI المحلي.

نجحت **الحالات الحية الست دون فشل أو تخطٍ**، مع اكتمال تجهيز وتنظيف الكلاسات الثلاثة. أصبحت سجلات البيانات والمحاولات الستة الخاصة بالتشغيل فارغة. الأدلة في `target/form-interactions-live.log` و`target/form-interactions-baseline/live-testng-results.xml` و`target/form-interactions-baseline/live-journal-check.json`. هذا تحقق محدد للتفاعلات المشتركة، وليس تشغيل Regression الحي بالكامل.

لإعادة التحقق الحي:

```powershell
mvn --batch-mode --no-transfer-progress '-DExecutionType=LocalHeadless' '-Dallure.report.generate=false' '-Dallure.results.directory=target/form-interactions-live/allure-results' '-Dallure.report.directory=target/form-interactions-live/allure-report' '-Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/form-interactions-smoke-testng.xml' test
```

## الدفعة الثامنة: توسيع التفاعلات إلى الأصناف والحركات المالية

أصبح الاختيار المشترك مستخدمًا في خمس وحدات. أضيفت سياسة `EXACT` للأصناف، وسياسة `EXACT_THEN_CONTAINS` للحركات المالية: تبحث الثانية عن التطابق الكامل أولًا، ثم تستخدم الاحتواء فقط إن لم توجد مطابقة كاملة. الخيارات المتشابهة لا تُفضّل على خيار مطابق، والتباس المطابقة الكاملة أو الاحتواء يوقف الاختيار. بقيت رسائل الالتباس الخاصة بالوحدتين كما كانت.

تستخدم الأصناف المكوّن نفسه للكتابة والضغط مع طريقة المسح السابقة وblur. بقيت مواضع العناصر وAllure Steps ومهل الانتظار والتمرير الخاصة بكل وحدة. بقيت كتابة المبالغ المالية والتحقق الرقمي منها كما هما، وكذلك جمع أدلة قوائم الحركات المالية عند انتهاء المهلة. أضيفت إعادة اكتساب حقل الاختيار عند تحديثه للوحدتين تحت مهلة الصفحة الحالية؛ لا تشمل إعادة حفظ المستند.

أضيفت تسع حالات لسياسات المطابقة وأولوية التطابق الكامل والتباس fallback، وست حالات لاستخدام المكوّن من صفحتي الأصناف والحركات المالية أثناء تحديث القائمة ورفض الاختيار الملتبس. نجحت **379 حالة محلية دون فشل أو تخطٍ**. المقارنة العكسية أكدت بقاء بقية الصفحتين دون تغيير، بما فيها تشخيص القوائم المالية وكتابة المبالغ. الأدلة: `target/extended-forms-ci-final.log` و`target/extended-forms-baseline/verification.json`.

الفحص الحي `additional/standalone/products-finance-interactions-smoke-testng.xml` يختار أربع حالات: إنشاء صنف بوحدة فرعية وإلزامية الفئة، وسند قبض بتوزيع على فاتورة ورفض المبلغ الصفري. يشمل تجهيز الكلاسين وتنظيفهما، ويظل مستقلًا عن CI المحلي.

نجحت **الحالات الحية الأربع دون فشل أو تخطٍ** مع اكتمال تجهيز وتنظيف الكلاسين. أصبحت سجلات البيانات والمحاولات الأربعة الخاصة بالتشغيل فارغة، وظلت السجلات التاريخية محفوظة. الأدلة: `target/extended-forms-live.log` و`target/extended-forms-baseline/live-testng-results.xml` و`target/extended-forms-baseline/live-journal-check.json`. لم يُشغّل Regression الحي بالكامل.

```powershell
mvn --batch-mode --no-transfer-progress '-DExecutionType=LocalHeadless' '-Dallure.report.generate=false' '-Dallure.results.directory=target/extended-forms-live/allure-results' '-Dallure.report.directory=target/extended-forms-live/allure-report' '-Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/products-finance-interactions-smoke-testng.xml' test
```

## إغلاق التنظيم والتحقق المجمع

أُضيفت مداخل الفحص الجديدة ومدخل `acceptance-testng.xml` إلى الاختبار المحلي لصحة ملفات TestNG، وصُحح الوصف القديم لمشاركة المتصفح في Regression. يوفر `scripts/Test-Project.ps1` تشغيلًا محليًا أو محليًا ثم قبولًا أو Regression، مع حفظ الأدلة وفشل الطرفية عند فشل مرحلة، ودون تنظيف سجلات الاستعادة.

نجح التحقق المجمع في **386 حالة محلية و11 حالة قبول حية** دون فشل أو تخطٍ، مع اكتمال تنظيف البيانات وخلو 18 سجلًا يخص التشغيل. الحالة الختامية وحدود النطاق والعيوب الخارجية موثقة في [FINAL_REVIEW_AR.md](FINAL_REVIEW_AR.md). لا يتطلب إغلاق التنظيم تقسيم كل كلاس كبير أو إخفاء عيوب ERP المعروفة.
