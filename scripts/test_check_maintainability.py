# شرح الملف: يراجع أن سياسة الصيانة تشمل حجم ملفات الاختبارات أيضًا، دون قبول ملف ضخم لأنه خارج الكود الرئيسي.
from pathlib import Path
import contextlib
import io
import tempfile
import unittest
import check_maintainability as policy
class MaintainabilityScopeTest(unittest.TestCase):
    def run_policy(self, source):
        with tempfile.TemporaryDirectory() as root:
            path = Path(root) / "src/test/java/example/ExampleTest.java"
            path.parent.mkdir(parents=True)
            path.write_text(source, encoding="utf8")
            previous = policy.ROOT
            try:
                policy.ROOT = Path(root)
                output = io.StringIO()
                with contextlib.redirect_stdout(output):
                    result = policy.main()
                return result, output.getvalue()
            finally:
                policy.ROOT = previous
    def test_oversized_test_source_is_rejected(self):
        result, output = self.run_policy("// line\n" * 401)
        self.assertTrue(result)
        self.assertIn("400", output)
    def test_small_enabled_test_source_is_accepted(self):
        result, _ = self.run_policy("class ExampleTest {}\n")
        self.assertFalse(result)
    def test_disabled_case_remains_rejected(self):
        result, output = self.run_policy("@Test(enabled=false)\nclass ExampleTest {}\n")
        self.assertTrue(result)
        self.assertIn("disabled test", output)
if __name__ == "__main__":
    unittest.main()
