/* شرح الملف: يبني اسم مستخدم التجربة بنفس بادئة الشركة في حساب الدخول المرجعي، ويرفض اسمًا فارغًا
 * أو اسمًا مرتبطًا بشركة أخرى.
 */
package Plugin_ERP.Util;

public final class LoginUsernames {
    private LoginUsernames() {}

    public static String withinSameCompany(String referenceLogin, String localUsername) {
        if (referenceLogin == null || referenceLogin.isBlank()) {
            throw new IllegalArgumentException("Reference login is missing");
        }
        if (localUsername == null || localUsername.isBlank() || localUsername.contains("\\")) {
            throw new IllegalArgumentException("Expected an unqualified fixture username");
        }
        int companySeparatorIndex = referenceLogin.indexOf('\\');
        if (companySeparatorIndex == 0) {
            throw new IllegalArgumentException("Company prefix is empty");
        }
        return companySeparatorIndex < 0
                ? localUsername
                : referenceLogin.substring(0, companySeparatorIndex + 1) + localUsername;
    }
}
