# Purchase validation

Latest result per declared class, method and data-provider arguments. Historical failures are retained separately.

Declared invocations: **139**. Results: **5 failed**, **134 passed**.

Nonempty fixture/attempt journals: **0**. Existing files changed outside purchase scope: **0**. Original public methods preserved: **25/25**.

| Case | Parameters | Latest result | Evidence |
|---|---|---|---|
| Purches.PurchaseCalculationTest.aggregateTaxRounding |  | passed |  |
| Purches.PurchaseCalculationTest.emptyCalculationIsZero |  | passed |  |
| Purches.PurchaseCalculationTest.exclusiveVat |  | passed |  |
| Purches.PurchaseCalculationTest.fixedDiscountWithInclusiveVat |  | passed |  |
| Purches.PurchaseCalculationTest.fixedDiscountWithoutVat |  | passed |  |
| Purches.PurchaseCalculationTest.fractionalQuantityAndRounding |  | passed |  |
| Purches.PurchaseCalculationTest.fullFixedDiscount |  | passed |  |
| Purches.PurchaseCalculationTest.fullPercentageDiscount |  | passed |  |
| Purches.PurchaseCalculationTest.inclusiveVat |  | passed |  |
| Purches.PurchaseCalculationTest.percentageDiscountBeforeExclusiveVat |  | passed |  |
| Purches.PurchaseCalculationTest.threeDecimalInclusiveRounding |  | passed |  |
| Purches.PurchaseCalculationTest.untaxedInvoice |  | passed |  |
| Purches.PurchaseCleanupTest.acceptsEmptyStringReadAfterCommittedDeleteTimeout |  | passed |  |
| Purches.PurchaseCleanupTest.acceptsMissingDeleteOnlyAfterReadConfirmsAbsence |  | passed |  |
| Purches.PurchaseCleanupTest.acceptsVerifiedMissingMasterDefaultDto |  | passed |  |
| Purches.PurchaseCleanupTest.acceptsVerifiedMissingMovementDefaultDto |  | passed |  |
| Purches.PurchaseCleanupTest.actualPageLifecycleDefersCleanupUntilGroupEndEvenAfterFailure |  | passed |  |
| Purches.PurchaseCleanupTest.boundsRolledBackDeadlockRetries |  | passed |  |
| Purches.PurchaseCleanupTest.checksIdentityBeforeFirstDeletion |  | passed |  |
| Purches.PurchaseCleanupTest.cleansOwnedItemCommittedBeforeCreateFailure |  | passed |  |
| Purches.PurchaseCleanupTest.cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems |  | passed |  |
| Purches.PurchaseCleanupTest.customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts |  | passed |  |
| Purches.PurchaseCleanupTest.deletesOwnedMovementBeforeItsItemsAndStores |  | passed |  |
| Purches.PurchaseCleanupTest.doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted |  | passed |  |
| Purches.PurchaseCleanupTest.interruptedCleanupJournalsOnlyRemainingOwnedRecords |  | passed |  |
| Purches.PurchaseCleanupTest.linkedReturnIsDeletedBeforeSourceInvoice |  | passed |  |
| Purches.PurchaseCleanupTest.negativeSaveDetectsACommitBehindFailedResponse |  | passed |  |
| Purches.PurchaseCleanupTest.pageFixturesAreSharedButCaseBalancesStayIsolated |  | passed |  |
| Purches.PurchaseCleanupTest.pageTransitionRetainsAndRecoversThePreviousFailedSave |  | passed |  |
| Purches.PurchaseCleanupTest.paidInvoiceIsDeletedBeforePaymentMethodAndBank |  | passed |  |
| Purches.PurchaseCleanupTest.receiptLookupUsesDetailsAndPreservesUnrelatedReceipts |  | passed |  |
| Purches.PurchaseCleanupTest.recoversInvalidItemByUniqueEnglishMarker |  | passed |  |
| Purches.PurchaseCleanupTest.recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts |  | passed |  |
| Purches.PurchaseCleanupTest.recoversLostResponseForEachSalesDocumentKind | PurchaseOrders, PurchOrder | passed |  |
| Purches.PurchaseCleanupTest.recoversLostResponseForEachSalesDocumentKind | Purchases, Purch | passed |  |
| Purches.PurchaseCleanupTest.recoversLostResponseForEachSalesDocumentKind | ReturnPurchases, ReturnPurch | passed |  |
| Purches.PurchaseCleanupTest.recoversSecondReturnWithoutConfusingKnownFirstReturn |  | passed |  |
| Purches.PurchaseCleanupTest.recoversSparsePurchaseListThroughVerifiedDetails |  | passed |  |
| Purches.PurchaseCleanupTest.recoversStoreCommittedBeforeLostCreateResponse |  | passed |  |
| Purches.PurchaseCleanupTest.recoversUiItemCommittedBeforeFailedResponse |  | passed |  |
| Purches.PurchaseCleanupTest.recoversUpdatedNoteAfterLostUpdateResponse |  | passed |  |
| Purches.PurchaseCleanupTest.recoversWarehouseCreateCommittedBeforeLostResponse |  | passed |  |
| Purches.PurchaseCleanupTest.refusesJournalRecoveryInAnotherBranch |  | passed |  |
| Purches.PurchaseCleanupTest.refusesJournalRecoveryInAnotherTenant |  | passed |  |
| Purches.PurchaseCleanupTest.refusesTrackingUnownedRecord |  | passed |  |
| Purches.PurchaseCleanupTest.rejectsMissingDeleteWhenOwnedRecordStillExists |  | passed |  |
| Purches.PurchaseCleanupTest.rejectsSalesDocumentWithChangedItemIdentity |  | passed |  |
| Purches.PurchaseCleanupTest.rejectsSalesDocumentWithChangedStoreIdentity |  | passed |  |
| Purches.PurchaseCleanupTest.removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges |  | passed |  |
| Purches.PurchaseCleanupTest.resumesPendingStoreCreationFromDurableJournal |  | passed |  |
| Purches.PurchaseCleanupTest.retainsBlockedParentsAndRetriesOnlyItsOwnRecords |  | passed |  |
| Purches.PurchaseCleanupTest.retainsStockDependenciesWhenMovementDeletionFails |  | passed |  |
| Purches.PurchaseCleanupTest.retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved |  | passed |  |
| Purches.PurchaseCleanupTest.retriesRolledBackDeadlockWithoutDuplicateFixture |  | passed |  |
| Purches.PurchaseCleanupTest.retriesUncommittedDeleteBeforeParentAccounts |  | passed |  |
| Purches.PurchaseCleanupTest.uncertainStoreLookupFailurePreservesItsAccounts |  | passed |  |
| Purches.PurchaseCleanupTest.uncertainStoreMustStillReferenceItsOwnedAccount |  | passed |  |
| Purches.PurchaseCleanupTest.uncertainWarehouseLookupFailureRetainsAllParents |  | passed |  |
| Purches.PurchaseCleanupTest.updatedRecordDeletesBeforeItsNewReference |  | passed |  |
| Purches.PurchaseCleanupTest.verifiesCommittedBranchUnlinkAfterLostResponse |  | passed |  |
| Purches.PurchaseCleanupTest.waitsForOriginalBranchHydrationBeforeFixtureAccess |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.cashDocumentUsesOwnedPayment |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.deletesOnlyOwnedDocument |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.emptyDocumentCannotSave |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.fixedDiscountRecalculates |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.fractionalQuantityPersists |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.missingStoreCannotSave |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.missingSupplierCannotSave |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.negativePriceCannotSave |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.negativeQuantityCannotSave |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.newClearsUnsavedLines |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.percentageDiscountRecalculates |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.removedItemIsNotSaved |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.savedDocumentCanBeEdited |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.savedDocumentReopensWithOwnedValues |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.savesAndPrintsOwnedDocument |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.savesOwnedItemByBarcode |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.savesOwnedItemByName |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.savesOwnedItemFromPicker |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.twoOwnedItemsPersist |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.unicodeNotesPersist |  | passed |  |
| Purches.PurchaseInvoiceCoverageTest.zeroQuantityCannotSave |  | failed | Invalid purchase was saved expected [true] but found [false] |
| Purches.PurchaseLinkedCoverageTest.linkedReturnKeepsOriginalQuantity |  | failed | Expected 3, actual 2 expected [0] but found [-1] |
| Purches.PurchaseLinkedCoverageTest.ownedOrderConvertsToPurchase |  | passed |  |
| Purches.PurchaseListCoverageTest.clearCodeRestoresSupplierScope |  | passed |  |
| Purches.PurchaseListCoverageTest.conflictingFiltersAreEmpty |  | passed |  |
| Purches.PurchaseListCoverageTest.dateRangeDoesNotLeaveStaleRows |  | passed |  |
| Purches.PurchaseListCoverageTest.exactCodeFilter |  | passed |  |
| Purches.PurchaseListCoverageTest.exportsExcel |  | passed |  |
| Purches.PurchaseListCoverageTest.exportsPdf |  | passed |  |
| Purches.PurchaseListCoverageTest.pageSizePreservesResults | 100 | passed |  |
| Purches.PurchaseListCoverageTest.pageSizePreservesResults | 25 | passed |  |
| Purches.PurchaseListCoverageTest.pageSizePreservesResults | 50 | passed |  |
| Purches.PurchaseListCoverageTest.pageSizePreservesResults | 500 | passed |  |
| Purches.PurchaseListCoverageTest.referenceFilter |  | passed |  |
| Purches.PurchaseListCoverageTest.sortKeepsOwnedDocuments |  | passed |  |
| Purches.PurchaseListCoverageTest.supplierFilterIsOwned |  | passed |  |
| Purches.PurchaseListCoverageTest.unmatchedCodeClearsRows |  | passed |  |
| Purches.PurchaseOrderCoverageTest.cashDocumentUsesOwnedPayment |  | passed |  |
| Purches.PurchaseOrderCoverageTest.cashOrderCreatesReceiptWithoutStockMovement |  | failed | Cash purchase order did not create a supplier receipt expected [false] but found [true] |
| Purches.PurchaseOrderCoverageTest.deletesOnlyOwnedDocument |  | passed |  |
| Purches.PurchaseOrderCoverageTest.emptyDocumentCannotSave |  | passed |  |
| Purches.PurchaseOrderCoverageTest.fractionalQuantityPersists |  | passed |  |
| Purches.PurchaseOrderCoverageTest.missingStoreCannotSave |  | passed |  |
| Purches.PurchaseOrderCoverageTest.missingSupplierCannotSave |  | passed |  |
| Purches.PurchaseOrderCoverageTest.negativePriceCannotSave |  | passed |  |
| Purches.PurchaseOrderCoverageTest.negativeQuantityCannotSave |  | passed |  |
| Purches.PurchaseOrderCoverageTest.newClearsUnsavedLines |  | passed |  |
| Purches.PurchaseOrderCoverageTest.removedItemIsNotSaved |  | passed |  |
| Purches.PurchaseOrderCoverageTest.savedDocumentCanBeEdited |  | passed |  |
| Purches.PurchaseOrderCoverageTest.savedDocumentReopensWithOwnedValues |  | passed |  |
| Purches.PurchaseOrderCoverageTest.savesAndPrintsOwnedDocument |  | passed |  |
| Purches.PurchaseOrderCoverageTest.savesOwnedItemByBarcode |  | passed |  |
| Purches.PurchaseOrderCoverageTest.savesOwnedItemByName |  | passed |  |
| Purches.PurchaseOrderCoverageTest.savesOwnedItemFromPicker |  | passed |  |
| Purches.PurchaseOrderCoverageTest.twoOwnedItemsPersist |  | passed |  |
| Purches.PurchaseOrderCoverageTest.unicodeNotesPersist |  | passed |  |
| Purches.PurchaseOrderCoverageTest.zeroQuantityCannotSave |  | failed | Invalid purchase was saved expected [true] but found [false] |
| Purches.PurchaseReturnCoverageTest.cashDocumentUsesOwnedPayment |  | passed |  |
| Purches.PurchaseReturnCoverageTest.deletesOnlyOwnedDocument |  | passed |  |
| Purches.PurchaseReturnCoverageTest.emptyDocumentCannotSave |  | failed | Invalid purchase was saved expected [true] but found [false] |
| Purches.PurchaseReturnCoverageTest.fixedDiscountRecalculates |  | passed |  |
| Purches.PurchaseReturnCoverageTest.fractionalQuantityPersists |  | passed |  |
| Purches.PurchaseReturnCoverageTest.missingStoreCannotSave |  | passed |  |
| Purches.PurchaseReturnCoverageTest.missingSupplierCannotSave |  | passed |  |
| Purches.PurchaseReturnCoverageTest.negativePriceCannotSave |  | passed |  |
| Purches.PurchaseReturnCoverageTest.negativeQuantityCannotSave |  | passed |  |
| Purches.PurchaseReturnCoverageTest.newClearsUnsavedLines |  | passed |  |
| Purches.PurchaseReturnCoverageTest.percentageDiscountRecalculates |  | passed |  |
| Purches.PurchaseReturnCoverageTest.removedItemIsNotSaved |  | passed |  |
| Purches.PurchaseReturnCoverageTest.savedDocumentCanBeEdited |  | passed |  |
| Purches.PurchaseReturnCoverageTest.savedDocumentReopensWithOwnedValues |  | passed |  |
| Purches.PurchaseReturnCoverageTest.savesAndPrintsOwnedDocument |  | passed |  |
| Purches.PurchaseReturnCoverageTest.savesOwnedItemByBarcode |  | passed |  |
| Purches.PurchaseReturnCoverageTest.savesOwnedItemByName |  | passed |  |
| Purches.PurchaseReturnCoverageTest.savesOwnedItemFromPicker |  | passed |  |
| Purches.PurchaseReturnCoverageTest.twoOwnedItemsPersist |  | passed |  |
| Purches.PurchaseReturnCoverageTest.unicodeNotesPersist |  | passed |  |
| Purches.PurchaseReturnCoverageTest.zeroQuantityCannotSave |  | passed |  |
