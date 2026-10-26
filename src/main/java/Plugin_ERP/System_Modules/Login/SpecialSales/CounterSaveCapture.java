package Plugin_ERP.System_Modules.Login.SpecialSales;

import java.util.List;
import java.util.Map;

/** Observe requests and outcomes for owned counter invoices. */
final class CounterSaveCapture {
    private final SpecialSalesPage owner;

    CounterSaveCapture(SpecialSalesPage owner) {
        this.owner = owner;
    }

    void capture(String marker) {
        owner.js(
                """
                const root=document.querySelector('#app').__vue__,store=root.$store;
                window.__specialSalesQA={marker:arguments[0],endpoint:arguments[1],attempts:[],saves:[],errors:[],pending:0};
                if(store.__specialSalesCaptureInstalled)return;store.__specialSalesCaptureInstalled=true;const dispatch=store.dispatch.bind(store);
                store.dispatch=function(action,payload,...rest){const c=window.__specialSalesQA;
                  const owned=c&&payload?.url===c.endpoint&&['app/create','app/update'].includes(action)&&payload?.data?.notes?.startsWith(c.marker);
                  const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;
                  if(owned){c.pending++;c.attempts.push({endpoint:payload.url,values:sent});}
                  return dispatch(action,payload,...rest).then(result=>{if(owned){c.pending--;const entity=result?.data||result;c.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'?entity:null),values:sent});}return result;})
                    .catch(error=>{if(owned){c.pending--;c.errors.push(JSON.stringify(error?.response?.data||error?.data||{message:String(error)}));}throw error;});};
                """,
                marker,
                owner.endpoint());
    }

    String captureMarker() {
        return (String) owner.js("return window.__specialSalesQA?.marker||null");
    }

    List<Map<String, Object>> attempts() {
        return (List<Map<String, Object>>) owner.js("return window.__specialSalesQA?.attempts||[]");
    }

    List<Map<String, Object>> saves() {
        return (List<Map<String, Object>>) owner.js("return window.__specialSalesQA?.saves||[]");
    }

    void settled() {
        owner.ready.until(
                d ->
                        !Boolean.TRUE.equals(
                                owner.js("return (window.__specialSalesQA?.pending||0)>0")));
    }

    void awaitSave() {
        owner.ready.until(d -> !owner.saves().isEmpty());
        owner.settled();
    }

    void awaitSaveCount(int count) {
        owner.ready.until(d -> owner.saves().size() >= count);
        owner.settled();
    }
}
