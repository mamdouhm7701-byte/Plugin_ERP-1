# Business run isolation and activity coverage implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create one fresh API branch per business run/configuration, preserve the legacy runner, and finish qualifying business journeys with explicit evidence.

**Architecture:** The native Business runner owns a unique run directory and creates one branch before tests begin. Tests in the run share immutable branch policies and own their transaction data. Existing explicit profiles remain usable for diagnosis; automatic Business runs never search or reuse them.

**Tech Stack:** Java 21, Selenium, TestNG, Maven, Spotless, authenticated ERP API.

**Spec:** User-approved discussion in this chat: one branch per run/configuration, not per test; independent case data; preserve existing behavior; execute inline without repeated approval requests.

## Global constraints

- Do not reset existing edits, commit, push, or change WebData.properties.
- Live branch provisioning currently uses the qualified Cloud/Demo environment.
- Keep a durable creation intent before API writes; unresolved responses cannot cause a second create for the same run.
- Never edit historical branch policies or force disabled controls.
- Keep real product discrepancies visible; screen access alone is not business coverage.
- Branch-wide assertions run serially; no unproved parallel isolation.

## Review focus

- Two runs with identical choices must create distinct branch identities.
- Re-entering one run with an unresolved API response must not duplicate its branch.
- No test may start before branch readback and selected-session verification.
- Linked concrete invoices need a server-supported relationship readback, not a guessed UI field.
- Cleanup can delete only exact owned records and must retain recovery journals on failure.

## Execution ledger

Ruling: Continue in the shared checkout — required prior edits, live fixture journals and user IDE paths already live here; a fresh checkout would omit them. No worktree or branch change.

### Task 1: Fresh branch per native Business run

- [x] Pin creation identities to the unique run directory with a failing regression test for repeated identical choices.
- [x] Remove matching-profile reuse from automatic preparation; preserve explicit-profile runner behavior and pending-intent protection.
- [x] Update runner documentation and verify local unit/format checks.
- [x] Execute two real preparations with identical choices and record distinct server branch IDs and matching policy readbacks.

Evidence: fresh-business-branch-red.log reproduced reuse; fresh-business-branch-green.log passed7 local tests. fresh-business-preparation-proof.json records distinct live branches8ba1bfae9927cb5c and69e1d07c046e82bf for identical choices, verified in19.4/13.2seconds. These are complete preparations, not per-case overhead.

### Task 2: Concrete persisted relationship and journey qualification

- [x] Inspect the native edit/load API contract and identify a supported saved relationship readback.
- [x] Verify exact source reservation, delivery, invoice identity, independently calculated totals and stock effects.
- [x] Run all concrete cases with successful owned cleanup before enabling them in Business checks.

Native contract ruling: the invoice edit GET omits delivery IDs and the UI never reloads that array. Verify the exact IDs in the submitted invoice, persisted document identities, reopening, and the native server billing-availability list: the exact owned delivery is present before billing and absent afterward for its independent customer. Do not assert a field the native endpoint never returns. The linked journey passed in fresh-business-concrete-results and business-complete-concrete-results; complete-suite qualification is tracked separately because master/route setup failures must not be hidden.

Progress: Public fresh branch3/3 passed; Construction fresh branch5/5 passed; Restaurant qualified fresh branch5/5 passed with empty fixture journal341ca3f3-33a. Showroom dedicated owned receipt-to-chassis-sale journey passed both discount modes in the original runner: verified-business-car_showroom-live.log 5/5. Concrete original runner passed20/21: all3 stock journeys,11 routes and5 master lifecycles; Drivers SVG action target corrected from native render contract, awaiting final live rerun.

### Task 3: Remaining activity and policy contracts

- [x] Audit the route matrix against existing business journeys, distinguishing CRUD, financial behavior and navigation-only checks.
- [ ] Add missing behavior using observed native contracts; keep unknown business rules explicitly unresolved rather than inventing expectations.
- [ ] Exercise supported financial policy combinations with independent fixtures; verify inventory and accounting separately.
- [ ] Qualify activity-specific journeys and expand native suite selection only with live evidence.

