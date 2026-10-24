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
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .withMessage(resource + " list provider has not completed loading")
                .until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript("""
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
                    return Boolean(table&&loaded&&!table.computedBusy&&Array.isArray(table.localItems));
                    """, resource)));
    }
}
