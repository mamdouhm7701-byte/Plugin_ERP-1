# شرح الملف: يجمع ملفات شرح المشروع في دليل للقراءة.
"""Build a source-backed Arabic file atlas and an offline HTML study guide.

Only documentation is written. Credentials, generated run folders and private
configuration values are deliberately not copied into the guide.
HTML rendering optionally uses Python-Markdown (also found in target/documentation-tools).
"""
from __future__ import annotations
import argparse
import collections
import hashlib
import html
import json
import os
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
GUIDE = ROOT / 'docs/learning'
ATLAS = GUIDE / 'atlas'
LITERAL = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*[\s\S]*?\*/')
DECLARATION = re.compile(r'(?m)^[ \t]*(?P<access>public|protected|private)?[ \t]*(?:static[ \t]+)?(?:final[ \t]+)?(?P<type>[\w.$<>?,\[\]]+(?:[ \t]+[\w.$<>?,\[\]]+)*)[ \t]+(?P<name>[A-Za-z_$][\w$]*)\((?P<params>[^{};]*?)\)[ \t]*(?:throws[ \t]+[^{};]+)?\{')
DATE = '2026-10-24'

EXACT = {
 'pom':'مواصفات بناء Maven: إصدار Java والمكتبات والمكونات الإضافية وprofile الفحوص المحلية.',
 'Abstract':'واجهة الصفحة العامة: تجمع سياق الجلسة وتفوض الحقول والتنقل والسجلات والطباعة لأدوات منفصلة.',
 'DocumentPage':'منسق القدرات المشتركة للمستند: البنود والقراءة والخصم والتحقق والطباعة؛ لا يرث من مجال المتعاملين.',
 'AbstractForDealer':'منسق المتعاملين: حقول مشتركة مع مكونات للفورم وقراءة القائمة وإجراءات السجل.',
 'AbstractForSales':'تخصص المبيعات: قراءة بنودها وإجمالياتها وفورمها وطباعة أرقامها، فوق DocumentPage.',
 'AbstractForStore':'اسم توافق رفيع لصفحات المستندات المخزنية فوق DocumentPage؛ ليس مالك بيانات الاختبار.',
 'AbstractForPurchases':'اسم توافق رفيع لصفحات المشتريات فوق DocumentPage؛ ليس أبًا للمبيعات حاليًا.',
 'AbstractForBills':'اسم توافق رفيع لقدرات المستندات المشتركة.',
 'PageContext':'حاوية اعتمادات جلسة صفحة واحدة: المتصفح وأدوات العناصر والانتظار والتبويبات والإجراءات.',
 'BaseTest':'تجهيز المتصفح والدخول قبل كلاس الاختبارات، وحماية الجلسة، ثم إغلاق المتصفح المملوك.',
 'WebDriverFactory':'مصنع المتصفح: يختار النوع ويحتفظ بمتصفح الخيط ويمنع بدء آخر فوق جلسة نشطة.',
 'AbstractDriver':'عقد إنشاء المتصفح وقراءة عنوان Selenium Grid عند الاستخدام البعيد.',
 'Chrome':'خيارات Chrome وإنشاء متصفحه محليًا أو بدون نافذة أو عبر Selenium Grid حسب الإعداد.',
 'Edge':'خيارات Edge وإنشاء متصفحه حسب إعداد طريقة التنفيذ.',
 'Firefox':'مصنع Firefox بالاسم الإملائي الحالي في المشروع؛ راجع التنفيذ لمعرفة طرق التشغيل المدعومة.',
 'Browser':'تعداد أنواع المتصفح وربط النوع بمصنعه.',
 'Pages':'دليل مسارات شاشات ERP المسماة في الكود؛ وجود route لا يثبت وجود تغطية كاملة له.',
 'TestNGListeners':'يراقب دورة التنفيذ ونتيجة الحالة، يجمع الأدلة، ويطبق سياسة بناء التقرير وإرساله.',
 'PropertyReader':'واجهة طلب الإعدادات واختيار رابط البيئة وبيانات الدخول وسياسة الحسابات.',
 'ConfigurationProperties':'قراءة ملفات الإعداد ودمج الملف المحلي ومتغيرات الدخول وخصائص JVM بأولوية محددة.',
 'FormInteractions':'سياسات مشتركة للضغط والكتابة واختيار قوائم مع مطابقة وتحقق واضحين.',
 'NativeCheckbox':'التعامل مع ضبط Checkbox مع مراجعة الحالة الفعلية.',
 'DocumentDates':'توحيد التعامل مع تواريخ المستندات المستخدمة في السيناريوهات.',
 'PrintedInvoiceAssertions':'مقارنة محتوى طباعة الفاتورة وبنودها وأرقامها بالقيم المطلوبة في السيناريو.',
 'ScreenShotsUtils':'التقاط صور دليل من جلسة المتصفح مع أسماء ومواقع حفظ.',
 'OSUtils':'تمييز نظام التشغيل للمسارات والأوامر الخاصة به.',
 'TimeManage':'توليد وقت نصي لأسماء الأدلة أو علامة زمنية مختصرة.',
 'FileUtils':'مساعدات قديمة لإنشاء مجلد وإعادة تسمية ملف؛ ليست مالك fixture journals.',
 'LoginjsonReader':'قراءة ملف JSON واستخراج قيمة بمسار JSONPath في الاستخدامات القديمة.',
 'VideoConverterUtils':'مساعد تحويل فيديو مولد إلى صيغة أخرى وفق أدواته المتاحة؛ ليس دليل تفعيل تسجيل كل حالة.',
 'InvoiceCalculator':'حساب إجماليات الفاتورة والخصم والضريبة الشاملة وغير الشاملة والتقريب بشكل مستقل عن الشاشة.',
 'InvoiceRow':'نموذج بند حسابي: اسم وكمية وسعر وقيمة بند.',
 'InvoiceTotals':'نموذج الإجماليات المتوقع حسابيًا: الإجمالي والخصم وقبل الضريبة والضريبة والصافي.',
 'ReportTotals':'نموذج قيم الإجماليات المقروءة من التقرير المطبوع.',
 'DiscountType':'نوع الخصم: قيمة أم نسبة؛ ليس رقم الخصم نفسه.',
 'TaxMode':'هل الأسعار شاملة الضريبة أم غير شاملة في المسار الحسابي المعني.',
 'BaseAction':'قاعدة أدوات Selenium التي تتلقى المتصفح والانتظار وتقدم scroll وJS click ومساعدًا للنقر خارج الحقل.',
 'ElementActions':'واجهة أدوات قديمة تنشئ مساعداتها وتقدم قراءة وضغطًا وكتابة وعمليات صفوف؛ ما زالت واسعة المسؤولية.',
 'DropdownActions':'اختيار قيم من قوائم Vue وواجهات قوائم قديمة مع سياسات إعادة محاولة.',
 'CheckboxActions':'عدة استراتيجيات لقراءة وضبط ومراجعة Checkbox بحسب شكل الشاشة.',
 'GridActions':'قراءة صفوف وجداول وتعديلها وحذفها؛ تميز بين حذف مملوك وحذف عام قديم عند الاستخدام.',
 'WaitActions':'انتظار روابط وعناصر وجداول وتقارير وحالات شاشة.',
 'WaitManagement':'بناء FluentWait من مهلة الإعداد وقائمة الاستثناءات المؤقتة.',
 'BrowserAction':'مساعدات تنقل وتبويبات وإعادة محاولة مرتبطة بالمتصفح.',
 'ValidationActions':'قراءة وتطبيع ومطابقة نصوص وأرقام وتواريخ ظاهرة.',
 'PriceListActions':'عمليات قديمة خاصة بمستخدمي/أصناف وأسعار قائمة أسعار، رغم وجودها داخل أدوات عامة.',
 'UtilityActions':'تواريخ وتشخيص قيم وعناصر ومساعدات متنوعة.',
 'AlertAction':'التعامل مع رسائل وتنبيهات وتأكيدات بحسب نوعها داخل المتصفح.',
 'LogUtils':'كتابة رسائل السجل بمستويات مختلفة؛ كتابة INFO ليست assertion.',
 'FixtureSession':'يحفظ عنوان خدمة ERP والفرع ويتحقق من نفس السياق قبل التعامل مع بيانات التجربة.',
 'FixtureJournals':'حفظ وقراءة سجلات ملكية بيانات الاختبار محليًا.',
 'PendingFixtureWrites':'تسجيل محاولة إنشاء قبل إرسالها ومعالجة التزام خادم ضاع رده.',
 'WorkflowCleanupOrder':'تحديد ترتيب تنظيف كيانات رحلة مركبة وفق اعتمادها على بعضها.',
 'WorkflowEntity':'وصف كيان يشارك في ترتيب تنظيف رحلة عمل.',
 'FixtureBrowser':'أداة دعم لاختبارات fixture المحلية لمحاكاة ما يلزم من جلسة المتصفح.',
 'SalesTaxProfile':'قراءة وتفسير سياسة ضريبة المبيعات لحساب المتوقع المناسب لسياقها.',
 'TreasuryAmounts':'تمثيل/مراجعة مبالغ تخص سيناريوهات تسليم الخزينة.',
 'TreasuryFixtures':'تجهيز بيانات تدفقات الخزينة المملوكة للاختبارات.',
 'BaseAssertion':'واجهة تحقق قديمة تربط قراءة العناصر باستدعاء hard أو soft assertion.',
 'Validation':'تنفيذ soft assertions قديم مرتبط بالخيط ويجمع الفشل عند AsserAll مع تنظيف حالته.',
 'ScreenPage':'عناصر UI عامة للشاشات الموسعة: فتح وجاهزية وحقول وقراءات؛ ليست حالة اختبار في حد ذاتها.',
 'AllureConstants':'مسارات ومواقع نتائج التقرير وتاريخه المستخدمة في خدمات Allure.',
 'AllureBinaryManger':'تجهيز بيانات نتائج Allure وأداة بنائه وحفظ التاريخ حسب المسارات الحالية.',
 'AllureReportGenerator':'تنسيق بناء تقرير Allure من النتائج الموجودة.',
 'AllureReportPolicy':'تحديد هل يولد HTML ويفتح تلقائيًا؛ النتائج الخام سياسة منفصلة عن الفتح.',
 'AllureEnvironment':'كتابة معلومات بيئة التشغيل في التقرير مع تحديث بيانات المتصفح.',
 'AllureAttachment':'تجميع/إضافة مرفقات التقرير حسب الواجهة الموجودة.',
 'AllureUtils':'مساعدات عامة مرتبطة بـAllure في الاستدعاءات الحالية.',
 'ReportMail':'قراءة سياسة البريد وبناء وإرسال التقرير؛ لا يحدد نجاح حالة ERP.',
 'ReportMailCommand':'مدخل أمر إرسال التقارير بصورة مستقلة وفق إعداداته.',
 'ReportMailTemplate':'شكل ملخص رسالة نتائج الاختبار.',
 'ReportDelivery':'توزيع ملفات التقرير بين المرفقات وروابط Drive وفق الحجم المتاح.',
 'DriveReportStore':'رفع وحفظ ملف تقرير على Drive باستخدام إعدادات الخدمة المصرح بها.',
 'ScenarioAccess':'التقاط سياق وصول أو شاشة لإضافته كدليل في التقرير.',
 'ScenarioAssertions':'إدارة سياق رحلة التحقق والأدلة أثناء الحالة.',
 'ScenarioSoftAssert':'SoftAssert يدعم تسجيل تفاصيل المقارنات في تقرير السيناريو.',
 'ScreenNames':'أسماء مقروءة للشاشات عند فتح مساراتها داخل الأدلة.',
 'InvoiceBusinessValidation':'الحساب والمقارنة وإرفاق المتوقع والفعلـي عبر قراءات المستند، مع الحفاظ على override.',
 'PurchaseCalendar':'اختيار التاريخ في تقويم المشتريات وقراءة أسماء الشهور العربية والإنجليزية والرقمية.',
 'InvoiceListCalendar':'تاريخ فلاتر قائمة الفواتير وتقويمها، بما فيه قراءة الشهر.',
 'InvoiceListFilters':'حقول وخيارات وقيم فلاتر قائمة الفواتير وخط أساس الجدول قبل التطبيق.',
 'InvoiceListGrid':'قراءة الصفوف والأعمدة والفرز وانتظار استقرار تحديث الجدول.',
 'InvoiceListPagination':'حجم الصفحة والتنقل وأرقام الصفحات في قائمة الفواتير.',
 'InvoiceListExport':'تحديد زر التصدير المرئي المناسب للصيغة؛ لا يثبت محتوى الملف بمفرده.',
 'InvoicListPage':'واجهة قائمة الفواتير التي تنسق الفلاتر والجدول والتقويم والتنقل والتصدير.',
 'GridProviderWait':'انتظار جاهزية مزود بيانات جدول Vue لنطاق الخزن أو المناطق قبل قراءة DOM.',
 'MainBage':'صفحة/مساعد قديم مرتبط بالقائمة الرئيسية بحسب ميثوداته الحالية؛ الاسم كما هو في المصدر.',
 'LoginPage':'حقول وإجراءات الدخول والتحقق من الانتقال بعده.',
}

