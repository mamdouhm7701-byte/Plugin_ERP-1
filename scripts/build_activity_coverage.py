"""Join existing test coverage with observed activity navigation without inferring capabilities."""
import argparse
import csv
import json
import re
from urllib.parse import urlsplit
from pathlib import Path

ACTIVITIES = ("Public", "Restaurant", "Construction", "Concrete", "CarShowroom")


def normalize(route):
    return "#/" + route.removeprefix("#").strip("/")


def validated_business(evidence, screens):
    route = evidence.get("route")
    origin = urlsplit(str(evidence.get("origin", "")))
    journeys = evidence.get("journeys")
    required_text = ("branchId", "caseClass", "evidenceLog")
    if (evidence.get("activity") not in ACTIVITIES or evidence.get("status") != "PASSED"
            or evidence.get("cleanupVerified") is not True
            or evidence.get("coverage", "PARTIAL") != "PARTIAL"
            or not isinstance(route, str) or normalize(route) not in screens
            or origin.scheme not in ("http", "https") or not origin.hostname
            or origin.username is not None or origin.password is not None
            or origin.path not in ("", "/") or origin.query or origin.fragment
            or not re.fullmatch(r"[a-f0-9]{64}", str(evidence.get("profileFingerprint", "")))
            or any(not isinstance(evidence.get(key), str) or not evidence[key].strip() for key in required_text)
            or not isinstance(journeys, list) or not journeys
            or any(not isinstance(journey, str) or not journey.strip() for journey in journeys)):
        raise ValueError("Business evidence must identify a cleaned, passed, scoped live journey")
    return evidence["activity"], normalize(route)


def build(source, inventories, access_evidence=(), business_evidence=()):
    screens = {normalize(row["route"]): row for row in source}
    access = {}
    for evidence in access_evidence:
        if evidence["activity"] not in ACTIVITIES or evidence["directAccess"] != "PASSED":
            raise ValueError("Invalid direct-access evidence")
        access[(evidence["activity"], normalize(evidence["route"]))] = evidence
    observed = {}
    for inventory in inventories:
        activity = inventory["branch"]["activityType"]
        if activity not in ACTIVITIES:
            raise ValueError("Unknown inventory activity")
        for link in inventory["evidence"]["navigationLinks"]:
            route = normalize(link["route"])
            screens.setdefault(route, {"screen": link["label"], "route": route, "classes": "",
                                        "coverage": "inventory-only", "enabled_methods": "0"})
            observed.setdefault((activity, route), []).append(link)
    business = {}
    for evidence in business_evidence:
        key = validated_business(evidence, screens)
        business.setdefault(key, []).append(evidence)
    rows = []
    for activity in ACTIVITIES:
        for route, screen in sorted(screens.items()):
            links = observed.get((activity, route), [])
            journeys = business.get((activity, route), [])
            classes = [screen.get("classes", "")] + sorted({item["caseClass"] for item in journeys})
            rows.append({"activity": activity, "screen": screen["screen"], "route": route,
                         "navigation_evidence": "OBSERVED" if links else "NOT_OBSERVED",
                         "visible_in_snapshot": str(any(link["visible"] for link in links)).lower() if links else "UNKNOWN",
                         "activity_applicability": "UNVERIFIED",
                         "direct_access": "PASSED" if (activity, route) in access else "NOT_RUN",
                         "existing_test_classes": "; ".join(value for value in classes if value),
                         "existing_coverage": "Activity live tests - partial journey" if journeys else screen.get("coverage", ""),
                         "activity_business_validation": "PARTIAL" if journeys else "NOT_RUN",
                         "business_journeys": "; ".join(sorted({name for item in journeys for name in item["journeys"]}))})
    return rows


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=Path("docs/testing/SCREEN_COVERAGE_2026_10_27.csv"))
    parser.add_argument("--inventory", type=Path, action="append", default=[])
    parser.add_argument("--access-evidence", type=Path, action="append", default=[])
    parser.add_argument("--business-evidence", type=Path, action="append", default=[])
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    with args.source.open(encoding="utf-8-sig", newline="") as stream:
        source = list(csv.DictReader(stream))
    inventories = [json.loads(path.read_text(encoding="utf-8-sig")) for path in args.inventory]
    evidence = [json.loads(path.read_text(encoding="utf-8-sig")) for path in args.access_evidence]
    business = [json.loads(path.read_text(encoding="utf-8-sig")) for path in args.business_evidence]
    rows = build(source, inventories, evidence, business)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", encoding="utf-8-sig", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)
    print(f"Wrote {len(rows)} activity/screen entries; business validation remains explicit.")


if __name__ == "__main__":
    main()
