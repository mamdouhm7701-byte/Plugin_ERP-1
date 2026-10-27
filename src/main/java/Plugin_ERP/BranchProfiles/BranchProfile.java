package Plugin_ERP.BranchProfiles;

import java.util.ArrayList;
import java.util.Objects;

/** An immutable expected profile. Verification must never rewrite it to match the application. */
public record BranchProfile(String name, BranchSnapshot expected) {
    public BranchProfile {
        Objects.requireNonNull(name);
        Objects.requireNonNull(expected);
    }

    public String fingerprint() {
        StringBuilder canonical =
                new StringBuilder("branch-profile-v1\n").append(name).append('\n');
        expected.fields()
                .forEach(
                        (key, value) ->
                                canonical.append(key).append('=').append(value).append('\n'));
        try {
            byte[] digest =
                    java.security.MessageDigest.getInstance("SHA-256")
                            .digest(
                                    canonical
                                            .toString()
                                            .getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException failure) {
            throw new IllegalStateException("SHA-256 is unavailable", failure);
        }
    }

    public void verify(BranchSnapshot actual) {
        var mismatches = new ArrayList<String>();
        expected.fields()
                .forEach(
                        (field, value) -> {
                            if (!Objects.equals(value, actual.fields().get(field)))
                                mismatches.add(field);
                        });
        if (!mismatches.isEmpty()) {
            throw new IllegalStateException(
                    "Branch profile '"
                            + name
                            + "' mismatch: "
                            + String.join(", ", mismatches)
                            + "; refusing profile-based scenario preparation");
        }
    }
}
