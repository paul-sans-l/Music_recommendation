package Objects;

public class Authorizer {
    String AuthorizationCode;
    String AccessToken;

    public Authorizer(String AuthorizationCode, String AccessToken) {
        this.AuthorizationCode = AuthorizationCode;
        this.AccessToken = AccessToken;
    }

    public String getAuthorizationCode() {
        return AuthorizationCode;
    }

    public String getAccessToken() {
        return AccessToken;
    }
}
