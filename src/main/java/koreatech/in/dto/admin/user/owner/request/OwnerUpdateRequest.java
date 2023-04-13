package koreatech.in.dto.admin.user.owner.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OwnerUpdateRequest {
    @NotNull(message = "사업자등록번호는 필수입니다")
    @Size(max=12, message = "사업자등록번호의 길이는 최대 12자 입니다")
    @ApiModelProperty(notes = "사업자등록번호 \n" +
            "- not null \n" +
            "- 최대 12자", example = "2019161116", required = true)
    private String company_registration_number;

    @Size(max = 255, message = "url의 길이는 최대 255자입니다.")
    @ApiModelProperty(notes = "url \n" +
            "- 최대 255자", example = "static.koreatech.in/example.png")
    private String company_registration_certificate_image_url;

    @ApiModelProperty(notes = "상점수정권한", example = "default=0")
    private Boolean grant_shop;

    @ApiModelProperty(notes = "이벤트수정권한", example = "default=0")
    private Boolean grant_event;
}
