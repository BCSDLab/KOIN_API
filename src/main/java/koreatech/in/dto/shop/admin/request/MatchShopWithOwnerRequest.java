package koreatech.in.dto.shop.admin.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class MatchShopWithOwnerRequest {
    @NotNull(message = "owner_id는 필수입니다.")
    @ApiModelProperty(notes = "사장님 고유 id", example = "7")
    private Integer owner_id;
}
