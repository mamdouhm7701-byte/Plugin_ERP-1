package DealerModelTest;

import Plugin_ERP.System_Modules.Login.BasicData.RoutLine;
import Plugin_ERP.System_Modules.Login.DealerModel.RoutLineForDealer;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class RoutLineTest extends BaseTest {


    private String RoutLineUrl = "https://binfares.plugin-erp.com/#/agentItinerary";


    @Test
    public void AddRoutLine() {
        RoutLineForDealer routLineForDealer = new RoutLineForDealer(driver);
        routLineForDealer.OpenRoutLine();
        routLineForDealer.AddRoutLine();
        routLineForDealer.AddArabicRoutLine("اختبار خط ");
        routLineForDealer.SelectByKeys("محمد السيد");
        routLineForDealer.SelectByKeysForDealers("بدون مستند");
        routLineForDealer.AddVisitCount("5");
        routLineForDealer.AddDayCount("10");
        routLineForDealer.SelectByKeysForDealers("نيو كود");
        routLineForDealer.SelectByKeysForDealers("الفرع الرئيسي");
        routLineForDealer.ClickToDeletAllButton();
        routLineForDealer.ClickAddAllButton();
        routLineForDealer.AddNotesForRoutLine("اختبار اضافه خط السير");

        List<String> titlesBefore = routLineForDealer.getTodayEventsTitles(); // ⬅️ ضيف السطر ده الأول

        int countBefore = titlesBefore.size();

        routLineForDealer.ClickToSaveButton();

        routLineForDealer.waitForTodayEventCountToIncrease(countBefore);

        List<String> titlesAfter = routLineForDealer.getTodayEventsTitles();
        int countAfter = titlesAfter.size();

        Assert.assertTrue(countAfter > countBefore,
                "لم يتم إضافة أي حدث جديد. قبل: " + countBefore + " - بعد: " + countAfter);

// ✅ الأحداث الجديدة بس (اللي مكانتش موجودة قبل كده)
        List<String> newTitles = new ArrayList<>(titlesAfter);
        newTitles.removeAll(titlesBefore);

        Allure.step("الأحداث الجديدة المضافة: " + String.join(", ", newTitles));

        boolean allNewContainSalesmanName = newTitles.stream().allMatch(t -> t.contains("محمد السيد"));
        Assert.assertTrue(allNewContainSalesmanName, "فيه حدث جديد ما بيحتويش اسم المندوب المتوقع");

    }


}



