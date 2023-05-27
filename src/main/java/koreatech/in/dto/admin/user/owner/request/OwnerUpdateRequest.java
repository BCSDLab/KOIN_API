package koreatech.in.dto.admin.user.owner.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.admin.user.request.UserUpdateRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel("AdminOwnerUpdateRequest")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OwnerUpdateRequest extends UserUpdateRequest {
    @NotBlank(message = "사업자 등록 번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}", message = "사업자 등록 번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "사업자 등록 번호 \n"
            + "- not blank \n"
            + "- 사업자 등록 번호 형식이어야 함"
            , example = "012-34-56789"
            , required = true
    )
    private String company_registration_number;

    @ApiModelProperty(notes = "상점 수정 권한", example = "false")
    private Boolean grant_shop;

    @ApiModelProperty(notes = "이벤트수정권한", example = "false")
    private Boolean grant_event;

    @ApiModelProperty(hidden = true)
    private String password;
}
