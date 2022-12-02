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
    @ApiModelProperty(notes = "이름", example = "김주원")
    private String name;

    @ApiModelProperty(notes = "학번", example = "2019136037")
    @Size(max = 10, message = "학번은 최대 10자입니다.")
    private String student_number;

    @NotNull(message = "소속 트랙은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "소속 트랙", example = "BackEnd")
    private String track;

    @NotNull(message = "직급은 비워둘 수 없습니다.")
    @Pattern(regexp = "Mentor|Regular", message = "직급은 Mentor 또는 Regular 여야 합니다.")
    @ApiModelProperty(notes = "직급", example = "Regular")
    private String position;

    @ApiModelProperty(notes = "이메일", example = "damiano102777@naver.com")
    private String email;

    @ApiModelProperty(notes = "이미지 링크", example = "http://url.com")
    private String image_url;
}
