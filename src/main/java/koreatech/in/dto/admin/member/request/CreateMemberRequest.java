package koreatech.in.dto.admin.member.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter @Setter
public class CreateMemberRequest {
    @NotNull(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름의 길이는 최대 50자입니다.")
    @ApiModelProperty(notes = "이름 \n" +
                              "- not null \n" +
                              "- 최대 50자", example = "김주원", required = true)
    private String name;

    @Size(max = 10, message = "학번의 길이는 최대 10자입니다.")
    @ApiModelProperty(notes = "학번 \n" +
                              "- 최대 10자", example = "2019136037")
    private String student_number;

    @NotNull(message = "트랙은 필수입니다.")
    @Pattern(regexp = "^(Android|BackEnd|FrontEnd|Game|UI\\/UX)$", message = "트랙의 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "소속 트랙 \n" +
                              "- not null \n" +
                              "- Android, BackEnd, FrontEnd, Game, UI/UX 중 택 1 " +
                              "(정규식 `^(Android|BackEnd|FrontEnd|Game|UI\\/UX)$` 을 만족해야함)", example = "BackEnd", required = true)
    private String track;

    @NotNull(message = "직급은 필수입니다.")
    @Pattern(regexp = "^(Mentor|Regular)$", message = "직급의 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "직급 \n" +
                              "- not null \n" +
                              "- Mentor 또는 Regular 중 택 1 " +
                              "(정규식 `^(Mentor|Regular)$` 을 만족해야함)", example = "Regular", required = true)
    private String position;

    @Size(max = 100, message = "이메일의 길이는 최대 100자입니다.")
    @ApiModelProperty(notes = "이메일 \n" +
                              "- 최대 100자", example = "damiano102777@naver.com")
    private String email;

    @Size(max = 65535, message = "이미지 링크의 길이는 최대 65535자입니다.")
    @ApiModelProperty(notes = "이미지 링크 \n" +
                              "- 최대 65535자", example = "https://example.com")
    private String image_url;
}
