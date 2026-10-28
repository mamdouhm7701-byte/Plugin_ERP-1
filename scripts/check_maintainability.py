# شرح الملف: يراجع قواعد ترتيب المشروع والملفات الكبيرة والأخطاء المتكررة.
"""Enforce the structural cleanup without reading generated reports or local credentials."""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LITERALS = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*[\s\S]*?\*/')
LEGACY = re.compile(r"\b(?:Utiel|Listners|ElmentAction|WaitMangment|Firfox|TaypeTextB?)\b")


def violations(path, source):
    issues = []
    if len(source.splitlines()) > 400:
        issues.append("main Java class exceeds the 400-line review budget")
    if LEGACY.search(source):
        issues.append("legacy Java identifier remains")
    if path.name != "VueComponentAccess.java" and re.search(r"__vue__|\$children", source):
        issues.append("Vue root/traversal must use VueComponentAccess")
    if "Bots" in path.parts and "Thread.sleep" in source:
        issues.append("fixed UI sleep must be replaced with an observable condition")
    masked = LITERALS.sub(lambda match: " " * len(match[0]), source)
    for catch in re.finditer(r"catch\s*\([^)]*\)\s*\{", masked):
        depth, end = 1, catch.end()
        while depth and end < len(masked):
            depth += (masked[end] == "{") - (masked[end] == "}")
            end += 1
        body = source[catch.end():end - 1]
        if not re.search(r"\bthrow\b|LogUtils\.|printStackTrace|System\.(out|err)|addSuppressed", body):
            issues.append("catch has neither diagnostics nor a propagated failure")
    return issues


def main():
    findings = []
    for path in sorted((ROOT / "src/main/java").rglob("*.java")):
        for issue in violations(path, path.read_text(encoding="utf-8")):
            findings.append(f"{path.relative_to(ROOT).as_posix()}: {issue}")
    for path in (ROOT / "src/test/java").rglob("*.java"):
        if re.search(r"enabled\s*=\s*false", path.read_text(encoding="utf-8")):
            findings.append(f"{path.relative_to(ROOT).as_posix()}: disabled test requires explicit removal or repair")
    for finding in findings:
        print(finding)
    print(f"Maintainability policy violations: {len(findings)}")
    return bool(findings)


if __name__ == "__main__":
    sys.exit(main())
