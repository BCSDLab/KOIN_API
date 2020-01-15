package koreatech.in.domain.Callvan;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Participant {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }, message = "room_id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "room 고유 id", example = "10")
    private Integer room_id;
    @ApiModelProperty(notes = "유저 고유 id", example = "10")
    private Integer user_id;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
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

    public Integer getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Integer room_id) {
        this.room_id = room_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
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

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", room_id=" + room_id +
                ", user_id=" + user_id +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    public void update(Participant participant) {
        if(participant.room_id != null) {
            this.room_id = participant.room_id;
        }
        if(participant.user_id != null) {
            this.user_id = participant.user_id;
        }
        if(participant.is_deleted != null) {
            this.is_deleted = participant.is_deleted;
        }
    }
}
