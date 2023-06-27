package koreatech.in.util.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;

public class Origin {
    private static final String HTTPS = "https";
    private static final String HTTP = "http";
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int EMPTY_PORT = -1;

    private final URI uri;

    private Origin(URI uri) {
        this.uri = uri;
    }

    public static Origin from(String url) {
        return new Origin(makeURI(url.toLowerCase()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Origin other = (Origin) o;

        return this.uri.getScheme().equals(other.uri.getScheme())
                && this.uri.getHost().equals(other.uri.getHost())
                && this.uri.getPort() == other.uri.getPort();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri.getScheme(), uri.getHost(), uri.getPort());
    }

    private static URI makeURI(String url) {
        try {
            URI uri = URI.create(url);

            if (uri.getPort() != EMPTY_PORT) {
                return uri;
            }
            return new URIBuilder().setScheme(uri.getScheme())
                    .setHost(uri.getHost())
                    .setPort(makePortFrom(uri.getScheme()))
                    .build();

        } catch (IllegalArgumentException | URISyntaxException exception) {
            throw new IllegalArgumentException(String.format("origin의 %s가 규칙에 맞지 않습니다.", url));
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(String.format("origin의 %s가 비어있습니다.", url));
        }
    }

    private static int makePortFrom(String scheme) {
        if (StringUtils.isEmpty(scheme)) {
            throw new IllegalArgumentException();
        }

        if (HTTPS.equals(scheme)) {
            return DEFAULT_HTTPS_PORT;
        }
        if (HTTP.equals(scheme)) {
            return DEFAULT_HTTP_PORT;
        }
        return EMPTY_PORT;
    }

}
