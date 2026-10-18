# Products automation

The four existing test classes retain all 24 original scenario names. Coverage currently contains 85 UI cases across products, categories, units and price lists, plus 15 cleanup regression tests. See `src/test/resources/Test-Data/Products_Coverage.md` for the case catalog.

Each scenario creates its own prerequisites through the authenticated ERP API client and current branch. Product categories, units, stores, accounts, component items and price-list items are created on demand. Tests never depend on existing tenant names, product codes, IDs, record order or another test running first. Edit/delete cases seed their own record; row actions target its unique name.

The existing four Page Objects extend `ProductsModulePage`, which owns navigation, forms, modal pickers, dropdowns, exact-row actions, capture of actual UI saves and exports. `ProductScenarioTest` contains orchestration and persisted-data assertions. Product-specific stock/subunit/component interactions remain in `AddProductsPage`, and price-list item interactions remain in `ProductListPage`. Existing shared Dealer and Financial Page Objects are unchanged.

`ProductsTestData` journals every owned record in `target/products-fixtures/`. Cleanup removes children before their referenced parents, verifies deletion by ID, preserves failed children and their parents, and retries transient failures. Owned stores are temporarily linked to the current branch because general product/price-list lookups restrict items to that branch's stores. The helper journals this membership before adding it, fetches the branch again before each update, and removes only the owned store ID on cleanup. Existing stores, the default store and concurrent edits are preserved; it does not restore an old snapshot of the branch. Membership is removed after dependent items and before the store/account fixtures.

A UI-created item inserted before a failed response is recovered by exact generated name plus its own unit/category IDs for cleanup only; the original failed creation remains a failure. A missing-record response is accepted only after an independent read proves absence. No DeleteAll action is used. Empty journal arrays indicate completed cleanup.

New cases include cancelling creation/edit/delete, searching and clearing no-result searches, list structure, real PDF/Excel signatures, optional English names, category flags, decimal prices, stock balances, multiple stores, control flags, Arabic product names, invalid stock values, duplicate stores/subunits, conversion rates of zero/one/negative values, required subunit barcodes, price limits, duplicate product barcodes and duplicate item/unit pairs in price lists. Business rules that the live application contradicts must be investigated, not hidden by weakening assertions.

Run sequentially on Java 21 using the current tenant/login configuration:

```powershell
mvn '-DExecutionType=LocalHeadless' '-DargLine=-Xmx384m' '-Dsurefire.suiteXmlFiles=src/test/resources/products-testng.xml' test
```

The legacy `Test-Data/ProductCategories.xml` points to the same complete suite. `ProductsSuitePolicy` keeps cleanup failures visible while allowing independent product cases to continue. A product-only login wait verifies the current branch after the shared login helper and handles a submit click made before Vue finishes loading; it does not change login settings.

Validation on 18 October 2026: the user completed the preceding 93-case suite with 75 passes and 18 failures/broken cases. Its original results are archived in `target/products-user-run-20261018-204218`.

The targeted repairs cover stale filter-button clicks, waiting for the search panel, the price-list edit icon, waiting for the selected record to finish loading before typing, equivalent Arabic category-validation spelling, and the visible `aria-invalid` state when a control hides its validation text. The six affected edit/required-field cases and all 15 cleanup regressions subsequently passed together: **21/21**, with no skips, in `target/products-edit-load-recheck-results`. Actual sent and persisted values are attached to save results.

The confirmed price rule is **minimum <= sale <= maximum**, including equality at either boundary and equality of all three. Negative sale prices remain forbidden. Seven new cases bring the suite to **100 tests: 85 UI + 15 cleanup regressions**. All 24 original method names remain present. The three equality cases pass with read-back verification. All seven invalid-price cases expose application defects: the API returns the entered invalid values after a successful save. These assertions remain enabled. Price-list PDF export also remains an application failure (`this.hide is not a function`); product PDF and Excel exports pass.

The reviewed report selects the latest result per case across three targeted runs: **25 UI cases + 15 cleanup regressions**, not a fresh execution of all 100 cases. Latest results: **32 passed, 8 failed, zero broken or skipped**. The eight failures are seven invalid-price combinations and price-list PDF export. All 14 repairable test failures from the user's run now pass. Raw runs are `target/products-latest-run-recheck-results`, `target/products-edit-load-recheck-results`, and `target/products-price-boundaries-results`. Consolidated counts and provenance are in `target/products-reviewed-summary.json`; the standalone report is `target/products-reviewed-report-20261018/index.html`.

The final journal recovery independently verified the remaining owned ID, and all product fixture journals are empty. Evidence: `target/products-final-journal-recovery.log` (`ALL_PRODUCTS_JOURNALS_CLEAN`). Only generated fixture IDs were deleted, in dependency order; temporary owned store memberships were removed before their stores and accounts.

See `KNOWN_BUGS.md` for the business-rule matrix and remaining application defects. The ERP frontend/backend sources are absent here; tests do not patch deployed application methods or fabricate successful exports. The changed Java sources remain within the Products Page Objects and Products test package. The listener registration adds only the product-scoped suite policy. Other modules' implementation was not changed or claimed to have been fully rerun.
