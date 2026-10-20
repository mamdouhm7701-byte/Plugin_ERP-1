# Purchase observations

Validation completed for all 139 declared invocations: 134 passed and 5 failed on the business contracts below. See [VALIDATION.md](VALIDATION.md). All owned purchase fixture and attempt journals were empty after cleanup. Historical attempts remain separate from the collated latest results in `target/purchase-latest-allure`.

| Observation | Expected contract | Execution evidence |
|---|---|---|
| Purchase invoice accepts a zero-quantity stock item | Reject the document without creating an invoice | `PurchaseInvoiceCoverageTest.zeroQuantityCannotSave` received a successful create response; the final contract run read back a persisted `Purch` line with quantity 0 and net 0. |
| Purchase order accepts a zero-quantity stock item | Reject the document without creating an order | `PurchaseOrderCoverageTest.zeroQuantityCannotSave` read back a persisted `PurchOrder` line with quantity 0 and net 0 in the final contract run. |
| Free purchase return accepts a document with no items | Reject the document without creating a return | `PurchaseReturnCoverageTest.emptyDocumentCannotSave` read back a persisted `ReturnPurch` document with `lines: null` and net 0 in the final contract run. The first attempt also produced a post-save `setupDefualts` error reading `discount` from an undefined object. |
| Cash purchase order has no supplier receipt observable through Collections | The user-confirmed rule requires a receipt and no stock movement | `PurchaseOrderCoverageTest.cashOrderCreatesReceiptWithoutStockMovement` saved paid/net 300 using an owned Other/bank payment method, but found no receipt for its generated supplier. The focused rerun also inspected receipt details when the list omitted dealer identity. This observation is specific to the payment method exercised; safe payment has not been established by this case. |
| Linked purchase return saves a changed quantity | Preserve the invoice quantity, as confirmed by the user | `PurchaseLinkedCoverageTest.linkedReturnKeepsOriginalQuantity` converted an owned invoice of quantity 3, changed the return to 2 through the screen, then read back quantity 2 and net 200. The attachment `Linked purchase return quantity evidence` contains the actual persisted response. |

An editable linked-return quantity alone is not treated as proof of a persisted business defect. The final contract case attempts the change and checks the stored quantity, source relationship and stock. Purchase source relationships are checked through the actual persisted list when the detail DTO omits them.

After the user's discount configuration change, all four fresh discount invocations passed with actual persistence: invoice and return net 285 for a fixed discount of 15, and net 270 for a 10% discount on 300. Stock and supplier balance checks also passed. Earlier discount-permission failures are superseded by these results.

These failures remain visible in the suite. This repository contains automation; no ERP application validation was patched or simulated to make the failed contracts pass.
