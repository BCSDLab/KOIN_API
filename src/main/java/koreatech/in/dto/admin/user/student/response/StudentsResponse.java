package koreatech.in.dto.admin.user.student.response;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.student.Student;
import koreatech.in.mapstruct.admin.user.StudentConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class StudentsResponse {
    @ApiModelProperty(notes = "조건에 해당하는 총 학생들의 수", example = "57", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "조건에 해당하는 학생들중에 현재 페이지에서 조회된 수", example = "10", required = true)
    private Integer current_count;

    @ApiModelProperty(notes = "조건에 해당하는 학생들을 조회할 수 있는 최대 페이지", example = "6", required = true)
    private Integer total_page;

    @ApiModelProperty(notes = "현재 페이지", example = "2", required = true)
    private Integer current_page;

    @ApiModelProperty(notes = "학생 리스트", required = true)
    private List<StudentsResponse.Students> students;

    @Getter
    @Builder
    public static class Students {
        @ApiModelProperty(notes = "고유 id", example = "10", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이메일", example = "koin123@koreatech.ac.kr", required = true)
        private String email;

        @ApiModelProperty(notes = "닉네임", example = "bbo")
        private String nickname;

        @ApiModelProperty(notes = "이름", example = "정보혁")
        private String name;

        @ApiModelProperty(notes = "전공", example = "컴퓨터공학부")
        private String major;

        @ApiModelProperty(notes = "학번", example = "2029136012")
        private String student_number;
    }

    public static StudentsResponse of(Integer totalCount, Integer totalPage, Integer currentPage, List<Student> students) {
        return StudentsResponse.builder()
                .total_count(totalCount)
                .total_page(totalPage)
                .current_page(currentPage)
                .current_count(students.size())
                .students(
                        students.stream()
                                .map(StudentConverter.INSTANCE::toStudentsResponse$Students)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
