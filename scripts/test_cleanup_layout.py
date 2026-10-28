# شرح الملف: يضمن أن تقسيم اختبارات تنظيف البيع والشراء لا يترك ملفات ضخمة أو يسقط مجموعة من حزمها.
from pathlib import Path
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


if __name__ == "__main__":
    unittest.main()
