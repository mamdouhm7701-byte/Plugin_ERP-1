package Plugin_ERP.Utiel;

/** New users retain local names in the API; their login uses the configured company's prefix. */
public final class LoginUsernames {
    private LoginUsernames() {}
    public static String withinSameCompany(String configuredLogin, String localUsername) {
        if(configuredLogin==null||configuredLogin.isBlank())throw new IllegalArgumentException("Reference login is missing");
        if(localUsername==null||localUsername.isBlank()||localUsername.contains("\\"))
            throw new IllegalArgumentException("Expected an unqualified fixture username");
        int separator=configuredLogin.indexOf('\\');
        if(separator==0)throw new IllegalArgumentException("Company prefix is empty");
        return separator<0?localUsername:configuredLogin.substring(0,separator+1)+localUsername;
    }
}
