package koreatech.in.util.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {
    private static final String CLIENT_ORIGIN_HEADER = "Origin";

    @Autowired
    private AllowedDomains allowedDomains;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        if (allowedDomains.include(getClientURL(request), getServerURL(request))) {
            response.setHeader("Access-Control-Allow-Origin", getClientURL(request));
        }

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers","X-Requested-With, Origin, Content-Type, Accept, Authorization, password");
        chain.doFilter(req, res);
    }

    private String getClientURL(HttpServletRequest request) {
        return request.getHeader(CLIENT_ORIGIN_HEADER);
    }

    private String getServerURL(HttpServletRequest request) {
        return request.getScheme() + Origin.SCHEME_SEPARATOR + request.getServerName() + Origin.PORT_SEPARATOR + request.getServerPort();
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}
