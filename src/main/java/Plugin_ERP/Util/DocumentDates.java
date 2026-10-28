/* شرح الملف: تحديد التواريخ المستخدمة في المستندات.
 */
package Plugin_ERP.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

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
            Plugin_ERP.Bots.LogUtils.debug(
                    "DocumentDates fallback: " + ignored.getClass().getSimpleName());

            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT)
                            .withResolverStyle(ResolverStyle.STRICT));
        }
    }
}
