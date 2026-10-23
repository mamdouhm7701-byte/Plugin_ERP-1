# Drive report delivery implementation plan

Spec: docs/testing/DRIVE_REPORT_DELIVERY_DESIGN_AR.md (approved).

Implementation stays in this checkout because the user's local mail settings and existing reports are needed for verification. Preserve unrelated changes and existing reports; do not run Regression again.

- [x] Add failing HTTP contract tests for upload, restricted recipient permissions, reuse, and upload failure; add mail routing tests for large attachments and unavailable Drive.
- [x] Implement a focused Drive API client using Java HttpClient, OAuth refresh tokens, resumable upload and a local delivery journal. Validate remote upload destinations and bound retries.
- [x] Route oversized reports to Drive, retain small attachments, and send an explicit degraded-delivery summary when upload/share fails. Save packaged reports for retry.
- [x] Add OAuth setup and resend entry points and PowerShell wrappers; document one-time Google Cloud prerequisites and GitHub secrets.
- [ ] Run local CI contracts and review changes; perform authenticated verification only when the user has provided OAuth client configuration and completed browser consent.

No public sharing, no secret values in logs, no automatic deletion from Drive. Test against a loopback HTTP server with dummy credentials. Record actual verification and remaining setup below.

Ruling: refuse custom parent folders instead of inheriting permissions — a shared parent could violate named-reader-only delivery. Final review identified this and verified the correction. Reuse fails closed when older recipients still retain permission; the guide explains manual revocation.

Evidence: observed red compilation for missing delivery/client types, then green focused tests. Added and observed failing parent-folder rejection regression, then fixed it. Added interrupted-upload/status-probe/partial-offset and sharing-denied cases. Initial full CI passed 403 cases; following changes passed 405 cases. Final suite rerun recorded in target/drive-final-ci.log. PowerShell entry points parse successfully; credential file paths are git-ignored.

Final-check limitation: concurrent unrelated edits to ExpensesTest, RevenuesTest and StoreTest import TestSupport.BasicDataScenarioTest, which does not exist in the checkout. The later full CI rerun fails test compilation for that reason. Do not claim that final full suite is green or restore another task's work. Build production and compile/run only the three mail/Drive test classes independently; output in target/drive-isolated-tests.log. This test execution uses a separate Allure results directory and no real SMTP/Drive credentials.

External remaining prerequisite: no config/Drive.local.properties or OAuth desktop JSON exists yet. No real Google upload or Drive-link email has been claimed; user must create a Desktop client and complete browser consent, then run the retry helper on the chosen same-run snapshot. Unrelated concurrent staged changes were left untouched.
