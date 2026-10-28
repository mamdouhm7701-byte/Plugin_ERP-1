# شرح الملف: يراجع بقاء أسماء حالات الشاشات ووصفها واختيارها بعد فصل خطوات التنفيذ الطويلة.
import json
from pathlib import Path
import unittest
ROOT = Path(__file__).resolve().parents[1]
class CaseDelegationTest(unittest.TestCase):
    def test_delegated_cases_keep_their_original_entry_points(self):
        path = ROOT / "docs/testing/evidence/case-workflow-delegation.json"
        self.assertTrue(path.exists(), "Missing preserved screen-case entry points")
        for entry in json.loads(path.read_text(encoding="utf8")):
            source = (ROOT / entry["source"]).read_text(encoding="utf8")
            self.assertLessEqual(len(source.splitlines()), 400, entry["source"])
            for method in entry["methods"]:
                self.assertIn("public void " + method["name"] + "(", source)
                helper = (ROOT / entry["directory"] / (method["workflow"] + ".java")).read_text(encoding="utf8")
                self.assertLessEqual(len(helper.splitlines()), 400)
                self.assertNotIn("@Test", helper)
                self.assertIn("void " + method["name"] + "(", helper)

if __name__ == "__main__":
    unittest.main()
