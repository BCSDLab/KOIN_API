package koreatech.in.dto.normal.user.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class LoginResponse {
    @ApiModelProperty(
            notes = "액세스 토큰",
            required = true,
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxOTQ1IiwiZXhwIjoxNjc3ODI0Njc1fQ.3TyxbP3edVyOVNYMGBY-t7_N1B8qMR_ZNNcbjuyWp8U"
    )
    private final String token;

    @ApiModelProperty(
            notes = "로그인한 회원의 신원 \n" +
                    "- `STUDENT`: 학생 \n" +
                    "- `OWNER`: 사장님 \n",
            required = true,
            example = "STUDENT"
    )
    private final String user_type;
}

