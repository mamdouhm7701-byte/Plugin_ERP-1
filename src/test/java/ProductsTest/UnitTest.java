/* شرح الملف: اختبارات الوحدات: إنشاء وحدة قطعة؛ تعديل اسم الوحدة؛ حذف وحدة غير مستخدمة؛ البحث وعدم
 * وجود نتائج وإعادة البحث؛ رفض وحدة دون اسم عربي.
 */
package ProductsTest;

import Plugin_ERP.System_Modules.Login.Products.*;
import io.qameta.allure.*;
import java.util.*;
import org.testng.annotations.Test;

@Epic("Web Cloud")
@Feature("الوحدات")
public class UnitTest extends ProductScenarioTest {
    @Override
    protected Kind kind() {
        return Kind.UNIT;
    }

    @Override
    protected ProductsModulePage modulePage() {
        return new UnitePage(driver);
    }

    @Test(groups = "products")
    @Story("إضافة وحدة قياس")
    public void AddNewUnite() {
        create();
    }

    @Test(groups = "products")
    @Story("تعديل وحدة الاختبار")
    public void EditUnite() {
        edit(false);
    }

    @Test(groups = "products")
    @Story("حذف وحدة الاختبار فقط")
    public void DeleteUnite() {
        deleteOwn();
    }

    @Test(groups = "products")
    @Story("الاسم العربي مطلوب")
    public void AddUniteWithoutArabicName() {
        required("name", "الاسم العربي");
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