### Task 4: Compatibility and evidence

- [x] Run the local CI suite, formatting, coverage-matrix tests and appropriate targeted live compatibility suites (full levels deferred by user).
- [x] Request one read-only review of final changes and resolve important findings.
- [ ] Update verified coverage, remaining external blockers and run instructions without claiming unexecuted coverage.

No commits are planned: the user has not requested a commit or push.

### Final verification checkpoint

- Local CI: target/business-final-ci.log,738 passed,0 failed/errors/skipped; Spotless check passed. Six coverage-builder tests passed; matrix450 rows with explicit partial evidence.
- Added exact owned showroom sale/color receipts after verifying their recovery journals are empty. Added concrete stock and five master receipts; explicitly identify the independent Drivers failure in their evidence scope.
- Native Drivers render binds edit/delete to SVG data-action-type itself, not its parent. Selector corrected using read-only native inspection (target/driver-action-contract.json).
- At09:35 the live rerun was blocked by the user's IntelliJ TestNG run started09:28. User explicitly requested leaving it running. Do not terminate it or bypass the live gate. Continue independent review; rerun concrete when it releases the lease.
- Full policy Cartesian live coverage, accounting postings, contract settlement/additional terms, and all activity screens remain unqualified. Existing business discrepancies remain red.

Concrete final qualification: target/final-business-concrete-live.log21/21 passed; exact owned journals f9cbb27b-020 (basic) and5b1d36d7-d32 (sales) all empty. Native keyboard menu activation resolves long menus and dashboard toast overlays. Local CI738 passed in target/business-keyboard-ci.log. User confirms contracts convert partially or fully to invoice; implementing that next. Public twelve-combination live qualification started.

### Confirmed construction scope added during qualification

User confirms: contracts convert to invoices partially or in full. Additional terms are added by editing the same contract and carry into its invoice.

- [x] Inspect native source conversion: invoice-list routes to pos-invoice with sourceTransactionId and sourceTransactionType=Contract; native loader subtracts consumedQuantity.
- [x] Implement ConstructionContractConversionTest: partial then remainder, entire quantity in one invoice, and same-ID additional terms edit followed by billing; independent VAT, stock and customer balance checks.
- [ ] Qualify all three new cases live, verify exact owned cleanup, then select the class in the original Construction Business plan.
- [ ] Update accepted evidence after original suite passes. Do not count a compile or unselected test as live coverage.

Public qualification runs serially through target/qualify-public-policies.ps1. Status target/public-live-policy-matrix.json; per-run stdout and Allure target/policy-public-*. The wrapper stops on the first failed run. target/build-public-policy-evidence.py validates full3/3 status and owned empty journals before exporting receipts. The contract trial uses target/ContractConversionLiveProbe.java with compiled probe/new test in target/contract-conversion-live-classes. Run only after the live gate is released; no concurrent write runs.

### Branch lifecycle correction requested by user

The accumulated per-run branches exhausted the tenant license. This was an implementation omission: test data cleanup did not delete the run branch. User explicitly requested cleanup.

-39 registry-owned branches deleted with exact ID/origin/both-name checks and server readback; no reference or historical branch selected by prefix search.
-One branch had pending SpecialSales journal1f9209bd-0b8. Resume its original profile first; both journals now[]. Delete only afterward.
-BusinessBranchCleanup now runs before lease release, validates registry provenance and origin, refuses master/mismatches/pending or malformed fixture journals, verifies server absence and updates durable stateDELETED. Cleanup failures remain visible.
-743 local tests passed including two cleanup gate tests. Full live lifecycle proof running target/branch-lifecycle-proof-live.log. Historical profiles/evidence retained locally; automatic runner still never reuses them.

