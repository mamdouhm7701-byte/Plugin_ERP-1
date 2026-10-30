/* شرح الملف: تطبيق اختيار النشاط والإعدادات على الحزمة المطلوبة.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.Util.PropertyReader;
import java.util.*;
import org.testng.IAlterSuiteListener;
import org.testng.xml.*;

public class BusinessLevelSuitePolicy implements IAlterSuiteListener {
    Optional<BusinessRunRequest> selectedBusiness() {
        return BranchProfiles.selected()
                .map(
                        profile -> {
                            var expected = profile.expected();
                            String additional =
                                    PropertyReader.getProperty("business.additionalTax");
                            if (expected.activityType() == ActivityType.RESTAURANT
                                    && !Set.of("true", "false")
                                            .contains(Objects.toString(additional, "")))
                                throw new IllegalStateException(
                                        "Selected restaurant profile requires business.additionalTax=true or false; use ERP - Business run to prepare it automatically");
                            return new BusinessRunRequest(
                                    expected.activityType(),
                                    expected.discountLevel(),
                                    expected.taxPolicy(),
                                    expected.stocktakingMethod(),
                                    Boolean.parseBoolean(additional));
                        });
    }

    @Override
    public void alter(List<XmlSuite> suites) {
        var selected = selectedBusiness();
        if (selected.isEmpty()) return;
        BusinessActivityApplicability.apply(selected.orElseThrow().activity(), suites);
        for (var original : new ArrayList<>(suites)) {
            String level = original.getParameter("business.level");
            if (level == null) continue;
            var additions = BusinessLevelContracts.missing(selected.orElseThrow(), level, suites);
            if (additions.isEmpty()) continue;
            var business = new XmlSuite();
            business.setName("Selected business / " + level);
            business.setParallel(XmlSuite.ParallelMode.NONE);
            business.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
            var test = new XmlTest(business);
            test.setName("Activity, policies and additional tax");
            test.setXmlClasses(additions);
            suites.add(business);
        }
    }
}
