package koreatech.in.util;

public class HttpHeaderValueAttacher {
    private final StringBuilder stringBuilder = new StringBuilder();
    private static final String COMMA = ", ";

    public static HttpHeaderValueAttacher start() {
        return new HttpHeaderValueAttacher();
    }

    public HttpHeaderValueAttacher attach(String headerValue) {
        if (this.stringBuilder.length() != 0) {
            stringBuilder.append(COMMA);
        }
        stringBuilder.append(headerValue);

        return this;
    }

    public String end() {
        return this.stringBuilder.toString();
    }
}
