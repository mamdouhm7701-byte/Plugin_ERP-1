package Plugin_ERP.BranchProfiles;

import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.util.*;

/** Business choices are independent of branch identity and never modify shared properties. */
public record BusinessRunRequest(
        ActivityType activity,
        DiscountLevel discount,
        TaxPolicy tax,
        StocktakingMethod stocktaking,
        boolean additionalTax) {
    public BusinessRunRequest {
        Objects.requireNonNull(activity);
        Objects.requireNonNull(discount);
        Objects.requireNonNull(tax);
        Objects.requireNonNull(stocktaking);
        if (additionalTax && activity != ActivityType.RESTAURANT)
            throw new IllegalArgumentException("Additional tax is only available for restaurants");
    }

    public Map<String, Object> fields() {
        return Map.of(
                "activityType",
                activity.code(),
                "discountLevel",
                discount.code(),
                "taxPolicy",
                tax.code(),
                "stocktakingMethod",
                stocktaking.code(),
                "enableAdditionalTax",
                additionalTax,
                "additionalTaxPercentage",
                additionalTax ? 100 : 0);
    }

    public boolean matches(BranchSnapshot snapshot) {
        return activity == snapshot.activityType()
                && discount == snapshot.discountLevel()
                && tax == snapshot.taxPolicy()
                && stocktaking == snapshot.stocktakingMethod();
    }

    public void verify(Map<String, ?> stored, String origin) {
        var values = new HashMap<String, Object>(stored);
        values.put("origin", origin);
        values.put("branchId", stored.get("id"));
        if (!matches(BranchSnapshot.from(values)))
            throw new IllegalStateException(
                    "Persisted branch policies do not match the requested business");
        boolean enabled = Boolean.TRUE.equals(stored.get("enableAdditionalTax"));
        if (enabled != additionalTax
                || (enabled
                        && new java.math.BigDecimal(
                                                Objects.toString(
                                                        stored.get("additionalTaxPercentage")))
                                        .compareTo(new java.math.BigDecimal("100"))
                                != 0))
            throw new IllegalStateException(
                    "Persisted additional-tax configuration does not match the request");
    }
}
