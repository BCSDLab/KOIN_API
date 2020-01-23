package koreatech.in.domain.TimeTable;

import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Semester {
    @ApiModelProperty(notes = "id", example = "1")
    private Integer id;

    @ApiModelProperty(notes = "학기", example = "20192")
    private String semester;

    public Integer getId() {
        return id;
    }

    public String getSemester() {
        return semester;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}