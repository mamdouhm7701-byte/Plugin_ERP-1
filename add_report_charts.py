from pathlib import Path
import xml.etree.ElementTree as E
from copy import deepcopy

src=Path(r'C:/Users/pcx/Desktop/SalesTotalsNetBased-ar-OptionB.rdl')
out=src.with_name(src.stem+'-Charts.rdl')
for _, pair in E.iterparse(src,events=['start-ns']):
    E.register_namespace(*pair)
tree=E.parse(src); root=tree.getroot(); ns=root.tag.split('}')[0][1:]; q=lambda t:'{'+ns+'}'+t
N={'r':ns}
def el(p,t,text=None,**attrs):
    a=E.SubElement(p,q(t),attrs)
    if text is not None:a.text=str(text)
    return a
def find(p,path):return p.find(path,N)
net='Sum(IIF(Fields!TransactionType.Value = 2, Fields!Net.Value * -1, Fields!Net.Value))'
total='Sum(IIF(Fields!TransactionType.Value = 2, Fields!Net.Value * -1, Fields!Net.Value), "MainQuery")'
ratio=f'={net} / IIF({total} = 0, 1, {total})'
def style(p):
    s=el(p,'Style'); el(s,'FontFamily','IBM Plex Sans Arabic'); el(s,'FontSize','8pt'); return s
def chart(name,group=None,title=None,inline=False):
    c=E.Element(q('Chart'),Name=name)
    m=el(el(el(c,'ChartCategoryHierarchy'),'ChartMembers'),'ChartMember')
    if group is not None:
        g=deepcopy(group);g.set('Name',name+'_Category');m.append(g)
        se=el(el(m,'SortExpressions'),'SortExpression');el(se,'Value','='+net);el(se,'Direction','Descending')
        el(m,'Label',find(g,'r:GroupExpressions/r:GroupExpression').text)
    else:el(m,'Label','')
    el(el(el(el(c,'ChartSeriesHierarchy'),'ChartMembers'),'ChartMember'),'Label','النسبة')
    s=el(el(el(c,'ChartData'),'ChartSeriesCollection'),'ChartSeries',Name=name+'_Share')
    dp=el(el(s,'ChartDataPoints'),'ChartDataPoint');el(el(dp,'ChartDataPointValues'),'Y',ratio)
    dl=el(dp,'ChartDataLabel');style(dl)
    if not inline:
        el(dl,'Label',ratio);el(dl,'Visible','true');el(dl,'Position','Auto');el(find(dl,'r:Style'),'Format','0.0%')
    el(dp,'ToolTip','=Format('+ratio[1:]+', "0.0%")')
    st=el(dp,'Style');el(st,'Color',f'=IIF({net} < 0, "#c45b50", "#55836b")')
    el(s,'Type','Bar');el(s,'Subtype','Plain');el(s,'ChartAreaName','Default');el(s,'ValueAxisName','Primary');el(s,'CategoryAxisName','Primary')
    a=el(el(c,'ChartAreas'),'ChartArea',Name='Default')
    for typ in ['ChartCategoryAxes','ChartValueAxes']:
        ax=el(el(a,typ),'ChartAxis',Name='Primary');style(ax)
        if inline:el(ax,'Visible','False')
        if typ=='ChartCategoryAxes':el(ax,'Reverse','true');el(ax,'Interval','1')
        else:
            el(find(ax,'r:Style'),'Format','0%')
            if inline:el(ax,'Minimum','=-1');el(ax,'Maximum','1')
        grid=el(ax,'ChartMajorGridLines');el(grid,'Enabled','False' if inline or typ=='ChartCategoryAxes' else 'True')
    if inline:
        pos=el(a,'ChartElementPosition')
        for k,v in [('Top',0),('Left',0),('Height',100),('Width',100)]:el(pos,k,v)
        pos=el(a,'ChartInnerPlotPosition')
        for k,v in [('Top',5),('Left',2),('Height',90),('Width',96)]:el(pos,k,v)
    if title:
        t=el(el(c,'ChartTitles'),'ChartTitle',Name=name+'_Title');el(t,'Caption',title);style(t)
    if not inline:el(c,'DataSetName','MainQuery')
    el(c,'NoRowsMessage','لا توجد بيانات')
    el(c,'Top','0cm' if inline else '10.58124cm');el(c,'Left','0cm')
    el(c,'Height','0.62cm' if inline else '6cm');el(c,'Width','1.2cm' if inline else '12cm')
    st=el(c,'Style');el(el(st,'Border'),'Style','None');el(st,'BackgroundColor','White')
    return c

