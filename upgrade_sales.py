# شرح الملف: يجهز أشكالًا مختلفة لتقرير المبيعات.
# أداة اختيارية لإنتاج أشكال مختلفة من تقرير المبيعات. تستخدم عند تعديل شكل التقرير فقط.
from pathlib import Path
from copy import deepcopy
from collections import Counter
import xml.etree.ElementTree as E
src=Path(r'C:/Users/pcx/Downloads/New folder/Reports/Reports/SalesTotalsNetBased-ar.rdl')
outdir=Path(r'C:/Users/pcx/Desktop/SalesReport-Upgrades');outdir.mkdir(exist_ok=True)
for _,p in E.iterparse(src,events=['start-ns']):E.register_namespace(*p)
original=E.parse(src).getroot();ns=original.tag.split('}')[0][1:];N={'r':ns};q=lambda t:'{'+ns+'}'+t
def f(p,s):return p.find(s,N)
def el(p,t,text=None,**kw):
 e=E.SubElement(p,q(t),kw);e.text=None if text is None else str(text);return e
def sv(p,t,v):
 e=f(p,'r:'+t)
 if e is None:e=el(p,t)
 e.text=str(v);return e
def style(p):
 e=f(p,'r:Style');return e if e is not None else el(p,'Style')
def cm(s):return float(s[:-2])*(2.54 if s.endswith('in') else 1)
def pos(e,x,y,w,h):
 for k,v in [('Left',x),('Top',y),('Width',w),('Height',h)]:sv(e,k,f'{v:.5f}cm')
def paint(tb,color,size='9pt',bg=None,bold=False):
 s=style(tb);sv(s,'VerticalAlign','Middle');sv(s,'Direction','RTL')
 for k in ['PaddingLeft','PaddingRight']:sv(s,k,'3pt')
 for run in tb.findall('.//r:TextRun',N):
  rs=style(run);sv(rs,'FontFamily','Dubai');sv(rs,'FontSize',size);sv(rs,'Color',color)
  if bold:sv(rs,'FontWeight','Bold')
 if bg:sv(s,'BackgroundColor',bg)
def text(p,name,value,x,y,w,h,color,size='9pt',bg=None,bold=False):
 tb=el(p,'Textbox',Name=name);el(tb,'CanGrow','true');el(tb,'KeepTogether','true')
 par=el(el(tb,'Paragraphs'),'Paragraph');run=el(el(par,'TextRuns'),'TextRun');el(run,'Value',value);sv(style(par),'TextAlign','Right')
 pos(tb,x,y,w,h);paint(tb,color,size,bg,bold);return tb
def exprs(r):return Counter(e.text for e in r.iter() if e.text and e.text.lstrip().startswith('='))
net='Sum(IIF(Fields!TransactionType.Value = 2, Fields!Net.Value * -1, Fields!Net.Value), "MainQuery")'
tax='Sum(IIF(Fields!TransactionType.Value = 2, Fields!TotalTaxes.Value * -1, Fields!TotalTaxes.Value), "MainQuery")'
discount='Sum(IIF(Fields!TransactionType.Value = 2, Fields!Discount.Value * -1, Fields!Discount.Value), "MainQuery")'
digits='First(Fields!DecimalDigits.Value, "BranchData")'
def chart(items,name,category,x,y,w,color):
 c=el(items,'Chart',Name=name)
 m=el(el(el(c,'ChartCategoryHierarchy'),'ChartMembers'),'ChartMember');g=el(m,'Group',Name=name+'Group');el(el(g,'GroupExpressions'),'GroupExpression',category)
 sort=el(el(m,'SortExpressions'),'SortExpression');el(sort,'Value','=Sum(IIF(Fields!TransactionType.Value = 2, -Fields!Net.Value, Fields!Net.Value))');el(sort,'Direction','Descending');el(m,'Label',category)
 if name=='TopGroupsChart':
  fl=el(el(g,'Filters'),'Filter');el(fl,'FilterExpression','=Sum(IIF(Fields!TransactionType.Value = 2, -Fields!Net.Value, Fields!Net.Value))');el(fl,'Operator','TopN');el(el(fl,'FilterValues'),'FilterValue','=10')
 el(el(el(el(c,'ChartSeriesHierarchy'),'ChartMembers'),'ChartMember'),'Label','الصافي')
 s=el(el(el(c,'ChartData'),'ChartSeriesCollection'),'ChartSeries',Name=name+'Net')
 dp=el(el(s,'ChartDataPoints'),'ChartDataPoint');el(el(dp,'ChartDataPointValues'),'Y','=Sum(IIF(Fields!TransactionType.Value = 2, -Fields!Net.Value, Fields!Net.Value))')
 dl=el(dp,'ChartDataLabel');el(dl,'Visible','true');el(style(dl),'Format','#,0.##');el(style(dl),'FontSize','8pt');el(style(dp),'Color',color)
 el(s,'Type','Bar');el(s,'ChartAreaName','Default');el(s,'CategoryAxisName','Primary');el(s,'ValueAxisName','Primary')
 a=el(el(c,'ChartAreas'),'ChartArea',Name='Default')
 for kind in ['ChartCategoryAxes','ChartValueAxes']:
  ax=el(el(a,kind),'ChartAxis',Name='Primary');el(style(ax),'FontFamily','Dubai');el(style(ax),'FontSize','8pt')
  if kind=='ChartCategoryAxes':el(ax,'Reverse','true');el(ax,'Interval','1')
  grid=el(ax,'ChartMajorGridLines');el(grid,'Enabled','False')
 el(c,'DataSetName','MainQuery');el(c,'NoRowsMessage','لا توجد بيانات');pos(c,x,y,w,5.6);el(style(c),'BackgroundColor','White')
 return c

