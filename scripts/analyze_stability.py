# شرح الملف: يقارن نفس حالات الاختبار بين الجولات ويكشف اختلاف النتائج أو الإعدادات أو الاختيار.
# فائدته: لا يمنح شهادة ثبات لرن ناقص أو متخطى أو لفشل متكرر حتى لو تشابهت الجولات.
"""Read saved TestNG results only; never connect to ERP or change a test outcome."""
import argparse
import hashlib
import html
import json
from pathlib import Path
import re
import xml.etree.ElementTree as ET
from urllib.parse import urlparse
from urllib.request import url2pathname


def readable_failures(folder):
    lookup = {}
    for summary in folder.glob("**/failure-review/summary.json"):
        try:
            rows = json.loads(summary.read_text(encoding="utf-8-sig")).get("cases", [])
            for row in rows:
                url = urlparse(row.get("evidence", ""))
                if url.scheme != "file" or url.netloc not in ("", "localhost"):
                    continue
                evidence = Path(url2pathname(url.path)).resolve()
                if not evidence.is_relative_to(folder.resolve()):
                    continue
                saved = json.loads(evidence.read_text(encoding="utf-8-sig"))
                message = (saved.get("statusDetails") or {}).get("message", "").strip().splitlines()
                first = message[0][:500] if message else ""
                lookup[(row.get("case", ""), first)] = {
                    "reason": row.get("reason", first), "outcome": row.get("outcome", "يحتاج مراجعة")}
        except (OSError, ValueError, TypeError) as failure:
            # Supplementary explanations must not replace the original XML evidence.
            print("تعذر قراءة شرح إضافي؛ تبقى نتيجة XML هي الدليل: " + type(failure).__name__)
    return lookup


def read_round(folder):
    settings_file = folder / "effective-settings.json"
    settings = json.loads(settings_file.read_text(encoding="utf-8-sig")) if settings_file.exists() else None
    reports = list(folder.rglob("testng-results.xml"))
    if len(reports) != 1:
        return {"error": "لا توجد نتيجة واحدة مكتملة لهذه الجولة", "cases": {}, "settings": settings}
    root = ET.parse(reports[0]).getroot()
    readable = readable_failures(folder)
    recorded = [m for m in root.iter("test-method") if m.get("is-config", "false").lower() != "true"]
    if root.get("total") is not None and int(root.get("total")) != len(recorded):
        return {"error": "عدد النتائج لا يطابق إجمالي التقرير؛ الدليل غير مكتمل", "cases": {}, "settings": settings}
    cases, configurations = {}, []
    for owner in root.iter("class"):
        for method in owner.findall("test-method"):
            status = method.get("status", "UNKNOWN")
            if method.get("is-config", "false").lower() == "true":
                if status != "PASS":
                    configurations.append(owner.get("name", "") + "." + method.get("name", ""))
                continue
            name = owner.get("name", "") + "." + method.get("name", "")
            parameters = ["".join(p.itertext()).strip() for p in method.findall("./params/param")]
            key = name + "#" + hashlib.sha256(json.dumps(parameters, ensure_ascii=False).encode()).hexdigest()[:16]
            message = method.findtext("./exception/message", "").strip().splitlines()
            reason = message[0][:500] if message else ""
            # Execution UUIDs and generated fixture names must not masquerade as new causes.
            normalized = re.sub(r"QA-[A-Za-z0-9_-]+|[0-9a-f]{8}-[0-9a-f-]{27,}|/0x[0-9a-f]+", "<متغير>", reason)
            details = readable.get((name, reason), {})
            outcome = "ناجح" if status == "PASS" else "حالة لم تُنفذ" if status == "SKIP" else details.get("outcome", "يحتاج مراجعة")
            invocation = {"status": status, "reason": details.get("reason", reason), "signature": normalized, "outcome": outcome}
            if key in cases:
                # Preserve repeated invocations/retries instead of silently taking the last pass.
                cases[key]["attempts"].append(invocation)
            else:
                cases[key] = {"name": name, "attempts": [invocation]}
    if int(root.get("ignored", "0")) > 0:
        configurations.append("حالات مستبعدة لم تنفذ؛ لا يمكن اعتبار الجولة ناجحة بالكامل")
    return {"settings": settings, "cases": cases, "configurationFailures": sorted(configurations),
            "error": "" if cases else "لم تنفذ أي حالة اختبار"}


