/* شرح الملف: اختبارات قوائم الأسعار: تعديل وطباعة قائمة الاختبار؛ حذف قائمة الاختبار؛ البحث وعدم
 * وجود نتائج وإعادة البحث؛ تصدير PDF لقائمة الأسعار؛ حفظ سعر كسري في قائمة الأسعار.
 */
package ProductsTest;

import Plugin_ERP.System_Modules.Login.Products.*;
import io.qameta.allure.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("قوائم الأسعار")
public class ProductListTest extends ProductScenarioTest {
    @Test(groups = "products")
    @Story("رفض تكرار نفس الصنف والوحدة في قائمة الأسعار")
    public void PR_DuplicatePriceListItem() {
        openNew();
        fill(null);
        page.prButton("اضافة اصناف");
        page.prPickItem(data.item(1).name());
        Assert.assertFalse(page.prValidation().isBlank());
        page.prDismiss();
        page.prClosePicker();
        Assert.assertEquals(page.prFormRowsContaining(data.item(1).name()), 1);
        Assert.assertTrue(page.prSaves().isEmpty());
    }

    @Override
    protected Kind kind() {
        return Kind.PRICE;
    }

    @Override
    protected ProductsModulePage modulePage() {
        return new ProductListPage(driver);
    }

    @Test(groups = "products")
    @Story("إضافة قائمة بأسعار أصناف الاختبار والطباعة")
    public void AddNewProductList() {
        openNew();
        fill(null);
        var r = save(true);
        assertDetails(data.read(r));
    }

    @Test(groups = "products")
    @Story("تعديل وطباعة قائمة الاختبار")
    public void EditPriceList() {
        edit(true);
    }

    @Test(groups = "products")
    @Story("حذف قائمة الاختبار")
    public void DeletePriceList() {
        deleteOwn();
    }

    @Test(groups = "products")
    @Story("اسم القائمة العربي مطلوب")
    public void AssertErorrMsg() {
        required("name", "عربي");
    }

    @Test(groups = "products")
    @Story("حفظ قائمة الأسعار دون طباعة")
    public void PR_SaveWithoutPrint() {
        create();
    }

    @Test(groups = "products")
    @Story("الاحتفاظ بكسور السعر")
    public void PR_DecimalPrice() {
        openNew();
        fill(null);
        ((ProductListPage) page).prItemPrice(data.item(1).name(), "125.75");
        var r = save(false);
        var row = (Map<?, ?>) ((List<?>) data.read(r).get("items")).get(0);
        assertNumber(row.get("salesPrice"), "125.75");
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
}
