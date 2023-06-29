package koreatech.in.util.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.http.client.utils.URIBuilder;

public class Origin {
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
                    .setPort(Scheme.getDefaultPortFor(uri.getScheme()))
                    .build();

        } catch (IllegalArgumentException | URISyntaxException exception) {
            throw new IllegalArgumentException(String.format("origin의 %s가 규칙에 맞지 않습니다.", url));
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(String.format("origin의 %s가 비어있습니다.", url));
        }
    }

}
