package koreatech.in.dto.normal.user.owner.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.user.response.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OwnerResponse extends UserResponse {

    @ApiModelProperty(notes = "이름 "
            , required = true
            , example = "정보혁"
    )
    private String name;

    @ApiModelProperty(notes = "사업자 등록 번호 "
            , example = "012-34-56789"
            , required = true
    )
    private String companyNumber;

    @ApiModelProperty(notes = "첨부 이미지들 \n"
            , required = true
    )
    @Valid
    private List<AttachmentUrlRequest> attachmentUrls;
}
