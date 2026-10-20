# Coverage catalog

The suite declares **169 invocations**: **114 browser scenarios** and **55 local calculation/cleanup regressions**. Counts include inherited form contracts and the four page-size values; they are not execution results.

| Area | Cases | Main checks |
|---|---:|---|
| Invoice | 24 | Shared form contract, owned price list, actual save/print and immutable saved invoice controls and barcode API fallback for a newly available owned item |
| Quotation | 22 | Shared form contract plus owned price list; no stock/customer-balance impact |
| Sales order | 24 | Shared contract, owned price list and explicit cash advance/credit neutrality without stock movement |
| Free sales return | 22 | Shared contract plus entered unit price; stock restoration and customer balance |
| Linked return | 4 | Partial/full return, over-original rejection, cumulative quantity limit |
| Sales list | 18 | Own-customer scope, exact code/notes/no results, page sizes 25/50/100/500, 26-document pagination and no duplicate IDs, sorting, filter clear, own agent, paid/unpaid/partial statuses, document types, valid/empty date ranges, actual Excel/PDF files |
| Calculator | 12 | Known net/tax/discount/rounding examples, inclusive/exclusive VAT |
| Cleanup | 43 | Identity/context guards, lost/partial responses, parent retention, membership preservation, cross-process recovery, linked-return and payment dependency order, foreign tenant/branch recovery rejection and lost responses for quotation/order/return |

The 21 shared form cases are: real save from picker/barcode/name; cash payment (quotation has no payment UI); fractional quantities; two items; removed line absent from persisted document; percentage/fixed discount; Unicode notes; empty lines, zero/negative quantity, missing customer/store, negative price; New clears unsaved lines; saved-document delete policy (invoice has no delete action, other types restore balances); reopening saved values; edit policy (invoice is read-only, other types replace original stock effect); maximum notes length.

Baseline UI fixtures are non-taxable. Taxable UI setup, POS, ZATCA submission and external messaging are not included. All 66 original test method names are retained in the three migrated compatibility classes, using owned fixtures and the same reusable scenario actions. They are available outside the main suite to avoid duplicate invocations. Failed application behavior remains an enabled assertion; it must not be converted into a passing test by changing business expectations or injecting UI state.

Price coverage follows the actual screens: invoice, quotation and order select an owned price list. Free return has no price-list selector; its additional case enters a valid unit price and checks persisted price, net, stock and customer balance. No inherited case is disabled to achieve this split.
