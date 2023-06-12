package koreatech.in.util.filter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AllowedDomains {

    public static final String DELIMETER = ",";
    public static final String DOT = ".";
    public static final String PROTOCOL_PREFIX = "https://";

    private final String originHost;

    private final List<String> crossHosts;

    /**
     * TODO
     * 1. https(프로토콜), port(포트번호) 처리
     * 2. 입력 중에서 무엇을 사용할 것인지? 전체 URL을 사용해야 하는 것 아닌지
     * 3. domain은 전체가 아니라 host(doamin)만 갖고 있음.
     * 4. 이메일 처리
     */
    @Autowired
    public AllowedDomains(@Value("${project.domain}") String allowDomain,
                          @Value("${cross.domains}") String crossDomainsLine) {
        this.originHost = makeDomainHost(allowDomain);
        this.crossHosts = makeCrossHosts(crossDomainsLine);
    }

    public boolean canAllow(String requestHost) {
        if (isEmpty(requestHost)) {
            return false;
        }

        return isIncludedInAllowedDomain(requestHost) || isIncludedInCrossDomains(requestHost);
    }

    private boolean isEmpty(String requestHost) {
        return requestHost == null || requestHost.isEmpty();
    }

    private boolean isIncludedInAllowedDomain(String requestHost) {
        return isIncludedIn(requestHost, this.originHost);
    }

    private boolean isIncludedInCrossDomains(String requestHost) {
        Optional<String> allowedDomain = crossHosts.stream()
                .filter(crossDomainHosts -> isIncludedIn(requestHost, crossDomainHosts)).findAny();

        return allowedDomain.isPresent();
    }

    private boolean isIncludedIn(String requestHost, String allowedDomainHost) {
        return allowedDomainHost.equals(requestHost) || requestHost.endsWith(suffixFrom(requestHost));
    }

    private Optional<String> getHost(String domain) {
        try {
            return Optional.of(new URL(makeUrl(domain)).getHost());
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    private String makeUrl(String domain) {
        return PROTOCOL_PREFIX + domain;
    }

    private String suffixFrom(String requestHost) {
        return DOT + requestHost;
    }

    private String makeDomainHost(String allowDomain) {
        return getHost(allowDomain)
                .orElseThrow(() -> new RuntimeException("In AllowedDomains, project.domain is invalid."));
    }

    private List<String> makeCrossHosts(String crossDomainsLine) {
        return Arrays.stream(crossDomainsLine.split(DELIMETER))
                .map(crossDomain -> getHost(crossDomain).orElseThrow(
                        () -> new RuntimeException("In AllowedDomains, cross.doamins are invalid.")))
                .collect(Collectors.toList());
    }

}
