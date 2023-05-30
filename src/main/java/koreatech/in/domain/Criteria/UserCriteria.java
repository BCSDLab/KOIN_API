package koreatech.in.domain.Criteria;

import koreatech.in.domain.User.UserType;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Getter
@Setter
public class UserCriteria extends Criteria{

    private Boolean isAuthed;

    private String nickname;

    private String email;
}
