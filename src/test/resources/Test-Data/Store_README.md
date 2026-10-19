# Warehouse tests

Run the five warehouse screens and cleanup regression tests:

```powershell
mvn '-Dsurefire.suiteXmlFiles=src/test/resources/store-testng.xml' test
```

Use the project's configured application URL and login. No tenant, branch, customer, supplier, item or warehouse ID is embedded in these scenarios. Every case creates its own accounts, units, categories, stores, items and required party, then uses the returned IDs. Manufacturing also creates its own component and checks its consumption. Browser interactions are in `StoreModulePage`; scenarios and API fixture assertions are in `StoreTest`.

The [coverage catalog](Store_Coverage.md) lists 160 cases: 131 UI cases and 29 cleanup regression cases. The original 45 method names remain available. A listed case is not a claim that it passed.

Cleanup deletes only verified owned records in reverse dependency order. A failed child deletion retains its parents. Temporary branch-store membership updates preserve unrelated store IDs and the existing default. Origin and branch guards reject cleanup if the browser changes context. `target/store-fixtures` records created dependencies; `target/store-attempts` records unresolved save requests. Preserve these files when a run is interrupted or cleanup fails. An unresolved request is checked before deleting its dependencies; ambiguous ownership stops cleanup.

Fixture creation is journaled before the API request, including accounts and stores. A lost create response is recovered by exact generated name and verified parent references before any dependent account is deleted. Missing, ambiguous or changed ownership retains the dependencies. Exchange feature scenarios use the picker as their baseline; dedicated name and barcode cases still test those paths independently.

For an interrupted run, launch the `StoreTest.StoreFixtureRecovery` main class with the chosen journal filename stems as arguments, using the same configured tenant and branch. It reads both fixture and uncertain-request journals, verifies their context, and resumes the same dependency-aware cleanup. It never scans and deletes all warehouse records.

Business assertions:

- A document must preserve the explicitly selected party and type. An agent document must not also contain a default customer or supplier.
- Cost totals and sale totals are checked separately against the columns actually displayed by each screen.
- Saved quantities and stock changes are checked against the test item's initial balances, including transfer destination and manufacturing components.
- PDF and Excel tests inspect actual captured downloads. Application failures remain test failures.

Known live findings and execution evidence are recorded in [Store_Known_Issues.md](Store_Known_Issues.md). Shared `AbstractForStore` and the other module scenarios are not modified by this implementation.
