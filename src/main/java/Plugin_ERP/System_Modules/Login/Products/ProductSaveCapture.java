package Plugin_ERP.System_Modules.Login.Products;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Product Save Capture: focused operations used by the original page facade. */
final class ProductSaveCapture {
    private final ProductsModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    ProductSaveCapture(ProductsModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    void prCapture(String marker) {
        Plugin_ERP.Util.VueComponentAccess.execute(
                driver,
                """
      const store=erpVue.root().$store;
      window.__productsQA={marker:arguments[0],saves:[],attempts:[],pending:0,errors:[]};
      if(store.__productsCaptureInstalled)return;store.__productsCaptureInstalled=true;
      const dispatch=store.dispatch.bind(store);
      store.dispatch=function(action,payload,...rest){const cap=window.__productsQA;
        const owned=(action==='app/create'||action==='app/update')&&cap&&
          [payload?.data?.arabicName,payload?.data?.englishName,payload?.data?.notes].some(v=>typeof v==='string'&&v.startsWith(cap.marker));
        const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;
        if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
        return dispatch(action,payload,...rest).then(result=>{if(owned){cap.pending--;const entity=result?.data||result;
          cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'?entity:null),values:sent});}return result;
        }).catch(error=>{if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});};
      """,
                marker);
    }

    String prCaptureMarker() {
        return (String)
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__productsQA?.marker||null");
    }

    List<Map<String, Object>> prSaves() {
        return (List<Map<String, Object>>)
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__productsQA?.saves||[]");
    }

    List<Map<String, Object>> prAttempts() {
        return (List<Map<String, Object>>)
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__productsQA?.attempts||[]");
    }

    void prSettled() {
        ready.until(
                d ->
                        !Boolean.TRUE.equals(
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d, "return (window.__productsQA?.pending||0)>0")));
    }

    String prSaveErrors() {
        return String.valueOf(
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__productsQA?.errors||[]"));
    }
}
