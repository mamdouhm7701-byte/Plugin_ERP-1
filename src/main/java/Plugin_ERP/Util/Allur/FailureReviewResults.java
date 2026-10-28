/* شرح الملف: قراءة نتائج الحالات وأخطاء التجهيز والتنظيف المحفوظة في Allure.
 * فائدته: يمنع اختفاء فشل التجهيز من الملخص لمجرد أن الحالة نفسها لم تبدأ.
 */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.Util.Allur.ReviewJson.*;
import java.nio.file.*;
import java.util.*;

final class FailureReviewResults {
    record Entry(Path file, JSONObject test, boolean configuration) {}

    private FailureReviewResults() {}

    static List<Entry> read(Path results) throws java.io.IOException {
        List<Entry> entries = new ArrayList<>();
        try (var files = Files.list(results)) {
            for (Path file : files.sorted().toList()) {
                String filename = file.getFileName().toString();
                if (!filename.endsWith("-result.json") && !filename.endsWith("-container.json"))
                    continue;
                JSONObject data = new JSONObject(Files.readString(file));
                if (filename.endsWith("-result.json")) {
                    entries.add(new Entry(file, data, false));
                    continue;
                }
                for (String stage : List.of("befores", "afters")) {
                    JSONArray fixtures = data.optJSONArray(stage);
                    if (fixtures == null) continue;
                    for (int i = 0; i < fixtures.length(); i++) {
                        JSONObject fixture = fixtures.getJSONObject(i);
                        if (!Set.of("failed", "broken").contains(fixture.optString("status")))
                            continue;
                        JSONObject copy = new JSONObject(fixture.toString());
                        String name =
                                data.optString("name", "configuration")
                                        + " :: "
                                        + fixture.optString("name");
                        copy.put("fullName", name)
                                .put(
                                        "name",
                                        (stage.equals("befores")
                                                        ? "تعذر التجهيز: "
                                                        : "تعذر التنظيف: ")
                                                + name);
                        Path evidenceName =
                                file.resolveSibling(
                                        filename.replace(
                                                "-container.json",
                                                "-" + stage + "-" + i + "-configuration.json"));
                        entries.add(new Entry(evidenceName, copy, true));
                    }
                }
            }
        }
        return entries;
    }
}