Branch cleanup completed:47 accumulated owned branches deleted and readback verified (40 registry-owned plus7 older creation-provenance branches). Pending restaurant journal recovered and empty first. Full native lifecycle proof3/3 passed and its new branch automatically deleted. Evidence docs/testing/evidence/owned-branch-cleanup-summary.json. LocalCI743/743. Historical reference profiles now intentionally point to deleted resources and are not live reusable targets.

### Minimum restaurant additional tax — latest user rule

Confirmed unit floor25: net unit10 ->additional25, VAT on35; quantity3 ->additional75, VAT15.75, net120.75. Inclusive11.5 ->net unit10 and same totals. Keep previous discount-before-additional and item-subject/VAT contracts.

-Local failing examples reproduced old proportional-only result (target/additional-minimum-red.log,7 failures).
-Calculator now computes max(proportional additional on discounted line base,25×quantity); this avoids early unit division rounding and preserves non-subject/disabled gates.
-753 localCI passed, target/additional-floor-ci.log. New saved-invoice test RestaurantMinimumAdditionalTaxTest with quantities1/3; automatic Business selection when additional enabled.
-Live trial serially targets NET_BASED,PRICE_INCLUDED,ITEM_BASED using target/MinimumAdditionalTaxLiveProbe.java; output target/minimum-additional-*-live.log. Active tool session93993. Each run uses automatic branch cleanup; stop on actual failure, do not suppress it or continue creating branches.

Minimum floor live completed:NetBased3/3,PriceIncluded3/3; four invoices saved/read. ItemBased1profile pass+2business failures:additional25/75 correct, VAT3/9 versus5.25/15.75, net38/114 versus40.25/120.75. All three fresh branches deleted via lifecycle cleanup, including failing run. Evidence persisted under docs/testing/evidence; do not mark ItemBased qualified. Coverage-builder six tests green.

### Independent final safety review and remaining activity coverage

Skill-directed read-only reviewer business_final_safety_review found two lifecycle issues, both reproduced locally and fixed:
-Confirmed create could precede preparation failure without registering cleanup. Preparation now invokes ownership callback immediately after persisted confirmed ID; listener output exists before prepare. Fault-path regression confirms cleanup once and settings restored.
-Retained financial/audit journals were not in cleanup guard. Added dealer-page-records, posting-recovery, treasury-audit, treasury-audit-masters, treasury-audit-financial, inventory-audit-documents; owned retained history prevents deletion; foreign history does not. Red and green outputs target/audit-branch-guard-*.log.
-755 local tests passed target/review-lifecycle-green-ci.log; reviewer rechecked and found no remaining critical issues in these fixes. WebData modification is user-owned; leave it untouched.

UnderReview qualification: Public3/3 passed. Concrete native screen has no ordinary picker; barcode saves succeeded. Native UnderReviews GET omits isConcrete, while saved request sets it; verify exact sent kind plus document identity and reopen on concrete-under-review route. This is a readback-contract correction, not a product expectation change. Active trial target/verified-underreview-concrete-live.log currently needs requalification with corrected readback check. Prepared post-create failure cleanup live probe target/PreparationFailureCleanupLiveProbe.java; run only after active cleanup releases gate.

Post-create fault live proof passed: server branch157b8518e84c7cf6 created, injected verification failure before tests, cleanup deleted/readback confirmed. docs/testing/evidence/preparation-failure-cleanup-proof.json. Public under-review receipt accepted with whole3/3 green and branch deletion. Concrete save/no-stock passed; reopen empty-form diagnosis pending native component ID vs route params (target/diagnosed-underreview-concrete-results).

### Latest verification ledger

