# إرسال التقارير إلى Gmail

الرسالة تعرض ملخص HTML عربيًا: حالة التشغيل، أعداد الناجح والفاشل والمتخطّي، نسبة النجاح، التينانت، المدة وتوقيت القاهرة، وأول خمس حالات فاشلة بأسباب مختصرة. توجد نسخة نصية بديلة لبرامج البريد التي لا تعرض HTML. عدد الفشل في الملخص يجمع failed وbroken. تستخدم الرسالة جداول وتنسيقات داخلية دون صور أو خطوط خارجية. قد تختلف بعض الزوايا والتفاصيل البصرية بين برامج البريد. قالب العرض في `ReportMailTemplate.java`، وإرسال الرسالة والمرفقات في `ReportMail.java`.

المرسل الافتراضي `eyd.mmdouh@gmail.com` والمستلم الافتراضي `mamdouhm7701@gmail.com`. عند التفعيل، يرسل مستمع TestNG رسالة لكل تشغيل تحتوي تقرير Allure مضغوطًا وتقرير TestNG Emailable مضغوطًا، وملخص Allure في نص الرسالة، حتى لو فشلت اختبارات. التشغيل الملغى أو المتوقف قبل التقارير لا يضمن إرسال رسالة.

## على جهازك

فعّل التحقق بخطوتين لحساب الإرسال وأنشئ App Password من https://myaccount.google.com/apppasswords ثم نفذ:

```powershell
./scripts/Set-ReportMail.ps1
```

السكربت يطلب كلمة مرور التطبيق بشكل مخفي ويحفظها في `config/ReportMail.local.properties` المتجاهل بواسطة Git. هذا ملف نصي محلي؛ لا تشاركه. لا ترسل كلمة المرور في المحادثة. بعدها شغّل الاختبارات كالمعتاد عبر Maven أو IntelliJ باستخدام ملفات TestNG التي تسجل `TestNGListeners`.

لإضافة مستلمين، عدّل `to` في الملف المحلي إلى عناوين مفصولة بفواصل، أو مررها للسكربت:

```powershell
./scripts/Set-ReportMail.ps1 -Recipients 'mamdouhm7701@gmail.com,second@example.com'
```

لإيقاف الإرسال اضبط `enabled=false` في الملف المحلي. إعدادات البيئة تتقدم على الإعدادات المحلية.

## GitHub Actions

أضف Repository Actions Secrets:

- `REPORT_MAIL_ENABLED`: القيمة `true`.
- `REPORT_MAIL_USERNAME`: حساب Gmail المرسل.
- `REPORT_MAIL_PASSWORD`: App Password.

اختياري: أضف Repository Actions Variable باسم `REPORT_MAIL_TO` للعناوين مفصولة بفواصل. عند غيابه يستخدم المستلم الافتراضي من `config/report-mail.properties`.

مسار Local checks يعمل على Windows وUbuntu، لذلك تصل رسالتان لكل تشغيل workflow، وكل رسالة تحتوي التقريرين. معرّف التشغيل والنظام في عنوان الرسالة. لا تتاح الأسرار لطلبات دمج forks، فلا يتفعل الإرسال فيها. لا تحفظ أسرار Gmail في YAML أو Git.

## التحقق والحدود

الإرسال يستخدم `smtp.gmail.com:587` وSTARTTLS إجباريًا والتحقق من شهادة الخادم. إجمالي المرفقات المضغوطة يجب ألا يتجاوز 17 MiB لتوفير مساحة لترميز البريد. فشل الإرسال يُسجل دون عرض بيانات المصادقة ولا يغير نتيجة الاختبارات، وتظل التقارير متاحة محليًا وفي artifacts الخاصة بـCI. ابحث في السجل عن `reports sent successfully` أو `Report email failed`.

افك ZIP الخاص بـTestNG وافتح `emailable-report.html`. مرفق Allure يستخدم وضع `--single-file`؛ فك ZIP وافتح `index.html` مباشرة في المتصفح. التقرير المحلي المعتاد يظل متاحًا أيضًا.

اختبارات التحقق المحلية لا ترسل بريدًا ولا تستخدم حساب Gmail:

```powershell
mvn '-Dtest=ReportMailTest,AllureReportPolicyTest' test
```

مرجع Google: https://support.google.com/mail/answer/7104828
