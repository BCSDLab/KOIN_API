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

    @Autowired
    private AllowedDomains allowedDomains;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
//        String clientURL = request.getRequestURL().toString();
//        String serverURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();


//        URI clientURI = new URI(clientURL);
//        String clientBaseURL = clientURI.getScheme() + "://" + clientURI.getAuthority();

        // 활용하기
        String requestHost = request.getHeader("Host");
        if (allowedDomains.canAllow(requestHost)) {
            response.setHeader("Access-Control-Allow-Origin", requestHost);
        }

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "X-Requested-With, Origin, Content-Type, Accept, Authorization, password");
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }
}
