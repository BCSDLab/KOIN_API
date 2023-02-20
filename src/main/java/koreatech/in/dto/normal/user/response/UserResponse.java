package koreatech.in.dto.normal.user.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class UserResponse {

    @ApiModelProperty(notes = "이메일 주소 \n"
            + "- not null \n"
            + "- 이메일 형식이어야 함"
            , required = true
            , example = "koin123@koreatech.ac.kr"
    )
    private String email;

    @ApiModelProperty(notes = "이름 \n"
            + "50자 이내여야 함"
            , example = "정보혁"
    )
    private String name;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
