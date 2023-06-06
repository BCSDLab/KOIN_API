package koreatech.in.domain.Criteria;

import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Getter
@Setter
public class StudentCriteria extends Criteria{

    private Boolean is_authed;

    private String nickname;

    private String email;
}
