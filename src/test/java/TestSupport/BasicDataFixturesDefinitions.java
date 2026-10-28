/* شرح الملف: تحديد البيانات المطلوبة لإنشاء سجلات التجربة، ضمن تجهيز بيانات التجربة.
 */
package TestSupport;

import static TestSupport.BasicDataFixtures.fields;

import TestSupport.BasicDataFixtures.Record;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

final class BasicDataFixturesDefinitions {
    private final BasicDataFixtures data;

    BasicDataFixturesDefinitions(BasicDataFixtures data) {
        this.data = data;
    }

    Record account(String key) {
        Record parent =
                data.fixture(
                        "account-main-" + key,
                        "Accounts",
                        fields(
                                "type",
                                "Main",
                                "code",
                                Integer.toString(
                                        java.util.concurrent.ThreadLocalRandom.current()
                                                .nextInt(10000000, 99999999)),
                                "nature",
                                "Debit",
                                "finalAccountType",
                                "Budget",
                                "level",
                                1,
                                "isDistributable",
                                false,
                                "isSharedBetweenBranches",
                                false,
                                "prevDebit",
                                0,
                                "prevCredit",
                                0,
                                "previousBalances",
                                List.of()));
        return data.fixture(
                "account-sub-" + key,
                "Accounts",
                fields(
                        "type",
                        "Sub",
                        "nature",
                        "Debit",
                        "code",
                        parent.values().get("code") + "01",
                        "finalAccountType",
                        "Budget",
                        "parentId",
                        parent.id(),
                        "level",
                        2,
                        "isDistributable",
                        false,
                        "isSharedBetweenBranches",
                        false,
                        "prevDebit",
                        0,
                        "prevCredit",
                        0,
                        "previousBalances",
                        List.of()));
    }

    Record bank(int index) {
        return data.fixture(
                "bank-" + index, "Banks", fields("accountId", account("bank-" + index).id()));
    }

    Record safe(int index) {
        return data.fixture(
                "safe-" + index, "Safes", fields("accountId", account("safe-" + index).id()));
    }

    Record user(String key, Object branchId) {
        Record saved =
                data.fixture(
                        "user-" + key,
                        "users",
                        fields(
                                "userName",
                                "qa_bd_" + UUID.randomUUID().toString().replace("-", ""),
                                "passwordHash",
                                UUID.randomUUID().toString() + "aA1!",
                                "isAdmin",
                                false,
                                "isActive",
                                true,
                                "maxAllowedDiscount",
                                0,
                                "permissions",
                                List.of(),
                                "branches",
                                List.of(fields("branchId", branchId)),
                                "defaultOtherPaymentMethods",
                                List.of(),
                                "imageUrl",
                                "",
                                "phone",
                                "",
                                "email",
                                "",
                                "base64Image",
                                "",
                                "imageExtension",
                                ""));
        var stored = data.read(saved);
        var values = new LinkedHashMap<>(saved.values());
        if (stored != null && stored.get("code") != null) values.put("code", stored.get("code"));
        return new Record(saved.endpoint(), saved.id(), saved.name(), values);
    }
}
