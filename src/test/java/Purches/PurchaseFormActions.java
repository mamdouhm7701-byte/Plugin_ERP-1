package Purches;
import java.util.*;
import org.testng.Assert;

/** Reusable scenarios; UI operations are implemented by PurchaseModulePage. */
public abstract class PurchaseFormActions extends PurchaseScenarioTest {
 protected void picker(){create("picker","Credit",false);}
 protected void barcode(){create("barcode","Credit",false);}
 protected void name(){create("name","Credit",false);}
 protected void cash(){create("picker","Cash",false);}
 protected void print(){create("picker","Credit",true);}
 protected void empty(){prepare("Credit");rejectSave();}
 protected void invalidQuantity(String quantity){prepare("Credit");fill("picker");page.puQuantity(item.name(),quantity);rejectSave();}
 protected void invalidPrice(){prepare("Credit");fill("picker");page.puSetLine(item.name(),kind()==PurchaseDocumentKind.INVOICE?"سعر الشراء":"السعر","-1");rejectSave();}
 protected void missing(String label){prepare("Credit");fill("picker");if(page.puCanClearChoice(label)){page.puClearChoice(label);rejectSave();}else{Assert.assertFalse(page.puChoiceEmpty(label));Assert.assertTrue(page.puSaves().isEmpty());assertStock("0");}}
 protected void fractional(){prepare("Credit");fill("picker");page.puQuantity(item.name(),"1.25");page.puWaitNet("125");var saved=save(false);sameNumber(firstLine(data.read(saved)).get("quantity"),"1.25");assertStock("1.25");}
 protected void discount(boolean percent){prepare("Credit");fill("picker");page.puDiscount(percent?"10":"15",percent);String net=percent?"270":"285";page.puWaitNet(net);var saved=save(false);sameNumber(data.read(saved).get("net"),net);assertStock("3");}
 protected void notes(){prepare("Credit");String note=marker+" ملاحظات مشتريات — Purchase 123";page.puNotes(note);fill("picker");var saved=save(false);Assert.assertEquals(data.read(saved).get("notes"),note);}
 protected void fresh(){prepare("Credit");fill("picker");page.puNew();Assert.assertTrue(((List<?>)page.puDocument().get("lines")).isEmpty());Assert.assertTrue(page.puSaves().isEmpty());assertStock("0");}
 protected void remove(){prepare("Credit");fill("picker");var second=ownItem(2);add(second,"picker");page.puRemoveLine(second.name());page.puWaitNet("300");var saved=save(false);Assert.assertEquals(((List<?>)data.read(saved).get("lines")).size(),1);Assert.assertEquals(firstLine(data.read(saved)).get("itemId"),item.id());assertStock("3");}
 protected void multiple(){prepare("Credit");fill("picker");var second=ownItem(2);var before=number(data.balance(second,1));add(second,"picker");page.puSetLine(second.name(),kind()==PurchaseDocumentKind.INVOICE?"سعر الشراء":"السعر","100");page.puWaitNet("400");var saved=save(false);var lines=(List<?>)data.read(saved).get("lines");Assert.assertEquals(lines.size(),2);assertStock("3");sameNumber(data.balance(second,1),before.add(kind()==PurchaseDocumentKind.INVOICE?number("1"):kind()==PurchaseDocumentKind.RETURN?number("-1"):number("0")).toPlainString());}
 protected void reopen(){var saved=create("picker","Credit",false);page.puEdit(documentKind().route,saved.id(),marker,item.name());var loaded=page.puDocument();Assert.assertEquals(loaded.get("dealerId"),supplier.id());Assert.assertEquals(loaded.get("storeId"),data.store(1).id());Assert.assertEquals(firstLine(loaded).get("itemId"),item.id());sameNumber(firstLine(loaded).get("quantity"),"3");}
 protected void edit(){var saved=create("picker","Credit",false);page.puEdit(documentKind().route,saved.id(),marker,item.name());page.puCapture(marker,data.store(1).id(),item.id(),documentKind().endpoint);page.puQuantity(item.name(),"2");page.puWaitNet("200");var updated=save(false);Assert.assertEquals(updated.id(),saved.id());sameNumber(firstLine(data.read(updated)).get("quantity"),"2");assertStock("2");}
 protected void delete(){var saved=create("picker","Credit",false);data.deleteOwned(saved);Assert.assertNull(data.read(saved));assertStock("0");}
 protected void linkedReturn(){
   convertedKind=PurchaseDocumentKind.INVOICE;var invoice=create("picker","Credit",false);String code=data.read(invoice).get("code").toString();
   page.puConvertOwned(supplier.name(),code,false,item.name());convertedKind=PurchaseDocumentKind.RETURN;
   for(int i=1;i<=2;i++)initialBalances.put(i,number(data.balance(item,i)));
   Assert.assertTrue(page.puLineReadOnly(item.name(),"الكمية"),"Linked purchase return quantity must be locked");
   sameNumber(firstLine(page.puDocument()).get("quantity"),"3");
   page.puNotes(marker);page.puCapture(marker,data.store(1).id(),item.id(),documentKind().endpoint);var returned=save(false);
   Assert.assertEquals(data.read(returned).get("sourceTransactionId"),invoice.id());sameNumber(firstLine(data.read(returned)).get("quantity"),"3");assertStock("3");
 }
 protected void convertOrder(){
   convertedKind=PurchaseDocumentKind.ORDER;var order=create("picker","Credit",false);String code=data.read(order).get("code").toString();
   page.puConvertOwned(supplier.name(),code,true,item.name());convertedKind=PurchaseDocumentKind.INVOICE;
   page.puNotes(marker);page.puCapture(marker,data.store(1).id(),item.id(),documentKind().endpoint);var invoice=save(false);
   Assert.assertEquals(data.read(invoice).get("sourceTransactionId"),order.id());assertStock("3");
 }
 @SuppressWarnings("unchecked") protected static Map<String,Object> firstLine(Map<String,Object> doc){return ((List<Map<String,Object>>)doc.get("lines")).get(0);}
}
