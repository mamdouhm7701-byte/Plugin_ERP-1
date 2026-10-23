# Module fixture lifecycle implementation plan

Goal: every runnable business test owns its prerequisites and mutable records, supports independent execution and cleans only owned data.

Architecture: retain existing module owners; add TestSupport.BasicDataFixtures and BasicDataScenarioTest for the missing basic-data/accounting modules. Reuse FixtureSession, PendingFixtureWrites and FixtureJournals.

Tech stack: Java 21, Selenium, TestNG, Maven.

Spec: docs/superpowers/specs/2026-10-23-module-fixture-lifecycle-design.md

## Constraints and review focus

Preserve user edits and all existing business assertions. Never rely on a first unrelated row, fixed account, bank, safe or user. Cleanup must retain parents on child failure, journal uncertain writes, reject foreign origin/branch and support retries. Keep tests sequential.

## Tasks

- [x] 1. Implement and test the shared owner and lifecycle in TestSupport. Public API: BasicDataFixtures.Record(endpoint,id,name,values); fixture(String key,String endpoint,Map values); account(String key); bank(int); safe(int); beginScenario(); close(). Base class exposes protected basicData() and ownedName(String endpoint,String label). UI request capture registers QA-BD records and uncertain saves. Test isolation, pending writes, exact identity cleanup, foreign session refusal, failure retention and idempotence.
- [x] 2. Convert BankTest, AreaTest, DisEntTest, RoutLineTest, PaymentTest to the base; generate UI names through ownedName, seed owned targets for read/edit/search, prepare dropdown dependencies lazily and preserve assertions.
- [x] 3. Convert CashTest, StoreTest, RevenuesTest, ExpensesTest, TaypeOfDiscountTest using the same contract; remove fixed account/bank references and best-effort-only cleanup.
- [x] 4. Audit and convert TreasuryHandoverTest and GenralAccountantTest.VouchersTest; ensure independent prerequisite creation, capture and recovery of business records.
- [x] 5. Add canonical basic-data/accounting suites and lifecycle/suite checks to offline CI; document standalone execution and recovery. Run focused tests and mvn -Pci verify, review final diff and resolve material findings.

Execute in the current checkout on branch TestCodeex to preserve the supplied workspace and its unrelated uncommitted edits. Independent adapter groups can be delegated with the shared interface above. Root owns integration, treasury/accounting and final validation. Do not run concurrent Maven processes against shared target journals.


## Execution ledger

- Shared core, basic adapters, voucher and dealer ownership migrated. 40 focused checks passed; subsequent full offline CI passed 429 cases with no failures or skips.
- User correction: treasury is closed and reviewed, never deleted. Closed/reviewed history and linked masters are deliberately archived for audit; this supersedes strict removal for treasury only.
- Treasury reconciles transaction totals, closing screen and printed report; mixed cash/other movement arithmetic and missing fields covered offline.
- Existing probe treasury 97aa46b1a8e89b90 closed/reviewed and its masters archived, preserving business history.
- Live focused validation underway; final outcome recorded in docs/testing/MODULE_FIXTURES_AR.md.

- Live recheck passed 3/3 with no skips; bank, independent journal totals and treasury close/report/review. Final offline revalidation runs after the UI fix and audit attachment.

- Final offline verification passed 430/430, no failures/skips, exit code 0. No unresolved treasury intents remain.
