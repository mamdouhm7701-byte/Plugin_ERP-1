# Module fixture lifecycle

Approved direction: every business module prepares, isolates and cleans its own test data like sales and purchases. Individual test methods must run without earlier methods or pre-existing business records.

Use the existing authenticated application API, current-origin/current-branch guards, unique case names, durable pending-write journals and reverse dependency cleanup. Preserve UI business assertions, including UI create/edit/delete coverage. Do not delete unrelated records or hide cleanup failures. Keep existing sales, purchase, products, dealers, warehouse, financial and special-sales fixture owners where they already meet this contract.

The missing basic-data and general-accounting coverage will use a shared TestSupport.BasicDataFixtures owner and TestSupport.BasicDataScenarioTest lifecycle. Stable prerequisites are lazy; mutable targets and accounts are case scoped. Capture owned UI saves as well as API-created prerequisites. Recovery accepts an explicit journal suffix in its original origin/branch.

Approaches considered: duplicate each module's owner (repeats safety logic); reuse sales payloads directly (couples unrelated modules); add a small owner using existing FixtureSession/PendingFixtureWrites/FixtureJournals (selected to match existing patterns).

Scope includes basic data, treasury handovers and journal vouchers, plus an audit of remaining runnable modules. Verification uses offline cleanup/isolation tests, compilation and the offline CI suite; live validation is recorded separately and only claimed if executed.
