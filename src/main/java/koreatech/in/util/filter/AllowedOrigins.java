package koreatech.in.util.filter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AllowedOrigins {
    private final Set<Origin> allowedOrigins;

    @Autowired
    public AllowedOrigins(@Value("#{'${allowed.origins}'.split(',')}") List<String> allowDomains) {
        this.allowedOrigins = makeOrigins(allowDomains);
    }

    public boolean include(String clientOrigin) {
        if (clientOrigin == null || clientOrigin.isEmpty()) {
            return false;
        }

        return allowedOrigins.contains(Origin.from(clientOrigin));
    }

    private Set<Origin> makeOrigins(List<String> allowDomains) {
        return allowDomains.stream().map(Origin::from).collect(Collectors.toSet());
    }

}
