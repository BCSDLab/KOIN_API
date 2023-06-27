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

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String clientOrigin = getClientOrigin(servletRequest);

        if (allowedDomains.include(clientOrigin)) {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", clientOrigin);
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        }

        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers","X-Requested-With, Origin, Content-Type, Accept, Authorization, password");
        chain.doFilter(servletRequest, servletResponse);
    }

    private String getClientOrigin(ServletRequest servletRequest) {
        return ((HttpServletRequest) servletRequest).getHeader(CLIENT_ORIGIN_HEADER);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}
