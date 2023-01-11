package koreatech.in.domain.BokDuck;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.User.User;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(description="LandComment")
public class LandComment {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "유저 고유 id", example = "10")
    private Integer user_id;
    @ApiModelProperty(notes = "원룸 고유 id", example = "10")
    private Integer land_id;
    @ApiModelProperty(notes = "내용", example = "댓글입니당")
    private String content;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }, message = "평점은 비워둘 수 없습니다.")
    @Min(value = 1, message = "평점은 1부터 10까지의 정수여야 합니다.")
    @Max(value = 10, message = "평점은 1부터 10까지의 정수여야 합니다.")
    @ApiModelProperty(notes = "1~10까지의 정수로 받고, 평점을 의미", example = "1")
    private Integer score;
    @ApiModelProperty(notes = "댓글 유저의 닉네임", example = "jahona")
    private String nickname;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(notes = "댓글 수정 권한", example = "false")
    private Boolean grantEdit;
    @ApiModelProperty(notes = "댓글 삭제 권한", example = "true")
    private Boolean grantDelete;
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

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getLand_id() {
        return land_id;
    }

    public void setLand_id(Integer land_id) {
        this.land_id = land_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Boolean getGrantEdit() {
        return grantEdit;
    }

    public void setGrantEdit(Boolean grantEdit) {
        this.grantEdit = grantEdit;
    }

    public Boolean getGrantDelete() {
        return grantDelete;
    }

    public void setGrantDelete(Boolean grantDelete) {
        this.grantDelete = grantDelete;
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

    public Boolean hasGrantDelete(User user) {
        if (user == null) return false;
        if ((user.getAuthority() != null) && user.getAuthority().getGrant_community()) return true;
        return (this.getUser_id().equals(user.getId()));
    }

    public Boolean hasGrantUpdate(User user) {
        return this.hasGrantDelete(user);
    }

    public void update(LandComment landComment) {
        if(landComment.user_id != null) {
            this.user_id = landComment.user_id;
        }
        if(landComment.land_id != null) {
            this.land_id = landComment.land_id;
        }
        if(landComment.content != null) {
            this.content = landComment.content;
        }
        if(landComment.score != null) {
            this.score = landComment.score;
        }
        if(landComment.nickname != null) {
            this.nickname = landComment.nickname;
        }
        if(landComment.is_deleted != null) {
            this.is_deleted = landComment.is_deleted;
        }
    }
}