package Purches;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.*;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;

/** Known arithmetic examples; independent of tenant settings and browser totals. */
public final class PurchaseCalculationTest {
  private static List<InvoiceRow> row(String quantity,String price){return List.of(new InvoiceRow("QA-SL-item",new BigDecimal(quantity),new BigDecimal(price)));}
  private static void check(List<InvoiceRow> rows,String discount,DiscountType discountType,String rate,TaxMode mode,int precision,String total,String discountValue,String beforeTax,String tax,String net){
    var result=InvoiceCalculator.calculate(rows,new BigDecimal(discount),discountType,new BigDecimal(rate),mode,precision);
    BigDecimal[] actual={result.getTotal(),result.getDiscount(),result.getBeforeTax(),result.getTax(),result.getNet()};
    String[] expected={total,discountValue,beforeTax,tax,net};
    for(int i=0;i<actual.length;i++)Assert.assertEquals(actual[i].compareTo(new BigDecimal(expected[i])),0,"Amount "+i+": "+result);
    Assert.assertEquals(result.getBeforeTax().add(result.getTax()).compareTo(result.getNet()),0);
  }
  @Test @Story("حساب ثلاثة بنود بسعر مائة دون ضريبة بصافي ثلاثمائة") public void untaxedInvoice(){check(row("3","100"),"0",DiscountType.VALUE,"0",TaxMode.EXCLUSIVE,2,"300","0","300","0","300");}
  @Test @Story("حساب ضريبة مضافة بنسبة خمسة عشر بالمائة بصافي ثلاثمائة وخمسة وأربعين") public void exclusiveVat(){check(row("3","100"),"0",DiscountType.VALUE,"15",TaxMode.EXCLUSIVE,2,"300","0","300","45","345");}
  @Test @Story("استخراج الضريبة من سعر شامل لها دون زيادة الصافي") public void inclusiveVat(){check(row("3","115"),"0",DiscountType.VALUE,"15",TaxMode.INCLUSIVE,2,"345","0","300","45","345");}
  @Test @Story("تطبيق خصم عشرة بالمائة قبل حساب الضريبة المضافة") public void percentageDiscountBeforeExclusiveVat(){check(row("3","100"),"10",DiscountType.PERCENTAGE,"15",TaxMode.EXCLUSIVE,2,"300","30","270","40.50","310.50");}
  @Test @Story("تطبيق خصم بالقيمة على سعر شامل الضريبة") public void fixedDiscountWithInclusiveVat(){check(row("3","115"),"25",DiscountType.VALUE,"15",TaxMode.INCLUSIVE,2,"345","25","278.26","41.74","320");}
  @Test @Story("حساب خصم بالقيمة على كمية كسرية دون ضريبة") public void fixedDiscountWithoutVat(){check(row("1.5","100"),"10",DiscountType.VALUE,"0",TaxMode.EXCLUSIVE,2,"150","10","140","0","140");}
  @Test @Story("خصم كامل بالنسبة يجعل الوعاء والضريبة والصافي صفراً") public void fullPercentageDiscount(){check(row("2","100"),"100",DiscountType.PERCENTAGE,"15",TaxMode.EXCLUSIVE,2,"200","200","0","0","0");}
  @Test @Story("خصم كامل بالقيمة يجعل الوعاء والضريبة والصافي صفراً") public void fullFixedDiscount(){check(row("1","100"),"100",DiscountType.VALUE,"15",TaxMode.INCLUSIVE,2,"100","100","0","0","0");}
  @Test @Story("تقريب الكمية الكسرية والسعر والضريبة إلى منزلتين") public void fractionalQuantityAndRounding(){check(row("1.25","100.01"),"0",DiscountType.VALUE,"5",TaxMode.EXCLUSIVE,2,"125.01","0","125.01","6.25","131.26");}
  @Test @Story("تقريب ضريبة مجموع البنود الصغيرة بدلاً من تضخيمها") public void aggregateTaxRounding(){check(List.of(new InvoiceRow("A",BigDecimal.ONE,new BigDecimal("0.01")),new InvoiceRow("B",BigDecimal.ONE,new BigDecimal("0.01")),new InvoiceRow("C",BigDecimal.ONE,new BigDecimal("0.01"))),"0",DiscountType.VALUE,"15",TaxMode.EXCLUSIVE,2,"0.03","0","0.03","0","0.03");}
  @Test @Story("تقريب السعر الشامل للضريبة إلى ثلاث منازل عشرية") public void threeDecimalInclusiveRounding(){check(row("1","115.005"),"0",DiscountType.VALUE,"15",TaxMode.INCLUSIVE,3,"115.005","0","100.004","15.001","115.005");}
  @Test @Story("الحساب بدون بنود يعيد جميع الإجماليات صفراً") public void emptyCalculationIsZero(){check(List.of(),"0",DiscountType.VALUE,"15",TaxMode.EXCLUSIVE,2,"0","0","0","0","0");}
}
