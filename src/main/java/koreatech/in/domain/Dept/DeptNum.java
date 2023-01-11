package koreatech.in.domain.Dept;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeptNum {

    @ApiModelProperty(name = "학과명", example = "컴퓨터공학부", readOnly = true)
    @JsonProperty(value = "name")
    private String dept_name;

    @ApiModelProperty(name = "학과 번호", example = "36", readOnly = true)
    private String dept_num;
}
