package Plugin_ERP.Utiel.Allur;

import java.nio.file.*;
import java.util.*;

/** Selects attachments and private Drive links before constructing MIME. */
final class ReportDelivery {
    interface Publisher { String publish(Path file) throws Exception; }
    record Plan(List<Path> attachments, String html, String plain, boolean complete) {}
    static Plan plan(List<Path> files, long budget, Publisher publisher) throws Exception {
        List<Path> attachments = new ArrayList<>();
        StringBuilder html = new StringBuilder(), plain = new StringBuilder();
        long remaining = budget; boolean complete = true;
        for (Path file : files) {
            String name = file.getFileName().toString();
            String safe = name.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;");
            String instruction = name.startsWith("allure") ? "index.html" : "emailable-report.html";
            if (Files.size(file) <= remaining) {
                attachments.add(file); remaining -= Files.size(file);
                html.append("<p><b dir=\"ltr\">").append(safe).append("</b> · مرفق بالرسالة</p>");
                plain.append("\nAttached: ").append(name);
            } else {
                try {
                    String link = publisher.publish(file);
                    if (!link.matches("https://drive\\.google\\.com/file/d/[A-Za-z0-9_-]+/view"))
                        throw new IllegalArgumentException("Invalid Drive link");
                    html.append("<p><a style=\"display:inline-block;background:#101e38;color:#ffffff;padding:12px 20px;border-radius:8px;text-decoration:none\" href=\"")
                            .append(link).append("\">تحميل ").append(safe).append(" من Google Drive</a></p>");
                    plain.append("\nDownload ").append(name).append(": ").append(link);
                } catch (Exception failure) {
                    complete = false;
                    Plugin_ERP.Bots.LogUtils.error("Drive delivery unavailable for " + name + " (" + failure.getClass().getSimpleName() + "); packaged report retained locally.");
                    html.append("<p style=\"color:#b42318\">تعذر رفع أو مشاركة ").append(safe)
                            .append(". النسخة الكاملة محفوظة على جهاز التشغيل لإعادة الإرسال.</p>");
                    plain.append("\nUnavailable: ").append(name).append(". Full report retained on runner for retry.");
                    continue;
                }
            }
            html.append("<p style=\"color:#64748b;font-size:12px\">فك الضغط وافتح <span dir=\"ltr\">").append(instruction).append("</span> في المتصفح.</p>");
            plain.append(". Extract ZIP and open ").append(instruction).append('.');
        }
        return new Plan(List.copyOf(attachments), html.toString(), plain.toString(), complete);
    }
}
