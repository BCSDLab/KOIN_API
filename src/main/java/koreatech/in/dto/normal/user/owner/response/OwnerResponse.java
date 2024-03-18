package koreatech.in.dto.normal.user.owner.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import koreatech.in.dto.normal.user.response.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel("OwnerResponse")
public class OwnerResponse extends UserResponse {

    @ApiModelProperty(notes = "이름 \n"
            , example = "정보혁"
            , required = true
    )
    private String name;

    @ApiModelProperty(notes = "사업자 등록 번호 "
            , example = "012-34-56789"
            , required = true
    )
    private String companyNumber;

    @ApiModelProperty(notes = "첨부파일 목록 \n"
            , required = true
    )
    @Valid
    private List<Attachment> attachments;

    @ApiModelProperty(notes = "가게 목록 \n"
            , required = true
    )
    @Valid
    private List<Shop> shops;

    @Getter
    @Builder
    @ApiModel("Attachment_in_OwnerResponse")
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Attachment {
        @ApiModelProperty(notes = "고유 id"
                , example = "2"
                , required = true)
        private Integer id;

        @ApiModelProperty(notes = "업로드된 파일 url"
                , example = "https://static.koreatech.in/example.png"
                , required = true
        )
        private final String fileUrl;

        @ApiModelProperty(notes = "업로드된 파일 명"
                , example = "example.png"
                , required = true
        )
        private final String fileName;
    }

    @Getter
    @Builder
    @ApiModel("Shop_in_OwnerResponse")
    public static class Shop {
        @ApiModelProperty(notes = "고유 id", example = "2", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "가장 맛있는 족발", required = true)
        private String name;
    }
}
