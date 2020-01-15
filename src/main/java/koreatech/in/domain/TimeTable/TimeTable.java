package koreatech.in.domain.TimeTable;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.User;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class TimeTable {
    @ApiModelProperty(notes = "id", example = "1", hidden = true)
    private Integer id;
    @ApiModelProperty(notes = "user_id", example = "1", hidden = true)
    private Integer user_id;
    @ApiModelProperty(notes = "semester_id", example = "1", hidden = true)
    private Integer semester_id;
    @ApiModelProperty(notes = "code", example = "MSA100")
    private String code;
    @ApiModelProperty(notes = "class_title", example = "2D공학")
    private String class_title;
    @ApiModelProperty(notes = "class_time", example = "[401,402,403, ... ,405]")
    private ArrayList<Integer> class_time;
    @ApiModelProperty(notes = "class_place", example = "2공학관 402호")
    private String class_place;
    @ApiModelProperty(notes = "professor", example = "김최박")
    private String professor;
    @ApiModelProperty(notes = "grades", example = "3")
    private String grades;
    @ApiModelProperty(notes = "lecture_class", example = "01")
    private String lecture_class;
    @ApiModelProperty(notes = "target", example = "컴퓨 1 컴퓨   ")
    private String target;
    @ApiModelProperty(notes = "regular_number", example = "35")
    private String regular_number;
    @ApiModelProperty(notes = "design_score", example = "1")
    private String design_score;
    @ApiModelProperty(notes = "department", example = "컴퓨터공학부")
    private String department;
    @ApiModelProperty(notes = "memo", example = "사용자의 메모메모")
    private String memo;
    @ApiModelProperty(notes = "is_deleted", example = "0")
    private Boolean is_deleted;

    public void setId(Integer id) { this.id = id; }

    public Integer getId() { return id; }

    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public Integer getUser_id() { return user_id; }

    public Integer getSemester_id() {
        return semester_id;
    }

    public void setSemester_id(Integer semester_id) { this.semester_id = semester_id; }

    public String getClass_title() { return class_title; }

    public void setClass_title(String class_title) { this.class_title = class_title; }

    public String getClass_time() {
        return class_time.toString();
    }

    public ArrayList<Integer> getClass_timeAsArray() { return class_time; }

    public void setClass_time(String class_time) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if(!Pattern.matches("\\[,*\\]", class_time)) {
            String[] splitted = class_time.replaceAll("\\[?\\s*\\]?", "").split(",");
            for (String item : splitted) ret.add(Integer.parseInt(item));
        }
        this.class_time = ret;
    }

    public String getClass_place() { return class_place; }

    public void setClass_place(String class_place) { this.class_place = class_place; }

    public String getProfessor() { return professor; }

    public void setProfessor(String professor) { this.professor = professor; }

    public String getGrades() { return grades; }

    public void setGrades(String grades) { this.grades = grades; }

    public String getMemo() { return memo; }

    public void setMemo(String memo) { this.memo = memo; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getLecture_class() { return lecture_class; }

    public void setLecture_class(String lecture_class) { this.lecture_class = lecture_class; }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }

    public String getRegular_number() { return regular_number; }

    public void setRegular_number(String regular_number) { this.regular_number = regular_number; }

    public String getDesign_score() { return design_score; }

    public void setDesign_score(String design_score) { this.design_score = design_score; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public Boolean getIs_deleted() { return is_deleted; }

    public void setIs_deleted(Boolean is_deleted) { this.is_deleted = is_deleted; }

    public void update(TimeTable timeTable) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(timeTable.class_title != null) {
            this.class_title = timeTable.class_title;
        }
        if(timeTable.class_time != null) {
            this.class_time = timeTable.class_time;
        }
        if(timeTable.class_place != null) {
            this.class_place = timeTable.class_place;
        }
        if(timeTable.professor != null) {
            this.professor = timeTable.professor;
        }
        if(timeTable.grades != null) {
            this.grades = timeTable.grades;
        }
        if(timeTable.memo != null) {
            this.memo = timeTable.memo;
        }
        if(timeTable.code != null) {
            this.code = timeTable.code;
        }
        if(timeTable.department != null) {
            this.department = timeTable.department;
        }
        if(timeTable.lecture_class != null) {
            this.lecture_class = timeTable.lecture_class;
        }
        if(timeTable.target != null) {
            this.target = timeTable.target;
        }
        if(timeTable.regular_number != null) {
            this.regular_number = timeTable.regular_number;
        }
        if(timeTable.design_score != null) {
            this.design_score = timeTable.design_score;
        }
    }

    public Boolean hasGrantDelete(User user) {
        if (user == null) return false;
        return (this.getUser_id().equals(user.getId()));
    }

    @Override
    public String toString() {
        return "TimeTable{" +
                ", user_id=" + user_id +
                ", semester_id='" + semester_id + '\'' +
                ", code='" + code + '\'' +
                ", class_title='" + class_title + '\'' +
                ", class_time='" + class_time + '\'' +
                ", class_place='" + class_place + '\'' +
                ", professor='" + professor + '\'' +
                ", grades='" + grades + '\'' +
                ", lecture_class='" + lecture_class + '\'' +
                ", target='" + target + '\'' +
                ", regular_number='" + regular_number + '\'' +
                ", design_score='" + design_score + '\'' +
                ", department='" + department + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
