# Page fixture lifecycle

Routine cleanup runs in `@AfterClass(alwaysRun=true)`. Each `@AfterMethod` still captures owned requests and records, retains failure evidence, and closes report windows. A failed final browser step does not prevent the class cleanup from trying its owned fixtures.

`BaseTest` owns an instance browser and wait, initialized in `@BeforeClass`. Every screen class logs in with a fresh browser; session readiness hooks for products, stores, sales and purchases also run at class scope, after base setup. Subclass `@AfterClass` hooks finish owned fixture cleanup before the inherited base teardown closes that class's browser. Failed login setup closes its browser immediately, retaining the original error and any teardown error as suppressed evidence. WebDriverFactory clears its thread-local reference even when quitting fails and rejects replacing an already active browser.

Methods within a screen class intentionally keep that class's browser and stable fixture references. Mutable records and captures remain case-scoped as described below. This change does not enable parallel methods or suites. Authentication tests retain their separate fresh-browser-per-method lifecycle. Local lifecycle harnesses bind their fake browser to the particular scenario instance rather than replacing a global driver.

Base login now waits for the login form and then the home page instead of sleeping five seconds. Eighteen redundant two-second sleeps before dealer navigation were removed from CustomersTest and SupplierTest; navigation and dealer form actions already wait for overlays, clickable controls and form readiness. Other polling/backoff and legacy waits remain outside this change.

Headless sessions explicitly keep a 1920×1080 window instead of maximizing: the initial live validation reproduced a login-readiness failure with headless maximize, while the same case passed with a fixed desktop viewport. `BrowserLifecycleTest` covers that choice alongside independent browser ownership, login failure cleanup, duplicate initialization and reference release after quit failure.

## Group boundaries and isolation

Sales fixture payloads and browser transport now live in `SalesFixtureDefinitions` and `SalesFixtureApi`. `SalesTestData` still owns case scopes, journals, recovery and cleanup ordering. Sales and purchase cleanup tests use separate in-memory API stubs. See [the first refactoring batch and validation](REFACTORING_AR.md).

Purchase browser transport lives in `PurchaseFixtureApi`, with the same origin/branch guard and request timeouts. `PurchaseTestData` retains its SQL deadlock retry policy, ownership, receipt pagination, recovery journals and cleanup ordering.

Purchase master-data payloads now live in `PurchaseFixtureDefinitions`, using the owner's existing `fixture` registration and `store` membership handling. `PurchaseTestData` retains the original calling interface and the scenario-dependent `seedPurchase` recipes, along with ownership, retries and cleanup.

Warehouse and special-sales payloads and browser transport live in their own `StoreFixtureDefinitions`/`StoreFixtureApi` and `SpecialSalesFixtureDefinitions`/`SpecialSalesFixtureApi` classes. Their owners retain branch membership, special-sales default restoration, pending-write recovery and cleanup ordering. Each cleanup regression class uses its own package-local `FixtureApiStub`; test assertions and case names are preserved.

The live `additional/standalone/fixture-refactor-smoke-testng.xml` suite selects eight cases across exchange orders, manufacturing, POS and restaurant invoices. It runs classes sequentially and relies on each class's existing fixture cleanup. It is separate from the offline CI suite. For commands and recorded validation, see [REFACTORING_AR.md](REFACTORING_AR.md).

Products, dealers and financial movements use the same responsibility boundaries, with their own `FixtureDefinitions`, `FixtureApi` and `FixtureApiStub` classes. Session guards still come from each owner's `FixtureSession`; pending-write journals and deletion policies remain with the owner. Financial invoice recipes receive the existing run suffix explicitly, while dealer fixture registration is package-local for the definitions helper. API response differences between modules are preserved.

The separate live `additional/standalone/fixture-data-refactor-smoke-testng.xml` suite selects ten cases across six classes: units, products, linked dealer fixtures, customers, financial lookup fixtures and customer receipts with invoice allocation. Both fixture smoke suites run sequentially and remain outside offline CI.

