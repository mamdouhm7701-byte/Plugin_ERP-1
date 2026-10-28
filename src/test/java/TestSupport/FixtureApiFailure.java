/* شرح الملف: يحتفظ بكود استجابة خدمة بيانات التجربة حتى يميز التنظيف بين الغياب المؤكد والفشل الذي يستحق إعادة المحاولة. */
package TestSupport;

public class FixtureApiFailure extends AssertionError {
    public final int status;

    public FixtureApiFailure(int status, String message) {
        super(message);
        this.status = status;
    }
}
