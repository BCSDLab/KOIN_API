package koreatech.in.dto.admin.user.owner.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.admin.user.response.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel("AdminOwnerResponse")
public class OwnerResponse extends UserResponse {

    @ApiModelProperty(value = "사업자 등록 번호 " + "(12자 이내)", example = "2019161116")
    private String company_registration_number;

    @ApiModelProperty(value = "이미지 url" + "(255자 이내)", example = "static.koreatech.in/example.png")
    private String company_registration_certificate_image_url;

    @ApiModelProperty(value = "상점수정권한", example = "default=0")
    private Boolean grant_shop;

    @ApiModelProperty(value = "이벤트수정권한", example = "default=0")
    private Boolean grant_event;
}

