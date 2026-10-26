package Plugin_ERP.Util.Allur;

import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DriveReportStoreTest {
    @Test
    public void uploadsResumablySharesOnlyNamedReadersAndReusesVerifiedFile() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        var uploads = new AtomicInteger();
        var grants = new ArrayList<String>();
        var broadSharing = new java.util.concurrent.atomic.AtomicBoolean();
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        byte[] contents = "complete report".getBytes(StandardCharsets.UTF_8);
        server.createContext(
                "/token",
                exchange -> {
                    byte[] body =
                            "{\"access_token\":\"dummy-access\"}".getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
        server.createContext(
                "/upload",
                exchange -> {
                    uploads.incrementAndGet();
                    exchange.getResponseHeaders().add("Location", base + "/session");
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                });
        server.createContext(
                "/session",
                exchange -> {
                    Assert.assertEquals(exchange.getRequestBody().readAllBytes(), contents);
                    byte[] body = "{\"id\":\"file123\"}".getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
        server.createContext(
                "/files/file123",
                exchange -> {
                    if (exchange.getRequestURI().getPath().endsWith("/permissions")
                            && exchange.getRequestMethod().equals("POST")) {
                        grants.add(
                                new String(
                                        exchange.getRequestBody().readAllBytes(),
                                        StandardCharsets.UTF_8));
                    }
                    String payload =
                            exchange.getRequestURI().getPath().endsWith("/permissions")
                                            && exchange.getRequestMethod().equals("GET")
                                    ? broadSharing.get()
                                            ? "{\"permissions\":[{\"type\":\"anyone\",\"role\":\"reader\"}]}"
                                            : "{\"permissions\":[{\"type\":\"user\",\"role\":\"reader\",\"emailAddress\":\"one@gmail.com\"},{\"type\":\"user\",\"role\":\"reader\",\"emailAddress\":\"two@gmail.com\"}]}"
                                    : "{\"id\":\"file123\",\"trashed\":false}";
                    byte[] body = payload.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
        server.start();
        var root = Files.createTempDirectory("drive-store-test-");
        try {
            Path file = root.resolve("allure.zip");
            Files.write(file, contents);
            var config = new Properties();
            config.setProperty("drive.client.id", "dummy");
            config.setProperty("drive.client.secret", "dummy");
            config.setProperty("drive.refresh.token", "dummy-refresh");
            var store = new DriveReportStore(config, URI.create(base), root.resolve("journal"));
            String first = store.publish(file, List.of("one@gmail.com", "two@gmail.com"));
            Assert.assertEquals(first, "https://drive.google.com/file/d/file123/view");
            Assert.assertEquals(
                    store.publish(file, List.of("one@gmail.com", "two@gmail.com")), first);
            Assert.assertEquals(uploads.get(), 1);
            Assert.assertTrue(
                    grants.stream()
                            .allMatch(
                                    g ->
                                            g.contains("\"role\":\"reader\"")
                                                    && g.contains("\"type\":\"user\"")
                                                    && !g.contains("anyone")));
            Assert.assertTrue(grants.stream().anyMatch(g -> g.contains("two@gmail.com")));
            broadSharing.set(true);
            Assert.expectThrows(
                    java.io.IOException.class,
                    () -> store.publish(file, List.of("one@gmail.com", "two@gmail.com")));
            try (var paths = Files.list(root.resolve("journal"))) {
                String journal = Files.readString(paths.findFirst().orElseThrow());
                Assert.assertFalse(journal.contains("dummy-refresh"));
                Assert.assertFalse(journal.contains("dummy-access"));
            }
        } finally {
            server.stop(0);
            try (var paths = Files.walk(root)) {
                for (Path p : paths.sorted(Comparator.reverseOrder()).toList())
                    Files.deleteIfExists(p);
            }
        }
    }

    @Test
    public void missingOAuthFailsWithoutEchoingSecrets() {
        Assert.expectThrows(
                IllegalStateException.class, () -> new DriveReportStore(new Properties()));
    }

    @Test
    public void refusesParentFoldersThatCouldInheritBroaderAccess() {
        Properties config = new Properties();
        for (String k : List.of("drive.client.id", "drive.client.secret", "drive.refresh.token"))
            config.setProperty(k, "dummy");
        config.setProperty("drive.folder.id", "public-parent");
        Assert.expectThrows(IllegalArgumentException.class, () -> new DriveReportStore(config));
    }

    @Test
    public void interruptedUploadUsesServerAcknowledgedOffsetAndDoesNotReturnUnsharedLink()
            throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        AtomicInteger requests = new AtomicInteger(), permissionCalls = new AtomicInteger();
        server.createContext(
                "/token",
                e -> {
                    byte[] b = "{\"access_token\":\"dummy\"}".getBytes(StandardCharsets.UTF_8);
                    e.sendResponseHeaders(200, b.length);
                    e.getResponseBody().write(b);
                    e.close();
                });
        server.createContext(
                "/upload",
                e -> {
                    e.getResponseHeaders().add("Location", base + "/session");
                    e.sendResponseHeaders(200, -1);
                    e.close();
                });
        server.createContext(
                "/session",
                e -> {
                    int n = requests.incrementAndGet();
                    String range = e.getRequestHeaders().getFirst("Content-Range");
                    if (n == 1) {
                        e.getRequestBody().readAllBytes();
                        e.sendResponseHeaders(503, -1);
                    } else if (n == 2) {
                        Assert.assertEquals(range, "bytes */10");
                        e.getResponseHeaders().add("Range", "bytes=0-3");
                        e.sendResponseHeaders(308, -1);
                    } else {
                        Assert.assertEquals(range, "bytes 4-9/10");
                        Assert.assertEquals(
                                new String(
                                        e.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                                "456789");
                        byte[] b = "{\"id\":\"resumed123\"}".getBytes(StandardCharsets.UTF_8);
                        e.sendResponseHeaders(200, b.length);
                        e.getResponseBody().write(b);
                    }
                    e.close();
                });
        server.createContext(
                "/files/resumed123/permissions",
                e -> {
                    permissionCalls.incrementAndGet();
                    e.getRequestBody().readAllBytes();
                    e.sendResponseHeaders(403, -1);
                    e.close();
                });
        server.start();
        Path root = Files.createTempDirectory("drive-resume-");
        try {
            Path file = root.resolve("allure.zip");
            Files.writeString(file, "0123456789");
            Properties config = new Properties();
            for (String k :
                    List.of("drive.client.id", "drive.client.secret", "drive.refresh.token"))
                config.setProperty(k, "dummy");
            var store = new DriveReportStore(config, URI.create(base), root.resolve("journal"));
            Assert.expectThrows(
                    java.io.IOException.class,
                    () -> store.publish(file, List.of("reader@gmail.com")));
            Assert.assertEquals(requests.get(), 3);
            Assert.assertEquals(permissionCalls.get(), 1);
            try (var paths = Files.list(root.resolve("journal"))) {
                String journal = Files.readString(paths.findFirst().orElseThrow());
                Assert.assertTrue(journal.contains("resumed123"));
                Assert.assertFalse(journal.contains("sharing.status=complete"));
            }
        } finally {
            server.stop(0);
            try (var paths = Files.walk(root)) {
                for (Path p : paths.sorted(Comparator.reverseOrder()).toList())
                    Files.deleteIfExists(p);
            }
        }
    }

    @Test
    public void largeReportFailureStillProducesExplicitNoticeAndKeepsSmallAttachment()
            throws Exception {
        var root = Files.createTempDirectory("mail-routing-");
        try {
            Path large = root.resolve("allure.zip"), small = root.resolve("testng.zip");
            Files.write(large, new byte[20]);
            Files.write(small, new byte[2]);
            var delivery =
                    ReportDelivery.plan(
                            List.of(large, small),
                            10,
                            path -> {
                                throw new java.io.IOException("private token must not be rendered");
                            });
            Assert.assertEquals(delivery.attachments(), List.of(small));
            Assert.assertTrue(delivery.html().contains("allure.zip"));
            Assert.assertFalse(delivery.html().contains("private token"));
            Assert.assertFalse(delivery.complete());
            var success =
                    ReportDelivery.plan(
                            List.of(large, small),
                            10,
                            path -> "https://drive.google.com/file/d/file123/view");
            Assert.assertTrue(
                    success.html().contains("https://drive.google.com/file/d/file123/view"));
            Assert.assertTrue(success.complete());
            Assert.assertEquals(success.attachments(), List.of(small));
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path p : paths.sorted(Comparator.reverseOrder()).toList())
                    Files.deleteIfExists(p);
            }
        }
    }
}
