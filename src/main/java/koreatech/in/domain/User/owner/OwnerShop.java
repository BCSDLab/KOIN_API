package koreatech.in.domain.User.owner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerShop {
    private final Integer owner_id;
    private final Integer shop_id;
    private final String shop_name;

    @JsonCreator
    public OwnerShop(@JsonProperty("owner_id") Integer owner_id,
                     @JsonProperty("shop_id") Integer shop_id,
                     @JsonProperty("shop_name") String shop_name) {
        this.owner_id = owner_id;
        this.shop_id = shop_id;
        this.shop_name = shop_name;
    }
}
