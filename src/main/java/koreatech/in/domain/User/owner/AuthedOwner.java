package koreatech.in.domain.User.owner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class AuthedOwner extends Owner{
    private Map<Integer, String> shops;
}
