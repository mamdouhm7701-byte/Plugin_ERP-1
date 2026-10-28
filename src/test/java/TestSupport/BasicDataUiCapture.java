/* شرح الملف: متابعة طلبات الحفظ الأصلية للبيانات الأساسية على الشاشة، والتقاط السجلات المحجوزة للتجربة فقط.
 * فائدته: ينتظر انتهاء الطلبات غير المحسومة ويترك التقاط كلمات السر خارج سجلات التجربة.
 */
package TestSupport;

import static TestSupport.BasicDataFixtures.ALLOWED;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class BasicDataUiCapture {
    private final BasicDataFixtures data;
    private final WebDriver driver;
    private final String suffix;
    private final Set<String> reservedNames;

    BasicDataUiCapture(
            BasicDataFixtures data, WebDriver driver, String suffix, Set<String> reservedNames) {
        this.data = data;
        this.driver = driver;
        this.suffix = suffix;
        this.reservedNames = reservedNames;
    }

    @SuppressWarnings("unchecked")
    public void collectUi() {
        Object raw =
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__basicDataQA?.saves||[]");
        if (!(raw instanceof List<?> rows)) return;
        for (Object value : rows) {
            var row = (Map<String, Object>) value;
            var values = (Map<String, Object>) row.get("values");
            String name =
                    java.util.stream.Stream.of("arabicName", "englishName", "notes", "fullName")
                            .map(values::get)
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .filter(reservedNames::contains)
                            .findFirst()
                            .orElse(null);
            if (name == null) throw new AssertionError("Unreserved basic data save");
            data.track((String) row.get("endpoint"), row.get("id"), name, values);
        }
    }

    public void installCapture() {
        Plugin_ERP.Util.VueComponentAccess.execute(
                driver,
                """
      const store=erpVue.root()?.$store;
      if(!store)return;
      const storageKey='qa-basic-data-'+arguments[1];
      if(window.__basicDataQA?.storageKey!==storageKey){
        const previous=sessionStorage.getItem(storageKey);
        window.__basicDataQA=previous?JSON.parse(previous):{saves:[],pending:0,storageKey};
      }
      if(store.__basicDataCaptureInstalled)return;
      store.__basicDataCaptureInstalled=true;
      const dispatch=store.dispatch.bind(store), allowed=new Set(arguments[0]);
      const persist=cap=>sessionStorage.setItem(cap.storageKey,JSON.stringify(cap));
      store.dispatch=function(action,payload,...rest){
        const data=payload?.data, names=window.__basicDataReservedNames||[],cap=window.__basicDataQA;
        const owned=['app/create','app/update'].includes(action)&&allowed.has(payload?.url)&&
          ['arabicName','englishName','notes','fullName'].some(key=>names.includes(data?.[key]));
        const sent=owned?JSON.parse(JSON.stringify(data)):null;
        if(owned){delete sent.passwordHash;delete sent.password;cap.pending++;
          (cap.attempts||=[]).push({endpoint:payload.url,values:sent});persist(cap);}
        let response;
        try{response=dispatch(action,payload,...rest);}
        catch(error){if(owned){cap.pending--;persist(cap);}throw error;}
        return Promise.resolve(response).then(result=>{
          if(owned){cap.pending--;const entity=result?.data||result;
            cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||
              (typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});persist(cap);}
          return result;
        },error=>{if(owned){cap.pending--;
          (cap.errors||=[]).push({endpoint:payload.url,status:error?.response?.status,
            detail:error?.response?.data??error?.data??{message:error?.message,errors:error?.errors}});
          persist(cap);}throw error;});
      };
      """,
                new ArrayList<>(ALLOWED),
                suffix);
        Plugin_ERP.Util.VueComponentAccess.execute(
                driver,
                "window.__basicDataReservedNames=arguments[0]",
                new ArrayList<>(reservedNames));
    }

    public void awaitUiSettled() {
        installCapture();
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                !Boolean.TRUE.equals(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d, "return (window.__basicDataQA?.pending||0)>0")));
    }
}
