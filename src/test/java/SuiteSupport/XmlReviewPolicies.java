/* شرح الملف: يحدد سياسات مراجعة XML؛ تشغيل IntelliJ المباشر يتم من xml-interactive-testng.xml حتى تظهر الحالات في شجرة TestNG. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import java.util.List;

final class XmlReviewPolicies {
    private XmlReviewPolicies() {}

    static List<TaxPolicy> policies(boolean matrix, TaxPolicy selected) {
        return matrix
                ? List.of(TaxPolicy.NET_BASED, TaxPolicy.ITEM_BASED, TaxPolicy.PRICE_INCLUDED)
                : List.of(selected);
    }
}
