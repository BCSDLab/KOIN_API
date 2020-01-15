package koreatech.in.domain.kut;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;

public class Calendar {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "년도", example = "2018")
    private String year;
    @ApiModelProperty(notes = "시작 월", example = "03")
    private String start_month;
    @ApiModelProperty(notes = "종료 월", example = "04")
    private String end_month;
    @ApiModelProperty(notes = "시작 일", example = "07")
    private String start_day;
    @ApiModelProperty(notes = "종료 일", example = "06")
    private String end_day;
    @ApiModelProperty(notes = "행사 내용", example = "수강신청 기간")
    private String schedule;
    @ApiModelProperty(hidden = true)
    private Integer seq;
    @ApiModelProperty(notes = "연속 여부(0: 하루, 1: 이틀 이상)", example = "1")
    private Boolean is_continued;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStart_month() {
        return start_month;
    }

    public void setStart_month(String start_month) {
        this.start_month = start_month;
    }

    public String getEnd_month() {
        return end_month;
    }

    public void setEnd_month(String end_month) {
        this.end_month = end_month;
    }

    public String getStart_day() {
        return start_day;
    }

    public void setStart_day(String start_day) {
        this.start_day = start_day;
    }

    public String getEnd_day() {
        return end_day;
    }

    public void setEnd_day(String end_day) {
        this.end_day = end_day;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Boolean getIs_continued() {
        return is_continued;
    }

    public void setIs_continued(Boolean is_continued) {
        this.is_continued = is_continued;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "id=" + id +
                ", year='" + year + '\'' +
                ", start_month='" + start_month + '\'' +
                ", end_month='" + end_month + '\'' +
                ", start_day='" + start_day + '\'' +
                ", end_day='" + end_day + '\'' +
                ", schedule='" + schedule + '\'' +
                ", seq=" + seq +
                ", is_continued=" + is_continued +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
