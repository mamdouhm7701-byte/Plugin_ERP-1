/* شرح الملف: تنفيذ حالات العملاء الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import io.qameta.allure.*;
import org.testng.Assert;

final class CustomersSaving2Checks {
    private final CustomersTest owner;

    CustomersSaving2Checks(CustomersTest owner) {
        this.owner = owner;
    }

    void Delete() {
        Customers customers = new Customers(owner.caseDriver());
        owner.deleteOwnedRecords(customers, Pages.CUSTOMERS, 3);
    }

    void SaveWithOutArabicName() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName("");
        customers.AddEnglishName("New Customer");
        customers.AddEnglishName(
                owner.ownedName(customers, Pages.CUSTOMERS, "INVALID-ARABIC-NAME"));
        customers.ClickSaveButton();
        Assert.assertTrue(
                customers.IsArabicNameRequiredErrorDisplayed(),
                "يجب رفض الحفظ وإظهار خطأ الاسم العربي الإلزامي");
    }

    void EnterWrongEmail() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddEmail("muhamm");
        customers.ClickSaveButton();
        Assert.assertTrue(customers.IsRightEmailErrorDisplayed());
    }

    void EnterWrongBankNumber() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickContactDataTab();
        customers.AddAccountNumber("123456789012lk3456");
        customers.ClickSaveButton();
        Assert.assertTrue(customers.AssertNumberOfBankWithOutLetter());
    }

    void AssertErrorMessagesInDataTax() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        Assert.assertTrue(customers.IsTaxNumberRequiredErrorDisplayed());
    }

    void AssertTaxNumberStartNumber3andEndStareNumber3() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.AddTaxNumber("123456789123451");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12345");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddAddress("شارع الملك فيصل");
        customers.ClickSaveButton();
        Assert.assertTrue(
                customers.isErrorPopupDisplayed(),
                "يجب إظهار تنبيه عند إدخال رقم ضريبي غير مطابق للصيغة");
    }

    void AssertBuildingNumber() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("113");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("12415");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickSaveButton();
        Assert.assertTrue(customers.AssertBuildingNumber());
    }

    void AssertPostNumberInclode5number() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1213");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("125");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickSaveButton();
        Assert.assertTrue(customers.AssertPostNumber());
    }

    void AssertAdress() {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(owner.ownedName(customers, Pages.CUSTOMERS, "VALIDATION"));
        customers.ClickLinkDataTax();
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.AddTaxNumber("323456789123453");
        customers.AddCountry("المملكة العربية السعودية");
        customers.AddCity("الرياض");
        customers.AddDistrict("الملز");
        customers.AddBuildingNumber("1113");
        customers.AddStreetName("شارع الملك عبدالعزيز");
        customers.AddPostalCode("11345");
        customers.SmartCheck("عميل شركة", true);
        customers.AssertCheckboxIsChecked("عميل شركة", true);
        customers.ClickContactDataTab();
        customers.AddContactPersonName("محمد علي");
        customers.AddAddress("");
        Assert.assertTrue(customers.AssertAdress());
        customers.ClickSaveButton();
    }

    void AsserShowErrorPopUpPhoneNumberDubelicateAndClickYes() {
        Customers customers = new Customers(owner.caseDriver());
        String phone = owner.seedCustomerPhone(customers);
        String recordName = owner.ownedName(customers, Pages.CUSTOMERS, "PHONE-YES");
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(recordName);
        customers.ClickContactDataTab();
        customers.AddMobileNumber(phone);
        customers.ClickSaveButton();
        customers.AssertPopupAndClickOK();
        owner.waitForRecord(customers, recordName);
    }

    void AsserShowErrorPopUpPhoneNumberDubelicateAndClickCansel() {
        Customers customers = new Customers(owner.caseDriver());
        String phone = owner.seedCustomerPhone(customers);
        String recordName = owner.ownedName(customers, Pages.CUSTOMERS, "PHONE-CANCEL");
        customers.NavigationHelper(Pages.CUSTOMERS);
        customers.ClickNewButtonForDealer();
        customers.AddArabicName(recordName);
        customers.ClickContactDataTab();
        customers.AddMobileNumber(phone);
        customers.ClickSaveButton();
        customers.AssertPopupAndClickCancel();
        customers.closeDealerFormWithoutSaving();
        Assert.assertEquals(customers.getExactDealerNameMatchCount(recordName), 0);
    }
}