METHODS = {
 'context':'إرجاع اعتمادات نفس جلسة الصفحة، لا إنشاء متصفح جديد.',
 'setup':'تجهيز المتصفح والدخول قبل اختبارات الكلاس.', 'tearDown':'إغلاق المتصفح المملوك وتنظيف مراجع الجلسة.',
 'requirePreparedSession':'منع متابعة السيناريو إذا فشل التجهيز أو لا توجد جلسة جاهزة.',
 'prepare':'تجهيز سياق الحالة وبياناتها واختيارات المستند.', 'save':'تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف.',
 'beginScenario':'بدء نطاق بيانات مستقل للحالة الحالية.', 'close':'إنهاء نطاق/مورد وتنظيفه حسب مالكه؛ اقرأ سياق الكلاس.',
 'verify':'مراجعة سياق أو عقد حسب الكلاس، مثل ثبات الفرع والعنوان.',
 'start':'تسجيل بداية محاولة أو إجراء.', 'committed':'إزالة محاولة معلقة بعد التأكد من تسجيلها.',
 'recover':'البحث عن آثار محاولة سابقة ومتابعة ملكيتها بأمان.', 'resume':'إعادة تحميل حالة تشغيل محفوظة للتحقق أو الاسترجاع.',
 'calculate':'احتساب القيم من البنود والسياسة المعطاة أو الإعدادات.',
 'calculateExclusive':'حساب حالة الأسعار غير الشاملة للضريبة.', 'calculateInclusive':'حساب حالة الأسعار الشاملة للضريبة.',
 'getInvoiceRows':'تحويل صفوف بنود الشاشة إلى نماذج قابلة للحساب.',
 'getSummaryValue':'قراءة قيمة إجمالية باسمها؛ قد يعاد تعريفها في التخصص.',
 'getReportTotals':'تجميع قيم الطباعة المقروءة في نموذج إجماليات.',
 'validateBusinessTotals':'حساب المتوقع ومقارنته بقيم المستند وإرفاق الدليل.',
 'assertInvoiceTotals':'مقارنة إجماليات الشاشة بالإجماليات المتوقعة.',
 'assertReportTotals':'مقارنة الطباعة بالمتوقع مع مراعاة نوع الضريبة.',
 'validateReport':'قراءة ومقارنة أرقام التقرير ثم تنسيق الرجوع حسب المسار.',
 'NavigationHelper':'فتح شاشة مسماة مع انتظارها وفق تنفيذ الصفحة.',
 'SelectFromDropDawn':'اختيار قيمة من قائمة بواسطة الحقل ونص الاختيار.',
 'typeText':'كتابة قديمة بمحاولات تحقق وتعامل مع عناصر متغيرة.',
 'typeTextAndVerify':'كتابة قديمة موسعة مع تحقق من القيمة وبدائل معالجة.',
 'ClickElement':'انتظار عنصر ظاهر ومفعل ثم الضغط عليه بالطريقة المحددة.',
 'ClickNew':'فتح نموذج سجل جديد.', 'ClickSaveButton':'الضغط على حفظ مع انتظار/معالجة الصفحة بحسب التنفيذ.',
 'ClickCloseButton':'إغلاق نموذج مع مراعاة حالة الشاشة.',
 'ClickSaveAndPrintButton':'الحفظ والطباعة ومتابعة تبويب التقرير.',
 'DeleteAll':'حذف عام قديم؛ لا تساويه بتنظيف السجلات المملوكة.',
 'Delete':'حذف وفق عقد هذا الملف؛ راجع هل يحدد سجلًا بعينه أم آخر صف.',
 'Edit':'فتح تعديل وفق عقد الملف؛ بعض المسارات القديمة تختار آخر سجل.',
 'AsserAll':'إظهار فشل soft assertions المجمعة ثم إزالة حالتها.',
 'parseMonth':'تفسير الشهر من عنوان التقويم.',
 'enabled':'قراءة هل خدمة/ميزة التقرير مفعلة من إعداداتها.',
 'generateReport':'المشاركة في بناء التقرير وفق callback TestNG وإعدادات التوصيل.',
 'onExecutionStart':'مهام بداية التنفيذ، ومنها إعداد نتائج التقرير ومعلوماته.',
 'onExecutionFinish':'مهام النهاية وفق سياسة التقرير وإغلاق ما تبقى من المتصفح.',
 'onTestSuccess':'تجميع أدلة نتيجة نجاح الحالة.', 'onTestFailure':'تجميع أدلة الفشل من الجلسة الحالية.',
 'onTestSkipped':'تجميع أدلة التخطي إن توفرت الجلسة.',
}

