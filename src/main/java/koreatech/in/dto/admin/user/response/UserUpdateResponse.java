package koreatech.in.dto.admin.user.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@SuperBuilder
public class UserUpdateResponse {

    @ApiModelProperty(notes = "이메일 주소 \n"
            , required = true
            , example = "koin123@koreatech.ac.kr"
    )
    private String email;

    @ApiModelProperty(notes = "이름 \n"
            , example = "정보혁"
    )
    private String name;
}
