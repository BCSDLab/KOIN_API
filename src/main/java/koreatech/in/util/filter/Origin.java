package koreatech.in.util.filter;

import java.net.URI;
import java.util.Objects;

public class Origin {
    public static final String SCHEME_SEPARATOR = "://";

    public static final int EMPTY_PORT = -1;
    private final URI uri;

    private Origin(URI uri) {
        this.uri = uri;
    }

    public static Origin from(String url) {
        return new Origin(makeURI(url));
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

        return isEqualScheme(other) && isEqualHost(other) && isEqualPort(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri.getScheme(), uri.getHost(), uri.getPort());
    }

    private boolean isEqualPort(Origin other) {
        return this.uri.getPort() == other.uri.getPort();
    }

    public boolean isPortEmpty() {
        return this.uri.getPort() == EMPTY_PORT;
    }

    private boolean isEqualHost(Origin other) {
        return this.uri.getHost().equals(other.uri.getHost());
    }

    private boolean isEqualScheme(Origin other) {
        return this.uri.getScheme().equals(other.uri.getScheme());
    }

    private static URI makeURI(String url) {
        try {
            return URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(String.format("allowed.origins의 %s가 규칙에 맞지 않습니다.", url));
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(String.format("allowed.origins의 %s가 비어있습니다.", url));
        }
    }

    public Origin withEmptyPort() {
        return Origin.from(this.uri.getScheme() + SCHEME_SEPARATOR + this.uri.getHost());
    }

}
