package koreatech.in.util.filter;

import java.util.Arrays;

public enum Scheme {
    HTTPS(443),
    HTTP(80),
    ;

    private static final int EMPTY = -1;

    private final int defaultPort;

    Scheme(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public static int getDefaultPortFor(String schemeName) {
        return Arrays.stream(Scheme.values())
                .filter(scheme -> scheme.name().equalsIgnoreCase(schemeName))
                .map(Scheme::getDefaultPort)
                .findAny()
                .orElse(EMPTY);
    }
}
