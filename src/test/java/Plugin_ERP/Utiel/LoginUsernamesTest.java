package Plugin_ERP.Utiel;

import org.testng.Assert;
import org.testng.annotations.Test;

public final class LoginUsernamesTest {
    @Test
    public void explicitCompanyLoginIsRetainedForTheOwnedUser() {
        Assert.assertEquals(
                LoginUsernames.withinSameCompany("example-company\\admin", "qa_user"),
                "example-company\\qa_user");
    }

    @Test
    public void domainBasedLoginLeavesTheUserLocal() {
        Assert.assertEquals(LoginUsernames.withinSameCompany("admin", "qa_user"), "qa_user");
    }

    @Test
    public void aFixtureCannotSelectAnUnrelatedCompany() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> LoginUsernames.withinSameCompany("own\\admin", "other\\qa_user"));
    }

    @Test
    public void missingLoginContextIsRejected() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> LoginUsernames.withinSameCompany(" ", "qa_user"));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> LoginUsernames.withinSameCompany("\\admin", "qa_user"));
    }
}
