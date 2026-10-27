package Plugin_ERP.System_Modules.Login.FinancialMovements;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Financial Save Capture: focused operations used by the original page facade. */
final class FinancialSaveCapture {
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> capturedAttempts() {
        return (List<Map<String, Object>>)
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__financialQA?.attempts||[]");
    }

    private final FinancialMovementPage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    FinancialSaveCapture(FinancialMovementPage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    void captureSaves(String marker) {
        Plugin_ERP.Util.VueComponentAccess.execute(
                driver,
                """
      const store=erpVue.root().$store;
      window.__financialQA={marker:arguments[0],saves:[],attempts:[],pending:0,errors:[]};
      if(store.__financialCaptureInstalled)return;
      store.__financialCaptureInstalled=true;
      const dispatch=store.dispatch.bind(store);
      store.dispatch=function(action,payload,...rest){
        const cap=window.__financialQA;
        const owned=(action==='app/create'||action==='app/update')&&payload?.data?.notes?.startsWith(cap?.marker);
        const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;
        if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
        return dispatch(action,payload,...rest).then(result=>{
          if(owned){cap.pending--;const entity=result?.data||result; cap.saves.push({endpoint:payload.url,
            id:sent.id||entity?.id||(typeof entity==='string'?entity:null),values:sent});}
          return result;
        }).catch(error=>{if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});
      };
      """,
                marker);
    }

    List<Map<String, Object>> capturedSaves() {
        return (List<Map<String, Object>>)
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__financialQA?.saves||[]");
    }

    String captureMarker() {
        return (String)
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return window.__financialQA?.marker||null");
    }

    boolean hasSaveStarted() {
        return Boolean.TRUE.equals(
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver,
                        "return !!window.__financialQA && (window.__financialQA.pending>0 || window.__financialQA.saves.length>0)"));
    }

    void awaitSavesSettled() {
        ready.until(
                d ->
                        !Boolean.TRUE.equals(
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d, "return (window.__financialQA?.pending||0)>0")));
    }
}
