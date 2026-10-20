package FinancialMovementsTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialFixtureTest extends BaseTest {
    @Test
    public void createdFixturesAppearInFinancialLookups() {
        try (FinancialTestData data = new FinancialTestData(driver)) {
            FinancialTestData.Record customer = data.customer(1),
                    supplier = data.supplier(1),
                    payment = data.payment(),
                    expense = data.expense(),
                    income = data.income();
            Map<String, Object> collection = data.lookup("Aggregates/CollectionViewModel");
            Assert.assertTrue(
                    contains(collection.get("customers"), customer.id()),
                    "Created customer absent from lookup: " + data.read(customer));
            Assert.assertTrue(
                    contains(collection.get("paymentMethods"), payment.id()),
                    "Created payment absent from lookup");
            Assert.assertEquals(
                    data.read(supplier).get("accountId"), supplier.values().get("accountId"));
            Assert.assertEquals(
                    data.read(expense).get("accountId"), expense.values().get("accountId"));
            Assert.assertEquals(
                    data.read(income).get("accountId"), income.values().get("accountId"));
        }
    }

    private boolean contains(Object list, Object id) {
        return list instanceof List<?> values
                && values.stream()
                        .anyMatch(v -> v instanceof Map<?, ?> row && id.equals(row.get("id")));
    }
}
