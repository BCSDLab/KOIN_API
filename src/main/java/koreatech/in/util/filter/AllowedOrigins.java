package koreatech.in.util.filter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AllowedOrigins {
    private final Set<Origin> origins;

    @Autowired
    public AllowedOrigins(@Value("#{'${allowed.origins}'.split(',')}") List<String> allowedOrigins) {
        this.origins = transformOrigins(allowedOrigins);
    }

    public boolean include(String clientOrigin) {
        if (StringUtils.isEmpty(clientOrigin)) {
            return false;
        }
        return origins.contains(Origin.from(clientOrigin));
    }

    private Set<Origin> transformOrigins(List<String> allowDomains) {
        return allowDomains.stream()
                .map(Origin::from)
                .collect(Collectors.toSet());
    }

}