WORDS = {
 'sales':'المبيعات','purchase':'المشتريات','purchases':'المشتريات','store':'المخزن','warehouse':'المخازن',
 'invoice':'الفاتورة','document':'المستند','line':'البند','lines':'البنود','quantity':'الكمية','price':'السعر',
 'discount':'الخصم','tax':'الضريبة','net':'الصافي','total':'الإجمالي','totals':'الإجماليات',
 'customer':'العميل','supplier':'المورد','account':'الحساب','bank':'البنك','safe':'الخزنة',
 'area':'المنطقة','payment':'السداد','notes':'الملاحظات','name':'الاسم','barcode':'الباركود',
 'picker':'نافذة الاختيار','scope':'النطاق','filter':'الفلتر','filters':'الفلاتر','rows':'الصفوف',
 'row':'الصف','headers':'عناوين الأعمدة','page':'الصفحة','size':'الحجم','date':'التاريخ','report':'التقرير',
 'print':'الطباعة','owned':'المملوك للاختبار','item':'الصنف','items':'الأصناف','choice':'الاختيار',
 'choices':'الاختيارات','session':'الجلسة','ready':'الجاهزية','validation':'التحقق','marker':'علامة الملكية',
 'capture':'تتبع الحفظ','saves':'عمليات الحفظ','attempts':'المحاولات','errors':'الأخطاء','cached':'المخزن مؤقتًا',
 'code':'الكود','english':'الإنجليزي','arabic':'العربي','debit':'المدين','credit':'الدائن',
 'settlement':'التسوية','balance':'الرصيد','sort':'الفرز','export':'التصدير','return':'المرتجع',
 'first':'الأول','last':'الأخير','next':'التالي','previous':'السابق','visible':'الظاهر','values':'القيم',
 'amount':'المبلغ','window':'التبويب','auxiliary':'الإضافي','fullscreen':'ملء الشاشة','form':'النموذج',
 'alert':'تنبيه المتصفح','swal':'رسالة تأكيد الصفحة','popup':'الرسالة المنبثقة','text':'النص',
 'element':'عنصر الشاشة','elements':'عناصر الشاشة','button':'الزر','checkbox':'مربع الاختيار',
 'dropdown':'القائمة المنسدلة','table':'الجدول','grid':'الجدول','locator':'محدد العنصر',
 'url':'عنوان الشاشة','title':'العنوان','message':'الرسالة','error':'الخطأ','value':'القيمة',
 'enabled':'الإتاحة','disabled':'التعطيل','selected':'القيمة المختارة','checked':'حالة التحديد',
 'confirm':'التأكيد','cancel':'الإلغاء','click':'النقر','scroll':'التمرير','login':'تسجيل الدخول',
 'inventory':'الجرد','audit':'سجل المراجعة','fixture':'بيانات الاختبار','journal':'سجل الاسترجاع',
}

def strip_literals(s: str) -> str:
    return LITERAL.sub(lambda m: ''.join('\n' if c == '\n' else ' ' for c in m[0]), s)

def words(name: str) -> list[str]:
    name = re.sub(r'^(sl|pu|sm|pr)(?=[A-Z])', '', name)
    return [w.lower() for w in re.findall(r'[A-Z]+(?=[A-Z][a-z]|$)|[A-Z]?[a-z]+|\d+', name)]

def method_purpose(name: str, test: bool = False) -> str:
    if name in METHODS: return METHODS[name]
    ws = words(name)
    labels = list(dict.fromkeys(WORDS[w] for w in ws if w in WORDS))
    subject = ' / '.join(labels[:5]) or 'القدرة التي يحددها اسم الدالة'
    first = ws[0] if ws else ''
    if test: return 'حالة تحقق مرتبطة بـ' + subject + '؛ تفاصيل الشرط والمتوقع داخل جسم الاختبار.'
    verbs = {'get':'قراءة أو إرجاع','read':'قراءة','is':'السؤال عن حالة','has':'السؤال عن وجود','can':'التحقق من إتاحة',
             'set':'تعيين','type':'كتابة','add':'إضافة','click':'ضغط إجراء','open':'فتح','close':'إغلاق',
             'wait':'انتظار','await':'انتظار','select':'اختيار','choose':'اختيار','clear':'مسح/إلغاء',
             'remove':'إزالة','delete':'حذف','confirm':'تأكيد','cancel':'إلغاء','dismiss':'إغلاق تنبيه',
             'assert':'مقارنة متوقعة','validate':'التحقق من','verify':'التحقق من','capture':'مراقبة',
             'find':'البحث عن','search':'البحث عن','normalize':'توحيد صيغة','parse':'تفسير',
             'request':'طلب إجراء','sort':'فرز','export':'تصدير','save':'حفظ','new':'بدء جديد',
             'prepare':'تجهيز','begin':'بدء نطاق','collect':'تجميع','clean':'تنظيف','cleanup':'تنظيف',
             'create':'إنشاء','edit':'تعديل','update':'تحديث','attach':'إرفاق','record':'تسجيل/تمرير',
             'matches':'مطابقة','matchesowned':'مطابقة الملكية','init':'تهيئة','quit':'إغلاق',
             'stock':'التعامل مع','payment':'التعامل مع','quantity':'ضبط','notes':'ضبط'}
    return verbs.get(first, 'تنفيذ جزء متعلق بـ') + ' ' + subject + '، حسب المدخلات والتخصص.'

