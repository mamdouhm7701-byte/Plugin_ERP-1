package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.Locale;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The search box can render before a basic-data table's first provider response. */
final class GridProviderWait {
    private final WebDriver driver;
    private final String resource;

    GridProviderWait(WebDriver driver, String resource) {
        this.driver = driver;
        this.resource = resource.toLowerCase(Locale.ROOT);
    }

    void awaitReady() {
        final String[] previous = {null};
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .withMessage(resource + " list provider has not completed loading")
                .until(d -> {
                    Object signature = ((JavascriptExecutor) d).executeScript("""
                    const root=document.querySelector('#app')?.__vue__;
                    if(!root)return false;
                    const grid=document.getElementById('mainTable'),endpoint=arguments[0];
                    const queue=[root];let table;
                    while(queue.length){
                        const vm=queue.shift();queue.push(...(vm.$children||[]));
                        if(vm.$options.name==='BTable'&&grid&&grid.getClientRects().length
                            &&(vm.$el===grid||vm.$el?.contains(grid))){table=vm;break;}
                    }
                    const loaded=performance.getEntriesByType('resource').some(r=>{
                        try{return new URL(r.name).pathname.split('/').filter(Boolean).pop()?.toLowerCase()===endpoint;}
                        catch(e){return false;}
                    });
                    if(!(table&&loaded&&!table.computedBusy&&Array.isArray(table.localItems)))return null;
                    return JSON.stringify(table.localItems);
                    """, resource);
                    if (!(signature instanceof String current)) { previous[0] = null; return false; }
                    boolean stable = current.equals(previous[0]);
                    previous[0] = current;
                    return stable;
                });
    }

    int exactMatches(String name) {
        awaitReady();
        return ((Number) ((JavascriptExecutor) driver).executeScript("""
            return [...document.querySelectorAll('#mainTable tbody tr[role="row"]')]
              .filter(r=>r.getClientRects().length&&[...r.querySelectorAll('td')]
                .some(c=>c.innerText.trim()===arguments[0])).length;
            """, name)).intValue();
    }
}