-Independent review completed; both actionable cleanup findings fixed with red-to-green tests. Preserve user-owned WebData configuration.
-If preparation and cleanup both fail, original preparation cause now remains primary with cleanup failure suppressed, verified by red-to-green regression. Full localCI756/756 (target/business-release-ci.log).
-Original expanded Public plan now has5 cases; current validation target/native-complete-public-results has5 passed, waiting final fixture/branch cleanup before acceptance.
-Original expanded Concrete plan has23 cases, including deliberate native under-review edit repro. Current sequential session31857 starts Concrete after Public completes. Expect no masking/skip of native UI failure. Verify branch deletion after failed suite.
# Native concrete report requests — 2026-10-28

- Added `CoverageTest.ConcreteReportRequestTest` to Concrete Business checks only. Six native preview submissions passed: cars, drivers, customer-project details/total, reservation details/total. The translated reservation menu title contains trailing whitespace; trimming the lookup fixed inspection without changing application state.
- Evidence exports only allowed report name, current branch, dates and cars/drivers flags. Signed report tokens stay in the browser. No report balance/content qualification is claimed.
- `target/concrete-all-native-report-requests-live.log`:6/6 passed; exact owned branch deletion verified. Durable receipt: `docs/testing/evidence/business/concrete-all-native-report-requests.json`.
- Exported passing unbilled-contract additional-terms proof and confirmed partial/full conversion remaining-quantity failures separately. Existing failing business oracles remain unchanged.
- Registry audit after report runs:58 DELETED,1 CREATE_REJECTED; no confirmed-created owned branch remains active in this registry.
- Final local verification: `target/business-report-final-ci.log`,757 tests,0 failures/errors/skips,BUILD SUCCESS; Spotless clean. Coverage builder450 rows and its6 tests passed; `git diff --check` clean. Exact passing Concrete under-review save receipt exported separately; its failing reopen is explicitly excluded.

## Rendered concrete reservation content — 2026-10-28

- Added `Invoice.ConcreteReservationReportTest`: native reservation2 at price100, rendered detail/total previews before delivery, native partial delivery1, then rendered previews again. Each report checks its exact owned customer/item and all six quantity/value cells on both item row and grand total; stock1000 then999.
- `TestSupport.ConcreteReservationReportAssertions` handles repeated two-level headers with page/column/row association. Five local checks reject wrong row values, wrong totals and headings from another page; partial delivery is independently reconciled.
- Routed this content test only to Concrete/NetBased Business checks. Other policies retain the six report-request checks; their rendered accounting values are not claimed qualified.
- `target/concrete-partial-delivery-reports-v2-results`:2 passed cases,4 rendered previews. Wait for exact owned branch deletion before accepting the durable receipt.
- `target/business-rendered-report-final-ci.log`:BUILD SUCCESS and Spotless clean; final counts and branch cleanup will be recorded after receipt acceptance.

### Accepted rendered report evidence and user-requested level integration

- Rendered reservation acceptance completed:2/2 live cases,4 previews before/after partial delivery. `docs/testing/evidence/business/concrete-reservation-rendered.json` verifies fixture journals empty and branch deleted. Local762/762 green, Spotless clean. Coverage rebuilt450 rows and6 builder tests green.
- User explicitly requested updating Smoke, Sanity, E2E and Regression, especially additional tax. Added canonical `BusinessLevelContracts` selection and deduplication; Business run levels retain all original selections and append only missing applicable contracts. E2E selects persisted additional-tax journeys, not the existing draft-only mixed/discount methods.
- Updated all four root XMLs with `BusinessLevelSuitePolicy` and recognized `business.level` metadata. Direct XML runs opt in via an explicit profile; restaurant profiles require explicit additional-tax true/false. Unprofiled direct runs preserve original cases. Native Run tests with an explicit profile uses the same business plan.
- Local routing validation: `target/business-level-routing-ci.log`:826 tests,0 failures/errors/skips,BUILD SUCCESS,Spotless clean. Includes60 restaurant selection combinations across5 levels/3 tax policies/2 discount modes/on-off, original-selection preservation, recursive Regression deduplication and direct XML policy idempotence. Full live executions of all four levels are not claimed.
- Registry audit:62 DELETED,1 CREATE_REJECTED, no active confirmed-created owned branch.

