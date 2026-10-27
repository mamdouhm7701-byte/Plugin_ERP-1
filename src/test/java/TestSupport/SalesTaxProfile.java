package TestSupport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;

/** توقع مستقل للصافي من إعداد الضريبة، لا من الصافي المحسوب في الشاشة. الأصناف هنا معفاة. */
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

    public String net(String amountAfterDiscount) {
        BigDecimal base = new BigDecimal(amountAfterDiscount);
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
