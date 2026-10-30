/* شرح الملف: يتأكد أن الحسابات الفرعية منفصلة، وأرقامها ثابتة وغير متكررة، وأن امتلاء حساب رئيسي يبدأ حسابًا جديدًا. */
package TestSupport;

import java.util.HashSet;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureAccountPoolTest {
    @Test
    public void casesKeepDifferentChildrenAndReuseOnlyTheSameRoleParent() {
        var pool = new FixtureAccountPool(true);
        var first = pool.account("customer", "case-1-account-sub-customer");
        var second = pool.account("customer", "case-2-account-sub-customer");
        Assert.assertEquals(first.parentKey(), second.parentKey());
        Assert.assertNotEquals(first.childSuffix(), second.childSuffix());
        Assert.assertEquals(pool.account("customer", "case-1-account-sub-customer"), first);
        Assert.assertNotEquals(
                pool.account("bank", "case-1-account-sub-bank").parentKey(), first.parentKey());
        Assert.assertEquals(
                new FixtureAccountPool(true).account("customer", "case-1-account-sub-customer"),
                first);
    }

    @Test
    public void aNewParentPreservesTwoDigitCodesAfterNinetyNineChildren() {
        var pool = new FixtureAccountPool(true);
        var identities = new HashSet<String>();
        for (int i = 1; i <= 205; i++) {
            var slot = pool.account("customer", "case-" + i);
            Assert.assertTrue(slot.childSuffix().matches("[0-9]{2}"));
            Assert.assertTrue(identities.add(slot.parentKey() + "/" + slot.childSuffix()));
        }
        Assert.assertEquals(pool.account("customer", "case-99").childSuffix(), "99");
        Assert.assertEquals(pool.account("customer", "case-100").childSuffix(), "01");
        Assert.assertNotEquals(
                pool.account("customer", "case-99").parentKey(),
                pool.account("customer", "case-100").parentKey());
    }

    @Test
    public void disablingThePoolKeepsTheOriginalParentKeyAndCode() {
        var pool = new FixtureAccountPool(false);
        Assert.assertEquals(
                pool.account("customer", "case-1"),
                new FixtureAccountPool.Slot("account-main-customer", "01"));
        Assert.assertEquals(pool.account("customer", "case-1"), pool.account("customer", "case-2"));
    }
}
