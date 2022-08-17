package koreatech.in.domain.MarketPlace;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.user.User;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ItemComment {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "item 고유 id", example = "10")
    private Integer item_id;
    @NotNull(message = "댓글 내용은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "내용", example = "답글입니다")
    private String content;
    @ApiModelProperty(notes = "답글 유저의 고유 id", example = "10")
    private Integer user_id;
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

    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
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

    public Boolean hasGrantUpdate(User user) {
        return this.hasGrantDelete(user);
    }

    public void update(ItemComment itemComment) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(itemComment.item_id != null) {
            this.item_id = itemComment.item_id;
        }
        if(itemComment.content != null) {
            this.content = itemComment.content;
        }
        if(itemComment.user_id != null) {
            this.user_id = itemComment.user_id;
        }
        if(itemComment.nickname != null) {
            this.nickname = itemComment.nickname;
        }
        if(itemComment.is_deleted != null) {
            this.is_deleted = itemComment.is_deleted;
        }
    }
}