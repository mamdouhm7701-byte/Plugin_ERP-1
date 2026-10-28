# شرح الملف: يربط نتائج تشغيل محفوظة بشاشات النشاط ويقيس أوقات الحالات والتجهيز والتنظيف.
# فائدته: يعرض الدليل والإعدادات وحدود القياس دون اعتبار نجاح مجموعة صغيرة اكتمالًا للشاشة.
"""Read-only evidence analysis. Output contains allowlisted metadata, never test parameters."""
import argparse
from collections import Counter, defaultdict
import csv
import html
import json
from pathlib import Path
import re


def interval_union(intervals):
    total, end = 0, None
    for start, stop in sorted(intervals):
        total += max(0, stop - max(start, end if end is not None else start))
        end = max(stop, end if end is not None else stop)
    return total


def interval(value):
    start, stop = value.get("start"), value.get("stop")
    if not isinstance(start, (int, float)) or not isinstance(stop, (int, float)) or stop < start:
        return None
    return start, stop


def scope_of(test):
    scopes = [l.get("value", "") for l in test.get("labels", []) if l.get("name") == "review.scope"]
    scope = scopes[0] if len(scopes) == 1 else ""
    match = re.search(r"(?:\{|,\s*)activityType=([A-Za-z]+)(?:,|\})", scope)
    return (match[1] if match else ""), scope


def owner_of(test):
    actual = {l.get("value") for l in test.get("labels", []) if l.get("name") == "testClass" and l.get("value")}
    if len(actual) == 1:
        return next(iter(actual))
    name = test.get("fullName", "")
    return name.rsplit(".", 1)[0] if "." in name else "unidentified"


def build(results, matrix):
    tests, fixtures, warnings = [], [], []
    for path in sorted(results.glob("*.json")):
        if not path.name.endswith(("-result.json", "-container.json")):
            continue
        try:
            value = json.loads(path.read_text(encoding="utf-8-sig"))
            if not isinstance(value, dict): raise ValueError("Result must be an object")
        except (ValueError, OSError) as failure:
            warnings.append(path.name + ": " + type(failure).__name__)
            continue
        if path.name.endswith("-result.json"):
            activity, scope = scope_of(value)
            tests.append({"id": value.get("uuid"), "case": value.get("fullName", value.get("name", "")),
                          "owner": owner_of(value), "status": value.get("status", "unknown"),
                          "activity": activity, "scope": scope, "interval": interval(value),
                          "evidence": path.resolve().as_uri()})
        else:
            fixtures.append(value)
    by_id = {t["id"]: t for t in tests if t["id"]}
    timings = defaultdict(lambda: {"invocations": 0, "testWorkMs": 0, "setupWorkMs": 0, "cleanupWorkMs": 0, "configurationFailures": 0})
    test_intervals, all_intervals = [], []
    invalid = 0
    for test in tests:
        group = timings[test["owner"]]
        group["invocations"] += 1
        if test["interval"]:
            start, stop = test["interval"]
            group["testWorkMs"] += stop - start
            test_intervals.append((start, stop)); all_intervals.append((start, stop))
        else:
            invalid += 1
    totals = {"setupWorkMs": 0, "cleanupWorkMs": 0, "configurationFailures": 0, "unattributedFixtureMs": 0}
    fixture_intervals = []
    for container in fixtures:
        owners = {by_id[c]["owner"] for c in container.get("children", []) if c in by_id}
        # A class-wide fixture is counted once. Shared multi-class fixtures remain unallocated.
        group = timings[next(iter(owners))] if len(owners) == 1 else None
        for stage, key in [("befores", "setupWorkMs"), ("afters", "cleanupWorkMs")]:
            for fixture in container.get(stage, []):
                if fixture.get("status") in ("failed", "broken"):
                    totals["configurationFailures"] += 1
                    if group is not None: group["configurationFailures"] += 1
                times = interval(fixture)
                if times is None:
                    invalid += 1
                    continue
                duration = times[1] - times[0]
                totals[key] += duration
                if group is not None: group[key] += duration
                else: totals["unattributedFixtureMs"] += duration
                all_intervals.append(times); fixture_intervals.append(times)
    coverage = []
    for screen in matrix:
        classes = {c.strip() for c in screen.get("classes", "").split(";") if c.strip()}
        matching = [t for t in tests if t["activity"] == screen["activity"] and t["owner"] in classes]
        counts = Counter(t["status"] for t in matching)
        coverage.append({"activity": screen["activity"], "route": screen["route"],
                         "screen": screen.get("screen", ""), "matchingResults": len(matching),
                         "passed": counts["passed"], "failed": counts["failed"], "broken": counts["broken"],
                         "skipped": counts["skipped"], "acceptance": "NOT_COMPLETE",
                         "evidenceState": "PARTIAL_RECORDED_RESULTS" if matching else "NO_MATCHING_SCOPED_RESULT",
                         "evidence": [{k: t[k] for k in ("case", "status", "scope", "evidence")} for t in matching]})
    classes = [{"class": owner, **value, "totalWorkMs": value["testWorkMs"] + value["setupWorkMs"] + value["cleanupWorkMs"]}
               for owner, value in timings.items()]
    classes.sort(key=lambda c: c["totalWorkMs"], reverse=True)
    return {"source": str(results.resolve()), "results": len(tests), "statuses": dict(Counter(t["status"] for t in tests)),
            "coverage": coverage, "unscopedResults": [t["case"] for t in tests if not t["activity"]],
            "warnings": warnings, "classesByWork": classes,
            "timing": {**totals, "invalidTimings": invalid, "testWallMs": interval_union(test_intervals),
                       "fixtureWallMs": interval_union(fixture_intervals),
                       "recordedSpanMs": max((b for a,b in all_intervals), default=0) - min((a for a,b in all_intervals), default=0)},
            "limits": ["Results are a snapshot; an active run can still change.",
                       "Class matches are partial evidence, never screen acceptance. Actual testClass labels resolve inherited owners when available.",
                       "Work durations can overlap across browsers; do not add them to derive wall time.",
                       "Recorded span excludes build and all preparation/cleanup outside Allure recording.",
                       "No worker recommendation without equal-scope repeated live measurements and sufficient capacity."]}


