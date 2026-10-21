package Plugin_ERP.Utiel.Allur;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Opt-in Gmail delivery; never logs authentication values. */
public final class ReportMail {
    private ReportMail() {}

    public static Properties configuration() throws Exception {
        Properties config = new Properties();
        for (String name : new String[]{"report-mail.properties", "ReportMail.local.properties"}) {
            Path file = AllureConstants.PROJECT_DIR.resolve("config").resolve(name);
            if (Files.isRegularFile(file)) try (var input = Files.newInputStream(file)) { config.load(input); }
        }
        for (String key : new String[]{"enabled", "to", "username", "password"}) {
            String value = System.getenv("REPORT_MAIL_" + key.toUpperCase(java.util.Locale.ROOT));
            if (value != null && !value.isBlank()) config.setProperty(key, value);
        }
        return config;
    }

    public static boolean enabled() {
        try { return Boolean.parseBoolean(configuration().getProperty("enabled", "false")); }
        catch (Exception failure) { throw new IllegalStateException("Cannot load report mail configuration", failure); }
    }

    static InternetAddress[] recipients(String value) throws Exception {
        InternetAddress[] addresses = InternetAddress.parse(value, true);
        if (addresses.length == 0) throw new IllegalArgumentException("Report recipient required");
        for (InternetAddress address : addresses) address.validate();
        return addresses;
    }

    static Path archive(Path directory, Path destination) throws Exception {
        if (!Files.isDirectory(directory)) throw new IllegalArgumentException("Report directory missing");
        Files.createDirectories(destination.toAbsolutePath().getParent());
        try (var zip = new ZipOutputStream(Files.newOutputStream(destination)); var paths = Files.walk(directory)) {
            for (Path file : paths.filter(Files::isRegularFile).sorted().toList()) {
                if (Files.isSymbolicLink(file)) continue;
                zip.putNextEntry(new ZipEntry(directory.relativize(file).toString().replace('\\', '/')));
                Files.copy(file, zip);
                zip.closeEntry();
            }
        }
        return destination;
    }

    public static void send(Path testngDirectory) throws Exception {
        Properties config = configuration();
        String user = config.getProperty("username", "").trim();
        String password = config.getProperty("password", "").replace(" ", "");
        if (user.isEmpty() || password.isEmpty())
            throw new IllegalStateException("Set REPORT_MAIL_USERNAME and REPORT_MAIL_PASSWORD or local mail settings");
        InternetAddress[] to = recipients(config.getProperty("to", ""));
        Path temporary = Files.createTempDirectory("plugin-erp-mail-");
        try {
            Path portable = temporary.resolve("portable-allure");
            AllureBinaryManger.generateSingleFileReport(portable);
            Path allure = archive(portable, temporary.resolve("allure-report.zip"));
            Path testng = archive(testngDirectory, temporary.resolve("testng-report.zip"));
            if (Files.size(allure) + Files.size(testng) > 17L * 1024 * 1024)
                throw new IllegalStateException("Reports exceed the mail attachment budget; use retained CI artifacts");
            Properties smtp = new Properties();
            smtp.setProperty("mail.smtp.host", "smtp.gmail.com");
            smtp.setProperty("mail.smtp.port", "587");
            smtp.setProperty("mail.smtp.auth", "true");
            smtp.setProperty("mail.smtp.starttls.enable", "true");
            smtp.setProperty("mail.smtp.starttls.required", "true");
            smtp.setProperty("mail.smtp.ssl.checkserveridentity", "true");
            for (String timeout : new String[]{"connectiontimeout", "timeout", "writetimeout"})
                smtp.setProperty("mail.smtp." + timeout, "20000");
            Session session = Session.getInstance(smtp);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject("Plugin ERP reports - " + System.getProperty("allure.run.id", "local"), "UTF-8");
            MimeMultipart body = new MimeMultipart();
            MimeBodyPart text = new MimeBodyPart();
            String summary = Files.readString(AllureConstants.ALLURE_REPORT_DIR.resolve("widgets/summary.json"));
            text.setText("Plugin ERP test run completed.\n\nAllure summary:\n" + summary
                    + "\n\nAttached: Allure and TestNG reports. Extract each ZIP before opening."
                    + "\nOpen index.html from the extracted Allure ZIP in your browser.\n", "UTF-8");
            body.addBodyPart(text);
            for (Path attachment : new Path[]{allure, testng}) {
                MimeBodyPart part = new MimeBodyPart();
                part.attachFile(attachment.toFile());
                body.addBodyPart(part);
            }
            message.setContent(body);
            try (Transport transport = session.getTransport("smtp")) {
                transport.connect(user, password);
                transport.sendMessage(message, message.getAllRecipients());
            } catch (org.eclipse.angus.mail.smtp.SMTPSendFailedException rejected) {
                Plugin_ERP.Bots.LogUtils.error("SMTP rejected report: " + rejected.getReturnCode()
                        + " " + rejected.getMessage().replace(password, "[redacted]"));
                throw rejected;
            }
        } finally {
            try (var paths = Files.walk(temporary)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
    }
}
