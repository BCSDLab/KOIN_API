package koreatech.in.domain.Dept;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class DeptInfoVO {

    @ApiModelProperty(name = "학과명", example = "컴퓨터공학부", readOnly = true)
    private String name;

    @ApiModelProperty(name = "교육과정 링크", example = "https://~", readOnly = true)
    private String curriculum_link;

    @ApiModelProperty(name = "허용되는 학과 번호들", readOnly = true)
    private ArrayList<String> dept_nums;
}
