package technbolts.compta.client.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class HttpClient {
    private final CookieStore cookieStore;
    private final DefaultHttpClient httpClient;

    private HttpResponse lastResponse = null;

    public HttpClient() {
        httpClient = new DefaultHttpClient();
        // Create a local instance of cookie store
        cookieStore = new BasicCookieStore();
        httpClient.setCookieStore(cookieStore);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public Cookie findCookie(String cookieName) {
        for (Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException {
        lastResponse = httpClient.execute(request);
        return lastResponse;
    }

    public int getLastStatusCode() {
        if (lastResponse == null)
            throw new IllegalStateException("lastResponse is null, no previous call");
        return lastResponse.getStatusLine().getStatusCode();
    }


    public void ensurePreviousConnectionConsumed() {
        try {
            lastResponse.getEntity().consumeContent();
        } catch (IOException e) {
            // Rien à faire
        }
    }
}
