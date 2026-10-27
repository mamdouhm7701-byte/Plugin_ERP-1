package Plugin_ERP.System_Modules.Login.Invoice;

import java.util.List;
import java.util.Map;

/** Observe requests and outcomes for owned sales documents. */
final class SalesSaveCapture {
    private final SalesModulePage owner;

    SalesSaveCapture(SalesModulePage owner) {
        this.owner = owner;
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    void slCapture(String marker, Object storeId, Object itemId, String endpoint) {
        owner.js(
                """
    const store=erpVue.root().$store;
    window.__salesQA={marker:arguments[0],storeId:arguments[1],itemId:arguments[2],endpoint:arguments[3],saves:[],attempts:[],pending:0,errors:[],lookups:[],uiErrors:[]};
    if(!window.__salesQAErrorsInstalled){window.__salesQAErrorsInstalled=true;
      addEventListener('error',e=>window.__salesQA?.uiErrors.push(e.message));
      addEventListener('unhandledrejection',e=>window.__salesQA?.uiErrors.push(String(e.reason?.stack||e.reason)));}
    if(store.__salesQACaptureInstalled)return;store.__salesQACaptureInstalled=true;const dispatch=store.dispatch.bind(store);
    store.dispatch=function(action,payload,...rest){const cap=window.__salesQA;
      const lookup=cap&&action==='app/get'&&/^items[?]/i.test(payload?.url||'')&&String(payload.url).includes('storeId='+cap.storeId);
      const owned=cap&&['app/create','app/update'].includes(action)&&payload?.url===cap.endpoint&&
        ((typeof payload?.data?.notes==='string'&&payload.data.notes.startsWith(cap.marker))||
        (payload.data?.storeId===cap.storeId&&payload.data.lines?.length>0&&payload.data.lines.every(l=>l.itemId===cap.itemId)));
      const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
      return dispatch(action,payload,...rest).then(result=>{if(lookup){const data=result?.data||result;cap.lookups.push({url:payload.url,total:data?.totalRecords,items:data?.pageData?.map(i=>({id:i.id,arabicName:i.arabicName}))});}
        if(owned){cap.pending--;const entity=result?.data||result;
        cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});}return result;})
        .catch(error=>{if(lookup)cap.lookups.push({url:payload.url,error:String(error),status:error?.response?.status});if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});};
    """,
                marker,
                storeId,
                itemId,
                endpoint);
    }

    List<Map<String, Object>> slSaves() {
        return (List<Map<String, Object>>) owner.js("return window.__salesQA?.saves||[]");
    }

    List<Map<String, Object>> slAttempts() {
        return (List<Map<String, Object>>) owner.js("return window.__salesQA?.attempts||[]");
    }

    void slSettled() {
        owner.ready.until(
                d -> !Boolean.TRUE.equals(owner.js("return (window.__salesQA?.pending||0)>0")));
    }

    String slSaveErrors() {
        return String.valueOf(owner.js("return window.__salesQA?.errors||[]"));
    }

    String slDiagnostics() {
        return String.valueOf(
                owner.js(
                        "return JSON.stringify({lookups:window.__salesQA?.lookups||[],errors:window.__salesQA?.uiErrors||[]})"));
    }

    String slCaptureMarker() {
        return (String) owner.js("return window.__salesQA?.marker||null");
    }
}
