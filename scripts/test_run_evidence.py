# شرح الملف: يراجع ربط أدلة التغطية وقياس مدد التنفيذ دون اختلاق اكتمال شاشة أو خلط تجهيز بالتنفيذ.
import json
from pathlib import Path
import tempfile
import unittest

from build_run_evidence import build, interval_union


class RunEvidenceTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)

    def tearDown(self):
        self.temp.cleanup()

    def save(self, name, data):
        (self.root / name).write_text(json.dumps(data), encoding="utf8")

    def result(self, status="passed", scope="{activityType=Restaurant, taxPolicy=NetBased}"):
        return {"uuid": "case1", "fullName": "SpecialSalesTest.RestaurantInvoiceTest.save", "status": status,
                "start": 1000, "stop": 3000, "labels": [{"name": "review.scope", "value": scope}]}

    def test_overlapping_durations_are_not_added_as_wall_time(self):
        self.assertEqual(interval_union([(0, 10), (5, 15), (20, 25)]), 20)

    def test_failed_fixture_and_missing_scope_do_not_become_screen_acceptance(self):
        self.save("one-result.json", self.result(scope="legacy:test"))
        report = build(self.root, [{"activity": "Restaurant", "route": "#/restaurant-menu", "classes": "SpecialSalesTest.RestaurantInvoiceTest"}])
        self.assertEqual(report["coverage"][0]["acceptance"], "NOT_COMPLETE")
        self.assertEqual(report["coverage"][0]["matchingResults"], 0)
        self.assertEqual(len(report["unscopedResults"]), 1)

    def test_matching_pass_is_partial_evidence_not_complete_screen(self):
        self.save("one-result.json", self.result())
        report = build(self.root, [{"activity": "Restaurant", "route": "#/restaurant-menu", "classes": "SpecialSalesTest.RestaurantInvoiceTest"}])
        row = report["coverage"][0]
        self.assertEqual(row["passed"], 1)
        self.assertEqual(row["acceptance"], "NOT_COMPLETE")
        self.assertTrue(row["evidence"][0]["scope"].endswith("NetBased}"))

    def test_setup_and_cleanup_are_measured_separately_and_linked_by_child_id(self):
        self.save("one-result.json", self.result())
        self.save("fixture-container.json", {"children": ["case1"], "befores": [{"name": "setup", "start": 0, "stop": 900, "status": "passed"}],
                                               "afters": [{"name": "cleanup", "start": 3100, "stop": 3500, "status": "broken"}]})
        report = build(self.root, [])
        self.assertEqual(report["timing"]["testWallMs"], 2000)
        self.assertEqual(report["timing"]["setupWorkMs"], 900)
        self.assertEqual(report["timing"]["cleanupWorkMs"], 400)
        self.assertEqual(report["timing"]["recordedSpanMs"], 3500)
        self.assertEqual(report["timing"]["configurationFailures"], 1)

    def test_retry_and_missing_timestamp_do_not_disappear(self):
        value = self.result("failed"); self.save("first-result.json", value)
        value = self.result(); value["uuid"] = "retry"; value.pop("start"); self.save("retry-result.json", value)
        report = build(self.root, [])
        self.assertEqual(report["results"], 2)
        self.assertEqual(report["timing"]["invalidTimings"], 1)
        self.assertEqual(report["statuses"]["failed"], 1)

    def test_inherited_case_uses_the_actual_test_class_label(self):
        value = self.result()
        value["fullName"] = "CoverageTest.MasterCoverageScenario.create"
        value["labels"].append({"name": "testClass", "value": "CoverageTest.TablesCoverageTest"})
        self.save("one-result.json", value)
        report = build(self.root, [{"activity": "Restaurant", "route": "#/tables", "classes": "CoverageTest.TablesCoverageTest"}])
        self.assertEqual(report["coverage"][0]["matchingResults"], 1)


if __name__ == "__main__":
    unittest.main()
