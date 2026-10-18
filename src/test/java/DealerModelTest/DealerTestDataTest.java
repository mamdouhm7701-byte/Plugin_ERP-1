package DealerModelTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DealerTestDataTest extends BaseTest {
  @Test
  public void createLinkedFixturesInCurrentTenant() {
    try (DealerTestData data = new DealerTestData(driver)) {
      Assert.assertNotNull(data.area().id());
      Assert.assertNotNull(data.classification("Customers").id());
      Assert.assertNotNull(data.classification("Suppliers").id());
      Assert.assertNotNull(data.mainDealer("Customers").id());
      Assert.assertNotNull(data.mainDealer("Suppliers").id());
      Assert.assertNotNull(data.agent("Sales").id());
      Assert.assertNotNull(data.agent("Purch").id());
      Assert.assertNotNull(data.item(1).id());
      Assert.assertNotNull(data.item(2).id());
      Assert.assertEquals(data.read(data.agent("Sales")).get("areaId"), data.area().id());
      Assert.assertEquals(data.read(data.item(1)).get("categoryId"), data.category().id());
      Assert.assertEquals(data.read(data.item(1)).get("mainUnitId"), data.unit().id());
      Assert.assertNotNull(data.priceList().id());
    }
  }
}
