from pathlib import Path
from copy import deepcopy
from collections import Counter
import xml.etree.ElementTree as E
src=Path(r'C:/Users/pcx/Desktop/Invoice-ar.rdl');out=src.with_name('Invoice-ar-Sidebar-Plum.rdl')
for _,p in E.iterparse(src,events=['start-ns']):E.register_namespace(*p)
tree=E.parse(src);root=tree.getroot();ns=root.tag.split('}')[0][1:];N={'r':ns};q=lambda t:'{'+ns+'}'+t
original=deepcopy(root)
plum='#3F214F';gold='#E7AA52';muted='#8D7A99';pale='#F5F0F7';ink='#33283B';line='#E3D8E9'
def f(p,path):return p.find(path,N)
def add(p,t,text=None,**a):
 e=E.SubElement(p,q(t),a);e.text=text;return e
def setv(p,t,v):
 e=f(p,'r:'+t)
 if e is None:e=add(p,t)
 e.text=str(v);return e
def sty(p):
 s=f(p,'r:Style')
 return s if s is not None else add(p,'Style')
def named(name):return next(e for e in root.iter() if e.get('Name')==name)
def pos(e,x,y,w,h):
 for t,v in [('Top',y),('Left',x),('Height',h),('Width',w)]:setv(e,t,f'{v:.5f}cm')
def paint(tb,bg=None,color=ink,size='9pt',bold=False,align='Right'):
 s=sty(tb);setv(s,'VerticalAlign','Middle');setv(s,'Direction','RTL')
 for k in ['PaddingLeft','PaddingRight']:setv(s,k,'4pt')
 for k in ['PaddingTop','PaddingBottom']:setv(s,k,'2pt')
 border=f(s,'r:Border')
 if border is None:border=add(s,'Border')
 setv(border,'Style','None')
 if bg:setv(s,'BackgroundColor',bg)
 for p in tb.findall('r:Paragraphs/r:Paragraph',N):setv(sty(p),'TextAlign',align)
 for run in tb.findall('.//r:TextRun',N):
  rs=sty(run);setv(rs,'FontFamily','Dubai');setv(rs,'FontSize',size);setv(rs,'Color',color)
  if bold:setv(rs,'FontWeight','Bold')
def textbox(parent,name,text,x,y,w,h,bg=None,color=ink,size='9pt',bold=False,align='Right'):
 tb=add(parent,'Textbox',Name=name);add(tb,'CanGrow','true');add(tb,'KeepTogether','true')
 run=add(add(add(add(tb,'Paragraphs'),'Paragraph'),'TextRuns'),'TextRun');add(run,'Value',text)
 pos(tb,x,y,w,h);paint(tb,bg,color,size,bold,align);return tb
body=root.find('.//r:Body',N);items=f(body,'r:ReportItems')
def rect(name,x,y,w,h,bg):
 e=E.Element(q('Rectangle'),Name=name);ri=add(e,'ReportItems');add(e,'KeepTogether','true');pos(e,x,y,w,h)
 setv(sty(e),'BackgroundColor',bg);items.insert(0,e);return e,ri
def move(name,dest,x,y,w,h):
 e=named(name)
 for p in root.iter():
  if e in list(p):p.remove(e);break
 dest.append(e);pos(e,x,y,w,h);return e
for tb in root.findall('.//r:Textbox',N):paint(tb)
for c in root.findall('.//r:Color',N):
 if c.text in ['SlateBlue','LightGrey']:c.text=line
# Header: real logo and original tax QR expressions remain untouched.
pos(named('CompanyLogo'),0.1,0.1,4.5,3.2);pos(named('Image2'),15.15,0.1,4.1,3.6)
pos(named('Textbox62'),4.85,0.25,9.9,1.65);paint(named('Textbox62'),color=plum,size='18pt',bold=True)
for run in named('Textbox62').findall('.//r:TextRun',N):
 value=f(run,'r:Value').text or ''
 if 'TAX' in value:setv(sty(run),'FontSize','9pt')
 elif not value.strip():setv(sty(run),'FontSize','3pt')
