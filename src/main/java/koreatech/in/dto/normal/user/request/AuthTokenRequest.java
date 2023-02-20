package koreatech.in.dto.normal.user.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenRequest {

    @NotNull(message = "토큰은 필수입니다.")
    @ApiModelProperty(notes
            = "인증 토큰\n"
            + "- not null \n"
            , required = true
    )
    private String token;
}
