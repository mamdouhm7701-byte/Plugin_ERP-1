/* شرح الملف: الاحتفاظ بصورة من إعدادات الفرع للمقارنة.
 */
package Plugin_ERP.BranchProfiles;

import static Plugin_ERP.BranchProfiles.BranchPolicies.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

public record BranchSnapshot(
        String origin,
        String branchId,
        ActivityType activityType,
        DiscountLevel discountLevel,
        TaxPolicy taxPolicy,
        StocktakingMethod stocktakingMethod) {
    public BranchSnapshot {
        origin = canonicalOrigin(origin);
        branchId = required("branchId", branchId);
        java.util.Objects.requireNonNull(activityType, "Activity is required");
        java.util.Objects.requireNonNull(discountLevel, "Discount level is required");
        java.util.Objects.requireNonNull(taxPolicy, "Tax policy is required");
        java.util.Objects.requireNonNull(stocktakingMethod, "Stocktaking method is required");
    }

    public static BranchSnapshot from(Map<String, ?> values) {
        return new BranchSnapshot(
                required("origin", values.get("origin")),
                required("branchId", values.get("branchId")),
                parse(
                        ActivityType.class,
                        "activityType",
                        required("activityType", values.get("activityType"))),
                parse(
                        DiscountLevel.class,
                        "discountLevel",
                        required("discountLevel", values.get("discountLevel"))),
                parse(TaxPolicy.class, "taxPolicy", required("taxPolicy", values.get("taxPolicy"))),
                parse(
                        StocktakingMethod.class,
                        "stocktakingMethod",
                        required("stocktakingMethod", values.get("stocktakingMethod"))));
    }

    public Map<String, String> fields() {
        var fields = new LinkedHashMap<String, String>();
        fields.put("origin", origin);
        fields.put("branchId", branchId);
        fields.put("activityType", activityType.code());
        fields.put("discountLevel", discountLevel.code());
        fields.put("taxPolicy", taxPolicy.code());
        fields.put("stocktakingMethod", stocktakingMethod.code());
        return java.util.Collections.unmodifiableMap(fields);
    }

    private static String required(String name, Object value) {
        if (!(value instanceof String || value instanceof Number)
                || value.toString().isBlank()
                || value.toString().equals("null"))
            throw new IllegalArgumentException("Missing branch profile " + name);
        return value.toString().trim();
    }

    private static String canonicalOrigin(String value) {
        try {
            URI uri = new URI(required("origin", value));
            String scheme = uri.getScheme();
            if (!("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
                    || uri.getHost() == null
                    || uri.getUserInfo() != null
                    || uri.getQuery() != null
                    || uri.getFragment() != null
                    || !(uri.getPath().isEmpty() || uri.getPath().equals("/"))) {
                throw new IllegalArgumentException(
                        "Branch profile origin must be an HTTP(S) origin without credentials, query or route");
            }
            int port = uri.getPort();
            if ((scheme.equalsIgnoreCase("http") && port == 80)
                    || (scheme.equalsIgnoreCase("https") && port == 443)) port = -1;
            return new URI(
                            scheme.toLowerCase(java.util.Locale.ROOT),
                            null,
                            uri.getHost().toLowerCase(java.util.Locale.ROOT),
                            port,
                            null,
                            null,
                            null)
                    .toString();
        } catch (URISyntaxException failure) {
            throw new IllegalArgumentException("Invalid branch profile origin", failure);
        }
    }
}
