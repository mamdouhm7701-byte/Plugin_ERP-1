package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RoutLineForDealer extends AbstractForDealer {
    public RoutLineForDealer(WebDriver driver) {
        super(driver);
    }

    private static final String OWNED_CALENDAR =
            """
        const queue=[erpVue.root()];
        let calendar;
        while(queue.length){const vm=queue.shift(); if(!vm)continue;
          if(vm.$options.methods?.getCustomersForParams && vm.$options.methods?.getEvents){calendar=vm;break;}
          queue.push(...(erpVue.children(vm)||[]));}
        if(!calendar)throw Error('Route calendar is unavailable');
        """;

    /** Select references through the visible form; component state is read only. */
    public void PrepareOwnedRoute(
            String agentName, String customerName, String statusName, String areaName) {
        var ready = new WebDriverWait(driver, Duration.ofSeconds(30));
        var form =
                new Plugin_ERP.Util.FormInteractions(
                        ready,
                        element ->
                                ((org.openqa.selenium.JavascriptExecutor) driver)
                                        .executeScript(
                                                "arguments[0].scrollIntoView({block:'center'})",
                                                element));
        chooseOwned(form, "مندوب", agentName);
        chooseOwned(form, "المناطق", areaName);
        chooseOwned(form, "العملاء", customerName);
        // Dependent customer/area updates can reset the status; choose it last.
        chooseOwned(form, "الحالة", statusName);
    }

    private void chooseOwned(Plugin_ERP.Util.FormInteractions form, String label, String name) {
        form.choose(
                By.xpath(
                        "//*[@id='sidebar-add-new-event']//*[self::label or self::legend][normalize-space(.)='"
                                + label
                                + "']/following::input[contains(@class,'vs__search') and ancestor::*[@id='sidebar-add-new-event']][1]"),
                name,
                Plugin_ERP.Util.FormInteractions.OptionMatch.EXACT_THEN_CONTAINS);
    }

    @Step("التحقق من اختيار العميل {customerId} في خط السير")
    public void AssertOnlyOwnedCustomerSelected(Object customerId) {
        Object valid =
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                OWNED_CALENDAR
                                        + """
            const selected=calendar.selectedItem.extendedProps.customers;
            return selected.length===1 && selected[0].id===arguments[0];
            """,
                                customerId);
        if (!Boolean.TRUE.equals(valid))
            throw new AssertionError("Route must only contain the owned customer");
    }

    public java.util.List<String> OwnedEventTitles(String marker) {
        @SuppressWarnings("unchecked")
        java.util.List<String> titles =
                (java.util.List<String>)
                        ((org.openqa.selenium.JavascriptExecutor) driver)
                                .executeScript(
                                        OWNED_CALENDAR
                                                + """
                    return calendar.calendarOptions.events
                        .filter(event=>event.extendedProps?.title===arguments[0])
                        .map(event=>event.title);
                    """,
                                        marker);
        return titles;
    }

    // Locators
    private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
    private By OpenRoutLine = By.xpath("//span[text()='خط السير']");

    private By CreateEventBtn = By.cssSelector("button[aria-controls='sidebar-add-new-event']");
    private By TitleInput = By.cssSelector("input#Title");
    private By VisitCountFieldset = By.id("visitCount");
    private By DayCountInput = By.cssSelector("input#dayCount");
    private By FromDateBtn = By.xpath("//input[@name='fromDate']/parent::div//button");
    private By ToDateBtn = By.id("example-datepicker2");
    private By AddBtn = By.cssSelector("button[type='submit'].btn-primary");
    private By CloseSidebarBtn = By.cssSelector("#sidebar-add-new-event svg.feather-x");
    private By EventDescriptionTextarea = By.cssSelector("textarea#event-description");

    private By AddAllBtn = By.xpath("//button[contains(normalize-space(text()), 'إضافة الجميع')]");
    private By DeleteAll = By.xpath("//button[contains(normalize-space(text()), 'إزالة الجميع')]");
    private By GatherVisitsTab =
            By.xpath("//a[@role='tab' and contains(text(), 'تجميع الزيارات')]");
    private By CalendarTab = By.xpath("//a[@role='tab' and contains(text(), 'التقويم')]");
    private By DeleteBtn = By.cssSelector("#sidebar-add-new-event svg.feather-trash");

    private By LastRowDeleteBtn = By.cssSelector("tr:last-child button[data-action-type='delete']");
    private By LastRowInfoIcon = By.cssSelector("tr:last-child svg.feather-info");
    private By LastRowEyeIcon =
            By.xpath("(//*[name()='svg' and contains(@class,'feather-eye')])[last()]");

    private By getDayCell(String date) {
        return By.cssSelector("td[data-date='" + date + "']");
    }

    private By getDayEvents(String date) {
        return By.cssSelector("td[data-date='" + date + "'] div.fc-daygrid-event-harness");
    }

    private By getDayEventTitles(String date) {
        return By.cssSelector("td[data-date='" + date + "'] div.fc-event-title");
    }

    // Methods

    @Step("فتح قائمه خط السير")
    public void OpenRoutLine() {
        utilites.ClickElement(OpenDealerModel);
        utilites.ClickElement(OpenRoutLine);
        utilites.ClickElement(OpenDealerModel);
    }

    @Step("الضغط على انشاء خط السير")
    public void AddRoutLine() {
        utilites.ClickElement(CreateEventBtn);
    }

    @Step("ادخال اسم خط السير")
    public void AddArabicRoutLine(String Title) {
        utilites.typeTextAndVerify(TitleInput, Title);
    }

    @Step("ادخال عدد الزيارات")
    public void AddVisitCount(String VisitCount) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(VisitCountFieldset, VisitCount);
    }

    @Step("ادخال عدد ايام الزيارات")
    public void AddDayCount(String DayCount) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(DayCountInput, DayCount);
    }

    @Step("ادخال من تاريخ")
    public void AddFromDate() {
        String todayDate = utilites.Utility().TypeTodayDate("yyyy-MM-dd");
        utilites.ClickElement(FromDateBtn);
        utilites.ClickElement(getDayCell(todayDate));
    }

    @Step("ادخال الى تاريخ")
    public void AddToDate(String ToDate) {
        utilites.typeTextAndVerify(ToDateBtn, ToDate);
    }

    @Step("الضغط على حفظ")
    public void ClickToSaveButton() {
        utilites.ClickElement(AddBtn);
    }

    @Step("الضغط على علامه الاكس")
    public void ClickCloseButton() {
        utilites.ClickElement(CloseSidebarBtn);
    }

    @Step("ادخال الملاحظات")
    public void AddNotesForRoutLine(String Notes) {
        utilites.typeTextAndVerify(EventDescriptionTextarea, Notes);
    }

    @Step("الضغط على اضافه الجميع")
    public void ClickAddAllButton() {
        utilites.ClickElement(AddAllBtn);
    }

    @Step("الضغط على ازاله الجميع")
    public void ClickToDeletAllButton() {
        utilites.ClickElement(DeleteAll);
    }

    @Step("الضغط على تجميع الزيارات")
    public void ClickGatherVisitsTab() {
        utilites.ClickElement(GatherVisitsTab);
    }

    @Step("الضغط على تاب التقويم")
    public void ClickCalendarTab() {
        utilites.ClickElement(CalendarTab);
    }

    @Step("الضغط على الحذف من داخل القائمه")
    public void ClickDeleteBtn() {
        utilites.ClickElement(DeleteBtn);
    }

    @Step("فتح قائمه الزيارات")
    public void OpenMenuIcon() {
        utilites.ClickElement(LastRowInfoIcon);
    }

    @Step("الضغط على حذف الزيارة")
    public void ClickLastRowDeleteBtn() {
        utilites.ClickElement(LastRowDeleteBtn);
    }

    @Step("التعديل على اول عميل من خلال الضغط على ايقونه العين")
    public void ClickLastRowInfoIcon() {
        utilites.ClickElement(LastRowEyeIcon);
    }

    @Step("تحديد اليوم الحالي")
    public void selectToday() {
        String todayDate = utilites.Utility().TypeTodayDate("yyyy-MM-dd");
        utilites.ClickElement(getDayCell(todayDate));
    }

    @Step("فتح أول زيارة في اليوم {date}")
    public void clickFirstEventForDay(String date) {
        List<WebElement> events = driver.findElements(getDayEventTitles(date));
        events.get(0).click();
    }

    public int getEventsCountForDay(String date) {
        return driver.findElements(getDayEvents(date)).size();
    }

    public List<String> getEventsTitlesForDay(String date) {
        List<WebElement> elements = driver.findElements(getDayEventTitles(date));
        List<String> titles = new ArrayList<>();
        for (WebElement el : elements) {
            titles.add(el.getText());
        }
        return titles;
    }

    public void waitForEventCountToIncrease(String date, int countBefore) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> getEventsCountForDay(date) > countBefore);
    }

    public int getTodayEventsCount() {
        String todayDate = utilites.Utility().TypeTodayDate("yyyy-MM-dd");
        return getEventsCountForDay(todayDate);
    }

    public List<String> getTodayEventsTitles() {
        String todayDate = utilites.Utility().TypeTodayDate("yyyy-MM-dd");
        return getEventsTitlesForDay(todayDate);
    }

    public void waitForTodayEventCountToIncrease(int countBefore) {
        String todayDate = utilites.Utility().TypeTodayDate("yyyy-MM-dd");
        waitForEventCountToIncrease(todayDate, countBefore);
    }
}
