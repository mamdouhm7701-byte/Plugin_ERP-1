package Plugin_ERP.Utiel;

import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;

/** Associates report values with their printed row/column or label, never a page-wide number. */
public final class PrintedInvoiceAssertions {
    public record Cell(String text, double x, double y, double width, double height, String page) {}
    public record Line(String item, BigDecimal quantity, BigDecimal price) {}
    private PrintedInvoiceAssertions() {}

    @SuppressWarnings("unchecked") public static void invoice(WebDriver driver, String code, String party,
            List<Line> lines, BigDecimal net) {
        List<Map<String,Object>> raw=(List<Map<String,Object>>)((JavascriptExecutor)driver).executeScript("""
            return [...document.querySelectorAll('.e-reportviewer-paragrap,.e-reportviewer-paragrap *')]
              .filter(e=>e.children.length===0&&e.getClientRects().length&&e.innerText.trim())
              .map(e=>{const r=e.getBoundingClientRect();return {text:e.innerText.trim(),x:r.x,y:r.y,
                width:r.width,height:r.height,page:e.closest('[id*="_pageDiv"],[id*="_pageContainer"]')?.id||'document'};});
            """);
        io.qameta.allure.Allure.addAttachment("Printed invoice cells","application/json",org.json.simple.JSONValue.toJSONString(raw),".json");
        var cells=raw.stream().map(c->new Cell(c.get("text").toString(),number(c,"x"),number(c,"y"),
                number(c,"width"),number(c,"height"),c.get("page").toString())).toList();
        verify(cells,code,party,lines,net);
    }
    private static double number(Map<String,Object> row,String key) { return ((Number)row.get(key)).doubleValue(); }
    public static void verify(List<Cell> cells,String code,String party,List<Line> lines,BigDecimal net) {
        if(cells.stream().noneMatch(c->c.text().contains(party))) throw new AssertionError("Printed party differs: "+party);
        labeled(cells,Set.of("رقم الفاتورة","رقم المستند","الكود","رقم","INVOICE NO"),new BigDecimal(code));
        for(Line line:lines) {
            var rows=cells.stream().filter(c->c.text().equals(line.item())).toList();
            if(rows.size()!=1)throw new AssertionError("Expected one printed item row: "+line.item()+"; found "+rows.size());
            Cell row=rows.getFirst();
            column(cells,row,Set.of("الكمية","الكميه"),line.quantity());
            column(cells,row,Set.of("السعر","سعر الوحدة","سعر البيع","سعر الشراء"),line.price());
        }
        labeled(cells,Set.of("الصافي","الصافى","صافي الفاتورة","صافى الفاتورة","الإجمالي النهائي","صافي المبلغ","الصافي بعد الضريبة"),net);
    }
    private static String label(String text) { return text.replace(":","").replace("：","").trim(); }
    private static boolean sameRow(Cell a,Cell b) {
        return a.page().equals(b.page())&&Math.abs((a.y()+a.height()/2)-(b.y()+b.height()/2))<=Math.max(4,Math.max(a.height(),b.height())*.65);
    }
    private static Optional<BigDecimal> money(Cell cell) {
        try{return Optional.of(new BigDecimal(new Plugin_ERP.Bots.ValidationActions(null,null,null).normalizePrice(cell.text())));}
        catch(NumberFormatException e){return Optional.empty();}
    }
    private static void column(List<Cell> cells,Cell row,Set<String> names,BigDecimal expected) {
        var headings=cells.stream().filter(c->c.page().equals(row.page())&&c.y()<row.y()&&names.contains(label(c.text())))
                .sorted(Comparator.comparingDouble(Cell::y).reversed()).toList();
        if(headings.isEmpty())throw new AssertionError("Missing printed column "+names);
        Cell heading=headings.getFirst();
        var values=cells.stream().filter(c->sameRow(c,row)&&money(c).isPresent())
                .filter(c->Math.abs((c.x()+c.width()/2)-(heading.x()+heading.width()/2))<=Math.max(heading.width(),c.width())*.6)
                .toList();
        if(values.size()!=1||money(values.getFirst()).orElseThrow().compareTo(expected)!=0)
            throw new AssertionError("Printed "+names+" differs on item "+row.text()+": expected "+expected+", cells="+values);
    }
    private static void labeled(List<Cell> cells,Set<String> names,BigDecimal expected) {
        for(Cell label:cells) if(names.contains(label(label.text()))) {
            var candidates=cells.stream().filter(c->sameRow(c,label)&&money(c).isPresent())
                    .sorted(Comparator.comparingDouble(c->Math.abs((c.x()+c.width()/2)-(label.x()+label.width()/2)))).toList();
            if(!candidates.isEmpty()&&money(candidates.getFirst()).orElseThrow().compareTo(expected)==0)return;
        }
        throw new AssertionError("Printed labeled value "+names+" differs: expected "+expected);
    }
}
