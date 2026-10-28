# شرح الملف: يجرب فحص حماية البيانات الخاصة للتأكد من أنه يكتشفها.
import unittest
from check_secrets import findings

class CredentialPolicyTest(unittest.TestCase):
    def test_populated_credentials_are_rejected_without_values(self):
        for path, data in [('Login.properties', b'Test.Password=synthetic-example'),
                           ('Login.json', b'{"password":"synthetic-example"}'),
                           ('mail.yaml', b'password: synthetic-example')]:
            result = findings(path, data)
            self.assertTrue(result)
            self.assertNotIn('synthetic-example', str(result))

    def test_blank_and_external_secret_references_are_allowed(self):
        self.assertEqual([], findings('Login.properties', b'Test.Password=\n'))
        self.assertEqual([], findings('workflow.yml', b'PASSWORD: ${{ secrets.PASSWORD }}'))
        self.assertEqual([], findings('Login.json', b'{"password":""}'))

    def test_sensitive_files_and_generated_reports_are_rejected(self):
        for path in ['config/Login.local.properties', '.env', '.env.production',
                     'tls/private.pem', 'allure-single-report/index.html', 'outputs/data.xlsx']:
            self.assertTrue(findings(path, b''), path)

    def test_private_key_embedded_in_source_is_rejected(self):
        self.assertTrue(findings('example.txt', b'-----BEGIN ' + b'PRIVATE KEY-----'))

if __name__ == '__main__':
    unittest.main()