def analyze(output):
    manifest = json.loads((output / "rounds.json").read_text(encoding="utf-8-sig"))
    rounds = []
    for item in manifest["rounds"]:
        folder = (output / item["folder"]).resolve()
        if not folder.is_relative_to(output.resolve()):
            raise ValueError("Round folder must remain inside its stability run")
        try:
            result = read_round(folder)
        except (ET.ParseError, ValueError, OSError) as failure:
            result = {"error": "تعذر قراءة دليل الجولة: " + type(failure).__name__, "cases": {}, "settings": None}
        rounds.append({**item, **result})
    verdict, explanation = "BLOCKED", "الجولات أو أدلتها غير مكتملة؛ لا يمكن إثبات الثبات."
    if len(rounds) == manifest["requested"] and len(rounds) >= 3 and all(
            not r["error"] and r["settings"] is not None and r["exitCode"] < 2 for r in rounds):
        settings = [json.dumps(r["settings"], sort_keys=True) for r in rounds]
        selections = [set(r["cases"]) for r in rounds]
        if len(set(settings)) != 1 or any(s != selections[0] for s in selections):
            verdict, explanation = "NOT_COMPARABLE", "الإعدادات أو المتصفحات الفعلية أو الحالات المختارة اختلفت بين الجولات."
        else:
            signatures = [json.dumps({k: [(a["status"], a["signature"]) for a in v["attempts"]]
                                      for k, v in sorted(r["cases"].items())}, sort_keys=True) for r in rounds]
            green = all(r["exitCode"] == 0 and not r["configurationFailures"] and all(
                all(a["status"] == "PASS" for a in case["attempts"]) for case in r["cases"].values()) for r in rounds)
            if green:
                verdict, explanation = "STABLE_PASS", "نفس الحالات ونفس الإعدادات نجحت في كل الجولات دون تخطٍ أو خطأ تجهيز وتنظيف مسجل."
            elif len(set(signatures)) == 1 and all(r["configurationFailures"] == rounds[0]["configurationFailures"] for r in rounds):
                verdict, explanation = "REPEATED_FAILURE", "الفشل أو التخطي متكرر بنفس الصورة؛ هذا ليس نجاحًا ولا شهادة ثبات أخضر."
            else:
                verdict, explanation = "UNSTABLE", "نتيجة الحالة أو سبب توقفها اختلف بين جولات نفس الإعداد."
    return {"verdict": verdict, "explanation": explanation, "requested": manifest["requested"], "rounds": rounds}


def write_report(output, report):
    (output / "stability-summary.json").write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf8")
    esc = html.escape
    content = ["<!doctype html><html lang='ar' dir='rtl'><meta charset='UTF-8'><title>ثبات التشغيل</title>",
               "<style>body{font-family:Tahoma;margin:30px;line-height:1.9}td,th{border:1px solid #ccc;padding:8px;overflow-wrap:anywhere}table{border-collapse:collapse;width:100%}</style>",
               "<h1>مقارنة جولات التشغيل</h1><p>" + esc(report["verdict"]) + " — " + esc(report["explanation"]) + "</p>",
               "<p>النتيجة تخص المجموعة والإعدادات المسجلة فقط. نجاح الفحوص المحلية لا يثبت ثبات ERP الحي.</p>",
               "<table><tr><th>الحالة</th><th>نتائج الجولات وأسباب التوقف</th></tr>"]
    keys = sorted(set().union(*(r["cases"] for r in report["rounds"])))
    for key in keys:
        case = next(r["cases"][key] for r in report["rounds"] if key in r["cases"])
        content.append("<tr><td>" + esc(case["name"]) + "</td><td>")
        for i, result in enumerate(report["rounds"], 1):
            attempts = result["cases"].get(key, {}).get("attempts", [])
            text = "; ".join(a["outcome"] + (": " + a["reason"] if a["reason"] else "") for a in attempts) or "لم تظهر في الجولة"
            content.append("<p>جولة " + str(i) + ": " + esc(text) + "</p>")
        content.append("</td></tr>")
    content.append("</table><h2>أدلة الجولات</h2>")
    for i, result in enumerate(report["rounds"], 1):
        content.append("<p>جولة " + str(i) + ": <a href='./" + esc(result["folder"], quote=True)
                       + "/'>النتائج والسجل</a> " + esc(result["error"]) + "</p>")
    (output / "stability-summary.html").write_text("\n".join(content) + "</html>", encoding="utf8")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    summary = analyze(args.output)
    write_report(args.output, summary)
    print(summary["verdict"], "—", summary["explanation"])
    raise SystemExit(0 if summary["verdict"] == "STABLE_PASS" else 1)