pos(named('Textbox12'),6.1,2.1,8.65,0.8);paint(named('Textbox12'),color=plum,size='12pt',bold=True)
pos(named('Textbox60'),4.85,2.1,2.6,0.8);paint(named('Textbox60'),color=muted,size='8pt',align='Left')
pos(named('Line4'),4.85,1.98,9.9,0);setv(sty(named('Line4')),'Border','')
# Restore a real border node for the gold header line.
st=sty(named('Line4'));b=f(st,'r:Border');b.text=None;setv(b,'Color',gold);setv(b,'Style','Solid');setv(b,'Width','3pt')
textbox(items,'VerifyCaption','Scan to verify | امسح للتحقق',15.1,3.72,4.2,0.4,color=muted,size='7pt',align='Center')
# Stack customer and supplier cards on the left.
for name,y in [('Rectangle3',4.35),('Rectangle2',8.05)]:
 e=named(name);pos(e,0.1,y,13.05,3.5);setv(sty(e),'BackgroundColor','White')
 b=f(sty(e),'r:Border')
 if b is None:b=add(sty(e),'Border')
 setv(b,'Color',line);setv(b,'Style','Solid');setv(b,'Width','1pt')
ri3=f(named('Rectangle3'),'r:ReportItems');ri2=f(named('Rectangle2'),'r:ReportItems')
for name,coords in {
 'Textbox44':(8.6,.05,4.2,.55),'Textbox48':(.2,.05,4,.55),'Line2':(.2,.65,12.6,0),
 'Textbox2':(.25,.72,12.5,.65),'Textbox4':(.25,1.4,12.5,.55),'Textbox10':(.25,1.97,12.5,.65),'Textbox8':(.25,2.67,7,.55),
 'Textbox43':(8.6,.05,4.2,.55),'Textbox45':(.2,.05,4,.55),'Line1':(.2,.65,12.6,0),
 'Textbox7':(.25,.72,12.5,.65),'Textbox1':(.25,1.97,12.5,.65),'Textbox41':(.25,2.65,12.5,.6)
}.items():pos(named(name),*coords)
for name in ['Textbox44','Textbox43']:paint(named(name),color=plum,bold=True,size='11pt')
for name in ['Textbox48','Textbox45']:paint(named(name),color=gold,bold=True,size='8pt',align='Left')
move('Textbox52',ri2,.25,1.4,12.5,.55)
f(named('Textbox1'),'.//r:Value').text='="عنوان المورد: " & First(Fields!Address.Value, "CompanyData")'
textbox(ri3,'CustomerPhone','=First(Fields!DealerPhone.Value, "Invoice")',7.5,2.67,5.25,.55,color=muted)
for ri,name in [(ri3,'ClientAccent'),(ri2,'SupplierAccent')]:
 a=add(ri,'Rectangle',Name=name);pos(a,12.92,0,.12,3.5);setv(sty(a),'BackgroundColor',gold)
# Sidebar metadata uses existing expressions and dataset fields.
side,sri=rect('PlumSidebar',13.45,4.35,5.85,7.2,plum)
for name,coords in {'Textbox18':(.2,.1,3.4,.4),'Textbox31':(.15,.1,1.8,.4),'Textbox22':(.2,.5,5.4,.65),
 'Textbox39':(.2,1.27,5.4,.4),'Textbox42':(.15,1.27,1.9,.4),'Textbox37':(.2,1.7,5.4,.65),
 'Textbox17':(.2,2.55,5.4,.95)}.items():
 tb=move(name,sri,*coords);paint(tb,color='White' if name in ['Textbox22','Textbox37','Textbox17'] else '#C7B6D0',size='10pt' if name in ['Textbox22','Textbox37','Textbox17'] else '8pt',bold=name in ['Textbox22','Textbox37'])
for label,expr,y,tag in [('طريقة الدفع','=First(Fields!ArabicName.Value, "InvoicePayments")',3.65,'Payment'),('المخزن','=First(Fields!StoreArabicName.Value, "Invoice")',4.8,'Store'),('رقم الطلب','=First(Fields!OrderNumber.Value, "Invoice")',5.95,'Order')]:
 textbox(sri,tag+'Caption',label,.2,y,5.4,.4,color='#C7B6D0',size='8pt')
 textbox(sri,tag+'Value',expr,.2,y+.4,5.4,.6,color='White',bold=True,size='10pt')
# Item table: add barcode without changing any existing item expressions.
t=named('Tablix3');pos(t,.1,11.9,19.2,2.65)
cols=f(t,'r:TablixBody/r:TablixColumns')
for c,w in zip(cols,[2.1,2.1,1.8,9.7]):setv(c,'Width',str(w)+'cm')
c=add(cols,'TablixColumn');add(c,'Width','3.5cm')
add(f(t,'r:TablixColumnHierarchy/r:TablixMembers'),'TablixMember')
rows=f(t,'r:TablixBody/r:TablixRows')
for i,row in enumerate(rows):
 setv(row,'Height',['1.05cm','.8cm','.55cm'][i]);cells=f(row,'r:TablixCells');cell=add(cells,'TablixCell')
 if i<2:
  cc=add(cell,'CellContents');textbox(cc,'BarcodeHeader' if i==0 else 'BarcodeValue','باركود الصنف\nITEM BARCODE' if i==0 else '=Fields!Barcode.Value',0,0,3.5,.8)
 else:setv(f(cells[0],'r:CellContents'),'ColSpan','5')
 for tb in row.findall('.//r:Textbox',N):
  paint(tb,bg=plum if i==0 else '=IIF(RowNumber(Nothing) Mod 2 = 0, "'+pale+'", "White")',color='White' if i==0 else ink,size='8pt' if i==0 or i==2 else '9pt',bold=i==0)
  if i>0:
   bb=add(sty(tb),'BottomBorder');add(bb,'Color',line);add(bb,'Style','Solid');add(bb,'Width','0.5pt')
