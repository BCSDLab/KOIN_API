package koreatech.in.dto.normal.user.owner.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import koreatech.in.dto.global.AttachmentUrlRequest;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OwnerUpdateRequest // extends UserUpdateRequest // 사장님 Update에 대한 요청이 정확히 확인되지 않아, 필수적인 첨부파일만 넘기도록 함.
{

    @Size(min = 3, max = 5, message = "이미지는 사업자등록증, 영업신고증, 통장사본을 포함하여 최소 3개 최대 5개까지 가능합니다.")
    @ApiModelProperty(notes = "첨부 이미지들 \n"
            + "- not null \n"
            + "- 이미지는 최소 3개 최대 5개까지 허용됨 \n"
            + "- 모든 이미지들이 코인 이미지 형식이어야 함 \n"
            , dataType = "[Lkoreatech.in.dto.global.AttachmentUrlRequest;"
    )
    @Valid
    private List<AttachmentUrlRequest> attachmentUrls;
}