def role(path: Path, s: str, active: str, name: str) -> str:
    if not s.strip(): return 'الملف فارغ تمامًا في النسخة الحالية؛ لا يحتوي تنفيذًا أو كلاسًا نشطًا.'
    if not active.strip(): return 'المحتوى معلق/تعليقات فقط في النسخة الحالية؛ لا يوجد تنفيذ Java نشط.'
    if re.fullmatch(r'\s*package[^;]+;\s*public class \w+\s*\{\s*\}\s*',active):
        return 'هيكل كلاس فارغ؛ الاسم موجود لكن لا توجد وظيفة قراءة منفذة فيه حاليًا.'
    if name in EXACT: return EXACT[name]
    p = path.as_posix()
    if name.endswith('FixtureApiStub'): return 'بديل محلي لـAPI المجال؛ يحاكي البيانات والنجاح والفشل لاختبار منطقنا دون ERP.'
    if name.endswith('FixtureDefinitions') or name.endswith('FixturesDefinitions'): return 'وصفات حقول ومراجع بيانات التجربة؛ يتولى المالك تسجيل هويات ما ينشأ وتنظيفه.'
    if name.endswith('FixtureApi') or name.endswith('FixturesApi'): return 'نقل طلبات fixtures وقراءتها عبر جلسة المتصفح المصرح بها، مع حارس السياق في المجال.'
    if 'Recovery' in name: return 'مسار متابعة سجلات بيانات اختبار محفوظة بعد توقف أو فشل، مع التحقق من نطاقها.'
    if name.endswith('TestData') or name == 'BasicDataFixtures': return 'مالك بيانات مجال الاختبار: الإنشاء والقراءة وتتبع الهوية والتنظيف والاسترجاع وفق آليته.'
    if name.endswith('ScenarioTest') or name.endswith('Scenario'):
        return 'تجهيز ودورة حياة مشتركة للحالات في هذا المجال؛ يربط الصفحة ببيانات الاختبار وقراءة النتائج.'
    if 'CleanupTest' in name or name.endswith('FixturesTest'): return 'فحوص محلية لمنطق ملكية البيانات وتنظيفها وحالات الفشل والرفض؛ لا تفسر الاسم كتشغيل ERP حي تلقائيًا.'
    if name.endswith('ApiStub'): return 'محاكاة ردود نقل البيانات لدعم فحوص الكود محليًا.'
    if 'EndToEndTest' in name: return 'اختبارات رحلة أعمال تربط عدة عمليات ومراجع وتتحقق من النتيجة النهائية وترتيب تنظيفها.'
    if name.endswith('Workflow'): return 'خطوات رحلة أعمال معاد استخدامها، بدل تكرار تسلسل عدة شاشات داخل كل اختبار.'
    if name.endswith('SuitePolicy'): return 'قواعد مستوى/اختيار حالات المجال أو ضبطها؛ ليست صفحة أو وصف بيانات ERP.'
    if name.endswith('DocumentKind') or name.endswith('MovementKind') or name.endswith('MovementKind'):
        return 'تعريف أنواع مستندات/حركات المجال وربطها بمساراتها أو خصائص سلوكها.'
    if 'ExportAssertions' in name or name == 'ExportedWorkbookAssertions': return 'مراجعة مخرجات تصدير المجال أو محتوى الملف الفعلي، بدلاً من الاكتفاء بضغط زر التصدير.'
    if name.endswith('Assertions') or name.endswith('ScreenAssertions'): return 'مقارنات مشتركة تتكرر في سيناريوهات المجال مع رسائل فشل مفهومة.'
    if name.endswith('FormActions') or name.endswith('ListActions'): return 'خطوات وتحقيقات مشتركة تعرضها اختبارات المجال النهائية دون نسخ نفس الجسم لكل نوع.'
    if '/src/test/' in '/' + p or p.startswith('src/test/'):
        if name.endswith('Test') or name.endswith('TestCases'):
            return 'حالات اختبار أو عقود محلية في المجال المحدد بمسار الملف؛ راجع @Test والأب ومرجع XML لمعرفة الاختيار والتنفيذ.'
    if name.endswith('SaveCapture'): return 'تتبع محاولات ونتائج الحفظ المملوكة لهذا المجال؛ منفصل عن نقر زر الحفظ ومقارنة قواعد العمل.'
    if name.endswith('PrintWindows'): return 'تنسيق تبويب الطباعة والرجوع للرئيسي واستدعاء مراجعة المحتوى في مجال المستند.'
    if name.endswith('DocumentForm'): return 'حقول النموذج وجاهزيته وحفظه وتنبيهاته في مجال المستند، بدون ملكية بيانات التجربة.'
    if name.endswith('DocumentLines'): return 'بنود المستند أو سلة الأصناف: القراءة والكمية والأسعار والحذف والإجماليات بحسب المجال.'
    if name.endswith('ItemSelection'): return 'البحث واختيار الصنف بالاسم/الباركود/النافذة بحسب ما تسمح به ميثوداته.'
    if name.endswith('ChoiceControls') or name == 'SalesDropdownControls': return 'اختيار وقراءة ومسح القيم في قوائم نموذج المجال.'
    if name.endswith('RecordList') or name.endswith('Records') or name.endswith('RecordActions'): return 'قراءة قائمة السجلات وتحديد سجل بعينه والتعديل والحذف والتأكيد وفق مجال الصفحة.'
    if name.endswith('GridControls') or name.endswith('ListControls'): return 'قراءة وترتيب وتنقل جدول الشاشة وتغيير حجمه أو تصديره بحسب قدراته.'
    if name.endswith('InvoiceRows') or name.endswith('Snapshot') or name.endswith('OrderRows'): return 'تحويل بنود الشاشة وقيمها إلى نماذج أو لقطة بيانات للمقارنة والحساب.'
    if name.endswith('SummaryReader') or name.endswith('PrintedTotals'): return 'قراءة قيم إجماليات الشاشة أو الطباعة حسب نموذج وselectors هذا التخصص.'
    if name.endswith('FormControls') or name.endswith('FormReader'): return 'عناصر وقراءات نموذج محدد؛ فصل عن القائمة وملكية بيانات السيناريو.'
    if name.endswith('ModulePage'): return 'واجهة تنسيق قدرات مجال متكرر؛ تستدعي مكونات متخصصة وتحتفظ بنقاط دخول الاختبارات.'
    if name.startswith('Page'): return 'قدرة مشتركة مفصولة من واجهة الصفحة العامة؛ تحدد أسماء ميثوداتها نوع الفعل المحدد.'
    if name.startswith('Document'): return 'قدرة مستند مشتركة مفصولة عن تنسيق الصفحة؛ مثل الحقول أو الأعمدة أو الدفع أو الطباعة.'
    if name.startswith('Product') and name in ['ProductUnits','ProductComponents']: return 'إجراءات وحدات الصنف أو مكوناته، مفصولة عن حقول بياناته الأساسية.'
    if 'Transitions' in name: return 'تنسيق تحويل مستند أو إنشاء مرتجع مرتبط أو حذف مستند الاختبار المحدد.'
    if 'Calendar' in name: return 'التعامل مع تقويم الشاشة وتواريخه ضمن مجاله.'
    if 'Workflow' in name: return 'خطوات رحلة مشتركة في المجال.'
    return 'تخصص شاشة أو مكون مرتبط بالمجال الموضح في المسار؛ راجع واجهته وعلاقاته أدناه لتحديد خطواته المتاحة.'