# Keep the totals tablix, its grouping, visibility, and exact financial expressions.
t=named('Tablix2');pos(t,.1,15.05,11.5,5.0)
for col in t.findall('r:TablixBody/r:TablixColumns/r:TablixColumn',N):setv(col,'Width','8.1cm')
for h in t.findall('.//r:TablixHeader',N):setv(h,'Size','3.4cm')
for row in t.findall('r:TablixBody/r:TablixRows/r:TablixRow',N):setv(row,'Height','.68cm')
for tb in t.findall('.//r:Textbox',N):paint(tb,bg=pale,size='9pt')
for name in ['Textbox50']:
 paint(named(name),bg=gold,color='White',size='12pt',bold=True)
for tb in t.findall('.//r:Textbox',N):
 if any(v.text and 'الصافي بعد الضريبة' in v.text for v in tb.findall('.//r:Value',N)):paint(tb,bg=gold,color='White',size='11pt',bold=True)
amount,ari=rect('InvoiceAmountCard',13.45,15.05,5.85,2.45,plum)
textbox(ari,'AmountCaption','صافي الفاتورة | INVOICE NET',.2,.2,5.4,.5,color='#D5C6DE',size='9pt')
textbox(ari,'AmountNet','=FormatNumber(First(Fields!Net.Value, "Invoice"), First(Fields!DecimalDigits.Value, "BranchData"))',.2,.8,5.4,1.0,color='White',size='23pt',bold=True)
textbox(ari,'AmountHint','شامل ضريبة القيمة المضافة',.2,1.9,5.4,.35,color='#D5C6DE',size='7pt')
textbox(items,'NotesCaption','ملاحظات | NOTES',13.45,17.8,5.85,.6,color=plum,size='10pt',bold=True)
pos(named('Textbox34'),13.45,18.45,5.85,2.15);paint(named('Textbox34'),bg=pale,size='9pt')
pos(named('Textbox20'),11.8,22.1,7.5,.65);paint(named('Textbox20'),color=plum,bold=True)
textbox(items,'ReceiverSignature','توقيع المستلم',.1,22.1,7.5,.65,color=muted,align='Center')
textbox(items,'CompanyTerms','=First(Fields!Notes.Value, "CompanyData")',.1,21.0,19.2,.75,bg=pale,size='8pt')
setv(body,'Height','23.3cm');setv(root.find('.//r:ReportSection',N),'Width','19.4cm')
page=root.find('.//r:Page',N)
for k,v in [('PageWidth','21cm'),('PageHeight','29.7cm'),('LeftMargin','.8cm'),('RightMargin','.8cm'),('TopMargin','.5cm'),('BottomMargin','.5cm')]:setv(page,k,v)
for tb in page.findall('.//r:Textbox',N):paint(tb,color=muted,size='7pt')
setv(sty(named('Line3')).find('r:Border',N),'Color',line)
# Verification: every original expression and non-layout data section is preserved.
def expressions(r):return Counter(e.text for e in r.iter() if e.text and e.text.lstrip().startswith('='))
missing=expressions(original)-expressions(root);assert not missing,missing
for tag in ['DataSources','DataSets','ReportParameters','Code','CodeModules']:
 a=f(original,'r:'+tag);b=f(root,'r:'+tag)
 assert (E.tostring(a) if a is not None else None)==(E.tostring(b) if b is not None else None),tag
names=[e.get('Name') for e in root.iter() if e.tag in {q(k) for k in ['Textbox','Rectangle','Tablix','Image','Line']}]
assert len(names)==len(set(names))
assert len(cols)==5
for row in rows:assert len(f(row,'r:TablixCells'))==5
E.indent(tree,space='  ');tree.write(out,encoding='utf-8',xml_declaration=True);E.parse(out)
print(out);print('PASS: all original expressions retained; datasets, parameters, code and QR unchanged; unique item names; five item columns; XML parsed.')
