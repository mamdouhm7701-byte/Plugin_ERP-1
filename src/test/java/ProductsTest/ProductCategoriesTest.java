package ProductsTest;

import Plugin_ERP.System_Modules.Login.Products.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.*;

@Epic("Web Cloud") @Feature("فئات الأصناف")
public class ProductCategoriesTest extends ProductScenarioTest {
  @Override protected Kind kind(){return Kind.CATEGORY;}
  @Override protected ProductsModulePage modulePage(){return new ProductCategories(driver);}
  @Test(groups="products") @Story("إضافة فئة رئيسية")
  public void AddProductCategory(){create();}
  @Test(groups="products") @Story("إضافة فئة فرعية بربط مستقل")
  public void AddBranchProduct(){openNew();fill(null);page.prCheck("رئيسية",false);page.prChoose("الفئه الكبري",data.mainCategory().name());page.prCheck("فئة نقاط بيع",true);var r=save(false);var stored=data.read(r);Assert.assertEquals(stored.get("parentCategoryId"),data.mainCategory().id());Assert.assertEquals(stored.get("isForPOS"),true);}
  @Test(groups="products") @Story("تعديل فئة فرعية مستقلة")
  public void EditBranchProduct(){var parent=data.mainCategory();var r=data.fixture(marker,kind().endpoint,ProductsTestData.fields("isMain",false,"parentCategoryId",parent.id()));locate(r);page.prEdit(r.name());page.prArabic(marker+" EDIT");page.prCheck("فئة مطعم",true);save(false);}
  @Test(groups="products") @Story("حذف فئة الاختبار فقط")
  public void DeleteBranchProduct(){deleteOwn();}
  @Test(groups="products") @Story("اسم الفئة العربي مطلوب")
  public void AddProductCategoryWithoutArabicName(){required("name","الاسم العربي");}
  @Test(groups="products") @Story("حفظ خصائص فئة المطعم ونقاط البيع")
  public void PR_CategoryFlags(){openNew();fill(null);page.prCheck("فئة مطعم",true);page.prCheck("فئة نقاط بيع",true);var r=save(false);var s=data.read(r);Assert.assertEquals(s.get("isForResturants"),true);Assert.assertEquals(s.get("isForPOS"),true);}
  @Test(groups="products") @Story("إغلاق دون حفظ")
  public void PR_CancelNew(){commonCancelNew();}
  @Test(groups="products") @Story("إلغاء التعديل دون حفظ")
  public void PR_CancelEdit(){commonCancelEdit();}
  @Test(groups="products") @Story("إلغاء حذف سجل الاختبار")
  public void PR_CancelDelete(){commonCancelDelete();}
  @Test(groups="products") @Story("البحث وعدم وجود نتائج وإعادة البحث")
  public void PR_SearchAndReset(){commonSearch();}
  @Test(groups="products") @Story("أعمدة القائمة وسجل الاختبار")
  public void PR_ListStructure(){commonList();}
  @Test(groups="products") @Story("تصدير PDF صالح")
  public void PR_ExportPdf(){commonExport("pdf");}
  @Test(groups="products") @Story("تصدير Excel صالح")
  public void PR_ExportExcel(){commonExport("excel");}
  @Test(groups="products") @Story("حفظ دون الاسم الإنجليزي الاختياري")
  public void PR_OptionalEnglish(){optionalEnglish();}
}
