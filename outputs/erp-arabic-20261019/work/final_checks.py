from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
from lxml import etree as ET
import json,re,hashlib
base=Path(__file__).parent;root=base.parents[2];out=base.parent
d=json.loads((base/'content.json').read_text(encoding='utf-8'))
ids=[c['id'] for c in d['cases']];assert len(ids)==len(set(ids))==408
missing=[]
for c in d['cases']:
 if c['source'].startswith(('src/','docs/')) and not(root/c['source']).is_file():missing.append((c['id'],c['source']))
 assert c['steps'].startswith('1. ') and len(c['steps'].splitlines())>=2
 assert c['status']=='لم تنفذ' and c['actual']==''
assert not missing,missing
changed=[]
for c in d['sources']:
 if hashlib.sha256((root/c['path']).read_bytes()).hexdigest()!=c['sha256']:changed.append(c['path'])
# XML تعديل خاصية العرض العربي غير المكشوفة في واجهة أداة الجداول
path=out/'Plugin_ERP_Manual_Test_Cases_AR.xlsx';ns={'x':'http://schemas.openxmlformats.org/spreadsheetml/2006/main'}
with ZipFile(path) as z:parts={n:z.read(n) for n in z.namelist()}
for name,content in list(parts.items()):
 if re.fullmatch(r'xl/worksheets/sheet\d+.xml',name):
  t=ET.fromstring(content)
  for v in t.findall('x:sheetViews/x:sheetView',ns):v.set('rightToLeft','1');v.set('showGridLines','0');v.set('zoomScale','85')
  parts[name]=ET.tostring(t,encoding='UTF-8',xml_declaration=True)
 if name=='xl/styles.xml':
  t=ET.fromstring(content)
  for a in t.findall('.//x:alignment',ns):a.set('readingOrder','2')
  parts[name]=ET.tostring(t,encoding='UTF-8',xml_declaration=True)
tmp=path.with_suffix('.tmp.xlsx')
with ZipFile(tmp,'w',ZIP_DEFLATED) as z:
 for n,v in parts.items():z.writestr(n,v)
tmp.replace(path)
with ZipFile(path) as z:
 sheets=[n for n in z.namelist() if re.fullmatch(r'xl/worksheets/sheet\d+.xml',n)]
 assert len(sheets)==13
 for n in sheets:
  t=ET.fromstring(z.read(n));assert t.find('x:sheetViews/x:sheetView',ns).get('rightToLeft')=='1'
 assert sum(len(ET.fromstring(z.read(n)).findall('x:dataValidations/x:dataValidation',ns)) for n in sheets)>=10
 assert len([n for n in z.namelist() if n.startswith('xl/tables/table')])==11
report={'manual_cases':len(ids),'source_refs_valid':not missing,'source_files_changed_since_review':changed,'sheets':len(sheets),'initial_status':'لم تنفذ','RTL':True,'no_live_ERP_tests_run':True}
(base/'final_checks.json').write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(report,ensure_ascii=False))