for mode,accent,light,title in [('01-Modern-Teal','#176B64','#EDF6F3','نظرة شاملة على المبيعات'),('02-Executive-Navy','#253B60','#EEF2F8','لوحة تحليل المبيعات'),('03-Print-Classic','#30343B','#F1F1F1','تقرير إجمالي المبيعات')]:
 r=deepcopy(original);body=r.find('.//r:Body',N);items=f(body,'r:ReportItems');table=items.find('r:Tablix',N)
 named=lambda name:next(e for e in r.iter() if e.get('Name')==name)
 for tb in r.findall('.//r:Textbox',N):paint(tb,'#28323C','8pt')
 # Standard A4 landscape: fit every column into the printable width.
 section=r.find('.//r:ReportSection',N);sv(section,'Width','28.1cm')
 page=r.find('.//r:Page',N)
 for k,v in [('PageWidth','29.7cm'),('PageHeight','21cm'),('LeftMargin','.8cm'),('RightMargin','.8cm'),('TopMargin','.6cm'),('BottomMargin','.6cm'),('InteractiveWidth','29.7cm'),('InteractiveHeight','21cm')]:sv(page,k,v)
 for container in [f(page,'r:PageHeader'),f(page,'r:PageFooter')]:
  for e in f(container,'r:ReportItems'):
   left=f(e,'r:Left');width=f(e,'r:Width')
   if left is not None:left.text=f'{cm(left.text)*.95:.5f}cm'
   if width is not None:width.text=f'{cm(width.text)*.95:.5f}cm'
 sv(f(page,'r:PageFooter'),'Height','1.95cm')
 pos(named('Textbox1'),.1,.1,27.9,.9);paint(named('Textbox1'),accent,'20pt',bold=True)
 # Caption changed, original calculated values are preserved verbatim.
 f(named('Textbox1'),'.//r:Value').text=title
 filters=named('Rectangle2');pos(filters,.1,1.2,27.9,2.2);sv(style(filters),'BackgroundColor',light)
 mapping={'Textbox15':(24.2,.15,3.3,.65),'Textbox2':(17.5,.15,6.5,.65),'Textbox16':(10.5,.15,3.3,.65),'Textbox6':(.3,.15,10,.65),'Textbox17':(24.2,1.1,3.3,.65),'Textbox19':(17.5,1.1,6.5,.65),'Textbox161':(10.2,1.1,3.6,.65),'Textbox18':(.3,1.1,9.6,.65)}
 for name,coords in mapping.items():pos(named(name),*coords)
 cols=table.findall('r:TablixBody/r:TablixColumns/r:TablixColumn',N);full=sum(cm(f(c,'r:Width').text) for c in cols)
 for c in cols:sv(c,'Width',f'{cm(f(c,"r:Width").text)*27.9/full:.6f}cm')
 rows=table.findall('r:TablixBody/r:TablixRows/r:TablixRow',N)
 for i,row in enumerate(rows):
  for tb in row.findall('.//r:Textbox',N):
   bg=accent if i==0 else light if i>=2 else '=IIF(RowNumber(Nothing) Mod 2 = 0, "'+light+'", "White")'
   paint(tb,'White' if i==0 else '#28323C','8pt',bg,i!=1)
   s=style(tb)
   for k in ['Border','TopBorder','BottomBorder','LeftBorder','RightBorder']:
    border=f(s,'r:'+k)
    if border is not None:sv(border,'Color','#D8DEE3');sv(border,'Style','None')
   border=sv(s,'BottomBorder','');border.text=None;sv(border,'Color','#D8DEE3');sv(border,'Style','Solid');sv(border,'Width','.5pt')
   if mode.startswith('03'):sv(style(tb),'BackgroundColor','White' if i==1 else light);paint(tb,accent,'8pt',bold=i!=1)
  old=cm(f(row,'r:Height').text);sv(row,'Height',f'{max(old,.7 if i else .9):.5f}cm')
 # Cards use transaction-level grouped totals: no item-level duplicate sums.
 top=3.8
 metrics=[('صافي المبيعات',f'=FormatNumber({net}, {digits})'),('عدد الحركات','=Format(CountDistinct(Fields!Id.Value, "MainQuery"), "#,0")'),('إجمالي الضريبة',f'=FormatNumber({tax}, {digits})'),('إجمالي الخصومات',f'=FormatNumber({discount}, {digits})')]
 for i,(label,value) in enumerate(metrics):
  x=.1+i*7.05;h=1.35 if mode.startswith('03') else 1.9
  card=el(items,'Rectangle',Name='SummaryCard'+str(i));ri=el(card,'ReportItems');el(card,'KeepTogether','true');pos(card,x,top,6.75,h);sv(style(card),'BackgroundColor',light)
  text(ri,'SummaryCaption'+str(i),label,.15,.1,6.4,.45,accent,'9pt',bold=True)
  text(ri,'SummaryValue'+str(i),value,.15,.6,6.4,.65 if mode.startswith('03') else 1,accent,'15pt' if mode.startswith('03') else '21pt',bold=True)
 table_top=6.3 if not mode.startswith('03') else 5.55
 if mode.startswith('02'):
  # Same grouping keys as the report; include all transaction types and top ten groups.
  category='=Switch((Fields!TransactionType.Value = 2 and Fields!TransactionChannel.Value = 1),"مرتجع مبيعات",(Fields!TransactionType.Value = 1 and Fields!TransactionChannel.Value = 1),"مبيعات",(Fields!TransactionType.Value = 1 and Fields!TransactionChannel.Value = 2),"مطاعم") & " " & IIf(Fields!PaymentType.Value = 1,"نقدي","آجل")'
  text(items,'TypeChartTitle','صافي المبيعات حسب نوع الحركة',14.4,6.2,13.6,.65,accent,'11pt',bold=True)
  text(items,'GroupsChartTitle','أعلى ١٠ تجميعات حسب الصافي',.1,6.2,13.6,.65,accent,'11pt',bold=True)
  chart(items,'TypeMixChart',category,14.4,6.95,13.6,accent)
  chart(items,'TopGroupsChart','=Fields(Parameters!groupBy.Value).Value',.1,6.95,13.6,'#3B8A8E')
  table_top=13.35
 text(items,'TransactionsHeading','تفاصيل الحركات',.1,table_top-.55,27.9,.5,accent,'10pt',bold=True)
 height=sum(cm(f(row,'r:Height').text) for row in rows);pos(table,.1,table_top,27.9,height);sv(body,'Height',f'{table_top+height+.3:.5f}cm')
 assert not (exprs(original)-exprs(r)), 'Original expression missing'
 for tag in ['DataSources','DataSets','ReportParameters','Code','CodeModules']:
  a=f(original,'r:'+tag);b=f(r,'r:'+tag);assert (E.tostring(a) if a is not None else None)==(E.tostring(b) if b is not None else None),tag
 assert E.tostring(f(original.find('.//r:Tablix',N),'r:TablixRowHierarchy'))==E.tostring(f(table,'r:TablixRowHierarchy'))
 names=[e.get('Name') for e in r.iter() if e.tag in {q(k) for k in ['Textbox','Rectangle','Tablix','Chart','Image']}];assert len(names)==len(set(names))
 path=outdir/('SalesTotals-'+mode+'.rdl');tree=E.ElementTree(r);E.indent(tree,space='  ');tree.write(path,encoding='utf-8',xml_declaration=True);E.parse(path)
 print(path.name+': PASS expressions, datasets, parameters, grouping, unique names, XML')
