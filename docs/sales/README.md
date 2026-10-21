# Sales automation

Run `mvn -DExecutionType=LocalHeadless -Dsurefire.suiteXmlFiles=src/test/resources/sales-testng.xml test`.

The new suite covers invoice, quotation, sales order, free return, linked return, sales list, arithmetic and fixture cleanup. The original SalesInvoiceTestCases, InvoiceTest and InvoiceListTestCases entry points are also migrated to owned fixtures. Their names and data-provider signatures are retained. Equivalent compatibility cases stay outside the main suite to avoid duplicate runs; use additional/legacy/sales-compatibility-testng.xml to run them separately.

## Business contracts

- Sales orders neither reserve nor deduct inventory. Credit orders do not change the customer balance. Cash orders record the customer advance.
- Linked returns cannot exceed the original sold quantity. Free returns are allowed without a source invoice.
- Saved sales invoices are immutable: no quantity/price edit and no invoice delete action. This rule applies to the normal sales invoice only; quotation, sales order and return allow edits/deletion. POS and restaurant invoices have separate behavior and are outside this core suite.
- Invoices reduce stock; returns restore it. Other owned stores remain unchanged.
- Customer signed balance is debit minus credit, including previous balances.

## Data and cleanup

Each page group reuses its owned QA-SL stores, store accounts, units and main category. Each scenario creates independent items, item categories, customers/accounts, agents, banks/safes/payment methods and their accounts, price lists and documents, so balances and list filters cannot leak between cases. API fixtures are arranged before opening the screen; document entry and save use native UI interactions through SalesModulePage and its typed POMs. Read-only Vue state and API readback verify the actual persisted result. No existing customer's name, barcode, store ID, tenant ID or default payment method is assumed. Group boundaries and regression checks are documented in [Page fixture lifecycle](../testing/PAGE_FIXTURE_LIFECYCLE.md).

Cleanup runs after the page group (`@AfterClass(alwaysRun=true)`), including failed cases. After each case, the harness captures requests/evidence and closes auxiliary report windows without deleting data. Interrupted requests are recovered before the next case starts. Only recorded owned IDs are deleted, in reverse dependency order. Owned stores are temporarily linked to the active branch; cleanup removes only those membership IDs while preserving existing links. Never run concurrent mutating suites against the same branch. The harness checks the current origin and branch before each fixture request. Explicit deletion cases still exercise their own document's delete behavior during the case.

Durable journals are in `target/sales-fixtures` and `target/sales-attempts`. Interrupted create responses are resolved before deleting parents. If cleanup cannot establish ownership or a dependent delete fails, it retains the related records and reports the journal rather than deleting broadly. Recovery entry point: `Invoice.SalesFixtureRecovery <journal-suffix>` using the compiled test classpath and the same application/branch configuration.

Tax arithmetic has explicit independent examples for inclusive/exclusive VAT, rounding and discounts. These are calculator tests; they do not claim that taxable invoice UI configurations have been exercised. Baseline UI scenarios use their own non-taxable item and customer. Customer fixtures enable the actual quotation/salesReturn/cashSales/creditSales/salesOrder capabilities and read them back before entering the screen.

## Scope limits

The core sales menu has four document types and the sales list. POS, e-invoice submission, messaging and sales dashboards are outside this suite. Export checks observe actual generated files and verify signatures. For quotation/order/return, delete lifecycle checks use the API because the current sales list does not expose a delete action. Normal invoice cases verify that edit/delete actions are unavailable. Privileged fixture cleanup removes only generated records; it is not treated as a normal invoice business action.

Execution evidence and remaining failures are recorded separately in `target/sales-validation-summary.md`. A written case is not a passing execution.

Latest verified execution snapshot: [VALIDATION.md](VALIDATION.md). The snapshot aggregates the latest result for each declared invocation; historical attempts remain separate.