body=root.find('.//r:Body',N);items=find(body,'r:ReportItems')
for table_name,prefix,title in [('TblTypeMix','Mix','توزيع الحركات حسب النسبة'),('TblTopGroups','Top','أعلى التجميعات حسب النسبة')]:
    t=items.find(f'r:Tablix[@Name="{table_name}"]',N)
    cols=find(t,'r:TablixBody/r:TablixColumns');col=E.Element(q('TablixColumn'));el(col,'Width','1.2cm');cols.insert(0,col)
    members=find(t,'r:TablixColumnHierarchy/r:TablixMembers');members.insert(0,E.Element(q('TablixMember')))
    for i,row in enumerate(find(t,'r:TablixBody/r:TablixRows')):
        cells=find(row,'r:TablixCells');cell=E.Element(q('TablixCell'));cc=el(cell,'CellContents')
        if i==1:cc.append(chart(prefix+'ShareBar',inline=True))
        else:
            tb=deepcopy(find(cells[0],'r:CellContents/r:Textbox'));tb.set('Name',prefix+'Bar'+str(i))
            for v in tb.findall('.//r:Value',N):v.text='الرسم' if i==0 else ''
            for d in list(tb):
                if d.tag.endswith('}DefaultName'):tb.remove(d)
            cc.append(tb)
        cells.insert(0,cell)
    oldleft=float(find(t,'r:Left').text[:-2]);width=float(find(t,'r:Width').text[:-2])
    find(t,'r:Left').text=f'{oldleft-1.2:.5f}cm';find(t,'r:Width').text=f'{width+1.2:.5f}cm'
    group=find(t,'r:TablixRowHierarchy/r:TablixMembers/r:TablixMember/r:Group')
    c=chart(prefix+'DistributionChart',group,title)
    find(c,'r:Left').text=find(t,'r:Left').text;find(c,'r:Width').text=find(t,'r:Width').text
    items.append(c)
for name in ['TransactionsTitle','Tablix4']:
    node=next(a for a in items if a.get('Name')==name)
    top=find(node,'r:Top');top.text=f'{float(top.text[:-2])+6.8:.5f}cm'
find(body,'r:Height').text='21.58156cm'
E.indent(tree,space='  ');tree.write(out,encoding='utf-8',xml_declaration=True)

# Verify output structure and preserve the original data definitions.
r=E.parse(out).getroot()
assert E.tostring(root.find('r:DataSets',N))==E.tostring(r.find('r:DataSets',N))
names=[a.get('Name') for a in r.iter() if a.tag in {q('Textbox'),q('Chart'),q('Tablix'),q('Rectangle')}]
assert len(names)==len(set(names)), 'Duplicate report item names'
assert len(r.findall('.//r:Chart',N))==4
for t in r.findall('.//r:Tablix',N):
    if t.get('Name') not in ['TblTypeMix','TblTopGroups']: continue
    cols=t.findall('r:TablixBody/r:TablixColumns/r:TablixColumn',N)
    for row in t.findall('r:TablixBody/r:TablixRows/r:TablixRow',N):
        cells=row.findall('r:TablixCells/r:TablixCell',N)
        spans=[int(c.findtext('r:CellContents/r:ColSpan','1',N)) for c in cells]
        assert sum(spans)==len(cols),(t.get('Name'),spans,len(cols))
print(str(out))
print('Verified: 4 charts; unique report item names; table column alignment; unchanged datasets.')
