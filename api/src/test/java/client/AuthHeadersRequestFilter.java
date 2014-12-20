package client;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.Credentials;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class AuthHeadersRequestFilter implements ClientRequestFilter {
    private String userName;
    private String password;

    public AuthHeadersRequestFilter(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("Authorization", "Basic " + Base64.encodeBase64String((userName + ':' + password).getBytes()));
    }
}
