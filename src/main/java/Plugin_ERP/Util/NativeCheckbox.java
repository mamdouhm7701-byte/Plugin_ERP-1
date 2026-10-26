package Plugin_ERP.Util;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Clicks Bootstrap's rendered checkbox square with native pointer input. */
public final class NativeCheckbox {
    private NativeCheckbox() {}

    public static void set(WebDriver driver, WebElement input, boolean selected) {
        if (input.isSelected() == selected) return;
        if (input.isDisplayed()) input.click();
        else {
            @SuppressWarnings("unchecked")
            List<Number> point =
                    (List<Number>)
                            ((JavascriptExecutor) driver)
                                    .executeScript(
                                            """
                const input=arguments[0],label=[...document.querySelectorAll('label[for]')]
                  .find(e=>e.htmlFor===input.id);
                if(!label)throw Error('Checkbox has no associated label');
                label.scrollIntoView({block:'center',inline:'nearest',behavior:'instant'});
                const origin=getComputedStyle(label).position==='static'?label.offsetParent:label;
                if(!origin)throw Error('Checkbox has no rendered containing block');
                const r=origin.getBoundingClientRect(),s=getComputedStyle(label,'::before');
                const width=parseFloat(s.width),height=parseFloat(s.height),right=parseFloat(s.right);
                const left=Number.isFinite(parseFloat(s.left))?parseFloat(s.left):origin.clientWidth-right-width;
                const top=parseFloat(s.top);
                if(![left,top,width,height].every(Number.isFinite)||width<=0||height<=0)
                  throw Error('Checkbox square is not rendered');
                return [r.left+origin.clientLeft+left+width/2,r.top+origin.clientTop+top+height/2,innerWidth,innerHeight];
                """,
                                            input);
            if (point.get(0).doubleValue() < 0
                    || point.get(0).doubleValue() >= point.get(2).doubleValue()
                    || point.get(1).doubleValue() < 0
                    || point.get(1).doubleValue() >= point.get(3).doubleValue())
                throw new AssertionError("Checkbox square outside the viewport: " + point);
            new Actions(driver)
                    .moveToLocation(
                            (int) Math.round(point.get(0).doubleValue()),
                            (int) Math.round(point.get(1).doubleValue()))
                    .click()
                    .perform();
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> input.isSelected() == selected);
    }
}
