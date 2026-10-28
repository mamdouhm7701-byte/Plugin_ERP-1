/* شرح الملف: تنفيذ حالات العملاء الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import io.qameta.allure.*;

final class CustomersSavingChecks {
    private final CustomersTest owner;

    CustomersSavingChecks(CustomersTest owner) {
        this.owner = owner;
    }

    void CU_005_OwnedCustomerCreateEditDelete() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Customers page = new Customers(owner.caseDriver());
        String originalName = owner.ownedName(page, Pages.CUSTOMERS, "LIFECYCLE-ORIGINAL");
        String editedName = owner.ownedName(page, Pages.CUSTOMERS, "LIFECYCLE-EDITED");
        page.NavigationHelper(Pages.CUSTOMERS);

        DealerModelScreenAssertions.assertOwnedCreateEditDelete(
                owner.caseDriver(),
                page,
                originalName,
                editedName,
                form -> {
                    ((Customers) form).ClickBasicDataTab();
                    form.AddEnglishName("QA Customer " + suffix);
                },
                () -> page.NavigationHelper(Pages.CUSTOMERS));
    }

    void AddCustomerFirstPage() {
        owner.prepareScenarioReferences("AddCustomerFirstPage");
        Customers customers = new Customers(owner.caseDriver());
        String recordName = owner.ownedName(customers, Pages.CUSTOMERS, "AddCustomerFirstPage");
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(recordName);
        customers.AddEnglishName("Test " + recordName);
        customers.SelectFromDropDawn(owner.data().area().name(), "اسم المنطقة");
        customers.SelectFromDropDawn(owner.data().account().name(), "رقم الحساب");
        customers.SelectFromDropDawn(
                owner.data().classification("Customers").name(), "تصنيف العميل");
        customers.SelectFromDropDawn(
                owner.data().mainDealer("Customers").name(), "اسم المتعامل الرئيسي");
        customers.SelectFromDropDawn(owner.data().agent("Sales").name(), "المندوب");
        customers.AddAllowanceDays("30");
        customers.AddDateForDealer();
        customers.AddCommercialRegisterNumber("123456789");
        customers.SelectCheckboxByText("خصم العميل", true);
        customers.AssertCheckboxIsChecked("خصم العميل", true);
        customers.AddDiscountValue("10");
        customers.AddValueLimit("1000");
        customers.AddNoteForDealer("هذا العميل مهم جدا");
        customers.AssertCheckboxIsChecked("فعال", true);
        customers.SmartCheck("عام", false);
        customers.SmartCheck("عميل نقدي", false);
        customers.AssertCheckboxIsChecked("عام", false);
        customers.AssertCheckboxIsChecked("عميل نقدي", false);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", true);
        customers.SmartCheck("خاضع للضريبة", true);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", true);
        customers.ClickSaveButton();
        owner.waitForRecord(customers, recordName);
    }

    void AddCustomerSecondPage() {
        Customers customers = new Customers(owner.caseDriver());
        String recordName = owner.ownedName(customers, Pages.CUSTOMERS, "AddCustomerSecondPage");
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(recordName);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddAddress("شارع الملك فيصل");
        customers.AddMobileNumber(owner.uniquePhone());
        customers.AddLandLine("0112345678");
        customers.AddEmail(owner.uniqueEmail());
        customers.AddExtraNumber("0597654321");
        customers.AddAnotherId("987654321");
        customers.AddLocation("https://maps.app.goo.gl/mcLw5htkdHgY7NZm9");
        customers.AddBankName("البنك الأهلي");
        customers.AddAccountNumber("1234567890123456");
        customers.AddAgentName("أحمد علي");
        customers.AddAgentPhone("0598765432");
        customers.AddIban("SA1234567890123456789012");
        customers.ClickSaveButton();
        owner.waitForRecord(customers, recordName);
    }

    void AddCustomerThirdPage() {
        Customers customers = new Customers(owner.caseDriver());
        String recordName = owner.ownedName(customers, Pages.CUSTOMERS, "AddCustomerThirdPage");
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(recordName);
        customers.ClickContactDataTab();
        customers.AddAddress("الرياض، الملز، شارع الملك عبدالعزيز، مبنى 1213");
        customers.ClickLinkDataTax();
        customers.AssertCheckboxIsChecked("عميل شركة", false);
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.AddTaxNumber(owner.uniqueTaxNumber());
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12345");
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickSaveButton();
        owner.waitForRecord(customers, recordName);
    }

    void AddCustomerFourthPage() {
        Customers customers = new Customers(owner.caseDriver());
        String recordName = owner.ownedName(customers, Pages.CUSTOMERS, "AddCustomerFourthPage");
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(recordName);
        customers.ClickCustomerControlTab();
        customers.SmartCheck("مبيعات نقدية", false);
        customers.SmartCheck("مبيعات اجلة", false);
        customers.SmartCheck("مرتجع مبيعات", false);
        customers.SmartCheck("عرض سعر", false);
        customers.SmartCheck("امر بيع", false);
        customers.SmartCheck("خصم العميل", false);
        customers.AssertCheckboxIsChecked("مبيعات نقدية", false);
        customers.AssertCheckboxIsChecked("مبيعات اجلة", false);
        customers.AssertCheckboxIsChecked("مرتجع مبيعات", false);
        customers.AssertCheckboxIsChecked("عرض سعر", false);
        customers.AssertCheckboxIsChecked("امر بيع", false);
        customers.AssertCheckboxIsChecked("خصم العميل", false);
        customers.ClickSaveButton();
        owner.waitForRecord(customers, recordName);
    }

    void Edit() {
        owner.prepareScenarioReferences("Edit");
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        String originalName = owner.seedRecord(customers, Pages.CUSTOMERS, "EDIT-ORIGINAL");
        customers.openDealerForEditByName(originalName);
        String editedName = owner.ownedName(customers, Pages.CUSTOMERS, "EDIT-UPDATED");
        customers.AddArabicName(editedName);
        customers.AddEnglishName("Test " + editedName);
        customers.SelectFromDropDawn(owner.data().area().name(), "اسم المنطقة");
        customers.SelectFromDropDawn(owner.data().account().name(), "رقم الحساب");
        customers.SelectFromDropDawn(
                owner.data().classification("Customers").name(), "تصنيف العميل");
        customers.SelectFromDropDawn(
                owner.data().mainDealer("Customers").name(), "اسم المتعامل الرئيسي");
        customers.SelectFromDropDawn(owner.data().agent("Sales").name(), "المندوب");
        customers.AddAllowanceDays("100");
        customers.AddDateForDealer();
        customers.AddCommercialRegisterNumber("133456789");
        customers.SelectCheckboxByText("خصم العميل", false);
        customers.SelectCheckboxByText("خصم العميل", false);
        customers.SelectCheckboxByText("فعال", false);
        customers.AddValueLimit("10000");
        customers.AddNoteForDealer("تست معدل");
        customers.AssertCheckboxIsChecked("فعال", false);
        customers.SmartCheck("عام", false);
        customers.SmartCheck("عميل نقدي", false);
        customers.AssertCheckboxIsChecked("عام", false);
        customers.AssertCheckboxIsChecked("عميل نقدي", false);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", true);
        customers.SmartCheck("خاضع للضريبة", true);
        customers.AssertCheckboxIsChecked("خاضع للضريبة", true);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("معدل");
        customers.AddAddress("معدل");
        customers.AddMobileNumber(owner.uniquePhone());
        customers.AddLandLine("0112345278");
        customers.AddEmail(owner.uniqueEmail());
        customers.AddExtraNumber("0597652321");
        customers.AddAnotherId("987654321");
        customers.AddLocation("https://maps.app.goo.gl/mcLw5htkdHgY7NZm9");
        customers.AddBankName("البنك الأهلي");
        customers.AddAccountNumber("1234567890123456");
        customers.AddAgentName("أحمد علي");
        customers.AddAgentPhone("0598765432");
        customers.AddIban("SA1234567890123456789012");
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.AddTaxNumber(owner.uniqueTaxNumber());
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12345");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickCustomerControlTab();
        customers.SmartCheck("مبيعات نقدية", true);
        customers.SmartCheck("مبيعات اجلة", true);
        customers.SmartCheck("مرتجع مبيعات", true);
        customers.SmartCheck("عرض سعر", true);
        customers.SmartCheck("امر بيع", true);
        customers.SmartCheck("خصم العميل", false);
        customers.AssertCheckboxIsChecked("مبيعات نقدية", true);
        customers.AssertCheckboxIsChecked("مبيعات اجلة", true);
        customers.AssertCheckboxIsChecked("مرتجع مبيعات", true);
        customers.AssertCheckboxIsChecked("عرض سعر", true);
        customers.AssertCheckboxIsChecked("امر بيع", true);
        customers.AssertCheckboxIsChecked("خصم العميل", false);
        customers.ClickSaveButton();
        owner.waitForRecord(customers, editedName);
    }
}
