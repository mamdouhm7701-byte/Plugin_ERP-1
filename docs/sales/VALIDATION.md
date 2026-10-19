Verified 2026-10-19T17:58+03:00

# Sales validation summary

Declared main suite: **169 invocations** (114 UI; 55 local calculation/cleanup). Compatibility aliases are not added to this count.

Latest executed active cases: **169**. Status: {'passed': 165, 'failed': 4}. UI subset: {'passed': 110, 'failed': 4}. Not executed: 0.

Latest result per class/method/parameters is used. Earlier attempts remain in their run folders. Superseded invoice edit and return price-list expectations are excluded: normal invoices are immutable, and the return screen instead exercises valid entered unit pricing.

Original public test method names preserved: **66/66**.
Store source files changed against pre-sales SHA-256 baseline: **0**.
Nonempty fixture/attempt journals: **0**.

## Latest failing cases

- `Invoice.SalesInvoiceCoverageTest.barcodeApiFallbackAddsOwnedItemAbsentFromCache`: Owned item returned by the API, but the invoice row was not added; browser error reads undefined costPrice. [sales-page-final-ui-results]
- `Invoice.SalesInvoiceCoverageTest.savesOwnedItemByBarcode`: Owned item returned by the API, but the invoice row was not added; browser error reads undefined costPrice. [sales-page-final-ui-results]
- `Invoice.SalesOrderTest.cashDocumentUsesOwnedPayment`: Expected customer balance delta -300, actual 0 [sales-page-final-ui-results]
- `Invoice.SalesOrderTest.cashOrderRecordsCustomerAdvanceWithoutStockMovement`: Expected customer balance delta -300, actual 0 [sales-page-final-ui-results]

## Final focused run configuration failures

None.

## Execution scope

Results aggregate the preserved partial user run and focused verification runs; this is not a claim that all 169 cases completed in one end-to-end run.

## Run folders

- `sales-cleanup-initial-results`
- `sales-final-contracts-results`
- `sales-focused-results`
- `sales-list-final-results`
- `sales-list-probe-results`
- `sales-list-probe-visible-results`
- `sales-list-ready-results`
- `sales-page-cleanup-results`
- `sales-page-final-ui-results`
- `sales-page-guard-final-results`
- `sales-page-guard-results`
- `sales-page-remaining-results`
- `sales-page-unit-results`
- `sales-search-fixed-results`
- `sales-seed-check-results`
- `sales-unit-final-results`
- `sales-unit-results`
- `sales-unit-updated-results`
- `sales-user-run-results`
- `sales-verification-results`

## Pending journals

None.

## Limits

A written case is not a passing execution. Baseline UI fixtures are non-taxable; VAT scenarios here validate the calculator. POS, restaurant invoices, e-invoice submission and external messaging are outside the core menu suite. ERP application defects are documented in docs/sales/KNOWN_BUGS.md; the ERP application source is not in this repository.

## Page cleanup verification

Cleanup runs in `@AfterClass(alwaysRun=true)`. `@AfterMethod` captures requests, failure evidence and closes auxiliary report windows without deleting database fixtures. Stable prerequisites are shared per page; items, item categories, customers, customer accounts and documents remain isolated per case. Explicit document-deletion scenarios still test their own delete action.

The focused page-lifecycle run passed both UI cases with the same store and different customer/item IDs, then removed all owned records at the end of the class. Local cleanup/calculation regressions passed 55/55. The final six corrected UI cases passed 6/6. All 18 sales-list invocations passed, including actual PDF and Excel signatures. Whole-project Maven test-compile and diff whitespace checks passed.

Latest selected Allure results and their linked evidence are collected under `target/sales-latest-allure` (169 unique invocations). Earlier attempts remain available in the run folders.
