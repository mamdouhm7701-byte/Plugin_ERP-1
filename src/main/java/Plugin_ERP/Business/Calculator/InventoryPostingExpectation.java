package Plugin_ERP.Business.Calculator;

import Plugin_ERP.BranchProfiles.BranchPolicies.StocktakingMethod;
import java.math.BigDecimal;
import java.util.*;

/** Independent amounts; callers must supply the branch's explicitly verified account bindings. */
public final class InventoryPostingExpectation {
    public record Accounts(
            String counterparty,
            String sales,
            String vat,
            String additional,
            String inventory,
            String costOfSales) {
        public Accounts {
            for (String id : List.of(counterparty, sales, vat, additional, inventory, costOfSales))
                if (id.isBlank())
                    throw new IllegalArgumentException("Posting account binding is missing");
        }
    }

    public record Sale(
            BigDecimal goodsBeforeVat,
            BigDecimal additionalTax,
            BigDecimal vat,
            BigDecimal independentlySeededCost,
            boolean returned) {
        public Sale {
            for (var value : List.of(goodsBeforeVat, additionalTax, vat, independentlySeededCost))
                if (value.signum() < 0)
                    throw new IllegalArgumentException(
                            "Use the returned flag, not negative recipe amounts");
        }
    }

    private InventoryPostingExpectation() {}

    /** Positive is debit, negative is credit; equal account bindings are consolidated. */
    public static Map<String, BigDecimal> signedBalances(
            Sale sale, StocktakingMethod method, Accounts accounts) {
        Objects.requireNonNull(method);
        var balances = new LinkedHashMap<String, BigDecimal>();
        add(
                balances,
                accounts.counterparty(),
                sale.goodsBeforeVat().add(sale.additionalTax()).add(sale.vat()));
        add(balances, accounts.sales(), sale.goodsBeforeVat().negate());
        add(balances, accounts.additional(), sale.additionalTax().negate());
        add(balances, accounts.vat(), sale.vat().negate());
        if (method == StocktakingMethod.CONTINUOUS) {
            add(balances, accounts.costOfSales(), sale.independentlySeededCost());
            add(balances, accounts.inventory(), sale.independentlySeededCost().negate());
        }
        if (sale.returned()) balances.replaceAll((id, amount) -> amount.negate());
        balances.entrySet().removeIf(e -> e.getValue().signum() == 0);
        return Collections.unmodifiableMap(balances);
    }

    private static void add(Map<String, BigDecimal> balances, String account, BigDecimal amount) {
        balances.merge(account, amount, BigDecimal::add);
    }
}
