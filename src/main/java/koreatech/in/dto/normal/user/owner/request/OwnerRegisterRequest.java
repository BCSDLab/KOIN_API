package koreatech.in.dto.normal.user.owner.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import koreatech.in.annotation.OwnerRegistrationInfomation;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.user.request.UserRegisterRequest;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@OwnerRegistrationInfomation(
        companyNumber = "companyNumber",
        attachmentUrls = "attachmentUrls"
)
public class OwnerRegisterRequest extends UserRegisterRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "이름 \n"
            + "- not null \n"
            + "50자 이내여야 함"
            , required = true
            , example = "정보혁"
    )
    private String name;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}", message = "사업자 등록 번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "사업자 등록 번호 \n"
            + "- required = false \n"
            + "- 사업자 등록 번호 형식이어야 함"
            + "- 사업자등록번호와 첨부파일에 대해 아무것도 입력 안하거나 혹은 모두 입력하거나 둘 중 하나에 만족해야 함."
            , example = "012-34-56789"
            , required = false
    )
    private String companyNumber;

    @Size(min = 1, max = 5, message = "이미지는 사업자등록증, 영업신고증, 통장사본을 포함하여 최소 1개 최대 5개까지 가능합니다.")
    @ApiModelProperty(notes = "첨부 이미지들 \n"
            + "- required = false \n"
            + "- 이미지는 최소 1개 최대 5개까지 허용됨 \n"
            + "- 모든 이미지들이 코인 이미지 형식이어야 함 \n"
            , dataType = "[Lkoreatech.in.dto.global.AttachmentUrlRequest;"
            , required = false
    )
    @Valid
    private List<AttachmentUrlRequest> attachmentUrls;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호\n"
            + "- not null"
            + "- 휴대폰 번호 형식이여야 함"
            ,example = "010-0000-0000"
            ,required = true
    )
    private String phoneNumber;

    @ApiModelProperty(notes = "상점 ID \n"
            ,required = false)
    private Integer shopId;

    @ApiModelProperty(notes = "상점명 \n"
            ,required = false)
    private String shopName;
}
