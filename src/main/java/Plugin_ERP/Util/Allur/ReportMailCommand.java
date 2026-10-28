/* شرح الملف: تشغيل إرسال تقرير محفوظ عند طلبه.
 */
package Plugin_ERP.Util.Allur;

import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public final class ReportMailCommand {
    private ReportMailCommand() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            throw new IllegalArgumentException("Use authorize, send-current, or resend <bundle>");
        switch (args[0]) {
            case "authorize" -> {
                if (args.length != 2)
                    throw new IllegalArgumentException("Desktop OAuth client JSON path required");
                authorize(Path.of(args[1]));
            }
            case "send-current" -> {
                Path testng = Path.of(args.length > 1 ? args[1] : "target/surefire-reports");
                if (!Files.isRegularFile(testng.resolve("emailable-report.html")))
                    throw new IllegalArgumentException(
                            "Select the TestNG report directory from the same run");
                // Copy only the portable TestNG HTML, not unrelated old XML/screenshots in
                // Surefire.
                Path temporary = Files.createTempDirectory("report-testng-existing-");
                try {
                    Files.copy(
                            testng.resolve("emailable-report.html"),
                            temporary.resolve("emailable-report.html"));
                    ReportMail.send(temporary);
                } finally {
                    Files.deleteIfExists(temporary.resolve("emailable-report.html"));
                    Files.deleteIfExists(temporary);
                }
                System.out.println(
                        "SMTP accepted report summary. Check target/report-mail/deliveries for full delivery status.");
            }
            case "resend" -> {
                if (args.length != 2)
                    throw new IllegalArgumentException("Retained delivery directory required");
                ReportMail.sendPackaged(Path.of(args[1]));
                System.out.println(
                        "SMTP accepted retry. Check delivery-status.txt in the selected directory.");
            }
            default -> throw new IllegalArgumentException("Unknown command");
        }
    }

    private static String random() {
        byte[] value = new byte[32];
        new SecureRandom().nextBytes(value);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private static Map<String, String> query(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw != null)
            for (String pair : raw.split("&")) {
                String[] parts = pair.split("=", 2);
                result.put(
                        URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        parts.length == 2
                                ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                                : "");
            }
        return result;
    }

    private static void authorize(Path source) throws Exception {
        Map<?, ?> root = DriveReportStore.json(Files.readString(source));
        if (!(root.get("installed") instanceof Map<?, ?> installed))
            throw new IllegalArgumentException(
                    "Download an OAuth Desktop app client, not a service account or web client");
        String clientId = Objects.toString(installed.get("client_id"), ""),
                secret = Objects.toString(installed.get("client_secret"), "");
        if (clientId.isBlank() || secret.isBlank())
            throw new IllegalArgumentException("OAuth Desktop client fields missing");
        String verifier = random(), state = random();
        String challenge =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                MessageDigest.getInstance("SHA-256")
                                        .digest(verifier.getBytes(StandardCharsets.US_ASCII)));
        HttpServer callback = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String redirect = "http://127.0.0.1:" + callback.getAddress().getPort() + "/oauth/callback";
        CompletableFuture<String> code = new CompletableFuture<>();
        callback.createContext(
                "/oauth/callback",
                exchange -> {
                    var params = query(exchange.getRequestURI().getRawQuery());
                    boolean valid = state.equals(params.get("state")) && params.containsKey("code");
                    byte[] text =
                            (valid
                                            ? "Authorization received. Return to the terminal to finish setup."
                                            : "Authorization failed or was cancelled. Return to the terminal.")
                                    .getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                    exchange.sendResponseHeaders(valid ? 200 : 400, text.length);
                    exchange.getResponseBody().write(text);
                    exchange.close();
                    if (state.equals(params.get("state"))) {
                        if (valid) code.complete(params.get("code"));
                        else
                            code.completeExceptionally(
                                    new IllegalStateException("Google authorization cancelled"));
                    }
                });
        callback.start();
        try {
            URI auth =
                    URI.create(
                            "https://accounts.google.com/o/oauth2/v2/auth?"
                                    + DriveReportStore.form(
                                            Map.of(
                                                    "client_id",
                                                    clientId,
                                                    "redirect_uri",
                                                    redirect,
                                                    "response_type",
                                                    "code",
                                                    "scope",
                                                    "https://www.googleapis.com/auth/drive.file",
                                                    "access_type",
                                                    "offline",
                                                    "prompt",
                                                    "consent",
                                                    "state",
                                                    state,
                                                    "code_challenge",
                                                    challenge,
                                                    "code_challenge_method",
                                                    "S256")));
            System.out.println(
                    "Open this Google authorization link in your browser and choose the Drive report owner account:");
            System.out.println(auth);
            try {
                if (java.awt.Desktop.isDesktopSupported())
                    java.awt.Desktop.getDesktop().browse(auth);
            } catch (Exception ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "ReportMailCommand fallback: " + ignored.getClass().getSimpleName());

                /* The printed authorization URL remains usable. */
            }
            String authorizationCode = code.get(5, TimeUnit.MINUTES);
            var response =
                    HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(20))
                            .build()
                            .send(
                                    HttpRequest.newBuilder(
                                                    URI.create(
                                                            "https://oauth2.googleapis.com/token"))
                                            .timeout(Duration.ofSeconds(30))
                                            .header(
                                                    "Content-Type",
                                                    "application/x-www-form-urlencoded")
                                            .POST(
                                                    HttpRequest.BodyPublishers.ofString(
                                                            DriveReportStore.form(
                                                                    Map.of(
                                                                            "client_id",
                                                                            clientId,
                                                                            "client_secret",
                                                                            secret,
                                                                            "code",
                                                                            authorizationCode,
                                                                            "code_verifier",
                                                                            verifier,
                                                                            "grant_type",
                                                                            "authorization_code",
                                                                            "redirect_uri",
                                                                            redirect))))
                                            .build(),
                                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new IllegalStateException(
                        "Google token exchange failed HTTP " + response.statusCode());
            String refresh =
                    Objects.toString(
                            DriveReportStore.json(response.body()).get("refresh_token"), "");
            if (refresh.isBlank())
                throw new IllegalStateException("No offline token returned. Repeat Google consent");
            Properties config = new Properties();
            config.setProperty("drive.client.id", clientId);
            config.setProperty("drive.client.secret", secret);
            config.setProperty("drive.refresh.token", refresh);
            Path output = AllureConstants.PROJECT_DIR.resolve("config/Drive.local.properties");
            Files.createDirectories(output.getParent());
            try (var out = Files.newOutputStream(output)) {
                config.store(out, "Local Google Drive OAuth. Never share or commit this file.");
            }
            System.out.println(
                    "Drive authorized. Local OAuth settings saved; authentication values are not displayed.");
        } finally {
            callback.stop(0);
        }
    }
}
