# Warehouse execution findings

## Confirmed business defect

An Exchange Order created with a newly generated agent persisted the selected agent in `selectedDestinationId` but also persisted the branch's existing customer in `dealerId`. The business rule confirmed by the user is that no default party is allowed: an agent document must contain its selected agent only. Dedicated primary-party, agent and destination identity cases cover Exchange and Addition. No assertion was changed to accept the default customer.

Reproduced again by `ExchangeOrderTest.SM_AgentIdentity` using the item picker in `target/store-interactions-corrected-results`. Addition's agent identity case passed, so this result is specific to the observed Exchange path.

The ERP application source is not part of this automation repository. These tests detect the persisted-data defect; they do not repair application code.

## Name-search cache defect

Name autocomplete did not display the newly created item in several live runs, although authenticated item lookup returned it. Inspection of the loaded application's methods showed that `cachedItems` loads sales items for `currentBranch.defaultStoreId` into session storage, while Addition's `onInputChange` searches that cached array whenever it exists. Selecting the test's own different warehouse does not make this path query its items. This explains the observed stale autocomplete data on that path. Dedicated name cases remain enabled; tests do not replace the cache with fabricated suggestions or change the branch default. Evidence: `target/store-inspect-addition-order.json` and `target/store-name-lookup-evidence.json`.

Reproduced by `AdditionOrderTest.AddAdditionOrderTestByName` in `target/store-interactions-corrected-results`: the session cache existed, the owned item was absent from it, and no item lookup was made.

Exchange barcode also failed to add the owned item with no observed item lookup; its precise cause still needs separate confirmation.

## Corrected test assertion

The initial barcode calculation run compared the DTO's `price` (transaction cost on these screens) with the visible sale price. The assertion now reads `salesPrice`. Reruns for Addition, Manufacturing, Damage and Transfer passed both displayed-total checks, persisted-price checks and stock effects. Picker search now uses the actual Arabic-name field attribute rather than an assumed static ID. Missing-store validation follows the empty form path because adding a line locks the store selector.

## Interrupted-run cleanup

An old interrupted create left an unjournaled store linked to two journaled test accounts. Cleanup correctly retained those accounts when the server rejected deletion. Recovery matched the store's exact generated name and its owned account reference, deleted the store first, then deleted the accounts. An interrupted transfer was recovered and cleaned separately. Fixture create attempts are now journaled before the request; four regression cases cover lost store-create responses, lookup failure, changed account references and resuming from disk.

## Verified execution

- Focused verification on 19 October 2026: **44 distinct cases**, **42 passed**, **2 failed** for the application defects above. This combines the latest result per class and method; it is not a full 160-case run.
- `target/store-cleanup-master-recovery-results`: all 29 cleanup regression cases passed.
- `target/store-calculations-corrected-results`: 25 cleanup cases plus four screen calculation cases passed.
- `target/store-interactions-results`: edit, owned-document deletion, zero quantity, PDF, Excel and Addition agent identity passed. The two test defects from that run were corrected and rerun separately.
- `target/store-interactions-corrected-results`: item picker, save and print passed; the name-cache and Exchange-party defects above were reproduced. The missing-store case received a further correction for the locked selector and has its final result in `target/store-final-checks-results`.
- `target/store-final-checks-results`: missing-store validation and Exchange cost/sale calculations passed. Calculation and stock-effect checks have therefore passed on all five screens.

All known live fixture and uncertain-attempt journals were empty after final cleanup, including the recovered interrupted-run store and transfer. The other ERP module sources and shared `AbstractForStore` were unchanged.

The complete 160-case suite has not been run as one successful suite. The consolidated verification summary is `target/store-validation-summary.md`; it reports the latest result per class and method across the listed focused runs, rather than presenting them as a full-suite pass.
