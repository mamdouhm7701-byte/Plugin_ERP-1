/* شرح الملف: اختبارات قائمة الأصناف: تعديل أسعار صنف موجود؛ حذف الصنف؛ البحث وعدم وجود نتائج وإعادة
 * البحث؛ رفض الوحدة الأساسية كوحدة إضافية؛ تصدير Excel صالح.
 */
package ProductsTest;

import Plugin_ERP.System_Modules.Login.Products.*;
import io.qameta.allure.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("الأصناف")
public class AddProductsTest extends ProductScenarioTest {
    @Override
    protected Kind kind() {
        return Kind.ITEM;
    }

    @Override
    protected ProductsModulePage modulePage() {
        return new AddProductsPage(driver);
    }

    @Test(groups = "products")
    @Story("إضافة صنف بالمخزن ووحدة فرعية")
    public void addNewProductWithValidData() {
        openNew();
        fill(null);
        ((AddProductsPage) page)
                .prSubunit(
                        data.unit(2).name(),
                        "PR" + UUID.randomUUID().toString().replace("-", ""),
                        "10");
        var r = save(false);
        assertDetails(data.read(r));
        var rows = (List<?>) data.read(r).get("subUnits");
        Assert.assertEquals(rows.size(), 1);
        var row = (Map<?, ?>) rows.get(0);
        Assert.assertEquals(row.get("unitId"), data.unit(2).id(), "مطابقة وحدة القياس");
        assertNumber(row.get("conversionRate"), "10");
    }

    @Test(groups = "products")
    @Story("تعديل الصنف")
    public void EditProduct() {
        edit(false);
    }

    @Test(groups = "products")
    @Story("إضافة مكون للصنف")
    public void AddComponent() {
        var component = data.item(2);
        openNew();
        fill(null);
        ((AddProductsPage) page).prComponent(data.store(1).name(), component.name(), "5");
        var r = save(false);
        var rows = (List<?>) data.read(r).get("components");
        Assert.assertEquals(rows.size(), 1);
        var row = (Map<?, ?>) rows.get(0);
        Assert.assertEquals(row.get("componentId"), component.id());
        assertNumber(row.get("requiredQuantity"), "5");
    }

    @Test(groups = "products")
    @Story("حذف الصنف")
    public void DeleteProduct() {
        deleteOwn();
    }

    @Test(groups = "products")
    @Story("الباركود الأساسي مطلوب")
    public void AddProductWithOutBarcode() {
        required("barcode", "باركود");
    }

    @Test(groups = "products")
    @Story("الفئة مطلوبة")
    public void AddProductWithOutCategory() {
        required("category", "فئة");
    }

    @Test(groups = "products")
    @Story("الوحدة الأساسية مطلوبة")
    public void AddProductWithOutUnit() {
        required("unit", "وحدة");
    }

    @Test(groups = "products")
    @Story("مخزن واحد على الأقل مطلوب")
    public void AddProductWithOutStock() {
        required("store", "مخزن");
    }

    @Test(groups = "products")
    @Story("رفض الوحدة الأساسية كوحدة فرعية")
    public void AddProductWithSameUnit() {
        subunitInvalid("same");
    }

    @Test(groups = "products")
    @Story("معامل التحويل مطلوب")
    public void AddProductWithNoTransUnit() {
        subunitInvalid("rate");
    }

    @Test(groups = "products")
    @Story("باركود الوحدة الفرعية مطلوب")
    public void AddTransWithoutBarcode() {
        subunitInvalid("barcode");
    }

    @Test(groups = "products")
    @Story("اسم الصنف العربي مطلوب")
    public void PR_RequiredArabic() {
        required("name", "الاسم العربي");
    }

    @Test(groups = "products")
    @Story("حفظ صنف بوحدة أساسية فقط")
    public void PR_SimpleProduct() {
        create();
    }

    @Test(groups = "products")
    @Story("رفض معامل تحويل يساوي واحد")
    public void PR_SubunitRateOne() {
        subunitInvalid("one");
    }

    @Test(groups = "products")
    @Story("رفض معامل تحويل سالب")
    public void PR_SubunitNegativeRate() {
        subunitInvalid("negative");
    }

