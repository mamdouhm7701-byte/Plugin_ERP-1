/* شرح الملف: ربط مقارنة التشغيلات بنفس التننت والنشاط وسياسات الفرع بدل خلط نتائج إعدادات مختلفة.
 * فائدته: يمنع اعتبار نجاح إعداد مختلف تأكيدًا لإصلاح الحالة السابقة.
 */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.BranchProfiles.BranchProfiles;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.model.*;
import java.util.TreeMap;

public final class FailureReviewScope {
    private FailureReviewScope() {}

    public static void apply(TestResult test) {
        String scope;
        try {
            scope =
                    BranchProfiles.selected()
                            .map(
                                    profile -> {
                                        var fields = new TreeMap<>(profile.expected().fields());
                                        fields.remove("branchId");
                                        return fields.toString();
                                    })
                            .orElseGet(() -> "legacy:" + PropertyReader.getApplicationUrl());
            scope += ";additional=" + System.getProperty("business.additionalTax", "unspecified");
        } catch (RuntimeException failure) {
            Plugin_ERP.Bots.LogUtils.warn(
                    "Review scope unavailable; cross-run comparison disabled for this run.");
            scope =
                    "unverified:"
                            + System.getProperty(
                                    "allure.run.id", java.util.UUID.randomUUID().toString());
        }
        test.getLabels().removeIf(label -> label.getName().equals("review.scope"));
        test.getLabels().add(new Label().setName("review.scope").setValue(scope));
    }
}
