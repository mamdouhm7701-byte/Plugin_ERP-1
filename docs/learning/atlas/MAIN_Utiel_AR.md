# الأدوات والإعداد والتقارير

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-2851a84e48e3"></a>
## AllureAttachment.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureAttachment.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureAttachment.java)

**دوره:** هيكل كلاس فارغ؛ الاسم موجود لكن لا توجد وظيفة قراءة منفذة فيه حاليًا.

**حالته:** هيكل فارغ · **عدد أسطره:** 3

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-d01d270aef86"></a>
## AllureBinaryManger.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureBinaryManger.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureBinaryManger.java)

**دوره:** تجهيز بيانات نتائج Allure وأداة بنائه وحفظ التاريخ حسب المسارات الحالية.

**حالته:** مصدر نشط · **عدد أسطره:** 319

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

**ملفات تشير إليه مباشرة:** [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void cleanAllure(boolean cleanResults, boolean cleanReport)` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void prepareResultsForRun()` | public | تجهيز القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void copyHistoryFromReportToResults()` | public | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `public static void generateSingleFileReport(Path destination) throws Exception` | public | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `public static void generateReportOnly()` | public | تنفيذ جزء متعلق بـ التقرير، حسب المدخلات والتخصص. |
| `public static void generateAndOpenDetached()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static Path prepareAllureCLI() throws Exception` | private | تجهيز القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static Optional<Path> findPreferredAllureExecutableUnder(Path root) throws IOException` | private | البحث عن القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static Path downloadZip() throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void unzip(Path zip, Path toDir) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void runCommand(String... cmd) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void deleteDirIfExists(Path dir)` | private | حذف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-92ef6e0b0e4e"></a>
## AllureConstants.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureConstants.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureConstants.java)

**دوره:** مسارات ومواقع نتائج التقرير وتاريخه المستخدمة في خدمات Allure.

**حالته:** مصدر نشط · **عدد أسطره:** 56

**ملفات تشير إليه مباشرة:** [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `static Path outputPath(Path project, String configured)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-fdcdbfe78f62"></a>
## AllureEnvironment.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureEnvironment.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureEnvironment.java)

**دوره:** كتابة معلومات بيئة التشغيل في التقرير مع تحديث بيانات المتصفح.

**حالته:** مصدر نشط · **عدد أسطره:** 113

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

**ملفات تشير إليه مباشرة:** [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static synchronized void writeEnvironment(String browserName, String browserVersion)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void writeBaseEnvironmentOnce()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static synchronized void updateBrowser(String browserName, String browserVersion)` | public | تحديث القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void putSafe(Properties props, String key, String value)` | private | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |
| `private static String safe(String value)` | private | تنفيذ جزء متعلق بـ الخزنة، حسب المدخلات والتخصص. |

<a id="file-18bdb24878c5"></a>
## AllureReportGenerator.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureReportGenerator.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureReportGenerator.java)

**دوره:** هيكل كلاس فارغ؛ الاسم موجود لكن لا توجد وظيفة قراءة منفذة فيه حاليًا.

**حالته:** هيكل فارغ · **عدد أسطره:** 3

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-4fa275d1cd80"></a>
## AllureReportPolicy.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureReportPolicy.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureReportPolicy.java)

**دوره:** تحديد هل يولد HTML ويفتح تلقائيًا؛ النتائج الخام سياسة منفصلة عن الفتح.

**حالته:** مصدر نشط · **عدد أسطره:** 19

**ملفات تشير إليه مباشرة:** [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record AllureReportPolicy(boolean generate, boolean open)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static AllureReportPolicy current()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static AllureReportPolicy from(Properties properties, String ci)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-9048c0acaaa9"></a>
## AllureUtils.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/AllureUtils.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/AllureUtils.java)

**دوره:** مساعدات عامة مرتبطة بـAllure في الاستدعاءات الحالية.

**حالته:** مصدر نشط · **عدد أسطره:** 57

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

**ملفات تشير إليه مباشرة:** [ScreenShotsUtils.java](MAIN_Utiel_AR.md#file-db7a1ec24b8d)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private AllureUtils()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static void cleanAllureResults()` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void attachScreenshotToAllure(String attachmentName, String filePath)` | public | إرفاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void attachFileToAllure( String attachmentName, String filePath, String mimeType, String extension)` | public | إرفاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-803f4a58e3cd"></a>
## DriveReportStore.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/DriveReportStore.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/DriveReportStore.java)

**دوره:** رفع وحفظ ملف تقرير على Drive باستخدام إعدادات الخدمة المصرح بها.

**حالته:** مصدر نشط · **عدد أسطره:** 178

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `DriveReportStore(Properties config)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `DriveReportStore(Properties config, URI testBase, Path journal)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private URI endpoint(String live, String local)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static Map<?, ?> json(String value) throws Exception` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static String form(Map<String, String> values)` | داخل الحزمة | تنفيذ جزء متعلق بـ النموذج، حسب المدخلات والتخصص. |
| `private String token() throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private HttpRequest.Builder request(URI uri) throws Exception` | private | طلب إجراء القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void requireSuccess(HttpResponse<?> response) throws IOException` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private HttpResponse<String> jsonRequest(String method, URI uri, Map<String, ?> body) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static String digest(Path file) throws Exception` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private URI fileUri(String id, String suffix)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void save(Path path, Properties state) throws Exception` | private | تنفيذ/تنسيق الحفظ ومتابعة النتيجة في سياق الملف. |
| `synchronized String publish(Path file, List<String> recipients) throws Exception` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String upload(Path file) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void verifySharing(String id, List<String> recipients) throws Exception` | private | التحقق من القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-0e9145a2b48f"></a>
## ReportDelivery.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ReportDelivery.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ReportDelivery.java)

**دوره:** توزيع ملفات التقرير بين المرفقات وروابط Drive وفق الحجم المتاح.

**حالته:** مصدر نشط · **عدد أسطره:** 44

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `record Plan(List<Path> attachments, String html, String plain, boolean complete)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static Plan plan(List<Path> files, long budget, Publisher publisher) throws Exception` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-511a86fca1ff"></a>
## ReportMail.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ReportMail.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ReportMail.java)

**دوره:** قراءة سياسة البريد وبناء وإرسال التقرير؛ لا يحدد نجاح حالة ERP.

**حالته:** مصدر نشط · **عدد أسطره:** 142

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ReportMail()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static Properties configuration() throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static boolean enabled()` | public | قراءة هل خدمة/ميزة التقرير مفعلة من إعداداتها. |
| `static InternetAddress[] recipients(String value) throws Exception` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `static Path archive(Path directory, Path destination) throws Exception` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void send(Path testngDirectory) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void sendPackaged(Path bundle) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-8f38fd7725ed"></a>
## ReportMailCommand.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ReportMailCommand.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ReportMailCommand.java)

**دوره:** مدخل أمر إرسال التقارير بصورة مستقلة وفق إعداداته.

**حالته:** مصدر نشط · **عدد أسطره:** 108

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ReportMailCommand()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static void main(String[] args) throws Exception` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static String random()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static Map<String, String> query(String raw)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void authorize(Path source) throws Exception` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-e578590ea497"></a>
## ReportMailTemplate.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ReportMailTemplate.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ReportMailTemplate.java)

**دوره:** شكل ملخص رسالة نتائج الاختبار.

**حالته:** مصدر نشط · **عدد أسطره:** 141

**يرتبط مباشرة بملفات داخل المشروع:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ReportMailTemplate()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `JSONObject getJSONObject(String key)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `JSONObject optJSONObject(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `JSONArray optJSONArray(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String optString(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String optString(String key, String fallback)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int optInt(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `long optLong(String key)` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private record JSONArray(List<?> values)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `int length()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `JSONObject getJSONObject(int index)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public record Failure(String name, String message)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public record Run(String suite, String tenant, String id, int total, int passed, int failed, int broken, int skipped, long start, long duration, List<Failure> failures)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static Run read(Path report, Path results) throws Exception` | public | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static String status(Run r)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String subject(Run r)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static String escape(String text)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static String date(Run r)` | private | تنفيذ جزء متعلق بـ التاريخ، حسب المدخلات والتخصص. |
| `private static String duration(Run r)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String plain(Run r)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static String shortMessage(String message)` | private | تنفيذ جزء متعلق بـ الرسالة، حسب المدخلات والتخصص. |
| `private static String metric(String label, int count, String color)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String html(Run r)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-be475b81d93d"></a>
## ScenarioAccess.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ScenarioAccess.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ScenarioAccess.java)

**دوره:** التقاط سياق وصول أو شاشة لإضافته كدليل في التقرير.

**حالته:** مصدر نشط · **عدد أسطره:** 53

**يرتبط مباشرة بملفات داخل المشروع:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ScenarioAccess()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private record Credentials(String username, String password)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void login(WebDriver driver, String username, String password)` | public | تنفيذ جزء متعلق بـ تسجيل الدخول، حسب المدخلات والتخصص. |
| `public static void capture(WebDriver driver, String label)` | public | مراقبة تتبع الحفظ، حسب المدخلات والتخصص. |
| `private static void parameter(java.util.List<Parameter> parameters, String name, String value)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-480625bfa4ad"></a>
## ScenarioAssertions.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ScenarioAssertions.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ScenarioAssertions.java)

**دوره:** إدارة سياق رحلة التحقق والأدلة أثناء الحالة.

**حالته:** مصدر نشط · **عدد أسطره:** 119

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void begin()` | public | بدء نطاق القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void end()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void recordComparison(Object actual, Object expected, String message, AssertionError failure)` | public | تسجيل/تمرير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String title(String method, Object[] args)` | public | تنفيذ جزء متعلق بـ العنوان، حسب المدخلات والتخصص. |
| `private static String display(Object value)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void assertionCall()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void start(JoinPoint call)` | public | تسجيل بداية محاولة أو إجراء. |
| `public void passed()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void failed(Throwable failure)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private void finish(Throwable failure)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-00bcc88744b4"></a>
## ScenarioSoftAssert.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ScenarioSoftAssert.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ScenarioSoftAssert.java)

**دوره:** SoftAssert يدعم تسجيل تفاصيل المقارنات في تقرير السيناريو.

**حالته:** مصدر نشط · **عدد أسطره:** 14 · **الأب المباشر:** `SoftAssert`

**إزاي تقرأه؟** اقرأ السلوك المحلي هنا مع واجهة الأب؛ قِصر الملف قد يكون لأنه يرث اختبارات أو يفوض قدرات، وليس لأنه بلا وظيفة.

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-335d6b9743c2"></a>
## ScreenNames.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/Allur/ScreenNames.java](../../../src/main/java/Plugin_ERP/Utiel/Allur/ScreenNames.java)

**دوره:** أسماء مقروءة للشاشات عند فتح مساراتها داخل الأدلة.

**حالته:** مصدر نشط · **عدد أسطره:** 44

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private ScreenNames()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static String name(String route)` | public | تنفيذ جزء متعلق بـ الاسم، حسب المدخلات والتخصص. |
| `public static void opening(String route)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-c2d822c8a7f2"></a>
## ConfigurationProperties.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/ConfigurationProperties.java](../../../src/main/java/Plugin_ERP/Utiel/ConfigurationProperties.java)

**دوره:** قراءة ملفات الإعداد ودمج الملف المحلي ومتغيرات الدخول وخصائص JVM بأولوية محددة.

**حالته:** مصدر نشط · **عدد أسطره:** 76

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `ConfigurationProperties(Path resources, Path localLogin, Function<String, String> environment, Supplier<Properties> systemProperties)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `synchronized Properties reload()` | داخل الحزمة | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `String get(String key)` | داخل الحزمة | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private String environmentOverride(String key)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void read(Path file, Properties into)` | private | قراءة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-46cb2e8c0575"></a>
## CSVReader.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/CSVReader.java](../../../src/main/java/Plugin_ERP/Utiel/CSVReader.java)

**دوره:** هيكل كلاس فارغ؛ الاسم موجود لكن لا توجد وظيفة قراءة منفذة فيه حاليًا.

**حالته:** هيكل فارغ · **عدد أسطره:** 3

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-fedb3dfc92da"></a>
## DocumentDates.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/DocumentDates.java](../../../src/main/java/Plugin_ERP/Utiel/DocumentDates.java)

**دوره:** توحيد التعامل مع تواريخ المستندات المستخدمة في السيناريوهات.

**حالته:** مصدر نشط · **عدد أسطره:** 30

**ملفات تشير إليه مباشرة:** [DocumentDatesTest.java](TEST_SuiteSupport_AR.md#file-736bb797f117)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private DocumentDates()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static LocalDate parse(String displayed)` | public | تفسير القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-2bacb4022a8d"></a>
## ExcelReader.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/ExcelReader.java](../../../src/main/java/Plugin_ERP/Utiel/ExcelReader.java)

**دوره:** هيكل كلاس فارغ؛ الاسم موجود لكن لا توجد وظيفة قراءة منفذة فيه حاليًا.

**حالته:** هيكل فارغ · **عدد أسطره:** 3

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-2c6c1e0edde0"></a>
## FileUtils.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/FileUtils.java](../../../src/main/java/Plugin_ERP/Utiel/FileUtils.java)

**دوره:** مساعدات قديمة لإنشاء مجلد وإعادة تسمية ملف؛ ليست مالك fixture journals.

**حالته:** مصدر نشط · **عدد أسطره:** 66

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void renameFile(String oldPath, String newPath)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void creatdictory(String path)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void clean(File file)` | public | تنظيف القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-9b67771be2a9"></a>
## FormInteractions.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/FormInteractions.java](../../../src/main/java/Plugin_ERP/Utiel/FormInteractions.java)

**دوره:** سياسات مشتركة للضغط والكتابة واختيار قوائم مع مطابقة وتحقق واضحين.

**حالته:** مصدر نشط · **عدد أسطره:** 109

**ملفات تشير إليه مباشرة:** [ScreenPage.java](MAIN_PAGES_Coverage_AR.md#file-12dda9e13e49)، [FinancialMovementPage.java](MAIN_PAGES_FinancialMovements_AR.md#file-191723319129)، [FinancialRecordList.java](MAIN_PAGES_FinancialMovements_AR.md#file-bd8ca5afe472)، [FinancialSaveCapture.java](MAIN_PAGES_FinancialMovements_AR.md#file-2c811c15a35a)، [SalesChoiceControls.java](MAIN_PAGES_Invoice_AR.md#file-86abb798021c)، [SalesDocumentForm.java](MAIN_PAGES_Invoice_AR.md#file-633e381a995a)، [SalesModulePage.java](MAIN_PAGES_Invoice_AR.md#file-24084c016c58)، [SalesPrintWindows.java](MAIN_PAGES_Invoice_AR.md#file-72c1e0b27bdd)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public boolean matches(String text, String name)` | public | مطابقة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public FormInteractions(WebDriverWait ready, Consumer<WebElement> scroll)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public void click(By locator)` | public | ضغط إجراء النقر، حسب المدخلات والتخصص. |
| `public void type(By locator, String value, boolean blur, InputMode mode)` | public | كتابة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void choose(By locator, String name, OptionMatch match)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public void choose(By locator, String name, OptionMatch match, String ambiguityMessage)` | public | اختيار القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-ecebb4beb31f"></a>
## LoginjsonReader.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/LoginjsonReader.java](../../../src/main/java/Plugin_ERP/Utiel/LoginjsonReader.java)

**دوره:** قراءة ملف JSON واستخراج قيمة بمسار JSONPath في الاستخدامات القديمة.

**حالته:** مصدر نشط · **عدد أسطره:** 39

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public LoginjsonReader(String jsonfilename)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public String getJsonData(String jsonpath)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-dc7bc0049ac9"></a>
## NativeCheckbox.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/NativeCheckbox.java](../../../src/main/java/Plugin_ERP/Utiel/NativeCheckbox.java)

**دوره:** التعامل مع ضبط Checkbox مع مراجعة الحالة الفعلية.

**حالته:** مصدر نشط · **عدد أسطره:** 39

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `private NativeCheckbox()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public static void set(WebDriver driver, WebElement input, boolean selected)` | public | تعيين القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-5d3abdb5513e"></a>
## OSUtils.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/OSUtils.java](../../../src/main/java/Plugin_ERP/Utiel/OSUtils.java)

**دوره:** تمييز نظام التشغيل للمسارات والأوامر الخاصة به.

**حالته:** مصدر نشط · **عدد أسطره:** 23

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static OS getOperatingSystem()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-385a7bce6e02"></a>
## PrintedInvoiceAssertions.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/PrintedInvoiceAssertions.java](../../../src/main/java/Plugin_ERP/Utiel/PrintedInvoiceAssertions.java)

**دوره:** مقارنة محتوى طباعة الفاتورة وبنودها وأرقامها بالقيم المطلوبة في السيناريو.

**حالته:** مصدر نشط · **عدد أسطره:** 66

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public record Cell(String text, double x, double y, double width, double height, String page)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public record Line(String item, BigDecimal quantity, BigDecimal price)` | public | تنفيذ جزء متعلق بـ البند، حسب المدخلات والتخصص. |
| `private PrintedInvoiceAssertions()` | private | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `private static double number(Map<String,Object> row,String key)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void verify(List<Cell> cells,String code,String party,List<Line> lines,BigDecimal net)` | public | مراجعة سياق أو عقد حسب الكلاس، مثل ثبات الفرع والعنوان. |
| `private static String label(String text)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static boolean sameRow(Cell a,Cell b)` | private | تنفيذ جزء متعلق بـ الصف، حسب المدخلات والتخصص. |
| `private static Optional<BigDecimal> money(Cell cell)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void column(List<Cell> cells,Cell row,Set<String> names,BigDecimal expected)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static void labeled(List<Cell> cells,Set<String> names,BigDecimal expected)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-94d06e1d47d8"></a>
## PropertyReader.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/PropertyReader.java](../../../src/main/java/Plugin_ERP/Utiel/PropertyReader.java)

**دوره:** واجهة طلب الإعدادات واختيار رابط البيئة وبيانات الدخول وسياسة الحسابات.

**حالته:** مصدر نشط · **عدد أسطره:** 91

**يرتبط مباشرة بملفات داخل المشروع:** [DiscountType.java](MAIN_Models_AR.md#file-2a24a7347cbc)، [TaxMode.java](MAIN_Models_AR.md#file-4601a98d3779)

**ملفات تشير إليه مباشرة:** [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [ElmentAction.java](MAIN_Bots_AR.md#file-11a21e33ca10)، [WaitActions.java](MAIN_Bots_AR.md#file-0ea40183d75b)، [WaitMangment.java](MAIN_Bots_AR.md#file-98afc8ddbc07)، [InvoiceCalculator.java](MAIN_Business_AR.md#file-e5f4eb40b205)، [AbstractDriver.java](MAIN_Drivers_AR.md#file-a1539b959b16)، [Chrome.java](MAIN_Drivers_AR.md#file-14889194f9e4)، [Edge.java](MAIN_Drivers_AR.md#file-e957d0c8ef7e)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static Properties loadProperties()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String getProperty(String key)` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String getApplicationUrl()` | public | قراءة أو إرجاع عنوان الشاشة، حسب المدخلات والتخصص. |
| `public static String getValidUsername()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String getValidPassword()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private PolicyManager()` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static TaxMode getTaxMode()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public static DiscountType getDiscountType()` | public | قراءة أو إرجاع الخصم، حسب المدخلات والتخصص. |
| `public static BigDecimal getTaxPercentage()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public static int getPrecision()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static boolean isExclusive()` | public | السؤال عن حالة القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-db7a1ec24b8d"></a>
## ScreenShotsUtils.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/ScreenShotsUtils.java](../../../src/main/java/Plugin_ERP/Utiel/ScreenShotsUtils.java)

**دوره:** التقاط صور دليل من جلسة المتصفح مع أسماء ومواقع حفظ.

**حالته:** مصدر نشط · **عدد أسطره:** 36

**يرتبط مباشرة بملفات داخل المشروع:** [LogUtils.java](MAIN_Bots_AR.md#file-8a3132f9a33b)، [AllureUtils.java](MAIN_Utiel_AR.md#file-9048c0acaaa9)

**ملفات تشير إليه مباشرة:** [TestNGListeners.java](MAIN_Listners_AR.md#file-010e5aae21a3)، [LoginTest.java](TEST_Tests_AR.md#file-3a9647b96039)

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static void takeScreenShot(WebDriver driver, String name)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-cf3e0eedcb1e"></a>
## TimeManage.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/TimeManage.java](../../../src/main/java/Plugin_ERP/Utiel/TimeManage.java)

**دوره:** توليد وقت نصي لأسماء الأدلة أو علامة زمنية مختصرة.

**حالته:** مصدر نشط · **عدد أسطره:** 22

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static String getTimestamp()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static String getSimpleTimestamp()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `public static void main(String[] args)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-0c52fa4cd7f5"></a>
## VideoConverterUtils.java

**المصدر:** [src/main/java/Plugin_ERP/Utiel/VideoConverterUtils.java](../../../src/main/java/Plugin_ERP/Utiel/VideoConverterUtils.java)

**دوره:** مساعد تحويل فيديو مولد إلى صيغة أخرى وفق أدواته المتاحة؛ ليس دليل تفعيل تسجيل كل حالة.

**حالته:** مصدر نشط · **عدد أسطره:** 41

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static File convertAviToMp4(File aviFile)` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

