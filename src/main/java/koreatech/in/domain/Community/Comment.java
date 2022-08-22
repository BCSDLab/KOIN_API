package koreatech.in.domain.Community;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.user.User;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Comment {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "게시글 고유 id", example = "10")
    private Integer article_id;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "댓글 내용은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "내용", example = "답글입니다")
    private String content;
    @ApiModelProperty(notes = "답글 유저의 고유 id", example = "10")
    private Integer user_id;
    @ApiModelProperty(notes = "댓글 유저의 닉네임", example = "jahona")
    private String nickname;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    private Boolean grantEdit;
    @ApiModelProperty(hidden = true)
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

    public Integer getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Integer article_id) {
        this.article_id = article_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
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

    public Boolean hasGrantUpdate(User user) { return this.hasGrantDelete(user); }
    //TODO: is_deleted가 true 일 때 스코프 처리

    public void update(Comment comment) {
        if(comment.article_id != null) {
            this.article_id = comment.article_id;
        }
        if(comment.content != null) {
            this.content = comment.content;
        }
        if(comment.user_id != null) {
            this.user_id = comment.user_id;
        }
        if(comment.nickname != null) {
            this.nickname = comment.nickname;
        }
        if(comment.is_deleted != null) {
            this.is_deleted = comment.is_deleted;
        }
        if(comment.grantEdit != null) {
            this.grantEdit = comment.grantEdit;
        }
        if(comment.grantDelete != null) {
            this.grantDelete = comment.grantDelete;
        }
    }
}
