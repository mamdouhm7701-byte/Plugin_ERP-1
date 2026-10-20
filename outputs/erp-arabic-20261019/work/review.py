from pathlib import Path
import re,json,hashlib,collections
root=Path(__file__).resolve().parents[3]
out=Path(__file__).parent
classes={}; methods=[]; sources=[]
for p in sorted((root/'src').rglob('*.java')):
 s=p.read_text(encoding='utf-8-sig'); rel=p.relative_to(root).as_posix()
 sources.append(dict(path=rel,lines=len(s.splitlines()),sha256=hashlib.sha256(p.read_bytes()).hexdigest()))
 c=re.search(r'(?:public\s+)?(abstract\s+)?(?:final\s+)?class\s+(\w+)(?:\s+extends\s+(\w+))?',s)
 if not c: continue
 name=c[2]; classes[name]=dict(name=name,parent=c[3],abstract=bool(c[1]),path=rel,methods=[])
 if '/test/' not in rel: continue
 pattern=r'(@Test\b(?:(?!@Test\b|\bpublic\s+(?:final\s+)?void\b)[\s\S])*?)\bpublic\s+(?:final\s+)?void\s+(\w+)\s*\([^)]*\)\s*(?:throws[^\{]+)?\{'
 for m in re.finditer(pattern,s):
  boundary=max(s.rfind('}',0,m.start()),s.rfind('{',0,m.start()),s.rfind(';',0,m.start()))
  ann=s[boundary+1:m.end()]
  if not re.search(r'@Test\b',ann):continue
  pos=m.end(); depth=1; quote=None; esc=False
  while pos<len(s) and depth:
   ch=s[pos]
   if quote:
    if esc:esc=False
    elif ch=='\\':esc=True
    elif ch==quote:quote=None
   elif ch in '\"\'':quote=ch
   elif ch=='{':depth+=1
   elif ch=='}':depth-=1
   pos+=1
  story=re.search(r'@Story\("([^"]*)"\)',ann)
  desc=re.search(r'@Description\("([^"]*)"\)',ann)
  item=dict(cls=name,method=m[2],story=story[1] if story else '',description=desc[1] if desc else '',enabled=not bool(re.search(r'enabled\s*=\s*false',ann)),path=rel,line=s[:m.start()].count('\n')+1,body=s[m.end():pos-1],annotations=ann)
  classes[name]['methods'].append(item);methods.append(item)
suites=[]
for p in sorted((root/'src').rglob('*.xml')):
 s=p.read_text(encoding='utf-8-sig')
 if '<suite' in s:suites.append(dict(path=p.relative_to(root).as_posix(),classes=re.findall(r'<class\s+name="([^"]+)"',s),parallel=re.findall(r'parallel="([^"]+)"',s)))
data=dict(sources=sources,classes=classes,methods=methods,suites=suites)
(out/'review.json').write_text(json.dumps(data,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(dict(java_files=len(sources),declared_tests=len(methods),disabled=sum(not x['enabled'] for x in methods),suites=len(suites),by_folder=dict(collections.Counter(x['path'].split('/')[3] for x in methods))),ensure_ascii=False))
