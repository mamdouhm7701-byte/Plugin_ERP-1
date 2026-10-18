# Dealer scenario data

Dealer tests create their own records in the currently configured ERP environment. They do not require a particular tenant, existing region, agent, classification, main dealer, warehouse, product barcode, or account name. Tenant and login configuration remain unchanged.

`DealerTestData` creates prerequisites through the application's authenticated API client, using the current session's branch. Each scenario gets its own fixture context and unique names. Related records use the IDs returned by creation:

- Region and dealer classifications → sales/purchasing agents.
- Main account → subaccount → warehouse.
- Main category → category, plus unit and warehouse → products.
- Customer/supplier scenarios select the region, account, agent, classification and main dealer created for that scenario.
- Product commission scenarios search generated barcodes and use their own warehouse containing two generated products.
- Duplicate-phone scenarios first create a customer with a generated phone number, then reuse that exact number.

`DealerScenarioTest` prepares prerequisites before opening the tested form. It tracks the records created by each scenario. Edit, delete, cancel-edit and cancel-delete tests address those exact records rather than the first/last shared row. Cleanup removes scenario records first, then prerequisite records in reverse dependency order, using captured IDs.

Business enum values such as Customers/Suppliers, Sales/Purch, and percentage commission remain fixed because they describe the behavior under test, not tenant data. The environment still needs the ERP application, a selected company/branch/year, and a user with permission to create/delete the prerequisite entities.

Run all 80 Dealer scenarios, excluding Route Line:

```powershell
mvn '-Dsurefire.suiteXmlFiles=src/test/resources/dealer-model-testng.xml' test
```

Verify prerequisite creation, persisted relationships and cleanup separately:

```powershell
mvn '-Dtest=DealerModelTest.DealerTestDataTest' test
```

Use Java 21, the project's configured release. No scenario is disabled with `enabled = false` in the five Dealer test classes. Route Line remains outside this suite, as requested.

For a new scenario, call `prepareScenarioReferences(...)` (or explicitly create its required records with `data()`) before opening the form. Read names and IDs from that fixture context; do not paste names or codes from an existing tenant.

## Verification on 2026-10-18

Seven representative scenarios passed: persisted fixture relationships and cleanup; Agent basic data; Agent barcode search; Agent category selection; Agent explicit item selection; Customer basic data; Supplier basic data. The final UI verification run passed all four selected cases with no errors or skips. Static checks confirmed that all 80 Dealer scenarios are enabled and that the five classes contain no tenant host literals, fixed barcode searches, or arbitrary first/last-row edit/delete calls.

This verifies data setup and representative use in the configured environment. The entire 80-scenario business suite was not rerun after the data refactor, and another tenant was not exercised. Earlier application failures such as Supplier PDF export remain separate from fixture portability.

Combined evidence: `target/dealer-data-report/index.html`. Final UI run: `target/dealer-owned-data-final.log`.

### Latest Customer failure recheck

Both reported Customer failures now pass: `AddCustomerThirdPage` checks the company flag's default before enabling it, keeps it enabled, and supplies the required customer address; duplicate-phone cancellation waits for the dismissed SweetAlert to disappear before closing the form and verifies that the cancelled record was not created. Form closing excludes SweetAlert buttons.

The targeted run passed all six tests (two Customer scenarios and four cleanup regressions), with zero failures, errors, or skips. Scenario cleanup also passed. Cleanup handles an already committed DELETE whose verification GET returns the ERP's empty-string response, retains blocked parents, and only deletes owned records in dependency order. Evidence: `target/dealer-customers-two-final.log` and `target/surefire-reports/testng-results.xml`.

### Classification failure recheck

The five reported Classification failures shared a fixture setup error: `seedRecord` used the searchable Vue selector for the standard classification-type dropdown. It now uses the same standard dropdown helper as the passing classification creation scenarios. The complete `CustomerClassificationTest` class passed all 14 tests, with zero failures, errors, or skips; all cleanup hooks passed. Evidence: `target/classification-final-recheck.log` and `target/surefire-reports/TEST-DealerModelTest.CustomerClassificationTest.xml`. Original failing results are preserved in `target/classification-last-run/allure-results`.

### Supplier loading and PDF recheck

Basic-data tab activation now retries actual clicks while a transient loading overlay intercepts them, within a 20-second timeout. It does not bypass the overlay with JavaScript. Supplier list structure, cancelling supplier edits, and Excel export passed. Supplier PDF still fails in the deployed application with `TypeError: this.hide is not a function`; the Supplier export helper matches the working Customer export path, but no PDF is produced. Application frontend sources are absent from this repository, so this application defect remains unresolved. All scenario cleanup hooks passed. Evidence: `target/supplier-latest-recheck.log`; original results: `target/supplier-last-failures/allure-results`.
