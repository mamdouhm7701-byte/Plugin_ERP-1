# إعداد رفع التقارير الكبيرة إلى Google Drive

## ماذا يحدث بعد الإعداد؟

يظل إجمالي المرفقات المباشرة ضمن 17 MiB. إذا كبر Allure أو TestNG، يرفع ZIP كاملًا إلى Drive ويضع رابطًا داخل الميل. الصور والمرفقات لا تُحذف من التقرير. الرابط لتنزيل ZIP، وليس لفتح تطبيق Allure داخل Drive: نزل الملف وفك الضغط وافتح index.html. المستلم يحتاج تسجيل الدخول إلى حساب Google الذي يطابق عنوان البريد المشارك معه.

تظل نافذة Allure المحلية تعمل وفق إعدادات الفتح السابقة. الملفات الصغيرة لا تحتاج تفويض Drive. إذا فشل الرفع أو المشاركة، يرسل الملخص مع تنبيه واضح ويحفظ المرفقات لإعادة المحاولة؛ لا يرسل رابطًا غير متاح باعتباره تسليمًا ناجحًا.

## تفويض أول مرة — على حساب مالك التقارير

App Password المخصصة لـGmail لا تمنح صلاحية Drive. يلزم OAuth Desktop client للمشروع، حتى يعمل الرفع من Java وIntelliJ وMaven دون محادثة مفتوحة. ربط إضافة Drive في Codex وحده لا يزود Java بهذا التفويض.

1. افتح [Google Cloud Console](https://console.cloud.google.com/) بحساب تملك فيه صلاحية إنشاء مشروع، وأنشئ أو اختر مشروعًا.
2. من APIs & Services → Library فعّل **Google Drive API**.
3. من Google Auth Platform جهز Branding وAudience. إذا التطبيق External وفي حالة Testing، أضف حساب Drive المطلوب ضمن Test users. توضح Google أن refresh tokens لتطبيق External في Testing بصلاحيات Drive قد تنتهي بعد 7 أيام؛ الإعداد الدائم يحتاج وضع النشر المناسب وسياسة حسابك. لا تتجاوز تحذيرات Google؛ استخدم مشروعًا تملكه أو راجع مسؤول الحساب.
4. من Clients أنشئ OAuth client بنوع **Desktop app** ونزل ملف JSON. لا تختار service account أو Web application.
5. احفظ الملف داخل المشروع باسم `config/drive-client.local.json`؛ هذا الاسم متجاهل في Git. لا ترسل محتوياته في المحادثة.
6. من Terminal المشروع شغّل:

```powershell
./scripts/Connect-ReportDrive.ps1 -ClientJson './config/drive-client.local.json'
```

يفتح رابط Google الرسمي؛ اختر الحساب الذي سيملك التقارير ووافق على الصلاحية المحددة `drive.file`. الأفضل استخدام `eyd.mmdouh@gmail.com` إن كنت تريد نفس حساب الإرسال. الأداة تستقبل callback على 127.0.0.1 لمدة خمس دقائق، وتستخدم state وPKCE، وتحفظ refresh token محليًا في `config/Drive.local.properties` دون طباعته. الملف نصي حساس ويتجاهله Git؛ احمه كملف كلمة مرور ولا تشاركه.

النتيجة المطلوبة: `Drive authorized`. لا تدخل كلمة مرور Google في Terminal أو المحادثة.

## إعادة إرسال التقرير بدون تشغيل Regression

التشغيل التالي بعد الإعداد يرفع الكبير تلقائيًا. لإرسال التقرير الحالي قبل أي رن أخرى:

```powershell
./scripts/Send-ReportMail.ps1 -TestNgDirectory './target/surefire-reports'
```

لا تستخدم هذا الأمر إلا إذا كان Allure results وAllure report وTestNG من نفس الرن. عند تشغيل TestNG من IntelliJ قد يكون مجلد TestNG مختلفًا؛ اختر المجلد الذي يحتوي `emailable-report.html` لنفس الرن، أو استخدم النسخة المحفوظة أدناه. الأمر لا يشغل اختبارات ولا يحذف البيانات.

التسليم الجديد يحفظ مجلدًا تحت `target/report-mail/deliveries` يحتوي ZIPs وملخص الرن. لإعادة إرسال نفس النسخة، حتى لو شغلت اختبارات أخرى بعدها:

```powershell
./scripts/Send-ReportMail.ps1 -Bundle './target/report-mail/deliveries/اسم-المجلد'
```

`delivery-status.txt` يوضح قبول SMTP والتسليم الكامل أو الجزئي. قبول SMTP لا يثبت ظهور الرسالة في Inbox. محاولة إعادة الإرسال صريحة وقد ترسل رسالة أخرى؛ لا تُعد SMTP تلقائيًا بعد قبول غير مؤكد.

## الملكية والمشاركة والمساحة

- المشاركة لكل عنوان في `to` بصلاحية reader دون رسائل إشعار مشاركة إضافية. لا يُفعّل anyone/public. الحساب المالك يظل له الوصول؛ قد لا تعمل بعض الإيميلات الخارجية إذا منعت سياسة Google Workspace المشاركة.
- لا يتوقف إرسال الملخص بسبب منع المشاركة، لكن تسليم الملف الكبير يُسجل جزئيًا.
- الرفع في جذر My Drive فقط لتجنب توريث صلاحيات مجلد مشترك. تحديد `drive.folder.id` يُرفض، ويتحقق التطبيق من قائمة صلاحيات الملف بعد المشاركة قبل إرسال الرابط.
- الملفات المرفوعة والمحلية تبقى محفوظة؛ لا توجد سياسة حذف تلقائي. راقب مساحة Drive و`target/report-mail`، واحفظ التقارير المطلوبة قبل `mvn clean`.
- سجل Drive المحلي في `target/report-mail/drive-journal` مرتبط ببصمة الملف والتفويض، ويعيد استخدام الملف عند إعادة نفس النسخة؛ يعيد مشاركة المستلمين ويتحقق من صلاحياتهم. تغيير التوكن أو حذف السجل قد يرفع نسخة جديدة. عند إزالة عنوان من قائمة المستلمين لا يحذف التطبيق صلاحيات قديمة تلقائيًا؛ ارفع نسخة جديدة أو ألغ المشاركة القديمة يدويًا من Drive حتى ينجح التحقق.

## GitHub Actions

بعد نجاح التفويض المحلي، أضف Secrets مستقلة:

- `REPORT_DRIVE_CLIENT_ID`
- `REPORT_DRIVE_CLIENT_SECRET`
- `REPORT_DRIVE_REFRESH_TOKEN`

تطابق قيم `drive.client.id` و`drive.client.secret` و`drive.refresh.token` من الملف المحلي. لا تنسخ القيم إلى YAML أو Git. Workflow الحالي يقرأ هذه Secrets ويحتفظ بنسخ التسليم كـartifacts لمدة 7 أيام. ملفات Drive نفسها تستمر حتى حذفها من حساب المالك.

## التحقق المحلي

```powershell
mvn '-Dtest=DriveReportStoreTest,ReportMailTemplateTest,ReportMailTest' test
mvn -Pci verify
```

الاختبارات تستخدم خادم HTTP محليًا وبيانات وهمية؛ لا ترفع ملفات إلى حساب Google.

المراجع: [رفع ملفات Drive](https://developers.google.com/workspace/drive/api/guides/manage-uploads)، [OAuth للتطبيقات المحلية](https://developers.google.com/identity/protocols/oauth2/native-app).
