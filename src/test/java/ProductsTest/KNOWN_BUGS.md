# Product application defects — 18 October 2026

Price-contract clarification on 25 October 2026: zero/zero/zero is valid, as is equality of all three nonzero values. Invalid bounds must show visible feedback beneath a price field AND prevent saving. `PR_AllPricesZero` now covers zero values with persisted read-back; price tests also verify the actual inputs after typing. Invalid-price checks read field-scoped feedback rather than treating a success dialog as validation. The results below are historical evidence from 18 October; see `docs/testing/PRODUCT_PRICE_BUSINESS_2026_10_25_AR.md` for the focused recheck.

The business owner confirmed **minimum <= sale <= maximum**, with equality allowed. Negative sale prices must be rejected. Every case creates its own prerequisites and product, verifies the saved entity by its captured ID, and cleans its own data in dependency order.

| Test | Minimum | Sale | Maximum | Expected | Observed |
| --- | --- | --- | --- | --- | --- |
| `PR_NegativeSalePrice` | 0 | -1 | 110 | Reject | Saved invalid values; test fails |
| `PR_MinimumAboveSalePrice` | 120 | 100.25 | 150 | Reject | Saved invalid values; test fails |
| `PR_MaximumBelowSalePrice` | 50 | 100.25 | 80 | Reject | Saved invalid values; test fails |
| `PR_MinimumAboveMaximum` | 120 | 100 | 110 | Reject | Saved invalid values; test fails |
| `PR_MaximumBelowMinimum` | 90 | 100 | 80 | Reject | Saved invalid values; test fails |
| `PR_SalePriceBelowMinimum` | 90 | 80 | 110 | Reject | Saved invalid values; test fails |
| `PR_SalePriceAboveMaximum` | 90 | 120 | 110 | Reject | Saved invalid values; test fails |
| `PR_AllPricesEqual` | 100.25 | 100.25 | 100.25 | Accept | Saved exact values; test passes |
| `PR_SalePriceEqualsMinimum` | 90 | 90 | 110 | Accept | Saved exact values; test passes |
| `PR_SalePriceEqualsMaximum` | 90 | 110 | 110 | Accept | Saved exact values; test passes |

All seven rejected combinations were actually persisted with the entered invalid prices. The `Unexpected saved product prices` JSON attachment includes entered values, the actual UI request and the API read-back response. Evidence: `target/products-price-boundaries-results`. These are application validation defects, and the tests intentionally remain failing.

Reproduction: create a product with unique names/barcode and the test-created category, main unit and store; enter a table row's prices and save. Expected: reject every invalid combination with relevant price validation. Actual: success and a persisted product. The three equality combinations correctly save and pass.

`ProductListTest.PR_ExportPdf` also fails in both the user run and the targeted recheck: clicking price-list PDF export produces `TypeError: this.hide is not a function` and no download. Product PDF and Excel exports pass after repairing list search. Export tests validate actual generated file signatures; they do not generate replacement files. Evidence: `target/products-latest-run-recheck-results`.

The ERP frontend and backend source projects are absent from this automation repository. These application defects cannot be repaired here. Tests preserve failure evidence rather than modify the deployed application or relax the confirmed business assertions.
