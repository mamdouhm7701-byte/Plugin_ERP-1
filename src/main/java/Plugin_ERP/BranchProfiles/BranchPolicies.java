/* شرح الملف: تجميع اختيارات النشاط والخصم والضريبة والجرد المتاحة للفرع.
 */
package Plugin_ERP.BranchProfiles;

public final class BranchPolicies {
    private BranchPolicies() {}

    public interface CodedPolicy {
        String code();
    }

    public enum ActivityType implements CodedPolicy {
        PUBLIC("Public"),
        RESTAURANT("Restaurant"),
        CONSTRUCTION("Construction"),
        CONCRETE("Concrete"),
        CAR_SHOWROOM("CarShowroom");
        private final String code;

        ActivityType(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    public enum DiscountLevel implements CodedPolicy {
        TRANSACTION("Transaction"),
        ITEMS("Items");
        private final String code;

        DiscountLevel(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    public enum TaxPolicy implements CodedPolicy {
        NET_BASED("NetBased"),
        ITEM_BASED("ItemBased"),
        PRICE_INCLUDED("PriceIncluded");
        private final String code;

        TaxPolicy(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    public enum StocktakingMethod implements CodedPolicy {
        PERIODIC("Periodic"),
        CONTINUOUS("Continuous");
        private final String code;

        StocktakingMethod(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    static <E extends Enum<E> & CodedPolicy> E parse(Class<E> type, String field, String code) {
        for (E candidate : type.getEnumConstants()) {
            if (candidate.code().equals(code)) return candidate;
        }
        throw new IllegalArgumentException("Unknown branch profile " + field + ": " + code);
    }
}
