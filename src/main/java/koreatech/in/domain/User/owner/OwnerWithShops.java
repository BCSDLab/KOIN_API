package koreatech.in.domain.User.owner;

import java.util.List;
import koreatech.in.domain.Shop.Shop;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OwnerWithShops extends Owner {
    private List<Shop> shops;
}