def write(output, report):
    output.mkdir(parents=True, exist_ok=True)
    (output / "run-evidence.json").write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf8")
    esc = html.escape
    content = ["<!doctype html><html lang='ar' dir='rtl'><meta charset='UTF-8'><title>التغطية والوقت بالدليل</title>",
               "<style>body{font-family:Tahoma;margin:24px;line-height:1.9}table{width:100%;border-collapse:collapse}td,th{border:1px solid #ccc;padding:8px;overflow-wrap:anywhere}details{margin:12px 0}</style>",
               "<h1>التغطية والوقت بالدليل</h1><p>لقطة نتائج محفوظة؛ لا تثبت اكتمال شاشة أو انتهاء الرن الحي.</p>",
               "<p>النتائج: " + str(report["results"]) + " | حالات بلا نشاط موثق: " + str(len(report["unscopedResults"])) + "</p>",
               "<h2>أبطأ الكلاسات حسب زمن العمل المسجل</h2><p>المدد بالثواني. قد تتداخل بين المتصفحات؛ لا تجمعها للحصول على وقت الرن.</p>",
               "<table><tr><th>الكلاس</th><th>تجهيز</th><th>تنفيذ</th><th>تنظيف</th><th>أخطاء التجهيز والتنظيف</th></tr>"]
    for row in report["classesByWork"]:
        content.append("<tr><td>" + esc(row["class"]) + "</td>" + "".join("<td>" + str(round(row[key]/1000, 2)) + "</td>" for key in ("setupWorkMs", "testWorkMs", "cleanupWorkMs")) + "<td>" + str(row["configurationFailures"]) + "</td></tr>")
    content.append("</table><h2>أدلة الشاشات حسب النشاط</h2><p>ظهور نجاح هنا دليل جزئي للحالات المعروضة وإعداداتها فقط؛ الشاشة لا تعتمد تلقائيًا.</p>")
    for row in report["coverage"]:
        content.append("<details><summary>" + esc(row["activity"] + " | " + row["screen"] + " | " + row["route"]) + " — نتائج مرتبطة: " + str(row["matchingResults"]) + "</summary>")
        for result in row["evidence"]:
            content.append("<p>" + esc(result["status"] + " | " + result["case"] + " | " + result["scope"]) + " <a href='" + esc(result["evidence"], quote=True) + "'>الدليل الأصلي</a></p>")
        content.append("</details>")
    content.append("<p>روابط الأدلة تشير للمصدر الأصلي؛ احتفظ بمجلد النتائج أو أرشفه قبل الرن التالي. تفاصيل الحدود والأوقات الكلية في run-evidence.json.</p></html>")
    (output / "run-evidence.html").write_text("\n".join(content), encoding="utf8")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("results", type=Path)
    parser.add_argument("output", type=Path)
    parser.add_argument("--matrix", type=Path, default=Path(__file__).resolve().parents[1] / "docs/testing/BUSINESS_COMPLETION_MATRIX.csv")
    args = parser.parse_args()
    with args.matrix.open(encoding="utf-8-sig", newline="") as stream:
        matrix = list(csv.DictReader(stream))
    result = build(args.results, matrix)
    write(args.output, result)
    print("Recorded results:", result["results"], "| classes:", len(result["classesByWork"]), "| unscoped:", len(result["unscopedResults"]))