def java_info(path: Path) -> dict:
    s = path.read_text(encoding='utf-8-sig'); active = strip_literals(s)
    match = re.search(r'\b(?:class|enum|record|interface)\s+(\w+)',active)
    name = match[1] if match else path.stem
    package = re.search(r'(?m)^package\s+([\w.]+);',active)
    parent = re.search(r'\bclass\s+\w+(?:<[^>]+>)?\s+extends\s+([\w.]+)',active)
    declarations=[]
    matches = list(DECLARATION.finditer(active))
    constructors = re.compile(r'(?m)^[ \t]*(?P<access>public|protected|private)?[ \t]*(?P<type>)(?P<name>'+re.escape(name)+r')\((?P<params>[^{};]*?)\)[ \t]*(?:throws[ \t]+[^{};]+)?\{')
    seen = {m.start() for m in matches}
    matches.extend(m for m in constructors.finditer(active) if m.start() not in seen)
    for m in matches:
        type_words = m['type'].strip().split()
        if type_words and type_words[0] in {'return','throw','new','if','for','while','switch','catch','case'}: continue
        start=m.start(); brace=active.index('{',start);end=brace+1;depth=1
        while depth and end < len(active):
            depth += (active[end]=='{')-(active[end]=='}');end+=1
        sig=re.sub(r'\s+',' ',s[start:brace].strip())
        annotation=s[max(0,start-350):start]
        # A previous method's annotation must not classify this one as a test.
        before=annotation.rsplit('}',1)[-1]
        test=bool(re.search(r'@Test\b',before))
        access = re.match(r'^(public|protected|private)\b', sig)
        purpose = ('منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار.'
                   if m['name'] == name else method_purpose(m['name'],test))
        declarations.append({'name':m['name'],'signature':sig,'visibility':access[1] if access else 'داخل الحزمة',
                             'line':s[:start].count('\n')+1,'test':test,
                             'purpose':purpose})
    abstract_methods = re.compile(r'(?m)^[ \t]*(?P<access>public|protected)?[ \t]*abstract[ \t]+[\w.$<>?,\[\] ]+[ \t]+(?P<name>\w+)\([^;{}]*\)[ \t]*(?:throws[ \t]+[^;{}]+)?;')
    for m in abstract_methods.finditer(active):
        sig = re.sub(r'\s+', ' ', s[m.start():m.end()].strip())
        declarations.append({'name':m['name'],'signature':sig,'visibility':m['access'] or 'داخل الحزمة',
                             'line':s[:m.start()].count('\n')+1,'test':False,
                             'purpose':'عقد abstract يحدد ما يجب أن توفره الكلاسات المتخصصة؛ '+method_purpose(m['name'])})
    declarations.sort(key=lambda item: item['line'])
    imports=re.findall(r'(?m)^import\s+(?:static\s+)?([\w.*]+);',s)
    return {'path':path.relative_to(ROOT).as_posix(),'name':name,'package':package[1] if package else '',
            'parent':parent[1] if parent else '', 'lines':len(s.splitlines()), 'methods':declarations,
            'imports':imports,'role':role(path.relative_to(ROOT),s,active,name),'text':active,
            'sha256':hashlib.sha256(path.read_bytes()).hexdigest(),
            'status':'فارغ' if not s.strip() else 'تعليقات فقط' if not active.strip() else 'هيكل فارغ' if not declarations and re.search(r'class \w+\s*\{\s*\}',active) else 'مصدر نشط'}

def group(info: dict) -> tuple[str,str]:
    path=info['path']
    if path.startswith('src/main/java/Plugin_ERP/System_Modules/Login/'):
        tail=path.split('System_Modules/Login/',1)[1]
        key=tail.split('/')[0] if '/' in tail else 'Shared'
        return 'MAIN_PAGES_'+key, 'صفحات ومكونات '+{'Shared':'القواعد المشتركة','Invoice':'المبيعات وقائمة الفواتير','purches':'المشتريات','BasicData':'البيانات الأساسية','DealerModel':'المتعاملين','StoreModel':'مستندات المخازن','Products':'المنتجات','FinancialMovements':'الحركات المالية','GenralAccountant':'القيود اليومية','SpecialSales':'الكاشير والمطعم','Login':'الدخول','Coverage':'الشاشات الموسعة'}.get(key,key)
    if path.startswith('src/main/java/'):
        key=path.split('Plugin_ERP/',1)[1].split('/')[0]
        return 'MAIN_'+key, {'Bots':'أدوات Bots','Drivers':'مصانع المتصفحات','Enums':'تعداد المسارات','Models':'نماذج الحسابات','Business':'حاسبة الأعمال','Listeners':'مراقب التشغيل','Util':'الأدوات والإعداد والتقارير','Valditon':'التحقق القديم'}.get(key,key)
    tail=path.split('src/test/java/',1)[1]
    key=tail.split('/')[0]
    if key=='Plugin_ERP':
        key='Tests' if '/Tests/' in tail else 'FrameworkContracts'
    return 'TEST_'+key, 'اختبارات ودعم '+{'Invoice':'المبيعات','Purches':'المشتريات','StoreTest':'المخازن','ProductsTest':'المنتجات','DealerModelTest':'المتعاملين','FinancialMovementsTest':'الحركات المالية','SpecialSalesTest':'الكاشير والمطعم','CoverageTest':'التغطية الموسعة','GenralAccountantTest':'القيود اليومية','SuiteSupport':'عقود التشغيل','TestSupport':'التجهيز المشترك','Tests':'الدخول والبيانات الأساسية','FrameworkContracts':'المكونات والإطار'}.get(key,key)

def anchor(path: str) -> str:
    return 'file-'+hashlib.sha1(path.encode()).hexdigest()[:12]

def build_atlas() -> list[dict]:
    ATLAS.mkdir(parents=True,exist_ok=True)
    sources=[*sorted((ROOT/'src/main/java').rglob('*.java')),*sorted((ROOT/'src/test/java').rglob('*.java'))]
    infos=[java_info(p) for p in sources]
    grouped=collections.defaultdict(list);titles={};dest={}
    for item in infos:
        key,title=group(item);grouped[key].append(item);titles[key]=title;dest[item['path']]=key+'_AR.md'
    fqn={i['package']+'.'+i['name']:i for i in infos if i['package']}
    byname=collections.defaultdict(list)
    for i in infos:byname[i['name']].append(i)
    for i in infos:
        deps=[]
        for imp in i['imports']:
            if imp in fqn:deps.append(fqn[imp]['path'])
        if i['parent']:
            parent=fqn.get(i['package']+'.'+i['parent'])
            if parent:deps.append(parent['path'])
        i['dependencies']=sorted(set(deps));i['callers']=[]
    lookup={i['path']:i for i in infos}
    for i in infos:
        for dep in i['dependencies']:lookup[dep]['callers'].append(i['path'])
    index=['# أطلس ملفات المشروع','', '[الرئيسية](../README_AR.md)','',
           'كل بطاقة تقرأ ملفًا فعليًا من النسخة الحالية، وتعرض دوره وأبَه واعتماداته المباشرة وميثوداته المعلنة. الشرح المختصر للميثود وسيلة قراءة، وليس مواصفة كاملة لكل حالة شرطية داخل جسمها. العلاقات هنا من imports والأب المباشر؛ لا تدّعي حصر كل نداء ديناميكي أو انعكاسي.','',
           '| القسم | عدد الملفات |','|---|---:|']
    def ref(path:str)->str:
        return '['+Path(path).name+']('+dest[path]+'#'+anchor(path)+')'
    for key, items in sorted(grouped.items()):
        index.append('| ['+titles[key]+']('+key+'_AR.md) | '+str(len(items))+' |')
        doc=['# '+titles[key],'','[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)','',
             'اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.','']
        for i in items:
            source=(ROOT/i['path']);link=os.path.relpath(source,ATLAS).replace('\\','/')
            doc += ['<a id="'+anchor(i['path'])+'"></a>','## '+Path(i['path']).name,'',
                    '**المصدر:** ['+i['path']+']('+link+')','',
                    '**دوره:** '+i['role'],'',
                    '**حالته:** '+i['status']+' · **عدد أسطره:** '+str(i['lines'])+
                    (' · **الأب المباشر:** `'+i['parent']+'`' if i['parent'] else ''), '']
            if i['dependencies']:doc += ['**يرتبط مباشرة بملفات داخل المشروع:** '+ '، '.join(ref(p) for p in i['dependencies'][:12])+('؛ وباقي الاعتمادات في المصدر.' if len(i['dependencies'])>12 else ''),'']
            if i['callers']:doc += ['**ملفات تشير إليه مباشرة:** '+ '، '.join(ref(p) for p in i['callers'][:8])+('؛ العرض مختصر وليس قائمة كاملة.' if len(i['callers'])>8 else ''),'']
            if i['parent']:doc += ['**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.','']
            if i['methods']:
                doc += ['### خريطة الميثودات المعلنة','', '| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |','|---|---|---|']
                for m in i['methods']:
                    sig=m['signature'].replace('|','\\|')
                    doc.append('| `'+sig+'` | '+str(m['visibility'])+(' · @Test' if m['test'] else '')+' | '+m['purpose']+' |')
                doc.append('')
            else:doc += ['**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.','']
            if i['name'].endswith('Test') or i['name'].endswith('TestCases'):
                doc += ['**علاقته بالتشغيل:** ابحث عن الاسم الكامل `'+i['package']+'.'+i['name']+'` في [مرجع suites](../SUITE_REFERENCE_AR.md). وجوده في المصدر أو وجود @Test لا يعني أن كل XML يختاره.','']
        (ATLAS/(key+'_AR.md')).write_text('\n'.join(doc)+'\n',encoding='utf-8')
    index += ['| [الإعداد والموارد والسكربتات وملفات الجذر](RESOURCES_AND_TOOLS_AR.md) | راجع الحصر بالداخل |','',
              'لا تُدرج هنا سجلات Git الداخلية وملفات IDE والمكتبات المحملة ونسخ النتائج المولدة كأنها مصادر مستقلة. شرح أنواعها في خريطة المشروع وفصل النتائج. ملفات الإعداد الخاصة المحلية تُذكر كدور فقط ولا تُقرأ قيمها.']
    (ATLAS/'README_AR.md').write_text('\n'.join(index)+'\n',encoding='utf-8')
    clean=[{k:v for k,v in i.items() if k!='text'} for i in infos]
    (GUIDE/'SOURCE_SNAPSHOT.json').write_text(json.dumps({'date':DATE,'javaFiles':len(clean),'files':clean},ensure_ascii=False,indent=2),encoding='utf-8')
    return infos

