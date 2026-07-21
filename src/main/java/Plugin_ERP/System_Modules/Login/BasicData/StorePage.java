package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Driver;
import java.time.Duration;
import javax.swing.text.Utilities;

public class StorePage extends AbstractForDealer {


    //Constructors
    public StorePage(WebDriver driver) {
        super(driver);

    }


    //locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenStore = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'المخازن')]");
    private By Clickyestodelet = By.xpath("//button[contains(@class, \"swal2-confirm\") and contains(text(), \"نعم\")]");


    //Methods
    @Step("Open Store Page")
    public void OpenStorePage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenStore);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();



    }

    @Step("Delete Store")
    public void DeleteStore() {

        String deletedPk = utilites.grid().getLastRowPk();

        By deletedRow = By.xpath("//tr[@data-pk='" + deletedPk + "']");

        System.out.println("🗑️ Start deleting row with pk: " + deletedPk);

        utilites.ClickElement(utilites.grid().getLastDeleteButton());
        utilites.ClickElement(Clickyestodelet);

        long startTime = System.currentTimeMillis();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .numberOfElementsToBe(deletedRow, 0)
        );

        long endTime = System.currentTimeMillis();

        System.out.println("✅ Row deleted successfully");
        System.out.println("⏱️ Delete wait time: "
                + ((endTime - startTime) / 1000.0) + " seconds");

        Assert.assertTrue(
                driver.findElements(deletedRow).isEmpty(),
                "❌ الصف لم يُحذف! data-pk: " + deletedPk
        );
    }
}





