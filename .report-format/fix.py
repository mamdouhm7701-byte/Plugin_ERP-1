from docx import Document
from docx.oxml.ns import qn
p=r'C:\Users\pcx\Desktop\تقرير إجمالي المبيعات - منسق.docx'
d=Document(p)
for el in [d.element,*[s.element for s in d.styles]]:
 for b in list(el.iter(qn('w:pBdr'))): b.getparent().remove(b)
d.save(p)