## User-selected environment, tenant and browsers — 2026-10-28

- Replaced the two-option Demo/current environment selector with Cloud/Test, an editable configured tenant list, editable application URL, optional run-only username/password, and a1–6 concurrent-browser maximum. Existing four-argument Selection construction remains compatible for earlier callers.
- Added `BusinessRunTarget`: validates the target, resolves existing or one-off credentials, applies Cloud tenant keys or TestURL/Test credential keys, redacts its display, and never writes credentials to shared files. All run overrides restore after cleanup/failure; target metadata and requested/allowed browser counts go to run-settings.json without credentials.
- Removed the old Cloud/Demo-only provisioning guard under the user's explicit request to run any tenant. Before creation, the authenticated origin must match the selected endpoint. Existing exact ownership, immutable policy readback, pending recovery and branch deletion guards remain in place.
- Added `BusinessSuiteScheduling`: reviewed independent classes only can overlap; configuration-mutating classes remain exclusive, methods/data-provider rows serial. Existing native plans receive the selected browser budget. Browser count is a maximum constrained by available RAM/CPU and selected independent classes, not a promise that every activity uses all requested browsers.
- Final local validation: `target/business-target-workers-release-ci.log`,836 tests,0 failures/errors/skips,BUILD SUCCESS,Spotless clean. Tests cover Cloud/custom tenant, alternate Test endpoint, run-only credentials, restoration before/after preparation failure, origin mismatch, preserved case selection and safe parallel partitioning.
- Read-only configured-catalog/plan probe found5 Cloud tenants and2 Test endpoints; a Smoke plan at maximum3 uses9 reviewed classes in parallel and all unreviewed cases exclusively. Durable local proof: `docs/testing/evidence/business-run-options-local-proof.json`. No live run on other tenants is claimed. Registry audit still shows no active confirmed-created owned branch.

## Completion pass and CI investigation — 2026-10-28 afternoon

- Ruling: Continue the already-approved implementation in this checkout; leave user IDE runs alone. Full level runs remain the user's deferred item2; targeted new-case qualification continues through the shared gate.
- Confirmed rules: no delivery fees; returns must refund original tax (user reports a defect); round final invoice net to2 decimals, retain intermediate precision. Restaurant calculator now preserves raw intermediate amounts; legacy general calculation remains compatible and a separate final-rounding contract was added.
- Native restaurant NetBased extended run:4 boundary recipes passed;2 precision recipes failed (57.59 vs57.58,115.18 vs115.17), and partial/full returns lost original minimum additional tax (23 vs40.25,69 vs120.75). Exact evidence and limits recorded in KNOWN_BUSINESS_ISSUES.md; no skipped or weakened expectations. Trial order assertions were corrected to persisted payment identity/value and one stock deduction rather than unrelated isComplete metadata.
- Public accounting Periodic and Continuous now both reconcile actual persisted source voucher account IDs and amounts against independent cost80×2. Posting requires complete native branch links; all27 observed account fields are populated with owned fixture accounts. Unposting retains canceled vouchers, so cleanup verifies exact source ID+canceled state and deletes only its own voucher before account cleanup. Both accepted runs deleted fixtures and branches; receipts public-periodic-accounting.json and public-continuous-accounting.json.
- Ruling: Preserve posting recovery if a posted source voucher cannot be identified; do not clear a journal simply because source isPosted became false after cancellation.
- Added restaurant boundary/order/linked-return cases, showroom linked-return case, general final-rounding case and explicit Public accounting case. Broad-level routing is activity-aware; Smoke keeps short applicable tax checks. New-case live qualification is tracked separately from class assignment.
- Route assignment audit rebuilt450 entries:326 assigned,64 specialized for another activity,60 outside existing scope. Assignment is not accepted live business coverage. Four new builder guards pass; existing6 activity coverage builder tests pass.
- PR40 revision9c9b81af66e31a498aba213f4e6d5beb38f8ae7e failed the same3 BusinessRunListenerTest cases on Windows and Ubuntu, caused by implicit dependency on local Demo credentials. Test-only target now uses explicit synthetic credentials.13 listener contracts passed with Demo credentials blank; full suite later853/853 green, Spotless clean in target/business-and-ci-current-validation.log. No commit/push or remote rerun was performed.
- IDE15:41 run selected configuration methods only in temp-testng-customsuite.xml, with no @Test. Allure summary had0 cases. Added a clear empty-selection diagnostic; regression observed RED then GREEN. Correct entry is Alt+Shift+F10 > ERP - Business run.
- Additional native restaurant PriceIncluded order run: all3 order types passed exact tax/payment/stock assertions; await group cleanup and branch deletion before exporting acceptance.
- Concrete operational report probe stopped selecting a global Project in reservations. Observed native render uses customerProjects and Customers/{id}/projects for that form; next inspect the native customer project binding before writing a fixture relationship. Probe fixtures and branch were cleaned; this is setup diagnosis, not a report arithmetic defect.
- Remaining: qualify showroom return; complete customer-project/car/driver rendered report content; finish corrected restaurant order evidence and additional-policy checks; final local verification and whole-change review. Full Cartesian/suite runs are not claimed.


