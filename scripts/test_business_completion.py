# شرح الملف: يراجع أن توزيع الحالات على الشاشات لا يتحول إلى ادعاء اكتمال.
import tempfile,unittest
from pathlib import Path
from build_business_completion import build
class CompletionMatrixTest(unittest.TestCase):
 def row(self,**changes):
  value=dict(route='#/invoice/new',activity='Public',screen='Sales',existing_coverage='',existing_test_classes='Invoice.Existing',activity_business_validation='NOT_RUN')
  value.update(changes);return value
 def test_assignment_does_not_become_live_acceptance(self):
  with tempfile.TemporaryDirectory() as d:
   root=Path(d);file=root/'src/test/java/Invoice/Existing.java';file.parent.mkdir(parents=True);file.touch()
   row=build([self.row()],root)[0]
   self.assertEqual(row['assignment'],'ASSIGNED');self.assertEqual(row['acceptance'],'NOT_COMPLETE');self.assertEqual(row['native_business_evidence'],'NOT_RUN')
 def test_missing_class_is_visible(self):
  with tempfile.TemporaryDirectory() as d:self.assertEqual(build([self.row()],Path(d))[0]['assignment'],'MISSING_CONTRACT')
 def test_special_screen_is_not_assigned_to_other_activity(self):
  with tempfile.TemporaryDirectory() as d:
   row=build([self.row(route='#/contracts/new')],Path(d))[0]
   self.assertEqual(row['assignment'],'SPECIALIZED_FOR_OTHER_ACTIVITY');self.assertEqual(row['classes'],'')
 def test_original_scope_exclusion_is_preserved(self):
  with tempfile.TemporaryDirectory() as d:
   row=build([self.row(existing_coverage='خارج النطاق')],Path(d))[0]
   self.assertEqual(row['assignment'],'EXCLUDED_BY_EXISTING_SCOPE');self.assertEqual(row['classes'],'')
if __name__=='__main__':unittest.main()
