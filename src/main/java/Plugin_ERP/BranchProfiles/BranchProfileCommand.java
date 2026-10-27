package Plugin_ERP.BranchProfiles;

import java.nio.file.Files;
import java.nio.file.Path;

/** Pins a runner's expectation before any live work, without accessing the application. */
public final class BranchProfileCommand {
    private BranchProfileCommand() {}

    public static void main(String[] args) throws java.io.IOException {
        if (args.length < 2 || args.length > 3)
            throw new IllegalArgumentException(
                    "Expected profile name, manifest path and optional fingerprint");
        write(
                args[0],
                Path.of("config", "branch-profiles"),
                Path.of(args[1]),
                args.length == 3 ? args[2] : null);
    }

    public static void write(String name, Path directory, Path manifest, String fingerprint)
            throws java.io.IOException {
        var profile =
                BranchProfiles.selected(name, directory, fingerprint)
                        .orElseThrow(
                                () -> new IllegalArgumentException("A profile name is required"));
        var snapshot = new java.util.LinkedHashMap<String, Object>();
        snapshot.put("name", profile.name());
        snapshot.put("fingerprint", profile.fingerprint());
        snapshot.put("expected", profile.expected().fields());
        if (Files.exists(manifest)) {
            Object parsed;
            try {
                parsed = new org.json.simple.parser.JSONParser().parse(Files.readString(manifest));
            } catch (org.json.simple.parser.ParseException failure) {
                throw new IllegalStateException("Invalid run manifest", failure);
            }
            if (!(parsed instanceof java.util.Map<?, ?> previous))
                throw new IllegalStateException("Invalid run manifest");
            if (!profile.fingerprint().equals(previous.get("fingerprint")))
                throw new IllegalStateException("Run manifest belongs to another branch profile");
            return;
        }
        Files.createDirectories(manifest.toAbsolutePath().getParent());
        Files.writeString(
                manifest,
                org.json.simple.JSONValue.toJSONString(snapshot),
                java.nio.file.StandardOpenOption.CREATE_NEW);
    }
}