## Final targeted completion checkpoint — 2026-10-28 18:21 Cairo

- Source/code validation:868 local tests,0 failures/errors/skips,BUILD SUCCESS and Spotless clean; target/business-and-ci-final-verification.log. Credential and maintainability checks:0 violations;6 activity coverage +4 completion catalog guards pass. No commit/push.
- Accepted native receipts now include Public periodic/continuous postings; Restaurant PriceIncluded Internal/External/Delivery; exact showroom linked chassis return;4 Concrete operational rendered reports. All corresponding branches and fixture journals cleared. Original2 reservation content cases passed after shared setup extraction; their combined compatibility run exposed a loading overlay in the new invoice picker, corrected with condition-based native retry and a fault-injection regression. The separate final operational run passed after this fix.
- One fresh read-only review found no Critical and one Important: report group identity was global. Regression reproduced the false acceptance (RED), then page/group binding and group-total boundaries corrected it (GREEN).13 report/overlay checks and4 actual native report snapshots passed; final native operational case passed. Minor final-status documentation refreshed here. No second reviewer or implementer agents used.
- Review rulings: full level runs remain the user's deferred item2; no active run interference; ERP product fixes outside this test repository remain recorded failures; additional accounting activity/policy/return combinations are not qualified by the two Public/NetBased receipts; commits/remote reruns/deployment excluded; read-only reviewer did not run tests while root performed verification.
- Additional true business discrepancy: Public drafts25.036 ->28.80 vs28.79; two items57.59 vs57.58. Recorded separately, stopped before save; cleanup verified.
- Ruling: history route registered but redirects home on the fresh Public child branch. Access requirements are unresolved, so retain standalone diagnostic but remove its automatic mandatory business routing; regression RED/GREEN protects legacy selection. Do not infer CRUD/business coverage from class existence or route registration.
- Original user IDE15:15 run was recovered from IDE history: API Network Error in RestaurantMinimumAdditionalTaxTest cleanup, then login setup timeout caused3 RestaurantAdditionalTaxSmokeTest cases to be ignored. Later15:41 rerun selected configuration methods only. Resumed only exact journal ff1a9446-e49 on its original Test origin/profile; all13 retained fixture records and branch e18b414710bc9e33 cleaned. Registry now85 DELETED,1 CREATE_REJECTED,0 confirmed-created active branches.
- Scope remains explicit:450 route/activity assignments are a code/coverage audit, not450 completed native business validations. Full Cartesian policy runs and all role/screen paths are not claimed; full Smoke/Sanity/E2E/Regression runs remain deferred by the user.
