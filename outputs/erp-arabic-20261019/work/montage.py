from pypdf import PdfReader
from PIL import Image
from pathlib import Path
b=Path(__file__).parent;r=PdfReader(b/'Guide_QA.pdf')
print('Pages',len(r.pages));print('Text by page',[(i+1,len(p.extract_text() or '')) for i,p in enumerate(r.pages)])
files=sorted((b/'doc-preview').glob('final-*.png'))
for k in range(0,len(files),3):
 canvas=Image.new('RGB',(2100,1050),'#cccccc')
 for i,f in enumerate(files[k:k+3]):
  im=Image.open(f);im.thumbnail((700,1000));canvas.paste(im,(i*700,20))
 canvas.save(b/f'doc-contact-{k//3}.png')
files=list((b/'sheet-previews').glob('*.png'))
for k in range(0,len(files),3):
 canvas=Image.new('RGB',(1650,1400),'#cccccc')
 for i,f in enumerate(files[k:k+3]):
  im=Image.open(f);im.thumbnail((1600,440));canvas.paste(im,(20,i*460+15))
 canvas.save(b/f'sheet-contact-{k//3}.png')
