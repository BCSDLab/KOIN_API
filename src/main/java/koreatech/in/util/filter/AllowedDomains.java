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

    @Autowired
    public AllowedDomains(@Value("${allowed.origins}") String allowDomainsLine) {
        this.allowedOrigins = makeOrigins(allowDomainsLine);
    }

    public boolean include(String clientOrigin) {
        if (clientOrigin == null || clientOrigin.isEmpty()) {
            return false;
        }

        return allowedOrigins.contains(Origin.from(clientOrigin));
    }

    private Set<Origin> makeOrigins(String allowDomainLine) {
        return Arrays.stream(allowDomainLine.split(LINE_DELIMITER))
                .map(Origin::from)
                .collect(Collectors.toSet());
    }

}
