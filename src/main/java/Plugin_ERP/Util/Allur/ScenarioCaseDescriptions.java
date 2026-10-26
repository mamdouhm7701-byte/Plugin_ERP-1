package Plugin_ERP.Util.Allur;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Human-authored case details, independent of execution results and failure diagnosis. */
public final class ScenarioCaseDescriptions {
    private static final Map<String, CaseDetails> CASES = load();

    private ScenarioCaseDescriptions() {}

    public static String html(String className, String method) {
        CaseDetails details = CASES.get(className + "#" + method);
        if (details == null) return null;
        StringBuilder out =
                new StringBuilder(
                        "<div lang=\"ar\" dir=\"rtl\" style=\"text-align:right;line-height:1.8\">");
        section(out, "بنختبر إيه؟", details.objective());
        section(out, "قبل التجربة", details.preconditions());
        section(out, "بيانات التجربة", details.data());
        out.append("<h3>خطوات التجربة</h3><ol>");
        for (String step : details.steps()) out.append("<li>").append(escape(step)).append("</li>");
        out.append("</ol>");
        section(out, "المتوقع", details.expected());
        return out.append("</div>").toString();
    }

    public static int size() {
        return CASES.size();
    }

    public static java.util.Set<String> references() {
        return CASES.keySet();
    }

    public static String title(String className, String method) {
        CaseDetails details = CASES.get(className + "#" + method);
        return details == null ? null : details.title();
    }

    private static void section(StringBuilder out, String name, String text) {
        if (!text.isBlank())
            out.append("<h3>").append(name).append("</h3><p>").append(escape(text)).append("</p>");
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    record CaseDetails(
            String title,
            String objective,
            String preconditions,
            String data,
            List<String> steps,
            String expected) {
        CaseDetails {
            steps = List.copyOf(steps);
        }
    }

    private static Map<String, CaseDetails> load() {
        Map<String, CaseDetails> result = new LinkedHashMap<>();
        for (String name :
                new String[] {
                    "allure-case-descriptions-master.json",
                    "allure-case-descriptions-transactions.json",
                    "allure-case-descriptions-remaining.json"
                }) {
            try (var stream =
                    ScenarioCaseDescriptions.class.getClassLoader().getResourceAsStream(name)) {
                if (stream == null) throw invalid(name, "resource is missing from the classpath");
                Map<String, CaseDetails> entries =
                        parse(name, new InputStreamReader(stream, StandardCharsets.UTF_8));
                for (var entry : entries.entrySet()) {
                    if (result.putIfAbsent(entry.getKey(), entry.getValue()) != null)
                        throw invalid(name, "duplicate case " + entry.getKey());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read case explanations: " + name, e);
            }
        }
        return Map.copyOf(result);
    }

    static Map<String, CaseDetails> parse(String source, Reader reader) {
        final Object parsed;
        try {
            parsed = new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Cannot parse case explanations: " + source, e);
        }
        if (!(parsed instanceof Map<?, ?> root)) throw invalid(source, "root must be an object");
        Map<String, CaseDetails> result = new LinkedHashMap<>();
        for (var entry : root.entrySet()) {
            if (!(entry.getKey() instanceof String key)
                    || key.indexOf('#') <= 0
                    || key.endsWith("#")
                    || key.indexOf('#') != key.lastIndexOf('#'))
                throw invalid(source, "case reference must be Class#method");
            String location = source + " [" + key + "]";
            if (!(entry.getValue() instanceof Map<?, ?> fields))
                throw invalid(location, "case must be an object");
            if (!(fields.get("steps") instanceof List<?> steps) || steps.isEmpty())
                throw invalid(location, "steps must be a nonempty list of text");
            for (Object step : steps) {
                if (!(step instanceof String text) || text.isBlank())
                    throw invalid(location, "each step must be nonblank text");
            }
            result.put(
                    key,
                    new CaseDetails(
                            required(fields, "title", location),
                            required(fields, "objective", location),
                            required(fields, "preconditions", location),
                            required(fields, "data", location),
                            steps.stream().map(String.class::cast).toList(),
                            required(fields, "expected", location)));
        }
        return Map.copyOf(result);
    }

    private static String required(Map<?, ?> fields, String name, String location) {
        if (!(fields.get(name) instanceof String text) || text.isBlank())
            throw invalid(location, name + " must be nonblank text");
        return text;
    }

    private static IllegalStateException invalid(String location, String problem) {
        return new IllegalStateException(
                "Invalid case explanations in " + location + ": " + problem);
    }
}
