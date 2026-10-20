import fs from 'node:fs/promises';
import {Workbook,SpreadsheetFile} from '@oai/artifact-tool';
const dir=new URL('.',import.meta.url).pathname.replace(/^\/([A-Z]:)/,'$1');
const data=JSON.parse(await fs.readFile(new URL('content.json',import.meta.url),'utf8'));
const wb=Workbook.create();
const summary=wb.worksheets.add('ملخص التنفيذ');
const modules=['الدخول','البيانات الأساسية','المتعاملون','الأصناف','المبيعات','المشتريات','المخازن','الحركات المالية','القيود اليومية','تكامل مقترح'];
const tabs=Object.fromEntries(modules.map(m=>[m,wb.worksheets.add(m)]));
const trace=wb.worksheets.add('تتبع الأوتوميشن');const guide=wb.worksheets.add('دليل الاستخدام');
const headers=['رقم الحالة','الشاشة','السيناريو','الأولوية','المتطلبات السابقة','بيانات الاختبار','خطوات التنفيذ','النتيجة المتوقعة','حالة التنفيذ','النتيجة الفعلية','المختبِر','تاريخ التنفيذ','رقم العيب','التنظيف بعد المجموعة','مرجع المشروع','أساس الحالة','نوع الاختبار','ملاحظات المراجعة'];
const widths=[14,25,40,13,48,50,72,72,17,45,20,18,18,52,66,26,23,56];
const statuses=['لم تنفذ','نجحت','فشلت','محجوبة','غير منطبقة'];
function col(n){let s='';for(n++;n;n=Math.floor((n-1)/26))s=String.fromCharCode(65+(n-1)%26)+s;return s;}
function base(sh,title,lastCol,lastRow){
 sh.showGridLines=false;let range=sh.getRange(`A1:${lastCol}${lastRow}`);range.format.font={name:'Arial',size:11,color:'#172B4D'};range.format.verticalAlignment='top';range.format.horizontalAlignment='right';
 sh.getRange('A2').values=[[title]];sh.getRange('A2').format.font={name:'Arial',size:16,bold:true,color:'#172B4D'};sh.getRange('A2').format.rowHeight=30;
}
let total=0;
for(let mi=0;mi<modules.length;mi++){
 const name=modules[mi],sh=tabs[name],cs=data.cases.filter(c=>c.module===name),end=cs.length+5;total+=cs.length;
 base(sh,`حالات ${name}`,'R',end);sh.getRange('A3').values=[['19 أكتوبر 2026 | الحالات لم تُنفذ لهذا التسليم | الأصفر خانات يملؤها المختبِر']];
 sh.getRange('A3').format.font={name:'Arial',size:11,color:'#596579'};
 sh.getRange('A5:R5').values=[headers];
 sh.getRange(`A6:R${end}`).values=cs.map(c=>[c.id,c.screen,c.title,c.priority,c.pre,c.data,c.steps,c.expected,c.status,c.actual,c.tester,null,c.bug,c.cleanup,c.source,c.basis,c.kind,c.note]);
 const t=sh.tables.add(`A5:R${end}`,true,`Cases_${mi}`);t.showFilterButton=true;
 sh.getRange('A5:R5').format={fill:'#203B61',font:{name:'Arial',size:11,bold:true,color:'#FFFFFF'},wrapText:true,verticalAlignment:'center',horizontalAlignment:'center',rowHeight:36};
 sh.getRange(`A6:R${end}`).format.wrapText=true;
 for(let i=0;i<18;i++)sh.getRange(`${col(i)}5:${col(i)}${end}`).format.columnWidth=widths[i];
 for(let i=0;i<cs.length;i++){
  const r=i+6,c=cs[i];sh.getRange(`A${r}:R${r}`).format.rowHeight=Math.max(140,Math.min(230,Math.ceil(c.steps.length/75)*18+20));
  if(i%2===1)sh.getRange(`A${r}:R${r}`).format.fill='#F3F6FA';
 }
 sh.getRange(`I6:M${end}`).format.fill='#FFF2CC';sh.getRange(`L6:L${end}`).setNumberFormat('yyyy-mm-dd');
 sh.getRange(`I6:I${end}`).dataValidation={rule:{type:'list',values:statuses}};
 sh.getRange(`I6:I${end}`).conditionalFormats.add('cellIs',{operator:'equal',formula:'"فشلت"',format:{fill:'#FCE4D6',font:{color:'#9C0006',bold:true}}});
 sh.getRange(`I6:I${end}`).conditionalFormats.add('cellIs',{operator:'equal',formula:'"محجوبة"',format:{fill:'#FCE4D6',font:{color:'#9C6500'}}});
 sh.freezePanes.freezeRows(5);sh.freezePanes.freezeColumns(2);
}
base(summary,'متابعة تنفيذ اختبارات Plugin ERP','I',21);summary.tabColor='#203B61';
summary.getRange('A3').values=[['408 حالات مانويل | نتائج جديدة يبدأ تسجيلها عند التنفيذ | 19 أكتوبر 2026']];
summary.getRange('A5:I5').values=[['الموديول','إجمالي الحالات','لم تنفذ','نجحت','فشلت','محجوبة','غير منطبقة','نسبة المنفذ','نسبة النجاح']];
modules.forEach((m,i)=>{
 const r=i+6,n=data.cases.filter(c=>c.module===m).length,e=n+5;summary.getRange(`A${r}`).values=[[m]];
 summary.getRange(`B${r}`).formulas=[[`=COUNTA('${m}'!A6:A${e})`]];
 statuses.forEach((s,j)=>summary.getRange(`${col(j+2)}${r}`).formulas=[[`=COUNTIF('${m}'!I6:I${e},"${s}")`]]);
 summary.getRange(`H${r}`).formulas=[[`=IFERROR((D${r}+E${r})/(B${r}-G${r}),0)`]];
 summary.getRange(`I${r}`).formulas=[[`=IFERROR(D${r}/(D${r}+E${r}),0)`]];
});
summary.getRange('A17').values=[['الإجمالي']];for(let c=1;c<=6;c++)summary.getRange(`${col(c)}17`).formulas=[[`=SUM(${col(c)}6:${col(c)}15)`]];
summary.getRange('H17').formulas=[['=IFERROR((D17+E17)/(B17-G17),0)']];summary.getRange('I17').formulas=[['=IFERROR(D17/(D17+E17),0)']];
summary.getRange('H6:I17').setNumberFormat('0.0%');summary.getRange('A5:A17').format.columnWidth=28;summary.getRange('B5:I17').format.columnWidth=18;
summary.getRange('A5:I5').format={fill:'#203B61',font:{name:'Arial',size:11,bold:true,color:'#FFFFFF'},wrapText:true,rowHeight:35};summary.getRange('A6:I17').format.rowHeight=30;summary.getRange('A17:I17').format.fill='#DCE6F1';
summary.getRange('A19').values=[['نسبة المنفذ = نجحت وفشلت ÷ الإجمالي دون غير المنطبق؛ المحجوبة لا تعد تنفيذاً مكتملاً.']];
summary.getRange('A20').values=[['نسبة النجاح = نجحت ÷ نجحت وفشلت؛ صفر مع عدم التنفيذ لا يعني فشل الحالات.']];
summary.getRange('A21').values=[['كل تبويب موديول قابل للفلترة؛ راجع دليل الاستخدام قبل البدء.']];
base(trace,'سجل تعريفات اختبارات الأوتوميشن','G',data.trace.length+5);
trace.getRange('A3').values=[['حصر تعريفات من المصدر وليس عدد استدعاءات أو نتائج تنفيذ؛ الوراثة والمعاملات والسويتات قد تغير العدد.']];
trace.getRange('A5:G5').values=[['الكلاس','الدالة','وصف المصدر','التصنيف','حالة التعريف','المصدر والسطر','حدود القراءة']];
trace.getRange(`A6:G${data.trace.length+5}`).values=data.trace;trace.tables.add(`A5:G${data.trace.length+5}`,true,'AutomationTrace');
trace.getRange(`A5:G${data.trace.length+5}`).format.wrapText=true;trace.getRange(`A6:G${data.trace.length+5}`).format.rowHeight=70;
[36,65,85,34,18,100,65].forEach((w,i)=>trace.getRange(`${col(i)}5:${col(i)}${data.trace.length+5}`).format.columnWidth=w);
trace.getRange('A5:G5').format={fill:'#203B61',font:{name:'Arial',size:11,bold:true,color:'#FFFFFF'},rowHeight:30};trace.freezePanes.freezeRows(5);trace.freezePanes.freezeColumns(1);
const guideRows=[
 ['الغرض','حالات تنفيذ يدوي بالعربية لتسهيل البداية؛ الدليل Word يشرح النظام والتشغيل والنتائج.'],
 ['نطاق النسخة','408 حالات أساسية ومقترحة؛ لا تدعي تحويل كل تعريف أو كل مسار في المشروع إلى حالة مانويل مستقلة.'],
 ['المصدر','مراجعة Java وملفات السويت والتغطية والعقود الموجودة في المشروع بتاريخ 19 أكتوبر 2026؛ لا تنفيذ حي جديد.'],
 ['المستخرج من المشروع','صياغة مانويل مبنية على السيناريو أو العقد في مرجع المشروع، وليست بالضرورة تطابقاً حرفياً لدالة واحدة.'],
 ['المقترح يحتاج اعتماد','سيناريو أو توسيع تحقق لم تثبت متطلباته بالكامل من المشروع؛ يعتمد قبل استخدامه كبوابة إصدار.'],
 ['أول خطوة','اختر الموديول ثم فلتر الشاشة والأولوية وأساس الحالة. راجع البيانات والمتوقع قبل التنفيذ.'],
 ['تجهيز البيانات','استبدل QA-MAN-<التاريخ>-<رقم الحالة> بقيمة فريدة، وسجل أكواد السجلات. الأرقام أمثلة يدوية وليست بيانات تشغيل.'],
 ['التسجيل','الأصفر خانات قابلة للتعبئة: الحالة والفعلي والمختبِر والتاريخ ورقم العيب. لا تسجل كلمات المرور.'],
 ['لم تنفذ','الحالة لم تبدأ في دورة التنفيذ الحالية. جميع الحالات تبدأ بهذه القيمة.'],
 ['نجحت','الفعلي يطابق المتوقع؛ رفض حفظ غير صالح يعد نجاحاً للحالة.'],
 ['فشلت','يمكن تنفيذ الحالة لكن الفعلي يخالف المتوقع؛ سجل رقم عيب ودليلاً.'],
 ['محجوبة','متطلب أو صلاحية أو إجراء يدوي غير متاح يمنع التحقق؛ اكتب السبب.'],
 ['غير منطبقة','الخيار غير مدعوم أو خارج الإصدار بقرار معتمد؛ اذكر السبب والقرار.'],
 ['ملاحظة العيوب','العيب الموثق سابقاً ليس نتيجة حالية. احتفظ بالتوقع وأعد التنفيذ؛ لا تملأ النتيجة تلقائياً بالفشل.'],
 ['التنظيف','بعد مجموعة الصفحة احذف التابع قبل المرجع وفق الصلاحيات؛ لا تحذف بيانات تشغيل أو تستخدم حذفاً عاماً.'],
 ['الفواتير المقيدة','حذف تجهيز الأوتوميشن بصلاحية خاصة لا يعني وجود حق حذف يدوي؛ استخدم بيئة مخصصة وإجراء تنظيف معتمد.'],
 ['تتبع الأوتوميشن','تعريفات دوال؛ الوراثة وDataProvider والمداخل المتوافقة تمنع مساواة العدد بعدد الحالات الفعلية.'],
 ['التحقق الآلي للملف','معادلات الملخص مرتبطة بحالات التنفيذ. تمت مراجعة القيم والتصدير والعينات المرئية؛ لا يعد ذلك تنفيذ ERP.'],
 ['فجوات متعمدة','الصلاحيات والتوازي وانقطاع الاتصال والتغطية الضريبية الكاملة تحتاج اعتماداً وتجهيزاً إضافيين؛ مقترحات التكامل منفصلة.'],
 ['التاريخ','أدخل تاريخ Excel صالحاً في عمود تاريخ التنفيذ؛ العرض yyyy-mm-dd.'],
];
base(guide,'كيفية تنفيذ الحالات وتسجيل النتائج','B',guideRows.length+5);guide.getRange('A5:B5').values=[['الموضوع','التعليمات']];guide.getRange(`A6:B${guideRows.length+5}`).values=guideRows;guide.getRange(`A5:B${guideRows.length+5}`).format.wrapText=true;guide.getRange(`A6:B${guideRows.length+5}`).format.rowHeight=65;guide.getRange('A5:A25').format.columnWidth=30;guide.getRange('B5:B25').format.columnWidth=125;guide.getRange('A5:B5').format={fill:'#203B61',font:{name:'Arial',size:11,bold:true,color:'#FFFFFF'},rowHeight:30};
// اختبار تغيير حالة فعلية ثم إعادتها قبل التسليم
tabs['الدخول'].getRange('I6').values=[['نجحت']];wb.recalculate();let probe=summary.getRange('D6').values[0][0];if(probe!==1)throw Error('Summary does not react: '+probe);tabs['الدخول'].getRange('I6').values=[['لم تنفذ']];
wb.recalculate();const val=summary.getRange('B17:C17').values[0];if(val[0]!==total||val[1]!==total)throw Error('Total mismatch '+JSON.stringify(val));
await fs.writeFile(new URL('verification.json',import.meta.url),JSON.stringify({manualCases:total,traceRows:data.trace.length,totalCheck:val,statusChangeProbe:probe},null,2));
console.log((await wb.inspect({kind:'match',searchTerm:'#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A|#NUM!|#NULL!',options:{useRegex:true,maxResults:20},summary:'formula errors'})).ndjson);
const xlsx=await SpreadsheetFile.exportXlsx(wb);await xlsx.save(new URL('../Plugin_ERP_Manual_Test_Cases_AR.xlsx',import.meta.url).pathname.replace(/^\/([A-Z]:)/,'$1'));
await fs.mkdir(new URL('sheet-previews/',import.meta.url),{recursive:true});
for(const sh of [summary,...modules.map(m=>tabs[m]),trace,guide]){
 const range=sh===summary?'A2:I17':sh===guide?'A5:B10':sh===trace?'A5:C8':'A5:D8';
 try{const png=await wb.render({sheetName:sh.name,range,scale:1.3,format:'png'});await fs.writeFile(new URL(`sheet-previews/${sh.name}.png`,import.meta.url),new Uint8Array(await png.arrayBuffer()));console.log('Rendered '+sh.name);}catch(e){console.log('Render issue '+sh.name+': '+e.message);throw e;}
}
const detail=await wb.render({sheetName:'المبيعات',range:'F5:H7',scale:1.2,format:'png'});await fs.writeFile(new URL('sheet-previews/detail.png',import.meta.url),new Uint8Array(await detail.arrayBuffer()));
console.log('Saved '+total+' manual cases.');
