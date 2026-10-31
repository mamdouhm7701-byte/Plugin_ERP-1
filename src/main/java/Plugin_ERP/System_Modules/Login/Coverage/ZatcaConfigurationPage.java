/* شرح الملف: يقرأ حالة الربط مع الهيئة ويعرض هل الجهاز مهيأ أو ينتظر استكمال الإعدادات أو كلمة مرور المسؤول، دون تسجيل الجهاز أو إرسال فواتير. */
package Plugin_ERP.System_Modules.Login.Coverage;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class ZatcaConfigurationPage extends ScreenPage {
    public enum State {
        CONFIGURED,
        INCOMPLETE,
        REGISTRATION,
        UNKNOWN
    }

    public ZatcaConfigurationPage(WebDriver driver) {
        super(driver);
    }

    public static State state(String text, boolean passwordVisible) {
        String normalized =
                text.replace('أ', 'ا').replace('إ', 'ا').replace('ى', 'ي').replaceAll("\\s+", " ");
        if (normalized.contains("تم تهيئة جهاز من قبل")) return State.CONFIGURED;
        if (normalized.contains("اعدادات تهيئة غير مكتمل")) return State.INCOMPLETE;
        return passwordVisible ? State.REGISTRATION : State.UNKNOWN;
    }

    @Step("قراءة حالة الربط مع الهيئة دون الضغط على تسجيل")
    public State awaitState() {
        return ready.withMessage(
                        "لم تظهر حالة الربط: جهاز مهيأ أو إعدادات ناقصة أو نموذج كلمة المرور")
                .until(
                        current -> {
                            State state =
                                    state(
                                            bodyText(),
                                            visible(By.cssSelector("input[type='password']")));
                            return state == State.UNKNOWN ? null : state;
                        });
    }

    @Step("التحقق من ظهور رمز التحقق وزر التسجيل دون استخدامهما")
    public boolean incompleteControlsVisible() {
        return bodyText().contains("رمز التحقق")
                && visible(By.cssSelector("input:not([type='hidden'])"))
                && visible(button("تسجيل"));
    }
}
