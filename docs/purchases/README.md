# Purchase automation

Run `mvn -DExecutionType=LocalHeadless -Dsurefire.suiteXmlFiles=src/test/resources/purchases-testng.xml test`.

The main suite covers purchase invoices, purchase orders, free returns, document conversion, the purchase list, arithmetic and cleanup safety. The original 25 public test names are preserved in `purchases-compatibility-testng.xml`; they use owned data rather than fixed suppliers, stores or barcodes. Each case has an Arabic Allure story. See [COVERAGE.md](COVERAGE.md).

## Confirmed business rules

- Purchase orders do not change inventory. Cash orders must generate a receipt.
- Purchase invoices, orders and returns allow editing and deletion.
- Purchase returns may be free or linked to an invoice. Linked return quantities must be locked.
- Purchase invoices increase stock; purchase returns reduce it. An unrelated owned store must retain its balance.

## Fixtures and page objects

`PurchaseModulePage` owns browser interactions. `PurchaseScenarioTest` orchestrates scenarios and verifies API readback. `PurchaseTestData` creates prerequisites in the currently authenticated origin and branch. It shares stable stores, units, main category and agent within each page group. Suppliers, their accounts, items, banks, safes, payment methods and payment accounts are isolated per case because their balances or search results may change. No existing supplier, item, barcode, tenant or store identity is assumed.

Cleanup runs at `@AfterClass(alwaysRun=true)`, after each page. It deletes only recorded owned identities in reverse dependency order and retains blocked parents when deletion or ownership verification fails. Cases that test deletion explicitly delete only their own document. Requests with uncertain responses are recovered before removing dependent records. Journals are in `target/purchase-fixtures` and `target/purchase-attempts`. Recovery of one journal uses `Purches.PurchaseFixtureRecovery <suffix>` in its original application and branch.

The main suite has separate fixture groups for each of the three document forms, the purchase list, and the conversion workflows. The conversion group intentionally visits multiple screens while retaining the source and child documents until the workflow group finishes. Compatibility aliases run in a separate suite. Explicit deletion feature cases and local cleanup regression tests still delete within their case because the deletion itself is the behavior being verified; routine ERP fixture cleanup remains at the end of the page/workflow group.

Run mutating suites sequentially against a branch. Read-only form discovery and local arithmetic/cleanup tests do not prove successful UI coverage. Written cases and executed results are reported separately in the validation report.

Discount cases always verify screen arithmetic. The save must either persist the correct net and ledger/stock effects, or return the specific backend discount-permission rejection with no document or balance change. When the view model exposes the user's numeric discount limit, the result must also agree with that limit. Some versions omit the limit; those runs establish the observed backend permission response without inventing a numeric allowance. A run that rejects the discount does not establish coverage of permitted discount persistence.

The baseline UI fixtures are non-taxable. Inclusive/exclusive tax and rounding examples exercise the independent calculator; they do not establish taxable purchase-screen coverage.
