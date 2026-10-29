/* شرح الملف: يطبق مراجعة الإضافة والتعديل والحذف والطباعة ومقارنة الأرقام على المطعم باستخدام سياسة الفرع المختارة. */
package SpecialSalesTest;

import Plugin_ERP.System_Modules.Login.SpecialSales.*;

public final class RestaurantInvoiceTaxReviewTest extends SpecialInvoiceTaxReview {
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }
}