def describe_resource(path:Path)->str:
    rel=path.relative_to(ROOT).as_posix();name=path.name
    exact={
      'pom.xml':'مواصفات البناء والمكتبات والإضافات وprofile CI. اقرأ فصل البنية والتشغيل قبل تعديل إصدار مكتبة.',
      '.gitignore':'أنواع ملفات لا تدخل Git، ومنها النتائج والاعتمادات المحلية؛ ليس نظام تنظيف ولا تشفيرًا.',
      'README.md':'مدخل المشروع القديم وفيه معلومات تشغيل ونتائج بتاريخها؛ بعض أرقامه تاريخية مقارنة بالدليل الحالي.',
      'WebData.properties':'اختيار البيئة والـTenant والمتصفح وطريقة التشغيل والروابط. القيم محلية متغيرة؛ لا تُنسخ هنا.',
      'Login.properties':'مفاتيح/قالب بيانات الدخول العام؛ الأسرار الفعلية تُدار محليًا أو بالبيئة.',
      'Wait.properties':'إعدادات مدد الانتظار العامة.', 'Cash.properties':'سياسات/إعدادات مالية عامة تستخدمها أجزاء الحساب.',
      'allure.properties':'إعداد مسار/سلوك Allure وفق المفاتيح الموجودة.',
      'log4j2.properties':'تنسيق ومستويات رسائل logging.', 'video.properties':'إعدادات الفيديو؛ وجودها لا يثبت تسجيل كل اختبار.',
      'SeleniumGrid.properties':'عنوان خدمة المتصفح البعيد وبيانات اتصال Grid غير المحلية.',
      'Test-Project.ps1':'يشغل الفحوص المحلية ثم القبول أو Regression حسب Mode ويحفظ سجلًا منفصلًا ولا ينفذ clean.',
      'check_secrets.py':'يراجع الملفات المسموح فحصها لمنع تسريب أسرار وتقارير خاصة إلى Git.',
      'test_check_secrets.py':'فحص محلي لسلوك كاشف الأسرار.',
      'Set-ReportMail.ps1':'مساعد إعداد خدمة إرسال التقرير؛ لا تشغله كتمرين دون قصد تغيير الإعداد.',
      'Send-ReportMail.ps1':'إعادة إرسال تقارير وفق إعداد الخدمة والمستلمين.',
      'Connect-ReportDrive.ps1':'إعداد اتصال Drive لتوصيل تقارير كبيرة؛ وظيفة خارجية مستقلة عن اختبار ERP.',
      'local-checks.yml':'GitHub Actions للفحوص المحلية على Windows وUbuntu ورفع أدلتها؛ ليس ERP Regression حيًا.',
      'pre-commit':'فحص عند محاولة commit وفق محتوى hook؛ لا يكفي وجوده دون تفعيل hooksPath.',
      'report-mail.properties':'إعداد عام لخدمة البريد؛ يعرض الدليل أسماء المفاتيح فقط ولا ينسخ مستلمين أو أسرارًا.',
      'upgrade_sales.py':'سكربت عمل جانبي لتعديل ملفات RDL بمسارات محلية ثابتة؛ ليس جزءًا من بناء Java المعتاد.',
      'style_invoice.py':'سكربت تنسيق جانبي لتقرير فاتورة RDL؛ يعتمد على ملف خارج المستودع حسب المصدر.',
      'repair_executive.py':'سكربت عمل جانبي لإصلاح قالب تقرير RDL باستخدام chart-template؛ لا يُشغل ضمن Maven.',
      'add_report_charts.py':'سكربت جانبي لمعالجة مخططات تقرير؛ راجع مساراته قبل تنفيذه.',
      'chart-template.xml':'قالب XML لمخطط يستخدمه سكربت تقرير جانبي، وليس TestNG suite.',
      'ms-chart-reference.rdl':'ملف مرجعي لتصميم تقرير بصيغة RDL، وليس اختبار Selenium.',
      'build_learning_guide.py':'أداة توليد هذا الأطلس ونسخة HTML من الدليل؛ تقرأ المصادر وتكتب التوثيق فقط.',
      'DocumentationSuiteInventory.java':'حصر اختيار TestNG الفعلي باستخدام قارئ عقود المشروع؛ يكتب بيانات مرجع المجموعات دون تنفيذ الاختبارات أو فتح ERP.',
    }
    if name in exact:return exact[name]
    if name.endswith('-testng.xml'):return 'اختيار حالات TestNG؛ راجع مرجع suites لعدده ومكوناته وأسماء الحالات الفعلية.'
    if name=='org.testng.ITestNGListener':return 'تسجيل خدمة listener لاكتشافها من TestNG حسب آلية الخدمة؛ لا يحتوي حالات اختبار.'
    if '/additional/legacy/' in rel:return 'تعريف توافق/استخدام قديم؛ لا تساويه بالمجموعة الأساسية الحديثة دون فحص اختيارها.'
    if '/Test-Data/' in rel:return 'بيانات أو توثيق حالات/تغطية حسب الصيغة؛ ليس بالضرورة ملف تشغيل TestNG. تراجع الاستخدامات الفعلية.'
    if name.endswith('.md'):return 'شرح قريب من الكود أو حصر تغطية؛ مستند وليس تنفيذ Java.'
    if name.endswith('.xml'):return 'ملف XML يحدد دوره من الجذر والمصدر؛ لا يعني امتداده أنه TestNG.'
    return 'ملف دعم ضمن المسار المذكور؛ راجع بنيته واستخداماته قبل تشغيله أو تعديله.'

