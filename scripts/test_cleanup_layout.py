# شرح الملف: يضمن أن تقسيم اختبارات تنظيف البيع والشراء لا يترك ملفات ضخمة أو يسقط مجموعة من حزمها.
from pathlib import Path
import json
import re
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]


class CleanupLayoutTest(unittest.TestCase):
    def test_cleanup_groups_are_small_and_present_in_every_original_suite(self):
        for package, prefix, suites in [
                ("Invoice", "Sales", ["ci-testng.xml", "sales-testng.xml", "Test-Data/SalesTest.xml", "additional/standalone/fixture-lifecycle-testng.xml"]),
                ("Purches", "Purchase", ["ci-testng.xml", "purchases-testng.xml", "Test-Data/Purches.xml"])]:
            groups = list((ROOT / "src/test/java" / package).glob(prefix + "*Cleanup*Test.java"))
            self.assertGreaterEqual(len(groups), 4)
            for path in groups:
                source = path.read_text(encoding="utf8")
                self.assertLessEqual(len(source.splitlines()), 400, path.name)
                self.assertIn("@Test", source)
                name = package + "." + path.stem
                for suite in suites:
                    classes = {node.get("name") for node in ET.parse(ROOT / "src/test/resources" / suite).iter("class")}
                    self.assertIn(name, classes, suite)

    def test_other_cleanup_groups_preserve_every_case_and_original_suite(self):
        manifest_path = ROOT / "docs/testing/evidence/remaining-cleanup-layout.json"
        self.assertTrue(manifest_path.exists(), "Missing preserved cleanup selection manifest")
        for entry in json.loads(manifest_path.read_text(encoding="utf8")):
            found = {}
            for class_name in entry["groups"]:
                path = ROOT / "src/test/java" / (class_name.replace(".", "/") + ".java")
                source = path.read_text(encoding="utf8")
                self.assertLessEqual(len(source.splitlines()), 400, str(path))
                for name in re.findall(r"^    public void (\w+)\(", source, re.M):
                    self.assertNotIn(name, found, "Duplicate cleanup case")
                    found[name] = class_name
                for suite in entry["suites"]:
                    classes = [node.get("name") for node in ET.parse(ROOT / "src/test/resources" / suite).iter("class")]
                    self.assertEqual(classes.count(class_name), 1, suite + ": " + class_name)
            self.assertEqual(set(found), set(entry["methods"]), entry["original"])

    def test_launcher_contract_groups_fit_review_budget_and_remain_in_ci(self):
        suite = ET.parse(ROOT / "src/test/resources/ci-testng.xml")
        classes = [node.get("name") for node in suite.iter("class")]
        for name in ["BusinessRunListenerTest", "BusinessActivityPlanTest", "SuiteDefinitionsTest", "AdditionalTaxSuiteDefinitionsTest"]:
            path = ROOT / "src/test/java/SuiteSupport" / (name + ".java")
            self.assertTrue(path.exists(), name)
            self.assertLessEqual(len(path.read_text(encoding="utf8").splitlines()), 400, name)
            self.assertEqual(classes.count("SuiteSupport." + name), 1, name)


if __name__ == "__main__":
    unittest.main()
