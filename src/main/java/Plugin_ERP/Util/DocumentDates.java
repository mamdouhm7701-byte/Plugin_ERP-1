package Plugin_ERP.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Dates displayed by the Arabic document date picker, including invisible direction marks. */
public final class DocumentDates {
    private DocumentDates() {}

    public static LocalDate parse(String displayed) {
        StringBuilder normalized = new StringBuilder();
        displayed
                .strip()
                .codePoints()
                .filter(c -> Character.getType(c) != Character.FORMAT)
                .forEach(
                        c -> {
                            int digit = Character.digit(c, 10);
                            normalized.appendCodePoint(
                                    digit >= 0 ? '0' + digit : c == '/' ? '-' : c);
                        });
        String value = normalized.toString();
        DateTimeFormatter iso =
                DateTimeFormatter.ofPattern("uuuu-M-d", Locale.ROOT)
                        .withResolverStyle(ResolverStyle.STRICT);
        try {
            return LocalDate.parse(value, iso);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT)
                            .withResolverStyle(ResolverStyle.STRICT));
        }
    }
}
