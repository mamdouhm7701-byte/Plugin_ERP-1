# Application defects observed during sales verification

## Cash sales order does not affect the customer's balance

Confirmed by `SalesOrderTest.cashOrderRecordsCustomerAdvanceWithoutStockMovement` on 2026-10-19.

- Create a new owned customer, item, store, bank and payment method.
- Enter a cash sales order with quantity 3, price 100 and an actual Other payment of 300.
- Save through the UI and read the saved order and customer through the real API.
- The order persists as Cash with net 300, paid 300 and the owned payment method/value.
- Stock is unchanged, which is correct.
- The customer's signed balance changes by **0**, while the business rule requires the cash payment to affect the customer's balance (the test expects a credit advance of 300).

The assertion remains enabled. Before/after customer DTOs and the persisted order are attached to Allure in `target/sales-verification-results` and reproduced in `target/sales-final-contracts-results`. The order DTO reports `isAdvancePayment: false`; the application team should check whether the cash-order UI/API should post the payment or set the appropriate advance-payment flag. This repository contains automation code, not the ERP implementation, so the application behavior has not been patched here.

The page-fixture lifecycle verification reproduced the same cash-order balance failure with a per-case customer, bank, payment method and accounts: `SalesOrderTest.cashDocumentUsesOwnedPayment` in `target/lifecycle-ui-results`. The credit-order case passed and final group cleanup succeeded. See [Lifecycle validation](../testing/LIFECYCLE_VALIDATION.md); this targeted check does not replace the earlier full sales validation.

## Harness issues corrected

These are not application defects: customer fixture flags must use `salesReturn` and enable `quotation`; dropdown options can decorate the customer's name; totals/save controls are outside the form; horizontally scrolled quantity headers require their actual text content. Failures from earlier runs must be replaced by reruns after these fixes, not counted as product bugs.

Further corrections: numeric fields are overwritten without an intermediate empty value; picker entry waits for the modal to close; item readiness follows the actual owned-store lookup rather than assuming a particular cache implementation. Discount scenarios create their own customer with an adequate discount allowance and explicitly enter the initial zero discount. List text filters use the application's native Enter handler. Linked returns assert the inherited owned agent instead of trying to edit its locked field. Source/funding invoices use the line's total cost and settle their stock effect before a return baseline is taken.

Page cleanup now runs once per page group, with independent items, customers, customer accounts and document markers for each case. Stable page prerequisites are shared. Branch hydration is awaited without weakening the foreign-origin/branch refusal. Sales tests use a desktop viewport so responsive collapsed inputs do not produce false failures. Account recovery uses the application's actual account-tree endpoint rather than the unsupported paginated Accounts route.

The list's initial provider request must settle before selecting filters or changing page size. A live rerun confirmed that waiting first produces requests containing the owned dealer ID and exactly the two owned rows. Earlier list timeouts from changing filters during initial loading are harness issues, not confirmed application defects.

## Barcode API fallback throws when the item is absent from the cache

Observed while adding an owned item's barcode on the real sales invoice screen. The API returned the exact owned item, but no invoice row was added. The browser captured `TypeError: Cannot read properties of undefined (reading 'costPrice')`.

Read-only inspection of the served component shows that the fallback adds the main unit to one unit array, then looks up its cost price in a different array that can still be empty. No application code or cache was patched. The normal positive barcode case waits for the actual item lookup to finish. A separate enabled regression, `barcodeApiFallbackAddsOwnedItemAbsentFromCache`, creates a new owned item after the first lookup and exercises the API fallback deliberately. Its post-fix execution is recorded in the validation summary.

The final contracts rerun reproduced the same `costPrice` exception in both barcode cases after the real lookup returned the owned item. Evidence is in `target/sales-final-contracts-results`; this is no longer a cache-readiness timeout in the harness.

## Zero quantity rerun

The earlier full-suite run reported a committed quotation during the zero-quantity scenario. After stabilizing native numeric entry (focus, select-all overwrite, blur), the final `QuotationTest.zeroQuantityCannotSave` rerun passed, including the persisted-document identity check. The historical failure is not listed as a confirmed outstanding application defect.

## Confirmed business clarification

Saved normal sales invoices cannot be edited or deleted. Their read-only fields and print-only controls are expected, not a defect. The original positive invoice edit scenario has been replaced with an immutability assertion. This restriction does not apply to quotation, sales order or return, as confirmed by the user. POS and restaurant workflows are separate.

## Final verification on 2026-10-19

The selected latest results cover all 169 declared invocations: 165 passed and four failed, with no broken or unexecuted cases. The four failures reproduce the two application defects above: two normal-invoice barcode contracts and two cash-order balance contracts. Search by name/barcode in quotation, order and return passed. All 18 list invocations, including actual PDF and Excel exports, passed.

All fixture/attempt journals are empty, final page cleanup hooks passed, the original 66 method names are preserved, Store sources match their pre-sales hash baseline and whole-project test compilation passed. Detailed evidence is in VALIDATION.md and target/sales-latest-allure.
