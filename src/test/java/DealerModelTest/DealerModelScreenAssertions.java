package DealerModelTest;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import java.time.Duration;
import java.util.function.Consumer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;

final class DealerModelScreenAssertions {
    private DealerModelScreenAssertions() {}

    static void assertListAndActionsDisplayed(AbstractForDealer page) {
        Assert.assertTrue(
                page.isDealerListStructureDisplayed(),
                "يجب أن تظهر أعمدة القائمة وأزرار تعديل/حذف الصفوف");
        Assert.assertTrue(page.getVisibleDealerRowCount() > 0, "يجب أن تحتوي القائمة على سجلات");
    }

    static void assertNewFormCanBeClosedWithoutSaving(AbstractForDealer page) {
        String unsavedName = "QA-UNSAVED-DEALER-" + System.currentTimeMillis();

        page.ClickNewButtonForDealer();
        Assert.assertTrue(page.isDealerCreationFormDisplayed(), "يجب أن يظهر نموذج الإضافة");
        Assert.assertEquals(
                page.getMissingDealerCommonFormControls(),
                "",
                "يجب ظهور الحقول والأزرار المشتركة للنموذج");
        page.AddArabicName(unsavedName);
        page.closeDealerFormWithoutSaving();

        Assert.assertFalse(
                page.isDealerNameVisibleInList(unsavedName),
                "لا يجب ظهور الاسم غير المحفوظ في القائمة");
    }

    static void assertEditCanBeCancelled(AbstractForDealer page) {
        page.openFirstDealerForEdit();
        String name = page.getDealerArabicNameValue();
        page.closeDealerFormWithoutSaving();
        assertEditCanBeCancelled(page, name);
    }

    static void assertEditCanBeCancelled(AbstractForDealer page, String originalName) {
        String unsavedName = "QA-CANCEL-EDIT-" + System.currentTimeMillis();

        page.openDealerForEditByName(originalName);
        Assert.assertFalse(originalName.isBlank(), "يجب تحميل اسم الصف المحدد في نموذج التعديل");
        page.AddArabicName(unsavedName);
        page.closeDealerFormWithoutSaving();

        page.openDealerForEditByName(originalName);
        Assert.assertEquals(
                page.getDealerArabicNameValue(),
                originalName,
                "إغلاق التعديل دون حفظ يجب أن يبقي الاسم الأصلي");
        page.closeDealerFormWithoutSaving();
    }

    static void assertDeleteCanBeCancelled(AbstractForDealer page) {
        assertDeleteCanBeCancelled(page, page.getFirstDealerArabicName());
    }

    static void assertDeleteCanBeCancelled(AbstractForDealer page, String originalName) {
        page.requestDealerDeleteByName(originalName);
        Assert.assertTrue(
                page.isDealerDeleteConfirmationDisplayed(),
                "يجب أن تظهر نافذة تأكيد الحذف قبل حذف السجل");
        page.cancelDealerDelete();
        Assert.assertEquals(
                page.getExactDealerNameMatchCount(originalName),
                1,
                "إلغاء الحذف يجب أن يبقي نفس السجل في القائمة");
    }

    static void assertOwnedCreateEditDelete(
            WebDriver driver,
            AbstractForDealer page,
            String originalName,
            String editedName,
            Consumer<AbstractForDealer> fillRequiredFields,
            Runnable openList) {
        try {
            page.ClickNewButtonForDealer();
            page.AddArabicName(originalName);
            fillRequiredFields.accept(page);
            page.ClickSaveButton();
            waitForExactCount(driver, page, originalName, 1);
            page.dismissDealerSaveSuccessIfPresent();

            page.openDealerForEditByName(originalName);
            Assert.assertEquals(page.getDealerArabicNameValue(), originalName);
            page.AddArabicName(editedName);
            page.ClickSaveButton();
            waitForExactCount(driver, page, editedName, 1);
            page.dismissDealerSaveSuccessIfPresent();
            Assert.assertEquals(page.getExactDealerNameMatchCount(originalName), 0);

            page.requestDealerDeleteByName(editedName);
            page.cancelDealerDelete();
            Assert.assertEquals(
                    page.getExactDealerNameMatchCount(editedName),
                    1,
                    "إلغاء حذف سجل الاختبار يجب أن يبقي السجل المملوك للاختبار");

            page.requestDealerDeleteByName(editedName);
            page.confirmDealerDeleteByName(editedName);
            Assert.assertEquals(page.getExactDealerNameMatchCount(editedName), 0);
        } finally {
            try {
                if (page.isDealerCreationFormDisplayed()) {
                    page.closeDealerFormWithoutSaving();
                }
                openList.run();
                for (String name : new String[] {editedName, originalName}) {
                    if (page.getExactDealerNameMatchCount(name) > 0) {
                        page.requestDealerDeleteByName(name);
                        page.confirmDealerDeleteByName(name);
                    }
                }
            } catch (RuntimeException cleanupFailure) {
                System.err.println(
                        "Could not clean owned Dealer Model test data: "
                                + cleanupFailure.getMessage());
            }
        }
    }

    private static void waitForExactCount(
            WebDriver driver, AbstractForDealer page, String name, int count) {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ignored -> page.getExactDealerNameMatchCount(name) == count);
    }
}
