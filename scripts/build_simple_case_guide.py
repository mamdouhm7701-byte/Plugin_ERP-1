# شرح الملف: يجمع وصف الحالات في دليل عربي يمكن البحث فيه.
# فائدته: يوضح هذه المهمة أو الاختيارات عند قراءة المشروع من داخل الملفات.
import pathlib,json,html
all={}
for p in pathlib.Path('src/main/resources').glob('allure-case-descriptions-*.json'):all.update(json.loads(p.read_text(encoding='utf-8')))
esc=html.escape
out=['<!doctype html><html lang="ar" dir="rtl"><meta charset="utf-8"><title>دليل حالات الاختبار</title><style>body{font-family:Tahoma,Arial;line-height:1.9;background:#f4f6fa;color:#1b2638;margin:0}main{max-width:1000px;margin:auto;padding:24px}article{background:white;padding:22px;margin:16px 0;border:1px solid #dde3ed;border-radius:10px}h1{font-size:28px}h2{font-size:21px;color:#4c3fad}h3{font-size:16px;margin-bottom:4px}input{padding:14px;width:95%;font:inherit;border:1px solid #aaa;border-radius:8px}li{padding:3px}small{color:#667}header{position:sticky;top:0;background:#f4f6fa;padding:8px}</style><main><header><h1>دليل حالات الاختبار</h1><p>شرح '+str(len(all))+' حالة. ده دليل للخطوات والنتيجة المطلوبة، مش نتيجة تشغيل.</p><input id="search" placeholder="ابحث باسم الشاشة أو الحالة" aria-label="ابحث باسم الشاشة أو الحالة"></header>']
for key,v in all.items():
 out+=['<article><h2>'+esc(v['title'])+'</h2><h3>بنختبر إيه؟</h3><p>'+esc(v['objective'])+'</p><h3>قبل التجربة</h3><p>'+esc(v['preconditions'])+'</p><h3>بيانات التجربة</h3><ul>']
 out+=['<li>'+esc(x.strip())+'</li>' for x in v['data'].split('؛') if x.strip()]
 out+=['</ul><h3>خطوات التجربة</h3><ol>']+['<li>'+esc(x)+'</li>' for x in v['steps']]+['</ol><h3>المتوقع</h3><p>'+esc(v['expected'])+'</p></article>']
out+=['<script>document.getElementById("search").addEventListener("input",e=>{const q=e.target.value.trim().toLowerCase();for(const c of document.querySelectorAll("article"))c.hidden=!c.textContent.toLowerCase().includes(q);});</script></main></html>']
pathlib.Path('docs/testing/TEST_CASES_SIMPLE_AR.html').write_text(''.join(out),encoding='utf-8');print('Built searchable guide with',len(all),'cases')
