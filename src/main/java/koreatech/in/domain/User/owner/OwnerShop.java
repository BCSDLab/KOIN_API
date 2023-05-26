package koreatech.in.domain.User.owner;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class OwnerShop {
    private final Integer owner_id;
    private final Integer shop_id;
    private final String shop_name;
}