def build_resources()->int:
    paths=[]
    for folder in ['src/main/resources','src/test/resources','scripts','.github','.mvn','.githooks']:
        base=ROOT/folder
        if base.exists():paths.extend(p for p in base.rglob('*') if p.is_file() and '__pycache__' not in p.parts)
    paths.extend(ROOT/p for p in ['pom.xml','README.md','.gitignore','config/report-mail.properties','upgrade_sales.py','style_invoice.py','repair_executive.py','add_report_charts.py','chart-template.xml','ms-chart-reference.rdl'] if (ROOT/p).is_file())
    paths=sorted(set(paths));doc=['# الإعداد والموارد والسكربتات وملفات الجذر','', '[فهرس الأطلس](README_AR.md)','',
        'تعرض هذه البطاقات الدور وأسماء البنى أو المفاتيح؛ لا تعرض قيم الدخول أو إعدادات خاصة. الملفات المحلية *.local.* لا تدخل الحصر النصي.','']
    for path in paths:
        rel=path.relative_to(ROOT).as_posix();link=os.path.relpath(path,ATLAS).replace('\\','/')
        doc+=['## '+rel,'','**الملف:** ['+path.name+']('+link+')','',describe_resource(path),'']
        if path.suffix=='.properties':
            keys=[]
            for line in path.read_text(encoding='utf-8-sig').splitlines():
                line=line.strip()
                if not line or line.startswith(('#','!')):continue
                m=re.match(r'([^=:\s]+)\s*[=:]',line)
                if m:keys.append(m[1])
            doc+=['**أسماء المفاتيح فقط:** '+('، '.join('`'+k+'`' for k in keys) if keys else 'لا توجد مفاتيح نشطة بصيغة key=value استخرجها الحصر.')+' القيم لا تظهر في الدليل.','']
        elif path.suffix=='.xml':
            try:
                tree=ET.parse(path);counts=collections.Counter(el.tag.split('}')[-1] for el in tree.iter())
                doc+=['**بنية XML:** الجذر `'+tree.getroot().tag.split('}')[-1]+'`؛ أكثر العناصر: '+ '، '.join('`'+k+'` × '+str(v) for k,v in counts.most_common(8))+'. عدد العناصر ليس عدد حالات منفذة.','']
            except ET.ParseError:doc+=['**البنية:** تعذر تحليل XML كوثيقة مستقلة؛ راجع المصدر قبل الاستخدام.','']
    doc+=['## ملفات محلية ومخرجات ليست مصادر للدراسة ملفًا ملفًا','',
          '`config/Login.local.properties` وملفات البريد/Drive المحلية: إعدادات خاصة لا ننسخ قيمها. `target` و`allure-results` و`allure-report` و`test-output` و`screenshots` و`videos`: نواتج وسجلات، يشرح الفصل التاسع أنواعها. ملفات `hs_err_pid*.log` و`replay_pid*.log`: تشخيصات JVM تاريخية لا تمثل سلوك الاختبارات المقصود. `.idea` وملفات IDE: تفضيلات جهاز. `.git`: تاريخ Git الداخلي. لا تنفذ حذفًا عامًا لأي منها على أساس قراءة هذه الفقرة؛ خصوصًا سجلات target قد تكون لازمة للاسترجاع.','',
          'ملفات `docs/testing` والتوثيق القديم وملفات `outputs` هي مراجع أو تسليمات تاريخية وليست المصدر التنفيذي. الدليل الجديد مستقل ويذكر عند الحاجة ما إذا كانت معلومة تاريخية أو من الكود الحالي.']
    (ATLAS/'RESOURCES_AND_TOOLS_AR.md').write_text('\n'.join(doc)+'\n',encoding='utf-8')
    return len(paths)

def build_suites()->None:
    source=ROOT/'target/documentation-suite-inventory.json'
    if not source.exists():raise SystemExit('Missing target/documentation-suite-inventory.json; run DocumentationSuiteInventory with the test dependency classpath first.')
    rows=json.loads(source.read_text(encoding='utf-8'));errors=[r for r in rows if 'inspectionError' in r]
    if errors:raise SystemExit('Suite inspection is incomplete: '+str(errors))
    doc=['# مرجع ملفات التشغيل الفعلية','', '[الرئيسية](README_AR.md)','',
         'البيانات التالية مستخرجة بواسطة قارئ TestNG نفسه المستخدم في فحوص عقود المشروع، بما فيه الاختبارات الموروثة وinclude/exclude. لا يُفتح متصفح ولا تنفذ الحالات خلال الحصر. الرقم دوال مختارة، وليس مرات تنفيذ DataProvider أو عدد الشاشات.','',
         '| الملف | الدوال المختارة | الدوال الفريدة داخل الملف |','|---|---:|---:|']
    for r in rows:doc.append('| `'+r['file']+'` | '+str(r['selectedMethods'])+' | '+str(r['uniqueMethods'])+' |')
    doc+=['','## كيف تقرأ الاختيارات؟','',
          '`<suite>` المجموعة، و`<test>` مجموعة تنظيمية داخلها، و`<class>` كلاس مختار. `<include name="...">` يختار اسم دالة أو نمطًا، و`<exclude>` يستبعده. `<suite-files>` يضم ملفات تشغيل أخرى. لا تخلط `<test>` في XML مع عدد @Test النهائي. التنفيذ الحالي متسلسل، وتفحص SuiteDefinitionsTest هذا القيد.','',
          'يمكن لنفس الحالة أن تدخل Smoke وRegression وهذا طبيعي بين مستويين. غير الطبيعي تكرارها بلا قصد مرتين داخل نفس اختيار المجموعة. الملفات تحت additional/standalone تحقق نطاقًا محددًا، وليست كلها مراحل تشغيل إلزامية في كل تعديل. ملفات legacy للتوافق القديم.','']
    for r in rows:
        doc+=['## '+r['file'],'', '**عدد الدوال:** '+str(r['selectedMethods'])+'، **الفريد:** '+str(r['uniqueMethods'])+'.','',
              '<details><summary>افتح قائمة أسماء الحالات الفعلية</summary>','', '```text',*r['cases'],'```','','</details>','']
    (GUIDE/'SUITE_REFERENCE_AR.md').write_text('\n'.join(doc)+'\n',encoding='utf-8')

def offline_diagram(match: re.Match) -> str:
    """Render the guide's small engineering diagrams without a network dependency."""
    code = match[1]
    if code.lstrip().startswith('classDiagram'):
        edges = re.findall(r'(\w+)\s+<\|--\s+(\w+)', code)
        children = collections.defaultdict(list)
        for parent, child in edges: children[parent].append(child)
        roots = [p for p in children if p not in {c for _, c in edges}]
        def branch(name):
            return '<li><span>'+html.escape(name)+'</span>'+(
                '<ul>'+''.join(branch(c) for c in children[name])+'</ul>' if children[name] else '')+'</li>'
        return '\n<figure class="diagram"><figcaption>شجرة الوراثة: كل فرع تحت الكلاس الذي يرث منه</figcaption><ul class="inheritance">'+''.join(branch(r) for r in roots)+'</ul></figure>\n'
    labels = re.findall(r'\w+\[([^\]]+)\]', code)
    labels = list(dict.fromkeys(labels))
    return '\n<figure class="diagram"><figcaption>اتبع الخطوات من أعلى إلى أسفل</figcaption><ol class="flow">'+''.join('<li>'+html.escape(label)+'</li>' for label in labels)+'</ol></figure>\n'

