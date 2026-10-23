from pathlib import Path
from copy import deepcopy
from collections import Counter
import xml.etree.ElementTree as E
base=Path(r'C:/Users/pcx/Desktop/SalesReport-Upgrades')
src=base/'SalesTotals-02-Executive-Navy.rdl'
for _,p in E.iterparse(src,events=['start-ns']):E.register_namespace(*p)
tree=E.parse(src);r=tree.getroot();ns=r.tag.split('}')[0][1:];N={'r':ns};q=lambda t:'{'+ns+'}'+t
template=E.parse(r'C:/Users/pcx/Desktop/Plugin_ERP/chart-template.xml').getroot()
for e in template.iter():e.tag=q(e.tag.split('}')[-1])
items=r.find('.//r:Body/r:ReportItems',N)
for old in list(items):
 if old.tag!=q('Chart'):continue
 c=deepcopy(template);name=old.get('Name');c.set('Name',name)
 for tag in ['ChartCategoryHierarchy','ChartSeriesHierarchy','DataSetName','Top','Left','Height','Width']:
  target=c.find('r:'+tag,N)
  if target is not None:c.remove(target)
  source=old.find('r:'+tag,N)
  if source is not None:c.append(deepcopy(source))
 for g in c.findall('.//r:ChartTitle',N):g.set('Name',name+'Title');g.find('r:Caption',N).text=''
 series=c.find('.//r:ChartSeries',N);series.set('Name',name+'Net')
 series.find('.//r:Y',N).text=old.find('.//r:Y',N).text
 series.find('r:ChartDataPoints/r:ChartDataPoint/r:Style',N).append(E.Element(q('Color')))
 series.find('r:ChartDataPoints/r:ChartDataPoint/r:Style/r:Color',N).text='#253B60' if name=='TypeMixChart' else '#3B8A8E'
 label=series.find('.//r:ChartDataLabel',N);E.SubElement(label,q('Visible')).text='true';E.SubElement(label.find('r:Style',N),q('Format')).text='#,0.##'
 for tag in ['ChartBorderSkin','ChartCustomPaletteColors']:
  e=c.find('r:'+tag,N)
  if e is not None:c.remove(e)
 for s in c.findall('.//r:Style',N):
  for e in list(s):
   if e.tag in {q('BackgroundGradientType'),q('BackgroundGradientEndColor'),q('ShadowOffset'),q('ShadowColor')}:s.remove(e)
 for font in c.findall('.//r:FontFamily',N):font.text='Dubai'
 for bg in c.findall('.//r:BackgroundColor',N):bg.text='White'
 msg=c.find('r:ChartNoDataMessage',N);msg.set('Name',name+'NoData');msg.find('r:Caption',N).text='لا توجد بيانات'
 items.remove(old);items.append(c)
out=base/'SalesTotals-02-Executive-Navy-Fixed.rdl';E.indent(tree,space='  ');tree.write(out,encoding='utf-8',xml_declaration=True)
orig=E.parse(r'C:/Users/pcx/Downloads/New folder/Reports/Reports/SalesTotalsNetBased-ar.rdl').getroot()
ex=lambda root:Counter(e.text for e in root.iter() if e.text and e.text.lstrip().startswith('='))
assert not (ex(orig)-ex(r))
for tag in ['DataSources','DataSets','ReportParameters','Code']:
 a=orig.find('r:'+tag,N);b=r.find('r:'+tag,N)
 # Ignore serialization whitespace only.
 def norm(e):
  if e is None:return None
  return (e.tag,tuple(sorted(e.attrib.items())),(e.text or '').strip(),tuple(norm(x) for x in e))
 assert norm(a)==norm(b),tag
print(out);print('PASS original expressions and data definitions preserved; charts rebuilt from complete Microsoft RDL example')
