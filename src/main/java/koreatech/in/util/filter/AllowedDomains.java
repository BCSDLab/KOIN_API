package koreatech.in.util.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AllowedDomains {
    private static final String LINE_DELIMITER = ",";

    private final Set<Origin> allowedOrigins;
    private final Set<Origin> allowedOriginsWithEmptyPort;

    @Autowired
    public AllowedDomains(@Value("${allowed.origins}") String allowDomainsLine) {
        this.allowedOrigins = makeOrigins(allowDomainsLine);
        this.allowedOriginsWithEmptyPort = makeOriginsWithEmptyPort(allowDomainsLine);
    }

    public boolean include(String clientOrigin, String serverOrigin) {
        if(clientOrigin == null || clientOrigin.isEmpty()) {
            return false;
        }
        if (isSameOrigin(clientOrigin, serverOrigin)) {
            return true;
        }

        return isAllowedUrl(Origin.from(clientOrigin));
    }

    private boolean isSameOrigin(String clientUrl, String serverUrl) {
        return clientUrl.equals(serverUrl);
    }

    private boolean isAllowedUrl(Origin clinetOrigin) {
        return allowedOrigins.contains(clinetOrigin) || allowedOriginsWithEmptyPort.contains(clinetOrigin.withEmptyPort());
    }

    private Set<Origin> makeOrigins(String allowDomainLine) {
        return Arrays.stream(allowDomainLine.split(LINE_DELIMITER))
                .map(Origin::from)
                .filter(origin -> !origin.isPortEmpty())
                .collect(Collectors.toSet());
    }

    private Set<Origin> makeOriginsWithEmptyPort(String allowDomainLine) {
        return Arrays.stream(allowDomainLine.split(LINE_DELIMITER))
                .map(Origin::from)
                .filter(Origin::isPortEmpty)
                .collect(Collectors.toSet());
    }

}
