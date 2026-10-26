package Plugin_ERP.Util.Allur;

import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

/** Records each deferred comparison at its actual position without changing assertAll semantics. */
public final class ScenarioSoftAssert extends SoftAssert {
    @Override
    public void onAssertSuccess(IAssert<?> assertion) {
        ScenarioAssertions.recordComparison(
                assertion.getActual(), assertion.getExpected(), assertion.getMessage(), null);
    }

    @Override
    public void onAssertFailure(IAssert<?> assertion, AssertionError error) {
        ScenarioAssertions.recordComparison(
                assertion.getActual(), assertion.getExpected(), assertion.getMessage(), error);
    }
}
