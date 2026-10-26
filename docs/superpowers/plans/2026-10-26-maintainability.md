# Maintainability implementation plan

Goal: consistent formatting, corrected Java names, observable text-entry failures and an independent treasury oracle.
Architecture: preserve application behavior and existing suite coverage while migrating all internal references together. Use the existing Java 21/Maven CI gate.

- [x] Add Spotless (AOSP Java formatting, tracked text hygiene) and bind check to verify.
- [x] Rename Util -> Util, Listeners -> Listeners, ElementActions -> ElementActions, WaitManagement -> WaitManagement, Firefox -> Firefox, typeText -> typeText, typeTextAndVerify -> typeTextAndVerify; update Java/XML/scripts/docs and file paths.
- [x] Extract text entry into a focused helper; retry only Selenium interaction failures, preserve final cause, redact values, verify writes. Audit silent catches, distinguish expected polling misses from hidden errors.
- [x] Derive treasury expectation from known opening balance and no owned movements; compare both endpoint ledger and UI/print to the independent expectation. Test independent arithmetic locally.
- [x] Apply formatter, run full CI contracts, secret policy and stale-reference scan; record limitations.

Constraints: no live ERP mutations or outgoing messages; no commits/push requested. Keep existing workspace changes. No blanket logging of credentials or DOM.
Ruling: user explicitly selected the recommended approach; continue without another design approval.

Execution: text-entry false-positive regression observed failing, targeted tests then passed. Treasury opening-only oracle is explicitly limited to no movements. 95 empty/sentinel-only main catches audited; compound fallback blocks are not claimed fully audited. ElementActions reduced to 256 lines. No IDE rename tool available; migration verified by compilation and suite contracts.