    @Test(groups = "products")
    @Story("رفض تكرار الوحدة الفرعية")
    public void PR_DuplicateSubunit() {
        subunitInvalid("duplicate");
    }

    @Test(groups = "products")
    @Story("رفض تكرار المخزن")
    public void PR_DuplicateStore() {
        stockInvalid("duplicate");
    }

    @Test(groups = "products")
    @Story("رفض رصيد افتتاحي سالب")
    public void PR_NegativeOpening() {
        stockInvalid("opening");
    }

    @Test(groups = "products")
    @Story("رفض الحد الأدنى السالب")
    public void PR_NegativeMinimum() {
        stockInvalid("minimum");
    }

    @Test(groups = "products")
    @Story("رفض تكلفة سالبة")
    public void PR_NegativeCost() {
        stockInvalid("cost");
    }

    @Test(groups = "products")
    @Story("حفظ صلاحيات استخدام الصنف")
    public void PR_ControlFlags() {
        openNew();
        fill(null);
        page.prTab("التحكم في الأصناف");
        page.prCheck("مبيعات", false);
        page.prCheck("المشتريات", false);
        var r = save(false);
        var s = data.read(r);
        Assert.assertEquals(s.get("sales"), false);
        Assert.assertEquals(s.get("purch"), false);
    }

    @Test(groups = "products")
    @Story("حفظ رصيد المخزن وتكلفته")
    public void PR_StockBalances() {
        openNew();
        fill("store");
        ((AddProductsPage) page).prStock(data.store(1).name(), "12.5", "3", "80.25");
        var r = save(false);
        var row = (Map<?, ?>) ((List<?>) data.read(r).get("stores")).get(0);
        assertNumber(row.get("openingBalance"), "12.5");
        assertNumber(row.get("costPrice"), "80.25");
    }

    @Test(groups = "products")
    @Story("إغلاق دون حفظ")
    public void PR_CancelNew() {
        commonCancelNew();
    }

    @Test(groups = "products")
    @Story("إلغاء التعديل دون حفظ")
    public void PR_CancelEdit() {
        commonCancelEdit();
    }

    @Test(groups = "products")
    @Story("إلغاء حذف سجل الاختبار")
    public void PR_CancelDelete() {
        commonCancelDelete();
    }

    @Test(groups = "products")
    @Story("البحث وعدم وجود نتائج وإعادة البحث")
    public void PR_SearchAndReset() {
        commonSearch();
    }

    @Test(groups = "products")
    @Story("أعمدة القائمة وسجل الاختبار")
    public void PR_ListStructure() {
        commonList();
    }

    @Test(groups = "products")
    @Story("تصدير PDF صالح")
    public void PR_ExportPdf() {
        commonExport("pdf");
    }

    @Test(groups = "products")
    @Story("تصدير Excel صالح")
    public void PR_ExportExcel() {
        commonExport("excel");
    }

    @Test(groups = "products")
    @Story("حفظ دون الاسم الإنجليزي الاختياري")
    public void PR_OptionalEnglish() {
        optionalEnglish();
    }

    private void subunitInvalid(String reason) {
        new ProductInputValidationChecks(this).subunitInvalid(reason);
    }

    @Test(groups = "products")
    @Story("رفض سعر بيع سالب")
    public void PR_NegativeSalePrice() {
        pricesInvalid("-1", "0", "110");
    }

    @Test(groups = "products")
    @Story("رفض حد أدنى أعلى من سعر البيع")
    public void PR_MinimumAboveSalePrice() {
        pricesInvalid("100.25", "120", "150");
    }

    @Test(groups = "products")
    @Story("رفض حد أقصى أقل من سعر البيع")
    public void PR_MaximumBelowSalePrice() {
        pricesInvalid("100.25", "50", "80");
    }

    @Test(groups = "products")
    @Story("رفض حد أدنى أكبر من الحد الأقصى")
    public void PR_MinimumAboveMaximum() {
        pricesInvalid("100", "120", "110");
    }

    @Test(groups = "products")
    @Story("رفض سعر بيع أقل من الحد الأدنى")
    public void PR_SalePriceBelowMinimum() {
        pricesInvalid("80", "90", "110");
    }

