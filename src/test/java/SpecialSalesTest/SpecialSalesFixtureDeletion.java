/* شرح الملف: حذف بيانات المطعم المؤكدة الملكية والتحقق بعد كل استجابة ضائعة، دون حذف سجل تغيّرت علامته.
 * فائدته: يفصل سبب فشل التنظيف عن خطوات إنشاء الطلب وحساب ضريبته.
 */
package SpecialSalesTest;

import static SpecialSalesTest.SpecialSalesTestData.fields;

import SpecialSalesTest.SpecialSalesFixtureApi.ApiFailure;
import SpecialSalesTest.SpecialSalesTestData.Record;
import java.util.*;
import java.util.function.Consumer;

final class SpecialSalesFixtureDeletion {
    private static final String BRANCH_STORE_LINK = "SpecialSalesBranchStoreMembership";
    private final SpecialSalesTestData data;
    private final SpecialSalesFixtureApi api;
    private final Consumer<Record> unlink;

    SpecialSalesFixtureDeletion(
            SpecialSalesTestData data, SpecialSalesFixtureApi api, Consumer<Record> unlink) {
        this.data = data;
        this.api = api;
        this.unlink = unlink;
    }

    private static boolean retryable(ApiFailure failure) {
        return failure.status == 0
                || failure.status == 408
                || failure.status == 429
                || failure.status >= 500;
    }

    void remove(Record record) {
        Map<String, Object> owned = data.read(record);
        if (owned == null) return;
        if (!record.name().equals(owned.get("arabicName"))
                && !record.name().equals(owned.get("englishName"))
                && !record.name().equals(owned.get("notes")))
            throw new AssertionError(
                    "Refusing cleanup: fixture ownership changed for " + record.id());
        AssertionError lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                if (record.endpoint().equals(BRANCH_STORE_LINK)) unlink.accept(record);
                else api.call("delete", record.endpoint(), fields("id", record.id()));
                if (data.read(record) == null) return;
                lastFailure =
                        new AssertionError(
                                "Delete did not remove owned record: "
                                        + record.endpoint()
                                        + "/"
                                        + record.id());
            } catch (ApiFailure failure) {
                if (failure.status == 404
                        || failure.status == 410
                        || (failure.status == 400
                                && failure.getMessage().contains("Common.ObjectNotFound"))) {
                    if (data.read(record) == null) return;
                    throw new AssertionError(
                            "Delete endpoint did not remove existing owned record: "
                                    + record.endpoint()
                                    + "/"
                                    + record.id(),
                            failure);
                }
                if (!retryable(failure)) throw failure;
                lastFailure = failure;
            } catch (org.openqa.selenium.TimeoutException failure) {
                lastFailure =
                        new AssertionError(
                                "Delete response timed out for " + record.endpoint(), failure);
            }
            // A timed-out DELETE may have committed. Read back before sending another DELETE.
            try {
                Map<String, Object> existing = data.read(record);
                if (existing == null) {
                    return;
                }
                if (!record.name().equals(existing.get("arabicName"))
                        && !record.name().equals(existing.get("notes"))
                        && !record.name().equals(existing.get("englishName"))) {
                    throw new AssertionError(
                            "Refusing cleanup: fixture ownership changed for " + record.id());
                }
            } catch (ApiFailure failure) {
                if (failure.status == 404 || failure.status == 410) return;
                if (!retryable(failure)) throw failure;
                lastFailure = failure;
            } catch (org.openqa.selenium.TimeoutException failure) {
                lastFailure =
                        new AssertionError(
                                "Could not verify timed-out deletion of " + record.endpoint(),
                                failure);
            }
        }
        throw lastFailure;
    }
}
