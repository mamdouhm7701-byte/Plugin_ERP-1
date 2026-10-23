package Purches;

import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("المشتريات - RETURN")
public class ReturnOrderTest extends PurchaseFormActions {
    protected PurchaseDocumentKind kind() {
        return PurchaseDocumentKind.RETURN;
    }

    @Test
    @Story("إنشاء مرتجع مشتريات حر بأصناف الاختبار")
    @Description(
            "إنشاء مرتجع مشتريات حر بأصناف الاختبار، باستخدام بيانات يولدها الاختبار في الفرع"
                    + " الحالي، مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد"
                    + " انتهائها.")
    public void AddReturnOrderTest() {
        picker();
    }

    @Test
    @Story("إنشاء مرتجع مشتريات بخصم بالقيمة")
    @Description(
            "إنشاء مرتجع مشتريات بخصم بالقيمة، باستخدام بيانات يولدها الاختبار في الفرع"
                    + " الحالي، مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد"
                    + " انتهائها.")
    public void AddReturnOrderTestWithDiscount() {
        discount(false);
    }

    @Test
    @Story("إنشاء مرتجع مشتريات بالباركود")
    @Description(
            "إنشاء مرتجع مشتريات بالباركود، باستخدام بيانات يولدها الاختبار في الفرع الحالي،"
                    + " مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد انتهائها.")
    public void AddPurchesBillsByBarcodTest() {
        barcode();
    }

    @Test
    @Story("إنشاء مرتجع مشتريات بالاسم")
    @Description(
            "إنشاء مرتجع مشتريات بالاسم، باستخدام بيانات يولدها الاختبار في الفرع الحالي، مع"
                    + " التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد انتهائها.")
    public void AddPurchesBillsByNameTest() {
        name();
    }

    @Test
    @Story("حفظ مرتجع مشتريات بأصناف الاختبار")
    @Description(
            "حفظ مرتجع مشتريات بأصناف الاختبار، باستخدام بيانات يولدها الاختبار في الفرع"
                    + " الحالي، مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد"
                    + " انتهائها.")
    public void AddPurchesBillsWithSaveBuutonTest() {
        picker();
    }

    @Test
    @Story("حذف مرتجع الاختبار فقط وعكس حركة المخزون")
    @Description(
            "حذف مرتجع الاختبار فقط وعكس حركة المخزون، باستخدام بيانات يولدها الاختبار في"
                    + " الفرع الحالي، مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة"
                    + " بعد انتهائها.")
    public void DeleteReturnOrderTest() {
        delete();
    }

    @Test
    @Story("منع مرتجع مشتريات بدون مورد")
    @Description(
            "منع مرتجع مشتريات بدون مورد، باستخدام بيانات يولدها الاختبار في الفرع الحالي، مع"
                    + " التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد انتهائها.")
    public void AddWithOutSupplier() {
        missing("المورد");
    }

    @Test
    @Story("منع مرتجع مشتريات بدون أصناف")
    @Description(
            "منع مرتجع مشتريات بدون أصناف، باستخدام بيانات يولدها الاختبار في الفرع الحالي،"
                    + " مع التحقق من النتيجة الفعلية وحذف بيانات الصفحة المحددة بعد انتهائها.")
    public void AddWithOutProduct() {
        empty();
    }
}
