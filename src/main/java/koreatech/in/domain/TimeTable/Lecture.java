package koreatech.in.domain.TimeTable;

import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Lecture {
    @ApiModelProperty(notes = "강의 코드", example = "MTF371")
    private String code;
    @ApiModelProperty(notes = "강의 이름", example = "3D 모델링과 프린팅 실습")
    private String name;
    @ApiModelProperty(notes = "대상 학년", example = "3")
    private String grades;
    @ApiModelProperty(notes = "강의 분반", example = "01", name = "class")
    private String lecture_class;
    @ApiModelProperty(notes = "수강 인원", example = "48")
    private String regular_number;
    @ApiModelProperty(notes = "강의 학과: 기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부", example = "컴퓨터공학부")
    private String department;
    @ApiModelProperty(notes = "강의 대상", example = "메카 3 제어")
    private String target;
    @ApiModelProperty(notes = "강의 교수", example = "안채헌")
    private String professor;
    @ApiModelProperty(notes = "영어강의 여부", example = "N")
    private String is_english;
    @ApiModelProperty(notes = "설계 학점", example = "1")
    private String design_score;
    @ApiModelProperty(notes = "이러닝 여부", example = "N")
    private String is_elearning;
    @ApiModelProperty(notes = "강의 시간", example = "[400,401,402,403,404,405,406,407]")
    private Integer[] class_time;

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getGrades() { return grades; }

    public void setGrades(String grades) { this.grades = grades; }

    public String getLecture_class() { return lecture_class; }

    public void setLecture_class(String lecture_class) { this.lecture_class = lecture_class; }

    public String getRegular_number() { return regular_number; }

    public void setRegular_number(String regular_number) { this.regular_number = regular_number; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }

    public String getProfessor() { return professor; }

    public void setProfessor(String professor) { this.professor = professor; }

    public String getIs_english() { return is_english; }

    public void setIs_english(String is_english) { this.is_english = is_english; }

    public String getDesign_score() { return design_score; }

    public void setDesign_score(String design_score) { this.design_score = design_score; }

    public String getIs_elearning() { return is_elearning; }

    public void setIs_elearning(String is_elearning) { this.is_elearning = is_elearning; }

    public Integer[] getClass_time() { return class_time; }

    // Modification for Database transfer
    public void setClass_time(String class_time) {
        // class_time stored in database follows "[401, 402, 403, ... , 501]" format
        if(Pattern.matches("\\[,*\\]", class_time)) {
            this.class_time = new Integer[]{};
        }
        else {
            String[] splitted = class_time.replaceAll("\\[?\\s*\\]?", "").split(",");
            this.class_time = new Integer[splitted.length];
            this.class_time = Arrays.stream(splitted).map(Integer::valueOf).toArray(Integer[]::new);
        }
    }

    @Override
    public String toString() {
        return "TimeTable{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", grades='" + grades + '\'' +
                ", lecture_class='" + lecture_class + '\'' +
                ", regular_number='" + regular_number + '\'' +
                ", department='" + department + '\'' +
                ", target='" + target + '\'' +
                ", professor='" + professor + '\'' +
                ", is_english='" + is_english + '\'' +
                ", design_score='" + design_score + '\'' +
                ", is_elearning='" + is_elearning + '\'' +
                ", class_time=" + Arrays.toString(class_time) +
                '}';
    }
}