package Plugin_ERP.System_Modules.Login.purches;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;

/** Observe requests and outcomes for owned purchase documents. */
final class PurchaseSaveCapture {
    private final PurchaseModulePage owner;

    PurchaseSaveCapture(PurchaseModulePage owner) {
        this.owner = owner;
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    void puCapture(String marker, Object storeId, Object itemId, String endpoint) {
        owner.js(
                """
    const store=document.querySelector('#app').__vue__.$store;
    window.__purchaseQA={marker:arguments[0],storeId:arguments[1],itemId:arguments[2],endpoint:arguments[3],saves:[],attempts:[],pending:0,errors:[],lookups:[],uiErrors:[]};
    if(!window.__purchaseQAErrorsInstalled){window.__purchaseQAErrorsInstalled=true;
      addEventListener('error',e=>window.__purchaseQA?.uiErrors.push(e.message));
      addEventListener('unhandledrejection',e=>window.__purchaseQA?.uiErrors.push(String(e.reason?.stack||e.reason)));}
    if(store.__purchaseQACaptureInstalled)return;store.__purchaseQACaptureInstalled=true;const dispatch=store.dispatch.bind(store);
    store.dispatch=function(action,payload,...rest){const cap=window.__purchaseQA;
      const lookup=cap&&action==='app/get'&&/^items[?]/i.test(payload?.url||'')&&String(payload.url).includes('storeId='+cap.storeId);
      const owned=cap&&['app/create','app/update'].includes(action)&&payload?.url===cap.endpoint&&
        ((typeof payload?.data?.notes==='string'&&payload.data.notes.startsWith(cap.marker))||
        (payload.data?.storeId===cap.storeId&&payload.data.lines?.length>0&&payload.data.lines.every(l=>l.itemId===cap.itemId)));
      const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
      return dispatch(action,payload,...rest).then(result=>{if(lookup){const data=result?.data||result;cap.lookups.push({url:payload.url,total:data?.totalRecords,items:data?.pageData?.map(i=>({id:i.id,arabicName:i.arabicName}))});}
        if(owned){cap.pending--;const entity=result?.data||result;
        cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});}return result;})
        .catch(error=>{if(lookup)cap.lookups.push({url:payload.url,error:String(error),status:error?.response?.status});if(owned){cap.pending--;cap.errors.push(JSON.stringify(error?.response?.data||error?.data||{message:String(error),status:error?.status}));}throw error;});};
    """,
                marker,
                storeId,
                itemId,
                endpoint);
    }

    List<Map<String, Object>> puSaves() {
        return (List<Map<String, Object>>) owner.js("return window.__purchaseQA?.saves||[]");
    }

    List<Map<String, Object>> puAttempts() {
        return (List<Map<String, Object>>) owner.js("return window.__purchaseQA?.attempts||[]");
    }

    void puSettled() {
        owner.ready.until(
                d -> !Boolean.TRUE.equals(owner.js("return (window.__purchaseQA?.pending||0)>0")));
    }

    String puSaveErrors() {
        return String.valueOf(owner.js("return window.__purchaseQA?.errors||[]"));
    }

    String puDiagnostics() {
        return String.valueOf(
                owner.js(
                        "return JSON.stringify({lookups:window.__purchaseQA?.lookups||[],errors:window.__purchaseQA?.uiErrors||[],saveErrors:window.__purchaseQA?.errors||[],pending:window.__purchaseQA?.pending,attempts:window.__purchaseQA?.attempts,saves:window.__purchaseQA?.saves})"));
    }

    void puWaitSaveOutcome() {
        owner.ready.until(
                d ->
                        !owner.puSaves().isEmpty()
                                || Boolean.TRUE.equals(
                                        owner.js(
                                                "return (window.__purchaseQA?.errors||[]).length>0"))
                                || !d.findElements(
                                                By.cssSelector(
                                                        "form [aria-invalid='true'],form input:invalid,form select:invalid,form textarea:invalid"))
                                        .isEmpty()
                                || d
                                        .findElements(
                                                By.cssSelector(
                                                        ".text-danger,.invalid-feedback,.swal2-html-container,.swal2-title"))
                                        .stream()
                                        .anyMatch(e -> e.isDisplayed() && !e.getText().isBlank()));
    }

    String puCaptureMarker() {
        return (String) owner.js("return window.__purchaseQA?.marker||null");
    }
}
