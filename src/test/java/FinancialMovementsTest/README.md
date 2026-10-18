# Financial movements automation

The seven original classes retain all 74 named scenarios. Each class also includes eight new scenarios: list structure, cancelling creation/editing/deletion, rejecting zero/negative amounts, and valid PDF/Excel export. Total UI coverage: 130 cases, plus the fixture verification and ten cleanup regression tests (141 tests in the complete suite).

The Page Objects own routes, locators, dropdowns, forms, exact-row operations and export interactions. `FinancialScenarioTest` orchestrates isolated scenarios and asserts persisted values, tax calculations, allocation and reports. `FinancialTestData` creates prerequisites through the authenticated application's API client using the current branch. No tenant names, hostnames, existing dealer/account IDs or keyboard-position selections are embedded in the tests.

Edit/delete tests first create their own movement. Each saved UI request is recorded by endpoint, ID and unique notes. Cleanup removes those movements and their dependent fixtures in reverse dependency order; blocked parents remain pending and failures stay visible. It never invokes DeleteAll. The ownership journal in `target/financial-fixtures/` retains unresolved IDs if cleanup needs investigation. Empty journal arrays mean cleanup completed.

Deletion is verified by reading the same ID afterwards. The ERP returns blank strings or default DTOs without an identity for deleted records. Its Customers detail endpoint can return HTTP 500 for a deleted ID; only a complete paged lookup proving that ID absent is accepted as deletion. A DELETE returning HTTP 400 with `Common.ObjectNotFound` is accepted only after a detail read independently proves absence. Other HTTP 400 failures remain failures. An updated movement is ordered after any newly created reference so it is removed before those references.

Allocation scenarios create their own credit invoice, item, unit, category and store, then allocate only to that invoice. They do not select the first invoice belonging to an existing customer or supplier. Outside-year tests derive the date from the active fiscal year. Fixtures enable the transaction permissions that the dealer creation forms enable by default.

Run on Java 21 using the existing tenant/login configuration:

```powershell
mvn '-DExecutionType=LocalHeadless' '-DargLine=-Xmx384m' '-Dsurefire.suiteXmlFiles=src/test/resources/financial-movements-testng.xml' test
```

The legacy `Test-Data/FinanicalMovment.xml` is also updated: Customer Discount is included and the duplicate Other Revenue entry is removed. Tests remain sequential because the repository's BaseTest shares one WebDriver. Configuration cleanup failures remain failures and do not silently skip subsequent cases.

No dealer tests or shared dealer Page Objects were changed for this work.

Validation on 18 October 2026 used the complete suite followed by focused reruns of failures and affected interactions. The complete run initially passed 129 of 140 cases; fixture cleanup errors also affected configuration results. The first rerun passed 20 of 21, exposing one dropdown remount timing issue. That issue was then fixed in the POM and verified again. The final report contains the latest result per test case across these runs, rather than claiming that the entire suite was rerun in one clean invocation. Raw historical results remain in `target/financial-complete-results`, `target/financial-retest-results` and `target/financial-confirm-results`. Other modules were compiled with the project but their full live suites were not rerun.

Final verification: the last focused run passed **21/21**, with zero failures or skips. Latest results across all 141 distinct cases are **141 passed**. All ownership journals were empty after completion. The consolidated, standalone Allure report is `target/financial-report/index.html`; it preserves the latest result per case while raw runs retain earlier failures.
