package koreatech.in.dto.admin.user.owner.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.admin.user.response.UserUpdateResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@ApiModel("AdminOwnerUpdateResponse")
public class OwnerUpdateResponse extends UserUpdateResponse {
    @ApiModelProperty(notes = "사업자등록번호" + "- 최대 12자", example = "012-34-56789")
    private String company_registration_number;

    @ApiModelProperty(notes = "상점 수정 권한", example = "false")
    private Boolean grant_shop;

    @ApiModelProperty(notes = "이벤트수정권한", example = "false")
    private Boolean grant_event;
}
