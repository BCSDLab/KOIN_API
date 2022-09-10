package koreatech.in.domain.Dept;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeptInfo {

    @ApiModelProperty(name = "학과 번호", example = "36", readOnly = true)
    private String dept_num;

    @ApiModelProperty(name = "학과명", example = "컴퓨터공학부", readOnly = true)
    private String name;

    @ApiModelProperty(name = "교육과정 링크", example = "https://~", readOnly = true)
    private String curriculum_link;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Boolean is_deleted;
}
