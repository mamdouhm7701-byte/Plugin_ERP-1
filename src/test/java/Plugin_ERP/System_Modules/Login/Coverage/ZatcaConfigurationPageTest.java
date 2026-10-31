/* شرح الملف: يراجع حالات شاشة الربط الثلاث، ومنها رسالة الإعدادات الناقصة التي ظهرت في الريجريشن، دون الاتصال بالهيئة. */
package Plugin_ERP.System_Modules.Login.Coverage;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class ZatcaConfigurationPageTest {
    @DataProvider
    public Object[][] states() {
        return new Object[][] {
            {"تم تهيئة جهاز من قبل", false, ZatcaConfigurationPage.State.CONFIGURED},
            {"هناك اعدادات تهيئة غير مكتمله", false, ZatcaConfigurationPage.State.INCOMPLETE},
            {"هناك إعدادات تهيئة غير مكتملة", false, ZatcaConfigurationPage.State.INCOMPLETE},
            {"كلمة مرور المسؤول", true, ZatcaConfigurationPage.State.REGISTRATION},
            {"الربط مع هيئة الزكاة", false, ZatcaConfigurationPage.State.UNKNOWN}
        };
    }

    @Test(dataProvider = "states")
    public void recognizesTheActualStateWithoutTreatingAnUnknownPageAsReady(
            String text, boolean password, ZatcaConfigurationPage.State expected) {
        Assert.assertEquals(ZatcaConfigurationPage.state(text, password), expected);
    }
}
