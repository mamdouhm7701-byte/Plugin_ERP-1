import {Workbook} from '@oai/artifact-tool';
let w=Workbook.create();w.worksheets.add('Test');console.log(w.help('worksheet',{search:'rightToLeft|view|direction',include:'index,notes,examples',maxChars:2200}).ndjson);
