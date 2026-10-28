/* شرح الملف: مطابقة القيد المحفوظ مع الحسابات والمبالغ المتوقعة.
 */
package TestSupport;

import java.math.BigDecimal;
import java.util.*;

public final class OwnedPostingReconciliation {
    public record Entry(String sourceId, String accountId, BigDecimal debit, BigDecimal credit) {
        public Entry {
            Objects.requireNonNull(sourceId);
            Objects.requireNonNull(accountId);
            if (debit == null
                    || credit == null
                    || debit.signum() < 0
                    || credit.signum() < 0
                    || (debit.signum() > 0 && credit.signum() > 0))
                throw new IllegalArgumentException("Invalid posting side amounts");
        }
    }

    private OwnedPostingReconciliation() {}

    public static void verify(
            String ownedSourceId, Map<String, BigDecimal> expected, List<Entry> actual) {
        if (ownedSourceId == null
                || ownedSourceId.isBlank()
                || expected.isEmpty()
                || actual.isEmpty())
            throw new AssertionError("Owned posting recipe and persisted entries are required");
        var observed = new TreeMap<String, BigDecimal>();
        for (var entry : actual) {
            if (!ownedSourceId.equals(entry.sourceId()))
                throw new AssertionError("Posting belongs to another source");
            observed.merge(
                    entry.accountId(), entry.debit().subtract(entry.credit()), BigDecimal::add);
        }
        observed.entrySet().removeIf(e -> e.getValue().signum() == 0);
        if (!observed.keySet().equals(expected.keySet()))
            throw new AssertionError("Posting account set differs from the verified bindings");
        for (var entry : expected.entrySet())
            if (observed.get(entry.getKey()).compareTo(entry.getValue()) != 0)
                throw new AssertionError(
                        "Posting differs for account "
                                + entry.getKey()
                                + ": expected "
                                + entry.getValue()
                                + ", actual "
                                + observed.get(entry.getKey()));
        if (observed.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).signum() != 0)
            throw new AssertionError("Owned posting is not balanced");
    }
}
