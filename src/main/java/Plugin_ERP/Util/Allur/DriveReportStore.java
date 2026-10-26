package Plugin_ERP.Util.Allur;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

/** Drive API with restricted sharing. Access and refresh tokens never enter the journal/log. */
final class DriveReportStore {
    private final Properties config;
    private final URI testBase;
    private final Path journal;
    private final HttpClient client =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    private String access;

    DriveReportStore(Properties config) {
        this(config, null, AllureConstants.PROJECT_DIR.resolve("target/report-mail/drive-journal"));
    }

    DriveReportStore(Properties config, URI testBase, Path journal) {
        this.config = config;
        this.testBase = testBase;
        this.journal = journal;
        for (String key : List.of("drive.client.id", "drive.client.secret", "drive.refresh.token"))
            if (config.getProperty(key, "").isBlank())
                throw new IllegalStateException("Drive OAuth setup required");
        if (!config.getProperty("drive.folder.id", "").isBlank())
            throw new IllegalArgumentException(
                    "Report uploads require My Drive root to avoid inherited sharing");
        if (testBase != null
                && !("http".equals(testBase.getScheme()) && "127.0.0.1".equals(testBase.getHost())))
            throw new IllegalArgumentException("Test server must be loopback");
    }

    private URI endpoint(String live, String local) {
        return testBase == null ? URI.create(live) : testBase.resolve(local);
    }

    static Map<?, ?> json(String value) throws Exception {
        return (Map<?, ?>) new JSONParser().parse(value);
    }

    static String form(Map<String, String> values) {
        return values.entrySet().stream()
                .map(
                        e ->
                                URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                                        + "="
                                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("&"));
    }

    private String token() throws Exception {
        if (access == null) {
            var response =
                    client.send(
                            HttpRequest.newBuilder(
                                            endpoint(
                                                    "https://oauth2.googleapis.com/token",
                                                    "/token"))
                                    .timeout(Duration.ofSeconds(30))
                                    .header("Content-Type", "application/x-www-form-urlencoded")
                                    .POST(
                                            HttpRequest.BodyPublishers.ofString(
                                                    form(
                                                            Map.of(
                                                                    "client_id",
                                                                    config.getProperty(
                                                                            "drive.client.id"),
                                                                    "client_secret",
                                                                    config.getProperty(
                                                                            "drive.client.secret"),
                                                                    "refresh_token",
                                                                    config.getProperty(
                                                                            "drive.refresh.token"),
                                                                    "grant_type",
                                                                    "refresh_token"))))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());
            requireSuccess(response);
            access = Objects.toString(json(response.body()).get("access_token"), "");
            if (access.isBlank()) throw new IOException("Drive OAuth returned no token");
        }
        return access;
    }

    private HttpRequest.Builder request(URI uri) throws Exception {
        return HttpRequest.newBuilder(uri)
                .timeout(Duration.ofMinutes(2))
                .header("Authorization", "Bearer " + token());
    }

