/* شرح الملف: يطبق مراجعة الإضافة والتعديل والحذف والطباعة ومقارنة الأرقام على نقاط البيع باستخدام سياسة الفرع المختارة. */
package SpecialSalesTest;

import Plugin_ERP.System_Modules.Login.SpecialSales.*;

public final class PosInvoiceTaxReviewTest extends SpecialInvoiceTaxReview {
    protected SpecialSalesPage createPage() {
        return new PointOfSalePage(driver);
    }
}
