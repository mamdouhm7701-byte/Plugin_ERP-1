# شرح الملف: يجرب مقارنة الجولات بنتائج محفوظة مصطنعة، بما فيها التخطي والفشل المتكرر وتغير الإعدادات.
# فائدته: يمنع تقرير الثبات من تحويل رن ناقص أو إعادة محاولة ناجحة إلى نجاح ثابت.
import json
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET

from analyze_stability import analyze, write_report


class StabilityAnalysisTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.rounds = []

    def tearDown(self):
        self.temp.cleanup()

    def round(self, number, status="PASS", policy="NetBased", name="sales", exit_code=0, retry=False):
        folder = self.root / f"round-{number}"
        folder.mkdir()
        (folder / "effective-settings.json").write_text(json.dumps({"tax": policy, "workers": 1}), encoding="utf8")
        root = ET.Element("testng-results")
        owner = ET.SubElement(root, "class", name="Invoice")
        method = ET.SubElement(owner, "test-method", name=name, status=status)
        if status != "PASS":
            ET.SubElement(ET.SubElement(method, "exception"), "message").text = "VAT expected115 actual100"
        if retry:
            ET.SubElement(owner, "test-method", name=name, status="PASS")
        ET.ElementTree(root).write(folder / "testng-results.xml", encoding="utf8")
        self.rounds.append({"folder": folder.name, "exitCode": exit_code})
        (self.root / "rounds.json").write_text(json.dumps({"requested": 3, "rounds": self.rounds}), encoding="utf8")

    def verdict(self):
        return analyze(self.root)["verdict"]

    def test_three_complete_passes_prove_only_the_selected_scope(self):
        for i in range(3): self.round(i)
        self.assertEqual(self.verdict(), "STABLE_PASS")

    def test_mixed_result_is_unstable(self):
        for i in range(3): self.round(i, status="FAIL" if i == 1 else "PASS", exit_code=1 if i == 1 else 0)
        self.assertEqual(self.verdict(), "UNSTABLE")

    def test_repeated_failure_is_not_green(self):
        for i in range(3): self.round(i, status="FAIL", exit_code=1)
        self.assertEqual(self.verdict(), "REPEATED_FAILURE")

    def test_identical_skips_are_not_green(self):
        for i in range(3): self.round(i, status="SKIP", exit_code=1)
        self.assertNotEqual(self.verdict(), "STABLE_PASS")

    def test_changed_policy_or_case_selection_is_not_comparable(self):
        for i in range(3): self.round(i, policy="PriceIncluded" if i == 1 else "NetBased")
        self.assertEqual(self.verdict(), "NOT_COMPARABLE")
        folder = self.root / "round-1"
        (folder / "effective-settings.json").write_text(json.dumps({"tax": "NetBased", "workers": 1}), encoding="utf8")
        path = folder / "testng-results.xml"
        tree = ET.parse(path)
        tree.find("./class/test-method").set("name", "anotherCase")
        tree.write(path, encoding="utf8")
        self.assertEqual(self.verdict(), "NOT_COMPARABLE")

    def test_missing_round_and_uncertain_cleanup_are_blocked(self):
        self.round(0)
        self.assertEqual(self.verdict(), "BLOCKED")
        self.round(1)
        self.round(2, exit_code=2)
        self.assertEqual(self.verdict(), "BLOCKED")

    def test_successful_retry_does_not_erase_the_failure(self):
        for i in range(3): self.round(i, status="FAIL", retry=True)
        self.assertNotEqual(self.verdict(), "STABLE_PASS")

    def test_report_escapes_failure_content(self):
        for i in range(3): self.round(i, status="FAIL", name="<script>")
        write_report(self.root, analyze(self.root))
        text = (self.root / "stability-summary.html").read_text(encoding="utf8")
        self.assertNotIn("<script>", text)
        self.assertIn("&lt;script&gt;", text)

    def test_truncated_result_count_cannot_prove_stability(self):
        for i in range(3):
            self.round(i)
            path = self.root / f"round-{i}" / "testng-results.xml"
            tree = ET.parse(path)
            tree.getroot().set("total", "10")
            tree.write(path, encoding="utf8")
        self.assertEqual(self.verdict(), "BLOCKED")

    def test_ignored_methods_are_not_a_green_round(self):
        for i in range(3):
            self.round(i)
            path = self.root / f"round-{i}" / "testng-results.xml"
            tree = ET.parse(path)
            tree.getroot().set("ignored", "1")
            tree.write(path, encoding="utf8")
        self.assertNotEqual(self.verdict(), "STABLE_PASS")

    def test_failure_uses_matching_readable_classification_from_its_own_evidence(self):
        for i in range(3):
            self.round(i, status="FAIL", exit_code=1)
            folder = self.root / f"round-{i}"
            review = folder / "failure-review"
            review.mkdir()
            evidence = review / "case.json"
            evidence.write_text(json.dumps({"statusDetails": {"message": "VAT expected115 actual100"}}), encoding="utf8")
            (review / "summary.json").write_text(json.dumps({"cases": [{"case": "Invoice.sales", "evidence": evidence.as_uri(), "reason": "الضريبة الفعلية مختلفة عن المتوقعة", "outcome": "عيب تطبيق"}]}), encoding="utf8")
        report = analyze(self.root)
        attempt = next(iter(report["rounds"][0]["cases"].values()))["attempts"][0]
        self.assertEqual(attempt["outcome"], "عيب تطبيق")
        self.assertIn("الضريبة", attempt["reason"])
        self.assertEqual(report["verdict"], "REPEATED_FAILURE")


if __name__ == "__main__":
    unittest.main()
