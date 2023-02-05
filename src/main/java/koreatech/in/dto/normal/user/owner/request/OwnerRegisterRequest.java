package koreatech.in.dto.normal.user.owner.request;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.user.request.UserRegisterRequest;


public class OwnerRegisterRequest extends UserRegisterRequest {

    @NotBlank(message = "사업자 등록 번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}", message = "사업자 등록 번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "사업자 등록 번호 \n"
            + "- not blank \n"
            + "- 사업자 등록 번호 형식이어야 함"
            , example = "012-34-56789"
            , required = true
    )
    private String companyNumber;

    //TODO 23.02.05 박한수 10mb 이하 이미지 파일만 허용 어떻게 처리할 것인지.
    @NotNull(message = "이미지 첨부는 필수입니다.")
    @Size(min = 3, max = 5, message = "이미지는 사업자등록증, 영업신고증, 통장사본을 포함하여 최대 5개까지 가능합니다.")
    @ApiModelProperty(notes = "첨부 이미지들 \n"
            + "- not null \n"
            + "- 이미지는 최소 3개 최대 5개까지 허용됨 \n"
            + "- 모든 이미지들이 코인 이미지 형식이어야 함 \n"
            , example = "012-34-56789"
            , required = true
    )
    @Valid
    private List<AttachmentUrlRequest> companyCertificateAttachmentUrls;
}