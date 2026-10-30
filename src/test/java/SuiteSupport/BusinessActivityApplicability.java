/* شرح الملف: يفصل رحلات الفواتير العامة عن رحلات شاسيه السيارات، ويذكر الحالات غير المناسبة بدل اعتبارها اختبارات ناجحة. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import java.util.*;
import org.testng.xml.*;

final class BusinessActivityApplicability {
    private BusinessActivityApplicability() {}

    static List<Map<String, Object>> apply(ActivityType activity, List<XmlSuite> suites) {
        var omitted = new ArrayList<Map<String, Object>>();
        if (activity != ActivityType.CAR_SHOWROOM) return omitted;
        for (var suite : suites) {
            for (var test : suite.getTests()) {
                var retained = new ArrayList<XmlClass>();
                for (var type : test.getXmlClasses()) {
                    if (genericInvoiceJourney(type)) {
                        omitted.add(
                                Map.of(
                                        "class",
                                        type.getName(),
                                        "methods",
                                        BusinessLevelContracts.methods(type),
                                        "reason",
                                        "غير مناسب لمعرض السيارات: الرحلة العامة تستخدم كميات قابلة للتعديل دون شاسيه. تستخدم رحلة السيارة شاسيه مستقلًا وكمية واحدة.",
                                        "replacement",
                                        "Invoice.CarShowroomSalesTest",
                                        "countsAsPassed",
                                        false));
                        System.out.println(
                                "حالة خارج نطاق نشاط المعرض: "
                                        + type.getName()
                                        + "؛ ليست نجاحًا. تفاصيل النطاق مسجلة عند تشغيل خطة البيزنس.");
                    } else retained.add(type);
                }
                test.setXmlClasses(retained);
            }
            omitted.addAll(apply(activity, suite.getChildSuites()));
        }
        return omitted;
    }

    private static boolean genericInvoiceJourney(XmlClass type) {
        if (type.getName().equals("Invoice.CarShowroomSalesTest")) return false;
        try {
            Class<?> test = Class.forName(type.getName());
            return Invoice.SalesScenarioTest.class.isAssignableFrom(test)
                    || Purches.PurchaseScenarioTest.class.isAssignableFrom(test)
                    || type.getName().equals("StoreTest.ManufacturingSalesEndToEndTest");
        } catch (ClassNotFoundException failure) {
            throw new IllegalStateException(
                    "Unknown activity scenario: " + type.getName(), failure);
        }
    }
}
