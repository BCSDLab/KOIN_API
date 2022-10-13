package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class MatchShopWithOwnerDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer shop_id;

    @NotNull(message = "상점 고유 id는 필수입니다.")
    @ApiModelProperty(notes = "사장님 고유 id")
    private Integer owner_id;

    public MatchShopWithOwnerDTO init(Integer shop_id) {
        this.shop_id = shop_id;
        return this;
    }
}