def build_html()->Path:
    sys.path.insert(0,str(ROOT/'target/documentation-tools'))
    try:import markdown
    except ImportError:raise SystemExit('HTML requires Python-Markdown. Install it outside the Java dependency tree, e.g. python -m pip install --target target/documentation-tools Markdown')
    docs=[GUIDE/'README_AR.md',*sorted(p for p in GUIDE.glob('*.md') if p.name!='README_AR.md'),ATLAS/'README_AR.md',*sorted(p for p in ATLAS.glob('*.md') if p.name!='README_AR.md')]
    ids={p.resolve():'doc-'+hashlib.sha1(p.relative_to(GUIDE).as_posix().encode()).hexdigest()[:12] for p in docs}
    chunks=[];nav=[]
    for p in docs:
        text=p.read_text(encoding='utf-8');title=next((line[2:] for line in text.splitlines() if line.startswith('# ')),p.stem)
        did=ids[p.resolve()]
        text = re.sub(r'```mermaid\s*\n(.*?)```', offline_diagram, text, flags=re.S)
        rendered=markdown.markdown(text,extensions=['tables','fenced_code','toc','sane_lists'])
        # Prefix heading IDs and link to the corresponding section of this one-file book.
        anchors=re.findall(r'\bid="([^"]+)"',rendered)
        for a in anchors:rendered=rendered.replace('id="'+a+'"','id="'+did+'--'+a+'"')
        def href(m):
            target=html.unescape(m[1])
            if re.match(r'^(https?:|mailto:|data:)',target):return m[0]
            file,sep,frag=target.partition('#')
            resolved=(p.parent/file).resolve() if file else p.resolve()
            if resolved in ids:
                value='#'+ids[resolved]+('--'+frag if frag else '')
            elif target.startswith('#'):value='#'+did+'--'+frag
            else:value=os.path.relpath(resolved,GUIDE).replace('\\','/')+(sep+frag if sep else '')
            return 'href="'+html.escape(value,quote=True)+'"'
        rendered=re.sub(r'href="([^"]*)"',href,rendered)
        chunks.append('<section class="chapter" id="'+did+'" data-title="'+html.escape(title,quote=True)+'"><div class="source-label">'+html.escape(p.relative_to(GUIDE).as_posix())+'</div>'+rendered+'</section>')
        nav.append('<a href="#'+did+'">'+html.escape(title)+'</a>')
    style='''
    :root{--ink:#182b3d;--muted:#5c6f7b;--paper:#fff;--bg:#edf2f5;--accent:#0f766e}
    *{box-sizing:border-box}html{scroll-behavior:smooth}body{margin:0;background:var(--bg);color:var(--ink);font:17px/1.95 Tahoma,Arial,sans-serif;direction:rtl}
    header{padding:34px 5vw;background:#163548;color:#fff}header h1{margin:0;font-size:30px}header p{margin:8px 0}
    .layout{display:grid;grid-template-columns:295px minmax(0,1100px);gap:24px;max-width:1480px;margin:24px auto;padding:0 20px}
    aside{position:sticky;top:12px;align-self:start;max-height:95vh;overflow:auto;background:#fff;border-radius:12px;padding:18px;border:1px solid #d9e4e9}
    aside a{display:block;color:var(--ink);padding:7px 0;border-bottom:1px solid #edf2f5;font-size:14px;text-decoration:none}
    .chapter{background:var(--paper);padding:34px 42px;margin-bottom:24px;border:1px solid #dce5e9;border-radius:14px;scroll-margin-top:18px;overflow-wrap:anywhere}
    h1{font-size:28px;line-height:1.5}h2{font-size:23px;border-bottom:2px solid #dcebe8;padding-bottom:8px;margin-top:36px}h3{font-size:19px;color:#155e5a}
    a{color:var(--accent)}p{margin:14px 0}table{width:100%;border-collapse:collapse;font-size:14px;margin:22px 0}th,td{border:1px solid #d9e2e8;padding:10px;vertical-align:top}th{background:#eaf4f1;text-align:right}tr:nth-child(even){background:#f8fafb}
    pre{direction:ltr;text-align:left;background:#172b3c;color:#edf5fa;padding:20px;border-radius:10px;overflow:auto;line-height:1.6;font-size:13px}code{direction:ltr;unicode-bidi:embed;font:0.88em Consolas,monospace}p code,li code,td code{background:#edf2f5;color:#163548;padding:2px 5px;border-radius:4px}
    .source-label{font:12px Consolas,monospace;direction:ltr;color:var(--muted);border-bottom:1px solid #e5ebef;padding-bottom:10px}details{border:1px solid #dce5e9;border-radius:8px;padding:12px;margin:14px 0}summary{cursor:pointer;color:var(--accent)}
    input{width:100%;padding:12px;border:1px solid #b8cecf;border-radius:8px;font:inherit;margin-bottom:12px}button{padding:10px 14px;border:0;border-radius:7px;background:var(--accent);color:white;cursor:pointer;font:inherit}.controls{display:flex;gap:8px;align-items:center;flex-wrap:wrap}.note{font-size:14px;color:#d6e7ec}
    aside a[hidden]{display:none}
    .diagram{padding:20px;background:#f1f8f8;border:1px solid #c8dddd;border-radius:10px;margin:20px 0}.diagram figcaption{font-weight:bold;margin-bottom:15px}.flow{list-style:none;padding:0;max-width:600px;margin:auto;text-align:center}.flow li{padding:9px;border:1px solid #91b8b4;background:white;border-radius:6px;margin-bottom:22px;position:relative}.flow li:not(:last-child):after{content:'↓';position:absolute;bottom:-26px;left:48%;color:#147a75}.inheritance{direction:ltr;text-align:left;font-family:Consolas,monospace}.inheritance ul{padding-left:25px;border-left:2px solid #91b8b4;margin:8px 0}.inheritance li{list-style:none;margin:8px}.inheritance span{display:inline-block;background:white;border:1px solid #91b8b4;border-radius:5px;padding:5px 10px}
    @media(max-width:900px){.layout{display:block;padding:0 10px}aside{position:relative;max-height:300px;margin-bottom:20px}.chapter{padding:20px}table{font-size:12px}header h1{font-size:25px}}
    @media print{body{background:#fff;font-size:12pt}header{background:#fff;color:#000;padding:0}aside,.controls,.note,.source-label{display:none}.layout{display:block;margin:0;padding:0}.chapter{border:0;padding:0;break-before:page}pre{white-space:pre-wrap;color:#000;background:#f3f3f3}a{color:#000;text-decoration:none}tr{break-inside:avoid}}
    '''
    page='<!doctype html><html lang="ar" dir="rtl"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>دليل دراسة Plugin ERP — 24 أكتوبر 2026</title><style>'+style+'</style></head><body><header><h1>دليل دراسة مشروع Plugin ERP من الصفر</h1><p>شرح مبسط + مرجع ملف بملف · نسخة 24 أكتوبر 2026</p><div class="controls"><button onclick="window.print()">طباعة أو حفظ PDF من المتصفح</button><span>للبحث داخل النص استخدم Ctrl+F</span></div><p class="note">نسخة HTML مستقلة للقراءة؛ روابط المصدر تحتاج بقاء مجلد المشروع بجوارها. الرسوم مضمّنة وتعمل دون اتصال بالإنترنت.</p></header><div class="layout"><aside><input id="navSearch" aria-label="بحث في الفصول" placeholder="فلترة عناوين الفصول والأطالس">'+''.join(nav)+'</aside><main>'+''.join(chunks)+'</main></div><script>document.getElementById("navSearch").addEventListener("input",function(){const q=this.value.trim().toLowerCase();document.querySelectorAll("aside a").forEach(a=>a.hidden=!a.textContent.toLowerCase().includes(q));});</script></body></html>'
    dest=GUIDE/'PROJECT_STUDY_GUIDE_AR.html';dest.write_text(page,encoding='utf-8');return dest

def validate()->dict:
    docs=list(GUIDE.rglob('*.md'));bad=[];source_links=0
    for p in docs:
        text=p.read_text(encoding='utf-8')
        for target in re.findall(r'\]\(([^)]+)\)',text):
            if re.match(r'^(https?:|mailto:)',target):continue
            file=target.split('#',1)[0]
            if not file:continue
            resolved=(p.parent/file).resolve()
            if not resolved.exists():bad.append({'file':p.relative_to(ROOT).as_posix(),'target':target})
            elif resolved.suffix=='.java':source_links+=1
        if '\ufffd' in text:bad.append({'file':str(p),'target':'replacement character'})
    snapshot=json.loads((GUIDE/'SOURCE_SNAPSHOT.json').read_text(encoding='utf-8'))
    actual={p.relative_to(ROOT).as_posix() for f in ['src/main/java','src/test/java'] for p in (ROOT/f).rglob('*.java')}
    covered={i['path'] for i in snapshot['files']}
    if actual!=covered:bad.append({'file':'SOURCE_SNAPSHOT','target':'source coverage mismatch'})
    result={'date':DATE,'markdownFiles':len(docs),'javaSourcesCovered':len(covered),'javaSourceLinks':source_links,'missingLinksOrEncodingErrors':bad}
    (GUIDE/'DOCUMENTATION_CHECK.json').write_text(json.dumps(result,ensure_ascii=False,indent=2),encoding='utf-8')
    if bad:raise SystemExit(json.dumps(result,ensure_ascii=True))
    return result

def main()->None:
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--html',action='store_true');args=parser.parse_args()
    infos=build_atlas();resources=build_resources();build_suites();result=validate();result['resourceFilesCovered']=resources
    if args.html:result['html']=str(build_html().relative_to(ROOT))
    print(json.dumps(result,ensure_ascii=True))

if __name__=='__main__':main()
