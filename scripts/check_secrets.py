# شرح الملف: يراجع عدم إضافة بيانات دخول أو تقارير خاصة إلى الملفات المشتركة.
"""Reject credential files and populated credential settings; never print values."""
import re
import subprocess
import sys
from pathlib import Path

GENERATED = {'allure-results', 'allure-report', 'allure-single-report',
             'test-output', 'test-outputs', 'screenshots', 'videos', 'outputs'}
KEY = r'[\w.-]*(?:password|passwd|secret|token|api[_-]?key)[\w.-]*'
PROPERTY = re.compile(r'^\s*(' + KEY + r')\s*[=:]\s*(.*?)\s*$', re.I)
JSON_VALUE = re.compile(r'"(' + KEY + r')"\s*:\s*"([^"\n]*)"', re.I)

def findings(path, data):
    name = Path(path).name.lower()
    parts = path.replace('\\', '/').lower().split('/')
    issues = []
    if (parts[0] in GENERATED or '.local.properties' in name
            or name == '.env' or (name.startswith('.env.') and not name.endswith(('example', 'sample')))
            or name.endswith(('.pem', '.key', '.p12', '.pfx', '.jks', '.keystore'))
            or name in {'id_rsa', 'id_ed25519'}):
        issues.append('sensitive or generated file must remain untracked')
    if name.endswith(('.properties', '.json', '.yml', '.yaml')) or name.startswith('.env'):
        for number, line in enumerate(data.decode('utf-8', errors='replace').splitlines(), 1):
            if line.lstrip().startswith(('#', '//', '!')):
                continue
            matches = list(JSON_VALUE.finditer(line))
            prop = PROPERTY.match(line)
            if prop:
                matches.append(prop)
            for match in matches:
                value = match[2].strip().strip('"\'')
                if value and not value.startswith(('${', '<')):
                    issues.append(f'line {number}: populated credential key {match[1]}')
    if b'-----BEGIN ' in data and re.search(rb'-----BEGIN (?:[A-Z]+ )*PRIVATE KEY-----', data):
        issues.append('private key material')
    return issues

def main():
    sys.stdout.reconfigure(encoding='utf-8', errors='backslashreplace')
    paths = subprocess.check_output(['git', 'ls-files', '-z']).split(b'\0')
    count = 0
    for raw in paths:
        if not raw:
            continue
        path = raw.decode('utf-8')
        file = Path(path)
        if not file.is_file():
            continue
        data = (subprocess.check_output(['git', 'show', ':' + path])
                if '--staged' in sys.argv else file.read_bytes())
        for issue in findings(path, data):
            print(f'{path}: {issue}')
            count += 1
    print(f'Credential policy violations: {count} (values are never printed)')
    return int(count > 0)

if __name__ == '__main__':
    sys.exit(main())
