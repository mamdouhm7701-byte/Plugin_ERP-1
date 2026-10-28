/* شرح الملف: حفظ طرق الاختبار المطلوبة والمستبعدة صراحة من خطة الجولة قبل تشغيلها.
 * فائدته: يميز الاستبعاد المقصود عند اختيار مجموعة صغيرة عن حالة مطلوبة لم تنفذ.
 */
package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;
import org.testng.xml.*;

public final class StabilitySelectionEvidence {
    private StabilitySelectionEvidence() {}

    static void write(Path output, List<XmlSuite> suites, String provenance) throws Exception {
        var expected = new TreeSet<String>();
        var excluded = new TreeSet<String>();
        for (var suite : suites)
            for (var test : suite.getTests())
                for (var type : test.getXmlClasses()) {
                    var includes =
                            type.getIncludedMethods().stream().map(XmlInclude::getName).toList();
                    var excludes = type.getExcludedMethods();
                    for (var method : Class.forName(type.getName()).getMethods()) {
                        var annotation = method.getAnnotation(org.testng.annotations.Test.class);
                        if (annotation == null || !annotation.enabled()) continue;
                        String name = method.getName(), identity = type.getName() + "." + name;
                        boolean selected =
                                (includes.isEmpty() || includes.stream().anyMatch(name::matches))
                                        && excludes.stream().noneMatch(name::matches);
                        if (selected) expected.add(identity);
                        else excluded.add(identity);
                    }
                }
        Files.writeString(
                output.resolve("selection-manifest.json"),
                new JSONObject(
                                Map.of(
                                        "expectedMethods",
                                        expected,
                                        "explicitlyExcludedMethods",
                                        excluded,
                                        "provenance",
                                        provenance))
                        .toString(2));
    }
}