    @Test(groups = "products")
    @Story("رفض سعر بيع أكبر من الحد الأقصى")
    public void PR_SalePriceAboveMaximum() {
        pricesInvalid("120", "90", "110");
    }

    @Test(groups = "products")
    @Story("رفض حد أقصى أقل من الحد الأدنى")
    public void PR_MaximumBelowMinimum() {
        pricesInvalid("100", "90", "80");
    }

    @Test(groups = "products")
    @Story("قبول تساوي أسعار البيع الثلاثة مع الكسور")
    public void PR_AllPricesEqual() {
        pricesValid("100.25", "100.25", "100.25");
    }

    @Test(groups = "products")
    @Story("قبول تساوي أسعار البيع الثلاثة بالصفر")
    public void PR_AllPricesZero() {
        pricesValid("0", "0", "0");
    }

    @Test(groups = "products")
    @Story("قبول سعر البيع عند الحد الأدنى")
    public void PR_SalePriceEqualsMinimum() {
        pricesValid("90", "90", "110");
    }

    @Test(groups = "products")
    @Story("قبول سعر البيع عند الحد الأقصى")
    public void PR_SalePriceEqualsMaximum() {
        pricesValid("110", "90", "110");
    }

    private void enterPrices(String sale, String minimum, String maximum) {
        new ProductInputValidationChecks(this).enterPrices(sale, minimum, maximum);
    }

    private void pricesValid(String sale, String minimum, String maximum) {
        new ProductInputValidationChecks(this).pricesValid(sale, minimum, maximum);
    }

    private void pricesInvalid(String sale, String minimum, String maximum) {
        new ProductInputValidationChecks(this).pricesInvalid(sale, minimum, maximum);
    }

    @Test(groups = "products")
    @Story("التحقق من تكرار باركود صنف مملوك للاختبار")
    public void PR_DuplicateMainBarcode() {
        var existing = data.item(2);
        openNew();
        fill(null);
        page.prField("mainUnitBarcode", existing.values().get("mainUnitBarcode").toString());
        page.prSave();
        String message = page.prValidation();
        page.prSettled();
        capture();
        Assert.assertTrue(page.prSaves().isEmpty(), "Duplicate barcode was persisted");
        Assert.assertFalse(message.isBlank());
    }

    @Test(groups = "products")
    @Story("تعديل أسعار الصنف مع الاحتفاظ بالكسور")
    public void PR_UpdatePrices() {
        var initial = seed();
        locate(initial);
        page.prEdit(initial.name());
        page.prField("salesPrice", "150.75");
        page.prField("minSalesPrice", "120");
        page.prField("maxSalesPrice", "200");
        var updated = save(false);
        Assert.assertEquals(updated.id(), initial.id());
        var s = data.read(updated);
        assertNumber(s.get("salesPrice"), "150.75");
        assertNumber(s.get("minSalesPrice"), "120");
        assertNumber(s.get("maxSalesPrice"), "200");
    }

    @Test(groups = "products")
    @Story("ربط الصنف بمخزنين مختلفين")
    public void PR_AddSecondStore() {
        var initial = seed();
        var second = data.store(2);
        locate(initial);
        page.prEdit(initial.name());
        ((AddProductsPage) page).prStock(second.name(), "0", "0", "90");
        var updated = save(false);
        var rows = (List<?>) data.read(updated).get("stores");
        Assert.assertEquals(rows.size(), 2);
        Assert.assertTrue(
                rows.stream().anyMatch(r -> second.id().equals(((Map<?, ?>) r).get("storeId"))),
                "التحقق من المخزن");
    }

    @Test(groups = "products")
    @Story("حفظ اسم عربي للصنف مع البيانات المدخلة الفريدة")
    public void PR_ArabicProductName() {
        openNew();
        fill(null);
        page.prArabic(marker + " صنف اختبار");
        var r = save(false);
        Assert.assertEquals(
                data.read(r).get("arabicName"), marker + " صنف اختبار", "مطابقة الاسم العربي");
    }

    private void stockInvalid(String reason) {
        new ProductInputValidationChecks(this).stockInvalid(reason);
    }
}
