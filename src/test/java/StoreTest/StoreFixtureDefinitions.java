package StoreTest;

import static StoreTest.StoreTestData.fields;

import StoreTest.StoreTestData.Record;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Payload definitions; ownership, branch state, recovery and cleanup stay with the owner. */
final class StoreFixtureDefinitions {
  private final StoreTestData data;

  StoreFixtureDefinitions(StoreTestData data) {
      this.data = data;
  }

  Record account(String key) {
    Record parent = data.fixture("account-main-"+key, "Accounts", fields("type", "Main",
        "code", Integer.toString(java.util.concurrent.ThreadLocalRandom.current().nextInt(10000000, 99999999)),
        "nature", "Debit", "finalAccountType", "Budget", "level", 1,
        "isDistributable", false, "isSharedBetweenBranches", false,
        "prevDebit", 0, "prevCredit", 0, "previousBalances", List.of()));
    return data.fixture("account-sub-"+key, "Accounts", fields("type", "Sub", "nature", "Debit",
        "code", parent.values().get("code") + "01", "finalAccountType", "Budget",
        "parentId", parent.id(), "level", 2, "isDistributable", false,
        "isSharedBetweenBranches", false, "prevDebit", 0, "prevCredit", 0,
        "previousBalances", List.of()));
  }

  Record unit(int index) {return data.fixture("unit-"+index,"Units",fields("conversionRates",List.of()));}

  Record mainCategory(){return data.fixture("category-main","ItemCategories",fields("isMain",true));}

  Record category(){return data.fixture("category","ItemCategories",fields("isMain",false,"parentCategoryId",mainCategory().id()));}

  Map<String,Object> itemValues(){
    return fields("categoryId",category().id(),"mainUnitId",unit(1).id(),
        "mainUnitBarcode","ST"+UUID.randomUUID().toString().replace("-",""),"type","RawMaterial","isStoreItem",true,
        "isTaxable",false,"itemTax",0,"salesPrice",100,"minSalesPrice",90,"maxSalesPrice",110,
        "sales",true,"purch",true,"returnSales",true,"returnPurch",true,"quotation",true,
        "salesOrder",true,"purchOrder",true,"storageTransfer",true,"additionOrder",true,
        "exchangeOrder",true,"damageAdjustment",true,"isScalesItem",false,
        "subUnits",List.of(),"components",List.of(),"additions",List.of(),"offers",List.of(),"discounts",List.of(),
        "stores",List.of(fields("storeId",data.store(1).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,
            "minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
  }

  Record item(int index){
    Map<String,Object> values=itemValues();
    values.put("stores",List.of(fields("storeId",data.store(1).id(),"itemPlace","QA","openingBalance",1000,"currentBalance",1000,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true),
      fields("storeId",data.store(2).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
    return data.fixture("item-"+index,"Items",values);
  }

  Record manufactured(){return manufactured(1);}

  Record manufactured(int index){
    Record component=item(2);Map<String,Object> values=itemValues();values.put("isManufacturingItem",true);
    values.put("stores",List.of(fields("storeId",data.store(1).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true),
      fields("storeId",data.store(2).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
    values.put("componentsStoreId",data.store(1).id());values.put("components",List.of(fields("componentId",component.id(),"unitId",unit(1).id(),"requiredQuantity",2)));
    return data.fixture("manufactured-"+index,"Items",values);
  }

  Record agent(){return agent(false);}

  Record agent(boolean purchase){return data.fixture(purchase?"agent-purchase":"agent-sales","Agents",fields("nature",purchase?"Purch":"Sales","isActive",true,"commissionSlices",List.of(),"itemCommissions",List.of()));}

  Record destination(){return data.fixture("destination","ExchangeDestinations",fields("accountId",account("destination").id()));}

  Record supplier(){return data.fixture("supplier","Suppliers",fields("isActive",true,"cashPurchase",true,"creditPurchase",true,"isTaxable",false,"isClosed",false,"purchaseReturn",true,"purchaseOrder",true,"dealerType","Supplier","isSameBranch",true,"allowanceDays",0,"accountId",account("supplier").id()));}

  Record customer(){return data.fixture("customer","Customers",fields("isActive",true,"cashSales",true,"creditNote", true, "salesDiscount", true, "creditSales",true,"salesReturn",true,"quotation",true,"isTaxable",true,"isOnTheFly", false, "isClosed",false,"salesOrder",true,"dealerType","Customer","isSameBranch",true,"allowanceDays",0,"accountId",account("customer").id()));}
}
