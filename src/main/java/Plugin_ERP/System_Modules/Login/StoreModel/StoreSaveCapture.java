package Plugin_ERP.System_Modules.Login.StoreModel;

import java.util.List;
import java.util.Map;

/** Observe requests and outcomes for owned warehouse documents. */
final class StoreSaveCapture {
    private final StoreModulePage owner;

    StoreSaveCapture(StoreModulePage owner) {
        this.owner = owner;
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    void smCapture(String marker, Object storeId, Object itemId) {
        owner.js(
                """
    const store=document.querySelector('#app').__vue__.$store;
    window.__storeQA={marker:arguments[0],storeId:arguments[1],itemId:arguments[2],saves:[],attempts:[],pending:0,errors:[],lookups:[],uiErrors:[]};
    if(!window.__storeQAErrorsInstalled){window.__storeQAErrorsInstalled=true;
      addEventListener('error',e=>window.__storeQA?.uiErrors.push(e.message));
      addEventListener('unhandledrejection',e=>window.__storeQA?.uiErrors.push(String(e.reason?.stack||e.reason)));}
    if(store.__storeQACaptureInstalled)return;store.__storeQACaptureInstalled=true;const dispatch=store.dispatch.bind(store);
    store.dispatch=function(action,payload,...rest){const cap=window.__storeQA;
      const lookup=cap&&action==='app/get'&&/^items[?]/i.test(payload?.url||'')&&String(payload.url).includes('storeId='+cap.storeId);
      const owned=cap&&['app/create','app/update'].includes(action)&&
        ((typeof payload?.data?.notes==='string'&&payload.data.notes.startsWith(cap.marker))||
        (payload?.url==='ManufacturingOrders'&&payload.data?.storeId===cap.storeId&&payload.data.lines?.length>0&&payload.data.lines.every(l=>l.itemId===cap.itemId)));
      const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
      return dispatch(action,payload,...rest).then(result=>{if(lookup){const data=result?.data||result;cap.lookups.push({url:payload.url,total:data?.totalRecords,items:data?.pageData?.map(i=>({id:i.id,arabicName:i.arabicName,currentBalance:i.currentBalance,costPrice:i.costPrice,stores:i.stores?.map(s=>({storeId:s.storeId,currentBalance:s.currentBalance,costPrice:s.costPrice}))}))});}
        if(owned){cap.pending--;const entity=result?.data||result;
        cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});}return result;})
        .catch(error=>{const detail=error?.response?.data??error?.data??{message:error?.message,errors:error?.errors,error:error?.error,title:error?.title,detail:error?.detail,code:error?.code};
          const diagnostic=JSON.stringify({status:error?.response?.status??error?.status,detail});
          if(lookup)cap.lookups.push({url:payload.url,error:diagnostic,status:error?.response?.status});if(owned){cap.pending--;cap.errors.push(diagnostic);}throw error;});};
    """,
                marker,
                storeId,
                itemId);
    }

    String smCaptureMarker() {
        return (String) owner.js("return window.__storeQA?.marker||null");
    }

    List<Map<String, Object>> smSaves() {
        return (List<Map<String, Object>>) owner.js("return window.__storeQA?.saves||[]");
    }

    List<Map<String, Object>> smAttempts() {
        return (List<Map<String, Object>>) owner.js("return window.__storeQA?.attempts||[]");
    }

    void smSettled() {
        owner.ready.until(
                d -> !Boolean.TRUE.equals(owner.js("return (window.__storeQA?.pending||0)>0")));
    }

    String smSaveErrors() {
        return String.valueOf(owner.js("return window.__storeQA?.errors||[]"));
    }

    String smDiagnostics() {
        return String.valueOf(
                owner.js(
                        "return JSON.stringify({lookups:window.__storeQA?.lookups||[],errors:window.__storeQA?.uiErrors||[],saveErrors:window.__storeQA?.errors||[],attempts:window.__storeQA?.attempts||[],saves:window.__storeQA?.saves||[]})"));
    }
}
