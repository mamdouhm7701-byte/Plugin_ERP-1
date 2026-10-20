# Fixture lifecycle validation

Validation after completing purchases and applying page-level cleanup to Sales, Products, Stores, Financial Movements and Dealers.

Local cleanup/lifecycle invocations: **125**, results: **125 passed**.
Latest targeted UI cases: **10**, results: **1 failed**, **9 passed**.

| Local group | Passed invocations |
|---|---|
| DealerModelTest | 11 |
| FinancialMovementsTest | 16 |
| Invoice | 46 |
| ProductsTest | 20 |
| StoreTest | 32 |

Original public methods removed across the 67 recorded source files: **0**. Failed configuration hooks for selected results: **0**. Nonempty newly produced fixture/attempt/page-record journals: **0**. Whole-project test compilation passed. Historical journals that predate this change were not deleted or claimed as recovered.

| Screen | Case | Latest result | Evidence |
|---|---|---|---|
| DealerModelTest.AgentTest | `AddAgentByAllProducts` | passed |  |
| DealerModelTest.AgentTest | `AddAgentWithChoseItem` | passed |  |
| FinancialMovementsTest.CustomerReceiptTest | `AddResetWithSaveTest` | passed |  |
| FinancialMovementsTest.CustomerReceiptTest | `FM_CancelEditWithoutSaving` | passed |  |
| Invoice.SalesOrderTest | `cashDocumentUsesOwnedPayment` | failed | Expected customer balance delta -300, actual 0 |
| Invoice.SalesOrderTest | `savesOwnedItemFromPicker` | passed |  |
| ProductsTest.AddProductsTest | `PR_SimpleProduct` | passed |  |
| ProductsTest.AddProductsTest | `PR_StockBalances` | passed |  |
| StoreTest.TransBetweenStoreTest | `SM_DecimalQuantity` | passed |  |
| StoreTest.TransBetweenStoreTest | `SM_RemoveItem` | passed |  |

The cash sales-order failure reproduces the previously documented application defect: an owned cash order persists its payment, but the customer balance changes by 0 rather than a credit advance of 300. Its assertion remains enabled. See [Sales application findings](../sales/KNOWN_BUGS.md). It is separate from the five purchase defects in [Purchase findings](../purchases/KNOWN_BUGS.md).

The first financial cancel-edit attempt exposed a harness issue: the view temporarily clears its branch during a full reload. The final guard waits for the same branch to hydrate, while immediately refusing another non-null branch or a changed origin. The added local regression and fresh UI rerun establish the correction.

The two Dealer cases were rerun using the final complete-list cleanup, which verifies exact owned identities beyond the visible table page and rejects ambiguous names. New local tests exercise hidden list pages and duplicate ownership markers.

Local APIs and browser lifecycle harnesses are not live screen coverage. The targeted UI run exercises two cases per selected page/workflow group; it is not a rerun of every UI case in the five module suites. Original case names and coverage remain enabled.

Evidence: latest selected results and their attachments are collated in `target/lifecycle-latest-allure`. Source run folders are `target/lifecycle-hydration-local-results`, `target/lifecycle-ui-results`, `target/lifecycle-followup-results`, and `target/lifecycle-hydration-results`. Group boundaries, recovery commands and deliberate per-case deletion exceptions are in [Page fixture lifecycle](PAGE_FIXTURE_LIFECYCLE.md).
