package koreatech.in.domain.Faq;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Faq {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.UpdateAdmin.class }, message = "질문은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "질문", example = "질문1")
    private String question;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.UpdateAdmin.class }, message = "답변은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "답변", example = "답변1")
    private String answer;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;
    @ApiModelProperty(notes = "관련 동아리 id", example = "10")
    private Integer circle_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
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

    public Integer getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(Integer circle_id) {
        this.circle_id = circle_id;
    }

    @Override
    public String toString() {
        return "Faq{" +
                "id=" + id +
                ", question=" + question +
                ", answer=" + answer +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", circle_id=" + circle_id +
                '}';
    }

    public void update(Faq faq) {
        if(faq.question != null) {
            this.question = faq.question;
        }
        if(faq.answer != null) {
            this.answer = faq.answer;
        }
        if(faq.is_deleted != null) {
            this.is_deleted = faq.is_deleted;
        }
        if(faq.circle_id != null) {
            this.circle_id = faq.circle_id;
        }
    }
}
