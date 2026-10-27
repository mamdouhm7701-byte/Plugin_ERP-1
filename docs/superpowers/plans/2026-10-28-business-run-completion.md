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

- [ ] Inspect the native edit/load API contract and identify a supported saved relationship readback.
- [ ] Verify exact source reservation, delivery, invoice identity, independently calculated totals and stock effects.
- [ ] Run all concrete cases with successful owned cleanup before enabling them in Business checks.

### Task 3: Remaining activity and policy contracts

- [ ] Audit the route matrix against existing business journeys, distinguishing CRUD, financial behavior and navigation-only checks.
- [ ] Add missing behavior using observed native contracts; keep unknown business rules explicitly unresolved rather than inventing expectations.
- [ ] Exercise supported financial policy combinations with independent fixtures; verify inventory and accounting separately.
- [ ] Qualify activity-specific journeys and expand native suite selection only with live evidence.

### Task 4: Compatibility and evidence

- [ ] Run the local CI suite, formatting, coverage-matrix tests and appropriate live compatibility suites.
- [ ] Request one read-only review of final changes and resolve important findings.
- [ ] Update verified coverage, remaining external blockers and run instructions without claiming unexecuted coverage.

No commits are planned: the user has not requested a commit or push.
