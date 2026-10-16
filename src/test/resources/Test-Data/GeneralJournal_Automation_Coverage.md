# General Journal Automation Coverage

The attached workbook contains 40 cases. The automated suite ran 29 enabled tests successfully (0 failures). It covers journal draft calculations, account selection, notes, save/print/edit/delete lifecycle, tax settlement checkbox, and marked save attempts. Each marked test voucher that the UI accepted was deleted immediately. The final list check also found and removed one older `AUTO-JV-` record left by an earlier lifecycle run, then confirmed no `AUTO-JV-` records remain.

| Workbook IDs | Coverage | Status |
|---|---|---|
| JV-001–005, JV-008, JV-018 | Balanced/unbalanced draft totals, adding rows, edits, and decimals | Enabled; draft only. |
| JV-002 | Unbalanced save | Enabled; marked attempt; deletes the test voucher if accepted. |
| JV-006 | Zero-value voucher | Confirmed frontend defect: the UI accepted and saved the zero-value test voucher. The automation deleted it immediately. |
| JV-007 | Negative amount | Confirmed frontend defect: the UI accepted a balanced voucher with debit -100 and credit -100. The automation deleted it immediately. |
| JV-009 | Debit and credit on the same row | Confirmed frontend defect: the UI accepted debit 100 and credit 100 on one row. The automation deleted it immediately. |
| JV-010–016 | Search, select, and close existing account picker | Enabled; does not create accounts. |
| JV-017 | Add a new account | Excluded; account setup belongs to its separate screen and is outside this suite. |
| JV-019 | Enter amount without selecting an account | Excluded; the UI does not expose amount fields until an account is selected. |
| JV-020, JV-022 | Tax settlement checkbox | Enabled; checks and unchecks the checkbox shown for the 15% VAT account. Does not inspect downstream settlement results. |
| JV-021 | Tax return integration | Excluded; tax return behavior is outside the requested scope. |
| JV-023–025 | Tax distribution | User rule: 115 inclusive of 15% VAT must split into 100 base plus 15 VAT. The inspected voucher form exposes no tax-distribution action or base/tax fields, so this behavior remains unverified. |
| JV-026–027 | Missing-data and duplicate tax distribution | Excluded as unspecified scenarios. |
| JV-028 | Voucher date | Enabled; checks the default and browser validity. The field is read-only in the inspected UI. |
| JV-029 | Invalid voucher date | Excluded as an unspecified scenario. |
| JV-030 | Empty voucher | Enabled; confirms the form remains open after a blank save attempt. |
| JV-031 | Reference | Excluded; reference is outside the requested scope. |
| JV-032 | Header and row notes | Enabled; checks draft input. |
| JV-033–034, JV-036, JV-038 | Save, list, reopen, edit, save and print, compare printed amounts, remove a draft line, and cleanup | Enabled in `SavePrintEditAndDeleteTestVoucher`; deletes the marked test voucher. |
| JV-035 | Duplicate submission | Excluded as unspecified behavior. |
| JV-037 | Remove draft row | Enabled; verifies totals update. |
| JV-039 | Limited user permissions | Excluded; testing uses the authorized admin account. |
| JV-040 | Account search in voucher rows | Disabled; the visible search accepts text but does not filter the rows in the inspected UI. This remains a known frontend defect. |

No test case marked Unknown remains in the Java test class. The original workbook is unchanged.
