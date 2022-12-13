package koreatech.in.dto.member.admin.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class UpdateMemberRequest {
    @NotNull(message = "이름은 비워둘 수 없습니다.")
    @Size(max = 50, message = "이름은 최대 50자 입니다.")
    @ApiModelProperty(notes = "이름 \n" +
            "- NOT NULL \n" +
            "- 최대 50자", example = "김주원", required = true)
    private String name;

    @Size(max = 10, message = "학번은 최대 10자입니다.")
    @ApiModelProperty(notes = "학번 \n" +
            "- NULL 가능 \n" +
            "- 최대 10자", example = "2019136037")
    private String student_number;

    @NotNull(message = "소속 트랙은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "소속 트랙 \n" +
            "- NOT NULL \n" +
            "- Android, BackEnd, FrontEnd, Game, UI/UX 중 택 1", example = "BackEnd", required = true)
    private String track;

    @NotNull(message = "직급은 비워둘 수 없습니다.")
    @Pattern(regexp = "Mentor|Regular", message = "직급은 Mentor 또는 Regular 여야 합니다.")
    @ApiModelProperty(notes = "직급 \n" +
            "- NOT NULL \n" +
            "- Mentor 또는 Regular 중 택 1", example = "Regular", required = true)
    private String position;

    @ApiModelProperty(notes = "이메일 \n" +
            "- NULL 가능", example = "damiano102777@naver.com")
    private String email;

    @ApiModelProperty(notes = "이미지 링크 \n" +
            "- NULL 가능", example = "http://url.com")
    private String image_url;
}
