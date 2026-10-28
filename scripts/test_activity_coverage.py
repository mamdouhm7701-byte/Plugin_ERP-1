# شرح الملف: يراجع أن دليل التغطية لا يعلن نجاحًا دون دليل.
import unittest
from build_activity_coverage import build, ACTIVITIES


class ActivityCoverageTest(unittest.TestCase):
    def test_navigation_never_claims_business_coverage_or_other_activity_support(self):
        source = [{"screen": "invoice", "route": "#/invoice", "classes": "ExistingTest", "coverage": "partial"}]
        inventories = [{"branch": {"activityType": "Restaurant"}, "evidence": {"navigationLinks": [
            {"route": "#/invoice", "label": "invoice", "visible": True},
            {"route": "#/kitchen-orders", "label": "kitchen", "visible": False}]}}]
        rows = build(source, inventories)
        self.assertEqual(len(rows), 2 * len(ACTIVITIES))
        self.assertTrue(all(row["activity_business_validation"] == "NOT_RUN" for row in rows))
        self.assertTrue(all(row["activity_applicability"] == "UNVERIFIED" for row in rows))
        self.assertEqual(next(row for row in rows if row["activity"] == "Public" and row["route"] == "#/kitchen-orders")["navigation_evidence"], "NOT_OBSERVED")
        self.assertEqual(next(row for row in rows if row["activity"] == "Restaurant" and row["route"] == "#/kitchen-orders")["visible_in_snapshot"], "false")
        self.assertEqual(next(row for row in rows if row["activity"] == "Restaurant" and row["route"] == "#/invoice")["existing_test_classes"], "ExistingTest")

    def test_direct_access_does_not_claim_business_journey_validation(self):
        source = [{"screen": "tables", "route": "#/tables"}]
        evidence = [{"activity": "Restaurant", "route": "#/tables", "directAccess": "PASSED"}]
        rows = build(source, [], evidence)
        restaurant = next(row for row in rows if row["activity"] == "Restaurant")
        self.assertEqual(restaurant["direct_access"], "PASSED")
        self.assertEqual(restaurant["activity_business_validation"], "NOT_RUN")
        self.assertTrue(all(row["direct_access"] == "NOT_RUN" for row in rows if row["activity"] != "Restaurant"))

    def business_evidence(self):
        return {"activity": "Construction", "route": "#/contracts/new", "status": "PASSED",
                "cleanupVerified": True, "origin": "https://demo.example", "branchId": "owned-branch",
                "profileFingerprint": "a" * 64, "caseClass": "Invoice.ConstructionContractTest",
                "journeys": ["create/read; stock and customer balance unchanged"], "evidenceLog": "target/contract-live.log"}

    def test_cleaned_live_journey_is_partial_and_does_not_transfer_to_other_activities(self):
        source = [{"screen": "contracts", "route": "#/contracts/new"}]
        rows = build(source, [], business_evidence=[self.business_evidence()])
        construction = next(row for row in rows if row["activity"] == "Construction")
        self.assertEqual(construction["activity_business_validation"], "PARTIAL")
        self.assertIn("stock and customer balance unchanged", construction["business_journeys"])
        self.assertIn("Invoice.ConstructionContractTest", construction["existing_test_classes"])
        self.assertTrue(all(row["activity_business_validation"] == "NOT_RUN" for row in rows if row["activity"] != "Construction"))
        self.assertEqual(construction["direct_access"], "NOT_RUN")

    def test_failed_unclean_or_incomplete_business_evidence_is_rejected(self):
        source = [{"screen": "contracts", "route": "#/contracts/new"}]
        for key, value in [("status", "FAILED"), ("cleanupVerified", False), ("journeys", []),
                           ("profileFingerprint", "missing"), ("activity", "Unknown")]:
            evidence = dict(self.business_evidence(), **{key: value})
            with self.subTest(key=key), self.assertRaises(ValueError):
                build(source, [], business_evidence=[evidence])

    def test_business_evidence_for_an_uninventoried_route_is_rejected(self):
        with self.assertRaises(ValueError):
            build([], [], business_evidence=[self.business_evidence()])

    def test_unknown_activity_is_rejected(self):
        with self.assertRaises(ValueError):
            build([], [{"branch": {"activityType": "Unknown"}}])


if __name__ == "__main__":
    unittest.main()
