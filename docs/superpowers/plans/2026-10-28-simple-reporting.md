# Simple reports and file explanations — 2026-10-28

User-approved scope: simplify all case descriptions and recorded comparisons, explain every page in plain Arabic, classify failures without pretending every test failure is an application bug, remove only proven disposable files.

- [x] Inspect existing895 descriptions,151 page/helper sources and failure rendering.
- [x] Rewrite descriptions without changing case keys, expected business rules or actual execution.
- [x] Add plain page purpose comments; retain helpers and manual report tools with proven uses.
- [x] Show concrete field names/expected/actual values in recorded assertions; preserve assertion behavior.
- [x] Add domain and cause classification, explicit known/new status; ambiguous failures remain unconfirmed.
- [x] Verify local reporting/case-catalog tests and fullCI; no live writes necessary.
- [ ] Delete only resolved workspace-local disposable cache/crash logs; preserve recovery journals, current evidence and tool dependencies.

Ruling: continue in existing user checkout, no branch/worktree/commit/push. Agent tasks were bounded to nonoverlapping resource/page trees and read-only file audit; root integrates and verifies. Existing manual RDL/DOCX report scripts are used/documented and must be explained rather than guessed unused.


Execution checkpoint:
- Existing895 descriptions rewritten in short Arabic with unchanged keys/schema; added23 native business descriptions, total918. Fixed the bank creation example against actual test data rather than falsely claiming a fixed IBAN/name. Added searchable generated HTML guide and regeneration script.
-151 page/helper class comments added, verified against git HEAD: no executable differences in that subtree. Kept400-line source budget by shortening one comment.
- ReadableChecks maps recognized business field names and boolean values. Native entry and field assertions record real expected/actual only inside active cases, excluding password inputs. Existing numeric comparisons retain arithmetic and failure behavior.
- FailureClassification adds domain/source/confirmation labels and a plain failure-summary section. Known requires explicit reference, skipped is not a bug, network and test execution are distinguished, unknown causes remain unresolved. Existing Allure categories translated to Arabic and explicit multi-segment case reference supported.
- Local focused validation27 checks passed; fullCI873 passed at19:49 before one additional uncertainty classification test; final full rerun in target/simple-reporting-final-ci.log includes it. No live ERP execution or settings/branch writes made for this task.
- File audit found documented manual RDL and DOCX report tools, retained with purpose comments. Disposable Python bytecode33,607bytes and19 ignored JVM crash/replay logs3,065,395bytes identified. Tool policy rejected the automatic deletion command before execution; no deletion attempted through another mechanism. FILE_PURPOSE_AND_CLEANUP_AR.md records the retained files and reason.
- Delegated description/page work terminated at agent usage limits after writing files; root audited and completed corrections, integrations, tests and documentation. No further agents launched.

Final evidence: target/simple-reporting-final-ci.log874 local tests,0 failures/errors/skips,BUILD SUCCESS;151 comment-only page changes independently compared with HEAD,918 descriptions parsed and reflection-checked. Formatting, maintainability and credential scans passed. Disposable cleanup remains explicitly blocked by tool policy; no files falsely reported deleted.

## User clarification — explanations inside every file

The user clarified that they will read the repository file by file, so page-only comments were insufficient.

- Added an Arabic purpose and benefit at the beginning of all553 Java files (552 under src plus the documentation inventory tool), including tests, calculators, browser helpers, data setup, cleanup, scheduling and reporting. No executable Java changes were made for this clarification; per-file normalized-content comparison was enforced while adding comments.
- Added native comments to127 scripts, settings, suite files, workflow/run configurations and report templates. Preserved XML declarations, shell shebangs, original PowerShell encoding where present and all configured values.
- JSON, CSV, images, PDFs and archive formats were not corrupted by invented inline comments. Added `شرح_الملفات.md` in their own folders, with a purpose for each listed file; no per-file companion proliferation.
- Validation target/every-file-comment-verification.json:13 existing Python files retain the same parsed operations;72 XML/RDL files retain the same structure;40 settings/script files retain all noncomment content. CI XML already includes the prior reporting test addition, accounted for explicitly rather than pretending it is a comment change.
- Final target/every-file-comment-final-ci.log:874 tests,0 failures/errors/skips,BUILD SUCCESS. Final Spotless apply/check passed in target/every-file-comment-final-format.log. Maintainability and credential scans passed; no live ERP operation, deletion, commit or push.
