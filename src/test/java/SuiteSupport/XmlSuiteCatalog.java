/* شرح الملف: يعرض ملفات تشغيل الواجهة مع وصف محتواها ويمنع اختيار ملفات داخلية أو خارج المشروع. */
package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import org.testng.xml.*;
import org.testng.xml.internal.Parser;

final class XmlSuiteCatalog {
    static final String INVOICE_REVIEW = "invoice-tax-review-testng.xml";

    record Entry(String path, String description) {
        public String toString() {
            return path;
        }
    }

    private XmlSuiteCatalog() {}

    static List<Entry> load(Path project) {
        Path resources = project.toAbsolutePath().normalize().resolve("src/test/resources");
        try (var paths = Files.walk(resources)) {
            return paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith("testng.xml"))
                    .map(resources::relativize)
                    .map(p -> p.toString().replace('\\', '/'))
                    .filter(XmlSuiteCatalog::selectable)
                    .sorted()
                    .map(p -> new Entry(p, describe(resources.resolve(p), p)))
                    .toList();
        } catch (Exception failure) {
            throw new IllegalStateException("تعذر قراءة ملفات التشغيل", failure);
        }
    }

    private static boolean selectable(String path) {
        return !path.contains("/internal/")
                && !path.contains("/legacy/")
                && !path.contains("interactive")
                && !path.equals("ci-testng.xml")
                && !path.contains("refactor")
                && !path.contains("fixture-lifecycle")
                && !path.contains("browser-isolation");
    }

    private static String describe(Path file, String path) {
        if (path.equals(INVOICE_REVIEW))
            return "مراجعة فواتير المبيعات ونقاط البيع والمطعم والمشتريات: إضافة وتعديل وحذف وطباعة ومقارنة الضريبة والصافي. المبيعات العادية: التحقق من منع التعديل والحذف بعد الحفظ وفق السلوك الحالي. يعمل تلقائيًا على السياسات الثلاث، بفرع مطاعم جديد لكل سياسة. يراجع الإعفاء تحت ضريبة الأصناف. الإضافية حسب اختيارك. التشغيل متتابع لحماية إعدادات الفرع.";
        try {
            var suites = new Parser(file.toString()).parseToList();
            var names = new ArrayList<String>();
            var classes = new ArrayList<String>();
            collect(suites, names, classes);
            String purpose =
                    switch (path) {
                        case "smoke-testng.xml" -> "فحص سريع لأهم خطوات التشغيل.";
                        case "sanity-testng.xml" -> "مراجعة أساسية أوسع للتأكد من سلامة الوظائف.";
                        case "e2e-testng.xml" -> "رحلات عمل مترابطة من البداية للنهاية.";
                        case "regression-testng.xml" ->
                                "مراجعة موسعة للموديولات؛ قد تستغرق عدة ساعات.";
                        case "sales-testng.xml" ->
                                "فواتير المبيعات والقوائم والمرتجعات والحركات المرتبطة بها.";
                        case "purchases-testng.xml" -> "فواتير وأوامر ومرتجعات المشتريات.";
                        case "special-sales-testng.xml" ->
                                "المطاعم ونقاط البيع والحالات الخاصة بهما.";
                        case "store-testng.xml" -> "المخازن وحركات المخزون.";
                        case "products-testng.xml" -> "بيانات الأصناف ووحداتها وأسعارها.";
                        case "basic-data-testng.xml" ->
                                "البيانات الأساسية مثل المناطق والمندوبين والخزن.";
                        case "dealer-model-testng.xml" ->
                                "بيانات العملاء والموردين وتصنيفاتهم وإضافة السجلات وتعديلها وحذفها.";
                        case "financial-movements-testng.xml" ->
                                "القبض والصرف والتحويلات المالية والتحقق من الأرصدة.";
                        case "general-accounting-testng.xml" ->
                                "شاشات الحسابات العامة وشجرة الحسابات ومراكز التكلفة.";
                        case "login-testng.xml" ->
                                "تسجيل الدخول والرسائل المعروضة للبيانات الصحيحة والخاطئة.";
                        case "report-navigation-testng.xml" ->
                                "فتح شاشات التقارير والتأكد من جاهزيتها؛ لا يثبت صحة كل حساباتها.";
                        case "coverage-expansion-testng.xml" ->
                                "حالات إضافية لتوسيع تغطية الشاشات؛ راجع قائمة الحالات التالية لمعرفة نطاقها.";
                        case "coverage-observations-testng.xml" ->
                                "مراجعة الملاحظات المسجلة أثناء استكمال تغطية الشاشات.";
                        case "coverage-recovery-testng.xml" ->
                                "استرجاع وتنظيف بيانات الاختبارات المملوكة المتبقية؛ قد يحذف بيانات تجارب سابقة تخص الإطار.";
                        case "coverage-verification-testng.xml" ->
                                "مراجعة حالات التغطية التي تحتاج تحققًا مستقلًا.";
                        default -> standaloneDescription(path, names);
                    };
            return purpose
                    + "\nالكلاسات الموجودة: "
                    + String.join("، ", classes)
                    + "\nيستخدم سياسة الضريبة والنشاط المختارين فقط. بعض الملفات تتطلب نشاطًا أو إعدادات محددة؛ راجع أسماء الحالات أدناه وملف XML. لا يغيّر السياسات الثلاث تلقائيًا.";
        } catch (Exception failure) {
            throw new IllegalStateException("تعذر وصف ملف التشغيل: " + path, failure);
        }
    }

    private static String standaloneDescription(String path, List<String> names) {
        String name = Path.of(path).getFileName().toString();
        return switch (name) {
            case "restaurant-testng.xml" ->
                    "فاتورة المطعم: أصناف وكميات وتعليق ودفع وتعديل الطلب المعلق وحذف وطباعة.";
            case "point-of-sale-testng.xml" ->
                    "نقاط البيع: حفظ نقدي وآجل وتعديل الكمية وحذف وطباعة ومراجعة المخزون.";
            case "smoke-restaurant-additional-testng.xml" ->
                    "فحص سريع لحساب ضريبة المطعم الإضافية؛ يحتاج تفعيلها في إعدادات الفرع.";
            case "smoke-restaurant-item-tax-testng.xml" ->
                    "إضافية المطعم مع سياسة ضريبة الأصناف والإعفاء؛ يحتاج إعدادات المطعم المطابقة.";
            case "purchase-smoke-testng.xml" ->
                    "حفظ مشتريات أساسية والتحقق من بياناتها وأثرها على المخزون.";
            case "purchase-calendar-smoke-testng.xml" ->
                    "اختيار تواريخ مستندات المشتريات من التقويم.";
            case "products-finance-interactions-smoke-testng.xml" ->
                    "تفاعل حقول الأصناف والحركات المالية والتحقق من الإدخال.";
            case "module-lifecycle-smoke-testng.xml" ->
                    "دورة مبسطة لإنشاء سجلات وتعديلها وحذف بيانات الاختبار.";
            case "form-interactions-smoke-testng.xml" ->
                    "حقول النماذج والقوائم واختيار القيم والتحقق من استجابتها.";
            case "construction-contract-testng.xml" ->
                    "العقود: إنشاء وتعديل بنود وتحويل كلي أو جزئي إلى فاتورة ومراجعة المتبقي.";
            case "concrete-stock-testng.xml" ->
                    "الخرسانة: حجز وتسليم وفواتير ومراجعة خصم المخزون عند إذن التسليم.";
            case "concrete-reservation-testng.xml" ->
                    "حجز الخرسانة ومراجعة ظهوره في عمليات التسليم والتقارير.";
            case "car-showroom-business-testng.xml" ->
                    "معرض السيارات: بيع شاسيه محدد ومراجعة الفاتورة والبيانات المرتبطة.";
            case "branch-profile-sales-contract-testng.xml" ->
                    "مقارنة حساب فاتورة البيع بسياسة الضريبة والخصم المختارين للفرع.";
            case "branch-policy-drafts-testng.xml" ->
                    "حساب مسودات حسب سياسة الفرع؛ المسودة لا تحفظ فاتورة فعلية.";
            case "activity-screen-access-testng.xml" ->
                    "التأكد من إمكانية فتح شاشات النشاط المختار؛ لا يغطي دورة عمل كل شاشة.";
            case "activity-master-lifecycle-testng.xml" ->
                    "إضافة وتعديل وحذف البيانات الأساسية الخاصة بالنشاط.";
            case "activity-business-screens-testng.xml" ->
                    "حالات أعمال الشاشات الخاصة بالنشاط، وفق الحالات المذكورة في الملف.";
            case "allure-journey-smoke-testng.xml" ->
                    "تجربة رحلة مختصرة ومراجعة ظهور خطواتها وأدلتها في التقرير.";
            case "acceptance-testng.xml" -> "حالات قبول مختارة لمراجعة سلوك الشاشات والحسابات.";
            case "parallel-three-screens-testng.xml", "parallel-three-pilot-testng.xml" ->
                    "تجربة توزيع شاشات مستقلة؛ من شاشة XML الجديدة تنفذ بالتتابع لحماية الإعدادات.";
            case "report-navigation-parallel-testng.xml" ->
                    "فتح شاشات التقارير؛ من شاشة XML الجديدة تنفذ بالتتابع.";
            default -> "مجموعة: " + String.join("، ", names);
        };
    }

    private static void collect(List<XmlSuite> suites, List<String> names, List<String> classes) {
        for (var suite : suites) {
            names.add(suite.getName());
            for (var test : suite.getTests())
                for (var type : test.getXmlClasses())
                    classes.add(
                            type.getName()
                                    + (type.getIncludedMethods().isEmpty()
                                            ? ""
                                            : " "
                                                    + type.getIncludedMethods().stream()
                                                            .map(XmlInclude::getName)
                                                            .toList()));
            collect(suite.getChildSuites(), names, classes);
        }
    }

    static List<XmlSuite> plan(Path project, String selected, int workers, Path output)
            throws Exception {
        if (workers < 1 || workers > 6) throw new IllegalArgumentException("Choose 1–6 browsers");
        var entry =
                load(project).stream()
                        .filter(e -> e.path().equals(selected))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "ملف التشغيل غير موجود في القائمة"));
        Path file =
                project.toAbsolutePath()
                        .normalize()
                        .resolve("src/test/resources")
                        .resolve(entry.path());
        var suites = new Parser(file.toString()).parseToList();
        // Preserve parameters, method filters, suite listeners and ordering. XML runs are
        // sequential initially.
        sequential(suites);
        Files.createDirectories(output);
        Files.writeString(
                output.resolve("xml-selection.json"),
                new org.json.JSONObject(
                                Map.of(
                                        "xml",
                                        entry.path(),
                                        "description",
                                        entry.description(),
                                        "requestedBrowsers",
                                        workers,
                                        "selectedBrowsers",
                                        1,
                                        "reason",
                                        "XML review preserves ordering and shared settings"))
                        .toString(2));
        return suites;
    }

    private static void sequential(List<XmlSuite> suites) {
        for (var suite : suites) {
            suite.setParallel(XmlSuite.ParallelMode.NONE);
            suite.setThreadCount(1);
            suite.setDataProviderThreadCount(1);
            for (var test : suite.getTests()) {
                test.setParallel(XmlSuite.ParallelMode.NONE);
                test.setThreadCount(1);
            }
            sequential(suite.getChildSuites());
        }
    }
}