| Module | Fixture group | Shared stable references | Independent per case |
|---|---|---|---|
| Products | One group for each item, category, unit or price-list screen | Units, main category, stores and store accounts | Items, subcategories, price lists and every UI target record |
| Stores | One group per movement form: issue, receipt, manufacturing, damage, transfer | Units, main category, stores and store accounts | Items/components, parties, party accounts and documents |
| Financial movements | One group per receipt, payment, expense, income, customer discount, supplier discount or transfer form | Invoice unit, invoice main category, invoice store and store accounts when needed | Customers/suppliers, agents, banks, safes, payments, expenses/incomes/discounts, their accounts, invoice items and transactions |
| Dealers | Separate groups for agents, classifications, customers, main dealers and suppliers | Area, classification/main-dealer references, unit and main item category | UI target dealers, accounts, store, items, subcategory and price list |
| Sales | Invoice, quotation, order and return form groups; a separate list group and linked-return workflow group | Units, main category, stores and store accounts | Customers/accounts, items, agents, banks/safes/payment methods and accounts, price lists and discount types |

An Agent commission case has its own store/items/category: an “all products” selector must not pick items left by another case. The sales linked-return workflow intentionally spans the invoice and return screens, retaining source and children until the workflow group ends. List cases isolate their customer/supplier and documents so retained earlier rows cannot change search expectations. Sales compatibility aliases are in a separate suite; they do not duplicate the main form/list groups.

Tests that edit a UI record seed an independent target. Shared references are only selected, never edited by these scenarios. If a future test edits a shared prerequisite, it must create a private copy or restore it before the next case.

## Cleanup and recovery

Only recorded owned identities are deleted, in reverse dependency order. Unresolved child deletion retains its parents. Progress is journaled after every successful deletion. Journal replacement uses a temporary file and atomic move, with bounded retries for transient Windows file locks; a failed replacement retains the prior recovery file.

Fixture creation is recorded before the API call. Unknown outcomes are resolved by generated name, readback identity and parent references before parent cleanup. Ambiguous ownership, incomplete lookup, a different origin/branch or changed dependencies stops cleanup and preserves the journals. Captures from a previous case are ignored if the marker differs.

Dealer routine cleanup reads the complete paginated API list, resolves the exact generated name to one identity, deletes that identity and verifies its absence by ID. It does not infer absence from the visible UI page. Explicit Dealer deletion feature cases still exercise the native UI. Branch guards wait for a temporarily null branch after a reload, but immediately reject a non-null foreign branch or a changed origin.

| Module | Durable directories | Explicit recovery main class |
|---|---|---|
| Sales | `target/sales-fixtures`, `target/sales-attempts` | `Invoice.SalesFixtureRecovery <suffix>` |
| Products | `target/products-fixtures`, `target/products-attempts` | `ProductsTest.ProductsFixtureRecovery <suffix>` |
| Stores | `target/store-fixtures`, `target/store-attempts` | `StoreTest.StoreFixtureRecovery <suffix>` |
| Financial | `target/financial-fixtures`, `target/financial-attempts` | `FinancialMovementsTest.FinancialFixtureRecovery <suffix>` |
| Dealers | `target/dealer-page-records`, `target/dealer-fixtures`, `target/dealer-attempts` | `DealerModelTest.DealerFixtureRecovery page <page-suffix>` or `fixtures <fixture-suffix>` |

Pass an exact journal suffix, using the same application and branch. Dealer page recovery removes its exact named UI records first, then resumes the linked fixture journal. Recovery never scans and deletes all business records. Historical journal files without origin/branch metadata are deliberately rejected by the new recovery loaders.

## Cases that still delete during the case

- Explicit delete/create-edit-delete feature cases: deletion is the behavior under test and targets only that case's record.
- Local cleanup regressions: each case controls an independent in-memory API and deliberately exercises cleanup/retry/interruption; they never use live ERP data.
- `FinancialFixtureTest.createdFixturesAppearInFinancialLookups`: a self-contained fixture API/lookup smoke check, separate from the financial form groups. Its private context closes after its single fixture check.

## Verification

Run local regressions with `mvn -Dsurefire.suiteXmlFiles=src/test/resources/additional/standalone/fixture-lifecycle-testng.xml test`. These use mock APIs and disposable browser harnesses. They verify actual before/after callbacks as well as shared references, case isolation, recovery after lost responses, blocked-parent retention, branch guards and interruption progress. They do not establish live screen coverage.

The latest local run passed all **125** invocations, including 24 added regression cases. A separate targeted UI run exercises two cases per selected module group; its final results and journal checks are recorded in [LIFECYCLE_VALIDATION.md](LIFECYCLE_VALIDATION.md).
