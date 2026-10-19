package Invoice;

enum SalesDocumentKind {
  INVOICE("invoice","Sales"), QUOTATION("quotation","quotation"),
  ORDER("sales-order","SalesOrder"), RETURN("return-sales","ReturnSales");
  final String route,type,endpoint;
  SalesDocumentKind(String route,String type){this.route=route;this.type=type;this.endpoint="ItemTransactions-"+type;}
  boolean stockMovement(){return this==INVOICE||this==RETURN;}
}
