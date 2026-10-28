/* شرح الملف: قراءة طريقة حساب ضريبة البيع المستخدمة في التجربة.
 */
package TestSupport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;

public record SalesTaxProfile(String policy, List<BigDecimal> rates) {
    @SuppressWarnings("unchecked")
    public static SalesTaxProfile capture(WebDriver driver) {
        var config =
                (Map<String, Object>)
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                """
            const root=erpVue.root();
            const queue=[root],seen=new Set();
            while(queue.length){const vm=queue.shift();if(!vm||seen.has(vm))continue;seen.add(vm);
              if(vm.selectedItem&&Array.isArray(vm.companyTax)&&vm.currentBranch?.taxPolicy)
                return {policy:vm.currentBranch.taxPolicy,rates:vm.companyTax.map(t=>String(t.taxPercentage))};
              queue.push(...(erpVue.children(vm)||[]));}
            throw new Error('Sales tax configuration is not ready');
            """);
        return new SalesTaxProfile(
                config.get("policy").toString(),
                ((List<Object>) config.get("rates"))
                        .stream().map(v -> new BigDecimal(v.toString())).toList());
    }

    /**
     * Explicit treatment for taxable or exempt positive sales recipes under the confirmed 15%
     * contract.
     */
    public String net(String amountAfterDiscount, boolean taxExempt) {
        if (rates.size() != 1 || rates.getFirst().compareTo(new BigDecimal("15")) != 0)
            throw new IllegalStateException(
                    "Confirmed item-tax contract requires exactly one 15% rate");
        var selected =
                java.util.Arrays.stream(Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy.values())
                        .filter(value -> value.code().equalsIgnoreCase(policy))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Unsupported tax policy: " + policy));
        var line =
                new Plugin_ERP.Business.Calculator.PolicyInvoiceLine(
                        "fixture",
                        BigDecimal.ONE,
                        new BigDecimal(amountAfterDiscount),
                        BigDecimal.ZERO,
                        Plugin_ERP.Models.DiscountType.VALUE,
                        taxExempt);
        return Plugin_ERP.Business.Calculator.BranchInvoiceCalculator.calculate(
                        List.of(line),
                        selected,
                        2,
                        Plugin_ERP.Business.Calculator.BranchInvoiceCalculator.TaxRounding.INVOICE)
                .getNet()
                .toPlainString();
    }

    public String net(String amountAfterDiscount) {
        BigDecimal base = new BigDecimal(amountAfterDiscount);
        if (base.signum() >= 0
                && rates.size() == 1
                && rates.getFirst().compareTo(new BigDecimal("15")) == 0)
            return net(amountAfterDiscount, true);
        return switch (policy.toLowerCase(java.util.Locale.ROOT)) {
            case "netbased" ->
                    base.add(
                                    rates.stream()
                                            .map(
                                                    rate ->
                                                            base.multiply(rate)
                                                                    .movePointLeft(2)
                                                                    .setScale(
                                                                            2,
                                                                            RoundingMode.HALF_UP))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                            .toPlainString();
            case "priceincluded", "itembased" -> base.toPlainString();
            default -> throw new AssertionError("Unsupported tax policy: " + policy);
        };
    }
}
