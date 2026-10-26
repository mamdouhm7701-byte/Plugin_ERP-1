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
        for (String name : new String[]{"report-mail.properties", "ReportMail.local.properties", "Drive.local.properties"}) {
            Path file = AllureConstants.PROJECT_DIR.resolve("config").resolve(name);
            if (Files.isRegularFile(file)) try (var input = Files.newInputStream(file)) { config.load(input); }
        }
        for (String key : new String[]{"enabled", "to", "username", "password"}) {
            String value = System.getenv("REPORT_MAIL_" + key.toUpperCase(java.util.Locale.ROOT));
            if (value != null && !value.isBlank()) config.setProperty(key, value);
        }
        for (String key : new String[]{"drive.client.id", "drive.client.secret", "drive.refresh.token", "drive.folder.id"}) {
            String value = System.getenv("REPORT_" + key.toUpperCase(java.util.Locale.ROOT).replace('.', '_'));
            if (value != null && !value.isBlank()) config.setProperty(key, value);
        }
        return config;
    }

    public static boolean enabled() {
        try { return enabled(configuration()); }
        catch (Exception failure) { throw new IllegalStateException("Cannot load report mail configuration", failure); }
    }

    static boolean enabled(Properties config) {
        return Boolean.parseBoolean(config.getProperty("enabled", "false").trim())
                && !config.getProperty("to", "").isBlank();
    }

    public static void validateConfiguration() throws Exception {
        validateConfiguration(configuration());
    }

    static void validateConfiguration(Properties config) throws Exception {
        recipients(config.getProperty("to", ""));
        if (config.getProperty("username", "").isBlank()
                || config.getProperty("password", "").replace(" ", "").isBlank()) {
            throw new IllegalStateException("Set REPORT_MAIL_USERNAME and REPORT_MAIL_PASSWORD or local mail settings");
        }
    }

    static InternetAddress[] recipients(String value) throws Exception {
        InternetAddress[] addresses = InternetAddress.parse(value == null ? "" : value.trim(), true);
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
        validateConfiguration();
        Path bundle = AllureConstants.PROJECT_DIR.resolve("target/report-mail/deliveries")
                .resolve(java.time.Instant.now().toEpochMilli() + "-" + java.util.UUID.randomUUID());
        Files.createDirectories(bundle);
        var run = ReportMailTemplate.read(AllureConstants.ALLURE_REPORT_DIR, AllureConstants.ALLURE_RESULTS_DIR);
        Files.writeString(bundle.resolve("subject.txt"), ReportMailTemplate.subject(run));
        Files.writeString(bundle.resolve("summary.html"), ReportMailTemplate.html(run));
        Files.writeString(bundle.resolve("summary.txt"), ReportMailTemplate.plain(run));
        Path temporary = Files.createTempDirectory("plugin-erp-mail-");
        try {
            Path portable = temporary.resolve("portable-allure");
            AllureBinaryManger.generateSingleFileReport(portable);
            archive(portable, bundle.resolve("allure-report.zip"));
            archive(testngDirectory, bundle.resolve("testng-report.zip"));
        } finally {
            try (var paths = Files.walk(temporary)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
        sendPackaged(bundle);
    }

    /** Resends a retained snapshot without touching results or running tests. */
    public static void sendPackaged(Path bundle) throws Exception {
        Properties config = configuration();
        validateConfiguration(config);
        String user = config.getProperty("username", "").trim();
        String password = config.getProperty("password", "").replace(" ", "");
        InternetAddress[] to = recipients(config.getProperty("to", ""));
        Path allure = bundle.resolve("allure-report.zip"), testng = bundle.resolve("testng-report.zip");
        // Initialize Drive lazily: small reports do not require OAuth setup.
        var delivery = ReportDelivery.plan(java.util.List.of(allure, testng), 17L * 1024 * 1024,
                file -> new DriveReportStore(config).publish(file,
                        java.util.Arrays.stream(to).map(InternetAddress::getAddress).toList()));
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
            message.setSubject(Files.readString(bundle.resolve("subject.txt")).replaceAll("[\\r\\n]", " "), "UTF-8");
            MimeMultipart body = new MimeMultipart();
            MimeBodyPart text = new MimeBodyPart();
            MimeMultipart alternatives = new MimeMultipart("alternative");
            MimeBodyPart plain = new MimeBodyPart();
            plain.setText(Files.readString(bundle.resolve("summary.txt")) + delivery.plain(), "UTF-8");
            alternatives.addBodyPart(plain);
            MimeBodyPart html = new MimeBodyPart();
            html.setContent(Files.readString(bundle.resolve("summary.html")).replace("<!--REPORT_DELIVERY-->", delivery.html()), "text/html; charset=UTF-8");
            alternatives.addBodyPart(html);
            text.setContent(alternatives);
            body.addBodyPart(text);
            for (Path attachment : delivery.attachments()) {
                MimeBodyPart part = new MimeBodyPart();
                part.attachFile(attachment.toFile());
                body.addBodyPart(part);
            }
            message.setContent(body);
            try (Transport transport = session.getTransport("smtp")) {
                transport.connect(user, password);
                transport.sendMessage(message, message.getAllRecipients());
                System.setProperty("report.mail.last.status", delivery.complete() ? "sent" : "partial");
                Files.writeString(bundle.resolve("delivery-status.txt"), java.time.Instant.now() + " SMTP accepted; reports="
                        + (delivery.complete() ? "complete" : "partial; Drive upload or sharing unavailable"));
                if (!delivery.complete()) Plugin_ERP.Bots.LogUtils.error("Summary email accepted, but large report delivery is incomplete. Retry retained bundle after configuring Drive.");
            } catch (org.eclipse.angus.mail.smtp.SMTPSendFailedException rejected) {
                Plugin_ERP.Bots.LogUtils.error("SMTP rejected report: " + rejected.getReturnCode()
                        + " " + rejected.getMessage().replace(password, "[redacted]"));
                throw rejected;
            }
    }
}