    private static void requireSuccess(HttpResponse<?> response) throws IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new IOException("Drive request failed HTTP " + response.statusCode());
    }

    private HttpResponse<String> jsonRequest(String method, URI uri, Map<String, ?> body)
            throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            var response =
                    client.send(
                            request(uri)
                                    .header("Content-Type", "application/json; charset=UTF-8")
                                    .method(
                                            method,
                                            HttpRequest.BodyPublishers.ofString(
                                                    JSONValue.toJSONString(body)))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 401 && attempt == 0) {
                access = null;
                continue;
            }
            if ((response.statusCode() == 429 || response.statusCode() >= 500) && attempt < 2) {
                Thread.sleep(500L << attempt);
                continue;
            }
            requireSuccess(response);
            return response;
        }
        throw new IOException("Drive request retry exhausted");
    }

    static String digest(Path file) throws Exception {
        var digest = MessageDigest.getInstance("SHA-256");
        try (var in = Files.newInputStream(file)) {
            byte[] buffer = new byte[65536];
            int n;
            while ((n = in.read(buffer)) != -1) digest.update(buffer, 0, n);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private URI fileUri(String id, String suffix) {
        if (!id.matches("[A-Za-z0-9_-]+"))
            throw new IllegalArgumentException("Invalid Drive file identifier");
        return endpoint(
                "https://www.googleapis.com/drive/v3/files/" + id + suffix,
                "/files/" + id + suffix);
    }

    private void save(Path path, Properties state) throws Exception {
        Files.createDirectories(path.getParent());
        Path next = path.resolveSibling(path.getFileName() + ".tmp");
        try (var out = Files.newOutputStream(next)) {
            state.store(out, "Drive delivery journal: no authentication values");
        }
        Files.move(next, path, StandardCopyOption.REPLACE_EXISTING);
    }

    synchronized String publish(Path file, List<String> recipients) throws Exception {
        if (recipients.isEmpty()) throw new IllegalArgumentException("Drive recipients required");
        String accountKey =
                config.getProperty("drive.client.id")
                        + "\n"
                        + config.getProperty("drive.refresh.token");
        String identity =
                HexFormat.of()
                        .formatHex(
                                MessageDigest.getInstance("SHA-256")
                                        .digest(accountKey.getBytes(StandardCharsets.UTF_8)));
        Path record =
                journal.resolve(identity.substring(0, 16) + "-" + digest(file) + ".properties");
        Properties state = new Properties();
        if (Files.isRegularFile(record))
            try (var in = Files.newInputStream(record)) {
                state.load(in);
            }
        String id = state.getProperty("id");
        if (id != null) {
            var check =
                    client.send(
                            request(fileUri(id, "?fields=id,trashed")).GET().build(),
                            HttpResponse.BodyHandlers.ofString());
            if (check.statusCode() == 404
                    || (check.statusCode() == 200
                            && Boolean.TRUE.equals(json(check.body()).get("trashed")))) id = null;
            else requireSuccess(check);
        }
        if (id == null) {
            id = upload(file);
            state.clear();
            state.setProperty("id", id);
            save(record, state);
        }
        for (String email : new LinkedHashSet<>(recipients)) {
            // Reapply on retry: permissions may have been revoked since the previous attempt.
            jsonRequest(
                    "POST",
                    fileUri(id, "/permissions?sendNotificationEmail=false"),
                    Map.of("type", "user", "role", "reader", "emailAddress", email));
        }
        verifySharing(id, recipients);
        String link = "https://drive.google.com/file/d/" + id + "/view";
        state.setProperty("url", link);
        state.setProperty("shared.with", String.join(",", recipients));
        state.setProperty("sharing.status", "complete");
        save(record, state);
        return link;
    }

    private String upload(Path file) throws Exception {
        Map<String, Object> metadata = new LinkedHashMap<>();
        String run =
                System.getProperty("allure.run.id", "local").replaceAll("[^A-Za-z0-9._-]", "_");
        metadata.put("name", "PluginERP-" + run + "-" + file.getFileName());
        metadata.put("mimeType", "application/zip");
        var init =
                jsonRequest(
                        "POST",
                        endpoint(
                                "https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable&fields=id",
                                "/upload"),
                        metadata);
        URI session =
                URI.create(
                        init.headers()
                                .firstValue("Location")
                                .orElseThrow(
                                        () -> new IOException("Missing Drive upload session")));
        if (testBase == null
                ? !("https".equals(session.getScheme())
                        && "www.googleapis.com".equals(session.getHost())
                        && session.getUserInfo() == null
                        && (session.getPort() == -1 || session.getPort() == 443))
                : !Objects.equals(session.getAuthority(), testBase.getAuthority()))
            throw new IOException("Invalid upload destination");
        long length = Files.size(file), offset = 0;
        int failures = 0;
        if (length == 0) throw new IOException("Empty report archive");
        try (var input = new RandomAccessFile(file.toFile(), "r")) {
            while (offset < length) {
                int count = (int) Math.min(8L * 1024 * 1024, length - offset);
                byte[] chunk = new byte[count];
                input.seek(offset);
                input.readFully(chunk);
                HttpResponse<String> response;
                try {
                    response =
                            client.send(
                                    request(session)
                                            .header("Content-Type", "application/zip")
                                            .header(
                                                    "Content-Range",
                                                    "bytes "
                                                            + offset
                                                            + "-"
                                                            + (offset + count - 1)
                                                            + "/"
                                                            + length)
                                            .PUT(HttpRequest.BodyPublishers.ofByteArray(chunk))
                                            .build(),
                                    HttpResponse.BodyHandlers.ofString());
                } catch (IOException interrupted) {
                    response = null;
                }
                if (response == null
                        || response.statusCode() == 429
                        || response.statusCode() >= 500
                        || response.statusCode() == 401) {
                    if (++failures > 3) throw new IOException("Drive upload retry exhausted");
                    if (response != null && response.statusCode() == 401) access = null;
                    Thread.sleep(500L * failures);
                    response =
                            client.send(
                                    request(session)
                                            .header("Content-Range", "bytes */" + length)
                                            .PUT(HttpRequest.BodyPublishers.noBody())
                                            .build(),
                                    HttpResponse.BodyHandlers.ofString());
                }
                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    String id = Objects.toString(json(response.body()).get("id"), "");
                    if (!id.matches("[A-Za-z0-9_-]+"))
                        throw new IOException("Drive returned no file identifier");
                    return id;
                }
                if (response.statusCode() != 308) {
                    requireSuccess(response);
                    throw new IOException("Unexpected Drive upload response");
                }
                String range = response.headers().firstValue("Range").orElse("");
                long next =
                        range.isEmpty()
                                ? 0
                                : Long.parseLong(range.substring(range.lastIndexOf('-') + 1)) + 1;
                if (next < 0 || next > length)
                    throw new IOException("Invalid acknowledged upload range");
                if (next <= offset && ++failures > 3) throw new IOException("Drive upload stalled");
                offset = next;
            }
        }
        throw new IOException("Drive upload ended without file metadata");
    }

    private void verifySharing(String id, List<String> recipients) throws Exception {
        Set<String> expected = new HashSet<>();
        for (String email : recipients) expected.add(email.toLowerCase(Locale.ROOT));
        Set<String> actual = new HashSet<>();
        String page = "";
        do {
            String suffix =
                    "/permissions?fields=nextPageToken,permissions(type,role,emailAddress)&pageSize=100";
            if (!page.isEmpty())
                suffix += "&pageToken=" + URLEncoder.encode(page, StandardCharsets.UTF_8);
            var response =
                    client.send(
                            request(fileUri(id, suffix)).GET().build(),
                            HttpResponse.BodyHandlers.ofString());
            requireSuccess(response);
            Map<?, ?> body = json(response.body());
            if (!(body.get("permissions") instanceof List<?> permissions))
                throw new IOException("Missing Drive permissions");
            for (Object item : permissions) {
                Map<?, ?> permission = (Map<?, ?>) item;
                String email =
                        Objects.toString(permission.get("emailAddress"), "")
                                .toLowerCase(Locale.ROOT);
                if ("owner".equals(permission.get("role"))) {
                    if (expected.contains(email)) actual.add(email);
                    continue;
                }
                if (!"user".equals(permission.get("type"))
                        || !"reader".equals(permission.get("role"))
                        || !expected.contains(email))
                    throw new IOException(
                            "Report has access beyond named recipients; review Drive sharing");
                actual.add(email);
            }
            page = Objects.toString(body.get("nextPageToken"), "");
        } while (!page.isEmpty());
        if (!actual.containsAll(expected))
            throw new IOException("Recipient access could not be verified");
    }
}
