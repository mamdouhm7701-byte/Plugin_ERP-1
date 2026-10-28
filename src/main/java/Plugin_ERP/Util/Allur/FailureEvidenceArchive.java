/* شرح الملف: حفظ نسخة محلية من نتيجة الحالة ومرفقاتها حتى لا تضيع عند التشغيل التالي.
 * فائدته: يتيح فتح دليل الفشل أو إعادة الاختبار من الملخص القديم.
 */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.Util.Allur.ReviewJson.*;
import java.nio.file.*;

final class FailureEvidenceArchive {
    private FailureEvidenceArchive() {}

    static JSONArray links(JSONObject node, Path destination) {
        JSONArray links = new JSONArray();
        JSONArray attachments = node.optJSONArray("attachments");
        if (attachments != null)
            for (int i = 0; i < attachments.length(); i++) {
                JSONObject attachment = attachments.getJSONObject(i);
                String name = attachment.optString("source");
                if (name.isBlank()
                        || name.contains("/")
                        || name.contains("\\")
                        || name.contains(":")) continue;
                Path file = destination.resolve(name);
                if (Files.isRegularFile(file))
                    links.put(
                            new JSONObject()
                                    .put("name", attachment.optString("name", "دليل الحالة"))
                                    .put("url", file.toAbsolutePath().toUri().toString()));
            }
        JSONArray steps = node.optJSONArray("steps");
        if (steps != null)
            for (int i = 0; i < steps.length(); i++) {
                JSONArray children = links(steps.getJSONObject(i), destination);
                for (int n = 0; n < children.length(); n++) links.put(children.get(n));
            }
        return links;
    }

    static Path save(Path source, Path result, Path destination, JSONObject test)
            throws java.io.IOException {
        Files.createDirectories(destination);
        copyAttachments(source.toAbsolutePath().normalize(), destination, test);
        // Credentials and arbitrary input values are not needed in the review copy.
        JSONObject safe = new JSONObject(test.toString());
        safe.remove("parameters");
        safe.remove("links");
        Path saved = destination.resolve(result.getFileName().toString());
        Files.writeString(saved, safe.toString());
        return saved;
    }

    private static void copyAttachments(Path source, Path destination, JSONObject node)
            throws java.io.IOException {
        JSONArray attachments = node.optJSONArray("attachments");
        if (attachments != null)
            for (int i = 0; i < attachments.length(); i++) {
                String name = attachments.getJSONObject(i).optString("source");
                // Allure attachment names are flat filenames; reject external or nested paths.
                if (name.isBlank()
                        || name.contains("/")
                        || name.contains("\\")
                        || name.contains(":")) continue;
                Path file = source.resolve(name).normalize();
                if (!file.getParent().equals(source)
                        || !Files.isRegularFile(file)
                        || Files.isSymbolicLink(file)) continue;
                Files.copy(file, destination.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
        JSONArray steps = node.optJSONArray("steps");
        if (steps != null)
            for (int i = 0; i < steps.length(); i++)
                copyAttachments(source, destination, steps.getJSONObject(i));
    }
}
